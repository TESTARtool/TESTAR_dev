/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless;

import java.util.List;

import org.testar.config.composition.CompositionDescriptor;
import org.testar.config.composition.CompositionLoader;
import org.testar.config.policy.PolicyDescriptor;
import org.testar.config.policy.PolicyLoader;
import org.testar.config.ConfigTags;
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
import org.testar.plugin.OperatingSystems;
import org.testar.plugin.PlatformOrchestrator;
import org.testar.plugin.PlatformServices;
import org.testar.plugin.configuration.PlatformSessionSpecification;
import org.testar.plugin.PlatformSessionSpecFactory;
import org.testar.plugin.configuration.PolicySessionConfiguration;
import org.testar.plugin.configuration.ServiceSessionConfiguration;
import org.testar.plugin.reporting.SessionReportingManager;
import org.testar.scriptless.capability.SettingsCapability;
import org.testar.scriptless.platform.AndroidScriptlessPlatformRuntime;
import org.testar.scriptless.platform.ScriptlessPlatformRuntime;
import org.testar.scriptless.platform.WebdriverScriptlessPlatformRuntime;
import org.testar.scriptless.platform.WindowsScriptlessPlatformRuntime;

/**
 * Builds the two composition layers used by the scriptless runtime:
 *
 * 1. runtime services
 *    Shared operational services such as system, state, action derivation,
 *    selection, execution, and oracle evaluation. These come from the platform
 *    architecture assembled by {@code plugin} and {@code engine}.
 *
 * 2. scriptless capabilities
 *    TESTAR runtime behaviors such as settings setup, test-session hooks,
 *    test-sequence hooks, stop criteria, and scriptless-side oracle composition.
 *
 * The scriptless module does not rebuild the whole platform stack itself.
 * Instead, it starts from the default platform/session composition and applies
 * only the scriptless-specific overrides that are needed for a concrete runtime.
 */
public final class ScriptlessFactory {

    private static final ScriptlessPlatformRuntime WEBDRIVER_RUNTIME = new WebdriverScriptlessPlatformRuntime();
    private static final ScriptlessPlatformRuntime WINDOWS_RUNTIME = new WindowsScriptlessPlatformRuntime();
    private static final ScriptlessPlatformRuntime ANDROID_RUNTIME = new AndroidScriptlessPlatformRuntime();

    private ScriptlessFactory() {
    }

    public static TestingServices buildServices(RuntimeContext runtimeContext) {
        Assert.notNull(runtimeContext);

        PlatformSessionSpecification sessionSpec = PlatformSessionSpecFactory.fromSettings(runtimeContext.settings());
        CompositionDescriptor compositionDescriptor = loadCompositionDescriptor(runtimeContext);
        PolicyDescriptor policyDescriptor = loadPolicyDescriptor(runtimeContext);
        ScriptlessPlatformRuntime platformRuntime = runtimeFor(sessionSpec);

        ServiceSessionConfiguration serviceConfiguration = platformRuntime
                .createServiceConfiguration(sessionSpec, runtimeContext, compositionDescriptor);
        // Scriptless reuses the shared plugin resolution path and only supplies its session-specific overrides.
        PlatformServices platformServices = resolvePlatformServices(
                runtimeContext,
                sessionSpec,
                policyDescriptor,
                serviceConfiguration
        );
        // Reporting becomes fully active when a sequence starts, but the runtime keeps one manager instance throughout.
        SessionReportingManager sessionReportingManager = SessionReportingManager.deferred(sessionSpec.getTarget());

        return platformRuntime.createTestingServices(
                platformServices,
                runtimeContext,
                compositionDescriptor,
                sessionReportingManager
        );
    }

    public static SettingsCapability buildSettingsCapability(RuntimeContext runtimeContext) {
        Assert.notNull(runtimeContext);

        CompositionDescriptor compositionDescriptor = loadCompositionDescriptor(runtimeContext);
        ScriptlessPlatformRuntime platformRuntime = runtimeFor(runtimeContext.settings());

        return platformRuntime.createSettingsCapability(runtimeContext, compositionDescriptor);
    }

    public static ScriptlessCapabilities buildScriptlessCapabilities(RuntimeContext runtimeContext) {
        Assert.notNull(runtimeContext);

        CompositionDescriptor compositionDescriptor = loadCompositionDescriptor(runtimeContext);
        ScriptlessPlatformRuntime platformRuntime = runtimeFor(runtimeContext.settings());

        return platformRuntime.createCapabilities(
                runtimeContext,
                compositionDescriptor,
                ScriptlessCapabilities.defaults()
        );
    }

    private static PlatformServices resolvePlatformServices(RuntimeContext runtimeContext,
                                                            PlatformSessionSpecification sessionSpec,
                                                            PolicyDescriptor policyDescriptor,
                                                            ServiceSessionConfiguration serviceConfiguration) {
        return PlatformOrchestrator.resolve(
                sessionSpec,
                buildPolicyConfiguration(runtimeContext, policyDescriptor),
                serviceConfiguration
        );
    }

    private static PolicySessionConfiguration buildPolicyConfiguration(RuntimeContext runtimeContext,
                                                                      PolicyDescriptor policyDescriptor) {
        PolicySessionConfiguration.Builder builder = PolicySessionConfiguration.builder();

        loadAndApplyPolicies(
                builder,
                ClickablePolicy.class,
                policyDescriptor.replaceClickablePolicies(),
                policyDescriptor.clickablePolicies(),
                runtimeContext
        );
        loadAndApplyPolicies(
                builder,
                TypeablePolicy.class,
                policyDescriptor.replaceTypeablePolicies(),
                policyDescriptor.typeablePolicies(),
                runtimeContext
        );
        loadAndApplyPolicies(
                builder,
                ScrollablePolicy.class,
                policyDescriptor.replaceScrollablePolicies(),
                policyDescriptor.scrollablePolicies(),
                runtimeContext
        );
        loadAndApplyPolicies(
                builder,
                SelectablePolicy.class,
                policyDescriptor.replaceSelectablePolicies(),
                policyDescriptor.selectablePolicies(),
                runtimeContext
        );
        loadAndApplyPolicies(
                builder,
                EnabledPolicy.class,
                policyDescriptor.replaceEnabledPolicies(),
                policyDescriptor.enabledPolicies(),
                runtimeContext
        );
        loadAndApplyPolicies(
                builder,
                BlockedPolicy.class,
                policyDescriptor.replaceBlockedPolicies(),
                policyDescriptor.blockedPolicies(),
                runtimeContext
        );
        loadAndApplyPolicies(
                builder,
                WidgetFilterPolicy.class,
                policyDescriptor.replaceWidgetFilterPolicies(),
                policyDescriptor.widgetFilterPolicies(),
                runtimeContext
        );
        loadAndApplyPolicies(
                builder,
                VisiblePolicy.class,
                policyDescriptor.replaceVisiblePolicies(),
                policyDescriptor.visiblePolicies(),
                runtimeContext
        );
        loadAndApplyPolicies(
                builder,
                TopLevelPolicy.class,
                policyDescriptor.replaceTopLevelPolicies(),
                policyDescriptor.topLevelPolicies(),
                runtimeContext
        );

        return builder.build();
    }

    private static <T extends Policy> void loadAndApplyPolicies(PolicySessionConfiguration.Builder builder,
                                                                Class<T> policyType,
                                                                boolean replaceBuiltIns,
                                                                List<String> policyClassNames,
                                                                RuntimeContext runtimeContext) {
        List<T> policies = PolicyLoader.loadPolicies(
                policyClassNames,
                policyType,
                runtimeContext.settings()
        );
        applyPolicies(builder, policyType, replaceBuiltIns, policies);
    }

    private static <T extends Policy> void applyPolicies(PolicySessionConfiguration.Builder builder,
                                                         Class<T> policyType,
                                                         boolean replaceBuiltIns,
                                                         List<T> policies) {
        if (replaceBuiltIns) {
            builder.replacePolicies(policyType, policies);
            return;
        }

        for (T policy : policies) {
            builder.addPolicy(policyType, policy);
        }
    }

    private static ScriptlessPlatformRuntime runtimeFor(PlatformSessionSpecification sessionSpec) {
        OperatingSystems operatingSystem = sessionSpec.getOperatingSystem();
        if (operatingSystem == OperatingSystems.WEBDRIVER) {
            return WEBDRIVER_RUNTIME;
        }
        if (operatingSystem == OperatingSystems.ANDROID) {
            return ANDROID_RUNTIME;
        }
        if (operatingSystem == OperatingSystems.WINDOWS || operatingSystem == OperatingSystems.WINDOWS_10) {
            return WINDOWS_RUNTIME;
        }
        return WINDOWS_RUNTIME;
    }

    private static ScriptlessPlatformRuntime runtimeFor(Settings settings) {
        String sutConnector = settings.get(ConfigTags.SUTConnector, "");
        if (Settings.SUT_CONNECTOR_WEBDRIVER.equals(sutConnector)) {
            return WEBDRIVER_RUNTIME;
        }
        if (Settings.SUT_CONNECTOR_ANDROID_APPIUM.equals(sutConnector)) {
            return ANDROID_RUNTIME;
        }
        return WINDOWS_RUNTIME;
    }

    private static CompositionDescriptor loadCompositionDescriptor(RuntimeContext runtimeContext) {
        return CompositionLoader.loadDescriptor(runtimeContext.settings());
    }

    private static PolicyDescriptor loadPolicyDescriptor(RuntimeContext runtimeContext) {
        return PolicyLoader.loadDescriptor(runtimeContext.settings());
    }
}
