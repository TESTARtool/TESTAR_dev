package org.testar.statemodel;

import org.junit.Before;
import org.junit.Test;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Shape;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.actions.PasteText;
import org.testar.stub.WidgetStub;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class ModelWidgetTest {

    private ModelWidget modelWidget;

    @Before
    public void setUp() {
        modelWidget = new ModelWidget("widget-1");
    }

    @Test
    public void testModelWidgetConstructor() {
        assertEquals("widget-1", modelWidget.getId());
        assertNotNull(modelWidget.getAttributes());
        assertNotNull(modelWidget.getChildren());
        assertTrue(modelWidget.getChildren().isEmpty());
    }

    @Test
    public void testAddStringAttribute() {
        Tag<String> stringTag = Tags.Title;
        modelWidget.addAttribute(stringTag, "value-123");
        assertEquals("value-123", modelWidget.getAttributes().get(stringTag));
    }

    @Test
    public void testAddBooleanAttribute() {
        Tag<Boolean> booleanTag = Tags.Enabled;
        modelWidget.addAttribute(booleanTag, false);
        assertEquals(false, modelWidget.getAttributes().get(booleanTag));
    }

    @Test
    public void testAddShapeAttribute() {
        Tag<Shape> shapeTag = Tags.Shape;
        Rect rect = Rect.from(1, 1, 10, 10);
        modelWidget.addAttribute(shapeTag, rect);
        assertEquals(rect, modelWidget.getAttributes().get(shapeTag));
    }

    @Test
    public void testAddWidgetAttribute() {
        Tag<Widget> widgetTag = Tags.OriginWidget;
        Widget widget = new WidgetStub();
        modelWidget.addAttribute(widgetTag, widget);
        assertEquals(widget, modelWidget.getAttributes().get(widgetTag));
    }

    @Test
    public void testAddActionAttribute() {
        Tag<Action> actionTag = Tags.ExecutedAction;
        Action action = new PasteText("inputText");
        modelWidget.addAttribute(actionTag, action);
        assertEquals(action, modelWidget.getAttributes().get(actionTag));
    }

    @Test
    public void testAddChildAndParentSet() {
        ModelWidget child = new ModelWidget("child-1");
        modelWidget.addChild(child);

        List<ModelWidget> children = modelWidget.getChildren();
        assertEquals(1, children.size());
        assertEquals(child, children.get(0));
        assertEquals(modelWidget, child.getParent());
    }

    @Test
    public void testSetAndGetParent() {
        ModelWidget parent = new ModelWidget("parent-1");
        modelWidget.setParent(parent);
        assertEquals(parent, modelWidget.getParent());
    }

    @Test
    public void testSetAndGetRootWidget() {
        AbstractState abstractState = new AbstractState("abstract-root", Collections.emptySet());
        ConcreteState root = new ConcreteState("root-1", abstractState);

        modelWidget.setRootWidget(root);
        assertEquals(root, modelWidget.getRootWidget());
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullId() {
        modelWidget = new ModelWidget(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyId() {
        modelWidget = new ModelWidget("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithBlankId() {
        modelWidget = new ModelWidget("    ");
    }

    @Test(expected = NullPointerException.class)
    public void testAddNullChild() {
        modelWidget.addChild(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testChildrenListIsUnmodifiable() {
        modelWidget.getChildren().add(new ModelWidget("illegal-child"));
    }

}
