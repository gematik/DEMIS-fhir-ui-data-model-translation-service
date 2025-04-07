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

import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import java.util.List;
import java.util.SequencedCollection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LaboratoryDataLoaderCtr {

  private final LaboratoryDataLoaderSrv laboratoryDataLoaderSrv;
  private final boolean isNotification73Active;

  public LaboratoryDataLoaderCtr(
      LaboratoryDataLoaderSrv laboratoryDataLoaderSrv,
      @Value("${feature.flag.notification7_3}") boolean isNotification73Active) {
    this.laboratoryDataLoaderSrv = laboratoryDataLoaderSrv;
    this.isNotification73Active = isNotification73Active;
  }

  @GetMapping({"/laboratory/federalStates", "/laboratory/7.1/federalStates"})
  public List<CodeDisplay> getAvailableFederalStates() {
    log.info("Get call for all federal states");
    return laboratoryDataLoaderSrv.getAllFederalStates();
  }

  @GetMapping({
    "/laboratory/federalState/{federalState}",
    "/laboratory/7.1/federalState/{federalState}"
  })
  public List<CodeDisplay> getLaboratoryDataForSpecificCodeAndFederalState(
      @PathVariable String federalState) {
    log.info("Get call for federal state: {}", federalState);
    return laboratoryDataLoaderSrv.getDataForPathogenCodesForFederalState(federalState);
  }

  @GetMapping({
    "/laboratory/federalState/pathogenData/{code}",
    "/laboratory/7.1/federalState/pathogenData/{code}",
    "/laboratory/7.3/pathogenData/{code}"
  })
  public LabNotificationData getFederalStateLaboratoryDataForSpecificCode(
      @PathVariable String code) {
    log.info("Get call for federal state pathogen data: {}", code);
    return laboratoryDataLoaderSrv.getDataForFederalPathogenCode(code);
  }

  @GetMapping("/laboratory/7.3")
  public SequencedCollection<CodeDisplay> get73NotificationCategories() {
    if (isNotification73Active) {
      return laboratoryDataLoaderSrv.getAvailableNotificationCategories(
          PathogenNotificationCategory.P_7_3);
    } else {
      throw new UnsupportedOperationException("Feature flag for §7.3 is not active");
    }
  }
}
