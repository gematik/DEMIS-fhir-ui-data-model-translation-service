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

import static de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils.collectAll;

import de.gematik.demis.fhir_ui_data_model_translation_service.laboratory.ValueSetFileCategorizer;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for managing snapshot files used in the application. This class categorizes and processes
 * various types of files such as CodeSystem, ValueSet, and ConceptMap. It also handles the addition
 * of test data for laboratory and disease categories.
 */
@Service
@Slf4j
public class SnapshotFilesService {

  // Constants representing different file categories.
  public static final String METHOD = "method";
  public static final String MATERIAL = "material";
  public static final String ANSWER_SET = "answerSet";
  public static final String SUBSTANCE = "substance";
  public static final String RESISTANCE = "resistance";
  public static final String RESISTANCE_GENE = "resistanceGene";

  // Configuration properties injected via @Value annotations.
  private final String profileSourcePath;
  private final String profileLoincFileName;
  private final String profileNotificationCategoryRegressionFileName;
  private final String profileNotificationDiseaseCategoryCodeSystemFileName;
  private final String federalStateFileName;
  private final boolean addTestData;
  private final boolean addDiseaseTestData;
  private final String gappTestDataSourcePath;
  private final String gapdTestDataSourcePath;
  private final String profileNotificationCategoryValueSetFileName;
  private final String profileNotificationCategoryNonNominalValueSetFileName;
  private final String profileNotificationDiseaseCategoryValueSetFileName;
  private final String profileNotificationDiseaseCategoryNonNominalValueSetFileName;

  // Lists to store categorized files.
  @Getter private List<File> methods = new ArrayList<>();
  @Getter private List<File> materials = new ArrayList<>();
  @Getter private List<File> answerSets = new ArrayList<>();
  @Getter private List<File> substances = new ArrayList<>();
  @Getter private List<File> questionnaires = new ArrayList<>();
  @Getter private List<File> conceptMaps = new ArrayList<>();
  @Getter private List<File> resistances = new ArrayList<>();
  @Getter private List<File> resistanceGenes = new ArrayList<>();
  @Getter private List<File> codeSystemFiles = new ArrayList<>();
  @Getter private List<File> valueSetFiles = new ArrayList<>();
  @Getter private File profileNotificationCategoryCodeSystemFile;
  @Getter private File profileDiseaseNotificationCategoryRegressionFile;
  @Getter private File profileNotificationCategoryValueSetFile;
  @Getter private File profileNotificationCategoryNonNomimalValueSetFile;
  @Getter private File profileNotificationDiseaseCategoryValueSetFile;
  @Getter private File profileNotificationDiseaseCategoryNonNominalValueSetFile;
  @Getter private File profileLoincFile;
  @Getter private File federalStateFile;
  @Getter private List<File> rawFiles;

  /**
   * Constructor for SnapshotFilesService. Initializes the service with configuration properties.
   *
   * @param profileSourcePath Path to the root directory containing profile files.
   * @param profileLoincFileName Name of the LOINC file.
   * @param profileNotificationCategoryCodeSystemFileName Name of the notification category
   *     CodeSystem file.
   * @param profileNotificationCategoryValueSetFileName Name of the notification category ValueSet
   *     file (7.1).
   * @param profileNotificationCategoryNonNominalValueSetFileName Name of the non-nominal
   *     notification category ValueSet file (7.3).
   * @param profileNotificationDiseaseCategoryValueSetFileName Name of the disease category ValueSet
   *     file (6.1).
   * @param profileNotificationDiseaseCategoryNonNominalValueSetFileName Name of the non-nominal
   *     disease category ValueSet file (7.3).
   * @param profileNotificationDiseaseCategoryCodeSystemFileName Name of the disease category
   *     CodeSystem file.
   * @param federalStateFileName Name of the federal state file.
   * @param addTestData Flag indicating whether to add laboratory test data.
   * @param addDiseaseTestData Flag indicating whether to add disease test data.
   * @param testDataSourcePathGAPP Path to the GAPP test data source.
   * @param testDataSourcePathGAPD Path to the GAPD test data source.
   */
  public SnapshotFilesService(
      @Value("${data.path.profile.root}") String profileSourcePath,
      @Value("${data.loinc.file.name}") String profileLoincFileName,
      @Value("${data.notification.category.file.name}")
          String profileNotificationCategoryCodeSystemFileName,
      @Value("${data.notification.laboratory.category.7.1.file.name}")
          String profileNotificationCategoryValueSetFileName,
      @Value("${data.notification.laboratory.category.7.3.file.name}")
          String profileNotificationCategoryNonNominalValueSetFileName,
      @Value("${data.notification.disease.category.6.1.file.name}")
          String profileNotificationDiseaseCategoryValueSetFileName,
      @Value("${data.notification.disease.category.7.3.file.name}")
          String profileNotificationDiseaseCategoryNonNominalValueSetFileName,
      @Value("${data.notification.disease.category.file.name}")
          String profileNotificationDiseaseCategoryCodeSystemFileName,
      @Value("${data.notification.federal.state.list.name}") String federalStateFileName,
      @Value("${add.test.data.laboratory.sorting}") boolean addTestData,
      @Value("${add.test.data.disease}") boolean addDiseaseTestData,
      @Value("${data.path.gapp.data}") String testDataSourcePathGAPP,
      @Value("${data.path.gapd.data}") String testDataSourcePathGAPD) {

    this.profileSourcePath = profileSourcePath;
    this.profileLoincFileName = profileLoincFileName;
    this.profileNotificationCategoryRegressionFileName =
        profileNotificationCategoryCodeSystemFileName;
    this.profileNotificationDiseaseCategoryCodeSystemFileName =
        profileNotificationDiseaseCategoryCodeSystemFileName;
    this.federalStateFileName = federalStateFileName;
    this.addTestData = addTestData;
    this.addDiseaseTestData = addDiseaseTestData;
    this.gappTestDataSourcePath = testDataSourcePathGAPP;
    this.gapdTestDataSourcePath = testDataSourcePathGAPD;

    this.profileNotificationCategoryValueSetFileName = profileNotificationCategoryValueSetFileName;
    this.profileNotificationCategoryNonNominalValueSetFileName =
        profileNotificationCategoryNonNominalValueSetFileName;
    this.profileNotificationDiseaseCategoryValueSetFileName =
        profileNotificationDiseaseCategoryValueSetFileName;
    this.profileNotificationDiseaseCategoryNonNominalValueSetFileName =
        profileNotificationDiseaseCategoryNonNominalValueSetFileName;
  }

  /**
   * Initializes the service after construction. Categorizes and processes files from the configured
   * root directory.
   */
  @PostConstruct
  void init() {
    File rootDir = new File(profileSourcePath);
    rawFiles =
        collectAll(
            Objects.requireNonNull(
                rootDir.listFiles(), "Profile source path is invalid: " + profileSourcePath));
    ValueSetFileCategorizer valueSetFileCategorizer = new ValueSetFileCategorizer();

    addLaboratoryTestData();

    addDiseaseTestData();

    for (File file : rawFiles) {
      processRawFiles(file, valueSetFileCategorizer);
    }

    methods = valueSetFileCategorizer.getFilesForCategory(METHOD);
    materials = valueSetFileCategorizer.getFilesForCategory(MATERIAL);
    answerSets = valueSetFileCategorizer.getFilesForCategory(ANSWER_SET);
    substances = valueSetFileCategorizer.getFilesForCategory(SUBSTANCE);
    resistances = valueSetFileCategorizer.getFilesForCategory(RESISTANCE);
    resistanceGenes = valueSetFileCategorizer.getFilesForCategory(RESISTANCE_GENE);
  }

  /**
   * Processes raw files and categorizes them based on their names.
   *
   * @param file The file to process.
   * @param valueSetFileCategorizer The categorizer for ValueSet files.
   */
  private void processRawFiles(File file, ValueSetFileCategorizer valueSetFileCategorizer) {
    if (file.getName().contains("CodeSystem")) {
      codeSystemFiles.add(file);
      checkAndSetCodeSystemFiles(
          profileLoincFileName, profileNotificationCategoryRegressionFileName, file);
    }
    if (file.getName().contains("Category")) {
      checkAndSetNotificationCategoryFiles(file);
    }
    if (file.getName().contains("Questionnaire")) {
      checkAndSetQuestionnaires(file);
    }
    if (file.getName().contains("ConceptMap")) {
      checkAndSetConceptMaps(file);
    }
    if (file.getName().contains("ValueSet")) {
      valueSetFiles.add(file);
      valueSetFileCategorizer.categorizeFile(file);
    }
  }

  /** Adds disease test data to the raw files list if enabled. */
  private void addDiseaseTestData() {
    if (addDiseaseTestData && (gapdTestDataSourcePath != null)) {
      log.error("Disease test data is no longer supported!");
    }
  }

  /** Adds laboratory test data to the raw files list if enabled. */
  private void addLaboratoryTestData() {
    if (addTestData) {
      rawFiles.addAll(new LaboratoryTestData(gappTestDataSourcePath).getFiles());
    }
  }

  /**
   * Checks and sets ConceptMap files based on their names.
   *
   * @param file The file to check.
   */
  private void checkAndSetConceptMaps(File file) {
    String name = file.getName();
    log.info("analysing {} for concept maps", name);
    if (name.contains("ConceptMap")) {
      log.info("{} saved as concept map", file.getAbsolutePath());
      conceptMaps.add(file);
    }
  }

  /**
   * Checks and sets Questionnaire files based on their names.
   *
   * @param file The file to check.
   */
  private void checkAndSetQuestionnaires(File file) {
    String name = file.getName();
    log.info("analysing {} for questionnaire", name);
    if (name.contains("Questionnaire")) {
      log.info("{} saved as questionnaire", file.getAbsolutePath());
      questionnaires.add(file);
    }
  }

  /**
   * Checks and sets Notification Category files based on their names.
   *
   * @param file The file to check.
   */
  private void checkAndSetNotificationCategoryFiles(File file) {
    String name = file.getName();
    log.info("analysing {} for value set", name);
    if (profileNotificationCategoryValueSetFileName.equals(name)) {
      profileNotificationCategoryValueSetFile = file;
      log.info(
          "Found Notification Category 7.1 File {}",
          profileNotificationCategoryValueSetFile.getAbsolutePath());
    } else if (profileNotificationCategoryNonNominalValueSetFileName.equals(name)) {
      profileNotificationCategoryNonNomimalValueSetFile = file;
      log.info(
          "Found Notification Category 7.3 File {}",
          profileNotificationCategoryNonNomimalValueSetFile.getAbsolutePath());
    } else if (profileNotificationDiseaseCategoryValueSetFileName.equals(name)) {
      profileNotificationDiseaseCategoryValueSetFile = file;
      log.info(
          "Found Notification Disease Category 6.1 File {}",
          profileNotificationDiseaseCategoryValueSetFile.getAbsolutePath());
    } else if (profileNotificationDiseaseCategoryNonNominalValueSetFileName.equals(name)) {
      profileNotificationDiseaseCategoryNonNominalValueSetFile = file;
      log.info(
          "Found Notification Disease Category 7.3 File {}",
          profileNotificationDiseaseCategoryNonNominalValueSetFile.getAbsolutePath());
    }
  }

  /**
   * Checks and sets CodeSystem files based on their names.
   *
   * @param profileLoincFileName Name of the LOINC file.
   * @param profileNotificationCategoryFileName Name of the notification category file.
   * @param file The file to check.
   */
  private void checkAndSetCodeSystemFiles(
      String profileLoincFileName, String profileNotificationCategoryFileName, File file) {
    String name = file.getName();
    log.info("analysing {} for code systems", name);
    if (file.getAbsolutePath().contains("CodeSystem")) {
      if (name.contains(profileLoincFileName)) {
        profileLoincFile = file;
        log.info("Found Loinc File {}", profileLoincFile.getAbsolutePath());
      } else if (profileNotificationCategoryFileName.equals(name)) {
        profileNotificationCategoryCodeSystemFile = file;
        log.info(
            "Found Notification Category File {}",
            profileNotificationCategoryCodeSystemFile.getAbsolutePath());
      } else if (profileNotificationDiseaseCategoryCodeSystemFileName.equals(name)) {
        profileDiseaseNotificationCategoryRegressionFile = file;
        log.info(
            "Found Disease Notification Category File {}",
            profileDiseaseNotificationCategoryRegressionFile.getAbsolutePath());
      } else if (federalStateFileName.equals(name)) {
        federalStateFile = file;
        log.info(
            "Found Federal State Notification Category File {}",
            federalStateFile.getAbsolutePath());
      }
    }
  }
}
