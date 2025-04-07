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
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ValueSetCtr.class)
class ValueSetCtrTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private DataLoaderSrv dataLoaderSrv;

  @Test
  void shouldReturDisplayValueForOneCode() throws Exception {
    String code = "6309-9";
    String system = "laboratoryTestABVP";
    CodeDisplay expectedCodeDisplay =
        CodeDisplay.builder()
            .code(code)
            .display("Arbovirus identified in Blood by Organism specific culture")
            .build();

    when(dataLoaderSrv.getValueSetData(system, "6309-9")).thenReturn(expectedCodeDisplay);

    mockMvc
        .perform(
            get("/ValueSet")
                .param("system", system)
                .param("code", code)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                                                    {
                                                      "code": "6309-9",
                                                      "display": "Arbovirus identified in Blood by Organism specific culture"
                                                    }"""));
  }

  @Test
  void shouldReturDisplayValueForOneCodePathVariable() throws Exception {
    String code = "6309-9";
    String system = "laboratoryTestABVP";
    CodeDisplay expectedCodeDisplay =
        CodeDisplay.builder()
            .code(code)
            .display("Arbovirus identified in Blood by Organism specific culture")
            .build();

    when(dataLoaderSrv.getValueSetData(system, "6309-9")).thenReturn(expectedCodeDisplay);

    mockMvc
        .perform(get("/ValueSet/laboratoryTestABVP/6309-9").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                                                                    {
                                                                      "code": "6309-9",
                                                                      "display": "Arbovirus identified in Blood by Organism specific culture"
                                                                    }"""));
  }

  @Test
  void shouldReturnListOfAllCodesWithDisplay() throws Exception {
    String system = "laboratoryTestABVP";
    List<CodeDisplay> expectedCodeDisplayList =
        asList(
            CodeDisplay.builder()
                .code("6309-9")
                .display("Arbovirus identified in Blood by Organism specific culture")
                .build(),
            CodeDisplay.builder()
                .code("74031-6")
                .display("Arbovirus identified in Serum by Immunofluorescence")
                .build());

    when(dataLoaderSrv.getValueSetData(system)).thenReturn(expectedCodeDisplayList);

    mockMvc
        .perform(get("/ValueSet").param("system", system).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                                                    [
                                                    {
                                                      "code": "6309-9",
                                                      "display": "Arbovirus identified in Blood by Organism specific culture"
                                                    },{
                                                      "code": "74031-6",
                                                      "display": "Arbovirus identified in Serum by Immunofluorescence"
                                                    }
                                                    ]"""));
  }

  @Test
  void shouldReturnListOfAllCodesWithDisplayPathVariable() throws Exception {
    String system = "laboratoryTestABVP";
    List<CodeDisplay> expectedCodeDisplayList =
        asList(
            CodeDisplay.builder()
                .code("6309-9")
                .display("Arbovirus identified in Blood by Organism specific culture")
                .build(),
            CodeDisplay.builder()
                .code("74031-6")
                .display("Arbovirus identified in Serum by Immunofluorescence")
                .build());

    when(dataLoaderSrv.getValueSetData(system)).thenReturn(expectedCodeDisplayList);

    mockMvc
        .perform(get("/ValueSet/laboratoryTestABVP").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                                                                    [
                                                                    {
                                                                      "code": "6309-9",
                                                                      "display": "Arbovirus identified in Blood by Organism specific culture"
                                                                    },{
                                                                      "code": "74031-6",
                                                                      "display": "Arbovirus identified in Serum by Immunofluorescence"
                                                                    }
                                                                    ]"""));
  }

  @Test
  void shouldReturnListOfAllRessourceNames() throws Exception {
    List<String> fileNameList = asList("laboratoryTestABVP", "laboratoryTest");

    when(dataLoaderSrv.getValueSet()).thenReturn(fileNameList);

    mockMvc
        .perform(get("/ValueSet").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                                                    [
                                                    "laboratoryTestABVP",
                                                    "laboratoryTest"
                                                    ]"""));
  }

  @Test
  void testHandleCodeNotFoundException() throws Exception {
    String code = "code";
    String system = "system";
    when(dataLoaderSrv.getValueSetData(system, code))
        .thenThrow(new DataNotFoundExcp("the code code was not found in system"));

    mockMvc
        .perform(get("/ValueSet").param("code", code).param("system", system))
        .andExpect(status().isNotImplemented())
        .andExpect(content().string("the code code was not found in system"));
  }

  @Test
  void testHandleNoDtaFoundException() throws Exception {
    String system = "system";
    when(dataLoaderSrv.getValueSetData(system)).thenThrow(new DataNotFoundExcp("system1"));

    mockMvc
        .perform(get("/ValueSet").param("system", system))
        .andExpect(status().isNotImplemented())
        .andExpect(content().string("system1"));
  }
}
