/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.derivation;

import java.util.Set;

import org.testar.core.Assert;
import org.testar.core.action.Action;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.Widget;

/**
 * Traverses the state and delegates action contribution for every
 * widget allowed by the active filter policy.
 */
public final class StateActionDeriver implements ActionDeriver {

    private final WidgetActionDeriver widgetActionDeriver;

    public StateActionDeriver(WidgetActionDeriver widgetActionDeriver) {
        this.widgetActionDeriver = Assert.notNull(widgetActionDeriver);
    }

    @Override
    public void derive(SUT system, State state, ActionDerivationContext context, Set<Action> actions) {
        Assert.notNull(state, context, actions);
        for (Widget widget : state) {
            if (context.enabledPolicy().isEnabled(widget)
                    && !context.blockedPolicy().isBlocked(widget)
                    && context.widgetFilterPolicy().allows(widget)) {
                widgetActionDeriver.derive(system, state, widget, context, actions);
            }
        }
    }
}
