package de.gematik.demis.fhir_ui_data_model_translation_service.exception;

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

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
@Slf4j
public class ExceptionCtr {

  @ExceptionHandler({DataNotFoundExcp.class})
  @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
  @ResponseBody
  public String handleCodeNotFoundException(DataNotFoundExcp exception) {
    log.warn("Error while loading code: {}", exception.getMessage());
    return exception.getMessage();
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public String handleOtherExceptions(Exception exception) {
    log.error("unhandled exception: {}", exception.getMessage());
    return exception.getMessage();
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<String> handleResponseStatusExceptions(ResponseStatusException exception) {
    HttpStatusCode status =
        Objects.requireNonNullElse(exception.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    return ResponseEntity.status(status).body(exception.getMessage());
  }
}
