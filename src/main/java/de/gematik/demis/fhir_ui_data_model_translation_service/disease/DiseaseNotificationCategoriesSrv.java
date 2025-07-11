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

import static de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils.addToUnmodifiableList;
import static de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils.addToUnmodifiableMap;
import static de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils.createDiseaseTestData;
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
import org.hl7.fhir.r4.model.ValueSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DiseaseNotificationCategoriesSrv {

  private final SnapshotFilesService snapshotFilesService;
  private final FhirContext fhirContext;
  private final boolean filterCodesActive;
  private final List<String> denyList;
  private final boolean addTestData;

  private List<CodeDisplay> categoriesList;
  private List<CodeDisplay> categoriesListNonNominal;
  private Map<String, CodeDisplay> categoriesMap;
  private Map<String, CodeDisplay> categoriesMapNonNominal;

  public DiseaseNotificationCategoriesSrv(
      SnapshotFilesService snapshotFilesService,
      FhirContext fhirContext,
      @Value("${data.disease.notification.category.deny.active}") boolean filterCodesActive,
      @Value("${data.disease.notification.category.deny.list}") List<String> denyList,
      @Value("${add.test.data.disease}") boolean addTestData) {
    this.filterCodesActive = filterCodesActive;
    this.denyList = denyList;
    this.snapshotFilesService = snapshotFilesService;
    this.fhirContext = fhirContext;
    this.addTestData = addTestData;
  }

  private static String normalizeCode(String code) {
    return code.toLowerCase();
  }

  @PostConstruct
  void createCategories() {
    createLists();
    createMaps();
    if (addTestData) {
      log.info("adding test data for error case");
      CodeDisplay testDataCodeDisplay = createDiseaseTestData();
      categoriesList = addToUnmodifiableList(categoriesList, testDataCodeDisplay);
      categoriesListNonNominal =
          addToUnmodifiableList(categoriesListNonNominal, testDataCodeDisplay);
      categoriesMap =
          addToUnmodifiableMap(
              categoriesMap, normalizeCode(testDataCodeDisplay.getCode()), testDataCodeDisplay);
      categoriesMapNonNominal =
          addToUnmodifiableMap(
              categoriesMapNonNominal,
              normalizeCode(testDataCodeDisplay.getCode()),
              testDataCodeDisplay);
    }
  }

  private void createLists() {
    List<CodeDisplay> codeDisplays =
        readValueSetFile(
            this.snapshotFilesService.getProfileNotificationDiseaseCategoryValueSetFile());
    this.categoriesList = Collections.unmodifiableList(sort(codeDisplays));
    if (filterCodesActive) {
      this.categoriesList =
          this.categoriesList.stream()
              .filter(codeDisplay -> !denyList.contains(normalizeCode(codeDisplay.getCode())))
              .toList();
    }
    if (log.isInfoEnabled()) {
      log.info(
          "Loaded disease notification categories. Size: {} Codes: {}",
          categoriesList.size(),
          categoriesList.stream()
              .map(CodeDisplay::getCode)
              .sorted()
              .collect(Collectors.joining(", ", "[", "]")));
    }

    List<CodeDisplay> codeDisplaysNonNominal =
        readValueSetFile(
            this.snapshotFilesService
                .getProfileNotificationDiseaseCategoryNonNominalValueSetFile());
    this.categoriesListNonNominal = Collections.unmodifiableList(sort(codeDisplaysNonNominal));
    if (filterCodesActive) {
      this.categoriesListNonNominal =
          this.categoriesListNonNominal.stream()
              .filter(codeDisplay -> !denyList.contains(normalizeCode(codeDisplay.getCode())))
              .toList();
    }
    if (log.isInfoEnabled()) {
      log.info(
          "Loaded disease notification categories. Size: {} Codes: {}",
          categoriesListNonNominal.size(),
          categoriesListNonNominal.stream()
              .map(CodeDisplay::getCode)
              .sorted()
              .collect(Collectors.joining(", ", "[", "]")));
    }
  }

  private void createMaps() {
    this.categoriesMap =
        Collections.unmodifiableMap(
            this.categoriesList.stream()
                .collect(Collectors.toMap(c -> normalizeCode(c.getCode()), c -> c)));
    this.categoriesMapNonNominal =
        Collections.unmodifiableMap(
            this.categoriesListNonNominal.stream()
                .collect(Collectors.toMap(c -> normalizeCode(c.getCode()), c -> c)));
  }

  private List<CodeDisplay> readValueSetFile(File file) {
    if (file == null) {
      log.warn("No disease notification category file found. file was null. Returning empty list");
      return Collections.emptyList();
    }
    try {
      final String notificationCategoryCodeSystemString = getFileString(file);
      ValueSet notificationCategoryValueSet =
          fhirContext
              .newJsonParser()
              .parseResource(ValueSet.class, notificationCategoryCodeSystemString);
      return extractNotificationCategories(notificationCategoryValueSet);
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
   * Get category by code from §6.1 notification categories
   *
   * @param code code
   * @return category or <code>null</code>
   */
  public CodeDisplay getCategory(String code) {
    return categoriesMap.get(normalizeCode(code));
  }

  /**
   * Get category by code from §7.3 notification categories
   *
   * @param code code
   * @return category or <code>null</code>
   */
  public CodeDisplay getCategoryNonNominal(String code) {
    return categoriesMapNonNominal.get(normalizeCode(code));
  }

  /**
   * Get categories for §6.1
   *
   * @return categories
   */
  public List<CodeDisplay> getCategories() {
    return categoriesList;
  }

  /**
   * Get categories for §7.3 disease
   *
   * @return categories
   */
  public List<CodeDisplay> getCategoriesNonNominal() {
    return categoriesListNonNominal;
  }
}
