package de.gematik.demis.fhir_ui_data_model_translation_service.translation;

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

import static org.assertj.core.api.Assertions.assertThat;

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhir_ui_data_model_translation_service.FeatureFlags;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.Designation;
import java.io.File;
import java.io.IOException;
import java.util.*;
import org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionDesignationComponent;
import org.hl7.fhir.r4.model.Coding;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CodeSystemsTest {

  public static final String CODE_SYSTEM_NULL_FLAVOR =
      "http://terminology.hl7.org/CodeSystem/v3-NullFlavor";
  private static final FeatureFlags FEATURE_FLAGS_ENABLED =
      FeatureFlags.builder().addCodeDisplayVersion(true).build();
  private static final File CODE_SYSTEM_NO_CONTENT =
      new File("src/test/resources/profilesNotForIT/CodeSystem-pzn.json");

  private final File addressUseFile =
      new File("src/test/resources/profiles/CodeSystem/CodeSystem-addressUse.json");
  private final File addressUseFile2 =
      new File("src/test/resources/profiles/CodeSystem/CodeSystem-addressUse2.json");
  private final File loincFile =
      new File("src/test/resources/profiles/CodeSystem/CodeSystem-loinc-2.74.json");
  private final File translationNullFlavorFile =
      new File("src/test/resources/profiles/CodeSystem/CodeSystem-translationNullFlavor.json");
  private final File v3FlavorFile =
      new File("src/test/resources/profiles/CodeSystem/CodeSystem-v3-NullFlavor.json");
  private final File v3FlavorFileReduced =
      new File("src/test/resources/profilesNotForIT/CodeSystem-v3-NullFlavor-ReducedContent.json");

  private final File fileForSorting =
      new File("src/test/resources/profilesNotForIT/CodeSystem-addressUseForSorting.json");

  @Test
  void shouldSortCodeSystemEntries() throws IOException {
    LinkedHashSet<File> files = new LinkedHashSet<>(Collections.singletonList(fileForSorting));
    CodeSystems codeSystems =
        new CodeSystems(
            files, FhirContext.forR4Cached(), Collections.emptyList(), FEATURE_FLAGS_ENABLED);
    codeSystems.build();

    Map<String, CodeDisplay> codeSystem =
        codeSystems.getCodeSystemData().get("https://demis.rki.de/fhir/CodeSystem/addressUse");
    assertThat(codeSystem).hasSize(3);
    assertThat(codeSystem.keySet()).containsExactly("primary", "ordinary", "current");
  }

  @Test
  void shouldCreateCodeSystems() throws IOException {
    LinkedHashSet<File> files = new LinkedHashSet<>(Arrays.asList(addressUseFile, loincFile));
    CodeSystems codeSystems =
        new CodeSystems(
            files, FhirContext.forR4Cached(), Collections.emptyList(), FEATURE_FLAGS_ENABLED);
    codeSystems.build();

    Map<String, Map<String, CodeDisplay>> codeSystemData = codeSystems.getCodeSystemData();
    assertThat(codeSystemData).hasSize(4);
    assertThat(codeSystemData.get("http://loinc.org")).isNotNull().isInstanceOf(Map.class);
    assertThat(codeSystemData.get("https://demis.rki.de/fhir/CodeSystem/addressUse"))
        .isNotNull()
        .isInstanceOf(Map.class);
  }

  @Test
  @DisplayName("should create code systems with two system with same url but different versions")
  void shouldCreateCodeSystemsWithTwoSystemWithSameUrlButDifferenVersions() throws IOException {
    LinkedHashSet<File> files =
        new LinkedHashSet<>(Arrays.asList(addressUseFile, addressUseFile2, loincFile));
    CodeSystems codeSystems =
        new CodeSystems(
            files, FhirContext.forR4Cached(), Collections.emptyList(), FEATURE_FLAGS_ENABLED);
    codeSystems.build();

    Map<String, Map<String, CodeDisplay>> codeSystemData = codeSystems.getCodeSystemData();
    assertThat(codeSystemData).hasSize(5);
    assertThat(codeSystemData.get("http://loinc.org")).isNotNull().isInstanceOf(Map.class);
    assertThat(codeSystemData.get("https://demis.rki.de/fhir/CodeSystem/addressUse"))
        .isNotNull()
        .isInstanceOf(Map.class);
    Map<String, CodeDisplay> addressUse200 =
        codeSystemData.get("https://demis.rki.de/fhir/CodeSystem/addressUse|2.0.0");
    Map<String, CodeDisplay> addressUse =
        codeSystemData.get("https://demis.rki.de/fhir/CodeSystem/addressUse");
    Map<String, CodeDisplay> addressUse100 =
        codeSystemData.get("https://demis.rki.de/fhir/CodeSystem/addressUse|1.1.0");
    assertThat(addressUse200).isEqualTo(addressUse);
    assertThat(addressUse100).isNotEqualTo(addressUse);
  }

  @Test
  void shouldProcessSupplementDataWithMissingSupplementedCodeSystem() throws IOException {
    LinkedHashSet<File> files =
        new LinkedHashSet<>(Collections.singletonList(translationNullFlavorFile));
    CodeSystems codeSystems =
        new CodeSystems(
            files, FhirContext.forR4Cached(), Collections.emptyList(), FEATURE_FLAGS_ENABLED);
    codeSystems.build();

    Map<String, Map<String, CodeDisplay>> codeSystemData = codeSystems.getCodeSystemData();
    assertThat(codeSystemData).hasSize(2);
    assertThat(codeSystemData.get("https://demis.rki.de/fhir/CodeSystem/translationNullFlavor"))
        .isNotNull()
        .isInstanceOf(Map.class);
  }

  @Test
  void shouldProcessSupplementDataAndAddDataToCodeSystem() throws IOException {
    LinkedHashSet<File> files =
        new LinkedHashSet<>(Arrays.asList(translationNullFlavorFile, v3FlavorFile));
    CodeSystems codeSystems =
        new CodeSystems(
            files, FhirContext.forR4Cached(), Collections.emptyList(), FEATURE_FLAGS_ENABLED);
    codeSystems.build();

    Map<String, Map<String, CodeDisplay>> codeSystemData = codeSystems.getCodeSystemData();
    assertThat(codeSystemData).hasSize(4);
    assertThat(codeSystemData.get("https://demis.rki.de/fhir/CodeSystem/translationNullFlavor"))
        .isNotNull()
        .isInstanceOf(Map.class);
    assertThat(codeSystemData.get("http://terminology.hl7.org/CodeSystem/v3-NullFlavor"))
        .isNotNull()
        .isInstanceOf(Map.class);
    assertThat(
            codeSystemData
                .get("http://terminology.hl7.org/CodeSystem/v3-NullFlavor")
                .get("INV")
                .getDesignations())
        .contains(new Designation("de", "ungültig"));
  }

  @Test
  void shouldProcessSupplementDataAndAddDataToCodeSystemWithINVMissing() throws IOException {
    LinkedHashSet<File> files =
        new LinkedHashSet<>(Arrays.asList(translationNullFlavorFile, v3FlavorFileReduced));
    CodeSystems codeSystems =
        new CodeSystems(
            files, FhirContext.forR4Cached(), Collections.emptyList(), FEATURE_FLAGS_ENABLED);
    codeSystems.build();

    Map<String, Map<String, CodeDisplay>> codeSystemData = codeSystems.getCodeSystemData();
    assertThat(codeSystemData).hasSize(4);
    assertThat(codeSystemData.get("https://demis.rki.de/fhir/CodeSystem/translationNullFlavor"))
        .isNotNull()
        .isInstanceOf(Map.class);
    assertThat(codeSystemData.get("http://terminology.hl7.org/CodeSystem/v3-NullFlavor"))
        .isNotNull()
        .isInstanceOf(Map.class);
    assertThat(codeSystemData.get("http://terminology.hl7.org/CodeSystem/v3-NullFlavor").get("INV"))
        .isNull();
  }

  @Test
  void shouldExcludeBreadcrumb() throws IOException {
    LinkedHashSet<File> files =
        new LinkedHashSet<>(Arrays.asList(translationNullFlavorFile, v3FlavorFileReduced));
    CodeSystems codeSystems =
        new CodeSystems(
            files,
            FhirContext.forR4Cached(),
            List.of(CODE_SYSTEM_NULL_FLAVOR),
            FEATURE_FLAGS_ENABLED);
    codeSystems.build();

    Map<String, Map<String, CodeDisplay>> codeSystemData = codeSystems.getCodeSystemData();
    Map<String, CodeDisplay> nullFlavors =
        codeSystemData.get("http://terminology.hl7.org/CodeSystem/v3-NullFlavor");
    CodeDisplay notAsked = nullFlavors.get("NASK");
    assertThat(notAsked).isNotNull();
    assertThat(notAsked.getDesignations()).contains(new Designation("de", "nicht erhoben"));
    assertThat(notAsked.getBreadcrumb()).isNull();
    assertThat(notAsked.getVersion()).isNotNull();
  }

  @Test
  void shouldAddCodeSystemToData() throws IOException {
    LinkedHashSet<File> files = new LinkedHashSet<>(Arrays.asList(addressUseFile, loincFile));
    CodeSystems codeSystems =
        new CodeSystems(
            files, FhirContext.forR4Cached(), Collections.emptyList(), FEATURE_FLAGS_ENABLED);
    codeSystems.build();

    Map<String, CodeDisplay> mapToAdd = new HashMap<>();
    mapToAdd.put(
        "testKey",
        CodeDisplay.builder().code("testCode").display("testDisplay").system("testSystem").build());
    CodeDisplayMapWithVersion codeDisplayMapWithVersion =
        new CodeDisplayMapWithVersion("testVersion", mapToAdd);

    codeSystems.addCodeSystem("testSystem", codeDisplayMapWithVersion);

    assertThat(codeSystems.getCodeSystemData().get("testSystem"))
        .isNotNull()
        .isInstanceOf(Map.class)
        .isEqualTo(mapToAdd);
  }

  @Test
  void shouldIgnoreCodeSystemsWithoutContent() throws IOException {
    LinkedHashSet<File> files = new LinkedHashSet<>(List.of(CODE_SYSTEM_NO_CONTENT));
    CodeSystems codeSystems =
        new CodeSystems(
            files, FhirContext.forR4Cached(), Collections.emptyList(), FEATURE_FLAGS_ENABLED);
    codeSystems.build();

    Map<String, Map<String, CodeDisplay>> codeSystemData = codeSystems.getCodeSystemData();
    assertThat(codeSystemData).isEmpty();
  }

  @Nested
  class RegressionTests {

    @Test
    void shouldCreateCodeSystems() throws IOException {
      LinkedHashSet<File> files = new LinkedHashSet<>(Arrays.asList(addressUseFile, loincFile));
      CodeSystems codeSystems =
          new CodeSystems(
              files, FhirContext.forR4Cached(), Collections.emptyList(), FEATURE_FLAGS_ENABLED);
      codeSystems.build();

      Map<String, Map<String, CodeDisplay>> codeSystemData = codeSystems.getCodeSystemData();
      assertThat(codeSystemData).hasSize(4);
      assertThat(codeSystemData.get("http://loinc.org")).isNotNull().isInstanceOf(Map.class);
      assertThat(codeSystemData.get("https://demis.rki.de/fhir/CodeSystem/addressUse"))
          .isNotNull()
          .isInstanceOf(Map.class);
    }

    @Test
    void shouldProcessSupplementDataWithMissingSupplementedCodeSystem() throws IOException {
      LinkedHashSet<File> files =
          new LinkedHashSet<>(Collections.singletonList(translationNullFlavorFile));
      CodeSystems codeSystems =
          new CodeSystems(
              files, FhirContext.forR4Cached(), Collections.emptyList(), FEATURE_FLAGS_ENABLED);
      codeSystems.build();

      Map<String, Map<String, CodeDisplay>> codeSystemData = codeSystems.getCodeSystemData();
      assertThat(codeSystemData).hasSize(2);
      assertThat(codeSystemData.get("https://demis.rki.de/fhir/CodeSystem/translationNullFlavor"))
          .isNotNull()
          .isInstanceOf(Map.class);
    }

    @Test
    void shouldProcessSupplementDataAndAddDataToCodeSystem() throws IOException {
      LinkedHashSet<File> files =
          new LinkedHashSet<>(Arrays.asList(translationNullFlavorFile, v3FlavorFile));
      CodeSystems codeSystems =
          new CodeSystems(
              files, FhirContext.forR4Cached(), Collections.emptyList(), FEATURE_FLAGS_ENABLED);
      codeSystems.build();

      Map<String, Map<String, CodeDisplay>> codeSystemData = codeSystems.getCodeSystemData();
      assertThat(codeSystemData).hasSize(4);
      assertThat(codeSystemData.get("https://demis.rki.de/fhir/CodeSystem/translationNullFlavor"))
          .isNotNull()
          .isInstanceOf(Map.class);
      assertThat(codeSystemData.get("http://terminology.hl7.org/CodeSystem/v3-NullFlavor"))
          .isNotNull()
          .isInstanceOf(Map.class);
      assertThat(
              codeSystemData
                  .get("http://terminology.hl7.org/CodeSystem/v3-NullFlavor")
                  .get("INV")
                  .getDesignations())
          .contains(new Designation("de", "ungültig"));
    }

    @Test
    void shouldProcessSupplementDataAndAddDataToCodeSystemWithINVMissing() throws IOException {
      LinkedHashSet<File> files =
          new LinkedHashSet<>(Arrays.asList(translationNullFlavorFile, v3FlavorFileReduced));
      CodeSystems codeSystems =
          new CodeSystems(
              files, FhirContext.forR4Cached(), Collections.emptyList(), FEATURE_FLAGS_ENABLED);
      codeSystems.build();

      Map<String, Map<String, CodeDisplay>> codeSystemData = codeSystems.getCodeSystemData();
      assertThat(codeSystemData).hasSize(4);
      assertThat(codeSystemData.get("https://demis.rki.de/fhir/CodeSystem/translationNullFlavor"))
          .isNotNull()
          .isInstanceOf(Map.class);
      assertThat(codeSystemData.get("http://terminology.hl7.org/CodeSystem/v3-NullFlavor"))
          .isNotNull()
          .isInstanceOf(Map.class);
      assertThat(
              codeSystemData.get("http://terminology.hl7.org/CodeSystem/v3-NullFlavor").get("INV"))
          .isNull();
    }

    @Test
    void shouldAddCodeSystemToData() throws IOException {
      LinkedHashSet<File> files = new LinkedHashSet<>(Arrays.asList(addressUseFile, loincFile));
      CodeSystems codeSystems =
          new CodeSystems(
              files, FhirContext.forR4Cached(), Collections.emptyList(), FEATURE_FLAGS_ENABLED);
      codeSystems.build();

      Map<String, CodeDisplay> mapToAdd = new HashMap<>();
      mapToAdd.put(
          "testKey",
          CodeDisplay.builder()
              .code("testCode")
              .display("testDisplay")
              .system("testSystem")
              .build());

      CodeDisplayMapWithVersion codeDisplayMapWithVersion =
          new CodeDisplayMapWithVersion("testVersion", mapToAdd);

      codeSystems.addCodeSystem("testSystem", codeDisplayMapWithVersion);

      assertThat(codeSystems.getCodeSystemData().get("testSystem"))
          .isNotNull()
          .isInstanceOf(Map.class)
          .isEqualTo(mapToAdd);
    }
  }

  @Nested
  @DisplayName("Tests for extractUseOrNull method")
  class ExtractUseOrNullTests {

    @Test
    @DisplayName("should return null when Use is null")
    void shouldReturnNullWhenUseIsNull() {
      var designation = new ConceptDefinitionDesignationComponent();
      designation.setLanguage("de");
      designation.setValue("deutscher Text");

      var result = CodeSystems.extractUseOrNull(designation);

      assertThat(result).isNull();
    }

    @Test
    @DisplayName("should return Use when Use has system and code")
    void shouldReturnUseWhenUseHasSystemAndCode() {
      var designation = new ConceptDefinitionDesignationComponent();
      designation.setLanguage("de");
      designation.setValue("deutscher Text");
      var coding = new Coding();
      coding.setSystem("http://snomed.info/sct");
      coding.setCode("900000000000013009");
      designation.setUse(coding);

      var result = CodeSystems.extractUseOrNull(designation);

      assertThat(result).isNotNull();
      assertThat(result.system()).isEqualTo("http://snomed.info/sct");
      assertThat(result.code()).isEqualTo("900000000000013009");
    }

    @Test
    @DisplayName("should return null when Use has null code")
    void shouldReturnNullWhenUseHasNullCode() {
      var designation = new ConceptDefinitionDesignationComponent();
      designation.setLanguage("de");
      designation.setValue("deutscher Text");
      var coding = new Coding();
      coding.setSystem("http://example.org/system");
      designation.setUse(coding);

      var result = CodeSystems.extractUseOrNull(designation);

      assertThat(result).isNull();
    }

    @Test
    @DisplayName("should return null when Use has null system")
    void shouldReturnNullWhenUseHasNullSystem() {
      var designation = new ConceptDefinitionDesignationComponent();
      designation.setLanguage("de");
      designation.setValue("deutscher Text");
      var coding = new Coding();
      coding.setCode("synonym");
      designation.setUse(coding);

      var result = CodeSystems.extractUseOrNull(designation);

      assertThat(result).isNull();
    }

    @Test
    @DisplayName("should return null when Use has both code and system as null")
    void shouldReturnNullWhenUseHasBothCodeAndSystemAsNull() {
      var designation = new ConceptDefinitionDesignationComponent();
      designation.setLanguage("de");
      designation.setValue("deutscher Text");
      var coding = new Coding();
      designation.setUse(coding);

      var result = CodeSystems.extractUseOrNull(designation);

      assertThat(result).isNull();
    }

    @ParameterizedTest
    @CsvSource({
      "'', '', 'both system and code are empty strings'",
      "'', 'validCode', 'system is empty string'",
      "'http://example.org/system', '', 'code is empty string'"
    })
    @DisplayName("should return null when empty strings are provided")
    void shouldReturnNullWhenEmptyStringsAreProvided(String system, String code, String scenario) {
      var designation = new ConceptDefinitionDesignationComponent();
      designation.setLanguage("de");
      designation.setValue("deutscher Text");
      var coding = new Coding();
      coding.setSystem(system);
      coding.setCode(code);
      designation.setUse(coding);

      var result = CodeSystems.extractUseOrNull(designation);

      assertThat(result).as("scenario: %s", scenario).isNull();
    }

    @Test
    @DisplayName("should handle Use with display value")
    void shouldHandleUseWithDisplayValue() {
      var designation = new ConceptDefinitionDesignationComponent();
      designation.setLanguage("en");
      designation.setValue("english text");
      var coding = new Coding();
      coding.setSystem("http://terminology.hl7.org/CodeSystem/designation-usage");
      coding.setCode("definition");
      coding.setDisplay("Definition");
      designation.setUse(coding);

      var result = CodeSystems.extractUseOrNull(designation);

      assertThat(result).isNotNull();
      assertThat(result.system())
          .isEqualTo("http://terminology.hl7.org/CodeSystem/designation-usage");
      assertThat(result.code()).isEqualTo("definition");
    }
  }
}
