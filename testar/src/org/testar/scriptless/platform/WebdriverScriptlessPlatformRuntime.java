/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.platform;

import org.testar.config.composition.CompositionDescriptor;
import org.testar.core.action.resolver.ActionResolver;
import org.testar.core.service.ActionDerivationService;
import org.testar.core.service.ActionExecutionService;
import org.testar.core.service.ActionSelectorService;
import org.testar.core.service.OracleEvaluationService;
import org.testar.core.service.StateService;
import org.testar.core.service.SystemService;
import org.testar.engine.state.StateCompositionPlan;
import org.testar.plugin.PlatformServices;
import org.testar.plugin.configuration.PlatformSessionSpecification;
import org.testar.plugin.configuration.PlatformDefaultSessionConfigurations;
import org.testar.plugin.configuration.ServiceSessionConfiguration;
import org.testar.plugin.reporting.SessionReportingManager;
import org.testar.scriptless.RuntimeContext;
import org.testar.scriptless.ScriptlessCapabilities;
import org.testar.scriptless.TestingServices;
import org.testar.scriptless.capability.SettingsCapability;
import org.testar.scriptless.capability.StopCriteriaCapability;
import org.testar.scriptless.capability.TestSequenceCapability;
import org.testar.scriptless.capability.TestSessionCapability;
import org.testar.scriptless.capability.webdriver.WebdriverSettingsCapability;
import org.testar.scriptless.capability.webdriver.WebdriverStopCriteriaCapability;
import org.testar.scriptless.capability.webdriver.WebdriverTestSequenceCapability;
import org.testar.scriptless.capability.webdriver.WebdriverTestSessionCapability;
import org.testar.scriptless.service.ScriptlessOracleComposer;
import org.testar.scriptless.service.webdriver.ScriptlessWebdriverActionDerivationService;
import org.testar.scriptless.service.webdriver.ScriptlessWebdriverActionExecutionService;
import org.testar.scriptless.service.webdriver.ScriptlessWebdriverActionSelectorService;
import org.testar.scriptless.service.webdriver.ScriptlessWebdriverOracleComposer;
import org.testar.scriptless.service.webdriver.ScriptlessWebdriverStateService;
import org.testar.scriptless.service.webdriver.ScriptlessWebdriverSystemService;
import org.testar.statemodel.StateModelManager;

public final class WebdriverScriptlessPlatformRuntime extends AbstractScriptlessPlatformRuntime {

    @Override
    public ServiceSessionConfiguration createServiceConfiguration(PlatformSessionSpecification sessionSpec,
                                                                 RuntimeContext runtimeContext,
                                                                 CompositionDescriptor compositionDescriptor) {
        StateCompositionPlan defaultStatePlan = PlatformDefaultSessionConfigurations
                .webdriverServiceConfiguration(sessionSpec)
                .stateCompositionPlanOverride()
                .orElseThrow();
        StateCompositionPlan scriptlessStatePlan = new StateCompositionPlan(
                new ScriptlessWebdriverStateService(defaultStatePlan.stateService(), runtimeContext),
                defaultStatePlan::query
        );
        return serviceConfigurationBuilder(runtimeContext, compositionDescriptor)
                .overrideStateCompositionPlan(scriptlessStatePlan)
                .build();
    }

    @Override
    public TestingServices createTestingServices(PlatformServices platformServices,
                                                 RuntimeContext runtimeContext,
                                                 CompositionDescriptor compositionDescriptor,
                                                 SessionReportingManager sessionReportingManager) {
        SystemService systemService = new ScriptlessWebdriverSystemService(
                platformServices.systemService(),
                runtimeContext
        );
        StateService stateService = platformServices.stateService();
        OracleEvaluationService oracleEvaluationService = platformServices.oracleEvaluationService();
        StateModelManager stateModelManager = platformServices.stateModelManager();
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
        ServiceBundle serviceBundle = serviceBundle(
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
        return testingServices(wrapServices(serviceBundle, runtimeContext, compositionDescriptor));
    }

    @Override
    public ScriptlessCapabilities createCapabilities(RuntimeContext runtimeContext,
                                                     CompositionDescriptor compositionDescriptor,
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
        CapabilityBundle capabilityBundle = capabilityBundle(
                settingsCapability,
                testSessionCapability,
                testSequenceCapability,
                scriptlessOracleComposer,
                stopCriteriaCapability
        );
        CapabilityBundle wrappedCapabilityBundle = wrapCapabilities(
                capabilityBundle,
                runtimeContext,
                compositionDescriptor
        );
        return capabilities(wrappedCapabilityBundle);
    }
}
