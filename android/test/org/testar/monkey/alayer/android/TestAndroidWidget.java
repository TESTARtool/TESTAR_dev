package org.testar.monkey.alayer.android;

import org.junit.Assert;
import org.junit.Test;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.android.enums.AndroidTags;

public class TestAndroidWidget {

	@Test
	public void testEmptyAndroidWidgetToString() {
		AndroidState androidState = new AndroidState(null);
		AndroidWidget androidWidget = new AndroidWidget(androidState, androidState, null);

		Assert.assertEquals(androidWidget.toString(AndroidTags.AndroidText), "");
	}

	@Test
	public void testAndroidWidgetToString() {
		AndroidState androidState = new AndroidState(null);
		AndroidWidget androidWidget = new AndroidWidget(androidState, androidState, null);

		androidWidget.set(AndroidTags.AndroidText, "mobileText");
		Assert.assertEquals(androidWidget.toString(AndroidTags.AndroidText), "mobileText");

		androidWidget.set(AndroidTags.AndroidEnabled, false);
		Assert.assertEquals(androidWidget.toString(AndroidTags.AndroidEnabled), "false");

		androidWidget.set(AndroidTags.AndroidNodeIndex, 123);
		Assert.assertEquals(androidWidget.toString(AndroidTags.AndroidNodeIndex), "123");

		androidWidget.set(AndroidTags.AndroidBounds, Rect.fromCoordinates(0, 0, 100, 100));
		Assert.assertEquals(androidWidget.toString(AndroidTags.AndroidBounds), "Rect [x:0.0 y:0.0 w:100.0 h:100.0]");

		Assert.assertEquals(androidWidget.toString(
				AndroidTags.AndroidText,
				AndroidTags.AndroidEnabled,
				AndroidTags.AndroidNodeIndex,
				AndroidTags.AndroidBounds), "mobileText,false,123,Rect [x:0.0 y:0.0 w:100.0 h:100.0]");
	}

}
