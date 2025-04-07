package de.gematik.demis.fhir_ui_data_model_translation_service.laboratory;

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

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import de.gematik.demis.fhir_ui_data_model_translation_service.exception.DataNotFoundExcp;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.objects.TestObjects;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LaboratoryDataLoaderSrvTest {

  @Mock private LabDataPreparationSrv labDataPreparationSrvMock;
  @Mock private FederalStateNotificationCategoryData federalStateNotificationCategoryDataMock;
  private LaboratoryDataLoaderSrv laboratoryDataLoaderSrv;

  @Test
  void shouldThrowExceptionIfNoDataForFederalPathogenCode() {
    when(labDataPreparationSrvMock.getLaboratoryDataMap()).thenReturn(Collections.emptyMap());

    initTestObject();

    assertThatThrownBy(() -> laboratoryDataLoaderSrv.getDataForFederalPathogenCode("code"))
        .isInstanceOf(DataNotFoundExcp.class)
        .hasMessageContaining("The code code was not found in the stored data");
  }

  private void initTestObject() {
    this.laboratoryDataLoaderSrv =
        new LaboratoryDataLoaderSrv(
            this.labDataPreparationSrvMock, this.federalStateNotificationCategoryDataMock);
  }

  @Test
  void shouldThrowExceptionIfNotDataForFederalState() {
    Map<String, List<CodeDisplay>> map = new HashMap<>();
    map.put("otherCode", singletonList(TestObjects.codeDisplay().code1()));
    when(federalStateNotificationCategoryDataMock.getFederalStateNotificationCategories())
        .thenReturn(map);

    initTestObject();

    assertThatThrownBy(() -> laboratoryDataLoaderSrv.getDataForPathogenCodesForFederalState("code"))
        .isInstanceOf(DataNotFoundExcp.class)
        .hasMessageContaining("The code code was not found in the stored data");
  }

  @Test
  void should() {
    when(labDataPreparationSrvMock.getNotificationCategories(PathogenNotificationCategory.P_7_3))
        .thenReturn(emptyList());

    initTestObject();

    assertThatThrownBy(
            () ->
                laboratoryDataLoaderSrv.getAvailableNotificationCategories(
                    PathogenNotificationCategory.P_7_3))
        .isInstanceOf(DataNotFoundExcp.class)
        .hasMessageContaining("No data found for code 7.3");
  }
}
