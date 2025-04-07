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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.DiseaseClipboardProps;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.ImportSpec;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Props;
import java.util.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Questionnaire;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Service
public class ClipboardProcessor {

  private static final Collection<String> INPUT_TYPES =
      List.of(
          FieldGroup.TYPE_CODING,
          FieldGroup.TYPE_CODING_RADIO,
          FieldGroup.TYPE_INPUT,
          FieldGroup.TYPE_TEXT_AREA);

  private final DiseaseClipboardProps diseaseClipboardProps;

  /**
   * Create clipboard configuration at field group
   *
   * @param key clipboard key external systems use to write clipboard data
   * @param multi <code>true</code> if multiple values are allowed, <code>false</code> if only one
   * @param fieldGroup target field group
   */
  public static void createClipboard(String key, boolean multi, FieldGroup fieldGroup) {
    final var props = getOrCreateProps(fieldGroup);
    final var clipboard = ImportSpec.builder().importKey(key).multi(multi).build();
    props.setImportSpec(clipboard);
  }

  private static Props getOrCreateProps(FieldGroup fieldGroup) {
    Props props = fieldGroup.getProps();
    if (props == null) {
      props = Props.builder().build();
      fieldGroup.setProps(props);
    }
    return props;
  }

  /**
   * Creates clipboard configuration at given field group, if defined in clipboard configuration.
   *
   * @param item source questionnaire item
   * @param fieldGroup target field group
   */
  void createClipboard(Questionnaire.QuestionnaireItemComponent item, FieldGroup fieldGroup) {
    if (isInputFieldGroup(fieldGroup)) {
      Optional<String> clipboardKey = clipboardKey(item);
      if (clipboardKey.isPresent()) {
        String key = clipboardKey.get();
        boolean multi = item.getRepeats();
        createClipboard(key, multi, fieldGroup);
      }
    }
  }

  private boolean isInputFieldGroup(FieldGroup fieldGroup) {
    String type = StringUtils.trimToNull(fieldGroup.getType());
    if (type != null) {
      return INPUT_TYPES.contains(type.toLowerCase());
    }
    return false;
  }

  private Optional<String> clipboardKey(Questionnaire.QuestionnaireItemComponent item) {
    String linkId = item.getLinkId();
    return this.diseaseClipboardProps.common().entrySet().stream()
        .filter(e -> e.getKey().startsWith(linkId))
        .map(Map.Entry::getValue)
        .findFirst();
  }
}
