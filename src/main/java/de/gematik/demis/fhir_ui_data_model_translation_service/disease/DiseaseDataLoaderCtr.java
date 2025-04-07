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

import com.fasterxml.jackson.core.JsonProcessingException;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FormlyFieldConfigs;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.QuestionnaireTranslation;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Slf4j
@RequiredArgsConstructor
public class DiseaseDataLoaderCtr {

  private final DiseaseDataLoaderSrv diseaseDataLoaderSrv;

  @GetMapping("/disease/questionnaire/{code}/items")
  public QuestionnaireTranslation getQuestionsForSpecificCode(@PathVariable String code) {
    log.info("Serving call for disease notification questionnaire translations. Code: {}", code);
    QuestionnaireTranslation translation = diseaseDataLoaderSrv.getQuestionnaireTranslations(code);
    if (translation == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "questionnaire translation code: " + code);
    }
    return translation;
  }

  @GetMapping("/disease")
  public List<CodeDisplay> getAllAvailableCodes() {
    log.info("Get call for all codes list");
    return diseaseDataLoaderSrv.getAllPossibleDiseaseCodes();
  }

  @GetMapping("/disease/questionnaire/{code}/formly")
  public Map<String, FormlyFieldConfigs[]> getFormlyRepresentationOfQuestionnaire(
      @PathVariable String code) throws JsonProcessingException {
    log.info("Get call for specific questionnaire as fhir representation for {}", code);
    return diseaseDataLoaderSrv.getData(code);
  }
}
