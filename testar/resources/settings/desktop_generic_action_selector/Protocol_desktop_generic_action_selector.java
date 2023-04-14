/***************************************************************************************************
 *
 * Copyright (c) 2013 - 2023 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 - 2023 Open Universiteit - www.ou.nl
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

import java.util.Set;

import org.testar.*;
import org.testar.monkey.Settings;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.protocols.DesktopProtocol;
import org.testar.simplestategraph.GuiStateGraphWithVisitedActions;

/**
 * This protocol provides additional implementation to improve TESTAR action selection mechanism. 
 */
public class Protocol_desktop_generic_action_selector extends DesktopProtocol {

	private ActionSelectorProxy selector;

	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 * @param   settings  the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings){
		super.initialize(settings);

		/**
		 * Compare the descriptions of the actions in the current and previous state, 
		 * to prioritize the selection of new actions in the current state. 
		 */
		// selector = new PrioritizeNewActionsSelector();

		/**
		 *  Initialize a simple GUI state graph for Q-learning. 
		 *  This implementation uses AbstractIDCustom abstraction to identify states and actions. 
		 */
		// selector = new QLearningActionSelector(settings.get(ConfigTags.MaxReward), settings.get(ConfigTags.Discount));

		/**
		 * Initialize a simple in-memory state model to prioritize the unvisited actions along the run. 
		 * This implementation uses AbstractIDCustom abstraction to identify states and actions. 
		 */
		selector = new ActionSelectorProxy(new GuiStateGraphWithVisitedActions());
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

		//The super method returns a ONLY actions for killing unwanted processes if needed, or bringing the SUT to
		//the foreground. You should add all other actions here yourself.
		// These "special" actions are prioritized over the normal GUI actions in selectAction() / preSelectAction().
		Set<Action> actions = super.deriveActions(system,state);

		// Derive left-click actions, click and type actions, and scroll actions from
		// top level widgets of the GUI:
		DerivedActions derived = deriveClickTypeScrollActionsFromTopLevelWidgets(actions, state);

		if(actions.isEmpty()){
			// If the top level widgets did not have any executable widgets, try all widgets:
			// System.out.println("No actions from top level widgets, changing to all widgets.");
			// Derive left-click actions, click and type actions, and scroll actions from
			// all widgets of the GUI:
			derived = deriveClickTypeScrollActionsFromAllWidgets(actions, state);
		}

		// Generate mode visualization purposes (Shift + Up)
		actions = selector.deriveActions(actions);

		//return the set of derived actions
		return actions;
	}

	/**
	 * Select one of the available actions using an action selection algorithm (for example random action selection)
	 *
	 * super.selectAction(state, actions) updates information to the HTML sequence report
	 *
	 * @param state the SUT's current state
	 * @param actions the set of derived actions
	 * @return  the selected action (non-null!)
	 */
	@Override
	protected Action selectAction(State state, Set<Action> actions){
		// Use the desired action selector
		Action action = selector.selectAction(state, actions);
		// If no action is available with the selector, use the state model or a random selection
		if(action == null)
		{
			action = super.selectAction(state, actions);
		}
		return action;
	}

	/**
	 * Execute the selected action.
	 *
	 * super.executeAction(system, state, action) is updating the HTML sequence report with selected action
	 *
	 * @param system the SUT
	 * @param state the SUT's current state
	 * @param action the action to execute
	 * @return whether or not the execution succeeded
	 */
	@Override
	protected boolean executeAction(SUT system, State state, Action action){
		selector.executeAction(action);
		return super.executeAction(system, state, action);
	}
}
