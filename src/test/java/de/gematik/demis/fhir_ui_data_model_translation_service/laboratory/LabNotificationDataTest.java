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
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.objects.TestObjects;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LabNotificationDataTest {

  private final List<CodeDisplay> emptyList = Collections.emptyList();
  private final List<CodeDisplay> methodList =
      Collections.singletonList(TestObjects.codeDisplay().methodCode());
  private final List<CodeDisplay> materialList =
      Collections.singletonList(TestObjects.codeDisplay().materialCode());
  private final List<CodeDisplay> answerList =
      Collections.singletonList(TestObjects.codeDisplay().answerCode());
  private final List<CodeDisplay> substanceList =
      Collections.singletonList(TestObjects.codeDisplay().substanceCode());
  private final List<CodeDisplay> resistanceList =
      Collections.singletonList(TestObjects.codeDisplay().resistanceCode());
  private final List<CodeDisplay> resistanceGeneList =
      Collections.singletonList(TestObjects.codeDisplay().resistanceGeneCode());

  @Nested
  @DisplayName("tests if a data sets are usable")
  class IsUsabelCases {

    @Test
    @DisplayName("is not usable if materials are missing")
    void shouldReturnFalseForMissingMaterialTests() {
      assertThat(
              new LabNotificationData(
                      TestObjects.codeDisplay().code1(),
                      "header",
                      "subheader",
                      methodList,
                      emptyList,
                      answerList,
                      substanceList,
                      resistanceList,
                      resistanceGeneList)
                  .isUseable())
          .isFalse();
    }

    @Test
    @DisplayName("is not usable if answerSets are missing")
    void shouldReturnFalseForMissingAnswerSet() {
      assertThat(
              new LabNotificationData(
                      TestObjects.codeDisplay().code1(),
                      "header",
                      "subheader",
                      methodList,
                      materialList,
                      emptyList,
                      substanceList,
                      resistanceList,
                      resistanceGeneList)
                  .isUseable())
          .isFalse();
    }

    @Test
    @DisplayName("is usable if substances are missing")
    void shouldReturnTrueForMissingSubstances() {
      assertThat(
              new LabNotificationData(
                      TestObjects.codeDisplay().code1(),
                      "header",
                      "subheader",
                      methodList,
                      materialList,
                      answerList,
                      emptyList,
                      resistanceList,
                      resistanceGeneList)
                  .isUseable())
          .isTrue();
    }

    @Test
    @DisplayName("is not usable if methods are missing")
    void shouldReturnFalseForMissingMethods() {
      assertThat(
              new LabNotificationData(
                      TestObjects.codeDisplay().code1(),
                      "header",
                      "subheader",
                      emptyList,
                      materialList,
                      answerList,
                      substanceList,
                      resistanceList,
                      resistanceGeneList)
                  .isUseable())
          .isFalse();
    }
  }
}
