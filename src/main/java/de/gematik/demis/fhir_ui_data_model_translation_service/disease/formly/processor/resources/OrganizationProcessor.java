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

import static de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Wrapper.FORM_FIELD;
import static de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Wrapper.PANEL;
import static de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.ClipboardProcessor.createClipboard;

import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.DiseaseClipboardProps;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Props;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.ChoiceProcessor;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.EnableWhenProcessor;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.translation.DataLoaderSrv;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Questionnaire;
import org.springframework.stereotype.Component;

/** usable to add organization resource which is referenced to by a parent question */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrganizationProcessor {

  private static final String LINK_ID_COUNTRY = "country";
  private static final String ISO_3166 = "https://demis.rki.de/fhir/ValueSet/answerSetCountry";
  private static final String CLIPBOARD_MARKER_ORGANIZATION = ".org.";

  private final EnableWhenProcessor enableWhenProcessor;
  private final DiseaseClipboardProps diseaseClipboardProps;
  private final DataLoaderSrv dataLoaderSrv;

  /**
   * This method creates the formly representation of a standard Fhir organization. Three parts are
   * created: Address, Contact person and Contact option. For styling purposes, these are bracketed
   * with FieldGroups. Logical_Group_1 and Logical_Group_2 are still in the test stage for the
   * frontend and should be used for styling.
   *
   * @param item FHIR questionnaire item
   * @param parent Formly parent
   * @return field group
   */
  public FieldGroup createFieldGroup(
      Questionnaire.QuestionnaireItemComponent item, FieldGroup parent) {
    return createFieldGroup(item, parent, null, null);
  }

  FieldGroup createFieldGroup(
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
    FieldGroup organization = createPanelFieldGroup("Organization", parent, "Einrichtung");
    organization.setClassName("LinkId_" + item.getLinkId());
    if (copyOrganization != null) {
      copyOrganization.addTo(organization);
    }
    createName(item, organization);
    createAddress(item, organization);
    createContact(item, organization, copyContact);
    createTelecom(item, organization);
    return organization;
  }

  private void createName(Questionnaire.QuestionnaireItemComponent item, FieldGroup organization) {
    FieldGroup fieldGroup =
        createInputFieldGroup(
            item, "name", "institutionName", "Name der Einrichtung", organization);
    fieldGroup.getProps().setRequired(Boolean.TRUE);
  }

  private void createAddress(Questionnaire.QuestionnaireItemComponent item, FieldGroup parent) {
    FieldGroup address = createPanelFieldGroup("address", parent, "Adresse");
    createInputFieldGroup(item, "street", "street", "Straße", address);
    createInputFieldGroup(item, "houseNumber", "houseNumber", "Hausnummer", address);
    createInputFieldGroup(item, "postalCode", "zip", "Postleitzahl", address);
    createInputFieldGroup(item, "city", "city", "Stadt", address);
    createCountry(item, address);
  }

  private void createCountry(Questionnaire.QuestionnaireItemComponent item, FieldGroup address) {
    CodeDisplay[] options =
        this.dataLoaderSrv.getValueSetData(ISO_3166).toArray(CodeDisplay[]::new);
    FieldGroup input =
        FieldGroup.builder()
            .type(FieldGroup.TYPE_CODING)
            .key(LINK_ID_COUNTRY + ".answer." + FieldGroup.KEY_VALUE_CODING)
            .className(LINK_ID_COUNTRY)
            .parent(address)
            .props(Props.builder().label("Land").options(options).clearable(true).build())
            .build();
    ChoiceProcessor.enableValidation(input);
    clipboardKey(item, LINK_ID_COUNTRY).ifPresent(key -> createClipboard(key, false, input));
  }

  private void createContact(
      Questionnaire.QuestionnaireItemComponent item, FieldGroup parent, Checkbox checkbox) {
    FieldGroup contact = createPanelFieldGroup("contact", parent, "Ansprechperson");
    if (checkbox != null) {
      checkbox.addTo(contact);
    }
    createInputFieldGroup(item, "name.prefix", "prefix", "Titel", contact);
    createInputFieldGroup(item, "name.given", "firstname", "Vorname", contact);
    createInputFieldGroup(item, "name.family", "lastname", "Nachname", contact);
  }

  private void createTelecom(Questionnaire.QuestionnaireItemComponent item, FieldGroup outerGroup) {
    FieldGroup telecom = createPanelFieldGroup("telecom", outerGroup, "Kontaktmöglichkeiten");
    createInputFieldGroup(item, "phone", "phoneNo", "Telefonnummer", telecom);
    createInputFieldGroup(item, "email", "email", "Email", telecom);
  }

  private FieldGroup createInputFieldGroup(
      Questionnaire.QuestionnaireItemComponent item,
      String parameter,
      String className,
      String label,
      FieldGroup parent) {
    FieldGroup fieldGroup =
        FieldGroup.builder()
            .type(FieldGroup.TYPE_INPUT)
            .key(parameter + ".answer.valueString")
            .wrappers(List.of(FORM_FIELD))
            .className(className)
            .props(Props.builder().label(label).build())
            .parent(parent)
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
    String itemLinkId = item.getLinkId();
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
}
