/***************************************************************************************************
 *
 * Copyright (c) 2019 - 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2019 - 2025 Open Universiteit - www.ou.nl
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/


package org.testar.monkey.alayer.webdriver.enums;

import java.util.HashMap;
import java.util.Map;

import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Tags;

import static org.testar.StateManagementTags.*;


public class WdMapping {

	// a mapping from the state management tags to webdriver tags
	private static Map<Tag<?>, Tag<?>> stateTagMappingWebdriver = new HashMap<Tag<?>, Tag<?>>()
	{
		{
			// Web
			put(WebWidgetId, WdTags.WebId);
			put(WebWidgetName ,WdTags.WebName);
			put(WebWidgetTagName, WdTags.WebTagName);
			put(WebWidgetTextContent, WdTags.WebTextContent);
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
