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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.Wrapper.FORM_FIELD;
import static de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.ClipboardProcessor.createClipboard;

import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.DiseaseClipboardProps;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.model.*;
import de.gematik.demis.fhir_ui_data_model_translation_service.disease.formly.processor.ChoiceProcessor;
import de.gematik.demis.fhir_ui_data_model_translation_service.exception.DataNotFoundExcp;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.translation.DataLoaderSrv;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiseaseProcessor {

  public static final String LINK_ID_RECORDED_DATE = "recordedDate";
  public static final String LINK_ID_ONSET = "onset";
  public static final String LINK_ID_NOTE = "note";
  public static final String LINK_ID_EVIDENCE = "evidence";
  private static final String EVIDENCES_LABEL = "Symptome und -Manifestationen";

  private final DataLoaderSrv dataLoaderSrv;
  private final DiseaseClipboardProps diseaseClipboardProps;

  public FieldGroup[] createFieldGroup(String diseaseCode) {
    List<FieldGroup> fieldGroups = new ArrayList<>();
    fieldGroups.add(createDateOfDiagnosesFieldGroup());
    fieldGroups.add(createDiseaseBeginnFieldGroup());
    addEvidences(diseaseCode, fieldGroups);
    fieldGroups.add(createNoteFieldGroup());
    return fieldGroups.toArray(new FieldGroup[0]);
  }

  private void addEvidences(String diseaseCode, List<FieldGroup> fieldGroups) {
    FieldGroup evidences = createEvidencesFieldGroup(diseaseCode);
    if (evidences != null) {
      fieldGroups.add(evidences);
    }
  }

  private FieldGroup createDateOfDiagnosesFieldGroup() {
    FieldGroup diagnosisDate = FieldGroup.builder().key(LINK_ID_RECORDED_DATE).build();
    FieldGroup input =
        FieldGroup.builder()
            .key("answer.valueDate")
            .type(FieldGroup.TYPE_INPUT)
            .parent(diagnosisDate)
            .props(
                Props.builder()
                    .label("Datum Diagnosestellung")
                    .placeholder("TT.MM.JJJJ | MM.JJJJ | JJJJ")
                    .build())
            .wrappers(List.of(FORM_FIELD))
            .className("LinkId_recordedDate")
            .build();
    clipboardKey(LINK_ID_RECORDED_DATE).ifPresent(key -> createClipboard(key, false, input));
    return diagnosisDate;
  }

  private FieldGroup createDiseaseBeginnFieldGroup() {
    FieldGroup diseaseBegin = FieldGroup.builder().key(LINK_ID_ONSET).build();
    FieldGroup input =
        FieldGroup.builder()
            .key("answer.valueDate")
            .type(FieldGroup.TYPE_INPUT)
            .parent(diseaseBegin)
            .props(
                Props.builder()
                    .label("Erkrankungsbeginn")
                    .placeholder("TT.MM.JJJJ | MM.JJJJ | JJJJ")
                    .build())
            .wrappers(List.of(FORM_FIELD))
            .className("LinkId_onset")
            .build();
    this.diseaseClipboardProps.condition().keySet().stream()
        .filter(k -> k.endsWith(LINK_ID_ONSET))
        .findFirst()
        .ifPresent(c -> createClipboard(c, false, input));
    clipboardKey(LINK_ID_ONSET).ifPresent(key -> createClipboard(key, false, input));
    return diseaseBegin;
  }

  private FieldGroup createNoteFieldGroup() {
    FieldGroup note = FieldGroup.builder().key(LINK_ID_NOTE).build();
    FieldGroup input =
        FieldGroup.builder()
            .key("answer.valueString")
            .type(FieldGroup.TYPE_INPUT)
            .parent(note)
            .props(Props.builder().label("Diagnosehinweise").build())
            .wrappers(List.of(FORM_FIELD))
            .className("LinkId_note")
            .build();
    clipboardKey(LINK_ID_NOTE).ifPresent(key -> createClipboard(key, false, input));
    return note;
  }

  private FieldGroup createEvidencesFieldGroup(String diseaseCode) {
    List<CodeDisplay> options = getCodeDisplays(diseaseCode);
    if (options.isEmpty()) {
      return null;
    }
    FieldGroup evidences = FieldGroup.builder().key(LINK_ID_EVIDENCE).build();
    FieldGroup input =
        FieldGroup.builder()
            .key("answer." + FieldGroup.KEY_VALUE_CODING)
            .type(FieldGroup.TYPE_CODING_MULTI)
            .parent(evidences)
            .props(
                Props.builder()
                    .options(options.toArray(new CodeDisplay[0]))
                    .required(false)
                    .clearable(true)
                    .label(EVIDENCES_LABEL)
                    .build())
            .wrappers(List.of(FORM_FIELD))
            .className("LinkId_evidence")
            .build();
    ChoiceProcessor.enableValidation(input);
    clipboardKey(LINK_ID_EVIDENCE).ifPresent(key -> createClipboard(key, true, input));
    return evidences;
  }

  private List<CodeDisplay> getCodeDisplays(String diseaseCode) {
    try {
      return this.dataLoaderSrv.getValueSetData(
          "https://demis.rki.de/fhir/ValueSet/evidence" + diseaseCode.toUpperCase());
    } catch (DataNotFoundExcp e) {
      log.info("Failed to load evidences of disease! No data found. Disease: {}", diseaseCode, e);
      return Collections.emptyList();
    }
  }

  private Optional<String> clipboardKey(String linkId) {
    return this.diseaseClipboardProps.condition().entrySet().stream()
        .filter(e -> linkId.equals(e.getKey()))
        .map(Map.Entry::getValue)
        .findFirst();
  }
}
