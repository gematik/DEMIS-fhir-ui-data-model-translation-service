package de.gematik.demis.fhir_ui_data_model_translation_service.model;

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

import static de.gematik.demis.fhir_ui_data_model_translation_service.utils.Utils.GERMAN_DESIGNATION_ID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * This is the FrontEndModel of the codes with display and designation. The information is used to
 * display content in the reporting portal, e.g. when selecting 'Meldetatbestaende'
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
public class CodeDisplay {
  private String code;
  private String display;
  @Builder.Default private Set<Designation> designations = new HashSet<>();
  @JsonIgnore private int order;
  private String system;
  private String breadcrumb;

  public List<String> extractHeadersFromCodeDisplay() {
    String[] parts = getDisplay().split(";", 2);
    return parts.length > 1 ? Stream.of(parts).map(String::trim).toList() : List.of(getDisplay());
  }

  public Set<Designation> extractGermanDesignations() {
    return designations.stream()
        .filter(designation -> designation.language().startsWith("de"))
        .collect(Collectors.toSet());
  }

  public void addDesignation(Designation designation) {
    if (this.designations == null) {
      this.designations = new HashSet<>();
    }
    this.designations.add(designation);
  }

  public void addDesignation(Set<Designation> designations) {
    if (this.designations == null) {
      this.designations = new HashSet<>();
    }
    this.designations.addAll(designations);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CodeDisplay that = (CodeDisplay) o;
    return order == that.order
        && Objects.equals(code, that.code)
        && Objects.equals(display, that.display)
        && Objects.equals(designations, that.designations)
        && Objects.equals(system, that.system);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, display, designations, order, system);
  }

  public static boolean isTwoCharacterCode(CodeDisplay codeDisplay) {
    return codeDisplay.getCode().length() == 2;
  }

  public static String getGermanDesignation(CodeDisplay codeDisplay) {
    return codeDisplay.getDesignations().stream()
        .filter(designation -> designation.language().startsWith(GERMAN_DESIGNATION_ID))
        .findFirst()
        .map(Designation::value)
        .orElse(codeDisplay.getDisplay());
  }
}
