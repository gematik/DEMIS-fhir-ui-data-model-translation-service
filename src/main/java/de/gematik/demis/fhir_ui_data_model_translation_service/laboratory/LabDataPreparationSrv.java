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
 * #L%
 */

import static de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils.createTestDataForErrorCase;
import static de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils.createTestDataForSorting;
import static de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils.extractOrder;
import static de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils.getCodeFromFileName;
import static java.util.Collections.*;

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.Designation;
import de.gematik.demis.fhir_ui_data_model_translation_service.utils.SnapshotFilesService;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.hl7.fhir.r4.model.ValueSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** This service provides specific data for a notification category code. */
@Service
@Slf4j
public class LabDataPreparationSrv {

  private static final Pattern METHOD_PATTERN = Pattern.compile("ValueSet-method(.{4})\\.json");
  private static final Pattern MATERIAL_PATTERN = Pattern.compile("ValueSet-material(.{4})\\.json");
  private static final Pattern ANSWER_SET_PATTERN =
      Pattern.compile("ValueSet-answerSet(.{4})\\.json");
  private static final Pattern SUBSTANCE_PATTERN =
      Pattern.compile("ValueSet-substance(.{4})\\.json");
  private static final Pattern RESISTANCE_PATTERN =
      Pattern.compile("ValueSet-resistance(.{4})\\.json");
  private static final Pattern RESISTANCEGENE_PATTERN =
      Pattern.compile("ValueSet-resistanceGene(.{4})\\.json");

  private final SnapshotFilesService snapshotFilesService;
  private final NotificationCategoryList notificationCategoryList;
  private final FhirContext fhirContext;
  private final boolean addTestDataErrorCase;
  private final boolean addTestDataSortingCase;
  private final boolean isNotification73ProcessingActive;
  @Getter private Map<String, LabNotificationData> laboratoryDataMap; // 7.3, 7.1
  private Map<PathogenNotificationCategory, SequencedCollection<CodeDisplay>>
      pathogenNotificationCategories;
  private List<CodeDisplay> notificationCategories;

  public LabDataPreparationSrv(
      FhirContext fhirContext,
      SnapshotFilesService snapshotFilesService,
      NotificationCategoryList notificationCategoryList,
      @Value("${add.test.data.error.case.for.lab}") boolean addTestDataErrorCase,
      @Value("${add.test.data.laboratory.sorting}") boolean addTestDataSortingCase,
      @Value("${feature.flag.notification7_3}") boolean isNotification73ProcessingActive) {
    this.fhirContext = fhirContext;
    this.snapshotFilesService = snapshotFilesService;
    this.notificationCategoryList = notificationCategoryList;
    this.addTestDataErrorCase = addTestDataErrorCase;
    this.addTestDataSortingCase = addTestDataSortingCase;
    this.isNotification73ProcessingActive = isNotification73ProcessingActive;
  }

  @PostConstruct
  protected void initializeData() {
    this.laboratoryDataMap = new HashMap<>();
    this.notificationCategories = new ArrayList<>();

    this.pathogenNotificationCategories =
        notificationCategoryList.getPathogenNotificationCategories();
    if (isNotification73ProcessingActive) {
      this.notificationCategories =
          pathogenNotificationCategories.values().stream()
              .flatMap(SequencedCollection::stream)
              .toList();
    }

    // remove this block with feature.flag.notification7_3
    if ((!this.isNotification73ProcessingActive) || notificationCategories.isEmpty()) {
      this.notificationCategories = notificationCategoryList.getPathogenNotificationCategoryList();
    }

    Map<String, List<CodeDisplay>> methodMap = createCodeToMethodMap();
    Map<String, List<CodeDisplay>> materialMap = createCodeToMaterialMap();
    Map<String, List<CodeDisplay>> answerSetMap = createCodeToAnswerSetMap();
    Map<String, List<CodeDisplay>> substanceMap = createCodeToSubstanceMap();
    Map<String, List<CodeDisplay>> resistanceMap = createCodeToResistanceMap();
    Map<String, List<CodeDisplay>> resistanceGeneMap = createCodeToResistanceGeneMap();

    if (addTestDataErrorCase) {
      log.info("addTestDataErrorCase");
      addTestDataErrorCase(methodMap, materialMap, answerSetMap, substanceMap);
    }

    if (addTestDataSortingCase) {
      log.info("addTestDataSortingCase");
      addTestDataSortingCase();
    }

    for (var cd : this.notificationCategories) {
      String code = cd.getCode();

      List<CodeDisplay> methodList = methodMap.getOrDefault(code, emptyList());
      List<CodeDisplay> materialList = materialMap.getOrDefault(code, emptyList());
      List<CodeDisplay> answerSetList = answerSetMap.getOrDefault(code, emptyList());
      List<CodeDisplay> substanceList = substanceMap.getOrDefault(code, emptyList());
      List<CodeDisplay> resistanceList = resistanceMap.getOrDefault(code, emptyList());
      List<CodeDisplay> resistanceGeneList = resistanceGeneMap.getOrDefault(code, emptyList());

      List<String> headerList = cd.extractHeadersFromCodeDisplay();
      String subheaderOpt = headerList.size() > 1 ? headerList.get(1) : null;

      LabNotificationData laboratoryJsonDataModel =
          new LabNotificationData(
              cd,
              headerList.get(0),
              subheaderOpt,
              methodList,
              materialList,
              answerSetList,
              substanceList,
              resistanceList,
              resistanceGeneList);

      if (laboratoryJsonDataModel.isUseable()) {
        log.warn("Notification category {} is not useable", code);
        laboratoryDataMap.put(code, laboratoryJsonDataModel);
      }
    }
  }

  public SequencedCollection<CodeDisplay> getNotificationCategories(
      PathogenNotificationCategory category) {
    return pathogenNotificationCategories.getOrDefault(category, emptyList());
  }

  private void addTestDataErrorCase(
      Map<String, List<CodeDisplay>> methodMap,
      Map<String, List<CodeDisplay>> materialMap,
      Map<String, List<CodeDisplay>> answerSetMap,
      Map<String, List<CodeDisplay>> substanceMap) {

    List<CodeDisplay> method =
        List.of(
            CodeDisplay.builder().code("testDataMethod").display("testDataMethodDisplay").build());
    List<CodeDisplay> material =
        List.of(
            CodeDisplay.builder()
                .code("testDataMaterial")
                .display("testDataMaterialDisplay")
                .build());
    List<CodeDisplay> answerSet = Collections.emptyList();
    List<CodeDisplay> substances =
        List.of(
            CodeDisplay.builder().code("testDataAnswer").display("testDataAnswerDisplay").build());

    methodMap.put("abcd", method);
    materialMap.put("abcd", material);
    answerSetMap.put("abcd", answerSet);
    substanceMap.put("abcd", substances);

    List<CodeDisplay> tmpnotificationCategories = new ArrayList<>();
    tmpnotificationCategories.addAll(this.notificationCategories);
    tmpnotificationCategories.add(createTestDataForErrorCase());
    this.notificationCategories = tmpnotificationCategories;
  }

  private void addTestDataSortingCase() {
    List<CodeDisplay> tmpnotificationCategories = new ArrayList<>();
    tmpnotificationCategories.addAll(this.notificationCategories);
    tmpnotificationCategories.add(createTestDataForSorting());
    this.notificationCategories = tmpnotificationCategories;
  }

  /**
   * @deprecated use {@link NotificationCategoryList#getNotificationCategories()}
   */
  @Deprecated(forRemoval = true)
  public List<CodeDisplay> getNotificationCategories() {
    return notificationCategories.stream()
        .sorted(
            Comparator.comparing(
                (CodeDisplay cd) ->
                    cd.extractGermanDesignations().stream()
                        .findFirst()
                        .map(Designation::value)
                        .orElse(cd.getDisplay())))
        .toList();
  }

  private Map<String, List<CodeDisplay>> createCodeToMethodMap() {

    return processFileList(this.snapshotFilesService.getMethods(), METHOD_PATTERN);
  }

  private Map<String, List<CodeDisplay>> createCodeToMaterialMap() {
    return processFileList(this.snapshotFilesService.getMaterials(), MATERIAL_PATTERN);
  }

  private Map<String, List<CodeDisplay>> createCodeToAnswerSetMap() {
    return processFileList(this.snapshotFilesService.getAnswerSets(), ANSWER_SET_PATTERN);
  }

  private Map<String, List<CodeDisplay>> createCodeToSubstanceMap() {
    return processFileList(this.snapshotFilesService.getSubstances(), SUBSTANCE_PATTERN);
  }

  private Map<String, List<CodeDisplay>> createCodeToResistanceMap() {
    return processFileList(this.snapshotFilesService.getResistances(), RESISTANCE_PATTERN);
  }

  private Map<String, List<CodeDisplay>> createCodeToResistanceGeneMap() {
    return processFileList(this.snapshotFilesService.getResistanceGenes(), RESISTANCEGENE_PATTERN);
  }

  private Map<String, List<CodeDisplay>> processFileList(List<File> fileList, Pattern pattern) {
    if (fileList == null) {
      return Collections.emptyMap();
    }
    Map<String, List<CodeDisplay>> resultMap = new HashMap<>();
    fileList.forEach(file -> addLaboratoryJsonDataModel(file, pattern, resultMap));
    return resultMap;
  }

  private void addLaboratoryJsonDataModel(
      File file, Pattern pattern, Map<String, List<CodeDisplay>> map) {

    Optional<String> codeOptional = getCodeFromFileName(file, pattern);

    if (codeOptional.isPresent()) {
      // get Data from file

      List<CodeDisplay> codeDisplayList = getCodeValuesOfAValueSet(file);
      if (!codeDisplayList.isEmpty()) {
        List<CodeDisplay> listToAdd = filterAndSortList(codeDisplayList);
        map.put(codeOptional.get().toLowerCase(), listToAdd);
      }
    }
  }

  private List<CodeDisplay> filterAndSortList(List<CodeDisplay> codeDisplayList) {
    return codeDisplayList.stream()
        .filter(codeDisplay -> codeDisplay.getOrder() != 0)
        .sorted(Comparator.comparingInt(CodeDisplay::getOrder).reversed())
        .toList();
  }

  private List<CodeDisplay> getCodeValuesOfAValueSet(File file) {
    try {
      String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
      ValueSet valueSet = fhirContext.newJsonParser().parseResource(ValueSet.class, json);

      List<CodeDisplay> codeDisplays =
          valueSet.getCompose().getInclude().get(0).getConcept().stream()
              .map(
                  component ->
                      CodeDisplay.builder()
                          .code(component.getCode())
                          .display(component.getDisplay())
                          .designations(extractDesignations(component.getDesignation()))
                          .order(extractOrder(component))
                          .build())
              .toList();
      return filterAndSortList(codeDisplays);
    } catch (IOException e) {
      log.error("error while reading file {}", file.getName());
      return emptyList();
    }
  }

  private Set<Designation> extractDesignations(
      List<ValueSet.ConceptReferenceDesignationComponent> designations) {
    return designations.stream()
        .map(
            designationCompoment ->
                new Designation(
                    designationCompoment.getLanguage(), designationCompoment.getValue()))
        .collect(Collectors.toSet());
  }
}
