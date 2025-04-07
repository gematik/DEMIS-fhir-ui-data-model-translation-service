package de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.LinkedList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Builder
public class FieldGroup {

  public static final String TYPE_REPEAT = "repeat-section";
  public static final String TYPE_INPUT = "input";
  public static final String TYPE_CODING = "autocomplete-coding";
  public static final String TYPE_CODING_MULTI = "autocomplete-multi-coding";
  public static final String TYPE_CODING_RADIO = "radio-button-coding";
  public static final String TYPE_TEXT_AREA = "textarea";

  /**
   * Component to let the user choose one option of many. This could be a radio-button component or
   * a drop-down component.
   */
  public static final String KEY_VALUE_CODING = "valueCoding";

  private String key;
  private String template;
  private String type;
  private String fieldGroupClassName;
  @Setter private Props props;
  private Validator validators;
  private FieldArray fieldArray;
  private List<Wrapper> wrappers;
  @Setter private String className;
  private Object defaultValue;

  /** Children of this field group. */
  @JsonProperty("fieldGroup")
  private List<FieldGroup> fieldGroups;

  /** Parent of this field group. */
  @JsonIgnore private FieldGroup parent;

  public static FieldGroupBuilder builder() {
    return new ChildCareFieldGroupBuilder();
  }

  public void addValidator(String validator) {
    if (this.validators == null) {
      this.validators = new Validator();
    }
    this.validators.getValidation().add(validator);
  }

  /**
   * Set field array and clear children if field array is set.
   *
   * @param fieldArray field array
   */
  public void setFieldArray(FieldArray fieldArray) {
    this.fieldArray = fieldArray;
    eliminateDoubletChildren();
  }

  private void eliminateDoubletChildren() {
    if (this.fieldArray != null) {
      FieldGroup[] arrayGroups = this.fieldArray.getFieldGroup();
      if ((arrayGroups != null) && (arrayGroups.length > 0)) {
        this.fieldGroups = null;
      }
    }
  }

  /** Lombok builder extension that automatically adds the created field group to its parent. */
  public static final class ChildCareFieldGroupBuilder extends FieldGroupBuilder {

    private static void addChild(FieldGroup fieldGroup) {
      FieldGroup parent = fieldGroup.parent;
      if (parent != null) {
        if (parent.fieldGroups == null) {
          parent.fieldGroups = new LinkedList<>();
        }
        parent.fieldGroups.add(fieldGroup);
      }
    }

    public FieldGroup build() {
      FieldGroup fieldGroup = super.build();
      addChild(fieldGroup);
      return fieldGroup;
    }
  }
}
