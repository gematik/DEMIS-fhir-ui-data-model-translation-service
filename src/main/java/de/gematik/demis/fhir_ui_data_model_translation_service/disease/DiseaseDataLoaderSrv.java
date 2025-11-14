package de.gematik.demis.fhir_ui_data_model_translation_service.disease;

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

import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.DiseaseDataPreparationSrv;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FormlyFieldConfigs;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.QuestionnaireTranslation;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.notification.builder.demis.fhir.notification.types.NotificationCategory;
import io.micrometer.observation.annotation.Observed;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiseaseDataLoaderSrv {

  private final QuestionnairePreparation questionnairePreparation;
  private final DiseaseNotificationCategoriesSrvRegression categoriesSrvRegression;
  private final DiseaseNotificationCategoriesSrv categoriesSrv;
  private final DiseaseDataPreparationSrv diseaseDataPreparationSrv;

  @Value("${feature.flag.notifications.7_3}")
  private boolean isNotification73Active;

  @Value("${feature.flag.snapshot.6.active}")
  private boolean isSnapshot6Active;

  private Map<String, QuestionnaireTranslation> translations;
  private List<CodeDisplay>
      possibleDiseaseCodesRegresssion; // TODO Delete with notification7_3 flag
  private List<CodeDisplay> possibleDiseaseCodes;
  private List<CodeDisplay> possibleDiseaseCodesNonNominal;

  @Observed(
      name = "question-list",
      contextualName = "question-list",
      lowCardinalityKeyValues = {"disease", "fhir"})
  public QuestionnaireTranslation getQuestionnaireTranslations(String code) {
    return translations.get(code.toLowerCase());
  }

  @PostConstruct
  void init() {
    translations = questionnairePreparation.build();
    possibleDiseaseCodesRegresssion = categoriesSrvRegression.getCategories();
    possibleDiseaseCodes = categoriesSrv.getCategories();
    possibleDiseaseCodesNonNominal = categoriesSrv.getCategoriesNonNominal();
  }

  @Observed(
      name = "all-codes",
      contextualName = "all-codes",
      lowCardinalityKeyValues = {"disease", "fhir"})
  public List<CodeDisplay> getAllPossibleDiseaseCodes() {
    if (isNotification73Active || isSnapshot6Active) {
      return possibleDiseaseCodes;
    } else {
      return possibleDiseaseCodesRegresssion;
    }
  }

  @Observed(
      name = "all-codes",
      contextualName = "all-codes",
      lowCardinalityKeyValues = {"disease", "fhir"})
  public List<CodeDisplay> getPossibleDiseaseCodesNonNominal() {
    if (isNotification73Active) {
      return possibleDiseaseCodesNonNominal;
    } else {
      throw new UnsupportedOperationException("Feature flag for §7.3 is not active");
    }
  }

  @Observed(
      name = "questionnaire",
      contextualName = "questionnaire",
      lowCardinalityKeyValues = {"disease", "fhir"})
  public Map<String, FormlyFieldConfigs[]> getData(String code) {
    return diseaseDataPreparationSrv.getQuestionnaire(code);
  }

  @Observed(
      name = "questionnaire",
      contextualName = "questionnaire",
      lowCardinalityKeyValues = {"disease", "fhir"})
  public Map<String, FormlyFieldConfigs[]> getData(
      String code, NotificationCategory notificationCategory) {
    return diseaseDataPreparationSrv.getQuestionnaire(code, notificationCategory);
  }

  /**
   * Get the category by code
   *
   * @param code category code
   * @return category
   */
  public Optional<CodeDisplay> getCategory(String code) {
    return possibleDiseaseCodesRegresssion.stream()
        .filter(c -> c.getCode().equals(code))
        .findFirst();
  }
}
