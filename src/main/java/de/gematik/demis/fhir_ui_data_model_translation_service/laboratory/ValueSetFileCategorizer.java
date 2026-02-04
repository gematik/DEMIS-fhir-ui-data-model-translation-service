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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for categorizing files into predefined categories based on their filenames. The
 * categorization is performed using regular expressions that match specific patterns.
 */
@Slf4j
public class ValueSetFileCategorizer {

  /**
   * Inner class representing a file category. Each category is defined by a regex pattern and a
   * list of files that match the pattern.
   */
  private static class FileCategory {
    private final Pattern pattern; // Regex pattern for matching filenames.
    private final List<File> files; // List of files belonging to this category.

    /**
     * Constructor for creating a FileCategory.
     *
     * @param regex The regex pattern used to match filenames for this category.
     */
    public FileCategory(String regex) {
      this.pattern = Pattern.compile(regex);
      this.files = new ArrayList<>();
    }

    /**
     * Gets the regex pattern for this category.
     *
     * @return The regex pattern.
     */
    public Pattern getPattern() {
      return pattern;
    }

    /**
     * Gets the list of files in this category.
     *
     * @return The list of files.
     */
    public List<File> getFiles() {
      return files;
    }
  }

  // Map storing categories by their names, each associated with a FileCategory object.
  private final Map<String, FileCategory> categoryMap = new HashMap<>();

  /**
   * Constructor for ValueSetFileCategorizer. Initializes the category map with predefined
   * categories and their corresponding regex patterns.
   */
  public ValueSetFileCategorizer() {
    categoryMap.put("resistance", new FileCategory(".*resistance[A-Z]{3}P.json"));
    categoryMap.put("resistanceGene", new FileCategory(".*resistanceGene[A-Z]{3}P.json"));
    categoryMap.put("method", new FileCategory(".*method[A-Z]{3}P.json"));
    categoryMap.put("substance", new FileCategory(".*substance[A-Z]{3}P.json"));
    categoryMap.put("material", new FileCategory(".*material[A-Z]{3}P.json"));
    categoryMap.put("answerSet", new FileCategory(".*answerSet[A-Z]{3}P.json"));
  }

  /**
   * Categorizes a given file by matching its name against the regex patterns of all categories.
   *
   * @param file The file to categorize.
   */
  public void categorizeFile(File file) {
    String fileName = file.getName();
    for (Map.Entry<String, FileCategory> entry : categoryMap.entrySet()) {
      FileCategory category = entry.getValue();
      if (category.getPattern().matcher(fileName).matches()) {
        category.getFiles().add(file);
        log.info("File " + fileName + " categorized as " + entry.getKey());
        return;
      }
    }
    log.info("File " + fileName + " does not match any category.");
  }

  /**
   * Retrieves the list of files for a specific category.
   *
   * @param category The name of the category.
   * @return The list of files in the specified category, or null if the category does not exist.
   */
  public List<File> getFilesForCategory(String category) {
    return categoryMap.get(category).getFiles();
  }
}
