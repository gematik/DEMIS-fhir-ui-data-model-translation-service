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

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hl7.fhir.r4.model.Coding;

/**
 * Use of a designation.
 *
 * <h2>Example Used to translate UCUM codes into different human-readable texts (e.g., short and
 * long forms). For this translation, the designation \`use\` is encoded using SNOMED CT codes.
 *
 * <ul>
 *   <li>900000000000003001: Fully specified name
 *   <li>900000000000013009: Synonym (core metadata concept)
 * </ul>
 *
 * @param system URL of the code system that defines use codes
 * @param code code identifying the use
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Use(String system, String code) {

  public static Use toUse(Coding coding) {
    if (coding == null) {
      return null;
    }
    return new Use(coding.getSystem(), coding.getCode());
  }
}
