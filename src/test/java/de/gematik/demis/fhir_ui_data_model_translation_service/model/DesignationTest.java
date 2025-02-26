package de.gematik.demis.fhir_ui_data_model_translation_service.model;

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

import static de.gematik.demis.fhir_ui_data_model_translation_service.model.Designation.getDesignations;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Set;
import org.hl7.fhir.r4.model.CodeSystem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DesignationTest {

  @DisplayName("Test static getDesignation method with 1 designation ")
  @Test
  void testGetDesignation() {

    CodeSystem.ConceptDefinitionComponent conceptDefinitionComponent =
        new CodeSystem.ConceptDefinitionComponent();
    conceptDefinitionComponent.setDesignation(
        Collections.singletonList(
            new CodeSystem.ConceptDefinitionDesignationComponent()
                .setLanguage("language")
                .setValue("value")));

    Set<Designation> designations = getDesignations(conceptDefinitionComponent);
    assertThat(designations).containsExactly(new Designation("language", "value"));
  }

  @DisplayName("Test static getDesignation method with 2 designations ")
  @Test
  void testGetDesignation2() {

    CodeSystem.ConceptDefinitionComponent conceptDefinitionComponent =
        new CodeSystem.ConceptDefinitionComponent();
    conceptDefinitionComponent.setDesignation(
        asList(
            new CodeSystem.ConceptDefinitionDesignationComponent()
                .setLanguage("language")
                .setValue("value"),
            new CodeSystem.ConceptDefinitionDesignationComponent()
                .setLanguage("language2")
                .setValue("value2")));

    Set<Designation> designations = getDesignations(conceptDefinitionComponent);
    assertThat(designations)
        .containsExactly(
            new Designation("language", "value"), new Designation("language2", "value2"));
  }
}
