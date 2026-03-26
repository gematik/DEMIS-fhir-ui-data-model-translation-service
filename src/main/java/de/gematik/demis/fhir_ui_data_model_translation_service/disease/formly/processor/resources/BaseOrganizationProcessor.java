package de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.resources;

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

import static de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Wrapper.FORM_FIELD;
import static de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Wrapper.PANEL;
import static de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.ClipboardProcessor.createClipboard;

import de.gematik.demis.fhir_ui_data_model_translation_service.FeatureFlags;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.DiseaseClipboardProps;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Props;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Validation;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Validator;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.ChoiceProcessor;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.EnableWhenProcessor;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.ItemProcessor;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.translation.DataLoaderSrv;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Questionnaire;

@RequiredArgsConstructor
@Slf4j
abstract class BaseOrganizationProcessor implements ItemProcessor {

  static final String COUNTRY_LINK_ID = "country";
  static final String COUNTRY_VALUE_SET_ISO_3166 =
      "https://demis.rki.de/fhir/ValueSet/answerSetCountry";
  static final String CLIPBOARD_MARKER_ORGANIZATION = ".org.";
  static final String TYPE_LINK_ID = "type";
  static final String TYPE_VALUE_SET = "https://demis.rki.de/fhir/ValueSet/organizationType";
  private static final int BSNR_MAX_LENGTH = 9;

  private final EnableWhenProcessor enableWhenProcessor;
  private final DiseaseClipboardProps diseaseClipboardProps;
  private final DataLoaderSrv dataLoaderSrv;
  private final FeatureFlags featureFlags;

  @Override
  public final FieldGroup[] createFieldGroup(
      Questionnaire.QuestionnaireItemComponent item, FieldGroup parent, String diseaseCode) {
    return createFieldGroup(item, parent, null, null).toArray();
  }

  private CodeDisplay[] loadValueSet(String valueSetUrl) {
    return this.dataLoaderSrv.getValueSetData(valueSetUrl).toArray(CodeDisplay[]::new);
  }

  final FieldGroup createFieldGroup(
      Questionnaire.QuestionnaireItemComponent item,
      FieldGroup parent,
      Checkbox copyOrganization,
      Checkbox copyContact) {
    final var organization = createOrganization(item, parent, copyOrganization, copyContact);
    this.enableWhenProcessor.createEnableWhens(item, organization);
    return organization;
  }

  private FieldGroup createOrganization(
      Questionnaire.QuestionnaireItemComponent item,
      FieldGroup parent,
      Checkbox copyOrganization,
      Checkbox copyContact) {
    final FieldGroup organization = createPanelFieldGroup(getItemKey(), parent, "Einrichtung");
    organization.setClassName("LinkId_" + item.getLinkId());
    if (copyOrganization != null) {
      copyOrganization.addTo(organization);
    }
    createName(item, organization);
    createBsnr(item, organization);
    createType(item, organization);
    createAddress(item, organization);
    createContact(item, organization, copyContact);
    createTelecom(item, organization);
    return organization;
  }

  /**
   * Get name to set as key of root field group for organization. Default is "Organization",
   * override this method to set a different key.
   *
   * @return key for root field group of organization.
   */
  String getItemKey() {
    return "Organization";
  }

  private void createType(Questionnaire.QuestionnaireItemComponent item, FieldGroup parent) {
    final FeatureSpec feature = getTypeFeatureSpec();
    if (feature.enabled()) {
      final CodeDisplay[] options = loadValueSet(TYPE_VALUE_SET);
      final var props = Props.builder().label("Typ").options(options).clearable(true);
      if (feature.required()) {
        props.required(true);
      }
      final FieldGroup input =
          FieldGroup.builder()
              .type(FieldGroup.TYPE_CODING)
              .key(TYPE_LINK_ID + ".answer." + FieldGroup.KEY_VALUE_CODING)
              .className(TYPE_LINK_ID)
              .parent(parent)
              .props(props.build())
              .build();
      ChoiceProcessor.enableValidation(input);
      clipboardKey(item, TYPE_LINK_ID).ifPresent(key -> createClipboard(key, false, input));
    }
  }

  /**
   * Override this method to enable type input field creation.
   *
   * @return type feature configuration
   */
  FeatureSpec getTypeFeatureSpec() {
    return FeatureSpec.DISABLED;
  }

  private void createBsnr(Questionnaire.QuestionnaireItemComponent item, FieldGroup parent) {
    final FeatureSpec feature = getBsnrFeatureSpec();
    if (feature.enabled()) {
      final FieldGroup fieldGroup =
          createInputFieldGroup(
              item,
              "bsnr",
              "bsnr",
              "Betriebsstättennummer",
              feature.required(),
              parent,
              Validation.BSNR);
      fieldGroup.getProps().setMaxLength(BSNR_MAX_LENGTH);
    }
  }

  /**
   * Override this method to enable identifier input field creation.
   *
   * @return identifier feature configuration
   */
  FeatureSpec getBsnrFeatureSpec() {
    return FeatureSpec.DISABLED;
  }

  private void createName(Questionnaire.QuestionnaireItemComponent item, FieldGroup organization) {
    final FieldGroup fieldGroup =
        createInputFieldGroup(
            item, "name", "institutionName", "Name der Einrichtung", organization, Validation.TEXT);
    fieldGroup.getProps().setRequired(Boolean.TRUE);
  }

  private void createAddress(Questionnaire.QuestionnaireItemComponent item, FieldGroup parent) {
    final FieldGroup address = createPanelFieldGroup("address", parent, "Adresse");
    createInputFieldGroup(item, "street", "street", "Straße", address, Validation.TEXT);
    createInputFieldGroup(
        item, "houseNumber", "houseNumber", "Hausnummer", address, Validation.HOUSE_NUMBER);
    createInputFieldGroup(
        item, "postalCode", "zip", "Postleitzahl", address, Validation.INTERNATIONAL_ZIP);
    createInputFieldGroup(item, "city", "city", "Stadt", address, Validation.TEXT);
    createCountry(item, address);
  }

  private void createCountry(Questionnaire.QuestionnaireItemComponent item, FieldGroup address) {
    final CodeDisplay[] options = loadValueSet(COUNTRY_VALUE_SET_ISO_3166);
    final FieldGroup input =
        FieldGroup.builder()
            .type(FieldGroup.TYPE_CODING)
            .key(COUNTRY_LINK_ID + ".answer." + FieldGroup.KEY_VALUE_CODING)
            .className(COUNTRY_LINK_ID)
            .parent(address)
            .props(Props.builder().label("Land").options(options).clearable(true).build())
            .validators(Validator.of(Validation.TEXT))
            .build();
    ChoiceProcessor.enableValidation(input);
    clipboardKey(item, COUNTRY_LINK_ID).ifPresent(key -> createClipboard(key, false, input));
  }

  private void createContact(
      Questionnaire.QuestionnaireItemComponent item, FieldGroup parent, Checkbox checkbox) {
    final FieldGroup contact = createPanelFieldGroup("contact", parent, "Ansprechperson");
    if (checkbox != null) {
      checkbox.addTo(contact);
    }
    createInputFieldGroup(item, "name.prefix", "prefix", "Titel", contact, Validation.NAME);
    createInputFieldGroup(item, "name.given", "firstname", "Vorname", contact, Validation.NAME);
    createInputFieldGroup(item, "name.family", "lastname", "Nachname", contact, Validation.NAME);
  }

  private void createTelecom(Questionnaire.QuestionnaireItemComponent item, FieldGroup outerGroup) {
    final FieldGroup telecom = createPanelFieldGroup("telecom", outerGroup, "Kontaktmöglichkeiten");
    final FieldGroup phone =
        createInputFieldGroup(item, "phone", "phoneNo", "Telefonnummer", telecom, Validation.PHONE);
    final FieldGroup email =
        createInputFieldGroup(item, "email", "email", "Email", telecom, Validation.EMAIL);
    if (this.featureFlags.isDiseaseQuestionnaireOrgInputValidation()) {
      // explicitly not required because UI applies required:true
      // as default for phone and email fields
      phone.getProps().setRequired(false);
      email.getProps().setRequired(false);
    }
  }

  private FieldGroup createInputFieldGroup(
      Questionnaire.QuestionnaireItemComponent item,
      String parameter,
      String className,
      String label,
      FieldGroup parent,
      Validation... validations) {
    return createInputFieldGroup(item, parameter, className, label, false, parent, validations);
  }

  private FieldGroup createInputFieldGroup(
      Questionnaire.QuestionnaireItemComponent item,
      String parameter,
      String className,
      String label,
      boolean required,
      FieldGroup parent,
      Validation... validations) {
    final var props = Props.builder().label(label);
    if (required) {
      props.required(true);
    }
    final FieldGroup fieldGroup =
        FieldGroup.builder()
            .type(FieldGroup.TYPE_INPUT)
            .key(parameter + ".answer.valueString")
            .wrappers(List.of(FORM_FIELD))
            .className(className)
            .props(props.build())
            .parent(parent)
            .validators(
                this.featureFlags.isDiseaseQuestionnaireOrgInputValidation()
                    ? Validator.of(validations)
                    : null)
            .build();
    clipboardKey(item, parameter).ifPresent(key -> createClipboard(key, false, fieldGroup));
    return fieldGroup;
  }

  private FieldGroup createPanelFieldGroup(String item, FieldGroup parent, String label) {
    return FieldGroup.builder()
        .key(item)
        .parent(parent)
        .wrappers(List.of(PANEL))
        .props(Props.builder().label(label).build())
        .build();
  }

  private Optional<String> clipboardKey(
      Questionnaire.QuestionnaireItemComponent item, String parameterLinkId) {
    final String itemLinkId = item.getLinkId();
    return this.diseaseClipboardProps.common().entrySet().stream()
        .filter(e -> clipboardKey(e, itemLinkId, parameterLinkId))
        .map(Map.Entry::getValue)
        .findFirst();
  }

  private boolean clipboardKey(
      Map.Entry<String, String> clipboard, String itemLinkId, String parameterLinkId) {
    final String key = clipboard.getKey();
    return key.contains(CLIPBOARD_MARKER_ORGANIZATION)
        && key.contains(itemLinkId)
        && key.contains(parameterLinkId);
  }

  record FeatureSpec(boolean enabled, boolean required) {
    static final FeatureSpec DISABLED = new FeatureSpec(false, false);
    static final FeatureSpec ENABLED_OPTIONAL = new FeatureSpec(true, false);
    static final FeatureSpec ENABLED_REQUIRED = new FeatureSpec(true, true);
  }
}
