/**
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2019 - 2025 Universitat Politecnica de Valencia - www.upv.es
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

import org.openqa.selenium.By;
import org.testar.CodingManager;
import org.testar.SutVisualization;
import org.testar.action.priorization.llm.LlmActionSelector;
import org.testar.llm.LlmTestGoal;
import org.testar.llm.prompt.OracleWebPromptGenerator;
import org.testar.llm.prompt.OracleImagePromptGenerator;
import org.testar.llm.prompt.ActionWebPromptGenerator;
import org.testar.managers.InputDataManager;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.NOP;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.alayer.exceptions.SystemStartException;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.oracles.llm.LlmOracle;
import org.testar.protocols.WebdriverProtocol;
import org.testar.settings.Settings;
import org.testar.statemodel.analysis.condition.BasicConditionEvaluator;
import java.util.*;
import static org.testar.monkey.alayer.Tags.Blocked;
import static org.testar.monkey.alayer.Tags.Enabled;

public class Protocol_webdriver_newspark_drawnames_llm extends WebdriverProtocol {

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
	 *
	 * @param settings the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings) {

		super.initialize(settings);

		// Configure the test goals
		setupTestGoals(settings.get(ConfigTags.LlmTestGoals));

		// Initialize the LlmActionSelector using the LLM settings
		llmActionSelector = new LlmActionSelector(settings, new ActionWebPromptGenerator());

		// Initialize the LlmOracle using the LLM settings
		llmOracle = new LlmOracle(settings, new OracleImagePromptGenerator());
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
	protected void beginSequence(SUT system, State state) {
		super.beginSequence(system, state);
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

		for (Widget widget : state) {
		    if(widget.get(Tags.Role, Roles.Widget).equals(WdRoles.WdSELECT)) {
		        widget.set(Tags.Desc, widget.get(Tags.Desc).concat(siblingDesc(widget)));
		    }
		}

		return state;
	}

	private String siblingDesc(Widget widget) {
	    if (widget == null) return "";
	    Widget parent = widget.parent();
	    if (parent == null) return "";

	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < parent.childCount(); i++) {
	        Widget child = parent.child(i);
	        if (child == null || child == widget) continue;

	        String desc = child.get(Tags.Desc, "");
	        if (!desc.isEmpty()) {
	            sb.append(": ").append(desc);
	        }
	    }
	    return sb.toString();
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

		return verdict;
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
			// The blackListed widgets are those that have been filtered during the SPY mode with the
			//CAPS_LOCK + SHIFT + Click clickfilter functionality.
			if(blackListed(widget)){
				if(isTypeable(widget)){
					filteredActions.add(ac.clickTypeInto(widget, InputDataManager.getRandomTextInputData(), true));
				} else {
					filteredActions.add(ac.leftClickAt(widget));
				}
				continue;
			}

			// slides can happen, even though the widget might be blocked
			// addSlidingActions(actions, ac, scrollArrowSize, scrollThick, widget);

			// type into text boxes
			if (isAtBrowserCanvas(widget) && isTypeable(widget)) {
				if(whiteListed(widget) || isUnfiltered(widget)){
					actions.add(ac.clickTypeInto(widget, InputDataManager.getRandomTextInputData(), true));
				}else{
					// filtered and not white listed:
					filteredActions.add(ac.clickTypeInto(widget, InputDataManager.getRandomTextInputData(), true));
				}
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
	protected boolean isClickable(Widget widget) {
		// If the element is blocked, Testar can't click on or type in the widget
		if (widget.get(Blocked, false) && !widget.get(WdTags.WebIsShadow, false)) {
			return false;
		}

		// Top right menu button
		if(widget.get(WdTags.WebCssClasses, "").contains("menu-hamburger-button")) {
			return true;
		}

		// Category buttons
		if(widget.get(WdTags.WebCssClasses, "").contains("[chip]")
				|| widget.get(WdTags.WebCssClasses, "").contains("[chip, active]")) {
			return true;
		}

		// span element son of multi-select option div
		if(widget.get(Tags.Role, Roles.Widget).equals(WdRoles.WdSPAN)
				&& widget.parent() != null
				&& widget.parent().get(WdTags.WebCssClasses, "").contains("option")) {
			return true;
		}

		return super.isClickable(widget);
	}

	@Override
	protected boolean isTypeable(Widget widget) {
		// If the element is blocked, Testar can't click on or type in the widget
		if (widget.get(Blocked, false) && !widget.get(WdTags.WebIsShadow, false)) {
			return false;
		}

		return super.isTypeable(widget);
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
	 * @param system the SUT
	 * @param state  the SUT's current state
	 * @param action the action to execute
	 * @return whether or not the execution succeeded
	 */
	@Override
	protected boolean executeAction(SUT system, State state, Action action) {
		return super.executeAction(system, state, action);
	}

	/**
	 * TESTAR uses this method to determine when to stop the generation of actions for the
	 * current sequence. You could stop the sequence's generation after a given amount of executed
	 * actions or after a specific time etc.
	 *
	 * @return if <code>true</code> continue generation, else stop
	 */
	@Override
	protected boolean moreActions(State state) {
		return super.moreActions(state);
	}

	/**
	 * This method is invoked each time after TESTAR finished the generation of a sequence.
	 */
	@Override
	protected void finishSequence() {
		super.finishSequence();
	}

	/**
	 * TESTAR uses this method to determine when to stop the entire test.
	 * You could stop the test after a given amount of generated sequences or
	 * after a specific time etc.
	 *
	 * @return if <code>true</code> continue test, else stop
	 */
	@Override
	protected boolean moreSequences() {
		return super.moreSequences();
	}

}
