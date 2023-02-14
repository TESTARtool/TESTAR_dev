/**
 * Copyright (c) 2018 - 2022 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2022 Universitat Politecnica de Valencia - www.upv.es
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

import org.testar.SutVisualization;
import org.testar.monkey.Pair;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.SystemStartException;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.WdElement;
import org.testar.monkey.alayer.webdriver.WdWidget;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.plugin.NativeLinker;
import org.testar.protocols.WebdriverProtocol;

import com.google.common.collect.Comparators;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.testar.monkey.alayer.Tags.Blocked;
import static org.testar.monkey.alayer.Tags.Enabled;
import static org.testar.monkey.alayer.webdriver.Constants.scrollArrowSize;
import static org.testar.monkey.alayer.webdriver.Constants.scrollThick;

/**
 * Protocol with functional oracles examples to detect:
 * - Web dummy button
 * - Web select list without items
 * - Web text area with max length 0 + add example to dummy HTML SUT
 * - If a web text string is a number and contains more than X decimals + add example to dummy HTML SUT
 * - Radio button panel with only one option (input)
 * - Panel without children (form, div)
 * - Web alert with suspicious message
 * - TODO: JavaScript loop to hang the browser - devTools
 * - TODO: JavaScript refresh browser constantly - devTools
 * - TODO: textarea with rows and columns to detect enter click
 * 
 * - Instead of joining Verdicts, try to recognize and save different Verdict exception in different sequences.
 */
public class Protocol_webdriver_functional extends WebdriverProtocol {

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
	 * This methods is called before each test sequence, before startSystem(),
	 * allowing for example using external profiling software on the SUT
	 *
	 * HTML sequence report will be initialized in the super.preSequencePreparations() for each sequence
	 */
	@Override
	protected void preSequencePreparations() {
		super.preSequencePreparations();
		WdDriver.alertMessage = ""; // reset webdriver alert
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

		// https://github.com/ferpasri/parabank/tree/injected_failures
		// custom_compile_and_deploy.bat
		// http://localhost:8080/parabank
		// parabank script login sequence
		/*
		Util.pause(1);
		WdDriver.executeScript("document.getElementsByName('username')[0].setAttribute('value','john');");
		WdDriver.executeScript("document.getElementsByName('password')[0].setAttribute('value','demo');");
		WdDriver.executeScript("document.getElementsByName('login')[0].submit();");
		Util.pause(1);
		*/

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
		// Reset the functional action and verdict
		functionalAction = null;
		functionalVerdict = Verdict.OK;
	}

	/**
	 * This is a helper method used by the default implementation of <code>buildState()</code>
	 * It examines the SUT's current state and returns an oracle verdict.
	 *
	 * @return oracle verdict, which determines whether the state is erroneous and why.
	 */
	@Override
	protected Verdict getVerdict(State state) {
		Verdict verdict = super.getVerdict(state);

		verdict = getUniqueFunctionalVerdict(verdict, state);

		// If the final Verdict is not OK but was already detected in a previous sequence
		String currentVerdictInfo = verdict.info().replace("\n", " ");
		if( listErrorVerdictInfo.stream().anyMatch( verdictInfo -> verdictInfo.contains( currentVerdictInfo ) ) ) {
			// Consider as OK to continue testing
			verdict = Verdict.OK;
			webConsoleVerdict = Verdict.OK;
		}

		return verdict;
	}

	/**
	 * This method returns a unique failure verdict of one state.
	 * Even if multiple failures can be reported together.
	 * 
	 * @param verdict
	 * @param state
	 * @return
	 */
	private Verdict getUniqueFunctionalVerdict(Verdict verdict, State state) {
		//TODO: Refactor Verdict class or this method feature
		// Due this is the unique method, only start the verdict checking if no failure exists.
		if(verdict == Verdict.OK) {
			// Check the functional Verdict that detects dummy buttons to the current state verdict.
			Verdict buttonVerdict = functionalButtonVerdict(state);
			if(buttonVerdict != Verdict.OK && listErrorVerdictInfo.stream().noneMatch( info -> info.contains( buttonVerdict.info().replace("\n", " ") ))) return buttonVerdict;

			// Check the functional Verdict that detects select elements without items to the current state verdict.
			Verdict emptySelectListVerdict = emptySelectItemsVerdict(state);
			if(emptySelectListVerdict != Verdict.OK && listErrorVerdictInfo.stream().noneMatch( info -> info.contains( emptySelectListVerdict.info().replace("\n", " ") ))) return emptySelectListVerdict;

			// Check the functional Verdict that detects select elements with unsorted items to the current state verdict.
			Verdict unsortedSelectListVerdict = unsortedSelectOptionsVerdict(state);
			if(unsortedSelectListVerdict != Verdict.OK && listErrorVerdictInfo.stream().noneMatch( info -> info.contains( unsortedSelectListVerdict.info().replace("\n", " ") ))) return unsortedSelectListVerdict;

			// Check the functional Verdict that detects if exists a number with more than X decimals.
			Verdict decimalsVerdict = numberWithLotOfDecimals(state, 2);
			if(decimalsVerdict != Verdict.OK && listErrorVerdictInfo.stream().noneMatch( info -> info.contains( decimalsVerdict.info().replace("\n", " ") ))) return decimalsVerdict;

			// Check the functional Verdict that detects if exists a textArea Widget without length.
			Verdict textAreaVerdict = textAreaWithoutLength(state, Arrays.asList(WdRoles.WdTEXTAREA));
			if(textAreaVerdict != Verdict.OK && listErrorVerdictInfo.stream().noneMatch( info -> info.contains( textAreaVerdict.info().replace("\n", " ") ))) return textAreaVerdict;

			// Check the functional Verdict that detects if a web element does not contain children.
			Verdict emptyElementVerdict = elementWithoutChildren(state, Arrays.asList(WdRoles.WdFORM, WdRoles.WdDIV));
			if(emptyElementVerdict != Verdict.OK && listErrorVerdictInfo.stream().noneMatch( info -> info.contains( emptyElementVerdict.info().replace("\n", " ") ))) return emptyElementVerdict;

			// Check the functional Verdict that detects if a web radio input contains a unique option.
			Verdict uniqueRadioVerdict = uniqueRadioInput(state);
			if(uniqueRadioVerdict != Verdict.OK && listErrorVerdictInfo.stream().noneMatch( info -> info.contains( uniqueRadioVerdict.info().replace("\n", " ") ))) return uniqueRadioVerdict;

			// Check the functional Verdict that detects if a web alert contains a suspicious message.
			Verdict alertSuspiciousVerdict = alertSuspiciousMessage(state, ".*[lL]ogin.*");
			if(alertSuspiciousVerdict != Verdict.OK && listErrorVerdictInfo.stream().noneMatch( info -> info.contains( alertSuspiciousVerdict.info().replace("\n", " ") ))) return alertSuspiciousVerdict;

			// Check the functional Verdict that detects if web table contains duplicated rows.
			Verdict duplicateRowsInTableVerdict = duplicateRowsInTable(state);
			if(duplicateRowsInTableVerdict != Verdict.OK && listErrorVerdictInfo.stream().noneMatch( info -> info.contains( duplicateRowsInTableVerdict.info().replace("\n", " ") ))) return duplicateRowsInTableVerdict;
		}
		return verdict;
	}

	private Verdict functionalButtonVerdict(State state) {
		// If the last executed action is a click on a web button
		if(functionalAction != null 
				&& functionalAction.get(Tags.OriginWidget, null) != null 
				&& functionalAction.get(Tags.Desc, "").contains("Click")
				&& functionalAction.get(Tags.OriginWidget).get(Tags.Role, Roles.Widget).equals(WdRoles.WdBUTTON)) {

			// Compare previous and current state AbstractIDCustom identifiers
			// to determine if interacting with the button does nothing in the SUT state
			String previousStateId = latestState.get(Tags.AbstractIDCustom, "NoPreviousId");
			String currentStateId = state.get(Tags.AbstractIDCustom, "NoCurrentId");

			// NOTE 1: Because we are comparing the states using the AbstractIDCustom property, 
			// it is important to consider the used abstraction: test.settings - AbstractStateAttributes (WebWidgetId, WebWidgetTextContent)
			// NOTE 2: A button alert can prompt a message, but TESTAR saves the text message and returns to the state
			// this is also something to consider :/
			// TODO: Improve the existence of web alerts messages within the State ID
			if(previousStateId.equals(currentStateId) && WdDriver.alertMessage.isEmpty()) {
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

	private Verdict emptySelectItemsVerdict(State state) {
		Verdict emptySelectListVerdict = Verdict.OK;

		for(Widget w : state) {
			// For the web select elements with an Id property
			if(w.get(Tags.Role, Roles.Widget).equals(WdRoles.WdSELECT) && !w.get(WdTags.WebId, "").isEmpty()) {
				String elementId = w.get(WdTags.WebId, "");
				String query = String.format("return document.getElementById('%s').length", elementId);
				Long selectItemsLength = (Long) WdDriver.executeScript(query);
				// Verify that contains at least one item element
				if (selectItemsLength.intValue() <= 1) {
					String verdictMsg = String.format("Empty or Unique Select element detected! Role: %s , Path: %s , Desc: %s", 
							w.get(Tags.Role), w.get(Tags.Path), w.get(Tags.Desc, ""));

					emptySelectListVerdict = new Verdict(Verdict.SEVERITY_WARNING, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape)));
				}
			}
		}

		return emptySelectListVerdict;
	}

	private Verdict unsortedSelectOptionsVerdict(State state) {
		Verdict unsortedSelectElementVerdict = Verdict.OK;

		for(Widget w : state) {
			// For the web select elements with an Id property
			if(w.get(Tags.Role, Roles.Widget).equals(WdRoles.WdSELECT) && !w.get(WdTags.WebId, "").isEmpty()) {
				String elementId = w.get(WdTags.WebId, "");
				String query = String.format("return [...document.getElementById('%s').options].map(o => o.value)", elementId);
				ArrayList<String> selectOptionsList = (ArrayList<String>) WdDriver.executeScript(query);

				// Now that we have collected all the array list of the option values verify that is sorted 
				if(!isSorted(selectOptionsList)) {

					String verdictMsg = String.format("Detected a Select web element with unsorted elements! Role: %s , Path: %s , WebId: %s", 
							w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""));

					return new Verdict(Verdict.SEVERITY_WARNING, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape)));
				}
			}
		}

		return unsortedSelectElementVerdict;
	}

	private static boolean isSorted(List<String> listOfStrings) {
		return Comparators.isInOrder(listOfStrings, Comparator.<String> naturalOrder());
	}

	private Verdict numberWithLotOfDecimals(State state, int maxDecimals) {
		Verdict decimalsVerdict = Verdict.OK;
		for(Widget w : state) {
			// If the widget contains a web text that is a double number
			if(!w.get(WdTags.WebTextContent, "").isEmpty() && isNumeric(w.get(WdTags.WebTextContent))) {
				// Count the decimal places of the text number
				String number = w.get(WdTags.WebTextContent).replace(",", ".");
				int decimalPlaces = number.length() - number.indexOf('.') - 1;

				if(number.contains(".") && decimalPlaces > maxDecimals) {
					String verdictMsg = String.format("Widget with more than %s decimals! Role: %s , Path: %s , WebId: %s , WebTextContent: %s", 
							maxDecimals, w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""), w.get(WdTags.WebTextContent, ""));

					decimalsVerdict = decimalsVerdict.join(new Verdict(Verdict.SEVERITY_WARNING, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
				}
			}
		}
		return decimalsVerdict;
	}

	private Verdict textAreaWithoutLength(State state, List<Role> roles) {
		Verdict textAreaVerdict = Verdict.OK;
		for(Widget w : state) {
			if(roles.contains(w.get(Tags.Role, Roles.Widget)) && w.get(WdTags.WebMaxLength) == 0) {

				String verdictMsg = String.format("TextArea Widget with 0 Length detected! Role: %s , Path: %s , WebId: %s", 
						w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""));

				textAreaVerdict = textAreaVerdict.join(new Verdict(Verdict.SEVERITY_WARNING, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
			}
		}
		return textAreaVerdict;
	}

	private Verdict elementWithoutChildren(State state, List<Role> roles) {
		Verdict emptyChildrenVerdict = Verdict.OK;
		for(Widget w : state) {
			if(roles.contains(w.get(Tags.Role, Roles.Widget)) && w.childCount() < 1) {

				String verdictMsg = String.format("Detected a Web element without child elements! Role: %s , Path: %s , WebId: %s", 
						w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""));

				emptyChildrenVerdict = emptyChildrenVerdict.join(new Verdict(Verdict.SEVERITY_WARNING, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
			}
		}
		return emptyChildrenVerdict;
	}

	private Verdict uniqueRadioInput(State state) {
		Verdict radioInputVerdict = Verdict.OK;
		for(Widget w : state) {
			if(isRadioInput(w) && !siblingRoleElement(w, WdRoles.WdINPUT)) {

				String verdictMsg = String.format("Detected a Web radio input element with a Unique option! Role: %s , Path: %s , WebId: %s , WebTextContent: %s", 
						w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""), w.get(WdTags.WebTextContent, ""));

				radioInputVerdict = radioInputVerdict.join(new Verdict(Verdict.SEVERITY_WARNING, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
			}
		}
		return radioInputVerdict;
	}

	private boolean isRadioInput(Widget w) {
		return w.get(Tags.Role, Roles.Widget).equals(WdRoles.WdINPUT) && w.get(WdTags.WebType, "").equalsIgnoreCase("radio");
	}

	private Verdict duplicateRowsInTable(State state) {
		Verdict duplicateRowsInTableVerdict = Verdict.OK;
		for(Widget w : state) {
			if(w.get(Tags.Role, Roles.Widget).equals(WdRoles.WdTABLE)) {
				List<Pair<Widget, String>> rowElementsDescription = new ArrayList<>();
				extractAllRowDescriptionsFromTable(w, rowElementsDescription);

				// https://stackoverflow.com/a/52296246
				List<String> duplicatedDescriptions =    
						rowElementsDescription.stream().collect(Collectors.groupingBy(Pair::right))
						.entrySet()
						.stream()
						.filter(e -> e.getValue().size() > 1)
						.map(Map.Entry::getKey)
						.collect(Collectors.toList());

				// If the list of duplicated descriptions contains a matching prepare the verdict
				if(!duplicatedDescriptions.isEmpty()) {
					String verdictMsg = String.format("Detected a Table with duplicated rows! Role: %s , WebId: %s", 
							w.get(Tags.Role), w.get(WdTags.WebId, ""));

					duplicateRowsInTableVerdict = duplicateRowsInTableVerdict.join(new Verdict(Verdict.SEVERITY_WARNING, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
				}

			}
		}

		return duplicateRowsInTableVerdict;
	}

	private void extractAllRowDescriptionsFromTable(Widget w, List<Pair<Widget, String>> rowElementsDescription) {
		if(w.get(Tags.Role, Roles.Widget).equals(WdRoles.WdTR)) {
			rowElementsDescription.add(new Pair<Widget, String>(w, obtainWidgetTreeDescription(w)));
		}

		// Iterate through the form element widgets
		for(int i = 0; i < w.childCount(); i++) {
			extractAllRowDescriptionsFromTable(w.child(i), rowElementsDescription);
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

	private Verdict alertSuspiciousMessage(State state, String pattern) {
		Verdict alertVerdict = Verdict.OK;
		if(!WdDriver.alertMessage.isEmpty()) {
			Matcher matcher = Pattern.compile(pattern).matcher(WdDriver.alertMessage);
			if (matcher.find()) {
				// The widget to remark is the state by default
				Widget w = state;
				// But if the alert was prompt by executing an action in a widget, remark this widget
				if(lastExecutedAction != null  && lastExecutedAction.get(Tags.OriginWidget, null) != null) {
					w = lastExecutedAction.get(Tags.OriginWidget);
				}

				String verdictMsg = String.format("Detected an alert with a suspicious message %s ! Role: %s , Path: %s , WebId: %s , WebTextContent: %s", 
						WdDriver.alertMessage, w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""), w.get(WdTags.WebTextContent, ""));

				alertVerdict = alertVerdict.join(new Verdict(Verdict.SEVERITY_WARNING, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
			}
		}
		return alertVerdict;
	}

	private boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		try {
			double d = Double.parseDouble(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	/**
	 * Check if a widget contains a sibling element.
	 */
	private boolean siblingRoleElement(Widget w, Role role) {
		if(w.parent() == null) return false;
		Widget parent = w.parent();
		for(int i=0; i < parent.childCount(); i++) {
			// If the parent contains a widget child that is not the current widget, return true
			if(parent.child(i).get(Tags.Role, Roles.Widget).equals(role) && parent.child(i) != w) {
				return true;
			}
		}
		return false;
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
					filteredActions.add(ac.clickTypeInto(widget, this.getRandomText(widget), true));
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
					actions.add(ac.clickTypeInto(widget, this.getRandomText(widget), true));
				}else{
					// filtered and not white listed:
					filteredActions.add(ac.clickTypeInto(widget, this.getRandomText(widget), true));
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
		Role role = widget.get(Tags.Role, Roles.Widget);
		if (Role.isOneOf(role, NativeLinker.getNativeClickableRoles())) {
			// Input type are special...
			if (role.equals(WdRoles.WdINPUT)) {
				String type = ((WdWidget) widget).element.type;
				return WdRoles.clickableInputTypes().contains(type);
			}
			return true;
		}

		WdElement element = ((WdWidget) widget).element;
		if (element.isClickable) {
			return true;
		}

		Set<String> clickSet = new HashSet<>(clickableClasses);
		clickSet.retainAll(element.cssClasses);
		return clickSet.size() > 0;
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
		functionalAction = action;
		WdDriver.alertMessage = ""; // reset webdriver alert for next state fetch
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
