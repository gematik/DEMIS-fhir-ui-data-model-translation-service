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
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Props;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * Date picker with features:
 *
 * <ul>
 *   <li>Allows to select a date with different precisions (day, month, year)
 *   <li>Allows to set a minimum and maximum date
 *   <li>Allows to start with year selection
 *   <li>Allows to set a default minimum date (1900-01-01)
 *   <li>Allows to set a maximum date that is not in the future
 * </ul>
 */
@SuperBuilder
public class DatePicker extends TextInput {

  /** Default key for the date picker field */
  public static final String KEY_DEFAULT = "valueDate";

  /** Reasonable minimum date for a date picker */
  private static final LocalDate DEFAULT_MIN_DATE = LocalDate.of(1900, 1, 1);

  private List<Precision> allowedPrecisions;
  @Builder.Default private boolean minDateDefault = true;
  private boolean maxDateNotInFuture;
  private LocalDate minDate;
  private LocalDate maxDate;
  private boolean startWithYearSelection;

  @Override
  void applyTo(FieldGroup.FieldGroupBuilder builder) {
    super.applyTo(builder);
    builder.key(KEY_DEFAULT);
    builder.type(FieldGroup.TYPE_DATEPICKER);
  }

  @Override
  void applyTo(Props.PropsBuilder builder) {
    super.applyTo(builder);
    setAllowedPrecisions(builder);
    setPlaceholder(builder);
    setMinDate(builder);
    setMaxDate(builder);
    setYearSelection(builder);
  }

  private void setAllowedPrecisions(Props.PropsBuilder builder) {
    if ((allowedPrecisions != null)
        && !allowedPrecisions.containsAll(EnumSet.allOf(Precision.class))) {
      builder.allowedPrecisions(
          allowedPrecisions.stream().sorted().map(Precision::getText).toArray(String[]::new));
    }
  }

  private void setPlaceholder(Props.PropsBuilder builder) {
    final List<Precision> precisions;
    if (allowedPrecisions == null || allowedPrecisions.isEmpty()) {
      precisions = new ArrayList<>(EnumSet.allOf(Precision.class));
    } else {
      precisions = allowedPrecisions;
    }
    builder.placeholder(
        precisions.stream()
            .sorted()
            .map(Precision::getPlaceholder)
            .collect(Collectors.joining(" | ")));
  }

  private void setMinDate(Props.PropsBuilder builder) {
    if (minDate != null) {
      builder.minDate(minDate.toString());
    } else if (minDateDefault) {
      builder.minDate(DEFAULT_MIN_DATE.toString());
    }
  }

  private void setMaxDate(Props.PropsBuilder builder) {
    if (maxDate != null) {
      builder.maxDate(maxDate.toString());
    } else if (maxDateNotInFuture) {
      builder.maxDate(LocalDate.now().toString());
    }
  }

  private void setYearSelection(Props.PropsBuilder builder) {
    if (startWithYearSelection) {
      builder.multiYear(Boolean.TRUE);
    }
  }

  /**
   * Parses a regex and infers which date precisions are allowed. It assumes that the regex contains
   * only valid ISO date formats, separated by '|' if multiple formats are allowed.
   */
  static List<Precision> detectPrecisionsFromRegex(String regex) {
    if (regex == null || regex.isBlank()) return List.of();
    EnumSet<Precision> result = EnumSet.noneOf(Precision.class);

    for (String alternative : regex.split("\\|")) {
      // Normalize by removing possible dash within brackets (e.g. [0-9]) to not interfere with
      // dash as separator between date parts (year, month, day)
      String normalized = alternative.trim().replaceAll("\\[[^]]*+]", "[X]");

      long dashCount = normalized.chars().filter(ch -> ch == '-').count();
      if (dashCount == 2) {
        result.add(Precision.DAY);
      } else if (dashCount == 1) {
        result.add(Precision.MONTH);
      } else if (dashCount == 0) {
        result.add(Precision.YEAR);
      }
    }
    return new ArrayList<>(result);
  }

  /**
   * Extend the builder with a custom method to set allowed precisions from a regex.
   *
   * @param <C>
   * @param <B>
   */
  public abstract static class DatePickerBuilder<
          C extends DatePicker, B extends DatePickerBuilder<C, B>>
      extends TextInputBuilder<C, B> {

    public B allowedPrecisionsFromRegex(String regex) {
      var precisionFromRegex = detectPrecisionsFromRegex(regex);
      if (!precisionFromRegex.isEmpty()) {
        this.allowedPrecisions = precisionFromRegex;
      }
      return self();
    }
  }

  @Getter
  public enum Precision {

    /*
     * The order of the enum values is important, as it determines the order in which the
     * placeholders are displayed in the UI.
     */

    /** The full date is of interest, e.g. 01.01.2023 */
    DAY("day", "TT.MM.JJJJ"),

    /** Only the month and year are of interest, e.g. 01.2023 or 01.01.2023 */
    MONTH("month", "MM.JJJJ"),

    /** Only the year is of interest, e.g. 2023 */
    YEAR("year", "JJJJ");

    private final String text;
    private final String placeholder;

    Precision(String text, String placeholder) {
      this.text = text;
      this.placeholder = placeholder;
    }
  }
}
