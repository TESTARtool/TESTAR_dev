/**
 * Copyright (c) 2024 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2024 - 2025 Universitat Politecnica de Valencia - www.upv.es
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

import org.testar.CodingManager;
import org.testar.SutVisualization;
import org.testar.action.priorization.llm.LlmActionSelector;
import org.testar.llm.LlmTestGoal;
import org.testar.llm.prompt.ActionWebPromptGenerator;
import org.testar.managers.InputDataManager;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.*;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.alayer.exceptions.SystemStartException;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.plugin.NativeLinker;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Main;
import org.testar.monkey.Util;
import org.testar.protocols.WebdriverProtocol;
import org.testar.settings.Settings;
import org.testar.statemodel.StateModelManagerFactory;
import org.testar.statemodel.analysis.condition.BasicConditionEvaluator;
import org.testar.statemodel.analysis.condition.ConditionEvaluator;
import org.testar.statemodel.analysis.condition.CheckConditionEvaluator;
import org.testar.statemodel.analysis.metric.LlmMetricsCollector;
import org.testar.statemodel.analysis.metric.MetricsManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.testar.monkey.alayer.Tags.Blocked;
import static org.testar.monkey.alayer.Tags.Enabled;
import static org.testar.monkey.alayer.webdriver.Constants.scrollArrowSize;
import static org.testar.monkey.alayer.webdriver.Constants.scrollThick;

public class Protocol_webdriver_llm_state_model_evaluator extends WebdriverProtocol {
	// The LLM Action selector needs to be initialize with the settings
	private LlmActionSelector llmActionSelector;
	private MetricsManager metricsManager;
	private ConditionEvaluator conditionEvaluator;

	private List<LlmTestGoal> testGoals = new ArrayList<>();
	private Queue<LlmTestGoal> testGoalQueue;
	private LlmTestGoal currentTestGoal;

	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 *
	 * @param settings the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings) {
		// Download OrientDB and initialize a testar (testar:testar) database
		setupOrientDB();

		super.initialize(settings);

		// Configure the test goals
		setupTestGoals(settings.get(ConfigTags.LlmTestGoals));

		// Initialize the LlmActionSelector using the LLM settings
		llmActionSelector = new LlmActionSelector(settings, new ActionWebPromptGenerator());

		// Initialize the metrics collector to analyze the state model
		metricsManager = new MetricsManager(new LlmMetricsCollector(testGoals));

		conditionEvaluator = new BasicConditionEvaluator();
	}

	private void setupTestGoals(List<String> testGoalsList) {
		for(String testGoal : testGoalsList) {
			CheckConditionEvaluator checkEvaluator = new CheckConditionEvaluator(WdTags.WebInnerHTML, testGoal);
			testGoals.add(new LlmTestGoal(testGoal, checkEvaluator.getConditions()));
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
		conditionEvaluator.clear();
		conditionEvaluator.addConditions(currentTestGoal.getCompletionConditions());

		// Reset llm action selector
		llmActionSelector.reset(currentTestGoal, false);

		// Use timestamp to create a unique new state model
		String appName = settings.get(ConfigTags.ApplicationName, "");
		String appVersion = settings.get(ConfigTags.ApplicationVersion, "");
		String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

		// stop and start a new state model manager
		stateModelManager.notifyTestingEnded();
		stateModelManager = StateModelManagerFactory.getStateModelManager(
				appName, (appVersion + "_" + timestamp), settings);
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
		return super.startSystem();
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

		String modelIdentifier = stateModelManager.getModelIdentifier();

		if(conditionEvaluator.evaluateConditions(modelIdentifier, stateModelManager)) {
			// Test goal was completed, retrieve next test goal from queue.
			currentTestGoal = testGoalQueue.poll();

			// Poll returns null if there are no more items remaining in the queue.
			if(currentTestGoal == null) {
				// No more test goals remaining, terminate sequence.
				System.out.println("Test goal completed, but no more test goals.");
				return new Verdict(Verdict.Severity.CONDITION_COMPLETE, "All test goal conditions completed.");
			} else {
				System.out.println("Test goal completed, moving to next test goal.");
				llmActionSelector.reset(currentTestGoal, true);
				conditionEvaluator.clear();
				conditionEvaluator.addConditions(currentTestGoal.getCompletionConditions());
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
					filteredActions.add(ac.clickTypeInto(widget, InputDataManager.getRandomTextInputData(widget), true));
				} else {
					filteredActions.add(ac.leftClickAt(widget));
				}
				continue;
			}

			// slides can happen, even though the widget might be blocked
			//addSlidingActions(actions, ac, scrollArrowSize, scrollThick, widget);

			// If the element is blocked, Testar can't click on or type in the widget
			if (widget.get(Blocked, false) && !widget.get(WdTags.WebIsShadow, false)) {
				continue;
			}

			// type into text boxes
			if (isAtBrowserCanvas(widget) && isTypeable(widget)) {
				if(whiteListed(widget) || isUnfiltered(widget)){
					// Type a random Number, Alphabetic, URL, Date or Email input
					// If the LLM agent is enabled, this input will be changed
					actions.add(ac.clickTypeInto(widget, InputDataManager.getRandomTextInputData(widget), true));
				}else{
					// filtered and not white listed:
					filteredActions.add(ac.clickTypeInto(widget, InputDataManager.getRandomTextInputData(widget), true));
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
		String modelIdentifier = stateModelManager.getModelIdentifier();
		metricsManager.collect(modelIdentifier, stateModelManager, llmActionSelector.getInvalidActions());

		// Finished final sequence
		if(sequenceCount == settings.get(ConfigTags.Sequences)) {
			metricsManager.finish();
		}

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

	private void setupOrientDB() {
		String directoryPath = Main.settingsDir + File.separator + "webdriver_llm_state_model_evaluator";
		String downloadUrl = "https://repo1.maven.org/maven2/com/orientechnologies/orientdb-community/3.2.38/orientdb-community-3.2.38.zip";
		String zipFilePath = directoryPath + "/orientdb-community-3.2.38.zip";
		String extractDir = directoryPath + "/orientdb-community-3.2.38";

		// If OrientDB already exists, we dont need to download anything
		if(new File(extractDir).exists()) return;

		try {
			// Create the directory if it doesn't exist
			File directory = new File(directoryPath);
			if (!directory.exists()) {
				directory.mkdirs();
			}

			// Download the zip file
			try (InputStream in = new URL(downloadUrl).openStream();
					FileOutputStream out = new FileOutputStream(zipFilePath)) {
				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = in.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
				}
			}

			// Extract the zip file
			try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath))) {
				ZipEntry entry;
				while ((entry = zipIn.getNextEntry()) != null) {
					File filePath = new File(directoryPath, entry.getName());
					if (entry.isDirectory()) {
						filePath.mkdirs();
					} else {
						// Ensure parent directories exist
						File parentDir = filePath.getParentFile();
						if (!parentDir.exists()) {
							parentDir.mkdirs();
						}
						try (FileOutputStream out = new FileOutputStream(filePath)) {
							byte[] buffer = new byte[4096];
							int len;
							while ((len = zipIn.read(buffer)) > 0) {
								out.write(buffer, 0, len);
							}
						}
					}
					zipIn.closeEntry();
				}
			}

			// Change to the bin directory and execute the command
			ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "console.bat", "CREATE", "DATABASE", "plocal:../databases/testar", "testar", "testar");
			processBuilder.directory(new File(extractDir + "/bin"));
			processBuilder.inheritIO();
			Process process = processBuilder.start();

			// Wait for the command to complete
			int exitCode = process.waitFor();
			if (exitCode != 0) {
				throw new RuntimeException("Command execution failed with exit code " + exitCode);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
