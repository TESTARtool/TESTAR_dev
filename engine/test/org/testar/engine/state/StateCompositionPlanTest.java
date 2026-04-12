package org.testar.engine.state;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.engine.policy.SessionPolicyContext;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public final class StateCompositionPlanTest {

    @Test
    public void leafWidgetsPlanReturnsOnlyLeafWidgets() {
        StateStub state = new StateStub();
        state.set(Tags.Title, "root");
        WidgetStub parent = child(state, "parent");
        child(parent, "leaf-1");
        child(state, "leaf-2");

        State projectedState = StateCompositionPlan.leafWidgets(system -> state)
                .query(state, new SessionPolicyContext());

        List<String> widgetTitles = widgetTitles(projectedState);
        Assert.assertEquals(2, widgetTitles.size());
        Assert.assertTrue(widgetTitles.contains("leaf-1"));
        Assert.assertTrue(widgetTitles.contains("leaf-2"));
        Assert.assertEquals("root", projectedState.get(Tags.Title, null));
    }

    private static WidgetStub child(StateStub parentState, String title) {
        WidgetStub child = new WidgetStub();
        child.setRoot(parentState);
        child.setParent(parentState);
        child.set(Tags.Title, title);
        parentState.addChild(child);
        return child;
    }

    private static WidgetStub child(WidgetStub parentWidget, String title) {
        WidgetStub child = new WidgetStub();
        child.setRoot(parentWidget.root());
        child.setParent(parentWidget);
        child.set(Tags.Title, title);
        parentWidget.addChild(child);
        return child;
    }

    private static List<String> widgetTitles(State state) {
        List<String> widgetTitles = new ArrayList<>();
        for (Widget widget : state) {
            if (widget == state) {
                continue;
            }
            widgetTitles.add(widget.get(Tags.Title, null));
        }
        return widgetTitles;
    }
}
