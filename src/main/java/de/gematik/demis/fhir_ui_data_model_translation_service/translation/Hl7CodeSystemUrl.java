package de.gematik.demis.fhir_ui_data_model_translation_service.translation;

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

import java.util.Optional;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class Hl7CodeSystemUrl implements Supplier<Optional<String>> {

  private static final String MARKER_URL = "\"url\":";
  private static final String MARKER_EXTENSION = "\"extension\":";
  private static final char EXTENSION_END = ']';

  private final String json;

  private int startIndex = 0;
  private int urlIndex = 0;
  private int extensionIndex = -1;

  @Override
  public Optional<String> get() {
    while (true) {
      findNextUrl();
      if (urlIndex < 0) {
        return Optional.empty();
      }
      if (extensionBeforeUrl()) {
        skipExtension();
      } else {
        return Optional.of(readUrl());
      }
    }
  }

  private void findNextUrl() {
    this.urlIndex = json.indexOf(MARKER_URL, this.startIndex);
  }

  private boolean extensionBeforeUrl() {
    this.extensionIndex = this.json.indexOf(MARKER_EXTENSION, this.startIndex);
    return (this.extensionIndex > 0) && (this.extensionIndex < this.urlIndex);
  }

  private void skipExtension() {
    this.startIndex = this.json.indexOf(EXTENSION_END, this.urlIndex);
  }

  private String readUrl() {
    int beginIndex = findBeginIndex();
    int endIndex = findEndIndex(beginIndex);
    return this.json.substring(beginIndex, endIndex);
  }

  private int findBeginIndex() {
    int beginIndex = this.json.indexOf(':', this.urlIndex);
    return this.json.indexOf('"', beginIndex) + 1;
  }

  private int findEndIndex(int beginIndex) {
    return this.json.indexOf('"', beginIndex);
  }
}
