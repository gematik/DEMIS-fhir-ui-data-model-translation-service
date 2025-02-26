package de.gematik.demis.fhir_ui_data_model_translation_service.objects;

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

import static java.util.Arrays.asList;

import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.Designation;
import java.util.List;
import java.util.Set;

public class NotificationCategoryListTO {

  public static List<CodeDisplay> unsortedList() {
    return asList(third(), first(), second());
  }

  public static List<CodeDisplay> sortedList() {
    return asList(first(), second(), third());
  }

  public static CodeDisplay second() {
    return CodeDisplay.builder()
        .code("second")
        .display("secondDisplay")
        .designations(Set.of(new Designation("de-DE", "designation2")))
        .build();
  }

  public static CodeDisplay third() {
    return CodeDisplay.builder()
        .code("third")
        .display("thirdDisplay")
        .designations(Set.of(new Designation("de-DE", "designation3")))
        .build();
  }

  public static CodeDisplay first() {
    return CodeDisplay.builder()
        .code("first")
        .display("firstDisplay")
        .designations(Set.of(new Designation("de-DE", "designation1")))
        .build();
  }
}
