package org.testar.protocols;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.testar.monkey.Assert;
import org.testar.monkey.ConfigTags;
import org.testar.settings.Settings;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.windows.UIARoles;
import org.testar.stub.WidgetStub;

public class TestFilteredWidgets extends GenericUtilsProtocol {

	private static WidgetStub widget = new WidgetStub();

	@Before
	public void settings_setup() {
		settings = new Settings();

		// Fake that the widget can be hit
		widget.set(Tags.Shape, Rect.fromCoordinates(0, 0, 100, 100));
		widget.set(Tags.HitTester, Util.TrueTester);
	}

	@Test
	public void test_filtered_button_by_role() {
		widget.set(Tags.Role, UIARoles.UIAButton);

		String nonStringTags = "Title;Role";
		settings.set(ConfigTags.TagsToFilter, Arrays.asList(nonStringTags.split(";")));
		settings.set(ConfigTags.ClickFilter, ".*UIAButton.*");

		Assert.isTrue(!isUnfiltered(widget));
	}

	@Test
	public void test_unfiltered_button_by_role() {
		widget.set(Tags.Role, WdRoles.WdBUTTON);

		String nonStringTags = "Title;Role";
		settings.set(ConfigTags.TagsToFilter, Arrays.asList(nonStringTags.split(";")));
		settings.set(ConfigTags.ClickFilter, ".*UIAButton.*");

		Assert.isTrue(isUnfiltered(widget));
	}

	@Test
	public void test_unfiltered_inverse_regex() {
		widget.set(Tags.Title, "View");
		widget.set(Tags.Role, UIARoles.UIAMenuItem);

		String nonStringTags = "Title;Role";
		settings.set(ConfigTags.TagsToFilter, Arrays.asList(nonStringTags.split(";")));
		settings.set(ConfigTags.ClickFilter, "^(?!(.*View.*|.*Scientific.*|.*MenuItem.*)$).*$");

		Assert.isTrue(isUnfiltered(widget));
	}

	@Test
	public void test_filtered_inverse_regex() {
		widget.set(Tags.Title, "View");
		widget.set(Tags.Role, UIARoles.UIAButton);

		String nonStringTags = "Title;Role";
		settings.set(ConfigTags.TagsToFilter, Arrays.asList(nonStringTags.split(";")));
		settings.set(ConfigTags.ClickFilter, "^(?!(.*View.*|.*Scientific.*|.*MenuItem.*)$).*$");

		Assert.isTrue(!isUnfiltered(widget));
	}

}
