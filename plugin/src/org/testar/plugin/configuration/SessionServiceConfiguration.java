/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin.configuration;

import java.util.Optional;

import org.testar.core.Assert;
import org.testar.engine.action.derivation.ActionDerivationPlan;
import org.testar.engine.action.execution.ActionExecutionPlan;
import org.testar.engine.action.resolver.ActionResolverPlan;
import org.testar.engine.action.selection.ActionSelectorPlan;
import org.testar.engine.state.StateCompositionPlan;
import org.testar.engine.system.SystemCompositionPlan;

/**
 * Plugin-side configuration contract that describes how session services
 * should be composed from platform defaults and service plans.
 */
public final class SessionServiceConfiguration {

    private final boolean includePlatformDefaults;
    private final Optional<SystemCompositionPlan> systemCompositionPlanOverride;
    private final Optional<StateCompositionPlan> stateCompositionPlanOverride;
    private final Optional<ActionDerivationPlan> actionDerivationPlanOverride;
    private final Optional<ActionSelectorPlan> actionSelectorPlanOverride;
    private final Optional<ActionResolverPlan> actionResolverPlanOverride;
    private final Optional<ActionExecutionPlan> actionExecutionPlanOverride;

    private SessionServiceConfiguration(boolean includePlatformDefaults,
                                        Optional<SystemCompositionPlan> systemCompositionPlanOverride,
                                        Optional<StateCompositionPlan> stateCompositionPlanOverride,
                                        Optional<ActionDerivationPlan> actionDerivationPlanOverride,
                                        Optional<ActionSelectorPlan> actionSelectorPlanOverride,
                                        Optional<ActionResolverPlan> actionResolverPlanOverride,
                                        Optional<ActionExecutionPlan> actionExecutionPlanOverride) {
        this.includePlatformDefaults = includePlatformDefaults;
        this.systemCompositionPlanOverride = Assert.notNull(systemCompositionPlanOverride);
        this.stateCompositionPlanOverride = Assert.notNull(stateCompositionPlanOverride);
        this.actionDerivationPlanOverride = Assert.notNull(actionDerivationPlanOverride);
        this.actionSelectorPlanOverride = Assert.notNull(actionSelectorPlanOverride);
        this.actionResolverPlanOverride = Assert.notNull(actionResolverPlanOverride);
        this.actionExecutionPlanOverride = Assert.notNull(actionExecutionPlanOverride);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SessionServiceConfiguration defaults() {
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

    public Optional<ActionDerivationPlan> actionDerivationPlanOverride() {
        return actionDerivationPlanOverride;
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

    public static final class Builder {

        private boolean includePlatformDefaults = true;
        private Optional<SystemCompositionPlan> systemCompositionPlanOverride = Optional.empty();
        private Optional<StateCompositionPlan> stateCompositionPlanOverride = Optional.empty();
        private Optional<ActionDerivationPlan> actionDerivationPlanOverride = Optional.empty();
        private Optional<ActionSelectorPlan> actionSelectorPlanOverride = Optional.empty();
        private Optional<ActionResolverPlan> actionResolverPlanOverride = Optional.empty();
        private Optional<ActionExecutionPlan> actionExecutionPlanOverride = Optional.empty();

        private Builder() {
        }

        public Builder includePlatformDefaults(boolean includePlatformDefaults) {
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

        public Builder overrideActionDerivationPlan(ActionDerivationPlan actionDerivationPlan) {
            this.actionDerivationPlanOverride = Optional.of(Assert.notNull(actionDerivationPlan));
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

        public SessionServiceConfiguration build() {
            return new SessionServiceConfiguration(
                    includePlatformDefaults,
                    systemCompositionPlanOverride,
                    stateCompositionPlanOverride,
                    actionDerivationPlanOverride,
                    actionSelectorPlanOverride,
                    actionResolverPlanOverride,
                    actionExecutionPlanOverride
            );
        }
    }
}
