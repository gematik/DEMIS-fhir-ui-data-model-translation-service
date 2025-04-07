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

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ConceptMapsCtr {

  private final ConceptMapPreparationSrv conceptMapPreparationSrv;

  @GetMapping("/conceptmap")
  public List<String> getAllConceptMaps() {
    log.info("Get call for all concept maps");
    return conceptMapPreparationSrv.getAllAvailableMaps();
  }

  @GetMapping("/conceptmap/{name}")
  public Map<String, String> getConceptMap(@PathVariable String name) {
    log.info("Get call for concept map {}", name);
    return conceptMapPreparationSrv.getMap(name);
  }

  @GetMapping("/conceptmap/{name}/{code}")
  public String getCode(@PathVariable String name, @PathVariable String code) {
    log.info("Get call for concept map {} and code {}", name, code);
    return conceptMapPreparationSrv.getCode(name, code);
  }
}
