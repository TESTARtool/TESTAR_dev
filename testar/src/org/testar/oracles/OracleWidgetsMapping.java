/***************************************************************************************************
 *
 * Copyright (c) 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2025 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.oracles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;

public interface OracleWidgetsMapping {

	default List<Widget> getWidgets(String elementType, State state) {
		List<Widget> lst = new ArrayList<>();
		Role elementRole = element2Role.get(elementType);
		for (Widget w : state) {
			Role widgetRole = w.get(Tags.Role, Roles.Invalid);
			if (elementRole.equals(widgetRole)) {
				lst.add(w);
			}
		}
		return lst;
	}

	default Widget getWidget(String elementType, String selector, State state) {
		List<Tag<?>> tagPriority = selectorString2Tags.get(elementType);

		for (Widget w : getWidgets(elementType, state)) {
			for (Tag<?> tag : tagPriority) {
				Object tagValue = w.get(tag, null);
				if (tagValue instanceof String && selector.equals(tagValue)) {
					return w;
				}
			}
		}
		return null;
	}

	default Boolean evaluateIsStatus(Widget w, String status) {
		List<Tag<?>> tagPriority = statusTags.get(status);

		for (Tag<?> tag : tagPriority) {
			Object value = w.get(tag, null);

			// Special cases first
			if (status.equals("empty")) {
				if (value instanceof String && ((String) value).isEmpty()) {
					return true;
				}
			} else if (status.equals("filled")) {
				if (value instanceof String && !((String) value).isEmpty()) {
					return true;
				}
			} else if (status.equals("readonly")) {
				if (value instanceof Boolean && !((Boolean) value)) {
					return true; // readonly = not focusable
				}
			} else {
				// The rest is gewoon boolean case (e.g. visible, enabled, selected...)
				if (value instanceof Boolean) {
					return (Boolean) value;
				}
			}
		}

		// None of the tags confirmed the status
		return null;
	}

	//syntax Condition
	//  = "has nonempty" Name attr
	//where attr can be one of "label", "alttext", "role", "placeholder", "tooltip"
	//found possible with the mapping in attributeTags

	default boolean evaluateHasAttribute(Widget w, String attr) {
		List<Tag<?>> tagPriority = attributeTags.get(attr);

		for (Tag<?> tag : tagPriority) {
			Object value = w.get(tag, null);

			if (value instanceof String && !((String) value).isEmpty()) {
				return true; //it has the attribute and it is not empty
			}
		}

		// Attribute was not found
		return false; 
	}

	Map<String, Set<String>> validStatusPerElement = Map.ofEntries(
			Map.entry("button", Set.of("visible", "enabled", "focused", "clickable")),
			Map.entry("input_text", Set.of("visible", "enabled", "empty", "filled", "focused", "readonly")),
			Map.entry("checkbox", Set.of("visible", "enabled", "checked", "selected")),
			Map.entry("radio", Set.of("visible", "enabled", "checked", "selected")),
			Map.entry("dropdown", Set.of("visible", "enabled", "empty", "filled", "selected")),
			Map.entry("label", Set.of("visible")),
			Map.entry("image", Set.of("visible", "offscreen", "onscreen")),
			Map.entry("link", Set.of("visible", "clickable")),
			Map.entry("alert", Set.of("visible")),
			Map.entry("element", Set.of("visible", "enabled", "focused", "offscreen", "onscreen"))
			);

	Map<String, Role> element2Role = Map.of(
			"button", WdRoles.WdBUTTON,
			"input_text", Roles.Text,
			"static_text", WdRoles.WdLABEL,
			//"alert", Roles.Alert,
			"dropdown", WdRoles.WdSELECT,
			"checkbox", WdRoles.WdINPUT,
			"radio", WdRoles.WdINPUT,
			"image", WdRoles.WdIMG,
			"link", WdRoles.WdA,
			"label", WdRoles.WdLABEL,
			"element", Roles.Widget
			);

	Map<String, List<Tag<?>>> selectorString2Tags = Map.ofEntries(
			Map.entry("button", List.of(Tags.Title, WdTags.WebGenericTitle, WdTags.WebTextContent)),
			Map.entry("input_text", List.of(Tags.Title, WdTags.WebName, WdTags.WebGenericTitle)),
			Map.entry("static_text", List.of(Tags.Title, WdTags.WebTextContent, WdTags.WebGenericTitle)),
			Map.entry("alert", List.of(Tags.Title, WdTags.WebGenericTitle)),
			Map.entry("dropdown", List.of(Tags.Title, WdTags.WebGenericTitle)),
			Map.entry("checkbox", List.of(Tags.Title, WdTags.WebGenericTitle)),
			Map.entry("radio", List.of(Tags.Title, WdTags.WebGenericTitle)),
			Map.entry("image", List.of(WdTags.WebAlt, Tags.Desc, WdTags.WebTitle, WdTags.WebGenericTitle)),
			Map.entry("link", List.of(Tags.Title, WdTags.WebHref, WdTags.WebGenericTitle)),
			Map.entry("label", List.of(Tags.Title, WdTags.WebGenericTitle)),
			Map.entry("element", List.of(Tags.Title, WdTags.WebGenericTitle))
			);

	Map<String, List<Tag<?>>> statusTags = Map.ofEntries(
			Map.entry("visible", List.of(WdTags.WebIsFullOnScreen)),
			Map.entry("offscreen", List.of(WdTags.WebIsOffScreen)),
			Map.entry("onscreen", List.of(WdTags.WebIsFullOnScreen)),

			Map.entry("enabled", List.of(WdTags.WebIsEnabled)),
			Map.entry("disabled", List.of(WdTags.WebIsDisabled)),
			Map.entry("clickable", List.of(WdTags.WebIsClickable)),
			Map.entry("focused", List.of(WdTags.WebHasKeyboardFocus)),
			Map.entry("readonly", List.of(WdTags.WebIsKeyboardFocusable)),

			Map.entry("selected", List.of(WdTags.WebIsSelected)),
			Map.entry("checked", List.of(WdTags.WebIsChecked)),

			Map.entry("empty", List.of(WdTags.WebValue)),
			Map.entry("filled", List.of(WdTags.WebValue))
			);

	Map<String, List<Tag<?>>> attributeTags = Map.ofEntries(
			Map.entry("label", List.of(Tags.Title, WdTags.WebGenericTitle)),
			Map.entry("alttext", List.of(WdTags.WebAlt)),
			Map.entry("role", List.of(Tags.Role)),
			Map.entry("placeholder", List.of(WdTags.WebPlaceholder)),
			Map.entry("tooltip", List.of(Tags.Desc, WdTags.WebTextContent))
			);

}
