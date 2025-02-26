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
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhir_ui_data_model_translation_service.exception.DataNotFoundExcp;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.Designation;
import de.gematik.demis.fhir_ui_data_model_translation_service.objects.code.displays.AddressUseTOs;
import de.gematik.demis.fhir_ui_data_model_translation_service.utils.SnapshotFilesService;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
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

  private static File addressUseFile;
  private static File LOINCFile;
  private static File emptyLOINCFile;
  private static File notificationCategoryFile;
  private static File geographicRegion;
  private static File laboratoryTestINVPFile;
  private static File methodInvpFile;
  private static File materialInvpFile;
  private static File methodFalseGroup;
  private static File addressUseFalseGroup;
  private static File fileForTestCoverage;
  private static File supplementFile;
  private static File codeSystemWithSupplementedData;
  private static File snomedCodes;
  @Mock private SnapshotFilesService snapshotFilesServiceMock;
  @Mock private Hl7CodeSystemSrv hl7CodeSystemSrvMock;

  @BeforeAll
  static void setup() {
    addressUseFile = new File("src/test/resources/profiles/CodeSystem/CodeSystem-addressUse.json");
    LOINCFile = new File("src/test/resources/profiles/CodeSystem/CodeSystem-loinc-2.74.json");
    emptyLOINCFile = new File("src/test/resources/profiles/CodeSystem/emptyLOINC.json");
    notificationCategoryFile =
        new File("src/test/resources/profiles/CodeSystem/CodeSystem-notificationCategory.json");
    methodFalseGroup = new File("src/test/resources/profiles/CodeSystem/method_FalseGroup.json");
    geographicRegion = new File("src/test/resources/profiles/ValueSet/geographicRegion.json");
    laboratoryTestINVPFile =
        new File("src/test/resources/profiles/ValueSet/ValueSet-laboratoryTestINVP.json");
    materialInvpFile = new File("src/test/resources/profiles/ValueSet/ValueSet-materialINVP.json");
    methodInvpFile = new File("src/test/resources/profiles/ValueSet/ValueSet-methodINVP.json");
    addressUseFalseGroup =
        new File("src/test/resources/profiles/ValueSet/addressUse_FalseGroup.json");
    codeSystemWithSupplementedData =
        new File("src/test/resources/profiles/CodeSystem/CodeSystem-v3-NullFlavor.json");
    supplementFile = new File("src/test/resources/profiles/CodeSystem/translationNullFlavor.json");

    fileForTestCoverage =
        new File("src/test/resources/profiles/ValueSet/addressUse_FalseGroup.json");

    snomedCodes =
        new File("src/test/resources/profiles/CodeSystem/CodeSystem-snomedct-20230331.json");
  }

  @Test
  void testConstructorAndInit_shouldAddCodeSystemAndValueSetData() throws IOException {

    when(snapshotFilesServiceMock.getRawFiles())
        .thenReturn(
            asList(
                addressUseFile,
                LOINCFile,
                notificationCategoryFile,
                geographicRegion,
                laboratoryTestINVPFile,
                methodInvpFile,
                materialInvpFile,
                fileForTestCoverage));

    DataLoaderSrv dataLoaderSrv =
        new DataLoaderSrv(
            "src/test/resources/profiles".replace("/", File.separator),
            snapshotFilesServiceMock,
            FhirContext.forR4(),
            hl7CodeSystemSrvMock);
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

    when(snapshotFilesServiceMock.getRawFiles())
        .thenReturn(
            asList(addressUseFile, methodFalseGroup, addressUseFalseGroup, materialInvpFile));

    DataLoaderSrv dataLoaderSrv =
        new DataLoaderSrv(
            "src/test/resources/profiles".replace("/", File.separator),
            snapshotFilesServiceMock,
            FhirContext.forR4(),
            hl7CodeSystemSrvMock);
    dataLoaderSrv.initialize();

    assertThat(dataLoaderSrv.getCodeSystems()).hasSize(2);
    assertThat(dataLoaderSrv.getValueSet()).hasSize(2);
  }

  @Test
  void testConstructorAndInit_shouldAddCodeSystemAndValueSetDataExtraPathSeperatorAtSourcePath()
      throws IOException {

    when(snapshotFilesServiceMock.getRawFiles())
        .thenReturn(
            asList(
                addressUseFile,
                LOINCFile,
                notificationCategoryFile,
                geographicRegion,
                laboratoryTestINVPFile,
                methodInvpFile,
                materialInvpFile));

    DataLoaderSrv dataLoaderSrv =
        new DataLoaderSrv(
            "src/test/resources/profiles/".replace("/", File.separator),
            snapshotFilesServiceMock,
            FhirContext.forR4(),
            hl7CodeSystemSrvMock);
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

    when(snapshotFilesServiceMock.getRawFiles())
        .thenReturn(Collections.singletonList(addressUseFile));

    DataLoaderSrv dataLoaderSrv =
        new DataLoaderSrv(
            "src/test/resources/profiles".replace("/", File.separator),
            snapshotFilesServiceMock,
            FhirContext.forR4(),
            hl7CodeSystemSrvMock);
    dataLoaderSrv.initialize();

    assertThat(dataLoaderSrv.getCodeSystemData("https://demis.rki.de/fhir/CodeSystem/addressUse"))
        .hasSize(3)
        .containsExactlyInAnyOrderElementsOf(
            asList(AddressUseTOs.current(), AddressUseTOs.ordinary(), AddressUseTOs.primary()));
  }

  @Test
  void shouldThrowExceptionIfCodeIsNotContainedByValueSet() throws IOException {

    when(snapshotFilesServiceMock.getRawFiles())
        .thenReturn(Collections.singletonList(methodInvpFile));

    DataLoaderSrv dataLoaderSrv =
        new DataLoaderSrv(
            "src/test/resources/profiles".replace("/", File.separator),
            snapshotFilesServiceMock,
            FhirContext.forR4(),
            hl7CodeSystemSrvMock);
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

    when(snapshotFilesServiceMock.getRawFiles()).thenReturn(emptyList());

    DataLoaderSrv dataLoaderSrv =
        new DataLoaderSrv(
            "src/test/resources/profiles".replace("/", File.separator),
            snapshotFilesServiceMock,
            FhirContext.forR4(),
            hl7CodeSystemSrvMock);
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

    when(snapshotFilesServiceMock.getRawFiles())
        .thenReturn(asList(emptyLOINCFile, geographicRegion));

    DataLoaderSrv dataLoaderSrv =
        new DataLoaderSrv(
            "src/test/resources/profiles".replace("/", File.separator),
            snapshotFilesServiceMock,
            FhirContext.forR4(),
            hl7CodeSystemSrvMock);
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

    when(snapshotFilesServiceMock.getRawFiles())
        .thenReturn(Collections.singletonList(materialInvpFile));

    DataLoaderSrv dataLoaderSrv =
        new DataLoaderSrv(
            "src/test/resources/profiles".replace("/", File.separator),
            snapshotFilesServiceMock,
            FhirContext.forR4(),
            hl7CodeSystemSrvMock);
    dataLoaderSrv.initialize();

    assertThat(dataLoaderSrv.getValueSetData("https://demis.rki.de/fhir/ValueSet/materialINVP"))
        .hasSize(9)
        .contains(
            CodeDisplay.builder()
                .code("258607008")
                .order(100)
                .display("Bronchoalveolar lavage fluid specimen (specimen)")
                .system("http://snomed.info/sct")
                .designations(
                    Set.of(
                        new Designation("de-DE", "Bronchoalveoläre Lavage"),
                        new Designation(
                            "en-US", "Bronchoalveolar lavage fluid specimen (specimen)")))
                .build());

    assertThatThrownBy(() -> dataLoaderSrv.getValueSetData("raboof"))
        .isInstanceOf(DataNotFoundExcp.class)
        .hasMessage("No data found for %s!", "raboof");
  }

  @Test
  void testConstructorAndInit_shouldAddCodeSystemData() throws IOException {

    when(snapshotFilesServiceMock.getRawFiles())
        .thenReturn(asList(addressUseFile, LOINCFile, notificationCategoryFile));

    DataLoaderSrv dataLoaderSrv =
        new DataLoaderSrv(
            "src/test/resources/profiles".replace("/", File.separator),
            snapshotFilesServiceMock,
            FhirContext.forR4(),
            hl7CodeSystemSrvMock);
    dataLoaderSrv.initialize();

    assertThat(
            dataLoaderSrv.getCodeSystemData(
                "https://demis.rki.de/fhir/CodeSystem/addressUse", "current"))
        .isEqualTo(
            CodeDisplay.builder()
                .code("current")
                .display("Derzeitiger Aufenthaltsort")
                .designations(Set.of(new Designation("en-US", "Current Residence")))
                .system("https://demis.rki.de/fhir/CodeSystem/addressUse")
                .order(100)
                .build());

    assertThat(dataLoaderSrv.getCodeSystemData("http://loinc.org", "100-8"))
        .isEqualTo(
            CodeDisplay.builder()
                .code("100-8")
                .display("Cefoperazone [Susceptibility] by Minimum inhibitory concentration (MIC)")
                .system("http://loinc.org")
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

    when(snapshotFilesServiceMock.getRawFiles())
        .thenReturn(
            asList(geographicRegion, laboratoryTestINVPFile, methodInvpFile, materialInvpFile));

    DataLoaderSrv dataLoaderSrv =
        new DataLoaderSrv(
            "src/test/resources/profiles".replace("/", File.separator),
            snapshotFilesServiceMock,
            FhirContext.forR4(),
            hl7CodeSystemSrvMock);
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
                .order(100)
                .designations(
                    Set.of(
                        new Designation("de-DE", "Bronchoalveoläre Lavage"),
                        new Designation(
                            "en-US", "Bronchoalveolar lavage fluid specimen (specimen)")))
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

    when(snapshotFilesServiceMock.getRawFiles())
        .thenReturn(Collections.singletonList(materialInvpFile));

    DataLoaderSrv dataLoaderSrv =
        new DataLoaderSrv(
            "src/test/resources/profiles".replace("/", File.separator),
            snapshotFilesServiceMock,
            FhirContext.forR4(),
            hl7CodeSystemSrvMock);
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

    CodeDisplay expected =
        CodeDisplay.builder()
            .code("actual")
            .display("actual")
            .system("http://hl7.org/fhir/CodeSystem/condition-ver-status")
            .build();
    when(hl7CodeSystemSrvMock.getFileContent("http://hl7.org/fhir/CodeSystem/condition-ver-status"))
        .thenThrow(new IOException());

    when(snapshotFilesServiceMock.getRawFiles())
        .thenReturn(Collections.singletonList(materialInvpFile));

    DataLoaderSrv dataLoaderSrv =
        new DataLoaderSrv(
            "src/test/resources/profiles".replace("/", File.separator),
            snapshotFilesServiceMock,
            FhirContext.forR4(),
            hl7CodeSystemSrvMock);
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

    when(snapshotFilesServiceMock.getRawFiles())
        .thenReturn(Collections.singletonList(materialInvpFile));

    DataLoaderSrv dataLoaderSrv =
        new DataLoaderSrv(
            "src/test/resources/profiles".replace("/", File.separator),
            snapshotFilesServiceMock,
            FhirContext.forR4(),
            hl7CodeSystemSrvMock);
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

      when(snapshotFilesServiceMock.getRawFiles())
          .thenReturn(asList(codeSystemWithSupplementedData, supplementFile));

      DataLoaderSrv dataLoaderSrv =
          new DataLoaderSrv(
              "src/test/resources/profiles".replace("/", File.separator),
              snapshotFilesServiceMock,
              FhirContext.forR4(),
              hl7CodeSystemSrvMock);
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

      when(snapshotFilesServiceMock.getRawFiles())
          .thenReturn(Collections.singletonList(codeSystemWithSupplementedData));

      DataLoaderSrv dataLoaderSrv =
          new DataLoaderSrv(
              "src/test/resources/profiles".replace("/", File.separator),
              snapshotFilesServiceMock,
              FhirContext.forR4(),
              hl7CodeSystemSrvMock);
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

      when(snapshotFilesServiceMock.getRawFiles())
          .thenReturn(asList(codeSystemWithSupplementedData, supplementFile));

      DataLoaderSrv dataLoaderSrv =
          new DataLoaderSrv(
              "src/test/resources/profiles".replace("/", File.separator),
              snapshotFilesServiceMock,
              FhirContext.forR4(),
              hl7CodeSystemSrvMock);
      dataLoaderSrv.initialize();

      assertThat(
              dataLoaderSrv.getCodeSystemData(
                  "http://terminology.hl7.org/CodeSystem/v3-NullFlavor", "NI"))
          .isEqualTo(
              CodeDisplay.builder()
                  .code("NI")
                  .display("NoInformation")
                  .system("http://terminology.hl7.org/CodeSystem/v3-NullFlavor")
                  .designations(Set.of(new Designation("de", "keine Information")))
                  .order(100)
                  .build());
    }

    @Test
    @DisplayName("should not add supplementary data to single code call")
    void shouldNotAddSupplementaryDataToSingleCodeCall() throws IOException {

      when(snapshotFilesServiceMock.getRawFiles())
          .thenReturn(Collections.singletonList(codeSystemWithSupplementedData));

      DataLoaderSrv dataLoaderSrv =
          new DataLoaderSrv(
              "src/test/resources/profiles".replace("/", File.separator),
              snapshotFilesServiceMock,
              FhirContext.forR4(),
              hl7CodeSystemSrvMock);
      dataLoaderSrv.initialize();

      assertThat(
              dataLoaderSrv.getCodeSystemData(
                  "http://terminology.hl7.org/CodeSystem/v3-NullFlavor", "NI"))
          .isEqualTo(
              CodeDisplay.builder()
                  .code("NI")
                  .display("NoInformation")
                  .system("http://terminology.hl7.org/CodeSystem/v3-NullFlavor")
                  .order(100)
                  .build());
    }
  }

  @Nested
  @DisplayName("test for additional designations from value sets")
  class TestForDesignationsFromValueSets {

    @Test
    void shouldAddDesignationFromValueSetIfGiven() throws IOException {

      when(snapshotFilesServiceMock.getRawFiles())
          .thenReturn(asList(snomedCodes, materialInvpFile));

      DataLoaderSrv dataLoaderSrv =
          new DataLoaderSrv(
              "src/test/resources/profiles".replace("/", File.separator),
              snapshotFilesServiceMock,
              FhirContext.forR4(),
              hl7CodeSystemSrvMock);
      dataLoaderSrv.initialize();

      assertThat(
              dataLoaderSrv
                  .getCodeSystemData("http://snomed.info/sct", "258607008")
                  .getDesignations())
          .contains(new Designation("de-DE", "Bronchoalveoläre Lavage"));

      assertThat(dataLoaderSrv.getCodeSystemData("http://snomed.info/sct"))
          .contains(
              CodeDisplay.builder()
                  .code("258607008")
                  .display("Bronchoalveolar lavage fluid specimen (specimen)")
                  .designations(
                      Set.of(
                          new Designation(
                              "en-US", "Bronchoalveolar lavage fluid specimen (specimen)"),
                          new Designation("de-DE", "Bronchoalveoläre Lavage")))
                  .system("http://snomed.info/sct")
                  .order(100)
                  .build());
    }

    @Test
    void shouldHandleNoCodeSystemForValueSet() throws IOException {

      when(snapshotFilesServiceMock.getRawFiles())
          .thenReturn(Collections.singletonList(materialInvpFile));

      DataLoaderSrv dataLoaderSrv =
          new DataLoaderSrv(
              "src/test/resources/profiles".replace("/", File.separator),
              snapshotFilesServiceMock,
              FhirContext.forR4(),
              hl7CodeSystemSrvMock);
      dataLoaderSrv.initialize();

      assertThat(dataLoaderSrv.getValueSet())
          .contains("https://demis.rki.de/fhir/ValueSet/materialINVP");
    }

    @Test
    void shouldHandleCodeNotInCodeSystem() throws IOException {

      var emptySnomedCodes =
          new File("src/test/resources/profiles/CodeSystem/CodeSystem-snomedct-empty.json");

      when(snapshotFilesServiceMock.getRawFiles())
          .thenReturn(asList(emptySnomedCodes, materialInvpFile));

      DataLoaderSrv dataLoaderSrv =
          new DataLoaderSrv(
              "src/test/resources/profiles".replace("/", File.separator),
              snapshotFilesServiceMock,
              FhirContext.forR4(),
              hl7CodeSystemSrvMock);
      dataLoaderSrv.initialize();

      assertThat(dataLoaderSrv.getValueSet())
          .contains("https://demis.rki.de/fhir/ValueSet/materialINVP");
    }
  }
}
