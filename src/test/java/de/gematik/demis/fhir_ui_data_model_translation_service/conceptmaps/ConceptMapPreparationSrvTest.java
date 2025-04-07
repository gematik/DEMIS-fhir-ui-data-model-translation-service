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

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhir_ui_data_model_translation_service.exception.DataNotFoundExcp;
import de.gematik.demis.fhir_ui_data_model_translation_service.utils.SnapshotFilesService;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConceptMapPreparationSrvTest {

  private final List<File> files =
      List.of(
          new File(
              "src/test/resources/profiles/ConceptMap/ConceptMap-ISO3166CountryCodes2DEMISCountryCodes.json"),
          new File(
              "src/test/resources/profiles/ConceptMap/ConceptMap-LOINCMaterialToSNOMEDMaterial.json"),
          new File(
              "src/test/resources/profiles/ConceptMap/ConceptMap-LOINCMethodToSNOMEDMethod.json"),
          new File(
              "src/test/resources/profiles/ConceptMap/ConceptMap-NotificationCategoryToTransmissionCategory.json"),
          new File(
              "src/test/resources/profiles/ConceptMap/ConceptMap-NotificationDiseaseCategoryToTransmissionCategory.json"));
  @Mock private SnapshotFilesService snapshotFilesServiceMock;
  @Mock private FhirContext fhirContextMock;

  @Test
  void testInit() {
    when(snapshotFilesServiceMock.getConceptMaps()).thenReturn(files);
    when(fhirContextMock.newJsonParser()).thenReturn(FhirContext.forR4Cached().newJsonParser());
    ConceptMapPreparationSrv conceptMapPreparationSrv =
        new ConceptMapPreparationSrv(snapshotFilesServiceMock, fhirContextMock);
    conceptMapPreparationSrv.init();

    verify(snapshotFilesServiceMock).getConceptMaps();
    verify(fhirContextMock, times(5)).newJsonParser();
  }

  @Test
  void testGetAllAvailableMaps() {
    when(snapshotFilesServiceMock.getConceptMaps()).thenReturn(files);
    when(fhirContextMock.newJsonParser()).thenReturn(FhirContext.forR4Cached().newJsonParser());
    ConceptMapPreparationSrv conceptMapPreparationSrv =
        new ConceptMapPreparationSrv(snapshotFilesServiceMock, fhirContextMock);
    conceptMapPreparationSrv.init();

    List<String> allAvailableMaps = conceptMapPreparationSrv.getAllAvailableMaps();

    assertThat(allAvailableMaps)
        .hasSize(10)
        .containsExactlyInAnyOrder(
            "https://demis.rki.de/fhir/ConceptMap/LOINCMethodToSNOMEDMethod",
            "https://demis.rki.de/fhir/ConceptMap/NotificationCategoryToTransmissionCategory",
            "https://demis.rki.de/fhir/ConceptMap/LOINCMaterialToSNOMEDMaterial",
            "LOINCMethodToSNOMEDMethod",
            "NotificationCategoryToTransmissionCategory",
            "https://demis.rki.de/fhir/ConceptMap/NotificationDiseaseCategoryToTransmissionCategory",
            "NotificationDiseaseCategoryToTransmissionCategory",
            "ISO3166CountryCodes2DEMISCountryCodes",
            "https://demis.rki.de/fhir/ConceptMap/ISO3166CountryCodes2DEMISCountryCodes",
            "LOINCMaterialToSNOMEDMaterial");
  }

  @Test
  void testGetMap() {
    when(snapshotFilesServiceMock.getConceptMaps())
        .thenReturn(
            singletonList(
                new File("src/test/resources/profiles/ConceptMap/ConceptMap-foobar.json")));
    when(fhirContextMock.newJsonParser()).thenReturn(FhirContext.forR4Cached().newJsonParser());
    ConceptMapPreparationSrv conceptMapPreparationSrv =
        new ConceptMapPreparationSrv(snapshotFilesServiceMock, fhirContextMock);
    conceptMapPreparationSrv.init();
    Map<String, String> map1 = conceptMapPreparationSrv.getMap("foobar");
    Map<String, String> map2 =
        conceptMapPreparationSrv.getMap("https://demis.rki.de/fhir/ConceptMap/foobar");
    assertThat(map1).isEqualTo(map2);

    Map<String, String> expectedMap = new HashMap<>();
    expectedMap.put("band", "ban");
    expectedMap.put("bpsd", "bps");

    assertThat(map1).isEqualTo(expectedMap);
  }

  @Test
  void testGetCode() {
    when(snapshotFilesServiceMock.getConceptMaps())
        .thenReturn(
            singletonList(
                new File("src/test/resources/profiles/ConceptMap/ConceptMap-foobar.json")));
    when(fhirContextMock.newJsonParser()).thenReturn(FhirContext.forR4Cached().newJsonParser());
    ConceptMapPreparationSrv conceptMapPreparationSrv =
        new ConceptMapPreparationSrv(snapshotFilesServiceMock, fhirContextMock);
    conceptMapPreparationSrv.init();
    String code =
        conceptMapPreparationSrv.getCode("https://demis.rki.de/fhir/ConceptMap/foobar", "band");
    assertThat(code).isEqualTo("ban");
    String code2 = conceptMapPreparationSrv.getCode("foobar", "band");
    assertThat(code2).isEqualTo("ban");
  }

  @Test
  void testExceptionWhileGetMap() {
    when(snapshotFilesServiceMock.getConceptMaps())
        .thenReturn(
            singletonList(
                new File("src/test/resources/profiles/ConceptMap/ConceptMap-foobar.json")));
    when(fhirContextMock.newJsonParser()).thenReturn(FhirContext.forR4Cached().newJsonParser());
    ConceptMapPreparationSrv conceptMapPreparationSrv =
        new ConceptMapPreparationSrv(snapshotFilesServiceMock, fhirContextMock);
    conceptMapPreparationSrv.init();
    assertThrows(
        DataNotFoundExcp.class,
        () ->
            conceptMapPreparationSrv.getMap("NotificationDiseaseCategoryToTransmissionCategory2"));
  }

  @Test
  void testExceptionWhileGetCodeForMissingConceptMap() {
    when(snapshotFilesServiceMock.getConceptMaps())
        .thenReturn(
            singletonList(
                new File("src/test/resources/profiles/ConceptMap/ConceptMap-foobar.json")));
    when(fhirContextMock.newJsonParser()).thenReturn(FhirContext.forR4Cached().newJsonParser());
    ConceptMapPreparationSrv conceptMapPreparationSrv =
        new ConceptMapPreparationSrv(snapshotFilesServiceMock, fhirContextMock);
    conceptMapPreparationSrv.init();
    assertThrows(
        DataNotFoundExcp.class,
        () ->
            conceptMapPreparationSrv.getCode(
                "NotificationDiseaseCategoryToTransmissionCategory2", "band"));
  }

  @Test
  void testExceptionWhileGetCodeForMissingCode() {
    when(snapshotFilesServiceMock.getConceptMaps())
        .thenReturn(
            singletonList(
                new File("src/test/resources/profiles/ConceptMap/ConceptMap-foobar.json")));
    when(fhirContextMock.newJsonParser()).thenReturn(FhirContext.forR4Cached().newJsonParser());
    ConceptMapPreparationSrv conceptMapPreparationSrv =
        new ConceptMapPreparationSrv(snapshotFilesServiceMock, fhirContextMock);
    conceptMapPreparationSrv.init();
    assertThrows(
        DataNotFoundExcp.class,
        () ->
            conceptMapPreparationSrv.getCode(
                "https://demis.rki.de/fhir/ConceptMap/foobar", "asdf"));
  }
}
