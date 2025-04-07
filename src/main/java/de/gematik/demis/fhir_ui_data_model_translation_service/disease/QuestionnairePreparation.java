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

import ca.uhn.fhir.context.FhirContext;
import com.google.common.collect.ImmutableMap;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.QuestionnaireTranslation;
import de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Questionnaire;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
class QuestionnairePreparation {

  private final Questionnaires questionnaires;
  private final FhirContext fhirContext;

  private final Map<String, QuestionnaireTranslation> questionnairesWithLinkIdAndQuestions =
      new HashMap<>();

  public Map<String, QuestionnaireTranslation> build() {
    return ImmutableMap.copyOf(questionnairesWithLinkIdAndQuestions);
  }

  @PostConstruct
  protected void init() {
    questionnaires
        .getDiseaseQuestionnaires()
        .forEach(
            (key, value) -> {
              try {
                log.info("Loading questionnaire: {}", key);
                questionnairesWithLinkIdAndQuestions.put(
                    key.toLowerCase(), createLinkIdAndQuestionMap(value));
              } catch (IOException e) {
                log.error("Error while loading questionnaire: {}", key);
              }
              log.info("Loading questionnaire: {}", key);
            });
  }

  private QuestionnaireTranslation createLinkIdAndQuestionMap(File value) throws IOException {
    // read File
    String fileString = Utils.getFileString(value);
    Questionnaire questionnaire =
        fhirContext.newJsonParser().parseResource(Questionnaire.class, fileString);

    Map<String, String> linkIdAndQuestionMap = new HashMap<>();
    extractDataFromItemList(questionnaire.getItem(), linkIdAndQuestionMap);

    return QuestionnaireTranslation.builder()
        .items(linkIdAndQuestionMap)
        .title(questionnaire.getTitle())
        .build();
  }

  private void extractDataFromItemList(
      List<Questionnaire.QuestionnaireItemComponent> items,
      Map<String, String> linkIdAndQuestionMap) {
    // search recursively for linkId and question
    items.forEach(item -> addLinkIdAndQuestionToMap(item, linkIdAndQuestionMap));
  }

  private void addLinkIdAndQuestionToMap(
      Questionnaire.QuestionnaireItemComponent item, Map<String, String> linkIdAndQuestionMap) {
    linkIdAndQuestionMap.put(item.getLinkId(), item.getText());
    extractDataFromItemList(item.getItem(), linkIdAndQuestionMap);
  }
}
