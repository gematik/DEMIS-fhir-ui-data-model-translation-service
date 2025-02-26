package de.gematik.demis.fhir_ui_data_model_translation_service;

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

import de.gematik.demis.fhir_ui_data_model_translation_service.laboratory.LabDataPreparationSrv;
import de.gematik.demis.fhir_ui_data_model_translation_service.laboratory.LaboratoryDataLoaderSrv;
import de.gematik.demis.fhir_ui_data_model_translation_service.laboratory.PathogenNotificationCategory;
import de.gematik.demis.fhir_ui_data_model_translation_service.utils.SnapshotFilesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(profiles = "test")
class FhirUiDataModelTranslationServiceApplicationTests {

  @Autowired private LabDataPreparationSrv labDataPreparationSrv;
  @Autowired private SnapshotFilesService snapshotFilesService;

  @Autowired private LaboratoryDataLoaderSrv laboratoryDataLoaderSrv;

  @Test
  void contextLoads() {
    assertThat(labDataPreparationSrv).isNotNull();
    assertThat(labDataPreparationSrv.getLaboratoryDataMap()).isNotNull();

    assertThat(snapshotFilesService).isNotNull();
    assertThat(snapshotFilesService.getProfileNotificationCategoryFile()).isNotNull();
    assertThat(snapshotFilesService.getMaterials()).isNotNull();
    assertThat(snapshotFilesService.getProfileLoincFile()).isNotNull();
    assertThat(snapshotFilesService.getMethods()).isNotNull();

    assertThat(laboratoryDataLoaderSrv.getAllFederalStates()).hasSize(16);
    assertThat(laboratoryDataLoaderSrv.getDataForPathogenCodesForFederalState("DE-BW")).hasSize(73);
    assertThat(
            laboratoryDataLoaderSrv.getAvailableNotificationCategories(
                PathogenNotificationCategory.P_7_3))
        .hasSize(6);
  }
}
