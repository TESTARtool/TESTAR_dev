/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.tag;

import java.util.HashMap;
import java.util.Map;

import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;

import static org.testar.core.StateManagementTags.WebWidgetAlt;
import static org.testar.core.StateManagementTags.WebWidgetAriaLabel;
import static org.testar.core.StateManagementTags.WebWidgetAriaLabelledBy;
import static org.testar.core.StateManagementTags.WebWidgetCssClasses;
import static org.testar.core.StateManagementTags.WebWidgetDisplay;
import static org.testar.core.StateManagementTags.WebWidgetHref;
import static org.testar.core.StateManagementTags.WebWidgetId;
import static org.testar.core.StateManagementTags.WebWidgetInnerText;
import static org.testar.core.StateManagementTags.WebWidgetIsDisabled;
import static org.testar.core.StateManagementTags.WebWidgetIsOffScreen;
import static org.testar.core.StateManagementTags.WebWidgetName;
import static org.testar.core.StateManagementTags.WebWidgetPlaceholder;
import static org.testar.core.StateManagementTags.WebWidgetSrc;
import static org.testar.core.StateManagementTags.WebWidgetStyle;
import static org.testar.core.StateManagementTags.WebWidgetTagName;
import static org.testar.core.StateManagementTags.WebWidgetTarget;
import static org.testar.core.StateManagementTags.WebWidgetTextContent;
import static org.testar.core.StateManagementTags.WebWidgetTitle;
import static org.testar.core.StateManagementTags.WebWidgetType;
import static org.testar.core.StateManagementTags.WebWidgetValue;
import static org.testar.core.StateManagementTags.WidgetBoundary;
import static org.testar.core.StateManagementTags.WidgetControlType;
import static org.testar.core.StateManagementTags.WidgetIsContentElement;
import static org.testar.core.StateManagementTags.WidgetIsControlElement;
import static org.testar.core.StateManagementTags.WidgetIsEnabled;
import static org.testar.core.StateManagementTags.WidgetPath;
import static org.testar.core.StateManagementTags.WidgetTitle;


public class WdMapping {

    // a mapping from the state management tags to webdriver tags
    private static Map<Tag<?>, Tag<?>> stateTagMappingWebdriver = new HashMap<Tag<?>, Tag<?>>() {
        {
            // Web
            put(WebWidgetId, WdTags.WebId);
            put(WebWidgetName ,WdTags.WebName);
            put(WebWidgetTagName, WdTags.WebTagName);
            put(WebWidgetTextContent, WdTags.WebTextContent);
            put(WebWidgetInnerText, WdTags.WebInnerText);
            put(WebWidgetTitle, WdTags.WebTitle);
            put(WebWidgetHref, WdTags.WebHref);
            put(WebWidgetValue, WdTags.WebValue);
            put(WebWidgetStyle, WdTags.WebStyle);
            put(WebWidgetTarget, WdTags.WebTarget);
            put(WebWidgetAlt, WdTags.WebAlt);
            put(WebWidgetType, WdTags.WebType);
            put(WebWidgetCssClasses, WdTags.WebCssClasses);
            put(WebWidgetDisplay, WdTags.WebDisplay);
            put(WebWidgetIsOffScreen, WdTags.WebIsOffScreen);
            put(WebWidgetSrc, WdTags.WebSrc);
            put(WebWidgetPlaceholder, WdTags.WebPlaceholder);
            put(WebWidgetIsDisabled, WdTags.WebIsDisabled);
            put(WebWidgetAriaLabel, WdTags.WebAriaLabel);
            put(WebWidgetAriaLabelledBy, WdTags.WebAriaLabelledBy);

            // Generic
            put(WidgetControlType, WdTags.WebTagName);
            put(WidgetTitle, WdTags.WebGenericTitle);
            put(WidgetIsEnabled, WdTags.WebIsEnabled);
            put(WidgetBoundary, WdTags.WebBoundingRectangle);
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
    @SuppressWarnings("unchecked")
    public static <T> Tag<T> getMappedStateTag(Tag<T> mappedTag) {
        return (Tag<T>) stateTagMappingWebdriver.getOrDefault(mappedTag, null);
    }
}
