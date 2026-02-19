package de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.fhir;

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
import org.springframework.stereotype.Component;

/** Regex extension for validating values. */
@OnlyInDiseaseContext
@Component
public final class RegexExtension extends DefaultPortalFhirExtension {

  /** The URL of the regex extension. */
  public static final String URL = "http://hl7.org/fhir/StructureDefinition/regex";

  public RegexExtension() {
    super(URL);
  }
}
