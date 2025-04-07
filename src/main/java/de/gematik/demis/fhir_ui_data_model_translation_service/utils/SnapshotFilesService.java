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

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SnapshotFilesService {

  private static final String RESISTANCE = "resistance";
  private static final String RESISTANCE_GENE = "resistanceGene";
  private static final String METHOD = "method";
  private static final String SUBSTANCE = "substance";
  private static final String ANSWER_SET = "answerSet";
  private static final String GENE = "Gene";
  private final String profileSourcePath;
  private final String profileLoincFileName;
  private final String profileNotificationCategoryFileName;
  private final String profileNotificationDiseaseCategoryFileName;
  private final String federalStateFileName;
  private final boolean addTestData;
  private final String testDataSourcePath;

  @Getter private List<File> methods;
  @Getter private List<File> materials;
  @Getter private List<File> answerSets;
  @Getter private List<File> substances;
  @Getter private List<File> questionnaires;
  @Getter private List<File> conceptMaps;
  @Getter private List<File> resistances;
  @Getter private List<File> resistanceGenes;
  @Getter private File profileNotificationCategoryFile;
  @Getter private File profileDiseaseNotificationCategoryFile;
  @Getter private File profileLoincFile;
  @Getter private File federalStateFile;
  @Getter private List<File> rawFiles;

  public SnapshotFilesService(
      @Value("${data.path.profile.root}") String profileSourcePath,
      @Value("${data.loinc.file.name}") String profileLoincFileName,
      @Value("${data.notification.category.file.name}") String profileNotificationCategoryFileName,
      @Value("${data.notification.disease.category.file.name}")
          String profileNotificationDiseaseCategoryFileName,
      @Value("${data.notification.federal.state.list.name}") String federalStateFileName,
      @Value("${add.test.data.laboratory.sorting}") boolean addTestData,
      @Value("${data.path.gapp.data}") String testDataSourcePath) {

    this.profileSourcePath = profileSourcePath;
    this.profileLoincFileName = profileLoincFileName;
    this.profileNotificationCategoryFileName = profileNotificationCategoryFileName;
    this.profileNotificationDiseaseCategoryFileName = profileNotificationDiseaseCategoryFileName;
    this.federalStateFileName = federalStateFileName;
    this.addTestData = addTestData;
    this.testDataSourcePath = testDataSourcePath;
  }

  private static List<File> collectAll(File[] files) {
    List<File> resultList = new ArrayList<>();
    // Sort files by name to ensure that the order is always the same
    // This is especially important for the code system files
    final List<File> sortedFiles = Arrays.stream(files).sorted().toList();
    for (File file : sortedFiles) {
      log.debug("read file {}", file.getAbsolutePath());
      if (file.isDirectory()) {
        log.debug("Found folder {}", file.getAbsolutePath());
        resultList.addAll(collectAll(file.listFiles())); // Calls same method again.
      } else {
        log.debug("Found file: {}", file.getAbsolutePath());
        resultList.add(file);
      }
    }
    return resultList;
  }

  @PostConstruct
  void init() {
    File rootDir = new File(profileSourcePath);
    rawFiles = collectAll(Objects.requireNonNull(rootDir.listFiles()));

    if (addTestData && testDataSourcePath != null) {
      File gappRoute = new File(testDataSourcePath);
      if (gappRoute.exists()) {
        rawFiles.addAll(collectAll(Objects.requireNonNull(gappRoute.listFiles())));
      } else {
        log.warn("No test data found at {}", testDataSourcePath);
      }
    }

    materials = new ArrayList<>();
    methods = new ArrayList<>();
    answerSets = new ArrayList<>();
    substances = new ArrayList<>();
    questionnaires = new ArrayList<>();
    conceptMaps = new ArrayList<>();
    resistances = new ArrayList<>();
    resistanceGenes = new ArrayList<>();

    for (File file : rawFiles) {
      checkAndSetCodeSystemFiles(profileLoincFileName, profileNotificationCategoryFileName, file);
      checkAndSetStructureDefinitionsOrValueSets(file);
      checkAndSetQuestionnaires(file);
      checkAndSetConceptMaps(file);
    }
  }

  private void checkAndSetConceptMaps(File file) {
    String name = file.getName();
    log.info("analysing {} for concept maps", name);
    if (name.contains("ConceptMap")) {
      log.info("{} saved as concept map", file.getAbsolutePath());
      conceptMaps.add(file);
    }
  }

  private void checkAndSetQuestionnaires(File file) {
    String name = file.getName();
    log.info("analysing {} for questionnaire", name);
    if (name.contains("Questionnaire")) {
      log.info("{} saved as questionnaire", file.getAbsolutePath());
      questionnaires.add(file);
    }
  }

  private void checkAndSetStructureDefinitionsOrValueSets(File file) {
    String name = file.getName();
    log.info("analysing {} for value set", name);
    if (name.contains("material")) {
      log.info("{} saved as material", file.getAbsolutePath());
      materials.add(file);
    } else if (name.contains(ANSWER_SET)) {
      log.info("{} saved as answerSet", file.getAbsolutePath());
      answerSets.add(file);
    } else if (name.contains(SUBSTANCE)) {
      log.info("{} saved as substance", file.getAbsolutePath());
      substances.add(file);
    } else if (name.contains(METHOD)) {
      log.info("{} saved as method", file.getAbsolutePath());
      methods.add(file);
    } else if (name.contains(RESISTANCE) && !name.contains(GENE)) {
      log.info("{} saved as resistance", file.getAbsolutePath());
      resistances.add(file);
    } else if (name.contains(RESISTANCE_GENE)) {
      log.info("{} saved as resistanceGene", file.getAbsolutePath());
      resistanceGenes.add(file);
    }
  }

  private void checkAndSetCodeSystemFiles(
      String profileLoincFileName, String profileNotificationCategoryFileName, File file) {
    String name = file.getName();
    log.info("analysing {} for code systems", name);
    if (file.getAbsolutePath().contains("CodeSystem")) {
      if (name.contains(profileLoincFileName)) {
        profileLoincFile = file;
        log.info("Found Loinc File {}", profileLoincFile.getAbsolutePath());
      } else if (profileNotificationCategoryFileName.equals(name)) {
        profileNotificationCategoryFile = file;
        log.info(
            "Found Notification Category File {}",
            profileNotificationCategoryFile.getAbsolutePath());
      } else if (profileNotificationDiseaseCategoryFileName.equals(name)) {
        profileDiseaseNotificationCategoryFile = file;
        log.info(
            "Found Disease Notification Category File {}",
            profileDiseaseNotificationCategoryFile.getAbsolutePath());
      } else if (federalStateFileName.equals(name)) {
        federalStateFile = file;
        log.info(
            "Found Federal State Notification Category File {}",
            federalStateFile.getAbsolutePath());
      }
    }
  }
}
