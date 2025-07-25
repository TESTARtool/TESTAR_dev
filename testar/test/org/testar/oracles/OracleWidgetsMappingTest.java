package org.testar.oracles;

import org.junit.Before;
import org.junit.Test;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

import static org.junit.Assert.*;

import java.util.List;

public class OracleWidgetsMappingTest {

    private OracleWidgetsMapping mapping;
    private StateStub state;

    @Before
    public void setup() {
        mapping = new OracleWidgetsMapping() {};
        state = new StateStub();
    }

    private WidgetStub createWidget(WidgetStub parent, Role role, String webTextContent) {
        WidgetStub widget = new WidgetStub();
        widget.set(Tags.Role, role);
        widget.set(WdTags.WebTextContent, webTextContent);
        widget.setParent(parent);
        parent.addChild(widget);
        return widget;
    }

    /** Get Widget tests **/

    @Test
    public void testGetExistingWidget() {
        WidgetStub widgetMenuItem = createWidget(state, WdRoles.WdLI, "Home");
        assertNotNull(mapping.getWidget("menu_item", "Home", state));
        assertEquals(widgetMenuItem, mapping.getWidget("menu_item", "Home", state));
    }

    @Test
    public void testGetNotExistingWidget() {
        // The DSL getVerdict allows and manages the null logic
        assertNull(mapping.getWidget("menu_item", "BlaBla", state));
    }

    /** Get List of Widgets tests **/

    @Test
    public void testGetListExistingWidgets() {
        WidgetStub home = createWidget(state, WdRoles.WdLI, "Home");
        WidgetStub contact = createWidget(state, WdRoles.WdLI, "Contact");

        var widgets = mapping.getWidgets("menu_item", state);
        assertNotNull(widgets);
        assertEquals(2, widgets.size());
        assertTrue(widgets.contains(home));
        assertTrue(widgets.contains(contact));
    }

    @Test
    public void testGetListEmptyWidgets() {
        var widgets = mapping.getWidgets("some_widget", state);
        assertNotNull(widgets);
        assertEquals(0, widgets.size());
    }

    /** Get Property tests **/

    @Test
    public void testGetNotNullWidget() {
        Object result = mapping.getProperty(new WidgetStub(), "random");
        assertNotNull(result);
    }

    @Test
    public void testGetNotNullProperty() {
        WidgetStub home = createWidget(state, WdRoles.WdLI, "Home");

        Object result = mapping.getProperty(home, "random");
        assertNotNull(result);
    }

    /** Evaluate Contains tests **/

    @Test
    public void testDefaultPropertyContains() {
        String text = "Home";
        WidgetStub home = createWidget(state, WdRoles.WdLI, text);

        Object textObject = mapping.getProperty(home, "text");
        assertNotNull(textObject);
        assertEquals(text, textObject);
        assertTrue(mapping.evaluateContains(textObject, "Home"));
        assertFalse(mapping.evaluateContains(textObject, "OTHER"));
    }

    @Test
    public void testStringPropertyContains() {
        WidgetStub home = createWidget(state, WdRoles.WdLI, "Home");

        String fontSize = "11px";
        home.set(WdTags.WebComputedFontSize, fontSize);

        Object fontSizeObject = mapping.getProperty(home, "fontsize");
        assertNotNull(fontSizeObject);
        assertEquals(fontSize, fontSizeObject);
        assertTrue(mapping.evaluateContains(fontSizeObject, "11px"));
        assertFalse(mapping.evaluateContains(fontSizeObject, "12px"));
    }

    /** Evaluate Is Equals tests **/

    @Test
    public void testColorPropertyIsEquals() {
        WidgetStub home = createWidget(state, WdRoles.WdLI, "Home");

        java.awt.Color color = new java.awt.Color(0, 1, 2);
        home.set(WdTags.WebComputedColor, color);

        Object colorProperty = mapping.getProperty(home, "color");
        assertNotNull(colorProperty);
        assertEquals(color, colorProperty);
        assertTrue(mapping.evaluateIsEqualTo(colorProperty, color));
        assertFalse(mapping.evaluateIsEqualTo(colorProperty, new java.awt.Color(5, 5, 5)));
    }

    /** Evaluate Is Status tests **/

    @Test
    public void testWidgetTrueStatus() {
        WidgetStub home = createWidget(state, WdRoles.WdLI, "Home");
        home.set(WdTags.WebIsDisplayed, true);

        assertNotNull(mapping.evaluateIsStatus(home, "visible"));
        assertTrue(mapping.evaluateIsStatus(home, "visible"));
    }

    @Test
    public void testWidgetObjectTrueStatus() {
        WidgetStub home = createWidget(state, WdRoles.WdLI, "Home");
        home.set(WdTags.WebIsDisplayed, true);

        assertNotNull(mapping.evaluateIsStatus((Object)home, "visible"));
        assertTrue(mapping.evaluateIsStatus((Object)home, "visible"));
    }

    @Test
    public void testWidgetFalseStatus() {
        WidgetStub home = createWidget(state, WdRoles.WdLI, "Home");
        home.set(WdTags.WebIsFullOnScreen, false);

        assertNotNull(mapping.evaluateIsStatus(home, "visible"));
        assertFalse(mapping.evaluateIsStatus(home, "visible"));
    }

    @Test
    public void testWidgetObjectFalseStatus() {
        WidgetStub home = createWidget(state, WdRoles.WdLI, "Home");
        home.set(WdTags.WebIsFullOnScreen, false);

        assertNotNull(mapping.evaluateIsStatus((Object)home, "visible"));
        assertFalse(mapping.evaluateIsStatus((Object)home, "visible"));
    }

    /** Evaluate Has Attribute tests **/

    @Test
    public void testWidgetHasAttribute() {
        WidgetStub home = createWidget(state, WdRoles.WdLI, "Home");
        home.set(WdTags.WebComputedColor, new java.awt.Color(0, 1, 2));

        assertNotNull(mapping.evaluateHasAttribute(home, "text"));
        assertTrue(mapping.evaluateHasAttribute(home, "text"));

        assertNotNull(mapping.evaluateHasAttribute(home, "color"));
        assertTrue(mapping.evaluateHasAttribute(home, "color"));
    }

    @Test
    public void testWidgetEmptyStringAttribute() {
        WidgetStub home = createWidget(state, WdRoles.WdLI, "Home");
        home.set(WdTags.WebAlt, "");

        assertNotNull(mapping.evaluateHasAttribute(home, "alttext"));
        assertFalse(mapping.evaluateHasAttribute(home, "alttext"));
    }

    @Test
    public void testObjectWidgetHasAttribute() {
        WidgetStub home = createWidget(state, WdRoles.WdLI, "Home");
        home.set(WdTags.WebComputedColor, new java.awt.Color(0, 1, 2));

        assertNotNull(mapping.evaluateHasAttribute((Object)home, "text"));
        assertTrue(mapping.evaluateHasAttribute((Object)home, "text"));

        assertNotNull(mapping.evaluateHasAttribute((Object)home, "color"));
        assertTrue(mapping.evaluateHasAttribute((Object)home, "color"));
    }

    @Test
    public void testWidgetHasNotAttribute() {
        WidgetStub home = createWidget(state, WdRoles.WdLI, "Home");

        assertNotNull(mapping.evaluateHasAttribute(home, "color"));
        assertFalse(mapping.evaluateHasAttribute(home, "color"));
    }

    @Test
    public void testObjectWidgetHasNotAttribute() {
        WidgetStub home = createWidget(state, WdRoles.WdLI, "Home");

        assertNotNull(mapping.evaluateHasAttribute((Object)home, "color"));
        assertFalse(mapping.evaluateHasAttribute((Object)home, "color"));
    }

    @Test
    public void testNullWidgetHasNotAttribute() {
        assertNotNull(mapping.evaluateHasAttribute(new WidgetStub(), "text"));
        assertFalse(mapping.evaluateHasAttribute(new WidgetStub(), "text"));
    }

    @Test
    public void testNullObjectHasNotAttribute() {
        assertNotNull(mapping.evaluateHasAttribute(new Object(), "text"));
        assertFalse(mapping.evaluateHasAttribute(new Object(), "text"));
    }

    /** Children property and status tests **/

    @SuppressWarnings("unchecked")
    @Test
    public void testGetChildrenProperty() {
        WidgetStub menu = createWidget(state, WdRoles.WdUL, "Menu");
        WidgetStub home = createWidget(menu, WdRoles.WdLI, "Home");
        WidgetStub contact = createWidget(menu, WdRoles.WdLI, "Contact");

        Object result = mapping.getProperty(menu, "children");
        assertNotNull(result);
        assertTrue(result instanceof List);

        java.util.List<Object> children = ((java.util.List<Object>)mapping.getProperty(menu, "children"));
        assertEquals(2, children.size());

        assertTrue(children.contains(home));
        assertTrue(children.contains(contact));
    }

    @Test
    public void testTrueChildrenStatus() {
        WidgetStub menu = createWidget(state, WdRoles.WdUL, "Menu");
        WidgetStub home = createWidget(menu, WdRoles.WdLI, "Home");
        home.set(WdTags.WebIsEnabled, true);
        WidgetStub contact = createWidget(menu, WdRoles.WdLI, "Contact");
        contact.set(WdTags.WebIsEnabled, true);

        Object result = mapping.getProperty(menu, "children");
        assertNotNull(result);
        assertTrue(result instanceof List);

        @SuppressWarnings("unchecked")
        java.util.List<Object> children = ((java.util.List<Object>)mapping.getProperty(menu, "children"));
        assertEquals(2, children.size());

        assertTrue(mapping.evaluateAreStatus(children, "enabled"));
    }

    @Test
    public void testFalseChildrenStatus() {
        WidgetStub menu = createWidget(state, WdRoles.WdUL, "Menu");
        WidgetStub home = createWidget(menu, WdRoles.WdLI, "Home");
        home.set(WdTags.WebIsEnabled, true);
        WidgetStub contact = createWidget(menu, WdRoles.WdLI, "Contact");
        contact.set(WdTags.WebIsEnabled, false);

        Object result = mapping.getProperty(menu, "children");
        assertNotNull(result);
        assertTrue(result instanceof List);

        @SuppressWarnings("unchecked")
        java.util.List<Object> children = ((java.util.List<Object>)mapping.getProperty(menu, "children"));
        assertEquals(2, children.size());

        assertFalse(mapping.evaluateAreStatus(children, "enabled"));
    }

    @Test
    public void testFalseChildrenNullStatus() {
        WidgetStub menu = createWidget(state, WdRoles.WdUL, "Menu");
        createWidget(menu, WdRoles.WdLI, "Home");
        createWidget(menu, WdRoles.WdLI, "Contact");

        Object result = mapping.getProperty(menu, "children");
        assertNotNull(result);
        assertTrue(result instanceof List);

        @SuppressWarnings("unchecked")
        java.util.List<Object> children = ((java.util.List<Object>)mapping.getProperty(menu, "children"));
        assertEquals(2, children.size());

        assertFalse(mapping.evaluateAreStatus(children, "enabled"));
    }
}
