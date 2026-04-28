package de.gematik.demis.fhir_ui_data_model_translation_service.disease;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.gematik.demis.fhir_ui_data_model_translation_service.conceptmaps.ConceptMapPreparationSrv;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DiseaseFollowupSrvTest {

  private DiseaseDataLoaderSrv diseaseDataLoaderSrv;
  private ConceptMapPreparationSrv conceptMapPreparationSrv;
  private DiseaseFollowupSrv diseaseFollowupSrv;

  @BeforeEach
  void setUp() {
    diseaseDataLoaderSrv = mock(DiseaseDataLoaderSrv.class);
    conceptMapPreparationSrv = mock(ConceptMapPreparationSrv.class);
    diseaseFollowupSrv = new DiseaseFollowupSrv(diseaseDataLoaderSrv, conceptMapPreparationSrv);
  }

  @Test
  void testGetPossibleDiseaseCodesForFollowUp_CodeDisplayPresent() {
    String code = "mybd";
    CodeDisplay codeDisplay =
        CodeDisplay.builder().code(code).display("Tuberkulose, bei Behandlungsabbruch").build();
    when(diseaseDataLoaderSrv.getCodeDisplay(code)).thenReturn(Optional.of(codeDisplay));

    Set<CodeDisplay> result = diseaseFollowupSrv.getPossibleDiseaseCodesForFollowUp(code);

    assertEquals(1, result.size());
    assertTrue(result.contains(codeDisplay));
    verify(conceptMapPreparationSrv, never()).getPossibleCodesFromConceptMap(any(), any(), any());
  }

  @Test
  void testGetPossibleDiseaseCodesForFollowUp_CodeDisplayNotPresent() {
    String code = "mytp";
    Set<String> codes = new HashSet<>(Set.of("mytd", "mybd"));
    CodeDisplay codeDisplay = CodeDisplay.builder().code(code).display("Tuberkulose").build();
    CodeDisplay codeDisplay2 =
        CodeDisplay.builder().code(code).display("Tuberkulose, bei Behandlungsabbruch").build();

    when(diseaseDataLoaderSrv.getCodeDisplay(code)).thenReturn(Optional.empty());
    when(conceptMapPreparationSrv.getPossibleCodesFromConceptMap(eq(code), any(), any()))
        .thenReturn(codes);
    when(diseaseDataLoaderSrv.getCodeDisplay("mytd")).thenReturn(Optional.of(codeDisplay));
    when(diseaseDataLoaderSrv.getCodeDisplay("mybd")).thenReturn(Optional.of(codeDisplay2));

    Set<CodeDisplay> result = diseaseFollowupSrv.getPossibleDiseaseCodesForFollowUp(code);

    assertEquals(2, result.size());
    assertTrue(result.contains(codeDisplay));
  }

  @Test
  void testGetPossibleDiseaseCodesForFollowUp_NoCodeDisplayFound() {
    String code = "mytn";
    String possibleCode = "mytd";

    when(diseaseDataLoaderSrv.getCodeDisplay(code)).thenReturn(Optional.empty());
    when(conceptMapPreparationSrv.getPossibleCodesFromConceptMap(eq(code), any(), any()))
        .thenReturn(Set.of(possibleCode));
    when(diseaseDataLoaderSrv.getCodeDisplay(possibleCode)).thenReturn(Optional.empty());

    Set<CodeDisplay> result = diseaseFollowupSrv.getPossibleDiseaseCodesForFollowUp(code);

    assertTrue(result.isEmpty());
  }

  @Test
  void testGetPossibleDiseaseCodesForNonNominalFollowUp_CodeDisplayPresent() {
    String code = "hivd";
    CodeDisplay codeDisplay =
        CodeDisplay.builder().code(code).display("Humanes Immundefizienz-Virus (HIV)").build();
    when(diseaseDataLoaderSrv.getCodeDisplayForNonNominal(code))
        .thenReturn(Optional.of(codeDisplay));

    Set<CodeDisplay> result = diseaseFollowupSrv.getPossibleDiseaseCodesForNonNominalFollowUp(code);

    assertEquals(1, result.size());
    assertTrue(result.contains(codeDisplay));
    verify(conceptMapPreparationSrv, never()).getPossibleCodesFromConceptMap(any(), any(), any());
  }

  @Test
  void testGetPossibleDiseaseCodesForNonNominalFollowUp_CodeDisplayNotPresent() {
    String code = "hivp";
    Set<String> codes = new HashSet<>(Set.of("hivd", "hivx"));
    CodeDisplay codeDisplay =
        CodeDisplay.builder().code(code).display("Humanes Immundefizienz-Virus (HIV)").build();
    CodeDisplay codeDisplay2 = CodeDisplay.builder().code(code).display("Ein neues HIV").build();

    when(diseaseDataLoaderSrv.getCodeDisplayForNonNominal(code)).thenReturn(Optional.empty());
    when(conceptMapPreparationSrv.getPossibleCodesFromConceptMap(eq(code), any(), any()))
        .thenReturn(codes);
    when(diseaseDataLoaderSrv.getCodeDisplayForNonNominal("hivd"))
        .thenReturn(Optional.of(codeDisplay));
    when(diseaseDataLoaderSrv.getCodeDisplayForNonNominal("hivx"))
        .thenReturn(Optional.of(codeDisplay2));

    Set<CodeDisplay> result = diseaseFollowupSrv.getPossibleDiseaseCodesForNonNominalFollowUp(code);

    assertEquals(2, result.size());
    assertTrue(result.contains(codeDisplay));
  }

  @Test
  void testGetPossibleDiseaseCodesForNonNominalFollowUp_NoCodeDisplayFound() {
    String code = "hivd";
    String possibleCode = "hivx";

    when(diseaseDataLoaderSrv.getCodeDisplayForNonNominal(code)).thenReturn(Optional.empty());
    when(conceptMapPreparationSrv.getPossibleCodesFromConceptMap(eq(code), any(), any()))
        .thenReturn(Set.of(possibleCode));
    when(diseaseDataLoaderSrv.getCodeDisplayForNonNominal(possibleCode))
        .thenReturn(Optional.empty());

    Set<CodeDisplay> result = diseaseFollowupSrv.getPossibleDiseaseCodesForNonNominalFollowUp(code);

    assertTrue(result.isEmpty());
  }
}
