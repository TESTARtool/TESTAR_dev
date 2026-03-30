/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.action.Action;
import org.testar.engine.action.policy.CompositeClickablePolicy;
import org.testar.engine.action.policy.CompositeScrollablePolicy;
import org.testar.engine.action.policy.CompositeTypeablePolicy;
import org.testar.engine.action.policy.CompositeWidgetFilterPolicy;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public final class StateActionDeriverTest {

    @Test
    public void delegatesForEveryAllowedWidget() {
        StateStub state = new StateStub();
        WidgetStub child1 = new WidgetStub();
        WidgetStub child2 = new WidgetStub();
        state.addChild(child1);
        state.addChild(child2);

        AtomicInteger visited = new AtomicInteger();
        StateActionDeriver deriver = new StateActionDeriver(
                (system, currentState, widget, context, actions) -> visited.incrementAndGet()
        );

        deriver.derive(null, state, allowAllContext(), Collections.<Action>emptySet());

        Assert.assertEquals(3, visited.get());
    }

    @Test
    public void skipsWidgetsRejectedByFilterPolicy() {
        StateStub state = new StateStub();
        WidgetStub allowed = new WidgetStub();
        WidgetStub filtered = new WidgetStub();
        state.addChild(allowed);
        state.addChild(filtered);

        AtomicInteger visited = new AtomicInteger();
        StateActionDeriver deriver = new StateActionDeriver(
                (system, currentState, widget, context, actions) -> visited.incrementAndGet()
        );

        deriver.derive(null, state, filteredContext(filtered), Collections.<Action>emptySet());

        Assert.assertEquals(2, visited.get());
    }

    private static ActionDerivationContext allowAllContext() {
        return new ActionDerivationContext(
                CompositeClickablePolicy.empty(),
                CompositeTypeablePolicy.empty(),
                CompositeScrollablePolicy.empty(),
                CompositeWidgetFilterPolicy.allowAll()
        );
    }

    private static ActionDerivationContext filteredContext(WidgetStub filteredWidget) {
        return new ActionDerivationContext(
                CompositeClickablePolicy.empty(),
                CompositeTypeablePolicy.empty(),
                CompositeScrollablePolicy.empty(),
                new CompositeWidgetFilterPolicy(Collections.singletonList(widget -> widget != filteredWidget))
        );
    }
}
