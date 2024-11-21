package org.testar.monkey.alayer.webdriver;

import org.junit.Assert;
import org.junit.Test;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.webdriver.enums.WdTags;

public class TestWebdriverWidget {

	@Test
	public void testEmptyWebdriverWidgetToString() {
		WdState wdState = new WdState(null);
		WdWidget wdWidget = new WdWidget(wdState, wdState, null);

		Assert.assertEquals(wdWidget.toString(WdTags.WebStyle), "");
	}

	@Test
	public void testWebdriverWidgetToString() {
		WdState wdState = new WdState(null);
		WdWidget wdWidget = new WdWidget(wdState, wdState, null);

		wdWidget.set(WdTags.WebStyle, "webStyle");
		Assert.assertEquals(wdWidget.toString(WdTags.WebStyle), "webStyle");

		wdWidget.set(WdTags.WebControlType, (long)2);
		Assert.assertEquals(wdWidget.toString(WdTags.WebControlType), "2");

		wdWidget.set(WdTags.WebIsBlocked, false);
		Assert.assertEquals(wdWidget.toString(WdTags.WebIsBlocked), "false");

		wdWidget.set(WdTags.WebBoundingRectangle, Rect.fromCoordinates(0, 2, 12, 12));
		Assert.assertEquals(wdWidget.toString(WdTags.WebBoundingRectangle), "Rect [x:0.0 y:2.0 w:12.0 h:10.0]");

		Assert.assertEquals(wdWidget.toString(
				WdTags.WebStyle,
				WdTags.WebControlType,
				WdTags.WebIsBlocked,
				WdTags.WebBoundingRectangle), "webStyle,2,false,Rect [x:0.0 y:2.0 w:12.0 h:10.0]");
	}

}
