package org.testar.monkey.alayer.webdriver;

import org.junit.Assert;
import org.junit.Test;
import org.testar.CodingManager;
import org.testar.StateManagementTags;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.webdriver.enums.WdTags;

public class TestWebdriverStateManagementTag {

	@Test
	public void testWebdriverMapping() {
		WdState wdState = new WdState(null);
		wdState.set(WdTags.WebId, "UniqueId");
		wdState.set(WdTags.WebHref, "Link.org");

		Assert.assertEquals(wdState.get(StateManagementTags.WebWidgetId), "UniqueId");
		Assert.assertEquals(wdState.get(StateManagementTags.WebWidgetHref), "Link.org");
	}

	@Test
	public void testWebdriverCodingIDs() {
		Tag<?>[] abstractTags = new Tag<?>[]{StateManagementTags.WebWidgetId, StateManagementTags.WebWidgetHref};
		CodingManager.setCustomTagsForAbstractId(abstractTags);

		WdState wdState = new WdState(null);
		WdWidget wdWidget = new WdWidget(wdState, wdState, null);
		wdWidget.set(WdTags.WebId, "UniqueId");
		wdWidget.set(WdTags.WebHref, "Link.org");

		// Build the first AbstractID and check the StateManagementTags uses the Wd values
		CodingManager.buildIDs(wdWidget);
		Assert.assertEquals(wdWidget.get(Tags.AbstractID), "WA1r9aad8101406797296");

		// Change WebId value to verify the AbstractID changes
		wdWidget.set(WdTags.WebId, "UniqueIdNEW");
		wdWidget.set(WdTags.WebHref, "Link.org");
		CodingManager.buildIDs(wdWidget);
		Assert.assertEquals(wdWidget.get(Tags.AbstractID), "WA1vaf6dg131408350915");

		// Change WebHref value to verify the AbstractID changes
		wdWidget.set(WdTags.WebId, "UniqueIdNEW");
		wdWidget.set(WdTags.WebHref, "Link.com");
		CodingManager.buildIDs(wdWidget);
		Assert.assertEquals(wdWidget.get(Tags.AbstractID), "WA1gxbnlz131612186923");
	}

}
