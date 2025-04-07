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

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class Hl7CodeSystemSrvTest {

  private final FhirContext fhirContext = FhirContext.forR4Cached();

  private final String hl7DataPath = "src/test/resources/HL7/terminology/r4";

  private Hl7CodeSystemSrv hl7CodeSystemSrv;

  @BeforeEach
  void setUp() {
    hl7CodeSystemSrv = new Hl7CodeSystemSrv(hl7DataPath, fhirContext);
  }

  @DisplayName("initialization test")
  @Test
  void shouldInitializeService() throws IOException {
    hl7CodeSystemSrv.init();

    assertThat(
            hl7CodeSystemSrv.containsContent(
                "http://terminology.hl7.org/CodeSystem/condition-ver-status"))
        .isTrue();
    assertThat(
            hl7CodeSystemSrv.containsContent(
                "http://terminology.hl7.org/ValueSet/condition-ver-status"))
        .isFalse();

    CodeDisplayMapWithVersion fileContent =
        hl7CodeSystemSrv.getFileContent(
            "http://terminology.hl7.org/CodeSystem/condition-ver-status");
    assertThat(fileContent).isNotNull();
    assertThat(fileContent.codeDisplayMap())
        .hasSize(6)
        .containsEntry(
            "unconfirmed", CodeDisplay.builder().code("unconfirmed").display("Unconfirmed").build())
        .containsEntry(
            "provisional", CodeDisplay.builder().code("provisional").display("Provisional").build())
        .containsEntry(
            "differential",
            CodeDisplay.builder().code("differential").display("Differential").build())
        .containsEntry(
            "confirmed", CodeDisplay.builder().code("confirmed").display("Confirmed").build())
        .containsEntry("refuted", CodeDisplay.builder().code("refuted").display("Refuted").build())
        .containsEntry(
            "entered-in-error",
            CodeDisplay.builder().code("entered-in-error").display("Entered in Error").build());
  }
}
