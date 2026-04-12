package org.testar.webdriver.state;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.state.State;
import org.testar.engine.policy.SessionPolicyContext;
import org.testar.engine.state.StateCompositionPlan;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;
import org.testar.webdriver.tag.WdTags;

public final class WebdriverStateCompositionPlanTest {

    @Test
    public void textWidgetsUsesWebTextContent() {
        StateStub state = new StateStub();
        child(state, "Submit");
        child(state, " ");

        StateCompositionPlan plan = StateCompositionPlan.widgetsWithText(system -> state, WdTags.WebTextContent);

        State projectedState = plan.query(state, new SessionPolicyContext());

        Assert.assertEquals(1, projectedState.childCount());
        Assert.assertEquals("Submit", projectedState.child(0).get(WdTags.WebTextContent, null));
    }

    private static WidgetStub child(StateStub state, String textContent) {
        WidgetStub child = new WidgetStub();
        child.setRoot(state);
        child.setParent(state);
        child.set(WdTags.WebTextContent, textContent);
        state.addChild(child);
        return child;
    }
}
