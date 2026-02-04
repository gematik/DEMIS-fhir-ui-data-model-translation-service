package de.gematik.demis.fhir_ui_data_model_translation_service.laboratory;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.gematik.demis.fhir_ui_data_model_translation_service.conceptmaps.ConceptMapPreparationSrv;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LaboratoryFollowupSrvTest {

  private ConceptMapPreparationSrv conceptMapPreparationSrv;
  private LaboratoryDataLoaderSrv laboratoryDataLoaderSrv;
  private LaboratoryFollowupSrv laboratoryFollowupSrv;

  @BeforeEach
  void setUp() {
    conceptMapPreparationSrv = mock(ConceptMapPreparationSrv.class);
    laboratoryDataLoaderSrv = mock(LaboratoryDataLoaderSrv.class);
    laboratoryFollowupSrv =
        new LaboratoryFollowupSrv(conceptMapPreparationSrv, laboratoryDataLoaderSrv);
  }

  @Test
  void testGetPossibleLaboratoryCodesForFollowUp_CodeDisplayPresent() {
    String code = "mytp";
    PathogenNotificationCategory paragraph = PathogenNotificationCategory.P_7_1;
    CodeDisplay codeDisplay =
        CodeDisplay.builder().code(code).display("Mycobacterium Tuberculosis").build();
    when(laboratoryDataLoaderSrv.getAvailableNotificationCategory(code, paragraph))
        .thenReturn(Optional.of(codeDisplay));

    Set<CodeDisplay> result =
        laboratoryFollowupSrv.getPossibleLaboratoryCodesForFollowUp(code, paragraph);

    assertEquals(1, result.size());
    assertTrue(result.contains(codeDisplay));
    verify(conceptMapPreparationSrv, never()).getPossibleCodesFromConceptMap(any(), any(), any());
  }

  @Test
  void testGetPossibleLaboratoryCodesForFollowUp_CodeDisplayNotPresent() {
    String code = "mybd";
    String possibleCode = "mytp";
    PathogenNotificationCategory paragraph = PathogenNotificationCategory.P_7_1;
    CodeDisplay codeDisplay =
        CodeDisplay.builder().code(code).display("Mycobacterium Tuberculosis").build();

    when(laboratoryDataLoaderSrv.getAvailableNotificationCategory(code, paragraph))
        .thenReturn(Optional.empty());
    when(conceptMapPreparationSrv.getPossibleCodesFromConceptMap(eq(code), any(), any()))
        .thenReturn(Set.of(possibleCode));
    when(laboratoryDataLoaderSrv.getAvailableNotificationCategory(possibleCode, paragraph))
        .thenReturn(Optional.of(codeDisplay));

    Set<CodeDisplay> result =
        laboratoryFollowupSrv.getPossibleLaboratoryCodesForFollowUp(code, paragraph);

    assertEquals(1, result.size());
    assertTrue(result.contains(codeDisplay));
  }

  @Test
  void testGetPossibleLaboratoryCodesForFollowUp_NoCodeDisplayFound() {
    String code = "mytn";
    String possibleCode = "mytp";
    PathogenNotificationCategory paragraph = PathogenNotificationCategory.P_7_1;

    when(laboratoryDataLoaderSrv.getAvailableNotificationCategory(code, paragraph))
        .thenReturn(Optional.empty());
    when(conceptMapPreparationSrv.getPossibleCodesFromConceptMap(eq(code), any(), any()))
        .thenReturn(Set.of(possibleCode));
    when(laboratoryDataLoaderSrv.getAvailableNotificationCategory(possibleCode, paragraph))
        .thenReturn(Optional.empty());

    Set<CodeDisplay> result =
        laboratoryFollowupSrv.getPossibleLaboratoryCodesForFollowUp(code, paragraph);

    assertTrue(result.isEmpty());
  }
}
