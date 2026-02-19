package de.gematik.demis.fhir_ui_data_model_translation_service.translation;

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

import static de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils.extractOrder;
import static de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils.getFileString;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import de.gematik.demis.fhir_ui_data_model_translation_service.FeatureFlags;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.Designation;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.Use;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.CodeSystem;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
@Slf4j
class CodeSystems {

  private final LinkedHashSet<File> codeSystemFiles;
  private final FhirContext fhirContext;
  private final List<String> excludedCodeSystems;
  private final FeatureFlags featureFlags;

  /**
   * map that contains pairs of supplemented code systems and supplement code system. the key is the
   * code systems that gets the supplement. the value is the code system that supplements the key
   * code system
   */
  private final Map<String, String> codeSystemToSupplement = new HashMap<>();

  @Getter private Map<String, Map<String, CodeDisplay>> codeSystemData = new ConcurrentHashMap<>();
  @Getter private Map<String, String> codeSystemVersions = new ConcurrentHashMap<>();

  private static Map<String, CodeDisplay> sortCodeSystemEntries(
      Map<String, CodeDisplay> keyToCodeDisplayMap) {
    return keyToCodeDisplayMap.entrySet().stream()
        .sorted(
            Comparator.comparingInt(
                    (Map.Entry<String, CodeDisplay> entry) -> entry.getValue().getOrder())
                .reversed()) // reversed because we have to sort in descending order
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (oldValue, newValue) -> oldValue,
                LinkedHashMap::new));
  }

  protected static @Nullable Use extractUseOrNull(
      CodeSystem.ConceptDefinitionDesignationComponent conceptDesignation) {
    return conceptDesignation.getUse() != null
            && conceptDesignation.getUse().getCode() != null
            && conceptDesignation.getUse().getSystem() != null
        ? Use.toUse(conceptDesignation.getUse())
        : null;
  }

  private void addDesignations(
      CodeSystem.ConceptDefinitionComponent concept, CodeDisplay codeDisplay) {
    // check for designations and add designations to code display
    if (!concept.getDesignation().isEmpty()) {
      List<CodeSystem.ConceptDefinitionDesignationComponent> designation = concept.getDesignation();
      Set<Designation> designations = new LinkedHashSet<>();
      final boolean addDesignationUseData = featureFlags.isAddDesignationUse();
      for (var conceptDesignation : designation) {
        if (addDesignationUseData) {
          Use use = extractUseOrNull(conceptDesignation);
          designations.add(
              new Designation(
                  conceptDesignation.getLanguage(), conceptDesignation.getValue(), use));
        } else {
          if (conceptDesignation.getUse() != null
              && (conceptDesignation.getUse().getCode() != null
                      && !conceptDesignation.getUse().getCode().equals("FullySpecifiedName")
                  || conceptDesignation.getUse().getCode() == null)) {
            designations.add(
                new Designation(conceptDesignation.getLanguage(), conceptDesignation.getValue()));
          }
        }
      }
      codeDisplay.setDesignations(designations);
    }
  }

  void addCodeSystem(String system, CodeDisplayMapWithVersion codeDisplays) {
    Map<String, CodeDisplay> codeSystem = codeDisplays.codeDisplayMap();
    codeSystemData.put(system, codeSystem);
    codeSystemData.put(system + "|" + codeDisplays.version(), codeSystem);
    sortCodeSystemData();

    // check for supplements for new CodeSystem
    String supplementKey = codeSystemToSupplement.get(system);
    if (supplementKey == null) {
      supplementKey = codeSystemToSupplement.get(system + "|" + codeDisplays.version());
    }

    if (supplementKey != null) {
      Map<String, CodeDisplay> supplement = codeSystemData.get(supplementKey);

      processSupplementEntriesAndAddToSupplementedCodeSystem(
          system, supplement, codeSystem, supplementKey);
    }
  }

  private void processSupplementEntriesAndAddToSupplementedCodeSystem(
      String system,
      Map<String, CodeDisplay> supplement,
      Map<String, CodeDisplay> codeSystem,
      String supplementKey) {
    for (Map.Entry<String, CodeDisplay> codeDisplayEntry : supplement.entrySet()) {
      if (codeSystem != null) {
        CodeDisplay codeDisplay = codeSystem.get(codeDisplayEntry.getKey());
        if (codeDisplay == null) {
          log.info(
              "Code {} not found in code system {} but was given in {}",
              codeDisplayEntry.getKey(),
              system,
              supplementKey);
          continue;
        }
        codeDisplay.addDesignation(codeDisplayEntry.getValue().getDesignations());
      }
    }
  }

  /**
   * reads code systems and creates code displays. reads supplement data and adds it to the code
   *
   * @return this very object
   * @throws IOException if reading/parsing files fails
   */
  CodeSystems build() throws IOException {
    for (File file : codeSystemFiles) {
      // read/parse file
      try {
        CodeSystem codeSystem =
            fhirContext.newJsonParser().parseResource(CodeSystem.class, getFileString(file));
        if (codeSystem.getContent() == CodeSystem.CodeSystemContentMode.NOTPRESENT) {
          log.info("Code system in file {} has no content. Skipping file.", file.getName());
          continue;
        }

        // get data and map to CodeDisplay
        final String systemUrl = codeSystem.getUrl();
        final String version = codeSystem.getVersion();

        String fileNameKey = systemUrl;
        if (version != null) {
          fileNameKey += "|" + version;
        }
        codeSystemData.putIfAbsent(fileNameKey, new LinkedHashMap<>());
        Map<String, CodeDisplay> keyToCodeDisplayMap = codeSystemData.get(fileNameKey);

        for (CodeSystem.ConceptDefinitionComponent concept : codeSystem.getConcept()) {
          extractCodesRecursive(
              keyToCodeDisplayMap, concept, systemUrl, version, fileNameKey, null);
        }

        // add supplementsystem if marked as supplement
        addSupplementSystemIfMarkedAsSupp(codeSystem, fileNameKey);

        // sort code displays for conceptorder
        Map<String, CodeDisplay> sorted = sortCodeSystemEntries(keyToCodeDisplayMap);
        codeSystemData.put(fileNameKey, sorted);

        // check for multiple versions of the same code system
        List<String> keysWithSameUrl =
            codeSystemData.keySet().stream().filter(key -> key.startsWith(systemUrl)).toList();
        // order keysWithSameUrl and search for latest version
        Optional<String> codeSystemWithHighestVersion =
            keysWithSameUrl.stream().max(Comparator.naturalOrder());

        // add highest version as standard version if found code system is the current processed one
        if (codeSystemWithHighestVersion.isPresent()
            && codeSystemWithHighestVersion.get().equals(fileNameKey)) {
          codeSystemData.put(systemUrl, codeSystemData.get(codeSystemWithHighestVersion.get()));
        } else if (codeSystemWithHighestVersion.isEmpty()) {
          // when no version is found, add the current version as standard version
          codeSystemData.put(systemUrl, keyToCodeDisplayMap);
        }

        codeSystemVersions.put(codeSystem.getUrl(), codeSystem.getVersion());

      } catch (DataFormatException e) {
        log.error("Error while reading {}", file.getName(), e);
      }
    }

    // check supplement data and add to codeDisplays for every entry in codeSystemToSupplement
    addSupplementData();

    sortCodeSystemData();

    return this;
  }

  private void sortCodeSystemData() {
    codeSystemData =
        codeSystemData.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (oldValue, newValue) -> oldValue,
                    LinkedHashMap::new));
  }

  private void addSupplementData() {
    for (Map.Entry<String, String> entry : codeSystemToSupplement.entrySet()) {
      String supplementedKey = entry.getKey();
      String supplementKey = entry.getValue();

      Map<String, CodeDisplay> codeDisplayMapToBeSupplemented = codeSystemData.get(supplementedKey);
      Map<String, CodeDisplay> supplement = codeSystemData.get(supplementKey);

      processSupplementEntriesAndAddToSupplementedCodeSystem(
          supplementedKey, supplement, codeDisplayMapToBeSupplemented, supplementKey);
    }
  }

  /**
   * Extracts codes from concepts of a code system recursively. The codes are added to the given
   * map. a code system can have concepts whose elements contain other concepts.
   *
   * @param filesForKeywordMap map to add the extracted codes to
   * @param concept concept to extract codes from
   * @param system code system url
   * @param version code system version
   * @param fileNameKey file name key of the code system
   * @param breadCrumb breadcrumb to add to the code display
   */
  private void extractCodesRecursive(
      Map<String, CodeDisplay> filesForKeywordMap,
      CodeSystem.ConceptDefinitionComponent concept,
      String system,
      String version,
      String fileNameKey,
      String breadCrumb) {
    // create code display
    CodeDisplay codeDisplay = createCodeDisplay(concept, system, version, fileNameKey, breadCrumb);

    addDesignations(concept, codeDisplay);

    filesForKeywordMap.put(concept.getCode(), codeDisplay);

    for (var internalConcept : concept.getConcept()) {
      String breadCrumb1 =
          ((breadCrumb == null ? "" : breadCrumb + "|") + codeDisplay.getDisplay()).trim();
      extractCodesRecursive(
          filesForKeywordMap, internalConcept, system, version, fileNameKey, breadCrumb1);
    }
  }

  private CodeDisplay createCodeDisplay(
      CodeSystem.ConceptDefinitionComponent concept,
      String system,
      String version,
      String fileNameKey,
      String breadCrumb) {
    final var builder = CodeDisplay.builder();
    builder.system(system);
    if (featureFlags.isAddCodeDisplayVersion()) {
      builder.version(version);
    }
    builder.code(concept.getCode());
    builder.display(concept.getDisplay());
    builder.order(extractOrder(concept));
    if ((breadCrumb != null)
        && this.excludedCodeSystems.stream().noneMatch(fileNameKey::startsWith)) {
      builder.breadcrumb(breadCrumb);
    }
    return builder.build();
  }

  /**
   * Adds the supplement system to the map if the metadata resource is marked as supplement. only
   * usable for code systems since only code systems can (currently) be marked as supplementary code
   * systems
   *
   * @param metadataResource the code system to check
   * @param fileNameKey the file name key of the code system
   */
  private void addSupplementSystemIfMarkedAsSupp(CodeSystem metadataResource, String fileNameKey) {
    if (metadataResource.getSupplements() != null) {
      codeSystemToSupplement.put(metadataResource.getSupplements(), fileNameKey);
    }
  }
}
