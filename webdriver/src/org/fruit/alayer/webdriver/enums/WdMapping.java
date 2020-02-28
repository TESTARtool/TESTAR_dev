package org.fruit.alayer.webdriver.enums;

import java.util.HashMap;
import java.util.Map;

import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;

import static es.upv.staq.testar.StateManagementTags.*;


public class WdMapping {

	// a mapping from the state management tags to webdriver tags
	private static Map<Tag<?>, Tag<?>> stateTagMappingWebdriver = new HashMap<Tag<?>, Tag<?>>()
	{
		{
			// Web
			put(WebWidgetId, WdTags.WebId);
			put(WebWidgetName ,WdTags.WebName);
			put(WebWidgetTagName, WdTags.WebTagName);
			put(WebWidgetTextContext, WdTags.WebTextContext);
			put(WebWidgetTitle, WdTags.WebTitle);
			put(WebWidgetHref, WdTags.WebHref);
			put(WebWidgetValue, WdTags.WebValue);
			put(WebWidgetStyle, WdTags.WebStyle);
			put(WebWidgetTarget, WdTags.WebTarget);
			put(WebWidgetAlt, WdTags.WebAlt);
			put(WebWidgetType, WdTags.WebType);
			put(WebWidgetCssClasses, WdTags.WebCssClasses);
			put(WebWidgetDisplay, WdTags.WebDisplay);

			// Generic
			put(WidgetControlType, WdTags.WebTagName);
			put(WidgetTitle, WdTags.WebTitle);
			put(WidgetIsEnabled, WdTags.WebIsEnabled);
			put(WidgetBoundary, WdTags.WebBoundary);
			put(WidgetIsOffscreen, WdTags.WebIsOffScreen);
			put(WidgetPath, Tags.Path);
			put(WidgetIsContentElement, WdTags.WebIsContentElement);
			put(WidgetIsControlElement, WdTags.WebIsControlElement);

		}
	};

	/**
	 * This method will return its equivalent, internal Wd tag, if available.
	 * @param mappedTag
	 * @return
	 */
	public static <T> Tag<T> getMappedStateTag(Tag<T> mappedTag) {
		return (Tag<T>) stateTagMappingWebdriver.getOrDefault(mappedTag, null);
	}
}
