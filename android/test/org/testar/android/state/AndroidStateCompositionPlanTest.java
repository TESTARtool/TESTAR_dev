package org.testar.android.state;

import org.junit.Assert;
import org.junit.Test;
import org.testar.android.tag.AndroidTags;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.engine.policy.SessionPolicyContext;
import org.testar.engine.state.StateCompositionPlan;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public final class AndroidStateCompositionPlanTest {

    @Test
    public void textWidgetsUsesAndroidText() {
        StateStub state = new StateStub();
        child(state, "Save", "", "android.widget.Button", true);
        child(state, "", "", "android.widget.TextView", true);

        StateCompositionPlan plan = StateCompositionPlan.widgetsWithText(system -> state, AndroidTags.AndroidText);

        State projectedState = plan.query(state, new SessionPolicyContext());

        Assert.assertEquals(1, projectedState.childCount());
        Assert.assertEquals("Save", projectedState.child(0).get(AndroidTags.AndroidText, null));
    }

    @Test
    public void semanticWidgetsUseAndroidDescriptor() {
        StateStub state = new StateStub();
        child(state, "Login", "button_login", "android.widget.Button", true);
        child(state, "", "", "android.widget.LinearLayout", true);
        child(state, "Hidden", "hidden_id", "android.widget.TextView", false);

        StateCompositionPlan plan = AndroidStateCompositionPlan.appiumSemanticWidgets(10.0);

        State projectedState = plan.query(state, new SessionPolicyContext());

        Assert.assertEquals(1, projectedState.childCount());
        String description = projectedState.child(0).get(Tags.Desc, "");
        Assert.assertTrue(description.contains("role=android.widget.button"));
        Assert.assertTrue(description.contains("label=Login"));
        Assert.assertTrue(description.contains("value=button_login"));
    }

    private static WidgetStub child(StateStub state,
                                    String text,
                                    String resourceId,
                                    String className,
                                    boolean displayed) {
        WidgetStub child = new WidgetStub();
        child.setRoot(state);
        child.setParent(state);
        child.set(AndroidTags.AndroidText, text);
        child.set(AndroidTags.AndroidResourceId, resourceId);
        child.set(AndroidTags.AndroidClassName, className);
        child.set(AndroidTags.AndroidDisplayed, displayed);
        child.set(Tags.Title, text);
        child.set(Tags.Desc, className);
        state.addChild(child);
        return child;
    }
}
