package de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.fhir;

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

import static org.assertj.core.api.Assertions.assertThat;

import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r4.model.StringType;
import org.junit.jupiter.api.Test;

class PortalFhirExtensionTest {

  private static final String TOOLTIP = "Hello world!";
  private static final String REGEX = "^[A-Za-z]+$";

  private final RegexExtension regexExtension = new RegexExtension();
  private final TooltipExtension tooltipExtension = new TooltipExtension();
  private final Questionnaire.QuestionnaireItemComponent item =
      new Questionnaire.QuestionnaireItemComponent();

  @Test
  void givenTooltipWhenGetStringValueThenTooltip() {
    item.addExtension(
        new Extension().setUrl(TooltipExtension.URL).setValue(new StringType(TOOLTIP)));
    verifyNoRegex();
    verifyTooltip(TOOLTIP);
  }

  @Test
  void givenNoExtensionWhenGetStringValueThenNull() {
    verifyNoRegex();
    verifyNoTooltip();
  }

  @Test
  void givenRegexWhenGetStringValueThenRegex() {
    item.addExtension(new Extension().setUrl(RegexExtension.URL).setValue(new StringType(REGEX)));
    verifyRegex(REGEX);
    verifyNoTooltip();
  }

  @Test
  void givenRegexAndTooltipWhenGetStringValueThenBoth() {
    item.addExtension(new Extension().setUrl(RegexExtension.URL).setValue(new StringType(REGEX)));
    item.addExtension(
        new Extension().setUrl(TooltipExtension.URL).setValue(new StringType(TOOLTIP)));
    verifyRegex(REGEX);
    verifyTooltip(TOOLTIP);
  }

  @Test
  void givenOtherExtensionWhenGetStringValueThenNull() {
    item.addExtension(
        new Extension().setUrl(RegexExtension.URL + "/foobar").setValue(new StringType("Other")));
    verifyNoRegex();
    verifyNoTooltip();
  }

  private void verifyNoTooltip() {
    verifyTooltip(null);
  }

  private void verifyNoRegex() {
    verifyRegex(null);
  }

  private void verifyTooltip(String expectedTooltip) {
    verifyValue(tooltipExtension.getStringValueOrNull(item), expectedTooltip);
  }

  private void verifyRegex(String expectedRegex) {
    verifyValue(regexExtension.getStringValueOrNull(item), expectedRegex);
  }

  private void verifyValue(String actualValue, String expectedValue) {
    if (expectedValue == null) {
      assertThat(actualValue).isNull();
    } else {
      assertThat(actualValue).isEqualTo(expectedValue);
    }
  }
}
