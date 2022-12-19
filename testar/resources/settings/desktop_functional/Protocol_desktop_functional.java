/***************************************************************************************************
 *
 * Copyright (c) 2013 - 2022 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 - 2022 Open Universiteit - www.ou.nl
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.testar.DerivedActions;
import org.testar.SutVisualization;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.alayer.windows.UIARoles;
import org.testar.monkey.alayer.windows.UIATags;
import org.testar.protocols.DesktopProtocol;

import com.google.common.collect.Comparators;

/**
 * Protocol with functional oracles examples to detect:
 * - Form has no title 
 * - Checkbox without a caption
 * - List with unsorted items
 * - Dummy button (does nothing)
 * - List without child's
 * - TODO: Radio button panel with only one option
 * - TODO: Panel without children
 * - TODO: Buttons (3x) with proper text (spell checker) - OK
 * - TODO: Textbox over another textbox - OK - ElementMap, Rect intersection or existing libraries
 * - https://www.youtube.com/watch?v=omuxzPT050w
 * 
 * - TODO: Two wrong ancor buttons. If you resize the form, these buttons do not align correct. - Research Anchor properties
 * - TODO: Tab order is all over the place - Research next element properties - Check tree order UIAutomation (sorted?)
 * - TODO: Make a configurable verdict to detect if dialog or windows do not contains a question mark "?" Notepad and Robin examples
 * 
 * - Instead of joining Verdicts, try to recognize and save different Verdict exception in different sequences.
 */
public class Protocol_desktop_functional extends DesktopProtocol {

	private Action functionalAction = null;
	private Verdict functionalVerdict = Verdict.OK;
	private List<String> listErrorVerdictInfo = new ArrayList<>();

	/**
	 * This method is called before the first test sequence, allowing for example setting up the test environment
	 */
	@Override
	protected void initTestSession() {
		super.initTestSession();
		listErrorVerdictInfo = new ArrayList<>();
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
		// Reset the functional action and verdict
		functionalAction = null;
		functionalVerdict = Verdict.OK;
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
		Verdict verdict = super.getVerdict(state);

		// Add the Verdict that detects if the SUT contains a mandatory widget role without title
		verdict = verdict.join(mandatoryWidgetRoleWithTitle(state, Arrays.asList(UIARoles.UIAWindow, UIARoles.UIACheckBox, UIARoles.UIAListItem)));

		// Add the Verdict that detects if the SUT contains a list with unsorted elements
		verdict = verdict.join(detectEmptyAndUnsortedListElements(state, Arrays.asList(UIARoles.UIAList, UIARoles.UIATree)));

		// Add the functional Verdict that detects dummy buttons to the current state verdict.
		verdict = verdict.join(dummyButtonVerdict(state));

		// If the final Verdict is not OK but was already detected in a previous sequence
		String currentVerdictInfo = verdict.info().replace("\n", " ");
		if( listErrorVerdictInfo.stream().anyMatch( verdictInfo -> verdictInfo.contains( currentVerdictInfo ) ) ) {
			// Consider as OK to continue testing
			verdict = Verdict.OK;
		}

		return verdict;
	}

	private Verdict mandatoryWidgetRoleWithTitle(State state, List<Role> roles) {
		Verdict emptyTitleVerdict = Verdict.OK;
		for(Widget w : state) {
			if(roles.contains(w.get(Tags.Role, Roles.Widget)) && w.get(Tags.Title, "").isEmpty()) {

				String verdictMsg = String.format("Widget without Mandatory Title detected! Role: %s , Path: %s , AutomationId: %s", 
						w.get(Tags.Role), w.get(Tags.Path), w.get(UIATags.UIAAutomationId, ""));

				emptyTitleVerdict = emptyTitleVerdict.join(new Verdict(Verdict.SEVERITY_WARNING, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
			}
		}
		return emptyTitleVerdict;
	}

	/**
	 * Verify that the List elements of the state:
	 * 1- Are not empty and contain child
	 * 2- Do not contain unsorted elements
	 * 
	 * If these failures are detected, 
	 * return a Warning Verdict pointing to the List Widget.
	 * 
	 * @param state
	 * @return
	 */
	private Verdict detectEmptyAndUnsortedListElements(State state, List<Role> roles) {
		for(Widget w : state) {
			// 1 - Check that the list is not empty
			if(roles.contains(w.get(Tags.Role, Roles.Widget)) && w.childCount() < 1) {

				String verdictMsg = String.format("Detected a List element without child elements! Title: %s , Role: %s , Path: %s , AutomationId: %s", 
						w.get(Tags.Title, ""), w.get(Tags.Role), w.get(Tags.Path), w.get(UIATags.UIAAutomationId, ""));

				return new Verdict(Verdict.SEVERITY_WARNING, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape)));
			}
			// 2 - Check that the list do not contain unsorted elements
			else if(roles.contains(w.get(Tags.Role, Roles.Widget)) && w.childCount() > 0) {
				// Iterate trough the List elements to save all Titles
				List<String> elementsTitleList = new ArrayList<String>();
				for(int i = 0; i < w.childCount(); i++) {
					// Add the Title of the list element child to the list
					Widget childWidget = w.child(i);
					elementsTitleList.add(childWidget.get(Tags.Title, ""));
				}
				// Now that we have collected all the list Titles verify that is sorted 
				if(!isSorted(elementsTitleList)) {

					String verdictMsg = String.format("Detected a List element with unsorted elements! Title: %s , Role: %s , Path: %s , AutomationId: %s", 
							w.get(Tags.Title, ""), w.get(Tags.Role), w.get(Tags.Path), w.get(UIATags.UIAAutomationId, ""));

					return new Verdict(Verdict.SEVERITY_WARNING, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape)));
				}
			}
		}
		return Verdict.OK;
	}

	private static boolean isSorted(List<String> listOfStrings) {
		return Comparators.isInOrder(listOfStrings, Comparator.<String> naturalOrder());
	}

	private Verdict dummyButtonVerdict(State state) {
		// If the last executed action is a click on a web button
		if(functionalAction != null 
				&& functionalAction.get(Tags.OriginWidget) != null 
				&& functionalAction.get(Tags.Desc, "").contains("Click")
				&& functionalAction.get(Tags.OriginWidget).get(Tags.Role, Roles.Widget).equals(UIARoles.UIAButton)) {

			// Compare previous and current state AbstractIDCustom identifiers
			// to determine if interacting with the button does nothing in the SUT state
			String previousStateId = latestState.get(Tags.AbstractIDCustom, "NoPreviousId");
			String currentStateId = state.get(Tags.AbstractIDCustom, "NoCurrentId");

			// NOTE: Because we are comparing the states using the AbstractIDCustom property, 
			// it is important to consider the used abstraction: test.settings - AbstractStateAttributes (WebWidgetId, WebWidgetTextContent)
			if(previousStateId.equals(currentStateId)) {
				Widget w = functionalAction.get(Tags.OriginWidget);
				String verdictMsg = String.format("Dummy Button detected! Role: %s , Path: %s , Desc: %s", 
						w.get(Tags.Role), w.get(Tags.Path), w.get(Tags.Desc, ""));

				functionalVerdict = new Verdict(Verdict.SEVERITY_WARNING, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape)));
			}

			// getState and getVerdict are executed more than one time after executing an action. 
			// Then previous state becomes current state in the second execution...
			// Set to null to prevent multiple checks.
			// We need to fix this in the TESTAR internal flow :D
			functionalAction = null;
		}

		return functionalVerdict;
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
		return(super.selectAction(state, actions));
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
		functionalAction = action;
		return super.executeAction(system, state, action);
	}

	/**
	 * This method is invoked each time after TESTAR finished the generation of a sequence.
	 */
	@Override
	protected void finishSequence() {
		super.finishSequence();
		// If the final Verdict is not OK and the verdict is not saved in the list
		// This is a new run fail verdict
		if(getFinalVerdict().severity() > Verdict.SEVERITY_OK && !listErrorVerdictInfo.contains(getFinalVerdict().info().replace("\n", " "))) {
			listErrorVerdictInfo.add(getFinalVerdict().info().replace("\n", " "));
		}
	}
}
