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
            "ValueSet-notificationCategory.json",
            "ValueSet-notificationCategoryNonNominal.json",
            "ValueSet-notificationDiseaseCategory.json",
            "ValueSet-notificationDiseaseCategoryNonNominal.json",
            "CodeSystem-notificationDiseaseCategory.json",
            "CodeSystem-CodeSystemISO31662DE.json",
            false,
            false,
            "someString",
            "someStringAsPathForGAPDData");

    profileSnapshotFileService.init();

    assertThat(profileSnapshotFileService.getProfileLoincFile())
        .isEqualTo(new File("src/test/resources/profiles/CodeSystem/CodeSystem-loinc-2.74.json"));
    assertThat(profileSnapshotFileService.getProfileNotificationCategoryCodeSystemFile())
        .isEqualTo(
            new File(
                "src/test/resources/profiles/CodeSystem/CodeSystem-notificationCategory.json"));

    assertThat(profileSnapshotFileService.getProfileNotificationCategoryValueSetFile())
        .isEqualTo(
            new File("src/test/resources/profiles/ValueSet/ValueSet-notificationCategory.json"));

    assertThat(profileSnapshotFileService.getProfileNotificationCategoryNonNomimalValueSetFile())
        .isEqualTo(
            new File(
                "src/test/resources/profiles/ValueSet/ValueSet-notificationCategoryNonNominal.json"));

    assertThat(profileSnapshotFileService.getProfileNotificationDiseaseCategoryValueSetFile())
        .isEqualTo(
            new File(
                "src/test/resources/profiles/ValueSet/ValueSet-notificationDiseaseCategory.json"));

    assertThat(
            profileSnapshotFileService
                .getProfileNotificationDiseaseCategoryNonNominalValueSetFile())
        .isEqualTo(
            new File(
                "src/test/resources/profiles/ValueSet/ValueSet-notificationDiseaseCategoryNonNominal.json"));

    assertThat(profileSnapshotFileService.getProfileDiseaseNotificationCategoryRegressionFile())
        .isEqualTo(
            new File(
                "src/test/resources/profiles/CodeSystem/CodeSystem-notificationDiseaseCategory.json"));

    assertThat(profileSnapshotFileService.getMaterials()).hasSize(4);
    assertThat(profileSnapshotFileService.getMethods()).hasSize(4); // methodWrongGroup
    assertThat(profileSnapshotFileService.getAnswerSets()).hasSize(4);
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
            "ValueSet-notificationCategory7.1.json",
            "ValueSet-notificationCategory7.3.json",
            "ValueSet-notificationDiseaseCategory6.1.json",
            "ValueSet-notificationDiseaseCategory7.3.json",
            "CodeSystem-notificationDiseaseCategory.json",
            "CodeSystem-CodeSystemISO31662DE.json",
            true,
            true,
            "src/main/resources/fhir-profile-snapshots/GAPP",
            "src/test/resources/GAPD");

    profileSnapshotFileService.init();

    assertThat(profileSnapshotFileService.getProfileLoincFile())
        .isEqualTo(new File("src/test/resources/profiles/CodeSystem/CodeSystem-loinc-2.74.json"));
    assertThat(profileSnapshotFileService.getProfileNotificationCategoryCodeSystemFile())
        .isEqualTo(
            new File(
                "src/test/resources/profiles/CodeSystem/CodeSystem-notificationCategory.json"));
    assertThat(profileSnapshotFileService.getProfileDiseaseNotificationCategoryRegressionFile())
        .isEqualTo(
            new File(
                "src/test/resources/profiles/CodeSystem/CodeSystem-notificationDiseaseCategory.json"));

    assertThat(profileSnapshotFileService.getMaterials()).hasSize(5);
    assertThat(profileSnapshotFileService.getMethods()).hasSize(5);
    assertThat(profileSnapshotFileService.getAnswerSets()).hasSize(5);
    assertThat(profileSnapshotFileService.getSubstances()).hasSize(2);
    assertThat(profileSnapshotFileService.getResistances()).hasSize(3);
    assertThat(profileSnapshotFileService.getResistanceGenes()).hasSize(3);
  }

  @Test
  void shouldHandleNullGappPath() {
    SnapshotFilesService profileSnapshotFileService =
        new SnapshotFilesService(
            "src/test/resources/profiles",
            "CodeSystem-loinc-2.74.json",
            "CodeSystem-notificationCategory.json",
            "ValueSet-notificationCategory7.1.json",
            "ValueSet-notificationCategory7.3.json",
            "ValueSet-notificationDiseaseCategory6.1.json",
            "ValueSet-notificationDiseaseCategory7.3.json",
            "CodeSystem-notificationDiseaseCategory.json",
            "CodeSystem-CodeSystemISO31662DE.json",
            true,
            true,
            null,
            null);

    profileSnapshotFileService.init();

    assertThat(profileSnapshotFileService.getProfileLoincFile())
        .isEqualTo(new File("src/test/resources/profiles/CodeSystem/CodeSystem-loinc-2.74.json"));
    assertThat(profileSnapshotFileService.getProfileNotificationCategoryCodeSystemFile())
        .isEqualTo(
            new File(
                "src/test/resources/profiles/CodeSystem/CodeSystem-notificationCategory.json"));
    assertThat(profileSnapshotFileService.getProfileDiseaseNotificationCategoryRegressionFile())
        .isEqualTo(
            new File(
                "src/test/resources/profiles/CodeSystem/CodeSystem-notificationDiseaseCategory.json"));

    assertThat(profileSnapshotFileService.getMaterials()).hasSize(4);
    assertThat(profileSnapshotFileService.getMethods()).hasSize(4);
    assertThat(profileSnapshotFileService.getAnswerSets()).hasSize(4);
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
            "ValueSet-notificationCategory7.1.json",
            "ValueSet-notificationCategory7.3.json",
            "ValueSet-notificationDiseaseCategory6.1.json",
            "ValueSet-notificationDiseaseCategory7.3.json",
            "CodeSystem-notificationDiseaseCategory.json",
            "CodeSystem-CodeSystemISO31662DE.json",
            true,
            true,
            "foobar",
            "foobar2");

    profileSnapshotFileService.init();

    assertThat(profileSnapshotFileService.getProfileLoincFile())
        .isEqualTo(new File("src/test/resources/profiles/CodeSystem/CodeSystem-loinc-2.74.json"));
    assertThat(profileSnapshotFileService.getProfileNotificationCategoryCodeSystemFile())
        .isEqualTo(
            new File(
                "src/test/resources/profiles/CodeSystem/CodeSystem-notificationCategory.json"));
    assertThat(profileSnapshotFileService.getProfileDiseaseNotificationCategoryRegressionFile())
        .isEqualTo(
            new File(
                "src/test/resources/profiles/CodeSystem/CodeSystem-notificationDiseaseCategory.json"));

    assertThat(profileSnapshotFileService.getMaterials()).hasSize(4);
    assertThat(profileSnapshotFileService.getMethods()).hasSize(4);
    assertThat(profileSnapshotFileService.getAnswerSets()).hasSize(4);
    assertThat(profileSnapshotFileService.getSubstances()).hasSize(1);
    assertThat(profileSnapshotFileService.getResistances()).hasSize(2);
    assertThat(profileSnapshotFileService.getResistanceGenes()).hasSize(2);
  }
}
