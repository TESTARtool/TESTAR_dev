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
import org.testar.core.action.Action;
import org.testar.core.service.ActionIdentifierService;
import org.testar.core.service.ActionDerivationService;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
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
    private final ActionIdentifierService actionIdentifierService;
    private final ActionSemanticDisambiguator actionSemanticDisambiguator;

    public ComposedActionDerivationService(SessionPolicyContext context, ActionDerivationPlan plan) {
        this(
                context,
                Assert.notNull(plan).forcedDerivers(),
                plan.defaultDerivers(),
                plan.fallbackDerivers(),
                new DefaultActionIdentifierService(),
                new ActionSemanticDisambiguator()
        );
    }

    public ComposedActionDerivationService(SessionPolicyContext context,
                                           ActionDerivationPlan plan,
                                           ActionIdentifierService actionIdentifierService) {
        this(
                context,
                Assert.notNull(plan).forcedDerivers(),
                plan.defaultDerivers(),
                plan.fallbackDerivers(),
                actionIdentifierService,
                new ActionSemanticDisambiguator()
        );
    }

    ComposedActionDerivationService(SessionPolicyContext context,
                                    List<ActionDeriver> forcedDerivers,
                                    List<ActionDeriver> defaultDerivers,
                                    List<ActionDeriver> fallbackDerivers,
                                    ActionIdentifierService actionIdentifierService,
                                    ActionSemanticDisambiguator actionSemanticDisambiguator) {
        this.context = Assert.notNull(context);
        this.forcedDerivers = Collections.unmodifiableList(Assert.notNull(forcedDerivers));
        this.defaultDerivers = Collections.unmodifiableList(Assert.notNull(defaultDerivers));
        this.fallbackDerivers = Collections.unmodifiableList(Assert.notNull(fallbackDerivers));
        this.actionIdentifierService = Assert.notNull(actionIdentifierService);
        this.actionSemanticDisambiguator = Assert.notNull(actionSemanticDisambiguator);
    }

    public static ComposedActionDerivationService compose(SessionPolicyContext context, ActionDerivationPlan plan) {
        return new ComposedActionDerivationService(context, plan);
    }

    public static ComposedActionDerivationService compose(SessionPolicyContext context,
                                                          ActionDerivationPlan plan,
                                                          ActionIdentifierService actionIdentifierService) {
        return new ComposedActionDerivationService(context, plan, actionIdentifierService);
    }

    @Override
    public Set<Action> deriveActions(SUT system, State state) {
        Assert.notNull(state);
        Set<Action> forcedActions = derive(system, state, forcedDerivers);
        if (!forcedActions.isEmpty()) {
            return identifyAndDisambiguate(state, forcedActions);
        }
        Set<Action> defaultActions = derive(system, state, defaultDerivers);
        if (!defaultActions.isEmpty()) {
            return identifyAndDisambiguate(state, defaultActions);
        }
        Set<Action> fallbackActions = derive(system, state, fallbackDerivers);
        return identifyAndDisambiguate(state, fallbackActions);
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

    private Set<Action> identifyAndDisambiguate(State state, Set<Action> actions) {
        Set<Action> identifiedActions = actionIdentifierService.identifyActions(state, actions);
        actionSemanticDisambiguator.disambiguate(identifiedActions);
        return identifiedActions;
    }
}
