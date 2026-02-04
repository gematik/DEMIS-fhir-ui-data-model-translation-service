package de.gematik.demis.fhir_ui_data_model_translation_service.model;

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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Set;
import java.util.stream.Collectors;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.ValueSet;

/**
 * The designation contains the values for the German translation of code display pairs from
 * DEMIS-FHIR info model
 *
 * @param language language code (e.g. "de-DE")
 * @param value translated value
 * @param use the use of the designation
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Designation(String language, String value, Use use) {

  public Designation(String language, String value) {
    this(language, value, null);
  }

  public static Set<Designation> getDesignations(
      CodeSystem.ConceptDefinitionComponent stringConceptDefinitionComponentMap) {
    return stringConceptDefinitionComponentMap.getDesignation().stream()
        .map(Designation::toDesignation)
        .collect(Collectors.toSet());
  }

  public static Set<Designation> getDesignations(
      ValueSet.ConceptReferenceComponent conceptSetComponent) {
    return conceptSetComponent.getDesignation().stream()
        .map(Designation::toDesignation)
        .collect(Collectors.toSet());
  }

  private static Designation toDesignation(
      CodeSystem.ConceptDefinitionDesignationComponent designation) {
    if (designation.getUse() != null
        && designation.getUse().getCode() != null
        && designation.getUse().getSystem() != null) {
      return new Designation(
          designation.getLanguage(), designation.getValue(), Use.toUse(designation.getUse()));
    }
    return new Designation(designation.getLanguage(), designation.getValue());
  }

  private static Designation toDesignation(
      ValueSet.ConceptReferenceDesignationComponent designation) {
    if (designation.getUse() != null
        && designation.getUse().getCode() != null
        && designation.getUse().getSystem() != null) {
      return new Designation(
          designation.getLanguage(), designation.getValue(), Use.toUse(designation.getUse()));
    }
    return new Designation(designation.getLanguage(), designation.getValue());
  }
}
