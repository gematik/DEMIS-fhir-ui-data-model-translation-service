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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.Designation;
import java.util.Set;

public class InvpAnswerSetCodeTOs {

  public CodeDisplay loinc_407479009() {
    return CodeDisplay.builder()
        .code("407479009")
        .display("Influenza A virus (organism)")
        .designations(
            Set.of(
                new Designation("en-US", "Influenza A virus (organism)"),
                new Designation("de-DE", "Influenza A-Virus")))
        .order(50)
        .build();
  }

  public CodeDisplay loinc_715350001() {
    return CodeDisplay.builder()
        .code("715350001")
        .display("Influenza A virus subtype H10N7 (organism)")
        .designations(
            Set.of(
                new Designation("en-US", "Influenza A virus subtype H10N7 (organism)"),
                new Designation("de-DE", "Influenza A-Virus, Subtyp H10N7")))
        .order(10)
        .build();
  }

  public CodeDisplay loinc_715350001R() {
    return CodeDisplay.builder()
        .code("715350001")
        .display("Influenza A virus subtype H10N7 (organism)")
        .designations(
            Set.of(
                new Designation("en-US", "Influenza A virus subtype H10N7 (organism)"),
                new Designation("de-DE", "Influenza A-Virus, Subtyp H10N7")))
        .order(100)
        .build();
  }

  public CodeDisplay loinc_442352004() {
    return CodeDisplay.builder()
        .code("442352004")
        .display("Influenza A virus subtype H1N1 (organism)")
        .designations(
            Set.of(
                new Designation("en-US", "Influenza A virus subtype H1N1 (organism)"),
                new Designation("de-DE", "Influenza A-Virus (H1N1)")))
        .order(99)
        .build();
  }

  public CodeDisplay loinc_700350009() {
    return CodeDisplay.builder()
        .code("700350009")
        .display("Influenza A virus subtype H10N8 (organism)")
        .designations(
            Set.of(
                new Designation("en-US", "Influenza A virus subtype H10N8 (organism)"),
                new Designation("de-DE", "Influenza A-Virus, Subtyp H10N8")))
        .order(100)
        .build();
  }
}
