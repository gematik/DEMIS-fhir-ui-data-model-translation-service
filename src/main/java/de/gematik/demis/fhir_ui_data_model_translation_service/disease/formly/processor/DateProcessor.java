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
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Questionnaire;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DateProcessor implements ItemProcessor {

  private final EnableWhenProcessor enableWhenProcessor;
  private final ClipboardProcessor clipboardProcessor;

  @Override
  public FieldGroup[] createFieldGroup(
      Questionnaire.QuestionnaireItemComponent item, FieldGroup parent, String diseaseCode) {
    final var fieldGroup =
        FieldGroup.builder()
            .type(FieldGroup.TYPE_INPUT)
            .props(createProperties(item))
            .key("valueDate")
            .parent(parent)
            .wrappers(List.of(FORM_FIELD))
            .className("LinkId_" + item.getLinkId())
            .build();
    this.enableWhenProcessor.createEnableWhens(item, fieldGroup);
    this.clipboardProcessor.createClipboard(item, fieldGroup);
    return new FieldGroup[] {fieldGroup};
  }

  private Props createProperties(Questionnaire.QuestionnaireItemComponent item) {
    return Props.builder().placeholder("TT.MM.JJJJ | MM.JJJJ | JJJJ").label(item.getText()).build();
  }
}
