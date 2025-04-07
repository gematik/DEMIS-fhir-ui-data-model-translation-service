package de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly;

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

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhir_ui_data_model_translation_service.FeatureFlags;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.DiseaseNotificationCategoriesSrv;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.Questionnaires;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.*;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.*;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.resources.DiseaseProcessor;
import de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Questionnaire;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class DiseaseDataPreparationSrv {

  private static final String COVID = "cvdd";
  private static final String LINK_ID_HOSPITALIZATION_REASON = "reason";

  private final DiseaseNotificationCategoriesSrv categoriesSrv;
  private final Questionnaires questionnaires;
  private final FhirContext fhirContext;
  private final ChoiceProcessor choiceProcessor;
  private final DateProcessor dateProcessor;

  private final TextProcessor textProcessor;
  private final GroupProcessor groupProcessor;
  private final ReferenceProcessor referenceProcessor;
  private final DiseaseProcessor diseaseProcessor;
  private final EnableWhenProcessor enableWhenProcessor;

  private final FeatureFlags featureFlags;

  @Getter private final Map<String, FormlyFieldConfigs[]> questionnaireMap = new HashMap<>();

  @PostConstruct
  void init() {
    for (Map.Entry<String, File> entry : questionnaires.getDiseaseQuestionnaires().entrySet()) {
      log.info("Loading questionnaire: {}", entry.getKey());
      try {
        parseQuestionnaireAndExtractData(entry);
      } catch (IOException e) {
        log.error("Error while loading questionnaire: {}", entry.getKey(), e);
      }
      log.info("Loaded questionnaire: {}", entry.getKey());
    }
  }

  public Map<String, FormlyFieldConfigs[]> getQuestionnaire(String code) {
    Map<String, FormlyFieldConfigs[]> returnMap = new HashMap<>();
    // add condition
    String title = this.categoriesSrv.getCategory(code).getDisplay();
    FormlyFieldConfigs conditionHeader =
        FormlyFieldConfigs.builder().template(title).className("QUESTIONNAIRE-TITLE").build();
    FormlyFieldConfigs conditionFormlyFieldConfig =
        FormlyFieldConfigs.builder()
            .fieldGroup(diseaseProcessor.createFieldGroup(code))
            .fieldGroupClassName("QUESTIONS")
            .build();
    returnMap.put(
        "conditionConfigs", new FormlyFieldConfigs[] {conditionHeader, conditionFormlyFieldConfig});

    // add common and specific questionnaires
    returnMap.put("commonConfig", questionnaireMap.get("common"));
    returnMap.put("questionnaireConfigs", questionnaireMap.get(code.toLowerCase()));

    return returnMap;
  }

  private void parseQuestionnaireAndExtractData(Map.Entry<String, File> entry) throws IOException {
    // parse file
    String fileString = Utils.getFileString(entry.getValue());
    Questionnaire questionnaire =
        fhirContext.newJsonParser().parseResource(Questionnaire.class, fileString);

    List<FormlyFieldConfigs> completeData = new ArrayList<>();

    FormlyFieldConfigs mainTitlePart =
        FormlyFieldConfigs.builder()
            .template(questionnaire.getTitle())
            .className("QUESTIONNAIRE-TITLE")
            .build();

    // create main template entry
    completeData.add(mainTitlePart);

    // loop through all main questionnaireItems
    FieldGroup root = FieldGroup.builder().key("root").build();
    final AtomicInteger repeatSectionId = new AtomicInteger(0);
    final String diseaseCode = entry.getKey();
    for (Questionnaire.QuestionnaireItemComponent item : questionnaire.getItem()) {
      if (!ignoreItem(item, diseaseCode)) {
        extractData(item, root, diseaseCode, repeatSectionId);
      }
    }
    FormlyFieldConfigs allQuestions =
        FormlyFieldConfigs.builder()
            .fieldGroup(root.getFieldGroups().toArray(new FieldGroup[0]))
            .fieldGroupClassName("QUESTIONS")
            .build();

    completeData.add(allQuestions);
    FormlyFieldConfigs[] mainFieldGroups = completeData.toArray(new FormlyFieldConfigs[0]);
    questionnaireMap.put(diseaseCode.toLowerCase(), mainFieldGroups);
  }

  private void extractData(
      Questionnaire.QuestionnaireItemComponent item,
      FieldGroup parent,
      String diseaseCode,
      AtomicInteger repeatSectionId) {
    if (isRepeatSection(item)) {
      createRepeatSection(item, parent, diseaseCode, repeatSectionId);
    } else {
      createGroup(item, parent, diseaseCode, repeatSectionId);
    }
  }

  private void createGroup(
      Questionnaire.QuestionnaireItemComponent item,
      FieldGroup parent,
      String diseaseCode,
      AtomicInteger repeatSectionId) {
    final FieldGroup fieldGroupBracket;
    if (item.getType() == Questionnaire.QuestionnaireItemType.GROUP) {
      fieldGroupBracket = this.groupProcessor.createFieldGroup(item, parent, diseaseCode)[0];
    } else {
      fieldGroupBracket =
          FieldGroup.builder().key(item.getLinkId() + ".answer").parent(parent).build();
      extractDataAndType(item, fieldGroupBracket, diseaseCode);
    }
    for (Questionnaire.QuestionnaireItemComponent subItem : item.getItem()) {
      extractData(subItem, fieldGroupBracket, diseaseCode, repeatSectionId);
    }
  }

  private boolean isRepeatSection(Questionnaire.QuestionnaireItemComponent item) {
    if (item.getRepeats()) {
      return switch (item.getType()) {
        case Questionnaire.QuestionnaireItemType.GROUP,
                Questionnaire.QuestionnaireItemType.REFERENCE ->
            true;
        case Questionnaire.QuestionnaireItemType.CHOICE -> {
          final List<Questionnaire.QuestionnaireItemComponent> subitems = item.getItem();
          yield (subitems != null) && !subitems.isEmpty();
        }
        default -> {
          log.error(
              "Repeatable item of unsupported type in questionnaire profile! LinkId: {} Type: {}",
              item.getLinkId(),
              item.getType());
          yield true;
        }
      };
    }
    return false;
  }

  /**
   * Create field group for repeatable questionnaire response item.
   *
   * <h1>FHIR-to-Formly backgrounds</h1>
   *
   * <p><b>This is not trivial!</b> RKI FHIR profiles tag the items with "REPEATS". This means, the
   * item itself may occur multiple times. But Formly in fronted only knows repeat sections. A
   * repeat sections contains one or multiple items that may be repeated.
   *
   * <p>This is why we can not just create a field group with the link ID of the given item and mark
   * it as repeat section. We have to add a layer in between that serves as bracket.
   *
   * <p>And also this comes with a drawback. Formly currently only renders a repeat section field
   * group if it contains a key parameter. And this results in an additional questionnaire response
   * item being sent to the gateway.
   *
   * <h2>Alternatives</h2>
   *
   * I really tried to avoid the additional "repeat-section" questionnaire response item being sent
   * to the gateway. But no other way worked. I tried to use the parent field group as repeat
   * section and added the actually repeated item field group as field array to the parent. But
   * then, the enable-when condition creation algorithm failed to resolve the parents. This was when
   * I gave up and added repeat-section-code to the gateway questionnaire response item processing.
   *
   * @param item questionnaire response item
   * @param parent parent field group
   * @param diseaseCode disease category code
   */
  private void createRepeatSection(
      Questionnaire.QuestionnaireItemComponent item,
      FieldGroup parent,
      String diseaseCode,
      AtomicInteger repeatSectionId) {
    FieldGroup repeatSection =
        FieldGroup.builder()
            .key(FieldGroup.TYPE_REPEAT + "-" + repeatSectionId.incrementAndGet())
            .type(FieldGroup.TYPE_REPEAT)
            .className("REPEATER LinkId_" + item.getLinkId())
            .fieldGroupClassName("REPEATED LinkId_" + item.getLinkId())
            .props(Props.builder().itemName(item.getText()).required(item.getRequired()).build())
            .parent(parent)
            .build();
    this.enableWhenProcessor.createEnableWhens(item, repeatSection);
    createRepeatSectionFieldArray(item, diseaseCode, repeatSection, repeatSectionId);
  }

  private void createRepeatSectionFieldArray(
      Questionnaire.QuestionnaireItemComponent item,
      String diseaseCode,
      FieldGroup repeatSection,
      AtomicInteger repeatSectionId) {
    String linkId = item.getLinkId();
    String key =
        item.getType().equals(Questionnaire.QuestionnaireItemType.GROUP)
            ? linkId
            : linkId + ".answer";
    FieldGroup repeatedGroup = FieldGroup.builder().key(key).parent(repeatSection).build();
    if (item.getType() != Questionnaire.QuestionnaireItemType.GROUP) {
      extractDataAndType(item, repeatedGroup, diseaseCode);
    }
    for (Questionnaire.QuestionnaireItemComponent subItem : item.getItem()) {
      extractData(subItem, repeatedGroup, diseaseCode, repeatSectionId);
    }
    FieldArray fieldArray =
        FieldArray.builder().fieldGroup(new FieldGroup[] {repeatedGroup}).build();
    repeatSection.setFieldArray(fieldArray);
    this.enableWhenProcessor.incrementIntersectingEnableWhens(fieldArray);
  }

  // creates single field groups for single questions
  private void extractDataAndType(
      Questionnaire.QuestionnaireItemComponent item, FieldGroup parent, String diseaseCode) {
    switch (item.getType()) {
      case REFERENCE:
        referenceProcessor.createFieldGroup(item, parent, diseaseCode);
        break;
      case CHOICE:
        choiceProcessor.createFieldGroup(item, parent, diseaseCode);
        break;
      case DATE:
        dateProcessor.createFieldGroup(item, parent, diseaseCode);
        break;
      case TEXT:
        textProcessor.createFieldGroup(item, parent, diseaseCode);
        break;
      case GROUP:
        throw new IllegalStateException("group processing is not allowed here");
      default:
        log.error("Unsupported questionnaire item type: {}", item.getType());
        break;
    }
  }

  private boolean ignoreItem(Questionnaire.QuestionnaireItemComponent item, String diseaseCode) {
    if (featureFlags.isMoveHospitalizationReason()
        && StringUtils.equalsIgnoreCase(diseaseCode, COVID)
        && StringUtils.equalsIgnoreCase(item.getLinkId(), LINK_ID_HOSPITALIZATION_REASON)) {
      log.info(
          "Ignoring questionnaire item hospitalization reason for COVID as configured by feature flag");
      return true;
    }
    return false;
  }
}
