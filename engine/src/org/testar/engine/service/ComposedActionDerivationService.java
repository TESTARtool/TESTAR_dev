/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.service;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.testar.core.Assert;
import org.testar.core.CodingManager;
import org.testar.core.action.Action;
import org.testar.core.service.ActionDerivationService;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.engine.action.derivation.ActionDerivationPlan;
import org.testar.engine.action.derivation.ActionDeriver;
import org.testar.engine.policy.SessionPolicyContext;

/**
 * Policy-based action derivation service that executes the configured
 * derivation plan across forced, default, and fallback phases.
 */
public final class ComposedActionDerivationService implements ActionDerivationService {

    private final SessionPolicyContext context;
    private final List<ActionDeriver> forcedDerivers;
    private final List<ActionDeriver> defaultDerivers;
    private final List<ActionDeriver> fallbackDerivers;

    public ComposedActionDerivationService(SessionPolicyContext context, ActionDerivationPlan plan) {
        this(
                context,
                Assert.notNull(plan).forcedDerivers(),
                plan.defaultDerivers(),
                plan.fallbackDerivers()
        );
    }

    ComposedActionDerivationService(SessionPolicyContext context,
                                           List<ActionDeriver> forcedDerivers,
                                           List<ActionDeriver> defaultDerivers,
                                           List<ActionDeriver> fallbackDerivers) {
        this.context = Assert.notNull(context);
        this.forcedDerivers = Collections.unmodifiableList(Assert.notNull(forcedDerivers));
        this.defaultDerivers = Collections.unmodifiableList(Assert.notNull(defaultDerivers));
        this.fallbackDerivers = Collections.unmodifiableList(Assert.notNull(fallbackDerivers));
    }

    public static ComposedActionDerivationService compose(SessionPolicyContext context, ActionDerivationPlan plan) {
        return new ComposedActionDerivationService(context, plan);
    }

    @Override
    public Set<Action> deriveActions(SUT system, State state) {
        Assert.notNull(state);
        Set<Action> forcedActions = derive(system, state, forcedDerivers);
        if (!forcedActions.isEmpty()) {
            buildActionsIdentifiers(state, forcedActions);
            return forcedActions;
        }
        Set<Action> defaultActions = derive(system, state, defaultDerivers);
        if (!defaultActions.isEmpty()) {
            buildActionsIdentifiers(state, defaultActions);
            return defaultActions;
        }
        Set<Action> fallbackActions = derive(system, state, fallbackDerivers);
        buildActionsIdentifiers(state, fallbackActions);
        return fallbackActions;
    }

    public SessionPolicyContext context() {
        return context;
    }

    private Set<Action> derive(SUT system, State state, List<ActionDeriver> derivers) {
        Set<Action> actions = new LinkedHashSet<>();
        for (ActionDeriver deriver : derivers) {
            actions.addAll(deriver.derive(system, state, context));
        }
        return actions;
    }

    private void buildActionsIdentifiers(State state, Set<Action> actions) {
        if (actions.isEmpty()) {
            return;
        }

        Set<Action> widgetActions = new LinkedHashSet<>();
        for (Action action : actions) {
            if (hasOriginWidgetPath(action)) {
                widgetActions.add(action);
            } else {
                CodingManager.buildEnvironmentActionIDs(state, action);
            }
        }

        if (!widgetActions.isEmpty()) {
            CodingManager.buildIDs(state, widgetActions);
        }
    }

    private boolean hasOriginWidgetPath(Action action) {
        Widget originWidget = action.get(Tags.OriginWidget, null);
        return originWidget != null && originWidget.get(Tags.Path, null) != null;
    }
}
