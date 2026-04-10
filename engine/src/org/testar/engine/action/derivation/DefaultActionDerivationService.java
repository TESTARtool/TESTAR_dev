/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.derivation;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.testar.core.Assert;
import org.testar.core.CodingManager;
import org.testar.core.action.Action;
import org.testar.core.policy.BlockedPolicy;
import org.testar.core.policy.ClickablePolicy;
import org.testar.core.policy.EnabledPolicy;
import org.testar.core.policy.ScrollablePolicy;
import org.testar.core.policy.TypeablePolicy;
import org.testar.core.policy.WidgetFilterPolicy;
import org.testar.core.service.ActionDerivationService;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.engine.policy.CompositeBlockedPolicy;
import org.testar.engine.policy.CompositeClickablePolicy;
import org.testar.engine.policy.CompositeEnabledPolicy;
import org.testar.engine.policy.CompositeScrollablePolicy;
import org.testar.engine.policy.CompositeTypeablePolicy;
import org.testar.engine.policy.CompositeWidgetFilterPolicy;

/**
 * Default policy-based action derivation service.
 */
public final class DefaultActionDerivationService implements ActionDerivationService {

    private final ActionDerivationContext context;
    private final List<ActionDeriver> forcedDerivers;
    private final List<ActionDeriver> defaultDerivers;
    private final List<ActionDeriver> fallbackDerivers;

    public DefaultActionDerivationService(ActionDerivationContext context, List<ActionDeriver> derivers) {
        this(context, Collections.emptyList(), derivers, Collections.emptyList());
    }

    public DefaultActionDerivationService(ActionDerivationContext context,
                                          List<ActionDeriver> forcedDerivers,
                                          List<ActionDeriver> defaultDerivers,
                                          List<ActionDeriver> fallbackDerivers) {
        this.context = Assert.notNull(context);
        this.forcedDerivers = Collections.unmodifiableList(Assert.notNull(forcedDerivers));
        this.defaultDerivers = Collections.unmodifiableList(Assert.notNull(defaultDerivers));
        this.fallbackDerivers = Collections.unmodifiableList(Assert.notNull(fallbackDerivers));
    }

    public static DefaultActionDerivationService empty() {
        return new DefaultActionDerivationService(
                new ActionDerivationContext(
                        CompositeClickablePolicy.empty(),
                        CompositeTypeablePolicy.empty(),
                        CompositeScrollablePolicy.empty(),
                        CompositeEnabledPolicy.allowAll(),
                        CompositeBlockedPolicy.allowNone(),
                        CompositeWidgetFilterPolicy.allowAll()
                ),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    public static DefaultActionDerivationService withPolicies(List<ClickablePolicy> clickablePolicies,
                                                              List<TypeablePolicy> typeablePolicies,
                                                              List<ScrollablePolicy> scrollablePolicies,
                                                              List<EnabledPolicy> enabledPolicies,
                                                              List<BlockedPolicy> blockedPolicies,
                                                              List<WidgetFilterPolicy> widgetFilterPolicies,
                                                              List<ActionDeriver> derivers) {
        return prioritizedWithPolicies(
                clickablePolicies,
                typeablePolicies,
                scrollablePolicies,
                enabledPolicies,
                blockedPolicies,
                widgetFilterPolicies,
                Collections.emptyList(),
                derivers,
                Collections.emptyList()
        );
    }

    public static DefaultActionDerivationService prioritizedWithPolicies(List<ClickablePolicy> clickablePolicies,
                                                                        List<TypeablePolicy> typeablePolicies,
                                                                        List<ScrollablePolicy> scrollablePolicies,
                                                                        List<EnabledPolicy> enabledPolicies,
                                                                        List<BlockedPolicy> blockedPolicies,
                                                                        List<WidgetFilterPolicy> widgetFilterPolicies,
                                                                        List<ActionDeriver> forcedDerivers,
                                                                        List<ActionDeriver> defaultDerivers,
                                                                        List<ActionDeriver> fallbackDerivers) {
        return new DefaultActionDerivationService(
                new ActionDerivationContext(
                        new CompositeClickablePolicy(clickablePolicies),
                        new CompositeTypeablePolicy(typeablePolicies),
                        new CompositeScrollablePolicy(scrollablePolicies),
                        new CompositeEnabledPolicy(enabledPolicies),
                        new CompositeBlockedPolicy(blockedPolicies),
                        new CompositeWidgetFilterPolicy(widgetFilterPolicies)
                ),
                forcedDerivers,
                defaultDerivers,
                fallbackDerivers
        );
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

    public ActionDerivationContext context() {
        return context;
    }

    private Set<Action> derive(SUT system, State state, List<ActionDeriver> derivers) {
        Set<Action> actions = new LinkedHashSet<>();
        for (ActionDeriver deriver : derivers) {
            deriver.derive(system, state, context, actions);
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
