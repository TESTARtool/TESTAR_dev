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

import org.languagetool.language.AmericanEnglish;
import org.languagetool.language.BritishEnglish;
import org.languagetool.language.Dutch;
//import org.apache.commons.lang.NotImplementedException;
//import org.openqa.selenium.remote.RemoteWebDriver;
//import org.openqa.selenium.devtools.DevTools;
//import org.openqa.selenium.devtools.HasDevTools;
//import org.openqa.selenium.devtools.v137.network.Network;
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
//import org.testar.monkey.alayer.webdriver.Constants;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.PrintWriter;
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
    Oracle idea  3: formButtonEnabledAfterTypingChangesVerdict and formButtonMustBeDisabledIfNoChangesVerdict
    Oracle idea  4: Single quote ', double quote ", ' OR 1=1 OR ''=' and @# added to custom_input_data.txt. This will trigger 500 Internal Server Error if SQL Injection is possible or leak data.
	Oracle idea  5: '<memo>alpha beta gamma' added to custom_input_data.txt to trigger parsing errors
	Oracle idea  6: GenericVerdict.UnicodeReplacementCharacter and unicode characters added custom_input_data.txt
    Oracle idea  7: Just switch username in DigiOffice database to login with a user with different permissions and config
    Oracle idea  8: GenericVerdict.WidgetClashDetection
    Oracle idea 11: Generic.Spellchecker
    Oracle idea 12: WebVerdict.SingleSelectItems
    Oracle idea 18: WebVerdict.UnsortedSelectItems
    Oracle idea 20: functionalButtonVerdict // Dummy button detection
    Oracle idea 22: GenericVerdict.SensitiveData
    Oracle idea 24: Long text (261x A chars) added to custom_input_data.txt
    Oracle idea 28: WebVerdict.imageResolutionDifferences
    Oracle idea 30: GenericVerdict.WidgetAlignmentMetric
    Oracle idea 31: Just run TESTAR on multiple machines at the same time
    Oracle idea 34: WebVerdict.HTMLOrXMLTagsInText
	Oracle idea 39: WebVerdict.DuplicateULItems
    Oracle idea 40: WebVerdict.DuplicateText
    Oracle idea 44: WebVerdict.TooManyItemSelectItems
    Oracle idea 48: WebVerdict.NumberWithLotOfDecimals
    Oracle idea 50: WebVerdict.TextAreaWithoutLength
    Oracle idea 51: WebVerdict.EmptySelectItems
    Oracle idea 52: WebVerdict.SingleRadioInput
    Oracle idea 57: WebVerdict.AlertSuspiciousMessage
    Oracle idea 59: WebVerdict.ElementWithoutChildren
    Oracle idea 65: watcherFileEmptyFile
    Oracle idea 70: WebVerdict.DuplicatedRowsInTable
    Oracle idea 75: GenericVerdict.SensitiveData
    Oracle idea 79: detectWidgetsThatShouldBeInSync
    Oracle idea 81: watcherFileEmptyFile
    Oracle idea 89: WebVerdict.DuplicateSelectItems
    Oracle idea 90: GenericVerdict.CommonTestOrDummyPhrases
    Oracle idea 94: WebVerdict.ZeroNumbersInTable
    Oracle idea 97: TESTAR GUI > Tab Oracles > checkboxes Enable Web Console [Error,Warning] Oracle
    Oracle idea 98: detectUntranslatedText
    Oracle idea 99: Added XSS script in custom_input_data.txt which write a console error which will be picked up by Oracle idea 97
 */
    


public class Protocol_webdriver_functional_digioffice extends WebdriverProtocol {
	private Action functionalAction = null;
	private Verdict functionalVerdict = Verdict.OK;
	private List<String> listErrorVerdictInfo = new ArrayList<>();

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

    private String errorVerdictInfoFilePath()
    {
        return Main.settingsDir + File.separator + "webdriver_functional_digioffice" + File.separator + "ErrorVerdictInfo.txt";
    }

    private void loadErrorVerdictInfoFile()
    {   
         ObjectInputStream ois = null;
         try
         {
            File errorVerdictInfoFile = new File(errorVerdictInfoFilePath()).getCanonicalFile();
            if (errorVerdictInfoFile.exists())
            {
                FileInputStream fis = new FileInputStream(errorVerdictInfoFile);
                ois = new ObjectInputStream(fis);
                listErrorVerdictInfo  = (List) ois.readObject();
                ois.close();
            }
            
        }
        catch(ClassNotFoundException e)            
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    

    private void saveErrorVerdictInfoFile()
    {   
        try (FileOutputStream fos = new FileOutputStream(errorVerdictInfoFilePath());
            ObjectOutputStream oos = new ObjectOutputStream(fos);) {
            oos.writeObject(listErrorVerdictInfo);

        } 
        catch (IOException e) {
           e.printStackTrace();
        }
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
          //wait(4000);
          
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

        // Load known errors in list
        loadErrorVerdictInfoFile();
        
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


    public static void writeTextToFile(String path, String content) {
        try (PrintWriter out = new PrintWriter(path)) {
            out.println(content);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }


    public static void generateObjBox(
        StringBuilder sb,
        String objectName,
        double x, double y, double z,
        double length, double width, double height,
        int[] vertexIndexRef) {
    
        int i = vertexIndexRef[0];
    
        double[][] vertices = new double[][] {
            {x, y, z},
            {x + length, y, z},
            {x + length, y + width, z},
            {x, y + width, z},
            {x, y, z + height},
            {x + length, y, z + height},
            {x + length, y + width, z + height},
            {x, y + width, z + height}
        };
    
        sb.append("o ").append(objectName).append("\n");
    
        for (double[] v : vertices) {
            sb.append(String.format("v %.3f %.3f %.3f\n", v[0], v[1], v[2]));
        }
    
        sb.append(String.format("f %d %d %d %d\n", i, i + 1, i + 2, i + 3));
        sb.append(String.format("f %d %d %d %d\n", i + 4, i + 5, i + 6, i + 7));
        sb.append(String.format("f %d %d %d %d\n", i, i + 4, i + 7, i + 3));
        sb.append(String.format("f %d %d %d %d\n", i + 1, i + 5, i + 6, i + 2));
        sb.append(String.format("f %d %d %d %d\n", i + 3, i + 2, i + 6, i + 7));
        sb.append(String.format("f %d %d %d %d\n", i, i + 1, i + 5, i + 4));
    
        vertexIndexRef[0] += 8;
    }


		public void WidgetTreeExportRecursive(StringBuilder objBuilder, Widget w, double z, int[] vertexIndexRef)
		{
            double thickness = 20.0;
            double spacing = 0.0;
            Rect rect = (Rect) w.get(Tags.Shape, null);
            
		
            double screenHeight = 1024.0; // TODO, get actual screensize, something like: Screen.PrimaryScreen.Bounds.Height
			// Flip Y and adjust for height
			var flippedY = screenHeight - rect.y() - rect.height();
           
            generateObjBox(objBuilder, w.get(Tags.AbstractID, ""), rect.x(), flippedY, z + spacing, rect.width(), rect.height() ,thickness, vertexIndexRef);

            for(int i = 0; i<w.childCount(); i++) {
                WidgetTreeExportRecursive(objBuilder, w.child(i), z + thickness + spacing, vertexIndexRef);
            }
            
            
		}

    private String obj3DFilePath()
    {
        return Main.settingsDir + File.separator + "webdriver_functional_digioffice" + File.separator + "3dobject.obj";
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

		// Uncomment when you want to write a 3D OBJ file to the settings directory. https://en.wikipedia.org/wiki/Wavefront_.obj_file
		// The obj file can be inserted as a 3D model in PowerPoint or can be used in modeling tool such as Blender to animate and give it texture.
		// Nice to have: It would have been nice to have a hotkey in Spy mode to export the 3D model instead of using it in test loop.
		// Nice to have: Use it in visual clash detection and color the clashing widgets.
        //build3DObjectFileFromGuiState(state);
        
		return verdict;
	}


    private void build3DObjectFileFromGuiState(State state)
    {
        StringBuilder objBuilder = new StringBuilder();
        int[] vertexIndex = {1};
        
        for(Widget w : state) {
		  WidgetTreeExportRecursive(objBuilder, w, 0, vertexIndex);
        }

        writeTextToFile(obj3DFilePath(),objBuilder.toString());
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


	// Detect text decorated with ^ characters, this means in DigiOffice that this text has no translation key and will not be translated when UI language is changed in DigiOffice
	// BAD:  ^Untranslated text^
    //       ^UntranslatedText^
	// GOOD: Untranslated text
	private Verdict detectUntranslatedText(State state)
	{
		Verdict verdict = Verdict.OK;
		String patternRegex = "\\^.*\\^";
		Pattern pattern = Pattern.compile(patternRegex);
		
		for(Widget w : state) {
			String desc = w.get(WdTags.WebTextContent, "");
			Matcher matcher = pattern.matcher(desc);
			
			if (matcher.find()) {
				String verdictMsg = String.format("Detected untranslated tags in widget! Role: %s , Path: %s , Desc: %s , WebTextContent: %s", 
						w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.Desc, ""), w.get(WdTags.WebTextContent, ""));
				verdict = verdict.join(new Verdict(Verdict.Severity.WARNING_UI_TRANSLATION_OR_SPELLING_ISSUE, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
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
				verdict = verdict.join(new Verdict(Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT, verdictMsg));
			}
		}

        String checkTableTDsAndTableCount = "for(let a of document.navContent.document.querySelectorAll('.table-data')) {recCount = Number(a.parentElement.parentElement.parentElement.querySelector('.total').innerText.replace(/\\D/g, '')); if (recCount <= 50 && Math.max(a.getElementsByTagName('tr').length-1,0) != recCount) { return 'Table has ' + (a.getElementsByTagName('tr').length-1).toString() + ' rows and tablecount '+ recCount +' are not equal';}}";
        String checkTableTDsAndTableCountMessage = (String)WdDriver.executeScript(checkTableTDsAndTableCount); // if table and tablecount are not equal, then this is reported
        
        if (checkTableTDsAndTableCountMessage != null && !checkTableTDsAndTableCountMessage.isEmpty())
        {
            System.out.println("Comparing checkTableTDsAndTableCountMessage: " + checkTableTDsAndTableCountMessage);
            verdict = verdict.join(new Verdict(Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT, checkTableTDsAndTableCountMessage));
        }
		return verdict;
	}


    // TODO: Gives false positives when opening Div style forms, such as Notitie fields. The OK button seems to be working in the UI, but TESTAR doesn't see that as a change.'
    // Dummy button
	private Verdict functionalButtonVerdict(State state, String ignorePatternRegEx) {
		// If the last executed action is a click on a web button
		if(functionalAction != null 
				&& functionalAction.get(Tags.OriginWidget, null) != null 
				&& functionalAction.get(Tags.Desc, "").contains("Click")
				&& functionalAction.get(Tags.OriginWidget).get(Tags.Role, Roles.Widget).equals(WdRoles.WdBUTTON)) {

			// Compare previous and current state AbstractIDCustom identifiers
			// to determine if interacting with the button does nothing in the SUT state
			String previousStateId = latestState.get(Tags.ConcreteID, "NoPreviousId");
			String currentStateId = state.get(Tags.ConcreteID, "NoCurrentId");

			// NOTE 1: Because we are comparing the states using the AbstractIDCustom property, 
			// it is important to consider the used abstraction: test.settings - AbstractStateAttributes (WebWidgetId, WebWidgetTextContent)
			// NOTE 2: A button alert can prompt a message, but TESTAR saves the text message and returns to the state
			// this is also something to consider :/
			// TODO: Improve the existence of web alerts messages within the State ID
			if(previousStateId.equals(currentStateId) && WdDriver.alertMessage.isEmpty()) {
    
                Pattern ignorePattern = Pattern.compile(ignorePatternRegEx);
                Widget w = functionalAction.get(Tags.OriginWidget);
                Matcher ignoreMatcher = ignorePattern.matcher(w.get(Tags.Desc, ""));

                if (ignorePatternRegEx == "" || !ignoreMatcher.find()) {
				    String verdictMsg = String.format("Dummy Button detected! Role: %s , Path: %s , Desc: %s", 
						w.get(Tags.Role), w.get(Tags.Path), w.get(Tags.Desc, ""));

				    functionalVerdict = new Verdict(Verdict.Severity.WARNING_UI_FLOW_FAULT, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape)));
                }
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
    
    						watcherEmptyfileVerdict = watcherEmptyfileVerdict.join(new Verdict(Verdict.Severity.WARNING_DATA_DATA_VALUE_NOT_STORED_OR_DELETED, verdictMsg));
    					}
    					
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
						return new Verdict(Verdict.Severity.WARNING_UI_ITEM_VISIBILITY_FAULT, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape)));
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
			addSlidingActions(actions, ac, widget);

			// If the element is blocked, Testar can't click on or type in the widget
			if (widget.get(Blocked, false) && !widget.get(WdTags.WebIsShadow, false)) {
				continue;
			}

			// type into text boxes
			if (isAtBrowserCanvas(widget) && isTypeable(widget)) {
				if(whiteListed(widget) || isUnfiltered(widget)){
					actions.add(ac.clickTypeInto(widget, InputDataManager.getRandomTextInputData(widget), true));
                    actions.add(ac.pasteTextInto(widget, InputDataManager.getRandomTextFromCustomInputDataFile(System.getProperty("user.dir") + "/settings/custom_input_data.txt"), true));
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
		if(getFinalVerdict().severity() > Verdict.Severity.OK.getValue() && !listErrorVerdictInfo.contains(getFinalVerdict().info().replace("\n", " "))) {
			listErrorVerdictInfo.add(getFinalVerdict().info().replace("\n", " "));
            saveErrorVerdictInfoFile();
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
		
		//Verdict spellCheckerVerdict = GenericVerdict.SpellChecker(state, WdTags.WebTextContent, new Dutch(), "Pagina generatietijd.*|\\d\\d.*");
		//if(spellCheckerVerdict != Verdict.OK) HTMLStateVerdictReport.reportStateVerdict(actionCount, state, spellCheckerVerdict);

		// Check the functional Verdict that detects if a form button is disabled after modifying the form inputs.
		//verdict = formButtonEnabledAfterTypingChangesVerdict(state);
		//if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects if a form button is enabled when it must not.
		//verdict = formButtonMustBeDisabledIfNoChangesVerdict(state);
		//if (shouldReturnVerdict(verdict)) return verdict;
        
        verdict = WebVerdict.imageResolutionDifferences(state,2,2);
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
        
        // Check the functional Verdict that detects if two widgets overlap
		// Also, add the roles or the classes of the widget sub-trees are needed to ignore
 		verdict = GenericVerdict.WidgetClashDetection(state, 
				Arrays.asList(WdRoles.WdCOL, WdRoles.WdCOLGROUP), // ignoredRoles, 
                Arrays.asList(
                /* whitelist: */
                    "multipleFindsWrapper",
                    "workspace-wrapper",
                    "modalOverlay",
                    "cke", // CKE editor 
                    "EntityListContainer",
                    "list-wrapper",
                    "tabs",
                    "RegProfButtons",
                    "ui-datepicker",
                    "RegistratieProfiel-SidePanel",
                    "table-loader", 
                    "dropdown-caret-wrapper", 
                    "fa-caret-down", 
                    "text", 
                    "ui-state-default", 
                    "collapsable-hitarea", 
                    "view-ec", 
                    "spinner", 
                    "opslaanensluiten", // overlay icon
                    "opslaanennieuw", // overlay icon 
                    "pijlbenedendropdown", 
                    "ui-dialog-content",
                    "ui-widget-overlay",
                    "ui-resizable-handle",
                    "sizer",
                    "previewsizer",
                    "navigation-sizer",
                    "ui-draggable",
                    "groepsvaktoevoegen", // due overlay icon
                    "veldtoevoegen", // due overlay icon
                    "voorbeeldweergave", // due overlay icon
                    "mat-button-focus-overlay", // due material design
                    "group-menu", // due material design
                    "cdk-overlay-container", // CDK
                    "cdk-visually-hidden", // CDK               
                    
                /* temporary until bug solved: */
                    "main-page-preview",
                    "ad",
                    "navImageContainer",
                    "workspace-container",
                    "workspace-toggler",
                    "environment",
                    "hitarea",
                    "fa-sort-down",
                    "fa-sort-up",
                    "fa-external-link-alt",
                    "group-title",
                    "mat-card-header",
                    "mat-ripple",
                    "mat-slide-toggle-thumb-container",
                    "table-h-slider", // horizontal slider
                    "submit-bar-wrap", // OK/Cancel button bar in Angular wizard 
                    "reportsTreePanel", // Report radiobutton tree
                    "table-legend-row", // Pager buttons #74843
                    "paginationColumn", // Crosstable pagination, bug #74650
                    "TopPanelSearch"
                             ), //ignoredClasses
                true, // joinVerdicts	
                false,  // checkOnlyLeafWidgets
                true); // checkWebStyles	
 		if (shouldReturnVerdict(verdict)) return verdict;
        
		//verdict = detectWidgetsThatShouldBeInSync(state);
		//if (shouldReturnVerdict(verdict)) return verdict;
		
        verdict = GenericVerdict.CommonTestOrDummyPhrases(state, WdTags.WebTextContent);
        if (shouldReturnVerdict(verdict)) return verdict;
        
        // Checks for zero numbers in tables
        verdict = WebVerdict.ZeroNumbersInTable(state);
        if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects if a downloaded file is empty.
		verdict = watcherFileEmptyFile();
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects dummy buttons to the current state verdict.
		verdict = functionalButtonVerdict(state, "download|zoomOut|zoomIn|viewFind|Keyboard shortcuts|print|button|secondaryToolbarToggle|sidebarToggle|viewThumbnail");
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects duplicate or repeated text in descriptions of widgets, ignore some common terms of DigiOffice, date, datetime, 3 phone formats and postal code
		verdict = WebVerdict.DuplicateText(state, "Project - Project met.*|RelatieRelatie|GebruikerGebruiker|DocumentDocument|AdresAdres|ElmElm|zzdp@zzdp\\.nl|RelID \\(RelID\\)|\\b [\\+>&] \\b|Vonk Vonk|Piet Piet|Arthur Arthur|[A-Z]\\.[A-Z]\\.|[A-Z][A-Z]|A{261}|0\\.0\\.0|0,0,0|Logo;Logo|DocDoc|DossierDossier|GebrGebr|RelRel|\\d\\d:\\d\\d:\\d\\d|\\d\\d-\\d\\d-\\d\\d\\d\\d\\s\\d\\d:\\d\\d:\\d\\d|\\d\\d-\\d\\d-\\d\\d\\d*\\d*|\\d\\d - \\d\\d \\d\\d \\d\\d \\d\\d|\\(?\\d\\d\\d\\)? -? ?\\d\\d\\d \\d\\d \\d\\d|\\(?\\d\\d\\d\\d\\)? -? ?\\d\\d \\d\\d \\d\\d|\\d\\d\\d\\d [A-Z][A-Z]|\\d\\d-\\d\\d-\\d\\d|0\\.0\\/0\\.0|0,0\\/0,0");
		if (shouldReturnVerdict(verdict)) return verdict;
		
		// Check the functional Verdict that detects HTML or XML tags in descriptions of widgets
		verdict = WebVerdict.HTMLOrXMLTagsInText(state,".* <> .*|<niet ingevuld>|&gt;|.*Postverwerking<\\/a>|<Nieuw>|%3Cscript%3Econsole\\.error%28%27XSS%20is%20possible%27%29%3B%3C%2Fscript%3E|.*\\.Config|.*_DMS_.*|.*_Bouw_.*|.*_CRM_.*|.*_Beheer_.*|ZoekFilter_.*|<memo>alpha beta gamma|.*>console\\.error\\(.*|<HuisstijlDir>|<VersieEnDatum>|<<major version>>\\.<<minor version>>|<<version>> \\(<<version date>>\\)");
		if (shouldReturnVerdict(verdict)) return verdict;

        // Check the functional Verdict that detects sensitive data, such as passwords or client secrets
        // https://en.wikipedia.org/wiki/List_of_the_most_common_passwords
        //verdict = detectSensitiveData(state, "123456|123456789|12345|qwerty|password|12345678|111111|123123|1234567890|1234567|qwerty123|000000|1q2w3e|aa12345678|abc123|password1|1234|qwertyuiop|123321|password123");
        verdict = GenericVerdict.SensitiveData(state, WdTags.WebTextContent, "qwerty123");
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

		// Check the functional Verdict that detects Unnumbered List (UL) child elements with duplicate items to the current state verdict.
		verdict = WebVerdict.DuplicateULItems(state);
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects if exists a number with more than X decimals.
		verdict = WebVerdict.NumberWithLotOfDecimals(state, 3, false);
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects if exists a textArea Widget without length.
		verdict = WebVerdict.TextAreaWithoutLength(state, Arrays.asList(WdRoles.WdTEXTAREA, WdRoles.WdINPUT));
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects if a web element does not contain children.
		//verdict = WebVerdict.ElementWithoutChildren(state, Arrays.asList(WdRoles.WdFORM, WdRoles.WdSELECT, WdRoles.WdTR, WdRoles.WdOPTGROUP, WdRoles.WdCOLGROUP, WdRoles.WdFIELDSET, WdRoles.WdDL, WdRoles.WdDATALIST, WdRoles.WdBODY));
		verdict = WebVerdict.ElementWithoutChildren(state, Arrays.asList(WdRoles.WdSELECT, WdRoles.WdTR, WdRoles.WdOPTGROUP, WdRoles.WdCOLGROUP, WdRoles.WdFIELDSET, WdRoles.WdDL, WdRoles.WdDATALIST));
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects if a web radio input contains a single option.
		//verdict = WebVerdict.SingleRadioInput(state);
		//if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects if a web alert contains a suspicious message.
		verdict = WebVerdict.AlertSuspiciousMessage(state, ".*[lL]ogin.*|.*[eE]rror.*|.*[eE]xcepti[o?]n.*|.*[Ee]xceptie.*|.*[fF]out.*|.*[pP]robleem.*", lastExecutedAction);
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check the functional Verdict that detects if web table contains duplicated rows.
		verdict = WebVerdict.DuplicatedRowsInTable(state);
		if (shouldReturnVerdict(verdict)) return verdict;

		// Check untranslated text tags
		verdict = detectUntranslatedText(state);
		if (shouldReturnVerdict(verdict)) return verdict;
        
		// Check The replacement character ? (often displayed as a black rhombus with a white question mark) is a symbol found in the Unicode standard at code point U+FFFD in the Specials table. It is used to indicate problems when a system is unable to render a stream of data to correct symbols
		verdict = GenericVerdict.UnicodeReplacementCharacter(state, WdTags.WebTextContent);
		if (shouldReturnVerdict(verdict)) return verdict;

		return verdict;
	}
}