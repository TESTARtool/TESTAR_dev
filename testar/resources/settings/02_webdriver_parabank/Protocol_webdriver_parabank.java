/**
 * Copyright (c) 2018 - 2021 Open Universiteit - www.ou.nl
 * Copyright (c) 2019 - 2021 Universitat Politecnica de Valencia - www.upv.es
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

import org.testar.managers.InputDataManager;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.*;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.webdriver.WdElement;
import org.testar.monkey.alayer.webdriver.WdWidget;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.plugin.NativeLinker;
import org.testar.protocols.WebdriverProtocol;

import java.util.*;

import static org.testar.monkey.alayer.Tags.Blocked;
import static org.testar.monkey.alayer.Tags.Enabled;

public class Protocol_webdriver_parabank extends WebdriverProtocol {

	/**
	 * This method is invoked each time the TESTAR starts the SUT to generate a new sequence.
	 * This can be used for example for bypassing a login screen by filling the username and password
	 * or bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
	 * the SUT's configuration files etc.)
	 */
	@Override
	protected void beginSequence(SUT system, State state) {
		super.beginSequence(system, state);

		// Add your login sequence here
		waitLeftClickAndTypeIntoWidgetWithMatchingTag("name","username", "john", state, system, 5,1.0);

		waitLeftClickAndTypeIntoWidgetWithMatchingTag("name","password", "demo", state, system, 5,1.0);

		waitAndLeftClickWidgetWithMatchingTag("value", "Log In", state, system, 5, 1.0);
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
		Set<Action> actions = super.deriveActions(system, state);

		// create an action compiler, which helps us create actions
		// such as clicks, drag&drop, typing ...
		StdActionCompiler ac = new AnnotatingActionCompiler();

		// Check if forced actions are needed to stay within allowed domains
		Set<Action> forcedActions = detectForcedActions(state, ac);
		if(forcedActions != null && !forcedActions.isEmpty()) { return forcedActions; }

		// iterate through all widgets
		for (Widget widget : state) {

			// If the web state contains a form, derive a compound action to fill the form
			if(isAtBrowserCanvas(widget) && widget.get(Tags.Role, Roles.Widget).equals(WdRoles.WdFORM)) {
				addFormFillingAction(actions, widget, ac);
			}

			// only consider enabled and non-tabu widgets
			if (!widget.get(Enabled, true)) { continue; }

			// If the element is blocked, Testar can't click on or type in the widget
			if (widget.get(Blocked, false) && !widget.get(WdTags.WebIsShadow, false)) { continue; }

			// type into text boxes
			if (isAtBrowserCanvas(widget) && isTypeable(widget)) {
				if(whiteListed(widget) || isUnfiltered(widget)){
					// Type a random Number, Alphabetic, URL, Date or Email input
					actions.add(ac.clickTypeInto(widget, InputDataManager.getRandomTextInputData(widget), true));
					// Paste a random input from a customizable input data file
					// Check testar/bin/settings/custom_input_data.txt
					//actions.add(ac.pasteTextInto(widget, InputDataManager.getRandomTextFromCustomInputDataFile(System.getProperty("user.dir") + "/settings/custom_input_data.txt"), true));
				}
			}

			// left clicks, but ignore links outside domain
			if (isAtBrowserCanvas(widget) && isClickable(widget)) {
				if(whiteListed(widget) || isUnfiltered(widget)){
					if (!isLinkDenied(widget)) {
						actions.add(ac.leftClickAt(widget));
					}
				}
			}
		}

		return actions;
	}

	/**
	 * Based on a widget WdForm automatically derive a compound action that includes:
	 * - Type text input data in the input and text area widgets
	 * - Click the submit button of the form
	 * 
	 * @param widgetForm
	 * @param ac
	 * @return
	 */
	private void addFormFillingAction(Set<Action> actions, Widget widgetForm, StdActionCompiler ac){
		CompoundAction.Builder formFillingAction = new CompoundAction.Builder();
		// First, add all type actions
		inputDataAction(widgetForm, ac, formFillingAction);
		// If the form does not contain typeable widgets, just ignore it and do not derive an action
		if(formFillingAction.compoundActionsCount() < 1) return;
		// If typeable actions exists, finally, add the submit click action
		clickSubmitAction(widgetForm, ac, formFillingAction);
		actions.add(formFillingAction.build(widgetForm));
	}

	/**
	 * Iterate through the form widget elements to check if these are input or text area widgets. 
	 * Then automatically include a type action in the compound action. 
	 * 
	 * @param widget
	 * @param ac
	 * @param formFillingAction
	 */
	private void inputDataAction(Widget widget, StdActionCompiler ac, CompoundAction.Builder formFillingAction) {
		// Type input only on typeable and not clickable widgets
		if(widget.get(Enabled, false) && !widget.get(Blocked, true)
				&& isTypeable(widget) && !isClickable(widget)) {
			formFillingAction.add(ac.clickTypeInto(widget, InputDataManager.getRandomTextInputData(widget), true), 1);
		}

		// Iterate through the form element widgets
		for(int i = 0; i < widget.childCount(); i++) {
			inputDataAction(widget.child(i), ac, formFillingAction);
		}
	}

	/**
	 * Iterate through the form widget elements to check if these are input submit widgets. 
	 * Then automatically include a click action in the compound action. 
	 * 
	 * @param widget
	 * @param ac
	 * @param formFillingAction
	 */
	private void clickSubmitAction(Widget widget, StdActionCompiler ac, CompoundAction.Builder formFillingAction) {
		// Clickable widgets but ignoring select elements
		if(widget.get(Enabled, false) && !widget.get(Blocked, true)
				&& isClickable(widget) && !widget.get(Tags.Role, Roles.Widget).equals(WdRoles.WdSELECT)) {
			formFillingAction.add(ac.leftClickAt(widget), 1);
		}

		// Iterate through the form element widgets
		for(int i = 0; i < widget.childCount(); i++) {
			clickSubmitAction(widget.child(i), ac, formFillingAction);
		}
	}

	@Override
	protected boolean isClickable(Widget widget) {
		Role role = widget.get(Tags.Role, Roles.Widget);
		if (Role.isOneOf(role, NativeLinker.getNativeClickableRoles())) {
			// Input type are special...
			if (role.equals(WdRoles.WdINPUT)) {
				String type = ((WdWidget) widget).element.type;
				return WdRoles.clickableInputTypes().contains(type);
			}
			return true;
		}

		WdElement element = ((WdWidget) widget).element;
		if (element.isClickable) {
			return true;
		}

		Set<String> clickSet = new HashSet<>(clickableClasses);
		clickSet.retainAll(element.cssClasses);
		return clickSet.size() > 0;
	}

	@Override
	protected boolean isTypeable(Widget widget) {
		Role role = widget.get(Tags.Role, Roles.Widget);
		if (Role.isOneOf(role, NativeLinker.getNativeTypeableRoles())) {

			// Specific class="input" for parasoft SUT
			if(widget.get(WdTags.WebCssClasses, "").contains("input")) {
				return true;
			}

			// Input type are special...
			if (role.equals(WdRoles.WdINPUT)) {
				String type = ((WdWidget) widget).element.type;
				return WdRoles.typeableInputTypes().contains(type.toLowerCase());
			}
			return true;
		}

		return false;
	}

	/**
	 * Select one of the possible actions (e.g. at random)
	 *
	 * @param state   the SUT's current state
	 * @param actions the set of available actions as computed by <code>buildActionsSet()</code>
	 * @return the selected action (non-null!)
	 */
	@Override
	protected Action selectAction(State state, Set<Action> actions) {
		// Prioritize form filling actions
		for(Action a : actions) {
			if(a.get(Tags.OriginWidget, null) != null && a.get(Tags.OriginWidget).get(Tags.Role, Roles.Widget).equals(WdRoles.WdFORM)) {
				// Give time to the form filling action to type the text
				settings.set(ConfigTags.ActionDuration, 5.0);
				return a;
			}
		}
		settings.set(ConfigTags.ActionDuration, 0.5);
		return super.selectAction(state, actions);
	}
}
