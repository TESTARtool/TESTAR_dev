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
import org.testar.plugin.PlatformServices;
import org.testar.plugin.configuration.PlatformSessionSpecification;
import org.testar.plugin.configuration.ServiceSessionConfiguration;
import org.testar.plugin.reporting.SessionReportingManager;
import org.testar.scriptless.RuntimeContext;
import org.testar.scriptless.ScriptlessCapabilities;
import org.testar.scriptless.TestingServices;
import org.testar.scriptless.capability.SettingsCapability;
import org.testar.scriptless.capability.StopCriteriaCapability;
import org.testar.scriptless.capability.TestSequenceCapability;
import org.testar.scriptless.capability.TestSessionCapability;
import org.testar.scriptless.capability.android.AndroidSettingsCapability;
import org.testar.scriptless.capability.android.AndroidStopCriteriaCapability;
import org.testar.scriptless.capability.android.AndroidTestSequenceCapability;
import org.testar.scriptless.capability.android.AndroidTestSessionCapability;
import org.testar.scriptless.service.ScriptlessOracleComposer;
import org.testar.scriptless.service.android.ScriptlessAndroidActionDerivationService;
import org.testar.scriptless.service.android.ScriptlessAndroidActionExecutionService;
import org.testar.scriptless.service.android.ScriptlessAndroidActionSelectorService;
import org.testar.scriptless.service.android.ScriptlessAndroidOracleComposer;
import org.testar.scriptless.service.android.ScriptlessAndroidStateService;
import org.testar.scriptless.service.android.ScriptlessAndroidSystemService;
import org.testar.statemodel.StateModelManager;

public final class AndroidScriptlessPlatformRuntime extends AbstractScriptlessPlatformRuntime {

    @Override
    public ServiceSessionConfiguration createServiceConfiguration(PlatformSessionSpecification sessionSpec,
                                                                 RuntimeContext runtimeContext,
                                                                 CompositionDescriptor compositionDescriptor) {
        return serviceConfigurationBuilder(runtimeContext, compositionDescriptor).build();
    }

    @Override
    public TestingServices createTestingServices(PlatformServices platformServices,
                                                 RuntimeContext runtimeContext,
                                                 CompositionDescriptor compositionDescriptor,
                                                 SessionReportingManager sessionReportingManager) {
        SystemService systemService = new ScriptlessAndroidSystemService(
                platformServices.systemService(),
                runtimeContext
        );
        StateService stateService = new ScriptlessAndroidStateService(
                platformServices.stateService()
        );
        OracleEvaluationService oracleEvaluationService = platformServices.oracleEvaluationService();
        StateModelManager stateModelManager = platformServices.stateModelManager();
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
