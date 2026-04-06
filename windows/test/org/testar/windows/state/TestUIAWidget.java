package org.testar.windows.state;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.alayer.Rect;
import org.testar.core.tag.Tags;
import org.testar.windows.alayer.UIARoles;
import org.testar.windows.tag.UIATags;

public class TestUIAWidget {

    @Test
    public void testUIAWidgetDescWithoutElement() {
        UIAWidget uiaWidget = createWidget(null);

        Assert.assertEquals("", uiaWidget.getUIAWidgetDescription());
    }

    @Test
    public void testUIAWidgetDescForClickable() {
        UIAElement uiaElement = new UIAElement();
        uiaElement.name = "Open";
        uiaElement.valuePattern = "ignored";

        UIAWidget uiaWidget = createWidget(uiaElement);
        uiaWidget.set(Tags.Role, UIARoles.UIAButton);

        Assert.assertEquals("Open", uiaWidget.getUIAWidgetDescription());
    }

    @Test
    public void testUIAWidgetDescForClickableNullName() {
        UIAElement uiaElement = new UIAElement();
        uiaElement.name = null;
        uiaElement.valuePattern = "ignored";

        UIAWidget uiaWidget = createWidget(uiaElement);
        uiaWidget.set(Tags.Role, UIARoles.UIAButton);

        Assert.assertEquals("", uiaWidget.getUIAWidgetDescription());
    }

    @Test
    public void testUIAWidgetDescForTypeable() {
        UIAElement uiaElement = new UIAElement();
        uiaElement.name = "Username";
        uiaElement.valuePattern = "john";

        UIAWidget uiaWidget = createTypeableWidget(uiaElement);

        Assert.assertEquals("Username: john", uiaWidget.getUIAWidgetDescription());
    }

    @Test
    public void testUIAWidgetDescForTypeableNullName() {
        UIAElement uiaElement = new UIAElement();
        uiaElement.name = null;
        uiaElement.valuePattern = "john";

        UIAWidget uiaWidget = createTypeableWidget(uiaElement);

        Assert.assertEquals("john", uiaWidget.getUIAWidgetDescription());
    }

    @Test
    public void testUIAWidgetDescForTypeableNullPattern() {
        UIAElement uiaElement = new UIAElement();
        uiaElement.name = "Username";
        uiaElement.valuePattern = null;

        UIAWidget uiaWidget = createTypeableWidget(uiaElement);

        Assert.assertEquals("Username", uiaWidget.getUIAWidgetDescription());
    }

	@Test
	public void testEmptyUIAWidgetToString() {
		UIAState uiaState = new UIAState(null);
		UIAWidget uiaWidget = new UIAWidget(uiaState, uiaState, null);

		Assert.assertEquals(uiaWidget.toString(UIATags.UIAAutomationId), "");
	}

	@Test
	public void testUIAWidgetToString() {
		UIAState uiaState = new UIAState(null);
		UIAWidget uiaWidget = new UIAWidget(uiaState, uiaState, null);

		uiaWidget.set(UIATags.UIAAutomationId, "uiaAutomationId");
		Assert.assertEquals(uiaWidget.toString(UIATags.UIAAutomationId), "uiaAutomationId");

		uiaWidget.set(UIATags.UIAProcessId, (long)-1);
		Assert.assertEquals(uiaWidget.toString(UIATags.UIAProcessId), "-1");

		uiaWidget.set(UIATags.UIAIsControlElement, true);
		Assert.assertEquals(uiaWidget.toString(UIATags.UIAIsControlElement), "true");

		uiaWidget.set(UIATags.UIABoundingRectangle, Rect.fromCoordinates(0, 2, 10, 10));
		Assert.assertEquals(uiaWidget.toString(UIATags.UIABoundingRectangle), "Rect [x:0.0 y:2.0 w:10.0 h:8.0]");

		Assert.assertEquals(uiaWidget.toString(
				UIATags.UIAAutomationId,
				UIATags.UIAProcessId,
				UIATags.UIAIsControlElement,
				UIATags.UIABoundingRectangle), "uiaAutomationId,-1,true,Rect [x:0.0 y:2.0 w:10.0 h:8.0]");
	}

    private UIAWidget createWidget(UIAElement uiaElement) {
        UIAState uiaState = new UIAState(null);
        return new UIAWidget(uiaState, uiaState, uiaElement);
    }

    private UIAWidget createTypeableWidget(UIAElement uiaElement) {
        UIAWidget uiaWidget = createWidget(uiaElement);
        uiaWidget.set(Tags.Role, UIARoles.UIAEdit);
        uiaWidget.set(UIATags.UIAIsKeyboardFocusable, true);
        uiaWidget.set(UIATags.UIAIsValuePatternAvailable, true);
        uiaWidget.set(UIATags.UIAValueIsReadOnly, false);
        return uiaWidget;
    }

}
