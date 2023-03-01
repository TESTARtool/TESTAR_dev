/**
 * Copyright (c) 2018 - 2023 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2023 Universitat Politecnica de Valencia - www.upv.es
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

import org.languagetool.language.AmericanEnglish;
import org.languagetool.language.BritishEnglish;
import org.languagetool.language.Dutch;
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
import org.testar.reporting.HTMLStateVerdictReport;
import org.testar.verdicts.GenericVerdict;
import org.testar.verdicts.WebVerdict;

import com.google.common.collect.Comparators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
 * - Spell checker in a file list that allows users to ignore. Also prepare a specific directory for the spell checker errors found.
 * - Add URL related with the states
 * 
 * - TODO: JavaScript loop to hang the browser - devTools
 * - TODO: JavaScript refresh browser constantly - devTools
 * - TODO: textarea with rows and columns to detect enter click
 * - TODO: List of possible issues for different verdicts and allow user to customize different oracles for the SUT elements. List like spell checking
 * - TODO: Now draw the widget highlight in all the screenshots of the state. Only in the last HTML report screen.
 * - TODO: Use the state screenshots of the sequences to train and use a model
 * - TODO: screenshot_sequence_x_states vs screenshot_sequence_x_actions
 * 
 * - Instead of joining Verdicts, try to recognize and save different Verdict exception in different sequences.
 */
public class Protocol_webdriver_functional_parabank extends WebdriverProtocol {

	private Action functionalAction = null;
	private Verdict functionalVerdict = Verdict.OK;
	private List<String> listErrorVerdictInfo = new ArrayList<>();

	// Watcher service
	Path downloadsPath;
	private List<String> watchEventDownloadedFiles = new ArrayList<>();

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

		Util.pause(1);
		WdDriver.executeScript("document.getElementsByName('username')[0].setAttribute('value','john');");
		WdDriver.executeScript("document.getElementsByName('password')[0].setAttribute('value','demo');");
		WdDriver.executeScript("document.getElementsByName('login')[0].submit();");
		Util.pause(1);

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

		//TODO: Reader of the logs should use log4j format

		// Reset the list of downloaded files
		watchEventDownloadedFiles = new ArrayList<>();
		// Create a watch service to check which files are downloaded when testing the SUT
		try {
			WatchService watchDownloadService = FileSystems.getDefault().newWatchService();
			downloadsPath = Paths.get(System.getProperty("user.home") + "/Downloads");
			System.out.println("Register WatchService in : " + downloadsPath);
			downloadsPath.register(watchDownloadService, StandardWatchEventKinds.ENTRY_CREATE);
			ExecutorService executor = Executors.newFixedThreadPool(1);
			executor.submit(() -> {
				// Watch all the possible downloaded files
				WatchKey key;
				try {
					while ((key = watchDownloadService.take()) != null) {
						for (WatchEvent<?> event : key.pollEvents()) {
							if(!((Path) event.context()).toString().contains(".tmp") && !((Path) event.context()).toString().contains(".crdownload")) {
								watchEventDownloadedFiles.add(((Path) event.context()).toString());
							}
						}
						key.reset();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This is a helper method used by the default implementation of <code>buildState()</code>
	 * It examines the SUT's current state and returns an oracle verdict.
	 *
	 * @return oracle verdict, which determines whether the state is erroneous and why.
	 */
	@Override
	protected Verdict getVerdict(State state) {
		// Obtain suspicious title verdicts
		Verdict verdict = super.getVerdict(state);

		// If the suspicious title Verdict is not OK but was already detected in a previous sequence
		// Consider as OK and continue the checking functional Verdicts
		// Else return the suspicious Verdict
		String suspiciousTitleVerdictInfo = verdict.info().replace("\n", " ");
		if( listErrorVerdictInfo.stream().anyMatch( verdictInfo -> verdictInfo.contains( suspiciousTitleVerdictInfo ))) {
			verdict = Verdict.OK;
			webConsoleVerdict = Verdict.OK;
		} else {
			return verdict;
		}

		verdict = getUniqueFunctionalVerdict(verdict, state);

		// If the functional Verdict is not OK but was already detected in a previous sequence
		// Consider as OK and continue the checking future state
		String functionalVerdictInfo = verdict.info().replace("\n", " ");
		if( listErrorVerdictInfo.stream().anyMatch( verdictInfo -> verdictInfo.contains( functionalVerdictInfo ))) {
			verdict = Verdict.OK;
		}

		return verdict;
	}

	/**
	 * This method returns a unique functional failure verdict of one state. 
	 * We do not join and do not report multiple failures together.
	 * 
	 * @param verdict
	 * @param state
	 * @return
	 */
	private Verdict getUniqueFunctionalVerdict(Verdict verdict, State state) {
		// Check the functional Verdict that detects the spell checking.
		// Instead of stop the sequence and report a warning verdict,
		// report the information in a specific HTML report
		// and continue testing
		Verdict spellCheckerVerdict = GenericVerdict.verdictSpellChecker(state, WdTags.WebTextContent, new AmericanEnglish());
		if(spellCheckerVerdict != Verdict.OK) HTMLStateVerdictReport.reportStateVerdict(actionCount, state, spellCheckerVerdict);

		// Check the functional Verdict that detects if a downloaded file is empty.
		verdict = watcherFileEmptyFile();
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects dummy buttons to the current state verdict.
		verdict = functionalButtonVerdict(state);
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects select elements without items to the current state verdict.
		verdict = WebVerdict.verdictEmptySelectItemsVerdict(state);
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects select elements with unsorted items to the current state verdict.
		verdict = WebVerdict.verdictUnsortedSelectOptionsVerdict(state);
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects if exists a number with more than X decimals.
		verdict = WebVerdict.verdictNumberWithLotOfDecimals(state, 2);
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects if exists a textArea Widget without length.
		verdict = WebVerdict.verdictTextAreaWithoutLength(state, Arrays.asList(WdRoles.WdTEXTAREA));
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects if a web element does not contain children.
		verdict = WebVerdict.verdictElementWithoutChildren(state, Arrays.asList(WdRoles.WdFORM, WdRoles.WdDIV));
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects if a web radio input contains a unique option.
		verdict = WebVerdict.verdictUniqueRadioInput(state);
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects if a web alert contains a suspicious message.
		verdict = WebVerdict.verdictAlertSuspiciousMessage(state, ".*[lL]ogin.*", lastExecutedAction);
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects if web table contains duplicated rows.
		verdict = WebVerdict.verdictDetectDuplicatedRowsInTable(state);
		if (shouldReturnVerdict(verdict)) return verdict;

		return verdict;
	}

	/**
	 * We want to return the verdict if it is not OK, 
	 * and not on the detected failures list (it's a new failure). 
	 * 
	 * @param verdict
	 * @return
	 */
	private boolean shouldReturnVerdict(Verdict verdict) {
		return verdict != Verdict.OK && listErrorVerdictInfo.stream().noneMatch(info -> info.contains(verdict.info().replace("\n", " ")));
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

	private Verdict watcherFileEmptyFile() {
		Verdict watcherEmptyfileVerdict = Verdict.OK;

		try {
			// If the register watcher detected some downloaded file
			if(!watchEventDownloadedFiles.isEmpty()) {
				// Iterate trough these files to check if they have content
				for(String file : watchEventDownloadedFiles) {
					String filePath = downloadsPath.toAbsolutePath() + File.separator + file;
					BufferedReader br = new BufferedReader(new FileReader(filePath));     
					if (br.readLine() == null) {
						// If last actions was executed over a widget, remark it
						if(lastExecutedAction != null  && lastExecutedAction.get(Tags.OriginWidget, null) != null) {
							Widget w = lastExecutedAction.get(Tags.OriginWidget);

							String verdictMsg = String.format("Detected a downloaded file of 0kb after interacting with! Role: %s , Path: %s , WebId: %s , WebTextContent: %s", 
									WdDriver.alertMessage, w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""), w.get(WdTags.WebTextContent, ""));

							watcherEmptyfileVerdict = watcherEmptyfileVerdict.join(new Verdict(Verdict.SEVERITY_WARNING, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
						} 
						// If there is no widget in the last executed action, just report a message
						else {
							String verdictMsg = String.format("Detected a downloaded file of 0kb!");

							watcherEmptyfileVerdict = watcherEmptyfileVerdict.join(new Verdict(Verdict.SEVERITY_WARNING, verdictMsg));
						}

					}
				}
			}
		} catch(Exception e) {}

		return watcherEmptyfileVerdict;
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

	@Override
	protected boolean isTypeable(Widget widget) {
		Role role = widget.get(Tags.Role, Roles.Widget);
		if (Role.isOneOf(role, NativeLinker.getNativeTypeableRoles())) {

			// Specific class="input" for parasoft SUT
			if(widget.get(WdTags.WebCssClasses, "").contains("input")) {
				return true;
			}

			// Input type are special...
			if (role.equals(WdRoles.WdINPUT)) {
				String type = ((WdWidget) widget).element.type;
				return WdRoles.typeableInputTypes().contains(type.toLowerCase());
			}
			return true;
		}

		return false;
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
