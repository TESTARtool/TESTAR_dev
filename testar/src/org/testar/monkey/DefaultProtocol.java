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

package org.testar.monkey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.SessionNotCreatedException;
import org.testar.*;
import org.testar.managers.NativeHookManager;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Canvas;
import org.testar.monkey.alayer.Color;
import org.testar.monkey.alayer.Shape;
import org.testar.monkey.alayer.Visualizer;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.ActivateSystem;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.KillProcess;
import org.testar.monkey.alayer.android.AndroidProtocolUtil;
import org.testar.monkey.alayer.devices.AWTMouse;
import org.testar.monkey.alayer.devices.DummyMouse;
import org.testar.monkey.alayer.devices.KBKeys;
import org.testar.monkey.alayer.devices.Mouse;
import org.testar.monkey.alayer.exceptions.*;
import org.testar.monkey.alayer.ios.IOSProtocolUtil;
import org.testar.monkey.alayer.visualizers.ShapeVisualizer;
import org.testar.monkey.alayer.webdriver.WdProtocolUtil;
import org.testar.monkey.alayer.windows.WinApiException;
import org.testar.oracles.Oracle;
import org.testar.oracles.log.LogOracle;
import org.testar.plugin.NativeLinker;
import org.testar.plugin.OperatingSystems;
import org.testar.reporting.ReportManager;
import org.testar.serialisation.LogSerialiser;
import org.testar.serialisation.ScreenshotSerialiser;
import org.testar.serialisation.TestSerialiser;
import org.testar.settings.Settings;
import org.testar.statemodel.StateModelManager;
import org.testar.statemodel.StateModelManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import static org.testar.monkey.alayer.Tags.*;

public class DefaultProtocol extends RuntimeControlsProtocol {

	public static boolean faultySequence;
	protected boolean logOracleEnabled;
	protected Oracle logOracle;
	private State stateForClickFilterLayerProtocol;

	protected ReportManager reportManager;
	public State getStateForClickFilterLayerProtocol() {
		return stateForClickFilterLayerProtocol;
	}

	public void setStateForClickFilterLayerProtocol(State stateForClickFilterLayerProtocol) {
		this.stateForClickFilterLayerProtocol = stateForClickFilterLayerProtocol;
	}

	String generatedSequence;
	public String getGeneratedSequenceName() {
		return generatedSequence;
	}

	File currentSeq;

	protected Mouse mouse;

	protected ProcessListener processListener = new ProcessListener();
	boolean enabledProcessListener = false;
	public static Verdict processVerdict = Verdict.OK;

	private Verdict replayVerdict;

	public Verdict getReplayVerdict() {
		return replayVerdict;
	}

	public void setReplayVerdict(Verdict replayVerdict) {
		this.replayVerdict = replayVerdict;
	}

	Verdict finalVerdict = Verdict.OK;
	public Verdict getFinalVerdict() {
		return finalVerdict;
	}

	protected String lastPrintParentsOf = "null-id";
	protected int actionCount;

	protected final int actionCount() {
		return actionCount;
	}

	protected int sequenceCount;

	protected final int sequenceCount() {
		return sequenceCount;
	}

	protected int lastSequenceActionNumber;
	double startTime;

	protected final double timeElapsed() {
		return Util.time() - startTime;
	}

	protected List<ProcessInfo> contextRunningProcesses = null;
	protected static final String            DATE_FORMAT             = "yyyy-MM-dd HH:mm:ss";
	protected static final Logger INDEXLOG = LogManager.getLogger();
	protected double passSeverity = Verdict.SEVERITY_OK;

	protected State latestState;
	public static Action lastExecutedAction = null;

	protected EventHandler eventHandler;
	protected Canvas               cv;
	protected Pattern              clickFilterPattern      = null;
	protected Map<String, Matcher> clickFilterMatchers     = new WeakHashMap<String, Matcher>();
	protected Pattern              suspiciousTitlesPattern = null;
	protected Map<String, Matcher> suspiciousTitlesMatchers = new WeakHashMap<String, Matcher>();
	private StateBuilder builder;

	protected int escAttempts = 0;

	protected StateModelManager stateModelManager;
	private   String            startOfSutDateString; //value set when SUT started, used for calculating the duration of test

	// Creating a logger with log4j library:
	private static Logger logger = LogManager.getLogger();


	/**
	 * This is the abstract flow of TESTAR (generate mode):
	 *
	 * - Initialize TESTAR settings
	 * - InitTestSession (before starting the first sequence)
	 * - OUTER LOOP:
	 * 		PreSequencePreparations (before each sequence, for example starting WebDriver, JaCoCo or another process for sequence)
	 * 		StartSUT
	 * 		BeginSequence (starting "script" on the GUI of the SUT, for example login)
	 * 		INNER LOOP
	 * 			GetState
	 * 			GetVerdict
	 * 			StopCriteria (moreActions/moreSequences/time?)
	 * 			DeriveActions
	 * 			SelectAction
	 * 			ExecuteAction
	 * 		FinishSequence (closing "script" on the GUI of the SUT, for example logout)
	 * 		StopSUT
	 * 		PostSequenceProcessing (after each sequence)
	 * - CloseTestSession (after finishing the last sequence)
	 *
	 */

	/**
	 * Starting the TESTAR loops
	 *
	 * @param settings
	 */
	public final void run(final Settings settings) {

		//Associate start settings of the first TESTAR dialog
		this.settings = settings;

		// If the environment is not headless, initialize the AWT mouse
		if (!GraphicsEnvironment.isHeadless()) {
			mouse = AWTMouse.build();
		} else {
			System.out.println("Headless environment! Initializing a DummyMouse device");
			mouse = DummyMouse.build();
		}

		//initialize TESTAR with the given settings:
		logger.info("TESTAR initializing with the given protocol settings");
		initialize(settings);

		try {

			if (mode() == Modes.View) {
				if(isHtmlFile() || isLogFile()) {
					try {
						File file = new File(settings.get(ConfigTags.PathToReplaySequence)).getCanonicalFile();
						Desktop.getDesktop().browse(file.toURI());
					} catch (IOException e) {
						popupMessage("Exception: Check the path of the file, something is wrong");
						System.out.println("Exception: Check the path of the file, something is wrong");
					} catch (NoSuchTagException e) {
						popupMessage("Exception: ConfigTags.PathToReplaySequence is missing");
						System.out.println("Exception: ConfigTags.PathToReplaySequence is missing");
					}
				} else if (!findHTMLreport().contains("error")) {
					try {
						File htmlFile = new File(findHTMLreport());
						Desktop.getDesktop().browse(htmlFile.toURI());
					} catch (IOException e) {
						popupMessage("Exception: Select a log or html file to visualize the TESTAR results");
						System.out.println("Exception: Select a log or html file to visualize the TESTAR results");
					}
				} else {
					popupMessage("Please select a file.html (output/reports) to use in the View mode");
					System.out.println("Exception: Please select a file.html (output/reports) to use in the View mode");
				}
			} else if (mode() == Modes.Replay && isValidFile()) {
				new ReplayMode().runReplayLoop(this);
			} else if (mode() == Modes.Spy) {
				new SpyMode().runSpyLoop(this);
			} else if(mode() == Modes.Record) {
				//new RecordMode().runRecordLoop(this);
				System.out.println("Dear User, TESTAR Record mode is disabled temporarily.");
			} else if (mode() == Modes.Generate) {
				new GenerateMode().runGenerateOuterLoop(this);
			}

		}catch(WinApiException we) {
			if(we.getMessage() != null) {
				popupMessage(we.getMessage());
				System.out.println(we.getMessage());
			}

			we.printStackTrace();

			this.mode = Modes.Quit;
		}catch(SessionNotCreatedException e) {
    		if(e.getMessage()!=null && e.getMessage().contains("Chrome version")) {

    			String msg = "*** Unsupported versions exception: Chrome browser and Selenium WebDriver versions *** \n"
    					+ "Please verify your Chrome browser version: chrome://settings/help \n"
    					+ "And download the appropriate ChromeDriver version: https://chromedriver.chromium.org/downloads \n"
    					+ "\n"
						//TODO check when implementing other webdriver than chromedriver
						//TODO remove when automatically killing webdriver process when creating the session fails
    					+ "As a result of this error, there is probably a \"chromedriver.exe\" process running. \n"
    					+ "Please use Windows Task Manager to stop that process.";

    			popupMessage(msg);
    			System.out.println(msg);
    			System.out.println(e.getMessage());
    		}else {
    			System.out.println("ERROR starting Selenium WebDriver");
    			e.printStackTrace();
    		}
		}catch (IllegalStateException e) {
			if (e.getMessage()!=null && e.getMessage().contains("driver executable does not exist")) {

				String msg = "Exception: Check whether chromedriver.exe path: \n"
				+settings.get(ConfigTags.SUTConnectorValue)
				+"\n exists and is correctly defined";

				popupMessage(msg);
				System.out.println(msg);
			}else {
				e.printStackTrace();
			}
		}catch(SystemStartException SystemStartException) {
			SystemStartException.printStackTrace();
			this.mode = Modes.Quit;
		}
		// can there be other kind of exceptions?

		//allowing close-up in the end of test session:
		closeTestSession();
		//Closing TESTAR EventHandler
		closeTestarTestSession();
	}

	/**
	 * Initialize TESTAR with the given settings:
	 *
	 * @param settings
	 */
	protected void initialize(Settings settings) {

		visualizationOn = settings.get(ConfigTags.VisualizeActions);

		startTime = Util.time();
		this.settings = settings;
		mode = settings.get(ConfigTags.Mode);

		builder = NativeLinker.getNativeStateBuilder(
				settings.get(ConfigTags.TimeToFreeze),
				settings.get(ConfigTags.AccessBridgeEnabled),
				settings.get(ConfigTags.SUTProcesses)
				);

		logOracleEnabled = settings.get(ConfigTags.LogOracleEnabled, false);

		if ( mode() == Modes.Generate || /*mode() == Modes.Record ||*/ mode() == Modes.Replay ) {
			//Create the output folders
			OutputStructure.calculateOuterLoopDateString();
			OutputStructure.sequenceInnerLoopCount = 0;
			OutputStructure.createOutputSUTname(settings);
			OutputStructure.createOutputFolders();

			// new state model manager
			stateModelManager = StateModelManagerFactory.getStateModelManager(settings);
		}

		//EventHandler is implemented in RuntimeControlsProtocol (super class):
		eventHandler = initializeEventHandler();

		//Initialize the JNativeHook library and register keyboard and mouse listeners
		NativeHookManager.registerNativeHook(eventHandler);

		LogSerialiser.log("'" + mode() + "' mode active.\n", LogSerialiser.LogLevel.Info);
	}

	/**
	 * Check if the selected file to Replay or View contains a valid fragment object
	 */

	private boolean isValidFile(){
		try {

			File seqFile = new File(settings.get(ConfigTags.PathToReplaySequence));

			FileInputStream     fis = new FileInputStream(seqFile);
			BufferedInputStream bis = new BufferedInputStream(fis);
			GZIPInputStream   gis = new GZIPInputStream(bis);
			ObjectInputStream ois = new ObjectInputStream(gis);

			ois.readObject();
			ois.close();

		} catch (ClassNotFoundException | IOException e) {
			popupMessage("ERROR: File is not readable, please select a correct TESTAR sequence file");
			System.out.println("ERROR: File is not readable, please select a correct file (output/sequences)");
			e.printStackTrace();

		    return false;
		}

		return true;
	}

	/**
	 * Check if the selected file to View is a html file
	 */
	private boolean isHtmlFile() {
		if(settings.get(ConfigTags.PathToReplaySequence).contains(".html"))
			return true;

		return false;
	}

	/**
	 * Check if the selected file to View is a log file
	 */
	private boolean isLogFile() {
		if(settings.get(ConfigTags.PathToReplaySequence).contains(".log"))
			return true;

		return false;
	}

	/**
	 * If the user selects a .testar object file to use the View mode, try to find the corresponding html file
	 */
	private String findHTMLreport() {
		String foundedHTML = "error";
		String path = settings.get(ConfigTags.PathToReplaySequence);
		if(path.contains(".testar")) {
			path = path.replace(".testar", ".html");

			int startIndex = path.indexOf(File.separator + "sequences");
			int endIndex = path.indexOf(File.separator, startIndex+2);

			String replace = path.substring(startIndex, endIndex+1);

			path = path.replace(replace, File.separator + "reports" + File.separator);
			if (new File(path).exists())
				foundedHTML = path;
		}

		return foundedHTML;
	}

	/**
	 * Show a popup message to get the user's attention and inform him.
	 * Only if GUI option is enabled (disabled for CI)
	 */
	private void popupMessage(String message) {
		if(settings.get(ConfigTags.ShowVisualSettingsDialogOnStartup)) {
			JFrame frame = new JFrame();

			JTextArea textArea = new JTextArea(message);
			textArea.setWrapStyleWord(true);
			textArea.setLineWrap(true);
			textArea.setEditable(false);

			JScrollPane scrollPane = new JScrollPane(textArea);
			scrollPane.setPreferredSize(new java.awt.Dimension(400, 200));

			JOptionPane.showMessageDialog(frame, scrollPane);
		}
	}

	/**
	 * This method is called from runGenerate() to initialize TESTAR for Generate-mode
	 */
	void initGenerateMode() {
		//TODO check why LogSerializer is closed and started again in the beginning of Generate-mode
		sequenceCount = 1;
		escAttempts = 0;
	}

	/**
	 * This method creates the name for generated sequence that can be replayed
	 * and starts the LogSerialiser for outputting the test sequence
	 *
	 * @return name of the generated sequence file
	 */
	String getAndStoreGeneratedSequence() {
		//TODO refactor replayable sequences with something better (model perhaps?)

		String sequenceCountDir = "_sequence_" + OutputStructure.sequenceInnerLoopCount;

		String generatedSequenceName = OutputStructure.sequencesOutputDir
				+ File.separator + OutputStructure.startInnerLoopDateString + "_"
				+ OutputStructure.executedSUTname + sequenceCountDir + ".testar";

		String logFileName = OutputStructure.logsOutputDir
				+ File.separator + OutputStructure.startInnerLoopDateString + "_"
				+ OutputStructure.executedSUTname + sequenceCountDir + ".log";

		String screenshotsDirectory = OutputStructure.startInnerLoopDateString + "_"
				+ OutputStructure.executedSUTname + sequenceCountDir;

		try {
			LogSerialiser.start(new PrintStream(new BufferedOutputStream(new FileOutputStream(new File(
					logFileName), true))),
					settings.get(ConfigTags.LogLevel));
		}catch (NoSuchTagException | FileNotFoundException e3) {
			e3.printStackTrace();
		}

		ScreenshotSerialiser.start(OutputStructure.screenshotsOutputDir, screenshotsDirectory);

		return generatedSequenceName;
	}

	/**
	 * This method creates a temporary file for saving the test sequence (that can be replayed)
	 * The name of the temporary file is changed in the end of the test sequence (not in this function)
	 *
	 * @return temporary file for saving the test sequence
	 */
	File getAndStoreSequenceFile() {
		LogSerialiser.log("Creating new sequence file...\n", LogSerialiser.LogLevel.Debug);

		String sequenceObject = settings.get(ConfigTags.TempDir)
				+ File.separator + OutputStructure.startInnerLoopDateString + "_"
				+ OutputStructure.executedSUTname
				+ "_sequence_" + OutputStructure.sequenceInnerLoopCount + ".testar";

		final File currentSeqObject = new File(sequenceObject);

		try {
			TestSerialiser.start(new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(currentSeqObject, true))));
			LogSerialiser.log("Created new sequence file!\n", LogSerialiser.LogLevel.Debug);
		} catch (IOException e) {
			LogSerialiser.log("I/O exception creating new sequence file\n", LogSerialiser.LogLevel.Critical);
		}

		return currentSeqObject;
	}

	/**
	 * This method calls the startSystem() and starts the LogSerialiser. 
	 *
	 * @return SUT system
	 */
	SUT startSUTandLogger() {
		// Start the SUT by launching the process or connecting to a running one
		SUT system = startSystem();
		// Reset LogSerialiser
		LogSerialiser.finish();
		LogSerialiser.exit();
		startOfSutDateString = Util.dateString(DATE_FORMAT);
		LogSerialiser.log(startOfSutDateString + " Starting SUT ...\n", LogSerialiser.LogLevel.Info);
		LogSerialiser.log("SUT is running!\n", LogSerialiser.LogLevel.Debug);
		LogSerialiser.log("Building canvas...\n", LogSerialiser.LogLevel.Debug);

		// Activate process Listeners if enabled in the test.settings
		if(enabledProcessListener)
			processListener.startListeners(system, settings);

		return system;
	}

	/**
	 * This method is initializing TESTAR for the start of test sequence
	 *
	 * @param system
	 */
	void startTestSequence(SUT system) {
		actionCount = 1;
		lastSequenceActionNumber = settings().get(ConfigTags.SequenceLength) + actionCount - 1;
		passSeverity = Verdict.SEVERITY_OK;
		processVerdict = Verdict.OK;
		this.cv = buildCanvas();
	}

	/**
	 * Closing test sequence of TESTAR in normal operation
	 */
	void endTestSequence(){
		LogSerialiser.log("Releasing canvas...\n", LogSerialiser.LogLevel.Debug);
		cv.release();
		ScreenshotSerialiser.exit();
		TestSerialiser.exit();
		//        String stopDateString = Util.dateString(DATE_FORMAT);
		//        String durationDateString = Util.diffDateString(DATE_FORMAT, startOfSutDateString, stopDateString);
		//        LogSerialiser.log("TESTAR stopped execution at " + stopDateString + "\n", LogSerialiser.LogLevel.Critical);
		//        LogSerialiser.log("Test duration was " + durationDateString + "\n", LogSerialiser.LogLevel.Critical);
		LogSerialiser.flush();
		LogSerialiser.finish();
		LogSerialiser.exit();

		//Delete the temporally testar file
		try {
			Util.delete(currentSeq);
		} catch (IOException e2) {
			LogSerialiser.log("I/O exception deleting <" + currentSeq + ">\n", LogSerialiser.LogLevel.Critical);
		}
	}

	/**
	 * Shutting down TESTAR in case of exception during Generate-mode outer loop (test sequence generation)
	 *
	 * @param system
	 * @param e
	 */
	void emergencyTerminateTestSequence(SUT system, Exception e){
		SystemProcessHandling.killTestLaunchedProcesses(this.contextRunningProcesses);
		ScreenshotSerialiser.finish();
		TestSerialiser.finish();
		ScreenshotSerialiser.exit();
		LogSerialiser.log("Exception <" + e.getMessage() + "> has been caught\n", LogSerialiser.LogLevel.Critical); // screenshots must be serialised
		int i = 1;
		StringBuffer trace = new StringBuffer();
		for (StackTraceElement t : e.getStackTrace())
			trace.append("\n\t[" + i++ + "] " + t.toString());
		System.out.println("Exception <" + e.getMessage() + "> has been caught; Stack trace:" + trace.toString());
		stopSystem(system);
		TestSerialiser.exit();
		LogSerialiser.flush();
		LogSerialiser.finish();
		LogSerialiser.exit();
		this.mode = Modes.Quit;

		//Delete the temporally testar file
		try {
			Util.delete(currentSeq);
		} catch (IOException e2) {
			LogSerialiser.log("I/O exception deleting <" + currentSeq + ">\n", LogSerialiser.LogLevel.Critical);
		}
	}

	void classifyAndCopySequenceIntoAppropriateDirectory(Verdict finalVerdict, String generatedSequence, File currentSeq){
		// Check if user wants to save or not the sequences without faults
		if (settings.get(ConfigTags.OnlySaveFaultySequences, false) && finalVerdict.severity() == Verdict.OK.severity()) {
			LogSerialiser.log("Skipped generated sequence OK (\"" + generatedSequence + "\")\n", LogSerialiser.LogLevel.Info);
		} else {
			LogSerialiser.log("Saved generated sequence (\"" + generatedSequence + "\")\n", LogSerialiser.LogLevel.Info);
			FileHandling.copyClassifiedSequence(generatedSequence, currentSeq, finalVerdict);
		}
	}

	/**
	 * Saving the action into the fragment for replayable sequence
	 *
	 * @param action
	 */
	void saveActionIntoFragmentForReplayableSequence(Action action, State state, Set<Action> actions) {
	    // create fragment
		TaggableBase fragment = new TaggableBase();
	    fragment.set(ExecutedAction, action);
	    fragment.set(ActionSet, actions);
	    fragment.set(ActionDuration, settings().get(ConfigTags.ActionDuration));
	    fragment.set(ActionDelay, settings().get(ConfigTags.TimeToWaitAfterAction));
	    fragment.set(SystemState, state);
	    fragment.set(OracleVerdict, getFinalVerdict());

	    //Find the target widget of the current action, and save the title into the fragment
	    if (state != null && action.get(Tags.OriginWidget, null) != null){
	        fragment.set(Tags.Title, action.get(Tags.OriginWidget).get(Tags.Title, ""));
	    }

	    LogSerialiser.log("Writing fragment to sequence file...\n", LogSerialiser.LogLevel.Debug);
	    TestSerialiser.write(fragment);
	}

	/**
	 * Wait until fragments have been written then close the test serialiser
	 */
	void writeAndCloseFragmentForReplayableSequence() {
	    //Wait since TestSerialiser and ScreenshotSerialiser write all fragments/src on sequence File
	    while(!TestSerialiser.isSavingQueueEmpty() || !ScreenshotSerialiser.isSavingQueueEmpty()) {
	        synchronized (this) {
	            try {
	                this.wait(1000);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
	    }

	    //closing ScreenshotSerialiser and TestSerialiser
	    ScreenshotSerialiser.finish();
	    ScreenshotSerialiser.exit();
	    TestSerialiser.finish();
	    TestSerialiser.exit();

	    LogSerialiser.log("Wrote fragment to sequence file!\n", LogSerialiser.LogLevel.Debug);
	    LogSerialiser.log("Sequence " + sequenceCount + " finished.\n", LogSerialiser.LogLevel.Info);
	}

	/**
	 * Saving the action information into the logs
	 *
	 * @param state
	 * @param action
	 * @param actionMode
	 */
	void saveActionInfoInLogs(State state, Action action, String actionMode) {

		//Obtain action information
		String[] actionRepresentation = Action.getActionRepresentation(state,action,"\t");

		//Output/logs folder
		LogSerialiser.log(String.format(actionMode+" [%d]: %s\n%s",
				actionCount,
				"\n @Action ConcreteID = " + action.get(Tags.ConcreteID, "ConcreteID not available") +
				" AbstractID = " + action.get(Tags.AbstractID, "AbstractID not available") +"\n"+

				" @State ConcreteID = " + state.get(Tags.ConcreteID, "ConcreteID not available") +
				" AbstractID = " + state.get(Tags.AbstractID, "AbstractID not available") +"\n",
				actionRepresentation[0]) + "\n",
				LogSerialiser.LogLevel.Info);
	}

	/**
	 * This method is called before the first test sequence, allowing for example setting up the test environment
	 */
	@Override
	protected void initTestSession() {

	}

	/**
	 * This methods is called before each test sequence, allowing for example using external profiling software on the SUT
	 */
	@Override
	protected void preSequencePreparations() {
		if(settings.get(ConfigTags.Mode) != Modes.Spy)
			reportManager = new ReportManager((mode() == Modes.Replay), settings());
		if (logOracleEnabled) {
			logOracle = createLogOracle(settings);
			logOracle.initialize();
		}
	}

	/**
	 * Method for creating the LogOracle. Can optionally be overridden in subclasses.
	 * @param settings
	 * @return
	 */
	public Oracle createLogOracle (Settings settings) {
		return new LogOracle(settings);
	}

	protected Canvas buildCanvas() {
		return NativeLinker.getNativeCanvas(Pen.PEN_DEFAULT);
	}

	@Override
	protected void beginSequence(SUT system, State state){
		// Reset the final verdict for the new sequence
		finalVerdict = Verdict.OK;
	}

	@Override
	protected void finishSequence(){
		// TODO: Consider moving kill test launched processes to stop system
		SystemProcessHandling.killTestLaunchedProcesses(this.contextRunningProcesses);
	}

	protected SUT startSystem() throws SystemStartException {
		this.contextRunningProcesses = SystemProcessHandling.getRunningProcesses("START");
		try{
			for(String d : settings().get(ConfigTags.Delete))
				Util.delete(d);
			for(Pair<String, String> fromTo : settings().get(ConfigTags.CopyFromTo))
				Util.copyToDirectory(fromTo.left(), fromTo.right());
		}catch(IOException ioe){
			throw new SystemStartException(ioe);
		}
		String sutConnectorType = settings().get(ConfigTags.SUTConnector);

		// WindowsTitle, ProcessName, and CommandLine must have a SUTConnectorValue:
		String connectorValue = settings().get(ConfigTags.SUTConnectorValue);
		if(connectorValue == null || connectorValue.length() == 0) {
			String msg = "It seems that the SUTConnectorValue setting is null or empty!\n"
					+ "Please provide a valid value for the SUTConnector: " + sutConnectorType;
			popupMessage(msg);
			throw new SystemStartException(msg);
		}

		if (sutConnectorType.equals(Settings.SUT_CONNECTOR_WINDOW_TITLE)) {
			SutConnectorWindowTitle sutConnector = new SutConnectorWindowTitle(settings().get(ConfigTags.SUTConnectorValue), 
					Math.round(settings().get(ConfigTags.StartupTime).doubleValue() * 1000.0), 
					builder,
					settings().get(ConfigTags.ForceForeground));
			return sutConnector.startOrConnectSut();
		}else if (sutConnectorType.startsWith(Settings.SUT_CONNECTOR_PROCESS_NAME)) {
			SutConnectorProcessName sutConnector = new SutConnectorProcessName(settings().get(ConfigTags.SUTConnectorValue), 
					Math.round(settings().get(ConfigTags.StartupTime).doubleValue() * 1000.0));
			return sutConnector.startOrConnectSut();
		}else{
			//Read the settings to know if user wants to start the process listener
			if(settings.get(ConfigTags.ProcessListenerEnabled)) {
				enabledProcessListener = processListener.enableProcessListeners(settings);
			}

			// for most windows applications and most jar files, this is where the SUT gets created!
			SutConnectorCommandLine sutConnector = new SutConnectorCommandLine(builder, enabledProcessListener, settings);
			//TODO startupTime and maxEngageTime seems to be the same, except one is double and the other is long?
			return sutConnector.startOrConnectSut();
		}
	}


	/**
	 * This method gets the state of the SUT
	 * It also call getVerdict() and saves it into the state
	 *
	 * @param system
	 * @return
	 * @throws StateBuildException
	 */
	@Override
	protected State getState(SUT system) throws StateBuildException {
		Assert.notNull(system);
		State state = builder.apply(system);

		buildStateIdentifiers(state);
		state = ProtocolUtil.calculateZIndices(state);

		setStateForClickFilterLayerProtocol(state);

		if(settings.get(ConfigTags.Mode) == Modes.Spy)
			return state;

		Verdict verdict = getVerdict(state);
		state.set(Tags.OracleVerdict, verdict);

		setStateScreenshot(state);

		if(mode() != Modes.Spy && verdict.severity() >= settings().get(ConfigTags.FaultThreshold))
		{
			faultySequence = true;
			LogSerialiser.log("Detected fault: " + verdict + "\n", LogSerialiser.LogLevel.Critical);
			// this was added to kill the SUT if it is frozen:
			if(verdict.severity() == Verdict.SEVERITY_NOT_RESPONDING)
			{
				//if the SUT is frozen, we should kill it!
				LogSerialiser.log("SUT frozen, trying to kill it!\n", LogSerialiser.LogLevel.Critical);
				SystemProcessHandling.killRunningProcesses(system, 100);
			}
		}
		else if(verdict.severity() != Verdict.SEVERITY_OK && verdict.severity() > passSeverity)
		{
			passSeverity = verdict.severity();
			LogSerialiser.log("Detected warning: " + verdict + "\n", LogSerialiser.LogLevel.Critical);
		}

		reportManager.addState(state);
		latestState = state;

		return state;
	}

	/**
	 * Take a Screenshot of the State and associate the path into state tag
	 */
	private void setStateScreenshot(State state) {
		Shape viewPort = state.get(Tags.Shape, null);
		if(viewPort != null){
			if(NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WEBDRIVER)){
				state.set(Tags.ScreenshotPath, WdProtocolUtil.getStateshot(state));
			}
			else if (NativeLinker.getPLATFORM_OS().contains(OperatingSystems.ANDROID)) {
				state.set(Tags.ScreenshotPath, AndroidProtocolUtil.getStateshot(state));
			}
			else if (NativeLinker.getPLATFORM_OS().contains(OperatingSystems.IOS)) {
				state.set(Tags.ScreenshotPath, IOSProtocolUtil.getStateshot(state));
			}
			else{
				state.set(Tags.ScreenshotPath, ProtocolUtil.getStateshot(state));
			}
		}
	}

	@Override
	protected Verdict getVerdict(State state){
		Assert.notNull(state);
		//-------------------
		// ORACLES FOR FREE
		//-------------------

		// if the SUT is not running and closed unexpectedly, we assume it crashed
		if(!state.get(IsRunning, false))
			return new Verdict(Verdict.SEVERITY_UNEXPECTEDCLOSE, "System is offline! Closed Unexpectedly! I assume it crashed!");

		// if the SUT does not respond within a given amount of time, we assume it crashed
		if(state.get(Tags.NotResponding, false)){
			return new Verdict(Verdict.SEVERITY_NOT_RESPONDING, "System is unresponsive! I assume something is wrong!");
		}

		//------------------------
		// ORACLES ALMOST FOR FREE
		//------------------------

		if (this.suspiciousTitlesPattern == null)
			this.suspiciousTitlesPattern = Pattern.compile(settings().get(ConfigTags.SuspiciousTags), Pattern.UNICODE_CHARACTER_CLASS);

		// search all widgets for suspicious String Values
		Verdict suspiciousValueVerdict = Verdict.OK;
		for(Widget w : state) {
			suspiciousValueVerdict = suspiciousStringValueMatcher(w);
			if(suspiciousValueVerdict.severity() == Verdict.SEVERITY_SUSPICIOUS_TAG) {
				return suspiciousValueVerdict;
			}
		}

		if ( logOracleEnabled ) {
			Verdict logVerdict = logOracle.getVerdict(state);
			if ( logVerdict.severity() == Verdict.SEVERITY_SUSPICIOUS_LOG ) {
				return logVerdict;
			}
		}

		// if everything was OK ...
		return Verdict.OK;
	}

	private Verdict suspiciousStringValueMatcher(Widget w) {
		Matcher m;

		for(String tagForSuspiciousOracle : settings.get(ConfigTags.TagsForSuspiciousOracle)){
			String tagValue = "";
			// First finding the Tag that matches the TagsToFilter string, then getting the value of that Tag:
			for(Tag<?> tag : w.tags()){
				if(w.get(tag, null) != null && tag.name().equals(tagForSuspiciousOracle)){
					// Force the replacement of new line characters to avoid the usage of (?s) regex in the regular expression
					tagValue = w.get(tag).toString().replace("\n", " ").replace("\r", " ");
					break;
					//System.out.println("DEBUG: tag found, "+tagToFilter+"="+tagValue);
				}
			}

			//Check whether the Tag value is empty or null
			if (tagValue == null || tagValue.isEmpty())
				continue; //no action

			//Ignore value ValuePattern for UIAEdit widgets
			if(tagValue.equals("ValuePattern") && w.get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIAEdit")) {
				continue;
			}

			m = this.suspiciousTitlesMatchers.get(tagValue);
			if (m == null){
				m = this.suspiciousTitlesPattern.matcher(tagValue);
				this.suspiciousTitlesMatchers.put(tagValue, m);
			}

			if (m.matches()){
				Visualizer visualizer = Util.NullVisualizer;
				Pen RedPen = Pen.newPen().setColor(Color.Red).setFillPattern(FillPattern.None).setStrokePattern(StrokePattern.Solid).build();
				// visualize the problematic widget, by marking it with a red box
				if(w.get(Tags.Shape, null) != null)
					visualizer = new ShapeVisualizer(RedPen, w.get(Tags.Shape), "Suspicious Tag", 0.5, 0.5);
				return new Verdict(Verdict.SEVERITY_SUSPICIOUS_TAG,
						"Discovered suspicious widget '" + tagForSuspiciousOracle + "' : '" + tagValue + "'.", visualizer);
			}
		}
		return Verdict.OK;
	}

	/**
	 * This methods prepares for deriving actions, but does not really derive them yet. This is left for lower
	 * level protocols. Here the parameters are set in case unwanted processes need to be killed or the SUT needs to be brought back
	 * to foreground. The latter is then done by selectActions in the AbstractProtocol.
	 * @param system
	 * @param state
	 * @return
	 * @throws ActionBuildException
	 */
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException{
		Assert.notNull(state);
		Set<Action> actions = new HashSet<Action>();

		// If there is an unwanted process running, we need to kill it.
		// This is an unwanted process that is defined in the filter.
		// So we set this.forceKillProcess = process.right();
		// And then select action will take care of making the next action to select the killing of the process.
		String processRE = settings().get(ConfigTags.ProcessesToKillDuringTest);
		if (processRE != null && !processRE.isEmpty()){
			state.set(Tags.RunningProcesses, system.getRunningProcesses());
			for(Pair<Long, String> process : state.get(Tags.RunningProcesses, Collections.<Pair<Long,String>>emptyList())){
				if(process.left().longValue() != system.get(Tags.PID).longValue() &&
						process.right() != null && process.right().matches(processRE)){ // pid x name

					String forceKillProcess = process.right();
					System.out.println("will kill unwanted process: " + process.left().longValue() + " (SYSTEM <" + system.get(Tags.PID).longValue() + ">)");

					LogSerialiser.log("Forcing kill-process <" + forceKillProcess + "> action\n", LogSerialiser.LogLevel.Info);
					Action killProcessAction = KillProcess.byName(forceKillProcess, 0);
					killProcessAction.set(Tags.Desc, "Kill Process with name '" + forceKillProcess + "'");
					killProcessAction.mapActionToWidget(state);
					return new HashSet<>(Collections.singletonList(killProcessAction));
				}
			}
		}

		// If the system is in the background, we need to force it into the foreground!
		// We set this.forceToForeground to true and selectAction will make sure that the next action we will select
		// is putting the SUT back into the foreground.
		if(!state.get(Tags.Foreground, true) && system.get(Tags.SystemActivator, null) != null){
			LogSerialiser.log("Forcing SUT activation (bring to foreground) action\n", LogSerialiser.LogLevel.Info);
			Action foregroundAction = new ActivateSystem();
			foregroundAction.set(Tags.Desc, "Bring the system to the foreground.");
			foregroundAction.mapActionToWidget(state);
			foregroundAction.set(Tags.Role, Roles.System);
			return new HashSet<>(Collections.singletonList(foregroundAction));
		}

		//Note this list is always empty in this deriveActions.
		return actions;
	}

	//TODO is this process handling Windows specific? move to SystemProcessHandling
	/**
	 * If unwanted processes need to be killed, the action returns an action to do that. If the SUT needs
	 * to be put in the foreground, then the action that is returned is putting it in the foreground.
	 * Otherwise it returns null.
	 * @param state
	 * @param actions
	 * @return null if no preSelected actions are needed.
	 */
	protected Set<Action> preSelectAction(SUT system, State state, Set<Action> actions){
		//Assert.isTrue(actions != null && !actions.isEmpty());

		// TESTAR didn't find any actions in the State of the SUT
		// It is set in a method actionExecuted that is not being called anywhere (yet?)
		if (actions.isEmpty()){
			System.out.println("DEBUG: Forcing ESC action in preActionSelection : Actions derivation seems to be EMPTY !");
			LogSerialiser.log("Forcing ESC action\n", LogSerialiser.LogLevel.Info);
			Action escAction = new AnnotatingActionCompiler().hitKey(KBKeys.VK_ESCAPE);
			escAction.mapActionToWidget(state);
			buildEnvironmentActionIdentifiers(state, escAction);
			reportManager.addActions(Collections.singleton(escAction));
			return new HashSet<>(Collections.singletonList(escAction));
		}

		// If there is an ActivateSystem action
		// We need to force its selection to move the system to the foreground
		Action forceForegroungAction = actions.stream().filter(a -> a instanceof ActivateSystem)
				.findAny().orElse(null);
		if (forceForegroungAction != null) {
			System.out.println("DEBUG: Forcing the System to be in the foreground !");
			reportManager.addActions(Collections.singleton(forceForegroungAction));
			return new HashSet<>(Collections.singletonList(forceForegroungAction));
		}

		reportManager.addActions(actions);

		return actions;
	}

	final static double MAX_ACTION_WAIT_FRAME = 1.0; // (seconds)
	//TODO move the CPU metric to another helper class that is not default "TrashBinCode" or "SUTprofiler"
	//TODO check how well the CPU usage based waiting works
	protected boolean executeAction(SUT system, State state, Action action){

		// adding the action that is going to be executed into report:
		reportManager.addSelectedAction(state, action);

		if(NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WEBDRIVER)){
			//System.out.println("DEBUG: Using WebDriver specific action shot.");
			WdProtocolUtil.getActionshot(state,action);
		}else{
			//System.out.println("DEBUG: normal action shot");
			ProtocolUtil.getActionshot(state,action);
		}

		double waitTime = settings.get(ConfigTags.TimeToWaitAfterAction);

		try{
			double halfWait = waitTime == 0 ? 0.01 : waitTime / 2.0; // seconds
			Util.pause(halfWait); // help for a better match of the state' actions visualization
			action.run(system, state, settings.get(ConfigTags.ActionDuration));
			int waitCycles = (int) (MAX_ACTION_WAIT_FRAME / halfWait);
			long actionCPU;
			do {
				long CPU1[] = NativeLinker.getCPUsage(system);
				Util.pause(halfWait);
				long CPU2[] = NativeLinker.getCPUsage(system);
				actionCPU = ( CPU2[0] + CPU2[1] - CPU1[0] - CPU1[1] );
				waitCycles--;
			} while (actionCPU > 0 && waitCycles > 0);

			//Save the executed action information into the logs
			saveActionInfoInLogs(state, action, "ExecutedAction");

			return true;
		}catch(ActionFailedException afe){
			return false;
		}
	}

	protected boolean replayAction(SUT system, State state, Action action, double actionWaitTime, double actionDuration){

	    // adding the action that is replayed into report:
	    reportManager.addSelectedAction(state, action);

	    // Get an action screenshot based on the NativeLinker platform
	    if(NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WEBDRIVER)) {
	        WdProtocolUtil.getActionshot(state,action);
	    } else {
	        ProtocolUtil.getActionshot(state,action);
	    }

	    try{
	        double halfWait = actionWaitTime == 0 ? 0.01 : actionWaitTime / 2.0; // seconds
	        Util.pause(halfWait); // help for a better match of the state' actions visualization
	        action.run(system, state, actionDuration);
	        int waitCycles = (int) (MAX_ACTION_WAIT_FRAME / halfWait);
	        long actionCPU;
	        do {
	            long CPU1[] = NativeLinker.getCPUsage(system);
	            Util.pause(halfWait);
	            long CPU2[] = NativeLinker.getCPUsage(system);
	            actionCPU = ( CPU2[0] + CPU2[1] - CPU1[0] - CPU1[1] );
	            waitCycles--;
	        } while (actionCPU > 0 && waitCycles > 0);

	        //Save the replayed action information into the logs
	        saveActionInfoInLogs(state, action, "ReplayedAction");

	        return true;
	    }catch(ActionFailedException afe){
	        return false;
	    }
	}

	/**
	 * This method is here, so that ClickFilterLayerProtocol can override it, and the behaviour is updated
	 *
	 * @param canvas
	 * @param state
	 * @param actions
	 */
	protected void visualizeActions(Canvas canvas, State state, Set<Action> actions){
		SutVisualization.visualizeActions(canvas, state, actions);
	}

	/**
	 * Select one of the available actions using the action selection algorithm of your choice. 
	 * The default select action mechanism tries to use the state model action selector. 
	 * If the state model is not enabled, it returns a random action. 
	 *
	 * @param state the SUT's current state
	 * @param actions the set of derived actions
	 * @return  the selected action
	 */
	protected Action selectAction(State state, Set<Action> actions){
		Assert.isTrue(actions != null && !actions.isEmpty());

		//Using the action selector of the state model:
		Action retAction = stateModelManager.getAbstractActionToExecute(actions);
		// If state model is not enabled, use random:
		if (retAction == null) {
			return RandomActionSelector.selectRandomAction(actions);
		}
		return retAction;
	}

	/**
	 * STOP criteria for selecting more actions for a sequence
	 * @param state
	 * @return
	 */
	protected boolean moreActions(State state) {
		return (!settings().get(ConfigTags.StopGenerationOnFault) || !faultySequence) &&
				state.get(Tags.IsRunning, false) && !state.get(Tags.NotResponding, false) &&
				//actionCount() < settings().get(ConfigTags.SequenceLength) &&
				actionCount() <= lastSequenceActionNumber &&
				timeElapsed() < settings().get(ConfigTags.MaxTime);
	}

	/**
	 * STOP criteria deciding whether more sequences are required in a test run
	 * @return
	 */
	protected boolean moreSequences() {
		return sequenceCount() <= settings().get(ConfigTags.Sequences) &&
				timeElapsed() < settings().get(ConfigTags.MaxTime);
	}

	@Override
	protected void stopSystem(SUT system) {

		if (system != null){
			AutomationCache ac = system.getNativeAutomationCache();
			if (ac != null)
				ac.releaseCachedAutomationElements();
		}
		if(system !=null){
			system.stop();
		}
	}
	
	/**
	 * This methods is called after each test sequence, allowing for example using external profiling software on the SUT
	 */
	@Override
	protected void postSequenceProcessing() {

		String status = "";
		String statusInfo = "";

		if(mode() == Modes.Replay) {
			reportManager.addTestVerdict(getReplayVerdict().join(processVerdict));
			status = (getReplayVerdict().join(processVerdict)).verdictSeverityTitle();
			statusInfo = (getReplayVerdict().join(processVerdict)).info();
		}
		else {
			reportManager.addTestVerdict(getFinalVerdict());
			status = (getFinalVerdict()).verdictSeverityTitle();
			statusInfo = (getFinalVerdict()).info();
		}

		String sequencesPath = getGeneratedSequenceName();
		try {
			sequencesPath = new File(getGeneratedSequenceName()).getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}

		statusInfo = statusInfo.replace("\n"+Verdict.OK.info(), "");

		//Timestamp(generated by logger) SUTname Mode SequenceFileObject Status "StatusInfo"
		INDEXLOG.info(OutputStructure.executedSUTname
					  + " " + settings.get(ConfigTags.Mode, mode())
					  + " " + sequencesPath
					  + " " + status + " \"" + statusInfo + "\"" );

		reportManager.finishReport();
	}

	/**
	 * method for closing the internal TESTAR test session
	 */
	private void closeTestarTestSession(){
		// Cleaning the JNativeHook native listeners started in initialize()
		NativeHookManager.unregisterNativeListener(eventHandler);
	}

	@Override
	protected void closeTestSession() {
	}

	/**
	 * Use CodingManager to create the Widget and State identifiers:
	 * ConcreteID, AbstractID,
	 * Abstract_R_ID, Abstract_R_T_ID, Abstract_R_T_P_ID
	 *
	 * @param state
	 */
	protected void buildStateIdentifiers(State state) {
	    CodingManager.buildIDs(state);
	}

	/**
	 * Use CodingManager to create the Actions identifiers:
	 * ConcreteID, AbstractID
	 *
	 * @param state
	 * @param actions
	 */
	protected void buildStateActionsIdentifiers(State state, Set<Action> actions) {
	    CodingManager.buildIDs(state, actions);
	    for(Action a : actions)
	    	if(a.get(Tags.AbstractID, null) == null)
	    		buildEnvironmentActionIdentifiers(state, a);
	}

	/**
	 * Use CodingManager to create the specific environment Action identifiers:
	 * ConcreteID, AbstractID
	 *
	 * @param state
	 * @param action
	 */
	protected void buildEnvironmentActionIdentifiers(State state, Action action) {
	    CodingManager.buildEnvironmentActionIDs(state, action);
	}

}
