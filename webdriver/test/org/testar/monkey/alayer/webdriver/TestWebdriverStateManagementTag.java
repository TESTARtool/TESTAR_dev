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
	public void test_WebdriverCodingIDs_mapping() {
		Tag<?>[] abstractTags = new Tag<?>[]{StateManagementTags.WebWidgetId, StateManagementTags.WebWidgetHref};
		CodingManager.setCustomTagsForAbstractId(abstractTags);

		WdState wdState = new WdState(null);
		WdWidget wdWidget = new WdWidget(wdState, wdState, null);

		// Build the first AbstractID and check the StateManagementTags uses the Wd values
		wdWidget.set(WdTags.WebId, "UniqueId");
		wdWidget.set(WdTags.WebHref, "Link.org");
		CodingManager.buildIDs(wdWidget);
		String originalAbstractId = wdWidget.get(Tags.AbstractID);
		Assert.assertTrue(originalAbstractId.contains("WA"));

		// Change WebId value to verify the AbstractID changes
		wdWidget.set(WdTags.WebId, "UniqueIdNEW");
		wdWidget.set(WdTags.WebHref, "Link.org");
		CodingManager.buildIDs(wdWidget);
		String webIdChangedAbstractId = wdWidget.get(Tags.AbstractID);
		Assert.assertTrue(webIdChangedAbstractId.contains("WA"));
		Assert.assertNotEquals(originalAbstractId, webIdChangedAbstractId);

		// Change WebHref value to verify the AbstractID changes
		wdWidget.set(WdTags.WebId, "UniqueIdNEW");
		wdWidget.set(WdTags.WebHref, "Link.com");
		CodingManager.buildIDs(wdWidget);
		String webHrefChangedAbstractId = wdWidget.get(Tags.AbstractID);
		Assert.assertTrue(webHrefChangedAbstractId.contains("WA"));
		Assert.assertNotEquals(originalAbstractId, webHrefChangedAbstractId);
		Assert.assertNotEquals(webIdChangedAbstractId, webHrefChangedAbstractId);
	}

	@Test
	public void test_WebId_mapping() {
		assertStringMapping(WdTags.WebId, StateManagementTags.WebWidgetId, "UniqueId");
	}

	@Test
	public void test_WebName_mapping() {
		assertStringMapping(WdTags.WebName, StateManagementTags.WebWidgetName, "search-input");
	}

	@Test
	public void test_WebTagName_mapping() {
		assertStringMapping(WdTags.WebTagName, StateManagementTags.WebWidgetTagName, "input");
	}

	@Test
	public void test_WebTextContent_mapping() {
		assertStringMapping(WdTags.WebTextContent, StateManagementTags.WebWidgetTextContent, "Text content");
	}

	@Test
	public void test_WebInnerText_mapping() {
		assertStringMapping(WdTags.WebInnerText, StateManagementTags.WebWidgetInnerText, "Visible text");
	}

	@Test
	public void test_WebTitle_mapping() {
		assertStringMapping(WdTags.WebTitle, StateManagementTags.WebWidgetTitle, "Widget title");
	}

	@Test
	public void test_WebHref_mapping() {
		assertStringMapping(WdTags.WebHref, StateManagementTags.WebWidgetHref, "Link.org");
	}

	@Test
	public void test_WebValue_mapping() {
		assertStringMapping(WdTags.WebValue, StateManagementTags.WebWidgetValue, "hello");
	}

	@Test
	public void test_WebStyle_mapping() {
		assertStringMapping(WdTags.WebStyle, StateManagementTags.WebWidgetStyle, "color:red;");
	}

	@Test
	public void test_WebTarget_mapping() {
		assertStringMapping(WdTags.WebTarget, StateManagementTags.WebWidgetTarget, "_blank");
	}

	@Test
	public void test_WebAlt_mapping() {
		assertStringMapping(WdTags.WebAlt, StateManagementTags.WebWidgetAlt, "banner-image");
	}

	@Test
	public void test_WebType_mapping() {
		assertStringMapping(WdTags.WebType, StateManagementTags.WebWidgetType, "text");
	}

	@Test
	public void test_WebCssClasses_mapping() {
		assertStringMapping(WdTags.WebCssClasses, StateManagementTags.WebWidgetCssClasses, "[btn, primary]");
	}

	@Test
	public void test_WebDisplay_mapping() {
		assertStringMapping(WdTags.WebDisplay, StateManagementTags.WebWidgetDisplay, "block");
	}

	@Test
	public void test_WebSrc_mapping() {
		assertStringMapping(WdTags.WebSrc, StateManagementTags.WebWidgetSrc, "https://testar.org/image.png");
	}

	@Test
	public void test_WebPlaceholder_mapping() {
		assertStringMapping(WdTags.WebPlaceholder, StateManagementTags.WebWidgetPlaceholder, "Type here");
	}

	@Test
	public void test_WebIsOffScreen_mapping() {
		assertBooleanMapping(WdTags.WebIsOffScreen, StateManagementTags.WebWidgetIsOffScreen, Boolean.TRUE);
	}

	@Test
	public void test_WebIsDisabled_mapping() {
		assertBooleanMapping(WdTags.WebIsDisabled, StateManagementTags.WebWidgetIsDisabled, Boolean.TRUE);
	}

	@Test
	public void test_WebAriaLabel_mapping() {
		assertStringMapping(WdTags.WebAriaLabel, StateManagementTags.WebWidgetAriaLabel, "aria-label");
	}

	@Test
	public void test_WebAriaLabelledBy_mapping() {
		assertStringMapping(WdTags.WebAriaLabelledBy, StateManagementTags.WebWidgetAriaLabelledBy, "aria labelled by");
	}

	@Test
	public void test_WidgetControlType_mapping() {
		assertStringMapping(WdTags.WebTagName, StateManagementTags.WidgetControlType, "button");
	}

	@Test
	public void test_WidgetTitle_mapping() {
		assertStringMapping(WdTags.WebGenericTitle, StateManagementTags.WidgetTitle, "Generic title");
	}

	@Test
	public void test_WidgetIsEnabled_mapping() {
		assertBooleanMapping(WdTags.WebIsEnabled, StateManagementTags.WidgetIsEnabled, Boolean.TRUE);
	}

	@Test
	public void test_WidgetPath_mapping() {
		assertStringMapping(Tags.Path, StateManagementTags.WidgetPath, "/html/body/div[1]");
	}

	@Test
	public void test_WidgetIsContentElement_mapping() {
		assertBooleanMapping(WdTags.WebIsContentElement, StateManagementTags.WidgetIsContentElement, Boolean.TRUE);
	}

	@Test
	public void test_WidgetIsControlElement_mapping() {
		assertBooleanMapping(WdTags.WebIsControlElement, StateManagementTags.WidgetIsControlElement, Boolean.TRUE);
	}

	private void assertStringMapping(Tag<String> wdTag, Tag<String> stateTag, String value) {
		WdState wdState = new WdState(null);
		wdState.set(wdTag, value);
		Assert.assertEquals(value, wdState.get(stateTag));
	}

	private void assertBooleanMapping(Tag<Boolean> wdTag, Tag<Boolean> stateTag, Boolean value) {
		WdState wdState = new WdState(null);
		wdState.set(wdTag, value);
		Assert.assertEquals(value, wdState.get(stateTag));
	}

}
