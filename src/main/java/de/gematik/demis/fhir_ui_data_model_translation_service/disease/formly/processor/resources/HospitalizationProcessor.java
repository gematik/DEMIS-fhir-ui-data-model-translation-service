package de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.resources;

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

import static de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.ClipboardProcessor.createClipboard;

import de.gematik.demis.fhir_ui_data_model_translation_service.FeatureFlags;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.DiseaseClipboardProps;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Props;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.ChoiceProcessor;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.translation.DataLoaderSrv;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Questionnaire;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HospitalizationProcessor {

  static final String ORGANIZATION_CHECKBOX_KEY = "copyNotifiedPersonCurrentAddress";
  static final String ORGANIZATION_CHECKBOX_LABEL =
      "Einrichtung aus Formularbereich \"Betroffene Person\" übernehmen (\"Derzeitiger Aufenthaltsort\")";
  static final String CONTACT_CHECKBOX_KEY = "copyNotifierContact";
  static final String CONTACT_CHECKBOX_LABEL =
      "Ansprechperson aus Formularbereich \"Meldende Person\" übernehmen";
  private static final Checkbox COPY_ORGANIZATION =
      new Checkbox(ORGANIZATION_CHECKBOX_KEY, ORGANIZATION_CHECKBOX_LABEL);
  private static final Checkbox COPY_CONTACT =
      new Checkbox(CONTACT_CHECKBOX_KEY, CONTACT_CHECKBOX_LABEL);

  private static final String REF_ELEMENT = "REF_ELEMENT";
  private static final String LINK_ID_PERIOD_START = "start";
  private static final String LINK_ID_PERIOD_END = "end";
  private static final String LINK_ID_REASON = "reason";
  private static final String REASON_VALUE_SET =
      "https://demis.rki.de/fhir/ValueSet/answerSetHospitalizationReason";
  private static final String SERVICE_TYPE_VALUE_SET =
      "https://demis.rki.de/fhir/ValueSet/hospitalizationServiceType";
  private static final String LINK_ID_SERVICE_TYPE = "serviceType";
  private static final String CLIPBOARD_MARKER_ENCOUNTER = "enc.";

  private final DataLoaderSrv dataLoaderSrv;
  private final OrganizationProcessor organizationProcessor;
  private final DiseaseClipboardProps diseaseClipboardProps;
  private final FeatureFlags featureFlags;

  public FieldGroup createFieldGroup(
      Questionnaire.QuestionnaireItemComponent item, FieldGroup parent) {
    FieldGroup hospitalization =
        FieldGroup.builder()
            .key("Hospitalization")
            .parent(parent)
            .fieldGroupClassName("ITEM_REFERENCE")
            .className("LinkId_" + item.getLinkId())
            .build();
    createServiceType(item, hospitalization);
    createPeriod(item, hospitalization);
    createReason(item, hospitalization);
    createServiceProvider(item, hospitalization);
    return hospitalization;
  }

  private void createServiceType(Questionnaire.QuestionnaireItemComponent item, FieldGroup parent) {
    FieldGroup serviceType =
        FieldGroup.builder()
            .key(LINK_ID_SERVICE_TYPE)
            .fieldGroupClassName(REF_ELEMENT)
            .parent(parent)
            .build();
    CodeDisplay[] options =
        this.dataLoaderSrv.getValueSetData(SERVICE_TYPE_VALUE_SET).toArray(CodeDisplay[]::new);
    FieldGroup input =
        FieldGroup.builder()
            .type(FieldGroup.TYPE_CODING)
            .key("answer." + FieldGroup.KEY_VALUE_CODING)
            .className("hospitalizationServiceType")
            .parent(serviceType)
            .props(
                Props.builder()
                    .label("Station")
                    .clearable(true)
                    .required(true)
                    .options(options)
                    .build())
            .build();
    ChoiceProcessor.enableValidation(input);
    clipboardKey(item, LINK_ID_SERVICE_TYPE).ifPresent(k -> createClipboard(k, false, input));
  }

  private void createPeriod(Questionnaire.QuestionnaireItemComponent item, FieldGroup parent) {
    FieldGroup period = FieldGroup.builder().key("period").parent(parent).build();
    createPeriodStart(item, period);
    createPeriodEnd(item, period);
  }

  private void createPeriodStart(Questionnaire.QuestionnaireItemComponent item, FieldGroup parent) {
    FieldGroup start =
        FieldGroup.builder()
            .key(LINK_ID_PERIOD_START)
            .fieldGroupClassName(REF_ELEMENT)
            .parent(parent)
            .build();
    FieldGroup input =
        FieldGroup.builder()
            .key("answer.valueDate")
            .className("hospitalizationStartDate")
            .type(FieldGroup.TYPE_INPUT)
            .parent(start)
            .props(
                Props.builder()
                    .required(true)
                    .label("Aufnahmedatum")
                    .placeholder("TT.MM.JJJJ | MM.JJJJ | JJJJ")
                    .build())
            .build();
    clipboardKey(item, LINK_ID_PERIOD_START).ifPresent(key -> createClipboard(key, false, input));
  }

  private void createPeriodEnd(Questionnaire.QuestionnaireItemComponent item, FieldGroup parent) {
    FieldGroup end =
        FieldGroup.builder()
            .key(LINK_ID_PERIOD_END)
            .fieldGroupClassName(REF_ELEMENT)
            .parent(parent)
            .build();
    FieldGroup input =
        FieldGroup.builder()
            .key("answer.valueDate")
            .className("hospitalizationEndDate")
            .type(FieldGroup.TYPE_INPUT)
            .parent(end)
            .props(
                Props.builder()
                    .required(false)
                    .label("Entlassdatum")
                    .placeholder("TT.MM.JJJJ | MM.JJJJ | JJJJ")
                    .build())
            .build();
    clipboardKey(item, LINK_ID_PERIOD_END).ifPresent(key -> createClipboard(key, false, input));
  }

  private void createReason(Questionnaire.QuestionnaireItemComponent item, FieldGroup parent) {
    if (!this.featureFlags.isMoveHospitalizationReason()) {
      return;
    }
    FieldGroup reason =
        FieldGroup.builder()
            .key(LINK_ID_REASON)
            .fieldGroupClassName(REF_ELEMENT)
            .parent(parent)
            .build();
    CodeDisplay[] options =
        this.dataLoaderSrv.getValueSetData(REASON_VALUE_SET).toArray(CodeDisplay[]::new);
    FieldGroup input =
        FieldGroup.builder()
            .type(FieldGroup.TYPE_CODING)
            .key("answer." + FieldGroup.KEY_VALUE_CODING)
            .className("hospitalizationReason")
            .parent(reason)
            .props(
                Props.builder()
                    .label("Grund")
                    .clearable(true)
                    .required(false)
                    .defaultCode("NASK")
                    .options(options)
                    .build())
            .build();
    ChoiceProcessor.enableValidation(input);
    clipboardKey(item, LINK_ID_REASON).ifPresent(key -> createClipboard(key, false, input));
  }

  private void createServiceProvider(
      Questionnaire.QuestionnaireItemComponent item, FieldGroup parent) {
    FieldGroup serviceProvider =
        FieldGroup.builder()
            .key("serviceProvider.answer")
            .fieldGroupClassName(REF_ELEMENT)
            .parent(parent)
            .build();
    final var enableWhen = item.getEnableWhen();
    item.setEnableWhen(Collections.emptyList());
    if (featureFlags.isHospCopyCheckboxes()) {
      this.organizationProcessor.createFieldGroup(
          item, serviceProvider, COPY_ORGANIZATION, COPY_CONTACT);
    } else {
      this.organizationProcessor.createFieldGroup(item, serviceProvider);
    }
    item.setEnableWhen(enableWhen);
  }

  private Optional<String> clipboardKey(
      Questionnaire.QuestionnaireItemComponent item, String parameterLinkId) {
    String itemLinkId = item.getLinkId();
    return this.diseaseClipboardProps.common().entrySet().stream()
        .filter(e -> clipboardKey(e, itemLinkId, parameterLinkId))
        .map(Map.Entry::getValue)
        .findFirst();
  }

  private boolean clipboardKey(
      Map.Entry<String, String> clipboard, String itemLinkId, String parameterLinkId) {
    final String key = clipboard.getKey();
    return key.startsWith(CLIPBOARD_MARKER_ENCOUNTER)
        && key.contains(itemLinkId)
        && key.contains(parameterLinkId);
  }
}
