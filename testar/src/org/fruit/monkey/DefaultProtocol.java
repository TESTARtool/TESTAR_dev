/***************************************************************************************************
 *
 * Copyright (c) 2013 - 2021 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 - 2021 Open Universiteit - www.ou.nl
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


package org.fruit.monkey;

import static org.fruit.alayer.Tags.ActionDelay;
import static org.fruit.alayer.Tags.ActionDuration;
import static org.fruit.alayer.Tags.ActionSet;
import static org.fruit.alayer.Tags.Desc;
import static org.fruit.alayer.Tags.ExecutedAction;
import static org.fruit.alayer.Tags.IsRunning;
import static org.fruit.alayer.Tags.OracleVerdict;
import static org.fruit.alayer.Tags.SystemState;
import static org.fruit.monkey.ConfigTags.LogLevel;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import es.upv.staq.testar.*;
import nl.ou.testar.*;
import nl.ou.testar.HtmlReporting.Reporting;
import nl.ou.testar.StateModel.StateModelManager;
import nl.ou.testar.StateModel.StateModelManagerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fruit.Assert;
import org.fruit.Pair;
import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.*;
import org.fruit.alayer.devices.AWTMouse;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.devices.Mouse;
import org.fruit.alayer.devices.MouseButtons;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.ActionFailedException;
import org.fruit.alayer.exceptions.NoSuchTagException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.exceptions.WidgetNotFoundException;
import org.fruit.alayer.visualizers.ShapeVisualizer;
import org.fruit.alayer.webdriver.WdProtocolUtil;
import org.fruit.alayer.windows.WinApiException;

import es.upv.staq.testar.managers.DataManager;
import es.upv.staq.testar.serialisation.LogSerialiser;
import es.upv.staq.testar.serialisation.ScreenshotSerialiser;
import es.upv.staq.testar.serialisation.TestSerialiser;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.openqa.selenium.SessionNotCreatedException;
import org.testar.OutputStructure;

public class DefaultProtocol extends RuntimeControlsProtocol {

	public static boolean faultySequence;
	private State stateForClickFilterLayerProtocol;

	protected Reporting htmlReport;
	public State getStateForClickFilterLayerProtocol() {
		return stateForClickFilterLayerProtocol;
	}

	public void setStateForClickFilterLayerProtocol(State stateForClickFilterLayerProtocol) {
		this.stateForClickFilterLayerProtocol = stateForClickFilterLayerProtocol;
	}

	private String generatedSequence;

	public String getGeneratedSequenceName() {
		return generatedSequence;
	}

	private File currentSeq;

	protected Mouse mouse = AWTMouse.build();
	protected State lastState = null;
	protected int nonReactingActionNumber;

	protected ProcessListener processListener = new ProcessListener();
	private boolean enabledProcessListener = false;
	public static Verdict processVerdict = Verdict.OK;
	
	private Verdict replayVerdict;

	public Verdict getReplayVerdict() {
		return replayVerdict;
	}

	public void setReplayVerdict(Verdict replayVerdict) {
		this.replayVerdict = replayVerdict;
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

	protected int firstSequenceActionNumber;
	protected int lastSequenceActionNumber;
	double startTime;

	protected final double timeElapsed() {
		return Util.time() - startTime;
	}

	protected List<ProcessInfo> contextRunningProcesses = null;
	protected static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	protected static final Logger INDEXLOG = LogManager.getLogger();
	protected double passSeverity = Verdict.SEVERITY_OK;

	public static Action lastExecutedAction = null;

	protected long lastStamp = -1;
	protected EventHandler eventHandler;
	protected Canvas cv;
	protected Pattern clickFilterPattern = null;
	protected Map<String, Matcher> clickFilterMatchers = new WeakHashMap<String, Matcher>();
	protected Pattern suspiciousTitlesPattern = null;
	protected Map<String, Matcher> suspiciousTitlesMatchers = new WeakHashMap<String, Matcher>();
	private StateBuilder builder;
	protected String forceKillProcess = null;
	protected boolean forceToForeground = false;
	protected int testFailTimes = 0;
	protected boolean nonSuitableAction = false;
	
	protected int escAttempts = 0;
	protected static final int MAX_ESC_ATTEMPTS = 99;

	protected boolean exceptionThrown = false;

	protected StateModelManager stateModelManager;
	private String startOfSutDateString; //value set when SUT started, used for calculating the duration of test

	protected final static Pen RedPen = Pen.newPen().setColor(Color.Red).
			setFillPattern(FillPattern.None).setStrokePattern(StrokePattern.Solid).build(),
			BluePen = Pen.newPen().setColor(Color.Blue).
			setFillPattern(FillPattern.None).setStrokePattern(StrokePattern.Solid).build();


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

		SUT system = null;

		//initialize TESTAR with the given settings:
		initialize(settings);

		try {

			if (mode() == Modes.View) {
				if(isHtmlFile() || isLogFile()) {
					try {
						File file = new File(settings.get(ConfigTags.PathToReplaySequence)).getCanonicalFile();
						Desktop.getDesktop().browse(file.toURI());
					}catch (Exception e) {
						popupMessage("Exception: Check the path of the file, something is wrong");
						System.out.println("Exception: Check the path of the file, something is wrong");
					}
				} else if (!findHTMLreport().contains("error")) {
					try {
						File htmlFile = new File(findHTMLreport());
						Desktop.getDesktop().browse(htmlFile.toURI());
					}catch (Exception e) {
						popupMessage("Exception: Select a log or html file to visualize the TESTAR resutls");
						System.out.println("Exception: Select a log or html file to visualize the TESTAR resutls");
					}
				}
				/*else if(isValidFile())
					new SequenceViewer(settings);*/
				else {
					popupMessage("Please select a file.html (output/HTMLreports) to use the View mode");
					System.out.println("Exception: Please select a file.html (output/HTMLreports) to use the View mode");
				}
			} else if (mode() == Modes.Replay && isValidFile()) {
				runReplayLoop();
			} else if (mode() == Modes.Spy) {
				runSpyLoop();
			} else if(mode() == Modes.Record) {
				runRecordLoop(system);
			} else if (mode() == Modes.Generate) {
				runGenerateOuterLoop(system);
			}

		}catch(WinApiException we) {

			String msg = "Exception: Check if current SUTs path: "+settings.get(ConfigTags.SUTConnectorValue)
			+" is a correct definition";

			popupMessage(msg);

			System.out.println(msg);

			this.mode = Modes.Quit;
			
		}catch(SessionNotCreatedException e) {
			
    		if(e.getMessage().contains("Chrome version")) {
    			
    			String msg = "*** Unsupported versions exception: Chrome browser and Selenium WebDriver versions *** \n"
    					+ "Please verify your Chrome browser version: chrome://settings/help \n"
    					+ "And download the appropiate ChromeDriver version: https://chromedriver.chromium.org/downloads \n"
    					+ "\n"
    					+ "Surely exists a residual process \"chromedriver.exe\" running. \n"
    					+ "You can use Task Manager to finish it.";
    			
    			popupMessage(msg);
    			
    			System.out.println(msg);
    			System.out.println(e.getMessage());
    			
    		}else {
    			System.out.println("********** ERROR starting Selenium WebDriver ********");
    			System.out.println(e.getMessage());
    		}
    		
		}catch (IllegalStateException e) {
			if (e.getMessage().contains("driver executable does not exist")) {
				
				String msg = "Exception: Check if chromedriver.exe path: \n"
				+settings.get(ConfigTags.SUTConnectorValue)
				+"\n exists or if is a correct definition";

				popupMessage(msg);

				System.out.println(msg);
			
			}else {
				e.printStackTrace();
			}
		
		}catch(SystemStartException SystemStartException) {
			SystemStartException.printStackTrace();
			this.mode = Modes.Quit;
		} catch (Exception e) {
			e.printStackTrace();
			this.mode = Modes.Quit;
		}

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

		//EventHandler is implemented in RuntimeControlsProtocol (super class):
		eventHandler = initializeEventHandler();

		builder = NativeLinker.getNativeStateBuilder(
				settings.get(ConfigTags.TimeToFreeze),
				settings.get(ConfigTags.AccessBridgeEnabled),
				settings.get(ConfigTags.SUTProcesses)
				);

		if ( mode() == Modes.Generate || mode() == Modes.Record || mode() == Modes.Replay ) {
			//Create the output folders
			OutputStructure.calculateOuterLoopDateString();
			OutputStructure.sequenceInnerLoopCount = 0;
			OutputStructure.createOutputSUTname(settings);
			OutputStructure.createOutputFolders();

			// new state model manager
			stateModelManager = StateModelManagerFactory.getStateModelManager(settings);
		}

		try {
			if (!settings.get(ConfigTags.UnattendedTests)) {
				LogSerialiser.log("Registering keyboard and mouse hooks\n", LogSerialiser.LogLevel.Debug);
				java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
				logger.setLevel(Level.OFF);
				logger.setUseParentHandlers(false);

				if (GlobalScreen.isNativeHookRegistered()) {
					GlobalScreen.unregisterNativeHook();
				}
				GlobalScreen.registerNativeHook();
				GlobalScreen.addNativeKeyListener(eventHandler);
				GlobalScreen.addNativeMouseListener(eventHandler);
				GlobalScreen.addNativeMouseMotionListener(eventHandler);
				LogSerialiser.log("Successfully registered keyboard and mouse hooks!\n", LogSerialiser.LogLevel.Debug);
			}

			LogSerialiser.log("'" + mode() + "' mode active.\n", LogSerialiser.LogLevel.Info);


		} catch (NativeHookException e) {
			LogSerialiser.log("Unable to install keyboard and mouse hooks!\n", LogSerialiser.LogLevel.Critical);
			throw new RuntimeException("Unable to install keyboard and mouse hooks!", e);
		}
	}

	/**
	 * Check if the selected file to Replay or View contains a valid fragment object
	 */

	private boolean isValidFile(){
		try {

			File seqFile = new File(settings.get(ConfigTags.PathToReplaySequence));

			FileInputStream fis = new FileInputStream(seqFile);
			BufferedInputStream bis = new BufferedInputStream(fis);
			GZIPInputStream gis = new GZIPInputStream(bis);
			ObjectInputStream ois = new ObjectInputStream(gis);

			ois.readObject();
			ois.close();

		} catch (ClassNotFoundException | IOException e) {
			popupMessage("ERROR: File is not a readable, please select a correct testar sequence file");

			System.out.println("ERROR: File is not a readable, please select a correct file (output/sequences)");

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

			path = path.replace(replace, File.separator + "HTMLreports" + File.separator);
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
			JOptionPane.showMessageDialog(frame, message);
		}
	}

	/**
	 * This method is called from runGenerate() to initialize TESTAR for Generate-mode
	 */
	private void initGenerateMode() {
		//TODO check why LogSerializer is closed and started again in the beginning of Generate-mode
		sequenceCount = 1;
		lastStamp = System.currentTimeMillis();
		escAttempts = 0;
	}

	/**
	 * This method creates the name for generated sequence that can be replayed
	 * and starts the LogSerialiser for outputting the test sequence
	 *
	 * @return name of the generated sequence file
	 */
	private String getAndStoreGeneratedSequence() {
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
					settings.get(LogLevel));
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
	private File getAndStoreSequenceFile() {
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
	 * This method calls the startSystem() if the system is not yet running
	 *
	 * @param system
	 * @return SUT system
	 */
	private SUT startSutIfNotRunning(SUT system) {
		//If system==null, we have started TESTAR from the Generate mode and system has not been started yet (if started in SPY-mode or Record-mode, the system is running already)
		if (system == null || !system.isRunning()) {
			system = startSystem();
			//Reset LogSerialiser
			LogSerialiser.finish();
			LogSerialiser.exit();
			startOfSutDateString = Util.dateString(DATE_FORMAT);
			LogSerialiser.log(startOfSutDateString + " Starting SUT ...\n", LogSerialiser.LogLevel.Info);
			LogSerialiser.log("SUT is running!\n", LogSerialiser.LogLevel.Debug);
			LogSerialiser.log("Building canvas...\n", LogSerialiser.LogLevel.Debug);

			//Activate process Listeners if enabled in the test.settings
			if(enabledProcessListener)
				processListener.startListeners(system, settings);
		}

		return system;
	}

	private TaggableBase fragment; // Fragment is used for saving a replayable sequence:
	private long tStart;

	/**
	 * This method is initializing TESTAR for the start of test sequence
	 *
	 * @param system
	 */
	private void startTestSequence(SUT system) {
		//for measuring the time of one sequence:
		tStart = System.currentTimeMillis();

		actionCount = 1;
		this.testFailTimes = 0;
		lastSequenceActionNumber = settings().get(ConfigTags.SequenceLength) + actionCount - 1;
		firstSequenceActionNumber = actionCount;
		passSeverity = Verdict.SEVERITY_OK;
		processVerdict = Verdict.OK;
		this.cv = buildCanvas();
	}

	/**
	 * Closing test sequence of TESTAR in normal operation
	 */
	private void endTestSequence(){
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
	private void emergencyTerminateTestSequence(SUT system, Exception e){
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

	/**
	 * Method to run TESTAR on Generate mode
	 *
	 * @param system
	 */
	protected void runGenerateOuterLoop(SUT system) {

		boolean startFromGenerate = false;
		if(system==null)
			startFromGenerate = true;

		//method for defining other init actions, like setup of external environment
		initTestSession();

		//initializing TESTAR for generate mode:
		initGenerateMode();

		/*
		 ***** OUTER LOOP - STARTING A NEW SEQUENCE
		 */
		while (mode() != Modes.Quit && moreSequences()) {
			exceptionThrown = false;

			synchronized(this){
				OutputStructure.calculateInnerLoopDateString();
				OutputStructure.sequenceInnerLoopCount++;
			}

			//empty method in defaultProtocol - allowing implementation in application specific protocols:
			preSequencePreparations();

			//reset the faulty variable because we started a new sequence
			faultySequence = false;

			//starting system if it's not running yet (TESTAR could be started in SPY-mode or Record-mode):
			system = startSutIfNotRunning(system);

			if(startFromGenerate) {
				//Generating the sequence file that can be replayed:
				generatedSequence = getAndStoreGeneratedSequence();
				currentSeq = getAndStoreSequenceFile();
			}

			//initializing TESTAR for a new sequence:
			startTestSequence(system);

			try {
				// getState() called before beginSequence:
				LogSerialiser.log("Obtaining system state before beginSequence...\n", LogSerialiser.LogLevel.Debug);
				State state = getState(system);

				// beginSequence() - a script to interact with GUI, for example login screen
				LogSerialiser.log("Starting sequence " + sequenceCount + " (output as: " + generatedSequence + ")\n\n", LogSerialiser.LogLevel.Info);
				beginSequence(system, state);
				
				//update state after begin sequence SUT modification
				state = getState(system);

				// notify the statemodelmanager
				stateModelManager.notifyTestSequencedStarted();

				/*
				 ***** starting the INNER LOOP:
				 */
				Verdict stateVerdict = runGenerateInnerLoop(system, state);

				//calling finishSequence() to allow scripting GUI interactions to close the SUT:
				finishSequence();

				// notify the state model manager of the sequence end
				stateModelManager.notifyTestSequenceStopped();

				writeAndCloseFragmentForReplayableSequence();

				if (faultySequence)
					LogSerialiser.log("Sequence contained faults!\n", LogSerialiser.LogLevel.Critical);

				Verdict finalVerdict = stateVerdict.join(processVerdict);

				//Copy sequence file into proper directory:
				classifyAndCopySequenceIntoAppropriateDirectory(finalVerdict, generatedSequence, currentSeq);

				//calling postSequenceProcessing() to allow resetting test environment after test sequence, etc
				postSequenceProcessing();

				//Ending test sequence of TESTAR:
				endTestSequence();

				LogSerialiser.log("End of test sequence - shutting down the SUT...\n", LogSerialiser.LogLevel.Info);
				stopSystem(system);
				LogSerialiser.log("... SUT has been shut down!\n", LogSerialiser.LogLevel.Debug);

				sequenceCount++;

			} catch (Exception e) {
				String message = "Thread: name=" + Thread.currentThread().getName() + ",id=" + Thread.currentThread().getId() + ", TESTAR throws exception";
				System.out.println(message);
				StringJoiner stackTrace = new StringJoiner(System.lineSeparator());
				stackTrace.add(message);
				Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).forEach(stackTrace::add);
				stateModelManager.notifyTestSequenceInterruptedBySystem(stackTrace.toString());
				exceptionThrown = true;
				e.printStackTrace();
				emergencyTerminateTestSequence(system, e);
			}
		}

		if (mode() == Modes.Quit && !exceptionThrown) {
			// the user initiated the shutdown
			stateModelManager.notifyTestSequenceInterruptedByUser();
		}


		// notify the statemodelmanager that the testing has finished
		stateModelManager.notifyTestingEnded();

		mode = Modes.Quit;
	}

	private void classifyAndCopySequenceIntoAppropriateDirectory(Verdict finalVerdict, String generatedSequence, File currentSeq){
		if (!settings().get(ConfigTags.OnlySaveFaultySequences) ||
				finalVerdict.severity() >= settings().get(ConfigTags.FaultThreshold)) {

			LogSerialiser.log("Saved generated sequence (\"" + generatedSequence + "\")\n", LogSerialiser.LogLevel.Info);

			FileHandling.copyClassifiedSequence(generatedSequence, currentSeq, finalVerdict);
		}
	}

	/**
	 * This is the inner loop of TESTAR Generate-mode:
	 * * 			GetState
	 * * 			GetVerdict
	 * * 			StopCriteria (moreActions/moreSequences/time?)
	 * * 			DeriveActions
	 * * 			SelectAction
	 * * 			ExecuteAction
	 *
	 * @param system
	 */
	private Verdict runGenerateInnerLoop(SUT system, State state) {
		/*
		 ***** INNER LOOP:
		 */
		while (mode() != Modes.Quit && moreActions(state)) {

			if (mode() == Modes.Record) {
				runRecordLoop(system);
			}

			// getState() including getVerdict() that is saved into the state:
			LogSerialiser.log("Obtained system state in inner loop of TESTAR...\n", LogSerialiser.LogLevel.Debug);
			cv.begin(); Util.clear(cv);

			//Deriving actions from the state:
			Set<Action> actions = deriveActions(system, state);
			buildStateActionsIdentifiers(state, actions);
			for(Action a : actions)
				if(a.get(Tags.AbstractIDCustom, null) == null)
				    buildEnvironmentActionIdentifiers(state, a);
			
			// notify to state model the current state
			stateModelManager.notifyNewStateReached(state, actions);

			//Showing the green dots if visualization is on:
			if(visualizationOn) visualizeActions(cv, state, actions);

			//Selecting one of the available actions:
			Action action = selectAction(state, actions);
			//Showing the red dot if visualization is on:
			if(visualizationOn) SutVisualization.visualizeSelectedAction(settings, cv, state, action);

			//before action execution, pass it to the state model manager
			stateModelManager.notifyActionExecution(action);

			//Executing the selected action:
			executeAction(system, state, action);
			lastExecutedAction = action;
			actionCount++;

			//Saving the actions and the executed action into replayable test sequence:
			saveActionIntoFragmentForReplayableSequence(action, state, actions);

			// Resetting the visualization:
			Util.clear(cv);
			cv.end();

			// fetch the new state
            state = getState(system);
		}

		// notify to state model the last state
		Set<Action> actions = deriveActions(system, state);
		buildStateActionsIdentifiers(state, actions);
		for(Action a : actions)
			if(a.get(Tags.AbstractIDCustom, null) == null)
			    buildEnvironmentActionIdentifiers(state, a);
		
		stateModelManager.notifyNewStateReached(state, actions);

		return getVerdict(state);
	}

	/**
	 * Saving the action into the fragment for replayable sequence
	 *
	 * @param action
	 */
	private void saveActionIntoFragmentForReplayableSequence(Action action, State state, Set<Action> actions) {
	    // create fragment
	    fragment = new TaggableBase();
	    fragment.set(ExecutedAction, action);
	    fragment.set(ActionSet, actions);
	    fragment.set(ActionDuration, settings().get(ConfigTags.ActionDuration));
	    fragment.set(ActionDelay, settings().get(ConfigTags.TimeToWaitAfterAction));
	    fragment.set(SystemState, state);
	    fragment.set(OracleVerdict, getVerdict(state));

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
	private void writeAndCloseFragmentForReplayableSequence() {
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
	private void saveActionInfoInLogs(State state, Action action, String actionMode) {

		//Obtain action information
		String[] actionRepresentation = Action.getActionRepresentation(state,action,"\t");

		//Output/logs folder
		LogSerialiser.log(String.format(actionMode+" [%d]: %s\n%s",
				actionCount,
				"\n @Action ConcreteID = " + action.get(Tags.ConcreteID,"ConcreteID not available") +
				" AbstractID = " + action.get(Tags.AbstractID,"AbstractID not available") +"\n"+
				" ConcreteID CUSTOM = " +  action.get(Tags.ConcreteIDCustom,"ConcreteID CUSTOM not available")+
				" AbstractID CUSTOM = " +  action.get(Tags.AbstractIDCustom,"AbstractID CUSTOM not available")+"\n"+

				" @State ConcreteID = " + state.get(Tags.ConcreteID,"ConcreteID not available") +
				" AbstractID = " + state.get(Tags.Abstract_R_ID,"Abstract_R_ID not available") +"\n"+
				" ConcreteID CUSTOM = "+ state.get(Tags.ConcreteIDCustom,"ConcreteID CUSTOM not available")+
				" AbstractID CUSTOM = "+state.get(Tags.AbstractIDCustom,"AbstractID CUSTOM not available")+"\n",
				actionRepresentation[0]) + "\n",
				LogSerialiser.LogLevel.Info);
	}

	/**
	 * Method to run TESTAR on Spy Mode.
	 */
	protected void runSpyLoop() {

		//Create or detect the SUT & build canvas representation
		SUT system = startSystem();
		this.cv = buildCanvas();

		while(mode() == Modes.Spy && system.isRunning()) {

			State state = getState(system);
			cv.begin(); Util.clear(cv);

			Set<Action> actions = deriveActions(system,state);
			buildStateActionsIdentifiers(state, actions);

			
			//in Spy-mode, always visualize the widget info under the mouse cursor:
			SutVisualization.visualizeState(visualizationOn, markParentWidget, mouse, lastPrintParentsOf, cv, state);

			//in Spy-mode, always visualize the green dots:
			visualizeActions(cv, state, actions);
			
			cv.end();

			int msRefresh = (int)(settings.get(ConfigTags.RefreshSpyCanvas, 0.5) * 1000);
			
			synchronized (this) {
				try {
					this.wait(msRefresh);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

		//If user closes the SUT while in Spy-mode, TESTAR will close (or go back to SettingsDialog):
		if(!system.isRunning()){
			this.mode = Modes.Quit;
		}

		Util.clear(cv);
		cv.end();
		
		//finishSequence() content, but SPY mode is not a sequence
		if(!NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WEBDRIVER)) {
			SystemProcessHandling.killTestLaunchedProcesses(this.contextRunningProcesses);
		}

		//Stop and close the SUT before return to the detectModeLoop
		stopSystem(system);

	}

	/**
	 * Method to run TESTAR on Record User Actions Mode.
	 * @param system
	 */
	protected void runRecordLoop(SUT system) {
		boolean startedRecordMode = false;

		//If system it's null means that we have started TESTAR from the Record User Actions Mode
		//We need to invoke the SUT & the canvas representation
		if(system == null) {

			synchronized(this){
				OutputStructure.calculateInnerLoopDateString();
				OutputStructure.sequenceInnerLoopCount++;
			}

			preSequencePreparations();
			
			//reset the faulty variable because we started a new execution
			faultySequence = false;

			system = startSystem();
			startedRecordMode = true;
			this.cv = buildCanvas();
			actionCount = 1;

			//Reset LogSerialiser
			LogSerialiser.finish();
			LogSerialiser.exit();

			//Generating the sequence file that can be replayed:
			generatedSequence = getAndStoreGeneratedSequence();
			currentSeq = getAndStoreSequenceFile();

			//Activate process Listeners if enabled in the test.settings
			if(enabledProcessListener)
				processListener.startListeners(system, settings);

			// notify the statemodelmanager
			stateModelManager.notifyTestSequencedStarted();
		}
		//else, SUT & canvas exists (startSystem() & buildCanvas() created from Generate mode)


		/**
		 * Start Record User Action Loop
		 */
		while(mode() == Modes.Record && system.isRunning()) {
			State state = getState(system);
			cv.begin(); Util.clear(cv);

			Set<Action> actions = deriveActions(system,state);
			buildStateActionsIdentifiers(state, actions);

			//notify the state model manager of the new state
			stateModelManager.notifyNewStateReached(state, actions);

			if(actions.isEmpty()){
				if (escAttempts >= MAX_ESC_ATTEMPTS){
					LogSerialiser.log("No available actions to execute! Tried ESC <" + MAX_ESC_ATTEMPTS + "> times. Stopping sequence generation!\n", LogSerialiser.LogLevel.Critical);
				}
				//----------------------------------
				// THERE MUST ALMOST BE ONE ACTION!
				//----------------------------------
				// if we did not find any actions, then we just hit escape, maybe that works ;-)
				Action escAction = new AnnotatingActionCompiler().hitKey(KBKeys.VK_ESCAPE);
				buildEnvironmentActionIdentifiers(state, escAction);
				actions.add(escAction);
				escAttempts++;
			} else
				escAttempts = 0;

			ActionStatus actionStatus = new ActionStatus();

			//Start Wait User Action Loop to obtain the Action did by the User
			waitUserActionLoop(cv, system, state, actionStatus);

			//Save the user action information into the logs
			if (actionStatus.isUserEventAction()) {

			    buildStateActionsIdentifiers(state, Collections.singleton(actionStatus.getAction()));

				//notify the state model manager of the executed action
				stateModelManager.notifyActionExecution(actionStatus.getAction());

				saveActionInfoInLogs(state, actionStatus.getAction(), "RecordedAction");
				lastExecutedAction = actionStatus.getAction();
				actionCount++;
			}

			/**
			 * When we close TESTAR with Shift+down arrow, last actions is detected like null
			 */
			if(actionStatus.getAction()!=null) {
				//System.out.println("DEBUG: User action is not null");
				saveActionIntoFragmentForReplayableSequence(actionStatus.getAction(), state, actions);
			}else {
				//System.out.println("DEBUG: User action ----- null");
			}


			Util.clear(cv);
			cv.end();
		}

		//If user change to Generate mode & we start TESTAR on Record mode, invoke Generate mode with the created SUT
		if(mode() == Modes.Generate && startedRecordMode){
			Util.clear(cv);
			cv.end();

			runGenerateOuterLoop(system);
		}

		//If user closes the SUT while in Record-mode, TESTAR will close (or go back to SettingsDialog):
		if(!system.isRunning()){
			this.mode = Modes.Quit;
		}

		if(startedRecordMode && mode() == Modes.Quit){
			// notify the statemodelmanager
			stateModelManager.notifyTestSequenceStopped();

			// notify the state model manager of the sequence end
			stateModelManager.notifyTestingEnded();

			//Closing fragment for recording replayable test sequence:
			writeAndCloseFragmentForReplayableSequence();

			//Copy sequence file into proper directory:
			classifyAndCopySequenceIntoAppropriateDirectory(Verdict.OK,generatedSequence,currentSeq);

			postSequenceProcessing();

			//If we want to Quit the current execution we stop the system
			stopSystem(system);
		}
	}

	/**
	 * Method to run TESTAR in replay mode.
	 * The sequence to replay is the one indicated in the settings parameter: PathToReplaySequence
	 * Read the replayable file, repeat saved actions and generate new sequences, oracles and logs
	 */
	protected void runReplayLoop(){
	    FileInputStream fis = null;
	    BufferedInputStream bis = null;
	    GZIPInputStream gis = null;
	    ObjectInputStream ois = null;

	    actionCount = 1;
	    boolean success = true;

	    //Reset LogSerialiser
	    LogSerialiser.finish();
	    LogSerialiser.exit();

	    synchronized(this){
	        OutputStructure.calculateInnerLoopDateString();
	        OutputStructure.sequenceInnerLoopCount++;
	    }

	    preSequencePreparations();

	    //reset the faulty variable because we started a new execution
	    faultySequence = false;

	    SUT system = startSystem();

	    try{
	        File seqFile = new File(settings.get(ConfigTags.PathToReplaySequence));

	        fis = new FileInputStream(seqFile);
	        bis = new BufferedInputStream(fis);
	        gis = new GZIPInputStream(bis);
	        ois = new ObjectInputStream(gis);

	        /**
	         * Initialize the fragment to create a new sequence and logs
	         */

	        //Generating the new sequence file that can be replayed:
	        generatedSequence = getAndStoreGeneratedSequence();
	        currentSeq = getAndStoreSequenceFile();

	        Canvas canvas = buildCanvas();
	        State state = getState(system);

	        setReplayVerdict(getVerdict(state));

	        // notify the statemodelmanager
	        stateModelManager.notifyTestSequencedStarted();

	        double rrt = settings.get(ConfigTags.ReplayRetryTime);

	        while(success && !faultySequence && mode() == Modes.Replay){

	            //Initialize local fragment and read saved action of PathToReplaySequence File
	            Taggable replayableFragment;
	            Action actionToReplay;
	            try {
	                replayableFragment = (Taggable) ois.readObject();
	                actionToReplay = replayableFragment.get(ExecutedAction); 
	            } catch(IOException ioe){
	                // Check if exception thrown because we finished replaying data
	                if(fis.available() <= 0) {
	                    success = true;
	                    break;
	                } else {
	                    success = false;
	                    String msg = "Exception " + ioe.getMessage() + " reading TESTAR replayableFragment: " + seqFile;
	                    setReplayVerdict(new Verdict(Verdict.SEVERITY_UNREPLAYABLE, msg));
	                    stateModelManager.notifyTestSequenceInterruptedBySystem(ioe.toString());
	                    break;
	                }
	            } catch(NullPointerException npe) {
	                success = false;
	                String msg = "Null exception replaying TESTAR action";
	                setReplayVerdict(new Verdict(Verdict.SEVERITY_UNREPLAYABLE, msg));
	                stateModelManager.notifyTestSequenceInterruptedBySystem(npe.toString());
	                break;
	            }

	            // Derive Actions of the current State
	            Set<Action> actions = deriveActions(system,state);
	            buildStateActionsIdentifiers(state, actions);

	            // notify to state model the current state
	            stateModelManager.notifyNewStateReached(state, actions);

	            success = false;
	            int tries = 0;
	            double start = Util.time();

	            while(!success && (Util.time() - start < rrt)){       
	                tries++;
	                canvas.begin(); Util.clear(canvas);
	                canvas.end();

	                /**
	                 * Check if we are replaying the sequence correctly,
	                 * comparing saved widgets with existing widgets in the current state
	                 */

	                //Obtain the widget Title of the repayable fragment
	                String widgetStringToFind = replayableFragment.get(Tags.Title, "");
	                //Could exist actions not associated with widgets
	                boolean actionHasWidgetAssociated = false;
	                //Check if we found the widget
	                boolean widgetTitleFound = false;

	                if (state != null){
	                    List<Finder> targets = actionToReplay.get(Tags.Targets, null);
	                    if (targets != null){
	                        actionHasWidgetAssociated = true;
	                        Widget w;
	                        for (Finder f : targets){
	                            w = f.apply(state);
	                            if (w != null){			
	                                //Can be this the widget the one that we want to find?
	                                if(widgetStringToFind.equals(w.get(Tags.Title, "")))
	                                    widgetTitleFound = true;
	                            }
	                        }
	                    }
	                }

	                //If action has an associated widget and we don't find it, we are not in the correct state
	                if(actionHasWidgetAssociated && !widgetTitleFound){
	                    success = false;
	                    String msg = "The Action " + actionToReplay.get(Tags.Desc, actionToReplay.toString())
	                    + " of the replayed sequence can not been replayed into "
	                    + " the State " + state.get(Tags.ConcreteID, state.toString());

	                    setReplayVerdict(new Verdict(Verdict.SEVERITY_UNREPLAYABLE, msg));

	                    break;
	                }

	                // In Replay-mode, we only show the red dot if visualizationOn is true:
	                if(visualizationOn) SutVisualization.visualizeSelectedAction(settings, canvas, state, actionToReplay);

	                double actionDuration = settings.get(ConfigTags.UseRecordedActionDurationAndWaitTimeDuringReplay) 
	                ? replayableFragment.get(Tags.ActionDuration, 0.0) : settings.get(ConfigTags.ActionDuration);
	                double actionDelay = settings.get(ConfigTags.UseRecordedActionDurationAndWaitTimeDuringReplay) 
	                ? replayableFragment.get(Tags.ActionDelay, 0.0) : settings.get(ConfigTags.TimeToWaitAfterAction);

	                try{
	                    if(tries < 2) {
	                        String replayMessage = String.format("Trying to replay (%d): %s... [time window = " + rrt + "]",
	                                actionCount, actionToReplay.get(Desc, actionToReplay.toString()));
	                        LogSerialiser.log(replayMessage, LogSerialiser.LogLevel.Info);
	                    } else {
	                        if(tries % 50 == 0)
	                            LogSerialiser.log(".\n", LogSerialiser.LogLevel.Info);
	                        else
	                            LogSerialiser.log(".", LogSerialiser.LogLevel.Info);
	                    }

	                    preSelectAction(state, actions);

	                    //before action execution, pass it to the state model manager
	                    stateModelManager.notifyActionExecution(actionToReplay);

	                    replayAction(system, state, actionToReplay, actionDelay, actionDuration);

	                    success = true;
	                    actionCount++;
	                    LogSerialiser.log("Success!\n", LogSerialiser.LogLevel.Info);
	                } catch(ActionFailedException afe){}

	                Util.pause(actionDelay);

	                state = getState(system);

	                //Saving the actions and the executed action into replayable test sequence:
	                saveActionIntoFragmentForReplayableSequence(actionToReplay, state, actions);

	                setReplayVerdict(getVerdict(state));
	            }
	        }

	        canvas.release();

	        // notify to state model the last state
	        Set<Action> actions = deriveActions(system, state);
	        buildStateActionsIdentifiers(state, actions);
	        for(Action a : actions)
	            if(a.get(Tags.AbstractIDCustom, null) == null)
	                buildEnvironmentActionIdentifiers(state, a);

	        stateModelManager.notifyNewStateReached(state, actions);

	    } catch(IOException ioe){
	        throw new RuntimeException("Cannot read file.", ioe);
	    } catch (ClassNotFoundException cnfe) {
	        throw new RuntimeException("Cannot read file.", cnfe);
	    } finally {
	        if (ois != null){
	            try { ois.close(); } catch (IOException e) { e.printStackTrace(); }
	        }
	        if (gis != null){
	            try { gis.close(); } catch (IOException e) { e.printStackTrace(); }
	        }
	        if (bis != null){
	            try { bis.close(); } catch (IOException e) { e.printStackTrace(); }
	        }
	        if (fis != null){
	            try { fis.close(); } catch (IOException e) { e.printStackTrace(); }
	        }
	        if (cv != null)
	            cv.release();
	        if (system != null)
	            system.stop();
	    }

	    if(faultySequence) {
	        String msg = "Replayed Sequence contains Errors: "+ getReplayVerdict().info();
	        System.out.println(msg);
	        LogSerialiser.log(msg, LogSerialiser.LogLevel.Info);

	    }else if(success){
	        String msg = "Sequence successfully replayed!\n";
	        System.out.println(msg);
	        LogSerialiser.log(msg, LogSerialiser.LogLevel.Info);

	    }else if(getReplayVerdict().severity() == Verdict.SEVERITY_UNREPLAYABLE){			
	        System.out.println(getReplayVerdict().info());
	        LogSerialiser.log(getReplayVerdict().info(), LogSerialiser.LogLevel.Critical);

	    }else{
	        String msg = "Fail replaying sequence.\n";
	        System.out.println(msg);
	        LogSerialiser.log(msg, LogSerialiser.LogLevel.Critical);
	    }

	    //calling finishSequence() to allow scripting GUI interactions to close the SUT:
	    finishSequence();

	    // notify the state model manager of the sequence end
	    stateModelManager.notifyTestSequenceStopped();

	    //Close and save the replayable fragment of the current sequence
	    writeAndCloseFragmentForReplayableSequence();

	    //Copy sequence file into proper directory:
	    classifyAndCopySequenceIntoAppropriateDirectory(getReplayVerdict(), generatedSequence, currentSeq);

	    LogSerialiser.finish();

	    postSequenceProcessing();

	    //Stop system and close the SUT
	    stopSystem(system);

	    // notify the statemodelmanager that the testing has finished
	    stateModelManager.notifyTestingEnded();

	    // Going back to TESTAR settings dialog if it was used to start replay:
	    mode = Modes.Quit;
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

	}

	protected Canvas buildCanvas() {
		return NativeLinker.getNativeCanvas(Pen.PEN_DEFAULT);
	}

	@Override
	protected void beginSequence(SUT system, State state){
		nonReactingActionNumber = 0;
	}

	@Override
	protected void finishSequence(){
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
		if (sutConnectorType.equals(Settings.SUT_CONNECTOR_WINDOW_TITLE)) {
			WindowsWindowTitleSutConnector sutConnector = new WindowsWindowTitleSutConnector(settings().get(ConfigTags.SUTConnectorValue), Math.round(settings().get(ConfigTags.StartupTime).doubleValue() * 1000.0), builder);
			return sutConnector.startOrConnectSut();
		}else if (sutConnectorType.startsWith(Settings.SUT_CONNECTOR_PROCESS_NAME)) {
			WindowsProcessNameSutConnector sutConnector = new WindowsProcessNameSutConnector(settings().get(ConfigTags.SUTConnectorValue), Math.round(settings().get(ConfigTags.StartupTime) * 1000.0));
			return sutConnector.startOrConnectSut();
		}else{
			// COMMANDLINE and WebDriver SUT CONNECTOR:
			Assert.hasTextSetting(settings().get(ConfigTags.SUTConnectorValue), "SUTConnectorValue");

			//Read the settings to know if user wants to start the process listener
			if(settings.get(ConfigTags.ProcessListenerEnabled)) {
				enabledProcessListener = processListener.enableProcessListeners(settings);
			}

			// for most windows applications and most jar files, this is where the SUT gets created!
			WindowsCommandLineSutConnector sutConnector = new WindowsCommandLineSutConnector(settings.get(ConfigTags.SUTConnectorValue),
					enabledProcessListener, settings().get(ConfigTags.StartupTime)*1000, Math.round(settings().get(ConfigTags.StartupTime).doubleValue() * 1000.0), builder, settings.get(ConfigTags.FlashFeedback));
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

		if (mode() != Modes.Spy && verdict.severity() >= settings().get(ConfigTags.FaultThreshold)){
			faultySequence = true;
			LogSerialiser.log("Detected fault: " + verdict + "\n", LogSerialiser.LogLevel.Critical);
			// this was added to kill the SUT if it is frozen:
			if(verdict.severity()==Verdict.SEVERITY_NOT_RESPONDING){
				//if the SUT is frozen, we should kill it!
				LogSerialiser.log("SUT frozen, trying to kill it!\n", LogSerialiser.LogLevel.Critical);
				SystemProcessHandling.killRunningProcesses(system, 100);
			}
		} else if (verdict.severity() != Verdict.SEVERITY_OK && verdict.severity() > passSeverity){
			passSeverity = verdict.severity();
			LogSerialiser.log("Detected warning: " + verdict + "\n", LogSerialiser.LogLevel.Critical);
		}
		
		return state;
	}

	/**
	 * Take a Screenshot of the State and associate the path into state tag
	 */
	private void setStateScreenshot(State state) {
		Shape viewPort = state.get(Tags.Shape, null);
		if(viewPort != null){
			if(NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WEBDRIVER)){
				//System.out.println("DEBUG: Using WebDriver specific state shot.");
				state.set(Tags.ScreenshotPath, WdProtocolUtil.getStateshot(state));
			}else{
				//System.out.println("DEBUG: normal state shot");
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

		// if the SUT is not running, we assume it crashed
		if(!state.get(IsRunning, false))
			return new Verdict(Verdict.SEVERITY_NOT_RUNNING, "System is offline! I assume it crashed!");

		// if the SUT does not respond within a given amount of time, we assume it crashed
		if(state.get(Tags.NotResponding, false)){
			return new Verdict(Verdict.SEVERITY_NOT_RESPONDING, "System is unresponsive! I assume something is wrong!");
		}
		//------------------------
		// ORACLES ALMOST FOR FREE
		//------------------------

		if (this.suspiciousTitlesPattern == null)
			this.suspiciousTitlesPattern = Pattern.compile(settings().get(ConfigTags.SuspiciousTitles), Pattern.UNICODE_CHARACTER_CLASS);

		// search all widgets for suspicious String Values
		Verdict suspiciousValueVerdict = Verdict.OK;
		for(Widget w : state) {
			suspiciousValueVerdict = suspiciousStringValueMatcher(w);
			if(suspiciousValueVerdict.severity() == Verdict.SEVERITY_SUSPICIOUS_TITLE) {
				return suspiciousValueVerdict;
			}
		}

		if (this.nonSuitableAction){
			this.nonSuitableAction = false;
			return new Verdict(Verdict.SEVERITY_WARNING, "Non suitable action for state");
		}

		// if everything was OK ...
		return Verdict.OK;
	}
	
	private Verdict suspiciousStringValueMatcher(Widget w) {
		Matcher m;

		for(String tagForSuspiciousOracle : settings.get(ConfigTags.TagsForSuspiciousOracle)){
			String tagValue = "";
			// First finding the Tag that matches the TagsToFilter string, then getting the value of that Tag:
			for(Tag tag : w.tags()){
				if(tag.name().equals(tagForSuspiciousOracle)){
					tagValue = w.get(tag, "");
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
				// visualize the problematic widget, by marking it with a red box
				if(w.get(Tags.Shape, null) != null)
					visualizer = new ShapeVisualizer(RedPen, w.get(Tags.Shape), "Suspicious Title", 0.5, 0.5);
				return new Verdict(Verdict.SEVERITY_SUSPICIOUS_TITLE,
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
					this.forceKillProcess = process.right();
					System.out.println("will kill unwanted process: " + process.left().longValue() + " (SYSTEM <" + system.get(Tags.PID).longValue() + ">)");
					return actions;
				}
			}
		}

		// If the system is in the background, we need to force it into the foreground!
		// We set this.forceToForeground to true and selectAction will make sure that the next action we will select
		// is putting the SUT back into the foreground.
		if(!state.get(Tags.Foreground, true) && system.get(Tags.SystemActivator, null) != null){
			this.forceToForeground = true;
			return actions;
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
	protected Action preSelectAction(State state, Set<Action> actions){
		//Assert.isTrue(actions != null && !actions.isEmpty());

		// If deriveActions indicated that there are processes that need to be killed
		// because they are in the process filters
		// Then here we will select the action to do that killing

		if (this.forceKillProcess != null){
			System.out.println("DEBUG: preActionSelection, forceKillProcess="+forceKillProcess);
			LogSerialiser.log("Forcing kill-process <" + this.forceKillProcess + "> action\n", LogSerialiser.LogLevel.Info);
			Action killProcessAction = KillProcess.byName(this.forceKillProcess, 0);
			killProcessAction.set(Tags.Desc, "Kill Process with name '" + this.forceKillProcess + "'");
			buildEnvironmentActionIdentifiers(state, killProcessAction);
			this.forceKillProcess = null;
			return killProcessAction;
		}

		// If deriveActions indicated that the SUT should be put back in the foreground
		// Then here we will select the action to do that

		else if (this.forceToForeground){
			LogSerialiser.log("Forcing SUT activation (bring to foreground) action\n", LogSerialiser.LogLevel.Info);
			Action foregroundAction = new ActivateSystem();
			foregroundAction.set(Tags.Desc, "Bring the system to the foreground.");
			buildEnvironmentActionIdentifiers(state, foregroundAction);
			this.forceToForeground = false;
			return foregroundAction;
		}

		// TESTAR didn't find any actions in the State of the SUT
		// It is set in a method actionExecuted that is not being called anywhere (yet?)
		else if (actions.isEmpty()){
			System.out.println("DEBUG: Forcing ESC action in preActionSelection : Actions derivation seems to be EMPTY !");
			LogSerialiser.log("Forcing ESC action\n", LogSerialiser.LogLevel.Info);
			Action escAction = new AnnotatingActionCompiler().hitKey(KBKeys.VK_ESCAPE);
			buildEnvironmentActionIdentifiers(state, escAction);
			return escAction;
		}

		return null;
	}

	final static double MAX_ACTION_WAIT_FRAME = 1.0; // (seconds)
	//TODO move the CPU metric to another helper class that is not default "TrashBinCode" or "SUTprofiler"
	//TODO check how well the CPU usage based waiting works
	protected boolean executeAction(SUT system, State state, Action action){

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
	 * Returns the next action that will be selected. If unwanted processes need to be killed, the action kills them. If the SUT needs
	 * to be put in the foreground, then the action is putting it in the foreground. Otherwise the action is selected according to
	 * action selection mechanism selected.
	 * @param state
	 * @param actions
	 * @return
	 */
	protected Action selectAction(State state, Set<Action> actions){
		Assert.isTrue(actions != null && !actions.isEmpty());

		Action a = preSelectAction(state, actions);
		if (a != null){
			return a;
		} else
			return RandomActionSelector.selectAction(actions);
	}

	protected String getRandomText(Widget w){
		return DataManager.getRandomData();
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
	public void mouseMoved(double x, double y) {} //for iEventListener

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

	@Override
	protected void postSequenceProcessing() {

	}

	/**
	 * method for closing the internal TESTAR test session
	 */
	private void closeTestarTestSession(){
		//cleaning the variables started in initialize()
		try {
			if (!settings.get(ConfigTags.UnattendedTests)) {
				if (GlobalScreen.isNativeHookRegistered()) {
					LogSerialiser.log("Unregistering keyboard and mouse hooks\n", LogSerialiser.LogLevel.Debug);
					GlobalScreen.removeNativeMouseMotionListener(eventHandler);
					GlobalScreen.removeNativeMouseListener(eventHandler);
					GlobalScreen.removeNativeKeyListener(eventHandler);
					GlobalScreen.unregisterNativeHook();
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void closeTestSession() {
	}

	//TODO move to ManualRecording helper class??
	//	/**
	//	 * Records user action (for example for Generate-Manual)
	//	 *
	//	 * @param state
	//	 * @return
	//	 */
	protected Action mapUserEvent(State state){
		Assert.notNull(userEvent);
		if (userEvent[0] instanceof MouseButtons){ // mouse events
			double x = ((Double)userEvent[1]).doubleValue();
			double y = ((Double)userEvent[2]).doubleValue();
			Widget w = null;
			try {
				w = Util.widgetFromPoint(state, x, y);
				x = 0.5; y = 0.5;
				if (userEvent[0] == MouseButtons.BUTTON1) // left click
					return (new AnnotatingActionCompiler()).leftClickAt(w,x,y);
				else if (userEvent[0] == MouseButtons.BUTTON3) // right click
					return (new AnnotatingActionCompiler()).rightClickAt(w,x,y);
			} catch (WidgetNotFoundException we){
				System.out.println("Mapping user event ... widget not found @(" + x + "," + y + ")");
				return null;
			}
		} else if (userEvent[0] instanceof KBKeys) // key events
			return (new AnnotatingActionCompiler()).hitKey((KBKeys)userEvent[0]);
		else if (userEvent[0] instanceof String){ // type events
			if (lastExecutedAction == null)
				return null;
			List<Finder> targets = lastExecutedAction.get(Tags.Targets,null);
			if (targets == null || targets.size() != 1)
				return null;
			try {
				Widget w = targets.get(0).apply(state);
				return (new AnnotatingActionCompiler()).clickTypeInto(w,(String)userEvent[0], true);
			} catch (WidgetNotFoundException we){
				return null;
			}
		}
		return null;
	}

	//TODO move to ManualRecording helper class??
	//	/**
	//	 * Waits for an user UI action.
	//	 * Requirement: Mode must be GenerateManual.
	//	 */
	protected void waitUserActionLoop(Canvas cv, SUT system, State state, ActionStatus actionStatus){
		while (mode() == Modes.Record && !actionStatus.isUserEventAction()){
			if (userEvent != null){
				actionStatus.setAction(mapUserEvent(state));
				actionStatus.setUserEventAction((actionStatus.getAction() != null));
				userEvent = null;
			}
			synchronized(this){
				try {
					this.wait(100);
				} catch (InterruptedException e) {}
			}
			state = getState(system);
			cv.begin(); Util.clear(cv);

			//In Record-mode, we activate the visualization with Shift+ArrowUP:
			if(visualizationOn) SutVisualization.visualizeState(false, markParentWidget, mouse, lastPrintParentsOf, cv,state);

			Set<Action> actions = deriveActions(system,state);
			buildStateActionsIdentifiers(state, actions);

			//In Record-mode, we activate the visualization with Shift+ArrowUP:
			if(visualizationOn) visualizeActions(cv, state, actions);

			cv.end();
		}
	}

	/**
	 * Use CodingManager to create the Widget and State identifiers: 
	 * ConcreteID, ConcreteIDCustom, AbstractID, AbstractIDCustom, 
	 * Abstract_R_ID, Abstract_R_T_ID, Abstract_R_T_P_ID 
	 * 
	 * @param state
	 */
	protected void buildStateIdentifiers(State state) {
	    CodingManager.buildIDs(state);
	}

	/**
	 * Use CodingManager to create the Actions identifiers: 
	 * ConcreteID, ConcreteIDCustom, AbstractID, AbstractIDCustom 
	 * 
	 * @param state
	 * @param actions
	 */
	protected void buildStateActionsIdentifiers(State state, Set<Action> actions) {
	    CodingManager.buildIDs(state, actions);
	}

	/**
	 * Use CodingManager to create the specific environment Action identifiers: 
	 * ConcreteID, ConcreteIDCustom, AbstractID, AbstractIDCustom 
	 * 
	 * @param state
	 * @param action
	 */
	protected void buildEnvironmentActionIdentifiers(State state, Action action) {
	    CodingManager.buildEnvironmentActionIDs(state, action);
	}

}
