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

import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.fhir_ui_data_model_translation_service.FeatureFlags;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.EnableWhen;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Props;
import org.junit.jupiter.api.Test;

class QuestionnaireLayoutProcessorTest {

  @Test
  void appliesIndentationWhenDiseaseIndentIsEnabled() {
    final QuestionnaireLayoutProcessor processor =
        new QuestionnaireLayoutProcessor(FeatureFlags.builder().diseaseIndent(true).build());
    final FieldGroup root = FieldGroup.builder().key("root").build();
    final FieldGroup followUp =
        FieldGroup.builder()
            .parent(root)
            .key("follow-up")
            .type(FieldGroup.TYPE_INPUT)
            .props(propsWithEnableWhen("sibling.answer"))
            .build();

    processor.applyLayout(root);

    assertThat(root.getFieldGroups()).hasSize(1);
    final FieldGroup wrapper = root.getFieldGroups().getFirst();
    assertThat(wrapper.getWrappers())
        .containsExactly(QuestionnaireLayoutItemIndentation.INDENTATION_WRAPPER);
    assertThat(wrapper.getFieldGroups()).containsExactly(followUp);
  }

  @Test
  void doesNotApplyIndentationWhenDiseaseIndentIsDisabled() {
    final QuestionnaireLayoutProcessor processor =
        new QuestionnaireLayoutProcessor(FeatureFlags.builder().diseaseIndent(false).build());
    final FieldGroup root = FieldGroup.builder().key("root").build();
    final FieldGroup followUp =
        FieldGroup.builder()
            .parent(root)
            .key("follow-up")
            .type(FieldGroup.TYPE_INPUT)
            .props(propsWithEnableWhen("sibling.answer"))
            .build();

    processor.applyLayout(root);

    assertThat(root.getFieldGroups()).containsExactly(followUp);
    assertThat(followUp.getWrappers()).isNull();
    assertThat(followUp.getProps().getEnableWhen())
        .singleElement()
        .extracting(EnableWhen::getPath)
        .isEqualTo("sibling.answer");
  }

  private Props propsWithEnableWhen(String path) {
    return Props.builder()
        .enableWhen(new EnableWhen[] {EnableWhen.builder().path(path).build()})
        .build();
  }
}
