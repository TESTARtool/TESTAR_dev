/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless;

import org.testar.core.Assert;
import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.core.action.resolver.ActionResolver;
import org.testar.core.policy.BlockedPolicy;
import org.testar.core.policy.ClickablePolicy;
import org.testar.core.policy.EnabledPolicy;
import org.testar.core.policy.ScrollablePolicy;
import org.testar.core.policy.SelectablePolicy;
import org.testar.core.policy.TopLevelPolicy;
import org.testar.core.policy.TypeablePolicy;
import org.testar.core.policy.VisiblePolicy;
import org.testar.core.policy.WidgetFilterPolicy;
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
import org.testar.scriptless.capability.android.AndroidStopCriteriaCapability;
import org.testar.scriptless.capability.android.AndroidTestSequenceCapability;
import org.testar.scriptless.capability.android.AndroidTestSessionCapability;
import org.testar.scriptless.capability.SettingsCapability;
import org.testar.scriptless.capability.StopCriteriaCapability;
import org.testar.scriptless.capability.TestSequenceCapability;
import org.testar.scriptless.capability.TestSessionCapability;
import org.testar.scriptless.capability.webdriver.WebdriverStopCriteriaCapability;
import org.testar.scriptless.capability.webdriver.WebdriverTestSequenceCapability;
import org.testar.scriptless.capability.webdriver.WebdriverSettingsCapability;
import org.testar.scriptless.capability.webdriver.WebdriverTestSessionCapability;
import org.testar.scriptless.capability.windows.WindowsSettingsCapability;
import org.testar.scriptless.capability.windows.WindowsStopCriteriaCapability;
import org.testar.scriptless.capability.windows.WindowsTestSequenceCapability;
import org.testar.scriptless.capability.windows.WindowsTestSessionCapability;
import org.testar.scriptless.composition.ScriptlessCompositionDescriptor;
import org.testar.scriptless.composition.ScriptlessCompositionLoader;
import org.testar.scriptless.policy.ScriptlessPolicyDescriptor;
import org.testar.scriptless.policy.ScriptlessPolicyLoader;
import org.testar.scriptless.service.android.ScriptlessAndroidActionDerivationService;
import org.testar.scriptless.service.android.ScriptlessAndroidActionExecutionService;
import org.testar.scriptless.service.android.ScriptlessAndroidActionSelectorService;
import org.testar.scriptless.service.android.ScriptlessAndroidOracleComposer;
import org.testar.scriptless.service.android.ScriptlessAndroidStateService;
import org.testar.scriptless.service.android.ScriptlessAndroidSystemService;
import org.testar.scriptless.service.ScriptlessOracleComposer;
import org.testar.scriptless.service.webdriver.ScriptlessWebdriverActionDerivationService;
import org.testar.scriptless.service.webdriver.ScriptlessWebdriverActionExecutionService;
import org.testar.scriptless.service.webdriver.ScriptlessWebdriverActionSelectorService;
import org.testar.scriptless.service.webdriver.ScriptlessWebdriverStateService;
import org.testar.scriptless.service.webdriver.ScriptlessWebdriverOracleComposer;
import org.testar.scriptless.service.webdriver.ScriptlessWebdriverSystemService;
import org.testar.scriptless.service.windows.ScriptlessWindowsActionDerivationService;
import org.testar.scriptless.service.windows.ScriptlessWindowsActionExecutionService;
import org.testar.scriptless.service.windows.ScriptlessWindowsActionSelectorService;
import org.testar.scriptless.service.windows.ScriptlessWindowsOracleComposer;
import org.testar.scriptless.service.windows.ScriptlessWindowsStateService;
import org.testar.scriptless.service.windows.ScriptlessWindowsSystemService;
import org.testar.statemodel.StateModelManager;

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

    private ScriptlessFactory() {
    }

    public static TestingServices buildServices(RuntimeContext runtimeContext) {
        Assert.notNull(runtimeContext);

        // Start from the normal platform session specification derived from the
        // current TESTAR settings. This selects the default platform service stack.
        PlatformSessionSpec sessionSpec = PlatformSessionSpecFactory.fromSettings(runtimeContext.settings());
        ScriptlessCompositionDescriptor compositionDescriptor = ScriptlessCompositionLoader
                .loadDescriptor(runtimeContext.settings());
        ScriptlessPolicyDescriptor policyDescriptor = ScriptlessPolicyLoader
                .loadDescriptor(runtimeContext.settings());
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
                buildPolicyConfiguration(runtimeContext, policyDescriptor),
                serviceConfiguration
        );
        SessionReportingManager sessionReportingManager = SessionReportingManager.create();

        TestingServices testingServices;
        if (sessionSpec.getOperatingSystem() == OperatingSystems.WEBDRIVER) {
            testingServices = webdriverTestingServices(
                    platformServices,
                    runtimeContext,
                    compositionDescriptor,
                    sessionReportingManager
            );
        } else if (sessionSpec.getOperatingSystem() == OperatingSystems.ANDROID) {
            testingServices = androidTestingServices(
                    platformServices,
                    runtimeContext,
                    compositionDescriptor,
                    sessionReportingManager
            );
        } else if (sessionSpec.getOperatingSystem() == OperatingSystems.WINDOWS
                || sessionSpec.getOperatingSystem() == OperatingSystems.WINDOWS_10) {
            testingServices = windowsTestingServices(
                    platformServices,
                    runtimeContext,
                    compositionDescriptor,
                    sessionReportingManager
            );
        } else {
            testingServices = TestingServices.fromPlatformServices(platformServices, sessionReportingManager);
        }

        return testingServices;
    }

    public static ScriptlessCapabilities buildCapabilities(RuntimeContext runtimeContext) {
        Assert.notNull(runtimeContext.settings());

        ScriptlessCapabilities capabilities = ScriptlessCapabilities.defaults();
        ScriptlessCompositionDescriptor compositionDescriptor = ScriptlessCompositionLoader
                .loadDescriptor(runtimeContext.settings());
        String sutConnector = runtimeContext.settings().get(ConfigTags.SUTConnector, "");

        if (Settings.SUT_CONNECTOR_WEBDRIVER.equals(sutConnector)) {
            capabilities = webdriverCapabilities(runtimeContext, compositionDescriptor, capabilities);
        } else if (Settings.SUT_CONNECTOR_ANDROID_APPIUM.equals(sutConnector)) {
            capabilities = androidCapabilities(runtimeContext, compositionDescriptor, capabilities);
        } else {
            capabilities = windowsCapabilities(runtimeContext, compositionDescriptor, capabilities);
        }

        return capabilities;
    }

    private static SessionPolicyConfiguration buildPolicyConfiguration(RuntimeContext runtimeContext,
                                                                      ScriptlessPolicyDescriptor policyDescriptor) {
        SessionPolicyConfiguration.Builder builder = SessionPolicyConfiguration.builder();

        List<ClickablePolicy> clickablePolicies = ScriptlessPolicyLoader.loadPolicies(
                policyDescriptor.clickablePolicies(),
                ClickablePolicy.class,
                runtimeContext.settings()
        );
        if (policyDescriptor.replaceClickablePolicies()) {
            builder.replacePolicies(ClickablePolicy.class, clickablePolicies);
        } else {
            for (ClickablePolicy policy : clickablePolicies) {
                builder.addPolicy(ClickablePolicy.class, policy);
            }
        }

        List<TypeablePolicy> typeablePolicies = ScriptlessPolicyLoader.loadPolicies(
                policyDescriptor.typeablePolicies(),
                TypeablePolicy.class,
                runtimeContext.settings()
        );
        if (policyDescriptor.replaceTypeablePolicies()) {
            builder.replacePolicies(TypeablePolicy.class, typeablePolicies);
        } else {
            for (TypeablePolicy policy : typeablePolicies) {
                builder.addPolicy(TypeablePolicy.class, policy);
            }
        }

        List<ScrollablePolicy> scrollablePolicies = ScriptlessPolicyLoader.loadPolicies(
                policyDescriptor.scrollablePolicies(),
                ScrollablePolicy.class,
                runtimeContext.settings()
        );
        if (policyDescriptor.replaceScrollablePolicies()) {
            builder.replacePolicies(ScrollablePolicy.class, scrollablePolicies);
        } else {
            for (ScrollablePolicy policy : scrollablePolicies) {
                builder.addPolicy(ScrollablePolicy.class, policy);
            }
        }

        List<SelectablePolicy> selectablePolicies = ScriptlessPolicyLoader.loadPolicies(
                policyDescriptor.selectablePolicies(),
                SelectablePolicy.class,
                runtimeContext.settings()
        );
        if (policyDescriptor.replaceSelectablePolicies()) {
            builder.replacePolicies(SelectablePolicy.class, selectablePolicies);
        } else {
            for (SelectablePolicy policy : selectablePolicies) {
                builder.addPolicy(SelectablePolicy.class, policy);
            }
        }

        List<EnabledPolicy> enabledPolicies = ScriptlessPolicyLoader.loadPolicies(
                policyDescriptor.enabledPolicies(),
                EnabledPolicy.class,
                runtimeContext.settings()
        );
        if (policyDescriptor.replaceEnabledPolicies()) {
            builder.replacePolicies(EnabledPolicy.class, enabledPolicies);
        } else {
            for (EnabledPolicy policy : enabledPolicies) {
                builder.addPolicy(EnabledPolicy.class, policy);
            }
        }

        List<BlockedPolicy> blockedPolicies = ScriptlessPolicyLoader.loadPolicies(
                policyDescriptor.blockedPolicies(),
                BlockedPolicy.class,
                runtimeContext.settings()
        );
        if (policyDescriptor.replaceBlockedPolicies()) {
            builder.replacePolicies(BlockedPolicy.class, blockedPolicies);
        } else {
            for (BlockedPolicy policy : blockedPolicies) {
                builder.addPolicy(BlockedPolicy.class, policy);
            }
        }

        List<WidgetFilterPolicy> widgetFilterPolicies = ScriptlessPolicyLoader.loadPolicies(
                policyDescriptor.widgetFilterPolicies(),
                WidgetFilterPolicy.class,
                runtimeContext.settings()
        );
        if (policyDescriptor.replaceWidgetFilterPolicies()) {
            builder.replacePolicies(WidgetFilterPolicy.class, widgetFilterPolicies);
        } else {
            for (WidgetFilterPolicy policy : widgetFilterPolicies) {
                builder.addPolicy(WidgetFilterPolicy.class, policy);
            }
        }

        List<VisiblePolicy> visiblePolicies = ScriptlessPolicyLoader.loadPolicies(
                policyDescriptor.visiblePolicies(),
                VisiblePolicy.class,
                runtimeContext.settings()
        );
        if (policyDescriptor.replaceVisiblePolicies()) {
            builder.replacePolicies(VisiblePolicy.class, visiblePolicies);
        } else {
            for (VisiblePolicy policy : visiblePolicies) {
                builder.addPolicy(VisiblePolicy.class, policy);
            }
        }

        List<TopLevelPolicy> topLevelPolicies = ScriptlessPolicyLoader.loadPolicies(
                policyDescriptor.topLevelPolicies(),
                TopLevelPolicy.class,
                runtimeContext.settings()
        );
        if (policyDescriptor.replaceTopLevelPolicies()) {
            builder.replacePolicies(TopLevelPolicy.class, topLevelPolicies);
        } else {
            for (TopLevelPolicy policy : topLevelPolicies) {
                builder.addPolicy(TopLevelPolicy.class, policy);
            }
        }

        return builder.build();
    }

    private static TestingServices webdriverTestingServices(PlatformServices platformServices,
                                                            RuntimeContext runtimeContext,
                                                            ScriptlessCompositionDescriptor compositionDescriptor,
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
                stateModelManager,
                platformServices.actionSelectorService()
        );
        ActionResolver actionResolver = platformServices.actionResolver();
        ActionExecutionService actionExecutionService = new ScriptlessWebdriverActionExecutionService(
                platformServices.actionExecutionService()
        );

        systemService = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.systemServiceClass(),
                SystemService.class,
                systemService,
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        );
        stateService = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.stateServiceClass(),
                StateService.class,
                stateService,
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        );
        actionDerivationService = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.actionDerivationServiceClass(),
                ActionDerivationService.class,
                actionDerivationService,
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        );
        actionSelectorService = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.actionSelectorServiceClass(),
                ActionSelectorService.class,
                actionSelectorService,
                new Object[]{stateModelManager},
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        );
        actionExecutionService = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.actionExecutionServiceClass(),
                ActionExecutionService.class,
                actionExecutionService,
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        );

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

    private static ScriptlessCapabilities webdriverCapabilities(RuntimeContext runtimeContext,
                                                                ScriptlessCompositionDescriptor compositionDescriptor,
                                                                ScriptlessCapabilities capabilities) {
        SettingsCapability settingsCapability = new WebdriverSettingsCapability(
                capabilities.settingsCapability()
        );
        TestSessionCapability testSessionCapability = new WebdriverTestSessionCapability(
                capabilities.testSessionCapability()
        );
        TestSequenceCapability testSequenceCapability = new WebdriverTestSequenceCapability(
                capabilities.testSequenceCapability()
        );
        ScriptlessOracleComposer scriptlessOracleComposer = new ScriptlessWebdriverOracleComposer(
                capabilities.scriptlessOracleComposer(),
                runtimeContext.settings()
        );
        StopCriteriaCapability stopCriteriaCapability = new WebdriverStopCriteriaCapability(
                capabilities.stopCriteriaCapability()
        );

        settingsCapability = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.settingsCapabilityClass(),
                SettingsCapability.class,
                settingsCapability,
                new Object[]{runtimeContext.settings()},
                new Object[]{runtimeContext},
                new Object[]{}
        );
        testSessionCapability = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.testSessionCapabilityClass(),
                TestSessionCapability.class,
                testSessionCapability,
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        );
        testSequenceCapability = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.testSequenceCapabilityClass(),
                TestSequenceCapability.class,
                testSequenceCapability,
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        );
        scriptlessOracleComposer = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.oracleComposerClass(),
                ScriptlessOracleComposer.class,
                scriptlessOracleComposer,
                new Object[]{runtimeContext.settings()},
                new Object[]{runtimeContext},
                new Object[]{}
        );
        stopCriteriaCapability = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.stopCriteriaCapabilityClass(),
                StopCriteriaCapability.class,
                stopCriteriaCapability,
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        );

        return ScriptlessCapabilities.builder()
                .withSettingsCapability(settingsCapability)
                .withTestSessionCapability(testSessionCapability)
                .withTestSequenceCapability(testSequenceCapability)
                .withScriptlessOracleComposer(scriptlessOracleComposer)
                .withStopCriteriaCapability(stopCriteriaCapability)
                .build();
    }

    private static TestingServices windowsTestingServices(PlatformServices platformServices,
                                                          RuntimeContext runtimeContext,
                                                          ScriptlessCompositionDescriptor compositionDescriptor,
                                                          SessionReportingManager sessionReportingManager) {
        SystemService systemService = new ScriptlessWindowsSystemService(
                platformServices.systemService(),
                runtimeContext
        );
        StateService stateService = platformServices.stateService();
        OracleEvaluationService oracleEvaluationService = platformServices.oracleEvaluationService();
        StateModelManager stateModelManager = platformServices.stateModelService();
        ActionDerivationService actionDerivationService = new ScriptlessWindowsActionDerivationService(
                platformServices.actionDerivationService()
        );
        ActionSelectorService actionSelectorService = new ScriptlessWindowsActionSelectorService(
                stateModelManager,
                platformServices.actionSelectorService()
        );
        ActionResolver actionResolver = platformServices.actionResolver();
        ActionExecutionService actionExecutionService = new ScriptlessWindowsActionExecutionService(
                platformServices.actionExecutionService()
        );

        systemService = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.systemServiceClass(),
                SystemService.class,
                systemService,
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        );
        stateService = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.stateServiceClass(),
                StateService.class,
                stateService,
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        );
        actionDerivationService = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.actionDerivationServiceClass(),
                ActionDerivationService.class,
                actionDerivationService,
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        );
        actionSelectorService = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.actionSelectorServiceClass(),
                ActionSelectorService.class,
                actionSelectorService,
                new Object[]{stateModelManager},
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        );
        actionExecutionService = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.actionExecutionServiceClass(),
                ActionExecutionService.class,
                actionExecutionService,
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        );

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

    private static TestingServices androidTestingServices(PlatformServices platformServices,
                                                          RuntimeContext runtimeContext,
                                                          ScriptlessCompositionDescriptor compositionDescriptor,
                                                          SessionReportingManager sessionReportingManager) {
        SystemService systemService = new ScriptlessAndroidSystemService(
                platformServices.systemService(),
                runtimeContext
        );
        StateService stateService = new ScriptlessAndroidStateService(
                platformServices.stateService()
        );
        OracleEvaluationService oracleEvaluationService = platformServices.oracleEvaluationService();
        StateModelManager stateModelManager = platformServices.stateModelService();
        ActionDerivationService actionDerivationService = new ScriptlessAndroidActionDerivationService(
                platformServices.actionDerivationService()
        );
        ActionSelectorService actionSelectorService = new ScriptlessAndroidActionSelectorService(
                stateModelManager,
                platformServices.actionSelectorService()
        );
        ActionResolver actionResolver = platformServices.actionResolver();
        ActionExecutionService actionExecutionService = new ScriptlessAndroidActionExecutionService(
                platformServices.actionExecutionService()
        );

        systemService = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.systemServiceClass(),
                SystemService.class,
                systemService,
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        );
        stateService = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.stateServiceClass(),
                StateService.class,
                stateService,
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        );
        actionDerivationService = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.actionDerivationServiceClass(),
                ActionDerivationService.class,
                actionDerivationService,
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        );
        actionSelectorService = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.actionSelectorServiceClass(),
                ActionSelectorService.class,
                actionSelectorService,
                new Object[]{stateModelManager},
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        );
        actionExecutionService = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.actionExecutionServiceClass(),
                ActionExecutionService.class,
                actionExecutionService,
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        );

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

    private static ScriptlessCapabilities windowsCapabilities(RuntimeContext runtimeContext,
                                                              ScriptlessCompositionDescriptor compositionDescriptor,
                                                              ScriptlessCapabilities capabilities) {
        SettingsCapability settingsCapability = new WindowsSettingsCapability(
                capabilities.settingsCapability()
        );
        TestSessionCapability testSessionCapability = new WindowsTestSessionCapability(
                capabilities.testSessionCapability()
        );
        TestSequenceCapability testSequenceCapability = new WindowsTestSequenceCapability(
                capabilities.testSequenceCapability()
        );
        ScriptlessOracleComposer scriptlessOracleComposer = new ScriptlessWindowsOracleComposer(
                capabilities.scriptlessOracleComposer()
        );
        StopCriteriaCapability stopCriteriaCapability = new WindowsStopCriteriaCapability(
                capabilities.stopCriteriaCapability()
        );

        settingsCapability = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.settingsCapabilityClass(),
                SettingsCapability.class,
                settingsCapability,
                new Object[]{runtimeContext.settings()},
                new Object[]{runtimeContext},
                new Object[]{}
        );
        testSessionCapability = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.testSessionCapabilityClass(),
                TestSessionCapability.class,
                testSessionCapability,
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        );
        testSequenceCapability = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.testSequenceCapabilityClass(),
                TestSequenceCapability.class,
                testSequenceCapability,
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        );
        scriptlessOracleComposer = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.oracleComposerClass(),
                ScriptlessOracleComposer.class,
                scriptlessOracleComposer,
                new Object[]{runtimeContext.settings()},
                new Object[]{runtimeContext},
                new Object[]{}
        );
        stopCriteriaCapability = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.stopCriteriaCapabilityClass(),
                StopCriteriaCapability.class,
                stopCriteriaCapability,
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        );

        return ScriptlessCapabilities.builder()
                .withSettingsCapability(settingsCapability)
                .withTestSessionCapability(testSessionCapability)
                .withTestSequenceCapability(testSequenceCapability)
                .withScriptlessOracleComposer(scriptlessOracleComposer)
                .withStopCriteriaCapability(stopCriteriaCapability)
                .build();
    }

    private static ScriptlessCapabilities androidCapabilities(RuntimeContext runtimeContext,
                                                              ScriptlessCompositionDescriptor compositionDescriptor,
                                                              ScriptlessCapabilities capabilities) {
        SettingsCapability settingsCapability = new AndroidSettingsCapability(
                capabilities.settingsCapability()
        );
        TestSessionCapability testSessionCapability = new AndroidTestSessionCapability(
                capabilities.testSessionCapability()
        );
        TestSequenceCapability testSequenceCapability = new AndroidTestSequenceCapability(
                capabilities.testSequenceCapability()
        );
        ScriptlessOracleComposer scriptlessOracleComposer = new ScriptlessAndroidOracleComposer(
                capabilities.scriptlessOracleComposer()
        );
        StopCriteriaCapability stopCriteriaCapability = new AndroidStopCriteriaCapability(
                capabilities.stopCriteriaCapability()
        );

        settingsCapability = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.settingsCapabilityClass(),
                SettingsCapability.class,
                settingsCapability,
                new Object[]{runtimeContext.settings()},
                new Object[]{runtimeContext},
                new Object[]{}
        );
        testSessionCapability = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.testSessionCapabilityClass(),
                TestSessionCapability.class,
                testSessionCapability,
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        );
        testSequenceCapability = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.testSequenceCapabilityClass(),
                TestSequenceCapability.class,
                testSequenceCapability,
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        );
        scriptlessOracleComposer = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.oracleComposerClass(),
                ScriptlessOracleComposer.class,
                scriptlessOracleComposer,
                new Object[]{runtimeContext.settings()},
                new Object[]{runtimeContext},
                new Object[]{}
        );
        stopCriteriaCapability = ScriptlessCompositionLoader.loadDelegateWrapper(
                compositionDescriptor.stopCriteriaCapabilityClass(),
                StopCriteriaCapability.class,
                stopCriteriaCapability,
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        );

        return ScriptlessCapabilities.builder()
                .withSettingsCapability(settingsCapability)
                .withTestSessionCapability(testSessionCapability)
                .withTestSequenceCapability(testSequenceCapability)
                .withScriptlessOracleComposer(scriptlessOracleComposer)
                .withStopCriteriaCapability(stopCriteriaCapability)
                .build();
    }
}
