/**
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2019 - 2025 Universitat Politecnica de Valencia - www.upv.es
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
 *
 */

import com.google.common.collect.ArrayListMultimap;
import org.testar.SutVisualization;
import org.testar.managers.InputDataManager;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Pair;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.NOP;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.actions.WdFillFormAction;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.alayer.exceptions.SystemStartException;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.protocols.WebdriverProtocol;
import org.testar.settings.Settings;

import java.util.*;
import static org.testar.monkey.alayer.Tags.Blocked;
import static org.testar.monkey.alayer.Tags.Enabled;
import static org.testar.monkey.alayer.webdriver.Constants.scrollArrowSize;
import static org.testar.monkey.alayer.webdriver.Constants.scrollThick;


public class Protocol_webdriver_rascal_verdict extends WebdriverProtocol {

	/**
	 * This is a helper method used by the default implementation of <code>buildState()</code>
	 * It examines the SUT's current state and returns an oracle verdict.
	 *
	 * @return oracle verdict, which determines whether the state is erroneous and why.
	 */
	@Override
	protected Verdict getVerdict(State state) {

		Verdict verdict = super.getVerdict(state);
		// system crashes, non-responsiveness and suspicious tags automatically detected!

		//-----------------------------------------------------------------------------
		// MORE SOPHISTICATED ORACLES CAN BE PROGRAMMED HERE (the sky is the limit ;-)
		//-----------------------------------------------------------------------------

		// ... YOU MAY WANT TO CHECK YOUR CUSTOM ORACLES HERE ...

		Verdict rascalButtonVerdict = getButtonVerdict(state);
		if(rascalButtonVerdict != Verdict.OK) return rascalButtonVerdict;

		Verdict rascalCheckBoxVerdict = getCheckBoxVerdict(state);
		if(rascalCheckBoxVerdict != Verdict.OK) return rascalCheckBoxVerdict;

		return verdict;
	}

	public Verdict getButtonVerdict(State state) {
		Verdict verdict = Verdict.OK;   

		Widget w_Submit = helperMethods.getWidget(state, "button", "Submit");

		if (w_Submit == null)
			return new Verdict(Verdict.Severity.WARNING, "Could not find button 'Submit'");

		Boolean is_enabled= helperMethods.evaluate_isStatus(w_Submit, "enabled");

		if (is_enabled == null)
			return new Verdict(Verdict.Severity.WARNING, "Could not find status 'enabled'");
		if (!is_enabled){
			return new Verdict(Verdict.Severity.FAIL, "button Submit must be enabled");
		}
		else verdict = Verdict.OK;

		return verdict;
	}

	public Verdict getCheckBoxVerdict(State state) {
		Verdict verdict = Verdict.OK;   

		Widget w_Accept_Terms = helperMethods.getWidget(state, "checkbox", "Accept Terms");

		if (w_Accept_Terms == null)
			return new Verdict(Verdict.Severity.WARNING, "Could not find 'checkbox' 'Accept Terms'");

		Boolean is_selected= helperMethods.evaluate_isStatus(w_Accept_Terms, "selected");

		if (is_selected == null)
			return new Verdict(Verdict.Severity.WARNING, "Could not status 'selected'");

		if (!is_selected){
			verdict = Verdict.OK;
		}
		else verdict = null;

		Widget w_Submit = helperMethods.getWidget(state, "button", "Submit");

		if (w_Submit == null)
			return new Verdict(Verdict.Severity.WARNING, "Could not find button 'Submit'");

		Boolean is_enabled= helperMethods.evaluate_isStatus(w_Submit, "enabled");

		if (is_enabled == null)
			return new Verdict(Verdict.Severity.WARNING, "Could not find status 'enabled'");
		if (!is_enabled){
			return new Verdict(Verdict.Severity.FAIL, "submit only enabled when terms accepted");
		}
		else verdict = Verdict.OK;

		return verdict;
	}

	/**
	 * This method is used by TESTAR to determine the set of currently available actions.
	 * You can use the SUT's current state, analyze the widgets and their properties to create
	 * a set of sensible actions, such as: "Click every Button which is enabled" etc.
	 * The return value is supposed to be non-null. If the returned set is empty, TESTAR
	 * will stop generation of the current action and continue with the next one.
	 *
	 * @param system the SUT
	 * @param state  the SUT's current state
	 * @return a set of actions
	 */
	@Override
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {
		// Kill unwanted processes, force SUT to foreground
		Set<Action> actions = super.deriveActions(system, state);
		Set<Action> filteredActions = new HashSet<>();

		// create an action compiler, which helps us create actions
		// such as clicks, drag&drop, typing ...
		StdActionCompiler ac = new AnnotatingActionCompiler();

		// Check if forced actions are needed to stay within allowed domains
		Set<Action> forcedActions = detectForcedActions(state, ac);

		// iterate through all widgets
		for (Widget widget : state) {

			// only consider enabled and non-tabu widgets
			if (!widget.get(Enabled, true)) {
				continue;
			}
			// The blackListed widgets are those that have been filtered during the SPY mode with the
			//CAPS_LOCK + SHIFT + Click clickfilter functionality.
			if(blackListed(widget)){
				if(isTypeable(widget)){
					filteredActions.add(ac.clickTypeInto(widget, InputDataManager.getRandomTextInputData(widget), true));
				} else {
					filteredActions.add(ac.leftClickAt(widget));
				}
				continue;
			}

			// slides can happen, even though the widget might be blocked
			addSlidingActions(actions, ac, scrollArrowSize, scrollThick, widget);

			// If the element is blocked, Testar can't click on or type in the widget
			if (widget.get(Blocked, false) && !widget.get(WdTags.WebIsShadow, false)) {
				continue;
			}

			// type into text boxes
			if (isAtBrowserCanvas(widget) && isTypeable(widget)) {
				if(whiteListed(widget) || isUnfiltered(widget)){
					actions.add(ac.clickTypeInto(widget, InputDataManager.getRandomTextInputData(widget), true));
				}else{
					// filtered and not white listed:
					filteredActions.add(ac.clickTypeInto(widget, InputDataManager.getRandomTextInputData(widget), true));
				}
			}

			// left clicks, but ignore links outside domain
			if (isAtBrowserCanvas(widget) && isClickable(widget)) {
				if(whiteListed(widget) || isUnfiltered(widget)){
					if (!isLinkDenied(widget)) {
						actions.add(ac.leftClickAt(widget));
					}else{
						// link denied:
						filteredActions.add(ac.leftClickAt(widget));
					}
				}else{
					// filtered and not white listed:
					filteredActions.add(ac.leftClickAt(widget));
				}
			}
		}

		// If we have forced actions, prioritize and filter the other ones
		if (forcedActions != null && forcedActions.size() > 0) {
			filteredActions = actions;
			actions = forcedActions;
		}

		//Showing the grey dots for filtered actions if visualization is on:
		if(visualizationOn || mode() == Modes.Spy) SutVisualization.visualizeFilteredActions(cv, state, filteredActions);

		return actions;
	}

}

class helperMethods {

	public static Widget getWidget(State state, String elementType, String selector) {
		Role elementRole = element2Role.get(elementType);
		List<Tag<?>> tagPriority = selectorString2Tags.get(elementType);

		for (Widget w : state) {
			Role widgetRole = w.get(Tags.Role, Roles.Widget);
			if (!elementRole.equals(widgetRole)) {
				continue;
			}

			for (Tag<?> tag : tagPriority) {
				Object tagValue = w.get(tag, null);
				if (tagValue instanceof String && selector.equals(tagValue)) {
					return w;
				}
			}
		}
		return null;
	}

	public static Boolean evaluate_isStatus(Widget w, String status) {
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

	public static boolean evaluate_hasAttribute(Widget w, String attr) {
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


	public static final Map<String, Set<String>> validStatusPerElement = Map.ofEntries(
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

	public static final Map<String, Role> element2Role = Map.of(
			"button", WdRoles.WdBUTTON,
			"input_text", Roles.Text,
			"static_text", WdRoles.WdLABEL,
			//"alert", Roles.Alert,
			"dropdown", WdRoles.WdSELECT,
			"checkbox", WdRoles.WdINPUT,
			"radio", WdRoles.WdINPUT,
			"image", WdRoles.WdIMG,
			"link", WdRoles.WdLINK,
			"label", WdRoles.WdLABEL,
			"element", Roles.Widget
			);

	public static final Map<String, List<Tag<?>>> selectorString2Tags = Map.ofEntries(
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

	public static final Map<String, List<Tag<?>>> statusTags = Map.ofEntries(
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

	public static final Map<String, List<Tag<?>>> attributeTags = Map.ofEntries(
			Map.entry("label", List.of(Tags.Title, WdTags.WebGenericTitle)),
			Map.entry("alttext", List.of(WdTags.WebAlt)),
			Map.entry("role", List.of(Tags.Role)),
			Map.entry("placeholder", List.of(WdTags.WebPlaceholder)),
			Map.entry("tooltip", List.of(Tags.Desc, WdTags.WebTextContent))
			);

}
