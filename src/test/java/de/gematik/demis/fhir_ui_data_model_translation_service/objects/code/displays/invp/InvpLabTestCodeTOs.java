package de.gematik.demis.fhir_ui_data_model_translation_service.objects.code.displays.invp;

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

public class InvpLabTestCodeTOs {

  public CodeDisplay loinc_101424_0() {
    return CodeDisplay.builder()
        .code("101424-0")
        .display(
            "Influenza virus A H3 RNA [Presence] in Respiratory specimen by NAA with probe detection")
        .build();
  }

  public CodeDisplay loinc_100343_3() {
    return CodeDisplay.builder()
        .code("100343-3")
        .display(
            "Influenza virus B RNA [Presence] in Saliva (oral fluid) by NAA with probe detection")
        .designations(
            Set.of(
                new Designation(
                    "de-DE",
                    "Influenza-Virus B-RNA [Nachweis] in Speichel mit Nukleinsäureamplifikation mit Sondendetektion")))
        .build();
  }
}
