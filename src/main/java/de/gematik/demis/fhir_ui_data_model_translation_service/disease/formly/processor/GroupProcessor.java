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

import static de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Wrapper.PANEL;
import static org.springframework.util.ObjectUtils.isEmpty;

import de.gematik.demis.fhir_ui_data_model_translation_service.FeatureFlags;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Props;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Questionnaire;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroupProcessor implements ItemProcessor {

  private final EnableWhenProcessor enableWhenProcessor;
  private final FeatureFlags featureFlags;

  @Override
  public FieldGroup[] createFieldGroup(
      Questionnaire.QuestionnaireItemComponent item, FieldGroup parent, String diseaseCode) {
    if (item == null) {
      return new FieldGroup[0];
    }
    String key = item.getLinkId();
    String fieldGroupClassName = item.getLinkId();
    String label = item.getText();
    // create fieldGroup for group of questionnaire components
    FieldGroup.FieldGroupBuilder fieldGroupBuilder =
        FieldGroup.builder()
            .key(key)
            .fieldGroupClassName("ITEM_GROUP " + fieldGroupClassName)
            .parent(parent);

    if (featureFlags.isDiseaseGroupTitle() && !isEmpty(label)) {
      fieldGroupBuilder.props(Props.builder().label(label).build());
      fieldGroupBuilder.wrappers(List.of(PANEL));
    }
    FieldGroup fieldGroupForGroupComponent = fieldGroupBuilder.build();
    this.enableWhenProcessor.createEnableWhens(item, fieldGroupForGroupComponent);
    return new FieldGroup[] {fieldGroupForGroupComponent};
  }
}
