package org.testar.monkey.alayer.windows;

import org.junit.Assert;
import org.junit.Test;
import org.testar.CodingManager;
import org.testar.StateManagementTags;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Tags;

public class TestWindowsStateManagementTag {

	@Test
	public void testWindowsMapping() {
		UIAState uiaState = new UIAState(null);
		uiaState.set(UIATags.UIAName, "UIAName");
		uiaState.set(UIATags.UIAControlType, 123L);

		Assert.assertEquals(uiaState.get(StateManagementTags.WidgetTitle), "UIAName");
		Assert.assertEquals(uiaState.get(StateManagementTags.WidgetControlType), 123L);
	}

	@Test
	public void testWindowsCodingIDs() {
		Tag<?>[] abstractTags = new Tag<?>[]{StateManagementTags.WidgetTitle, StateManagementTags.WidgetControlType};
		CodingManager.setCustomTagsForAbstractId(abstractTags);

		UIAState uiaState = new UIAState(null);
		UIAWidget uiaWidget = new UIAWidget(uiaState, uiaState, null);
		uiaWidget.set(UIATags.UIAName, "CustomName");
		uiaWidget.set(UIATags.UIAControlType, 123L);

		// Build the first AbstractID and check the StateManagementTags uses the UIA values
		CodingManager.buildIDs(uiaWidget);
		Assert.assertEquals(uiaWidget.get(Tags.AbstractID), "WA1sw1qpad773173131");

		// Change UIAName value to verify the AbstractID changes
		uiaWidget.set(UIATags.UIAName, "CustomNameNEW");
		uiaWidget.set(UIATags.UIAControlType, 123L);
		CodingManager.buildIDs(uiaWidget);
		Assert.assertEquals(uiaWidget.get(Tags.AbstractID), "WA12t05le101416017485");

		// Change UIAControlType value to verify the AbstractID changes
		uiaWidget.set(UIATags.UIAName, "CustomNameNEW");
		uiaWidget.set(UIATags.UIAControlType, 122L);
		CodingManager.buildIDs(uiaWidget);
		Assert.assertEquals(uiaWidget.get(Tags.AbstractID), "WA1b6014j103379124027");
	}

}
