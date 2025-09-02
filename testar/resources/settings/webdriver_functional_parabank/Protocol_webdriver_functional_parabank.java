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
import org.testar.managers.InputDataManager;
import org.testar.monkey.Pair;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.alayer.exceptions.SystemStartException;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.WdElement;
import org.testar.monkey.alayer.webdriver.WdWidget;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.plugin.NativeLinker;
import org.testar.protocols.WebdriverProtocol;
import org.testar.reporting.HTMLStateVerdictReport;
import org.testar.settings.Settings;
import org.testar.verdicts.GenericVerdict;
import org.testar.verdicts.WebVerdict;
import org.testar.monkey.Main;

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

	// Variable to track is a new form appeared in the state (should be reset when TESTAR opens up a new webpage or form)
	private Boolean _pristineStateForm = true;

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
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 * @param   settings  the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings){

       if (settings.get(org.testar.monkey.ConfigTags.AlwaysCompile))
       {
//		try {
//			// bat file that uses tscon.exe to disconnect without stop GUI session
//			File disconnectBatFile = new File(Main.settingsDir + File.separator + "webdriver_functional_parabank" + File.separator + "disconnectRDP.bat").getCanonicalFile();
//
//			// Launch and disconnect from RDP session
//			// This will prompt the UAC permission window if enabled in the System
//			if(disconnectBatFile.exists()) {
//				System.out.println("Running: " + disconnectBatFile);
//				Runtime.getRuntime().exec("cmd /c start \"\" " + disconnectBatFile);
//			} else {
//				System.out.println("THIS BAT DOES NOT EXIST: " + disconnectBatFile);
//			}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		// Wait because disconnect from system modifies internal Screen resolution
//		Util.pause(30);
       }
		super.initialize(settings);
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

		// Reset the functional action and verdict
		functionalAction = null;
		functionalVerdict = Verdict.OK;
		// Reset the form track for the new sequence
		// This will allow to check again the formButtonMustBeDisabledIfNoChangesVerdict verdict
		_pristineStateForm = true;

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
		// If we are in a new URL state, reset the form tracking
		// This will allow to check again the formButtonMustBeDisabledIfNoChangesVerdict verdict
		if(latestState != null 
				&& !latestState.get(WdTags.WebHref, "").isEmpty()
				&& !latestState.get(WdTags.WebHref).equals(WdDriver.getCurrentUrl())) 
		{
			_pristineStateForm = true;
		}

		return super.getState(system);
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
		} else if (verdict.severity() != Verdict.OK.severity()) {
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
		Verdict spellCheckerVerdict = GenericVerdict.SpellChecker(state, WdTags.WebTextContent, new AmericanEnglish(), "");
		if(spellCheckerVerdict != Verdict.OK) HTMLStateVerdictReport.reportStateVerdict(actionCount, state, spellCheckerVerdict);
		
        verdict = WebVerdict.imageResolutionDifferences(state,2,2); // ignore the 1x1 clear.gif image
        if (shouldReturnVerdict(verdict)) return verdict;
        
        
        verdict = GenericVerdict.WidgetAlignmentMetric(state, 50.0);
        if (shouldReturnVerdict(verdict)) return verdict;
        
        /*
        verdict = GenericVerdict.WidgetBalanceMetric(state, 50.0);
        if (shouldReturnVerdict(verdict)) return verdict;
        
        verdict = GenericVerdict.WidgetCenterAlignmentMetric(state, 50.0);
        if (shouldReturnVerdict(verdict)) return verdict;
        
        verdict = GenericVerdict.WidgetConcentricityMetric(state, 50.0);
        if (shouldReturnVerdict(verdict)) return verdict;
       
        verdict = GenericVerdict.WidgetDensityMetric(state, 10.0, 90.0);
        if (shouldReturnVerdict(verdict)) return verdict;
        
        verdict = GenericVerdict.WidgetSimplicityMetric(state, 50.0);
        if (shouldReturnVerdict(verdict)) return verdict;
        */
        
		// Check the functional Verdict that detects if a form button is disabled after modifying the form inputs.
		verdict = formButtonEnabledAfterTypingChangesVerdict(state);
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects if a form button is enabled when it must not.
		verdict = formButtonMustBeDisabledIfNoChangesVerdict(state);
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects if a downloaded file is empty.
		verdict = watcherFileEmptyFile();
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects dummy buttons to the current state verdict.
		verdict = functionalButtonVerdict(state);
		if (shouldReturnVerdict(verdict)) return verdict;
        
        // Check the functional Verdict that detects if two widgets overlap
		// Also, add the roles or the classes of the widget sub-trees are needed to ignore
 		verdict = GenericVerdict.WidgetClashDetection(state, 
				Collections.emptyList(), // ignoredRoles, 
                Collections.emptyList(), // ignoredClasses
                false,  // joinVerdicts	
                false, // checkOnlyLeafWidgets
                true); // checkWebStyles	
 		if (shouldReturnVerdict(verdict)) return verdict;
 
		// Check the functional Verdict that detects correct vertical alignment in role groups
		//verdict = alignmentForWidgetGroups(state, Arrays.asList(WdRoles.WdUL));
		//if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects duplicate or repeated text in descriptions of widgets
        verdict = WebVerdict.DuplicateText(state, "");
		if (shouldReturnVerdict(verdict)) return verdict;
		
		// Check for common test or dummy phrases, such as 'Test' or 'Debug'
        verdict = GenericVerdict.CommonTestOrDummyPhrases(state, WdTags.WebTextContent);
        if (shouldReturnVerdict(verdict)) return verdict;
        
        // Checks for zero numbers in tables
        verdict = WebVerdict.ZeroNumbersInTable(state);
        if (shouldReturnVerdict(verdict)) return verdict;
        
		// Check the functional Verdict that detects HTML or XML tags in descriptions of widgets
		verdict = WebVerdict.HTMLOrXMLTagsInText(state, "%3Cscript%3Econsole\\.error%28%27XSS%20is%20possible%27%29%3B%3C%2Fscript%3E|<memo>alpha beta gamma|.*>console\\.error\\(.*");
		if (shouldReturnVerdict(verdict)) return verdict;
		
        // Check the functional Verdict that detects sensitive data, such as passwords or client secrets
        // https://en.wikipedia.org/wiki/List_of_the_most_common_passwords
        //verdict = GenericVerdict.SensitiveData(state, WdTags.WebTextContent, "123456|123456789|qwerty|password|12345678|111111|123123|1234567890|1234567|qwerty123|000000|1q2w3e|aa12345678|abc123|password1|qwertyuiop|123321|password123");
        verdict = GenericVerdict.SensitiveData(state, WdTags.WebTextContent, "qwerty|qwerty123|1q2w3e|aa12345678|abc123|password1|qwertyuiop|123321|password123");
        if (shouldReturnVerdict(verdict)) return verdict;
        
		// Check the functional Verdict that detects select elements without items to the current state verdict.
		verdict = WebVerdict.EmptySelectItems(state);
		if (shouldReturnVerdict(verdict)) return verdict;
		
        // Add the functional Verdict that detects select elements with only one item to the current state verdict.
        verdict = WebVerdict.SingleSelectItems(state);
        if (shouldReturnVerdict(verdict)) return verdict;

        // Add the functional Verdict that detect that dropdownlist has more than theshold value items
        verdict = WebVerdict.TooManyItemSelectItems(state, 50);
        if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects select elements with unsorted items to the current state verdict.
		verdict = WebVerdict.UnsortedSelectItems(state);
		if (shouldReturnVerdict(verdict)) return verdict;
		
		// Check the functional Verdict that detects select elements with duplicate items to the current state verdict.
		verdict = WebVerdict.DuplicateSelectItems(state);
		if (shouldReturnVerdict(verdict)) return verdict;
		
		// Check the functional Verdict that detects if exists a number with more than X decimals.
		verdict = WebVerdict.NumberWithLotOfDecimals(state, 2, true);
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects if exists a textArea Widget without length.
		verdict = WebVerdict.TextAreaWithoutLength(state, Arrays.asList(WdRoles.WdTEXTAREA, WdRoles.WdINPUT));
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects if a web element does not contain children.
		verdict = WebVerdict.ElementWithoutChildren(state, Arrays.asList(WdRoles.WdFORM, WdRoles.WdDIV, WdRoles.WdSELECT, WdRoles.WdTR, WdRoles.WdOPTGROUP, WdRoles.WdCOLGROUP, WdRoles.WdFIELDSET, WdRoles.WdDL, WdRoles.WdDATALIST, WdRoles.WdBODY));
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects if a web radio input contains a single option.
		verdict = WebVerdict.SingleRadioInput(state);
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects if a web alert contains a suspicious message.
		verdict = WebVerdict.AlertSuspiciousMessage(state, ".*[lL]ogin.*|.*[eE]rror.*|.*[eE]xcep[ct]i[o?]n.*", lastExecutedAction);
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects if web table contains duplicated rows.
		verdict = WebVerdict.DuplicatedRowsInTable(state);
		if (shouldReturnVerdict(verdict)) return verdict;

        // Check The replacement character � (often displayed as a black rhombus with a white question mark) is a symbol found in the Unicode standard at code point U+FFFD in the Specials table. It is used to indicate problems when a system is unable to render a stream of data to correct symbols
        verdict = GenericVerdict.UnicodeReplacementCharacter(state, WdTags.WebTextContent);
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

	/**
	 * Obtain all leaf widgets of the State and verify if they overlap. 
	 * We can completely ignore leaf widgets (by Role or Web Class) if the are part of an undesired sub-tree. 
	 * Or we can consider the parent of the leaf widget to verify if it overlaps with other leaf widgets. 
	 * 
	 * @param state
	 * @param ignoreByRoles
	 * @param ignoreByClasses
	 * @param parentByRoles
	 * @param parentByClasses
	 * @return
	 */
    /*
	private Verdict twoLeafWidgetsOverlap(State state,  List<Role> ignoredRoles,  List<String> ignoredClasses,  List<Role> getParentByRoles,  List<String> getParentByClasses) {
		Verdict widgetsOverlapVerdict = Verdict.OK;

		// Prepare a list that contains all the Rectangles from the leaf widgets
		List<Pair<Widget, Rect>> leafWidgetsRects = new ArrayList<>();
		for(Widget w : state) {
			if(w.childCount() < 1 && w.get(Tags.Shape, null) != null) {
				// Some Widgets, such as Table Data (TD) with span elements, may produce false positives when verifying the leaf widget content. 
				// We can completely ignore the widget or sub-tree widgets that descend from these undesired Roles or Classes. 
				if(isOrDescendFromRole(w, ignoredRoles) || isOrDescendFromClass(w, ignoredClasses)) {
					continue;
				} 
				// With other widgets, such as icons, we may want to consider the parent as a leaf widget. 
				// We can ignore the leaf widget but consider his parent the leaf one. 
				else if(isOrDescendFromRole(w, getParentByRoles) || isOrDescendFromClass(w, getParentByClasses)) {
					if(w.parent() != null && w.parent().get(Tags.Shape, null) != null) {
						leafWidgetsRects.add(new Pair<Widget, Rect>(w.parent(), (Rect)w.parent().get(Tags.Shape)));
					}
				} else {
					leafWidgetsRects.add(new Pair<Widget, Rect>(w, (Rect)w.get(Tags.Shape)));
				}
			}
		}

		for(int i = 0; i < leafWidgetsRects.size(); i++) {
			for(int j = i + 1; j < leafWidgetsRects.size(); j++) {
				if(leafWidgetsRects.get(i) != leafWidgetsRects.get(j)) {
					Rect rectOne = leafWidgetsRects.get(i).right();
					Rect rectTwo = leafWidgetsRects.get(j).right();
					if(checkRectIntersection(rectOne, rectTwo)) {
						Widget firstWidget = leafWidgetsRects.get(i).left();
						String firstMsg = String.format("Title: %s , Role: %s , Path: %s , Desc: %s", 
								firstWidget.get(Tags.Title, "") , firstWidget.get(Tags.Role), firstWidget.get(Tags.Path), firstWidget.get(Tags.Desc, ""));

						Widget secondWidget = leafWidgetsRects.get(j).left();
						String secondMsg = String.format("Title: %s , Role: %s , Path: %s , Desc: %s", 
								secondWidget.get(Tags.Title, "") , secondWidget.get(Tags.Role), secondWidget.get(Tags.Path), secondWidget.get(Tags.Desc, ""));

						String verdictMsg = "Two Widgets Overlapping!" + " First! " + firstMsg + ". Second! " + secondMsg;

						int alpha = 127; // 50% transparent

						// Custom colors of overlapping widgets
						Rect firstWidgetRect = (Rect)firstWidget.get(Tags.Shape);
						java.awt.Color transparentRed = new java.awt.Color(255, 0, 0, alpha);
						firstWidgetRect.setColor(transparentRed);

						Rect secondWidgetRect = (Rect)secondWidget.get(Tags.Shape);
						java.awt.Color transparentYellow = new java.awt.Color(255, 204, 0, alpha);
						secondWidgetRect.setColor(transparentYellow);

						widgetsOverlapVerdict = widgetsOverlapVerdict.join(new Verdict(Verdict.SEVERITY_WARNING_UI_VISUAL_OR_RENDERING_FAULT, verdictMsg, Arrays.asList(firstWidgetRect, secondWidgetRect)));
					}
				}
			}
		}

		return widgetsOverlapVerdict;
	}

    */

	private boolean checkRectIntersection(Rect r1, Rect r2) {
		// TODO: If some widget contains a negative x or y axis
		// This checking can provoke a false positive recognition
		return !(r1.x() + r1.width() <= r2.x() ||
				r1.y() + r1.height() <= r2.y() ||
				r2.x() + r2.width() <= r1.x() ||
				r2.y() + r2.height() <= r1.y()); 
	}

	private boolean isOrDescendFromRole(Widget widget, List<Role> roles) {
		if (roles.contains(widget.get(Tags.Role, Roles.Widget))) return true;
		else if(widget.parent() == null) return false;
		else return isOrDescendFromRole(widget.parent(), roles);
	}
	private boolean isOrDescendFromClass(Widget widget, List<String> webClasses) {
		if (webClasses.stream().anyMatch(str -> widget.get(WdTags.WebCssClasses, "").contains(str))) return true;
		else if(widget.parent() == null) return false;
		else return isOrDescendFromClass(widget.parent(), webClasses);
	}

	// TODO: Improve by using the widgetAlignmentMetric functionality
	private Verdict alignmentForWidgetGroups(State state, List<Role> roleGroups) {
		Verdict alignmentVerdict = Verdict.OK;

		for(Widget w : state) {
			// If we found a widget group role on which verify the alignment 
			// and the widget contains a visible children
			if(roleGroups.contains(w.get(Tags.Role, Roles.Widget)) && w.childCount() > 0) {
				// Obtain the max depth of this widget sub-tree
				int maxDepth = getMaxWidgetTreeDepth(w);
				// Depth 1 corresponds to the original widget we descend
				// Then, we start at depth 2
				for(int i = 2; i <= maxDepth; i++) {
					// Get all sub-tree widgets at depth i
					List<Widget> groupWidgets = getWidgetsAtDepth(w, i);
					// Get the axis alignment of the first widget
					double verticalPosAlignment = ((Rect)groupWidgets.get(0).get(Tags.Shape)).x();
					double horizontalPosAlignment = ((Rect)groupWidgets.get(0).get(Tags.Shape)).y();
					// Verify all sub-tree widgets are in the same alignment
					for(Widget verifyWidget : groupWidgets) {
						if(verifyWidget.get(Tags.Shape, null) != null) {
							Rect childRect = (Rect)verifyWidget.get(Tags.Shape);

							Verdict verticalAlignmentVerdict = Verdict.OK;
							Verdict horizontalAlignmentVerdict = Verdict.OK;

							if(childRect.x() != verticalPosAlignment) {
								String verdictMsg = String.format("Invalid Vertical Alignment Detected! Title: %s , Role: %s , Path: %s , Desc: %s", 
										verifyWidget.get(Tags.Title, "") , verifyWidget.get(Tags.Role), verifyWidget.get(Tags.Path), verifyWidget.get(Tags.Desc, ""));

								verticalAlignmentVerdict = new Verdict(Verdict.Severity.WARNING, verdictMsg, Arrays.asList((Rect)verifyWidget.get(Tags.Shape)));
							}

							if(childRect.y() != horizontalPosAlignment) {
								String verdictMsg = String.format("Invalid Horizontal Alignment Detected! Title: %s , Role: %s , Path: %s , Desc: %s", 
										verifyWidget.get(Tags.Title, "") , verifyWidget.get(Tags.Role), verifyWidget.get(Tags.Path), verifyWidget.get(Tags.Desc, ""));

								horizontalAlignmentVerdict = new Verdict(Verdict.Severity.WARNING, verdictMsg, Arrays.asList((Rect)verifyWidget.get(Tags.Shape)));
							}

							if(verticalAlignmentVerdict != Verdict.OK && horizontalAlignmentVerdict != Verdict.OK) {
								alignmentVerdict = alignmentVerdict.join(verticalAlignmentVerdict).join(horizontalAlignmentVerdict);
							}
						}
					}
				}
			}
		}

		return alignmentVerdict;
	}

	private int getMaxWidgetTreeDepth(Widget widget) {
		int maxChildDepth = 0;
		if (widget.childCount() > 0) {
			for (int i = 0; i < widget.childCount(); i++) {
				Widget child = widget.child(i);
				int childDepth = getMaxWidgetTreeDepth(child);
				maxChildDepth = Math.max(maxChildDepth, childDepth);
			}
		}

		return maxChildDepth + 1;
	}

	private List<Widget> getWidgetsAtDepth(Widget original, int targetDepth) {
		List<Widget> result = new ArrayList<>();
		getWidgetsAtDepthRecursive(original, 1, targetDepth, result);
		return result;
	}

	private void getWidgetsAtDepthRecursive(Widget widget, int currentDepth, int targetDepth, List<Widget> result) {
		if (currentDepth == targetDepth) {
			result.add(widget);
		} else if (currentDepth < targetDepth) {
			if (widget.childCount() > 0) {
				for (int i = 0; i < widget.childCount(); i++) {
					Widget child = widget.child(i);
					getWidgetsAtDepthRecursive(child, currentDepth + 1, targetDepth, result);
				}
			}
		}
	}

	private Verdict functionalButtonVerdict(State state) {
		// If the last executed action is a click on a web button
		if(functionalAction != null 
				&& functionalAction.get(Tags.OriginWidget, null) != null 
				&& functionalAction.get(Tags.Desc, "").contains("Click")
				&& functionalAction.get(Tags.OriginWidget).get(Tags.Role, Roles.Widget).equals(WdRoles.WdBUTTON)) {

			// Compare previous and current state AbstractIDCustom identifiers
			// to determine if interacting with the button does nothing in the SUT state
			String previousStateId = latestState.get(Tags.AbstractID, "NoPreviousId");
			String currentStateId = state.get(Tags.AbstractID, "NoCurrentId");

			// NOTE 1: Because we are comparing the states using the AbstractIDCustom property, 
			// it is important to consider the used abstraction: test.settings - AbstractStateAttributes (WebWidgetId, WebWidgetTextContent)
			// NOTE 2: A button alert can prompt a message, but TESTAR saves the text message and returns to the state
			// this is also something to consider :/
			// TODO: Improve the existence of web alerts messages within the State ID
			if(previousStateId.equals(currentStateId) && WdDriver.alertMessage.isEmpty()) {
				Widget w = functionalAction.get(Tags.OriginWidget);
				String verdictMsg = String.format("Dummy Button detected! Role: %s , Path: %s , Desc: %s", 
						w.get(Tags.Role), w.get(Tags.Path), w.get(Tags.Desc, ""));

				functionalVerdict = new Verdict(Verdict.Severity.WARNING_UI_FLOW_FAULT, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape)));
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

							watcherEmptyfileVerdict = watcherEmptyfileVerdict.join(new Verdict(Verdict.Severity.WARNING_DATA_DATA_VALUE_NOT_STORED_OR_DELETED, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
						} 
						// If there is no widget in the last executed action, just report a message
						else {
							String verdictMsg = String.format("Detected a downloaded file of 0kb!");

							watcherEmptyfileVerdict = watcherEmptyfileVerdict.join(new Verdict(Verdict.Severity.WARNING_DATA_DATA_VALUE_NOT_STORED_OR_DELETED, verdictMsg));
						}

					}
				}
			}
		} catch(Exception e) {}

		return watcherEmptyfileVerdict;
	}

	private Verdict formButtonEnabledAfterTypingChangesVerdict(State state) {
		List<String> descriptionsOfWidgetsThatShouldBeEnabledWhenFormHasUnsavedChanges = new ArrayList<>();
		descriptionsOfWidgetsThatShouldBeEnabledWhenFormHasUnsavedChanges.add("submit-button");
		descriptionsOfWidgetsThatShouldBeEnabledWhenFormHasUnsavedChanges.add("save-button");
		descriptionsOfWidgetsThatShouldBeEnabledWhenFormHasUnsavedChanges.add("cancel-button");

		// If the last executed action is typing in a son of a form
		if(functionalAction != null
				&& functionalAction.get(Tags.OriginWidget, null) != null 
				&& functionalAction.get(Tags.Desc, "").startsWith("Type")
				&& isSonOfFormWidget(functionalAction.get(Tags.OriginWidget))
				&& functionalAction.get(Tags.OriginWidget).get(WdTags.WebMaxLength) > 0) 
		{
			// Because the form was altered, update the tracking variable
			// this will avoid false positives on formButtonMustBeDisabledIfNoChangesVerdict
			_pristineStateForm = false;

			for(Widget w : state) {
				if (descriptionsOfWidgetsThatShouldBeEnabledWhenFormHasUnsavedChanges.contains(w.get(Tags.Desc, ""))) {
					// Check that widgets are turned on when the last action was a type action in a input field
					if (w.get(WdTags.WebIsDisabled, false))	{
						String verdictMsg = String.format("Form Widget is NOT enabled while it should be! Role: %s , Path: %s , Desc: %s", 
								w.get(Tags.Role), w.get(Tags.Path), w.get(Tags.Desc, ""));
						return new Verdict(Verdict.Severity.WARNING_UI_ITEM_VISIBILITY_FAULT, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape)));
					}
				}
			}
		}

		return Verdict.OK;
	}

	private Verdict formButtonMustBeDisabledIfNoChangesVerdict(State state) {
		List<String> descriptionsOfWidgetsThatShouldBeDisabledIfFormHasNoChanges = new ArrayList<>();
		descriptionsOfWidgetsThatShouldBeDisabledIfFormHasNoChanges.add("submit-button");
		descriptionsOfWidgetsThatShouldBeDisabledIfFormHasNoChanges.add("save-button");
		descriptionsOfWidgetsThatShouldBeDisabledIfFormHasNoChanges.add("cancel-button");

		// If we are in a state with an unaltered form, apply the verdict
		if (_pristineStateForm)
		{
			for(Widget w : state) {
				if (descriptionsOfWidgetsThatShouldBeDisabledIfFormHasNoChanges.contains(w.get(Tags.Desc, ""))) {
					// check that widgets are turned off when there was no action executed yet
					if (!w.get(WdTags.WebIsDisabled, false))	{
						String verdictMsg = String.format("Form Widget IS enabled while it should not be! Role: %s , Path: %s , Desc: %s", 
								w.get(Tags.Role), w.get(Tags.Path), w.get(Tags.Desc, ""));
						return new Verdict(Verdict.Severity.WARNING_UI_ITEM_VISIBILITY_FAULT, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape)));
					}

				}
			}
		}

		return Verdict.OK;
	}

	private boolean isSonOfFormWidget(Widget widget) {
		if(widget.parent() == null) return false;
		else if (widget.parent().get(Tags.Role, Roles.Widget).equals(WdRoles.WdFORM)) return true;
		else return isSonOfFormWidget(widget.parent());
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
		if(widget.get(WdTags.WebIsDisabled, false)) return false;

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
		// If the web widget contains the "readonly" attribute, we do not want to derive type actions
		if(widget.get(WdTags.WebAttributeMap, null) != null && widget.get(WdTags.WebAttributeMap).containsKey("readonly")) {
			return false;
		}

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
		if(getFinalVerdict().severity() > Verdict.Severity.OK.getValue() && !listErrorVerdictInfo.contains(getFinalVerdict().info().replace("\n", " "))) {
			listErrorVerdictInfo.add(getFinalVerdict().info().replace("\n", " "));
		}
	}
}
