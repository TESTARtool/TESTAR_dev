/***************************************************************************************************
 *
 * Copyright (c) 2023 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2023 Open Universiteit - www.ou.nl
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

import org.testar.DerivedActions;
import org.testar.SutVisualization;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Settings;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.protocols.DesktopProtocol;
import org.testar.statemodel.StateModelTags;

/**
 * This protocol provides default TESTAR behaviour to test Windows desktop applications.
 *
 * It uses random action selection algorithm.
 */
public class Protocol_desktop_listening extends DesktopProtocol {

	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 * @param   settings  the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings){
		//Set before initialize StateModel
		settings.set(ConfigTags.ListeningMode, true);
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
		// top level widgets of the GUI:
		DerivedActions derived = deriveClickTypeScrollActionsFromTopLevelWidgets(actions, state);

		if(derived.getAvailableActions().isEmpty()){
			// If the top level widgets did not have any executable widgets, try all widgets:
			// Derive left-click actions, click and type actions, and scroll actions from
			// all widgets of the GUI:
			derived = deriveClickTypeScrollActionsFromAllWidgets(actions, state);
		}

		Set<Action> filteredActions = derived.getFilteredActions();
		actions = derived.getAvailableActions();

		//Showing the grey dots for filtered actions if visualization is on:
		if(visualizationOn || mode() == Modes.Spy) SutVisualization.visualizeFilteredActions(cv, state, filteredActions);

		//return the set of derived actions
		return actions;
	}

	/**
	 * Select one of the available actions using an action selection algorithm. 
	 * 
	 * It uses the state model action selector if the state model inference settings are configured and enabled. 
	 * If the state model is not enabled, it returns a random action. 
	 * 
	 * super.selectAction(state, actions) also updates the HTML sequence report information. 
	 *
	 * @param state the SUT's current state
	 * @param actions the set of derived actions
	 * @return  the selected action (non-null!)
	 */
	@Override
	protected Action selectAction(State state, Set<Action> actions){
		Action retAction = interestingActions(actions);

		if(retAction == null) return super.selectAction(state, actions);

		return retAction;
	}

	/**
	 * Select one Action randomly but applying Learning Mode Rewards to Increase the possibility to be selected.
	 * 
	 * @param actions
	 * @return Action
	 */
	public Action interestingActions(Set<Action> actions) {
		Set<Action> interestingModelActions = stateModelManager.getInterestingActions(actions);

		Action highAction = null;

		if(!interestingModelActions.isEmpty()) {
			System.out.println("\n ******* These interesting Actions come from State Model");
			for(Action a : interestingModelActions) {
				System.out.println("Description: " + a.get(Tags.Desc, "No description"));
				System.out.println("User Interest: " +a.get(StateModelTags.UserInterest, 0));
				highAction = a;
			}
			System.out.println("******* END of interesting Actions \n");
		}

		return highAction;
	}


	/**
	 * This method allow users to customize the Widget and State identifiers.
	 *
	 * By default TESTAR uses the CodingManager to create the Widget and State identifiers:
	 * ConcreteID, ConcreteIDCustom, AbstractID, AbstractIDCustom,
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
	 * ConcreteID, ConcreteIDCustom, AbstractID, AbstractIDCustom
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
	 * ConcreteID, ConcreteIDCustom, AbstractID, AbstractIDCustom
	 *
	 * @param state
	 * @param action
	 */
	@Override
	protected void buildEnvironmentActionIdentifiers(State state, Action action) { super.buildEnvironmentActionIdentifiers(state, action); }
}
