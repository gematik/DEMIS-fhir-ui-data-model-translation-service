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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import de.gematik.demis.fhir_ui_data_model_translation_service.objects.TestObjects;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CodeDisplayTest {

  @Test
  @DisplayName("Testcase for the util method extractFirstGermanDesignation()")
  void shouldReturnFirstGermanDesignationFromCodeDisplay() {
    Set<Designation> firstGermanDesignation =
        TestObjects.codeDisplayWithDesignation()
            .codeWithOneGermanDesignation()
            .extractGermanDesignations();
    assertThat(firstGermanDesignation)
        .containsExactly(new Designation("de-DE", "german designation"));
  }

  @Test
  @DisplayName(
      "Testcase for the util method extractFirstGermanDesignation() with multiple german designations")
  void shouldReturnFirstGermanDesignationFromMultipleGermanDesignations() {
    Set<Designation> firstGermanDesignation =
        TestObjects.codeDisplayWithDesignation()
            .codeWithTwoGermanDesignation()
            .extractGermanDesignations();
    assertThat(firstGermanDesignation)
        .isEqualTo(
            Set.of(
                new Designation("de-CH", "swiss designation"),
                new Designation("de-DE", "german designation")));
  }

  @Test
  @DisplayName(
      "Testcase for the util method extractFirstGermanDesignation() with multiple designations")
  void shouldReturnFirstGermanDesignationFromMultipleDesignations() {
    Set<Designation> firstGermanDesignation =
        TestObjects.codeDisplayWithDesignation()
            .codeWithOneEnglishAndOneGermanDesignation()
            .extractGermanDesignations();
    assertThat(firstGermanDesignation)
        .isEqualTo(Set.of(new Designation("de-DE", "german designation")));
  }

  @Test
  @DisplayName(
      "Testcase for the util method extractFirstGermanDesignation() with multiple designations")
  void shouldReturnEmptySet() {
    var testobject = TestObjects.codeDisplayWithDesignation().codeWithOneEnglishDesignation();
    assertThat(testobject.extractGermanDesignations()).isEmpty();
  }

  @Test
  void shouldCreateCodeDisplayWithNullForDesignations() {
    CodeDisplay codeDisplay =
        CodeDisplay.builder().code("code").display("dispaly").order(100).build();
    assertThat(codeDisplay.getDesignations()).isEmpty();
  }

  @Test
  @DisplayName("add single designation to codeDisplay without designation")
  void shouldAddDesignationToCodeDisplayWithNoDesignations() {
    CodeDisplay codeDisplay = CodeDisplay.builder().code("code").display("display").build();
    codeDisplay.addDesignation(new Designation("de-DE", "german designation"));
    assertThat(codeDisplay.getDesignations())
        .containsExactly(new Designation("de-DE", "german designation"));
  }

  @Test
  @DisplayName("add single designation to codeDisplay with designation")
  void shouldAddDesignationToCodeDisplayWithDesignation() {
    CodeDisplay codeDisplay =
        CodeDisplay.builder()
            .code("code")
            .display("display")
            .designations(new HashSet<>(List.of(new Designation("de-DE", "german designation"))))
            .build();
    codeDisplay.addDesignation(new Designation("de-CH", "swiss designation"));
    assertThat(codeDisplay.getDesignations())
        .containsExactlyInAnyOrder(
            new Designation("de-DE", "german designation"),
            new Designation("de-CH", "swiss designation"));
  }

  @Test
  @DisplayName("add multiple designation to codeDisplay without designation")
  void shouldAddMultipleDesignationToCodeDisplayWithNoDesignations() {
    CodeDisplay codeDisplay = CodeDisplay.builder().code("code").display("display").build();
    codeDisplay.addDesignation(
        Set.of(
            new Designation("de-DE", "german designation"),
            new Designation("en-US", "american english designation")));
    assertThat(codeDisplay.getDesignations())
        .containsExactlyInAnyOrder(
            new Designation("de-DE", "german designation"),
            new Designation("en-US", "american english designation"));
  }

  @Test
  @DisplayName("add multiple designation to codeDisplay with designation")
  void shouldMultipleDesignationToCodeDisplayWithDesignation() {
    CodeDisplay codeDisplay =
        CodeDisplay.builder()
            .code("code")
            .display("display")
            .designations(new HashSet<>(List.of(new Designation("de-DE", "german designation"))))
            .build();
    codeDisplay.addDesignation(
        Set.of(
            new Designation("de-CH", "swiss designation"),
            new Designation("en-US", "american english designation")));
    assertThat(codeDisplay.getDesignations())
        .containsExactlyInAnyOrder(
            new Designation("de-DE", "german designation"),
            new Designation("de-CH", "swiss designation"),
            new Designation("en-US", "american english designation"));
  }

  @Test
  @DisplayName("add single designation to codeDisplay without designation")
  void shouldAddEmptySetIfDesignationNull() {
    CodeDisplay codeDisplay = CodeDisplay.builder().code("code").display("display").build();
    codeDisplay.setDesignations(null);
    codeDisplay.addDesignation(new Designation("de-DE", "german designation"));
    assertThat(codeDisplay.getDesignations())
        .containsExactly(new Designation("de-DE", "german designation"));
  }

  @Test
  @DisplayName("add multiple designation to codeDisplay without designation")
  void shouldAddEmptySetIfDesignationNullMultipleDesignations() {
    CodeDisplay codeDisplay = CodeDisplay.builder().code("code").display("display").build();
    codeDisplay.setDesignations(null);
    codeDisplay.addDesignation(
        Set.of(
            new Designation("de-DE", "german designation"),
            new Designation("en-US", "american english designation")));
    assertThat(codeDisplay.getDesignations())
        .containsExactlyInAnyOrder(
            new Designation("de-DE", "german designation"),
            new Designation("en-US", "american english designation"));
  }

  @Nested
  @DisplayName("Testcases for the util method extractHeadersFromCodeDisplay()")
  class ExtractHeadersFromCodeDisplayTestCases {

    @Test
    void shouldReturnDisplayInListWithOneElement() {
      List<String> strings = TestObjects.codeDisplay().code1().extractHeadersFromCodeDisplay();
      assertThat(strings).hasSize(1).containsExactly("display1");
    }

    @Test
    void shouldReturnDisplayWithSemicolonInListWithTwoElement() {
      List<String> strings =
          TestObjects.codeDisplay().semicoloncase().extractHeadersFromCodeDisplay();
      assertThat(strings).hasSize(2).containsExactly("before the semicolon", "after the semicolon");
    }

    @Test
    void shouldReturnDisplayWithMulitpleSemicolonInListWithTwoElement() {
      List<String> strings =
          TestObjects.codeDisplay().multipleSemicolonCase().extractHeadersFromCodeDisplay();
      assertThat(strings)
          .hasSize(2)
          .containsExactly("before the semicolon", "after the semicolon; and some more");
    }
  }

  @Nested
  class HashCodeAndEqualsTestCases {
    @Test
    void equalsAndHashCode_sameObject() {
      CodeDisplay cd1 =
          CodeDisplay.builder()
              .code("code1")
              .display("display1")
              .designations(
                  new HashSet<Designation>() {
                    {
                      add(new Designation("de", "designation1"));
                    }
                  })
              .order(1)
              .system("system1")
              .build();

      assertThat(cd1).isEqualTo(cd1).hasSameHashCodeAs(cd1.hashCode());
    }

    @Test
    void equalsAndHashCode_equalObjects() {
      CodeDisplay cd1 =
          CodeDisplay.builder()
              .code("code1")
              .display("display1")
              .designations(
                  new HashSet<Designation>() {
                    {
                      add(new Designation("de", "designation1"));
                    }
                  })
              .order(1)
              .system("system1")
              .build();

      CodeDisplay cd2 =
          CodeDisplay.builder()
              .code("code1")
              .display("display1")
              .designations(
                  new HashSet<Designation>() {
                    {
                      add(new Designation("de", "designation1"));
                    }
                  })
              .order(1)
              .system("system1")
              .build();

      assertThat(cd1).isEqualTo(cd2).hasSameHashCodeAs(cd2.hashCode());
    }

    @Test
    void equalsAndHashCode_notEqualObjects() {
      CodeDisplay cd1 =
          CodeDisplay.builder()
              .code("code1")
              .display("display1")
              .designations(
                  new HashSet<Designation>() {
                    {
                      add(new Designation("de", "designation1"));
                    }
                  })
              .order(1)
              .system("system1")
              .build();

      CodeDisplay cd2 =
          CodeDisplay.builder()
              .code("code2")
              .display("display2")
              .designations(
                  new HashSet<Designation>() {
                    {
                      add(new Designation("de", "designation2"));
                    }
                  })
              .order(2)
              .system("system2")
              .build();

      assertThat(cd1).isNotEqualTo(cd2);
      assertThat(cd1.hashCode()).isNotEqualTo(cd2.hashCode());
    }
  }

  @Nested
  @DisplayName("get german designation tests")
  class GetGermanDesignationTests {
    @Test
    void testGetGermanDesignationWithNoDesignation() {
      CodeDisplay codeDisplay = CodeDisplay.builder().display("Test Display").build();
      assertThat(codeDisplay.getGermanDesignation(codeDisplay)).isEqualTo("Test Display");
    }

    @Test
    void testGetGermanDesignationWithDesignation() {
      CodeDisplay codeDisplay =
          CodeDisplay.builder()
              .designations(
                  Collections.singleton(new Designation("de-DE", "some german designation")))
              .build();
      assertThat(codeDisplay.getGermanDesignation(codeDisplay))
          .isEqualTo("some german designation");
    }

    @Test
    void testReturnDefaultInCaseOfNoGermanDesignation() {
      CodeDisplay codeDisplay =
          CodeDisplay.builder()
              .display("Test Display")
              .designations(
                  Collections.singleton(new Designation("en-US", "some english designation")))
              .build();
      assertThat(codeDisplay.getGermanDesignation(codeDisplay)).isEqualTo("Test Display");
    }

    @Test
    void testGetGermanDesigantionWithMultipleDesignations() {
      CodeDisplay codeDisplay =
          CodeDisplay.builder()
              .display("Test Display")
              .designations(
                  Set.of(
                      new Designation("en-US", "some english designation"),
                      new Designation("de-DE", "some german designation")))
              .build();
      assertThat(codeDisplay.getGermanDesignation(codeDisplay))
          .isEqualTo("some german designation");
    }
  }
}
