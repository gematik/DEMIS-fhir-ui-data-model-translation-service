package de.gematik.demis.fhir_ui_data_model_translation_service.disease;

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

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.objects.TestObjects;
import de.gematik.demis.fhir_ui_data_model_translation_service.utils.SnapshotFilesService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DiseaseNotificationCategoriesSrvTest {

  private static File notificationDiseaseCategoryFile;
  private static File notificationDisease6_1ValueSetFile;
  private static File notificationDisease7_3ValueSetFile;

  private static FhirContext fhirContext;

  @Mock private SnapshotFilesService snapshotFilesServiceMock;
  private DiseaseNotificationCategoriesSrv diseaseNotificationCategoriesSrv;

  @BeforeAll
  static void setUp() {
    notificationDiseaseCategoryFile =
        new File(
            "src/test/resources/profiles/CodeSystem/CodeSystem-notificationDiseaseCategory.json");
    notificationDisease6_1ValueSetFile =
        new File("src/test/resources/profiles/ValueSet/ValueSet-notificationDiseaseCategory.json");
    notificationDisease7_3ValueSetFile =
        new File(
            "src/test/resources/profiles/ValueSet/ValueSet-notificationDiseaseCategoryNonNominal.json");
    fhirContext = FhirContext.forR4();
  }

  @DisplayName(
      "io exception while reading notificationCategoryFile should lead to empty CodeDisplay list as result")
  @Test
  void shouldReturnEmptyListIfIOExceptionOccurs() {
    try (MockedStatic<IOUtils> utilities = Mockito.mockStatic(IOUtils.class)) {
      utilities
          .when(() -> IOUtils.toString(any(FileInputStream.class), eq(StandardCharsets.UTF_8)))
          .thenThrow(new IOException());

      when(snapshotFilesServiceMock.getProfileNotificationDiseaseCategoryValueSetFile())
          .thenReturn(notificationDisease6_1ValueSetFile);
      when(snapshotFilesServiceMock.getProfileNotificationDiseaseCategoryNonNominalValueSetFile())
          .thenReturn(notificationDisease7_3ValueSetFile);

      DiseaseNotificationCategoriesSrv testobject =
          new DiseaseNotificationCategoriesSrv(
              snapshotFilesServiceMock, fhirContext, false, emptyList(), false);
      testobject.createCategories();

      assertThat(testobject.getCategories()).isEmpty();
      assertThat(testobject.getCategoriesNonNominal()).isEmpty();
    }
  }

  @Nested
  @DisplayName("unfiltered disease notification category tests")
  class UnfilteredLabNotCatTests {
    @BeforeEach
    void setUp() {

      when(snapshotFilesServiceMock.getProfileNotificationDiseaseCategoryValueSetFile())
          .thenReturn(notificationDisease6_1ValueSetFile);
      when(snapshotFilesServiceMock.getProfileNotificationDiseaseCategoryNonNominalValueSetFile())
          .thenReturn(notificationDisease7_3ValueSetFile);
    }

    @Test
    @DisplayName("should return data from file for notification category with no filtering")
    void shouldReturnDataFromFileForNotificationCategoryWithNoFiltering() {
      diseaseNotificationCategoriesSrv =
          new DiseaseNotificationCategoriesSrv(
              snapshotFilesServiceMock, fhirContext, false, List.of("hivdd", "cvdd"), false);
      diseaseNotificationCategoriesSrv.createCategories();

      List<CodeDisplay> filteredDiseaseNotificationCategoryList6_1 =
          diseaseNotificationCategoriesSrv.getCategories();
      List<CodeDisplay> filteredDiseaseNotificationCategoryList7_3 =
          diseaseNotificationCategoriesSrv.getCategoriesNonNominal();

      assertThat(filteredDiseaseNotificationCategoryList6_1).hasSize(43);
      assertThat(filteredDiseaseNotificationCategoryList7_3).hasSize(6);
    }

    @Test
    @DisplayName("should return data from file for notification category")
    void shouldReturnDataFromFileForNotificationCategory() {

      diseaseNotificationCategoriesSrv =
          new DiseaseNotificationCategoriesSrv(
              snapshotFilesServiceMock, fhirContext, false, List.of("hivdd", "cvdd"), false);
      diseaseNotificationCategoriesSrv.createCategories();

      List<CodeDisplay> filteredDiseaseNotificationCategoryList6_1 =
          diseaseNotificationCategoriesSrv.getCategories();
      List<CodeDisplay> filteredDiseaseNotificationCategoryList7_3 =
          diseaseNotificationCategoriesSrv.getCategoriesNonNominal();

      // Expected data in order: cvdd, hbvd, band

      assertThat(filteredDiseaseNotificationCategoryList6_1)
          .hasSize(43)
          .extracting("code")
          .contains("cvdd", "hbvd", "band");
      assertThat(diseaseNotificationCategoriesSrv.getCategory("cvdd")).isNotNull();
      assertThat(diseaseNotificationCategoriesSrv.getCategory("hbvd")).isNotNull();
      assertThat(diseaseNotificationCategoriesSrv.getCategory("band")).isNotNull();
      assertThat(diseaseNotificationCategoriesSrv.getCategory("foo")).isNull();
      assertThat(diseaseNotificationCategoriesSrv.getCategory("hivd")).isNull();

      // Expected data in order:

      assertThat(filteredDiseaseNotificationCategoryList7_3)
          .hasSize(6)
          .extracting("code")
          .contains("chtd", "echd", "hivd", "negd", "toxd", "trpd");
      assertThat(diseaseNotificationCategoriesSrv.getCategoryNonNominal("hivd")).isNotNull();
      assertThat(diseaseNotificationCategoriesSrv.getCategoryNonNominal("echd")).isNotNull();
      assertThat(diseaseNotificationCategoriesSrv.getCategoryNonNominal("trpd")).isNotNull();
      assertThat(diseaseNotificationCategoriesSrv.getCategoryNonNominal("foo")).isNull();
      assertThat(diseaseNotificationCategoriesSrv.getCategoryNonNominal("cvdd")).isNull();
    }

    @Test
    @DisplayName("check designations and display values")
    void shouldReturnDataWithDesignationAndDisplay() {

      when(snapshotFilesServiceMock.getProfileNotificationDiseaseCategoryValueSetFile())
          .thenReturn(notificationDisease6_1ValueSetFile);
      when(snapshotFilesServiceMock.getProfileNotificationDiseaseCategoryNonNominalValueSetFile())
          .thenReturn(notificationDisease7_3ValueSetFile);

      diseaseNotificationCategoriesSrv =
          new DiseaseNotificationCategoriesSrv(
              snapshotFilesServiceMock, fhirContext, false, List.of("hivdd", "cvdd"), false);
      diseaseNotificationCategoriesSrv.createCategories();

      List<CodeDisplay> filteredDiseaseNotificationCategoryList6_1 =
          diseaseNotificationCategoriesSrv.getCategories();

      assertThat(filteredDiseaseNotificationCategoryList6_1)
          .hasSize(43)
          .contains(TestObjects.codeDisplayWithDesignation().cvdd());

      List<CodeDisplay> filteredDiseaseNotificationCategoryList7_3 =
          diseaseNotificationCategoriesSrv.getCategoriesNonNominal();

      assertThat(filteredDiseaseNotificationCategoryList7_3)
          .hasSize(6)
          .contains(TestObjects.codeDisplayWithDesignation().hivd());
    }

    @DisplayName(
        "DiseaseNotificationCategoryList should handle empty filter list through using an empty list and no filtering")
    @Test
    void shouldHandleEmptyFilterListGracefully() {
      when(snapshotFilesServiceMock.getProfileNotificationDiseaseCategoryValueSetFile())
          .thenReturn(notificationDisease6_1ValueSetFile);
      when(snapshotFilesServiceMock.getProfileNotificationDiseaseCategoryNonNominalValueSetFile())
          .thenReturn(notificationDisease7_3ValueSetFile);

      diseaseNotificationCategoriesSrv =
          new DiseaseNotificationCategoriesSrv(
              snapshotFilesServiceMock, fhirContext, false, List.of("hivdd", "cvdd"), false);
      diseaseNotificationCategoriesSrv.createCategories();

      List<CodeDisplay> filteredDiseaseNotificationCategoryList =
          diseaseNotificationCategoriesSrv.getCategories();

      assertThat(filteredDiseaseNotificationCategoryList).hasSize(43);
    }
  }

  @Nested
  @DisplayName("filtered disease notification category tests")
  class FilteredDiseaseNotCatTests {

    @BeforeEach
    void setUp() {

      when(snapshotFilesServiceMock.getProfileNotificationDiseaseCategoryValueSetFile())
          .thenReturn(notificationDisease6_1ValueSetFile);
      when(snapshotFilesServiceMock.getProfileNotificationDiseaseCategoryNonNominalValueSetFile())
          .thenReturn(notificationDisease7_3ValueSetFile);
    }

    @Test
    @DisplayName("should return data from file for notification category with no filtering")
    void shouldReturnDataFromFileForNotificationCategoryWithNoFiltering() {

      diseaseNotificationCategoriesSrv =
          new DiseaseNotificationCategoriesSrv(
              snapshotFilesServiceMock, fhirContext, true, List.of("hivd", "cvdd"), false);
      diseaseNotificationCategoriesSrv.createCategories();

      List<CodeDisplay> filteredDiseaseNotificationCategoryList6_1 =
          diseaseNotificationCategoriesSrv.getCategories();
      List<CodeDisplay> filteredDiseaseNotificationCategoryList7_3 =
          diseaseNotificationCategoriesSrv.getCategoriesNonNominal();

      assertThat(filteredDiseaseNotificationCategoryList6_1).hasSize(42);
      assertThat(filteredDiseaseNotificationCategoryList7_3).hasSize(5);
    }

    @Test
    @DisplayName("should return data from file for notification category")
    void shouldReturnDataFromFileForNotificationCategory() {

      diseaseNotificationCategoriesSrv =
          new DiseaseNotificationCategoriesSrv(
              snapshotFilesServiceMock, fhirContext, true, List.of("hivd", "cvdd"), false);
      diseaseNotificationCategoriesSrv.createCategories();

      List<CodeDisplay> filteredDiseaseNotificationCategoryList6_1 =
          diseaseNotificationCategoriesSrv.getCategories();

      assertThat(filteredDiseaseNotificationCategoryList6_1)
          .hasSize(42)
          .extracting("code")
          .contains("hbvd", "band")
          .doesNotContain("cvdd");

      List<CodeDisplay> filteredDiseaseNotificationCategoryList7_3 =
          diseaseNotificationCategoriesSrv.getCategoriesNonNominal();

      assertThat(filteredDiseaseNotificationCategoryList7_3)
          .hasSize(5)
          .extracting("code")
          .contains("chtd", "echd")
          .doesNotContain("hivd");
    }

    @Test
    @DisplayName("check designations and display values")
    void shouldReturnDataWithDesignationAndDisplay() {

      diseaseNotificationCategoriesSrv =
          new DiseaseNotificationCategoriesSrv(
              snapshotFilesServiceMock, fhirContext, true, List.of("hivd", "cvdd"), false);
      diseaseNotificationCategoriesSrv.createCategories();

      List<CodeDisplay> filteredDiseaseNotificationCategoryList6_1 =
          diseaseNotificationCategoriesSrv.getCategories();

      assertThat(filteredDiseaseNotificationCategoryList6_1)
          .hasSize(42)
          .contains(TestObjects.codeDisplayWithDesignation().vchd());

      List<CodeDisplay> filteredDiseaseNotificationCategoryList7_3 =
          diseaseNotificationCategoriesSrv.getCategoriesNonNominal();

      assertThat(filteredDiseaseNotificationCategoryList7_3)
          .hasSize(5)
          .contains(TestObjects.codeDisplayWithDesignation().chtd());
    }

    @DisplayName(
        "DiseaseNotificationCategoryList should handle empty filter list through using an empty list and no filtering")
    @Test
    void shouldHandleEmptyFilterListGracefully() {

      diseaseNotificationCategoriesSrv =
          new DiseaseNotificationCategoriesSrv(
              snapshotFilesServiceMock, fhirContext, true, List.of(), false);
      diseaseNotificationCategoriesSrv.createCategories();

      List<CodeDisplay> filteredDiseaseNotificationCategoryList6_1 =
          diseaseNotificationCategoriesSrv.getCategories();

      assertThat(filteredDiseaseNotificationCategoryList6_1).hasSize(43);
      List<CodeDisplay> filteredDiseaseNotificationCategoryList7_3 =
          diseaseNotificationCategoriesSrv.getCategoriesNonNominal();

      assertThat(filteredDiseaseNotificationCategoryList7_3).hasSize(6);
    }
  }

  @DisplayName(
      "DiseaseNotificationCategoryList should handle empty filter list through using an empty list and no filtering")
  @Test
  void shouldHandleEmptyFilterListGracefully() {
    when(snapshotFilesServiceMock.getProfileNotificationDiseaseCategoryValueSetFile())
        .thenReturn(notificationDisease6_1ValueSetFile);
    when(snapshotFilesServiceMock.getProfileNotificationDiseaseCategoryNonNominalValueSetFile())
        .thenReturn(notificationDisease7_3ValueSetFile);

    diseaseNotificationCategoriesSrv =
        new DiseaseNotificationCategoriesSrv(
            snapshotFilesServiceMock, fhirContext, false, List.of(), false);
    diseaseNotificationCategoriesSrv.createCategories();

    List<CodeDisplay> filteredDiseaseNotificationCategoryList =
        diseaseNotificationCategoriesSrv.getCategories();

    assertThat(filteredDiseaseNotificationCategoryList).hasSize(43);
  }

  @Test
  void shouldAddTestdata() {
    when(snapshotFilesServiceMock.getProfileNotificationDiseaseCategoryValueSetFile())
        .thenReturn(notificationDisease6_1ValueSetFile);
    when(snapshotFilesServiceMock.getProfileNotificationDiseaseCategoryNonNominalValueSetFile())
        .thenReturn(notificationDisease7_3ValueSetFile);

    diseaseNotificationCategoriesSrv =
        new DiseaseNotificationCategoriesSrv(
            snapshotFilesServiceMock, fhirContext, false, List.of(), true);
    diseaseNotificationCategoriesSrv.createCategories();

    assertThat(diseaseNotificationCategoriesSrv.getCategories()).hasSize(44);
    assertThat(diseaseNotificationCategoriesSrv.getCategoriesNonNominal()).hasSize(7);
  }
}
