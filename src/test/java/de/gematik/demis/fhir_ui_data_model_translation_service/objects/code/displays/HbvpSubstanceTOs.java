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

public class HbvpSubstanceTOs {

  public CodeDisplay snomed_22290004() {
    return CodeDisplay.builder()
        .code("22290004")
        .display("Antigen of Hepatitis B virus surface protein (substance)")
        .designations(Set.of(new Designation("de-DE", "HBs-Antigen")))
        .order(100)
        .build();
  }

  public CodeDisplay snomed_60605004() {
    return CodeDisplay.builder()
        .code("60605004")
        .display("Antigen of Hepatitis B virus e protein (substance)")
        .designations(Set.of(new Designation("de-DE", "HBe-Antigen")))
        .order(100)
        .build();
  }

  public CodeDisplay snomed_39082004() {
    return CodeDisplay.builder()
        .code("39082004")
        .display("Antigen of Hepatitis B virus core protein (substance)")
        .designations(Set.of(new Designation("de-DE", "HBc-Antigen")))
        .order(100)
        .build();
  }
}
