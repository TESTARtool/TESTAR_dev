/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.cli.profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.testar.cli.settings.CliSettingsLoader;
import org.testar.config.composition.CompositionDescriptor;
import org.testar.config.composition.CompositionLoader;
import org.testar.config.policy.PolicyDescriptor;
import org.testar.config.policy.PolicyLoader;
import org.testar.config.settings.Settings;
import org.testar.core.Assert;
import org.testar.core.policy.BlockedPolicy;
import org.testar.core.policy.ClickablePolicy;
import org.testar.core.policy.EnabledPolicy;
import org.testar.core.policy.Policy;
import org.testar.core.policy.ScrollablePolicy;
import org.testar.core.policy.SelectablePolicy;
import org.testar.core.policy.TopLevelPolicy;
import org.testar.core.policy.TypeablePolicy;
import org.testar.core.policy.VisiblePolicy;
import org.testar.core.policy.WidgetFilterPolicy;
import org.testar.plugin.configuration.PolicySessionConfiguration;
import org.testar.plugin.configuration.ServiceSessionConfiguration;

public final class CliProfileConfigurationLoader {

    private CliProfileConfigurationLoader() {
    }

    public static CliProfileConfiguration load(String profileName, Settings settings) {
        String resolvedProfileName = normalizeProfileName(profileName);
        CompositionDescriptor compositionDescriptor = CompositionLoader.loadDescriptor(settings);
        List<String> compatibilityWarnings = collectCliCompatibilityWarnings(compositionDescriptor);

        CliProfileConfiguration profileConfiguration = new CliProfileConfiguration(
                resolvedProfileName,
                settings,
                buildPolicyConfiguration(settings),
                buildServiceConfiguration(settings, compositionDescriptor),
                compositionDescriptor,
                compatibilityWarnings
        );

        return profileConfiguration;
    }

    public static List<String> listProfiles() {
        return CliSettingsLoader.listProfiles();
    }

    private static PolicySessionConfiguration buildPolicyConfiguration(Settings settings) {
        PolicyDescriptor policyDescriptor = PolicyLoader.loadDescriptor(settings);
        PolicySessionConfiguration.Builder builder = PolicySessionConfiguration.builder();

        configurePolicyFamily(
                builder,
                ClickablePolicy.class,
                policyDescriptor.replaceClickablePolicies(),
                policyDescriptor.clickablePolicies(),
                settings
        );
        configurePolicyFamily(
                builder,
                TypeablePolicy.class,
                policyDescriptor.replaceTypeablePolicies(),
                policyDescriptor.typeablePolicies(),
                settings
        );
        configurePolicyFamily(
                builder,
                ScrollablePolicy.class,
                policyDescriptor.replaceScrollablePolicies(),
                policyDescriptor.scrollablePolicies(),
                settings
        );
        configurePolicyFamily(
                builder,
                SelectablePolicy.class,
                policyDescriptor.replaceSelectablePolicies(),
                policyDescriptor.selectablePolicies(),
                settings
        );
        configurePolicyFamily(
                builder,
                EnabledPolicy.class,
                policyDescriptor.replaceEnabledPolicies(),
                policyDescriptor.enabledPolicies(),
                settings
        );
        configurePolicyFamily(
                builder,
                BlockedPolicy.class,
                policyDescriptor.replaceBlockedPolicies(),
                policyDescriptor.blockedPolicies(),
                settings
        );
        configurePolicyFamily(
                builder,
                WidgetFilterPolicy.class,
                policyDescriptor.replaceWidgetFilterPolicies(),
                policyDescriptor.widgetFilterPolicies(),
                settings
        );
        configurePolicyFamily(
                builder,
                VisiblePolicy.class,
                policyDescriptor.replaceVisiblePolicies(),
                policyDescriptor.visiblePolicies(),
                settings
        );
        configurePolicyFamily(
                builder,
                TopLevelPolicy.class,
                policyDescriptor.replaceTopLevelPolicies(),
                policyDescriptor.topLevelPolicies(),
                settings
        );

        return builder.build();
    }

    private static ServiceSessionConfiguration buildServiceConfiguration(Settings settings,
                                                                        CompositionDescriptor compositionDescriptor) {
        CliProfileConfiguration temporaryConfiguration = new CliProfileConfiguration(
                "temporary",
                settings,
                PolicySessionConfiguration.defaults(),
                ServiceSessionConfiguration.defaults(),
                compositionDescriptor,
                List.of()
        );

        return ServiceSessionConfiguration.builder()
                .overrideStateIdentifierService(temporaryConfiguration.stateIdentifierServiceOverride())
                .overrideActionIdentifierService(temporaryConfiguration.actionIdentifierServiceOverride())
                .build();
    }

    private static <T extends Policy> void configurePolicyFamily(PolicySessionConfiguration.Builder builder,
                                                                 Class<T> policyType,
                                                                 boolean replacePolicies,
                                                                 List<String> policyClassNames,
                                                                 Settings settings) {
        Assert.notNull(builder);
        Assert.notNull(policyType);
        Assert.notNull(policyClassNames);
        Assert.notNull(settings);

        List<T> policies = PolicyLoader.loadPolicies(policyClassNames, policyType, settings);
        if (policies.isEmpty()) {
            return;
        }

        if (replacePolicies) {
            builder.replacePolicies(policyType, policies);
            return;
        }

        for (T policy : policies) {
            builder.addPolicy(policyType, policy);
        }
    }

    private static List<String> collectCliCompatibilityWarnings(CompositionDescriptor compositionDescriptor) {
        List<String> warnings = new ArrayList<>();
        addUnsupportedWarning(
                warnings,
                compositionDescriptor.settingsCapabilityClass(),
                "CLI profiles do not support settingsCapabilityClass. Use test.settings directly."
        );
        addUnsupportedWarning(
                warnings,
                compositionDescriptor.testSessionCapabilityClass(),
                "CLI profiles do not support testSessionCapabilityClass."
        );
        addUnsupportedWarning(
                warnings,
                compositionDescriptor.testSequenceCapabilityClass(),
                "CLI profiles do not support testSequenceCapabilityClass."
        );
        addUnsupportedWarning(
                warnings,
                compositionDescriptor.stopCriteriaCapabilityClass(),
                "CLI profiles do not support stopCriteriaCapabilityClass."
        );
        addUnsupportedWarning(
                warnings,
                compositionDescriptor.oracleComposerClass(),
                "CLI profiles do not support oracleComposerClass."
        );
        return List.copyOf(warnings);
    }

    private static void addUnsupportedWarning(List<String> warnings,
                                              Optional<String> configuredClass,
                                              String message) {
        if (configuredClass.isPresent()) {
            warnings.add(message + " Ignoring configured class: " + configuredClass.get());
        }
    }

    private static String normalizeProfileName(String profileName) {
        return profileName == null ? "" : profileName.trim();
    }
}
