package de.gematik.demis.fhir_ui_data_model_translation_service.model.disease;

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

import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FormlyFieldConfigs;
import org.junit.jupiter.api.Test;

class FormlyFieldConfigsTest {
  @Test
  void testBuildPattern() {
    String expectedTemplate = "template";
    String expectedClassName = "className";
    FieldGroup[] expectedFieldGroup = new FieldGroup[] {FieldGroup.builder().build()};
    String expectedFieldGroupClassName = "fieldGroupClassName";

    FormlyFieldConfigs formlyFieldConfigs =
        FormlyFieldConfigs.builder()
            .template(expectedTemplate)
            .className(expectedClassName)
            .fieldGroup(expectedFieldGroup)
            .fieldGroupClassName(expectedFieldGroupClassName)
            .build();

    assertThat(formlyFieldConfigs.getTemplate()).isEqualTo(expectedTemplate);
    assertThat(formlyFieldConfigs.getClassName()).isEqualTo(expectedClassName);
    assertThat(formlyFieldConfigs.getFieldGroup()).isEqualTo(expectedFieldGroup);
    assertThat(formlyFieldConfigs.getFieldGroupClassName()).isEqualTo(expectedFieldGroupClassName);
  }
}
