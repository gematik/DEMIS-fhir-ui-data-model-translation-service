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
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@Slf4j
@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureObservability
@TestPropertySource(locations = "classpath:application-test.properties")
class LaboratoryDataLoaderCtrIT {

  // There are a lot of materials, only the first two are evaluated
  private final String partialMaterialList1 =
      """
            "materials":[{"code":"258607008","display":"Bronchoalveolar lavage fluid specimen (specimen)","designations":[{""";
  private final String partialMaterialList2 =
      """
            "language":"en-US","value":"Bronchoalveolar lavage fluid specimen (specimen)""";
  private final String partialMaterialList3 =
      """
            "language":"de-DE","value":"Bronchoalveoläre Lavage""";

  // There are a lot of methods, only the first two are evaluated
  private final String partialMethodList =
      """
"methods":[{"code":"121276004","display":"Antigen assay (procedure)","designations":[{"language":"en-US","value":"Antigen assay (procedure)"},{"language":"de-DE","value":"Antigennachweis"}]}""";

  // There are a lot of codes, only the first two are evaluated
  private final String invpCodeAndDisplayString =
      """
            {"codeDisplay":{"code":"invp","display":"Influenzavirus; Meldepflicht nur für den direkten Nachweis","designations":[{"language":"de-DE","value":"Influenzavirus"}]},"header":"Influenzavirus","subheader":"Meldepflicht nur für den direkten Nachweis",""";

  private final String partialAnswerSet =
      """
         "answerSet":[{"code":"407479009","display":"Influenza A virus (organism)","designations":[{"language":"en-US","value":"Influenza A virus (organism)"},{"language":"de-DE","value":"Influenza A-Virus"}]},{"code":"715350001","display":"Influenza A virus subtype H10N7 (organism)","designations":[{"language":"en-US","value":"Influenza A virus subtype H10N7 (organism)"},{"language":"de-DE","value":"Influenza A-Virus, Subtyp H10N7"}]},""";

  @Autowired private MockMvc mockMvc;

  @Test
  @DisplayName("get all available federal states")
  void shouldReturnAllFederalStates() throws Exception {
    MvcResult result =
        mockMvc
            .perform(get("/laboratory/7.1/federalStates"))
            .andExpect(status().isOk())
            .andReturn();

    assertThat(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
        .isEqualTo(
            """
            [{"code":"DE-BW","display":"Baden-Württemberg","designations":[]},{"code":"DE-BY","display":"Bayern","designations":[]},{"code":"DE-BE","display":"Berlin","designations":[]},{"code":"DE-BB","display":"Brandenburg","designations":[]},{"code":"DE-HB","display":"Bremen","designations":[]},{"code":"DE-HH","display":"Hamburg","designations":[]},{"code":"DE-HE","display":"Hessen","designations":[]},{"code":"DE-MV","display":"Mecklenburg-Vorpommern","designations":[]},{"code":"DE-NI","display":"Niedersachsen","designations":[]},{"code":"DE-NW","display":"Nordrhein-Westfalen","designations":[]},{"code":"DE-RP","display":"Rheinland-Pfalz","designations":[]},{"code":"DE-SL","display":"Saarland","designations":[]},{"code":"DE-SN","display":"Sachsen","designations":[]},{"code":"DE-ST","display":"Sachsen-Anhalt","designations":[]},{"code":"DE-SH","display":"Schleswig-Holstein","designations":[]},{"code":"DE-TH","display":"Thüringen","designations":[]}]""");
  }

  @Test
  @DisplayName("happy case test for federal state call with Sachsen")
  void shouldReturnDataForFederalState() throws Exception {
    MvcResult result =
        mockMvc
            .perform(get("/laboratory/7.1/federalState/DE-SN"))
            .andExpect(status().isOk())
            .andReturn();

    assertThat(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
        .isEqualTo(
            """
           [{"code":"acbp","display":"Acinetobacter spp. bei Nachweis einer Carbapenemase-Determinante oder mit verminderter Empfindlichkeit gegenüber Carbapenemen außer bei natürlicher Resistenz; Meldepflicht nur bei Infektion oder Kolonisation","designations":[{"language":"de-DE","value":"Acinetobacter spp., Carbapenem"}]},{"code":"advp","display":"Adenoviren; Meldepflicht nur für den direkten Nachweis im Konjunktivalabstrich","designations":[{"language":"de-DE","value":"Adenoviren, Konjunktivalabstrich"}]},{"code":"adep","display":"Adenoviren; Meldepflicht bei akuter Infektion für Nachweise aus allen Körpermaterialien","designations":[{"language":"de-DE","value":"Adenoviren, alle Materialien"}]},{"code":"eahp","display":"Amoebiasis, Entamoeba histolytica","designations":[{"language":"de-DE","value":"Entamoeba histolytica"}]},{"code":"abvp","display":"Arboviren (sonstige)","designations":[{"language":"de-DE","value":"Arboviren, sonstige"}]},{"code":"astp","display":"Astroviren","designations":[{"language":"de-DE","value":"Astroviren"}]},{"code":"banp","display":"Bacillus anthracis","designations":[{"language":"de-DE","value":"Bacillus anthracis"}]},{"code":"bpsp","display":"Bordetella pertussis, Bordetella parapertussis","designations":[{"language":"de-DE","value":"Bordetella pertussis/parapertussis"}]},{"code":"bovp","display":"humanpathogene Bornaviren; Meldepflicht nur für den direkten Nachweis","designations":[{"language":"de-DE","value":"Bornaviren"}]},{"code":"borp","display":"Borrelia recurrentis","designations":[{"language":"de-DE","value":"Borrelia recurrentis"}]},{"code":"bobp","display":"(Lyme-) Borreliose, Borrelia burgdorferi","designations":[{"language":"de-DE","value":"Borrelia burgdorferi"}]},{"code":"brup","display":"Brucella sp.","designations":[{"language":"de-DE","value":"Brucella sp."}]},{"code":"camp","display":"Campylobacter spp. (darmpathogen)","designations":[{"language":"de-DE","value":"Campylobacter spp., darmpathogen"}]},{"code":"caup","display":"Candida auris; Meldepflicht nur für den direkten Nachweis","designations":[{"language":"de-DE","value":"Candida auris"}]},{"code":"ckvp","display":"Chikungunya-Virus","designations":[{"language":"de-DE","value":"Chikungunya-Virus"}]},{"code":"chlp","display":"Chlamydia psittaci","designations":[{"language":"de-DE","value":"Chlamydia psittaci"}]},{"code":"clop","display":"Clostridium botulinum oder Toxinnachweis","designations":[{"language":"de-DE","value":"Clostridium botulinum"}]},{"code":"corp","display":"Corynebacterium spp. (Toxin bildend)","designations":[{"language":"de-DE","value":"Corynebacterium spp., Toxin bildend"}]},{"code":"coxp","display":"Coxiella burnetii","designations":[{"language":"de-DE","value":"Coxiella burnetii"}]},{"code":"cryp","display":"humanpathogene Cryptosporidium sp.","designations":[{"language":"de-DE","value":"Cryptosporidium sp., humanpathogen"}]},{"code":"cymp","display":"Cytomegalie, Cytomegalievirus","designations":[{"language":"de-DE","value":"Cytomegalievirus (CMV)"}]},{"code":"denp","display":"Denguevirus","designations":[{"language":"de-DE","value":"Denguevirus"}]},{"code":"ecop","display":"Escherichia coli (sonstige darmpathogene Stämme)","designations":[{"language":"de-DE","value":"E. coli, sonstige"}]},{"code":"ebvp","display":"Ebolavirus","designations":[{"language":"de-DE","value":"Ebolavirus"}]},{"code":"ebcp","display":"Enterobacterales bei Nachweis einer Carbapenemase-Determinante oder mit verminderter Empfindlichkeit gegenüber Carbapenemen außer bei natürlicher Resistenz; Meldepflicht nur bei Infektion oder Kolonisation","designations":[{"language":"de-DE","value":"Enterobacterales, Carbapenem"}]},{"code":"etvp","display":"Enteroviren","designations":[{"language":"de-DE","value":"Enteroviren"}]},{"code":"hfap","display":"Andere Erreger hämorrhagischer Fieber","designations":[{"language":"de-DE","value":"Erreger hämorrhagischer Fieber"}]},{"code":"ehcp","display":"Escherichia coli (enterohämorrhagische Stämme) (EHEC)","designations":[{"language":"de-DE","value":"E. coli, enterohämorrhagisch"}]},{"code":"frtp","display":"Francisella tularensis","designations":[{"language":"de-DE","value":"Francisella tularensis"}]},{"code":"fsvp","display":"FSME-Virus","designations":[{"language":"de-DE","value":"FSME-Virus"}]},{"code":"gbsp","display":"Gruppe-B-Streptokokken (GBS); Meldepflicht nur für den direkten Nachweis bei Schwangeren und Neugeborenen","designations":[{"language":"de-DE","value":"Gruppe-B-Streptokokken (GBS)"}]},{"code":"gfvp","display":"Gelbfiebervirus","designations":[{"language":"de-DE","value":"Gelbfiebervirus"}]},{"code":"gilp","display":"Giardia lamblia","designations":[{"language":"de-DE","value":"Giardia lamblia"}]},{"code":"hinp","display":"Haemophilus influenzae; Meldepflicht nur für den direkten Nachweis aus Liquor oder Blut","designations":[{"language":"de-DE","value":"Haemophilus influenzae"}]},{"code":"htvp","display":"Hantavirus","designations":[{"language":"de-DE","value":"Hantavirus"}]},{"code":"havp","display":"Hepatitis-A-Virus","designations":[{"language":"de-DE","value":"Hepatitis-A-Virus"}]},{"code":"hbvp","display":"Hepatitis-B-Virus; Meldepflicht für alle Nachweise","designations":[{"language":"de-DE","value":"Hepatitis-B-Virus"}]},{"code":"hcvp","display":"Hepatitis-C-Virus; Meldepflicht für alle Nachweise","designations":[{"language":"de-DE","value":"Hepatitis-C-Virus"}]},{"code":"hdvp","display":"Hepatitis-D-Virus; Meldepflicht für alle Nachweise","designations":[{"language":"de-DE","value":"Hepatitis-D-Virus"}]},{"code":"hevp","display":"Hepatitis-E-Virus","designations":[{"language":"de-DE","value":"Hepatitis-E-Virus"}]},{"code":"invp","display":"Influenzavirus; Meldepflicht nur für den direkten Nachweis","designations":[{"language":"de-DE","value":"Influenzavirus"}]},{"code":"lsvp","display":"Lassavirus","designations":[{"language":"de-DE","value":"Lassavirus"}]},{"code":"legp","display":"Legionella sp.","designations":[{"language":"de-DE","value":"Legionella sp."}]},{"code":"lepp","display":"humanpathogene Leptospira sp.","designations":[{"language":"de-DE","value":"Leptospira sp., humanpathogen"}]},{"code":"lisp","display":"Listeria monocytogenes; Meldepflicht nur für den direkten Nachweis aus Blut, Liquor oder anderen normalerweise sterilen Substraten sowie aus Abstrichen von Neugeborenen","designations":[{"language":"de-DE","value":"Listeria monocytogenes"}]},{"code":"plap","display":"Plasmodium spp. (Malaria)","designations":[{"language":"de-DE","value":"Malaria"}]},{"code":"mbvp","display":"Marburgvirus","designations":[{"language":"de-DE","value":"Marburgvirus"}]},{"code":"msvp","display":"Masernvirus","designations":[{"language":"de-DE","value":"Masernvirus"}]},{"code":"mrsp","display":"Middle-East-Respiratory-Syndrome-Coronavirus (MERS-CoV)","designations":[{"language":"de-DE","value":"MERS-CoV"}]},{"code":"mpxp","display":"Orthopockenviren (Mpoxvirus)","designations":[{"language":"de-DE","value":"Mpoxvirus"}]},{"code":"mrap","display":"Staphylococcus aureus (Methicillin-resistente Stämme); Meldepflicht nur für den Nachweis aus Blut oder Liquor","designations":[{"language":"de-DE","value":"MRSA"}]},{"code":"mpvp","display":"Mumpsvirus","designations":[{"language":"de-DE","value":"Mumpsvirus"}]},{"code":"mylp","display":"Mycobacterium leprae","designations":[{"language":"de-DE","value":"Mycobacterium leprae"}]},{"code":"mytp","display":"Mycobacterium tuberculosis, Mycobacterium africanum, Mycobacterium bovis; Meldepflicht für den direkten Erregernachweis sowie nachfolgend für das Ergebnis der Resistenzbestimmung; vorab auch für den Nachweis säurefester Stäbchen im Sputum","designations":[{"language":"de-DE","value":"Mycobacterium tuberculosis"}]},{"code":"mpmp","display":"Mycoplasmen","designations":[{"language":"de-DE","value":"Mycoplasmen"}]},{"code":"neip","display":"Neisseria meningitidis; Meldepflicht nur für den direkten Nachweis aus Liquor, Blut, hämorrhagischen Hautinfiltraten oder anderen normalerweise sterilen Substraten","designations":[{"language":"de-DE","value":"Neisseria meningitidis"}]},{"code":"novp","display":"Norovirus","designations":[{"language":"de-DE","value":"Norovirus"}]},{"code":"opxp","display":"Orthopockenviren (sonstige Orthopockenviren)","designations":[{"language":"de-DE","value":"Orthopockenvirus, sonstige"}]},{"code":"pinp","display":"Parainfluenzavirus","designations":[{"language":"de-DE","value":"Parainfluenza"}]},{"code":"povp","display":"Poliovirus","designations":[{"language":"de-DE","value":"Poliovirus"}]},{"code":"rbvp","display":"Rabiesvirus","designations":[{"language":"de-DE","value":"Rabiesvirus"}]},{"code":"ricp","display":"Rickettsia prowazekii","designations":[{"language":"de-DE","value":"Rickettsia prowazekii"}]},{"code":"pvbp","display":"Ringelröteln, Parvovirus B 19","designations":[{"language":"de-DE","value":"Parvovirus B 19"}]},{"code":"rtvp","display":"Rotavirus","designations":[{"language":"de-DE","value":"Rotavirus"}]},{"code":"rsvp","display":"Respiratorisches-Synzytial-Virus","designations":[{"language":"de-DE","value":"RSV"}]},{"code":"ruvp","display":"Rubellavirus","designations":[{"language":"de-DE","value":"Rubellavirus"}]},{"code":"spap","display":"Salmonella Paratyphi; Meldepflicht für alle direkten Nachweise","designations":[{"language":"de-DE","value":"Salmonella Paratyphi"}]},{"code":"styp","display":"Salmonella Typhi; Meldepflicht für alle direkten Nachweise","designations":[{"language":"de-DE","value":"Salmonella Typhi"}]},{"code":"salp","display":"Salmonella (sonstige)","designations":[{"language":"de-DE","value":"Salmonella, sonstige"}]},{"code":"cvsp","display":"Severe-Acute-Respiratory-Syndrome-Coronavirus-1 (SARS-CoV-1) (2003)","designations":[{"language":"de-DE","value":"SARS-CoV-1 (2003)"}]},{"code":"cvdp","display":"Severe-Acute-Respiratory-Syndrome-Coronavirus-2 (SARS-CoV-2)","designations":[{"language":"de-DE","value":"SARS-CoV-2"}]},{"code":"spyp","display":"Scharlach, Beta-hämolysierenden Streptokokken der Gruppe A","designations":[{"language":"de-DE","value":"Streptokokken der Gruppe A (GAS)"}]},{"code":"ship","display":"Shigella sp.","designations":[{"language":"de-DE","value":"Shigella sp."}]},{"code":"spnp","display":"Streptococcus pneumoniae; Meldepflicht nur für den direkten Nachweis aus Liquor, Blut, Gelenkpunktat oder anderen normalerweise sterilen Substraten","designations":[{"language":"de-DE","value":"Streptococcus pneumoniae"}]},{"code":"cltp","display":"Tetanus, Clostridium tetani","designations":[{"language":"de-DE","value":"Clostridium tetani"}]},{"code":"trip","display":"Trichinella spiralis","designations":[{"language":"de-DE","value":"Trichinella spiralis"}]},{"code":"pkvp","display":"Orthopockenviren (Variolavirus)","designations":[{"language":"de-DE","value":"Variolavirus"}]},{"code":"vzvp","display":"Varizella-Zoster-Virus","designations":[{"language":"de-DE","value":"Varizella-Zoster-Virus"}]},{"code":"vchp","display":"Vibrio cholerae","designations":[{"language":"de-DE","value":"Vibrio cholerae"}]},{"code":"ncvp","display":"Vibrio spp., humanpathogen (außer Erreger der Cholera); soweit ausschließlich eine Ohrinfektion vorliegt, nur bei Vibrio cholerae non O1/non-O139","designations":[{"language":"de-DE","value":"Vibrio spp. (außer Erreger der Cholera)"}]},{"code":"wbkp","display":"Krankheitserreger unter Berücksichtigung der Art der Krankheitserreger und der Häufigkeit ihres Nachweises wenn Hinweise auf eine schwerwiegende Gefahr für die Allgemeinheit bestehen","designations":[{"language":"de-DE","value":"Weitere bedrohliche Erreger"}]},{"code":"wnvp","display":"West-Nil-Virus","designations":[{"language":"de-DE","value":"West-Nil-Virus"}]},{"code":"ypsp","display":"Yersinia pestis","designations":[{"language":"de-DE","value":"Yersinia pestis"}]},{"code":"yenp","display":"Yersinia spp. (darmpathogen)","designations":[{"language":"de-DE","value":"Yersinia spp., darmpathogen"}]},{"code":"zkvp","display":"Zika-Virus","designations":[{"language":"de-DE","value":"Zika-Virus"}]}]""");
  }

  @Test
  @DisplayName("should return data for invp in federal state url")
  void invpDisplayTest() throws Exception {
    MvcResult result =
        mockMvc
            .perform(get("/laboratory/7.1/federalState/pathogenData/invp"))
            .andExpect(status().isOk())
            .andReturn();

    log.info("Result: " + result.getResponse().getContentAsString(StandardCharsets.UTF_8));

    assertThat(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
        .contains(invpCodeAndDisplayString);
  }

  @Test
  @DisplayName("should return all available §7.3 notification categories")
  void shouldReturnAll73NotificationCategories() throws Exception {
    MvcResult result =
        mockMvc.perform(get("/laboratory/7.3")).andExpect(status().isOk()).andReturn();

    assertThat(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
        .isEqualTo(
            """
            [{"code":"chtp","display":"Chlamydia trachomatis, sofern es sich um einen der Serotypen L1 bis L3 handelt","designations":[{"language":"de-DE","value":"Chlamydia trachomatis"}]},{"code":"echp","display":"Echinococcus sp.","designations":[{"language":"de-DE","value":"Echinococcus sp."}]},{"code":"hivp","display":"Humanes Immundefizienz-Virus (HIV)","designations":[{"language":"de-DE","value":"HIV"}]},{"code":"negp","display":"Neisseria gonorrhoeae","designations":[{"language":"de-DE","value":"Neisseria gonorrhoeae (Gonorrhö)"}]},{"code":"toxp","display":"Toxoplasma gondii; Meldepflicht nur bei konnatalen Infektionen","designations":[{"language":"de-DE","value":"Toxoplasma gondii, konnatal"}]},{"code":"trpp","display":"Treponema pallidum","designations":[{"language":"de-DE","value":"Treponema pallidum (Syphilis)"}]}]""");
  }
}
