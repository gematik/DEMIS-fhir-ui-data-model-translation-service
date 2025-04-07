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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@AllArgsConstructor
public class ValueSetCtr {

  private final DataLoaderSrv dataLoaderSrv;

  private final ObjectMapper objectMapper;

  /**
   * This endpoint provides retrieval of translations for FHIR code systems. Originally intended to
   * be structured hierarchically (e.g., ValueSet/{system}/{code}), but encountered issues as the
   * 'system' parameter is typically, but not always, in URL format. This caused problems with
   * Spring's encoding handling, resulting in a 400 error. Temporarily using request parameters to
   * address the issue.
   */
  @GetMapping(path = "/ValueSet", produces = MediaType.APPLICATION_JSON_VALUE)
  public String getAvailableCodeSystems(
      @RequestParam(required = false) String system,
      @RequestParam(required = false) String code,
      @RequestParam(required = false) String version)
      throws JsonProcessingException {
    return processEnteredData(system, code, version);
  }

  @GetMapping(path = "/ValueSet/{system}", produces = MediaType.APPLICATION_JSON_VALUE)
  public String getValueSetContent(
      @PathVariable("system") String system, @RequestParam(required = false) String version)
      throws JsonProcessingException {
    return processEnteredData(system, null, version);
  }

  @GetMapping(path = "/ValueSet/{system}/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
  public String getCode(
      @PathVariable("system") String system,
      @PathVariable("code") String code,
      @RequestParam(required = false) String version)
      throws JsonProcessingException {
    return processEnteredData(system, code, version);
  }

  private String processEnteredData(String system, String code, String version)
      throws JsonProcessingException {
    ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
    if (version != null && system != null) {
      system = system + "|" + version;
    }
    if (system != null && code != null) {
      log.info("Get call for ValueSet/{}/{}", system, code);
      return objectWriter.writeValueAsString(dataLoaderSrv.getValueSetData(system, code));
    } else if (system != null) {
      log.info("Get call for ValueSet/{}", system);
      return objectWriter.writeValueAsString(dataLoaderSrv.getValueSetData(system));
    } else {
      log.info("Get call for ValueSet");
      return objectWriter.writeValueAsString(dataLoaderSrv.getValueSet());
    }
  }
}
