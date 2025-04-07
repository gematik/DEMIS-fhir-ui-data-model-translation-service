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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhir_ui_data_model_translation_service.exception.DataNotFoundExcp;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.utils.SnapshotFilesService;
import io.micrometer.observation.annotation.Observed;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * This service loads and holds code systems and value sets from the DEMIS Fhir Infomdell and makes
 * them available in a structured way.
 */
@Service
@Slf4j
public class DataLoaderSrv {

  public static final String SYSTEM_S_IS_UNKNOWN = "the system %s is unknown";
  private final SnapshotFilesService snapshotFilesService;
  private final String profileSourcePath;
  private final FhirContext fhirContext;
  private final List<File> codeSystemFiles = new ArrayList<>();
  private final List<File> valueSetFiles = new ArrayList<>();
  private final Hl7CodeSystemSrv hl7CodeSystemSrv;
  private static final List<String> excludedCodeSystems =
      List.of("http://terminology.hl7.org/CodeSystem/v3-NullFlavor");
  private CodeSystems codeSystems;
  private ValueSets valueSets;

  public DataLoaderSrv(
      @Value("${data.path.profile.root}") String profileSourcePath,
      SnapshotFilesService snapshotFilesService,
      FhirContext fhirContext,
      Hl7CodeSystemSrv hl7CodeSystemSrv) {
    this.fhirContext = fhirContext;
    this.snapshotFilesService = snapshotFilesService;
    this.profileSourcePath = Paths.get(profileSourcePath) + File.separator;
    this.hl7CodeSystemSrv = hl7CodeSystemSrv;
  }

  @PostConstruct
  protected void initialize() throws IOException {
    List<File> rawFiles = snapshotFilesService.getRawFiles();
    for (File file : rawFiles) {
      createDataNew(file);
    }
    LinkedHashSet<File> sortedCodeSystems =
        codeSystemFiles.stream()
            .sorted(Comparator.comparing(File::getName))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    LinkedHashSet<File> sortedValueSets =
        valueSetFiles.stream()
            .sorted(Comparator.comparing(File::getName))
            .collect(Collectors.toCollection(LinkedHashSet::new));

    codeSystems = new CodeSystems(sortedCodeSystems, fhirContext, excludedCodeSystems).build();
    valueSets = new ValueSets(sortedValueSets, fhirContext, codeSystems).build();
  }

  private void createDataNew(File file) {
    log.info("Processing file {}", file.getName());
    String filePath = file.getPath();
    String reducedPath = filePath.replace(profileSourcePath, "");
    String keywordKey = reducedPath.split(Pattern.quote(File.separator))[0];

    if (keywordKey.equals("CodeSystem")) {
      codeSystemFiles.add(file);
    } else if (keywordKey.equals("ValueSet")) {
      valueSetFiles.add(file);
    }
  }

  private Map<String, CodeDisplay> getCodes(String system) {
    String tmpSystem = system.split("\\|")[0];
    Map<String, CodeDisplay> codes = codeSystems.getCodeSystemData().get(tmpSystem);
    if (codes == null || codes.isEmpty()) {
      if (hl7CodeSystemSrv.containsContent(tmpSystem)) {
        try {
          var standardCodeSystem = hl7CodeSystemSrv.getFileContent(tmpSystem);
          codeSystems.addCodeSystem(tmpSystem, standardCodeSystem);
          codes = codeSystems.getCodeSystemData().get(tmpSystem);
        } catch (IOException e) {
          throw new DataNotFoundExcp(String.format(SYSTEM_S_IS_UNKNOWN, system));
        }
      } else {
        throw new DataNotFoundExcp(String.format(SYSTEM_S_IS_UNKNOWN, system));
      }
    }
    return codes;
  }

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
   * @return
   */
  @Observed(
      name = "code-systems",
      contextualName = "code-systems",
      lowCardinalityKeyValues = {"code", "fhir"})
  public List<String> getCodeSystems() {
    return codeSystems.getCodeSystemData().keySet().stream().toList();
  }

  /**
   * returns a code display for a specific system url and code for a value set.
   *
   * @param system
   * @param code
   * @return
   */
  @Observed(
      name = "value-sets-system-code",
      contextualName = "value-sets-system-code",
      lowCardinalityKeyValues = {"value-sets", "fhir"})
  public CodeDisplay getValueSetData(String system, String code) {
    Map<String, CodeDisplay> codes = valueSets.getValueSetData().get(system.split("\\|")[0]);
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
   * @return
   */
  @Observed(
      name = "value-sets",
      contextualName = "value-sets",
      lowCardinalityKeyValues = {"value-sets", "fhir"})
  public List<String> getValueSet() {
    return valueSets.getValueSetData().keySet().stream().toList();
  }

  /**
   * returns all available data for a specific system url for a value set.
   *
   * @param system
   * @return
   */
  @Observed(
      name = "value-sets-system",
      contextualName = "value-sets-system",
      lowCardinalityKeyValues = {"value-sets", "fhir"})
  public List<CodeDisplay> getValueSetData(String system) {
    Map<String, CodeDisplay> valueSetMap = valueSets.getValueSetData().get(system.split("\\|")[0]);
    if (valueSetMap == null || valueSetMap.isEmpty()) {
      throw new DataNotFoundExcp(String.format("No data found for %s!", system));
    }
    return valueSetMap.entrySet().stream().map(Map.Entry::getValue).toList();
  }
}
