/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.derivation;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.action.Action;
import org.testar.engine.policy.CompositeBlockedPolicy;
import org.testar.engine.policy.CompositeClickablePolicy;
import org.testar.engine.policy.CompositeEnabledPolicy;
import org.testar.engine.policy.CompositeScrollablePolicy;
import org.testar.engine.policy.CompositeTypeablePolicy;
import org.testar.engine.policy.CompositeWidgetFilterPolicy;
import org.testar.engine.policy.TagBlockedPolicy;
import org.testar.engine.policy.TagEnabledPolicy;
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

    @Test
    public void skipsWidgetsRejectedByEnabledPolicy() {
        StateStub state = new StateStub();
        WidgetStub disabled = new WidgetStub();
        disabled.set(org.testar.core.tag.Tags.Enabled, false);
        state.addChild(disabled);

        AtomicInteger visited = new AtomicInteger();
        StateActionDeriver deriver = new StateActionDeriver(
                (system, currentState, widget, context, actions) -> visited.incrementAndGet()
        );

        deriver.derive(null, state, allowAllContext(), Collections.<Action>emptySet());

        Assert.assertEquals(1, visited.get());
    }

    @Test
    public void skipsWidgetsRejectedByBlockedPolicy() {
        StateStub state = new StateStub();
        WidgetStub blocked = new WidgetStub();
        blocked.set(org.testar.core.tag.Tags.Blocked, true);
        state.addChild(blocked);

        AtomicInteger visited = new AtomicInteger();
        StateActionDeriver deriver = new StateActionDeriver(
                (system, currentState, widget, context, actions) -> visited.incrementAndGet()
        );

        deriver.derive(null, state, allowAllContext(), Collections.<Action>emptySet());

        Assert.assertEquals(1, visited.get());
    }

    private static ActionDerivationContext allowAllContext() {
        return new ActionDerivationContext(
                CompositeClickablePolicy.empty(),
                CompositeTypeablePolicy.empty(),
                CompositeScrollablePolicy.empty(),
                new CompositeEnabledPolicy(Collections.singletonList(new TagEnabledPolicy())),
                new CompositeBlockedPolicy(Collections.singletonList(new TagBlockedPolicy())),
                CompositeWidgetFilterPolicy.allowAll()
        );
    }

    private static ActionDerivationContext filteredContext(WidgetStub filteredWidget) {
        return new ActionDerivationContext(
                CompositeClickablePolicy.empty(),
                CompositeTypeablePolicy.empty(),
                CompositeScrollablePolicy.empty(),
                CompositeEnabledPolicy.allowAll(),
                CompositeBlockedPolicy.allowNone(),
                new CompositeWidgetFilterPolicy(Collections.singletonList(widget -> widget != filteredWidget))
        );
    }
}
