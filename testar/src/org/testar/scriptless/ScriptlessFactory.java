/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless;

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
import org.testar.scriptless.composition.ScriptlessCompositionDescriptor;
import org.testar.scriptless.composition.ScriptlessCompositionLoader;
import org.testar.scriptless.platform.AndroidScriptlessPlatformRuntime;
import org.testar.scriptless.platform.ScriptlessPlatformRuntime;
import org.testar.scriptless.platform.WebdriverScriptlessPlatformRuntime;
import org.testar.scriptless.platform.WindowsScriptlessPlatformRuntime;
import org.testar.scriptless.policy.ScriptlessPolicyDescriptor;
import org.testar.scriptless.policy.ScriptlessPolicyLoader;

import java.util.List;

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
        ScriptlessCompositionDescriptor compositionDescriptor = ScriptlessCompositionLoader
                .loadDescriptor(runtimeContext.settings());
        ScriptlessPolicyDescriptor policyDescriptor = ScriptlessPolicyLoader
                .loadDescriptor(runtimeContext.settings());
        ScriptlessPlatformRuntime platformRuntime = runtimeFor(sessionSpec);

        ServiceSessionConfiguration serviceConfiguration = platformRuntime
                .createServiceConfiguration(sessionSpec, runtimeContext);
        PlatformServices platformServices = PlatformOrchestrator.resolve(
                sessionSpec,
                buildPolicyConfiguration(runtimeContext, policyDescriptor),
                serviceConfiguration
        );
        SessionReportingManager sessionReportingManager = SessionReportingManager.deferred(sessionSpec.getTarget());

        return platformRuntime.createTestingServices(
                platformServices,
                runtimeContext,
                compositionDescriptor,
                sessionReportingManager
        );
    }

    public static ScriptlessCapabilities buildCapabilities(RuntimeContext runtimeContext) {
        Assert.notNull(runtimeContext);

        ScriptlessCompositionDescriptor compositionDescriptor = ScriptlessCompositionLoader
                .loadDescriptor(runtimeContext.settings());
        ScriptlessPlatformRuntime platformRuntime = runtimeFor(runtimeContext.settings());

        return platformRuntime.createCapabilities(
                runtimeContext,
                compositionDescriptor,
                ScriptlessCapabilities.defaults()
        );
    }

    private static PolicySessionConfiguration buildPolicyConfiguration(RuntimeContext runtimeContext,
                                                                      ScriptlessPolicyDescriptor policyDescriptor) {
        PolicySessionConfiguration.Builder builder = PolicySessionConfiguration.builder();

        applyPolicies(
                builder,
                ClickablePolicy.class,
                policyDescriptor.replaceClickablePolicies(),
                ScriptlessPolicyLoader.loadPolicies(
                        policyDescriptor.clickablePolicies(),
                        ClickablePolicy.class,
                        runtimeContext.settings()
                )
        );
        applyPolicies(
                builder,
                TypeablePolicy.class,
                policyDescriptor.replaceTypeablePolicies(),
                ScriptlessPolicyLoader.loadPolicies(
                        policyDescriptor.typeablePolicies(),
                        TypeablePolicy.class,
                        runtimeContext.settings()
                )
        );
        applyPolicies(
                builder,
                ScrollablePolicy.class,
                policyDescriptor.replaceScrollablePolicies(),
                ScriptlessPolicyLoader.loadPolicies(
                        policyDescriptor.scrollablePolicies(),
                        ScrollablePolicy.class,
                        runtimeContext.settings()
                )
        );
        applyPolicies(
                builder,
                SelectablePolicy.class,
                policyDescriptor.replaceSelectablePolicies(),
                ScriptlessPolicyLoader.loadPolicies(
                        policyDescriptor.selectablePolicies(),
                        SelectablePolicy.class,
                        runtimeContext.settings()
                )
        );
        applyPolicies(
                builder,
                EnabledPolicy.class,
                policyDescriptor.replaceEnabledPolicies(),
                ScriptlessPolicyLoader.loadPolicies(
                        policyDescriptor.enabledPolicies(),
                        EnabledPolicy.class,
                        runtimeContext.settings()
                )
        );
        applyPolicies(
                builder,
                BlockedPolicy.class,
                policyDescriptor.replaceBlockedPolicies(),
                ScriptlessPolicyLoader.loadPolicies(
                        policyDescriptor.blockedPolicies(),
                        BlockedPolicy.class,
                        runtimeContext.settings()
                )
        );
        applyPolicies(
                builder,
                WidgetFilterPolicy.class,
                policyDescriptor.replaceWidgetFilterPolicies(),
                ScriptlessPolicyLoader.loadPolicies(
                        policyDescriptor.widgetFilterPolicies(),
                        WidgetFilterPolicy.class,
                        runtimeContext.settings()
                )
        );
        applyPolicies(
                builder,
                VisiblePolicy.class,
                policyDescriptor.replaceVisiblePolicies(),
                ScriptlessPolicyLoader.loadPolicies(
                        policyDescriptor.visiblePolicies(),
                        VisiblePolicy.class,
                        runtimeContext.settings()
                )
        );
        applyPolicies(
                builder,
                TopLevelPolicy.class,
                policyDescriptor.replaceTopLevelPolicies(),
                ScriptlessPolicyLoader.loadPolicies(
                        policyDescriptor.topLevelPolicies(),
                        TopLevelPolicy.class,
                        runtimeContext.settings()
                )
        );

        return builder.build();
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
}
