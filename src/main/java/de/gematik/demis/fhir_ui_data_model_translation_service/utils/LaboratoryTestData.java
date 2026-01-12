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
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import static de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils.collectAll;

import java.io.File;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
final class LaboratoryTestData {

  private final String path;

  /**
   * Get laboratory test data files either from classpath or file system.
   *
   * @return List of laboratory test data files
   */
  List<File> getFiles() {
    if ((path != null) && !path.isEmpty()) {
      if (ClasspathFhirJsonFiles.isClasspath(path)) {
        return new ClasspathFhirJsonFiles(path).createTempFiles();
      }
      return getProvidedFiles();
    }
    log.warn("No path provided for laboratory test data!");
    return List.of();
  }

  private List<File> getProvidedFiles() {
    log.info("Loading laboratory test files from file system: {}", path);
    final File directory = new File(path);
    if (directory.exists() && directory.isDirectory()) {
      return collectAll(
          Objects.requireNonNull(
              directory.listFiles(),
              "Could not read laboratory test files from file system: " + path));
    }
    log.warn("No test data found at {}", path);
    return List.of();
  }
}
