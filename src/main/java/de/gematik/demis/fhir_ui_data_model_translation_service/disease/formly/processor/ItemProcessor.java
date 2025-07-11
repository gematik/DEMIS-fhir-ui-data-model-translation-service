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

import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import org.hl7.fhir.r4.model.Questionnaire;

/**
 * The {@code ItemProcessor} interface defines the contract for processing FHIR Questionnaire items
 * into internal {@link FieldGroup} structures.
 *
 * <p>Implementations of this interface enable the flexible and extensible transformation of
 * individual {@link org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent} objects into
 * one or more {@link FieldGroup} instances, which can be used for further processing or rendering
 * in UI components.
 *
 * <p>Typical use cases include the dynamic generation of UI form fields based on FHIR-compliant
 * questionnaires and disease-specific customization of field groups.
 */
public interface ItemProcessor {

  /**
   * Converts a FHIR Questionnaire item into one or more {@link FieldGroup} objects.
   *
   * @param item the {@link org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent} to be
   *     processed
   * @param parent the parent {@link FieldGroup} object (optional, may be {@code null})
   * @param diseaseCode the context code for disease-specific customization (optional)
   * @return an array of {@link FieldGroup} objects representing the item
   */
  FieldGroup[] createFieldGroup(
      Questionnaire.QuestionnaireItemComponent item, FieldGroup parent, String diseaseCode);
}
