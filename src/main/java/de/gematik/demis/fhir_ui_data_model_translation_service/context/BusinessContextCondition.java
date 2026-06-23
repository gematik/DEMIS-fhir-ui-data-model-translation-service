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

import java.util.Arrays;
import java.util.Map;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

class BusinessContextCondition implements Condition {

  private static final String PACKAGE_NAME_PROPERTY = "fhir-profile.package-name";

  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    Environment env = context.getEnvironment();

    Map<String, Object> attrs =
        metadata.getAnnotationAttributes(ConditionalOnBusinessContext.class.getName());

    if (attrs == null) {
      // Defensive: the condition must only be used in combination with
      // @ConditionalOnBusinessContext
      return false;
    }

    BusinessContext[] allowedContexts = (BusinessContext[]) attrs.get("value");

    String packageName = env.getProperty(PACKAGE_NAME_PROPERTY);
    BusinessContext activeContext = BusinessContext.fromPackageName(packageName);

    return Arrays.stream(allowedContexts).anyMatch(allowed -> allowed == activeContext);
  }
}
