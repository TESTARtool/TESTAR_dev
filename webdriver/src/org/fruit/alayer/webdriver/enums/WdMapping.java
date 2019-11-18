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
            put(WidgetControlType, WdTags.WebTagName);
            put(WidgetIsEnabled, WdTags.WebIsEnabled);
            put(WidgetTitle, WdTags.WebName);
            put(WidgetHelpText, WdTags.WebHelpText);
            put(WidgetClassName, WdTags.WebCssClasses);
            put(WidgetBoundary, WdTags.WebBoundary);
            put(WidgetValueValue, WdTags.WebValue);
            put(WidgetAriaProperties, WdTags.WebHref);
            put(WidgetIsOffscreen, WdTags.WebIsOffScreen);
            put(WidgetIsContentElement, WdTags.WebIsContentElement);
            put(WidgetIsControlElement, WdTags.WebIsControlElement);
            put(WidgetAutomationId, WdTags.WebId);
            put(WidgetItemType, WdTags.WebType);
            put(WidgetPath, Tags.Path);
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
