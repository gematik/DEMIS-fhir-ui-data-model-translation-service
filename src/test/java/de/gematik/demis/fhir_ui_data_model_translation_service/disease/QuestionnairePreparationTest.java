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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import ca.uhn.fhir.context.FhirContext;
import com.google.common.collect.ImmutableMap;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.QuestionnaireTranslation;
import de.gematik.demis.fhir_ui_data_model_translation_service.objects.QuestionTextMap;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QuestionnairePreparationTest {

  public static final String KEY_CVDD = "cvdd";
  public static final String KEY_COMMON = "common";
  @Mock private Questionnaires questionnaires;

  private FhirContext fhirContext;

  private QuestionnairePreparation questionnairePreparation;

  @BeforeEach
  public void setUp() {
    questionnairePreparation = new QuestionnairePreparation(questionnaires, fhirContext);
  }

  @BeforeEach
  void init() {
    fhirContext = FhirContext.forR4Cached();
  }

  @DisplayName("create question text map")
  @Test
  void shouldCreateQuestionTextMap() {
    // create data
    File questionnaireCVDDFile =
        new File(
            "src/test/resources/profiles/Questionnaires/Questionnaire-DiseaseQuestionsCVDD.json");
    File questionnaireCommonFile =
        new File(
            "src/test/resources/profiles/Questionnaires/Questionnaire-DiseaseQuestionsCommon.json");
    Map<String, File> diseaseQuestionnaires = new HashMap<>();
    diseaseQuestionnaires.put(KEY_CVDD.toUpperCase(), questionnaireCVDDFile);
    diseaseQuestionnaires.put("Common", questionnaireCommonFile);

    // create mock
    when(questionnaires.getDiseaseQuestionnaires()).thenReturn(diseaseQuestionnaires);

    // get content from map
    questionnairePreparation = new QuestionnairePreparation(questionnaires, fhirContext);
    questionnairePreparation.init();

    // compare with expected
    Map<String, QuestionnaireTranslation> expected =
        ImmutableMap.of(
            KEY_CVDD, QuestionTextMap.cvddMap(), KEY_COMMON, QuestionTextMap.commonMap());
    Map<String, QuestionnaireTranslation> actual = questionnairePreparation.build();
    assertThat(actual)
        .isInstanceOf(ImmutableMap.class)
        .as("questionnaire translation map size")
        .hasSameSizeAs(expected)
        .containsOnlyKeys(expected.keySet());
    assertThat(actual.get(KEY_COMMON).getItems())
        .as("common questionnaire translation")
        .isEqualTo(expected.get(KEY_COMMON).getItems());
    assertThat(actual.get(KEY_CVDD).getItems())
        .as("CVDD questionnaire translation")
        .isEqualTo(expected.get(KEY_CVDD).getItems());
  }
}
