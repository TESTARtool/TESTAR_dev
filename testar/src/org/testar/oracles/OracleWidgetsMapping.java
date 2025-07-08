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

import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;

public interface OracleWidgetsMapping {

	default List<Widget> getWidgets(String elementType, State state) {
		List<Widget> lst = new ArrayList<>();
		List<Role> elementRoles = element2Role.get(elementType);
		for (Widget w : state) {
			Role widgetRole = w.get(Tags.Role, Roles.Invalid);
			if (elementRoles.contains(widgetRole)) {
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

	default Object getProperty(Widget w, String property) {
		List<Tag<?>> tagPriority = attributeTags.get(property);

		for (Tag<?> tag : tagPriority) {
			Object value = w.get(tag, null);

			if (value instanceof String) {
				if (!((String) value).isEmpty()) {
					return value;
				}
			} else if (tag.name().equals(Tags.WidgetChildren.name())) {
				return getWidgetChildren(w);
			} else if (value != null) {
				return value;
			}
		}

		return null;
	}

	default Boolean evaluateAreStatus(List<Object> listObj, String status) {
		if (listObj == null || listObj.isEmpty()) return false;

		// TODO: The logic of this joint status might be different based on status properties
		// enabled vs disable
		// empty vs filled
		Boolean result = true;
		for (Object obj : listObj) {
			result = (result && evaluateIsStatus(obj, status));
		}
		return result;
	}

	default Boolean evaluateIsStatus(Object obj, String status) {
		// User wants to evaluate the status if a widget
		if (obj instanceof Widget) {
			return evaluateIsStatus(((Widget)obj), status);
		}
		// TODO: Think if there are other non-widget objects status

		return false;
	}

	default Boolean evaluateIsStatus(Widget w, String status) {
		List<Tag<?>> tagPriority = statusTags.get(status);

		for (Tag<?> tag : tagPriority) {
			Object value = w.get(tag, null);

			// Special cases first
			if (status.equals("empty")) {
				if (value instanceof Integer) {
					return value.equals(0);
				} else if (value instanceof String) {
					return ((String) value).isEmpty();
				}
			} else if (status.equals("filled")) {
				if (value instanceof String) {
					return !((String) value).isEmpty();
				}
			} else if (status.equals("readonly")) {
				if (value instanceof Boolean) {
					return !((Boolean) value);
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

	default boolean evaluateHasAttribute(Object obj, String attr) {
		// User wants to evaluate if a widget has an attribute
		if (obj instanceof Widget) {
			return evaluateHasAttribute(((Widget)obj), attr);
		}

		// User wants to evaluate if in a list of widgets
		if (obj instanceof List<?>) {
			List<?> list = (List<?>) obj;
			for (Object item : list) {
				if (item instanceof Widget) {
					if (evaluateHasAttribute((Widget) item, attr)) {
						return true; // At least one widget has the attribute
					}
				}
			}
		}

		// TODO: Think other taggable objects like actions

		return false; 
	}

	default boolean evaluateHasAttribute(Widget w, String attr) {
		List<Tag<?>> tagPriority = attributeTags.get(attr);

		for (Tag<?> tag : tagPriority) {
			Object value = w.get(tag, null);

			if (value instanceof String 
					&& !((String) value).isEmpty()) {
				return true; //it has the attribute and it is not empty
			}
		}

		// Attribute was not found
		return false; 
	}

	default boolean evaluateContains(Object obj, String contain) {
		// User wants to evaluate if the widget contains a string
		if (obj instanceof Widget) {
			return evaluateContains(((Widget)obj), contain);
		}
		// User wants to evaluate a String Tag
		else if (obj instanceof String 
				&& !((String) obj).isEmpty()
				&& ((String) obj).contains(contain)) {
			return true;
		}

		return false;
	}

	default boolean evaluateContains(Widget w, String contain) {
		if (w == null || contain == null || contain.isEmpty() || w.tags() == null) {
			return false;
		}

		for (Tag<?> tag : w.tags()) {
			Object value = w.get(tag);
			if (value instanceof String 
					&& !((String) value).isEmpty()
					&& ((String) value).contains(contain)) {
				return true;
			}
		}

		return false;
	}

	//	default boolean evaluateAttributeContains(Widget w, String attr, String contain) {
	//		List<Tag<?>> tagPriority = attributeTags.get(attr);
	//
	//		for (Tag<?> tag : tagPriority) {
	//			Object value = w.get(tag, null);
	//
	//			if (value instanceof String 
	//					&& !((String) value).isEmpty()
	//					&& ((String) value).contains(contain)) {
	//				return true;
	//			}
	//		}
	//
	//		return false; 
	//	}

	default boolean evaluateMatches(Object obj, String regex) {
		// User wants to evaluate if the widget matches a regex
		if (obj instanceof Widget) {
			return evaluateMatches(((Widget)obj), regex);
		}
		// User wants to evaluate a String Tag
		else if (obj instanceof String 
				&& !((String) obj).isEmpty()) {

			Pattern pattern;
			try {
				pattern = Pattern.compile(regex);
			} catch (PatternSyntaxException e) {
				return false;
			}

			Matcher matcher = pattern.matcher((String) obj);
			if (matcher.find()) {
				return true;
			}

		}

		return false;
	}

	default boolean evaluateMatches(Widget w, String regex) {
		if (w == null || regex == null || regex.isEmpty() || w.tags() == null) {
			return false;
		}

		Pattern pattern;
		try {
			pattern = Pattern.compile(regex);
		} catch (PatternSyntaxException e) {
			return false;
		}

		for (Tag<?> tag : w.tags()) {
			Object value = w.get(tag);
			if (value instanceof String 
					&& !((String) value).isEmpty()) {
				Matcher matcher = pattern.matcher((String) value);
				if (matcher.find()) {
					return true;
				}
			}
		}

		return false;
	}

	default boolean evaluateIsEqualTo(Object obj, Object compare) {
		return Objects.equals(obj, compare);
	}

	default boolean evaluateIsEqualTo(Object obj, Supplier<Object> compareSupplier) {
		return Objects.equals(obj, compareSupplier.get());
	}

	default Color getBackgroundColor() {
		// This method makes no sense because one state can have a different background color. 
		// So it is widget-dependent.
		return Color.White;
	}

	private List<Widget> getWidgetChildren(Widget w) {
		List<Widget> children = new ArrayList<>();
		for(int i = 0; i < w.childCount(); i++) {
			children.add(w.child(i));
		}
		return children;
	}

	//	default boolean evaluateAttributeMatches(Widget w, String attr, String regex) {
	//		List<Tag<?>> tagPriority = attributeTags.get(attr);
	//
	//		Pattern pattern;
	//		try {
	//			pattern = Pattern.compile(regex);
	//		} catch (PatternSyntaxException e) {
	//			return false;
	//		}
	//
	//		for (Tag<?> tag : tagPriority) {
	//			Object value = w.get(tag, null);
	//
	//			if (value instanceof String 
	//					&& !((String) value).isEmpty()) {
	//				Matcher matcher = pattern.matcher((String) value);
	//				if (matcher.find()) {
	//					return true;
	//				}
	//			}
	//		}
	//
	//		return false; 
	//	}

	Map<String, Set<String>> validStatusPerElement = Map.ofEntries(
			Map.entry("button", Set.of("visible", "enabled", "focused", "clickable", "fullonscreen")),
			Map.entry("input_text", Set.of("visible", "enabled", "focused", "readonly", "empty", "filled", "fullonscreen")),
			Map.entry("input_numeric", Set.of("visible", "enabled", "focused", "readonly", "empty", "filled", "fullonscreen")),
			Map.entry("static_text", Set.of("visible", "enabled", "focused", "readonly", "empty", "fullonscreen")),
			Map.entry("checkbox", Set.of("visible", "enabled", "focused", "checked", "clickable", "fullonscreen")),
			Map.entry("radio", Set.of("visible", "enabled", "focused", "selected", "clickable", "fullonscreen")),
			Map.entry("dropdown", Set.of("visible", "enabled", "focused", "empty", "selected", "clickable", "fullonscreen")),
			Map.entry("label", Set.of("visible", "focused", "fullonscreen")),
			Map.entry("image", Set.of("visible", "focused", "fullonscreen")),
			Map.entry("link", Set.of("visible", "clickable", "focused", "fullonscreen")),
			Map.entry("alert", Set.of("visible", "focused", "fullonscreen")),
			Map.entry("panel", Set.of("visible", "focused", "empty", "fullonscreen")),
			Map.entry("table_data", Set.of("visible", "focused", "empty", "fullonscreen")),
			Map.entry("menu", Set.of("visible", "enabled", "focused", "clickable", "empty", "fullonscreen")),
			Map.entry("menu_item", Set.of("visible", "enabled", "focused", "clickable", "empty", "fullonscreen")),
			Map.entry("form", Set.of("visible", "focused", "empty", "fullonscreen")),
			Map.entry("element", Set.of("visible", "enabled", "focused", "empty", "fullonscreen"))
			);

	Map<String, List<Role>> element2Role = Map.ofEntries(
			Map.entry("button", List.of(WdRoles.WdBUTTON)),
			Map.entry("input_text", List.of(WdRoles.WdINPUT, Roles.Text)),
			Map.entry("input_numeric", List.of(WdRoles.WdINPUT, Roles.Text)),
			Map.entry("static_text", List.of(WdRoles.WdLABEL, WdRoles.WdP, WdRoles.WdSPAN)),
			Map.entry("alert", List.of(Roles.Widget)), // This is not an element itself
			Map.entry("dropdown", List.of(WdRoles.WdSELECT)),
			Map.entry("checkbox", List.of(WdRoles.WdINPUT)),
			Map.entry("radio", List.of(WdRoles.WdINPUT)),
			Map.entry("image", List.of(WdRoles.WdIMG)),
			Map.entry("link", List.of(WdRoles.WdA)),
			Map.entry("label", List.of(WdRoles.WdLABEL)),
			Map.entry("panel", List.of(WdRoles.WdDIV, WdRoles.WdSECTION, WdRoles.WdARTICLE, WdRoles.WdFIELDSET)),
			Map.entry("table_data", List.of(WdRoles.WdTD)),
			Map.entry("menu", List.of(WdRoles.WdMENU, WdRoles.WdUL, WdRoles.WdNAV)),
			Map.entry("menu_item", List.of(WdRoles.WdLI)),
			Map.entry("form", List.of(WdRoles.WdFORM)),
			Map.entry("element", List.of(Roles.Widget))
			);

	Map<String, List<Tag<?>>> selectorString2Tags = Map.ofEntries(
			Map.entry("button", List.of(Tags.Title, WdTags.WebGenericTitle, WdTags.WebTextContent)),
			Map.entry("input_text", List.of(Tags.Title, WdTags.WebName, WdTags.WebGenericTitle)),
			Map.entry("input_numeric", List.of(Tags.Title, WdTags.WebName, WdTags.WebGenericTitle)),
			Map.entry("static_text", List.of(Tags.Title, WdTags.WebTextContent, WdTags.WebGenericTitle)),
			Map.entry("alert", List.of(Tags.Title, WdTags.WebGenericTitle)),
			Map.entry("dropdown", List.of(Tags.Title, WdTags.WebGenericTitle)),
			Map.entry("checkbox", List.of(Tags.Title, WdTags.WebGenericTitle)),
			Map.entry("radio", List.of(Tags.Title, WdTags.WebGenericTitle)),
			Map.entry("image", List.of(WdTags.WebAlt, Tags.Desc, WdTags.WebTitle, WdTags.WebGenericTitle)),
			Map.entry("link", List.of(Tags.Title, WdTags.WebHref, WdTags.WebGenericTitle)),
			Map.entry("label", List.of(Tags.Title, WdTags.WebGenericTitle)),
			Map.entry("table_data", List.of(Tags.Title, WdTags.WebGenericTitle, WdTags.WebTextContent)),
			Map.entry("panel", List.of(Tags.Title, WdTags.WebId)),
			Map.entry("menu", List.of(Tags.Title, WdTags.WebGenericTitle, WdTags.WebTextContent)),
			Map.entry("menu_item", List.of(Tags.Title, WdTags.WebGenericTitle, WdTags.WebTextContent)),
			Map.entry("form", List.of(Tags.Title, WdTags.WebGenericTitle, WdTags.WebAriaLabel, WdTags.WebAriaLabelledBy)),
			Map.entry("element", List.of(Tags.Title, WdTags.WebGenericTitle))
			);

	Map<String, List<Tag<?>>> statusTags = Map.ofEntries(
			Map.entry("visible", List.of(WdTags.WebIsFullOnScreen)),
			Map.entry("fullonscreen", List.of(WdTags.WebIsFullOnScreen)),

			Map.entry("enabled", List.of(WdTags.WebIsEnabled)),
			Map.entry("disabled", List.of(WdTags.WebIsDisabled)),
			Map.entry("clickable", List.of(WdTags.WebIsClickable)),
			Map.entry("focused", List.of(WdTags.WebHasKeyboardFocus)),
			Map.entry("readonly", List.of(WdTags.WebIsKeyboardFocusable)),

			Map.entry("selected", List.of(WdTags.WebIsSelected)),
			Map.entry("checked", List.of(WdTags.WebIsChecked)),

			// TODO: Create WdTag with this labeled boolean logic
			// Map.entry("labeled", List.of(WdTags))

			Map.entry("empty", List.of(WdTags.WebLength, WdTags.WebValue)),
			Map.entry("filled", List.of(WdTags.WebValue))
			);

	Map<String, List<Tag<?>>> attributeTags = Map.ofEntries(
			Map.entry("label", List.of(Tags.Title, WdTags.WebGenericTitle)),
			Map.entry("alttext", List.of(WdTags.WebAlt)),
			Map.entry("role", List.of(Tags.Role)),
			Map.entry("placeholder", List.of(WdTags.WebPlaceholder)),
			Map.entry("text", List.of(WdTags.WebTextContent, WdTags.WebValue, Tags.Title)),
			Map.entry("tooltip", List.of(Tags.Desc, WdTags.WebTextContent)),
			Map.entry("fontsize", List.of(WdTags.WebComputedFontSize)),
			Map.entry("length", List.of(WdTags.WebLength)),
			Map.entry("title", List.of(WdTags.WebAriaLabel, WdTags.WebAriaLabelledBy, Tags.Title)),
			Map.entry("children", List.of(Tags.WidgetChildren)) // This Tag is null but used for DSL mapping
			);
	
	default List<Widget> runWidgetSelector(State state, String roleString, String rawString)
	{
		List<Tag<?>> tagPrioList  = selectorString2Tags.get(roleString);
		List<Widget> foundWidgets = new ArrayList<>();
		// get widgets with the correct role
		List<Widget> widgets = getWidgets(roleString, state);
		// prepare string for search
		String searchString = rawString.toLowerCase(Locale.ROOT).strip(); // lower case, and remove trailing and leading spaces
		
		
		for(int mode = 0; mode < 3; mode++) // matching modes: exact, starts with, partial (contains)
		{
			for(Tag<?> tag : tagPrioList) //run a round of search per tag in priority list
			{
				for(Widget widget : widgets)
				{
					Object tagValue = widget.get(tag, null);
					if(!foundWidgets.contains(widget) && tagValue instanceof String)
					{
						switch(mode) // apply the correct matching mode
						{
							case 0: // exact
								if(((String) tagValue).strip().equalsIgnoreCase(searchString))
									foundWidgets.add(widget);
								break;
							case 1: // starts with
								if(((String) tagValue).toLowerCase(Locale.ROOT).strip().startsWith(searchString))
									foundWidgets.add(widget);
								break;
							default: // partial (contains)
								if(((String) tagValue).toLowerCase(Locale.ROOT).strip().contains(searchString))
									foundWidgets.add(widget);
						}
					}
				}
				if(!foundWidgets.isEmpty())
					break; // is at least one widget is found during this round, stop the search
			}
		}
		
		return foundWidgets;
	}
}
