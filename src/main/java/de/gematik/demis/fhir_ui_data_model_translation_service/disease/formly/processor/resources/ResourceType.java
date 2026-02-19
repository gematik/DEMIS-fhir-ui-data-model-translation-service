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

import de.gematik.demis.notification.builder.demis.fhir.notification.utils.DemisConstants;
import java.util.Optional;
import org.hl7.fhir.r4.model.*;

/**
 * To be correct: Types of FHIR resources referenced by <b>questionnaire items</b> using either a
 * resource reference or a profile reference.
 */
enum ResourceType {

  /**
   * Immunization information matches with individual diseases. Example profile reference value:
   * <code>https://demis.rki.de/fhir/StructureDefinition/ImmunizationInformationCVDD</code>
   */
  IMMUNIZATION("https://demis.rki.de/fhir/StructureDefinition/ImmunizationInformation", false),

  /**
   * The profile reference value: <code>
   * https://demis.rki.de/fhir/StructureDefinition/Hospitalization</code>
   */
  HOSPITALIZATION(DemisConstants.PROFILE_HOSPITALIZATION, true),

  /** The resource reference value: <code>Organization</code> */
  ORGANIZATION("Organization", true),

  /**
   * The profile reference value: <code>
   * https://demis.rki.de/fhir/StructureDefinition/LaboratoryFacility</code>
   */
  LABORATORY_FACILITY(DemisConstants.PROFILE_LABORATORY_FACILITY, true),

  /**
   * The profile reference value: <code>
   * https://demis.rki.de/fhir/StructureDefinition/InfectProtectFacility</code>
   */
  INFECT_PROTECT_FACILITY(DemisConstants.PROFILE_INFECT_PROTECT_FACILITY, true);

  static final String REFERENCE_PROFILE =
      "http://hl7.org/fhir/StructureDefinition/questionnaire-referenceProfile";
  static final String REFERENCE_RESOURCE =
      "http://hl7.org/fhir/StructureDefinition/questionnaire-referenceResource";

  private final String marker;
  private final boolean exactMarkerMatch;

  ResourceType(String marker, boolean exactMarkerMatch) {
    this.marker = marker;
    this.exactMarkerMatch = exactMarkerMatch;
  }

  private static String extractReferenceType(Extension extension) {
    Type value = extension.getValue();
    if (value instanceof CodeType codeType) {
      return codeType.getValue();
    } else {
      // CanonicalType
      return ((CanonicalType) value).getValue();
    }
  }

  private static String extractReferenceType(Questionnaire.QuestionnaireItemComponent item) {
    Extension extension;
    // First check for referenceProfile
    extension = item.getExtensionByUrl(REFERENCE_PROFILE);
    if (extension != null) {
      return extractReferenceType(extension);
    }
    // Then check for referenceResource
    extension = item.getExtensionByUrl(REFERENCE_RESOURCE);
    if (extension != null) {
      return extractReferenceType(extension);
    }
    return null;
  }

  static Optional<ResourceType> fromItem(Questionnaire.QuestionnaireItemComponent item) {
    final String referenceType = extractReferenceType(item);
    for (ResourceType type : values()) {
      if (type.matchesReferenceType(referenceType)) {
        return Optional.of(type);
      }
    }
    return Optional.empty();
  }

  private boolean matchesReferenceType(String referenceType) {
    if ((referenceType == null) || referenceType.isEmpty()) {
      return false;
    }
    if (this.exactMarkerMatch) {
      return referenceType.equals(this.marker);
    }
    return referenceType.contains(this.marker);
  }
}
