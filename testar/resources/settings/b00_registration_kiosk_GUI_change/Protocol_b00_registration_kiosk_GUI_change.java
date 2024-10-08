/***************************************************************************************************
 *
 * Copyright (c) 2013 - 2024 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 - 2024 Open Universiteit - www.ou.nl
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

import static org.testar.monkey.alayer.Tags.Blocked;
import static org.testar.monkey.alayer.Tags.Enabled;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.testar.DerivedActions;
import org.testar.SutVisualization;
import org.testar.action.priorization.ActionTags;
import org.testar.managers.InputDataManager;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Drag;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.CompoundAction;
import org.testar.monkey.alayer.actions.NOP;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.actions.WdHistoryBackAction;
import org.testar.monkey.alayer.devices.KBKeys;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.alayer.exceptions.SystemStartException;
import org.testar.monkey.alayer.windows.UIARoles;
import org.testar.monkey.alayer.windows.UIATags;
import org.testar.protocols.DesktopProtocol;
import org.testar.screenshotjson.JsonUtils;
import org.testar.settings.Settings;

import com.google.common.collect.Lists;

public class Protocol_b00_registration_kiosk_GUI_change extends DesktopProtocol {


	/**
	 * This method is called when the TESTAR requests the state of the SUT.
	 * Here you can add additional information to the SUT's state or write your
	 * own state fetching routine. The state should have attached an oracle
	 * (TagName: <code>Tags.OracleVerdict</code>) which describes whether the
	 * state is erroneous and if so why.
	 *
	 * super.getState(system) puts the state information also to the HTML sequence report
	 *
	 * @return  the current state of the SUT with attached oracle.
	 */
	@Override
	protected State getState(SUT system) throws StateBuildException {
		State state = super.getState(system);
		for(Widget w : state) {
			// If ControlCustom widget pop-up that ask to complete a text field appears
			if(w.get(Tags.Role, Roles.Widget).equals(UIARoles.UIACustomControl)
					&& w.get(Tags.Title, "").toLowerCase().contains("comple")) {
				// Wait a few seconds to avoid discovering a new state
				Util.pause(8);
				return getState(system);
			}

		}
		return state;
	}

	/**
	 * This method is used by TESTAR to determine the set of currently available actions.
	 * You can use the SUT's current state, analyze the widgets and their properties to create
	 * a set of sensible actions, such as: "Click every Button which is enabled" etc.
	 * The return value is supposed to be non-null. If the returned set is empty, TESTAR
	 * will stop generation of the current action and continue with the next one.
	 * @param system the SUT
	 * @param state the SUT's current state
	 * @return  a set of actions
	 */
	@Override
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {

		//The super method returns kill unwanted processes or force SUT to foreground actions
		// These "special" actions are prioritized over the normal GUI actions in selectAction() / preSelectAction().
		Set<Action> actions = super.deriveActions(system,state);

		// To derive actions (such as clicks, drag&drop, typing ...) we should first create an action compiler.
		StdActionCompiler ac = new AnnotatingActionCompiler();

		// Iterate through top level widgets of the state trying to execute more interesting actions
		for(Widget w : state){

			// Only consider enabled and non-blocked widgets
			if(!w.get(Enabled, true) || w.get(Blocked, false)) {
				continue;
			}

			// The blackListed widgets are those that have been filtered during the SPY mode with the
			//CAPS_LOCK + SHIFT + Click clickfilter functionality.
			if (blackListed(w)) {
				continue;
			}

			//For widgets that are:
			// - clickable
			// and
			// - unFiltered by any of the regular expressions in the Filter-tab, or
			// - whitelisted using the clickfilter functionality in SPY mode (CAPS_LOCK + SHIFT + CNTR + Click)
			// We want to create actions that consist of left clicking on them
			if(isClickable(w)) {
				if((isUnfiltered(w) || whiteListed(w))){
					//Create a left click action with the Action Compiler, and add it to the set of derived actions
					actions.add(ac.leftClickAt(w));
				}
			}		

			/**
			 * ONLY small set of controled type actions
			 */

			//For widgets that are:
			// - typeable
			// and
			// - unFiltered by any of the regular expressions in the Filter-tab, or
			// - whitelisted using the clickfilter functionality in SPY mode (CAPS_LOCK + SHIFT + CNTR + Click)
			// We want to create actions that consist of typing into them
			if(isTypeable(w)){
				if(isUnfiltered(w) || whiteListed(w)) {
					// Generate a random telephone number of 10 digits
					if(w.get(Tags.Role, Roles.Widget).equals(UIARoles.UIAEdit) && 
							w.get(UIATags.UIAAutomationId, "").contains("rfid")) {
						actions.add(new CompoundAction.Builder()
								.add(ac.pasteTextInto(w, "1", true), 1)
								.add(ac.hitKey(KBKeys.VK_ENTER), 1)
								.build(w));
					}
				}
			}

		}

		//return the set of derived actions
		return actions;
	}

	@Override
	protected boolean isClickable(Widget w) {
		// Ignore widget children that come from the PDF document
		if(isSonOfPdfDocument(w)) 
			return false;

		// Ignore list widgets because are used to display information to users
		if(w.get(Tags.Role, Roles.Widget).equals(UIARoles.UIAList) 
				|| w.get(Tags.Role, Roles.Widget).equals(UIARoles.UIAListItem))
			return false;

		// Ignore hyperlink widgets that have no title (top-left home image)
		if(w.get(Tags.Role, Roles.Widget).equals(UIARoles.UIAHyperlink) 
				&& w.get(Tags.Title, "").isBlank())
			return false;

		// Numeric panel buttons that are text widgets are clickable
		if(w.get(Tags.Role, Roles.Widget).equals(UIARoles.UIAText) 
				&& w.get(UIATags.UIAIsControlElement)
				&& w.get(Tags.Title, "empty title").length() == 1) 
			return true;

		// Telefoonnummer UIASpinners are clickable and typeable
		if(w.get(Tags.Role, Roles.Widget).equals(UIARoles.UIASpinner) 
				&& w.get(Tags.Title, "").isBlank()) {
			return true;
		}

		return super.isClickable(w);
	}

	private boolean isSonOfPdfDocument(Widget w) {
		if(w.parent() == null) return false;
		else if (w.parent().get(Tags.Role, Roles.Widget).equals(UIARoles.UIADocument)
				&& w.get(Tags.Title, "").contains(".pdf")) return true;
		else return isSonOfPdfDocument(w.parent());
	}

	@Override
	protected Set<Action> preSelectAction(SUT system, State state, Set<Action> actions){
		if(actions.isEmpty()) {
			// Instead of retry derive actions, return a NOP action
			Action nopAction = new NOP();
			nopAction.set(Tags.Desc, "NOP action");
			buildEnvironmentActionIdentifiers(state, nopAction);
			actions = new HashSet<>(Collections.singletonList(nopAction));
		}
		return super.preSelectAction(system, state, actions);
	}

	/**
	 * This method allow users to customize the Widget and State identifiers.
	 *
	 * By default TESTAR uses the CodingManager to create the Widget and State identifiers:
	 * ConcreteID, AbstractID,
	 * Abstract_R_ID, Abstract_R_T_ID, Abstract_R_T_P_ID
	 *
	 * @param state
	 */
	@Override
	protected void buildStateIdentifiers(State state) {
		super.buildStateIdentifiers(state);
	}

	/**
	 * This method allow users to customize the Actions identifiers.
	 *
	 * By default TESTAR uses the CodingManager to create the Actions identifiers:
	 * ConcreteID, AbstractID
	 *
	 * @param state
	 * @param actions
	 */
	@Override
	protected void buildStateActionsIdentifiers(State state, Set<Action> actions) { super.buildStateActionsIdentifiers(state, actions); }

	/**
	 * This method allow users to customize the environment Action identifiers.
	 * These are Actions not related to a Widget (ForceToForeground, Keyboard, KillProcess, etc...)
	 *
	 * By default TESTAR uses the CodingManager to create the specific environment Action identifiers:
	 * ConcreteID, AbstractID
	 *
	 * @param state
	 * @param action
	 */
	@Override
	protected void buildEnvironmentActionIdentifiers(State state, Action action) { super.buildEnvironmentActionIdentifiers(state, action); }
}
