package de.gematik.demis.fhir_ui_data_model_translation_service.objects.code.displays;

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

import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.Designation;
import java.util.Set;

public class AddressUseTOs {

  public static CodeDisplay current() {
    return CodeDisplay.builder()
        .code("current")
        .display("Derzeitiger Aufenthaltsort")
        .designations(Set.of(new Designation("en-US", "Current Residence")))
        .system("https://demis.rki.de/fhir/CodeSystem/addressUse")
        .order(100)
        .build();
  }

  public static CodeDisplay ordinary() {
    return CodeDisplay.builder()
        .code("ordinary")
        .display("Gewöhnlicher Aufenthaltsort")
        .designations(Set.of(new Designation("en-US", "Ordinary Residence")))
        .system("https://demis.rki.de/fhir/CodeSystem/addressUse")
        .order(100)
        .build();
  }

  public static CodeDisplay primary() {
    return CodeDisplay.builder()
        .code("primary")
        .display("Hauptwohnsitz")
        .designations(Set.of(new Designation("en-US", "Primary Residence")))
        .system("https://demis.rki.de/fhir/CodeSystem/addressUse")
        .order(100)
        .build();
  }
}
