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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils.NOTIFICATION_CATEGORY_PROPERTY;
import static de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils.extractNotificationCategories;
import static de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils.getFileString;

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.utils.SnapshotFilesService;
import de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.CodeSystem;
import org.springframework.stereotype.Component;

/** This component provides the notification category list. */
@Component
@Slf4j
public class NotificationCategoryList {

  private final SnapshotFilesService snapshotFilesService;
  private final FhirContext fhirContext;

  public NotificationCategoryList(
      SnapshotFilesService snapshotFilesService, FhirContext fhirContext) {
    this.snapshotFilesService = snapshotFilesService;
    this.fhirContext = fhirContext;
  }

  public Map<PathogenNotificationCategory, SequencedCollection<CodeDisplay>>
      getPathogenNotificationCategories() {
    final Map<PathogenNotificationCategory, SequencedCollection<CodeDisplay>> result =
        new EnumMap<>(PathogenNotificationCategory.class);
    final File profileNotificationCategoryFile =
        this.snapshotFilesService.getProfileNotificationCategoryFile();
    final String fileString;
    try {
      fileString = getFileString(profileNotificationCategoryFile);
    } catch (IOException e) {
      log.error(
          "Error while reading notification category file '{}'",
          profileNotificationCategoryFile.getName());
      return Collections.emptyMap();
    }

    final CodeSystem notificationCategory =
        fhirContext.newJsonParser().parseResource(CodeSystem.class, fileString);
    for (final CodeSystem.ConceptDefinitionComponent component :
        notificationCategory.getConcept()) {
      final CodeDisplay display = Utils.createCodeDisplay(component);
      final Optional<CodeSystem.ConceptPropertyComponent> ifsgParagraph =
          component.getProperty().stream()
              .filter(p -> p.getCode().equals(NOTIFICATION_CATEGORY_PROPERTY))
              .findFirst();
      final Optional<PathogenNotificationCategory> target =
          ifsgParagraph.map(
              concept -> PathogenNotificationCategory.from(concept.getValue().toString()));
      if (target.isPresent()) {
        result.computeIfAbsent(target.get(), k -> new ArrayList<>()).add(display);
      } else {
        log.warn(
            "Notification category '{}' has no known property '{}'.",
            component.getCode(),
            NOTIFICATION_CATEGORY_PROPERTY);
      }
    }
    // to simplify insertion we don't look at the category,
    // but we still want to avoid leaking unknown categories to a client
    result.remove(PathogenNotificationCategory.UNKNOWN);
    return Collections.unmodifiableMap(result);
  }

  public List<CodeDisplay> getPathogenNotificationCategoryList() {

    try {
      String fileString =
          getFileString(this.snapshotFilesService.getProfileNotificationCategoryFile());
      CodeSystem notificationCategory =
          fhirContext.newJsonParser().parseResource(CodeSystem.class, fileString);

      List<CodeDisplay> codeDisplays = extractNotificationCategories(notificationCategory);

      codeDisplays =
          codeDisplays.stream()
              .sorted(
                  Comparator.comparing(CodeDisplay::getOrder)
                      .reversed()) // reversed so descending order is active
              .toList();

      if (log.isInfoEnabled()) {
        log.info(
            "Loaded pathogen notification categories. Size: {} Codes: {}",
            codeDisplays.size(),
            codeDisplays.stream()
                .map(CodeDisplay::getCode)
                .sorted()
                .collect(Collectors.joining(", ", "[", "]")));
      }
      return codeDisplays;

    } catch (IOException e) {
      log.error("Error while reading notification category file");
      return Collections.emptyList();
    }
  }
}
