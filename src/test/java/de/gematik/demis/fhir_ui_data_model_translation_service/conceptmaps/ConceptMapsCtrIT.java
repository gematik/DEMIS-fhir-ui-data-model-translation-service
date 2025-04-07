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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
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
class ConceptMapsCtrIT {

  @Autowired private MockMvc mockMvc;

  @Test
  void testGetAllConceptMap() throws Exception {

    MvcResult result = mockMvc.perform(get("/conceptmap")).andExpect(status().isOk()).andReturn();

    assertThat(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
        .isNotNull()
        .contains("https://demis.rki.de/fhir/ConceptMap/LOINCMethodToSNOMEDMethod")
        .contains("https://demis.rki.de/fhir/ConceptMap/NotificationCategoryToTransmissionCategory")
        .contains("foobar")
        .contains("https://demis.rki.de/fhir/ConceptMap/LOINCMaterialToSNOMEDMaterial")
        .contains("LOINCMethodToSNOMEDMethod")
        .contains("NotificationCategoryToTransmissionCategory")
        .contains(
            "https://demis.rki.de/fhir/ConceptMap/NotificationDiseaseCategoryToTransmissionCategory")
        .contains("NotificationDiseaseCategoryToTransmissionCategory")
        .contains("ISO3166CountryCodes2DEMISCountryCodes")
        .contains("https://demis.rki.de/fhir/ConceptMap/ISO3166CountryCodes2DEMISCountryCodes")
        .contains("LOINCMaterialToSNOMEDMaterial")
        .contains("https://demis.rki.de/fhir/ConceptMap/foobar");
  }

  @Test
  void testGetConceptMap() throws Exception {

    MvcResult result =
        mockMvc
            .perform(get("/conceptmap/NotificationCategoryToTransmissionCategory"))
            .andExpect(status().isOk())
            .andReturn();

    assertThat(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
        .isNotNull()
        .isEqualTo(
            "{\"spyp\":\"spy\",\"mylp\":\"myl\",\"astp\":\"ast\",\"corp\":\"cor\",\"hbvp\":\"hbv\",\"bpsp\":\"bps\",\"hdvp\":\"hdv\",\"etvp\":\"etv\",\"chlp\":\"chl\",\"novp\":\"nov\",\"ruvp\":\"ruv\",\"ship\":\"shi\",\"lepp\":\"lep\",\"cltp\":\"clt\",\"eahp\":\"eah\",\"ckvp\":\"ckv\",\"ypsp\":\"yps\",\"styp\":\"sty\",\"rsvp\":\"rsv\",\"legp\":\"leg\",\"cvdp\":\"cvd\",\"zkvp\":\"zkv\",\"trip\":\"tri\",\"mytp\":\"myt\",\"hfap\":\"hfa\",\"plap\":\"pla\",\"adep\":\"ade\",\"clop\":\"clo\",\"mrap\":\"mra\",\"lisp\":\"lis\",\"denp\":\"den\",\"neip\":\"nei\",\"opxp\":\"opx\",\"invp\":\"inv\",\"mpvp\":\"mpv\",\"camp\":\"cam\",\"advp\":\"adv\",\"hinp\":\"hin\",\"lsvp\":\"lsv\",\"mpmp\":\"mpm\",\"abvp\":\"abv\",\"cymp\":\"cym\",\"ebvp\":\"ebv\",\"mbvp\":\"mbv\",\"cvsp\":\"cvs\",\"pkvp\":\"pkv\",\"vchp\":\"vch\",\"havp\":\"hav\",\"brup\":\"bru\",\"hcvp\":\"hcv\",\"hevp\":\"hev\",\"wbkp\":\"wbk\",\"gbsp\":\"gbs\",\"rtvp\":\"rtv\",\"vzvp\":\"vzv\",\"coxp\":\"cox\",\"povp\":\"pov\",\"pinp\":\"pin\",\"ecop\":\"eco\",\"ricp\":\"ric\",\"msvp\":\"msv\",\"gilp\":\"gil\",\"caup\":\"cau\",\"acbp\":\"acb\",\"rbvp\":\"rbv\",\"pvbp\":\"pvb\",\"borp\":\"bor\",\"ebcp\":\"ebc\",\"wnvp\":\"wnv\",\"salp\":\"sal\",\"cryp\":\"cry\",\"mpxp\":\"mpx\",\"bovp\":\"bov\",\"gfvp\":\"gfv\",\"spnp\":\"spn\",\"banp\":\"ban\",\"mrsp\":\"mrs\",\"bobp\":\"bob\",\"yenp\":\"yen\",\"fsvp\":\"fsv\",\"htvp\":\"htv\",\"frtp\":\"frt\",\"spap\":\"spa\",\"ehcp\":\"ehc\"}");
  }

  @Test
  void testGetCode() throws Exception {

    MvcResult result =
        mockMvc
            .perform(get("/conceptmap/NotificationCategoryToTransmissionCategory/spyp"))
            .andExpect(status().isOk())
            .andReturn();

    assertThat(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
        .isNotNull()
        .isEqualTo("spy");
  }
}
