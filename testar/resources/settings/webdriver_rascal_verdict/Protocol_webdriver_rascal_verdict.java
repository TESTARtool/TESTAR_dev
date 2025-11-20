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
import org.testar.SutVisualization;
import org.testar.managers.InputDataManager;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Pair;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.NOP;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.actions.WdFillFormAction;
import org.testar.monkey.alayer.actions.WdSelectListAction;
import org.testar.monkey.alayer.actions.WdSelectListAction.JsTargetMethod;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.alayer.exceptions.SystemStartException;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.oracles.DslOracle;
import org.testar.oracles.Oracle;
import org.testar.oracles.OracleSelection;
import org.testar.protocols.WebdriverProtocol;
import org.testar.settings.Settings;

import java.util.*;
import static org.testar.monkey.alayer.Tags.Blocked;
import static org.testar.monkey.alayer.Tags.Enabled;
import static org.testar.monkey.alayer.webdriver.Constants.scrollArrowSize;
import static org.testar.monkey.alayer.webdriver.Constants.scrollThick;

public class Protocol_webdriver_rascal_verdict extends WebdriverProtocol {

	// This list tracks the detected erroneous verdicts to avoid duplicates
	private List<String> listOfDetectedErroneousVerdicts = new ArrayList<>();

	private List<Oracle> extendedOraclesList = new ArrayList<>();
	private List<Oracle> externalOraclesList = new ArrayList<>();

	private Map<Oracle, Boolean> _VACUOUS_PASS = Collections.synchronizedMap(new WeakHashMap<>());

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
	 * This methods is called before each test sequence, before startSystem(),
	 * allowing for example using external profiling software on the SUT
	 *
	 * HTML sequence report will be initialized in the super.preSequencePreparations() for each sequence
	 */
	@Override
	protected void preSequencePreparations() {
		super.preSequencePreparations();
		extendedOraclesList = OracleSelection.loadExtendedOracles(settings.get(ConfigTags.ExtendedOracles));
		for(Oracle extendedOracle : extendedOraclesList) { extendedOracle.initialize(); }
		externalOraclesList = OracleSelection.loadExternalJavaOracles(settings.get(ConfigTags.ExternalOracles));
		for(Oracle externalOracle : externalOraclesList) { externalOracle.initialize(); }
		_VACUOUS_PASS = Collections.synchronizedMap(new WeakHashMap<>());
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
		// system crashes, non-responsiveness and suspicious tags automatically detected!

		//-----------------------------------------------------------------------------
		// MORE SOPHISTICATED ORACLES CAN BE PROGRAMMED HERE (the sky is the limit ;-)
		//-----------------------------------------------------------------------------

		// ... YOU MAY WANT TO CHECK YOUR CUSTOM ORACLES HERE ...

		// "ExtendedOracles" offered by TESTAR in the test.settings or Oracles GUI dialog
		for (Oracle extendedOracle : extendedOraclesList) {
			Verdict extendedVerdict = extendedOracle.getVerdict(state);

			// If the Custom Verdict is not OK and was not detected in a previous sequence
			// return verdict with failure state
			if (extendedVerdict != Verdict.OK && !containsVerdictInfo(listOfDetectedErroneousVerdicts, extendedVerdict.info())) {
				return extendedVerdict;
			}

		}

		// "ExternalOracles" customized by the user and enabled in the test.settings or Oracles GUI dialog
		for (Oracle externalOracle : externalOraclesList) {
			Verdict externalVerdict = externalOracle.getVerdict(state);

			// Track Vacuous Pass from external oracles
			if(externalOracle instanceof DslOracle) {
				if(((DslOracle) externalOracle).isVacuousPass()) {
					_VACUOUS_PASS.putIfAbsent(externalOracle, Boolean.TRUE);
				} else {
					_VACUOUS_PASS.put(externalOracle, Boolean.FALSE);
				}
			}

			// If the Custom Verdict is not OK and was not detected in a previous sequence
			// return verdict with failure state
			if (externalVerdict != Verdict.OK && !containsVerdictInfo(listOfDetectedErroneousVerdicts, externalVerdict.info())) {
				return externalVerdict;
			}

		}

		// If _VACUOUS_PASS does not contain any true, return the base verdict.
		if(!_VACUOUS_PASS.values().stream().anyMatch(Boolean::booleanValue)) {
			return verdict;
		}

		// Else, create a vacuous verdict joining all vacuous oracles from the map.
		Verdict vacuousVerdict = new Verdict(Verdict.Severity.VACUOUS_PASS, "Vacuous Pass:");
		for (Map.Entry<Oracle, Boolean> e : _VACUOUS_PASS.entrySet()) {
			if (Boolean.TRUE.equals(e.getValue())) {
				Oracle oracle = e.getKey();
				vacuousVerdict = vacuousVerdict.join(new Verdict(Verdict.Severity.VACUOUS_PASS, oracle.getMessage()));
			}
		}

		return vacuousVerdict;
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
					filteredActions.add(ac.clickTypeInto(widget, InputDataManager.getRandomTextInputData(widget), true));
				} else {
					filteredActions.add(ac.leftClickAt(widget));
				}
				continue;
			}

			// slides can happen, even though the widget might be blocked
			addSlidingActions(actions, ac, widget);

			// If the element is blocked, Testar can't click on or type in the widget
			if (widget.get(Blocked, false) && !widget.get(WdTags.WebIsShadow, false)) {
				continue;
			}

			// type into text boxes
			if (isAtBrowserCanvas(widget) && isTypeable(widget)) {
				if(whiteListed(widget) || isUnfiltered(widget)){
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
						if(widget.get(Tags.Role, Roles.Widget).equals(WdRoles.WdSELECT)) {
							actions.add(randomFromSelectList(widget));
						} else {
							actions.add(ac.leftClickAt(widget));
						}
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

	private Action randomFromSelectList(Widget w) {
		String elementId = w.get(WdTags.WebId, "");
		String elementName = w.get(WdTags.WebName, "");

		if (!elementId.isEmpty()) {
			return selectRandomValue(elementId, JsTargetMethod.ID, w);
		} else if (!elementName.isEmpty()) {
			return selectRandomValue(elementName, JsTargetMethod.NAME, w);
		}

		return new AnnotatingActionCompiler().leftClickAt(w);
	}

	private Action selectRandomValue(String identifier, JsTargetMethod targetMethod, Widget w) {
		try {
			String lengthQuery;
			String valueQuery;

			switch (targetMethod) {
			case ID:
				lengthQuery = String.format("return document.getElementById('%s').options.length;", identifier);
				break;
			case NAME:
				lengthQuery = String.format("return document.getElementsByName('%s')[0].options.length;", identifier);
				break;
			default:
				return new AnnotatingActionCompiler().leftClickAt(w);
			}

			Object lengthResponse = WdDriver.executeScript(lengthQuery);
			int selectLength = (lengthResponse != null) ? Integer.parseInt(lengthResponse.toString()) : 1;

			int randomIndex = new Random().nextInt(selectLength);

			switch (targetMethod) {
			case ID:
				valueQuery = String.format("return document.getElementById('%s').options[%d].value;", identifier, randomIndex);
				break;
			case NAME:
				valueQuery = String.format("return document.getElementsByName('%s')[0].options[%d].value;", identifier, randomIndex);
				break;
			default:
				return new AnnotatingActionCompiler().leftClickAt(w);
			}

			Object valueResponse = WdDriver.executeScript(valueQuery);

			return (valueResponse != null)
					? new WdSelectListAction(identifier, valueResponse.toString(), w, targetMethod)
							: new AnnotatingActionCompiler().leftClickAt(w);

		} catch (Exception e) {
			return new AnnotatingActionCompiler().leftClickAt(w);
		}
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
		if(finalVerdict.severity() > Verdict.Severity.OK.getValue() && !listOfDetectedErroneousVerdicts.contains(finalVerdict.info().replace("\n", " "))) {
			listOfDetectedErroneousVerdicts.add(finalVerdict.info().replace("\n", " "));
		}
	}

}
