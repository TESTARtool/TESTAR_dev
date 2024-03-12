package org.testar.monkey.alayer.windows;

import org.junit.Assert;
import org.junit.Test;
import org.testar.monkey.alayer.Rect;

public class TestUIAWidget {

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

}
