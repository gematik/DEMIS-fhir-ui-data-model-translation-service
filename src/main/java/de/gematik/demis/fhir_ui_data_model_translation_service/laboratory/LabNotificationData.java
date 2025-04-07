package de.gematik.demis.fhir_ui_data_model_translation_service.laboratory;

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
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import java.util.List;

/**
 * Transmission object of all necessary data records for filling out a pathogen report. This data is
 * displayed on the diagnostics page of the general pathogen report after a specific pathogen has
 * been selected as a report reason.
 *
 * @param header -> long name of a pathogen following the notification category
 * @param subheader -> part of the long name after the first semicolon if there is any
 * @param methods
 * @param materials
 * @param answerSet
 * @param substances
 */
public record LabNotificationData(
    CodeDisplay codeDisplay,
    String header,
    String subheader,
    List<CodeDisplay> methods,
    List<CodeDisplay> materials,
    List<CodeDisplay> answerSet,
    List<CodeDisplay> substances,
    List<CodeDisplay> resistances,
    List<CodeDisplay> resistanceGenes) {

  @JsonIgnore
  public boolean isUseable() {
    return !methods.isEmpty() && !materials.isEmpty() && !answerSet.isEmpty();
  }
}
