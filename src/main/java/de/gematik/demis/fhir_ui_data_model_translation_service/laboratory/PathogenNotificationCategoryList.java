package de.gematik.demis.fhir_ui_data_model_translation_service.laboratory;

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

import static de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils.extractNotificationCategories;

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhir_ui_data_model_translation_service.context.OnlyInLaboratoryContext;
import de.gematik.demis.fhir_ui_data_model_translation_service.model.CodeDisplay;
import de.gematik.demis.fhir_ui_data_model_translation_service.utils.SnapshotFilesService;
import de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.ValueSet;
import org.springframework.stereotype.Component;

/** This component provides the notification category list. */
@Component
@OnlyInLaboratoryContext
@Slf4j
public class PathogenNotificationCategoryList {

  private final SnapshotFilesService snapshotFilesService;
  private final FhirContext fhirContext;

  public PathogenNotificationCategoryList(
      SnapshotFilesService snapshotFilesService, FhirContext fhirContext) {
    this.snapshotFilesService = snapshotFilesService;
    this.fhirContext = fhirContext;
  }

  public Map<PathogenNotificationCategory, SequencedCollection<CodeDisplay>>
      getPathogenNotificationCategories() {
    final Map<PathogenNotificationCategory, SequencedCollection<CodeDisplay>> result =
        new EnumMap<>(PathogenNotificationCategory.class);

    ValueSet notificationCategory =
        parseValueSetFromFile(
            this.snapshotFilesService.getProfileNotificationCategoryValueSetFile());
    if (notificationCategory != null) {
      result.put(
          PathogenNotificationCategory.P_7_1,
          Utils.extractNotificationCategories(notificationCategory));
    }

    File profileNotificationCategoryNonNomimalValueSetFile =
        this.snapshotFilesService.getProfileNotificationCategoryNonNomimalValueSetFile();
    if (profileNotificationCategoryNonNomimalValueSetFile != null) {
      ValueSet notificationCategoryNonNominal =
          parseValueSetFromFile(profileNotificationCategoryNonNomimalValueSetFile);
      if (notificationCategoryNonNominal != null) {
        List<CodeDisplay> codeDisplaysFromValueSet =
            extractNotificationCategories(notificationCategoryNonNominal);
        result.put(PathogenNotificationCategory.P_7_3, codeDisplaysFromValueSet);
      }
    }

    return Collections.unmodifiableMap(result);
  }

  private ValueSet parseValueSetFromFile(File file) {
    try {
      String fileString = Utils.getFileString(file);
      return fhirContext.newJsonParser().parseResource(ValueSet.class, fileString);
    } catch (IOException e) {
      log.error("Error while reading notification category file: '{}'", file.getName());
      return null;
    }
  }
}
