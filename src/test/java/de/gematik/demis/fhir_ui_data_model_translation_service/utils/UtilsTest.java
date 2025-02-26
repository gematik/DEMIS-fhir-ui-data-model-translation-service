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
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import org.hl7.fhir.r4.model.CodeSystem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class UtilsTest {

  @Nested
  @DisplayName("tests to extract codes or names from structure definition files")
  class ExtractCodeOrNamesFromFileName {
    @Test
    void testValidFileName() {
      File file = mock(File.class);
      when(file.exists()).thenReturn(true);
      when(file.getName()).thenReturn("Questionnaire-DiseaseQuestions123.json");
      Pattern pattern = Pattern.compile("Questionnaire-DiseaseQuestions(.*)\\.json");
      Optional<String> result = Utils.getCodeFromFileName(file, pattern);

      assertThat(result).isPresent().contains("123");
    }

    @Test
    void testInvalidFileName() {
      File file = mock(File.class);
      when(file.exists()).thenReturn(true);
      when(file.getName()).thenReturn("InvalidFileName.txt");
      Pattern pattern = Pattern.compile("Questionnaire-DiseaseQuestions(.*)\\.json");
      Optional<String> result = Utils.getCodeFromFileName(file, pattern);

      assertThat(result).isEmpty();
    }

    @Test
    void testFileNameWithSpaces() {
      File file = mock(File.class);
      when(file.exists()).thenReturn(true);
      when(file.getName()).thenReturn("Questionnaire-DiseaseQuestions Some File Name.json");
      Pattern pattern = Pattern.compile("Questionnaire-DiseaseQuestions(.*)\\.json");
      Optional<String> result = Utils.getCodeFromFileName(file, pattern);

      assertThat(result).isPresent().contains(" Some File Name");
    }
  }

  @Nested
  @DisplayName("tests to read file content")
  class ReadFileTests {
    @Test
    void testReadFromPath() throws IOException {
      // Create a temporary test file with some content
      Path tempFile = Files.createTempFile("testFile", ".txt");
      String content = "This is a test file content.";
      Files.write(tempFile, content.getBytes(), StandardOpenOption.CREATE);

      String fileString = Utils.getFileString(tempFile.toString());

      assertThat(fileString).isEqualTo(content);

      // Clean up by deleting the temporary test file
      Files.delete(tempFile);
    }

    @Test
    void testNonExistentPath() {
      String nonExistentFile = "non_existent_file.txt";

      // Use assertThrows to check for IOException when the file doesn't exist
      assertThatThrownBy(() -> Utils.getFileString(nonExistentFile))
          .isInstanceOf(IOException.class);
    }

    @Test
    void testReadFile() throws IOException {
      File tempFile = File.createTempFile("testFile", ".txt");
      String content = "This is a test file content.";

      try {
        Files.write(tempFile.toPath(), content.getBytes(), StandardOpenOption.CREATE);

        String fileString = Utils.getFileString(tempFile.toString());

        assertThat(fileString).isEqualTo(content);
      } finally {
        // Clean up by deleting the temporary test file
        tempFile.delete();
      }
    }

    @Test
    void testNonExistentFile() {
      String nonExistentFile = "non_existent_file.txt";

      // Use assertThrows to check for IOException when the file doesn't exist
      assertThatThrownBy(() -> Utils.getFileString(nonExistentFile))
          .isInstanceOf(IOException.class);
    }
  }

  @Nested
  @DisplayName("extract notification categories test")
  class ExtractionTests {
    private static final FhirContext fhirContext = FhirContext.forR4();

    @Test
    @DisplayName(
        "should return code display list for given notification category file, no filtering")
    void shouldReturnCodeDisplayListForGivenNotificationCategoryFile() throws IOException {
      var notificationCategoryFile =
          new File("src/test/resources/profiles/CodeSystem/CodeSystem-notificationCategory.json");
      var fileString = Utils.getFileString(notificationCategoryFile);
      var codeSystem = fhirContext.newJsonParser().parseResource(CodeSystem.class, fileString);
      List<CodeDisplay> result = Utils.extractNotificationCategories(codeSystem);

      assertThat(result).hasSize(91);
    }

    @ParameterizedTest
    @ValueSource(
        strings = {"src/test/resources/profiles/CodeSystem/CodeSystem-notificationCategory.json"})
    @DisplayName(
        "should return code display list for given notification category file with filtering")
    void shouldReturnCodeDisplayListForGivenNotificationCategoryFileWithFiltering(String file)
        throws IOException {
      var notificationCategoryFile = new File(file);
      var fileString = Utils.getFileString(notificationCategoryFile);
      var codeSystem = fhirContext.newJsonParser().parseResource(CodeSystem.class, fileString);

      List<CodeDisplay> withFederalCategories = Utils.extractNotificationCategories(codeSystem);
      assertThat(withFederalCategories).hasSize(91);
    }
  }
}
