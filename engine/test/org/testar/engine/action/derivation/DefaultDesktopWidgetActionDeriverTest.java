/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.derivation;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.Drag;
import org.testar.core.action.Action;
import org.testar.core.action.ActionRoles;
import org.testar.core.action.AnnotatingActionCompiler;
import org.testar.core.alayer.Role;
import org.testar.core.alayer.Position;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.TaggableBase;
import org.testar.engine.policy.CompositeBlockedPolicy;
import org.testar.engine.policy.CompositeClickablePolicy;
import org.testar.engine.policy.CompositeEnabledPolicy;
import org.testar.engine.policy.CompositeScrollablePolicy;
import org.testar.engine.policy.CompositeTypeablePolicy;
import org.testar.engine.policy.CompositeWidgetFilterPolicy;
import org.testar.stub.WidgetStub;

public final class DefaultDesktopWidgetActionDeriverTest {

    @Test
    public void derivesClickActionForClickableRole() {
        Role buttonRole = Role.from("UIAButton");
        WidgetStub widget = new WidgetStub();
        widget.set(org.testar.core.tag.Tags.Role, buttonRole);
        widget.set(org.testar.core.tag.Tags.Desc, "Button");

        DesktopWidgetActionDeriver deriver = new DesktopWidgetActionDeriver(
                new TestActionCompiler(),
                w -> "ignored",
                36.0,
                16.0
        );
        Set<Action> actions = new LinkedHashSet<>();

        deriver.derive(null, null, widget, contextFor(buttonRole, false, false), actions);

        Assert.assertEquals(1, actions.size());
        Assert.assertTrue(actions.iterator().next().get(org.testar.core.tag.Tags.Role, null).isA(ActionRoles.LeftClickAt));
    }

    @Test
    public void derivesTypeActionForTypeableWidget() {
        Role textRole = Role.from("UIAEdit");
        WidgetStub widget = new WidgetStub();
        widget.set(org.testar.core.tag.Tags.Role, textRole);
        widget.set(org.testar.core.tag.Tags.Desc, "TextBox");

        DesktopWidgetActionDeriver deriver = new DesktopWidgetActionDeriver(
                new TestActionCompiler(),
                w -> "hello",
                36.0,
                16.0
        );
        Set<Action> actions = new LinkedHashSet<>();

        deriver.derive(null, null, widget, contextFor(null, true, false), actions);

        Assert.assertEquals(1, actions.size());
        Assert.assertTrue(actions.iterator().next().get(org.testar.core.tag.Tags.Role, null).isA(ActionRoles.ClickTypeInto));
    }

    @Test
    public void derivesScrollActionWhenWidgetSupportsDrags() {
        WidgetStub widget = new ScrollableWidgetStub(new Drag(1, 2, 3, 4));
        DesktopWidgetActionDeriver deriver = new DesktopWidgetActionDeriver(
                new TestActionCompiler(),
                w -> "ignored",
                36.0,
                16.0
        );
        Set<Action> actions = new LinkedHashSet<>();

        deriver.derive(null, null, widget, contextFor(null, false, true), actions);

        Assert.assertEquals(1, actions.size());
        Assert.assertTrue(actions.iterator().next().get(org.testar.core.tag.Tags.Role, null).isA(ActionRoles.LeftDrag));
    }

    private static ActionDerivationContext contextFor(Role clickableRole, boolean typeable, boolean scrollable) {
        return new ActionDerivationContext(
                clickableRole == null
                        ? CompositeClickablePolicy.empty()
                        : new CompositeClickablePolicy(java.util.Collections.singletonList(
                                widget -> clickableRole.equals(widget.get(org.testar.core.tag.Tags.Role, null))
                        )),
                typeable
                        ? new CompositeTypeablePolicy(java.util.Collections.singletonList(widget -> true))
                        : CompositeTypeablePolicy.empty(),
                scrollable
                        ? new CompositeScrollablePolicy(java.util.Collections.singletonList(w -> true))
                        : CompositeScrollablePolicy.empty(),
                CompositeEnabledPolicy.allowAll(),
                CompositeBlockedPolicy.allowNone(),
                CompositeWidgetFilterPolicy.allowAll()
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
