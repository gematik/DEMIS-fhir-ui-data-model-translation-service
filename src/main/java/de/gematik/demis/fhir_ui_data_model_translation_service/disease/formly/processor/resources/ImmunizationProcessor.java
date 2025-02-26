package de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.resources;

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

import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Props;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.ChoiceProcessor;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.translation.DataLoaderSrv;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Questionnaire;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImmunizationProcessor {

  private static final String REF_ELEMENT = "REF_ELEMENT";

  private final DataLoaderSrv dataLoaderSrv;

  public FieldGroup createFieldGroup(
      Questionnaire.QuestionnaireItemComponent item, String diseaseCode, FieldGroup parent) {
    FieldGroup immunization =
        FieldGroup.builder()
            .key("Immunization")
            .parent(parent)
            .fieldGroupClassName("ITEM_REFERENCE")
            .className("LinkId_" + item.getLinkId())
            .build();
    createVaccineCode(diseaseCode, immunization);
    createOccurrence(immunization);
    createNote(immunization);
    return immunization;
  }

  private void createVaccineCode(String diseaseCode, FieldGroup parent) {
    FieldGroup vaccineCode =
        FieldGroup.builder()
            .key("vaccineCode")
            .fieldGroupClassName(REF_ELEMENT)
            .parent(parent)
            .build();
    CodeDisplay[] options =
        this.dataLoaderSrv
            .getValueSetData(
                "https://demis.rki.de/fhir/ValueSet/vaccine" + diseaseCode.toUpperCase())
            .toArray(CodeDisplay[]::new);
    FieldGroup input =
        FieldGroup.builder()
            .type(FieldGroup.TYPE_CODING)
            .key("answer." + FieldGroup.KEY_VALUE_CODING)
            .className("vaccine")
            .parent(vaccineCode)
            .props(
                Props.builder()
                    .label("Verabreichter Impfstoff")
                    .clearable(true)
                    .required(true)
                    .options(options)
                    .build())
            .build();
    ChoiceProcessor.enableValidation(input);
  }

  private void createOccurrence(FieldGroup parent) {
    FieldGroup occurrence =
        FieldGroup.builder()
            .key("occurrence")
            .fieldGroupClassName(REF_ELEMENT)
            .parent(parent)
            .build();
    FieldGroup.builder()
        .key("answer.valueDate")
        .className("vaccinationDate")
        .type(FieldGroup.TYPE_INPUT)
        .parent(occurrence)
        .props(
            Props.builder()
                .required(true)
                .label("Datum der Impfung")
                .placeholder("TT.MM.JJJJ | MM.JJJJ | JJJJ")
                .build())
        .build();
  }

  private void createNote(FieldGroup parent) {
    FieldGroup note =
        FieldGroup.builder().key("note").fieldGroupClassName(REF_ELEMENT).parent(parent).build();
    FieldGroup.builder()
        .type(FieldGroup.TYPE_TEXT_AREA)
        .key("answer.valueString")
        .className("note")
        .parent(note)
        .props(Props.builder().label("Hinweis zur Impfung").build())
        .build();
  }
}
