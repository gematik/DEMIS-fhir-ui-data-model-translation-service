package de.gematik.demis.fhir_ui_data_model_translation_service.laboratory;

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
import static org.mockito.Mockito.when;

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.Designation;
import de.gematik.demis.fhir_ui_data_model_translation_service.utils.SnapshotFilesService;
import java.io.File;
import java.util.Collections;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FederalStatePathogenNotificationCategoryDataTest {

  public static final CodeDisplay CVDP =
      CodeDisplay.builder()
          .code("cvdp")
          .display("Severe-Acute-Respiratory-Syndrome-Coronavirus-2 (SARS-CoV-2)")
          .designations(Collections.singleton(new Designation("de-DE", "SARS-CoV-2")))
          .order(100)
          .build();
  private static File notificationCategoryFile;
  private static File federalStateFile;
  private static FhirContext fhirContext;
  @Mock private SnapshotFilesService snapshotFilesServiceMock;

  @BeforeAll
  static void setUp() {
    notificationCategoryFile =
        new File("src/test/resources/profiles/CodeSystem/CodeSystem-notificationCategory.json");
    federalStateFile =
        new File("src/test/resources/profiles/CodeSystem/CodeSystem-CodeSystemISO31662DE.json");
    fhirContext = FhirContext.forR4Cached();
  }

  @Test
  void shouldCreateDataAndFillMapAndList() {
    when(snapshotFilesServiceMock.getProfileNotificationCategoryFile())
        .thenReturn(notificationCategoryFile);
    when(snapshotFilesServiceMock.getFederalStateFile()).thenReturn(federalStateFile);

    FederalStateNotificationCategoryData federalStateNotificationCategoryData =
        new FederalStateNotificationCategoryData(
            snapshotFilesServiceMock, fhirContext, false, "", false, false);
    federalStateNotificationCategoryData.createData();

    assertThat(federalStateNotificationCategoryData.getFederalStateNotificationCategories())
        .isNotEmpty();
    assertThat(federalStateNotificationCategoryData.getFederalStates()).isNotEmpty();
    assertThat(
            federalStateNotificationCategoryData
                .getFederalStateNotificationCategories()
                .get("DE-BW"))
        .isNotEmpty();
  }

  @Test
  void shouldMakeAllFederalStatesAvailable() {
    when(snapshotFilesServiceMock.getProfileNotificationCategoryFile())
        .thenReturn(notificationCategoryFile);
    when(snapshotFilesServiceMock.getFederalStateFile()).thenReturn(federalStateFile);

    FederalStateNotificationCategoryData federalStateNotificationCategoryData =
        new FederalStateNotificationCategoryData(
            snapshotFilesServiceMock, fhirContext, false, "", false, false);
    federalStateNotificationCategoryData.createData();

    assertThat(federalStateNotificationCategoryData.getFederalStates())
        .isNotEmpty()
        .hasSize(16)
        .contains(CodeDisplay.builder().code("DE-BW").display("Baden-Württemberg").build())
        .contains(CodeDisplay.builder().code("DE-BY").display("Bayern").build())
        .contains(CodeDisplay.builder().code("DE-BE").display("Berlin").build())
        .contains(CodeDisplay.builder().code("DE-BB").display("Brandenburg").build())
        .contains(CodeDisplay.builder().code("DE-HB").display("Bremen").build())
        .contains(CodeDisplay.builder().code("DE-HH").display("Hamburg").build())
        .contains(CodeDisplay.builder().code("DE-HE").display("Hessen").build())
        .contains(CodeDisplay.builder().code("DE-MV").display("Mecklenburg-Vorpommern").build())
        .contains(CodeDisplay.builder().code("DE-NI").display("Niedersachsen").build())
        .contains(CodeDisplay.builder().code("DE-NW").display("Nordrhein-Westfalen").build())
        .contains(CodeDisplay.builder().code("DE-RP").display("Rheinland-Pfalz").build())
        .contains(CodeDisplay.builder().code("DE-SL").display("Saarland").build())
        .contains(CodeDisplay.builder().code("DE-SN").display("Sachsen").build())
        .contains(CodeDisplay.builder().code("DE-ST").display("Sachsen-Anhalt").build())
        .contains(CodeDisplay.builder().code("DE-SH").display("Schleswig-Holstein").build())
        .contains(CodeDisplay.builder().code("DE-TH").display("Thüringen").build());
  }

  @Test
  void shouldFilterCodesIfActiveIsTrueAndListIsGiven() {
    when(snapshotFilesServiceMock.getProfileNotificationCategoryFile())
        .thenReturn(notificationCategoryFile);
    when(snapshotFilesServiceMock.getFederalStateFile()).thenReturn(federalStateFile);

    FederalStateNotificationCategoryData federalStateNotificationCategoryData =
        new FederalStateNotificationCategoryData(
            snapshotFilesServiceMock, fhirContext, true, "cvdp", false, false);
    federalStateNotificationCategoryData.createData();

    assertThat(federalStateNotificationCategoryData.getFederalStates()).isNotEmpty();
    assertThat(
            federalStateNotificationCategoryData
                .getFederalStateNotificationCategories()
                .get("DE-BW"))
        .doesNotContain(CVDP);
  }

  @Test
  void shouldNotFilterCodesIfActiveIsFalseAndListIsGiven() {
    when(snapshotFilesServiceMock.getProfileNotificationCategoryFile())
        .thenReturn(notificationCategoryFile);
    when(snapshotFilesServiceMock.getFederalStateFile()).thenReturn(federalStateFile);

    FederalStateNotificationCategoryData federalStateNotificationCategoryData =
        new FederalStateNotificationCategoryData(
            snapshotFilesServiceMock, fhirContext, false, "advp", false, false);
    federalStateNotificationCategoryData.createData();

    assertThat(federalStateNotificationCategoryData.getFederalStates()).isNotEmpty();
    assertThat(
            federalStateNotificationCategoryData
                .getFederalStateNotificationCategories()
                .get("DE-BW"))
        .contains(CVDP);
  }

  @Test
  void shouldNotFilterCodesIfActiveIsTrueAndListDoesNotContainCode() {
    when(snapshotFilesServiceMock.getProfileNotificationCategoryFile())
        .thenReturn(notificationCategoryFile);
    when(snapshotFilesServiceMock.getFederalStateFile()).thenReturn(federalStateFile);

    FederalStateNotificationCategoryData federalStateNotificationCategoryData =
        new FederalStateNotificationCategoryData(
            snapshotFilesServiceMock, fhirContext, true, "advp,hbvp", false, false);
    federalStateNotificationCategoryData.createData();

    assertThat(federalStateNotificationCategoryData.getFederalStates()).isNotEmpty();
    assertThat(
            federalStateNotificationCategoryData
                .getFederalStateNotificationCategories()
                .get("DE-BW"))
        .contains(CVDP);
  }

  @Test
  void shouldReturnCorrectOrderOfFile() {
    when(snapshotFilesServiceMock.getProfileNotificationCategoryFile())
        .thenReturn(notificationCategoryFile);
    when(snapshotFilesServiceMock.getFederalStateFile()).thenReturn(federalStateFile);

    FederalStateNotificationCategoryData federalStateNotificationCategoryData =
        new FederalStateNotificationCategoryData(
            snapshotFilesServiceMock, fhirContext, false, "", false, false);
    federalStateNotificationCategoryData.createData();

    assertThat(federalStateNotificationCategoryData.getFederalStates()).isNotEmpty();
    assertThat(
            federalStateNotificationCategoryData
                .getFederalStateNotificationCategories()
                .get("DE-SN"))
        .extracting("code")
        .containsExactlyInAnyOrder(
            "advp", "banp", "bpsp", "bovp", "borp", "brup", "camp", "ckvp", "chlp", "clop", "corp",
            "coxp", "denp", "cryp", "ebvp", "ehcp", "ecop", "frtp", "fsvp", "gfvp", "gilp", "hinp",
            "havp", "hbvp", "hcvp", "hdvp", "hevp", "htvp", "invp", "lsvp", "legp", "lepp", "lisp",
            "mbvp", "msvp", "mrsp", "mpvp", "mpxp", "mylp", "mytp", "neip", "novp", "opxp", "povp",
            "rbvp", "ricp", "rtvp", "ruvp", "spap", "styp", "salp", "cvdp", "cvsp", "ship", "spnp",
            "trip", "vzvp", "vchp", "wnvp", "ypsp", "yenp", "zkvp", "abvp", "hfap", "mrap", "ebcp",
            "acbp", "wbkp", "pkvp", "rsvp", "astp", "bobp", "cltp", "cymp", "eahp", "etvp", "gbsp",
            "mpmp", "pinp", "pvbp", "spyp", "adep", "caup", "plap", "ncvp");
  }

  @Test
  void shouldReturnListWithTestData() {
    when(snapshotFilesServiceMock.getProfileNotificationCategoryFile())
        .thenReturn(notificationCategoryFile);
    when(snapshotFilesServiceMock.getFederalStateFile()).thenReturn(federalStateFile);

    FederalStateNotificationCategoryData federalStateNotificationCategoryData =
        new FederalStateNotificationCategoryData(
            snapshotFilesServiceMock, fhirContext, false, "", true, false);
    federalStateNotificationCategoryData.createData();

    assertThat(federalStateNotificationCategoryData.getFederalStates()).isNotEmpty();
    assertThat(
            federalStateNotificationCategoryData
                .getFederalStateNotificationCategories()
                .get("DE-SN"))
        .extracting("code")
        .containsExactlyInAnyOrder(
            "advp", "banp", "bpsp", "bovp", "borp", "brup", "camp", "ckvp", "chlp", "clop", "corp",
            "coxp", "denp", "cryp", "ebvp", "ehcp", "ecop", "frtp", "fsvp", "gfvp", "gilp", "hinp",
            "havp", "hbvp", "hcvp", "hdvp", "hevp", "htvp", "invp", "lsvp", "legp", "lepp", "lisp",
            "mbvp", "msvp", "mrsp", "mpvp", "mpxp", "mylp", "mytp", "neip", "novp", "opxp", "povp",
            "rbvp", "ricp", "rtvp", "ruvp", "spap", "styp", "salp", "cvdp", "cvsp", "ship", "spnp",
            "trip", "vzvp", "vchp", "wnvp", "ypsp", "yenp", "zkvp", "abvp", "hfap", "mrap", "ebcp",
            "acbp", "wbkp", "pkvp", "rsvp", "astp", "bobp", "cltp", "cymp", "eahp", "etvp", "gbsp",
            "mpmp", "pinp", "pvbp", "spyp", "adep", "caup", "plap", "abcd", "ncvp");
  }

  @Test
  void shouldReturnListWithTestData2() {
    when(snapshotFilesServiceMock.getProfileNotificationCategoryFile())
        .thenReturn(notificationCategoryFile);
    when(snapshotFilesServiceMock.getFederalStateFile()).thenReturn(federalStateFile);

    FederalStateNotificationCategoryData federalStateNotificationCategoryData =
        new FederalStateNotificationCategoryData(
            snapshotFilesServiceMock, fhirContext, false, "", true, true);
    federalStateNotificationCategoryData.createData();

    assertThat(federalStateNotificationCategoryData.getFederalStates()).isNotEmpty();
    assertThat(
            federalStateNotificationCategoryData
                .getFederalStateNotificationCategories()
                .get("DE-SN"))
        .extracting("code")
        .containsExactlyInAnyOrder(
            "advp", "banp", "bpsp", "bovp", "borp", "brup", "camp", "ckvp", "chlp", "clop", "corp",
            "coxp", "denp", "cryp", "ebvp", "ehcp", "ecop", "frtp", "fsvp", "gfvp", "gilp", "hinp",
            "havp", "hbvp", "hcvp", "hdvp", "hevp", "htvp", "invp", "lsvp", "legp", "lepp", "lisp",
            "mbvp", "msvp", "mrsp", "mpvp", "mpxp", "mylp", "mytp", "neip", "novp", "opxp", "povp",
            "rbvp", "ricp", "rtvp", "ruvp", "spap", "styp", "salp", "cvdp", "cvsp", "ship", "spnp",
            "trip", "vzvp", "vchp", "wnvp", "ypsp", "yenp", "zkvp", "abvp", "hfap", "mrap", "ebcp",
            "acbp", "wbkp", "pkvp", "rsvp", "astp", "bobp", "cltp", "cymp", "eahp", "etvp", "gbsp",
            "mpmp", "pinp", "pvbp", "spyp", "adep", "caup", "plap", "abcd", "gapp", "ncvp");
  }

  @Test
  void shouldContainNoNotificationIfEverythingIsInDenyList() {
    when(snapshotFilesServiceMock.getProfileNotificationCategoryFile())
        .thenReturn(notificationCategoryFile);
    when(snapshotFilesServiceMock.getFederalStateFile()).thenReturn(federalStateFile);

    FederalStateNotificationCategoryData federalStateNotificationCategoryData =
        new FederalStateNotificationCategoryData(
            snapshotFilesServiceMock,
            fhirContext,
            true,
            "advp,banp,bpsp,bovp,borp,brup,camp,ckvp,chlp,clop,corp,coxp,denp,cryp,ebvp,ehcp,ecop,frtp,fsvp,gfvp,gilp,hinp,havp,hbvp,hcvp,hdvp,hevp,htvp,invp,lsvp,legp,lepp,lisp,mbvp,msvp,mrsp,mpvp,mpxp,mylp,mytp,ncvp,neip,novp,opxp,povp,rbvp,ricp,rtvp,ruvp,spap,styp,salp,cvdp,cvsp,ship,spnp,trip,vzvp,vchp,wnvp,ypsp,yenp,zkvp,abvp,hfap,mrap,ebcp,acbp,wbkp,pkvp,rsvp,astp,bobp,cltp,cymp,eahp,etvp,gbsp,mpmp,pinp,pvbp,spyp,adep,caup,plap",
            false,
            false);
    federalStateNotificationCategoryData.createData();

    assertThat(federalStateNotificationCategoryData.getFederalStates()).isNotEmpty();
    assertThat(
            federalStateNotificationCategoryData
                .getFederalStateNotificationCategories()
                .get("DE-SN"))
        .isEmpty();
  }
}
