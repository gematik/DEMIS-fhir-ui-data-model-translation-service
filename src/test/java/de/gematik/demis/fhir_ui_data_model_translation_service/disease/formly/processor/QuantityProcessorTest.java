package de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor;

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

import static org.assertj.core.api.Assertions.assertThat;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.fhir.TooltipExtension;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Props;
import org.hl7.fhir.r4.model.Questionnaire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class QuantityProcessorTest {

  private static final String FHIR_JSON_WITH_QUANTITY_BOUNDS =
"""
{
  "resourceType": "Questionnaire",
  "id": "DiseaseQuestionsTOXD",
  "url": "https://demis.rki.de/fhir/Questionnaire/DiseaseQuestionsTOXD",
  "version": "1.1.0",
  "name": "DiseaseQuestionsTOXD",
  "title": "Toxoplasma gondii; Meldepflicht nur bei konnatalen Infektionen: spezifische klinische und epidemiologische Angaben",
  "status": "active",
  "date": "2025-08-12",
  "description": "Toxoplasma gondii (konnatal) spezifische Informationsbedarfe werden in diesem meldetatbestandsspezifischen Fragebogen zusammengestellt. Dieser manifestiert sich als entsprechende QuestionnaireResponse innerhalb der Meldung.",
  "item": [
    {
      "extension": [
        {
          "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-minQuantity",
          "valueQuantity": {
            "value": 500,
            "system": "http://unitsofmeasure.org",
            "code": "wk"
          }
        },
        {
          "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-maxQuantity",
          "valueQuantity": {
            "value": 5000,
            "system": "http://unitsofmeasure.org",
            "code": "wk"
          }
        }
      ],
      "linkId": "newbornWeight",
      "text": "Welches Gewicht hatte das Neugeborene bei der Geburt (Angabe in Gramm)?",
      "type": "quantity",
      "required": true,
      "repeats": false
    }
    ]
    }\
""";

  private static final String FHIR_JSON =
"""
{
  "resourceType": "Questionnaire",
  "id": "DiseaseQuestionsTOXD",
  "url": "https://demis.rki.de/fhir/Questionnaire/DiseaseQuestionsTOXD",
  "version": "1.0.0",
  "name": "DiseaseQuestionsTOXD",
  "title": "Toxoplasma gondii; Meldepflicht nur bei konnatalen Infektionen: spezifische klinische und epidemiologische Angaben",
  "status": "draft",
  "date": "2025-04-25",
  "description": "Toxoplasma gondii (konnatal) spezifische Informationsbedarfe werden in diesem meldetatbestandsspezifischen Fragebogen zusammengestellt. Dieser manifestiert sich als entsprechende QuestionnaireResponse innerhalb der Meldung.",
  "item": [
    {
      "extension": [
        {
          "valueCoding": {
            "display": "week",
            "system": "http://unitsofmeasure.org",
            "code": [
              "wk"
            ]
          },
          "url": "http://hl7.org/fhir/StructureDefinition/questionnaire-unitOption"
        }
      ],
      "linkId": "pregnancyWeek",
      "text": "In welcher Schwangerschaftswoche fand die Geburt statt?",
      "type": "quantity",
      "required": true,
      "repeats": false
    }
  ]
}
""";

  private static final String EXPECTED_FORMLY_JSON =
"""
{
  "key": "pregnancyWeek",
  "type": "input",
  "props": {
    "required": true,
    "label": "In welcher Schwangerschaftswoche fand die Geburt statt?",
    "quantity": {
      "system": "http://unitsofmeasure.org",
      "unit": "week",
      "code": "wk"
    }
  },
  "validators": {
    "validation": [
      "numberValidator",
      "nonBlankValidator"
    ]
  },
  "wrappers": [
    "form-field"
  ],
  "className": "LinkId_pregnancyWeek"
}\
""";

  private QuantityProcessor quantityProcessor;

  @BeforeEach
  void setUp() {
    // Mocks für Abhängigkeiten
    EnableWhenProcessor enableWhenProcessor = Mockito.mock(EnableWhenProcessor.class);
    ClipboardProcessor clipboardProcessor = Mockito.mock(ClipboardProcessor.class);
    final QuantityBoundProcessor quantityBoundProcessor = new QuantityBoundProcessor();
    quantityProcessor =
        new QuantityProcessor(
            enableWhenProcessor,
            clipboardProcessor,
            quantityBoundProcessor,
            true,
            new TooltipExtension());
  }

  @Test
  void shouldCreateFieldGroupForQuantityItem() throws JsonProcessingException {
    // FHIR QuestionnaireItemComponent parsen
    FhirContext ctx = FhirContext.forR4Cached();
    IParser parser = ctx.newJsonParser();
    Questionnaire questionnaire = parser.parseResource(Questionnaire.class, FHIR_JSON);
    Questionnaire.QuestionnaireItemComponent item = questionnaire.getItemFirstRep();

    // Prozessor aufrufen
    FieldGroup[] result = quantityProcessor.createFieldGroup(item, null, null);

    // Prüfen
    assertThat(result).hasSize(1);
    FieldGroup fg = result[0];
    assertThat(fg.getKey()).isEqualTo("pregnancyWeek");
    assertThat(fg.getType()).isEqualTo(FieldGroup.TYPE_INPUT);
    assertThat(fg.getProps()).isNotNull();
    Props.Quantity quantity = fg.getProps().getQuantity();
    assertThat(quantity).isNotNull();
    assertThat(quantity.getSystem()).isEqualTo("http://unitsofmeasure.org");
    assertThat(quantity.getUnit()).isEqualTo("week");
    assertThat(quantity.getCode()).isEqualTo("wk");
    assertThat(fg.getProps().getLabel())
        .isEqualTo("In welcher Schwangerschaftswoche fand die Geburt statt?");
    assertThat(fg.getProps().getRequired()).isTrue();
    // Optional: Prüfe Validatoren, falls gewünscht
    assertThat(fg.getValidators()).isNotNull();
    assertThat(fg.getValidators().getValidation()).contains("numberValidator", "nonBlankValidator");

    ObjectMapper objectMapper = new ObjectMapper();
    String jsonResult = objectMapper.writeValueAsString(fg);

    assertThat(jsonResult).isEqualToIgnoringWhitespace(EXPECTED_FORMLY_JSON);
  }

  @Test
  void thatUpperAndLowerBoundAreExtracted() {
    final FhirContext ctx = FhirContext.forR4Cached();
    final IParser parser = ctx.newJsonParser();
    final Questionnaire questionnaire =
        parser.parseResource(Questionnaire.class, FHIR_JSON_WITH_QUANTITY_BOUNDS);
    final Questionnaire.QuestionnaireItemComponent item = questionnaire.getItemFirstRep();

    final FieldGroup[] result = quantityProcessor.createFieldGroup(item, null, null);

    assertThat(result).hasSize(1);
    final FieldGroup fg = result[0];
    assertThat(fg.getProps())
        .satisfies(
            p -> {
              assertThat(p.getMin()).isEqualByComparingTo("500");
              assertThat(p.getMax()).isEqualByComparingTo("5000");
              assertThat(p.getStep()).isEqualByComparingTo("1");
              assertThat(p.getQuantity().getSystem()).isEqualTo("http://unitsofmeasure.org");
              assertThat(p.getQuantity().getCode()).isEqualTo("wk");
            });
    assertThat(fg.getValidators()).isNull();
  }

  // Can be removed with FEATURE_FLAG_NOTIFICATIONS_7_3
  @Test
  void thatUpperAndLowerBoundAreNotExtractedWithDisabledFeatureFlag() {
    final EnableWhenProcessor enableWhenProcessor = Mockito.mock(EnableWhenProcessor.class);
    final ClipboardProcessor clipboardProcessor = Mockito.mock(ClipboardProcessor.class);
    final QuantityBoundProcessor quantityBoundProcessor = new QuantityBoundProcessor();
    quantityProcessor =
        new QuantityProcessor(
            enableWhenProcessor,
            clipboardProcessor,
            quantityBoundProcessor,
            false,
            new TooltipExtension());

    final FhirContext ctx = FhirContext.forR4Cached();
    final IParser parser = ctx.newJsonParser();
    final Questionnaire questionnaire =
        parser.parseResource(Questionnaire.class, FHIR_JSON_WITH_QUANTITY_BOUNDS);
    final Questionnaire.QuestionnaireItemComponent item = questionnaire.getItemFirstRep();

    final FieldGroup[] result = quantityProcessor.createFieldGroup(item, null, null);

    assertThat(result).hasSize(1);
    final FieldGroup fg = result[0];
    assertThat(fg.getProps())
        .satisfies(
            p -> {
              assertThat(p.getMin()).isNull();
              assertThat(p.getMax()).isNull();
              assertThat(p.getStep()).isNull();
            });
    assertThat(fg.getValidators().getValidation()).contains("numberValidator", "nonBlankValidator");
  }
}
