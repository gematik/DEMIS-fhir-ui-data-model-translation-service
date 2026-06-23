package de.gematik.demis.fhir_ui_data_model_translation_service.disease;

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

import static de.gematik.demis.fhir_ui_data_model_translation_service.utils.TestUtils.checkExpectedAndActualContentAsJsonNodes;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.micrometer.tracing.test.autoconfigure.AutoConfigureTracing;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureTracing
@ActiveProfiles("test")
@TestPropertySource(properties = {"feature.flag.disease-questionnaire-org-input-validation=false"})
class DiseaseDataLoaderCtrIT {

  @Autowired private JsonMapper mapper;
  @Autowired private MockMvc mockMvc;

  @ParameterizedTest
  @CsvSource({
    "/disease/questionnaire/CVDD/items, src/test/resources/expectedData/QuestionCVDDResult.json",
    "/disease/questionnaire/CLOD/formly, src/test/resources/expectedData/FormlyCLODResult.json",
    "/disease/questionnaire/CVDD/formly, src/test/resources/expectedData/FormlyCVDDResult.json",
    "/disease/questionnaire/HCVD/formly, src/test/resources/expectedData/FormlyHCVDResult.json",
    "/disease/questionnaire/IZVD/formly, src/test/resources/expectedData/FormlyIZVDResult.json",
    "/disease, src/test/resources/expectedData/DiseaseNotificationCategoryList.json",
    "/disease/6.1, src/test/resources/expectedData/DiseaseNotificationCategoryList.json",
    "/disease/7.3, src/test/resources/expectedData/DiseaseNotificationCategoryList7_3.json",
    "/disease/7.3/questionnaire/toxd/formly, src/test/resources/expectedData/FormlyTOXDResult.json"
  })
  @DisplayName("test result for specific endpoints")
  void getResultsForSpecificEndpoints(String endpoint, String expectedDataPath) throws Exception {
    String expected =
        Files.readString(Paths.get(expectedDataPath))
            .replace("${LOCAL_DATE_NOW}", LocalDate.now().toString());
    MvcResult result = mockMvc.perform(get(endpoint)).andExpect(status().isOk()).andReturn();
    String actual = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
    checkExpectedAndActualContentAsJsonNodes(actual, expected, mapper);
  }

  @ParameterizedTest
  @ValueSource(strings = {"/disease/questionnaire/unknown/items"})
  @DisplayName("404 for unknown endpoints")
  void get404ForUnknownEndpoints(String endpoint) throws Exception {
    this.mockMvc.perform(get(endpoint)).andExpect(status().isBadRequest());
  }
}
