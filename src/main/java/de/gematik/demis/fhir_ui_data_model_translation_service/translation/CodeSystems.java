package de.gematik.demis.fhir_ui_data_model_translation_service.translation;

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

import static de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils.extractOrder;
import static de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils.getFileString;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.Designation;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.CodeSystem;

@RequiredArgsConstructor
@Slf4j
class CodeSystems {

  private final LinkedHashSet<File> codeSystemFiles;
  private final FhirContext fhirContext;
  private final List<String> excludedCodeSystems;

  /**
   * map that contains pairs of supplemented code systems and supplement code system. the key is the
   * code systems that gets the supplement. the value is the code system that supplements the key
   * code system
   */
  private final Map<String, String> codeSystemToSupplement = new HashMap<>();

  @Getter private Map<String, Map<String, CodeDisplay>> codeSystemData = new ConcurrentHashMap<>();

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
   * @return
   * @throws IOException
   */
  CodeSystems build() throws IOException {
    for (File file : codeSystemFiles) {
      // read/parse file
      try {
        CodeSystem codeSystem =
            fhirContext.newJsonParser().parseResource(CodeSystem.class, getFileString(file));

        // get data and map to CodeDisplay
        String fileNameKey = codeSystem.getUrl();
        if (codeSystem.getVersion() != null) {
          fileNameKey += "|" + codeSystem.getVersion();
        }
        codeSystemData.putIfAbsent(fileNameKey, new LinkedHashMap<>());
        Map<String, CodeDisplay> keyToCodeDisplayMap = codeSystemData.get(fileNameKey);

        for (CodeSystem.ConceptDefinitionComponent concept : codeSystem.getConcept()) {
          extractCodesRecursive(
              keyToCodeDisplayMap, concept, codeSystem.getUrl(), fileNameKey, null);
        }

        // add supplementsystem if marked as supplement
        addSupplementSystemIfMarkedAsSupp(codeSystem, fileNameKey);

        // sort code displays for conceptorder
        Map<String, CodeDisplay> sorted = sortCodeSystemEntries(keyToCodeDisplayMap);
        codeSystemData.put(fileNameKey, sorted);

        // check for multiple versions of the same code system
        List<String> keysWithSameUrl =
            codeSystemData.keySet().stream()
                .filter(key -> key.startsWith(codeSystem.getUrl()))
                .toList();
        // order keysWithSameUrl and search for latest version
        Optional<String> codeSystemWithHighestVersion =
            keysWithSameUrl.stream().sorted(Comparator.reverseOrder()).findFirst();

        // add highest version as standard version if found code system is the current processed one
        if (codeSystemWithHighestVersion.isPresent()
            && codeSystemWithHighestVersion.get().equals(fileNameKey)) {
          codeSystemData.put(
              codeSystem.getUrl(), codeSystemData.get(codeSystemWithHighestVersion.get()));
        } else if (codeSystemWithHighestVersion.isEmpty()) {
          // when no version is found, add the current version as standard version
          codeSystemData.put(codeSystem.getUrl(), keyToCodeDisplayMap);
        }

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
   * extracts codes from concepts of a code system recursively. The codes are added to the given
   * map. a code system can have concepts whose elements contain other concepts.
   *
   * @param filesForKeywordMap
   * @param concept
   * @param fileNameKey
   */
  private void extractCodesRecursive(
      Map<String, CodeDisplay> filesForKeywordMap,
      CodeSystem.ConceptDefinitionComponent concept,
      String system,
      String fileNameKey,
      String breadCrumb) {
    // create code display
    CodeDisplay codeDisplay = createCodeDisplay(concept, system, fileNameKey, breadCrumb);

    // check for designations and add designations to code display
    if (!concept.getDesignation().isEmpty()) {
      List<CodeSystem.ConceptDefinitionDesignationComponent> designation = concept.getDesignation();
      Set<Designation> designations = new LinkedHashSet<>();
      for (var conceptDesignation : designation) {
        if (conceptDesignation.getUse() != null
            && (conceptDesignation.getUse().getCode() != null
                    && !conceptDesignation.getUse().getCode().equals("FullySpecifiedName")
                || conceptDesignation.getUse().getCode() == null)) {
          designations.add(
              new Designation(conceptDesignation.getLanguage(), conceptDesignation.getValue()));
        }
      }
      codeDisplay.setDesignations(designations);
    }

    filesForKeywordMap.put(concept.getCode(), codeDisplay);

    for (var internalConcept : concept.getConcept()) {
      String breadCrumb1 =
          ((breadCrumb == null ? "" : breadCrumb + "|") + codeDisplay.getDisplay()).trim();
      extractCodesRecursive(filesForKeywordMap, internalConcept, system, fileNameKey, breadCrumb1);
    }
  }

  private CodeDisplay createCodeDisplay(
      CodeSystem.ConceptDefinitionComponent concept,
      String system,
      String fileNameKey,
      String breadCrumb) {
    final var builder = CodeDisplay.builder();
    builder.system(system);
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
   * useable for code systems since only code systems can (currently) be marked as supplementary
   * code systems
   *
   * @param metadataResource
   * @param fileNameKey
   */
  private void addSupplementSystemIfMarkedAsSupp(CodeSystem metadataResource, String fileNameKey) {
    if (metadataResource.getSupplements() != null) {
      codeSystemToSupplement.put(metadataResource.getSupplements(), fileNameKey);
    }
  }
}
