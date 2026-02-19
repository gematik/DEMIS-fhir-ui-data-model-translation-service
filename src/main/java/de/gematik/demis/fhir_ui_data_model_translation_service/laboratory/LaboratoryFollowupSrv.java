package de.gematik.demis.fhir_ui_data_model_translation_service.laboratory;

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

import de.gematik.demis.fhir_ui_data_model_translation_service.conceptmaps.ConceptMapPreparationSrv;
import de.gematik.demis.fhir_ui_data_model_translation_service.context.OnlyInLaboratoryContext;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@OnlyInLaboratoryContext
@Slf4j
public class LaboratoryFollowupSrv {
  private final ConceptMapPreparationSrv conceptMapPreparationSrv;
  private final LaboratoryDataLoaderSrv laboratoryDataLoaderSrv;

  public LaboratoryFollowupSrv(
      ConceptMapPreparationSrv conceptMapPreparationSrv,
      LaboratoryDataLoaderSrv laboratoryDataLoaderSrv) {
    this.conceptMapPreparationSrv = conceptMapPreparationSrv;
    this.laboratoryDataLoaderSrv = laboratoryDataLoaderSrv;
  }

  /**
   * gets 3 digit code in §6.1 concept map * uses 3 digit code from §6.1 concept map to find 4 digit
   * code in concept map for §7.1 CodeDisplay * ValueSets must be used because they are displayed in
   * the UI
   *
   * @param code with 4 digits
   * @return CodeDisplays from ValueSets for all found 4 digit codes in §7.1 concept map for cross
   *     referencing follow up notification
   */
  public Set<CodeDisplay> getPossibleLaboratoryCodesForFollowUp(
      String code, PathogenNotificationCategory paragraph) {
    Set<CodeDisplay> result = new HashSet<>();
    Optional<CodeDisplay> codeDisplay =
        laboratoryDataLoaderSrv.getAvailableNotificationCategory(code, paragraph);
    if (codeDisplay.isPresent()) {
      result.add(codeDisplay.get());
    } else {
      Set<String> possibleCodes =
          conceptMapPreparationSrv.getPossibleCodesFromConceptMap(
              code,
              "NotificationDiseaseCategoryToTransmissionCategory",
              "NotificationCategoryToTransmissionCategory");
      for (String possibleCode : possibleCodes) {
        Optional<CodeDisplay> cd =
            laboratoryDataLoaderSrv.getAvailableNotificationCategory(possibleCode, paragraph);
        cd.ifPresent(result::add);
      }
    }
    return result;
  }
}
