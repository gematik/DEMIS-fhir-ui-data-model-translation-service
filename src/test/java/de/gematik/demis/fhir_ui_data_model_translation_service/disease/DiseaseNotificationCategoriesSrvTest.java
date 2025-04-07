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
  private static FhirContext fhirContext;

  @Mock private SnapshotFilesService snapshotFilesServiceMock;
  private DiseaseNotificationCategoriesSrv diseaseNotificationCategoriesSrv;

  @BeforeAll
  static void setUp() {
    notificationDiseaseCategoryFile =
        new File(
            "src/test/resources/profiles/CodeSystem/CodeSystem-notificationDiseaseCategory.json");
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

      when(snapshotFilesServiceMock.getProfileDiseaseNotificationCategoryFile())
          .thenReturn(notificationDiseaseCategoryFile);

      DiseaseNotificationCategoriesSrv testobject =
          new DiseaseNotificationCategoriesSrv(snapshotFilesServiceMock, fhirContext);
      testobject.createCategories();

      assertThat(testobject.getCategories()).isEmpty();
    }
  }

  @Nested
  @DisplayName("unfiltered laboratory notification category tests")
  class UnfilteredLabNotCatTests {
    @Test
    @DisplayName("should return data from file for notification category with no filtering")
    void shouldReturnDataFromFileForNotificationCategoryWithNoFiltering() {

      when(snapshotFilesServiceMock.getProfileDiseaseNotificationCategoryFile())
          .thenReturn(notificationDiseaseCategoryFile);

      diseaseNotificationCategoriesSrv =
          new DiseaseNotificationCategoriesSrv(snapshotFilesServiceMock, fhirContext);
      diseaseNotificationCategoriesSrv.createCategories();

      List<CodeDisplay> filteredDiseaseNotificationCategoryList =
          diseaseNotificationCategoriesSrv.getCategories();

      assertThat(filteredDiseaseNotificationCategoryList).hasSize(43);
    }

    @Test
    @DisplayName("should return data from file for notification category")
    void shouldReturnDataFromFileForNotificationCategory() {

      when(snapshotFilesServiceMock.getProfileDiseaseNotificationCategoryFile())
          .thenReturn(notificationDiseaseCategoryFile);

      diseaseNotificationCategoriesSrv =
          new DiseaseNotificationCategoriesSrv(snapshotFilesServiceMock, fhirContext);
      diseaseNotificationCategoriesSrv.createCategories();

      List<CodeDisplay> filteredDiseaseNotificationCategoryList =
          diseaseNotificationCategoriesSrv.getCategories();

      // Expected data in order: cvdd, hbvd, band

      assertThat(filteredDiseaseNotificationCategoryList)
          .hasSize(43)
          .extracting("code")
          .contains("cvdd", "hbvd", "band");
      assertThat(diseaseNotificationCategoriesSrv.getCategory("cvdd")).isNotNull();
      assertThat(diseaseNotificationCategoriesSrv.getCategory("hbvd")).isNotNull();
      assertThat(diseaseNotificationCategoriesSrv.getCategory("band")).isNotNull();
      assertThat(diseaseNotificationCategoriesSrv.getCategory("foo")).isNull();
    }

    @Test
    @DisplayName("should return data from file for notification category")
    void shouldReturnDataFromFileForNotificationCategoryRegression() {

      when(snapshotFilesServiceMock.getProfileDiseaseNotificationCategoryFile())
          .thenReturn(notificationDiseaseCategoryFile);

      diseaseNotificationCategoriesSrv =
          new DiseaseNotificationCategoriesSrv(snapshotFilesServiceMock, fhirContext);
      diseaseNotificationCategoriesSrv.createCategories();

      List<CodeDisplay> filteredDiseaseNotificationCategoryList =
          diseaseNotificationCategoriesSrv.getCategories();

      assertThat(filteredDiseaseNotificationCategoryList)
          .hasSize(43)
          .extracting("code")
          .contains("band", "cvdd", "hbvd");
    }

    @Test
    @DisplayName("check designations and display values")
    void shouldReturnDataWithDesignationAndDisplay() {

      when(snapshotFilesServiceMock.getProfileDiseaseNotificationCategoryFile())
          .thenReturn(notificationDiseaseCategoryFile);

      diseaseNotificationCategoriesSrv =
          new DiseaseNotificationCategoriesSrv(snapshotFilesServiceMock, fhirContext);
      diseaseNotificationCategoriesSrv.createCategories();

      List<CodeDisplay> filteredDiseaseNotificationCategoryList =
          diseaseNotificationCategoriesSrv.getCategories();

      assertThat(filteredDiseaseNotificationCategoryList)
          .hasSize(43)
          .contains(TestObjects.codeDisplayWithDesignation().cvdd());
    }

    @DisplayName(
        "DiseaseNotificationCategoryList should handle empty filter list through using an empty list and no filtering")
    @Test
    void shouldHandleEmptyFilterListGracefully() {
      when(snapshotFilesServiceMock.getProfileDiseaseNotificationCategoryFile())
          .thenReturn(notificationDiseaseCategoryFile);

      DiseaseNotificationCategoriesSrv diseaseNotificationCategoriesSrv =
          new DiseaseNotificationCategoriesSrv(snapshotFilesServiceMock, fhirContext);
      diseaseNotificationCategoriesSrv.createCategories();

      List<CodeDisplay> filteredDiseaseNotificationCategoryList =
          diseaseNotificationCategoriesSrv.getCategories();

      assertThat(filteredDiseaseNotificationCategoryList).hasSize(43);
    }
  }

  @Nested
  @DisplayName("filtered disease notification category tests")
  class FilteredDiseaseNotCatTests {
    @Test
    @DisplayName("should return data from file for notification category with no filtering")
    void shouldReturnDataFromFileForNotificationCategoryWithNoFiltering() {

      when(snapshotFilesServiceMock.getProfileDiseaseNotificationCategoryFile())
          .thenReturn(notificationDiseaseCategoryFile);

      diseaseNotificationCategoriesSrv =
          new DiseaseNotificationCategoriesSrv(snapshotFilesServiceMock, fhirContext);
      diseaseNotificationCategoriesSrv.createCategories();

      List<CodeDisplay> filteredDiseaseNotificationCategoryList =
          diseaseNotificationCategoriesSrv.getCategories();

      assertThat(filteredDiseaseNotificationCategoryList).hasSize(43);
    }

    @Test
    @DisplayName("should return data from file for notification category")
    void shouldReturnDataFromFileForNotificationCategory() {

      when(snapshotFilesServiceMock.getProfileDiseaseNotificationCategoryFile())
          .thenReturn(notificationDiseaseCategoryFile);

      diseaseNotificationCategoriesSrv =
          new DiseaseNotificationCategoriesSrv(snapshotFilesServiceMock, fhirContext);
      diseaseNotificationCategoriesSrv.createCategories();

      List<CodeDisplay> filteredDiseaseNotificationCategoryList =
          diseaseNotificationCategoriesSrv.getCategories();

      assertThat(filteredDiseaseNotificationCategoryList)
          .hasSize(43)
          .extracting("code")
          .contains("hbvd", "cvdd");
    }

    @Test
    @DisplayName("check designations and display values")
    void shouldReturnDataWithDesignationAndDisplay() {

      when(snapshotFilesServiceMock.getProfileDiseaseNotificationCategoryFile())
          .thenReturn(notificationDiseaseCategoryFile);

      diseaseNotificationCategoriesSrv =
          new DiseaseNotificationCategoriesSrv(snapshotFilesServiceMock, fhirContext);
      diseaseNotificationCategoriesSrv.createCategories();

      List<CodeDisplay> filteredDiseaseNotificationCategoryList =
          diseaseNotificationCategoriesSrv.getCategories();

      assertThat(filteredDiseaseNotificationCategoryList)
          .hasSize(43)
          .contains(TestObjects.codeDisplayWithDesignation().cvdd());
    }

    @DisplayName(
        "DiseaseNotificationCategoryList should handle empty filter list through using an empty list and no filtering")
    @Test
    void shouldHandleEmptyFilterListGracefully() {
      when(snapshotFilesServiceMock.getProfileDiseaseNotificationCategoryFile())
          .thenReturn(notificationDiseaseCategoryFile);

      DiseaseNotificationCategoriesSrv diseaseNotificationCategoriesSrv =
          new DiseaseNotificationCategoriesSrv(snapshotFilesServiceMock, fhirContext);
      diseaseNotificationCategoriesSrv.createCategories();

      List<CodeDisplay> filteredDiseaseNotificationCategoryList =
          diseaseNotificationCategoriesSrv.getCategories();

      assertThat(filteredDiseaseNotificationCategoryList).hasSize(43);
    }
  }

  @DisplayName(
      "DiseaseNotificationCategoryList should handle empty filter list through using an empty list and no filtering")
  @Test
  void shouldHandleEmptyFilterListGracefully() {
    when(snapshotFilesServiceMock.getProfileDiseaseNotificationCategoryFile()).thenReturn(null);

    DiseaseNotificationCategoriesSrv diseaseNotificationCategoriesSrv =
        new DiseaseNotificationCategoriesSrv(snapshotFilesServiceMock, fhirContext);
    diseaseNotificationCategoriesSrv.createCategories();

    List<CodeDisplay> filteredDiseaseNotificationCategoryList =
        diseaseNotificationCategoriesSrv.getCategories();

    assertThat(filteredDiseaseNotificationCategoryList).isEmpty();
  }
}
