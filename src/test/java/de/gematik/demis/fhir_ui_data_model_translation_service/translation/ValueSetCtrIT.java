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

import static de.gematik.demis.fhir_ui_data_model_translation_service.utils.TestUtils.checkExpectedAndActualContentAsJsonNodes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@Slf4j
@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureObservability
@TestPropertySource(locations = "classpath:application-test.properties")
public class ValueSetCtrIT {
  private final ObjectMapper mapper = new ObjectMapper();
  @Autowired private MockMvc mockMvc;

  @Nested
  @DisplayName(
      "Should use standard value set even if version is used, either as part of the system or as a parameter")
  class OptionalVersionTestCases {
    @Test
    @DisplayName("testcase for complete value set")
    void shouldReturnSameForParameterOrSystemVersion() throws Exception {
      MvcResult result =
          mockMvc
              .perform(
                  get("/ValueSet")
                      .param(
                          "system", "https://demis.rki.de/fhir/ValueSet/answerSetAcuteHCVD|1.0.0"))
              .andExpect(status().isOk())
              .andReturn();
      String contentAsString = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
      checkExpectedAndActualContentAsJsonNodes(
          contentAsString,
          "[{\"code\":\"ASKU\",\"display\":\"asked but unknown\",\"designations\":[{\"language\":\"de\",\"value\":\"nicht ermittelbar\"}],\"system\":\"http://terminology.hl7.org/CodeSystem/v3-NullFlavor\"},{\"code\":\"NASK\",\"display\":\"not asked\",\"designations\":[{\"language\":\"de\",\"value\":\"nicht erhoben\"}],\"system\":\"http://terminology.hl7.org/CodeSystem/v3-NullFlavor\"}]",
          mapper);

      MvcResult result2 =
          mockMvc
              .perform(
                  get("/ValueSet")
                      .param("system", "https://demis.rki.de/fhir/ValueSet/answerSetAcuteHCVD")
                      .param("version", "1.0.0"))
              .andExpect(status().isOk())
              .andReturn();

      String contentAsString2 = result2.getResponse().getContentAsString(StandardCharsets.UTF_8);
      assertThat(contentAsString2).isEqualTo(contentAsString);
    }

    @Test
    @DisplayName("testcase for code")
    void shouldReturnSameCodeDataForParameterVersionOrSystemVersion() throws Exception {
      MvcResult result =
          mockMvc
              .perform(
                  get("/ValueSet")
                      .param(
                          "system", "https://demis.rki.de/fhir/ValueSet/answerSetAcuteHCVD|1.0.0")
                      .param("code", "ASKU"))
              .andExpect(status().isOk())
              .andReturn();
      String contentAsString = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
      checkExpectedAndActualContentAsJsonNodes(
          contentAsString,
          "{\"code\":\"ASKU\",\"display\":\"asked but unknown\",\"designations\":[{\"language\":\"de\",\"value\":\"nicht ermittelbar\"}],\"system\":\"http://terminology.hl7.org/CodeSystem/v3-NullFlavor\"}",
          mapper);

      MvcResult result2 =
          mockMvc
              .perform(
                  get("/ValueSet")
                      .param("system", "https://demis.rki.de/fhir/ValueSet/answerSetAcuteHCVD")
                      .param("code", "ASKU")
                      .param("version", "1.0.0"))
              .andExpect(status().isOk())
              .andReturn();

      String contentAsString2 = result2.getResponse().getContentAsString(StandardCharsets.UTF_8);
      assertThat(contentAsString2).isEqualTo(contentAsString);
    }
  }
}
