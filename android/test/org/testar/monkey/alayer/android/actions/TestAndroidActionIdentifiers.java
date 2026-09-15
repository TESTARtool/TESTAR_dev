package org.testar.monkey.alayer.android.actions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testar.CodingManager;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

import java.util.LinkedHashSet;
import java.util.Set;

public class TestAndroidActionIdentifiers {

    private StateStub state;
    private WidgetStub widgetOne;
    private WidgetStub widgetTwo;

    @Before
    public void setUp() {
        state = new StateStub();
        state.set(AndroidTags.AndroidXpath, "[0]");

        widgetOne = buildWidget("[0,0,1]", "TextValueOne", "AccessibilityIdOne", "ClassNameOne", 0, 0, 100, 100);
        widgetTwo = buildWidget("[0,0,2]", "TextValueTwo", "AccessibilityIdTwo", "ClassNameTwo", 100, 0, 200, 100);

        state.addChild(widgetOne);
        widgetOne.setParent(state);

        state.addChild(widgetTwo);
        widgetTwo.setParent(state);

        CodingManager.buildIDs(state);
    }

    @Test
    public void clickAction_toStringIsNotEmpty() {
        Action action = new AndroidActionClick(state, widgetOne);

        Assert.assertFalse(action.toString(new Role[0]).isEmpty());
    }

    @Test
    public void longClickAction_toStringIsNotEmpty() {
        Action action = new AndroidActionLongClick(state, widgetOne);

        Assert.assertFalse(action.toString(new Role[0]).isEmpty());
    }

    @Test
    public void typeAction_toStringIsNotEmpty() {
        Action action = new AndroidActionType(state, widgetOne, "InputText");

        Assert.assertFalse(action.toString(new Role[0]).isEmpty());
    }

    @Test
    public void scrollAction_toStringIsNotEmpty() {
        Action action = new AndroidActionScroll(state, widgetOne);

        Assert.assertFalse(action.toString(new Role[0]).isEmpty());
    }

    @Test
    public void backAction_toStringIsNotEmpty() {
        Action action = new AndroidBackAction(state);

        Assert.assertFalse(action.toString(new Role[0]).isEmpty());
    }

    @Test
    public void systemCallAction_toStringIsNotEmpty() {
        Action action = new AndroidSystemActionCall(state);

        Assert.assertFalse(action.toString(new Role[0]).isEmpty());
    }

    @Test
    public void systemOrientationAction_toStringIsNotEmpty() {
        Action action = new AndroidSystemActionOrientation(state);

        Assert.assertFalse(action.toString(new Role[0]).isEmpty());
    }

    @Test
    public void systemTextAction_toStringIsNotEmpty() {
        Action action = new AndroidSystemActionText(state);

        Assert.assertFalse(action.toString(new Role[0]).isEmpty());
    }

    @Test
    public void clickActions_haveDifferentConcreteIdsForDifferentWidgets() {
        Action actionOne = new AndroidActionClick(state, widgetOne);
        Action actionTwo = new AndroidActionClick(state, widgetTwo);

        buildActionIds(actionOne, actionTwo);

        Assert.assertNotEquals(
                actionOne.get(Tags.ConcreteID, ""),
                actionTwo.get(Tags.ConcreteID, "")
        );
    }

    @Test
    public void longClickActions_haveDifferentConcreteIdsForDifferentWidgets() {
        Action actionOne = new AndroidActionLongClick(state, widgetOne);
        Action actionTwo = new AndroidActionLongClick(state, widgetTwo);

        buildActionIds(actionOne, actionTwo);

        Assert.assertNotEquals(
                actionOne.get(Tags.ConcreteID, ""),
                actionTwo.get(Tags.ConcreteID, "")
        );
    }

    @Test
    public void typeActions_haveDifferentConcreteIdsForDifferentWidgets() {
        Action actionOne = new AndroidActionType(state, widgetOne, "InputOne");
        Action actionTwo = new AndroidActionType(state, widgetTwo, "InputTwo");

        buildActionIds(actionOne, actionTwo);

        Assert.assertNotEquals(
                actionOne.get(Tags.ConcreteID, ""),
                actionTwo.get(Tags.ConcreteID, "")
        );
    }

    @Test
    public void scrollActions_haveDifferentConcreteIdsForDifferentWidgets() {
        Action actionOne = new AndroidActionScroll(state, widgetOne);
        Action actionTwo = new AndroidActionScroll(state, widgetTwo);

        buildActionIds(actionOne, actionTwo);

        Assert.assertNotEquals(
                actionOne.get(Tags.ConcreteID, ""),
                actionTwo.get(Tags.ConcreteID, "")
        );
    }

    private void buildActionIds(Action actionOne, Action actionTwo) {
        Set<Action> actions = new LinkedHashSet<>();
        actions.add(actionOne);
        actions.add(actionTwo);
        CodingManager.buildIDs(state, actions);
    }

    private WidgetStub buildWidget(
            String xpath,
            String text,
            String accessibilityId,
            String className,
            double x1,
            double y1,
            double x2,
            double y2
    ) {
        WidgetStub widget = new WidgetStub();
        widget.set(Tags.Shape, Rect.fromCoordinates(x1, y1, x2, y2));
        widget.set(AndroidTags.AndroidText, text);
        widget.set(AndroidTags.AndroidAccessibilityId, accessibilityId);
        widget.set(AndroidTags.AndroidClassName, className);
        widget.set(AndroidTags.AndroidXpath, xpath);
        return widget;
    }
}
