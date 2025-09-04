package de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor;

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
import static org.mockito.Mockito.mock;

import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import org.hl7.fhir.r4.model.Questionnaire;
import org.junit.jupiter.api.Test;

class TextProcessorTest {

  // Regression for DEMIS-3960
  @Test
  void thatRequiredAttributeIsInheritedFromItem() {
    final TextProcessor textProcessor =
        new TextProcessor(mock(EnableWhenProcessor.class), mock(ClipboardProcessor.class));

    final Questionnaire.QuestionnaireItemComponent item =
        new Questionnaire.QuestionnaireItemComponent();
    item.setRequired(true);
    final FieldGroup[] groups =
        textProcessor.createFieldGroup(item, FieldGroup.builder().build(), "any");

    assertThat(groups)
        .hasSize(1)
        .allSatisfy(
            fieldGroup -> {
              assertThat(fieldGroup.getProps().getRequired()).isTrue();
            });
  }
}
