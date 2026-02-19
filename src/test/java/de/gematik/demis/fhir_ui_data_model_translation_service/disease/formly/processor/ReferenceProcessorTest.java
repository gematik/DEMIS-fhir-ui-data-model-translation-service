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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.fhir.TooltipExtension;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.resources.ResourceProcessor;
import java.util.Optional;
import org.assertj.core.api.Assertions;
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

  private static final String DISEASE_CODE = "cvdd";

  @Mock private ResourceProcessor resourceProcessor;
  @Mock private EnableWhenProcessor enableWhenProcessor;
  @Mock private TooltipExtension tooltipExtension;
  @Mock private ItemProcessor itemProcessor;

  @InjectMocks private ReferenceProcessor referenceProcessor;

  @Test
  @DisplayName("profile reference of immunization information")
  void shouldCallItemProcessor() {

    // given
    final Questionnaire.QuestionnaireItemComponent item =
        new Questionnaire.QuestionnaireItemComponent();
    item.addExtension(
        new Extension("http://hl7.org/fhir/StructureDefinition/questionnaire-referenceProfile")
            .setValue(
                new CanonicalType(
                    "https://demis.rki.de/fhir/StructureDefinition/ImmunizationInformationCVDD")));
    final FieldGroup parent = FieldGroup.builder().build();
    final FieldGroup[] result = FieldGroup.builder().build().toArray();
    when(resourceProcessor.getItemProcessor(item)).thenReturn(Optional.of(itemProcessor));
    when(itemProcessor.createFieldGroup(item, parent, DISEASE_CODE)).thenReturn(result);

    // when
    final FieldGroup[] actual = referenceProcessor.createFieldGroup(item, parent, DISEASE_CODE);

    // then
    Assertions.assertThat(actual).isSameAs(result);
    verify(resourceProcessor).getItemProcessor(item);
    verifyNoInteractions(enableWhenProcessor);
    verifyNoInteractions(tooltipExtension);
  }

  /**
   * When no resource item processor is returned for the reference, the "ID only" case is handled.
   * This is the case when no profile reference is given but a resource reference is given that is
   * not mapped or when no reference is given at all.
   */
  @Test
  @DisplayName("empty or unknown resource reference")
  void shouldCallIdOnlyCase() {

    // given
    Questionnaire.QuestionnaireItemComponent item = new Questionnaire.QuestionnaireItemComponent();
    item.setLinkId("valueString");
    item.setRequired(true);
    item.setText("text");
    item.addExtension(
        new Extension("http://hl7.org/fhir/StructureDefinition/questionnaire-referenceResource")
            .setValue(new CanonicalType("unmappedResourceName")));
    FieldGroup parent = FieldGroup.builder().build();
    when(resourceProcessor.getItemProcessor(item)).thenReturn(Optional.empty());

    // when
    final FieldGroup[] fieldGroups = referenceProcessor.createFieldGroup(item, parent, null);

    // then
    assertThat(fieldGroups).hasSize(1);
    FieldGroup fieldGroup = fieldGroups[0];
    assertThat(fieldGroup.getKey())
        .as("ID on unmapped resource reference")
        .isEqualTo("valueReference");
    assertThat(fieldGroup.getType()).isEqualTo("input");
    assertThat(fieldGroup.getParent()).isEqualTo(parent);
    assertThat(fieldGroup.getProps().getRequired()).isTrue();
    assertThat(fieldGroup.getProps().getLabel()).isEqualTo("text");
    assertThat(fieldGroup.getClassName()).isEqualTo("LinkId_valueString");
    verify(enableWhenProcessor).createEnableWhens(item, fieldGroup);
    verify(enableWhenProcessor, Mockito.never()).incrementIntersectingEnableWhens(Mockito.any());
    verify(tooltipExtension).getStringValueOrNull(Mockito.any());
  }
}
