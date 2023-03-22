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

import org.testar.IActionDerive;
import org.testar.IActionExecutor;
import org.testar.IActionSelector;
import org.testar.PrioritizeNewActionsSelector;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Settings;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.protocols.DesktopProtocol;
import org.testar.simplestategraph.GuiStateGraphWithVisitedActions;
import org.testar.simplestategraph.QLearningActionSelector;

/**
 * This protocol provides default TESTAR behaviour to test Windows desktop applications.
 *
 * It uses random action selection algorithm.
 */
public class Protocol_desktop_generic_action_selector extends DesktopProtocol {


	private IActionSelector selector;

	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 * @param   settings  the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings){
		// initializing simple GUI state graph for Q-learning:
		// this implementation uses concreteStateID for state abstraction, so it may find too many states:

//		selector = new PrioritizeNewActionsSelector();
//		selector = new QLearningActionSelector(settings.get(ConfigTags.MaxReward),settings.get(ConfigTags.Discount));
		selector = new GuiStateGraphWithVisitedActions();

		super.initialize(settings);
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
		// top level (highest Z-index) widgets of the GUI:
		actions = deriveClickTypeScrollActionsFromTopLevelWidgets(actions, system, state);

		if(actions.isEmpty()){
			// If the top level widgets did not have any executable widgets, try all widgets:
//			System.out.println("No actions from top level widgets, changing to all widgets.");
			// Derive left-click actions, click and type actions, and scroll actions from
			// all widgets of the GUI:
			actions = deriveClickTypeScrollActionsFromAllWidgetsOfState(actions, system, state);
		}
		
		// Generate mode visualization purposes (Shift + Up)
		if(selector instanceof IActionDerive) {
			actions = ((IActionDerive) selector).deriveActions(actions);
		}

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
		Action action = selector.selectAction(state, actions);
		if(action == null)
			action = super.selectAction(state, actions);
		return(action);
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
		if(selector instanceof IActionExecutor)
		{
			((IActionExecutor) selector).executeAction(action);
		}
		/*System.out.println("Executed action: " + action.get(Tags.Desc, "NoCurrentDescAvailable")
		+ " -- Times executed: " + selector.timesExecuted(action));*/
		return super.executeAction(system, state, action);
	}
}
