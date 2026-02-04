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

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhir_ui_data_model_translation_service.FeatureFlags;
import de.gematik.demis.fhir_ui_data_model_translation_service.exception.DataNotFoundExcp;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.utils.SnapshotFilesService;
import io.micrometer.observation.annotation.Observed;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * This service loads and holds code systems and value sets from the DEMIS Fhir Infomdell and makes
 * them available in a structured way.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class DataLoaderSrv {

  private static final String SYSTEM_S_IS_UNKNOWN = "the system %s is unknown";
  private static final List<String> excludedCodeSystems =
      List.of("http://terminology.hl7.org/CodeSystem/v3-NullFlavor");

  private final SnapshotFilesService snapshotFilesService;
  private final FhirContext fhirContext;
  private final List<File> codeSystemFiles = new ArrayList<>();
  private final List<File> valueSetFiles = new ArrayList<>();
  private final Hl7CodeSystemSrv hl7CodeSystemSrv;
  private final FeatureFlags featureFlags;

  private CodeSystems codeSystems;
  private ValueSets valueSets;

  private static String getUnversionedSystemUrl(String system) {
    return system.split("\\|")[0];
  }

  @PostConstruct
  protected void initialize() throws IOException {
    codeSystemFiles.addAll(snapshotFilesService.getCodeSystemFiles());
    valueSetFiles.addAll(snapshotFilesService.getValueSetFiles());

    LinkedHashSet<File> sortedCodeSystems =
        codeSystemFiles.stream()
            .sorted(Comparator.comparing(File::getName))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    LinkedHashSet<File> sortedValueSets =
        valueSetFiles.stream()
            .sorted(Comparator.comparing(File::getName))
            .collect(Collectors.toCollection(LinkedHashSet::new));

    codeSystems =
        new CodeSystems(sortedCodeSystems, fhirContext, excludedCodeSystems, featureFlags).build();
    valueSets = new ValueSets(sortedValueSets, fhirContext, codeSystems, featureFlags).build();
  }

  private Map<String, CodeDisplay> getCodes(String system) {
    String tmpSystem = getUnversionedSystemUrl(system);
    Map<String, CodeDisplay> codes = codeSystems.getCodeSystemData().get(tmpSystem);
    if (codes == null || codes.isEmpty()) {
      if (hl7CodeSystemSrv.containsContent(tmpSystem)) {
        try {
          var standardCodeSystem = hl7CodeSystemSrv.getFileContent(tmpSystem);
          codeSystems.addCodeSystem(tmpSystem, standardCodeSystem);
          codes = codeSystems.getCodeSystemData().get(tmpSystem);
        } catch (IOException e) {
          log.error("Error loading HL7 code system for system {}", tmpSystem, e);
          throw new DataNotFoundExcp(String.format(SYSTEM_S_IS_UNKNOWN, system));
        }
      } else {
        throw new DataNotFoundExcp(String.format(SYSTEM_S_IS_UNKNOWN, system));
      }
    }
    return codes;
  }

  /**
   * Returns the {@link CodeDisplay} for a given code within a CodeSystem.
   *
   * @param system the CodeSystem URL
   * @param code the code to look up within the given system
   * @return the corresponding {@link CodeDisplay}
   * @throws DataNotFoundExcp if the code does not exist in the given system
   */
  @Observed(
      name = "code-system-data-code",
      contextualName = "code-system-data-code",
      lowCardinalityKeyValues = {"code", "fhir"})
  public CodeDisplay getCodeSystemData(String system, String code) {
    Map<String, CodeDisplay> codes = getCodes(system);
    CodeDisplay codeDisplay = codes.get(code);
    if (codeDisplay == null) {
      throw new DataNotFoundExcp(String.format("the code %s was not found in %s", code, system));
    }
    return codeDisplay;
  }

  /**
   * Returns all {@link CodeDisplay} entries for a given CodeSystem.
   *
   * @param system the CodeSystem URL
   * @return all {@link CodeDisplay} entries associated with the given CodeSystem
   */
  @Observed(
      name = "code-system-data",
      contextualName = "code-system-data",
      lowCardinalityKeyValues = {"code", "fhir"})
  public List<CodeDisplay> getCodeSystemData(String system) {
    Map<String, CodeDisplay> codes = getCodes(system);
    return new ArrayList<>(codes.values());
  }

  /**
   * returns all available code systems.
   *
   * @return list of code system URLs
   */
  @Observed(
      name = "code-systems",
      contextualName = "code-systems",
      lowCardinalityKeyValues = {"code", "fhir"})
  public List<String> getCodeSystems() {
    return codeSystems.getCodeSystemData().keySet().stream().toList();
  }

  /**
   * Returns the {@link CodeDisplay} for a given code within a ValueSet system.
   *
   * <p>The provided system URL may be versioned; the version part is stripped before the ValueSet
   * data is looked up.
   *
   * @param system the ValueSet system URL (may be versioned)
   * @param code the code to look up within the given system
   * @return the corresponding {@link CodeDisplay}
   * @throws DataNotFoundExcp if the system is unknown or the code does not exist
   */
  @Observed(
      name = "value-sets-system-code",
      contextualName = "value-sets-system-code",
      lowCardinalityKeyValues = {"value-sets", "fhir"})
  public CodeDisplay getValueSetData(String system, String code) {
    Map<String, CodeDisplay> codes =
        valueSets.getValueSetData().get(getUnversionedSystemUrl(system));
    if (codes == null) {
      throw new DataNotFoundExcp(String.format(SYSTEM_S_IS_UNKNOWN, system));
    }
    CodeDisplay conceptReferenceComponent = codes.get(code);
    if (conceptReferenceComponent == null) {
      throw new DataNotFoundExcp(String.format("the code %s was not found in %s", code, system));
    }
    return conceptReferenceComponent;
  }

  /**
   * returns all available value sets.
   *
   * @return list of value set URLs
   */
  @Observed(
      name = "value-sets",
      contextualName = "value-sets",
      lowCardinalityKeyValues = {"value-sets", "fhir"})
  public List<String> getValueSet() {
    return valueSets.getValueSetData().keySet().stream().toList();
  }

  /**
   * Returns all {@link CodeDisplay} entries for a ValueSet identified by its system URL.
   *
   * <p>The provided system URL may be versioned in that case, the version part is stripped before
   * the lookup is performed.
   *
   * @param system the ValueSet system URL
   * @return all {@link CodeDisplay} entries associated with the given ValueSet
   * @throws DataNotFoundExcp if no data is found for the given system
   */
  @Observed(
      name = "value-sets-system",
      contextualName = "value-sets-system",
      lowCardinalityKeyValues = {"value-sets", "fhir"})
  public List<CodeDisplay> getValueSetData(String system) {
    final String unversionedSystemUrl = getUnversionedSystemUrl(system);
    Map<String, CodeDisplay> valueSetMap = valueSets.getValueSetData().get(unversionedSystemUrl);
    if (valueSetMap == null || valueSetMap.isEmpty()) {
      throw new DataNotFoundExcp(String.format("No data found for %s!", system));
    }
    return valueSetMap.values().stream().toList();
  }

  public String getVersion(String systemUrl) {
    if (codeSystems.getCodeSystemVersions().isEmpty()) {
      throw new DataNotFoundExcp("the map for code systems was not initialized");
    }
    Map<String, String> codeSystemVersions = codeSystems.getCodeSystemVersions();
    if (codeSystemVersions.containsKey(systemUrl)) {
      return codeSystemVersions.get(systemUrl);
    } else {
      throw new DataNotFoundExcp(
          String.format(
              "the version of the code system %s was not added to the version map", systemUrl));
    }
  }
}
