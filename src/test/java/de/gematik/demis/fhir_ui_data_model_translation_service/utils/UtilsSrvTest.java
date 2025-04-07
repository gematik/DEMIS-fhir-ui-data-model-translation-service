package de.gematik.demis.fhir_ui_data_model_translation_service.utils;

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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.Designation;
import de.gematik.demis.fhir_ui_data_model_translation_service.translation.DataLoaderSrv;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UtilsSrvTest {

  private final DataLoaderSrv dataLoaderSrvMock = mock(DataLoaderSrv.class);

  @Test
  void testGetCountryCodes() {
    CodeDisplay codeDisplay1 =
        CodeDisplay.builder()
            .code("DE")
            .display("Germany")
            .designations(Collections.singleton(new Designation("de-DE", "Deutschland")))
            .build();
    CodeDisplay codeDisplay2 =
        CodeDisplay.builder()
            .code("FR")
            .display("France")
            .designations(Collections.singleton(new Designation("de-DE", "Frankreich")))
            .build();
    CodeDisplay codeDisplay3 =
        CodeDisplay.builder()
            .code("US")
            .display("United States")
            .designations(Collections.singleton(new Designation("de-DE", "Vereinigte Staaten")))
            .build();

    List<CodeDisplay> countrys = List.of(codeDisplay3, codeDisplay2, codeDisplay1);

    when(dataLoaderSrvMock.getCodeSystemData("data.country.code.system.url")).thenReturn(countrys);

    UtilsSrv utilsSrv = new UtilsSrv(dataLoaderSrvMock, "data.country.code.system.url");
    utilsSrv.init();
    assertThat(utilsSrv.getCountryCodes()).isNotNull();
    assertThat(utilsSrv.getCountryCodes())
        .containsExactly(codeDisplay1, codeDisplay2, codeDisplay3);
  }

  @Test
  void testGetCountryCodesOnlyTwoCharacterCodes() {
    CodeDisplay codeDisplay1 =
        CodeDisplay.builder()
            .code("DE")
            .display("Germany")
            .designations(Collections.singleton(new Designation("de-DE", "Deutschland")))
            .build();
    CodeDisplay codeDisplay2 =
        CodeDisplay.builder()
            .code("FRA")
            .display("France")
            .designations(Collections.singleton(new Designation("de-DE", "Frankreich")))
            .build();
    CodeDisplay codeDisplay3 =
        CodeDisplay.builder()
            .code("US")
            .display("United States")
            .designations(Collections.singleton(new Designation("de-DE", "Vereinigte Staaten")))
            .build();

    List<CodeDisplay> countrys = List.of(codeDisplay3, codeDisplay2, codeDisplay1);

    when(dataLoaderSrvMock.getCodeSystemData("data.country.code.system.url")).thenReturn(countrys);

    UtilsSrv utilsSrv = new UtilsSrv(dataLoaderSrvMock, "data.country.code.system.url");
    utilsSrv.init();
    assertThat(utilsSrv.getCountryCodes()).isNotNull();
    assertThat(utilsSrv.getCountryCodes()).containsExactly(codeDisplay1, codeDisplay3);
  }

  @Test
  void testNoDataHandledGracefully() {
    when(dataLoaderSrvMock.getCodeSystemData("data.country.code.system.url"))
        .thenReturn(Collections.emptyList());

    UtilsSrv utilsSrv = new UtilsSrv(dataLoaderSrvMock, "data.country.code.system.url");
    utilsSrv.init();
    assertThat(utilsSrv.getCountryCodes()).isNotNull().isEmpty();
  }
}
