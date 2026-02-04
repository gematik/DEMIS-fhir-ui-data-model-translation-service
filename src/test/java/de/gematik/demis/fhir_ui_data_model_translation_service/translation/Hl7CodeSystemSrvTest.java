package de.gematik.demis.fhir_ui_data_model_translation_service.translation;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class Hl7CodeSystemSrvTest {

  public static final String VALUE_SET_URL =
      "http://terminology.hl7.org/ValueSet/condition-ver-status";
  private static final String CODE_SYSTEM_URL =
      "http://terminology.hl7.org/CodeSystem/condition-ver-status";
  private final FhirContext fhirContext = FhirContext.forR4Cached();

  private Hl7CodeSystemSrv hl7CodeSystemSrv;

  @DisplayName("init from file system")
  @Test
  void initFromFileSystem() throws IOException {
    hl7CodeSystemSrv =
        new Hl7CodeSystemSrv("src/main/resources/fhir-profile-snapshots/r4", fhirContext);
    verify();
  }

  @DisplayName("init from classpath")
  @Test
  void initFromClasspath() throws IOException {
    hl7CodeSystemSrv = new Hl7CodeSystemSrv("classpath:/fhir-profile-snapshots/r4", fhirContext);
    verify();
  }

  private void verify() throws IOException {
    hl7CodeSystemSrv.init();
    assertThat(hl7CodeSystemSrv.containsContent(VALUE_SET_URL)).isFalse();
    verifyCodeSystem();
  }

  private void verifyCodeSystem() throws IOException {
    assertThat(hl7CodeSystemSrv.containsContent(CODE_SYSTEM_URL)).as(CODE_SYSTEM_URL).isTrue();
    CodeDisplayMapWithVersion fileContent = hl7CodeSystemSrv.getFileContent(CODE_SYSTEM_URL);
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

  @DisplayName("path is null")
  @Test
  void givenNullPathWhenContainsContentThenThrowException() {
    hl7CodeSystemSrv = new Hl7CodeSystemSrv(null, fhirContext);
    assertThatException().isThrownBy(hl7CodeSystemSrv::init);
  }
}
