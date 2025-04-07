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

import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.gematik.demis.fhir_ui_data_model_translation_service.exception.DataNotFoundExcp;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.Designation;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CodeSystemCtr.class)
class CodeSystemCtrTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private DataLoaderSrv dataLoaderSrv;

  @Test
  void shouldReturDisplayValueForOneCode() throws Exception {
    String code = "ordinary";
    String system = "addressUse";

    CodeDisplay expectedCodeDisplay =
        CodeDisplay.builder()
            .code(code)
            .display("Gewöhnlicher Aufenthaltsort")
            .designations(
                Set.of(new Designation("de", "Gewöhnlicher Aufenthaltsort mit weiterem Texte")))
            .build();

    when(dataLoaderSrv.getCodeSystemData(system, code)).thenReturn(expectedCodeDisplay);

    mockMvc
        .perform(
            get("/CodeSystem")
                .param("system", system)
                .param("code", code)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                                                    {
                                                      "code": "ordinary",
                                                      "display": "Gewöhnlicher Aufenthaltsort"
                                                    }"""));
  }

  @Test
  void shouldReturDisplayValueForOneCodePathParam() throws Exception {
    String code = "ordinary";
    String system = "addressUse";

    CodeDisplay expectedCodeDisplay =
        CodeDisplay.builder()
            .code(code)
            .display("Gewöhnlicher Aufenthaltsort")
            .designations(
                Set.of(new Designation("de", "Gewöhnlicher Aufenthaltsort mit weiterem Texte")))
            .build();

    when(dataLoaderSrv.getCodeSystemData(system, code)).thenReturn(expectedCodeDisplay);

    mockMvc
        .perform(get("/CodeSystem/addressUse/ordinary").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                                                                    {
                                                                      "code": "ordinary",
                                                                      "display": "Gewöhnlicher Aufenthaltsort"
                                                                    }"""));
  }

  @Test
  void shouldReturnListOfAllCodesWithDisplay() throws Exception {
    List<CodeDisplay> codeDisplayList =
        asList(
            CodeDisplay.builder().code("ordinary").display("Gewöhnlicher Aufenthaltsort").build(),
            CodeDisplay.builder().code("primary").display("Hauptwohnsitz").build());

    String fileName = "addressUse";
    when(dataLoaderSrv.getCodeSystemData(fileName)).thenReturn(codeDisplayList);

    mockMvc
        .perform(get("/CodeSystem").param("system", fileName).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                                                    [
                                                    {
                                                      "code": "ordinary",
                                                      "display": "Gewöhnlicher Aufenthaltsort"
                                                    },{
                                                      "code": "primary",
                                                      "display": "Hauptwohnsitz"
                                                    }
                                                    ]"""));
  }

  @Test
  void shouldReturnListOfAllCodesWithDisplayPathParam() throws Exception {
    List<CodeDisplay> codeDisplayList =
        asList(
            CodeDisplay.builder().code("ordinary").display("Gewöhnlicher Aufenthaltsort").build(),
            CodeDisplay.builder().code("primary").display("Hauptwohnsitz").build());

    String fileName = "addressUse";
    when(dataLoaderSrv.getCodeSystemData(fileName)).thenReturn(codeDisplayList);

    mockMvc
        .perform(get("/CodeSystem/addressUse").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                                                                    [
                                                                    {
                                                                      "code": "ordinary",
                                                                      "display": "Gewöhnlicher Aufenthaltsort"
                                                                    },{
                                                                      "code": "primary",
                                                                      "display": "Hauptwohnsitz"
                                                                    }
                                                                    ]"""));
  }

  @Test
  void shouldReturnListOfAllRessourceNames() throws Exception {
    List<String> fileList = asList("addressUse", "country");

    when(dataLoaderSrv.getCodeSystems()).thenReturn(fileList);

    mockMvc
        .perform(get("/CodeSystem").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                                                    [
                                                    "addressUse",
                                                    "country"
                                                    ]"""));
  }

  @Test
  void testHandleCodeNotFoundException() throws Exception {
    String code = "code";
    String system = "system";
    when(dataLoaderSrv.getCodeSystemData(system, code))
        .thenThrow(new DataNotFoundExcp("the code code was not found in system"));

    mockMvc
        .perform(get("/CodeSystem").param("code", code).param("system", system))
        .andExpect(status().isNotImplemented())
        .andExpect(content().string("the code code was not found in system"));
  }

  @Test
  void testHandleNoDtaFoundException() throws Exception {
    String system = "system";
    when(dataLoaderSrv.getCodeSystemData(system)).thenThrow(new DataNotFoundExcp("system1"));

    mockMvc
        .perform(get("/CodeSystem").param("system", system))
        .andExpect(status().isNotImplemented())
        .andExpect(content().string("system1"));
  }
}
