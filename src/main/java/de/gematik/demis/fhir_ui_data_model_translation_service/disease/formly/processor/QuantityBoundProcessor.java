package de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor;

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

import com.google.common.annotations.VisibleForTesting;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r4.model.Type;
import org.springframework.stereotype.Service;

/**
 * Process MinQuantity and MaxQuantity extensions for {@link
 * org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent}.
 *
 * <p><strong>Declaring quantity values</strong><br>
 * FHIR uses {@link BigDecimal} to represent the value of a {@link Quantity} type. A BigDecimal
 * carries a scale that tells us how precise the value is. For example:
 *
 * <pre>
 * scale 2 => 0.01
 * scale 3 => 0.001
 * </pre>
 *
 * <p>The task of this class is to extract min and max values from {@link Quantity}. If the snapshot
 * declares <code>X &gt; 0.270</code> it really means <code>min X = 0.271</code>. To calculate
 * <code>min = 0.271</code> our algorithm uses the scale to adjust the initial value of {@link
 * Quantity}. To ensure correctness it is important to get the correct scale. However, there are
 * pitfalls when using and parsing {@link BigDecimal}: Java truncates 0.270 to 0.27 when parsing a
 * double. Therefore <code>BigDecimal.valueOf(0.270)</code> sets scale to 2 instead of 3.
 *
 * <p>To ensure that we work with the correct scale do:
 *
 * <ul>
 *   <li>declare >= 0.271 or <= 0.269 so that we don't have to adjust and in case of doubt Java will
 *       parse the scale of the double correctly.
 *   <li>supply a String, because new BigDecimal("0.270") will result in the correct scale being
 *       set.
 * </ul>
 *
 * <p>We can't guess the scale just based on the scale of the starting value, because then we can't
 * differentiate between 0.28 and 0.279. This is only possible if we introduce additional
 * constraints on the comparator that can be used.
 */
@Service
public class QuantityBoundProcessor {

  @VisibleForTesting
  static final String MIN_QUANTITY_EXTENSION =
      "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-minQuantity";

  @VisibleForTesting
  static final String MAX_QUANTITY_EXTENSION =
      "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-maxQuantity";

  /**
   * Extract the value and adjust it as necessary for comparators <code>&lt;, &gt;</code>.
   *
   * @return the value of this bound so it always refers to a min or max value.
   */
  @Nonnull
  private static BigDecimal getValue(@Nonnull Extension quantity) {
    final Quantity q = getQuantity(quantity);
    final BigDecimal value = q.getValue();
    if (value == null) {
      throw new IllegalArgumentException("Quantity value is 'null'");
    }
    return value;
  }

  /**
   * Extract the value and adjust it as necessary for comparators <code>&lt;, &gt;</code>.
   *
   * @return the value of this bound so it always refers to a min or max value.
   */
  @Nonnull
  private static Quantity getQuantity(@Nonnull Extension extension) {
    if (extension.getValue() instanceof final Quantity q) {
      return q;
    }
    throw unsupportedValue(extension.getValue());
  }

  @Nonnull
  private static IllegalArgumentException unsupportedValue(@Nullable final Type value) {
    return new IllegalArgumentException(
        String.format("Expected value of type Quantity but got '%s'", value));
  }

  @Nonnull
  private static Optional<Extension> getAtMostOneExtension(
      @Nonnull Questionnaire.QuestionnaireItemComponent item, @Nonnull final String extensionUrl) {
    final List<Extension> extensionsByUrl = item.getExtensionsByUrl(extensionUrl);
    if (extensionsByUrl.size() > 1) {
      throw new IllegalArgumentException(
          String.format("Found more than one extension with URL '%s'.", extensionUrl));
    } else if (extensionsByUrl.isEmpty()) {
      return Optional.empty();
    }
    return Optional.ofNullable(extensionsByUrl.getFirst());
  }

  /**
   * @param item A questionnaire item with optional MinQuantity and/or MaxQuantity. If these are
   *     present, then exactly one comparator and exactly one value is expected.
   * @return {@link Optional#empty()} if no extension found or the minimum value that has to be
   *     supported. I.e. when this method returns 5, then 5 and more is valid.
   * @throws IllegalArgumentException in case the object does not conform to the expected state
   */
  @Nonnull
  public Optional<BigDecimal> findMin(
      @Nonnull final Questionnaire.QuestionnaireItemComponent item) {
    final Optional<Extension> quantityExtension =
        getAtMostOneExtension(item, MIN_QUANTITY_EXTENSION);
    return quantityExtension.map(QuantityBoundProcessor::getValue);
  }

  /**
   * @return always a value that expresses a maximum that has to be supported. I.e. when this method
   *     returns 5, then 5 and less is valid.
   */
  @Nonnull
  public Optional<BigDecimal> findMax(
      @Nonnull final Questionnaire.QuestionnaireItemComponent item) {
    final Optional<Extension> quantityExtension =
        getAtMostOneExtension(item, MAX_QUANTITY_EXTENSION);
    return quantityExtension.map(QuantityBoundProcessor::getValue);
  }

  /**
   * @return The smallest unit with the highest scale for an optional min and max value. If min =
   *     0.02 and max = 0.200 returns scaleOf(200) => 3.
   */
  @Nonnull
  public Optional<BigDecimal> findStepValue(
      @Nonnull final Questionnaire.QuestionnaireItemComponent item) {
    // Stream allows us to skip empty Optionals or if both are present to just take the highest
    // precision
    return Stream.of(findMin(item), findMax(item))
        .flatMap(Optional::stream)
        .mapToInt(BigDecimal::scale)
        .max()
        .stream()
        .mapToObj(scale -> BigDecimal.valueOf(1, scale))
        .findFirst();
  }

  /**
   * Return the code field associated with the Quantity value of the MIN/MAX extensions.
   *
   * @throws IllegalArgumentException if min AND max are given but their code value doesn't match
   * @throws IllegalArgumentException if no min OR max extension is present
   */
  @Nonnull
  public String findUnitCode(@Nonnull final Questionnaire.QuestionnaireItemComponent item) {
    final Optional<Extension> minExtension = getAtMostOneExtension(item, MIN_QUANTITY_EXTENSION);
    final Optional<Extension> maxExtension = getAtMostOneExtension(item, MAX_QUANTITY_EXTENSION);

    if (minExtension.isPresent() && maxExtension.isPresent()) {
      final String minCode = getQuantity(minExtension.get()).getCode();
      final String maxCode = getQuantity(maxExtension.get()).getCode();
      if (!Objects.equals(minCode, maxCode)) {
        throw new IllegalArgumentException("Expect Quantity#code for min and max to be equal.");
      }
      return minCode;
    }

    final Optional<String> unitCode =
        minExtension
            .map(QuantityBoundProcessor::getQuantity)
            .map(Quantity::getCode)
            .or(() -> maxExtension.map(QuantityBoundProcessor::getQuantity).map(Quantity::getCode));
    if (unitCode.isEmpty()) {
      throw new IllegalArgumentException("Expected Quantity#code but found none");
    }

    return unitCode.get();
  }

  /**
   * Return the code field associated with the Quantity value of the MIN/MAX extensions.
   *
   * @throws IllegalArgumentException if min AND max are given but their system value doesn't match
   * @throws IllegalArgumentException if no min OR max extension is present
   */
  @Nonnull
  public String findUnitSystem(@Nonnull final Questionnaire.QuestionnaireItemComponent item) {
    final Optional<Extension> minExtension = getAtMostOneExtension(item, MIN_QUANTITY_EXTENSION);
    final Optional<Extension> maxExtension = getAtMostOneExtension(item, MAX_QUANTITY_EXTENSION);

    if (minExtension.isPresent() && maxExtension.isPresent()) {
      final String minCode = getQuantity(minExtension.get()).getSystem();
      final String maxCode = getQuantity(maxExtension.get()).getSystem();
      if (!Objects.equals(minCode, maxCode)) {
        throw new IllegalArgumentException("Expect Quantity#code for min and max to be equal.");
      }
      return minCode;
    }

    final Optional<String> unitSystem =
        minExtension
            .map(QuantityBoundProcessor::getQuantity)
            .map(Quantity::getSystem)
            .or(
                () ->
                    maxExtension.map(QuantityBoundProcessor::getQuantity).map(Quantity::getSystem));
    if (unitSystem.isEmpty()) {
      throw new IllegalArgumentException("Expected Quantity#system but found none");
    }

    return unitSystem.get();
  }
}
