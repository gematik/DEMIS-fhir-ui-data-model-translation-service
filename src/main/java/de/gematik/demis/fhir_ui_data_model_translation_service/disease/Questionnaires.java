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
 * #L%
 */

import static de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils.getCodeFromFileName;

import de.gematik.demis.fhir_ui_data_model_translation_service.utils.SnapshotFilesService;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Questionnaires {

  private static final Pattern DISEASE_PATTERN =
      Pattern.compile("Questionnaire-DiseaseQuestions(.{4})\\.json");
  private static final Pattern STATISTIC_PATTERN =
      Pattern.compile("Questionnaire-StatisticQuestions(.*)\\.json");
  private final String diseaseCommonQuestion;
  private final SnapshotFilesService snapshotFilesService;
  @Getter private final Map<String, File> diseaseQuestionnaires = new HashMap<>();
  @Getter private final Map<String, File> statisticQuestionnaires = new HashMap<>();

  public Questionnaires(
      @Value("${data.disease.common.question}") String diseaseCommonQuestion,
      SnapshotFilesService snapshotFilesService) {
    this.diseaseCommonQuestion = diseaseCommonQuestion;
    this.snapshotFilesService = snapshotFilesService;
  }

  @PostConstruct
  void init() {
    List<File> questionnaires = snapshotFilesService.getQuestionnaires();
    for (File file : questionnaires) {
      log.info("adding questionnaire {}", file.getName());
      if (DISEASE_PATTERN.matcher(file.getName()).matches()) {
        String key = getCodeFromFileName(file, DISEASE_PATTERN).orElse(file.getName());
        diseaseQuestionnaires.put(key, file);
      } else if (STATISTIC_PATTERN.matcher(file.getName()).matches()) {
        String key = getCodeFromFileName(file, STATISTIC_PATTERN).orElse(file.getName());
        statisticQuestionnaires.put(key, file);
      } else if (diseaseCommonQuestion.equals(file.getName())) {
        diseaseQuestionnaires.put("common", file);
      }
    }
  }
}
