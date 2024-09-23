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
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.alayer.exceptions.SystemStartException;
import org.testar.monkey.alayer.windows.UIARoles;
import org.testar.monkey.alayer.windows.UIATags;
import org.testar.protocols.DesktopProtocol;
import org.testar.screenshotjson.JsonUtils;
import org.testar.settings.Settings;

import com.google.common.collect.Lists;

/**
 * This protocol provides default TESTAR behaviour to test Windows desktop applications.
 *
 * It uses random action selection algorithm.
 */
public class Protocol_desktop_b00_registration_kiosk extends DesktopProtocol {

	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 * @param   settings  the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings){
		super.initialize(settings);
	}

	/**
	 * This methods is called before each test sequence, before startSystem(),
	 * allowing for example using external profiling software on the SUT
	 *
	 * HTML sequence report will be initialized in the super.preSequencePreparations() for each sequence
	 */
	@Override
	protected void preSequencePreparations() {
		super.preSequencePreparations();
	}

	/**
	 * This method is called when TESTAR starts the System Under Test (SUT). The method should
	 * take care of
	 *   1) starting the SUT (you can use TESTAR's settings obtainable from <code>settings()</code> to find
	 *      out what executable to run)
	 *   2) waiting until the system is fully loaded and ready to be tested (with large systems, you might have to wait several
	 *      seconds until they have finished loading)
	 * @return  a started SUT, ready to be tested.
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
	protected void beginSequence(SUT system, State state){
		super.beginSequence(system, state);
	}

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
		return super.getState(system);
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
			// - typeable
			// and
			// - unFiltered by any of the regular expressions in the Filter-tab, or
			// - whitelisted using the clickfilter functionality in SPY mode (CAPS_LOCK + SHIFT + CNTR + Click)
			// We want to create actions that consist of typing into them
			if(isTypeable(w)){
				if(isUnfiltered(w) || whiteListed(w)) {
					// Generate a random telephone number of 10 digits
					if(w.get(Tags.Role, Roles.Widget).equals(UIARoles.UIASpinner) && isTelefoonnummerSibling(w)) {
						actions.add(ac.pasteTextInto(w, getRandomTelefoonnummer(), true));
					} else {
						//Create a type action with the Action Compiler, and add it to the set of derived actions
						actions.add(ac.pasteTextInto(w, getRandomRegistrationValues(), true));
						// Naughty strings
						actions.add(ac.pasteTextInto(w, InputDataManager.getRandomTextFromCustomInputDataFile(System.getProperty("user.dir") + "/settings/blns.txt"), true));
					}
				}
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

	@Override
	protected boolean isTypeable(Widget w) {
		// UIADocuments have the roles of panels but not typeable widgets
		if(w.get(Tags.Role, Roles.Widget).equals(UIARoles.UIADocument)) 
			return false;

		// Ignore widget children that come from the PDF document
		if(isSonOfPdfDocument(w)) 
			return false;

		// Telefoonnummer UIASpinners are clickable and typeable
		if(w.get(Tags.Role, Roles.Widget).equals(UIARoles.UIASpinner) 
				&& w.get(Tags.Title, "").isBlank()) {
			return true;
		}

		return super.isTypeable(w);
	}

	private boolean isTelefoonnummerSibling(Widget w) {
		if(w.parent() != null) {
			for(int i = 0; i < w.parent().childCount(); i++) {
				if(w.parent().child(i).get(Tags.Title, "").contains("Telefoonnummer")) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isSonOfPdfDocument(Widget w) {
		if(w.parent() == null) return false;
		else if (w.parent().get(Tags.Role, Roles.Widget).equals(UIARoles.UIADocument)
				&& w.get(Tags.Title, "").contains(".pdf")) return true;
		else return isSonOfPdfDocument(w.parent());
	}

	private String getRandomTelefoonnummer() {
		Random random = new Random();
		// The range for a 10-digit number is from 1000000000 (1 billion) to 9999999999 (just under 10 billion).
		long min = 1000000000L;
		long max = 9999999999L;
		long randomNumber = min + (long) (random.nextDouble() * (max - min));
		return Long.toString(randomNumber);
	}

	private String getRandomRegistrationValues(){
		List<String> urls = Lists.newArrayList(
				"1", 
				"testar", 
				"testar@testar.org");

		return urls.get(new Random().nextInt(urls.size()));
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
		List<Action> weightedActions = new ArrayList<>();
		List<Integer> weights = new ArrayList<>();

		// Iterate through each action to assign weights
		for (Action action : actions) {
			int weight = 1;  // Default weight

			// Increase weight for "Type" and "Paste" actions
			String actionRole = action.get(Tags.Role, Roles.Invalid).toString();
			if (actionRole.contains("Type") || actionRole.contains("Paste")) {
				weight = 3;
			}
			// Increase weight for submit actions related to "Aanmelden"
			else if (action.get(Tags.OriginWidget, state).get(Tags.Title, "").equals("Aanmelden")) {
				weight = 3;
			}

			// Add the action multiple times according to its weight
			for (int i = 0; i < weight; i++) {
				weightedActions.add(action);
			}
			weights.add(weight);  // For reference if needed later
		}

		// Select an action randomly from the weighted list
		Random randomGenerator = new Random();
		int randomIndex = randomGenerator.nextInt(weightedActions.size());

		return weightedActions.get(randomIndex);
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
