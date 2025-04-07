package de.gematik.demis.fhir_ui_data_model_translation_service.utils;

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

import java.io.File;
import org.junit.jupiter.api.Test;

class SnapshotFilesServiceTest {

  @Test
  void shouldFillAllFieldsWithRelevantData() {
    SnapshotFilesService profileSnapshotFileService =
        new SnapshotFilesService(
            "src/test/resources/profiles",
            "CodeSystem-loinc-2.74.json",
            "CodeSystem-notificationCategory.json",
            "CodeSystem-notificationDiseaseCategory.json",
            "CodeSystem-CodeSystemISO31662DE.json",
            false,
            "someString");

    profileSnapshotFileService.init();

    assertThat(profileSnapshotFileService.getProfileLoincFile())
        .isEqualTo(new File("src/test/resources/profiles/CodeSystem/CodeSystem-loinc-2.74.json"));
    assertThat(profileSnapshotFileService.getProfileNotificationCategoryFile())
        .isEqualTo(
            new File(
                "src/test/resources/profiles/CodeSystem/CodeSystem-notificationCategory.json"));
    assertThat(profileSnapshotFileService.getProfileDiseaseNotificationCategoryFile())
        .isEqualTo(
            new File(
                "src/test/resources/profiles/CodeSystem/CodeSystem-notificationDiseaseCategory.json"));

    assertThat(profileSnapshotFileService.getMaterials()).hasSize(4);
    assertThat(profileSnapshotFileService.getMethods()).hasSize(5);
    assertThat(profileSnapshotFileService.getAnswerSets()).hasSize(26);
    assertThat(profileSnapshotFileService.getSubstances()).hasSize(1);
    assertThat(profileSnapshotFileService.getResistances()).hasSize(2);
    assertThat(profileSnapshotFileService.getResistanceGenes()).hasSize(2);
  }

  @Test
  void shouldFillAllFieldsWithRelevantDataAndAdditionalTestData() {
    SnapshotFilesService profileSnapshotFileService =
        new SnapshotFilesService(
            "src/test/resources/profiles",
            "CodeSystem-loinc-2.74.json",
            "CodeSystem-notificationCategory.json",
            "CodeSystem-notificationDiseaseCategory.json",
            "CodeSystem-CodeSystemISO31662DE.json",
            true,
            "src/test/resources/GAPP");

    profileSnapshotFileService.init();

    assertThat(profileSnapshotFileService.getProfileLoincFile())
        .isEqualTo(new File("src/test/resources/profiles/CodeSystem/CodeSystem-loinc-2.74.json"));
    assertThat(profileSnapshotFileService.getProfileNotificationCategoryFile())
        .isEqualTo(
            new File(
                "src/test/resources/profiles/CodeSystem/CodeSystem-notificationCategory.json"));
    assertThat(profileSnapshotFileService.getProfileDiseaseNotificationCategoryFile())
        .isEqualTo(
            new File(
                "src/test/resources/profiles/CodeSystem/CodeSystem-notificationDiseaseCategory.json"));

    assertThat(profileSnapshotFileService.getMaterials()).hasSize(5);
    assertThat(profileSnapshotFileService.getMethods()).hasSize(6);
    assertThat(profileSnapshotFileService.getAnswerSets()).hasSize(27);
    assertThat(profileSnapshotFileService.getSubstances()).hasSize(2);
    assertThat(profileSnapshotFileService.getResistances()).hasSize(3);
    assertThat(profileSnapshotFileService.getResistanceGenes()).hasSize(3);
  }

  @Test
  void shouldHandlNullGAPPPath() {
    SnapshotFilesService profileSnapshotFileService =
        new SnapshotFilesService(
            "src/test/resources/profiles",
            "CodeSystem-loinc-2.74.json",
            "CodeSystem-notificationCategory.json",
            "CodeSystem-notificationDiseaseCategory.json",
            "CodeSystem-CodeSystemISO31662DE.json",
            true,
            null);

    profileSnapshotFileService.init();

    assertThat(profileSnapshotFileService.getProfileLoincFile())
        .isEqualTo(new File("src/test/resources/profiles/CodeSystem/CodeSystem-loinc-2.74.json"));
    assertThat(profileSnapshotFileService.getProfileNotificationCategoryFile())
        .isEqualTo(
            new File(
                "src/test/resources/profiles/CodeSystem/CodeSystem-notificationCategory.json"));
    assertThat(profileSnapshotFileService.getProfileDiseaseNotificationCategoryFile())
        .isEqualTo(
            new File(
                "src/test/resources/profiles/CodeSystem/CodeSystem-notificationDiseaseCategory.json"));

    assertThat(profileSnapshotFileService.getMaterials()).hasSize(4);
    assertThat(profileSnapshotFileService.getMethods()).hasSize(5);
    assertThat(profileSnapshotFileService.getAnswerSets()).hasSize(26);
    assertThat(profileSnapshotFileService.getSubstances()).hasSize(1);
    assertThat(profileSnapshotFileService.getResistances()).hasSize(2);
    assertThat(profileSnapshotFileService.getResistanceGenes()).hasSize(2);
  }

  @Test
  void shouldHandlNotExistingGAPPPath() {
    SnapshotFilesService profileSnapshotFileService =
        new SnapshotFilesService(
            "src/test/resources/profiles",
            "CodeSystem-loinc-2.74.json",
            "CodeSystem-notificationCategory.json",
            "CodeSystem-notificationDiseaseCategory.json",
            "CodeSystem-CodeSystemISO31662DE.json",
            true,
            "foobar");

    profileSnapshotFileService.init();

    assertThat(profileSnapshotFileService.getProfileLoincFile())
        .isEqualTo(new File("src/test/resources/profiles/CodeSystem/CodeSystem-loinc-2.74.json"));
    assertThat(profileSnapshotFileService.getProfileNotificationCategoryFile())
        .isEqualTo(
            new File(
                "src/test/resources/profiles/CodeSystem/CodeSystem-notificationCategory.json"));
    assertThat(profileSnapshotFileService.getProfileDiseaseNotificationCategoryFile())
        .isEqualTo(
            new File(
                "src/test/resources/profiles/CodeSystem/CodeSystem-notificationDiseaseCategory.json"));

    assertThat(profileSnapshotFileService.getMaterials()).hasSize(4);
    assertThat(profileSnapshotFileService.getMethods()).hasSize(5);
    assertThat(profileSnapshotFileService.getAnswerSets()).hasSize(26);
    assertThat(profileSnapshotFileService.getSubstances()).hasSize(1);
    assertThat(profileSnapshotFileService.getResistances()).hasSize(2);
    assertThat(profileSnapshotFileService.getResistanceGenes()).hasSize(2);
  }
}
