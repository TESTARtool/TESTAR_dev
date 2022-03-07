/**
 * 
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

import es.upv.staq.testar.CodingManager;
import es.upv.staq.testar.NativeLinker;

import org.fruit.alayer.*;
import org.fruit.alayer.actions.*;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.webdriver.*;
import org.fruit.alayer.webdriver.enums.WdRoles;
import org.fruit.alayer.webdriver.enums.WdTags;
import org.fruit.monkey.Settings;
import org.testar.distributed.SharedProtocol;

import nl.ou.testar.RandomActionSelector;
import java.util.*;
import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;

public class Protocol_webdriver_predicted_shared extends SharedProtocol {

	@Override
	protected void buildStateActionsIdentifiers(State state, Set<Action> actions) {
		CodingManager.buildIDs(state, actions);
		// Change the AbstractIDCustom of the actions to remove state dependency
		// An action over the same widget will have the same id regardless of the state it is in
		for (Action a : actions) {
			/* This new AbstractIDCustom uses: 
			 * - AbstractIDCustom of the OriginWidget calculated with the selected abstract properties (core-StateManagementTags)
			 * - The ActionRole type of this action (LeftClick, DoubleClick, ClickTypeInto, Drag, etc)
			 */
			a.set(Tags.AbstractIDCustom, CodingManager.ID_PREFIX_ACTION + CodingManager.ID_PREFIX_ABSTRACT_CUSTOM + 
					CodingManager.lowCollisionID(a.get(Tags.OriginWidget).get(Tags.AbstractIDCustom) + a.get(Tags.Role, ActionRoles.Action)));
		}
	}

	@Override
	protected void beginSequence(SUT system, State state) {
		super.beginSequence(system, state);
		moreSharedActions = true;
	}

	@Override
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {
		// Kill unwanted processes, force SUT to foreground
		Set<Action> actions = super.deriveActions(system, state);

		// create an action compiler, which helps us create actions
		// such as clicks, drag&drop, typing ...
		StdActionCompiler ac = new AnnotatingActionCompiler();

		// Check if forced actions are needed to stay within allowed domains
		Set<Action> forcedActions = detectForcedActions(state, ac);
		if (forcedActions != null && forcedActions.size() > 0) {
			return forcedActions;
		}

		// iterate through all widgets
		for (Widget widget : state) {
			// only consider enabled and non-tabu widgets
			if (!widget.get(Enabled, true) || blackListed(widget)) {
				continue;
			}

			// slides can happen, even though the widget might be blocked
			//addSlidingActions(actions, ac, scrollArrowSize, scrollThick, widget, state);

			// If the element is blocked, Testar can't click on or type in the widget
			if (widget.get(Blocked, false) && !widget.get(WdTags.WebIsShadow, false)) {
				continue;
			}

			// type into text boxes
			if (isAtBrowserCanvas(widget) && isTypeable(widget) && (whiteListed(widget) || isUnfiltered(widget))) {
				actions.add(ac.clickTypeInto(widget, this.getRandomText(widget), true));
			}

			// left clicks, but ignore links outside domain
			if (isAtBrowserCanvas(widget) && isClickable(widget) && (whiteListed(widget) || isUnfiltered(widget))) {
				if (!isLinkDenied(widget)) {
					actions.add(ac.leftClickAt(widget));
				}
			}
		}

		return actions;
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
			// Input type are special...
			if (role.equals(WdRoles.WdINPUT)) {
				String type = ((WdWidget) widget).element.type;
				return WdRoles.typeableInputTypes().contains(type);
			}
			return true;
		}

		return false;
	}

	@Override
	protected boolean moreActions(State state) {
		// Check if last traverse action leads TESTAR to the expected traverse destination state
		verifyTraversePathDeterminism(state);
		System.out.println("MoreSharedActions ? " + moreSharedActions);
		return moreSharedActions;
	}

	@Override
	protected boolean moreSequences() {
		boolean result = (countInDb("UnvisitedAbstractAction") > 0) || !stopSharedProtocol;
		System.out.println("moreSharedSequences ? " + result);
		return result;
	}

	/**
	 * Select one of the available actions using an action selection algorithm (for example random action selection)
	 *
	 * @param state the SUT's current state
	 * @param actions the set of derived actions
	 * @return  the selected action (non-null!)
	 */
	@Override
	protected Action selectAction(State state, Set<Action> actions) {
		// Call the preSelectAction method from the AbstractProtocol so that, if necessary,
		// unwanted processes are killed and SUT is put into foreground.
		Action retAction = preSelectAction(state, actions);
		if (retAction != null) { return retAction; }

		// targetSharedAction is an unvisited action
		// First check whether we do have a target shared action marked to execute; if not select one
		if (targetSharedAction == null) {
			targetSharedAction = getNewTargetSharedAction(state, actions);
		}

		if (targetSharedAction != null) {
			HashMap<String, Action> actionMap = ConvertActionSetToDictionary(actions);

			// Check if the target shared action to execute is in the current state
			if (actionMap.containsKey(targetSharedAction)) {
				System.out.println("Target Shared Action is in the current state, just select it");
				Action targetAction = getTargetActionFound(actionMap);
				return targetAction;
			} 
			// Target shared action to execute is not in the current state, calculate the path to reach our desired target action
			else {
				System.out.println("Needed Target Shared Action is unavailable, select from path to be followed");
				Action nextStepAction = traversePath(state, actions);
				return nextStepAction;
			}
		}

		System.out.println("**** Shared State Model Protocol did not find an action to select, return a random action ****");
		return RandomActionSelector.selectAction(actions);
	}

}
