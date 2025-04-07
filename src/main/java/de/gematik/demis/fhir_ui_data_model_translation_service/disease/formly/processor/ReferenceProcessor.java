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

import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Props;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.resources.HospitalizationProcessor;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.resources.ImmunizationProcessor;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.resources.OrganizationProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r4.model.Type;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReferenceProcessor implements ItemProcessor {

  public static final String REFERENCE_PROFILE =
      "http://hl7.org/fhir/StructureDefinition/questionnaire-referenceProfile";
  public static final String REFERENCE_RESOURCE =
      "http://hl7.org/fhir/StructureDefinition/questionnaire-referenceResource";

  private final ImmunizationProcessor immunizationProcessor;
  private final HospitalizationProcessor hospitalizationProcessor;
  private final OrganizationProcessor organizationProcessor;
  private final EnableWhenProcessor enableWhenProcessor;

  private static String extractReferenceType(Extension extension) {
    Type value = extension.getValue();
    if (value instanceof CodeType codeType) {
      return codeType.getValue();
    } else {
      // CanonicalType
      return ((CanonicalType) value).getValue();
    }
  }

  @Override
  public FieldGroup[] createFieldGroup(
      Questionnaire.QuestionnaireItemComponent item, FieldGroup parent, String diseaseCode) {
    Extension profileReference = item.getExtensionByUrl(REFERENCE_PROFILE);
    final FieldGroup fieldGroup;
    if (profileReference == null) {
      fieldGroup = createResourceReferenceFieldGroup(item, parent);
    } else {
      fieldGroup = createProfileReferenceFieldGroup(item, diseaseCode, profileReference, parent);
    }
    if (fieldGroup == null) {
      return new FieldGroup[0];
    }
    return new FieldGroup[] {fieldGroup};
  }

  private FieldGroup createResourceReferenceFieldGroup(
      Questionnaire.QuestionnaireItemComponent item, FieldGroup parent) {
    Extension resourceReference = item.getExtensionByUrl(REFERENCE_RESOURCE);
    if (resourceReference != null
        && extractReferenceType(resourceReference).contains("Organization")) {
      return this.organizationProcessor.createFieldGroup(item, parent);
    }
    return handleIdOnlyCase(item, parent);
  }

  private FieldGroup createProfileReferenceFieldGroup(
      Questionnaire.QuestionnaireItemComponent item,
      String diseaseCode,
      Extension profileReference,
      FieldGroup parent) {
    String type = extractReferenceType(profileReference);
    if (type.contains("ImmunizationInformation")) {
      return this.immunizationProcessor.createFieldGroup(item, diseaseCode, parent);
    } else if (type.contains("Hospitalization")) {
      return this.hospitalizationProcessor.createFieldGroup(item, parent);
    }
    log.info("Reference type not supported: {}", type);
    return null;
  }

  private FieldGroup handleIdOnlyCase(
      Questionnaire.QuestionnaireItemComponent item, FieldGroup parent) {
    FieldGroup fieldGroupValueString =
        FieldGroup.builder()
            .key("valueReference")
            .type(FieldGroup.TYPE_INPUT)
            .parent(parent)
            .props(Props.builder().required(item.getRequired()).label(item.getText()).build())
            .className("LinkId_" + item.getLinkId())
            .build();
    this.enableWhenProcessor.createEnableWhens(item, fieldGroupValueString);
    return fieldGroupValueString;
  }
}
