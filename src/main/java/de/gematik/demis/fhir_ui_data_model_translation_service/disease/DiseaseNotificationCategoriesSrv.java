package de.gematik.demis.fhir_ui_data_model_translation_service.disease;

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

import static de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils.extractNotificationCategories;
import static de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils.getFileString;

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.utils.SnapshotFilesService;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.CodeSystem;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DiseaseNotificationCategoriesSrv {

  private final SnapshotFilesService snapshotFilesService;
  private final FhirContext fhirContext;

  private List<CodeDisplay> categoriesList;
  private Map<String, CodeDisplay> categoriesMap;

  DiseaseNotificationCategoriesSrv(
      SnapshotFilesService snapshotFilesService, FhirContext fhirContext) {
    this.snapshotFilesService = snapshotFilesService;
    this.fhirContext = fhirContext;
  }

  private static String normalizeCode(String code) {
    return code.toLowerCase();
  }

  @PostConstruct
  void createCategories() {
    createList();
    createMap();
  }

  private void createList() {
    List<CodeDisplay> codeDisplays = readCodeSystemFile();
    this.categoriesList = Collections.unmodifiableList(sort(codeDisplays));
    if (log.isInfoEnabled()) {
      log.info(
          "Loaded disease notification categories. Size: {} Codes: {}",
          categoriesList.size(),
          categoriesList.stream()
              .map(CodeDisplay::getCode)
              .sorted()
              .collect(Collectors.joining(", ", "[", "]")));
    }
  }

  private void createMap() {
    this.categoriesMap =
        Collections.unmodifiableMap(
            this.categoriesList.stream()
                .collect(Collectors.toMap(c -> normalizeCode(c.getCode()), c -> c)));
  }

  private List<CodeDisplay> readCodeSystemFile() {
    File profileDiseaseNotificationCategoryFile =
        this.snapshotFilesService.getProfileDiseaseNotificationCategoryFile();
    if (profileDiseaseNotificationCategoryFile == null) {
      log.warn("No disease notification category file found. Returning empty list");
      return Collections.emptyList();
    }
    try {
      String fileString = getFileString(profileDiseaseNotificationCategoryFile);
      CodeSystem notificationCategory =
          fhirContext.newJsonParser().parseResource(CodeSystem.class, fileString);
      return extractNotificationCategories(notificationCategory);
    } catch (IOException e) {
      log.error("Error while reading notification category file", e);
      return Collections.emptyList();
    }
  }

  private List<CodeDisplay> sort(List<CodeDisplay> codeDisplays) {
    // reversed so descending order is active
    return codeDisplays.stream()
        .sorted(Comparator.comparing(CodeDisplay::getOrder).reversed())
        .toList();
  }

  /**
   * Get category by code
   *
   * @param code code
   * @return category or <code>null</code>
   */
  public CodeDisplay getCategory(String code) {
    return categoriesMap.get(normalizeCode(code));
  }

  /**
   * Get categories
   *
   * @return categories
   */
  public List<CodeDisplay> getCategories() {
    return categoriesList;
  }
}
