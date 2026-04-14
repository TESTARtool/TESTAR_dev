/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin.configuration;

import org.testar.core.Assert;
import org.testar.core.action.resolver.ActionResolver;
import org.testar.core.service.ActionDerivationService;
import org.testar.core.service.ActionExecutionService;
import org.testar.core.service.ActionSelectorService;
import org.testar.core.service.StateService;
import org.testar.core.service.SystemService;
import org.testar.engine.action.derivation.ActionDerivationPlan;
import org.testar.engine.action.execution.ActionExecutionPlan;
import org.testar.engine.action.resolver.ActionResolverPlan;
import org.testar.engine.action.selection.ActionSelectorPlan;
import org.testar.engine.action.selection.StateModelActionSelector;
import org.testar.engine.policy.SessionPolicyContext;
import org.testar.engine.service.ComposedActionDerivationService;
import org.testar.engine.service.ComposedActionExecutionService;
import org.testar.engine.service.ComposedActionResolver;
import org.testar.engine.service.ComposedActionSelectorService;
import org.testar.engine.service.ComposedStateService;
import org.testar.engine.service.ComposedSystemService;
import org.testar.engine.state.StateCompositionPlan;
import org.testar.engine.system.SystemCompositionPlan;
import org.testar.plugin.PlatformServices;
import org.testar.statemodel.StateModelManager;

/**
 * Builds the final platform service bundle from session context, state model,
 * platform defaults, and optional service-plan overrides.
 */
public final class SessionServiceComposer {

    private SessionServiceComposer() {
    }

    public static PlatformServices compose(SessionPolicyContext sessionPolicyContext,
                                           StateModelManager stateModelService,
                                           SessionServiceConfiguration configuration,
                                           SystemCompositionPlan defaultSystemCompositionPlan,
                                           StateCompositionPlan defaultStateCompositionPlan,
                                           ActionDerivationPlan defaultActionDerivationPlan,
                                           ActionSelectorPlan defaultActionSelectorPlan,
                                           ActionResolverPlan defaultActionResolverPlan,
                                           ActionExecutionPlan defaultActionExecutionPlan) {
        Assert.notNull(sessionPolicyContext);
        Assert.notNull(stateModelService);
        Assert.notNull(configuration);

        SystemCompositionPlan systemCompositionPlan = choosePlan(
                configuration.systemCompositionPlanOverride().orElse(null),
                defaultSystemCompositionPlan,
                configuration.includePlatformDefaults(),
                "system composition"
        );
        StateCompositionPlan stateCompositionPlan = choosePlan(
                configuration.stateCompositionPlanOverride().orElse(null),
                defaultStateCompositionPlan,
                configuration.includePlatformDefaults(),
                "state composition"
        );
        ActionDerivationPlan actionDerivationPlan = choosePlan(
                configuration.actionDerivationPlanOverride().orElse(null),
                defaultActionDerivationPlan,
                configuration.includePlatformDefaults(),
                "action derivation"
        );
        ActionSelectorPlan actionSelectorPlan = choosePlan(
                configuration.actionSelectorPlanOverride().orElse(null),
                defaultActionSelectorPlan,
                configuration.includePlatformDefaults(),
                "action selector"
        );
        ActionResolverPlan actionResolverPlan = choosePlan(
                configuration.actionResolverPlanOverride().orElse(null),
                defaultActionResolverPlan,
                configuration.includePlatformDefaults(),
                "action resolver"
        );
        ActionExecutionPlan actionExecutionPlan = choosePlan(
                configuration.actionExecutionPlanOverride().orElse(null),
                defaultActionExecutionPlan,
                configuration.includePlatformDefaults(),
                "action execution"
        );
        ActionSelectorPlan effectiveActionSelectorPlan = ActionSelectorPlan.withFallback(
                new StateModelActionSelector(stateModelService),
                ComposedActionSelectorService.compose(actionSelectorPlan)
        );

        SystemService systemService = ComposedSystemService.compose(systemCompositionPlan);
        StateService stateService = ComposedStateService.compose(sessionPolicyContext, stateCompositionPlan);
        ActionDerivationService actionDerivationService = ComposedActionDerivationService.compose(
                sessionPolicyContext,
                actionDerivationPlan
        );
        ActionSelectorService actionSelectorService = ComposedActionSelectorService.compose(effectiveActionSelectorPlan);
        ActionResolver actionResolver = ComposedActionResolver.compose(actionResolverPlan);
        ActionExecutionService actionExecutionService = ComposedActionExecutionService.compose(actionExecutionPlan);

        return new PlatformServices(
                systemService,
                stateService,
                stateModelService,
                actionDerivationService,
                actionSelectorService,
                actionResolver,
                actionExecutionService
        );
    }

    private static <T> T choosePlan(T overridePlan,
                                    T defaultPlan,
                                    boolean includePlatformDefaults,
                                    String planName) {
        if (overridePlan != null) {
            return overridePlan;
        }
        if (includePlatformDefaults && defaultPlan != null) {
            return defaultPlan;
        }
        throw new IllegalStateException("No " + planName + " plan is configured");
    }
}
