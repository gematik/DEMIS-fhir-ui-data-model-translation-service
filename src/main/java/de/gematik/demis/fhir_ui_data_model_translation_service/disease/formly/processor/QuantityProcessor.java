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

import static de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Wrapper.FORM_FIELD;

import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Props;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Questionnaire;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for processing quantity-related items in a FHIR Questionnaire. This class implements the
 * ItemProcessor interface and is responsible for creating field groups and extracting quantity
 * information from questionnaire items.
 */
@Service
public class QuantityProcessor implements ItemProcessor {

  // Dependencies for processing enableWhen conditions and clipboard functionality.
  private final EnableWhenProcessor enableWhenProcessor;
  private final ClipboardProcessor clipboardProcessor;
  private final QuantityBoundProcessor quantityBoundProcessor;
  private final boolean is73Enabled;

  QuantityProcessor(
      final EnableWhenProcessor enableWhenProcessor,
      final ClipboardProcessor clipboardProcessor,
      final QuantityBoundProcessor quantityBoundProcessor,
      @Value("${feature.flag.notifications.7_3}") final boolean is73Enabled) {
    this.enableWhenProcessor = enableWhenProcessor;
    this.clipboardProcessor = clipboardProcessor;
    this.quantityBoundProcessor = quantityBoundProcessor;
    this.is73Enabled = is73Enabled;
  }

  // URL for the unit extension in FHIR Questionnaire items.
  private static final String UNIT_EXTENSION_URL =
      "http://hl7.org/fhir/StructureDefinition/questionnaire-unitOption";

  /**
   * Creates field groups for a given questionnaire item.
   *
   * @param item The questionnaire item to process.
   * @param parent The parent field group.
   * @param diseaseCode The disease code associated with the questionnaire.
   * @return An array of FieldGroup objects representing the processed item.
   */
  @Override
  public FieldGroup[] createFieldGroup(
      Questionnaire.QuestionnaireItemComponent item, FieldGroup parent, String diseaseCode) {
    // Build the field group with properties and metadata.
    final var fieldGroup =
        FieldGroup.builder()
            .key(item.getLinkId()) // Unique identifier for the field group.
            .type(FieldGroup.TYPE_INPUT) // Specifies the type of input field.
            .props(createProps(item)) // Creates properties for the field group.
            .parent(parent) // Sets the parent field group.
            .wrappers(List.of(FORM_FIELD)) // Adds wrappers for the field group.
            .className("LinkId_" + item.getLinkId()) // Sets a CSS class name based on the link ID.
            .build();

    // Frontend will automatically take care of number format and nonBlank for input type=number,
    // all others
    // use the legacy mechanism of hardcoded validators
    if (!"number".equals(fieldGroup.getProps().getType())) {
      fieldGroup.addValidator("numberValidator");
      fieldGroup.addValidator("nonBlankValidator");
    }

    // Process enableWhen conditions for dynamic field visibility.
    enableWhenProcessor.createEnableWhens(item, fieldGroup);

    // Process clipboard functionality for the field group.
    clipboardProcessor.createClipboard(item, fieldGroup);

    // Return the created field group as an array.
    return new FieldGroup[] {fieldGroup};
  }

  /**
   * Creates properties for a given questionnaire item.
   *
   * @param item The questionnaire item to process.
   * @return A Props object containing the item's properties.
   */
  private Props createProps(Questionnaire.QuestionnaireItemComponent item) {
    final Props.PropsBuilder propsBuilder =
        Props.builder()
            .label(item.getText()) // Sets the label for the field group.
            .required(item.getRequired()) // Indicates whether the field is required.
            .quantity(extractQuantity(item)); // Extracts quantity information from the item.

    if (is73Enabled) {
      final Optional<BigDecimal> min = quantityBoundProcessor.findMin(item);
      min.ifPresent(propsBuilder::min);
      final Optional<BigDecimal> max = quantityBoundProcessor.findMax(item);
      max.ifPresent(propsBuilder::max);
      final Optional<BigDecimal> scale = quantityBoundProcessor.findStepValue(item);
      scale.ifPresent(propsBuilder::step);
      if (min.isPresent() || max.isPresent()) {
        propsBuilder.quantity(
            Props.Quantity.builder()
                .system(quantityBoundProcessor.findUnitSystem(item))
                .code(quantityBoundProcessor.findUnitCode(item))
                .build());
        propsBuilder.type("number");
      }
    }
    return propsBuilder.build();
  }

  /**
   * Extracts quantity information from a questionnaire item.
   *
   * @param item The questionnaire item to process.
   * @return A Props.Quantity object containing the quantity details, or null if not found.
   */
  private Props.Quantity extractQuantity(Questionnaire.QuestionnaireItemComponent item) {
    // Iterate through the item's extensions to find the unit extension.
    for (Extension extension : item.getExtension()) {
      if (UNIT_EXTENSION_URL.equals(extension.getUrl())) {
        // Extract the coding information from the extension.
        Coding coding = (Coding) extension.getValue();
        return Props.Quantity.builder()
            .system(coding.getSystem()) // Sets the system for the quantity.
            .unit(coding.getDisplay()) // Sets the unit display name.
            .code(coding.getCode()) // Sets the code for the quantity
            .build();
      }
    }
    // Return null if no quantity information is found.
    return null;
  }
}
