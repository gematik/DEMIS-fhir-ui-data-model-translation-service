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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.DiseaseDataPreparationSrv;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.QuestionnaireTranslation;
import de.gematik.demis.fhir_ui_data_model_translation_service.objects.QuestionTextMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DiseaseDataLoaderSrvTest {

  @Mock private QuestionnairePreparation questionnairePreparationMock;
  private DiseaseDataLoaderSrv diseaseDataLoaderSrv;
  @Mock private DiseaseNotificationCategoriesSrv diseaseNotificationCategoriesSrvMock;
  @Mock private DiseaseDataPreparationSrv diseaseDataPreparationSrvMock;

  @BeforeEach
  void setUp() {}

  @Test
  void getQuestionsTest() {
    String code = "testcode";
    QuestionnaireTranslation expecetedMap = QuestionTextMap.cvddMap();
    Map<String, QuestionnaireTranslation> mockedMap = Map.of(code, expecetedMap);
    when(questionnairePreparationMock.build()).thenReturn(mockedMap);

    diseaseDataLoaderSrv =
        new DiseaseDataLoaderSrv(
            questionnairePreparationMock,
            diseaseNotificationCategoriesSrvMock,
            diseaseDataPreparationSrvMock);
    diseaseDataLoaderSrv.init();

    verify(questionnairePreparationMock).build();

    QuestionnaireTranslation questions =
        diseaseDataLoaderSrv.getQuestionnaireTranslations("testCode");
    assertThat(questions).isEqualTo(expecetedMap);
  }
}
