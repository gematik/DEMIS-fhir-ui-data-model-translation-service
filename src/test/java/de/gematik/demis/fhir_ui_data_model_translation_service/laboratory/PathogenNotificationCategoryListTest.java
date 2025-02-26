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
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.Designation;
import de.gematik.demis.fhir_ui_data_model_translation_service.objects.TestObjects;
import de.gematik.demis.fhir_ui_data_model_translation_service.utils.SnapshotFilesService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.IOUtils;
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
class PathogenNotificationCategoryListTest {

  private File notificationCategoryFile;
  private FhirContext fhirContext;
  @Mock private SnapshotFilesService snapshotFilesServiceMock;
  private NotificationCategoryList notificationCategoryList;

  @BeforeEach
  void setUp() {
    notificationCategoryFile =
        new File("src/test/resources/profiles/CodeSystem/CodeSystem-notificationCategory.json");
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

      when(snapshotFilesServiceMock.getProfileNotificationCategoryFile())
          .thenReturn(notificationCategoryFile);

      var testobject = new NotificationCategoryList(snapshotFilesServiceMock, fhirContext);

      assertThat(testobject.getPathogenNotificationCategoryList()).isEmpty();
    }
  }

  @Test
  void shouldReturnMapIndexedByNotificationCategory() {
    notificationCategoryFile =
        new File("src/test/resources/profilesNotForIT/CodeSystem-notificationCategory.json");

    when(snapshotFilesServiceMock.getProfileNotificationCategoryFile())
        .thenReturn(notificationCategoryFile);

    notificationCategoryList = new NotificationCategoryList(snapshotFilesServiceMock, fhirContext);

    assertThat(
            notificationCategoryList
                .getPathogenNotificationCategories()
                .get(PathogenNotificationCategory.P_7_1))
        .extracting("code")
        .containsExactly("acbp", "advp");
    assertThat(
            notificationCategoryList
                .getPathogenNotificationCategories()
                .get(PathogenNotificationCategory.P_7_3))
        .extracting("code")
        .containsExactly("abvp");
    assertThat(notificationCategoryList.getPathogenNotificationCategories())
        .containsOnlyKeys(PathogenNotificationCategory.P_7_1, PathogenNotificationCategory.P_7_3);
  }

  @Test
  void shouldReturnEmptyMapWhenExceptionIsThrownWhileReadingNotificationCategoryFile() {
    notificationCategoryFile =
        new File("src/test/resources/profilesNotForIt/CodeSystem-notificationCategory.json");

    try (MockedStatic<IOUtils> utilities = Mockito.mockStatic(IOUtils.class)) {
      utilities
          .when(() -> IOUtils.toString(any(FileInputStream.class), eq(StandardCharsets.UTF_8)))
          .thenThrow(new IOException());

      when(snapshotFilesServiceMock.getProfileNotificationCategoryFile())
          .thenReturn(notificationCategoryFile);

      var testobject = new NotificationCategoryList(snapshotFilesServiceMock, fhirContext);

      assertThat(testobject.getPathogenNotificationCategories()).isEmpty();
    }
  }

  @Nested
  @DisplayName("unfiltered laboratory notification category tests")
  class UnfilteredLabNotCatTests {
    @Test
    @DisplayName("should return data from file for notification category with no filtering")
    void shouldReturnDataFromFileForNotificationCategoryWithNoFiltering() {

      when(snapshotFilesServiceMock.getProfileNotificationCategoryFile())
          .thenReturn(notificationCategoryFile);

      notificationCategoryList =
          new NotificationCategoryList(snapshotFilesServiceMock, fhirContext);

      List<CodeDisplay> filteredNotificationCategoryList =
          notificationCategoryList.getPathogenNotificationCategoryList();

      assertThat(filteredNotificationCategoryList).hasSize(91);
    }

    @Test
    @DisplayName("should return data from file for notification category")
    void shouldReturnDataFromFileForNotificationCategory() {

      when(snapshotFilesServiceMock.getProfileNotificationCategoryFile())
          .thenReturn(notificationCategoryFile);

      notificationCategoryList =
          new NotificationCategoryList(snapshotFilesServiceMock, fhirContext);

      List<CodeDisplay> filteredNotificationCategoryList =
          notificationCategoryList.getPathogenNotificationCategoryList();

      assertThat(filteredNotificationCategoryList)
          .hasSize(91)
          .extracting("code")
          .contains("hbvp", "invp");
    }

    @Test
    @DisplayName("check designations and display values")
    void shouldReturnDataWithDesignationAndDisplay() {

      when(snapshotFilesServiceMock.getProfileNotificationCategoryFile())
          .thenReturn(notificationCategoryFile);

      notificationCategoryList =
          new NotificationCategoryList(snapshotFilesServiceMock, fhirContext);

      List<CodeDisplay> filteredNotificationCategoryList =
          notificationCategoryList.getPathogenNotificationCategoryList();

      assertThat(filteredNotificationCategoryList)
          .hasSize(91)
          .contains(TestObjects.codeDisplayWithDesignation().invp());
    }

    @DisplayName(
        "NotificationCategoryList should handle empty filter list through using an empty list and no filtering")
    @Test
    void shouldHandleEmptyFilterListGracefully() {
      when(snapshotFilesServiceMock.getProfileNotificationCategoryFile())
          .thenReturn(notificationCategoryFile);

      NotificationCategoryList notificationCategoryList =
          new NotificationCategoryList(snapshotFilesServiceMock, fhirContext);

      List<CodeDisplay> filteredNotificationCategoryList =
          notificationCategoryList.getPathogenNotificationCategoryList();

      assertThat(filteredNotificationCategoryList).hasSize(91);
    }
  }

  @Nested
  @DisplayName("filtered laboratory notification category tests")
  class FilteredLabNotCatTests {
    @Test
    @DisplayName("should return data from file for notification category with no filtering")
    void shouldReturnDataFromFileForNotificationCategoryWithNoFiltering() {

      when(snapshotFilesServiceMock.getProfileNotificationCategoryFile())
          .thenReturn(notificationCategoryFile);

      notificationCategoryList =
          new NotificationCategoryList(snapshotFilesServiceMock, fhirContext);

      List<CodeDisplay> filteredNotificationCategoryList =
          notificationCategoryList.getPathogenNotificationCategoryList();

      assertThat(filteredNotificationCategoryList).hasSize(91);
    }

    @Test
    @DisplayName("should return data from file for notification category")
    void shouldReturnDataFromFileForNotificationCategory() {

      when(snapshotFilesServiceMock.getProfileNotificationCategoryFile())
          .thenReturn(notificationCategoryFile);

      notificationCategoryList =
          new NotificationCategoryList(snapshotFilesServiceMock, fhirContext);

      List<CodeDisplay> filteredNotificationCategoryList =
          notificationCategoryList.getPathogenNotificationCategoryList();

      assertThat(filteredNotificationCategoryList)
          .hasSize(91)
          .extracting("code")
          .contains("hbvp", "invp");
    }

    @Test
    @DisplayName("check designations and display values")
    void shouldReturnDataWithDesignationAndDisplay() {

      when(snapshotFilesServiceMock.getProfileNotificationCategoryFile())
          .thenReturn(notificationCategoryFile);

      notificationCategoryList =
          new NotificationCategoryList(snapshotFilesServiceMock, fhirContext);

      List<CodeDisplay> filteredNotificationCategoryList =
          notificationCategoryList.getPathogenNotificationCategoryList();

      assertThat(filteredNotificationCategoryList)
          .hasSize(91)
          .contains(
              CodeDisplay.builder()
                  .code("invp")
                  .display("Influenzavirus; Meldepflicht nur für den direkten Nachweis")
                  .designations(Set.of(new Designation("de-DE", "Influenzavirus")))
                  .order(100)
                  .build());
    }

    @DisplayName(
        "NotificationCategoryList should handle empty filter list through using an empty list and no filtering")
    @Test
    void shouldHandleEmptyFilterListGracefully() {
      when(snapshotFilesServiceMock.getProfileNotificationCategoryFile())
          .thenReturn(notificationCategoryFile);

      NotificationCategoryList notificationCategoryList =
          new NotificationCategoryList(snapshotFilesServiceMock, fhirContext);

      List<CodeDisplay> filteredNotificationCategoryList =
          notificationCategoryList.getPathogenNotificationCategoryList();

      assertThat(filteredNotificationCategoryList).hasSize(91);
    }
  }
}
