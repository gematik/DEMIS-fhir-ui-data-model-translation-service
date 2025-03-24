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

import de.gematik.demis.fhir_ui_data_model_translation_service.FeatureFlags;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.EnableWhen;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import java.util.Collections;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Questionnaire;
import org.junit.jupiter.api.Test;

class GroupProcessorTest {

  private final GroupProcessor groupProcessor =
      new GroupProcessor(new EnableWhenProcessor(), FeatureFlags.builder().build());

  @Test
  void createFieldGroup_withNullItem_shouldHandleCorrectly() {
    Questionnaire.QuestionnaireItemComponent item = null;
    FieldGroup parent = FieldGroup.builder().build();

    FieldGroup[] result = groupProcessor.createFieldGroup(item, parent, "diseaseCode");

    assertThat(result).isNotNull().isEmpty();
  }

  @Test
  void createFieldGroup_withNullParent_shouldHandleCorrectly() {
    Questionnaire.QuestionnaireItemComponent item = new Questionnaire.QuestionnaireItemComponent();
    FieldGroup parent = null;

    FieldGroup[] result = groupProcessor.createFieldGroup(item, parent, "diseaseCode");

    assertThat(result).isNotNull().hasSize(1);
    assertThat(result[0].getParent()).isNull();
  }

  @Test
  void createFieldGroup_withValidItemAndParent_shouldReturnCorrectFieldGroup() {
    Questionnaire.QuestionnaireItemComponent item = new Questionnaire.QuestionnaireItemComponent();
    item.setLinkId("testLinkId");
    FieldGroup parent = FieldGroup.builder().key("testParentKey").build();

    FieldGroup[] result = groupProcessor.createFieldGroup(item, parent, "diseaseCode");

    assertThat(result).isNotNull().hasSize(1);
    assertThat(result[0].getKey()).isEqualTo("testLinkId");
    assertThat(result[0].getParent()).isEqualTo(parent);
  }

  @Test
  void createFieldGroup_withLabel_shouldReturnFieldGroupWithLabel() {
    Questionnaire.QuestionnaireItemComponent item = new Questionnaire.QuestionnaireItemComponent();
    item.setText("testLabel");

    FieldGroup[] result = groupProcessor.createFieldGroup(item, null, "diseaseCode");

    assertThat(result).isNotNull().hasSize(1);
    assertThat(result[0].getProps().getLabel()).isEqualTo("testLabel");
  }

  @Test
  void createFieldGroup_withItemHavingExpressions_shouldReturnCorrectFieldGroup() {
    // Arrange
    String parentLinkId = "testParentKey";
    Questionnaire.QuestionnaireItemComponent item = new Questionnaire.QuestionnaireItemComponent();
    String linkId = "testLinkId";
    item.setLinkId(linkId);
    Questionnaire.QuestionnaireItemEnableWhenComponent o =
        new Questionnaire.QuestionnaireItemEnableWhenComponent();
    o.setAnswer(new Coding().setCode("hideValue"));
    o.setQuestion(parentLinkId);
    o.setOperator(Questionnaire.QuestionnaireItemOperator.GREATER_THAN);
    item.setEnableWhen(Collections.singletonList(o));
    FieldGroup parent = FieldGroup.builder().key(parentLinkId).build();

    // Act
    FieldGroup[] result = groupProcessor.createFieldGroup(item, parent, "diseaseCode");

    // Assert
    assertThat(result).isNotNull().hasSize(1);
    FieldGroup fieldGroup = result[0];
    assertThat(fieldGroup.getKey()).isEqualTo(linkId);
    assertThat(fieldGroup.getParent()).isEqualTo(parent);
    EnableWhen[] enableWhens = fieldGroup.getProps().getEnableWhen();
    assertThat(enableWhens).isNotNull().hasSize(1);
    EnableWhen enableWhen = enableWhens[0];
    assertThat(enableWhen.getPath()).as("enable when path").isEqualTo("parent");
    assertThat(enableWhen.getOp()).as("enable when operation").isEqualTo(">");
    assertThat(enableWhen.getValue()).as("enable when value").isEqualTo("hideValue");
  }
}
