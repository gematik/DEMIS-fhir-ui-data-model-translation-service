package de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.fieldtypes;

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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Custom Jackson serializer for date picker string fields ({@code minDate}, {@code maxDate}).
 *
 * <p>Resolves the {@link #TODAY_PLACEHOLDER} at serialization time to the current date in the
 * {@code Europe/Berlin} time zone. Every other value (including {@code null}) is written as-is.
 */
public class DatePickerSerializer extends JsonSerializer<String> {

  /**
   * Placeholder for today to be replaced with current date in Europe/Berlin. This is an internal
   * placeholder that never leaves FUTS.
   */
  public static final String TODAY_PLACEHOLDER = "${LOCAL_DATE_NOW}";

  static final ZoneId ZONE_EUROPE_BERLIN = ZoneId.of("Europe/Berlin");

  @Override
  public void serialize(String value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    if (TODAY_PLACEHOLDER.equals(value)) {
      gen.writeString(LocalDate.now(ZONE_EUROPE_BERLIN).toString());
    } else {
      gen.writeString(value);
    }
  }
}
