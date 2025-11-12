package de.gematik.demis.fhir_ui_data_model_translation_service.translation;

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

import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class Hl7CodeSystemUrlTest {

  @Test
  void testConditionVerStatus() throws IOException {
    final String file =
        "src/main/resources/fhir-profile-snapshots/r4/CodeSystem-condition-ver-status.json";
    final String json = Utils.getFileString(file);
    final Optional<String> url = new Hl7CodeSystemUrl(json).get();
    assertThat(url.get())
        .as("root level url")
        .isEqualTo("http://terminology.hl7.org/CodeSystem/condition-ver-status");
  }

  /**
   * Test for a CodeSystem that does not have a url at the root level.
   *
   * @throws IOException if file cannot be read
   */
  @Test
  void testConditionVerStatusReturnsOptionalEmpty() throws IOException {
    final String file =
        "src/test/resources/HL7/terminology/artificial/CodeSystem-condition-ver-status.json";
    final String json = Utils.getFileString(file);
    final Optional<String> url = new Hl7CodeSystemUrl(json).get();
    assertThat(url).isEmpty();
  }
}
