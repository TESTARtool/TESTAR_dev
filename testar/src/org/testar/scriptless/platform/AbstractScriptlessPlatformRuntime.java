/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.platform;

import java.util.Optional;

import org.testar.config.composition.CompositionDescriptor;
import org.testar.config.composition.CompositionLoader;
import org.testar.core.action.resolver.ActionResolver;
import org.testar.core.service.ActionIdentifierService;
import org.testar.core.service.ActionDerivationService;
import org.testar.core.service.ActionExecutionService;
import org.testar.core.service.ActionSelectorService;
import org.testar.core.service.OracleEvaluationService;
import org.testar.core.service.StateIdentifierService;
import org.testar.core.service.StateService;
import org.testar.core.service.SystemService;
import org.testar.engine.service.DefaultActionIdentifierService;
import org.testar.engine.service.DefaultStateIdentifierService;
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
import org.testar.scriptless.service.ScriptlessOracleComposer;
import org.testar.statemodel.StateModelManager;

abstract class AbstractScriptlessPlatformRuntime implements ScriptlessPlatformRuntime {

    @Override
    public ServiceSessionConfiguration createServiceConfiguration(PlatformSessionSpecification sessionSpec,
                                                                 RuntimeContext runtimeContext,
                                                                 CompositionDescriptor compositionDescriptor) {
        return serviceConfigurationBuilder(runtimeContext, compositionDescriptor).build();
    }

    protected final <T> T wrap(OptionalWrapper<T> optionalWrapper) {
        return CompositionLoader.loadDelegateWrapper(
                optionalWrapper.wrapperClassName,
                optionalWrapper.compositionDescriptor.customCompositionResource(),
                optionalWrapper.expectedType,
                optionalWrapper.delegate,
                optionalWrapper.extraArgumentOptions
        );
    }

    protected final TestingServices testingServices(ServiceBundle bundle) {
        return TestingServices.builder()
                .withSystemService(bundle.systemService)
                .withStateService(bundle.stateService)
                .withOracleEvaluationService(bundle.oracleEvaluationService)
                .withStateModelManager(bundle.stateModelManager)
                .withActionDerivationService(bundle.actionDerivationService)
                .withActionSelectorService(bundle.actionSelectorService)
                .withActionResolver(bundle.actionResolver)
                .withActionExecutionService(bundle.actionExecutionService)
                .withSessionReportingManager(bundle.sessionReportingManager)
                .build();
    }

    protected final ScriptlessCapabilities capabilities(SettingsCapability settingsCapability,
                                                        TestSessionCapability testSessionCapability,
                                                        TestSequenceCapability testSequenceCapability,
                                                        ScriptlessOracleComposer scriptlessOracleComposer,
                                                        StopCriteriaCapability stopCriteriaCapability) {
        return ScriptlessCapabilities.builder()
                .withSettingsCapability(settingsCapability)
                .withTestSessionCapability(testSessionCapability)
                .withTestSequenceCapability(testSequenceCapability)
                .withScriptlessOracleComposer(scriptlessOracleComposer)
                .withStopCriteriaCapability(stopCriteriaCapability)
                .build();
    }

    protected final ScriptlessCapabilities capabilities(CapabilityBundle bundle) {
        return capabilities(
                bundle.settingsCapability,
                bundle.testSessionCapability,
                bundle.testSequenceCapability,
                bundle.scriptlessOracleComposer,
                bundle.stopCriteriaCapability
        );
    }

    protected final ServiceSessionConfiguration.Builder serviceConfigurationBuilder(RuntimeContext runtimeContext,
                                                                                    CompositionDescriptor compositionDescriptor) {
        StateIdentifierService stateIdentifierService = wrap(wrapper(
                compositionDescriptor.stateIdentifierServiceClass(),
                compositionDescriptor,
                StateIdentifierService.class,
                new DefaultStateIdentifierService(),
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        ));
        ActionIdentifierService actionIdentifierService = wrap(wrapper(
                compositionDescriptor.actionIdentifierServiceClass(),
                compositionDescriptor,
                ActionIdentifierService.class,
                new DefaultActionIdentifierService(),
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        ));

        return ServiceSessionConfiguration.builder()
                .overrideStateIdentifierService(stateIdentifierService)
                .overrideActionIdentifierService(actionIdentifierService);
    }

    protected final ServiceBundle wrapServices(ServiceBundle bundle,
                                               RuntimeContext runtimeContext,
                                               CompositionDescriptor compositionDescriptor) {
        bundle.systemService = wrap(wrapper(
                compositionDescriptor.systemServiceClass(),
                compositionDescriptor,
                SystemService.class,
                bundle.systemService,
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        ));
        bundle.stateService = wrap(wrapper(
                compositionDescriptor.stateServiceClass(),
                compositionDescriptor,
                StateService.class,
                bundle.stateService,
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        ));
        bundle.actionDerivationService = wrap(wrapper(
                compositionDescriptor.actionDerivationServiceClass(),
                compositionDescriptor,
                ActionDerivationService.class,
                bundle.actionDerivationService,
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        ));
        bundle.actionSelectorService = wrap(wrapper(
                compositionDescriptor.actionSelectorServiceClass(),
                compositionDescriptor,
                ActionSelectorService.class,
                bundle.actionSelectorService,
                new Object[]{bundle.stateModelManager},
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        ));
        bundle.actionExecutionService = wrap(wrapper(
                compositionDescriptor.actionExecutionServiceClass(),
                compositionDescriptor,
                ActionExecutionService.class,
                bundle.actionExecutionService,
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        ));
        return bundle;
    }

    protected final CapabilityBundle wrapCapabilities(CapabilityBundle bundle,
                                                      RuntimeContext runtimeContext,
                                                      CompositionDescriptor compositionDescriptor) {
        bundle.settingsCapability = wrap(wrapper(
                compositionDescriptor.settingsCapabilityClass(),
                compositionDescriptor,
                SettingsCapability.class,
                bundle.settingsCapability,
                new Object[]{runtimeContext.settings()},
                new Object[]{runtimeContext},
                new Object[]{}
        ));
        bundle.testSessionCapability = wrap(wrapper(
                compositionDescriptor.testSessionCapabilityClass(),
                compositionDescriptor,
                TestSessionCapability.class,
                bundle.testSessionCapability,
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        ));
        bundle.testSequenceCapability = wrap(wrapper(
                compositionDescriptor.testSequenceCapabilityClass(),
                compositionDescriptor,
                TestSequenceCapability.class,
                bundle.testSequenceCapability,
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        ));
        bundle.scriptlessOracleComposer = wrap(wrapper(
                compositionDescriptor.oracleComposerClass(),
                compositionDescriptor,
                ScriptlessOracleComposer.class,
                bundle.scriptlessOracleComposer,
                new Object[]{runtimeContext.settings()},
                new Object[]{runtimeContext},
                new Object[]{}
        ));
        bundle.stopCriteriaCapability = wrap(wrapper(
                compositionDescriptor.stopCriteriaCapabilityClass(),
                compositionDescriptor,
                StopCriteriaCapability.class,
                bundle.stopCriteriaCapability,
                new Object[]{runtimeContext},
                new Object[]{runtimeContext.settings()},
                new Object[]{}
        ));
        return bundle;
    }

    @Override
    public SettingsCapability createSettingsCapability(RuntimeContext runtimeContext, CompositionDescriptor compositionDescriptor) {
        return wrap(wrapper(
                compositionDescriptor.settingsCapabilityClass(),
                compositionDescriptor,
                SettingsCapability.class,
                new SettingsCapability(),
                new Object[]{runtimeContext.settings()},
                new Object[]{runtimeContext},
                new Object[]{}
        ));
    }

    protected final ServiceBundle serviceBundle(SystemService systemService,
                                                StateService stateService,
                                                OracleEvaluationService oracleEvaluationService,
                                                StateModelManager stateModelManager,
                                                ActionDerivationService actionDerivationService,
                                                ActionSelectorService actionSelectorService,
                                                ActionResolver actionResolver,
                                                ActionExecutionService actionExecutionService,
                                                SessionReportingManager sessionReportingManager) {
        return new ServiceBundle(
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

    protected final CapabilityBundle capabilityBundle(SettingsCapability settingsCapability,
                                                      TestSessionCapability testSessionCapability,
                                                      TestSequenceCapability testSequenceCapability,
                                                      ScriptlessOracleComposer scriptlessOracleComposer,
                                                      StopCriteriaCapability stopCriteriaCapability) {
        return new CapabilityBundle(
                settingsCapability,
                testSessionCapability,
                testSequenceCapability,
                scriptlessOracleComposer,
                stopCriteriaCapability
        );
    }

    protected static final class OptionalWrapper<T> {

        private final Optional<String> wrapperClassName;
        private final CompositionDescriptor compositionDescriptor;
        private final Class<T> expectedType;
        private final T delegate;
        private final Object[][] extraArgumentOptions;

        private OptionalWrapper(Optional<String> wrapperClassName,
                                CompositionDescriptor compositionDescriptor,
                                Class<T> expectedType,
                                T delegate,
                                Object[]... extraArgumentOptions) {
            this.wrapperClassName = wrapperClassName;
            this.compositionDescriptor = compositionDescriptor;
            this.expectedType = expectedType;
            this.delegate = delegate;
            this.extraArgumentOptions = extraArgumentOptions;
        }
    }

    protected final <T> OptionalWrapper<T> wrapper(Optional<String> wrapperClassName,
                                                   CompositionDescriptor compositionDescriptor,
                                                   Class<T> expectedType,
                                                   T delegate,
                                                   Object[]... extraArgumentOptions) {
        return new OptionalWrapper<T>(
                wrapperClassName,
                compositionDescriptor,
                expectedType,
                delegate,
                extraArgumentOptions
        );
    }

    protected static final class ServiceBundle {

        private SystemService systemService;
        private StateService stateService;
        private OracleEvaluationService oracleEvaluationService;
        private StateModelManager stateModelManager;
        private ActionDerivationService actionDerivationService;
        private ActionSelectorService actionSelectorService;
        private ActionResolver actionResolver;
        private ActionExecutionService actionExecutionService;
        private SessionReportingManager sessionReportingManager;

        private ServiceBundle(SystemService systemService,
                              StateService stateService,
                              OracleEvaluationService oracleEvaluationService,
                              StateModelManager stateModelManager,
                              ActionDerivationService actionDerivationService,
                              ActionSelectorService actionSelectorService,
                              ActionResolver actionResolver,
                              ActionExecutionService actionExecutionService,
                              SessionReportingManager sessionReportingManager) {
            this.systemService = systemService;
            this.stateService = stateService;
            this.oracleEvaluationService = oracleEvaluationService;
            this.stateModelManager = stateModelManager;
            this.actionDerivationService = actionDerivationService;
            this.actionSelectorService = actionSelectorService;
            this.actionResolver = actionResolver;
            this.actionExecutionService = actionExecutionService;
            this.sessionReportingManager = sessionReportingManager;
        }
    }

    protected static final class CapabilityBundle {

        private SettingsCapability settingsCapability;
        private TestSessionCapability testSessionCapability;
        private TestSequenceCapability testSequenceCapability;
        private ScriptlessOracleComposer scriptlessOracleComposer;
        private StopCriteriaCapability stopCriteriaCapability;

        private CapabilityBundle(SettingsCapability settingsCapability,
                                 TestSessionCapability testSessionCapability,
                                 TestSequenceCapability testSequenceCapability,
                                 ScriptlessOracleComposer scriptlessOracleComposer,
                                 StopCriteriaCapability stopCriteriaCapability) {
            this.settingsCapability = settingsCapability;
            this.testSessionCapability = testSessionCapability;
            this.testSequenceCapability = testSequenceCapability;
            this.scriptlessOracleComposer = scriptlessOracleComposer;
            this.stopCriteriaCapability = stopCriteriaCapability;
        }
    }
}
