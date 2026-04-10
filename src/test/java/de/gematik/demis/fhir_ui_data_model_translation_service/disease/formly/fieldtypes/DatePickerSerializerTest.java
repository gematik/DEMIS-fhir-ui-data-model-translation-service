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

import static de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.fieldtypes.DatePickerSerializer.TODAY_PLACEHOLDER;
import static de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.fieldtypes.DatePickerSerializer.ZONE_EUROPE_BERLIN;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Props;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class DatePickerSerializerTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void resolvesPlaceholderToCurrentDateInBerlinTimezone() throws JsonProcessingException {
    Props props = Props.builder().maxDate(TODAY_PLACEHOLDER).build();

    String json = objectMapper.writeValueAsString(props);

    String expectedDate = LocalDate.now(ZONE_EUROPE_BERLIN).toString();
    assertThat(json).contains("\"maxDate\":\"" + expectedDate + "\"");
  }

  @Test
  void writesRegularDateAsIs() throws JsonProcessingException {
    Props props = Props.builder().maxDate("2020-06-15").build();

    String json = objectMapper.writeValueAsString(props);

    assertThat(json).contains("\"maxDate\":\"2020-06-15\"");
  }

  @Test
  void resolvesPlaceholderOnMinDate() throws JsonProcessingException {
    Props props = Props.builder().minDate(TODAY_PLACEHOLDER).build();

    String json = objectMapper.writeValueAsString(props);

    String expectedDate = LocalDate.now(ZONE_EUROPE_BERLIN).toString();
    assertThat(json).contains("\"minDate\":\"" + expectedDate + "\"");
  }

  @Test
  void nullDatesAreOmitted() throws JsonProcessingException {
    Props props = Props.builder().build();

    String json = objectMapper.writeValueAsString(props);

    assertThat(json).doesNotContain("minDate").doesNotContain("maxDate");
  }
}
