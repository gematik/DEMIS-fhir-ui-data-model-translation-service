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

import de.gematik.demis.fhir_ui_data_model_translation_service.context.OnlyInDiseaseContext;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.fhir.TooltipExtension;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Props;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.resources.ResourceProcessor;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Questionnaire;
import org.springframework.stereotype.Service;

@OnlyInDiseaseContext
@RequiredArgsConstructor
@Service
@Slf4j
public class ReferenceProcessor implements ItemProcessor {

  private final ResourceProcessor resourceProcessor;
  private final EnableWhenProcessor enableWhenProcessor;
  private final TooltipExtension tooltipExtension;

  /*
   * Wenn es eine unbekannte Profil-Referenz ist, wird ein leerer Array zurückgegeben.
   *
   * Wenn eine Ressourcen-Referenz unbekannt oder nicht vorhanden ist, dann geht es in den Fall "ID only".
   */
  @Override
  public FieldGroup[] createFieldGroup(
      Questionnaire.QuestionnaireItemComponent item, FieldGroup parent, String diseaseCode) {
    final Optional<ItemProcessor> processor = resourceProcessor.getItemProcessor(item);
    if (processor.isPresent()) {
      return processor.get().createFieldGroup(item, parent, diseaseCode);
    }
    return handleIdOnlyCase(item, parent).toArray();
  }

  private FieldGroup handleIdOnlyCase(
      Questionnaire.QuestionnaireItemComponent item, FieldGroup parent) {
    FieldGroup fieldGroupValueString =
        FieldGroup.builder()
            .key("valueReference")
            .type(FieldGroup.TYPE_INPUT)
            .parent(parent)
            .props(
                Props.builder()
                    .required(item.getRequired())
                    .label(item.getText())
                    .tooltip(tooltipExtension.getStringValueOrNull(item))
                    .build())
            .className("LinkId_" + item.getLinkId())
            .build();
    this.enableWhenProcessor.createEnableWhens(item, fieldGroupValueString);
    return fieldGroupValueString;
  }
}
