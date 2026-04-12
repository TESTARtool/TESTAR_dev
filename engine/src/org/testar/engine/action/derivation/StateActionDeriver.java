/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.derivation;

import java.util.LinkedHashSet;
import java.util.Set;

import org.testar.core.Assert;
import org.testar.core.action.Action;
import org.testar.core.policy.AtCanvasPolicy;
import org.testar.core.policy.BlockedPolicy;
import org.testar.core.policy.EnabledPolicy;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.policy.TopLevelPolicy;
import org.testar.core.policy.VisiblePolicy;
import org.testar.core.policy.WidgetFilterPolicy;
import org.testar.engine.policy.SessionPolicyContext;

/**
 * Traverses the state and returns the actions produced for every widget
 * allowed by the active derivation policies.
 */
public final class StateActionDeriver implements ActionDeriver {

    private final WidgetActionDeriver widgetActionDeriver;

    public StateActionDeriver(WidgetActionDeriver widgetActionDeriver) {
        this.widgetActionDeriver = Assert.notNull(widgetActionDeriver);
    }

    @Override
    public Set<Action> derive(SUT system, State state, SessionPolicyContext context) {
        Assert.notNull(state, context);
        Set<Action> actions = new LinkedHashSet<>();
        EnabledPolicy enabledPolicy = context.require(EnabledPolicy.class);
        BlockedPolicy blockedPolicy = context.require(BlockedPolicy.class);
        WidgetFilterPolicy widgetFilterPolicy = context.require(WidgetFilterPolicy.class);
        VisiblePolicy visiblePolicy = context.require(VisiblePolicy.class);
        AtCanvasPolicy atCanvasPolicy = context.require(AtCanvasPolicy.class);
        TopLevelPolicy topLevelPolicy = context.require(TopLevelPolicy.class);
        for (Widget widget : state) {
            if (enabledPolicy.isEnabled(widget)
                    && !blockedPolicy.isBlocked(widget)
                    && widgetFilterPolicy.allows(widget)
                    && visiblePolicy.isVisible(widget)
                    && atCanvasPolicy.isAtCanvas(widget)
                    && topLevelPolicy.isTopLevel(widget)) {
                actions.addAll(widgetActionDeriver.derive(system, state, widget, context));
            }
        }
        return actions;
    }
}
