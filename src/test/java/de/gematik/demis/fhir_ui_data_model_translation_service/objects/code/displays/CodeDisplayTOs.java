package de.gematik.demis.fhir_ui_data_model_translation_service.objects.code.displays;

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

import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.objects.code.displays.invp.InvpLabTestCodeTOs;
import de.gematik.demis.fhir_ui_data_model_translation_service.objects.code.displays.invp.InvpMaterialCodeTOs;

public class CodeDisplayTOs {

  public CodeDisplay code1() {
    return CodeDisplay.builder().code("code1").display("display1").build();
  }

  public CodeDisplay code2() {
    return CodeDisplay.builder().code("code2").display("display2").build();
  }

  public CodeDisplay labTestCode() {
    return CodeDisplay.builder().code("labTestCode").display("labTestDisplay").build();
  }

  public CodeDisplay materialCode() {
    return CodeDisplay.builder().code("materialCode").display("materialDisplay").build();
  }

  public InvpLabTestCodeTOs invpLabTestCodes() {
    return new InvpLabTestCodeTOs();
  }

  public InvpMaterialCodeTOs invpMaterialCodes() {
    return new InvpMaterialCodeTOs();
  }

  public InvpAnswerSetCodeTOs invpAnswerSetCodeTOs() {
    return new InvpAnswerSetCodeTOs();
  }

  public CodeDisplay answerCode() {
    return CodeDisplay.builder().code("answerCode").display("answerCodeDisplay").build();
  }

  public CodeDisplay substanceCode() {
    return CodeDisplay.builder().code("substanceCode").display("substanceCodeDisplay").build();
  }

  public CodeDisplay methodCode() {
    return CodeDisplay.builder().code("methodCode").display("methodCodeDisplay").build();
  }

  public CodeDisplay resistanceCode() {
    return CodeDisplay.builder().code("resistanceCode").display("resistanceCodeDisplay").build();
  }

  public CodeDisplay resistanceGeneCode() {
    return CodeDisplay.builder()
        .code("resistanceGeneCode")
        .display("resistanceGeneCodeDisplay")
        .build();
  }

  public HbvpSubstanceTOs hbvpSubstanceCodeTOs() {
    return new HbvpSubstanceTOs();
  }

  public CodeDisplay semicoloncase() {
    return CodeDisplay.builder()
        .code("someCode")
        .display("before the semicolon; after the semicolon")
        .build();
  }

  public CodeDisplay multipleSemicolonCase() {
    return CodeDisplay.builder()
        .code("someCode")
        .display("before the semicolon; after the semicolon; and some more")
        .build();
  }

  public CodeDisplay yes() {
    return CodeDisplay.builder()
        .code("YES")
        .display("ja")
        .system("https://some.system.com/blablubbli")
        .build();
  }

  public CodeDisplay nask() {
    return CodeDisplay.builder()
        .code("NASK")
        .display("nicht gefragt")
        .system("https://some.system.com/blablubbli")
        .build();
  }
}
