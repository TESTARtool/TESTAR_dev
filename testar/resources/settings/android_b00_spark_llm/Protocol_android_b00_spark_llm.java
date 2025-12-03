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
import org.testar.action.priorization.llm.LlmActionSelector;
import org.testar.llm.LlmTestGoal;
import org.testar.llm.prompt.OracleAndroidPromptGenerator;
import org.testar.llm.prompt.ActionStandardPromptGenerator;
import org.testar.managers.InputDataManager;
import org.testar.settings.Settings;
import org.testar.statemodel.StateModelManagerFactory;
import org.testar.statemodel.analysis.condition.BasicConditionEvaluator;
import org.testar.statemodel.analysis.condition.ConditionEvaluator;
import org.testar.statemodel.analysis.condition.GherkinConditionEvaluator;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.NOP;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.exceptions.*;
import org.testar.monkey.alayer.webdriver.Constants;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.oracles.llm.LlmOracle;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Main;
import org.testar.monkey.alayer.android.actions.*;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import org.testar.protocols.AndroidProtocol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Protocol_android_b00_spark_llm extends AndroidProtocol {

	// The LLM Action selector needs to be initialize with the settings
	private LlmActionSelector llmActionSelector;

	private List<LlmTestGoal> testGoals = new ArrayList<>();
	private Queue<LlmTestGoal> testGoalQueue;
	private LlmTestGoal currentTestGoal;

	// The LLM Oracle needs to be initialize with the settings
	private LlmOracle llmOracle;

	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 * @param   settings  the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings) {
		super.initialize(settings);

		// Configure the test goals
		setupTestGoals(settings.get(ConfigTags.LlmTestGoals));

		// Initialize the LlmActionSelector using the LLM settings
		llmActionSelector = new LlmActionSelector(settings, new ActionStandardPromptGenerator(AndroidTags.AndroidText, true));

		// Initialize the LlmOracle using the LLM settings
		llmOracle = new LlmOracle(settings, new OracleAndroidPromptGenerator(true));
	}

	private void setupTestGoals(List<String> testGoalsList) {
		for(String testGoal : testGoalsList) {
			// Empty BasicConditionEvaluator because the test goal decision is based on an LLM
			testGoals.add(new LlmTestGoal(testGoal, new BasicConditionEvaluator().getConditions()));
		}
	}

	/**
	 * This methods is called before each test sequence, allowing for example using external profiling software on the SUT
	 */
	@Override
	protected void preSequencePreparations() {
		super.preSequencePreparations();

		// Setup test goal queue
		testGoalQueue = new LinkedList<>();
		testGoalQueue.addAll(testGoals);
		currentTestGoal = testGoalQueue.poll();

		// Reset llm action selector
		llmActionSelector.reset(currentTestGoal, false);
		// Reset llm oracle
		llmOracle.reset(currentTestGoal, false);
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
	 * This method is called when TESTAR starts the System Under Test (SUT). The method should
	 * take care of
	 * 1) starting the SUT (you can use TESTAR's settings obtainable from <code>settings()</code> to find
	 * out what executable to run)
	 * 2) bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
	 * the SUT's configuratio files etc.)
	 * 3) waiting until the system is fully loaded and ready to be tested (with large systems, you might have to wait several
	 * seconds until they have finished loading)
	 *
	 * @return a started SUT, ready to be tested.
	 */
	@Override
	protected SUT startSystem() throws SystemStartException {
		SUT system = super.startSystem();
		return system;
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

		// The autoGrantPermissions option must be enabled in the DesiredCapabilities json file

		// Type username
		waitLeftClickAndTypeIntoWidgetWithMatchingTag(AndroidTags.AndroidHint, "username", "testar", state, system, 5, 2);
		// Type password
		waitLeftClickAndTypeIntoWidgetWithMatchingTag(AndroidTags.AndroidHint, "••••", "testar", state, system, 5, 2);
		// Click Sign In
		waitAndLeftClickWidgetWithMatchingTag(AndroidTags.AndroidText, "Submit", state, system, 5, 2);
	}

	// Override trigger click action logic for Android login
	@Override
	protected Action triggeredClickAction(State state, Widget widget) {
		return new AndroidActionClick(state, widget);
	}

	// Override trigger type action logic for Android login
	@Override
	protected Action triggeredTypeAction(State state, Widget widget, String textToType, boolean replaceText) {
		return new AndroidActionType(state, widget, textToType);
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
		return state;
	}

	/**
	 * This is a helper method used by the default implementation of <code>buildState()</code>
	 * It examines the SUT's current state and returns an oracle verdict.
	 *
	 * @return oracle verdict, which determines whether the state is erroneous and why.
	 */
	@Override
	protected Verdict getVerdict(State state) {
		// System crashes, non-responsiveness and suspicious tags automatically detected!
		// For web applications, web browser errors and warnings can also be enabled via settings
		Verdict verdict = super.getVerdict(state);

		if(actionCount > 1) {
			// If the technical condition evaluator determines the goal has not been achieved
			// Use the LLM as an Oracle to determine if the test goal has been completed
			Verdict llmVerdict = llmOracle.getVerdict(state);

			if(llmVerdict.severity() == Verdict.Severity.LLM_COMPLETE.getValue()) {
				// Test goal was completed, retrieve next test goal from queue.
				currentTestGoal = testGoalQueue.poll();

				// Poll returns null if there are no more items remaining in the queue.
				if(currentTestGoal == null) {
					// No more test goals remaining, terminate sequence.
					System.out.println("Test goal completed, but no more test goals.");
					return llmVerdict;
				} else {
					System.out.println("Test goal completed, moving to next test goal.");
					llmActionSelector.reset(currentTestGoal, true);
					llmOracle.reset(currentTestGoal, true);
				}
			}
		}

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

		// iterate through all widgets
		for (Widget widget : state) {

			// type into text boxes
			if (isTypeable(widget) && (whiteListed(widget) || isUnfiltered(widget))) {
				String randomInput = InputDataManager.getRandomTextInputData(widget);
				actions.add(new AndroidActionType(state, widget, randomInput));
			}

			// left clicks, but ignore links outside domain
			if (isClickable(widget) && (whiteListed(widget) || isUnfiltered(widget))) {
				actions.add(new AndroidActionClick(state, widget));
			}
		}

		return actions;
	}

	@Override
	protected boolean isClickable(Widget w) {
		// TODO: These element should contain the clickable property by default
		if(w.get(AndroidTags.AndroidClassName, "").contains("android.widget.TextView")) {
			w.set(AndroidTags.AndroidClickable, true);
			return true;
		}

		return super.isClickable(w);
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
		Action toExecute = llmActionSelector.selectAction(state, actions);

		// We need to set a state to NOP actions
		if(toExecute instanceof NOP) {
			toExecute.set(Tags.OriginWidget, state);
		}

		// We need the AbstractID for the state model
		if(toExecute.get(Tags.AbstractID, null) == null) {
			CodingManager.buildIDs(state, Collections.singleton(toExecute));
		}

		return toExecute;
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
		return super.executeAction(system, state, action);
	}

	/**
	 * TESTAR uses this method to determine when to stop the generation of actions for the
	 * current sequence. You can stop deriving more actions after:
	 * - a specified amount of executed actions, which is specified through the SequenceLength setting, or
	 * - after a specific time, that is set in the MaxTime setting
	 * @return  if <code>true</code> continue generation, else stop
	 */
	@Override
	protected boolean moreActions(State state) {
		return super.moreActions(state);
	}

	/**
	 * TESTAR uses this method to determine when to stop the entire test sequence
	 * You could stop the test after:
	 * - a specified amount of sequences, which is specified through the Sequences setting, or
	 * - after a specific time, that is set in the MaxTime setting
	 * @return  if <code>true</code> continue test, else stop
	 */
	@Override
	protected boolean moreSequences() {
		return super.moreSequences();
	}

	/**
	 * Here you can put graceful shutdown sequence for your SUT
	 * @param system
	 */
	@Override
	protected void stopSystem(SUT system) {
		super.stopSystem(system);
	}

	/**
	 * This methods is called after each test sequence, allowing for example using external profiling software on the SUT
	 *
	 * super.postSequenceProcessing() is adding test verdict into the HTML sequence report
	 */
	@Override
	protected void postSequenceProcessing() {
		super.postSequenceProcessing();
	}

}
