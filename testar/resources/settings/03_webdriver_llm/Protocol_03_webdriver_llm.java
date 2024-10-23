/**
 * Copyright (c) 2018 - 2023 Open Universiteit - www.ou.nl
 * Copyright (c) 2019 - 2023 Universitat Politecnica de Valencia - www.upv.es
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
import org.testar.SutVisualization;
import org.testar.action.priorization.llm.LlmActionSelector;
import org.testar.managers.InputDataManager;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.*;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.alayer.exceptions.SystemStartException;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.plugin.NativeLinker;
import org.testar.monkey.Pair;
import org.testar.protocols.WebdriverProtocol;
import org.testar.settings.Settings;

import java.util.*;
import java.util.stream.Collectors;

import static org.testar.monkey.alayer.Tags.Blocked;
import static org.testar.monkey.alayer.Tags.Enabled;
import static org.testar.monkey.alayer.webdriver.Constants.scrollArrowSize;
import static org.testar.monkey.alayer.webdriver.Constants.scrollThick;

public class Protocol_03_webdriver_llm extends WebdriverProtocol {
	private boolean testGoalAccomplished = false;

	// This list tracks the detected erroneous verdicts to avoid duplicates
	private List<String> listOfDetectedErroneousVerdicts = new ArrayList<>();

	// The LLM Action selector needs to be initialize with the settings
	private LlmActionSelector llmActionSelector;

	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 *
	 * @param settings the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings) {
		super.initialize(settings);

		// Initialize the LlmActionSelector using the LLM settings
		llmActionSelector = new LlmActionSelector(settings);

		// List of atributes to identify and close policy popups
		// Set to null to disable this feature
		policyAttributes = ArrayListMultimap.create();
		policyAttributes.put("class", "lfr-btn-label");

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
		if(testGoalAccomplished) {
			verdict = new Verdict(Verdict.SEVERITY_FAIL, "LLM believes test goal was accomplished, " +
					"test completed. This is not an error.");
		}
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

		return customVerdict;
	}

	private boolean containsVerdictInfo(List<String> listOfDetectedErroneousVerdicts, String currentVerdictInfo) {
		return listOfDetectedErroneousVerdicts.stream().anyMatch(verdictInfo -> verdictInfo.contains(currentVerdictInfo.replace("\n", " ")));
	}

	public Verdict detectNumberWithLotOfDecimals(State state, int maxDecimals) {
		for(Widget w : state) {
			// If the widget contains a web text that is a double number
			if(!w.get(WdTags.WebTextContent, "").isEmpty() && isNumeric(w.get(WdTags.WebTextContent))) {
				// Count the decimal places of the text number
				String number = w.get(WdTags.WebTextContent).replace(",", ".");
				int decimalPlaces = number.length() - number.indexOf('.') - 1;

				if(number.contains(".") && decimalPlaces > maxDecimals) {
					String verdictMsg = String.format("Widget with more than %s decimals! Role: %s , Path: %s , WebId: %s , WebTextContent: %s", 
							maxDecimals, w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""), w.get(WdTags.WebTextContent, ""));

					return new Verdict(Verdict.SEVERITY_WARNING, verdictMsg);
				}
			}
		}

		return Verdict.OK;
	}

	private boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		strNum = strNum.trim().replace("\u0024", "").replace("\u20AC", "");
		try {
			Double.parseDouble(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	// Detect dropdown select elements that does not contains values, or only one of them
	public Verdict detectEmptySelectItems(State state) {
		for(Widget w : state) {
			// For the web select elements with an Id property
			if(w.get(Tags.Role, Roles.Widget).equals(WdRoles.WdSELECT) && !w.get(WdTags.WebId, "").isEmpty()) {
				try {
					String elementId = w.get(WdTags.WebId, "");
					String query = String.format("return document.getElementById('%s').length", elementId);
					Long selectItemsLength = (Long) WdDriver.executeScript(query);
					// Verify that contains at least one item element
					if (selectItemsLength.intValue() <= 1) {
						String verdictMsg = String.format("Empty or Unique Select element detected! Role: %s , Path: %s , Desc: %s", 
								w.get(Tags.Role), w.get(Tags.Path), w.get(Tags.Desc, ""));

						return new Verdict(Verdict.SEVERITY_WARNING, verdictMsg);
					} 
				}catch (Exception e) {}
			}
		}

		return Verdict.OK;
	}

	// Detect duplicated items in a Select (dropdown list/listbox)
	public Verdict detectDuplicateSelectItems(State state) {
		for(Widget w : state) {
			// For the web select elements with an Id property
			if(w.get(Tags.Role, Roles.Widget).equals(WdRoles.WdSELECT) && !w.get(WdTags.WebId, "").isEmpty()) {
				try {
					String elementId = w.get(WdTags.WebId, "");
					String querylength = String.format("return ((document.getElementById('%s') != null) ? document.getElementById('%s').length : 0)", elementId, elementId);
					Long selectItemsLength = (Long) WdDriver.executeScript(querylength);

					if (selectItemsLength > 1) { 
						String queryTexts = String.format("return [...document.getElementById('%s').options].map(o => o.text)", elementId);
						@SuppressWarnings("unchecked")
						ArrayList<String> selectOptionsTextsList = (ArrayList<String>) WdDriver.executeScript(queryTexts);

						Set<String> duplicatesTexts = selectOptionsTextsList.stream()
								.filter(s -> Collections.frequency(selectOptionsTextsList, s) > 1)
								.collect(Collectors.toSet());

						// Now that we have collected all the duplicates in a list verify that there are no duplicates
						if(duplicatesTexts.size() > 0)
						{
							String verdictMsg = String.format("Detected a Select web element with duplicate display value elements! Role: %s , Path: %s , WebId: %s , Duplicate item(s): %s", 
									w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""), String.join(",", duplicatesTexts));

							return new Verdict(Verdict.SEVERITY_WARNING, verdictMsg);
						}
					}
				}catch (Exception e) {}
			}
		}

		return Verdict.OK;
	}

	public Verdict detectTextAreaWithoutLength(State state, List<Role> roles) {
		for(Widget w : state) {
			if(roles.contains(w.get(Tags.Role, Roles.Widget)) && w.get(WdTags.WebMaxLength) == 0) {

				String verdictMsg = String.format("TextArea Widget with 0 Length detected! Role: %s , Path: %s , WebId: %s", 
						w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""));

				return new Verdict(Verdict.SEVERITY_WARNING, verdictMsg);
			}
		}

		return Verdict.OK;
	}

	// Detect duplicated rows in a table by concatenating all visible values with an _ underscore
	public Verdict detectDuplicatedRowsInTable(State state) {
		for(Widget w : state) {
			if(w.get(Tags.Role, Roles.Widget).equals(WdRoles.WdTABLE)) {
				List<Pair<Widget, String>> rowElementsDescription = new ArrayList<>();
				extractAllRowDescriptionsFromTable(w, rowElementsDescription);

				List<Pair<Widget, String>> duplicatedDescriptions = 
						rowElementsDescription.stream()
						.collect(Collectors.groupingBy(Pair::right))
						.entrySet().stream()
						.filter(e -> e.getValue().size() > 1)
						.flatMap(e -> e.getValue().stream())
						.collect(Collectors.toList());

				// If the list of duplicated descriptions contains a matching prepare the verdict
				if(!duplicatedDescriptions.isEmpty()) {
					for(Pair<Widget, String> duplicatedWidget : duplicatedDescriptions) {
						// Ignore empty rows
						if (!duplicatedWidget.right().replaceAll("_","").isEmpty()) {
							String verdictMsg = String.format("Detected a duplicated rows in a Table! Role: %s , WebId: %s, Description: %s", 
									duplicatedWidget.left().get(Tags.Role), duplicatedWidget.left().get(WdTags.WebId, ""), duplicatedWidget.right());

							return new Verdict(Verdict.SEVERITY_WARNING, verdictMsg);
						}
					}
				}

			}
		}

		return Verdict.OK;
	}

	private void extractAllRowDescriptionsFromTable(Widget w, List<Pair<Widget, String>> rowElementsDescription) {
		if(w.get(Tags.Role, Roles.Widget).equals(WdRoles.WdTR)) {
			rowElementsDescription.add(new Pair<Widget, String>(w, obtainWidgetTreeDescription(w)));
		}

		// Iterate through the form element widgets
		for(int i = 0; i < w.childCount(); i++) {
			// If the children of the table are not sub-tables
			if(!w.child(i).get(Tags.Role, Roles.Widget).equals(WdRoles.WdTABLE)) {
				extractAllRowDescriptionsFromTable(w.child(i), rowElementsDescription);
			}
		}
	}

	private String obtainWidgetTreeDescription(Widget w) {
		String widgetDesc = w.get(WdTags.WebTextContent, "");

		// Iterate through the form element widgets
		for(int i = 0; i < w.childCount(); i++) {
			widgetDesc = widgetDesc + "_" + obtainWidgetTreeDescription(w.child(i));
		}

		return widgetDesc;
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

		/*
		// Check if the update profile element is found:
		Widget nameWidget = getWidgetWithMatchingTag(
				"id", "customer.firstName", state);

		if(nameWidget != null){
			// Update profile found, create and return the triggered action:
			// Create a compound action to include multiple actions as one:
			CompoundAction.Builder multiAction = new CompoundAction.Builder();

			// Action to type text into the Name field:
			multiAction.add(ac.clickTypeInto(
					nameWidget, "Triggered Name", true), 1.0);

			// Action to type text into id="customer.lastName":
			Widget lastNameWidget = getWidgetWithMatchingTag(
					"id", "customer.lastName", state);
			multiAction.add(ac.clickTypeInto(
					lastNameWidget, "Triggered Last Name", true), 1.0);

			// Action to type text into id="customer.address.street":
			Widget streetWidget = getWidgetWithMatchingTag(
					"id", "customer.address.street", state);
			multiAction.add(ac.clickTypeInto(
					streetWidget, "Triggered Street", true), 1.0);

			// Action to type text into id="customer.address.city":
			Widget cityWidget = getWidgetWithMatchingTag(
					"id", "customer.address.city", state);
			multiAction.add(ac.clickTypeInto(
					cityWidget, "Triggered City", true), 1.0);

			// You can add here more form widgets

			// Action on Update Profile button, value="Update Profile"
			Widget submitWidget = getWidgetWithMatchingTag(
					"value", "Update Profile", state);
			multiAction.add(ac.leftClickAt(submitWidget), 1.0);

			// Build the update profile compound action
			Action updateProfileAction = multiAction.build();

			// Returning a list of actions having only the updateProfileAction
			return new HashSet<>(Collections.singletonList(updateProfileAction));
		}
		 */

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
			addSlidingActions(actions, ac, scrollArrowSize, scrollThick, widget);

			// If the element is blocked, Testar can't click on or type in the widget
			if (widget.get(Blocked, false) && !widget.get(WdTags.WebIsShadow, false)) {
				continue;
			}

			// type into text boxes
			if (isAtBrowserCanvas(widget) && isTypeable(widget)) {
				if(whiteListed(widget) || isUnfiltered(widget)){
					// Type a random Number, Alphabetic, URL, Date or Email input
					actions.add(ac.clickTypeInto(widget, InputDataManager.getRandomTextInputData(widget), true));
					// Paste a random input from a customizable input data file
					// Check testar/bin/settings/custom_input_data.txt
					//actions.add(ac.pasteTextInto(widget, InputDataManager.getRandomTextFromCustomInputDataFile(System.getProperty("user.dir") + "/settings/custom_input_data.txt"), true));
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
		// Null is returned when the LLM wants to terminate the test (if the test goal is believed to be accomplished)
		// If there is a problem with action selection, a NOP action will be executed.
		if(toExecute == null) {
			// LLM thinks test goal is accomplished, perform no action and set flag for getVerdict to terminate test.
			testGoalAccomplished = true;
			return new NOP();
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
		// If the final Verdict is not OK and the verdict is not saved in the list
		// This is a new run fail verdict
		Verdict finalVerdict = getVerdict(latestState);
		if(finalVerdict.severity() > Verdict.SEVERITY_OK && !listOfDetectedErroneousVerdicts.contains(finalVerdict.info().replace("\n", " "))) {
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
