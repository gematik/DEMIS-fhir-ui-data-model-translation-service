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

import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.EnableWhen;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Props;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Wrapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class QuestionnaireLayoutItemIndentationTest {

  @Test
  void wrapsTypedFollowUpInPanelAndPrefixesEnableWhenPath() {
    final FieldGroup root = FieldGroup.builder().key("root").build();
    final FieldGroup followUp =
        FieldGroup.builder()
            .parent(root)
            .key("follow-up")
            .type(FieldGroup.TYPE_INPUT)
            .props(propsWithEnableWhen("sibling.answer"))
            .build();

    indent(root);

    assertThat(root.getFieldGroups()).hasSize(1);
    final FieldGroup wrapper = root.getFieldGroups().getFirst();
    assertThat(wrapper.getWrappers()).containsExactly(Wrapper.PANEL);
    assertThat(wrapper.getFieldGroups()).containsExactly(followUp);
    assertThat(followUp.getParent()).isSameAs(wrapper);
    assertThat(followUp.getProps().getEnableWhen())
        .singleElement()
        .extracting(EnableWhen::getPath)
        .isEqualTo("parent.sibling.answer");
  }

  @Test
  void addsPanelWrapperToFollowUpContainerWithoutReparenting() {
    final FieldGroup root = FieldGroup.builder().key("root").build();
    final FieldGroup containerFollowUp =
        FieldGroup.builder()
            .parent(root)
            .key("container")
            .props(propsWithEnableWhen("trigger"))
            .build();

    indent(root);

    assertThat(root.getFieldGroups()).containsExactly(containerFollowUp);
    assertThat(containerFollowUp.getWrappers()).containsExactly(Wrapper.PANEL);
    assertThat(containerFollowUp.getParent()).isSameAs(root);
  }

  @Test
  void indentsSubitemStructureViaLeadFollowUpAndRemainingSubitems() {
    final FieldGroup root = FieldGroup.builder().key("root").build();
    final FieldGroup section = FieldGroup.builder().parent(root).key("section").build();
    final FieldGroup leadFollowUp =
        FieldGroup.builder()
            .parent(section)
            .key("lead")
            .type(FieldGroup.TYPE_INPUT)
            .props(propsWithEnableWhen("trigger"))
            .build();
    final FieldGroup nestedSubitem = FieldGroup.builder().parent(section).key("nested").build();

    indent(root);

    assertThat(section.getFieldGroups()).hasSize(2);

    final FieldGroup leadWrapper = section.getFieldGroups().getFirst();
    assertThat(leadWrapper.getWrappers()).containsExactly(Wrapper.PANEL);
    assertThat(leadWrapper.getFieldGroups()).containsExactly(leadFollowUp);
    assertThat(leadFollowUp.getProps().getEnableWhen())
        .singleElement()
        .extracting(EnableWhen::getPath)
        .isEqualTo("parent.trigger");

    assertThat(section.getFieldGroups().get(1)).isSameAs(nestedSubitem);
    assertThat(nestedSubitem.getWrappers()).containsExactly(Wrapper.PANEL);
    assertThat(nestedSubitem.getParent()).isSameAs(section);
  }

  @Test
  void skipsRepeatSections() {
    final FieldGroup root = FieldGroup.builder().key("root").build();
    final FieldGroup repeatSection =
        FieldGroup.builder()
            .parent(root)
            .key("repeat")
            .type(FieldGroup.TYPE_REPEAT)
            .props(propsWithEnableWhen("trigger"))
            .build();

    indent(root);

    assertThat(root.getFieldGroups()).containsExactly(repeatSection);
    assertThat(repeatSection.getWrappers()).isNull();
    assertThat(repeatSection.getProps().getEnableWhen())
        .singleElement()
        .extracting(EnableWhen::getPath)
        .isEqualTo("trigger");
  }

  @Test
  void doesNotIndentAnswerItemDirectlyUnderItemGroup() {
    final FieldGroup root = FieldGroup.builder().key("root").build();
    final FieldGroup itemGroup =
        FieldGroup.builder()
            .parent(root)
            .key("group")
            .fieldGroupClassName(GroupProcessor.CLASSNAME_PREFIX + "my-group")
            .build();
    final FieldGroup answerFollowUp =
        FieldGroup.builder()
            .parent(itemGroup)
            .key("answer")
            .type(FieldGroup.TYPE_INPUT)
            .props(propsWithEnableWhen("trigger"))
            .build();

    indent(root);

    assertThat(itemGroup.getFieldGroups()).containsExactly(answerFollowUp);
    assertThat(answerFollowUp.getWrappers())
        .as("an answer item directly below an item group must not be indented")
        .isNull();
    assertThat(answerFollowUp.getProps().getEnableWhen())
        .singleElement()
        .extracting(EnableWhen::getPath)
        .as("the enable-when path must remain untouched when no indentation is applied")
        .isEqualTo("trigger");
  }

  @Test
  void doesNotIndentAnswerItemDirectlyUnderItemGroupRegardlessOfKeyCasing() {
    final FieldGroup root = FieldGroup.builder().key("root").build();
    final FieldGroup itemGroup =
        FieldGroup.builder()
            .parent(root)
            .key("group")
            .fieldGroupClassName(GroupProcessor.CLASSNAME_PREFIX + "my-group")
            .build();
    final FieldGroup answerFollowUp =
        FieldGroup.builder()
            .parent(itemGroup)
            .key("valueAnswerCoding")
            .type(FieldGroup.TYPE_INPUT)
            .props(propsWithEnableWhen("trigger"))
            .build();

    indent(root);

    assertThat(itemGroup.getFieldGroups()).containsExactly(answerFollowUp);
    assertThat(answerFollowUp.getWrappers())
        .as("the answer item detection must be case-insensitive and substring based")
        .isNull();
  }

  @Test
  void doesNotIndentFollowUpWhoseAnswerParentIsDirectlyUnderItemGroup() {
    final FieldGroup root = FieldGroup.builder().key("root").build();
    final FieldGroup itemGroup =
        FieldGroup.builder()
            .parent(root)
            .key("group")
            .fieldGroupClassName(GroupProcessor.CLASSNAME_PREFIX + "my-group")
            .build();
    final FieldGroup answerContainer =
        FieldGroup.builder().parent(itemGroup).key("answerContainer").build();
    final FieldGroup followUp =
        FieldGroup.builder()
            .parent(answerContainer)
            .key("follow-up")
            .type(FieldGroup.TYPE_INPUT)
            .props(propsWithEnableWhen("trigger"))
            .build();

    indent(root);

    assertThat(answerContainer.getFieldGroups()).containsExactly(followUp);
    assertThat(followUp.getWrappers())
        .as(
            "a follow-up whose answer parent sits directly below an item group must not be indented")
        .isNull();
    assertThat(followUp.getProps().getEnableWhen())
        .singleElement()
        .extracting(EnableWhen::getPath)
        .isEqualTo("trigger");
  }

  @Test
  void indentsAnswerItemWhenParentIsNotAnItemGroup() {
    final FieldGroup root = FieldGroup.builder().key("root").build();
    final FieldGroup section = FieldGroup.builder().parent(root).key("section").build();
    final FieldGroup answerFollowUp =
        FieldGroup.builder()
            .parent(section)
            .key("answer")
            .type(FieldGroup.TYPE_INPUT)
            .props(propsWithEnableWhen("trigger"))
            .build();

    indent(root);

    assertThat(section.getFieldGroups()).hasSize(1);
    final FieldGroup wrapper = section.getFieldGroups().getFirst();
    assertThat(wrapper.getWrappers())
        .as("an answer item below a plain container must still be indented")
        .containsExactly(Wrapper.PANEL);
    assertThat(wrapper.getFieldGroups()).containsExactly(answerFollowUp);
    assertThat(answerFollowUp.getProps().getEnableWhen())
        .singleElement()
        .extracting(EnableWhen::getPath)
        .isEqualTo("parent.trigger");
  }

  @Test
  void indentsAnswerItemNestedBelowTheFirstLevelOfAnItemGroup() {
    final FieldGroup root = FieldGroup.builder().key("root").build();
    final FieldGroup itemGroup =
        FieldGroup.builder()
            .parent(root)
            .key("group")
            .fieldGroupClassName(GroupProcessor.CLASSNAME_PREFIX + "my-group")
            .build();
    final FieldGroup container = FieldGroup.builder().parent(itemGroup).key("container").build();
    final FieldGroup answerFollowUp =
        FieldGroup.builder()
            .parent(container)
            .key("answer")
            .type(FieldGroup.TYPE_INPUT)
            .props(propsWithEnableWhen("trigger"))
            .build();

    indent(root);

    assertThat(container.getFieldGroups()).hasSize(1);
    final FieldGroup wrapper = container.getFieldGroups().getFirst();
    assertThat(wrapper.getWrappers())
        .as("indentation is only suppressed on the first level; deeper answer items are indented")
        .containsExactly(Wrapper.PANEL);
    assertThat(wrapper.getFieldGroups()).containsExactly(answerFollowUp);
    assertThat(answerFollowUp.getProps().getEnableWhen())
        .singleElement()
        .extracting(EnableWhen::getPath)
        .isEqualTo("parent.trigger");
  }

  @Test
  void appliesIndentationOnlyUpToTheDefaultMaximumLevel() {
    final int maxLevel = QuestionnaireLayoutItemIndentation.INDENTATION_LEVEL_MAX_DEFAULT;
    final FieldGroup root = FieldGroup.builder().key("root").build();
    final List<FieldGroup> chain = buildFollowUpChain(root, maxLevel + 1);

    indent(root, maxLevel);

    for (int level = 1; level <= maxLevel; level++) {
      assertThat(chain.get(level - 1).getWrappers())
          .as("indentation level %d must be applied", level)
          .containsExactly(Wrapper.PANEL);
    }
    assertThat(chain.get(maxLevel).getWrappers())
        .as("indentation level %d exceeds the maximum and must not be applied", maxLevel + 1)
        .isNull();
  }

  @Test
  void stopsIndentingBeyondTheConfiguredMaximumLevel() {
    final int maxLevel = 2;
    final FieldGroup root = FieldGroup.builder().key("root").build();
    final List<FieldGroup> chain = buildFollowUpChain(root, maxLevel + 1);

    indent(root, maxLevel);

    assertThat(chain.get(0).getWrappers()).containsExactly(Wrapper.PANEL);
    assertThat(chain.get(1).getWrappers()).containsExactly(Wrapper.PANEL);
    assertThat(chain.get(2).getWrappers())
        .as("indentation beyond the configured maximum level must not be applied")
        .isNull();
  }

  private void indent(FieldGroup root) {
    indent(root, QuestionnaireLayoutItemIndentation.INDENTATION_LEVEL_MAX_DEFAULT);
  }

  private void indent(FieldGroup root, int maxLevel) {
    new QuestionnaireLayoutItemIndentation(root, maxLevel).applyItemIndentation();
  }

  private List<FieldGroup> buildFollowUpChain(FieldGroup root, int depth) {
    final List<FieldGroup> chain = new ArrayList<>();
    FieldGroup current = root;
    for (int level = 1; level <= depth; level++) {
      current =
          FieldGroup.builder()
              .parent(current)
              .key("level-" + level)
              .props(propsWithEnableWhen("trigger-" + level))
              .build();
      chain.add(current);
    }
    return chain;
  }

  private Props propsWithEnableWhen(String path) {
    return Props.builder()
        .enableWhen(new EnableWhen[] {EnableWhen.builder().path(path).build()})
        .build();
  }
}
