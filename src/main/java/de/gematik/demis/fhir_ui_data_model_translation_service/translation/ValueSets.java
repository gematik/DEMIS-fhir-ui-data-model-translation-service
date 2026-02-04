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

import static de.gematik.demis.fhir_ui_data_model_translation_service.model.Use.toUse;
import static de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils.getFileString;

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhir_ui_data_model_translation_service.FeatureFlags;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.Designation;
import de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.ValueSet;

@RequiredArgsConstructor
@Slf4j
class ValueSets {

  private final LinkedHashSet<File> valueSetFiles;
  private final FhirContext fhirContext;
  private final CodeSystems codeSystems;
  private final FeatureFlags featureFlags;

  @Getter private Map<String, Map<String, CodeDisplay>> valueSetData = new ConcurrentHashMap<>();

  private List<ValueSetPreparation> valueSetPreparations;

  ValueSets build() {
    // phase 1 - read all files and create ValueSetPreparation objects
    // contain direct links to include elements and a boolean when
    // another value sets is called
    valueSetPreparations = new ArrayList<>();
    valueSetFiles.forEach(this::initialParsing);

    // phase 2 - process all ValueSetPreparation objects that do not need another value set. set all
    // preparation objects to processable, if their needed value set was processed
    processDataWithNoDependenciesOnOtherValueSets();

    // phase 3 - find all ValueSetPreparation objects that are processable and process them
    processDataWithDependenciesOnOtherValueSets();

    // phase 4 - sort all value sets
    valueSetData.entrySet().forEach(this::sortAndFilterValueSetMap);
    valueSetData =
        valueSetData.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (oldValue, newValue) -> oldValue,
                    LinkedHashMap::new));
    log.info("ValueSets build finished. Number of ValueSets: {}", valueSetData.size());
    return this;
  }

  private void processDataWithDependenciesOnOtherValueSets() {
    int index = 0;
    while (!valueSetPreparations.isEmpty() && index < valueSetPreparations.size()) {
      ValueSetPreparation valueSetPreparation = valueSetPreparations.get(index);
      boolean isAllDataAvailable = checkNeededValueSetIsAvailable(valueSetPreparation);
      if (isAllDataAvailable) {
        processIncludeData(valueSetPreparation);
        index = 0;
        valueSetPreparations.remove(valueSetPreparation);
      } else {
        index++;
      }
    }
  }

  private void processDataWithNoDependenciesOnOtherValueSets() {
    List<ValueSetPreparation> processed =
        valueSetPreparations.stream()
            .filter(ValueSetPreparation::isStandalone)
            .peek(this::processIncludeData)
            .toList();

    // remove all processed ValueSetPreparation objects from the list
    valueSetPreparations.removeAll(processed);
  }

  private void sortAndFilterValueSetMap(Map.Entry<String, Map<String, CodeDisplay>> entry) {
    var keyToValueSetMap = entry.getValue();
    var fileNameKey = entry.getKey();

    Map<String, CodeDisplay> sorted =
        keyToValueSetMap.entrySet().stream()
            .filter(e -> e.getValue().getOrder() > 0)
            .sorted(
                Comparator.comparingInt(
                        (Map.Entry<String, CodeDisplay> map) -> map.getValue().getOrder())
                    .reversed()) // reversed because we have to sort in descending order
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (oldValue, newValue) -> oldValue,
                    LinkedHashMap::new));
    valueSetData.put(fileNameKey, sorted);
  }

  private void initialParsing(File file) {
    try {
      log.info("Parsing value set file: {}", file.getName());
      ValueSet valueSet =
          fhirContext.newJsonParser().parseResource(ValueSet.class, getFileString(file));

      // create ValueSetPreparation object
      // we check if a value set contains other value sets in the include element. since the hapi
      // parser creates an empty list even if the element is not given in the file, we have to check
      // if the list is empty. so for alle include elements we check if the list value set is empty
      boolean isStandAlone =
          valueSet.getCompose().getInclude().stream()
              .allMatch(include -> include.getValueSet().isEmpty());
      valueSetPreparations.add(new ValueSetPreparation(valueSet, isStandAlone));
    } catch (Exception e) {
      log.error("Error while parsing file: {}", file.getName(), e);
    }
  }

  private boolean checkNeededValueSetIsAvailable(ValueSetPreparation valueSetPreparation) {
    List<ValueSet.ConceptSetComponent> include1 =
        valueSetPreparation.valueSet().getCompose().getInclude();
    for (ValueSet.ConceptSetComponent setComponent : include1) {
      if (setComponent.getValueSet() != null
          && !setComponent.getValueSet().isEmpty()
          && !valueSetData.containsKey(setComponent.getValueSet().getFirst().asStringValue())) {
        return false;
      }
    }
    return true;
  }

  private void processIncludeData(ValueSetPreparation valueSetPreparation) {
    log.info("Processing include data from value set: {}", valueSetPreparation.valueSet().getUrl());
    String fileNameKey = valueSetPreparation.valueSet.getUrl();
    String valueSetVersion = valueSetPreparation.valueSet.getVersion();
    if (valueSetVersion != null) {
      fileNameKey += "|" + valueSetVersion;
    }
    valueSetData.putIfAbsent(fileNameKey, new LinkedHashMap<>());
    Map<String, CodeDisplay> keyToValueSetMap = valueSetData.get(fileNameKey);

    List<ValueSet.ConceptSetComponent> include =
        valueSetPreparation.valueSet().getCompose().getInclude();

    for (ValueSet.ConceptSetComponent cSC : include) {
      if (cSC.getConcept().isEmpty()) {
        processIncludeWithCodeSystemOrValueSetData(cSC, keyToValueSetMap);
      } else {
        for (ValueSet.ConceptReferenceComponent cRC : cSC.getConcept()) {
          keyToValueSetMap.put(cRC.getCode(), createCodeDisplay(cRC, cSC));
        }
      }
    }

    List<String> keysWithSameUrl =
        valueSetData.keySet().stream()
            .filter(key -> key.startsWith(valueSetPreparation.valueSet.getUrl()))
            .toList();
    // order keysWithSameUrl and search for latest version
    Optional<String> valueSetWithHighestVersion =
        keysWithSameUrl.stream().max(Comparator.naturalOrder());

    // add the highest version of value set if found value set is the current processed one
    if (valueSetWithHighestVersion.isPresent()
        && valueSetWithHighestVersion.get().equals(fileNameKey)) {
      valueSetData.put(
          valueSetPreparation.valueSet.getUrl(),
          valueSetData.get(valueSetWithHighestVersion.get()));
    } else if (valueSetWithHighestVersion.isEmpty()) {
      // when no version is found, add the current version
      valueSetData.put(valueSetPreparation.valueSet.getUrl(), keyToValueSetMap);
    }
  }

  private void processIncludeWithCodeSystemOrValueSetData(
      ValueSet.ConceptSetComponent setComponent, Map<String, CodeDisplay> keyToValueSetMap) {
    Map<String, CodeDisplay> stringCodeDisplayMap;
    if (!setComponent.getValueSet().isEmpty()) {
      stringCodeDisplayMap =
          this.valueSetData.get(setComponent.getValueSet().getFirst().asStringValue());
    } else {
      stringCodeDisplayMap = codeSystems.getCodeSystemData().get(setComponent.getSystem());
    }
    if (stringCodeDisplayMap != null) {
      keyToValueSetMap.putAll(stringCodeDisplayMap);
    } else {
      log.warn("No data found for the specified ValueSet or CodeSystem.");
    }
  }

  private CodeDisplay createCodeDisplay(
      ValueSet.ConceptReferenceComponent referenceComponent,
      ValueSet.ConceptSetComponent setComponent) {
    // extract data and create new CodeDisplay. search for breadcrump in code system
    final var builder =
        CodeDisplay.builder()
            .code(referenceComponent.getCode())
            .display(referenceComponent.getDisplay())
            .system(setComponent.getSystem());
    if (featureFlags.isAddCodeDisplayVersion()) {
      builder.version(setComponent.getVersion());
    }
    final CodeDisplay codeDisplay = builder.order(Utils.extractOrder(referenceComponent)).build();
    if (referenceComponent.hasDesignation()) {
      Set<Designation> designations = new HashSet<>();
      if (featureFlags.isAddDesignationUse()) {
        referenceComponent
            .getDesignation()
            .forEach(
                cRDC ->
                    designations.add(
                        new Designation(
                            cRDC.getLanguage(), cRDC.getValue(), toUse(cRDC.getUse()))));
        codeDisplay.addDesignation(designations);
      } else {
        referenceComponent
            .getDesignation()
            .forEach(
                cRDC -> designations.add(new Designation(cRDC.getLanguage(), cRDC.getValue())));

        codeDisplay.addDesignation(designations);
      }
    }

    // get all codeSystems that start with the setCompoenets system
    List<String> codeSystemsStartingWithUrl =
        codeSystems.getCodeSystemData().keySet().stream()
            .filter(key -> key.startsWith(setComponent.getSystem()))
            .toList();
    // look for breadcrumbs/designations and add if available in both code systems and value sets
    for (String key : codeSystemsStartingWithUrl) {
      Map<String, CodeDisplay> codeDisplayMapFromCodeSystemData =
          codeSystems.getCodeSystemData().get(key);
      if (codeDisplayMapFromCodeSystemData != null) {
        CodeDisplay codeDisplayFromCodeSystems =
            codeDisplayMapFromCodeSystemData.get(referenceComponent.getCode());
        if (codeDisplayFromCodeSystems != null) {
          String breadcrumb = codeDisplayFromCodeSystems.getBreadcrumb();
          if (breadcrumb != null) {
            codeDisplay.setBreadcrumb(breadcrumb);
          }

          codeDisplay.addDesignation(codeDisplayFromCodeSystems.getDesignations());
          codeDisplayFromCodeSystems.addDesignation(codeDisplay.getDesignations());
        }
      }
    }
    return codeDisplay;
  }

  private record ValueSetPreparation(ValueSet valueSet, boolean isStandalone) {}
}
