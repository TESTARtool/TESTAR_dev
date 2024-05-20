/**
 * Copyright (c) 2018 - 2024 Open Universiteit - www.ou.nl
 * Copyright (c) 2019 - 2024 Universitat Politecnica de Valencia - www.upv.es
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

import org.testar.CodingManager;
import org.testar.RandomActionSelector;
import org.testar.SutVisualization;
import org.testar.managers.InputDataManager;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.*;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.alayer.exceptions.SystemStartException;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.plugin.NativeLinker;
import org.testar.monkey.Pair;
import org.testar.monkey.Util;
import org.testar.protocols.WebdriverProtocol;
import org.testar.settings.Settings;

import java.util.*;
import java.util.stream.Collectors;

import static org.testar.monkey.alayer.Tags.Blocked;
import static org.testar.monkey.alayer.Tags.Enabled;
import static org.testar.monkey.alayer.webdriver.Constants.scrollArrowSize;
import static org.testar.monkey.alayer.webdriver.Constants.scrollThick;

public class Protocol_webdriver_parabank extends WebdriverProtocol {

	@Override
	protected void buildStateIdentifiers(State state) {
		CodingManager.buildIDs(state);
		// Reset widgets AbstractID identifier values to empty
		for(Widget w : state) { w.set(Tags.AbstractID, ""); }
		// Custom the State AbstractIDCustom identifier
		customBuildAbstractID(state);
	}

	private synchronized void customBuildAbstractID(Widget widget){
		if (widget.parent() != null) {
			// If the widget is a son of a WdSELECT widget, force to use static web id for the main menu but ignore options
			if(isSonOfSelect(widget)) {
				// Use web id property of main select menu <select id="transactionType" name="transactionType"/>
				if(!widget.get(WdTags.WebId, "").isEmpty()) {
					widget.set(Tags.AbstractID, CodingManager.ID_PREFIX_WIDGET + CodingManager.ID_PREFIX_ABSTRACT + CodingManager.codify(widget, WdTags.WebId));
				}
				// <option value="January">January</option> or <option value="February">February</option> or <option value="March">March</option> or etc
				return;
			}

			// Bill pay, phone number widget has a dynamic WebId property, always force to use WebName
			if(widget.get(WdTags.WebName, "").equals("payee.phoneNumber")) { 
				widget.set(Tags.AbstractID, CodingManager.ID_PREFIX_WIDGET + CodingManager.ID_PREFIX_ABSTRACT + CodingManager.codify(widget, WdTags.WebName));
				return;
			}

			widget.set(Tags.AbstractID, CodingManager.ID_PREFIX_WIDGET + CodingManager.ID_PREFIX_ABSTRACT + CodingManager.codify(widget, CodingManager.getCustomTagsForAbstractId()));

		} else if (widget instanceof State) {
			StringBuilder abstractIdCustom;
			abstractIdCustom = new StringBuilder();
			for (Widget childWidget : (State) widget) {
				if (childWidget != widget) {
					customBuildAbstractID(childWidget);
					abstractIdCustom.append(childWidget.get(Tags.AbstractID));
				}
			}
			widget.set(Tags.AbstractID, CodingManager.ID_PREFIX_STATE + CodingManager.ID_PREFIX_ABSTRACT + CodingManager.lowCollisionID(abstractIdCustom.toString()));
		}
	}

	private boolean isSonOfSelect(Widget widget) {
		if(widget.parent() == null) return false;
		else if (widget.parent().get(Tags.Role, Roles.Widget).equals(WdRoles.WdSELECT)) return true;
		else return isSonOfSelect(widget.parent());
	}

	/**
	 * This method is invoked each time the TESTAR starts the SUT to generate a new sequence.
	 * This can be used for example for bypassing a login screen by filling the username and password
	 * or bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
	 * the SUT's configuration files etc.)
	 */
	@Override
	protected void beginSequence(SUT system, State state) {
		super.beginSequence(system, state);
		// Move the mouse to the corner because previous sequence mouse position may affect the model determinism (popup description)
		mouse.setCursor(5, 5);
		Util.pause(1);

		// Script login sequence
		WdDriver.executeScript("document.getElementsByName('username')[0].setAttribute('value','john');");
		WdDriver.executeScript("document.getElementsByName('password')[0].setAttribute('value','demo');");
		WdDriver.executeScript("document.getElementsByName('login')[0].submit();");
		Util.pause(1);
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
	protected State getState(SUT system) throws StateBuildException {
		// parabank wsdl pages have no widgets, we need to force a webdriver history back action
		if(WdDriver.getCurrentUrl().contains("wsdl") || WdDriver.getCurrentUrl().contains("wadl")) {
			WdDriver.executeScript("window.history.back();");
			Util.pause(1);
		}

		return super.getState(system);
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

			// If the element is blocked, Testar can't click on or type in the widget
			if (widget.get(Blocked, false) && !widget.get(WdTags.WebIsShadow, false)) {
				continue;
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

	@Override
	protected boolean isTypeable(Widget widget) {
		Role role = widget.get(Tags.Role, Roles.Widget);
		if (Role.isOneOf(role, NativeLinker.getNativeTypeableRoles())) {

			// Specific class="input" for parasoft SUT
			if(widget.get(WdTags.WebCssClasses, "").contains("input")) {
				return true;
			}
		}

		return super.isTypeable(widget);
	}

	@Override
	protected Action selectAction(State state, Set<Action> actions){
		Action retAction = stateModelManager.getAbstractActionToExecute(actions);
		if(retAction==null) {
			System.out.println("State model based action selection did not find an action. Using random action selection.");
			// if state model fails, use random (default would call preSelectAction() again, causing double actions HTML report):
			retAction = RandomActionSelector.selectRandomAction(actions);
		}
		return retAction;
	}

	@Override
	protected boolean executeAction(SUT system, State state, Action action) {
		return super.executeAction(system, state, action);
	}
}
