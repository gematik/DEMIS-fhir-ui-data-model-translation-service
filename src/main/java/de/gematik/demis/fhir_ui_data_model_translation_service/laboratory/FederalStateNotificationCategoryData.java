package de.gematik.demis.fhir_ui_data_model_translation_service.laboratory;

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

import static de.gematik.demis.fhir_ui_data_model_translation_service.model.Designation.getDesignations;
import static de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils.createTestDataForErrorCase;
import static de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils.createTestDataForSorting;
import static de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils.extractOrder;

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.utils.SnapshotFilesService;
import de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ValueSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FederalStateNotificationCategoryData {

  public static final String FEDERAL_STATE = "federal-state";
  private final SnapshotFilesService snapshotFilesService;
  private final FhirContext fhirContext;
  private final boolean filterCodesActive;
  private final String denyList;
  private final boolean addTestData;
  private final boolean addTestDataSortingCase;
  private List<String> denyListNotificationCategory;
  private List<String> notificationCategoryForNonNominal;
  @Getter private Map<String, List<CodeDisplay>> federalStateNotificationCategories;
  @Getter private List<CodeDisplay> federalStates;

  public FederalStateNotificationCategoryData(
      SnapshotFilesService snapshotFilesService,
      FhirContext fhirContext,
      @Value("${data.notification.category.deny.active}") boolean filterCodesActive,
      @Value("${data.notification.category.deny.list}") String denyList,
      @Value("${add.test.data.error.case.for.lab}") boolean addTestDataErrorCase,
      @Value("${add.test.data.laboratory.sorting}") boolean addTestDataSortingCase) {
    this.snapshotFilesService = snapshotFilesService;
    this.fhirContext = fhirContext;
    this.filterCodesActive = filterCodesActive;
    this.denyList = denyList;
    this.addTestData = addTestDataErrorCase;
    this.addTestDataSortingCase = addTestDataSortingCase;
  }

  @PostConstruct
  protected void createData() {
    federalStateNotificationCategories = new LinkedHashMap<>();
    federalStates = new ArrayList<>();
    denyListNotificationCategory = new ArrayList<>(Arrays.asList(denyList.split(",")));

    File federalStateNotificationCategoryFile = snapshotFilesService.getFederalStateFile();
    File notificationCategoryCodeSystemFile =
        snapshotFilesService.getProfileNotificationCategoryCodeSystemFile();
    File notificationCategoryValueSetFile =
        snapshotFilesService.getProfileNotificationCategoryValueSetFile();

    CodeSystem federalStateCodeSystem;
    CodeSystem notificationCategoryCodeSystem;
    ValueSet notificationCategoryValueSet;
    try {
      federalStateCodeSystem =
          fhirContext
              .newJsonParser()
              .parseResource(
                  CodeSystem.class, Utils.getFileString(federalStateNotificationCategoryFile));

    } catch (IOException e) {
      log.error(
          "Error parsing federal state file: {}",
          federalStateNotificationCategoryFile.getAbsolutePath(),
          e);
      return;
    }

    try {
      notificationCategoryCodeSystem =
          fhirContext
              .newJsonParser()
              .parseResource(
                  CodeSystem.class, Utils.getFileString(notificationCategoryCodeSystemFile));
    } catch (IOException e) {
      log.error(
          "Error parsing notification category file: {}",
          notificationCategoryCodeSystemFile.getAbsolutePath(),
          e);
      return;
    }

    try {
      notificationCategoryValueSet =
          fhirContext
              .newJsonParser()
              .parseResource(ValueSet.class, Utils.getFileString(notificationCategoryValueSetFile));
    } catch (IOException e) {
      log.error(
          "Error parsing notification category 7.1 file: {}",
          notificationCategoryValueSetFile.getAbsolutePath(),
          e);
      return;
    }

    federalStateCodeSystem.getConcept().forEach(this::addFederalStateDataToMapAndList);

    notificationCategoryForNonNominal =
        notificationCategoryValueSet.getCompose().getInclude().stream()
            .flatMap(include -> include.getConcept().stream())
            .map(concept -> concept.getCode())
            .toList();

    notificationCategoryCodeSystem.getConcept().forEach(this::processNotificationCategoryConcept);

    if (addTestData) {
      log.info("adding test data for error case");
      CodeDisplay testDataCodeDisplay = createTestDataForErrorCase();
      federalStateNotificationCategories.forEach((key, value) -> value.add(testDataCodeDisplay));
    }

    if (addTestDataSortingCase) {
      log.info("adding test data for sorting case");
      CodeDisplay testDataGAPP = createTestDataForSorting();
      federalStateNotificationCategories.forEach((key, value) -> value.add(testDataGAPP));
    }

    federalStateNotificationCategories.replaceAll(
        (key, value) ->
            value.stream()
                .sorted(
                    Comparator.comparing(CodeDisplay::getOrder)
                        .reversed()) // reversed so descending order is active;
                .toList());
  }

  private void processNotificationCategoryConcept(
      CodeSystem.ConceptDefinitionComponent conceptDefinitionComponent) {
    String code = conceptDefinitionComponent.getCode();

    // check if code is part of the 7.1 notification categories and not in the deny list
    if (!notificationCategoryForNonNominal.contains(code)
        || (filterCodesActive && denyListNotificationCategory.contains(code))) {
      return;
    }

    // check for federal state specific notification category
    List<CodeSystem.ConceptPropertyComponent> relevantFederalStatesForNotificationCategory =
        conceptDefinitionComponent.getProperty().stream()
            .filter(property -> property.getCode().equals(FEDERAL_STATE))
            .toList();

    if (relevantFederalStatesForNotificationCategory.isEmpty()) {
      // is not federal state specific, add to all federal states
      federalStates.forEach(
          federalStateCodeDisplay ->
              federalStateNotificationCategories
                  .get(federalStateCodeDisplay.getCode())
                  .add(
                      CodeDisplay.builder()
                          .code(code)
                          .display(conceptDefinitionComponent.getDisplay())
                          .designations(getDesignations(conceptDefinitionComponent))
                          .order(extractOrder(conceptDefinitionComponent))
                          .build()));
    } else {
      // is federal state specific, add to the specific federal state
      relevantFederalStatesForNotificationCategory.forEach(
          property -> {
            String federalStateCode = ((Coding) property.getValue()).getCode();
            if (federalStateNotificationCategories.containsKey(federalStateCode)) {
              federalStateNotificationCategories
                  .get(federalStateCode)
                  .add(
                      CodeDisplay.builder()
                          .code(code)
                          .display(conceptDefinitionComponent.getDisplay())
                          .designations(getDesignations(conceptDefinitionComponent))
                          .order(extractOrder(conceptDefinitionComponent))
                          .build());
            } else {
              log.error(
                  "Fatal: Federal state code {} not found in federal state notification categories",
                  federalStateCode);
            }
          });
    }
  }

  private void addFederalStateDataToMapAndList(CodeSystem.ConceptDefinitionComponent entry) {
    federalStateNotificationCategories.put(entry.getCode(), new ArrayList<>());
    federalStates.add(
        CodeDisplay.builder().code(entry.getCode()).display(entry.getDisplay()).build());
  }
}
