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

import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.EnableWhen;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldArray;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Props;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Questionnaire;
import org.junit.jupiter.api.Test;

class EnableWhenProcessorTest {

  private final EnableWhenProcessor enableWhenProcessor = new EnableWhenProcessor();

  @Test
  void createEnableWhens_shouldHandleTwoStepParent() {
    Questionnaire.QuestionnaireItemComponent item = new Questionnaire.QuestionnaireItemComponent();
    String parentLinkId = "question";
    var operator = Questionnaire.QuestionnaireItemOperator.EQUAL;
    String code = "code";
    var enableWhenComponent = createEnableWhen(parentLinkId, operator, code);

    item.setLinkId("linkId");
    item.addEnableWhen(enableWhenComponent);

    FieldGroup parent1 = FieldGroup.builder().key(parentLinkId).build();
    FieldGroup parent2 = FieldGroup.builder().key("intermediate-parent").parent(parent1).build();
    FieldGroup fieldGroup = FieldGroup.builder().parent(parent2).build();
    this.enableWhenProcessor.createEnableWhens(item, fieldGroup);

    Props props = fieldGroup.getProps();
    assertThat(props).isNotNull();
    EnableWhen[] enableWhens = props.getEnableWhen();
    assertThat(enableWhens).isNotNull().hasSize(1);
    EnableWhen enableWhen = enableWhens[0];
    assertThat(enableWhen).isNotNull();
    assertThat(enableWhen.getPath()).isEqualTo("parent.parent");
    assertThat(enableWhen.getOp()).isEqualTo(operator.toCode());
    assertThat(enableWhen.getValue()).isEqualTo(code);
  }

  @Test
  void createEnableWhens_shouldHandleItemWithMultipleEnableWhens() {

    // create FHIR item
    String parentLinkId = "question";
    Questionnaire.QuestionnaireItemComponent item = new Questionnaire.QuestionnaireItemComponent();
    var operator = Questionnaire.QuestionnaireItemOperator.EQUAL;
    String code1 = "code1";
    var enableWhenComponent1 = createEnableWhen(parentLinkId, operator, code1);
    String code2 = "code2";
    var enableWhenComponent2 = createEnableWhen(parentLinkId, operator, code2);
    item.setLinkId("linkId");
    item.addEnableWhen(enableWhenComponent1);
    item.addEnableWhen(enableWhenComponent2);

    // create Formly field groups
    FieldGroup fieldGroup =
        FieldGroup.builder().parent(FieldGroup.builder().key(parentLinkId).build()).build();
    this.enableWhenProcessor.createEnableWhens(item, fieldGroup);

    Props props = fieldGroup.getProps();
    assertThat(props).isNotNull();
    EnableWhen[] enableWhens = props.getEnableWhen();
    assertThat(enableWhens).isNotNull().hasSize(2);

    EnableWhen enableWhen1 = enableWhens[0];
    assertThat(enableWhen1).isNotNull();
    String path = "parent";
    assertThat(enableWhen1.getPath()).isEqualTo(path);
    assertThat(enableWhen1.getOp()).isEqualTo(operator.toCode());
    assertThat(enableWhen1.getValue()).isEqualTo(code1);

    EnableWhen enableWhen2 = enableWhens[1];
    assertThat(enableWhen2).isNotNull();
    assertThat(enableWhen2.getPath()).isEqualTo(path);
    assertThat(enableWhen2.getOp()).isEqualTo(operator.toCode());
    assertThat(enableWhen2.getValue()).isEqualTo(code2);
  }

  private Questionnaire.QuestionnaireItemEnableWhenComponent createEnableWhen(
      String parentLinkId, Questionnaire.QuestionnaireItemOperator operator, String code) {
    final var enableWhenComponent = new Questionnaire.QuestionnaireItemEnableWhenComponent();
    enableWhenComponent.setQuestion(parentLinkId);
    enableWhenComponent.setOperator(operator);
    enableWhenComponent.setAnswer(new Coding().setCode(code));
    return enableWhenComponent;
  }

  @Test
  void createEnableWhens_shouldIgnoreItemWithoutEnableWhen() {
    Questionnaire.QuestionnaireItemComponent item = new Questionnaire.QuestionnaireItemComponent();
    item.setLinkId("linkId");
    FieldGroup fieldGroup = FieldGroup.builder().props(Props.builder().build()).build();
    this.enableWhenProcessor.createEnableWhens(item, fieldGroup);
    EnableWhen[] enableWhens = fieldGroup.getProps().getEnableWhen();
    assertThat(enableWhens).isNull();
  }

  @Test
  void createEnableWhens_shouldResolveDirectSibling() {

    // create FHIR item
    Questionnaire.QuestionnaireItemComponent sibling2Item =
        new Questionnaire.QuestionnaireItemComponent();
    var operator = Questionnaire.QuestionnaireItemOperator.EQUAL;
    String code = "code";
    String sibling1LinkId = "sibling1";
    var enableWhenComponent = createEnableWhen(sibling1LinkId, operator, code);
    sibling2Item.setLinkId("linkId");
    sibling2Item.addEnableWhen(enableWhenComponent);

    // create Formly field groups
    FieldGroup parent = FieldGroup.builder().key("parent").build();
    FieldGroup sibling1 = FieldGroup.builder().key(sibling1LinkId).parent(parent).build();
    FieldGroup sibling2 = FieldGroup.builder().key("sibling2").parent(parent).build();

    this.enableWhenProcessor.createEnableWhens(sibling2Item, sibling2);

    Props props = sibling2.getProps();
    assertThat(props).as("field group properties").isNotNull();
    EnableWhen[] enableWhens = props.getEnableWhen();
    assertThat(enableWhens).isNotNull().hasSize(1);
    EnableWhen enableWhen = enableWhens[0];
    assertThat(enableWhen).isNotNull();
    assertThat(enableWhen.getPath())
        .as("same parent's other child that is first field group in list of children")
        .isEqualTo("parent.fieldGroup.0");
    assertThat(enableWhen.getOp()).isEqualTo(operator.toCode());
    assertThat(enableWhen.getValue()).isEqualTo(code);
  }

  @Test
  void createEnableWhens_shouldResolveParentSibling() {

    // create FHIR item
    Questionnaire.QuestionnaireItemComponent childItem =
        new Questionnaire.QuestionnaireItemComponent();
    var operator = Questionnaire.QuestionnaireItemOperator.EQUAL;
    String code = "code";
    var enableWhenComponent = createEnableWhen("uncle", operator, code);
    childItem.setLinkId("linkId");
    childItem.addEnableWhen(enableWhenComponent);

    // create Formly field groups
    FieldGroup granny = FieldGroup.builder().key("granny").build();
    FieldGroup parent = FieldGroup.builder().key("parent").parent(granny).build();
    FieldGroup uncle = FieldGroup.builder().key("uncle").parent(granny).build();
    FieldGroup child = FieldGroup.builder().key("child").parent(parent).build();

    this.enableWhenProcessor.createEnableWhens(childItem, child);

    Props props = child.getProps();
    assertThat(props).as("field group properties").isNotNull();
    EnableWhen[] enableWhens = props.getEnableWhen();
    assertThat(enableWhens).isNotNull().hasSize(1);
    EnableWhen enableWhen = enableWhens[0];
    assertThat(enableWhen).isNotNull();
    assertThat(enableWhen.getPath())
        .as("parent's sibling's child that is second field group in list of children")
        .isEqualTo("parent.parent.fieldGroup.1");
    assertThat(enableWhen.getOp()).isEqualTo(operator.toCode());
    assertThat(enableWhen.getValue()).isEqualTo(code);
  }

  @Test
  void createEnableWhens_shouldLogAndSkipOnMissingParent() {

    // create FHIR item
    Questionnaire.QuestionnaireItemComponent item = new Questionnaire.QuestionnaireItemComponent();
    var operator = Questionnaire.QuestionnaireItemOperator.EQUAL;
    String code = "code";
    var enableWhenComponent = createEnableWhen("question", operator, code);
    item.setLinkId("linkId");
    item.addEnableWhen(enableWhenComponent);

    // create Formly field groups
    FieldGroup parent1 = FieldGroup.builder().key("questionFail").build();
    FieldGroup parent2 = FieldGroup.builder().key("intermediate-parent").parent(parent1).build();
    FieldGroup fieldGroup = FieldGroup.builder().parent(parent2).build();
    this.enableWhenProcessor.createEnableWhens(item, fieldGroup);

    assertThat(fieldGroup.getProps()).as("skipped enable-when creation").isNull();
  }

  @Test
  void incrementIntersectingEnableWhens_shouldOnlyIncrementIntersectingPaths() {

    // outer parent
    String outerParentKey = "outerParent";
    FieldGroup outerParent = FieldGroup.builder().key(outerParentKey).build();

    // inner parent
    String innerParentKey = "innerParent";
    FieldGroup innerParent = FieldGroup.builder().key(innerParentKey).parent(outerParent).build();

    // intersecting child
    EnableWhen intersectingEnableWhen =
        EnableWhen.builder()
            .question(outerParentKey)
            .path("parent.parent")
            .op("=")
            .value("good")
            .build();
    FieldGroup innerIntersectingChild =
        FieldGroup.builder()
            .key("intersectingChild")
            .parent(innerParent)
            .props(Props.builder().enableWhen(new EnableWhen[] {intersectingEnableWhen}).build())
            .build();

    // non-intersecting child
    EnableWhen nonIntersectingEnableWhen =
        EnableWhen.builder().question(innerParentKey).path("parent").op("=").value("good").build();
    FieldGroup.builder()
        .key("nonIntersectingChild")
        .parent(innerParent)
        .props(Props.builder().enableWhen(new EnableWhen[] {nonIntersectingEnableWhen}).build())
        .build();

    // field array
    FieldArray fieldArray = FieldArray.builder().fieldGroup(new FieldGroup[] {innerParent}).build();

    // incrementation
    this.enableWhenProcessor.incrementIntersectingEnableWhens(fieldArray);

    assertThat(intersectingEnableWhen.getPath())
        .as("intersecting incremented enable-when path")
        .isEqualTo("parent.parent.parent");
    assertThat(nonIntersectingEnableWhen.getPath())
        .as("non-intersecting enable-when path")
        .isEqualTo("parent");
  }
}
