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
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.QuestionnaireTranslation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QuestionTextMap {

  public static QuestionnaireTranslation cvddMap() {
    Map<String, String> items = new HashMap<>();
    items.put("infectionEnvironmentSettingBegin", "Beginn Infektionsumfeld");
    items.put("reason", "Grund der Hospitalisierung");
    items.put("infectionEnvironmentSettingEnd", "Ende Infektionsumfeld");
    items.put("infectionEnvironmentSetting", "Infektionsumfeld vorhanden");
    items.put("infectionRiskKind", "Welche Risikofaktoren liegen bei der betroffenen Person vor?");
    items.put("infectionEnvironmentSettingGroup", null);
    items.put("infectionSource", "Kontakt zu bestätigtem Fall");
    items.put("trimester", "Trimester");
    items.put("outbreakNote", "Fallbezogene Zusatzinformationen zum Ausbruch");
    items.put(
        "immunization", "Wurde die betroffene Person jemals in Bezug auf die Krankheit geimpft?");
    items.put("outbreak", "Kann der gemeldete Fall einem Ausbruch zugeordnet werden?");
    items.put("immunizationRef", "Impfinformationen");
    items.put("infectionEnvironmentSettingKind", "Wahrscheinliches Infektionsumfeld");
    items.put("outbreakNotificationId", "Notification-Id der zugehörigen Ausbruchsmeldung");
    return QuestionnaireTranslation.builder()
        .title("Covid-19-spezifische klinische und epidemiologische Angaben")
        .items(Collections.unmodifiableMap(items))
        .build();
  }

  public static QuestionnaireTranslation commonMap() {
    Map<String, String> items = new HashMap<>();
    items.put(
        "labSpecimenTaken",
        "Wurde ein Labor mit der Durchführung einer Erregerdiagnostik beauftragt?");
    items.put("additionalInformation", "Wichtige Zusatzinformationen");
    items.put("infectProtectFacilityGroup", null);
    items.put("placeExposureHint", "Anmerkungen zum Expositionsort");
    items.put("hospitalizedGroup", null);
    items.put("hospitalizedEncounter", null);
    items.put("infectProtectFacilityOrganization", "Einrichtung");
    items.put("placeExposureBegin", "Beginn");
    items.put("placeExposureRegion", "Geografische Region");
    items.put("placeExposure", "Ist der wahrscheinliche Expositionsort bekannt?");
    items.put("isDead", "Ist die Person verstorben?");
    items.put("placeExposureGroup", null);
    items.put("militaryAffiliation", "Besteht eine Zugehörigkeit zur Bundeswehr?");
    items.put("hospitalized", "Ist bzw. wurde die Person ins Krankenhaus aufgenommen?");
    items.put(
        "organDonation",
        "Hat die Person in den letzten 6 Monaten Blut, Organe, Gewebe oder Zellen gespendet?");
    items.put("infectProtectFacilityBegin", "Beginn");
    items.put("infectProtectFacilityEnd", "Ende");
    items.put("infectProtectFacilityType", "Art der Einrichtung");
    items.put("placeExposureEnd", "Ende");
    items.put("deathDate", "Sterbedatum");
    items.put(
        "infectProtectFacilityRole", "In welcher Beziehung steht die Person zur Einrichtung?");
    items.put("labSpecimenLab", "Beauftragtes Labor");
    items.put(
        "infectProtectFacility",
        "Ist die betroffene Person in einer für den Infektionsschutz relevanten Einrichtung tätig, betreut oder untergebracht? (Die für den Infektionsschutz relevanten Einrichtungen sind im Infektionsschutzgesetz definiert. Dazu zählen u.a. Einrichtungen gemäß § 23 IfSG (z.B. Krankenhäuser, ärztliche Praxen, Dialyseeinrichtungen und Rettungsdienste), gemäß § 33 IfSG (z.B. Kitas, Kinderhorte, Schulen, Heime und Ferienlager) und gemäß §§ 35-36 IfSG (Pflegeeinrichtungen, Obdachlosunterkünfte, Einrichtungen zur gemeinschaftlichen Unterbringung von Asylsuchenden, sonstige Massenunterkünfte, Justizvollzugsanstalten).)");
    return QuestionnaireTranslation.builder()
        .title("Meldetatbestandsübergreifende klinische und epidemiologische Angaben")
        .items(Collections.unmodifiableMap(items))
        .build();
  }
}
