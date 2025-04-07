package de.gematik.demis.fhir_ui_data_model_translation_service.conceptmaps;

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
import de.gematik.demis.fhir_ui_data_model_translation_service.utils.SnapshotFilesService;
import de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.ConceptMap;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ConceptMapPreparationSrv {

  private final SnapshotFilesService snapshotFilesService;
  private final FhirContext fhirContext;

  private final Map<String, Map<String, String>> conceptMaps = new HashMap<>();

  public ConceptMapPreparationSrv(
      SnapshotFilesService snapshotFilesService, FhirContext fhirContext) {
    this.snapshotFilesService = snapshotFilesService;
    this.fhirContext = fhirContext;
  }

  @PostConstruct
  protected void init() {
    // read all files in the sub folder ConceptMap of the profileSourcePath
    List<File> conceptMapsFiles = snapshotFilesService.getConceptMaps();

    // for each file read the content and store it in the conceptMaps
    conceptMapsFiles.forEach(
        file -> {
          try {
            addFileToConceptMaps(file);
          } catch (IOException e) {
            log.warn("Error while reading file {}", file.getAbsolutePath(), e);
          }
        });
  }

  private void addFileToConceptMaps(File file) throws IOException {
    // read the content of the file and store it in the conceptMaps
    String fileString = Utils.getFileString(file);
    ConceptMap conceptMap = fhirContext.newJsonParser().parseResource(ConceptMap.class, fileString);

    // collect groups from concept map
    conceptMaps.putIfAbsent(conceptMap.getName(), new HashMap<>());
    Map<String, String> codeToDisplayMap = conceptMaps.get(conceptMap.getName());
    conceptMaps.putIfAbsent(conceptMap.getUrl(), codeToDisplayMap);
    conceptMap
        .getGroup()
        .forEach(
            group ->
                group
                    .getElement()
                    .forEach(
                        element -> {
                          String code = element.getCode();
                          String targetCode = element.getTarget().getFirst().getCode();
                          codeToDisplayMap.put(code, targetCode);
                        }));
  }

  public List<String> getAllAvailableMaps() {
    return conceptMaps.keySet().stream().toList();
  }

  public Map<String, String> getMap(String mapName) {
    Map<String, String> data = conceptMaps.get(mapName);
    if (data == null) {
      String errorMessage = String.format("The concept map %s was not found", mapName);
      log.info(errorMessage);
      throw new DataNotFoundExcp(errorMessage);
    }
    return data;
  }

  public String getCode(String mapName, String sourceCode) {
    Map<String, String> conceptMap = conceptMaps.get(mapName);
    if (conceptMap == null) {
      String errorMessage = String.format("The concept map %s was not found", mapName);
      log.info(errorMessage);
      throw new DataNotFoundExcp(errorMessage);
    }
    String mappedCode = conceptMap.get(sourceCode);
    if (mappedCode == null) {
      String errorMessage =
          String.format("The code %s was not found in the concept map %s", sourceCode, mapName);
      log.info(errorMessage);
      throw new DataNotFoundExcp(errorMessage);
    }
    return mappedCode;
  }
}
