package de.gematik.demis.fhir_ui_data_model_translation_service.model;

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

import static de.gematik.demis.fhir_ui_data_model_translation_service.model.Designation.getDesignations;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Set;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ValueSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

  @Nested
  @DisplayName(
      "Tests for toDesignation method with CodeSystem.ConceptDefinitionDesignationComponent")
  class ToDesignationCodeSystemTests {

    @Test
    @DisplayName("should create Designation without Use when Use is null")
    void shouldCreateDesignationWithoutUseWhenUseIsNull() {
      CodeSystem.ConceptDefinitionComponent conceptDefinitionComponent =
          new CodeSystem.ConceptDefinitionComponent();
      conceptDefinitionComponent.setDesignation(
          Collections.singletonList(
              new CodeSystem.ConceptDefinitionDesignationComponent()
                  .setLanguage("de")
                  .setValue("deutscher Text")));

      Set<Designation> designations = getDesignations(conceptDefinitionComponent);

      assertThat(designations).containsExactly(new Designation("de", "deutscher Text", null));
    }

    @Test
    @DisplayName("should create Designation with Use when Use has system and code")
    void shouldCreateDesignationWithUseWhenUseHasSystemAndCode() {
      CodeSystem.ConceptDefinitionComponent conceptDefinitionComponent =
          new CodeSystem.ConceptDefinitionComponent();
      Coding use = new Coding().setSystem("http://example.org/system").setCode("synonym");
      conceptDefinitionComponent.setDesignation(
          Collections.singletonList(
              new CodeSystem.ConceptDefinitionDesignationComponent()
                  .setLanguage("en")
                  .setValue("english text")
                  .setUse(use)));

      Set<Designation> designations = getDesignations(conceptDefinitionComponent);

      assertThat(designations)
          .containsExactly(
              new Designation(
                  "en", "english text", new Use("http://example.org/system", "synonym")));
    }

    @Test
    @DisplayName("should create Designation without Use when Use has null code")
    void shouldCreateDesignationWithoutUseWhenUseHasNullCode() {
      CodeSystem.ConceptDefinitionComponent conceptDefinitionComponent =
          new CodeSystem.ConceptDefinitionComponent();
      Coding use = new Coding().setSystem("http://example.org/system");
      conceptDefinitionComponent.setDesignation(
          Collections.singletonList(
              new CodeSystem.ConceptDefinitionDesignationComponent()
                  .setLanguage("fr")
                  .setValue("texte français")
                  .setUse(use)));

      Set<Designation> designations = getDesignations(conceptDefinitionComponent);

      assertThat(designations).containsExactly(new Designation("fr", "texte français", null));
    }

    @Test
    @DisplayName("should create Designation without Use when Use has null system")
    void shouldCreateDesignationWithoutUseWhenUseHasNullSystem() {
      CodeSystem.ConceptDefinitionComponent conceptDefinitionComponent =
          new CodeSystem.ConceptDefinitionComponent();
      Coding use = new Coding().setCode("synonym");
      conceptDefinitionComponent.setDesignation(
          Collections.singletonList(
              new CodeSystem.ConceptDefinitionDesignationComponent()
                  .setLanguage("es")
                  .setValue("texto español")
                  .setUse(use)));

      Set<Designation> designations = getDesignations(conceptDefinitionComponent);

      assertThat(designations).containsExactly(new Designation("es", "texto español", null));
    }
  }

  @Nested
  @DisplayName("Tests for toDesignation method with ValueSet.ConceptReferenceDesignationComponent")
  class ToDesignationValueSetTests {

    @Test
    @DisplayName("should create Designation without Use when Use is null")
    void shouldCreateDesignationWithoutUseWhenUseIsNull() {
      ValueSet.ConceptReferenceComponent conceptReferenceComponent =
          new ValueSet.ConceptReferenceComponent();
      conceptReferenceComponent.setDesignation(
          Collections.singletonList(
              new ValueSet.ConceptReferenceDesignationComponent()
                  .setLanguage("de")
                  .setValue("deutscher Text")));

      Set<Designation> designations = getDesignations(conceptReferenceComponent);

      assertThat(designations).containsExactly(new Designation("de", "deutscher Text", null));
    }

    @Test
    @DisplayName("should create Designation with Use when Use has system and code")
    void shouldCreateDesignationWithUseWhenUseHasSystemAndCode() {
      ValueSet.ConceptReferenceComponent conceptReferenceComponent =
          new ValueSet.ConceptReferenceComponent();
      Coding use = new Coding().setSystem("http://snomed.info/sct").setCode("900000000000013009");
      conceptReferenceComponent.setDesignation(
          Collections.singletonList(
              new ValueSet.ConceptReferenceDesignationComponent()
                  .setLanguage("en")
                  .setValue("english text")
                  .setUse(use)));

      Set<Designation> designations = getDesignations(conceptReferenceComponent);

      assertThat(designations)
          .containsExactly(
              new Designation(
                  "en", "english text", new Use("http://snomed.info/sct", "900000000000013009")));
    }

    @Test
    @DisplayName("should create Designation without Use when Use has null code")
    void shouldCreateDesignationWithoutUseWhenUseHasNullCode() {
      ValueSet.ConceptReferenceComponent conceptReferenceComponent =
          new ValueSet.ConceptReferenceComponent();
      Coding use = new Coding().setSystem("http://example.org/system");
      conceptReferenceComponent.setDesignation(
          Collections.singletonList(
              new ValueSet.ConceptReferenceDesignationComponent()
                  .setLanguage("fr")
                  .setValue("texte français")
                  .setUse(use)));

      Set<Designation> designations = getDesignations(conceptReferenceComponent);

      assertThat(designations).containsExactly(new Designation("fr", "texte français", null));
    }

    @Test
    @DisplayName("should create Designation without Use when Use has null system")
    void shouldCreateDesignationWithoutUseWhenUseHasNullSystem() {
      ValueSet.ConceptReferenceComponent conceptReferenceComponent =
          new ValueSet.ConceptReferenceComponent();
      Coding use = new Coding().setCode("synonym");
      conceptReferenceComponent.setDesignation(
          Collections.singletonList(
              new ValueSet.ConceptReferenceDesignationComponent()
                  .setLanguage("es")
                  .setValue("texto español")
                  .setUse(use)));

      Set<Designation> designations = getDesignations(conceptReferenceComponent);

      assertThat(designations).containsExactly(new Designation("es", "texto español", null));
    }

    @Test
    @DisplayName("should handle multiple designations with mixed Use values")
    void shouldHandleMultipleDesignationsWithMixedUseValues() {
      ValueSet.ConceptReferenceComponent conceptReferenceComponent =
          new ValueSet.ConceptReferenceComponent();
      Coding useWithBoth = new Coding().setSystem("http://example.org").setCode("code1");
      conceptReferenceComponent.setDesignation(
          asList(
              new ValueSet.ConceptReferenceDesignationComponent()
                  .setLanguage("de")
                  .setValue("Text mit Use")
                  .setUse(useWithBoth),
              new ValueSet.ConceptReferenceDesignationComponent()
                  .setLanguage("en")
                  .setValue("Text without Use")));

      Set<Designation> designations = getDesignations(conceptReferenceComponent);

      assertThat(designations)
          .containsExactlyInAnyOrder(
              new Designation("de", "Text mit Use", new Use("http://example.org", "code1")),
              new Designation("en", "Text without Use", null));
    }
  }
}
