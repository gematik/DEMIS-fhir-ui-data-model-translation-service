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

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.gematik.demis.fhir_ui_data_model_translation_service.utils.SnapshotFilesService;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QuestionnairesTest {

  @Mock private SnapshotFilesService snapshotFilesService;

  private Questionnaires questionnaires;

  private File diseaseFile;
  private File diseaseFile1;
  private File diseaseFile2;
  private File statisticFile;
  private File statisticFile1;
  private File statisticFile2;
  private File commonFile;

  @Test
  void initTest() {
    Questionnaires questionnaires =
        new Questionnaires("Questionnaire-DiseaseQuestionsCommon.json", snapshotFilesService);

    // Prepare test data
    File file1 =
        new File(
            "src/test/resources/profiles/Questionnaires/Questionnaire-DiseaseQuestionsCommon.json");
    File file2 =
        new File(
            "src/test/resources/profiles/Questionnaires/Questionnaire-DiseaseQuestionsCVDD.json");
    File file3 =
        new File(
            "src/test/resources/profiles/Questionnaires/Questionnaire-StatisticQuestionsBedOccupancy.json");
    File file4 = new File("src/test/resources/profiles/Questionnaires/DiseaseQuestionsCommon.json");
    List<File> files = Arrays.asList(file1, file2, file3, file4);

    // Mock dependencies
    when(snapshotFilesService.getQuestionnaires()).thenReturn(files);

    // Call the method under test
    questionnaires.init();

    // Assert that the maps have been populated correctly
    assertThat(questionnaires.getDiseaseQuestionnaires()).containsKey("CVDD");
    assertThat(questionnaires.getDiseaseQuestionnaires()).containsKey("common");
    assertThat(questionnaires.getStatisticQuestionnaires()).containsKey("BedOccupancy");

    // Assert that the maps do not contain file 4 as value
    assertThat(questionnaires.getDiseaseQuestionnaires()).doesNotContainValue(file4);
    assertThat(questionnaires.getStatisticQuestionnaires()).doesNotContainValue(file4);
  }

  @BeforeEach
  void setUp() {
    snapshotFilesService = mock(SnapshotFilesService.class);

    diseaseFile =
        new File(
            "src/test/resources/profiles/Questionnaires/Questionnaire-DiseaseQuestionsCVDD.json");
    diseaseFile1 =
        new File(
            "src/test/resources/profiles/Questionnaires/Questionnaire-DiseaseQuestionsCVD.json");
    diseaseFile2 =
        new File(
            "src/test/resources/profiles/Questionnaires/Questionnaire-DiseaseQuestionsCVDDD.json");
    statisticFile =
        new File(
            "src/test/resources/profiles/Questionnaires/Questionnaire-StatisticQuestionsBedOccupancy.json");
    statisticFile1 =
        new File(
            "src/test/resources/profiles/Questionnaires/Questionnaire-StatisticQuestionsShort.json");
    statisticFile2 =
        new File(
            "src/test/resources/profiles/Questionnaires/Questionnaire-StatisticQuestionsSomeLongNameForSomeRandomStatisticStuff.json");
    commonFile =
        new File(
            "src/test/resources/profiles/Questionnaires/Questionnaire-DiseaseQuestionCommon.json");
  }

  @Test
  void testInitForDiseaseSpecificQuestionnaires() {

    when(snapshotFilesService.getQuestionnaires())
        .thenReturn(asList(diseaseFile, diseaseFile1, diseaseFile2));

    questionnaires =
        new Questionnaires("Questionnaire-DiseaseQuestionCommon.json", snapshotFilesService);

    questionnaires.init();

    Map<String, File> expectedDiseaseQuestionnaires = new HashMap<>();
    expectedDiseaseQuestionnaires.put("CVDD", diseaseFile);

    assertThat(questionnaires.getDiseaseQuestionnaires()).isEqualTo(expectedDiseaseQuestionnaires);
    assertThat(questionnaires.getDiseaseQuestionnaires()).doesNotContainValue(diseaseFile1);
    assertThat(questionnaires.getDiseaseQuestionnaires()).doesNotContainValue(diseaseFile2);
  }

  @Test
  void testInitForCommonQuestionnaire() {

    when(snapshotFilesService.getQuestionnaires()).thenReturn(asList(commonFile));

    questionnaires =
        new Questionnaires("Questionnaire-DiseaseQuestionCommon.json", snapshotFilesService);

    questionnaires.init();

    Map<String, File> expectedDiseaseQuestionnaires = new HashMap<>();
    expectedDiseaseQuestionnaires.put("common", commonFile);

    assertThat(questionnaires.getDiseaseQuestionnaires())
        .hasSize(1)
        .isEqualTo(expectedDiseaseQuestionnaires);
  }

  @Test
  void testInitForStatisticQuestionnaires() {

    when(snapshotFilesService.getQuestionnaires())
        .thenReturn(asList(statisticFile, statisticFile1, statisticFile2));

    questionnaires =
        new Questionnaires("Questionnaire-DiseaseQuestionCommon.json", snapshotFilesService);

    questionnaires.init();

    Map<String, File> expectedStatisticQuestionnaires = new HashMap<>();
    expectedStatisticQuestionnaires.put("BedOccupancy", statisticFile);
    expectedStatisticQuestionnaires.put("Short", statisticFile1);
    expectedStatisticQuestionnaires.put("SomeLongNameForSomeRandomStatisticStuff", statisticFile2);

    assertThat(questionnaires.getStatisticQuestionnaires())
        .isEqualTo(expectedStatisticQuestionnaires);
  }
}
