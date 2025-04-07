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

import com.fasterxml.jackson.annotation.JsonInclude;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import lombok.Builder;
import lombok.Data;
import org.hl7.fhir.r4.model.Questionnaire;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Props {
  private CodeDisplay[] options;
  private Boolean required;
  private Boolean clearable;
  private String defaultCode;
  private String placeholder;
  private String itemName;
  private String label;
  private EnableWhen[] enableWhen;
  private Questionnaire.EnableWhenBehavior enableBehavior;
  private ImportSpec importSpec;
  private Boolean disabled;
}
