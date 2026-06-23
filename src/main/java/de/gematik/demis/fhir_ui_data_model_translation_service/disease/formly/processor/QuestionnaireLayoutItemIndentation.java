package de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor;

/*-
 * #%L
 * FHIR UI Data Model Translation Service
 * %%
 * Copyright (C) 2025 - 2026 gematik GmbH
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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.DiseaseDataPreparationSrv;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.EnableWhen;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Props;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Wrapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Strings;

@RequiredArgsConstructor
@Slf4j
final class QuestionnaireLayoutItemIndentation {

  static final int INDENTATION_LEVEL_MAX_DEFAULT = 4;

  private final FieldGroup root;
  private final int indentationLevelMax;

  private int indentationLevel = 0;

  void applyItemIndentation() {
    traverseAndIndentFollowUps(this.root);
  }

  private void traverseAndIndentFollowUps(FieldGroup fieldGroup) {

    // apply indentation
    boolean indentationLevelRaised = false;
    if (isFollowUp(fieldGroup)) {
      this.indentationLevel++;
      indentationLevelRaised = true;
      indentFollowUp(fieldGroup);
    }

    // recurse on children
    if (this.indentationLevel < this.indentationLevelMax) {
      determineChildrenToTraverse(fieldGroup).forEach(this::traverseAndIndentFollowUps);
    } else {
      log.debug(
          "Maximum indentation level of {} reached. No further indentation applied. Current node: {}",
          this.indentationLevelMax,
          getNodeName(fieldGroup));
    }

    // reset indentation level
    if (indentationLevelRaised) {
      this.indentationLevel--;
    }
  }

  private boolean isFollowUp(FieldGroup fieldGroup) {

    // include enable-when items (follow-up questions)
    final Props props = fieldGroup.getProps();
    if (props == null) {
      return false;
    }
    final EnableWhen[] enableWhens = props.getEnableWhen();
    if ((enableWhens == null) || (enableWhens.length == 0)) {
      return false;
    }

    // exclude repeat sections
    if (isRepeatSection(fieldGroup)) {
      return false;
    }

    // exclude item group children
    if (isAnswerDirectChildOfItemGroup(fieldGroup)) {
      return false;
    }

    return true;
  }

  private List<FieldGroup> determineChildrenToTraverse(FieldGroup parent) {
    final List<FieldGroup> children = parent.getFieldGroups();
    if ((children == null) || children.isEmpty()) {
      return Collections.emptyList();
    }
    if (matchesFollowUpSubitemStructure(children)) {
      log.debug(
          "Subitem structure found. Recursion on first child. Parent: {}", getNodeName(parent));
      return List.of(children.getFirst());
    }
    return children;
  }

  /**
   * Special case for nested subitems: the first child is a typed follow-up field, while the next
   * sibling is an untyped container holding dependent subitems. Traversal continues through the
   * first child only; the remaining siblings are handled during wrapping.
   *
   * <p>In FHIR Questionnaire an item that contains an answer may also contain further items
   * (subitems).
   *
   * <pre>
   *     Item: Did you travel through Britain in the 1990s?
   *     - Answer: Boolean
   *     - Items:
   *       - Item: Did you eat beef?
   *         - Answer: Boolean
   *       - Item: Did you visit a farm?
   *         - Answer: Boolean
   * </pre>
   *
   * But in Formly a field group of an input type must not contain further field groups. Instead,
   * the subitems are placed as siblings and wrapped within a container.
   *
   * <pre>
   *     FieldGroup
   *     - FieldGroup
   *       - label: Did you travel through Britain in the 1990s?
   *       - type: boolean
   *     - FieldGroup
   *       - label: Did you eat beef?
   *       - type: boolean
   *     - FieldGroup
   *       - label: Did you visit a farm?
   *       - type: boolean
   * </pre>
   */
  private boolean matchesFollowUpSubitemStructure(List<FieldGroup> siblings) {
    if ((siblings == null) || (siblings.size() <= 1)) {
      return false;
    }

    final FieldGroup first = siblings.getFirst();
    final FieldGroup second = siblings.get(1);
    final FieldGroup parent = first.getParent();

    return (parent != null)
        && !DiseaseDataPreparationSrv.FIELD_GROUP_ROOT.equals(parent.getKey())
        && hasType(first)
        && !hasType(second)
        && isFollowUp(first);
  }

  private void indentFollowUp(FieldGroup fieldGroup) {
    if (isContainer(fieldGroup)) {
      addPanelWrapper(fieldGroup);
    } else {
      wrapInPanelContainer(fieldGroup);
    }
  }

  /**
   * A first level answer item that is a child of an item group must not be indented, as the item
   * group itself is already drawn with a border.
   *
   * @param fieldGroup field group to check
   * @return <code>true</code> when child of item group, <code>false</code> if not
   */
  private boolean isAnswerDirectChildOfItemGroup(FieldGroup fieldGroup) {
    final FieldGroup answerFieldGroup = findSelfOrParentAnswerFieldGroup(fieldGroup);
    if (answerFieldGroup == null) {
      return false;
    }
    final FieldGroup parent = answerFieldGroup.getParent();
    if (parent == null) {
      return false;
    }
    if (!Strings.CS.startsWith(parent.getFieldGroupClassName(), GroupProcessor.CLASSNAME_PREFIX)) {
      return false;
    }
    log.debug("Found child of item group. FieldGroup: {}", getNodeName(answerFieldGroup));
    return true;
  }

  private FieldGroup findSelfOrParentAnswerFieldGroup(FieldGroup fieldGroup) {
    if (isAnswerFieldGroupByKey(fieldGroup)) {
      return fieldGroup;
    }
    final FieldGroup parent = fieldGroup.getParent();
    if (isAnswerFieldGroupByKey(parent)) {
      return parent;
    }
    return null;
  }

  private boolean isAnswerFieldGroupByKey(FieldGroup fieldGroup) {
    return (fieldGroup != null) && Strings.CI.contains(fieldGroup.getKey(), "answer");
  }

  private void addPanelWrapper(FieldGroup fieldGroup) {
    List<Wrapper> wrappers = fieldGroup.getWrappers();
    if (wrappers == null) {
      wrappers = new ArrayList<>();
      fieldGroup.setWrappers(wrappers);
      wrappers.add(Wrapper.PANEL);
    } else if (!wrappers.contains(Wrapper.PANEL)) {
      wrappers.add(Wrapper.PANEL);
    }
  }

  private boolean hasType(FieldGroup fieldGroup) {
    return fieldGroup.getType() != null;
  }

  private boolean isRepeatSection(FieldGroup fieldGroup) {
    return FieldGroup.TYPE_REPEAT.equals(fieldGroup.getType());
  }

  private boolean isContainer(FieldGroup fieldGroup) {
    return fieldGroup.getType() == null;
  }

  private void wrapInPanelContainer(FieldGroup fieldGroupToWrap) {

    // prepare
    final FieldGroup parent = fieldGroupToWrap.getParent();
    final boolean subitemStructure = matchesFollowUpSubitemStructure(parent.getFieldGroups());

    // wrap and replace
    replaceChildWithPanelContainer(parent, fieldGroupToWrap);
    prefixEnableWhenPathsWithParent(fieldGroupToWrap);

    // subitems
    if (subitemStructure) {
      log.debug("Applying indentation to subitem structure. Parent: {}", getNodeName(parent));
      final List<FieldGroup> subitems = parent.getFieldGroups().stream().skip(1).toList();
      subitems.forEach(this::indentFollowUp);
      subitems.forEach(this::traverseAndIndentFollowUps);
    }
  }

  private void replaceChildWithPanelContainer(FieldGroup parent, FieldGroup childToReplace) {
    final List<FieldGroup> children = parent.getFieldGroups();
    final int index = children.indexOf(childToReplace);
    final FieldGroup panelContainer =
        FieldGroup.builder()
            .wrappers(List.of(Wrapper.PANEL))
            .fieldGroups(List.of(childToReplace))
            .build();
    children.set(index, panelContainer);
    panelContainer.setParent(parent);
    childToReplace.setParent(panelContainer);
  }

  private void prefixEnableWhenPathsWithParent(FieldGroup fieldGroup) {
    Stream.of(fieldGroup.getProps().getEnableWhen())
        .forEach(enableWhen -> enableWhen.setPath("parent." + enableWhen.getPath()));
  }

  private String getNodeName(FieldGroup fieldGroup) {
    return Objects.requireNonNullElseGet(fieldGroup.getKey(), fieldGroup::getClassName);
  }
}
