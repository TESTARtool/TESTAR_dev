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

import com.google.common.collect.ArrayListMultimap;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testar.SutVisualization;
import org.testar.managers.InputDataManager;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Pair;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.NOP;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.actions.WdFillFormAction;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.alayer.exceptions.SystemStartException;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.protocols.WebdriverProtocol;
import org.testar.settings.Settings;

import java.util.*;
import static org.testar.monkey.alayer.Tags.Blocked;
import static org.testar.monkey.alayer.Tags.Enabled;
import static org.testar.monkey.alayer.webdriver.Constants.scrollArrowSize;
import static org.testar.monkey.alayer.webdriver.Constants.scrollThick;


public class Protocol_webdriver_mendix_academy extends WebdriverProtocol {

	// This list tracks the detected erroneous verdicts to avoid duplicates
	private List<String> listOfDetectedErroneousVerdicts = new ArrayList<>();

	private String inputDataFile = System.getProperty("user.dir") + "/settings/custom_input_data.txt";

	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 *
	 * @param settings the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings) {
		super.initialize(settings);

		// Reset the list when we start a new TESTAR run with multiple sequences
		listOfDetectedErroneousVerdicts = new ArrayList<>();
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

		// Go to login page
		WdDriver.executeScript("document.getElementById('mx-dock-login')?.click();");
		Util.pause(5);

		// Script login sequence
		WdDriver.getRemoteWebDriver().findElement(new By.ById("input-username")).sendKeys(System.getenv("MENDIX_USERNAME"));
		Util.pause(1);
		WdDriver.executeScript("document.getElementById('input-password').setAttribute('value','" + System.getenv("MENDIX_PASSWORD") + "');");
		Util.pause(1);
		WdDriver.executeScript("document.getElementById('login-button').click();");
		Util.pause(5);

		// Deny cookies
		WdDriver.executeScript("Array.from(document.querySelector('div#usercentrics-root').shadowRoot.querySelectorAll('button')).find(button => button.textContent.trim() === 'Deny')?.click();");
		Util.pause(5);

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

		// If the Verdict is not OK but was already detected in a previous sequence
		// Consider as OK to avoid duplicates and continue testing
		if (verdict != Verdict.OK && containsVerdictInfo(listOfDetectedErroneousVerdicts, verdict.info())) {
			// Consider as OK to continue testing
			verdict = Verdict.OK;
			webConsoleVerdict = Verdict.OK;
		} 
		// If the Verdict is not OK and was not duplicated...
		// We found an issue we need to report
		else if (verdict.severity() != Verdict.OK.severity()) {
			return verdict;
		}

		//-----------------------------------------------------------------------------
		// MORE SOPHISTICATED ORACLES CAN BE PROGRAMMED HERE (the sky is the limit ;-)
		//-----------------------------------------------------------------------------

		// ... YOU MAY WANT TO CHECK YOUR CUSTOM ORACLES HERE ...

		Verdict customVerdict = Verdict.OK;
		/*

		customVerdict = customVerdict.join(customVerdictImplementation(state));

		// If the Custom Verdict is not OK but was already detected in a previous sequence
		// Consider as OK to avoid duplicates
		if (customVerdict != Verdict.OK && containsVerdictInfo(listOfDetectedErroneousVerdicts, customVerdict.info())) {
			customVerdict = Verdict.OK;
		}

		 */

		return customVerdict;
	}

	private boolean containsVerdictInfo(List<String> listOfDetectedErroneousVerdicts, String currentVerdictInfo) {
		return listOfDetectedErroneousVerdicts.stream().anyMatch(verdictInfo -> verdictInfo.contains(currentVerdictInfo.replace("\n", " ")));
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
					filteredActions.add(ac.pasteTextInto(widget, InputDataManager.getRandomTextFromCustomInputDataFile(inputDataFile), true));
				} else {
					filteredActions.add(ac.leftClickAt(widget));
				}
				continue;
			}

			// slides can happen, even though the widget might be blocked
			// addSlidingActions(actions, ac, scrollArrowSize, scrollThick, widget);

			// If the element is blocked, Testar can't click on or type in the widget
			if (widget.get(Blocked, false) && !widget.get(WdTags.WebIsShadow, false)) {
				continue;
			}

			// type into text boxes
			if (isAtBrowserCanvas(widget) && isTypeable(widget)) {
				if(whiteListed(widget) || isUnfiltered(widget)){
					actions.add(ac.pasteTextInto(widget, InputDataManager.getRandomTextFromCustomInputDataFile(inputDataFile), true));
				}else{
					// filtered and not white listed:
					filteredActions.add(ac.pasteTextInto(widget, InputDataManager.getRandomTextFromCustomInputDataFile(inputDataFile), true));
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
		// Some header classes can be clickable
		// These are interesting widgets because contain action description information
		if(widget.get(Tags.Role, Roles.Widget).equals(WdRoles.WdH2)
				&& widget.get(WdTags.WebCssClasses).contains("pds-heading")
				&& isSonOfList(widget)){
			return true;
		}

		return super.isClickable(widget);
	}

	private boolean isSonOfList(Widget widget) {
		if(widget.parent() == null) return false;
		else if (widget.parent().get(Tags.Role, Roles.Widget).equals(WdRoles.WdLI)) return true;
		else return isSonOfList(widget.parent());
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
		return super.selectAction(state, actions);
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
		// If the final Verdict is not OK and the verdict is not saved in the list
		// This is a new run fail verdict
		Verdict finalVerdict = getVerdict(latestState);
		if(finalVerdict.severity() > Verdict.Severity.OK.getValue() 
				&& !listOfDetectedErroneousVerdicts.contains(finalVerdict.info().replace("\n", " "))) {
			listOfDetectedErroneousVerdicts.add(finalVerdict.info().replace("\n", " "));
		}
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
