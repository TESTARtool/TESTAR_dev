package org.testar.windows.tag;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.engine.policy.SessionPolicyContext;
import org.testar.engine.state.StateCompositionPlan;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public final class WindowsStateCompositionPlanTest {

    @Test
    public void textWidgetsUsesWidgetTitle() {
        StateStub state = new StateStub();
        child(state, "Save");
        child(state, "");

        StateCompositionPlan plan = StateCompositionPlan.widgetsWithText(system -> state, Tags.Title);

        State projectedState = plan.query(state, new SessionPolicyContext());

        Assert.assertEquals(1, projectedState.childCount());
        Assert.assertEquals("Save", projectedState.child(0).get(Tags.Title, null));
    }

    private static WidgetStub child(StateStub state, String title) {
        WidgetStub child = new WidgetStub();
        child.setRoot(state);
        child.setParent(state);
        child.set(Tags.Title, title);
        state.addChild(child);
        return child;
    }
}
