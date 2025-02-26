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

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.CodeSystem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
class Hl7CodeSystemSrv {

  private final String hl7DataPath;
  private final FhirContext fhirContext;
  private Map<String, String> urlToPathMap;

  Hl7CodeSystemSrv(
      @Value("${data.path.hl7.data.root}") String hl7DataPath, FhirContext fhirContext) {

    this.hl7DataPath = hl7DataPath;
    this.fhirContext = fhirContext;
  }

  @PostConstruct
  void init() {
    log.info("Initializing Hl7CodeSystemSrv");
    urlToPathMap = new HashMap<>();
    var rawFiles = new File(Paths.get(hl7DataPath) + File.separator).listFiles();
    if (rawFiles != null) {
      for (File file : rawFiles) {
        processFile(file);
      }
    }
    log.info("Initializing Hl7CodeSystemSrv finished");
  }

  boolean containsContent(String url) {
    return urlToPathMap.containsKey(url);
  }

  CodeDisplayMapWithVersion getFileContent(String url) throws IOException {
    Map<String, CodeDisplay> returnMap = new HashMap<>();
    String returnVersion = "";
    File file = new File(urlToPathMap.get(url));

    String fileString = Utils.getFileString(file);
    var resource = fhirContext.newJsonParser().parseResource(fileString);

    if (resource instanceof CodeSystem codeSystem) {
      List<CodeSystem.ConceptDefinitionComponent> concepts = codeSystem.getConcept();
      extractCodes(concepts, returnMap);
      returnVersion = codeSystem.getVersion();
    }

    return new CodeDisplayMapWithVersion(returnVersion, returnMap);
  }

  private void extractCodes(
      List<CodeSystem.ConceptDefinitionComponent> concepts, Map<String, CodeDisplay> returnMap) {
    concepts.forEach(
        concept -> {
          String code = concept.getCode();
          String display = concept.getDisplay();
          CodeDisplay codeDisplay = CodeDisplay.builder().code(code).display(display).build();
          returnMap.put(code, codeDisplay);
          if (concept.hasConcept()) {
            extractCodes(concept.getConcept(), returnMap);
          }
        });
  }

  private void processFile(File file) {
    try {
      String fileString = Utils.getFileString(file);
      if (fileString.contains("\"resourceType\": \"CodeSystem\"")) {
        findUrlAndAddToMap(file, fileString);
      }
    } catch (IOException e) {
      log.error("Error reading file {}", file.getName());
    }
  }

  private void findUrlAndAddToMap(File file, String fileString) {
    Optional<String> possibleUrl = new Hl7CodeSystemUrl(fileString).get();
    possibleUrl.ifPresentOrElse(
        url -> {
          urlToPathMap.put(url, file.getPath());
          log.info("Added code system with url {}", url);
        },
        () -> log.error("No url found in file {}", file.getName()));
  }
}
