package de.gematik.demis.fhir_ui_data_model_translation_service.laboratory;

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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhir_ui_data_model_translation_service.utils.SnapshotFilesService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PathogenPathogenNotificationCategoryListTest {

  private File notificationCategoryFile7_1;
  private File notificationCategoryFile7_3;
  private FhirContext fhirContext;
  @Mock private SnapshotFilesService snapshotFilesServiceMock;

  @BeforeEach
  void setUp() {
    notificationCategoryFile7_1 =
        new File("src/test/resources/profiles/CodeSystem/CodeSystem-notificationCategory.json");
    fhirContext = FhirContext.forR4();
  }

  @Test
  void shouldReturnMapIndexedByNotificationCategory() {
    notificationCategoryFile7_1 =
        new File("src/test/resources/profilesNotForIT/ValueSet-notificationCategory7.1.json");
    notificationCategoryFile7_3 =
        new File("src/test/resources/profilesNotForIT/ValueSet-notificationCategory7.3.json");

    when(snapshotFilesServiceMock.getProfileNotificationCategoryValueSetFile())
        .thenReturn(notificationCategoryFile7_1);
    when(snapshotFilesServiceMock.getProfileNotificationCategoryNonNomimalValueSetFile())
        .thenReturn(notificationCategoryFile7_3);

    PathogenNotificationCategoryList pathogenNotificationCategoryList =
        new PathogenNotificationCategoryList(snapshotFilesServiceMock, fhirContext);

    assertThat(
            pathogenNotificationCategoryList
                .getPathogenNotificationCategories()
                .get(PathogenNotificationCategory.P_7_1))
        .extracting("code")
        .containsExactly("acbp", "advp", "adep", "eahp", "abvp");
    assertThat(
            pathogenNotificationCategoryList
                .getPathogenNotificationCategories()
                .get(PathogenNotificationCategory.P_7_3))
        .extracting("code")
        .containsExactly("chtp", "echp", "hivp", "negp", "toxp", "trpp");
    assertThat(pathogenNotificationCategoryList.getPathogenNotificationCategories())
        .containsOnlyKeys(PathogenNotificationCategory.P_7_1, PathogenNotificationCategory.P_7_3);
  }

  @Test
  void shouldReturnEmptyMapWhenExceptionIsThrownWhileReadingNotificationCategoryFile() {
    notificationCategoryFile7_1 =
        new File("src/test/resources/profilesNotForIT/ValueSet-notificationCategory7.1.json");
    notificationCategoryFile7_3 =
        new File("src/test/resources/profilesNotForIT/ValueSet-notificationCategory7.3.json");

    try (MockedStatic<IOUtils> utilities = Mockito.mockStatic(IOUtils.class)) {
      utilities
          .when(() -> IOUtils.toString(any(FileInputStream.class), eq(StandardCharsets.UTF_8)))
          .thenThrow(new IOException());

      when(snapshotFilesServiceMock.getProfileNotificationCategoryValueSetFile())
          .thenReturn(notificationCategoryFile7_1);
      when(snapshotFilesServiceMock.getProfileNotificationCategoryNonNomimalValueSetFile())
          .thenReturn(notificationCategoryFile7_3);

      var testobject = new PathogenNotificationCategoryList(snapshotFilesServiceMock, fhirContext);

      assertThat(testobject.getPathogenNotificationCategories()).isEmpty();
    }
  }
}
