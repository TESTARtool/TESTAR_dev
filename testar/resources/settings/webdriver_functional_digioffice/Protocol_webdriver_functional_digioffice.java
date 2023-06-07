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

import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v109.performance.Performance;
import org.openqa.selenium.devtools.v109.performance.model.Metric;
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
import org.testar.verdicts.GenericVerdict;
import org.testar.verdicts.WebVerdict;
import org.testar.monkey.Settings;
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

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.parser.ParseException;
import org.apache.logging.log4j.core.parser.XmlLogEventParser;


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
public class Protocol_webdriver_functional_digioffice extends WebdriverProtocol {
	private Action functionalAction = null;
	private Verdict functionalVerdict = Verdict.OK;
	private List<String> listErrorVerdictInfo = new ArrayList<>();
	private DevTools devTools;
    private List<Metric> metricList = new ArrayList<>(); 

	// Watcher service
	Path downloadsPath;
	private List<String> watchEventDownloadedFiles = new ArrayList<>();

	// Variable to track is a new form appeared in the state (should be reset when TESTAR opens up a new webpage or form)
	private Boolean _pristineStateForm = true;
    private Boolean _isDetailPage = false;

/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 * @param   settings  the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings){

       if (settings.get(org.testar.monkey.ConfigTags.AlwaysCompile))
       {
		try {
			// bat file that uses tscon.exe to disconnect without stop GUI session
			File disconnectBatFile = new File(Main.settingsDir + File.separator + "webdriver_functional_digioffice" + File.separator + "disconnectRDP.bat").getCanonicalFile();

			// Launch and disconnect from RDP session
			// This will prompt the UAC permission window if enabled in the System
			if(disconnectBatFile.exists()) {
				System.out.println("Running: " + disconnectBatFile);
				Runtime.getRuntime().exec("cmd /c start \"\" " + disconnectBatFile);
			} else {
				System.out.println("THIS BAT DOES NOT EXIST: " + disconnectBatFile);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		// Wait because disconnect from system modifies internal Screen resolution
		Util.pause(30);
       }
		super.initialize(settings);
	}


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

		// Reset the functional action and verdict
		functionalAction = null;
		functionalVerdict = Verdict.OK;
		// Reset the form track for the new sequence
		// This will allow to check again the formButtonMustBeDisabledIfNoChangesVerdict verdict
		_pristineStateForm = true;

        // Initialize devtools for performance verdict
        // Initialize a session of the web developers tool
	    devTools = ((HasDevTools) WdDriver.getRemoteWebDriver()).getDevTools();
	    devTools.createSessionIfThereIsNotOne();

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
        String webUrl = WdDriver.getCurrentUrl();
        
		// If we are in a new URL state, reset the form tracking
		// This will allow to check again the formButtonMustBeDisabledIfNoChangesVerdict verdict
		if(latestState != null 
				&& !latestState.get(WdTags.WebHref, "").isEmpty()
				&& !latestState.get(WdTags.WebHref).equals(webUrl)) 
		{
           System.out.println("New form : " + webUrl);
          
          // Give UI some time to initialize the new form, otherwise false positives arise because not everything is in sync
          wait(4000);
          
          // Check RecID is present in the web url. If RecID is not present, but Entity is present in querystring then it is a new entity detailpage in DigiOffice.
          if(webUrl.contains("RecID=") && webUrl.contains("Entity=")) {
              _pristineStateForm = true;
              _isDetailPage = true;
              System.out.println("Set pristineState to TRUE");
          }
          else {
              _pristineStateForm = false;
              _isDetailPage = false;
              System.out.println("Set pristineState to FALSE");
          }
        }
        
        devTools.send(Performance.enable(Optional.empty()));
	    metricList = devTools.send(Performance.getMetrics());
	   
		return super.getState(system);
	}

    public static void wait(int ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
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
        
        loadSensitiveDataListFromFile();
        
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

    private Verdict detectSlowPerformance(double taskDurationThreshold)
    {
//metricList Info https://pptr.dev/next/api/puppeteer.page.metrics
        // Print all devTools performance metrics
//	    for(Metric m : metricList) {
//		  System.out.println(m.getName() + " = " + m.getValue());
//	  }
       Verdict verdict = Verdict.OK;
       double taskDuration = 0.0;
       Optional<Metric> metric = metricList.stream().filter(m -> "TaskDuration".equals(m.getName())).findFirst();
       
       if (metric.isPresent())
       {
           taskDuration = metric.get().getValue().doubleValue();
           if (taskDuration > taskDurationThreshold)
           {
               String verdictMsg = String.format("Detected slow pageload with metric TaskDuration which is combined duration of all tasks performed by browser. TaskDuration: %.02f seconds", taskDuration);
			  verdict = verdict.join(new Verdict(Verdict.SEVERITY_WARNING_RESOURCE_PERFORMANCE_ISSUE, verdictMsg));
           }
       }
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


	// Detect UI items that contain dummy, test or debug text
	// BAD:  Debug
    //       Dummy
    //       test
	// GOOD: 
	// When programmers debugging a program where they could only notice the problem in an other environment than the developer machine,
    // then they sometimes add code which show a message or value with a phrase or word such as 'debug' or 'test'. If the programmer
    // forget to remove that temporary code, then this text can be shown in a production release of the software.
	private Verdict detectCommonTestOrDummyPhrases(State state)
	{
		Verdict verdict = Verdict.OK;
		String patternRegex = "[Dd]ummy|[Tt]est][Dd]ebug";
		Pattern pattern = Pattern.compile(patternRegex);
		
		for(Widget w : state) {
			String desc = w.get(WdTags.WebTextContent, "");
			Matcher matcher = pattern.matcher(desc);

			if (matcher.find()) {
				String verdictMsg = String.format("Detected debug or test data values! Role: %s , Path: %s , WebId: %s , Desc: %s , WebTextContent: %s", 
						w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""), w.get(WdTags.Desc, ""), w.get(WdTags.WebTextContent, ""));
				verdict = verdict.join(new Verdict(Verdict.SEVERITY_WARNING_UI_ITEM_WRONG_VALUE_FAULT, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
			}
		}
		return verdict;
	}

    // Detect UI items that contain sensitive texts, such as passwords
	// BAD:  Pa$$w0rd
    //       7n_8Q~DyBzoE4aqBBXA-B7gVdX~5zFCZk5yVvb7b
	// GOOD: *******
	// Sensitive data such as Passwords and ClientSecrets should not be visible in their original form the UI, database, configuration or log files.
    // This data should be encrypted if they must be shown or stored somewhere.
    // There are two options to provide a list of sensitive data:
    // 1. Fill a ../Settings/SensitiveDataList.txt file with all sensitive text.
    // 2. Give a regular expression with matches sensitive text
    // Using the SensitiveDataList.txt file is the preferred way, because the sensitive data is then not in the protocol and you don't have to rewrite the data as a regular expression.
	private Verdict detectSensitiveData(State state, String sensitiveTextPatternRegEx)
	{
		Verdict verdict = Verdict.OK;
		Pattern pattern = Pattern.compile(sensitiveTextPatternRegEx);
		
		for(Widget w : state) {
			String desc = w.get(WdTags.WebTextContent, ""); //TODO: comments in html could leak passwords
            Matcher matcher = pattern.matcher(desc);

            if (!desc.isEmpty() && (sensitiveDataList.contains(desc) || matcher.find()))
            {
            	String verdictMsg = String.format("Detected sensitive data values! Role: %s , Path: %s , WebId: %s , Desc: %s , WebTextContent: %s", 
						w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""), w.get(WdTags.Desc, ""), w.get(WdTags.WebTextContent, ""));
				verdict = verdict.join(new Verdict(Verdict.SEVERITY_WARNING_UI_ITEM_WRONG_VALUE_FAULT, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
            }
		}
		return verdict;
	}

    // Load the sensitive data file from the settings directory
	public static String sensitiveDataListFile = Main.settingsDir + File.separator + "SensitiveDataList.txt";

	// Custom load sensitive data
	private static List<String> sensitiveDataList = new ArrayList<>();
	
    private static void loadSensitiveDataListFromFile()
    {
        try
        {
        	sensitiveDataList.clear();
            File file = new File(sensitiveDataListFile);
            if(file.exists() && !file.isDirectory()) 
            {
              Scanner s = new Scanner(file);
           
              while (s.hasNextLine()){
            	  sensitiveDataList.add(s.nextLine());
              }
              
              s.close();
            }
        }
        catch(java.io.FileNotFoundException ex)
        {
            // ignore errors
        }
        Collections.sort(sensitiveDataList);
    }

	// Detect text that contains zero values in tables
	// BAD:  0.00
    //       $ 0.00
	// GOOD: 
	// If zero values are shown in tables/grids, then this clutter the grid. It is better to don't display zero values. 
	// Exception to this rule may be row totals or column totals. 
	private Verdict detectZeroNumbers(State state)
	{
		Verdict verdict = Verdict.OK;
		String patternRegex = "\\s0[\\.,]0\\s";
		Pattern pattern = Pattern.compile(patternRegex);
		
		for(Widget w : state) {
			if (isSonOfTD(w))
            {
				String desc = w.get(WdTags.WebTextContent, "");
				Matcher matcher = pattern.matcher(desc);
			
				if (matcher.find()) {
					String verdictMsg = String.format("Detected zero values in table/grids! Role: %s , Path: %s , WebId: %s , Desc: %s , WebTextContent: %s", 
							w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""), w.get(WdTags.Desc, ""), w.get(WdTags.WebTextContent, ""));
					verdict = verdict.join(new Verdict(Verdict.SEVERITY_WARNING_UI_VISUAL_OR_RENDERING_FAULT, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
				}
			}
		}
		return verdict;
	}
	
	// Detect text decoraged with ^ charachters, this means that this text has no translation key and will not be translated when UI language is changed in DigiOffice
	// BAD:  ^Untranslated text^
    //       ^UntranslatedText^
	// GOOD: Untranslated text
	private Verdict detectUntranslatedText(State state)
	{
		Verdict verdict = Verdict.OK;
		String patternRegex = "\\^.*\\^";
		Pattern pattern = Pattern.compile(patternRegex);
		
		for(Widget w : state) {
			// TODO: Is WebValue a good tag? Should the same tags be used as the suspicious text verdicts which are set in the UI?
			String desc = w.get(WdTags.WebValue, "");
			Matcher matcher = pattern.matcher(desc);
			
			if (matcher.find()) {
				String verdictMsg = String.format("Detected untranslated tags in widget! Role: %s , Path: %s , WebId: %s , Desc: %s , WebValue: %s", 
						w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""), w.get(WdTags.Desc, ""), w.get(WdTags.WebValue, ""));
				verdict = verdict.join(new Verdict(Verdict.SEVERITY_WARNING_UI_TRANSLATION_OR_SPELLING_ISSUE, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
			}
		}
		return verdict;
	}

	// Detect the replacement character � (often displayed as a black rhombus with a white question mark) is a symbol found in the Unicode standard at code point U+FFFD in the Specials table. 
    // It is used to indicate problems when a system is unable to render a stream of data to correct symbols
    // https://en.wikipedia.org/wiki/Specials_(Unicode_block)
    // U+FFFD � REPLACEMENT CHARACTER used to replace an unknown, unrecognized, or unrepresentable character
	// BAD:  �
    //       f�r
	// GOOD: für
    private Verdict detectUnicodeReplacementCharacter(State state)
	{
		Verdict verdict = Verdict.OK;
		String patternRegex = ".*�.*"; // Look for a Unicode Replacement character.
		Pattern pattern = Pattern.compile(patternRegex);
		
		for(Widget w : state) {
			String desc = w.get(WdTags.WebValue, "");
			Matcher matcher = pattern.matcher(desc);
			
			if (matcher.find()) {
				String verdictMsg = String.format("Detected Unicode Replacement Character in widget! Role: %s , Path: %s , WebId: %s , Desc: %s , WebValue: %s", 
						w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""), w.get(WdTags.Desc, ""), w.get(WdTags.WebValue, ""));
				verdict = verdict.join(new Verdict(Verdict.SEVERITY_WARNING_UI_TRANSLATION_OR_SPELLING_ISSUE, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
			}
		}
		return verdict;
	}

	// Detect widgets that should be in sync
	// Examples:
	//       Form title should match match title bar
    //       Recordcount of grid should match with number of records shown in grid when record count is lower than 50 (due to paging feature in grid)
	//		 Column total does should match the sum of all cell values in column
	//       Warning icon should match the warning message
    //		 Focused navigation item should match Title
	//
	private Verdict detectWidgetsThatShouldBeInSync(State state)
	{
		Verdict verdict = Verdict.OK;

		// Page title
		String queryPageTitle = "return document.navContent.document.querySelector('#ctl00_cphDetail_ctl00_pnlMenu > div.ContentTitle').innerText";
		String pageTitle = (String) WdDriver.executeScript(queryPageTitle);

		// Navigation selected
		String queryNavigationItem = "elm = document.querySelector('ul.treeview'); if (elm) return elm.querySelector('.treenode-selected > span > a').firstChild.textContent";
		String navItem = (String) WdDriver.executeScript(queryNavigationItem);

		if (pageTitle != null && navItem != null && (!pageTitle.isEmpty() || !navItem.isEmpty()))
		{
            System.out.println("Comparing page title '" + pageTitle + "' and content title '" + navItem + "'");
            
			if (!pageTitle.equals(navItem))
			{
				String verdictMsg = String.format("Page title and navigation title are not the same! PageTitle: %s , NavigationItem: %s", 
						pageTitle, navItem);
				verdict = verdict.join(new Verdict(Verdict.SEVERITY_WARNING_UI_ITEM_WRONG_VALUE_FAULT, verdictMsg));
			}
		}

        String checkTableTDsAndTableCount = "for(let a of document.navContent.document.querySelectorAll('.table-data')) {recCount = Number(a.parentElement.parentElement.parentElement.querySelector('.total').innerText.replace(/\\D/g, '')); if (recCount <= 50 && Math.max(a.getElementsByTagName('tr').length-1,0) != recCount) { return 'Table has ' + (a.getElementsByTagName('tr').length-1).toString() + ' rows and tablecount '+ recCount +' are not equal';}}";
        String checkTableTDsAndTableCountMessage = (String)WdDriver.executeScript(checkTableTDsAndTableCount); // if table and tablecount are not equal, then this is reported
        
        if (checkTableTDsAndTableCountMessage != null && !checkTableTDsAndTableCountMessage.isEmpty())
        {
            System.out.println("Comparing checkTableTDsAndTableCountMessage: " + checkTableTDsAndTableCountMessage);
            verdict = verdict.join(new Verdict(Verdict.SEVERITY_WARNING_UI_ITEM_WRONG_VALUE_FAULT, checkTableTDsAndTableCountMessage));
        }
		return verdict;
	}


    // TODO: Gives false positives when opening Div style forms, such as Notitie fields. The OK button seems to be working in the UI, but TESTAR doesn't see that as a change.'
    // Dummy button
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

				functionalVerdict = new Verdict(Verdict.SEVERITY_WARNING_UI_FLOW_FAULT, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape)));
			}

			// getState and getVerdict are executed more than one time after executing an action. 
			// Then previous state becomes current state in the second execution...
			// Set to null to prevent multiple checks.
			// We need to fix this in the TESTAR internal flow :D
			functionalAction = null;
		}

		return functionalVerdict;
	}

	// https://www.baeldung.com/java-file-extension
	public Optional<String> getExtensionByStringHandling(String filename) {
	    return Optional.ofNullable(filename)
	      .filter(f -> f.contains("."))
	      .map(f -> f.substring(filename.lastIndexOf(".") + 1));
	}

	private Verdict watcherFileEmptyFile() {
		Verdict watcherEmptyfileVerdict = Verdict.OK;

		try {
			// If the register watcher detected some downloaded file
			if(!watchEventDownloadedFiles.isEmpty()) {
				// Iterate trough these files to check if they have content
				for(String file : watchEventDownloadedFiles) {
                    if (file != null)
                    {
    					String filePath = downloadsPath.toAbsolutePath() + File.separator + file;
    					
    					Optional<String> fileExtension = getExtensionByStringHandling(file);
    					
    					// Check that filename has an file extension. If not, then report this as a warning.
    					if (!fileExtension.isPresent() || (fileExtension.isPresent() && fileExtension.get().isEmpty()))
    					{
    						String verdictMsg = String.format("Detected a downloaded file '%s' without file extension!", file);
    
    						watcherEmptyfileVerdict = watcherEmptyfileVerdict.join(new Verdict(Verdict.SEVERITY_WARNING_DATA_DATA_VALUE_NOT_STORED_OR_DELETED, verdictMsg));
    					}
    					
    					BufferedReader br = new BufferedReader(new FileReader(filePath));     
    					if (br.readLine() == null) {
    						// If last actions was executed over a widget, remark it
    						if(lastExecutedAction != null  && lastExecutedAction.get(Tags.OriginWidget, null) != null) {
    							Widget w = lastExecutedAction.get(Tags.OriginWidget);
    
    							String verdictMsg = String.format("Detected a downloaded file of 0kb after interacting with! Role: %s , Path: %s , WebId: %s , WebTextContent: %s", 
    									WdDriver.alertMessage, w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""), w.get(WdTags.WebTextContent, ""));
    
    							watcherEmptyfileVerdict = watcherEmptyfileVerdict.join(new Verdict(Verdict.SEVERITY_WARNING_DATA_DATA_VALUE_NOT_STORED_OR_DELETED, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
    						} 
    						// If there is no widget in the last executed action, just report a message
    						else {
    							String verdictMsg = String.format("Detected a downloaded file of 0kb!");
    
    							watcherEmptyfileVerdict = watcherEmptyfileVerdict.join(new Verdict(Verdict.SEVERITY_WARNING_DATA_DATA_VALUE_NOT_STORED_OR_DELETED, verdictMsg));
    						}
    
    					}
                    }
				}
			}
		} catch(Exception e) {}

		return watcherEmptyfileVerdict;
	}

	private Verdict formButtonEnabledAfterTypingChangesVerdict(State state) {
		List<String> descriptionsOfWidgetsThatShouldBeEnabledWhenFormHasUnsavedChanges = new ArrayList<>();
		descriptionsOfWidgetsThatShouldBeEnabledWhenFormHasUnsavedChanges.add("btnOpslaan");
		descriptionsOfWidgetsThatShouldBeEnabledWhenFormHasUnsavedChanges.add("btnOpslaanEnSluiten");
		descriptionsOfWidgetsThatShouldBeEnabledWhenFormHasUnsavedChanges.add("btnOpslaanEnNieuw");

		// If the last executed action is typing in a son of a form
		if(_isDetailPage
                && functionalAction != null
				&& functionalAction.get(Tags.OriginWidget, null) != null 
				&& (functionalAction.get(Tags.Desc, "").startsWith("Type") || functionalAction.get(Tags.Desc, "").startsWith("Paste"))
				&& isSonOfFormWidget(functionalAction.get(Tags.OriginWidget))
				&& functionalAction.get(Tags.OriginWidget).get(WdTags.WebMaxLength) > 0) 
		{
			// Because the form was altered, update the tracking variable
			// this will avoid false positives on formButtonMustBeDisabledIfNoChangesVerdict
			_pristineStateForm = false;

			for(Widget w : state) {
				if (descriptionsOfWidgetsThatShouldBeEnabledWhenFormHasUnsavedChanges.contains(w.get(Tags.Desc, ""))) {
					// Check that widgets are turned on when the last action was a type action in a input field
					if (isDisabled(w))	{
						String verdictMsg = String.format("Form widget is not enabled while it should be! Role: %s , Path: %s , Desc: %s", 
								w.get(Tags.Role), w.get(Tags.Path), w.get(Tags.Desc, ""));
						return new Verdict(Verdict.SEVERITY_WARNING_UI_ITEM_VISIBILITY_FAULT, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape)));
					}
				}
			}
		}

		return Verdict.OK;
	}
	
	private boolean isDisabled(Widget w)
	{
		//return w.get(WdTags.WebIsDisabled, false);
        return w.get(WdTags.WebCssClasses, "").contains("item-disabled");
	}

	private Verdict formButtonMustBeDisabledIfNoChangesVerdict(State state) {
		List<String> descriptionsOfWidgetsThatShouldBeDisabledIfFormHasNoChanges = new ArrayList<>();
		descriptionsOfWidgetsThatShouldBeDisabledIfFormHasNoChanges.add("btnOpslaan");
		descriptionsOfWidgetsThatShouldBeDisabledIfFormHasNoChanges.add("btnOpslaanEnSluiten");
		descriptionsOfWidgetsThatShouldBeDisabledIfFormHasNoChanges.add("btnOpslaanEnNieuw");

		// If we are in a state with an unaltered form, apply the verdict
		if (_isDetailPage && _pristineStateForm)
		{
			for(Widget w : state) {
				if (descriptionsOfWidgetsThatShouldBeDisabledIfFormHasNoChanges.contains(w.get(Tags.Desc, ""))) {
					// check that widgets are turned off when there was no action executed yet
					if (!isDisabled(w))	{
						String verdictMsg = String.format("Form widget is enabled while it should not be! Role: %s , Path: %s , Desc: %s", 
								w.get(Tags.Role), w.get(Tags.Path), w.get(Tags.Desc, ""));
						return new Verdict(Verdict.SEVERITY_WARNING_UI_ITEM_VISIBILITY_FAULT, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape)));
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

	private boolean isSonOfTD(Widget widget) {
		if(widget.parent() == null) return false;
		else if (widget.parent().get(Tags.Role, Roles.Widget).equals(WdRoles.WdTD)) return true;
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
            if(isAtBrowserCanvas(widget) && isMenuItemClickable(widget)) {
        	   	actions.add(ac.leftClickAt(widget));
            }

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
					actions.add(ac.clickTypeInto(widget, InputDataManager.getRandomTextInputData(widget), true));
                    actions.add(ac.pasteTextInto(widget, InputDataManager.getRandomTextFromCustomInputDataFile(System.getProperty("user.dir") + "/settings/custom_input_data.txt"), true));
                    //actions.add(ac.pasteTextInto(widget, "\"'[]$(>%)@#>+_|:* $?{}/\\,Aa", true));
                    //actions.add(ac.pasteTextInto(widget, "<memo>bla bla bla", true)); // #942
                    //actions.add(ac.pasteTextIntoo(widget, "é € ý ì", true)); 
                    //actions.add(ac.pasteTextInto(widget, "@#", true)); // #70973
                    //actions.add(ac.pasteTextInto(widget, "01-01-2020", true)); // #70296
                    //actions.add(ac.pasteTextInto(widget, "31-12-2023", true)); // #70296
                    //actions.add(ac.pasteTextInto(widget, "<script>x=5;alert(`XSS ${x} mogelijk`);</script>", true)); // #58157
                    //actions.add(ac.pasteTextInto(widget, new String(new char[260]).replace("\0", "A"), true)); // #71074
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
                        //actions.add(ac.rightClickAt(widget));
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
        // If the web widget contains the "readonly" attribute, we do not want to derive type actions
        if(widget.get(WdTags.WebAttributeMap, null) != null && widget.get(WdTags.WebAttributeMap).containsKey("disabled")) {
            return false;
        }
        
        if(widget.get(WdTags.WebAttributeMap, null) != null && widget.get(WdTags.WebAttributeMap).containsKey("display") && widget.get(WdTags.WebAttributeMap).get("display").contains("none")){
            return false;
        }

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

    private boolean isMenuItemClickable(Widget widget) {
    	if(widget.get(WdTags.WebCssClasses, "").contains("[item,")) {
    		return true;
    	}
    	return false;
    }

	@Override
	protected boolean isTypeable(Widget widget) {
        //If the web widget contains the "readonly" attribute, we do not want to derive type actions
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
		if(getFinalVerdict().severity() > Verdict.SEVERITY_OK && !listErrorVerdictInfo.contains(getFinalVerdict().info().replace("\n", " "))) {
			listErrorVerdictInfo.add(getFinalVerdict().info().replace("\n", " "));
		}
	}

	private Verdict twoLeafWidgetsOverlap(State state) {
		Verdict widgetsOverlapVerdict = Verdict.OK;

		// Prepare a list that contains all the Rectangles from the leaf widgets
		List<Pair<Widget, Rect>> leafWidgetsRects = new ArrayList<>();
		for(Widget w : state) {
			if(w.childCount() < 1 && w.get(Tags.Shape, null) != null) {
				leafWidgetsRects.add(new Pair<Widget, Rect>(w, (Rect)w.get(Tags.Shape)));
			}
		}
		//TODO: Improve this list iteration
        //TODO: every overlap is reported double, because the widgets overlap on both sides. Make an ignore list if an overlap is detected. Or try use a INNER JOIN linq query to find all the overlapping widgets. 
		for(int i = 0; i < leafWidgetsRects.size(); i++) {
			for(int j = 0; j < leafWidgetsRects.size(); j++) {
                if(leafWidgetsRects.get(i) != leafWidgetsRects.get(j)) {
					Rect rectOne = leafWidgetsRects.get(i).right();
					Rect rectTwo = leafWidgetsRects.get(j).right();
					if(checkRectIntersection(rectOne, rectTwo)) {
						Widget firstWidget = leafWidgetsRects.get(i).left();
						String firstMsg = String.format("Title: %s , WebTextContent: %s , Role: %s , Path: %s , WebId: %s , X: %f, Y: %f, Width: %f, Height %f", 
								firstWidget.get(Tags.Title, ""), firstWidget.get(WdTags.WebTextContent, ""), firstWidget.get(Tags.Role), firstWidget.get(Tags.Path), firstWidget.get(WdTags.WebId, ""), rectOne.x(), rectOne.y(), rectOne.width(), rectOne.height());

						Widget secondWidget = leafWidgetsRects.get(j).left();
						String secondMsg = String.format("Title: %s , WebTextContent: %s , Role: %s , Path: %s , WebId: %s , X: %f, Y: %f, Width: %f, Height %f", 
								secondWidget.get(Tags.Title, ""), secondWidget.get(WdTags.WebTextContent, ""), secondWidget.get(Tags.Role), secondWidget.get(Tags.Path), secondWidget.get(WdTags.WebId, ""), rectTwo.x(), rectTwo.y(), rectTwo.width(), rectTwo.height());

						String verdictMsg = "Two Widgets Overlapping!" + "<br/>First!  " + firstMsg + "<br/>Second! " + secondMsg;
                        
                        // Trying to rule out some false positivies
                        if (!firstWidget.get(WdTags.WebTextContent, "").isEmpty() && !secondWidget.get(WdTags.WebTextContent, "").isEmpty())
                        {
                            Verdict verdict = new Verdict(Verdict.SEVERITY_WARNING_UI_VISUAL_OR_RENDERING_FAULT, verdictMsg, Arrays.asList((Rect)firstWidget.get(Tags.Shape), (Rect)secondWidget.get(Tags.Shape)));
						     //return verdict;
                            widgetsOverlapVerdict = widgetsOverlapVerdict.join(verdict);
                        }
					}
				}
			}
		}

		return widgetsOverlapVerdict;
	}

	private boolean checkRectIntersection(Rect r1, Rect r2) {
		return !(r1.x() + r1.width() <= r2.x() ||
				r1.y() + r1.height() <= r2.y() ||
				r2.x() + r2.width() <= r1.x() ||
				r2.y() + r2.height() <= r1.y()); 
	}

	private Verdict widgetAlignmentMetric(State state, double tresholdValue) {
		Verdict widgetAlignmentMetricVerdict = Verdict.OK;

	    ArrayList<Rect> regions = getRegions(state);
        
		// returns a value from 0.00 to 100.0. Lower is bad alignment.
		double alignmentMetric = calculateAlignmentMetric(regions);
        
        if (alignmentMetric <= tresholdValue)
        {
            String webUrl = WdDriver.getCurrentUrl();
            String verdictMsg = String.format("Alignment metric for page '%s' with value %f is below treshold value %f!",  webUrl, alignmentMetric, tresholdValue);
            Verdict verdict = new Verdict(Verdict.SEVERITY_WARNING_UI_VISUAL_OR_RENDERING_FAULT, verdictMsg, regions);
		    widgetAlignmentMetricVerdict = widgetAlignmentMetricVerdict.join(verdict);
        }

		return widgetAlignmentMetricVerdict;
	}

    private ArrayList<Rect> getRegions(State state)
    {
        	// Prepare a list that contains all the Rectangles from the leaf widgets
        ArrayList<Rect> regions = new ArrayList<Rect>();
      
		for(Widget w : state) {
			if(w.childCount() < 1 && w.get(Tags.Shape, null) != null) {
 				regions.add((Rect)w.get(Tags.Shape));
			}
		}
        return regions;
    }

    // Copied from: https://github.com/mathieuzen/questim/blob/workingapp/src/be/lilab/questim/server/Alignment.java
    private double calculateAlignmentMetric(ArrayList<Rect> regions)
    {	
        int treshold = 1;
		int verticalAlignment1 = 0;
		int horizontalAlignment1 = 0;
		int verticalAlignment2 = 0;
		int horizontalAlignment2 = 0;
		int DAV = 0;
		int DAH = 0;
		int n = regions.size();
		double value = 0.0;
		
		for(int i=0; i<regions.size();i++){	
			Rect r1 = regions.get(i);
			verticalAlignment1 = 0;
			horizontalAlignment1 = 0;
			verticalAlignment2 = 0;
			horizontalAlignment2 = 0;
			for(int j=0; j<regions.size();j++){
				if(j!=i){				
				Rect r2 = regions.get(j);
					if((r1.x()<=r2.x()+treshold) && (r1.x()>=r2.x()-treshold))
						verticalAlignment1=1;
					if((r1.x()+r1.width()<=r2.x()+r2.width()+treshold) && (r1.x()+r1.width()>=r2.x()+r2.width()-treshold))
						verticalAlignment2=1;
					if((r1.y()<=r2.y()+treshold) && (r1.y()>=r2.y()-treshold))
						horizontalAlignment1=1;
					if((r1.y()+r1.height()<=r2.y()+r2.height()+treshold) && (r1.y()+r1.height()>=r2.y()+r2.height()-treshold))
						horizontalAlignment2=1;
				}
			}
			
			DAV += verticalAlignment1 + verticalAlignment2;
			DAH += horizontalAlignment1 + horizontalAlignment2;
			
		}
		
		value = (double)(DAV + DAH)/(n*4);
		return value * 100;
    }
    
    private Verdict widgetBalanceMetric(State state, double tresholdValue) {
		Verdict widgetBalanceMetricVerdict = Verdict.OK;

		ArrayList<Rect> regions = getRegions(state);
        Rect sutRect = (Rect) state.child(0).get(Tags.Shape, null);
        
		// returns a value from 0.00 to 100.0. Lower is bad alignment.
		double balanceMetric = calculateBalanceMetric(regions, sutRect.width(), sutRect.height());
        
        if (balanceMetric <= tresholdValue)
        {
            String webUrl = WdDriver.getCurrentUrl();
            String verdictMsg = String.format("Balance metric for page '%s' with value %f is below treshold value %f!",  webUrl, balanceMetric, tresholdValue);
            Verdict verdict = new Verdict(Verdict.SEVERITY_WARNING_UI_VISUAL_OR_RENDERING_FAULT, verdictMsg, regions);
		    widgetBalanceMetricVerdict = widgetBalanceMetricVerdict.join(verdict);
        }

		return widgetBalanceMetricVerdict;
	}
    
   public double calculateBalanceMetric(ArrayList<Rect> regions, double frameWidth, double frameHeight) {
		
		double [][] belonging = new double [regions.size()][4];
		double [] a = new double [regions.size()];
		double value = 0.0;
		
		double BMvert;
		double BMhori;
		
		double amax = 0;
		
		double wl = 0;
		double wr = 0;
		double wt = 0;
		double wb = 0;
		
		for(int i=0;i<regions.size();i++){
			
			Rect r = regions.get(i);
			
			
			a[i] = (double)r.width()* (double)r.height();
			if(a[i]>amax){
				amax = a[i];
			}
			
			
			//Belonging tests
			//UL
			
			if((r.x() < frameWidth/2) && (r.x()+r.width() > frameWidth/2) && (r.y() < frameHeight/2) && (r.y()+r.height() > frameHeight/2))
			{
				belonging[i][0] = ((double)(frameWidth/2 - r.x())  * (frameHeight/2 - r.y()))/(r.width()*r.height());
			}
			
			else if((frameWidth/2 - r.x() < r.width()) && (frameHeight/2 - r.y() > r.height()))
			{
				belonging[i][0] = ((double)(frameWidth/2 - r.x())/(r.width()));
			}
			
			else if((frameWidth/2 - r.x() > r.width()) && (frameHeight/2 - r.y() < r.height()))
			{
				belonging[i][0] = ((double)(frameHeight/2 - r.y())/(r.height()));
			}
			
			else if((frameWidth/2 - r.x() > r.width()) && (frameHeight/2 - r.y() > r.height()))
			{
				belonging[i][0] = 1;
			}
			
			//UR
						
			if((r.x() < frameWidth/2) && (r.x()+r.width() > frameWidth/2) && (r.y()+r.height() > frameHeight/2))
			{
				belonging[i][1] = ((double)(r.x()+r.width()  - frameWidth/2)  * (frameHeight/2 - r.y()))/(r.width()*r.height());
			}
			
			else if((r.x()+r.width() > frameWidth/2) && (r.y()+r.height() < frameHeight/2))
			{
				belonging[i][1] = ((double)(r.x()+r.width()  - frameWidth/2)/(r.width()));
			}
			
			else if((r.x() > frameWidth/2) && (r.y()+r.height() > frameHeight/2))
			{
				belonging[i][1] = ((double)(frameHeight/2 - r.y())/(r.height()));
			}
			
			if((r.x() > frameWidth/2) && (r.y()+r.height() < frameHeight/2))
			{
				belonging[i][1] = 1;
			}
			
			//LL
			
			if((r.x() < frameWidth/2) && (r.x()+r.width() > frameWidth/2) && (r.y() < frameHeight/2) && (r.y()+r.height() > frameHeight/2))
			{
				belonging[i][2] = ((double)(frameWidth/2 - r.x())  * (r.y()+r.height() - frameHeight/2))/(r.width()*r.height());
			}
			
			else if((r.x() < frameWidth/2) && (r.x()+r.width() < frameWidth/2) && (r.y() < frameHeight/2) && (r.y()+r.height() > frameHeight/2))
			{
				belonging[i][2] = ((double)(r.y()+r.height()-frameHeight/2)/(r.height()));
			}
			
			else if((r.x() < frameWidth/2) && (r.x()+r.width() > frameWidth/2) && (r.y() > frameHeight/2) && (r.y()+r.height() > frameHeight/2))
			{
				belonging[i][2] = ((double)(frameWidth/2 - r.x())/(r.width()));
			}
			
			else if((r.x() < frameWidth/2) && (r.x()+r.width() < frameWidth/2) && (r.y() > frameHeight/2) && (r.y()+r.height() > frameHeight/2))
			{
				belonging[i][2] = 1;
			}
			
			//LR
			if((r.x() < frameWidth/2) && (r.x()+r.width() > frameWidth/2) && (r.y() < frameHeight/2) && (r.y()+r.height() > frameHeight/2))
			{
				belonging[i][3] = ((double)(r.x()+r.width()  - frameWidth/2)  * (r.y()+r.height() - frameHeight/2))/(r.width()*r.height());
			}
			
			else if((r.x() > frameWidth/2) && (r.x()+r.width() > frameWidth/2) && (r.y() < frameHeight/2) && (r.y()+r.height() > frameHeight/2))
			{
				belonging[i][3] = ((double)(r.y()+r.height()-frameHeight/2)/(r.height()));
			}
			
			else if((r.x() < frameWidth/2) && (r.x()+r.width() > frameWidth/2) && (r.y() > frameHeight/2) && (r.y()+r.height() > frameHeight/2))
			{
				belonging[i][3] = ((double)(r.x()+r.width()-frameWidth/2)/(r.width()));
			}
			
			else if((r.x() > frameWidth/2) && (r.x()+r.width() > frameWidth/2) && (r.y() > frameHeight/2) && (r.y()+r.height() > frameHeight/2))
			{
				belonging[i][3] = 1;
			}
			
			for(int j=0; j<4; j++){
				if(belonging[i][j]<0){
					belonging[i][j]=0;
				}
			}
			
			
			if(belonging[i][0] == 1 || belonging[i][1] == 1 || belonging[i][2] == 1 || belonging [i][3] == 1)
			{
			wl += belonging[i][0]*(1+(frameWidth/2 - (r.x()+r.width()/2))/frameWidth/2)*(a[i]/amax)+belonging[i][2]*(1+(frameWidth/2 - (r.x()+r.width()/2))/frameWidth/2)*(a[i]/amax);
			wr += belonging[i][1]*(1+((r.x()+r.width()/2) - frameWidth/2)/frameWidth/2)*(a[i]/amax)+belonging[i][3]*(1+((r.x()+r.width()/2) - frameWidth/2)/frameWidth/2)*(a[i]/amax);
			wt += belonging[i][0]*(1+(frameHeight/2 - (r.y()+r.height()/2))/frameHeight/2)*(a[i]/amax)+belonging[i][1]*(1+(frameHeight/2 - (r.y()+r.height()/2))/frameHeight/2)*(a[i]/amax);
			wb += belonging[i][2]*(1+((r.y()+r.height()/2) - frameHeight/2)/frameHeight/2)*(a[i]/amax)+belonging[i][3]*(1+((r.y()+r.height()/2) - frameHeight/2)/frameHeight/2)*(a[i]/amax);
			}
			else
			{
			wl += belonging[i][0]*(a[i]/amax)+belonging[i][2]*(a[i]/amax);
			wr += belonging[i][1]*(a[i]/amax)+belonging[i][3]*(a[i]/amax);
			wt += belonging[i][0]*(a[i]/amax)+belonging[i][1]*(a[i]/amax);
			wb += belonging[i][2]*(a[i]/amax)+belonging[i][3]*(a[i]/amax);
			}
			
		}
			
			
			BMvert = (wl-wr)/Math.max(wl,wr);
			BMhori = (wt-wb)/Math.max(wt,wb);
			
			value = 1-(Math.abs(BMvert)+Math.abs(BMhori))/2;			
		
		return 100 - value * 100;
	}

	private void testLog4J() {
     String logEntries = "<log4j:event logger=\"Log4JLibs.LogExample\" timestamp=\"1683569806499\" level=\"INFO\" thread=\"main\">\r\n" + 
        		"<log4j:message><![CDATA[Info AAAAAAAAAAAAAAAAAAAAAaa]]></log4j:message>\r\n" + 
        		"</log4j:event><log4j:event logger=\"Log4JLibs.LogExample\" timestamp=\"1683569806501\" level=\"WARN\" thread=\"main\">\r\n" + 
        		"<log4j:message><![CDATA[Warn BBBBBBBBBBBBBBBBBBbBBB]]></log4j:message>\r\n" + 
        		"</log4j:event>";
                
        XmlLogEventParser parser = new XmlLogEventParser();
        try {
        	int beginIndex = 0;
        	String endTag = "</log4j:event>";
        	
            // Parse the log file and iterate over the list of log events
        	while(logEntries.length() > 0) 
        	{
	        	int eventIndex = logEntries.indexOf(endTag);
	        	String logEntry = logEntries.substring(beginIndex, eventIndex);
	        	logEntries = logEntries.substring(eventIndex + endTag.length());
                LogEvent event = parser.parseFrom(logEntry);
                System.out.println(event.getMessage());
                break;
             }
         
        }
        catch(ParseException ex)
        {
        }
          finally {
            // Close the input stream
            //inputStream.close();
        }
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
		
		// Check the functional Verdict that detects if two widgets overlap
	    //verdict = twoLeafWidgetsOverlap(state);
		//if (shouldReturnVerdict(verdict)) return verdict;

		//Verdict spellCheckerVerdict = GenericVerdict.SpellChecker(state, WdTags.WebTextContent, new Dutch(), "Pagina generatietijd.*|\\d\\d.*");
		//if(spellCheckerVerdict != Verdict.OK) HTMLStateVerdictReport.reportStateVerdict(actionCount, state, spellCheckerVerdict);

		// Check the functional Verdict that detects if a form button is disabled after modifying the form inputs.
		//verdict = formButtonEnabledAfterTypingChangesVerdict(state);
		//if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects if a form button is enabled when it must not.
		//verdict = formButtonMustBeDisabledIfNoChangesVerdict(state);
		//if (shouldReturnVerdict(verdict)) return verdict;
        //testLog4J();
        
        verdict = widgetAlignmentMetric(state, 50.0);
        if (shouldReturnVerdict(verdict)) return verdict;
        
        verdict = widgetBalanceMetric(state, 50.0);
        if (shouldReturnVerdict(verdict)) return verdict;
        
		verdict = detectWidgetsThatShouldBeInSync(state);
		if (shouldReturnVerdict(verdict)) return verdict;
		
        verdict = detectCommonTestOrDummyPhrases(state);
        if (shouldReturnVerdict(verdict)) return verdict;
        
        //verdict = detectSlowPerformance(20.0); // seconds
        //if (shouldReturnVerdict(verdict)) return verdict;
        
        // Checks for zero numbers in tables
        verdict = detectZeroNumbers(state);
        if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects if a downloaded file is empty.
		verdict = watcherFileEmptyFile();
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects dummy buttons to the current state verdict.
		verdict = functionalButtonVerdict(state);
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects duplicate or repeated text in descriptions of widgets, ignore some common terms of DigiOffice, date, datetime, 3 phone formats and postal code
		verdict = WebVerdict.DetectDuplicateText(state, "MM|AA|A{260}|0\\.0\\.0|0,0,0|Logo;Logo|DocDoc|DossierDossier|GebrGebr|RelRel|\\d\\d:\\d\\d:\\d\\d|\\d\\d-\\d\\d-\\d\\d\\d\\d\\s\\d\\d:\\d\\d:\\d\\d|\\d\\d-\\d\\d-\\d\\d\\d*\\d*|\\d\\d - \\d\\d \\d\\d \\d\\d \\d\\d|\\(?\\d\\d\\d\\)? -? ?\\d\\d\\d \\d\\d \\d\\d|\\(?\\d\\d\\d\\d\\)? -? ?\\d\\d \\d\\d \\d\\d|\\d\\d\\d\\d [A-Z][A-Z]|\\d\\d-\\d\\d-\\d\\d|0\\.0\\/0\\.0|0,0\\/0,0");
		if (shouldReturnVerdict(verdict)) return verdict;
		
		// Check the functional Verdict that detects HTML or XML tags in descriptions of widgets
		verdict = WebVerdict.DetectHTMLOrXMLTagsInText(state,"%3Cscript%3Econsole\\.error%28%27XSS%20is%20possible%27%29%3B%3C%2Fscript%3E|.*\\.Config|.*_DMS_.*|.*_Bouw_.*|.*_CRM_.*|.*_Beheer_.*|ZoekFilter_.*|<memo>alpha beta gamma|.*>console\\.error\\(.*|<HuisstijlDir>|<VersieEnDatum>|<<major version>>\\.<<minor version>>|<<version>> \\(<<version date>>\\)");
		if (shouldReturnVerdict(verdict)) return verdict;

        // Check the functional Verdict that detects sensitive data, such as passwords or client secrets
        // https://en.wikipedia.org/wiki/List_of_the_most_common_passwords
        //verdict = detectSensitiveData(state, "123456|123456789|12345|qwerty|password|12345678|111111|123123|1234567890|1234567|qwerty123|000000|1q2w3e|aa12345678|abc123|password1|1234|qwertyuiop|123321|password123");
        verdict = detectSensitiveData(state, "123456|123456789|qwerty|password|12345678|111111|123123|1234567890|1234567|qwerty123|000000|1q2w3e|aa12345678|abc123|password1|qwertyuiop|123321|password123");
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
		verdict = WebVerdict.NumberWithLotOfDecimals(state, 2, false);
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects if exists a textArea Widget without length.
		verdict = WebVerdict.TextAreaWithoutLength(state, Arrays.asList(WdRoles.WdTEXTAREA));
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects if a web element does not contain children.
		verdict = WebVerdict.ElementWithoutChildren(state, Arrays.asList(WdRoles.WdFORM));
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects if a web radio input contains a single option.
		verdict = WebVerdict.SingleRadioInput(state);
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects if a web alert contains a suspicious message.
		verdict = WebVerdict.AlertSuspiciousMessage(state, ".*[lL]ogin.*", lastExecutedAction);
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects if web table contains duplicated rows.
		verdict = WebVerdict.DetectDuplicatedRowsInTable(state);
		if (shouldReturnVerdict(verdict)) return verdict;

        // Check untranslated text tags
        verdict = detectUntranslatedText(state);
        if (shouldReturnVerdict(verdict)) return verdict;
        
        // Check The replacement character � (often displayed as a black rhombus with a white question mark) is a symbol found in the Unicode standard at code point U+FFFD in the Specials table. It is used to indicate problems when a system is unable to render a stream of data to correct symbols
        verdict = detectUnicodeReplacementCharacter(state);
        if (shouldReturnVerdict(verdict)) return verdict;

		return verdict;
	}
}
