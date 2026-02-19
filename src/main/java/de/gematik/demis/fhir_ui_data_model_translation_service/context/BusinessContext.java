package de.gematik.demis.fhir_ui_data_model_translation_service.context;

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

import org.apache.commons.lang3.StringUtils;

/**
 * The FUTS can be used in different business context such as disease, laboratory, statistic or IGS.
 * Each of these contexts has its own FHIR profiles delivered in separate FHIR packages. This enum
 * represents the different business contexts and provides a method to deduce the active context(s)
 * from the name of the FHIR package the FUTS has been started with.
 */
enum BusinessContext {
  DISEASE,
  LABORATORY,
  STATISTIC,
  IGS,
  LEGACY_DISEASE_LABORATORY_STATISTIC, // Can be removed as soon as we only work with separate FHIR
  // packages
  UNKNOWN;

  /** This method maps the name of the FHIR package to a BusinessContext. */
  static BusinessContext fromPackageName(String packageName) {
    if (StringUtils.isBlank(packageName)) {
      throw new IllegalStateException("fhir-profile.package-name must be set and not blank");
    }
    packageName = packageName.toLowerCase();
    if (packageName.contains(".notification-api."))
      return BusinessContext.LEGACY_DISEASE_LABORATORY_STATISTIC;
    if (packageName.contains(".disease.")) return BusinessContext.DISEASE;
    if (packageName.contains(".laboratory.")) return BusinessContext.LABORATORY;
    if (packageName.contains(".statistic.")) return BusinessContext.STATISTIC;
    if (packageName.contains(".igs.")) return BusinessContext.IGS;

    return BusinessContext.UNKNOWN;
  }
}
