/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.windows.action.derivation;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.Drag;
import org.testar.core.action.Action;
import org.testar.core.action.ActionRoles;
import org.testar.core.action.AnnotatingActionCompiler;
import org.testar.core.alayer.Position;
import org.testar.core.alayer.Role;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.TaggableBase;
import org.testar.engine.policy.SessionPolicyContext;
import org.testar.engine.policy.composite.CompositeAtCanvasPolicy;
import org.testar.engine.policy.composite.CompositeBlockedPolicy;
import org.testar.engine.policy.composite.CompositeClickablePolicy;
import org.testar.engine.policy.composite.CompositeEnabledPolicy;
import org.testar.engine.policy.composite.CompositeScrollablePolicy;
import org.testar.engine.policy.composite.CompositeTopLevelPolicy;
import org.testar.engine.policy.composite.CompositeTypeablePolicy;
import org.testar.engine.policy.composite.CompositeVisiblePolicy;
import org.testar.engine.policy.composite.CompositeWidgetFilterPolicy;
import org.testar.stub.WidgetStub;

public final class WindowsWidgetActionDeriverTest {

    @Test
    public void derivesClickActionForClickableRole() {
        Role buttonRole = Role.from("UIAButton");
        WidgetStub widget = new WidgetStub();
        widget.set(org.testar.core.tag.Tags.Role, buttonRole);
        widget.set(org.testar.core.tag.Tags.Desc, "Button");

        WindowsWidgetActionDeriver deriver = new WindowsWidgetActionDeriver(
                new TestActionCompiler(),
                w -> "ignored",
                36.0,
                16.0
        );
        Set<Action> actions = deriver.derive(null, null, widget, contextFor(buttonRole, false, false));

        Assert.assertEquals(1, actions.size());
        Assert.assertTrue(actions.iterator().next().get(org.testar.core.tag.Tags.Role, null).isA(ActionRoles.LeftClickAt));
    }

    @Test
    public void derivesTypeActionForTypeableWidget() {
        Role textRole = Role.from("UIAEdit");
        WidgetStub widget = new WidgetStub();
        widget.set(org.testar.core.tag.Tags.Role, textRole);
        widget.set(org.testar.core.tag.Tags.Desc, "TextBox");

        WindowsWidgetActionDeriver deriver = new WindowsWidgetActionDeriver(
                new TestActionCompiler(),
                w -> "hello",
                36.0,
                16.0
        );
        Set<Action> actions = deriver.derive(null, null, widget, contextFor(null, true, false));

        Assert.assertEquals(1, actions.size());
        Assert.assertTrue(actions.iterator().next().get(org.testar.core.tag.Tags.Role, null).isA(ActionRoles.ClickTypeInto));
    }

    @Test
    public void derivesScrollActionWhenWidgetSupportsDrags() {
        WidgetStub widget = new ScrollableWidgetStub(new Drag(1, 2, 3, 4));
        WindowsWidgetActionDeriver deriver = new WindowsWidgetActionDeriver(
                new TestActionCompiler(),
                w -> "ignored",
                36.0,
                16.0
        );
        Set<Action> actions = deriver.derive(null, null, widget, contextFor(null, false, true));

        Assert.assertEquals(1, actions.size());
        Assert.assertTrue(actions.iterator().next().get(org.testar.core.tag.Tags.Role, null).isA(ActionRoles.LeftDrag));
    }

    private static SessionPolicyContext contextFor(Role clickableRole, boolean typeable, boolean scrollable) {
        return new SessionPolicyContext(
                clickableRole == null
                        ? new CompositeClickablePolicy(java.util.Collections.emptyList())
                        : new CompositeClickablePolicy(java.util.Collections.singletonList(
                                widget -> clickableRole.equals(widget.get(org.testar.core.tag.Tags.Role, null))
                        )),
                typeable
                        ? new CompositeTypeablePolicy(java.util.Collections.singletonList(widget -> true))
                        : new CompositeTypeablePolicy(java.util.Collections.emptyList()),
                scrollable
                        ? new CompositeScrollablePolicy(java.util.Collections.singletonList(w -> true))
                        : new CompositeScrollablePolicy(java.util.Collections.emptyList()),
                new CompositeEnabledPolicy(java.util.Collections.singletonList(widget -> true)),
                new CompositeBlockedPolicy(java.util.Collections.singletonList(widget -> false)),
                new CompositeWidgetFilterPolicy(java.util.Collections.singletonList(widget -> true)),
                new CompositeVisiblePolicy(java.util.Collections.singletonList(widget -> true)),
                new CompositeAtCanvasPolicy(java.util.Collections.singletonList(widget -> true)),
                new CompositeTopLevelPolicy(java.util.Collections.singletonList(widget -> true))
        );
    }

    private static final class ScrollableWidgetStub extends WidgetStub {

        private static final long serialVersionUID = 1L;
        private final Drag[] drags;

        private ScrollableWidgetStub(Drag... drags) {
            this.drags = drags;
        }

        @Override
        public Drag[] scrollDrags(double scrollArrowSize, double scrollThick) {
            return drags;
        }
    }

    private static final class TestActionCompiler extends AnnotatingActionCompiler {

        @Override
        public Action leftClickAt(org.testar.core.state.Widget widget, double relX, double relY) {
            return new TestAction(ActionRoles.LeftClickAt);
        }

        @Override
        public Action clickTypeInto(org.testar.core.state.Widget widget, double relX, double relY, String text,
                                    boolean replaceText) {
            return new TestAction(ActionRoles.ClickTypeInto);
        }

        @Override
        public Action slideFromTo(Position from, Position to, org.testar.core.state.Widget widget) {
            return new TestAction(ActionRoles.LeftDrag);
        }
    }

    private static final class TestAction extends TaggableBase implements Action {

        private static final long serialVersionUID = 1L;

        private TestAction(Role role) {
            set(org.testar.core.tag.Tags.Role, role);
        }

        @Override
        public void run(SUT system, State state, double duration) {
        }

        @Override
        public String toShortString() {
            return "test";
        }

        @Override
        public String toParametersString() {
            return "";
        }

        @Override
        public String toString(Role... discardParameters) {
            return "test";
        }
    }
}
