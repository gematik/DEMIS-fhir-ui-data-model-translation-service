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

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.Designation;
import de.gematik.demis.fhir_ui_data_model_translation_service.objects.NotificationCategoryListTO;
import de.gematik.demis.fhir_ui_data_model_translation_service.objects.TestObjects;
import de.gematik.demis.fhir_ui_data_model_translation_service.utils.SnapshotFilesService;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SequencedCollection;
import java.util.Set;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LabDataPreparationSrvTest {

  private static List<File> methodFiles;
  private static List<File> materialFiles;
  private static List<File> answerSetFiles;
  private static List<File> substanceFiles;

  private static File methodFile1;
  private static File methodFile2;
  private static File methodFile3;
  private static File materialFile1;
  private static File materialFile2;
  private static File materialFile3;

  private static File answerSetFile1;
  private static File answerSetFile2;
  private static File answerSetFile3;
  private static File substanceFile3;
  private static File gappMethodFile;
  private static File gappMaterialFile;
  private static File gappAnswerSetFile;
  @Mock private SnapshotFilesService snapshotFilesServiceMock;
  @Mock private NotificationCategoryList notificationCategoryListMock;

  @BeforeAll
  static void setUp() {
    materialFile1 = new File("src/test/resources/profiles/ValueSet/ValueSet-materialINVP.json");
    materialFile2 = new File("src/test/resources/profiles/ValueSet/ValueSet-materialLEGP.json");
    materialFile3 = new File("src/test/resources/profiles/ValueSet/ValueSet-materialHBVP.json");

    answerSetFile1 = new File("src/test/resources/profiles/ValueSet/ValueSet-answerSetINVP.json");
    answerSetFile2 = new File("src/test/resources/profiles/ValueSet/ValueSet-answerSetLEGP.json");
    answerSetFile3 = new File("src/test/resources/profiles/ValueSet/ValueSet-answerSetHBVP.json");

    substanceFile3 = new File("src/test/resources/profiles/ValueSet/ValueSet-substanceHBVP.json");

    methodFile1 = new File("src/test/resources/profiles/ValueSet/ValueSet-methodINVP.json");
    methodFile2 = new File("src/test/resources/profiles/ValueSet/ValueSet-methodLEGP.json");
    methodFile3 = new File("src/test/resources/profiles/ValueSet/ValueSet-methodHBVP.json");

    gappMethodFile = new File("src/test/resources/GAPP/ValueSet-methodGAPP.json");
    gappMaterialFile = new File("src/test/resources/GAPP/ValueSet-materialGAPP.json");
    gappAnswerSetFile = new File("src/test/resources/GAPP/ValueSet-answerSetGAPP.json");
  }

  private static void verifyInvpMethods(Map<String, LabNotificationData> laboratoryJsonDataMap) {
    assertThat(laboratoryJsonDataMap.get("invp").methods())
        .as("the method list should contain 30 codes after filtering")
        .hasSize(5)
        .contains(
            CodeDisplay.builder()
                .code("121276004")
                .display("Antigen assay (procedure)")
                .designations(
                    Set.of(
                        new Designation("en-US", "Antigen assay (procedure)"),
                        new Designation("de-DE", "Antigennachweis")))
                .order(100)
                .build())
        .doesNotContain(
            CodeDisplay.builder()
                .code("127785005")
                .display(
                    "Administration of substance to produce immunity, either active or passive (procedure)")
                .designations(
                    Set.of(
                        new Designation(
                            "en-US",
                            "Administration of substance to produce immunity, either active or passive (procedure)"),
                        new Designation("de-DE", "Aktive oder passive Immunisierung")))
                .order(0)
                .build());
  }

  @Test
  void shouldReturnSortedNotificationCategoryList() {
    materialFiles = asList(materialFile1, materialFile2, materialFile3);
    methodFiles = asList(methodFile1, methodFile2, methodFile3);
    answerSetFiles = asList(answerSetFile1, answerSetFile2, answerSetFile3);
    substanceFiles = Collections.singletonList(substanceFile3);
    when(snapshotFilesServiceMock.getMaterials()).thenReturn(materialFiles);
    when(snapshotFilesServiceMock.getMethods()).thenReturn(methodFiles);
    when(snapshotFilesServiceMock.getAnswerSets()).thenReturn(answerSetFiles);
    when(snapshotFilesServiceMock.getSubstances()).thenReturn(substanceFiles);
    when(notificationCategoryListMock.getPathogenNotificationCategoryList())
        .thenReturn(NotificationCategoryListTO.unsortedList());

    LabDataPreparationSrv labDataPreparationSrv =
        new LabDataPreparationSrv(
            FhirContext.forR4(),
            snapshotFilesServiceMock,
            notificationCategoryListMock,
            false,
            false,
            false);

    labDataPreparationSrv.initializeData();

    assertThat(labDataPreparationSrv.getNotificationCategories())
        .isEqualTo(NotificationCategoryListTO.sortedList());
  }

  @Test
  void shouldNotInitializeOnOldProfiles() {
    LabDataPreparationSrv labDataPreparationSrv =
        new LabDataPreparationSrv(
            FhirContext.forR4(),
            snapshotFilesServiceMock,
            notificationCategoryListMock,
            true,
            false,
            false);
    labDataPreparationSrv.initializeData();
    assertThat(labDataPreparationSrv.getLaboratoryDataMap()).isEmpty();
  }

  @Nested
  @DisplayName("unfiltered and unsorted testcases")
  class UnfilteredAndUnsorted {
    @Test
    void shouldAddAllDataInMapWithCorrespondingCodeAsKey() {
      materialFiles = asList(materialFile1, materialFile2, materialFile3);
      methodFiles = asList(methodFile1, methodFile2, methodFile3);
      answerSetFiles = asList(answerSetFile1, answerSetFile2, answerSetFile3);
      substanceFiles = Collections.singletonList(substanceFile3);
      when(snapshotFilesServiceMock.getMaterials()).thenReturn(materialFiles);
      when(snapshotFilesServiceMock.getMethods()).thenReturn(methodFiles);
      when(snapshotFilesServiceMock.getAnswerSets()).thenReturn(answerSetFiles);
      when(snapshotFilesServiceMock.getSubstances()).thenReturn(substanceFiles);
      when(notificationCategoryListMock.getPathogenNotificationCategoryList())
          .thenReturn(
              asList(
                  TestObjects.notificationCategoryCodeDisplay().invp(),
                  TestObjects.notificationCategoryCodeDisplay().hbvp(),
                  TestObjects.notificationCategoryCodeDisplay().legp()));

      LabDataPreparationSrv labDataPreparationSrv = initTestObject();

      Map<String, LabNotificationData> laboratoryJsonDataMap =
          labDataPreparationSrv.getLaboratoryDataMap();
      assertThat(laboratoryJsonDataMap)
          .as("the map should contain invp, hpvb and legp as keys as set in the mock")
          .hasSize(3)
          .containsKey("invp")
          .containsKey("hbvp")
          .containsKey("legp");
      assertThat(laboratoryJsonDataMap.get("invp"))
          .as("the key invp should lead to data.")
          .isNotNull();
      assertThat(laboratoryJsonDataMap.get("hbvp"))
          .as("the key invp should lead to data.")
          .isNotNull();
      assertThat(laboratoryJsonDataMap.get("legp"))
          .as("the key legp should lead to data.")
          .isNotNull();
      assertThat(laboratoryJsonDataMap.get("invp").codeDisplay())
          .as(
              "the value found through the key invp should contain the specific test data from the test objects.")
          .isEqualTo(TestObjects.notificationCategoryCodeDisplay().invp());

      verifyInvpMethods(laboratoryJsonDataMap);

      assertThat(laboratoryJsonDataMap.get("invp").materials())
          .as(
              "the material list should contain the set of 9 codes that are saved in the test data info model with order != 0.")
          .hasSize(9)
          .containsAll(List.of(TestObjects.codeDisplay().invpMaterialCodes().loinc_258607008()));

      assertThat(laboratoryJsonDataMap.get("invp").answerSet())
          .as(
              "the answerset list should contain the set of 23 codes that are saved in the test data info model")
          .hasSize(22)
          .contains(TestObjects.codeDisplay().invpAnswerSetCodeTOs().loinc_715350001R())
          .doesNotContain(TestObjects.codeDisplay().invpAnswerSetCodeTOs().loinc_407479009());

      assertThat(laboratoryJsonDataMap.get("hbvp").substances())
          .as(
              "the substance list should contain the set of 3 codes that are saved in the test data info model")
          .hasSize(3)
          .containsExactlyInAnyOrder(
              TestObjects.codeDisplay().hbvpSubstanceCodeTOs().snomed_22290004(),
              TestObjects.codeDisplay().hbvpSubstanceCodeTOs().snomed_60605004(),
              TestObjects.codeDisplay().hbvpSubstanceCodeTOs().snomed_39082004());
    }

    @Test
    void shouldAddNoDataIfOneFileIsMissing() {
      materialFiles = Collections.singletonList(materialFile1);
      methodFiles = Collections.singletonList(methodFile1);
      answerSetFiles = Collections.singletonList(answerSetFile1);
      when(snapshotFilesServiceMock.getMaterials()).thenReturn(materialFiles);
      when(snapshotFilesServiceMock.getMethods()).thenReturn(methodFiles);
      when(snapshotFilesServiceMock.getAnswerSets()).thenReturn(answerSetFiles);

      LabDataPreparationSrv labDataPreparationSrv = initTestObject();

      Map<String, LabNotificationData> laboratoryJsonDataMap =
          labDataPreparationSrv.getLaboratoryDataMap();
      assertThat(laboratoryJsonDataMap).isEmpty();
    }

    @Test
    void shouldAddNoDataIfAFileDoesNotExist() {
      File notExistingFile = new File("src/test/resource/pathNotExisting/materialINVP.json");
      materialFiles = List.of(notExistingFile);
      methodFiles = Collections.singletonList(methodFile1);
      answerSetFiles = Collections.singletonList(answerSetFile1);
      when(snapshotFilesServiceMock.getMaterials()).thenReturn(materialFiles);
      when(snapshotFilesServiceMock.getMethods()).thenReturn(methodFiles);
      when(snapshotFilesServiceMock.getAnswerSets()).thenReturn(answerSetFiles);

      LabDataPreparationSrv labDataPreparationSrv = initTestObject();

      Map<String, LabNotificationData> laboratoryJsonDataMap =
          labDataPreparationSrv.getLaboratoryDataMap();
      assertThat(laboratoryJsonDataMap).isEmpty();
    }

    @Test
    void shouldAddTestData() {
      materialFiles = asList(materialFile1, materialFile2, materialFile3);
      methodFiles = asList(methodFile1, methodFile2, methodFile3);
      answerSetFiles = asList(answerSetFile1, answerSetFile2, answerSetFile3);
      substanceFiles = Collections.singletonList(substanceFile3);
      when(snapshotFilesServiceMock.getMaterials()).thenReturn(materialFiles);
      when(snapshotFilesServiceMock.getMethods()).thenReturn(methodFiles);
      when(snapshotFilesServiceMock.getAnswerSets()).thenReturn(answerSetFiles);
      when(snapshotFilesServiceMock.getSubstances()).thenReturn(substanceFiles);
      when(notificationCategoryListMock.getPathogenNotificationCategoryList())
          .thenReturn(
              asList(
                  TestObjects.notificationCategoryCodeDisplay().invp(),
                  TestObjects.notificationCategoryCodeDisplay().hbvp(),
                  TestObjects.notificationCategoryCodeDisplay().legp()));
      LabDataPreparationSrv labDataPreparationSrv =
          new LabDataPreparationSrv(
              FhirContext.forR4(),
              snapshotFilesServiceMock,
              notificationCategoryListMock,
              true,
              false,
              false);
      labDataPreparationSrv.initializeData();

      assertThat(labDataPreparationSrv.getNotificationCategories())
          .extracting("code")
          .contains("abcd");
      assertThat(labDataPreparationSrv.getLaboratoryDataMap().get("abcd")).isNull();
    }

    private LabDataPreparationSrv initTestObject() {
      LabDataPreparationSrv labDataPreparationSrv =
          new LabDataPreparationSrv(
              FhirContext.forR4(),
              snapshotFilesServiceMock,
              notificationCategoryListMock,
              false,
              false,
              false);
      labDataPreparationSrv.initializeData();
      return labDataPreparationSrv;
    }
  }

  @Nested
  @DisplayName("filtered lists tests")
  class FilteredTestcases {
    @Test
    void shouldAddAllDataInMapWithCorrespondingCodeAsKey() {
      var materialFile = new File("src/test/resources/sortedValueSets/ValueSet-materialINVP.json");
      var answerSet = new File("src/test/resources/sortedValueSets/ValueSet-answerSetINVP.json");
      materialFiles = asList(materialFile, materialFile2, materialFile3);
      methodFiles = asList(methodFile1, methodFile2, methodFile3);
      answerSetFiles = asList(answerSet, answerSetFile2, answerSetFile3);
      substanceFiles = Collections.singletonList(substanceFile3);
      when(snapshotFilesServiceMock.getMaterials()).thenReturn(materialFiles);
      when(snapshotFilesServiceMock.getMethods()).thenReturn(methodFiles);
      when(snapshotFilesServiceMock.getAnswerSets()).thenReturn(answerSetFiles);
      when(snapshotFilesServiceMock.getSubstances()).thenReturn(substanceFiles);
      when(notificationCategoryListMock.getPathogenNotificationCategoryList())
          .thenReturn(
              asList(
                  TestObjects.notificationCategoryCodeDisplay().invp(),
                  TestObjects.notificationCategoryCodeDisplay().hbvp(),
                  TestObjects.notificationCategoryCodeDisplay().legp()));

      LabDataPreparationSrv labDataPreparationSrv = initTestObject();

      Map<String, LabNotificationData> laboratoryJsonDataMap =
          labDataPreparationSrv.getLaboratoryDataMap();
      assertThat(laboratoryJsonDataMap)
          .as("the map should contain invp, hbpv and legp as keys as set in the mock")
          .hasSize(3)
          .containsKey("invp")
          .containsKey("hbvp")
          .containsKey("legp");
      assertThat(laboratoryJsonDataMap.get("invp"))
          .as("the key invp should lead to data.")
          .isNotNull();
      assertThat(laboratoryJsonDataMap.get("hbvp"))
          .as("the key invp should lead to data.")
          .isNotNull();
      assertThat(laboratoryJsonDataMap.get("legp"))
          .as("the key legp should lead to data.")
          .isNotNull();
      assertThat(laboratoryJsonDataMap.get("invp").codeDisplay())
          .as(
              "the value found through the key invp should contain the specific test data from the test objects.")
          .isEqualTo(TestObjects.notificationCategoryCodeDisplay().invp());

      verifyInvpMethods(laboratoryJsonDataMap);

      assertThat(laboratoryJsonDataMap.get("invp").materials())
          .as(
              "the material list should contain the set of 5 codes that are saved in the test data info model.")
          .hasSize(5)
          .doesNotContain(TestObjects.codeDisplay().invpMaterialCodes().loinc_258607008())
          .contains(TestObjects.codeDisplay().invpMaterialCodes().loinc_309174004R());

      assertThat(laboratoryJsonDataMap.get("invp").answerSet())
          .as(
              "the answerset list should contain the set of 4 codes that are saved in the test data info model")
          .hasSize(4)
          .containsAll(
              List.of(
                  TestObjects.codeDisplay().invpAnswerSetCodeTOs().loinc_407479009(),
                  TestObjects.codeDisplay().invpAnswerSetCodeTOs().loinc_715350001()));

      assertThat(laboratoryJsonDataMap.get("hbvp").substances())
          .as(
              "the substance list should contain the set of 3 codes that are saved in the test data info model")
          .hasSize(3)
          .containsExactlyInAnyOrder(
              TestObjects.codeDisplay().hbvpSubstanceCodeTOs().snomed_22290004(),
              TestObjects.codeDisplay().hbvpSubstanceCodeTOs().snomed_60605004(),
              TestObjects.codeDisplay().hbvpSubstanceCodeTOs().snomed_39082004());
    }

    @Test
    void shouldAddNoDataIfOneFileIsMissing() {
      materialFiles = Collections.singletonList(materialFile1);
      methodFiles = Collections.singletonList(methodFile1);
      answerSetFiles = Collections.singletonList(answerSetFile1);
      when(snapshotFilesServiceMock.getMaterials()).thenReturn(materialFiles);
      when(snapshotFilesServiceMock.getMethods()).thenReturn(methodFiles);
      when(snapshotFilesServiceMock.getAnswerSets()).thenReturn(answerSetFiles);

      LabDataPreparationSrv labDataPreparationSrv = initTestObject();

      Map<String, LabNotificationData> laboratoryJsonDataMap =
          labDataPreparationSrv.getLaboratoryDataMap();
      assertThat(laboratoryJsonDataMap).isEmpty();
    }

    @Test
    void shouldAddNoDataIfAFileDoesNotExist() {
      File notExistingFile = new File("src/test/resource/pathNotExisting/materialINVP.json");
      materialFiles = List.of(notExistingFile);
      methodFiles = Collections.singletonList(methodFile1);
      answerSetFiles = Collections.singletonList(answerSetFile1);
      when(snapshotFilesServiceMock.getMaterials()).thenReturn(materialFiles);
      when(snapshotFilesServiceMock.getMethods()).thenReturn(methodFiles);
      when(snapshotFilesServiceMock.getAnswerSets()).thenReturn(answerSetFiles);

      LabDataPreparationSrv labDataPreparationSrv = initTestObject();

      Map<String, LabNotificationData> laboratoryJsonDataMap =
          labDataPreparationSrv.getLaboratoryDataMap();
      assertThat(laboratoryJsonDataMap).isEmpty();
    }

    @Test
    void shouldAddTestData() {
      materialFiles = asList(materialFile1, materialFile2, materialFile3);
      methodFiles = asList(methodFile1, methodFile2, methodFile3);
      answerSetFiles = asList(answerSetFile1, answerSetFile2, answerSetFile3);
      substanceFiles = Collections.singletonList(substanceFile3);
      when(snapshotFilesServiceMock.getMaterials()).thenReturn(materialFiles);
      when(snapshotFilesServiceMock.getMethods()).thenReturn(methodFiles);
      when(snapshotFilesServiceMock.getAnswerSets()).thenReturn(answerSetFiles);
      when(snapshotFilesServiceMock.getSubstances()).thenReturn(substanceFiles);
      when(notificationCategoryListMock.getPathogenNotificationCategoryList())
          .thenReturn(
              asList(
                  TestObjects.notificationCategoryCodeDisplay().invp(),
                  TestObjects.notificationCategoryCodeDisplay().hbvp(),
                  TestObjects.notificationCategoryCodeDisplay().legp()));
      LabDataPreparationSrv labDataPreparationSrv =
          new LabDataPreparationSrv(
              FhirContext.forR4(),
              snapshotFilesServiceMock,
              notificationCategoryListMock,
              true,
              false,
              false);
      labDataPreparationSrv.initializeData();

      assertThat(labDataPreparationSrv.getNotificationCategories())
          .extracting("code")
          .contains("abcd");
      assertThat(labDataPreparationSrv.getLaboratoryDataMap().get("abcd")).isNull();
    }

    private LabDataPreparationSrv initTestObject() {
      LabDataPreparationSrv labDataPreparationSrv =
          new LabDataPreparationSrv(
              FhirContext.forR4(),
              snapshotFilesServiceMock,
              notificationCategoryListMock,
              false,
              false,
              false);
      labDataPreparationSrv.initializeData();
      return labDataPreparationSrv;
    }
  }

  @Nested
  @DisplayName("filtered and sorted lists tests")
  class FilteredAndSortedTestcases {
    @Test
    void shouldAddAllDataInMapWithCorrespondingCodeAsKey() {
      var materialFile = new File("src/test/resources/sortedValueSets/ValueSet-materialINVP.json");
      var answerSet = new File("src/test/resources/sortedValueSets/ValueSet-answerSetINVP.json");
      materialFiles = asList(materialFile, materialFile2, materialFile3);
      methodFiles = asList(methodFile1, methodFile2, methodFile3);
      answerSetFiles = asList(answerSet, answerSetFile2, answerSetFile3);
      substanceFiles = Collections.singletonList(substanceFile3);
      when(snapshotFilesServiceMock.getMaterials()).thenReturn(materialFiles);
      when(snapshotFilesServiceMock.getMethods()).thenReturn(methodFiles);
      when(snapshotFilesServiceMock.getAnswerSets()).thenReturn(answerSetFiles);
      when(snapshotFilesServiceMock.getSubstances()).thenReturn(substanceFiles);
      when(notificationCategoryListMock.getPathogenNotificationCategoryList())
          .thenReturn(
              asList(
                  TestObjects.notificationCategoryCodeDisplay().invp(),
                  TestObjects.notificationCategoryCodeDisplay().hbvp(),
                  TestObjects.notificationCategoryCodeDisplay().legp()));

      LabDataPreparationSrv labDataPreparationSrv = initTestObject();

      Map<String, LabNotificationData> laboratoryJsonDataMap =
          labDataPreparationSrv.getLaboratoryDataMap();
      assertThat(laboratoryJsonDataMap)
          .as("the map should contain invp, hpvp and legp as keys as set in the mock")
          .hasSize(3)
          .containsKey("invp")
          .containsKey("hbvp")
          .containsKey("legp");
      assertThat(laboratoryJsonDataMap.get("invp"))
          .as("the key invp should lead to data.")
          .isNotNull();
      assertThat(laboratoryJsonDataMap.get("hbvp"))
          .as("the key invp should lead to data.")
          .isNotNull();
      assertThat(laboratoryJsonDataMap.get("legp"))
          .as("the key legp should lead to data.")
          .isNotNull();
      assertThat(laboratoryJsonDataMap.get("invp").codeDisplay())
          .as(
              "the value found through the key invp should contain the specific test data from the test objects.")
          .isEqualTo(TestObjects.notificationCategoryCodeDisplay().invp());

      verifyInvpMethods(laboratoryJsonDataMap);

      assertThat(laboratoryJsonDataMap.get("invp").materials())
          .as(
              "the material list should contain the set of 6 codes that are saved in the test data info model.")
          .hasSize(5)
          .extracting("code")
          .containsExactlyElementsOf(
              List.of("258500001", "258498002", "258450006", "119303007", "309174004"));

      assertThat(laboratoryJsonDataMap.get("invp").answerSet())
          .as(
              "the answerset list should contain the set of 5 codes that are saved in the test data info model")
          .hasSize(4)
          .containsExactly(
              TestObjects.codeDisplay().invpAnswerSetCodeTOs().loinc_700350009(),
              TestObjects.codeDisplay().invpAnswerSetCodeTOs().loinc_442352004(),
              TestObjects.codeDisplay().invpAnswerSetCodeTOs().loinc_407479009(),
              TestObjects.codeDisplay().invpAnswerSetCodeTOs().loinc_715350001());

      assertThat(laboratoryJsonDataMap.get("hbvp").substances())
          .as(
              "the substance list should contain the set of 3 codes that are saved in the test data info model")
          .hasSize(3)
          .containsExactlyInAnyOrder(
              TestObjects.codeDisplay().hbvpSubstanceCodeTOs().snomed_39082004(),
              TestObjects.codeDisplay().hbvpSubstanceCodeTOs().snomed_60605004(),
              TestObjects.codeDisplay().hbvpSubstanceCodeTOs().snomed_22290004());
    }

    @Test
    void shouldAddNoDataIfOneFileIsMissing() {
      materialFiles = Collections.singletonList(materialFile1);
      methodFiles = Collections.singletonList(methodFile1);
      answerSetFiles = Collections.singletonList(answerSetFile1);
      when(snapshotFilesServiceMock.getMaterials()).thenReturn(materialFiles);
      when(snapshotFilesServiceMock.getMethods()).thenReturn(methodFiles);
      when(snapshotFilesServiceMock.getAnswerSets()).thenReturn(answerSetFiles);

      LabDataPreparationSrv labDataPreparationSrv = initTestObject();

      Map<String, LabNotificationData> laboratoryJsonDataMap =
          labDataPreparationSrv.getLaboratoryDataMap();
      assertThat(laboratoryJsonDataMap).isEmpty();
    }

    @Test
    void shouldAddNoDataIfAFileDoesNotExist() {
      File notExistingFile = new File("src/test/resource/pathNotExisting/materialINVP.json");
      materialFiles = List.of(notExistingFile);
      methodFiles = Collections.singletonList(methodFile1);
      answerSetFiles = Collections.singletonList(answerSetFile1);
      when(snapshotFilesServiceMock.getMaterials()).thenReturn(materialFiles);
      when(snapshotFilesServiceMock.getMethods()).thenReturn(methodFiles);
      when(snapshotFilesServiceMock.getAnswerSets()).thenReturn(answerSetFiles);

      LabDataPreparationSrv labDataPreparationSrv = initTestObject();

      Map<String, LabNotificationData> laboratoryJsonDataMap =
          labDataPreparationSrv.getLaboratoryDataMap();
      assertThat(laboratoryJsonDataMap).isEmpty();
    }

    @Test
    void shouldAddTestDataErrorCase() {
      materialFiles = asList(materialFile1, materialFile2, materialFile3);
      methodFiles = asList(methodFile1, methodFile2, methodFile3);
      answerSetFiles = asList(answerSetFile1, answerSetFile2, answerSetFile3);
      substanceFiles = Collections.singletonList(substanceFile3);
      when(snapshotFilesServiceMock.getMaterials()).thenReturn(materialFiles);
      when(snapshotFilesServiceMock.getMethods()).thenReturn(methodFiles);
      when(snapshotFilesServiceMock.getAnswerSets()).thenReturn(answerSetFiles);
      when(snapshotFilesServiceMock.getSubstances()).thenReturn(substanceFiles);
      when(notificationCategoryListMock.getPathogenNotificationCategoryList())
          .thenReturn(
              asList(
                  TestObjects.notificationCategoryCodeDisplay().invp(),
                  TestObjects.notificationCategoryCodeDisplay().hbvp(),
                  TestObjects.notificationCategoryCodeDisplay().legp()));
      LabDataPreparationSrv labDataPreparationSrv =
          new LabDataPreparationSrv(
              FhirContext.forR4(),
              snapshotFilesServiceMock,
              notificationCategoryListMock,
              true,
              false,
              false);
      labDataPreparationSrv.initializeData();

      assertThat(labDataPreparationSrv.getNotificationCategories())
          .extracting("code")
          .contains("abcd");
      assertThat(labDataPreparationSrv.getLaboratoryDataMap().get("abcd")).isNull();
    }

    @Test
    void shouldAddTestDataWithSorting() {
      materialFiles = asList(materialFile1, materialFile2, materialFile3, gappMaterialFile);
      methodFiles = asList(methodFile1, methodFile2, methodFile3, gappMethodFile);
      answerSetFiles = asList(answerSetFile1, answerSetFile2, answerSetFile3, gappAnswerSetFile);
      substanceFiles = Collections.singletonList(substanceFile3);
      when(snapshotFilesServiceMock.getMaterials()).thenReturn(materialFiles);
      when(snapshotFilesServiceMock.getMethods()).thenReturn(methodFiles);
      when(snapshotFilesServiceMock.getAnswerSets()).thenReturn(answerSetFiles);
      when(snapshotFilesServiceMock.getSubstances()).thenReturn(substanceFiles);
      when(notificationCategoryListMock.getPathogenNotificationCategoryList())
          .thenReturn(
              asList(
                  TestObjects.notificationCategoryCodeDisplay().invp(),
                  TestObjects.notificationCategoryCodeDisplay().hbvp(),
                  TestObjects.notificationCategoryCodeDisplay().legp()));
      LabDataPreparationSrv labDataPreparationSrv =
          new LabDataPreparationSrv(
              FhirContext.forR4(),
              snapshotFilesServiceMock,
              notificationCategoryListMock,
              true,
              true,
              false);
      labDataPreparationSrv.initializeData();

      assertThat(labDataPreparationSrv.getNotificationCategories())
          .extracting("code")
          .contains("gapp");
      assertThat(labDataPreparationSrv.getLaboratoryDataMap().get("gapp")).isNotNull();
    }

    @Test
    void shouldReturnEmptyList() {
      when(notificationCategoryListMock.getPathogenNotificationCategories())
          .thenReturn(Collections.emptyMap());

      LabDataPreparationSrv labDataPreparationSrv =
          new LabDataPreparationSrv(
              FhirContext.forR4(),
              snapshotFilesServiceMock,
              notificationCategoryListMock,
              true,
              true,
              true);
      labDataPreparationSrv.initializeData();

      SequencedCollection<CodeDisplay> notificationCategories =
          labDataPreparationSrv.getNotificationCategories(PathogenNotificationCategory.P_7_3);
      assertThat(notificationCategories).isEmpty();
    }

    private LabDataPreparationSrv initTestObject() {
      LabDataPreparationSrv labDataPreparationSrv =
          new LabDataPreparationSrv(
              FhirContext.forR4(),
              snapshotFilesServiceMock,
              notificationCategoryListMock,
              false,
              false,
              false);
      labDataPreparationSrv.initializeData();
      return labDataPreparationSrv;
    }
  }
}
