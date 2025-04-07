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
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static de.gematik.demis.fhir_ui_data_model_translation_service.model.Designation.getDesignations;
import static de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils.NOTIFICATION_CATEGORY_PROPERTY;
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
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.CodeSystem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FederalStateNotificationCategoryData {

  private final SnapshotFilesService snapshotFilesService;
  private final FhirContext fhirContext;
  private final boolean filterCodesActive;
  private final String denyList;
  private final boolean addTestData;
  private final boolean addTestDataSortingCase;
  private List<String> denyListNotificationCategory;
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

  private boolean isIfsg71Paragraph(CodeSystem.ConceptDefinitionComponent notificationCategory) {
    final Optional<CodeSystem.ConceptPropertyComponent> ifsgParagraph =
        notificationCategory.getProperty().stream()
            .filter(p -> p.getCode().equals(NOTIFICATION_CATEGORY_PROPERTY))
            .findFirst();
    if (ifsgParagraph.isPresent()) {
      final Optional<PathogenNotificationCategory> target =
          ifsgParagraph.map(
              ifsgParagraphConcept ->
                  PathogenNotificationCategory.from(ifsgParagraphConcept.getValue().toString()));
      return target.isPresent() && target.get().equals(PathogenNotificationCategory.P_7_1);
    } else {
      return true;
    }
  }

  @PostConstruct
  protected void createData() {
    federalStateNotificationCategories = new LinkedHashMap<>();
    federalStates = new ArrayList<>();
    denyListNotificationCategory = new ArrayList<>(Arrays.asList(denyList.split(",")));

    File federalStateNotificationCategoryFile = snapshotFilesService.getFederalStateFile();
    File noticationCategoryFile = snapshotFilesService.getProfileNotificationCategoryFile();

    try {
      String federalStateFile = Utils.getFileString(federalStateNotificationCategoryFile);
      String notificationCategoryString = Utils.getFileString(noticationCategoryFile);

      CodeSystem federalStateCodeSystems =
          fhirContext.newJsonParser().parseResource(CodeSystem.class, federalStateFile);
      CodeSystem notificationCategory =
          fhirContext.newJsonParser().parseResource(CodeSystem.class, notificationCategoryString);

      federalStateCodeSystems
          .getConcept()
          .forEach(concept -> addCategoryList(concept, notificationCategory));

    } catch (IOException e) {
      log.error("Error in pathogen data creation while reading files: {}", e.getMessage());
    }
  }

  private void addCategoryList(
      CodeSystem.ConceptDefinitionComponent concept, CodeSystem notificationCategoryList) {

    // get federal state data and add to list
    String federalState = concept.getCode();
    CodeDisplay federalStateCodeDisplay =
        CodeDisplay.builder().code(federalState).display(concept.getDisplay()).build();
    federalStates.add(federalStateCodeDisplay);

    // filter for relevant notification categories for the federal state
    List<CodeSystem.ConceptDefinitionComponent> relevantNotifications =
        notificationCategoryList.getConcept().stream()
            .filter(
                notificationCategory ->
                    !filterCodesActive
                        || !denyListNotificationCategory.contains(notificationCategory.getCode()))
            .filter(
                notificationCategory ->
                    isRelevantForFederalState(notificationCategory, federalState))
            .filter(this::isIfsg71Paragraph)
            .toList();

    List<CodeDisplay> relevantCodeDisplays = new ArrayList<>();

    // create code display for each relevant notification category
    relevantNotifications.forEach(
        relevantNotification ->
            relevantCodeDisplays.add(
                CodeDisplay.builder()
                    .code(relevantNotification.getCode())
                    .display(relevantNotification.getDisplay())
                    .designations(getDesignations(relevantNotification))
                    .order(extractOrder(relevantNotification))
                    .build()));

    if (addTestData) {
      log.info("adding test data for error case");
      CodeDisplay testDataCodeDisplay = createTestDataForErrorCase();
      relevantCodeDisplays.add(testDataCodeDisplay);
    }

    if (addTestDataSortingCase) {
      log.info("adding test data for sorting case");
      CodeDisplay testDataGAPP = createTestDataForSorting();
      relevantCodeDisplays.add(testDataGAPP);
    }

    // sort relevant notification categories for concept order
    List<CodeDisplay> sortedList =
        relevantCodeDisplays.stream()
            .sorted(
                Comparator.comparing(CodeDisplay::getOrder)
                    .reversed()) // reversed so descending order is active
            .toList();
    federalStateNotificationCategories.put(federalState, sortedList);
    log.info(
        "added federal state {}|{} with size {}: {}",
        federalState,
        federalStateCodeDisplay.getDisplay(),
        sortedList.size(),
        sortedList.stream()
            .map(CodeDisplay::getCode)
            .sorted()
            .collect(Collectors.joining(", ", "[", "]")));
  }

  private boolean isRelevantForFederalState(
      CodeSystem.ConceptDefinitionComponent notificationCategory, String federalState) {
    List<CodeSystem.ConceptPropertyComponent> property = notificationCategory.getProperty();
    List<CodeSystem.ConceptPropertyComponent> federalStateList =
        property.stream().filter(p -> p.getCode().equals("federal-state")).toList();
    if (federalStateList.isEmpty()) {
      return true;
    }
    log.info(
        "checking federal state {} with notification category {}",
        federalState,
        notificationCategory.getCode());
    return federalStateList.stream()
        .anyMatch(p -> p.getValueCoding().getCode().equals(federalState));
  }
}
