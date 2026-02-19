package de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.resources;

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
import static org.mockito.Mockito.verifyNoInteractions;

import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.ItemProcessor;
import java.util.Optional;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.Questionnaire;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResourceProcessorTest {

  @Mock private ImmunizationProcessor immunizationProcessor;
  @Mock private HospitalizationProcessor hospitalizationProcessor;
  @Mock private DefaultOrganizationProcessor defaultOrganizationProcessor;
  @Mock private LaboratoryFacilityOrganizationProcessor laboratoryFacilityOrganizationProcessor;

  @Mock
  private InfectProtectFacilityOrganizationProcessor infectProtectFacilityOrganizationProcessor;

  @InjectMocks private ResourceProcessor resourceProcessor;

  @Test
  @DisplayName("empty array on unmapped profile reference")
  void givenUnmappedProfileReferenceWhenGetItemProcessorThenReturnEmptyArrayProcessor() {

    // given
    final Questionnaire.QuestionnaireItemComponent item =
        new Questionnaire.QuestionnaireItemComponent();
    item.addExtension(
        ResourceType.REFERENCE_PROFILE,
        new CanonicalType(
            "https://demis.rki.de/fhir/StructureDefinition/UnmappedProfileReference"));

    // when
    final Optional<ItemProcessor> actualProcessor = resourceProcessor.getItemProcessor(item);

    // then
    assertThat(actualProcessor).isPresent();
    final FieldGroup[] actualResult =
        actualProcessor.get().createFieldGroup(item, FieldGroup.builder().build(), "cvdd");
    assertThat(actualResult).isNotNull().isEmpty();

    verifyNoInteractions(immunizationProcessor);
    verifyNoInteractions(hospitalizationProcessor);
    verifyNoInteractions(defaultOrganizationProcessor);
    verifyNoInteractions(laboratoryFacilityOrganizationProcessor);
    verifyNoInteractions(infectProtectFacilityOrganizationProcessor);
  }
}
