/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.platform;

import org.testar.core.action.resolver.ActionResolver;
import org.testar.core.service.ActionDerivationService;
import org.testar.core.service.ActionExecutionService;
import org.testar.core.service.ActionSelectorService;
import org.testar.core.service.OracleEvaluationService;
import org.testar.core.service.StateService;
import org.testar.core.service.SystemService;
import org.testar.engine.state.StateCompositionPlan;
import org.testar.plugin.PlatformServices;
import org.testar.plugin.PlatformSessionSpec;
import org.testar.plugin.configuration.PlatformDefaultSessionConfigurations;
import org.testar.plugin.configuration.SessionServiceConfiguration;
import org.testar.plugin.reporting.SessionReportingManager;
import org.testar.scriptless.RuntimeContext;
import org.testar.scriptless.ScriptlessCapabilities;
import org.testar.scriptless.TestingServices;
import org.testar.scriptless.capability.SettingsCapability;
import org.testar.scriptless.capability.StopCriteriaCapability;
import org.testar.scriptless.capability.TestSequenceCapability;
import org.testar.scriptless.capability.TestSessionCapability;
import org.testar.scriptless.capability.windows.WindowsSettingsCapability;
import org.testar.scriptless.capability.windows.WindowsStopCriteriaCapability;
import org.testar.scriptless.capability.windows.WindowsTestSequenceCapability;
import org.testar.scriptless.capability.windows.WindowsTestSessionCapability;
import org.testar.scriptless.composition.ScriptlessCompositionDescriptor;
import org.testar.scriptless.service.ScriptlessOracleComposer;
import org.testar.scriptless.service.windows.ScriptlessWindowsActionDerivationService;
import org.testar.scriptless.service.windows.ScriptlessWindowsActionExecutionService;
import org.testar.scriptless.service.windows.ScriptlessWindowsActionSelectorService;
import org.testar.scriptless.service.windows.ScriptlessWindowsOracleComposer;
import org.testar.scriptless.service.windows.ScriptlessWindowsStateService;
import org.testar.scriptless.service.windows.ScriptlessWindowsSystemService;
import org.testar.statemodel.StateModelManager;

public final class WindowsScriptlessPlatformRuntime extends AbstractScriptlessPlatformRuntime {

    @Override
    public SessionServiceConfiguration createServiceConfiguration(PlatformSessionSpec sessionSpec, RuntimeContext runtimeContext) {
        StateCompositionPlan defaultStatePlan = PlatformDefaultSessionConfigurations
                .windowsServiceConfiguration(sessionSpec)
                .stateCompositionPlanOverride()
                .orElseThrow();
        StateCompositionPlan scriptlessStatePlan = new StateCompositionPlan(
                new ScriptlessWindowsStateService(defaultStatePlan.stateService(), runtimeContext),
                defaultStatePlan::query
        );
        return SessionServiceConfiguration.builder()
                .overrideStateCompositionPlan(scriptlessStatePlan)
                .build();
    }

    @Override
    public TestingServices createTestingServices(PlatformServices platformServices,
                                                 RuntimeContext runtimeContext,
                                                 ScriptlessCompositionDescriptor compositionDescriptor,
                                                 SessionReportingManager sessionReportingManager) {
        SystemService systemService = new ScriptlessWindowsSystemService(
                platformServices.systemService(),
                runtimeContext
        );
        StateService stateService = platformServices.stateService();
        OracleEvaluationService oracleEvaluationService = platformServices.oracleEvaluationService();
        StateModelManager stateModelManager = platformServices.stateModelManager();
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
