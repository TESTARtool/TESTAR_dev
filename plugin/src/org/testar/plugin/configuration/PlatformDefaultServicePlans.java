/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin.configuration;

import org.testar.core.Assert;
import org.testar.engine.action.derivation.ActionDerivationPlan;
import org.testar.engine.action.execution.ActionExecutionPlan;
import org.testar.engine.action.resolver.ActionResolverPlan;
import org.testar.engine.action.selection.ActionSelectorPlan;
import org.testar.engine.oracle.OracleEvaluationPlan;
import org.testar.engine.state.StateCompositionPlan;
import org.testar.engine.system.SystemCompositionPlan;

/**
 * Groups the platform default service plans used to assemble one session.
 */
public final class PlatformDefaultServicePlans {

    private final SystemCompositionPlan systemCompositionPlan;
    private final StateCompositionPlan stateCompositionPlan;
    private final ActionDerivationPlan actionDerivationPlan;
    private final ActionSelectorPlan actionSelectorPlan;
    private final ActionResolverPlan actionResolverPlan;
    private final ActionExecutionPlan actionExecutionPlan;
    private final OracleEvaluationPlan oracleEvaluationPlan;

    public PlatformDefaultServicePlans(SystemCompositionPlan systemCompositionPlan,
                                       StateCompositionPlan stateCompositionPlan,
                                       ActionDerivationPlan actionDerivationPlan,
                                       ActionSelectorPlan actionSelectorPlan,
                                       ActionResolverPlan actionResolverPlan,
                                       ActionExecutionPlan actionExecutionPlan,
                                       OracleEvaluationPlan oracleEvaluationPlan) {
        this.systemCompositionPlan = Assert.notNull(systemCompositionPlan);
        this.stateCompositionPlan = Assert.notNull(stateCompositionPlan);
        this.actionDerivationPlan = Assert.notNull(actionDerivationPlan);
        this.actionSelectorPlan = Assert.notNull(actionSelectorPlan);
        this.actionResolverPlan = Assert.notNull(actionResolverPlan);
        this.actionExecutionPlan = Assert.notNull(actionExecutionPlan);
        this.oracleEvaluationPlan = Assert.notNull(oracleEvaluationPlan);
    }

    public static PlatformDefaultServicePlans fromConfiguration(SessionServiceConfiguration configuration) {
        Assert.notNull(configuration);
        return new PlatformDefaultServicePlans(
                configuration.systemCompositionPlanOverride().orElseThrow(),
                configuration.stateCompositionPlanOverride().orElseThrow(),
                configuration.actionDerivationPlanOverride().orElseThrow(),
                configuration.actionSelectorPlanOverride().orElseThrow(),
                configuration.actionResolverPlanOverride().orElseThrow(),
                configuration.actionExecutionPlanOverride().orElseThrow(),
                configuration.oracleEvaluationPlanOverride().orElseThrow()
        );
    }

    public SystemCompositionPlan systemCompositionPlan() {
        return systemCompositionPlan;
    }

    public StateCompositionPlan stateCompositionPlan() {
        return stateCompositionPlan;
    }

    public ActionDerivationPlan actionDerivationPlan() {
        return actionDerivationPlan;
    }

    public ActionSelectorPlan actionSelectorPlan() {
        return actionSelectorPlan;
    }

    public ActionResolverPlan actionResolverPlan() {
        return actionResolverPlan;
    }

    public ActionExecutionPlan actionExecutionPlan() {
        return actionExecutionPlan;
    }

    public OracleEvaluationPlan oracleEvaluationPlan() {
        return oracleEvaluationPlan;
    }
}
