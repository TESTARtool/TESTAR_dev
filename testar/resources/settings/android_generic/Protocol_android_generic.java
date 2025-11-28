/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 - 2025 Open Universiteit - www.ou.nl
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

import org.testar.CodingManager;
import org.testar.RandomActionSelector;
import org.testar.managers.InputDataManager;
import org.testar.screenshotjson.JsonUtils;
import org.testar.settings.Settings;
import org.testar.simplestategraph.QLearningActionSelector;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.exceptions.*;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.alayer.android.actions.*;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import org.testar.protocols.AndroidProtocol;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;

public class Protocol_android_generic extends AndroidProtocol {
	int counter = 0;

	// Initialization for Qlearning action selector
	private QLearningActionSelector actionSelector;

	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 * @param   settings  the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings){
		// initializing simple GUI state graph for Q-learning:
		actionSelector = new QLearningActionSelector(settings.get(ConfigTags.MaxReward),settings.get(ConfigTags.Discount));
		super.initialize(settings);
	}

	/**
	 * Use CodingManager to create the Widget and State identifiers:
	 * ConcreteID, AbstractID,
	 * Abstract_R_ID, Abstract_R_T_ID, Abstract_R_T_P_ID
	 *
	 * @param state
	 */
	@Override
	protected void buildStateIdentifiers(State state) {
		// By default TESTAR invokes CodingManager,
		// but here you can change the way we define the identifiers of widgets and actions
		CodingManager.buildIDs(state);
	}

	/**
	 * Use CodingManager to create the Actions identifiers:
	 * ConcreteID, AbstractID
	 *
	 * @param state
	 * @param actions
	 */
	@Override
	protected void buildStateActionsIdentifiers(State state, Set<Action> actions) {
		// By default TESTAR invokes CodingManager,
		// but here you can change the way we define the identifiers of widgets and actions
		CodingManager.buildIDs(state, actions);
	}

	/**
	 * Use CodingManager to create the specific environment Action identifiers:
	 * ConcreteID, AbstractID
	 *
	 * @param state
	 * @param action
	 */
	@Override
	protected void buildEnvironmentActionIdentifiers(State state, Action action) {
		// By default TESTAR invokes CodingManager,
		// but here you can change the way we define the identifiers of widgets and actions
		CodingManager.buildEnvironmentActionIDs(state, action);
	}

	/**
	 * This method is invoked each time the TESTAR starts the SUT to generate a new sequence.
	 * This can be used for example for bypassing a login screen by filling the username and password
	 * or bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
	 * the SUT's configuration files etc.)
	 */
	@Override
	protected void beginSequence(SUT system, State state){
		super.beginSequence(system, state);
	}

	/**
	 * This method is called when the TESTAR requests the state of the SUT.
	 * Here you can add additional information to the SUT's state or write your
	 * own state fetching routine. The state should have attached an oracle
	 * (TagName: <code>Tags.OracleVerdict</code>) which describes whether the
	 * state is erroneous and if so why.
	 * @return  the current state of the SUT with attached oracle.
	 */
	@Override
	protected State getState(SUT system) throws StateBuildException {
		State state = super.getState(system);

		//TODO: Change to StateId JSON instead of the incremental counter
		// Creates a JSON file that allows debugging the information of the android widgets
		/*
	 	try {
	 		counter += 1;
	 		String debugWidgetTreePath = "output" + File.separator + "android_state_debug";
	 		Files.createDirectories(Paths.get(debugWidgetTreePath));
	 		String filePath = debugWidgetTreePath + File.separator + "tempJsonFile" + counter +".json";
	 		JsonUtils.createFullWidgetTreeJsonFile(state, filePath);
	 	} catch (IOException e) {
	 		e.printStackTrace();
	 	}
		 */

		return state;
	}

	/**
	 * The getVerdict methods implements the online state oracles that
	 * examine the SUT's current state and returns an oracle verdict.
	 * @return oracle verdict, which determines whether the state is erroneous and why.
	 */
	@Override
	protected Verdict getVerdict(State state){
		// The super methods implements the implicit online state oracles for:
		// system crashes
		// non-responsiveness
		// suspicious tags
		Verdict verdict = super.getVerdict(state);

		//--------------------------------------------------------
		// MORE SOPHISTICATED STATE ORACLES CAN BE PROGRAMMED HERE
		//--------------------------------------------------------

		return verdict;
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
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException{
		//The super method returns a ONLY actions for killing unwanted processes if needed, or bringing the SUT to
		//the foreground. You should add all other actions here yourself.
		// These "special" actions are prioritized over the normal GUI actions in selectAction() / preSelectAction().
		Set<Action> actions = super.deriveActions(system,state);

		// create an action compiler, which helps us create actions
		// such as clicks, drag&drop, typing ...
		StdActionCompiler ac = new AnnotatingActionCompiler();

		// boolean which ensures only the first encountered widget will get scroll action.
		boolean oneScroll = true;

		// iterate through all widgets
		for (Widget widget : state) {

			//TODO need to find a way to dynamically get the package.

			// Ignore widgets that are not whitelisted or that are filtred
			if(!(whiteListed(widget) || isUnfiltered(widget))) {
				continue;
			}

			// type into text boxes
			if (isTypeable(widget)) {
				String randomInput = InputDataManager.getRandomTextInputData(widget);
				actions.add(new AndroidActionType(state, widget, randomInput));
			}

			// left clicks
			if (isClickable(widget)) {
				actions.add(new AndroidActionClick(state, widget));
			}

			// Scroll action
			if (oneScroll) {
				if (isScrollable(widget)) {
					actions.add(new AndroidActionScroll(state, widget));
					oneScroll = false;
				}
			}

			if (isLongClickable(widget)) {
				actions.add(new AndroidActionLongClick(state, widget));
			}
		}

		// Add system calls
		// Workaround to pass a Android widget to the systemActions, otherwise will complain about not being able to set
		// Tags. Addtionally creating an SystemAction interface will not work as the returned type must be Action.
		Widget topWidget = state.root().child(0);
		Boolean checkAddSystemActions = settings.get(ConfigTags.UseSystemActions, false);

		if (checkAddSystemActions) {
			actions.add(
					//System orientation swap
					new AndroidSystemActionOrientation(state, topWidget)
					);

			actions.add(
					//Receive a call
					new AndroidSystemActionCall(state, topWidget)
					);

			actions.add(
					//Receive text message
					new AndroidSystemActionText(state, topWidget)
					);
		}

		return actions;
	}

	/**
	 * Select one of the available actions using an action selection algorithm (for example random action selection)
	 *
	 * @param state the SUT's current state
	 * @param actions the set of derived actions
	 * @return  the selected action (non-null!)
	 */
	@Override
	protected Action selectAction(State state, Set<Action> actions){
		return super.selectAction(state, actions);

		// Uncomment the next line to use the Qlearning action selector
		//System.out.println("Q-learning action selector");
		//retAction = actionSelector.selectAction(state,actions);
	}

}
