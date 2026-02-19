package de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import de.gematik.demis.fhir_ui_data_model_translation_service.FeatureFlags;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.DiseaseNotificationCategoriesSrvRegression;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.Questionnaires;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FormlyFieldConfigs;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.*;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.DiseaseProcessor;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DiseaseDataPreparationSrvTest {

  private final FhirContext fhirContext = FhirContext.forR4Cached();
  @Mock private DiseaseNotificationCategoriesSrvRegression categoriesSrvRegressionMock;
  @Mock private Questionnaires questionnairesMock;
  @Mock private ChoiceProcessor choiceProcessorMock;
  @Mock private DateProcessor dateProcessorMock;
  @Mock private TextProcessor textProcessorMock;
  @Mock private GroupProcessor groupProcessorMock;
  @Mock private ReferenceProcessor referenceProcessorMock;
  @Mock private DiseaseProcessor diseaseProcessorMock;
  @Mock private QuantityProcessor quantityProcessorMock;
  @Mock private FeatureFlags featureFlags;

  private DiseaseDataPreparationSrv diseaseDataPreparationSrv;

  @Test
  void shouldCreateQuestionnairesByCallingSupportingProcessors() throws JsonProcessingException {
    when(categoriesSrvRegressionMock.getCategory(anyString()))
        .thenReturn(CodeDisplay.builder().code("cvdd").display("Covid").build());
    when(choiceProcessorMock.createFieldGroup(any(), any(), anyString()))
        .thenReturn(new FieldGroup[0]);
    when(dateProcessorMock.createFieldGroup(any(), any(), anyString()))
        .thenReturn(new FieldGroup[0]);
    when(textProcessorMock.createFieldGroup(any(), any(), anyString()))
        .thenReturn(new FieldGroup[0]);
    when(referenceProcessorMock.createFieldGroup(any(), any(), anyString()))
        .thenReturn(new FieldGroup[0]);
    when(diseaseProcessorMock.createFieldGroup(anyString())).thenReturn(new FieldGroup[0]);
    when(featureFlags.isDiseaseStrict()).thenReturn(false);

    diseaseDataPreparationSrv =
        new DiseaseDataPreparationSrv(
            null,
            categoriesSrvRegressionMock,
            questionnairesMock,
            fhirContext,
            choiceProcessorMock,
            dateProcessorMock,
            textProcessorMock,
            groupProcessorMock,
            referenceProcessorMock,
            diseaseProcessorMock,
            new EnableWhenProcessor(),
            quantityProcessorMock,
            featureFlags);
    Map<String, File> someMap = new HashMap<>();
    someMap.put(
        "cvdd",
        new File(
            "src/test/resources/profiles/Questionnaires/Questionnaire-DiseaseQuestionsCVDD.json"));
    when(questionnairesMock.getDiseaseQuestionnaires()).thenReturn(someMap);

    diseaseDataPreparationSrv.init();
    Map<String, FormlyFieldConfigs[]> cvdd = diseaseDataPreparationSrv.getQuestionnaire("cvdd");
    assertThat(cvdd)
        .isNotNull()
        .containsKey("conditionConfigs")
        .containsKey("commonConfig")
        .containsKey("questionnaireConfigs")
        .hasSize(3);
  }
}
