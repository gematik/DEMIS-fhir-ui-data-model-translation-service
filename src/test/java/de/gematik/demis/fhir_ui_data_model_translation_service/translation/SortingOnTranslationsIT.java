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
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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
class SortingOnTranslationsIT {

  private final ObjectMapper mapper = new ObjectMapper();
  @Autowired private MockMvc mockMvc;

  @Test
  void shouldReturnListOfValueSetsSorted() throws Exception {
    MvcResult result = mockMvc.perform(get("/ValueSet")).andExpect(status().isOk()).andReturn();
    String contentAsString = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
    JsonNode actualContent = mapper.readTree(contentAsString);
    List<String> urls = mapper.convertValue(actualContent, List.class);
    assertThat(urls).isSortedAccordingTo(String::compareTo);
  }

  @Test
  void shouldReturnListOfCodeSystemsSorted() throws Exception {
    MvcResult result = mockMvc.perform(get("/CodeSystem")).andExpect(status().isOk()).andReturn();
    String contentAsString = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

    JsonNode actualContent = mapper.readTree(contentAsString);
    List<String> urls = mapper.convertValue(actualContent, List.class);
    assertThat(urls).isSortedAccordingTo(String::compareTo);
  }
}
