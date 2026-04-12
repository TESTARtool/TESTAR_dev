/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.derivation;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.action.Action;
import org.testar.engine.policy.SessionPolicyContext;
import org.testar.engine.policy.TagBlockedPolicy;
import org.testar.engine.policy.TagEnabledPolicy;
import org.testar.engine.policy.composite.CompositeAtCanvasPolicy;
import org.testar.engine.policy.composite.CompositeBlockedPolicy;
import org.testar.engine.policy.composite.CompositeClickablePolicy;
import org.testar.engine.policy.composite.CompositeEnabledPolicy;
import org.testar.engine.policy.composite.CompositeScrollablePolicy;
import org.testar.engine.policy.composite.CompositeTopLevelPolicy;
import org.testar.engine.policy.composite.CompositeTypeablePolicy;
import org.testar.engine.policy.composite.CompositeVisiblePolicy;
import org.testar.engine.policy.composite.CompositeWidgetFilterPolicy;
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
                (system, currentState, widget, context) -> {
                    visited.incrementAndGet();
                    return Collections.emptySet();
                }
        );

        Set<Action> actions = deriver.derive(null, state, allowAllContext());

        Assert.assertEquals(3, visited.get());
        Assert.assertTrue(actions.isEmpty());
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
                (system, currentState, widget, context) -> {
                    visited.incrementAndGet();
                    return Collections.emptySet();
                }
        );

        Set<Action> actions = deriver.derive(null, state, filteredContext(filtered));

        Assert.assertEquals(2, visited.get());
        Assert.assertTrue(actions.isEmpty());
    }

    @Test
    public void skipsWidgetsRejectedByEnabledPolicy() {
        StateStub state = new StateStub();
        WidgetStub disabled = new WidgetStub();
        disabled.set(org.testar.core.tag.Tags.Enabled, false);
        state.addChild(disabled);

        AtomicInteger visited = new AtomicInteger();
        StateActionDeriver deriver = new StateActionDeriver(
                (system, currentState, widget, context) -> {
                    visited.incrementAndGet();
                    return Collections.emptySet();
                }
        );

        Set<Action> actions = deriver.derive(null, state, allowAllContext());

        Assert.assertEquals(1, visited.get());
        Assert.assertTrue(actions.isEmpty());
    }

    @Test
    public void skipsWidgetsRejectedByBlockedPolicy() {
        StateStub state = new StateStub();
        WidgetStub blocked = new WidgetStub();
        blocked.set(org.testar.core.tag.Tags.Blocked, true);
        state.addChild(blocked);

        AtomicInteger visited = new AtomicInteger();
        StateActionDeriver deriver = new StateActionDeriver(
                (system, currentState, widget, context) -> {
                    visited.incrementAndGet();
                    return Collections.emptySet();
                }
        );

        Set<Action> actions = deriver.derive(null, state, allowAllContext());

        Assert.assertEquals(1, visited.get());
        Assert.assertTrue(actions.isEmpty());
    }

    private static SessionPolicyContext allowAllContext() {
        return new SessionPolicyContext(
                new CompositeClickablePolicy(Collections.emptyList()),
                new CompositeTypeablePolicy(Collections.emptyList()),
                new CompositeScrollablePolicy(Collections.emptyList()),
                new CompositeEnabledPolicy(Collections.singletonList(new TagEnabledPolicy())),
                new CompositeBlockedPolicy(Collections.singletonList(new TagBlockedPolicy())),
                new CompositeWidgetFilterPolicy(Collections.singletonList(widget -> true)),
                new CompositeVisiblePolicy(Collections.singletonList(widget -> true)),
                new CompositeAtCanvasPolicy(Collections.singletonList(widget -> true)),
                new CompositeTopLevelPolicy(Collections.singletonList(widget -> true))
        );
    }

    private static SessionPolicyContext filteredContext(WidgetStub filteredWidget) {
        return new SessionPolicyContext(
                new CompositeClickablePolicy(Collections.emptyList()),
                new CompositeTypeablePolicy(Collections.emptyList()),
                new CompositeScrollablePolicy(Collections.emptyList()),
                new CompositeEnabledPolicy(Collections.singletonList(widget -> true)),
                new CompositeBlockedPolicy(Collections.singletonList(widget -> false)),
                new CompositeWidgetFilterPolicy(Collections.singletonList(widget -> widget != filteredWidget)),
                new CompositeVisiblePolicy(Collections.singletonList(widget -> true)),
                new CompositeAtCanvasPolicy(Collections.singletonList(widget -> true)),
                new CompositeTopLevelPolicy(Collections.singletonList(widget -> true))
        );
    }
}
