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
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
class LaboratoryTestDataTest {

  @Test
  void givenValidClasspathWhenGetFilesThenReturnsFiles() throws IOException {
    final List<File> files =
        new LaboratoryTestData("classpath:/fhir-profile-snapshots/GAPP").getFiles();
    verifyJsonFiles(files);
  }

  @Test
  void givenValidFilesystemPathWhenGetFilesThenReturnsFiles() throws IOException {
    final String path = "src/main/resources/fhir-profile-snapshots/GAPP";
    final List<File> files = new LaboratoryTestData(path).getFiles();
    verifyJsonFiles(files);
  }

  private void verifyJsonFiles(List<File> files) throws IOException {
    assertThat(files).isNotEmpty().hasSize(6);
    final String json = Files.readString(files.getFirst().toPath());
    assertThat(json.trim()).as("JSON content").startsWith("{").endsWith("}");
  }

  @Test
  void givenInvalidClasspathWhenGetFilesThenReturnsEmptyList() {
    final LaboratoryTestData testData = new LaboratoryTestData("classpath:/invalid-path");
    final List<File> files = testData.getFiles();
    Assertions.assertThat(files).isEmpty();
  }

  @Test
  void givenInvalidPathWhenGetFilesThenThrowsException() {
    final LaboratoryTestData testData = new LaboratoryTestData("invalid-path");
    final List<File> files = testData.getFiles();
    Assertions.assertThat(files).isEmpty();
  }
}
