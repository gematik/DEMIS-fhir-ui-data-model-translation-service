package de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor;

/*-
 * #%L
 * FHIR UI Data Model Translation Service
 * %%
 * Copyright (C) 2025 gematik GmbH
 * %%
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the
 * European Commission – subsequent versions of the EUPL (the "Licence").
 * You may not use this work except in compliance with the Licence.
 *
 * You find a copy of the Licence in the "Licence" file or at
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.
 * In case of changes by gematik find details in the "Readme" file.
 *
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #L%
 */

import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.EnableWhen;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldArray;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Props;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Questionnaire;
import org.springframework.stereotype.Service;

/** Processes enable-when conditions. */
@Service
@Slf4j
public class EnableWhenProcessor {

  /**
   * Creates enable-when condition at field group, if defined on questionnaire item
   *
   * @param item source questionnaire item with optional enable-when condition
   * @param fieldGroup target Formly field group
   */
  public void createEnableWhens(
      Questionnaire.QuestionnaireItemComponent item, FieldGroup fieldGroup) {
    if (item != null) {
      final List<Questionnaire.QuestionnaireItemEnableWhenComponent> enableWhens =
          item.getEnableWhen();
      if ((enableWhens != null) && !enableWhens.isEmpty()) {
        setEnableWhen(
            enableWhens.stream()
                .map(ew -> createEnableWhen(ew, fieldGroup))
                .filter(Objects::nonNull)
                .toArray(EnableWhen[]::new),
            item,
            fieldGroup);
      }
    }
  }

  private EnableWhen createEnableWhen(
      Questionnaire.QuestionnaireItemEnableWhenComponent enableWhen, FieldGroup fieldGroup) {
    try {
      return EnableWhen.builder()
          .path(createPath(enableWhen.getQuestion(), fieldGroup))
          .op(enableWhen.getOperator().toCode())
          .value(enableWhen.getAnswerCoding().getCode())
          .question(enableWhen.getQuestion())
          .build();
    } catch (Exception e) {
      log.error(
          "DEMIS FHIR profile error! Failed to create enable-when condition! FieldGroup: {} Question: {}",
          fieldGroup.getKey(),
          enableWhen.getQuestion(),
          e);
      return null;
    }
  }

  private String createPath(String question, FieldGroup fieldGroup) {
    final StringBuilder path = new StringBuilder();
    final Collection<String> keys = createKeys(question);
    if (!createPath(fieldGroup, keys, path)) {
      throw new IllegalStateException(
          MessageFormat.format(
              "Failed to find parent field group! FieldGroup: {0} ParentKeys: {1}",
              fieldGroup.getKey(), keys));
    }
    return path.toString();
  }

  private Set<String> createKeys(String question) {
    /*
     * Not using Set.of() to have null-support with contains-method:
     * set.contains(null)
     */
    final Set<String> keys = new HashSet<>();
    keys.add(question);
    keys.add(question + ".answer");
    return Collections.unmodifiableSet(keys);
  }

  private boolean createPath(FieldGroup fieldGroup, Collection<String> keys, StringBuilder path) {
    if (keys.contains(fieldGroup.getKey())) {
      return true;
    }
    final var parent = fieldGroup.getParent();
    if (parent == null) {
      return false;
    }
    // we will either go to parent or take a sibling
    if (!path.isEmpty()) {
      path.append('.');
    }
    // check siblings
    List<FieldGroup> siblings = parent.getFieldGroups();
    if (siblings != null) {
      for (int i = 0; i < siblings.size(); i++) {
        FieldGroup sibling = siblings.get(i);
        if (keys.contains(sibling.getKey()) && (sibling != fieldGroup)) {
          path.append("parent.fieldGroup.");
          path.append(i);
          return true;
        }
      }
    }
    // go up to parent
    path.append("parent");
    return createPath(parent, keys, path);
  }

  private void setEnableWhen(
      EnableWhen[] enableWhen,
      Questionnaire.QuestionnaireItemComponent item,
      FieldGroup fieldGroup) {
    if (enableWhen.length > 0) {
      Props props = fieldGroup.getProps();
      if (props == null) {
        props = Props.builder().build();
        fieldGroup.setProps(props);
      }
      props.setEnableWhen(enableWhen);
      props.setEnableBehavior(item.getEnableBehavior());
    }
  }

  /**
   * Increments intersecting enable-when conditions of the field array's children. Intersecting
   * enable-when conditions are those that reference a field group that is a parent of the field
   * array.
   *
   * @param fieldArray field array
   */
  public void incrementIntersectingEnableWhens(FieldArray fieldArray) {
    final List<EnableWhen> enableWhens = new LinkedList<>();
    final Set<String> keys = new HashSet<>();
    for (FieldGroup fieldGroup : fieldArray.getFieldGroup()) {
      addLinkIdsAndEnableWhensRecursively(fieldGroup, keys, enableWhens);
    }
    enableWhens.stream().filter(ew -> intersecting(ew, keys)).forEach(this::increment);
  }

  private boolean intersecting(EnableWhen enableWhen, Set<String> keys) {
    final Set<String> parentKeys = createKeys(enableWhen.getQuestion());
    return Collections.disjoint(keys, parentKeys);
  }

  private void addLinkIdsAndEnableWhensRecursively(
      FieldGroup fieldGroup, Collection<String> keys, Collection<EnableWhen> enableWhens) {
    // key
    String key = fieldGroup.getKey();
    if (key != null) {
      keys.add(key);
    }
    // enable when
    Props props = fieldGroup.getProps();
    if (props != null) {
      EnableWhen[] enableArray = props.getEnableWhen();
      if (enableArray != null) {
        enableWhens.addAll(Arrays.asList(enableArray));
      }
    }
    // children
    List<FieldGroup> subGroups = fieldGroup.getFieldGroups();
    if ((subGroups != null)) {
      for (FieldGroup subGroup : subGroups) {
        addLinkIdsAndEnableWhensRecursively(subGroup, keys, enableWhens);
      }
    }
    // field array
    FieldArray fieldArray = fieldGroup.getFieldArray();
    if (fieldArray != null) {
      for (FieldGroup subGroup : fieldArray.getFieldGroup()) {
        addLinkIdsAndEnableWhensRecursively(subGroup, keys, enableWhens);
      }
    }
  }

  private void increment(EnableWhen enableWhen) {
    enableWhen.setPath("parent." + enableWhen.getPath());
  }
}
