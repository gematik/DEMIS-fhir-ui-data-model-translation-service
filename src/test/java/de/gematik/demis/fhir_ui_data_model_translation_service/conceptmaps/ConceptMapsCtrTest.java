package de.gematik.demis.fhir_ui_data_model_translation_service.conceptmaps;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConceptMapsCtrTest {

  @Mock private ConceptMapPreparationSrv conceptMapPreparationSrvMock;

  @InjectMocks private ConceptMapsCtr conceptMapsCtr;

  @Test
  void testGetAllConceptMaps() {

    conceptMapsCtr.getAllConceptMaps();

    verify(conceptMapPreparationSrvMock).getAllAvailableMaps();
  }

  @Test
  void testGetConceptMap() {
    String name = "name";

    conceptMapsCtr.getConceptMap(name);

    verify(conceptMapPreparationSrvMock).getMap(name);
  }

  @Test
  void testGetCode() {
    String name = "name";
    String code = "code";

    conceptMapsCtr.getCode(name, code);

    verify(conceptMapPreparationSrvMock).getCode(name, code);
  }
}
