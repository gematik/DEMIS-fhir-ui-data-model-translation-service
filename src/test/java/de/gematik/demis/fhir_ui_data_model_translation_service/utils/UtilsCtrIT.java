package de.gematik.demis.fhir_ui_data_model_translation_service.utils;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.micrometer.tracing.test.autoconfigure.AutoConfigureTracing;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@Slf4j
@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureTracing
@ActiveProfiles("test")
class UtilsCtrIT {

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldReturnListOfCountryCodesInOrder() throws Exception {
    mockMvc
        .perform(get("/utils/countryCodes"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
            [{"code":"DE","display":"Germany","designations":[{"language":"de-DE","value":"Deutschland"}],"system":"urn:iso:std:iso:3166","version":"1.0.0"},{"code":"NZ","display":"New Zealand","designations":[{"language":"de-DE","value":"Neuseeland"}],"system":"urn:iso:std:iso:3166","version":"1.0.0"},{"code":"CH","display":"Switzerland","designations":[{"language":"de-DE","value":"Schweiz"}],"system":"urn:iso:std:iso:3166","version":"1.0.0"}]"""));
  }
}
