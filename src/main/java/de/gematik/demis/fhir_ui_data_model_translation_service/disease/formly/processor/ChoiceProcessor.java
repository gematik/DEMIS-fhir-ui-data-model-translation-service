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

import static de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Wrapper.FORM_FIELD;

import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Props;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.translation.DataLoaderSrv;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Questionnaire;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChoiceProcessor implements ItemProcessor {

  private static final String TYPE_EXTENSION_URL =
      "http://hl7.org/fhir/StructureDefinition/questionnaire-itemControl";
  private static final String VALIDATOR = "codingValidator";
  private static final String TYPE_CODING_RKI = "drop-down-coding";

  private final EnableWhenProcessor enableWhenProcessor;
  private final DataLoaderSrv dataLoaderSrv;
  private final ClipboardProcessor clipboardProcessor;

  /**
   * Enables value coding validation if key and type of field group describe an autocomplete-coding
   *
   * @param fieldGroup field group
   */
  public static void enableValidation(FieldGroup fieldGroup) {
    if (StringUtils.contains(fieldGroup.getKey(), FieldGroup.KEY_VALUE_CODING)
        && StringUtils.equalsAny(
            fieldGroup.getType(), FieldGroup.TYPE_CODING, FieldGroup.TYPE_CODING_MULTI)) {
      fieldGroup.addValidator(VALIDATOR);
    }
  }

  private static boolean isRequired(Questionnaire.QuestionnaireItemComponent item) {
    /*
     * DEMIS-869 Fixing repeatable, required group item <code>infectionEnvironmentSettingGroup</code>
     * that does not contain a single required item in the RKI FHIR profile,
     * but the contained 'kind' shall be required at the user interface.
     */
    return item.getRequired() || isCovidInfectionEnvironmentSettingKind(item);
  }

  private static boolean isCovidInfectionEnvironmentSettingKind(
      Questionnaire.QuestionnaireItemComponent item) {
    return StringUtils.equals(item.getLinkId(), "infectionEnvironmentSettingKind")
        && StringUtils.equals(
            item.getAnswerValueSet(),
            "https://demis.rki.de/fhir/ValueSet/answerSetInfectionEnvironmentSettingCVDD");
  }

  @Override
  public FieldGroup[] createFieldGroup(
      Questionnaire.QuestionnaireItemComponent item, FieldGroup parent, String diseaseCode) {
    // analyse if extension is given, default value is "autocomplete-coding"
    // if extension is given and equals autocomplete or drop-down then autocomplete-coding, in
    // every other case it is the given type + "-coding"
    final var fieldGroup =
        FieldGroup.builder()
            .key(FieldGroup.KEY_VALUE_CODING)
            .type(getType(item))
            .props(createProperties(item))
            .parent(parent)
            .className("LinkId_" + item.getLinkId())
            .wrappers(List.of(FORM_FIELD))
            .build();
    enableValidation(fieldGroup);
    this.enableWhenProcessor.createEnableWhens(item, fieldGroup);
    this.clipboardProcessor.createClipboard(item, fieldGroup);
    return new FieldGroup[] {fieldGroup};
  }

  private Props createProperties(Questionnaire.QuestionnaireItemComponent item) {
    return Props.builder()
        .options(getOptions(item))
        .required(isRequired(item))
        .clearable(true)
        .defaultCode(getInitialValue(item))
        .label(item.getText())
        .build();
  }

  private CodeDisplay[] getOptions(Questionnaire.QuestionnaireItemComponent item) {
    final String valueSet = item.getAnswerValueSet();
    return dataLoaderSrv.getValueSetData(valueSet).toArray(CodeDisplay[]::new);
  }

  private String getType(Questionnaire.QuestionnaireItemComponent item) {
    String type = FieldGroup.TYPE_CODING;
    // handle extension
    final Extension extension = item.getExtensionByUrl(TYPE_EXTENSION_URL);
    if (extension != null) {
      type = ((CodeableConcept) extension.getValue()).getCoding().getFirst().getCode() + "-coding";
      if (TYPE_CODING_RKI.equals(type)) {
        type = FieldGroup.TYPE_CODING;
      }
    }
    // handle multiple-choice
    if (isMultipleChoice(item, type)) {
      type = FieldGroup.TYPE_CODING_MULTI;
    }
    return type;
  }

  private boolean isMultipleChoice(Questionnaire.QuestionnaireItemComponent item, String type) {
    return item.getRepeats()
        && FieldGroup.TYPE_CODING.equals(type)
        && ((item.getItem() == null) || item.getItem().isEmpty());
  }

  private String getInitialValue(Questionnaire.QuestionnaireItemComponent itemComponent) {
    if (itemComponent.getInitial().isEmpty()) {
      return null;
    }
    return ((Coding) itemComponent.getInitialFirstRep().getValue()).getCode();
  }
}
