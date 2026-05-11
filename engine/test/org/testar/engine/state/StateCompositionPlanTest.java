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

    @Test
    public void semanticWidgetsPlanDisambiguatesDuplicateDescriptions() {
        StateStub state = new StateStub();
        WidgetStub container = child(state, "Question group");
        container.set(Tags.Desc, "Question group");

        WidgetStub firstField = child(container, "field-1");
        firstField.set(Tags.AbstractID, "widget-1");

        WidgetStub secondField = child(container, "field-2");
        secondField.set(Tags.AbstractID, "widget-2");

        State projectedState = StateCompositionPlan.semanticWidgets(system -> state, new DuplicateDescriptor())
                .query(state, new SessionPolicyContext());

        List<String> descriptions = widgetDescriptions(projectedState);
        Assert.assertEquals(2, descriptions.size());
        Assert.assertTrue(descriptions.contains("role=input:text;label=Type your answer here.. [within 'Question group' #1]"));
        Assert.assertTrue(descriptions.contains("role=input:text;label=Type your answer here.. [within 'Question group' #2]"));
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

    private static List<String> widgetDescriptions(State state) {
        List<String> widgetDescriptions = new ArrayList<>();
        for (Widget widget : state) {
            if (widget == state) {
                continue;
            }
            widgetDescriptions.add(widget.get(Tags.Desc, null));
        }
        return widgetDescriptions;
    }

    private static final class DuplicateDescriptor implements SemanticWidgetDescriptor {

        @Override
        public boolean shouldInclude(Widget widget) {
            return widget.root() != widget && widget.childCount() == 0;
        }

        @Override
        public String roleOf(Widget widget) {
            return "input:text";
        }

        @Override
        public String labelOf(Widget widget) {
            return "Type your answer here..";
        }

        @Override
        public String valueOf(Widget widget) {
            return "";
        }
    }
}
