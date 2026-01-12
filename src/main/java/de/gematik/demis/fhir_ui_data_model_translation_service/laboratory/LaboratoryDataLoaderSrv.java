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
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import de.gematik.demis.fhir_ui_data_model_translation_service.exception.DataNotFoundExcp;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import io.micrometer.observation.annotation.Observed;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LaboratoryDataLoaderSrv {

  public static final String THE_CODE_S_WAS_NOT_FOUND_IN_THE_STORED_DATA =
      "The code %s was not found in the stored data";
  public static final String NO_DATA_FOUND_FOR_CODE = "No data found for code %s";

  private final LabDataPreparationSrv labDataPreparationSrv;
  private final FederalStateNotificationCategoryData federalStateNotificationCategoryData;
  private final FederalStateNotificationCategoryDataRegression
      federalStateNotificationCategoryDataRegression;
  private boolean isNotifications7_3active;

  public LaboratoryDataLoaderSrv(
      LabDataPreparationSrv labDataPreparationSrv,
      FederalStateNotificationCategoryData federalStateNotificationCategoryData,
      FederalStateNotificationCategoryDataRegression federalStateNotificationCategoryDataRegression,
      @Value("${feature.flag.notifications.7_3}") boolean isNotifications73active) {
    this.labDataPreparationSrv = labDataPreparationSrv;
    this.federalStateNotificationCategoryData = federalStateNotificationCategoryData;
    this.federalStateNotificationCategoryDataRegression =
        federalStateNotificationCategoryDataRegression;
    isNotifications7_3active = isNotifications73active;
  }

  private static void processNoData(String code, String errorMessage) {
    String message = String.format(errorMessage, code);
    throw new DataNotFoundExcp(message);
  }

  @Observed(
      name = "all-federal-states",
      contextualName = "all-federal-states",
      lowCardinalityKeyValues = {"laboratory", "fhir"})
  public List<CodeDisplay> getAllFederalStates() {
    return federalStateNotificationCategoryData.getFederalStates();
  }

  @Observed(
      name = "federal-state-codes",
      contextualName = "federal-state-codes",
      lowCardinalityKeyValues = {"laboratory", "fhir"})
  public List<CodeDisplay> getDataForPathogenCodesForFederalState(String federalState) {
    List<CodeDisplay> codeDisplays;
    if (isNotifications7_3active) {
      codeDisplays =
          federalStateNotificationCategoryData
              .getFederalStateNotificationCategories()
              .get(federalState);
    } else {
      codeDisplays =
          federalStateNotificationCategoryDataRegression
              .getFederalStateNotificationCategories()
              .get(federalState);
    }
    if (codeDisplays == null) {
      processNoData(federalState, THE_CODE_S_WAS_NOT_FOUND_IN_THE_STORED_DATA);
    }
    return codeDisplays;
  }

  @Observed(
      name = "federal-pathogen-code",
      contextualName = "federal-pathogen-code",
      lowCardinalityKeyValues = {"laboratory", "fhir"})
  public LabNotificationData getDataForFederalPathogenCode(String code) {
    LabNotificationData data = labDataPreparationSrv.getLaboratoryDataMap().get(code.toLowerCase());
    if (data == null) {
      processNoData(code, THE_CODE_S_WAS_NOT_FOUND_IN_THE_STORED_DATA);
    }
    return data;
  }

  public SequencedCollection<CodeDisplay> getAvailableNotificationCategories(
      PathogenNotificationCategory paragraph) {
    SequencedCollection<CodeDisplay> list =
        labDataPreparationSrv.getNotificationCategories(paragraph);
    if (list.isEmpty()) {
      processNoData(paragraph.getStringValue(), NO_DATA_FOUND_FOR_CODE);
    }
    return list;
  }

  public Optional<CodeDisplay> getAvailableNotificationCategory(
      String code, PathogenNotificationCategory paragraph) {
    return getAvailableNotificationCategories(paragraph).stream()
        .filter(cd -> cd.getCode().equals(code))
        .findFirst();
  }
}
