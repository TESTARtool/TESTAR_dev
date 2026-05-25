/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin.configuration;

import java.util.Optional;

import org.testar.core.Assert;
import org.testar.core.service.ActionIdentifierService;
import org.testar.core.service.StateIdentifierService;
import org.testar.engine.action.derivation.ActionDerivationPlan;
import org.testar.engine.action.execution.ActionExecutionPlan;
import org.testar.engine.action.resolver.ActionResolverPlan;
import org.testar.engine.action.selection.ActionSelectorPlan;
import org.testar.engine.oracle.OracleEvaluationPlan;
import org.testar.engine.state.StateCompositionPlan;
import org.testar.engine.system.SystemCompositionPlan;

/**
 * Plugin-side configuration contract that describes how session services
 * should be composed from platform defaults and service plans.
 */
public final class ServiceSessionConfiguration {

    private final boolean includePlatformDefaults;
    private final Optional<SystemCompositionPlan> systemCompositionPlanOverride;
    private final Optional<StateCompositionPlan> stateCompositionPlanOverride;
    private final Optional<StateIdentifierService> stateIdentifierServiceOverride;
    private final Optional<ActionDerivationPlan> actionDerivationPlanOverride;
    private final Optional<ActionIdentifierService> actionIdentifierServiceOverride;
    private final Optional<ActionSelectorPlan> actionSelectorPlanOverride;
    private final Optional<ActionResolverPlan> actionResolverPlanOverride;
    private final Optional<ActionExecutionPlan> actionExecutionPlanOverride;
    private final Optional<OracleEvaluationPlan> oracleEvaluationPlanOverride;

    private ServiceSessionConfiguration(boolean includePlatformDefaults,
                                        Optional<SystemCompositionPlan> systemCompositionPlanOverride,
                                        Optional<StateCompositionPlan> stateCompositionPlanOverride,
                                        Optional<StateIdentifierService> stateIdentifierServiceOverride,
                                        Optional<ActionDerivationPlan> actionDerivationPlanOverride,
                                        Optional<ActionIdentifierService> actionIdentifierServiceOverride,
                                        Optional<ActionSelectorPlan> actionSelectorPlanOverride,
                                        Optional<ActionResolverPlan> actionResolverPlanOverride,
                                        Optional<ActionExecutionPlan> actionExecutionPlanOverride,
                                        Optional<OracleEvaluationPlan> oracleEvaluationPlanOverride) {
        this.includePlatformDefaults = includePlatformDefaults;
        this.systemCompositionPlanOverride = Assert.notNull(systemCompositionPlanOverride);
        this.stateCompositionPlanOverride = Assert.notNull(stateCompositionPlanOverride);
        this.stateIdentifierServiceOverride = Assert.notNull(stateIdentifierServiceOverride);
        this.actionDerivationPlanOverride = Assert.notNull(actionDerivationPlanOverride);
        this.actionIdentifierServiceOverride = Assert.notNull(actionIdentifierServiceOverride);
        this.actionSelectorPlanOverride = Assert.notNull(actionSelectorPlanOverride);
        this.actionResolverPlanOverride = Assert.notNull(actionResolverPlanOverride);
        this.actionExecutionPlanOverride = Assert.notNull(actionExecutionPlanOverride);
        this.oracleEvaluationPlanOverride = Assert.notNull(oracleEvaluationPlanOverride);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static ServiceSessionConfiguration defaults() {
        return builder().build();
    }

    public boolean includePlatformDefaults() {
        return includePlatformDefaults;
    }

    public Optional<SystemCompositionPlan> systemCompositionPlanOverride() {
        return systemCompositionPlanOverride;
    }

    public Optional<StateCompositionPlan> stateCompositionPlanOverride() {
        return stateCompositionPlanOverride;
    }

    public Optional<StateIdentifierService> stateIdentifierServiceOverride() {
        return stateIdentifierServiceOverride;
    }

    public Optional<ActionDerivationPlan> actionDerivationPlanOverride() {
        return actionDerivationPlanOverride;
    }

    public Optional<ActionIdentifierService> actionIdentifierServiceOverride() {
        return actionIdentifierServiceOverride;
    }

    public Optional<ActionSelectorPlan> actionSelectorPlanOverride() {
        return actionSelectorPlanOverride;
    }

    public Optional<ActionResolverPlan> actionResolverPlanOverride() {
        return actionResolverPlanOverride;
    }

    public Optional<ActionExecutionPlan> actionExecutionPlanOverride() {
        return actionExecutionPlanOverride;
    }

    public Optional<OracleEvaluationPlan> oracleEvaluationPlanOverride() {
        return oracleEvaluationPlanOverride;
    }

    public static final class Builder {

        private boolean includePlatformDefaults = true;
        private Optional<SystemCompositionPlan> systemCompositionPlanOverride = Optional.empty();
        private Optional<StateCompositionPlan> stateCompositionPlanOverride = Optional.empty();
        private Optional<StateIdentifierService> stateIdentifierServiceOverride = Optional.empty();
        private Optional<ActionDerivationPlan> actionDerivationPlanOverride = Optional.empty();
        private Optional<ActionIdentifierService> actionIdentifierServiceOverride = Optional.empty();
        private Optional<ActionSelectorPlan> actionSelectorPlanOverride = Optional.empty();
        private Optional<ActionResolverPlan> actionResolverPlanOverride = Optional.empty();
        private Optional<ActionExecutionPlan> actionExecutionPlanOverride = Optional.empty();
        private Optional<OracleEvaluationPlan> oracleEvaluationPlanOverride = Optional.empty();

        private Builder() {
        }

        public Builder includePlatformDefaults(boolean includePlatformDefaults) {
            // When false, every required plan must be provided explicitly by overrides.
            this.includePlatformDefaults = includePlatformDefaults;
            return this;
        }

        public Builder overrideSystemCompositionPlan(SystemCompositionPlan systemCompositionPlan) {
            this.systemCompositionPlanOverride = Optional.of(Assert.notNull(systemCompositionPlan));
            return this;
        }

        public Builder overrideStateCompositionPlan(StateCompositionPlan stateCompositionPlan) {
            this.stateCompositionPlanOverride = Optional.of(Assert.notNull(stateCompositionPlan));
            return this;
        }

        public Builder overrideStateIdentifierService(StateIdentifierService stateIdentifierService) {
            this.stateIdentifierServiceOverride = Optional.of(Assert.notNull(stateIdentifierService));
            return this;
        }

        public Builder overrideActionDerivationPlan(ActionDerivationPlan actionDerivationPlan) {
            this.actionDerivationPlanOverride = Optional.of(Assert.notNull(actionDerivationPlan));
            return this;
        }

        public Builder overrideActionIdentifierService(ActionIdentifierService actionIdentifierService) {
            this.actionIdentifierServiceOverride = Optional.of(Assert.notNull(actionIdentifierService));
            return this;
        }

        public Builder overrideActionSelectorPlan(ActionSelectorPlan actionSelectorPlan) {
            this.actionSelectorPlanOverride = Optional.of(Assert.notNull(actionSelectorPlan));
            return this;
        }

        public Builder overrideActionResolverPlan(ActionResolverPlan actionResolverPlan) {
            this.actionResolverPlanOverride = Optional.of(Assert.notNull(actionResolverPlan));
            return this;
        }

        public Builder overrideActionExecutionPlan(ActionExecutionPlan actionExecutionPlan) {
            this.actionExecutionPlanOverride = Optional.of(Assert.notNull(actionExecutionPlan));
            return this;
        }

        public Builder overrideOracleEvaluationPlan(OracleEvaluationPlan oracleEvaluationPlan) {
            this.oracleEvaluationPlanOverride = Optional.of(Assert.notNull(oracleEvaluationPlan));
            return this;
        }

        public ServiceSessionConfiguration build() {
            return new ServiceSessionConfiguration(
                    includePlatformDefaults,
                    systemCompositionPlanOverride,
                    stateCompositionPlanOverride,
                    stateIdentifierServiceOverride,
                    actionDerivationPlanOverride,
                    actionIdentifierServiceOverride,
                    actionSelectorPlanOverride,
                    actionResolverPlanOverride,
                    actionExecutionPlanOverride,
                    oracleEvaluationPlanOverride
            );
        }
    }
}
