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
 * #L%
 */

import java.util.Map;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class QuestionnaireTranslation {

  private String title;
  private Map<String, String> items;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    QuestionnaireTranslation that = (QuestionnaireTranslation) o;
    return Objects.equals(title, that.title) && Objects.equals(items, that.items);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, items);
  }

  @Override
  public String toString() {
    return title;
  }
}
