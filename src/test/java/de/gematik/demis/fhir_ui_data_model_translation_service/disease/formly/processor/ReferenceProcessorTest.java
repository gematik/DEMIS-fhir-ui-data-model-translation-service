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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.resources.HospitalizationProcessor;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.resources.ImmunizationProcessor;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.resources.OrganizationProcessor;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Questionnaire;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReferenceProcessorTest {

  @Mock private ImmunizationProcessor immunizationProcessorMock;
  @Mock private HospitalizationProcessor hospitalizationProcessorMock;
  @Mock private OrganizationProcessor organizationProcessorMock;
  @Mock private EnableWhenProcessor enableWhenProcessor;

  @InjectMocks private ReferenceProcessor referenceProcessor;

  @Test
  @DisplayName("check call for immunizationProcessor")
  void shouldCallImmunizationProcessor() {
    Questionnaire.QuestionnaireItemComponent item = new Questionnaire.QuestionnaireItemComponent();
    item.addExtension(
        new Extension("http://hl7.org/fhir/StructureDefinition/questionnaire-referenceProfile")
            .setValue(new CanonicalType("ImmunizationInformation")));
    referenceProcessor.createFieldGroup(item, null, "abcd");
    verify(immunizationProcessorMock).createFieldGroup(item, "abcd", null);
    verifyNoInteractions(enableWhenProcessor);
  }

  @Test
  @DisplayName("check call for hospitalizationProcessor")
  void shouldCallHospitalizationProcessor() {
    Questionnaire.QuestionnaireItemComponent item = new Questionnaire.QuestionnaireItemComponent();
    item.addExtension(
        new Extension("http://hl7.org/fhir/StructureDefinition/questionnaire-referenceProfile")
            .setValue(new CanonicalType("Hospitalization")));
    referenceProcessor.createFieldGroup(item, null, null);
    verify(hospitalizationProcessorMock).createFieldGroup(item, null);
    verifyNoInteractions(enableWhenProcessor);
  }

  @Test
  @DisplayName("check call for organizationProcessor")
  void shouldCallOrganizationProcessor() {
    Questionnaire.QuestionnaireItemComponent item = new Questionnaire.QuestionnaireItemComponent();
    item.setLinkId("linkId");
    item.addExtension(
        new Extension("http://hl7.org/fhir/StructureDefinition/questionnaire-referenceResource")
            .setValue(new CanonicalType("Organization")));
    referenceProcessor.createFieldGroup(item, null, null);
    verify(organizationProcessorMock).createFieldGroup(item, null);
    verifyNoInteractions(enableWhenProcessor);
  }

  @Test
  @DisplayName("check call for id only case")
  void shouldCallIdOnlyCase() {
    Questionnaire.QuestionnaireItemComponent item = new Questionnaire.QuestionnaireItemComponent();
    item.setLinkId("valueString");
    item.setRequired(true);
    item.setText("text");
    item.addExtension(
        new Extension("http://hl7.org/fhir/StructureDefinition/questionnaire-referenceResource")
            .setValue(new CanonicalType("something")));
    FieldGroup parent = FieldGroup.builder().build();
    FieldGroup[] fieldGroups = referenceProcessor.createFieldGroup(item, parent, null);
    assertThat(fieldGroups).hasSize(1);

    FieldGroup fieldGroup = fieldGroups[0];
    assertThat(fieldGroup.getKey()).isEqualTo("valueReference");
    assertThat(fieldGroup.getType()).isEqualTo("input");
    assertThat(fieldGroup.getParent()).isEqualTo(parent);
    assertThat(fieldGroup.getProps().getRequired()).isTrue();
    assertThat(fieldGroup.getProps().getLabel()).isEqualTo("text");
    assertThat(fieldGroup.getClassName()).isEqualTo("LinkId_valueString");
    verify(enableWhenProcessor).createEnableWhens(item, fieldGroup);
    verify(enableWhenProcessor, Mockito.never()).incrementIntersectingEnableWhens(Mockito.any());
  }

  @Test
  @DisplayName("unkown case")
  void shouldReturnEmptyFieldGroup() {
    Questionnaire.QuestionnaireItemComponent item = new Questionnaire.QuestionnaireItemComponent();
    item.addExtension(
        new Extension("http://hl7.org/fhir/StructureDefinition/questionnaire-referenceProfile")
            .setValue(new CanonicalType("unkown")));
    FieldGroup[] fieldGroup = referenceProcessor.createFieldGroup(item, null, null);
    assertThat(fieldGroup).isEmpty();
    verifyNoInteractions(enableWhenProcessor);
  }
}
