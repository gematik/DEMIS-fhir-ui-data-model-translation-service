package de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.fieldtypes;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.*;

import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Props;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class DatePickerTest {

  private static final String CLASS_NAME = "clazz";
  private static final String LABEL = "Date Picker";

  private final FieldGroup parent = FieldGroup.builder().build();

  @Test
  void testAllProperties() {
    final LocalDate minDate = LocalDate.of(2020, 1, 1);
    final LocalDate maxDate = LocalDate.now();
    FieldGroup datePicker =
        DatePicker.builder()
            .label(LABEL)
            .allowedPrecisions(List.of(DatePicker.Precision.YEAR, DatePicker.Precision.DAY))
            .minDate(minDate)
            .maxDate(maxDate)
            .startWithYearSelection(true)
            .parent(parent)
            .className(CLASS_NAME)
            .build()
            .createFieldGroup();

    assertThat(datePicker).isNotNull();
    assertThat(datePicker.getKey()).isEqualTo(DatePicker.KEY_DEFAULT);
    assertThat(datePicker.getType()).isSameAs(FieldGroup.TYPE_DATEPICKER);
    assertThat(datePicker.getClassName()).isEqualTo(CLASS_NAME);
    assertThat(datePicker.getParent()).isSameAs(parent);

    final Props props = datePicker.getProps();
    assertThat(props.getAllowedPrecisions())
        .as("precisions sorted on enum order")
        .isEqualTo(new String[] {"day", "year"});
    assertThat(props.getPlaceholder())
        .as("placeholder sorted on enum order")
        .isEqualTo("TT.MM.JJJJ | JJJJ");
    assertThat(props.getMinDate()).isEqualTo(minDate.toString());
    assertThat(props.getMaxDate()).isEqualTo(maxDate.toString());
    assertThat(props.getMultiYear()).isTrue();
    assertThat(props.getLabel()).isEqualTo(LABEL);
  }

  @Test
  void testMinimumInputExceptions() {
    final var builder = DatePicker.builder();
    assertThatException()
        .isThrownBy(builder.build()::createFieldGroup)
        .withMessage("FieldGroup className must not be empty");
    builder.className(CLASS_NAME);
    assertThatException()
        .isThrownBy(builder.build()::createFieldGroup)
        .withMessage("FieldGroup parent must not be null");
    builder.parent(parent);
    assertThatNoException().isThrownBy(builder.build()::createFieldGroup);
  }

  @Test
  void testDefaults() {
    final FieldGroup datePicker =
        DatePicker.builder().parent(parent).className(CLASS_NAME).build().createFieldGroup();

    assertThat(datePicker.getKey()).isEqualTo(DatePicker.KEY_DEFAULT);
    assertThat(datePicker.getType()).isSameAs(FieldGroup.TYPE_DATEPICKER);
    assertThat(datePicker.getClassName()).isEqualTo(CLASS_NAME);
    assertThat(datePicker.getParent()).isSameAs(parent);

    final Props props = datePicker.getProps();
    assertThat(props.getAllowedPrecisions())
        .as("all precisions is default and not part of channel coding")
        .isNull();
    assertThat(props.getMinDate())
        .as("default minDate is 01.01.1900")
        .isEqualTo(LocalDate.of(1900, 1, 1).toString());
    assertThat(props.getMaxDate()).as("no default maxDate").isNull();
    assertThat(props.getMultiYear()).as("no default start with year selection").isNull();
  }

  @Test
  void testMaxDateNotInFuture() {
    final FieldGroup datePicker =
        DatePicker.builder()
            .parent(parent)
            .className(CLASS_NAME)
            .maxDateNotInFuture(true)
            .build()
            .createFieldGroup();

    assertThat(datePicker.getProps().getMaxDate()).isEqualTo(LocalDate.now().toString());
  }

  @Test
  void testTypeOverriding() {
    final FieldGroup datePicker =
        DatePicker.builder()
            .parent(parent)
            .className(CLASS_NAME)
            .type("custom-date-picker")
            .build()
            .createFieldGroup();

    assertThat(datePicker.getType()).isEqualTo("custom-date-picker");
  }

  @Test
  void testKeyOverriding() {
    final String customKey = "customDatePickerKey";
    final FieldGroup datePicker =
        DatePicker.builder()
            .parent(parent)
            .className(CLASS_NAME)
            .key(customKey)
            .build()
            .createFieldGroup();

    assertThat(datePicker.getKey()).isEqualTo(customKey);
  }
}
