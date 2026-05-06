package org.testar.android.action.derivation;

import java.util.Collections;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.testar.android.action.AndroidActionClick;
import org.testar.android.action.AndroidActionScroll;
import org.testar.android.action.AndroidActionType;
import org.testar.android.action.policy.AndroidClickablePolicy;
import org.testar.android.action.policy.AndroidScrollablePolicy;
import org.testar.android.action.policy.AndroidTypeablePolicy;
import org.testar.android.alayer.AndroidRoles;
import org.testar.android.tag.AndroidTags;
import org.testar.core.action.Action;
import org.testar.core.alayer.Rect;
import org.testar.core.tag.Tags;
import org.testar.engine.policy.SessionPolicyContext;
import org.testar.engine.policy.composite.CompositeClickablePolicy;
import org.testar.engine.policy.composite.CompositeScrollablePolicy;
import org.testar.engine.policy.composite.CompositeTypeablePolicy;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public final class AndroidWidgetActionDeriverTest {

    @Test
    public void derivesClickActionForClickableWidget() {
        StateStub state = new StateStub();
        WidgetStub widget = childWidget(state);
        widget.set(Tags.Role, AndroidRoles.AndroidButton);
        widget.set(AndroidTags.AndroidClickable, true);
        widget.set(AndroidTags.AndroidEnabled, true);
        widget.set(AndroidTags.AndroidDisplayed, true);

        AndroidWidgetActionDeriver deriver = new AndroidWidgetActionDeriver(w -> "ignored");
        Set<Action> actions = deriver.derive(null, state, widget, clickableTypeableContext());

        Assert.assertEquals(1, actions.size());
        Assert.assertTrue(actions.iterator().next() instanceof AndroidActionClick);
    }

    @Test
    public void derivesTypeActionForEligibleWidget() {
        StateStub state = new StateStub();
        WidgetStub widget = childWidget(state);
        widget.set(Tags.Role, AndroidRoles.AndroidEditText);
        widget.set(AndroidTags.AndroidEnabled, true);
        widget.set(AndroidTags.AndroidFocusable, true);
        widget.set(AndroidTags.AndroidDisplayed, true);

        AndroidWidgetActionDeriver deriver = new AndroidWidgetActionDeriver(w -> "hello");
        Set<Action> actions = deriver.derive(null, state, widget, clickableTypeableContext());

        Assert.assertEquals(1, actions.size());
        Assert.assertTrue(actions.iterator().next() instanceof AndroidActionType);
    }

    @Test
    public void derivesScrollActionForScrollableWidget() {
        StateStub state = new StateStub();
        WidgetStub widget = childWidget(state);
        widget.set(AndroidTags.AndroidScrollable, true);

        AndroidWidgetActionDeriver deriver = new AndroidWidgetActionDeriver(w -> "ignored");
        Set<Action> actions = deriver.derive(null, state, widget, clickableTypeableScrollableContext());

        Assert.assertEquals(1, actions.size());
        Assert.assertTrue(actions.iterator().next() instanceof AndroidActionScroll);
    }

    private static SessionPolicyContext clickableTypeableContext() {
        return clickableTypeableScrollableContext();
    }

    private static SessionPolicyContext clickableTypeableScrollableContext() {
        return new SessionPolicyContext(
                new CompositeClickablePolicy(Collections.singletonList(new AndroidClickablePolicy())),
                new CompositeTypeablePolicy(Collections.singletonList(new AndroidTypeablePolicy())),
                new CompositeScrollablePolicy(Collections.singletonList(new AndroidScrollablePolicy()))
        );
    }

    private static WidgetStub childWidget(StateStub state) {
        WidgetStub widget = new WidgetStub();
        state.addChild(widget);
        widget.setParent(state);
        widget.set(AndroidTags.AndroidXpath, "[0,0,1]");
        widget.set(AndroidTags.AndroidClassName, "android.widget.Button");
        widget.set(AndroidTags.AndroidText, "TextValue");
        widget.set(Tags.Shape, Rect.fromCoordinates(0, 0, 100, 100));
        return widget;
    }
}
