package de.gematik.demis.fhir_ui_data_model_translation_service.utils;

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

import static de.gematik.demis.fhir_ui_data_model_translation_service.model.Designation.getDesignations;

import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.Designation;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.ValueSet;

public final class Utils {

  private static final int DEFAULT_ORDER_VALUE = 100;
  public static final String GERMAN_DESIGNATION_ID = "de-DE";
  public static final String NOTIFICATION_CATEGORY_PROPERTY = "ifsg-paragraph";

  private Utils() {}

  public static String getFileString(File file) throws IOException {
    FileInputStream inputStream = new FileInputStream(file);
    return getFileString(inputStream);
  }

  public static String getFileString(String filePath) throws IOException {
    FileInputStream inputStream = new FileInputStream(filePath);
    return getFileString(inputStream);
  }

  private static String getFileString(FileInputStream inputStream) throws IOException {
    String fileString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    inputStream.close();
    return fileString;
  }

  public static Optional<String> getCodeFromFileName(File file, Pattern pattern) {
    if (file.exists()) {
      String name = file.getName();

      Matcher matcher = pattern.matcher(name);

      if (matcher.find()) {
        return Optional.of(matcher.group(1));
      }
    }
    return Optional.empty();
  }

  public static int extractOrder(ValueSet.ConceptReferenceComponent component) {
    Optional<Extension> orderExtensionOpt =
        Optional.ofNullable(
            component.getExtensionByUrl(
                "http://hl7.org/fhir/StructureDefinition/valueset-conceptOrder"));

    return orderExtensionOpt
        .map(extension -> Integer.valueOf(extension.getValue().primitiveValue()))
        .orElse(DEFAULT_ORDER_VALUE);
  }

  public static int extractOrder(CodeSystem.ConceptDefinitionComponent component) {
    Optional<Extension> orderExtensionOpt =
        Optional.ofNullable(
            component.getExtensionByUrl(
                "http://hl7.org/fhir/StructureDefinition/codesystem-conceptOrder"));

    return orderExtensionOpt
        .map(extension -> Integer.valueOf(extension.getValue().primitiveValue()))
        .orElse(DEFAULT_ORDER_VALUE);
  }

  /**
   * Extract notification categories from code system
   *
   * @param notificationCategory code system of notification categories
   * @return list of notification categories
   */
  public static List<CodeDisplay> extractNotificationCategories(CodeSystem notificationCategory) {
    return notificationCategory.getConcept().stream().map(Utils::createCodeDisplay).toList();
  }

  public static CodeDisplay createTestDataForErrorCase() {
    return CodeDisplay.builder()
        .code("abcd")
        .display("Testdata")
        .designations(
            Set.of(new Designation(GERMAN_DESIGNATION_ID, "Testdata Designation nicht benutzbar")))
        .order(1)
        .build();
  }

  public static CodeDisplay createTestDataForSorting() {
    return CodeDisplay.builder()
        .code("gapp")
        .display("Testdata2")
        .designations(
            Set.of(new Designation(GERMAN_DESIGNATION_ID, "Testada Designation mit Werten")))
        .order(1)
        .build();
  }

  public static CodeDisplay createCodeDisplay(CodeSystem.ConceptDefinitionComponent category) {
    return CodeDisplay.builder()
        .code(category.getCode())
        .display(category.getDisplay())
        .designations(getDesignations(category))
        .order(extractOrder(category))
        .build();
  }
}
