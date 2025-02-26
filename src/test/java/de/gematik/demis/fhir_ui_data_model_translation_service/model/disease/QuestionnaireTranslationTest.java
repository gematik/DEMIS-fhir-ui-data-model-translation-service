package de.gematik.demis.fhir_ui_data_model_translation_service.model.disease;

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

import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.QuestionnaireTranslation;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

class QuestionnaireTranslationTest {

  @Test
  void equalsAndHashCode_sameObject() {
    QuestionnaireTranslation qt1 =
        QuestionnaireTranslation.builder()
            .title("title1")
            .items(
                new HashMap<String, String>() {
                  {
                    put("item1", "value1");
                  }
                })
            .build();

    assertThat(qt1).isEqualTo(qt1).hasSameHashCodeAs(qt1.hashCode());
  }

  @Test
  void equalsAndHashCode_equalObjects() {
    QuestionnaireTranslation qt1 =
        QuestionnaireTranslation.builder()
            .title("title1")
            .items(
                new HashMap<String, String>() {
                  {
                    put("item1", "value1");
                  }
                })
            .build();

    QuestionnaireTranslation qt2 =
        QuestionnaireTranslation.builder()
            .title("title1")
            .items(
                new HashMap<String, String>() {
                  {
                    put("item1", "value1");
                  }
                })
            .build();

    assertThat(qt1).isEqualTo(qt2).hasSameHashCodeAs(qt2.hashCode());
  }

  @Test
  void equalsAndHashCode_notEqualObjects() {
    QuestionnaireTranslation qt1 =
        QuestionnaireTranslation.builder()
            .title("title1")
            .items(
                new HashMap<String, String>() {
                  {
                    put("item1", "value1");
                  }
                })
            .build();

    QuestionnaireTranslation qt2 =
        QuestionnaireTranslation.builder()
            .title("title2")
            .items(
                new HashMap<String, String>() {
                  {
                    put("item2", "value2");
                  }
                })
            .build();

    assertThat(qt1).isNotEqualTo(qt2);
    assertThat(qt1.hashCode()).isNotEqualTo(qt2.hashCode());
  }
}
