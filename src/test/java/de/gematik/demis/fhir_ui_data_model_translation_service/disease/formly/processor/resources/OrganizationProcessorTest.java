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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.DiseaseClipboardProps;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.EnableWhenProcessor;
import de.gematik.demis.fhir_ui_data_model_translation_service.translation.DataLoaderSrv;
import java.util.List;
import org.hl7.fhir.r4.model.Questionnaire;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrganizationProcessorTest {

  @Mock private DiseaseClipboardProps diseaseClipboardProps;
  @Mock private DataLoaderSrv dataLoaderSrv;

  @Test
  void createFieldGroup_shouldCreate() {
    OrganizationProcessor processor =
        new OrganizationProcessor(
            new EnableWhenProcessor(), this.diseaseClipboardProps, this.dataLoaderSrv);
    FieldGroup parent = FieldGroup.builder().build();
    Questionnaire.QuestionnaireItemComponent item = new Questionnaire.QuestionnaireItemComponent();
    item.setLinkId("testLinkId");

    FieldGroup organization = processor.createFieldGroup(item, parent);
    verifyOrganization(organization);
  }

  private void verifyOrganization(FieldGroup organization) {
    assertThat(organization).isNotNull();
    assertThat(organization.getClassName()).isEqualTo("LinkId_testLinkId");
    assertThat(organization.getKey()).isEqualTo("Organization");
    List<FieldGroup> parameters = organization.getFieldGroups();
    assertThat(parameters).hasSize(4);
    List<String> parameterKeys = parameters.stream().map(FieldGroup::getKey).toList();
    assertThat(parameterKeys)
        .containsExactly("name.answer.valueString", "address", "contact", "telecom");
    verifyAddress(parameters);
    verifyContact(parameters);
  }

  private void verifyAddress(List<FieldGroup> organization) {
    List<FieldGroup> parameters = organization.get(1).getFieldGroups();
    List<String> parameterKeys = parameters.stream().map(FieldGroup::getKey).toList();
    assertThat(parameterKeys)
        .containsExactly(
            "street.answer.valueString",
            "houseNumber.answer.valueString",
            "postalCode.answer.valueString",
            "city.answer.valueString",
            "country.answer.valueCoding");
  }

  private void verifyContact(List<FieldGroup> organization) {
    List<String> parameters =
        organization.get(2).getFieldGroups().stream().map(FieldGroup::getKey).toList();
    assertThat(parameters)
        .containsExactly(
            "name.prefix.answer.valueString",
            "name.given.answer.valueString",
            "name.family.answer.valueString");
  }
}
