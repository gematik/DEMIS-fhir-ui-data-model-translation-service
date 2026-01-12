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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

@RequiredArgsConstructor
@Slf4j
public final class ClasspathFhirJsonFiles {

  public static final String CLASSPATH_MARKER = "classpath:";

  private final String path;
  private final boolean posix =
      FileSystems.getDefault().supportedFileAttributeViews().contains("posix");

  private Path securedTempDirectory;

  /**
   * Check if the given path is a classpath path.
   *
   * @param path The path to check
   * @return True if the path is a classpath path, false otherwise
   */
  public static boolean isClasspath(String path) {
    return (path != null) && path.startsWith(CLASSPATH_MARKER);
  }

  public List<File> createTempFiles() {
    if (!isClasspath(this.path)) {
      throw new IllegalArgumentException("Path is not a classpath path: " + this.path);
    }
    log.info("Loading FHIR JSON files from classpath: {}", this.path);
    try {
      return getIntegratedResources().stream().map(this::createTempFile).toList();
    } catch (Exception e) {
      throw new IllegalArgumentException(
          "Could not read FHIR JSON files from classpath: " + this.path, e);
    }
  }

  private List<Resource> getIntegratedResources() throws IOException {
    final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
    try {
      return List.of(resourceResolver.getResources(this.path + "/**/*.json"));
    } catch (FileNotFoundException e) {
      log.warn("No FHIR JSON files directory found in classpath at: {}", this.path);
      return List.of();
    }
  }

  private File createTempFile(Resource resource) {
    final String filename = resource.getFilename();
    try {
      final Path tempFile = createSecureTempFile(filename);
      try (InputStream in = resource.getInputStream()) {
        Files.copy(in, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
      }
      tempFile.toFile().deleteOnExit();
      log.debug("Created temp file for resource {}: {}", filename, tempFile);
      return tempFile.toFile();
    } catch (Exception e) {
      throw new IllegalArgumentException("Could not create temp file for resource: " + filename, e);
    }
  }

  /**
   * Creates a secure temporary file in a dedicated directory with restrictive permissions.
   *
   * @param filename The original filename to use as suffix
   * @return Path to the created secure temporary file
   * @throws IOException if file creation fails
   */
  private synchronized Path createSecureTempFile(String filename) throws IOException {
    if (this.securedTempDirectory == null) {
      this.securedTempDirectory = initializeSecureTempDirectory();
    }
    final Path tempFile =
        this.securedTempDirectory.resolve("resource-" + UUID.randomUUID() + "-" + filename);
    Files.createFile(tempFile);
    if (posix) {
      try {
        Files.setPosixFilePermissions(tempFile, PosixFilePermissions.fromString("rw-------"));
      } catch (UnsupportedOperationException e) {
        log.warn("Could not set POSIX permissions on temporary file: {}", tempFile, e);
      }
    }
    return tempFile;
  }

  /**
   * Initializes a secure temporary directory with restrictive permissions.
   *
   * @return Path to the secured temporary directory
   * @throws IOException if directory creation fails
   */
  private Path initializeSecureTempDirectory() throws IOException {
    final String dirName = this.path.substring(this.path.lastIndexOf('/') + 1);
    final Path tempDir =
        Paths.get(System.getProperty("java.io.tmpdir"))
            .resolve("fhir-ui-temp-" + ProcessHandle.current().pid() + "-" + dirName);
    if (!Files.exists(tempDir)) {
      Files.createDirectory(tempDir);
      if (posix) {
        try {
          Files.setPosixFilePermissions(tempDir, PosixFilePermissions.fromString("rwx------"));
        } catch (UnsupportedOperationException e) {
          log.warn("Could not set POSIX permissions on temporary directory: {}", tempDir, e);
        }
      }
      log.debug("Created secured temporary directory: {}", tempDir);
    }
    return tempDir;
  }
}
