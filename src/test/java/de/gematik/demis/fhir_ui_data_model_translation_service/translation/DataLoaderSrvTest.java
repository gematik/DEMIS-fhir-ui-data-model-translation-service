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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhir_ui_data_model_translation_service.FeatureFlags;
import de.gematik.demis.fhir_ui_data_model_translation_service.exception.DataNotFoundExcp;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.Designation;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.Use;
import de.gematik.demis.fhir_ui_data_model_translation_service.objects.code.displays.AddressUseTOs;
import de.gematik.demis.fhir_ui_data_model_translation_service.utils.SnapshotFilesService;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DataLoaderSrvTest {

  private static final FeatureFlags FEATURE_FLAGS_ENABLED =
      FeatureFlags.builder().addCodeDisplayVersion(true).build();

  private static File addressUseFileCS;
  private static File loincFileCS;
  private static File emptyLoincFileCS;
  private static File notificationCategoryFileCS;
  private static File geographicRegionVS;
  private static File laboratoryTestINVPFileVS;
  private static File methodInvpFileVS;
  private static File materialInvpFileVS;
  private static File methodFalseGroupCS;
  private static File addressUseFalseGroupVS;
  private static File fileForTestCoverageVS;
  private static File supplementFileCS;
  private static File codeSystemWithSupplementedDataCS;
  private static File snomedCodesCS;
  @Mock private SnapshotFilesService snapshotFilesServiceMock;
  @Mock private Hl7CodeSystemSrv hl7CodeSystemSrvMock;

  @BeforeAll
  static void setup() {
    addressUseFileCS =
        new File("src/test/resources/profiles/CodeSystem/CodeSystem-addressUse.json");
    loincFileCS = new File("src/test/resources/profiles/CodeSystem/CodeSystem-loinc-2.74.json");
    emptyLoincFileCS =
        new File("src/test/resources/profiles/CodeSystem/CodeSystem-emptyLOINC.json");
    notificationCategoryFileCS =
        new File("src/test/resources/profiles/CodeSystem/CodeSystem-notificationCategory.json");
    methodFalseGroupCS =
        new File("src/test/resources/profiles/CodeSystem/CodeSystem-method_FalseGroup.json");
    geographicRegionVS =
        new File("src/test/resources/profiles/ValueSet/ValueSet-geographicRegion.json");
    laboratoryTestINVPFileVS =
        new File("src/test/resources/profiles/ValueSet/ValueSet-laboratoryTestINVP.json");
    materialInvpFileVS =
        new File("src/test/resources/profiles/ValueSet/ValueSet-materialINVP.json");
    methodInvpFileVS = new File("src/test/resources/profiles/ValueSet/ValueSet-methodINVP.json");
    addressUseFalseGroupVS =
        new File("src/test/resources/profiles/ValueSet/ValueSet-addressUse_FalseGroup.json");
    codeSystemWithSupplementedDataCS =
        new File("src/test/resources/profiles/CodeSystem/CodeSystem-v3-NullFlavor.json");
    supplementFileCS =
        new File("src/test/resources/profiles/CodeSystem/CodeSystem-translationNullFlavor.json");

    fileForTestCoverageVS =
        new File("src/test/resources/profiles/ValueSet/ValueSet-addressUse_FalseGroup.json");

    snomedCodesCS =
        new File("src/test/resources/profiles/CodeSystem/CodeSystem-snomedct-20230331.json");
  }

  @Test
  void testConstructorAndInit_shouldAddCodeSystemAndValueSetData() throws IOException {

    when(snapshotFilesServiceMock.getCodeSystemFiles())
        .thenReturn(List.of(addressUseFileCS, loincFileCS, notificationCategoryFileCS));
    when(snapshotFilesServiceMock.getValueSetFiles())
        .thenReturn(
            List.of(
                geographicRegionVS,
                laboratoryTestINVPFileVS,
                methodInvpFileVS,
                materialInvpFileVS,
                fileForTestCoverageVS));

    DataLoaderSrv dataLoaderSrv =
        new DataLoaderSrv(
            snapshotFilesServiceMock,
            FhirContext.forR4Cached(),
            hl7CodeSystemSrvMock,
            FEATURE_FLAGS_ENABLED);
    dataLoaderSrv.initialize();

    assertThat(dataLoaderSrv.getCodeSystems())
        .hasSize(6)
        .containsExactlyInAnyOrder(
            "https://demis.rki.de/fhir/CodeSystem/addressUse",
            "https://demis.rki.de/fhir/CodeSystem/addressUse|1.1.0",
            "http://loinc.org",
            "http://loinc.org|2.74",
            "https://demis.rki.de/fhir/CodeSystem/notificationCategory",
            "https://demis.rki.de/fhir/CodeSystem/notificationCategory|2.0.0");

    assertThat(dataLoaderSrv.getValueSet())
        .hasSize(8)
        .containsExactlyInAnyOrder(
            "https://demis.rki.de/fhir/ValueSet/geographicRegion",
            "https://demis.rki.de/fhir/ValueSet/geographicRegion|1.0.0",
            "https://demis.rki.de/fhir/ValueSet/laboratoryTestINVP",
            "https://demis.rki.de/fhir/ValueSet/laboratoryTestINVP|1.6.0",
            "https://demis.rki.de/fhir/ValueSet/materialINVP",
            "https://demis.rki.de/fhir/ValueSet/materialINVP|1.5.0",
            "https://demis.rki.de/fhir/ValueSet/methodINVP",
            "https://demis.rki.de/fhir/ValueSet/methodINVP|1.0.0");

    CodeDisplay codeDisplay = dataLoaderSrv.getCodeSystemData("http://loinc.org", "100343-3");
    assertThat(codeDisplay).isNotNull();
    assertThat(codeDisplay.getDesignations())
        .as("code display has at least 1 designation")
        .isNotEmpty();
  }

  @Test
  void testConstructorAndInit_NoExceptionForWronglyPutFiles() throws IOException {

    when(snapshotFilesServiceMock.getCodeSystemFiles())
        .thenReturn(List.of(addressUseFileCS, methodFalseGroupCS));
    when(snapshotFilesServiceMock.getValueSetFiles())
        .thenReturn(List.of(addressUseFalseGroupVS, materialInvpFileVS));

    DataLoaderSrv dataLoaderSrv =
        new DataLoaderSrv(
            snapshotFilesServiceMock,
            FhirContext.forR4Cached(),
            hl7CodeSystemSrvMock,
            FEATURE_FLAGS_ENABLED);
    dataLoaderSrv.initialize();

    assertThat(dataLoaderSrv.getCodeSystems()).hasSize(2);
    assertThat(dataLoaderSrv.getValueSet()).hasSize(2);
  }

  @Test
  void testConstructorAndInit_shouldAddCodeSystemAndValueSetDataExtraPathSeperatorAtSourcePath()
      throws IOException {

    when(snapshotFilesServiceMock.getCodeSystemFiles())
        .thenReturn(List.of(addressUseFileCS, loincFileCS, notificationCategoryFileCS));
    when(snapshotFilesServiceMock.getValueSetFiles())
        .thenReturn(
            List.of(
                geographicRegionVS,
                laboratoryTestINVPFileVS,
                methodInvpFileVS,
                materialInvpFileVS));

    DataLoaderSrv dataLoaderSrv =
        new DataLoaderSrv(
            snapshotFilesServiceMock,
            FhirContext.forR4Cached(),
            hl7CodeSystemSrvMock,
            FEATURE_FLAGS_ENABLED);
    dataLoaderSrv.initialize();

    assertThat(dataLoaderSrv.getCodeSystems())
        .hasSize(6)
        .containsExactlyInAnyOrder(
            "https://demis.rki.de/fhir/CodeSystem/addressUse",
            "https://demis.rki.de/fhir/CodeSystem/addressUse|1.1.0",
            "http://loinc.org",
            "http://loinc.org|2.74",
            "https://demis.rki.de/fhir/CodeSystem/notificationCategory",
            "https://demis.rki.de/fhir/CodeSystem/notificationCategory|2.0.0");

    assertThat(dataLoaderSrv.getValueSet())
        .hasSize(8)
        .containsExactlyInAnyOrder(
            "https://demis.rki.de/fhir/ValueSet/geographicRegion",
            "https://demis.rki.de/fhir/ValueSet/geographicRegion|1.0.0",
            "https://demis.rki.de/fhir/ValueSet/laboratoryTestINVP",
            "https://demis.rki.de/fhir/ValueSet/laboratoryTestINVP|1.6.0",
            "https://demis.rki.de/fhir/ValueSet/materialINVP",
            "https://demis.rki.de/fhir/ValueSet/materialINVP|1.5.0",
            "https://demis.rki.de/fhir/ValueSet/methodINVP",
            "https://demis.rki.de/fhir/ValueSet/methodINVP|1.0.0");
  }

  @Test
  void testGroupGetters_shouldReturnDataForOneCodeSystem() throws IOException {

    when(snapshotFilesServiceMock.getCodeSystemFiles()).thenReturn(List.of(addressUseFileCS));
    when(snapshotFilesServiceMock.getValueSetFiles()).thenReturn(List.of());

    DataLoaderSrv dataLoaderSrv =
        new DataLoaderSrv(
            snapshotFilesServiceMock,
            FhirContext.forR4Cached(),
            hl7CodeSystemSrvMock,
            FEATURE_FLAGS_ENABLED);
    dataLoaderSrv.initialize();

    final List<CodeDisplay> actualCodes =
        dataLoaderSrv.getCodeSystemData("https://demis.rki.de/fhir/CodeSystem/addressUse");
    assertThat(actualCodes)
        .hasSize(3)
        .containsExactlyInAnyOrderElementsOf(
            List.of(AddressUseTOs.current(), AddressUseTOs.ordinary(), AddressUseTOs.primary()));
  }

  @Test
  void shouldThrowExceptionIfCodeIsNotContainedByValueSet() throws IOException {

    when(snapshotFilesServiceMock.getCodeSystemFiles()).thenReturn(List.of());
    when(snapshotFilesServiceMock.getValueSetFiles()).thenReturn(List.of(methodInvpFileVS));

    DataLoaderSrv dataLoaderSrv =
        new DataLoaderSrv(
            snapshotFilesServiceMock,
            FhirContext.forR4Cached(),
            hl7CodeSystemSrvMock,
            FEATURE_FLAGS_ENABLED);
    dataLoaderSrv.initialize();

    assertThatThrownBy(
            () ->
                dataLoaderSrv.getValueSetData(
                    "https://demis.rki.de/fhir/ValueSet/methodINVP", "0000"))
        .isInstanceOf(DataNotFoundExcp.class)
        .hasMessage("the code 0000 was not found in https://demis.rki.de/fhir/ValueSet/methodINVP");
  }

  @Test
  void testExceptionForMissingData() throws IOException {

    when(snapshotFilesServiceMock.getCodeSystemFiles()).thenReturn(List.of());
    when(snapshotFilesServiceMock.getValueSetFiles()).thenReturn(List.of());

    DataLoaderSrv dataLoaderSrv =
        new DataLoaderSrv(
            snapshotFilesServiceMock,
            FhirContext.forR4Cached(),
            hl7CodeSystemSrvMock,
            FEATURE_FLAGS_ENABLED);
    dataLoaderSrv.initialize();

    assertThatThrownBy(() -> dataLoaderSrv.getCodeSystemData("foobar"))
        .isInstanceOf(DataNotFoundExcp.class)
        .hasMessage("the system foobar is unknown");

    assertThatThrownBy(
            () ->
                dataLoaderSrv.getValueSetData(
                    "https://demis.rki.de/fhir/CodeSystem/geographicRegion"))
        .isInstanceOf(DataNotFoundExcp.class)
        .hasMessage("No data found for https://demis.rki.de/fhir/CodeSystem/geographicRegion!");
  }

  @Test
  void testExceptionForNoData() throws IOException {

    when(snapshotFilesServiceMock.getCodeSystemFiles()).thenReturn(List.of(emptyLoincFileCS));
    when(snapshotFilesServiceMock.getValueSetFiles()).thenReturn(List.of(geographicRegionVS));

    DataLoaderSrv dataLoaderSrv =
        new DataLoaderSrv(
            snapshotFilesServiceMock,
            FhirContext.forR4Cached(),
            hl7CodeSystemSrvMock,
            FEATURE_FLAGS_ENABLED);
    dataLoaderSrv.initialize();

    assertThatThrownBy(() -> dataLoaderSrv.getCodeSystemData("foobar"))
        .isInstanceOf(DataNotFoundExcp.class)
        .hasMessage("the system foobar is unknown");

    assertThatThrownBy(
            () ->
                dataLoaderSrv.getValueSetData(
                    "https://demis.rki.de/fhir/CodeSystem/geographicRegion"))
        .isInstanceOf(DataNotFoundExcp.class)
        .hasMessage("No data found for https://demis.rki.de/fhir/CodeSystem/geographicRegion!");
  }

  @Test
  void testGroupGetters_shouldReturnDataForOneValueSet() throws IOException {

    when(snapshotFilesServiceMock.getCodeSystemFiles()).thenReturn(List.of());
    when(snapshotFilesServiceMock.getValueSetFiles()).thenReturn(List.of(materialInvpFileVS));

    DataLoaderSrv dataLoaderSrv =
        new DataLoaderSrv(
            snapshotFilesServiceMock,
            FhirContext.forR4Cached(),
            hl7CodeSystemSrvMock,
            FEATURE_FLAGS_ENABLED);
    dataLoaderSrv.initialize();

    assertThat(dataLoaderSrv.getValueSetData("https://demis.rki.de/fhir/ValueSet/materialINVP"))
        .hasSize(9)
        .contains(
            CodeDisplay.builder()
                .code("258607008")
                .order(100)
                .display("Bronchoalveolar lavage fluid specimen (specimen)")
                .system("http://snomed.info/sct")
                .version("http://snomed.info/sct/900000000000207008/version/20230331")
                .designations(
                    Set.of(
                        new Designation("de-DE", "Bronchoalveoläre Lavage", new Use(null, null)),
                        new Designation(
                            "en-US",
                            "Bronchoalveolar lavage fluid specimen (specimen)",
                            new Use("http://snomed.info/sct", "900000000000003001"))))
                .build());

    assertThatThrownBy(() -> dataLoaderSrv.getValueSetData("raboof"))
        .isInstanceOf(DataNotFoundExcp.class)
        .hasMessage("No data found for %s!", "raboof");
  }

  @Test
  void testConstructorAndInit_shouldAddCodeSystemData() throws IOException {

    when(snapshotFilesServiceMock.getCodeSystemFiles())
        .thenReturn(List.of(addressUseFileCS, loincFileCS, notificationCategoryFileCS));
    when(snapshotFilesServiceMock.getValueSetFiles()).thenReturn(List.of());

    DataLoaderSrv dataLoaderSrv =
        new DataLoaderSrv(
            snapshotFilesServiceMock,
            FhirContext.forR4Cached(),
            hl7CodeSystemSrvMock,
            FEATURE_FLAGS_ENABLED);
    dataLoaderSrv.initialize();

    assertThat(
            dataLoaderSrv.getCodeSystemData(
                "https://demis.rki.de/fhir/CodeSystem/addressUse", "current"))
        .isEqualTo(
            CodeDisplay.builder()
                .code("current")
                .display("Derzeitiger Aufenthaltsort")
                .designations(
                    Set.of(
                        new Designation(
                            "en-US",
                            "Current Residence",
                            new Use("http://snomed.info/sct", "900000000000003001"))))
                .system("https://demis.rki.de/fhir/CodeSystem/addressUse")
                .version("1.1.0")
                .order(100)
                .build());

    assertThat(dataLoaderSrv.getCodeSystemData("http://loinc.org", "100-8"))
        .isEqualTo(
            CodeDisplay.builder()
                .code("100-8")
                .display("Cefoperazone [Susceptibility] by Minimum inhibitory concentration (MIC)")
                .system("http://loinc.org")
                .version("2.74")
                .order(100)
                .build());
    assertThat(
            dataLoaderSrv.getCodeSystemData(
                "https://demis.rki.de/fhir/CodeSystem/notificationCategory", "advp"))
        .isEqualTo(
            CodeDisplay.builder()
                .code("advp")
                .display(
                    "Adenoviren; Meldepflicht nur für den direkten Nachweis im Konjunktivalabstrich")
                .system("https://demis.rki.de/fhir/CodeSystem/notificationCategory")
                .version("2.0.0")
                .designations(Set.of(new Designation("de-DE", "Adenoviren, Konjunktivalabstrich")))
                .order(100)
                .build());

    assertThatThrownBy(
            () ->
                dataLoaderSrv.getCodeSystemData(
                    "https://demis.rki.de/fhir/CodeSystem/notificationCategory", "raboof"))
        .isInstanceOf(DataNotFoundExcp.class)
        .hasMessage(
            "the code %s was not found in %s",
            "raboof", "https://demis.rki.de/fhir/CodeSystem/notificationCategory");

    assertThatThrownBy(() -> dataLoaderSrv.getCodeSystemData("raboof", "raboof"))
        .isInstanceOf(DataNotFoundExcp.class)
        .hasMessage("the system %s is unknown", "raboof");
  }

  @Test
  void testConstructorAndInit_shouldAddValueSetData() throws IOException {

    when(snapshotFilesServiceMock.getCodeSystemFiles()).thenReturn(List.of());
    when(snapshotFilesServiceMock.getValueSetFiles())
        .thenReturn(
            List.of(
                geographicRegionVS,
                laboratoryTestINVPFileVS,
                methodInvpFileVS,
                materialInvpFileVS));

    DataLoaderSrv dataLoaderSrv =
        new DataLoaderSrv(
            snapshotFilesServiceMock,
            FhirContext.forR4Cached(),
            hl7CodeSystemSrvMock,
            FEATURE_FLAGS_ENABLED);
    dataLoaderSrv.initialize();

    List<CodeDisplay> valueSetDataWithoutUrl =
        dataLoaderSrv.getValueSetData("https://demis.rki.de/fhir/ValueSet/materialINVP");
    List<CodeDisplay> valueSetDataWithUrl =
        dataLoaderSrv.getValueSetData("https://demis.rki.de/fhir/ValueSet/materialINVP|1.5.0");

    assertThat(valueSetDataWithoutUrl).isEqualTo(valueSetDataWithUrl);

    CodeDisplay valueSetCodeWithoutUrl =
        dataLoaderSrv.getValueSetData(
            "https://demis.rki.de/fhir/ValueSet/materialINVP", "258607008");

    CodeDisplay valueSetCodeWithUrl =
        dataLoaderSrv.getValueSetData(
            "https://demis.rki.de/fhir/ValueSet/materialINVP|1.5.0", "258607008");

    assertThat(valueSetCodeWithoutUrl).isEqualTo(valueSetCodeWithUrl);
    assertThat(valueSetCodeWithoutUrl)
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .isEqualTo(
            CodeDisplay.builder()
                .code("258607008")
                .display("Bronchoalveolar lavage fluid specimen (specimen)")
                .system("http://snomed.info/sct")
                .version("http://snomed.info/sct/900000000000207008/version/20230331")
                .order(100)
                .designations(
                    Set.of(
                        new Designation("de-DE", "Bronchoalveoläre Lavage", new Use(null, null)),
                        new Designation(
                            "en-US",
                            "Bronchoalveolar lavage fluid specimen (specimen)",
                            new Use("http://snomed.info/sct", "900000000000003001"))))
                .build());
    assertThat(
            dataLoaderSrv.getValueSetData(
                "https://demis.rki.de/fhir/ValueSet/laboratoryTestINVP", "101424-0"))
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .isEqualTo(
            CodeDisplay.builder()
                .code("101424-0")
                .display(
                    "Influenza virus A H3 RNA [Presence] in Respiratory specimen by NAA with probe detection")
                .system("http://loinc.org")
                .version("2.74")
                .order(100)
                .build());
  }

  @Test
  void shouldHandleMissingSystemWithLookingForStandardCodeSystemForSpecificCode()
      throws IOException {
    when(hl7CodeSystemSrvMock.containsContent(
            "http://hl7.org/fhir/CodeSystem/condition-ver-status"))
        .thenReturn(true);

    CodeDisplay expected =
        CodeDisplay.builder()
            .code("actual")
            .display("actual")
            .system("http://hl7.org/fhir/CodeSystem/condition-ver-status")
            .build();
    when(hl7CodeSystemSrvMock.getFileContent("http://hl7.org/fhir/CodeSystem/condition-ver-status"))
        .thenReturn(
            new CodeDisplayMapWithVersion(
                "http://hl7.org/fhir/CodeSystem/condition-ver-status", Map.of("actual", expected)));

    when(snapshotFilesServiceMock.getCodeSystemFiles()).thenReturn(List.of(materialInvpFileVS));
    when(snapshotFilesServiceMock.getValueSetFiles()).thenReturn(List.of());

    DataLoaderSrv dataLoaderSrv =
        new DataLoaderSrv(
            snapshotFilesServiceMock,
            FhirContext.forR4Cached(),
            hl7CodeSystemSrvMock,
            FEATURE_FLAGS_ENABLED);
    dataLoaderSrv.initialize();

    CodeDisplay actual =
        dataLoaderSrv.getCodeSystemData(
            "http://hl7.org/fhir/CodeSystem/condition-ver-status", "actual");

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void shouldHandleExceptionWhenProcessingStandardCodeSystemFile() throws IOException {
    when(hl7CodeSystemSrvMock.containsContent(
            "http://hl7.org/fhir/CodeSystem/condition-ver-status"))
        .thenReturn(true);

    when(hl7CodeSystemSrvMock.getFileContent("http://hl7.org/fhir/CodeSystem/condition-ver-status"))
        .thenThrow(new IOException());

    when(snapshotFilesServiceMock.getCodeSystemFiles()).thenReturn(List.of(materialInvpFileVS));
    when(snapshotFilesServiceMock.getValueSetFiles()).thenReturn(List.of());

    DataLoaderSrv dataLoaderSrv =
        new DataLoaderSrv(
            snapshotFilesServiceMock,
            FhirContext.forR4Cached(),
            hl7CodeSystemSrvMock,
            FEATURE_FLAGS_ENABLED);
    dataLoaderSrv.initialize();

    assertThatThrownBy(
            () ->
                dataLoaderSrv.getCodeSystemData(
                    "http://hl7.org/fhir/CodeSystem/condition-ver-status", "actual"))
        .isInstanceOf(DataNotFoundExcp.class)
        .hasMessage(
            "the system %s is unknown", "http://hl7.org/fhir/CodeSystem/condition-ver-status");
  }

  @Test
  void shouldHandleMissingSystemWithLookingForStandardCodeSystem() throws IOException {
    when(hl7CodeSystemSrvMock.containsContent(
            "http://hl7.org/fhir/CodeSystem/condition-ver-status"))
        .thenReturn(true);

    CodeDisplay mapEntry =
        CodeDisplay.builder()
            .code("actual")
            .display("actual")
            .system("http://hl7.org/fhir/CodeSystem/condition-ver-status")
            .build();
    Map<String, CodeDisplay> expectedMap = Map.of("actual", mapEntry);
    when(hl7CodeSystemSrvMock.getFileContent("http://hl7.org/fhir/CodeSystem/condition-ver-status"))
        .thenReturn(
            new CodeDisplayMapWithVersion(
                "http://hl7.org/fhir/CodeSystem/condition-ver-status", expectedMap));

    when(snapshotFilesServiceMock.getCodeSystemFiles()).thenReturn(List.of());
    when(snapshotFilesServiceMock.getValueSetFiles()).thenReturn(List.of(materialInvpFileVS));

    DataLoaderSrv dataLoaderSrv =
        new DataLoaderSrv(
            snapshotFilesServiceMock,
            FhirContext.forR4Cached(),
            hl7CodeSystemSrvMock,
            FEATURE_FLAGS_ENABLED);
    dataLoaderSrv.initialize();

    List<CodeDisplay> actual =
        dataLoaderSrv.getCodeSystemData("http://hl7.org/fhir/CodeSystem/condition-ver-status");

    assertThat(actual).isEqualTo(expectedMap.values().stream().toList());
  }

  @Nested
  @DisplayName("Test for supplementary data")
  class SupplementDataTest {

    @Test
    @DisplayName("should add supplementary data to code system")
    void shouldAddSupplementaryDataToCodeSystem() throws IOException {

      when(snapshotFilesServiceMock.getCodeSystemFiles())
          .thenReturn(List.of(codeSystemWithSupplementedDataCS, supplementFileCS));
      when(snapshotFilesServiceMock.getValueSetFiles()).thenReturn(List.of());

      DataLoaderSrv dataLoaderSrv =
          new DataLoaderSrv(
              snapshotFilesServiceMock,
              FhirContext.forR4Cached(),
              hl7CodeSystemSrvMock,
              FEATURE_FLAGS_ENABLED);
      dataLoaderSrv.initialize();

      assertThat(
              dataLoaderSrv.getCodeSystemData(
                  "http://terminology.hl7.org/CodeSystem/v3-NullFlavor"))
          .flatExtracting(CodeDisplay::getDesignations)
          .extracting("language")
          .contains("de");
    }

    @Test
    @DisplayName("should not add supplementary data to code system")
    void shouldNotAddSupplementaryDataToCodeSystem() throws IOException {

      when(snapshotFilesServiceMock.getCodeSystemFiles())
          .thenReturn(List.of(codeSystemWithSupplementedDataCS));
      when(snapshotFilesServiceMock.getValueSetFiles()).thenReturn(List.of());

      DataLoaderSrv dataLoaderSrv =
          new DataLoaderSrv(
              snapshotFilesServiceMock,
              FhirContext.forR4Cached(),
              hl7CodeSystemSrvMock,
              FEATURE_FLAGS_ENABLED);
      dataLoaderSrv.initialize();

      assertThat(
              dataLoaderSrv.getCodeSystemData(
                  "http://terminology.hl7.org/CodeSystem/v3-NullFlavor"))
          .flatExtracting(CodeDisplay::getDesignations)
          .extracting("language")
          .doesNotContain("de");
    }

    @Test
    @DisplayName("should add supplementary data to single code call")
    void shouldAddSupplementaryDataToSingleCodeCall() throws IOException {

      when(snapshotFilesServiceMock.getCodeSystemFiles())
          .thenReturn(List.of(codeSystemWithSupplementedDataCS, supplementFileCS));
      when(snapshotFilesServiceMock.getValueSetFiles()).thenReturn(List.of());

      DataLoaderSrv dataLoaderSrv =
          new DataLoaderSrv(
              snapshotFilesServiceMock,
              FhirContext.forR4Cached(),
              hl7CodeSystemSrvMock,
              FEATURE_FLAGS_ENABLED);
      dataLoaderSrv.initialize();

      assertThat(
              dataLoaderSrv.getCodeSystemData(
                  "http://terminology.hl7.org/CodeSystem/v3-NullFlavor", "NI"))
          .isEqualTo(
              CodeDisplay.builder()
                  .code("NI")
                  .display("NoInformation")
                  .system("http://terminology.hl7.org/CodeSystem/v3-NullFlavor")
                  .version("2018-08-12")
                  .designations(Set.of(new Designation("de", "keine Information")))
                  .order(100)
                  .build());
    }

    @Test
    @DisplayName("should not add supplementary data to single code call")
    void shouldNotAddSupplementaryDataToSingleCodeCall() throws IOException {

      when(snapshotFilesServiceMock.getCodeSystemFiles())
          .thenReturn(List.of(codeSystemWithSupplementedDataCS));
      when(snapshotFilesServiceMock.getValueSetFiles()).thenReturn(List.of());

      DataLoaderSrv dataLoaderSrv =
          new DataLoaderSrv(
              snapshotFilesServiceMock,
              FhirContext.forR4Cached(),
              hl7CodeSystemSrvMock,
              FEATURE_FLAGS_ENABLED);
      dataLoaderSrv.initialize();

      assertThat(
              dataLoaderSrv.getCodeSystemData(
                  "http://terminology.hl7.org/CodeSystem/v3-NullFlavor", "NI"))
          .isEqualTo(
              CodeDisplay.builder()
                  .code("NI")
                  .display("NoInformation")
                  .system("http://terminology.hl7.org/CodeSystem/v3-NullFlavor")
                  .version("2018-08-12")
                  .order(100)
                  .build());
    }
  }

  @Nested
  @DisplayName("test for additional designations from value sets")
  class TestForDesignationsFromValueSets {

    @Test
    void shouldAddDesignationFromValueSetIfGiven() throws IOException {

      when(snapshotFilesServiceMock.getCodeSystemFiles()).thenReturn(List.of(snomedCodesCS));
      when(snapshotFilesServiceMock.getValueSetFiles()).thenReturn(List.of(materialInvpFileVS));

      DataLoaderSrv dataLoaderSrv =
          new DataLoaderSrv(
              snapshotFilesServiceMock,
              FhirContext.forR4Cached(),
              hl7CodeSystemSrvMock,
              FEATURE_FLAGS_ENABLED);
      dataLoaderSrv.initialize();

      assertThat(
              dataLoaderSrv
                  .getCodeSystemData("http://snomed.info/sct", "258607008")
                  .getDesignations())
          .contains(new Designation("de-DE", "Bronchoalveoläre Lavage", new Use(null, null)));

      assertThat(dataLoaderSrv.getCodeSystemData("http://snomed.info/sct"))
          .contains(
              CodeDisplay.builder()
                  .code("258607008")
                  .display("Bronchoalveolar lavage fluid specimen (specimen)")
                  .designations(
                      Set.of(
                          new Designation(
                              "en-US",
                              "Bronchoalveolar lavage fluid specimen (specimen)",
                              new Use("http://snomed.info/sct", "900000000000003001")),
                          new Designation("de-DE", "Bronchoalveoläre Lavage", new Use(null, null))))
                  .system("http://snomed.info/sct")
                  .version("http://snomed.info/sct/900000000000207008/version/20230331")
                  .order(100)
                  .build());
    }

    @Test
    void shouldHandleNoCodeSystemForValueSet() throws IOException {

      when(snapshotFilesServiceMock.getCodeSystemFiles()).thenReturn(List.of());
      when(snapshotFilesServiceMock.getValueSetFiles()).thenReturn(List.of(materialInvpFileVS));

      DataLoaderSrv dataLoaderSrv =
          new DataLoaderSrv(
              snapshotFilesServiceMock,
              FhirContext.forR4Cached(),
              hl7CodeSystemSrvMock,
              FEATURE_FLAGS_ENABLED);
      dataLoaderSrv.initialize();

      assertThat(dataLoaderSrv.getValueSet())
          .contains("https://demis.rki.de/fhir/ValueSet/materialINVP");
    }

    @Test
    void shouldHandleCodeNotInCodeSystem() throws IOException {

      var emptySnomedCodes =
          new File("src/test/resources/profiles/CodeSystem/CodeSystem-snomedct-empty.json");

      when(snapshotFilesServiceMock.getCodeSystemFiles()).thenReturn(List.of(emptySnomedCodes));
      when(snapshotFilesServiceMock.getValueSetFiles()).thenReturn(List.of(materialInvpFileVS));

      DataLoaderSrv dataLoaderSrv =
          new DataLoaderSrv(
              snapshotFilesServiceMock,
              FhirContext.forR4Cached(),
              hl7CodeSystemSrvMock,
              FEATURE_FLAGS_ENABLED);
      dataLoaderSrv.initialize();

      assertThat(dataLoaderSrv.getValueSet())
          .contains("https://demis.rki.de/fhir/ValueSet/materialINVP");
    }
  }

  @Test
  void shouldReturnVersionFromSnomed() throws IOException {
    when(snapshotFilesServiceMock.getCodeSystemFiles()).thenReturn(List.of(snomedCodesCS));

    DataLoaderSrv dataLoaderSrv =
        new DataLoaderSrv(
            snapshotFilesServiceMock,
            FhirContext.forR4Cached(),
            hl7CodeSystemSrvMock,
            FEATURE_FLAGS_ENABLED);
    dataLoaderSrv.initialize();

    assertThat(dataLoaderSrv.getVersion("http://snomed.info/sct"))
        .isNotNull()
        .isEqualTo("http://snomed.info/sct/900000000000207008/version/20230331");
  }

  @Test
  void shouldReturnExceptionWhenCodeSystemNotPartOfData() throws IOException {
    when(snapshotFilesServiceMock.getCodeSystemFiles()).thenReturn(List.of(loincFileCS));

    DataLoaderSrv dataLoaderSrv =
        new DataLoaderSrv(
            snapshotFilesServiceMock,
            FhirContext.forR4Cached(),
            hl7CodeSystemSrvMock,
            FEATURE_FLAGS_ENABLED);
    dataLoaderSrv.initialize();

    assertThatThrownBy(() -> dataLoaderSrv.getVersion("http://snomed.info/sct"))
        .isInstanceOf(DataNotFoundExcp.class)
        .hasMessage(
            "the version of the code system http://snomed.info/sct was not added to the version map");
  }

  @Test
  void shouldReturnExceptionForNoCodeSystem() throws IOException {
    when(snapshotFilesServiceMock.getCodeSystemFiles()).thenReturn(List.of());

    DataLoaderSrv dataLoaderSrv =
        new DataLoaderSrv(
            snapshotFilesServiceMock,
            FhirContext.forR4Cached(),
            hl7CodeSystemSrvMock,
            FEATURE_FLAGS_ENABLED);
    dataLoaderSrv.initialize();

    assertThatThrownBy(() -> dataLoaderSrv.getVersion("http://snomed.info/sct"))
        .isInstanceOf(DataNotFoundExcp.class)
        .hasMessage("the map for code systems was not initialized");
  }
}
