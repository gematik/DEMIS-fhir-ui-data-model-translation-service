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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class BusinessContextConditionTest {

  private final ApplicationContextRunner runner =
      new ApplicationContextRunner().withUserConfiguration(TestConfig.class);

  @DisplayName("In legacy mode (package registry disabled ) business restriction never applies")
  @Test
  void createsBothBeans_whenPackageRegistryIsNotEnabled() {
    runner
        .withPropertyValues("feature.flag.package-registry.enabled=false")
        .run(
            ctx -> {
              assertThat(ctx).hasSingleBean(FakeLaboratoryOnlyBean.class);
              assertThat(ctx).hasSingleBean(FakeDiseaseOnlyBean.class);
            });
  }

  @Test
  void createsBothBeans_whenNotificationApiPackage() {
    runner
        .withPropertyValues("feature.flag.package-registry.enabled=true")
        .withPropertyValues("fhir-profile.package-name=demis.rki.notification-api.snapshots")
        .run(
            ctx -> {
              assertThat(ctx).hasSingleBean(FakeLaboratoryOnlyBean.class);
              assertThat(ctx).hasSingleBean(FakeDiseaseOnlyBean.class);
            });
  }

  @Test
  void createsLaboratoryBean_only_whenLaboratoryPackage() {
    runner
        .withPropertyValues("feature.flag.package-registry.enabled=true")
        .withPropertyValues("fhir-profile.package-name=demis.rki.laboratory.snapshots")
        .run(
            ctx -> {
              assertThat(ctx).hasSingleBean(FakeLaboratoryOnlyBean.class);
              assertThat(ctx).doesNotHaveBean(FakeDiseaseOnlyBean.class);
            });
  }

  @Test
  void createsDiseaseBean_only_whenDiseasePackage() {
    runner
        .withPropertyValues("feature.flag.package-registry.enabled=true")
        .withPropertyValues("fhir-profile.package-name=demis.rki.disease.snapshots")
        .run(
            ctx -> {
              assertThat(ctx).doesNotHaveBean(FakeLaboratoryOnlyBean.class);
              assertThat(ctx).hasSingleBean(FakeDiseaseOnlyBean.class);
            });
  }

  @Test
  void createsNoBeans_whenUnknownPackage() {
    runner
        .withPropertyValues("feature.flag.package-registry.enabled=true")
        .withPropertyValues("fhir-profile.package-name=demis.rki.foo.snapshots")
        .run(
            ctx -> {
              assertThat(ctx).doesNotHaveBean(FakeLaboratoryOnlyBean.class);
              assertThat(ctx).doesNotHaveBean(FakeDiseaseOnlyBean.class);
            });
  }

  @Test
  void contextFails_whenPackageNameIsMissing() {
    runner
        .withPropertyValues("feature.flag.package-registry.enabled=true")
        // no package name set !
        .run(
            ctx -> {
              assertThat(ctx).hasFailed();
              assertThat(ctx.getStartupFailure())
                  .isInstanceOf(IllegalStateException.class)
                  .hasMessageContaining("fhir-profile.package-name must be set");
            });
  }

  @Configuration
  static class TestConfig {

    @Bean
    @OnlyInLaboratoryContext
    FakeLaboratoryOnlyBean fakeLabOnlyBean() {
      return new FakeLaboratoryOnlyBean();
    }

    @Bean
    @OnlyInDiseaseContext
    FakeDiseaseOnlyBean fakeDiseaseOnlyBean() {
      return new FakeDiseaseOnlyBean();
    }
  }

  static class FakeLaboratoryOnlyBean {}

  static class FakeDiseaseOnlyBean {}
}
