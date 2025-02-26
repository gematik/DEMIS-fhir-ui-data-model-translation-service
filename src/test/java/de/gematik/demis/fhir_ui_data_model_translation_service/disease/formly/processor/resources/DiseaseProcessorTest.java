package de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.resources;

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
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.DiseaseClipboardProps;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.FieldGroup;
import de.gematik.demis.fhir_ui_data_model_translation_service.exception.DataNotFoundExcp;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.Designation;
import de.gematik.demis.fhir_ui_data_model_translation_service.translation.DataLoaderSrv;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DiseaseProcessorTest {

  @Mock DataLoaderSrv dataLoaderSrvMock;
  @Mock DiseaseClipboardProps diseaseClipboardProps;

  @Test
  void shouldReturnFieldGroupsWithEvidenceOptions() throws JsonProcessingException {

    String expectedJsonString =
        """
      [{"key":"recordedDate","fieldGroup":[{"key":"answer.valueDate","type":"input","props":{"placeholder":"TT.MM.JJJJ | MM.JJJJ | JJJJ","label":"Datum Diagnosestellung"},"wrappers":["form-field"],"className":"LinkId_recordedDate"}]},{"key":"onset","fieldGroup":[{"key":"answer.valueDate","type":"input","props":{"placeholder":"TT.MM.JJJJ | MM.JJJJ | JJJJ","label":"Erkrankungsbeginn"},"wrappers":["form-field"],"className":"LinkId_onset"}]},{"key":"evidence","fieldGroup":[{"key":"answer.valueCoding","type":"autocomplete-multi-coding","props":{"options":[{"code":"67782005","display":"akutes schweres Atemnotsyndrom (ARDS)","designations":[{"language":"en-US","value":"Acute respiratory distress syndrome (disorder)"}],"system":"http://snomed.info/sct"},{"code":"43724002","display":"Frösteln","designations":[{"language":"en-US","value":"Chill (finding)"}],"system":"http://snomed.info/sct"}],"required":false,"clearable":true,"label":"Symptome und -Manifestationen"},"validators":{"validation":["codingValidator"]},"wrappers":["form-field"],"className":"LinkId_evidence"}]},{"key":"note","fieldGroup":[{"key":"answer.valueString","type":"input","props":{"label":"Diagnosehinweise"},"wrappers":["form-field"],"className":"LinkId_note"}]}]""";

    CodeDisplay cd67782005 =
        CodeDisplay.builder()
            .code("67782005")
            .display("akutes schweres Atemnotsyndrom (ARDS)")
            .designations(
                Set.of(new Designation("en-US", "Acute respiratory distress syndrome (disorder)")))
            .system("http://snomed.info/sct")
            .build();

    CodeDisplay cd43724002 =
        CodeDisplay.builder()
            .code("43724002")
            .display("Frösteln")
            .designations(Set.of(new Designation("en-US", "Chill (finding)")))
            .system("http://snomed.info/sct")
            .build();

    ObjectMapper objectMapper = new ObjectMapper();

    when(dataLoaderSrvMock.getValueSetData("https://demis.rki.de/fhir/ValueSet/evidenceCVDD"))
        .thenReturn(asList(cd67782005, cd43724002));
    when(this.diseaseClipboardProps.condition()).thenReturn(Collections.emptyMap());

    DiseaseProcessor diseaseProcessor =
        new DiseaseProcessor(this.dataLoaderSrvMock, this.diseaseClipboardProps);
    FieldGroup[] cvdd = diseaseProcessor.createFieldGroup("cvdd");

    // pars cvdd to string with objectMapper
    String cvddString = objectMapper.writeValueAsString(cvdd);
    assertThat(cvddString).isEqualTo(expectedJsonString);
  }

  @Test
  void shouldHandleNoDataFoundOnEvidences() throws Exception {
    String expectedJsonString =
        """
          [{"key":"recordedDate","fieldGroup":[{"key":"answer.valueDate","type":"input","props":{"placeholder":"TT.MM.JJJJ | MM.JJJJ | JJJJ","label":"Datum Diagnosestellung"},"wrappers":["form-field"],"className":"LinkId_recordedDate"}]},{"key":"onset","fieldGroup":[{"key":"answer.valueDate","type":"input","props":{"placeholder":"TT.MM.JJJJ | MM.JJJJ | JJJJ","label":"Erkrankungsbeginn"},"wrappers":["form-field"],"className":"LinkId_onset"}]},{"key":"note","fieldGroup":[{"key":"answer.valueString","type":"input","props":{"label":"Diagnosehinweise"},"wrappers":["form-field"],"className":"LinkId_note"}]}]""";

    CodeDisplay cd67782005 =
        CodeDisplay.builder()
            .code("67782005")
            .display("akutes schweres Atemnotsyndrom (ARDS)")
            .designations(
                Set.of(new Designation("en-US", "Acute respiratory distress syndrome (disorder)")))
            .system("http://snomed.info/sct")
            .build();

    CodeDisplay cd43724002 =
        CodeDisplay.builder()
            .code("43724002")
            .display("Frösteln")
            .designations(Set.of(new Designation("en-US", "Chill (finding)")))
            .system("http://snomed.info/sct")
            .build();

    ObjectMapper objectMapper = new ObjectMapper();

    when(dataLoaderSrvMock.getValueSetData("https://demis.rki.de/fhir/ValueSet/evidenceCVDD"))
        .thenThrow(new DataNotFoundExcp("Database is empty."));
    when(this.diseaseClipboardProps.condition()).thenReturn(Collections.emptyMap());

    DiseaseProcessor diseaseProcessor =
        new DiseaseProcessor(this.dataLoaderSrvMock, this.diseaseClipboardProps);
    FieldGroup[] cvdd = diseaseProcessor.createFieldGroup("cvdd");

    // pars cvdd to string with objectMapper
    String cvddString = objectMapper.writeValueAsString(cvdd);
    assertThat(cvddString).isEqualTo(expectedJsonString);
  }
}
