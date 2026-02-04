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

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.hl7.fhir.r4.model.DecimalType;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Questionnaire;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class QuantityBoundProcessorTest {

  private final QuantityBoundProcessor service = new QuantityBoundProcessor();

  @Nested
  class Unit {
    @Test
    void thatUnitIsReturnedCorrectly() {
      final Questionnaire.QuestionnaireItemComponent item = createItem();
      final String unitCode = service.findUnitCode(item);
      final String unitSystem = service.findUnitSystem(item);
      assertThat(unitCode).isEqualTo("anyCode");
      assertThat(unitSystem).isEqualTo("anySystem");
    }

    @Test
    void thatItWorksWithOnlyMin() {
      final Questionnaire.QuestionnaireItemComponent item =
          new Questionnaire.QuestionnaireItemComponent();
      final Quantity minQuantity = new Quantity().setCode("anyCode").setSystem("anySystem");
      item.addExtension(QuantityBoundProcessor.MIN_QUANTITY_EXTENSION, minQuantity);
      final String unitCode = service.findUnitCode(item);
      final String unitSystem = service.findUnitSystem(item);
      assertThat(unitCode).isEqualTo("anyCode");
      assertThat(unitSystem).isEqualTo("anySystem");
    }

    @Test
    void thatItWorksWithOnlyMax() {
      final Questionnaire.QuestionnaireItemComponent item =
          new Questionnaire.QuestionnaireItemComponent();
      final Quantity maxQuantity = new Quantity().setCode("anyCode").setSystem("anySystem");
      item.addExtension(QuantityBoundProcessor.MAX_QUANTITY_EXTENSION, maxQuantity);
      final String unitCode = service.findUnitCode(item);
      final String unitSystem = service.findUnitSystem(item);
      assertThat(unitCode).isEqualTo("anyCode");
      assertThat(unitSystem).isEqualTo("anySystem");
    }
  }

  @Nested
  class Integers {

    @Test
    void thatMinMaxValuesAreIdentifiedCorrectly() {
      final Questionnaire.QuestionnaireItemComponent item = createItem();
      final Optional<BigDecimal> minValue = service.findMin(item);
      final Optional<BigDecimal> maxValue = service.findMax(item);
      assertThat(minValue).contains(BigDecimal.valueOf(10));
      assertThat(maxValue).contains(BigDecimal.valueOf(100));
    }
  }

  @Nested
  class Decimal {

    @Test
    void thatDecimalValuesAreAdjustedWithCorrectScale() {
      final Questionnaire.QuestionnaireItemComponent item =
          new Questionnaire.QuestionnaireItemComponent();
      // Due to the constraints the Quantity constructor and Java truncating 0.270 to 0.27 we have
      // to set BigDecimal explicitly
      final Quantity minQuantity = new Quantity().setValue(BigDecimal.valueOf(1, 2));
      item.addExtension(QuantityBoundProcessor.MIN_QUANTITY_EXTENSION, minQuantity);
      final Quantity maxQuantity = new Quantity().setValue(BigDecimal.valueOf(1, 3));
      item.addExtension(QuantityBoundProcessor.MAX_QUANTITY_EXTENSION, maxQuantity);
      final Optional<BigDecimal> precision = service.findStepValue(item);
      assertThat(precision).contains(BigDecimal.valueOf(1, 3));
    }

    @Test
    void thatBigDecimalReturnsCorrectScale() {
      final Questionnaire.QuestionnaireItemComponent item =
          new Questionnaire.QuestionnaireItemComponent();
      // Due to the constraints the Quantity constructor and Java truncating 0.270 to 0.27 we have
      // to set BigDecimal explicitly
      final Quantity minQuantity = new Quantity().setValue(new BigDecimal("0.270"));
      item.addExtension(QuantityBoundProcessor.MIN_QUANTITY_EXTENSION, minQuantity);
      final Quantity maxQuantity = new Quantity().setValue(new BigDecimal("1.300"));
      item.addExtension(QuantityBoundProcessor.MAX_QUANTITY_EXTENSION, maxQuantity);
      final Optional<BigDecimal> minValue = service.findMin(item);
      final Optional<BigDecimal> maxValue = service.findMax(item);
      assertThat(minValue).contains(new BigDecimal("0.270"));
      assertThat(maxValue).contains(new BigDecimal("1.300"));
    }
  }

  @Nested
  class FindScale {
    @Test
    void thatIntegersAreSupported() {
      final Questionnaire.QuestionnaireItemComponent item = createItem();
      final Optional<BigDecimal> precision = service.findStepValue(item);
      assertThat(precision).contains(BigDecimal.ONE);
    }

    @Test
    void thatDecimalsAreSupported() {
      final Questionnaire.QuestionnaireItemComponent item =
          new Questionnaire.QuestionnaireItemComponent();
      // Due to the constraints the Quantity constructor and Java truncating 0.270 to 0.27 we have
      // to set BigDecimal explicitly
      final Quantity minQuantity = new Quantity().setValue(new BigDecimal("0.270"));
      item.addExtension(QuantityBoundProcessor.MIN_QUANTITY_EXTENSION, minQuantity);
      final Quantity maxQuantity = new Quantity().setValue(new BigDecimal("1.300"));
      item.addExtension(QuantityBoundProcessor.MAX_QUANTITY_EXTENSION, maxQuantity);
      final Optional<BigDecimal> precision = service.findStepValue(item);
      assertThat(precision).contains(BigDecimal.valueOf(0.001));
    }

    @Test
    void thatHighestPrecisionWins() {
      final Questionnaire.QuestionnaireItemComponent item =
          new Questionnaire.QuestionnaireItemComponent();
      final Quantity minQuantity =
          new Quantity()
              .setComparator(Quantity.QuantityComparator.GREATER_THAN)
              .setValue(new BigDecimal("0.27"));
      item.addExtension(QuantityBoundProcessor.MIN_QUANTITY_EXTENSION, minQuantity);
      final Quantity maxQuantity =
          new Quantity()
              .setComparator(Quantity.QuantityComparator.LESS_THAN)
              .setValue(new BigDecimal("1.300"));
      item.addExtension(QuantityBoundProcessor.MAX_QUANTITY_EXTENSION, maxQuantity);
      final Optional<BigDecimal> step = service.findStepValue(item);
      assertThat(step).contains(BigDecimal.valueOf(0.001));
    }

    @Test
    void thatItWorksWithMissingMax() {
      final Questionnaire.QuestionnaireItemComponent item =
          new Questionnaire.QuestionnaireItemComponent();
      final Quantity minQuantity =
          new Quantity()
              .setComparator(Quantity.QuantityComparator.GREATER_THAN)
              .setValue(new BigDecimal("0.270"));
      item.addExtension(QuantityBoundProcessor.MIN_QUANTITY_EXTENSION, minQuantity);
      final Optional<BigDecimal> step = service.findStepValue(item);
      assertThat(step).contains(BigDecimal.valueOf(0.001));
    }

    @Test
    void thatItWorksWithMissingMin() {
      final Questionnaire.QuestionnaireItemComponent item =
          new Questionnaire.QuestionnaireItemComponent();
      final Quantity maxQuantity =
          new Quantity()
              .setComparator(Quantity.QuantityComparator.LESS_THAN)
              .setValue(new BigDecimal("1.300"));
      item.addExtension(QuantityBoundProcessor.MAX_QUANTITY_EXTENSION, maxQuantity);
      final Optional<BigDecimal> step = service.findStepValue(item);
      assertThat(step).contains(BigDecimal.valueOf(0.001));
    }

    @Test
    void thatEmptyIsReturnedForMissingQuantities() {
      final Questionnaire.QuestionnaireItemComponent item =
          new Questionnaire.QuestionnaireItemComponent();
      final Optional<BigDecimal> step = service.findStepValue(item);
      assertThat(step).isEmpty();
    }
  }

  @Nested
  class Validations {

    @Test
    void thatExceptionIsRaisedForMissingValue() {
      final Questionnaire.QuestionnaireItemComponent item =
          new Questionnaire.QuestionnaireItemComponent();
      item.addExtension(QuantityBoundProcessor.MIN_QUANTITY_EXTENSION, new Quantity());
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> service.findMin(item))
          .withMessage("Quantity value is 'null'");
    }

    @Test
    void thatExceptionIsRaisedForMultipleMinQuantityExtensions() {
      final Questionnaire.QuestionnaireItemComponent item = createItem();
      item.addExtension(QuantityBoundProcessor.MIN_QUANTITY_EXTENSION, new Quantity());
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> service.findMin(item))
          .withMessageStartingWith("Found more than one extension")
          .withMessageContaining(QuantityBoundProcessor.MIN_QUANTITY_EXTENSION);
      assertThatNoException().isThrownBy(() -> service.findMax(item));
    }

    @Test
    void thatExceptionIsRaisedForMultipleMaxQuantityExtensions() {
      final Questionnaire.QuestionnaireItemComponent item = createItem();
      item.addExtension(QuantityBoundProcessor.MAX_QUANTITY_EXTENSION, new Quantity());
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> service.findMax(item))
          .withMessageStartingWith("Found more than one extension")
          .withMessageContaining(QuantityBoundProcessor.MAX_QUANTITY_EXTENSION);
      assertThatNoException().isThrownBy(() -> service.findMin(item));
    }

    @Test
    void thatExceptionIsRaisedForInvalidValueOfQuantityExtension() {
      final Questionnaire.QuestionnaireItemComponent result =
          new Questionnaire.QuestionnaireItemComponent();
      result.addExtension(QuantityBoundProcessor.MIN_QUANTITY_EXTENSION, new DecimalType(10));
      result.addExtension(QuantityBoundProcessor.MAX_QUANTITY_EXTENSION, new DecimalType(100));
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> service.findMin(result))
          .withMessage("Expected value of type Quantity but got 'DecimalType[10]'");
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> service.findMax(result))
          .withMessage("Expected value of type Quantity but got 'DecimalType[100]'");
    }

    @Test
    void thatExceptionIsRaisedWhenUnitSystemIsMissing() {
      final Questionnaire.QuestionnaireItemComponent item =
          new Questionnaire.QuestionnaireItemComponent();
      final Quantity minQuantity = new Quantity().setCode("anyCode");
      item.addExtension(QuantityBoundProcessor.MIN_QUANTITY_EXTENSION, minQuantity);

      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> service.findUnitSystem(item));
    }

    @Test
    void thatExceptionIsRaisedWhenUnitCodeIsMissing() {
      final Questionnaire.QuestionnaireItemComponent item =
          new Questionnaire.QuestionnaireItemComponent();
      final Quantity minQuantity = new Quantity().setSystem("anySystem");
      item.addExtension(QuantityBoundProcessor.MIN_QUANTITY_EXTENSION, minQuantity);
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> service.findUnitCode(item));
    }

    @Test
    void thatExceptionIsRaisedIfMinMaxUnitSystemsDontMatch() {
      final Questionnaire.QuestionnaireItemComponent item =
          new Questionnaire.QuestionnaireItemComponent();
      final Quantity minQuantity = new Quantity().setSystem("anySystem");
      final Quantity maxQuantity = new Quantity().setSystem("otherSystem");
      item.addExtension(QuantityBoundProcessor.MIN_QUANTITY_EXTENSION, minQuantity);
      item.addExtension(QuantityBoundProcessor.MAX_QUANTITY_EXTENSION, maxQuantity);
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> service.findUnitSystem(item));
    }

    @Test
    void thatExceptionIsRaisedIfMinMaxUnitCodesDontMatch() {

      final Questionnaire.QuestionnaireItemComponent item =
          new Questionnaire.QuestionnaireItemComponent();
      final Quantity minQuantity = new Quantity().setCode("anyCode");
      final Quantity maxQuantity = new Quantity().setCode("otherCode");
      item.addExtension(QuantityBoundProcessor.MIN_QUANTITY_EXTENSION, minQuantity);
      item.addExtension(QuantityBoundProcessor.MAX_QUANTITY_EXTENSION, maxQuantity);

      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> service.findUnitCode(item));
    }
  }

  @Nonnull
  private Questionnaire.QuestionnaireItemComponent createItem() {
    final Questionnaire.QuestionnaireItemComponent result =
        new Questionnaire.QuestionnaireItemComponent();
    result.addExtension(
        QuantityBoundProcessor.MIN_QUANTITY_EXTENSION,
        new Quantity(10).setSystem("anySystem").setCode("anyCode").setUnit("anyUnit"));
    result.addExtension(
        QuantityBoundProcessor.MAX_QUANTITY_EXTENSION,
        new Quantity(100).setSystem("anySystem").setCode("anyCode").setUnit("anyUnit"));
    return result;
  }
}
