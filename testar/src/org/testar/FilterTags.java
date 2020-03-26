/***************************************************************************************************
 *
 * Copyright (c) 2020 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 Open Universiteit - www.ou.nl
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

package org.testar;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.webdriver.enums.WdTags;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

public class FilterTags {

	private FilterTags() {}

	private static Map<Tag<String>, Tag<String>> generalFilterTags = new HashMap<>();

	private static Map<Tag<String>, Tag<String>> webdriverFilterTags = new HashMap<>();

	/**
	 * Map the ConfigTags (key) that contains the regex pattern
	 * with the Tag (value) of the widget in which we have to check
	 */
	static {
		generalFilterTags.put(ConfigTags.ActionFilterWidgetTitle, Tags.Title);
		generalFilterTags.put(ConfigTags.ActionFilterWidgetDesc, Tags.Desc);
		generalFilterTags.put(ConfigTags.ActionFilterWidgetText, Tags.Text);
		generalFilterTags.put(ConfigTags.ActionFilterWidgetValuePattern, Tags.ValuePattern);

		webdriverFilterTags.put(ConfigTags.ActionFilterWebId, WdTags.WebId);
		webdriverFilterTags.put(ConfigTags.ActionFilterWebHref, WdTags.WebHref);
		webdriverFilterTags.put(ConfigTags.ActionFilterWebName, WdTags.WebName);
		webdriverFilterTags.put(ConfigTags.ActionFilterWebTagName, WdTags.WebTagName);
		webdriverFilterTags.put(ConfigTags.ActionFilterWebTitle, WdTags.WebTitle);
		webdriverFilterTags.put(ConfigTags.ActionFilterWebAlt, WdTags.WebAlt);
		webdriverFilterTags.put(ConfigTags.ActionFilterWebTextContent, WdTags.WebTextContent);
		webdriverFilterTags.put(ConfigTags.ActionFilterWebSrc, WdTags.WebSrc);
	}

	/**
	 * Match the defined pattern regex of the settings with the General widget Tags values
	 * to check if the widget is unfiltered
	 * @param widget
	 * @param settings
	 * @return
	 */
	public static boolean checkGeneralTagIsUnfiltered(Widget w, Settings settings) {

		for(Map.Entry<Tag<String>, Tag<String>> entry : generalFilterTags.entrySet()) {
			
			Pattern generalActionFilterPattern = null;
			Map<String, Matcher> generalActionFilterMatchers = new WeakHashMap<>();
			Matcher m;

			//Only if the widget has the tag with content, if empty check next
			if(!w.get(entry.getValue(),"").isEmpty()) {

				//Obtain the associated pattern of this Tag
				String regex = settings.get(entry.getKey());
				generalActionFilterPattern = Pattern.compile(regex, Pattern.UNICODE_CHARACTER_CLASS);
				m = generalActionFilterMatchers.get(w.get(entry.getValue()));

				m = generalActionFilterPattern.matcher(w.get(entry.getValue()));
				generalActionFilterMatchers.put(w.get(entry.getValue()), m);

				// If matches is not unfiltered, is filtered
				if(m.matches()) {
					return false;
				}

			}
		}

		return true;
	}

	/**
	 * Match the defined pattern regex of the settings with the Webdriver widget WdTags values
	 * to check if the widget is unfiltered
	 * @param widget
	 * @param settings
	 * @return
	 */
	public static boolean checkWebdriverTagIsUnfiltered(Widget w, Settings settings) {

		for(Map.Entry<Tag<String>, Tag<String>> entry : webdriverFilterTags.entrySet()) {

			Pattern webdriverActionFilterPattern = null;
			Map<String, Matcher> webdriverActionFilterMatchers = new WeakHashMap<>();
			Matcher m;
			
			//Only if the widget has the tag with content, if empty check next
			if(!w.get(entry.getValue(),"").isEmpty()) {

				//Obtain the associated pattern of this Tag
				String regex = settings.get(entry.getKey());
				webdriverActionFilterPattern = Pattern.compile(regex, Pattern.UNICODE_CHARACTER_CLASS);
				m = webdriverActionFilterMatchers.get(w.get(entry.getValue()));

				m = webdriverActionFilterPattern.matcher(w.get(entry.getValue()));
				webdriverActionFilterMatchers.put(w.get(entry.getValue()), m);

				// If matches is not unfiltered, is filtered
				if(m.matches()) {
					return false;
				}
			}
		}

		return true;
	}
}
