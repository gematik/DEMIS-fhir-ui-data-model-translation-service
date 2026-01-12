package de.gematik.demis.fhir_ui_data_model_translation_service.objects;

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
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.Designation;
import java.util.Set;

public class CodeDisplayWithDesignationTOs {

  public CodeDisplay codeDisplayDEDesignation() {
    return CodeDisplay.builder()
        .code("code")
        .display("codeDisplay")
        .designations(Set.of(new Designation("de", "designation")))
        .build();
  }

  public CodeDisplay code2DisplayDEDesignation() {
    return CodeDisplay.builder()
        .code("code2")
        .display("code2Display")
        .designations(Set.of(new Designation("de", "designation2")))
        .build();
  }

  public CodeDisplay codeWithOneGermanDesignation() {
    return CodeDisplay.builder()
        .code("someCode")
        .display("someDisplay")
        .designations(Set.of(new Designation("de-DE", "german designation")))
        .build();
  }

  public CodeDisplay codeWithOneEnglishDesignation() {
    return CodeDisplay.builder()
        .code("someCode")
        .display("someDisplay")
        .designations(Set.of(new Designation("en-US", "american english designation")))
        .build();
  }

  public CodeDisplay codeWithOneEnglishAndOneGermanDesignation() {
    return CodeDisplay.builder()
        .code("someCode")
        .display("someDisplay")
        .designations(
            Set.of(
                new Designation("en-US", "american english designation"),
                new Designation("de-DE", "german designation")))
        .build();
  }

  public CodeDisplay codeWithTwoGermanDesignation() {
    return CodeDisplay.builder()
        .code("someCode")
        .display("someDisplay")
        .designations(
            Set.of(
                new Designation("de-CH", "swiss designation"),
                new Designation("de-DE", "german designation"),
                new Designation("en-US", "american english designation")))
        .build();
  }

  public CodeDisplay invp() {
    return CodeDisplay.builder()
        .code("invp")
        .display("Influenzavirus; Meldepflicht nur für den direkten Nachweis")
        .designations(Set.of(new Designation("de-DE", "Influenzavirus")))
        .order(590)
        .build();
  }

  /**
   * this is used for regression tests with pre §7.3 processing where order was not set in the
   * notification disease catergory code system.
   *
   * @return CodeDisplay for invp with order set to 100
   */
  public CodeDisplay invpRegression() {
    return CodeDisplay.builder()
        .code("invp")
        .display("Influenzavirus; Meldepflicht nur für den direkten Nachweis")
        .designations(Set.of(new Designation("de-DE", "Influenzavirus")))
        .order(100)
        .build();
  }

  public CodeDisplay cvdd() {
    return CodeDisplay.builder()
        .code("cvdd")
        .display("Coronavirus-Krankheit-2019 (COVID-19)")
        .order(400)
        .designations(Set.of(new Designation("de-DE", "Coronavirus-Krankheit-2019 (COVID-19)")))
        .build();
  }

  /**
   * this is used for regression tests with pre §7.3 processing where order was not set in the
   * notification disease catergory code system.
   *
   * @return CodeDisplay for invp with order set to 100
   */
  public CodeDisplay cvddRegression() {
    return CodeDisplay.builder()
        .code("cvdd")
        .display("Coronavirus-Krankheit-2019 (COVID-19)")
        .order(37)
        .build();
  }

  public CodeDisplay vchd() {
    return CodeDisplay.builder()
        .code("vchd")
        .display("Cholera")
        .designations(Set.of(new Designation("de-DE", "Cholera")))
        .order(420)
        .build();
  }

  public CodeDisplay chtd() {
    return CodeDisplay.builder()
        .code("chtd")
        .display("Chlamydia trachomatis, sofern es sich um einen der Serotypen L1 bis L3 handelt")
        .order(60)
        .designations(Set.of(new Designation("de-DE", "Chlamydia trachomatis (L1 bis L3)")))
        .build();
  }

  public CodeDisplay hivd() {
    return CodeDisplay.builder()
        .code("hivd")
        .display("Humanes Immundefizienz-Virus (HIV)")
        .order(40)
        .designations(Set.of(new Designation("de-DE", "HIV")))
        .build();
  }
}
