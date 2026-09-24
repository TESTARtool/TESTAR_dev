package org.testar.scriptless.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.testar.core.tag.Tag;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public final class WidgetMatchingUtilTest {

    private static final Tag<String> TITLE = Tag.from("WebTitle", String.class);
    private static final Tag<String> CLASS_NAME = Tag.from("WebClassName", String.class);

    @Test
    public void findsWidgetByExactOrContainedTagValue() {
        StateStub state = stateWithWidget("Welcome to TESTAR", "primary");

        assertEquals(state.child(0), WidgetMatchingUtil.getWidgetWithMatchingTag(TITLE, "Welcome to TESTAR", state));
        assertEquals(state.child(0), WidgetMatchingUtil.getWidgetWithMatchingTag(TITLE, "TESTAR", state));
        assertNull(WidgetMatchingUtil.getWidgetWithMatchingTag(TITLE, "missing", state));
    }

    @Test
    public void findsWidgetByTagName() {
        StateStub state = stateWithWidget("Welcome to TESTAR", "primary");

        assertEquals(
                state.child(0),
                WidgetMatchingUtil.getWidgetWithMatchingTag("WebTitle", "Welcome to TESTAR", state)
        );
        assertEquals(state.child(0), WidgetMatchingUtil.getWidgetWithMatchingTag("WebTitle", "TESTAR", state));
        assertNull(WidgetMatchingUtil.getWidgetWithMatchingTag("WebTitle", "missing", state));
        assertNull(WidgetMatchingUtil.getWidgetWithMatchingTag("Unknown", "Welcome to TESTAR", state));
    }

    @Test
    public void findsWidgetByAllTagValues() {
        StateStub state = stateWithWidget("Welcome", "primary");
        Map<String, String> values = new LinkedHashMap<>();
        values.put("WebTitle", "Welcome");
        values.put("WebClassName", "primary");

        assertEquals(state.child(0), WidgetMatchingUtil.getWidgetWithMatchingTags(values, state));

        values.put("WebClassName", "secondary");
        assertNull(WidgetMatchingUtil.getWidgetWithMatchingTags(values, state));
    }

    @Test
    public void returnsNullForEmptyStateOrUnknownTag() {
        StateStub emptyState = new StateStub();
        assertNull(WidgetMatchingUtil.getWidgetWithMatchingTag(TITLE, "Welcome", emptyState));
        assertNull(WidgetMatchingUtil.getWidgetWithMatchingTags(Map.of("Unknown", "value"), emptyState));

        StateStub state = stateWithWidget("Welcome", "primary");
        assertNull(WidgetMatchingUtil.getWidgetWithMatchingTags(Map.of("Unknown", "value"), state));
    }

    private static StateStub stateWithWidget(String title, String className) {
        StateStub state = new StateStub();
        WidgetStub widget = new WidgetStub();
        widget.setParent(state);
        widget.set(TITLE, title);
        widget.set(CLASS_NAME, className);
        state.addChild(widget);
        return state;
    }
}
