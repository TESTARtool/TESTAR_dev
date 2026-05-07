/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.resolver;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.action.Action;
import org.testar.core.action.ActionRoles;
import org.testar.core.action.resolver.ResolvedAction;
import org.testar.core.alayer.Role;
import org.testar.core.alayer.Rect;
import org.testar.core.exceptions.ActionFailedException;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.TaggableBase;
import org.testar.core.tag.Tags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public final class DescriptionActionResolverTest {

    private final DescriptionActionResolver resolver = new DescriptionActionResolver();

    @Test
    public void resolveClickPrefersExactDescriptionMatch() {
        TestClickAction containsMatch = new TestClickAction("Android click on widget with text 'Sign In Submit'");
        containsMatch.set(Tags.AbstractID, "AAcontains");

        TestClickAction exactMatch = new TestClickAction("Sign In Submit");
        exactMatch.set(Tags.AbstractID, "AAexact");

        ResolvedAction resolvedAction = resolver.resolve(
                orderedActions(containsMatch, exactMatch),
                List.of("click", "Sign In Submit")
        );

        Assert.assertSame(exactMatch, resolvedAction.action());
        Assert.assertEquals("AAexact", resolvedAction.action().get(Tags.AbstractID, ""));
    }

    @Test
    public void resolveClickFallsBackToContainsMatch() {
        TestClickAction clickAction = new TestClickAction("Android click on widget with text 'Sign In Submit'");

        ResolvedAction resolvedAction = resolver.resolve(
                orderedActions(clickAction),
                List.of("click", "Sign In")
        );

        Assert.assertSame(clickAction, resolvedAction.action());
    }

    @Test
    public void resolveTypeRebuildsSameClassAndPreservesIdentityTags() {
        StateStub state = new StateStub();
        WidgetStub widget = createOriginWidget(state);
        SameClassTypeAction templateAction = new SameClassTypeAction(state, widget, "RandomInput");
        templateAction.set(Tags.AbstractID, "AAtype123");
        templateAction.set(Tags.ConcreteID, "CCtype123");
        templateAction.set(Tags.Desc, "Enter your username");

        ResolvedAction resolvedAction = resolver.resolve(
                orderedActions(templateAction),
                List.of("type", "Enter your username", "testar")
        );

        Assert.assertTrue(resolvedAction.action() instanceof SameClassTypeAction);
        Assert.assertEquals("AAtype123", resolvedAction.action().get(Tags.AbstractID, ""));
        Assert.assertEquals("CCtype123", resolvedAction.action().get(Tags.ConcreteID, ""));
        Assert.assertEquals("testar", resolvedAction.action().get(Tags.InputText, ""));
    }

    @Test
    public void resolveTypeFallsBackToGenericRebuildAndPreservesIdentityTags() {
        StateStub state = new StateStub();
        WidgetStub widget = createOriginWidget(state);
        FallbackTypeTemplateAction templateAction = new FallbackTypeTemplateAction(widget);
        templateAction.set(Tags.AbstractID, "AAfallback");
        templateAction.set(Tags.ConcreteID, "CCfallback");
        templateAction.set(Tags.Desc, "Enter your password");

        ResolvedAction resolvedAction = resolver.resolve(
                orderedActions(templateAction),
                List.of("type", "Enter your password", "Secret123")
        );

        Assert.assertNotSame(templateAction, resolvedAction.action());
        Assert.assertEquals("AAfallback", resolvedAction.action().get(Tags.AbstractID, ""));
        Assert.assertEquals("CCfallback", resolvedAction.action().get(Tags.ConcreteID, ""));
        Assert.assertTrue(resolvedAction.action().get(Tags.Desc, "").contains("Secret123"));
    }

    @Test
    public void resolveTypeMatchesMaskedPlaceholderText() {
        StateStub state = new StateStub();
        WidgetStub widget = createOriginWidget(state);
        SameClassTypeAction templateAction = new SameClassTypeAction(state, widget, "RandomInput");
        templateAction.set(Tags.Desc, "Android type on widget with hint '?????????' typing text: fooboo@org.com");

        ResolvedAction resolvedAction = resolver.resolve(
                orderedActions(templateAction),
                List.of("type", "????", "secretPass123")
        );

        Assert.assertTrue(resolvedAction.action() instanceof SameClassTypeAction);
        Assert.assertEquals("secretPass123", resolvedAction.action().get(Tags.InputText, ""));
    }

    @Test
    public void resolveRejectsUnsupportedMode() {
        IllegalArgumentException exception = expectIllegalArgumentException(
                () -> resolver.resolve(orderedActions(), List.of("drag", "Anything"))
        );

        Assert.assertEquals("Unsupported executeAction mode: drag", exception.getMessage());
    }

    @Test
    public void resolveRequiresSemanticText() {
        IllegalArgumentException exception = expectIllegalArgumentException(
                () -> resolver.resolve(orderedActions(), List.of("click"))
        );

        Assert.assertEquals("executeAction click requires semantic text", exception.getMessage());
    }

    @Test
    public void resolveTypeRequiresInputText() {
        IllegalArgumentException exception = expectIllegalArgumentException(
                () -> resolver.resolve(orderedActions(), List.of("type", "Username"))
        );

        Assert.assertEquals("executeAction type requires input text", exception.getMessage());
    }

    private WidgetStub createOriginWidget(StateStub state) {
        WidgetStub widget = new WidgetStub();
        state.addChild(widget);
        widget.setParent(state);
        widget.set(Tags.Shape, Rect.fromCoordinates(0, 0, 100, 100));
        widget.set(Tags.ConcreteID, "WCorigin123");
        widget.set(Tags.Desc, "Origin widget");
        return widget;
    }

    private Set<Action> orderedActions(Action... actions) {
        Set<Action> orderedActions = new LinkedHashSet<>();
        for (Action action : actions) {
            orderedActions.add(action);
        }
        return orderedActions;
    }

    private IllegalArgumentException expectIllegalArgumentException(ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch (IllegalArgumentException exception) {
            return exception;
        } catch (Exception exception) {
            throw new AssertionError("Expected IllegalArgumentException", exception);
        }
        throw new AssertionError("Expected IllegalArgumentException");
    }

    private interface ThrowingRunnable {

        void run() throws Exception;
    }

    private static final class TestClickAction extends TaggableBase implements Action {

        private static final long serialVersionUID = 1L;

        private TestClickAction(String description) {
            set(Tags.Role, ActionRoles.LeftClickAt);
            set(Tags.Desc, description);
        }

        @Override
        public void run(SUT system, State state, double duration) throws ActionFailedException {
        }

        @Override
        public String toShortString() {
            return get(Tags.Desc, "");
        }

        @Override
        public String toParametersString() {
            return "";
        }

        @Override
        public String toString(Role... discardParameters) {
            return get(Tags.Desc, "");
        }
    }

    private static final class SameClassTypeAction extends TaggableBase implements Action {

        private static final long serialVersionUID = 1L;

        public SameClassTypeAction(State state, Widget widget, String inputText) {
            set(Tags.Role, ActionRoles.ClickTypeInto);
            mapOriginWidget(widget);
            set(Tags.InputText, inputText);
            set(Tags.Desc, widget.get(Tags.Desc, "Type action"));
        }

        @Override
        public void run(SUT system, State state, double duration) throws ActionFailedException {
        }

        @Override
        public String toShortString() {
            return get(Tags.Desc, "");
        }

        @Override
        public String toParametersString() {
            return "";
        }

        @Override
        public String toString(Role... discardParameters) {
            return get(Tags.Desc, "");
        }
    }

    private static final class FallbackTypeTemplateAction extends TaggableBase implements Action {

        private static final long serialVersionUID = 1L;

        private FallbackTypeTemplateAction(Widget widget) {
            set(Tags.Role, ActionRoles.ClickTypeInto);
            mapOriginWidget(widget);
        }

        @Override
        public void run(SUT system, State state, double duration) throws ActionFailedException {
        }

        @Override
        public String toShortString() {
            return get(Tags.Desc, "");
        }

        @Override
        public String toParametersString() {
            return "";
        }

        @Override
        public String toString(Role... discardParameters) {
            return get(Tags.Desc, "");
        }
    }
}
