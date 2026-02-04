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

import static org.assertj.core.api.Assertions.assertThat;

import org.hl7.fhir.r4.model.Coding;
import org.junit.jupiter.api.Test;

class UseTest {
  @Test
  void shouldCreateUseFromCoding() {
    var coding = new Coding();
    coding.setSystem("http://example.com/system");
    coding.setCode("example-code");

    var use = Use.toUse(coding);

    assertThat(use.system()).isEqualTo("http://example.com/system");
    assertThat(use.code()).isEqualTo("example-code");
  }

  @Test
  void shouldHandleNullCoding() {
    assertThat(Use.toUse(null)).isNull();
  }
}
