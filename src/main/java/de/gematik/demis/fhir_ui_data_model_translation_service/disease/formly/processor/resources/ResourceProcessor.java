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

import de.gematik.demis.fhir_ui_data_model_translation_service.context.OnlyInDiseaseContext;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.ItemProcessor;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Questionnaire;
import org.springframework.stereotype.Component;

/** Processor for Questionnaire items containing resource references */
@OnlyInDiseaseContext
@RequiredArgsConstructor
@Component
public class ResourceProcessor {

  private final ImmunizationProcessor immunizationProcessor;
  private final HospitalizationProcessor hospitalizationProcessor;
  private final DefaultOrganizationProcessor defaultOrganizationProcessor;
  private final LaboratoryFacilityOrganizationProcessor laboratoryFacilityOrganizationProcessor;
  private final InfectProtectFacilityOrganizationProcessor
      infectProtectFacilityOrganizationProcessor;
  private final ItemProcessor unmappedProfileReferenceProcessor =
      new UnmappedProfileReferenceProcessor();

  private boolean containsProfileReference(Questionnaire.QuestionnaireItemComponent item) {
    return item.getExtensionByUrl(ResourceType.REFERENCE_PROFILE) != null;
  }

  public Optional<ItemProcessor> getItemProcessor(Questionnaire.QuestionnaireItemComponent item) {
    return ResourceType.fromItem(item)
        .map(this::getProcessorForResourceType)
        .or(
            () ->
                containsProfileReference(item)
                    ? Optional.of(unmappedProfileReferenceProcessor)
                    : Optional.empty());
  }

  private ItemProcessor getProcessorForResourceType(ResourceType resourceType) {
    return switch (resourceType) {
      case IMMUNIZATION -> this.immunizationProcessor;
      case HOSPITALIZATION -> this.hospitalizationProcessor;
      case ORGANIZATION -> this.defaultOrganizationProcessor;
      case LABORATORY_FACILITY -> this.laboratoryFacilityOrganizationProcessor;
      case INFECT_PROTECT_FACILITY -> this.infectProtectFacilityOrganizationProcessor;
    };
  }

  private static final class UnmappedProfileReferenceProcessor implements ItemProcessor {
    @Override
    public FieldGroup[] createFieldGroup(
        Questionnaire.QuestionnaireItemComponent item, FieldGroup parent, String diseaseCode) {
      return new FieldGroup[0];
    }
  }
}
