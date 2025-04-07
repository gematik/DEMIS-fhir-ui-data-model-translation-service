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
class CodeSystemsCtrIT {

  public static final String CODE_DISPLAY =
      "{\"code\":\"100343-3\",\"display\":\"Influenza virus B RNA [Presence] in Saliva (oral fluid) by NAA with probe detection\",\"designations\":[{\"language\":\"de-DE\",\"value\":\"Influenza-Virus B-RNA [Nachweis] in Speichel mit Nukleinsäureamplifikation mit Sondendetektion\"}],\"system\":\"http://loinc.org\"}";

  public static final String CODE_DISPLAY_2 =
      "{\"code\":\"provisional\",\"display\":\"Provisional\",\"designations\":[]}";
  private final ObjectMapper mapper = new ObjectMapper();
  @Autowired private MockMvc mockMvc;

  @Test
  @DisplayName("Should copy designation from value set to code system")
  void shouldCreateCodeSystems() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                get("/CodeSystem").param("system", "http://loinc.org").param("code", "100343-3"))
            .andExpect(status().isOk())
            .andReturn();
    String contentAsString = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
    checkExpectedAndActualContentAsJsonNodes(contentAsString, CODE_DISPLAY, mapper);
  }

  @Test
  @DisplayName("Should copy designation from value set to code system")
  void shouldAddDesignationsFrom() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                get("/CodeSystem")
                    .param("system", "http://snomed.info/sct")
                    .param("code", "258607008"))
            .andExpect(status().isOk())
            .andReturn();
    String contentAsString = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
    assertThat(contentAsString).contains("Bronchoalveoläre Lavage");
  }

  @Test
  @DisplayName("Should use standard code systems")
  void shouldUseStandardCodeSystems() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                get("/CodeSystem")
                    .param("system", "http://terminology.hl7.org/CodeSystem/condition-ver-status")
                    .param("code", "provisional"))
            .andExpect(status().isOk())
            .andReturn();
    String contentAsString = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
    checkExpectedAndActualContentAsJsonNodes(contentAsString, CODE_DISPLAY_2, mapper);
  }

  @Nested
  @DisplayName(
      "Should use standard value set even if version is used, either as part of the system or as a parameter")
  class OptionalVersion {

    @Test
    @DisplayName("testcase with code")
    void shouldReturnSameDataForCodeWithVersionAsParamOrAsPartOfSystem() throws Exception {
      MvcResult result =
          mockMvc
              .perform(
                  get("/CodeSystem")
                      .param(
                          "system",
                          "http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation|3.0.0")
                      .param("code", "HH"))
              .andExpect(status().isOk())
              .andReturn();
      String contentAsString = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
      checkExpectedAndActualContentAsJsonNodes(
          contentAsString,
          "{\"code\":\"HH\",\"display\":\"Critical high\",\"designations\":[]}",
          mapper);

      MvcResult result2 =
          mockMvc
              .perform(
                  get("/CodeSystem")
                      .param(
                          "system",
                          "http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation")
                      .param("code", "HH")
                      .param("version", "3.0.0"))
              .andExpect(status().isOk())
              .andReturn();

      String contentAsString2 = result2.getResponse().getContentAsString(StandardCharsets.UTF_8);
      assertThat(contentAsString2).isEqualTo(contentAsString);
    }

    @Test
    @DisplayName("test case complete CodeSystem")
    void shouldReturnSameDataForCodeSystemWithVersionAsParamOrAsPartOfSystem() throws Exception {
      MvcResult result =
          mockMvc
              .perform(
                  get("/CodeSystem")
                      .param(
                          "system",
                          "http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation|3.0.0"))
              .andExpect(status().isOk())
              .andReturn();
      String contentAsString = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
      checkExpectedAndActualContentAsJsonNodes(
          contentAsString,
          "[{\"code\":\"HH\",\"display\":\"Critical high\",\"designations\":[]},{\"code\":\"LL\",\"display\":\"Critical low\",\"designations\":[]},{\"code\":\"HM\",\"display\":\"Hold for Medical Review\",\"designations\":[]},{\"code\":\"ObservationInterpretationDetection\",\"display\":\"ObservationInterpretationDetection\",\"designations\":[]},{\"code\":\"LU\",\"display\":\"Significantly low\",\"designations\":[]},{\"code\":\"LX\",\"display\":\"below low threshold\",\"designations\":[]},{\"code\":\"HU\",\"display\":\"Significantly high\",\"designations\":[]},{\"code\":\"HX\",\"display\":\"above high threshold\",\"designations\":[]},{\"code\":\"SDD\",\"display\":\"Susceptible-dose dependent\",\"designations\":[]},{\"code\":\"Carrier\",\"display\":\"Carrier\",\"designations\":[]},{\"code\":\"IE\",\"display\":\"Insufficient evidence\",\"designations\":[]},{\"code\":\"AA\",\"display\":\"Critical abnormal\",\"designations\":[]},{\"code\":\"AC\",\"display\":\"Anti-complementary substances present\",\"designations\":[]},{\"code\":\"MS\",\"display\":\"moderately susceptible\",\"designations\":[]},{\"code\":\"EX\",\"display\":\"outside threshold\",\"designations\":[]},{\"code\":\"CAR\",\"display\":\"Carrier\",\"designations\":[]},{\"code\":\"POS\",\"display\":\"Positive\",\"designations\":[]},{\"code\":\"NCL\",\"display\":\"No CLSI defined breakpoint\",\"designations\":[]},{\"code\":\"ND\",\"display\":\"Not detected\",\"designations\":[]},{\"code\":\"<\",\"display\":\"Off scale low\",\"designations\":[]},{\"code\":\"TOX\",\"display\":\"Cytotoxic substance present\",\"designations\":[]},{\"code\":\"VS\",\"display\":\"very susceptible\",\"designations\":[]},{\"code\":\">\",\"display\":\"Off scale high\",\"designations\":[]},{\"code\":\"IND\",\"display\":\"Indeterminate\",\"designations\":[]},{\"code\":\"RR\",\"display\":\"Reactive\",\"designations\":[]},{\"code\":\"A\",\"display\":\"Abnormal\",\"designations\":[]},{\"code\":\"B\",\"display\":\"Better\",\"designations\":[]},{\"code\":\"D\",\"display\":\"Significant change down\",\"designations\":[]},{\"code\":\"NR\",\"display\":\"Non-reactive\",\"designations\":[]},{\"code\":\"NS\",\"display\":\"Non-susceptible\",\"designations\":[]},{\"code\":\"E\",\"display\":\"Equivocal\",\"designations\":[]},{\"code\":\"H\",\"display\":\"High\",\"designations\":[]},{\"code\":\"I\",\"display\":\"Intermediate\",\"designations\":[]},{\"code\":\"L\",\"display\":\"Low\",\"designations\":[]},{\"code\":\"UNE\",\"display\":\"Unexpected\",\"designations\":[]},{\"code\":\"N\",\"display\":\"Normal\",\"designations\":[]},{\"code\":\"ObservationInterpretationExpectation\",\"display\":\"ObservationInterpretationExpectation\",\"designations\":[]},{\"code\":\"_ObservationInterpretationSusceptibility\",\"display\":\"ObservationInterpretationSusceptibility\",\"designations\":[]},{\"code\":\"R\",\"display\":\"Resistant\",\"designations\":[]},{\"code\":\"S\",\"display\":\"Susceptible\",\"designations\":[]},{\"code\":\"U\",\"display\":\"Significant change up\",\"designations\":[]},{\"code\":\"SYN-S\",\"display\":\"Synergy - susceptible\",\"designations\":[]},{\"code\":\"SYN-R\",\"display\":\"Synergy - resistant\",\"designations\":[]},{\"code\":\"W\",\"display\":\"Worse\",\"designations\":[]},{\"code\":\"_GeneticObservationInterpretation\",\"display\":\"GeneticObservationInterpretation\",\"designations\":[]},{\"code\":\"WR\",\"display\":\"Weakly reactive\",\"designations\":[]},{\"code\":\"ReactivityObservationInterpretation\",\"display\":\"ReactivityObservationInterpretation\",\"designations\":[]},{\"code\":\"_ObservationInterpretationExceptions\",\"display\":\"ObservationInterpretationExceptions\",\"designations\":[]},{\"code\":\"OBX\",\"display\":\"Interpretation qualifiers in separate OBX segments\",\"designations\":[]},{\"code\":\"_ObservationInterpretationNormality\",\"display\":\"ObservationInterpretationNormality\",\"designations\":[]},{\"code\":\"L<\",\"display\":\"Significantly low\",\"designations\":[]},{\"code\":\"NEG\",\"display\":\"Negative\",\"designations\":[]},{\"code\":\"DET\",\"display\":\"Detected\",\"designations\":[]},{\"code\":\"_ObservationInterpretationChange\",\"display\":\"ObservationInterpretationChange\",\"designations\":[]},{\"code\":\"QCF\",\"display\":\"Quality control failure\",\"designations\":[]},{\"code\":\"H>\",\"display\":\"Significantly high\",\"designations\":[]},{\"code\":\"EXP\",\"display\":\"Expected\",\"designations\":[]}]",
          mapper);

      MvcResult result2 =
          mockMvc
              .perform(
                  get("/CodeSystem")
                      .param(
                          "system",
                          "http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation")
                      .param("version", "3.0.0"))
              .andExpect(status().isOk())
              .andReturn();

      String contentAsString2 = result2.getResponse().getContentAsString(StandardCharsets.UTF_8);
      assertThat(contentAsString2).isEqualTo(contentAsString);
    }
  }
}
