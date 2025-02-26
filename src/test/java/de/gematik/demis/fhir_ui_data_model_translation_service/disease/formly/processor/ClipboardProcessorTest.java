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
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.DiseaseClipboardProps;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.ImportSpec;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Props;
import java.util.Collections;
import org.hl7.fhir.r4.model.Questionnaire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClipboardProcessorTest {

  @Mock private DiseaseClipboardProps diseaseClipboardProps;
  private ClipboardProcessor clipboardProcessor;

  @BeforeEach
  void setup() {
    this.clipboardProcessor = new ClipboardProcessor(this.diseaseClipboardProps);
  }

  @Test
  void createClipboard_shouldCreateProps() {
    Mockito.when(this.diseaseClipboardProps.common())
        .thenReturn(Collections.singletonMap("key", "value"));
    Questionnaire.QuestionnaireItemComponent item = new Questionnaire.QuestionnaireItemComponent();
    item.setLinkId("key");
    FieldGroup fieldGroup = FieldGroup.builder().type(FieldGroup.TYPE_INPUT).build();
    this.clipboardProcessor.createClipboard(item, fieldGroup);
    Props props = fieldGroup.getProps();
    assertThat(props).isNotNull();
    ImportSpec importSpec = props.getImportSpec();
    assertThat(importSpec).isNotNull();
    assertThat(importSpec.getImportKey()).isEqualTo("value");
    assertThat(importSpec.isMulti()).isFalse();
  }

  @Test
  void createClipboard_shouldIgnoreNullTypeFieldGroup() {
    Questionnaire.QuestionnaireItemComponent item = new Questionnaire.QuestionnaireItemComponent();
    FieldGroup fieldGroup = FieldGroup.builder().build();
    this.clipboardProcessor.createClipboard(item, fieldGroup);
    assertThat(fieldGroup.getProps()).isNull();
  }
}
