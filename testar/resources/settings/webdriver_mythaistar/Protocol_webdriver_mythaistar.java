/**
 * Copyright (c) 2018 - 2022 Open Universiteit - www.ou.nl
 * Copyright (c) 2019 - 2022 Universitat Politecnica de Valencia - www.upv.es
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

import es.upv.staq.testar.NativeLinker;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.*;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.webdriver.*;
import org.fruit.alayer.webdriver.enums.WdRoles;
import org.fruit.alayer.webdriver.enums.WdTags;
import org.fruit.monkey.Settings;
import org.testar.protocols.WebdriverProtocol;

import java.util.*;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;

public class Protocol_webdriver_mythaistar extends WebdriverProtocol {

	//https://testar.org/images/development/experiments/mythaistar_all_dependencies.zip

	private static List<String> alwaysClickableClasses = Arrays.asList("owl-dt-control-button-content");

	private static List<String> typeableClasses = Arrays.asList(
			//Text input of Menu page
			"mat-form-field-label-wrapper", //bookTable Page, Sign UP and Email
			"owl-dt-timer-input" //Calendar dates
			);

	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 * @param   settings  the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings){
		super.initialize(settings);
		// Disable security to disable CORS policy
		WdDriver.disableSecurity = true;

		// Classes that are deemed clickable by the web framework
		clickableClasses = Arrays.asList(
				"v-menubar-menuitem", "v-menubar-menuitem-caption",
				//Main page
				"mat-button-ripple", "flag-icon", "mat-menu-ripple", "mat-icon", "mat-tab-label-content",
				//Menu page
				"mat-checkbox-label",
				"mat-select-arrow",
				"mat-expansion-panel-header-title",
				"order",
				//Sort by and options
				"mat-select-placeholder", "mat-option-ripple",
				//Calendar
				"owl-dt-calendar-cell-content",
				// Reservation cells
				"mat-cell",
				"ng-star-inserted",
				// Login and register tab
				"mat-ripple"
				);
	}

	/**
	 * This method is called when TESTAR requests the state of the SUT.
	 * Here you can add additional information to the SUT's state or write your
	 * own state fetching routine. The state should have attached an oracle
	 * (TagName: <code>Tags.OracleVerdict</code>) which describes whether the
	 * state is erroneous and if so why.
	 *
	 * @return the current state of the SUT with attached oracle.
	 */
	@Override
	protected State getState(SUT system) throws StateBuildException {
		State state = super.getState(system);

		System.out.println("State AbstractIDCustom: " + state.get(Tags.AbstractIDCustom));

		return state;
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

		// create an action compiler, which helps us create actions
		// such as clicks, drag&drop, typing ...
		StdActionCompiler ac = new AnnotatingActionCompiler();

		Set<Action> forcedActions = detectForcedActions(state, ac);
		if (forcedActions != null && forcedActions.size() > 0) {
			return forcedActions;
		}

		loginMyThaiStarAction(actions, state);

		registerMyThaiStarAction(actions, state);

		// iterate through all widgets
		for (Widget widget : state) {

			// left clicks, but ignore links outside domain
			if (isAlwaysClickable(widget)) {
				actions.add(ac.leftClickAt(widget));
			}

			// only consider enabled and non-tabu widgets
			if (!widget.get(Enabled, true) || blackListed(widget)) {
				continue;
			}

			// slides can happen, even though the widget might be blocked
			//addSlidingActions(actions, ac, scrollArrowSize, scrollThick, widget);

			// If the element is blocked, TESTAR can't click on or type in the widget
			if (widget.get(Blocked, false)) {
				continue;
			}

			// type into text boxes
			if (isAtBrowserCanvas(widget) && isTypeable(widget) && (whiteListed(widget) || isUnfiltered(widget)) ) {
				actions.add(ac.clickTypeInto(widget, getRandomMyThaiStarData(widget), true));
			}

			// left clicks, but ignore links outside domain
			if (isAtBrowserCanvas(widget) && isClickable(widget) && !isLinkDenied(widget) && (whiteListed(widget) || isUnfiltered(widget)) ) {
				// Click on select web items opens the menu but does not allow TESTAR to select an item,
				// thats why we need a custom action selection
				if(widget.get(Tags.Role, Roles.Widget).equals(WdRoles.WdSELECT)) {
					//actions.add(randomFromSelectList(widget));
				} else {
					actions.add(ac.leftClickAt(widget));
				}
			}
		}

		return actions;
	}

	/**
	 * Get specific text data for shopizer text fields
	 * 
	 * @param w
	 * @return
	 */
	private String getRandomMyThaiStarData(Widget w) {
		String[] example = {"aaaa", "1234", "01-01-2021"};
		if(w.get(WdTags.WebId, "").toLowerCase().contains("example")) {
			return example[new Random().nextInt(example.length)];
		}
		return this.getRandomText(w);
	}

	/**
	 * Randomly select one item from the select list widget. 
	 * 
	 * @param w
	 * @return
	 */
	private Action randomFromSelectList(Widget w) {
		int selectLength = 1;
		String elementId = w.get(WdTags.WebId, "");
		String elementName = w.get(WdTags.WebName, "");

		// Get the number of values of the specific select list item
		// Comment out because we are going to select always the first value of the select list
		/*try {
            String query = String.format("return document.getElementById('%s').length", elementId);
            Object response = WdDriver.executeScript(query);
            selectLength = ( response != null ? Integer.parseInt(response.toString()) : 1 );
        } catch (Exception e) {
            System.out.println("*** ACTION WARNING: problems trying to obtain select list length: " + elementId);
        }*/

		// Select one of the values randomly, or the first one if previous length failed
		if(!elementId.isEmpty()) {
			try {
				//String query = String.format("return document.getElementById('%s').item(%s).value", elementId, new Random().nextInt(selectLength));
				String query = String.format("return document.getElementById('%s').item(%s).value", elementId, selectLength);
				Object response = WdDriver.executeScript(query);
				return (response != null ?  new WdSelectListAction(elementId, response.toString(), w) : new AnnotatingActionCompiler().leftClickAt(w) );
			} catch (Exception e) {
				System.out.println("*** ACTION WARNING: problems trying randomly select a list value using WebId: " + elementId);
				e.printStackTrace();
			}
		} else if(!elementName.isEmpty()) {
			try {
				String query = String.format("return document.getElementsByName('%s')[0].item(%s).value", elementName, selectLength);
				Object response = WdDriver.executeScript(query);
				return (response != null ?  new WdSelectListAction(elementName, response.toString(), w) : new AnnotatingActionCompiler().leftClickAt(w) );
			} catch (Exception e) {
				System.out.println("*** ACTION WARNING: problems trying randomly select a list value using WebName: " + elementName);
				e.printStackTrace();
			}
		}

		return new AnnotatingActionCompiler().leftClickAt(w);
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

	private boolean isAlwaysClickable(Widget widget) {
		WdElement element = ((WdWidget) widget).element;
		Set<String> clickSet = new HashSet<>(alwaysClickableClasses);
		clickSet.retainAll(element.cssClasses);
		return clickSet.size() > 0;
	}

	@Override
	protected boolean isTypeable(Widget widget) {
		WdElement element = ((WdWidget) widget).element;
		Set<String> clickSet = new HashSet<>(typeableClasses);
		clickSet.retainAll(element.cssClasses);
		if(clickSet.size() > 0)
			return true;

		return false;
	}

	/**
	 * Check if current state contains the login form with username and password widgets. 
	 * In that case derive a login actions using waiter credentials. 
	 * 
	 * @param actions
	 * @param state
	 */
	private void loginMyThaiStarAction(Set<Action> actions, State state) {
		// If current state does not contains the desired login widgets, do not add a login action
		if(!stateContainsAllWebNameValues(state, Arrays.asList("username", "password"))) {
			return;
		}

		StdActionCompiler ac = new AnnotatingActionCompiler();
		Action loginAction = new CompoundAction.Builder()
				.add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "username", state), "waiter", true), 1)
				.add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "password", state), "waiter", true), 1)
				.add(ac.hitKey(KBKeys.VK_ENTER), 0.5)
				.build(getWidgetWithMatchingTag("name", "username", state));

		actions.add(loginAction);
	}

	/** 
	 * Check if current state contains the register form with email, password and accept terms widgets. 
	 * In that case derive a register actions using new email credentials. 
	 * 
	 * @param actions
	 * @param state
	 */
	private void registerMyThaiStarAction(Set<Action> actions, State state) {
		// If current state does not contains the desired register widgets, do not add a register action
		if(!stateContainsAllWebNameValues(state, Arrays.asList("email", "password", "confirmPassword", "registerTerms"))) {
			return;
		}

		StdActionCompiler ac = new AnnotatingActionCompiler();
		Action registerAction = new CompoundAction.Builder()
				.add(ac.pasteTextInto(getWidgetWithMatchingTag("name", "email", state), "email@email.com", true), 1)
				.add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "password", state), "email", true), 1)
				.add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "confirmPassword", state), "email", true), 1)
				.add(ac.leftClickAt(getWidgetWithMatchingTag("name", "registerTerms", state)), 1)
				.add(ac.hitKey(KBKeys.VK_ENTER), 0.5)
				.build(getWidgetWithMatchingTag("name", "email", state));

		actions.add(registerAction);
	}

	private boolean stateContainsAllWebNameValues(State state, List<String> webNameValues) {
		for(String value : webNameValues) {
			if(!stateContainsWebNameValue(state, value)) {
				return false;
			}
		}
		return true;
	}

	private boolean stateContainsWebNameValue(State state, String webNameValue) {
		for(Widget w : state) {
			if(w.get(WdTags.WebName,"").equals(webNameValue)) {
				return true;
			}
		}
		return false;
	}
}
