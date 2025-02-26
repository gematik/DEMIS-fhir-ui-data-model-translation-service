package de.gematik.demis.fhir_ui_data_model_translation_service.translation;

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

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.Designation;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ValueSetsTest {

  public static final String ABVP_LAB_TEST_CODE_EXAMPLE = "6309-9";
  static final FhirContext FHIR_CONTEXT = FhirContext.forR4();
  static final File EVIDENCE_CVDD =
      new File("src/test/resources/profilesNotForIT/ValueSet-evidenceCVDD.json");
  static final File EVIDENCE_CVDD2 =
      new File("src/test/resources/profilesNotForIT/ValueSet-evidenceCVDD2.json");
  static final File ABVP_LAB_TEST_FILE =
      new File("src/test/resources/profilesNotForIT/ValueSet-laboratoryTestABVP.json");
  static final File ANSWER_SET_FOR_SORT_FILE =
      new File("src/test/resources/sortedValueSets/ValueSet-answerSetINVP.json");
  static final String ABVP_LAB_TEST_SYSTEM =
      "https://demis.rki.de/fhir/ValueSet/laboratoryTestABVP";
  static final String BORP_LAB_TEST_FILE =
      "src/test/resources/profilesNotForIT/ValueSet-laboratoryTestBORP.json";
  static final String RESISTANCE_FILE =
      "src/test/resources/profilesNotForIT/ValueSet-resistance.json";
  static final String RESISTANCE_GENE_FILE =
      "src/test/resources/profilesNotForIT/ValueSet-resistanceGene.json";
  static final String SORT_FILTER_FILE =
      "src/test/resources/sortedValueSets/ValueSet-materialINVP.json";
  @Mock private CodeSystems codeSystemsMock;

  @Test
  void shouldCreateValueSetsWithoutExpansionData() {
    LinkedHashSet<File> files = new LinkedHashSet<>(singleton(EVIDENCE_CVDD));
    ValueSets valueSets = new ValueSets(files, FHIR_CONTEXT, codeSystemsMock);
    valueSets.build();

    assertThat(valueSets.getValueSetData()).isNotEmpty();
    // include data
    assertThat(valueSets.getValueSetData().get("https://demis.rki.de/fhir/ValueSet/evidenceCVDD"))
        .containsKey("67782005");
    CodeDisplay codeDisplay67782005 =
        valueSets
            .getValueSetData()
            .get("https://demis.rki.de/fhir/ValueSet/evidenceCVDD")
            .get("67782005");
    assertThat(codeDisplay67782005.getDisplay()).isEqualTo("akutes schweres Atemnotsyndrom (ARDS)");
    assertThat(codeDisplay67782005.getDesignations())
        .containsExactly(
            new Designation("en-US", "Acute respiratory distress syndrome (disorder)"));
    // expansion data
    assertThat(valueSets.getValueSetData().get("https://demis.rki.de/fhir/ValueSet/evidenceCVDD"))
        .doesNotContainKey("409966000");
  }

  @Test
  void shouldCreateThreeEntries() {
    LinkedHashSet<File> files = new LinkedHashSet<>(asList(EVIDENCE_CVDD, EVIDENCE_CVDD2));
    ValueSets valueSets = new ValueSets(files, FHIR_CONTEXT, codeSystemsMock);
    valueSets.build();

    assertThat(valueSets.getValueSetData()).isNotEmpty();
    // include data
    assertThat(valueSets.getValueSetData())
        .containsKey("https://demis.rki.de/fhir/ValueSet/evidenceCVDD");
    assertThat(valueSets.getValueSetData())
        .containsKey("https://demis.rki.de/fhir/ValueSet/evidenceCVDD|1.2.0");
    assertThat(valueSets.getValueSetData())
        .containsKey("https://demis.rki.de/fhir/ValueSet/evidenceCVDD|2.0.0");
    Map<String, CodeDisplay> evidenceCVDD1 =
        valueSets.getValueSetData().get("https://demis.rki.de/fhir/ValueSet/evidenceCVDD");
    Map<String, CodeDisplay> evicenceCVDD120 =
        valueSets.getValueSetData().get("https://demis.rki.de/fhir/ValueSet/evidenceCVDD|1.2.0");
    Map<String, CodeDisplay> evidenceCVDD200 =
        valueSets.getValueSetData().get("https://demis.rki.de/fhir/ValueSet/evidenceCVDD|2.0.0");

    assertThat(evidenceCVDD1).isNotEqualTo(evicenceCVDD120).isEqualTo(evidenceCVDD200);
  }

  @Test
  void shouldCreateValueSetWithDesignationFromCodeSystem() throws IOException {
    Map<String, CodeDisplay> mockCodeToCodeDisplayMap = new HashMap<>();
    Set<Designation> designations1 = new HashSet<>();
    designations1.add(
        new Designation("de-LU", "Arbovirus identifiziert in Blut mit erregerspezifischer Kultur"));
    mockCodeToCodeDisplayMap.put(
        ABVP_LAB_TEST_CODE_EXAMPLE, CodeDisplay.builder().designations(designations1).build());
    Map<String, Map<String, CodeDisplay>> mockAllCodeSystemCodeDisplayData = new HashMap<>();
    mockAllCodeSystemCodeDisplayData.put("http://loinc.org", mockCodeToCodeDisplayMap);
    when(codeSystemsMock.getCodeSystemData()).thenReturn(mockAllCodeSystemCodeDisplayData);
    Map<String, Map<String, CodeDisplay>> valueSets =
        new ValueSets(
                new LinkedHashSet<>(singleton(ABVP_LAB_TEST_FILE)), FHIR_CONTEXT, codeSystemsMock)
            .build()
            .getValueSetData();
    assertThat(valueSets).hasSize(2);
    Map<String, CodeDisplay> valueSet = valueSets.get(ABVP_LAB_TEST_SYSTEM);
    assertThat(valueSet)
        .as("value set codes")
        .isNotNull()
        .isNotEmpty()
        .as("key with designation")
        .containsKey(ABVP_LAB_TEST_CODE_EXAMPLE);
    CodeDisplay translation = valueSet.get(ABVP_LAB_TEST_CODE_EXAMPLE);
    assertThat(translation).isNotNull();
    Set<Designation> designations = translation.getDesignations();
    assertThat(designations)
        .as("designation list not broken and null")
        .isNotNull()
        .as("both designations exists")
        .hasSize(2);
    List<String> languages = designations.stream().map(Designation::language).toList();
    assertThat(languages).containsExactlyInAnyOrder("de-DE", "de-LU");
  }

  @Test
  void shouldSortValueSetAsCodeDisplayRepresentation() throws IOException {
    Map<String, Map<String, CodeDisplay>> valueSets =
        new ValueSets(
                new LinkedHashSet<>(singleton(ANSWER_SET_FOR_SORT_FILE)),
                FHIR_CONTEXT,
                codeSystemsMock)
            .build()
            .getValueSetData();
    assertThat(valueSets).hasSize(2);
    Map<String, CodeDisplay> valueSet =
        valueSets.get("https://demis.rki.de/fhir/ValueSet/answerSetINVP");
    assertThat(valueSet).as("value set as code display exists").isNotNull().isNotEmpty();
    Comparator<Object> comparator =
        (o1, o2) -> {
          Integer i1 = (Integer) o1;
          Integer i2 = (Integer) o2;
          return i2.compareTo(i1);
        };
    assertThat(valueSet.values()).extracting("order").isSortedAccordingTo(comparator);
  }

  @Test
  void shouldProcessValueSetsWithDependenciesToOtherValueSets() {

    List<File> fileList =
        List.of(
            new File(BORP_LAB_TEST_FILE),
            new File(RESISTANCE_FILE),
            new File(RESISTANCE_GENE_FILE));
    LinkedHashSet<File> files = new LinkedHashSet<>(fileList);
    ValueSets valueSets = new ValueSets(files, FHIR_CONTEXT, codeSystemsMock);
    valueSets.build();

    assertThat(valueSets.getValueSetData()).isNotEmpty();
    // include data
    assertThat(
            valueSets
                .getValueSetData()
                .get("https://demis.rki.de/fhir/ValueSet/laboratoryTestBORP"))
        .as("should contain code from include loinc data")
        .containsKey("49615-8")
        .as("should contain code from include value set entry")
        .containsKey("18860-7");
  }

  @Test
  void shouldSortAndFilterValueSet() {
    LinkedHashSet<File> files = new LinkedHashSet<>(singleton(new File(SORT_FILTER_FILE)));
    ValueSets valueSets = new ValueSets(files, FHIR_CONTEXT, codeSystemsMock);
    valueSets.build();

    Map<String, CodeDisplay> actual =
        valueSets.getValueSetData().get("https://demis.rki.de/fhir/ValueSet/materialINVP");
    assertThat(actual)
        .doesNotContainKeys("258607008", "258446004", "119401005", "119400006", "472894002");
    assertThat(actual.keySet())
        .containsExactly("258500001", "258498002", "258450006", "119303007", "309174004");
  }

  @Nested
  class RegressionTest {

    @Test
    void shouldCreateValueSetsWithoutExpansionData() throws IOException {
      LinkedHashSet<File> files = new LinkedHashSet<>(singleton(EVIDENCE_CVDD));
      ValueSets valueSets = new ValueSets(files, FHIR_CONTEXT, codeSystemsMock);
      valueSets.build();

      assertThat(valueSets.getValueSetData()).isNotEmpty();
      // include data
      assertThat(valueSets.getValueSetData().get("https://demis.rki.de/fhir/ValueSet/evidenceCVDD"))
          .containsKey("67782005");
      CodeDisplay codeDisplay67782005 =
          valueSets
              .getValueSetData()
              .get("https://demis.rki.de/fhir/ValueSet/evidenceCVDD")
              .get("67782005");
      assertThat(codeDisplay67782005.getDisplay())
          .isEqualTo("akutes schweres Atemnotsyndrom (ARDS)");
      assertThat(codeDisplay67782005.getDesignations())
          .containsExactly(
              new Designation("en-US", "Acute respiratory distress syndrome (disorder)"));
      // expansion data
      assertThat(valueSets.getValueSetData().get("https://demis.rki.de/fhir/ValueSet/evidenceCVDD"))
          .doesNotContainKey("409966000");
    }

    @Test
    void shouldCreateValueSetWithDesignation() throws IOException {
      Map<String, CodeDisplay> mockCodeToCodeDisplayMap = new HashMap<>();
      Set<Designation> designations1 = new HashSet<>();
      designations1.add(
          new Designation(
              "de-LU", "Arbovirus identifiziert in Blut mit erregerspezifischer Kultur"));
      mockCodeToCodeDisplayMap.put(
          ABVP_LAB_TEST_CODE_EXAMPLE, CodeDisplay.builder().designations(designations1).build());
      Map<String, Map<String, CodeDisplay>> mockAllCodeSystemCodeDisplayData = new HashMap<>();
      mockAllCodeSystemCodeDisplayData.put("http://loinc.org", mockCodeToCodeDisplayMap);
      when(codeSystemsMock.getCodeSystemData()).thenReturn(mockAllCodeSystemCodeDisplayData);
      Map<String, Map<String, CodeDisplay>> valueSets =
          new ValueSets(
                  new LinkedHashSet<>(singletonList(ABVP_LAB_TEST_FILE)),
                  FHIR_CONTEXT,
                  codeSystemsMock)
              .build()
              .getValueSetData();
      assertThat(valueSets).hasSize(2);
      Map<String, CodeDisplay> valueSet = valueSets.get(ABVP_LAB_TEST_SYSTEM);
      assertThat(valueSet)
          .as("value set codes")
          .isNotNull()
          .isNotEmpty()
          .as("key with designation")
          .containsKey(ABVP_LAB_TEST_CODE_EXAMPLE);
      CodeDisplay translation = valueSet.get(ABVP_LAB_TEST_CODE_EXAMPLE);
      assertThat(translation).isNotNull();
      Set<Designation> designations = translation.getDesignations();
      assertThat(designations)
          .as("designation list not broken and null")
          .isNotNull()
          .as("both designations exists")
          .hasSize(2);
      List<String> languages = designations.stream().map(Designation::language).toList();
      assertThat(languages).containsExactlyInAnyOrder("de-DE", "de-LU");
    }

    @Test
    void shouldSortValueSetAsCodeDisplayRepresentation() throws IOException {
      Map<String, Map<String, CodeDisplay>> valueSets =
          new ValueSets(
                  new LinkedHashSet<>(singleton(ANSWER_SET_FOR_SORT_FILE)),
                  FHIR_CONTEXT,
                  codeSystemsMock)
              .build()
              .getValueSetData();
      assertThat(valueSets).hasSize(2);
      Map<String, CodeDisplay> valueSet =
          valueSets.get("https://demis.rki.de/fhir/ValueSet/answerSetINVP");
      assertThat(valueSet).as("value set as code display exists").isNotNull().isNotEmpty();
      assertThat(valueSet.values()).extracting("order").isEqualTo(List.of(100, 99, 50, 10));
    }

    @Test
    void shouldSortAndFilterValueSet() {
      LinkedHashSet<File> files = new LinkedHashSet<>(singleton(new File(SORT_FILTER_FILE)));
      ValueSets valueSets = new ValueSets(files, FHIR_CONTEXT, codeSystemsMock);
      valueSets.build();

      Map<String, CodeDisplay> actual =
          valueSets.getValueSetData().get("https://demis.rki.de/fhir/ValueSet/materialINVP");
      assertThat(actual)
          .containsKeys("119303007", "258450006", "258498002", "258500001", "309174004");
    }
  }
}
