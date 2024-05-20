package org.testar.monkey.alayer.android;

import org.junit.Assert;
import org.junit.Test;
import org.testar.CodingManager;
import org.testar.StateManagementTags;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.android.enums.AndroidTags;

public class TestAndroidStateManagementTag {

	@Test
	public void testAndroidMapping() {
		AndroidState androidState = new AndroidState(null);
		androidState.set(AndroidTags.AndroidText, "mobileText");
		androidState.set(AndroidTags.AndroidAccessibilityId, "mobileAccessibilityId");

		Assert.assertEquals(androidState.get(StateManagementTags.WidgetTitle), "mobileText");
		Assert.assertEquals(androidState.get(StateManagementTags.WidgetAutomationId), "mobileAccessibilityId");
	}

	@Test
	public void testAndroidCodingIDs() {
		Tag<?>[] abstractTags = new Tag<?>[]{StateManagementTags.WidgetTitle, StateManagementTags.WidgetAutomationId};
		CodingManager.setCustomTagsForAbstractId(abstractTags);

		AndroidState androidState = new AndroidState(null);
		AndroidWidget androidWidget = new AndroidWidget(androidState, androidState, null);
		androidWidget.set(AndroidTags.AndroidText, "mobileText");
		androidWidget.set(AndroidTags.AndroidAccessibilityId, "mobileAccessibilityId");

		// Build the first AbstractID and check the StateManagementTags uses the Android values
		CodingManager.buildIDs(androidWidget);
		Assert.assertEquals(androidWidget.get(Tags.AbstractID), "WA1nhk37a1f1534562034");

		// Change AndroidText value to verify the AbstractID changes
		androidWidget.set(AndroidTags.AndroidText, "mobileTextNEW");
		androidWidget.set(AndroidTags.AndroidAccessibilityId, "mobileAccessibilityId");
		CodingManager.buildIDs(androidWidget);
		Assert.assertEquals(androidWidget.get(Tags.AbstractID), "WA1co2l5622543551600");

		// Change AndroidAccessibilityId value to verify the AbstractID changes
		androidWidget.set(AndroidTags.AndroidText, "mobileTextNEW");
		androidWidget.set(AndroidTags.AndroidAccessibilityId, "mobileAccessibility");
		CodingManager.buildIDs(androidWidget);
		Assert.assertEquals(androidWidget.get(Tags.AbstractID), "WA1tvgaud203868755727");
	}

}
