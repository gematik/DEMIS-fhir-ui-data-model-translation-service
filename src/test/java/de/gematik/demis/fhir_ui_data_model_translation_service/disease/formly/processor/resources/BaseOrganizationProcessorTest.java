package de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.resources;

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

import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.DiseaseClipboardProps;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.EnableWhenProcessor;
import de.gematik.demis.fhir_ui_data_model_translation_service.translation.DataLoaderSrv;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.hl7.fhir.r4.model.Questionnaire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BaseOrganizationProcessorTest {

  private static final String BSNR_KEY = "bsnr.answer.valueString";
  private static final String TYPE_KEY = "type.answer.valueCoding";
  private static final String TEST_ORGANIZATION_LINK_ID = "TestOrganization";

  @Mock private EnableWhenProcessor enableWhenProcessor;
  @Mock private DiseaseClipboardProps diseaseClipboardProps;
  @Mock private DataLoaderSrv dataLoaderSrv;
  private TestOrganizationProcessor processor;

  @BeforeEach
  void createOrganizationProcessor() {
    this.processor =
        new TestOrganizationProcessor(enableWhenProcessor, diseaseClipboardProps, dataLoaderSrv);
  }

  @Test
  @DisplayName("should create BSNR input and type input for organization")
  void shouldCreateBsnrAndTypeInputs() {

    // given
    final Questionnaire.QuestionnaireItemComponent item =
        new Questionnaire.QuestionnaireItemComponent();
    item.setLinkId("linkId1");
    final FieldGroup parent = FieldGroup.builder().build();

    // when
    final FieldGroup[] result = processor.createFieldGroup(item, parent, "cvdd");

    // then
    Assertions.assertThat(result).isNotNull().isNotEmpty().hasSize(1);
    final FieldGroup organization = result[0];
    Assertions.assertThat(organization.getKey()).isEqualTo(TEST_ORGANIZATION_LINK_ID);

    // then organization should have identifier and type inputs
    final List<FieldGroup> fieldGroups = organization.getFieldGroups();
    final List<String> inputs = fieldGroups.stream().map(FieldGroup::getKey).toList();
    Assertions.assertThat(inputs)
        .hasSize(6)
        .containsExactly(
            "name.answer.valueString", BSNR_KEY, TYPE_KEY, "address", "contact", "telecom");

    // then BSNR input should be required
    final FieldGroup bsnrInput = fieldGroups.get(1);
    Assertions.assertThat(bsnrInput.getKey()).isEqualTo(BSNR_KEY);
    Assertions.assertThat(bsnrInput.getProps().getRequired()).isNull();

    // then type input should be optional
    final FieldGroup typeInput = fieldGroups.get(2);
    Assertions.assertThat(typeInput.getKey()).isEqualTo(TYPE_KEY);
    Assertions.assertThat(typeInput.getProps().getRequired()).isTrue();
  }

  private static final class TestOrganizationProcessor extends BaseOrganizationProcessor {
    TestOrganizationProcessor(
        EnableWhenProcessor enableWhenProcessor,
        DiseaseClipboardProps diseaseClipboardProps,
        DataLoaderSrv dataLoaderSrv) {
      super(enableWhenProcessor, diseaseClipboardProps, dataLoaderSrv);
    }

    @Override
    FeatureSpec getBsnrFeatureSpec() {
      return FeatureSpec.ENABLED_OPTIONAL;
    }

    @Override
    FeatureSpec getTypeFeatureSpec() {
      return FeatureSpec.ENABLED_REQUIRED;
    }

    @Override
    String getItemKey() {
      return TEST_ORGANIZATION_LINK_ID;
    }
  }
}
