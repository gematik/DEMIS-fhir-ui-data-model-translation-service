package de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.fieldtypes;

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

import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Props;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

/**
 * Standard implementation of a field type providing common properties and methods.
 *
 * <h2>Default values</h2>
 *
 * <p>Explicitly set values override field type specific defaults.
 *
 * <p>Example: The date picker uses <code>key=valueDate</code> by default. If <code>
 * picker.key("my-key")</code> is called, this value is used instead of the default.
 */
@SuperBuilder
public abstract class DefaultFieldType implements FieldType {

  private String key;
  private String className;
  private String type;
  private FieldGroup parent;
  private String label;
  private String tooltip;
  private Boolean required;

  @Override
  public final FieldGroup createFieldGroup() {
    final var fieldGroupBuilder = createFieldGroupBuilder();
    setProps(fieldGroupBuilder);
    final FieldGroup fieldGroup = fieldGroupBuilder.build();
    validate(fieldGroup);
    return fieldGroup;
  }

  private void validate(FieldGroup fieldGroup) {
    if (StringUtils.isBlank(fieldGroup.getKey())) {
      throw new IllegalArgumentException("FieldGroup key must not be empty");
    }
    if (StringUtils.isBlank(fieldGroup.getType())) {
      throw new IllegalArgumentException("FieldGroup type must not be empty");
    }
    if (StringUtils.isBlank(fieldGroup.getClassName())) {
      throw new IllegalArgumentException("FieldGroup className must not be empty");
    }
    if (fieldGroup.getParent() == null) {
      throw new IllegalArgumentException("FieldGroup parent must not be null");
    }
  }

  private FieldGroup.FieldGroupBuilder createFieldGroupBuilder() {
    final var builder = FieldGroup.builder();
    applyTo(builder);
    if (key != null) {
      builder.key(key);
    }
    if (className != null) {
      builder.className(className);
    }
    if (type != null) {
      builder.type(type);
    }
    if (parent != null) {
      builder.parent(parent);
    }
    return builder;
  }

  private void setProps(FieldGroup.FieldGroupBuilder fieldGroupBuilder) {
    final var properties = Props.builder();
    applyTo(properties);
    if (label != null) {
      properties.label(label);
    }
    if (tooltip != null) {
      properties.tooltip(tooltip);
    }
    if (required != null) {
      properties.required(required);
    }
    fieldGroupBuilder.props(properties.build());
  }

  abstract void applyTo(FieldGroup.FieldGroupBuilder builder);

  abstract void applyTo(Props.PropsBuilder builder);
}
