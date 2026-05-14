/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless;

import org.testar.core.Assert;
import org.testar.core.action.resolver.ActionResolver;
import org.testar.core.service.ActionDerivationService;
import org.testar.core.service.ActionExecutionService;
import org.testar.core.service.ActionSelectorService;
import org.testar.core.service.OracleEvaluationService;
import org.testar.core.service.StateService;
import org.testar.core.service.SystemService;
import org.testar.engine.state.StateCompositionPlan;
import org.testar.plugin.OperatingSystems;
import org.testar.plugin.PlatformOrchestrator;
import org.testar.plugin.PlatformServices;
import org.testar.plugin.PlatformSessionSpec;
import org.testar.plugin.PlatformSessionSpecFactory;
import org.testar.plugin.configuration.PlatformDefaultSessionConfigurations;
import org.testar.plugin.configuration.SessionPolicyConfiguration;
import org.testar.plugin.configuration.SessionServiceConfiguration;
import org.testar.plugin.reporting.SessionReportingManager;
import org.testar.scriptless.capability.android.AndroidSettingsCapability;
import org.testar.scriptless.capability.android.AndroidTestSequenceCapability;
import org.testar.scriptless.capability.android.AndroidTestSessionCapability;
import org.testar.scriptless.capability.webdriver.WebdriverSettingsCapability;
import org.testar.scriptless.capability.webdriver.WebdriverTestSessionCapability;
import org.testar.scriptless.service.android.ScriptlessAndroidOracleComposer;
import org.testar.scriptless.service.webdriver.ScriptlessWebdriverActionDerivationService;
import org.testar.scriptless.service.webdriver.ScriptlessWebdriverActionSelectorService;
import org.testar.scriptless.service.webdriver.ScriptlessWebdriverStateService;
import org.testar.scriptless.service.webdriver.ScriptlessWebdriverOracleComposer;
import org.testar.scriptless.service.webdriver.ScriptlessWebdriverSystemService;
import org.testar.scriptless.service.windows.ScriptlessWindowsActionDerivationService;
import org.testar.scriptless.service.windows.ScriptlessWindowsStateService;
import org.testar.statemodel.StateModelManager;

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

    private ScriptlessFactory() {
    }

    public static TestingServices buildServices(RuntimeContext runtimeContext) {
        Assert.notNull(runtimeContext);

        // Start from the normal platform session specification derived from the
        // current TESTAR settings. This selects the default platform service stack.
        PlatformSessionSpec sessionSpec = PlatformSessionSpecFactory.fromSettings(runtimeContext.settings());
        SessionServiceConfiguration serviceConfiguration = SessionServiceConfiguration.defaults();

        if (sessionSpec.getOperatingSystem() == OperatingSystems.WEBDRIVER) {
            // WebDriver uses the shared platform services too, but scriptless
            // needs an extended state service around the default WebDriver state
            // service. The override keeps the normal WebDriver query/projection
            // plan and only replaces the wrapped state-fetching behavior.
            StateCompositionPlan defaultStatePlan = PlatformDefaultSessionConfigurations
                    .webdriverServiceConfiguration(sessionSpec)
                    .stateCompositionPlanOverride()
                    .orElseThrow();
            StateCompositionPlan scriptlessWebdriverStatePlan = new StateCompositionPlan(
                    new ScriptlessWebdriverStateService(defaultStatePlan.stateService(), runtimeContext),
                    defaultStatePlan::query
            );
            serviceConfiguration = SessionServiceConfiguration.builder()
                    .overrideStateCompositionPlan(scriptlessWebdriverStatePlan)
                    .build();
        } else if (sessionSpec.getOperatingSystem() == OperatingSystems.WINDOWS
                || sessionSpec.getOperatingSystem() == OperatingSystems.WINDOWS_10) {
            StateCompositionPlan defaultStatePlan = PlatformDefaultSessionConfigurations
                    .windowsServiceConfiguration(sessionSpec)
                    .stateCompositionPlanOverride()
                    .orElseThrow();
            StateCompositionPlan scriptlessWindowsStatePlan = new StateCompositionPlan(
                    new ScriptlessWindowsStateService(defaultStatePlan.stateService(), runtimeContext),
                    defaultStatePlan::query
            );
            serviceConfiguration = SessionServiceConfiguration.builder()
                    .overrideStateCompositionPlan(scriptlessWindowsStatePlan)
                    .build();
        }

        // Resolve the final platform service bundle from plugin/engine and then
        // add the reporting manager used by the scriptless runtime.
        PlatformServices platformServices = PlatformOrchestrator.resolve(
                sessionSpec,
                SessionPolicyConfiguration.defaults(),
                serviceConfiguration
        );
        SessionReportingManager sessionReportingManager = SessionReportingManager.create();

        if (sessionSpec.getOperatingSystem() == OperatingSystems.WEBDRIVER) {
            return webdriverTestingServices(platformServices, runtimeContext, sessionReportingManager);
        }
        if (sessionSpec.getOperatingSystem() == OperatingSystems.WINDOWS
                || sessionSpec.getOperatingSystem() == OperatingSystems.WINDOWS_10) {
            return windowsTestingServices(platformServices, sessionReportingManager);
        }

        return TestingServices.fromPlatformServices(platformServices, sessionReportingManager);
    }

    public static ScriptlessCapabilities buildCapabilities(RuntimeContext runtimeContext) {
        Assert.notNull(runtimeContext.settings());

        ScriptlessCapabilities capabilities = ScriptlessCapabilities.defaults();
        OperatingSystems operatingSystem = PlatformSessionSpecFactory
                .fromSettings(runtimeContext.settings())
                .getOperatingSystem();

        if (operatingSystem == OperatingSystems.WEBDRIVER) {
            return ScriptlessCapabilities.builder()
                    .withSettingsCapability(
                            new WebdriverSettingsCapability(capabilities.settingsCapability())
                    )
                    .withTestSessionCapability(
                            new WebdriverTestSessionCapability(capabilities.testSessionCapability())
                    )
                    .withTestSequenceCapability(capabilities.testSequenceCapability())
                    .withScriptlessOracleComposer(
                            new ScriptlessWebdriverOracleComposer(
                                    capabilities.scriptlessOracleComposer(),
                                    runtimeContext.settings()
                            )
                    )
                    .withStopCriteriaCapability(capabilities.stopCriteriaCapability())
                    .build();
        }

        if (operatingSystem == OperatingSystems.ANDROID) {
            return ScriptlessCapabilities.builder()
                    .withSettingsCapability(
                            new AndroidSettingsCapability(capabilities.settingsCapability())
                    )
                    .withTestSessionCapability(
                            new AndroidTestSessionCapability(capabilities.testSessionCapability())
                    )
                    .withTestSequenceCapability(
                            new AndroidTestSequenceCapability(capabilities.testSequenceCapability())
                    )
                    .withScriptlessOracleComposer(
                            new ScriptlessAndroidOracleComposer(capabilities.scriptlessOracleComposer())
                    )
                    .withStopCriteriaCapability(capabilities.stopCriteriaCapability())
                    .build();
        }

        return capabilities;
    }

    private static TestingServices webdriverTestingServices(PlatformServices platformServices,
                                                            RuntimeContext runtimeContext,
                                                            SessionReportingManager sessionReportingManager) {
        SystemService systemService = new ScriptlessWebdriverSystemService(
                platformServices.systemService(),
                runtimeContext
        );
        StateService stateService = platformServices.stateService();
        OracleEvaluationService oracleEvaluationService = platformServices.oracleEvaluationService();
        StateModelManager stateModelManager = platformServices.stateModelService();
        ActionDerivationService actionDerivationService = new ScriptlessWebdriverActionDerivationService(
                platformServices.actionDerivationService(),
                runtimeContext
        );
        ActionSelectorService actionSelectorService = new ScriptlessWebdriverActionSelectorService(
                platformServices.actionSelectorService(),
                runtimeContext
        );
        ActionResolver actionResolver = platformServices.actionResolver();
        ActionExecutionService actionExecutionService = platformServices.actionExecutionService();

        return new TestingServices(
                systemService,
                stateService,
                oracleEvaluationService,
                stateModelManager,
                actionDerivationService,
                actionSelectorService,
                actionResolver,
                actionExecutionService,
                sessionReportingManager
        );
    }

    private static TestingServices windowsTestingServices(PlatformServices platformServices,
                                                          SessionReportingManager sessionReportingManager) {
        SystemService systemService = platformServices.systemService();
        StateService stateService = platformServices.stateService();
        OracleEvaluationService oracleEvaluationService = platformServices.oracleEvaluationService();
        StateModelManager stateModelManager = platformServices.stateModelService();
        ActionDerivationService actionDerivationService = new ScriptlessWindowsActionDerivationService(
                platformServices.actionDerivationService()
        );
        ActionSelectorService actionSelectorService = platformServices.actionSelectorService();
        ActionResolver actionResolver = platformServices.actionResolver();
        ActionExecutionService actionExecutionService = platformServices.actionExecutionService();

        return new TestingServices(
                systemService,
                stateService,
                oracleEvaluationService,
                stateModelManager,
                actionDerivationService,
                actionSelectorService,
                actionResolver,
                actionExecutionService,
                sessionReportingManager
        );
    }
}
