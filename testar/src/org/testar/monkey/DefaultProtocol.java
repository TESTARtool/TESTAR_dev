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

package org.testar.monkey;

import static org.testar.monkey.alayer.Tags.ActionDelay;
import static org.testar.monkey.alayer.Tags.ActionDuration;
import static org.testar.monkey.alayer.Tags.ActionSet;
import static org.testar.monkey.alayer.Tags.ExecutedAction;
import static org.testar.monkey.alayer.Tags.IsRunning;
import static org.testar.monkey.alayer.Tags.OracleVerdict;
import static org.testar.monkey.alayer.Tags.SystemState;

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
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import nl.ou.testar.*;
import org.fruit.monkey.ProtocolDelegate;
import org.fruit.monkey.ReplayStateModelUtil;
import org.testar.*;
import org.testar.monkey.alayer.Canvas;
import org.testar.monkey.alayer.Color;
import org.testar.monkey.alayer.Shape;
import org.testar.monkey.alayer.actions.*;
import org.testar.reporting.Reporting;
import org.testar.statemodel.StateModelManager;
import org.testar.statemodel.StateModelManagerFactory;
import nl.ou.testar.StateModel.Exception.StateModelException;
import nl.ou.testar.jfx.dashboard.DashboardDelegate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.devices.AWTMouse;
import org.testar.monkey.alayer.devices.KBKeys;
import org.testar.monkey.alayer.devices.Mouse;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.ActionFailedException;
import org.testar.monkey.alayer.exceptions.NoSuchTagException;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.alayer.exceptions.SystemStartException;
import org.testar.monkey.alayer.visualizers.ShapeVisualizer;
import org.testar.monkey.alayer.webdriver.WdProtocolUtil;
import org.testar.monkey.alayer.windows.WinApiException;
import org.testar.plugin.NativeLinker;
import org.testar.plugin.OperatingSystems;
import org.testar.managers.DataManager;
import org.testar.serialisation.LogSerialiser;
import org.testar.serialisation.ScreenshotSerialiser;
import org.testar.serialisation.TestSerialiser;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.openqa.selenium.SessionNotCreatedException;

public class DefaultProtocol extends RuntimeControlsProtocol implements ActionResolver {

    public static boolean faultySequence;
    private State stateForClickFilterLayerProtocol;

    protected Reporting htmlReport;

    // TODO: progress monitor shouldn't depend on JavaFX
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

    protected Mouse mouse = AWTMouse.build();

    protected ProcessListener processListener = new ProcessListener();
    boolean enabledProcessListener = false;
    public static Verdict processVerdict = Verdict.OK;
    private Verdict replayVerdict = Verdict.OK;

    public Verdict getReplayVerdict() {
        return replayVerdict;
    }

    public void setReplayVerdict(Verdict replayVerdict) {
        this.replayVerdict = replayVerdict;
    }

    protected String lastPrintParentsOf = "null-id";
    protected int actionCount;

    // TODO: re-assign action resolver if needed
    protected ActionResolver actionResolver = this;

    private TaggableBase fragment = new TaggableBase();

    // Should not provide any action resolver next to itself
    @Override
    public ActionResolver nextResolver() {
        return null;
    }

    @Override
    public void setNextResolver(ActionResolver nextResolver) {
    }

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
    protected static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    protected static final Logger INDEXLOG = LogManager.getLogger();
    protected double passSeverity = Verdict.SEVERITY_OK;

    public static Action lastExecutedAction = null;

    protected EventHandler eventHandler;
    protected Canvas cv;
    protected Pattern clickFilterPattern = null;
    protected Map<String, Matcher> clickFilterMatchers = new WeakHashMap<String, Matcher>();
    protected Pattern suspiciousTitlesPattern = null;
    protected Map<String, Matcher> suspiciousTitlesMatchers = new WeakHashMap<String, Matcher>();
    private StateBuilder builder;

    protected int escAttempts = 0;

    protected StateModelManager stateModelManager;
    private String startOfSutDateString; // value set when SUT started, used for calculating the duration of test

    // Creating a logger with log4j library:
    private static Logger logger = LogManager.getLogger();

    protected ProtocolDelegate delegate;

    public ProtocolDelegate getDelegate() {
        return delegate;
    }

    /**
     * Puts a custom action resolver on the top of the chain
     *
     * @param customActionResolver
     */
    public void assignActionResolver(ActionResolver customActionResolver) {
        customActionResolver.setNextResolver(actionResolver);
        actionResolver = customActionResolver;
    }

    /**
     * Remove an action resolver from the top of the chain if not last
     *
     * @return true if resolver removed
     */
    public boolean resignActionResolver() {
        ActionResolver nextResolver = actionResolver.nextResolver();
        if (nextResolver == null) {
            return false;
        }
        actionResolver = nextResolver;
        return true;
    }

    public void setDelegate(ProtocolDelegate delegate) {
        this.delegate = delegate;
    }

    /**
     * This is the abstract flow of TESTAR (generate mode):
     *
     * - Initialize TESTAR settings
     * - InitTestSession (before starting the first sequence)
     * - OUTER LOOP:
     * PreSequencePreparations (before each sequence, for example starting
     * WebDriver, JaCoCo or another process for sequence)
     * StartSUT
     * BeginSequence (starting "script" on the GUI of the SUT, for example login)
     * INNER LOOP
     * GetState
     * GetVerdict
     * StopCriteria (moreActions/moreSequences/time?)
     * DeriveActions
     * SelectAction
     * ExecuteAction
     * FinishSequence (closing "script" on the GUI of the SUT, for example logout)
     * StopSUT
     * PostSequenceProcessing (after each sequence)
     * - CloseTestSession (after finishing the last sequence)
     *
     */

    /**
     * Starting the TESTAR loops
     *
     * @param settings
     */
    public final void run(final Settings settings) {

        // if (progressMonitor != null) {
        // progressMonitor.setTitle("Getting ready");
        // progressMonitor.beginTask("Preparing for test", 0);
        // }

        // Associate start settings of the first TESTAR dialog
        this.settings = settings;

        SUT system = null;

        // initialize TESTAR with the given settings:
        logger.trace("TESTAR initializing with the given protocol settings");
        initialize(settings);
        // if (progressMonitor != null) {
        // progressMonitor.setTitle("Testing");
        // }
        if (delegate != null)
            try {
                if (mode() == Modes.View) {

                    // if (progressMonitor != null) {
                    // progressMonitor.beginTask("View in progress", 0);
                    // }

                    if (isHtmlFile() || isLogFile()) {
                        try {
                            File file = new File(settings.get(ConfigTags.PathToReplaySequence)).getCanonicalFile();
                            delegate.openURI(file.toURI());
                        } catch (IOException e) {
                            delegate.popupMessage("Exception: Check the path of the file, something is wrong");
                            System.out.println("Exception: Check the path of the file, something is wrong");
                        } catch (NoSuchTagException e) {
                            delegate.popupMessage("Exception: ConfigTags.PathToReplaySequence is missing");
                            System.out.println("Exception: ConfigTags.PathToReplaySequence is missing");
                        }
                    } else if (!findHTMLreport().contains("error")) {
                        File htmlFile = new File(findHTMLreport());
                        delegate.openURI(htmlFile.toURI());
                    } else {
                        delegate.popupMessage("Please select a file.html (output/HTMLreports) to use in the View mode");
                        System.out.println(
                                "Exception: Please select a file.html (output/HTMLreports) to use in the View mode");
                    }
                } else {
                    runTestLoop(mode(), system);
                }

            } catch (WinApiException we) {

                String msg = "Exception: Check if current SUTs path: " + settings.get(ConfigTags.SUTConnectorValue)
                        + " is a correct definition";

                delegate.popupMessage(msg);

                System.out.println(msg);
                we.printStackTrace();

                this.mode = Modes.Quit;
            } catch (SessionNotCreatedException e) {
                if (e.getMessage() != null && e.getMessage().contains("Chrome version")) {

                    String msg = "*** Unsupported versions exception: Chrome browser and Selenium WebDriver versions *** \n"
                            + "Please verify your Chrome browser version: chrome://settings/help \n"
                            + "And download the appropriate ChromeDriver version: https://chromedriver.chromium.org/downloads \n"
                            + "\n"
                            + "Surely exists a residual process \"chromedriver.exe\" running. \n"
                            + "You can use Task Manager to finish it.";

                    delegate.popupMessage(msg);

                    System.out.println(msg);
                    System.out.println(e.getMessage());

                } else {
                    String msg = "********** ERROR starting Selenium WebDriver ********";
                    delegate.popupMessage(msg);

                    System.out.println(msg);
                    System.out.println(e.getMessage());
                }

            } catch (IllegalStateException e) {
                if (e.getMessage() != null && e.getMessage().contains("driver executable does not exist")) {

                    String msg = "Exception: Check if chromedriver.exe path: \n"
                            + settings.get(ConfigTags.SUTConnectorValue)
                            + "\n exists and is correctly defined";

                    delegate.popupMessage(msg);

                    System.out.println(msg);
                } else {
                    e.printStackTrace();
                }

            } catch (SystemStartException SystemStartException) {
                SystemStartException.printStackTrace();
                this.mode = Modes.Quit;
                delegate.popupMessage(SystemStartException.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                this.mode = Modes.Quit;
                delegate.popupMessage(e.getMessage());
            }
        // can there be other kind of exceptions?

        // Hide progress monitor
        // if (progressMonitor != null) {
        // progressMonitor.stop();
        // }

        System.out.println("Protocol finished");
        // allowing close-up in the end of test session:
        closeTestSession();
        // Closing TESTAR EventHandler
        closeTestarTestSession();
        System.out.println("All closed");
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

        // EventHandler is implemented in RuntimeControlsProtocol (super class):
        eventHandler = initializeEventHandler();

        builder = NativeLinker.getNativeStateBuilder(
                settings.get(ConfigTags.TimeToFreeze),
                settings.get(ConfigTags.AccessBridgeEnabled),
                settings.get(ConfigTags.SUTProcesses));

        if (mode() == Modes.Generate || mode() == Modes.Record || mode() == Modes.Replay
                || mode() == Modes.ReplayModel) {
            // Create the output folders
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
                java.util.logging.Logger logger = java.util.logging.Logger
                        .getLogger(GlobalScreen.class.getPackage().getName());
                logger.setLevel(Level.OFF);
                logger.setUseParentHandlers(false);
                // GlobalScreen.setEventDispatcher(new SwingDispatchService());

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

    protected void runTestLoop(Modes mode, SUT system) throws StateModelException {
        if (mode == Modes.Replay && isValidFile()) {
            new ReplayMode().runReplayLoop(this);
        } else if (mode == Modes.ReplayModel) {
            new ReplayStateModelMode().runReplayStateModelOuterLoop(this);
        } else if (mode == Modes.Spy) {
            new SpyMode().runSpyLoop(this);
        } else if (mode == Modes.Record) {
            new RecordMode().runRecordLoop(this, system);
        } else if (mode == Modes.Generate) {
            new GenerateMode().runGenerateOuterLoop(this, system);
        }
    }

    /**
     * Check if the selected file to Replay or View contains a valid fragment object
     */

    private boolean isValidFile() {
        try {

            File seqFile = new File(settings.get(ConfigTags.PathToReplaySequence));

            FileInputStream fis = new FileInputStream(seqFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            GZIPInputStream gis = new GZIPInputStream(bis);
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
        if (settings.get(ConfigTags.PathToReplaySequence).contains(".html"))
            return true;

        return false;
    }

    /**
     * Check if the selected file to View is a log file
     */
    private boolean isLogFile() {
        if (settings.get(ConfigTags.PathToReplaySequence).contains(".log"))
            return true;

        return false;
    }

    /**
     * If the user selects a .testar object file to use the View mode, try to find
     * the corresponding html file
     */
    private String findHTMLreport() {
        String foundedHTML = "error";
        String path = settings.get(ConfigTags.PathToReplaySequence);
        if (path.contains(".testar")) {
            path = path.replace(".testar", ".html");

            int startIndex = path.indexOf(File.separator + "sequences");
            int endIndex = path.indexOf(File.separator, startIndex + 2);

            String replace = path.substring(startIndex, endIndex + 1);

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
        System.out.println("An exception occurred: " + message);
        if (settings.get(ConfigTags.ShowVisualSettingsDialogOnStartup)) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(message);
                alert.showAndWait();
            });
        }
    }

    /**
     * This method is called from runGenerate() to initialize TESTAR for
     * Generate-mode
     */
    void initGenerateMode() {
        // TODO check why LogSerializer is closed and started again in the beginning of
        // Generate-mode
        sequenceCount = 1;
        escAttempts = 0;
    }

    /**
     * This method creates the name for generated sequence that can be replayed
     * and starts the LogSerialiser for outputting the test sequence
     *
     * @return name of the generated sequence file
     */
    protected String getAndStoreGeneratedSequence() {
        // TODO refactor replayable sequences with something better (model perhaps?)

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
        } catch (NoSuchTagException | FileNotFoundException e3) {
            popupMessage("Failed to store generated sequence");
            e3.printStackTrace();
        }

        ScreenshotSerialiser.start(OutputStructure.screenshotsOutputDir, screenshotsDirectory);

        return generatedSequenceName;
    }

    /**
     * This method creates a temporary file for saving the test sequence (that can
     * be replayed)
     * The name of the temporary file is changed in the end of the test sequence
     * (not in this function)
     *
     * @return temporary file for saving the test sequence
     */
    protected File getAndStoreSequenceFile() {
        LogSerialiser.log("Creating new sequence file...\n", LogSerialiser.LogLevel.Debug);

        String sequenceObject = settings.get(ConfigTags.TempDir)
                + File.separator + OutputStructure.startInnerLoopDateString + "_"
                + OutputStructure.executedSUTname
                + "_sequence_" + OutputStructure.sequenceInnerLoopCount + ".testar";

        final File currentSeqObject = new File(sequenceObject);

        try {
            TestSerialiser.start(
                    new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(currentSeqObject, true))));
            LogSerialiser.log("Created new sequence file!\n", LogSerialiser.LogLevel.Debug);
        } catch (IOException e) {
            popupMessage("I/O exception creating new sequence file");
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
    SUT startSutIfNotRunning(SUT system) {
        // If system==null, we have started TESTAR from the Generate mode and system has
        // not been started yet (if started in SPY-mode or Record-mode, the system is
        // running already)
        if (system == null || !system.isRunning()) {
            system = startSystem();
            // Reset LogSerialiser
            LogSerialiser.finish();
            LogSerialiser.exit();
            startOfSutDateString = Util.dateString(DATE_FORMAT);
            LogSerialiser.log(startOfSutDateString + " Starting SUT ...\n", LogSerialiser.LogLevel.Info);
            LogSerialiser.log("SUT is running!\n", LogSerialiser.LogLevel.Debug);
            LogSerialiser.log("Building canvas...\n", LogSerialiser.LogLevel.Debug);

            // Activate process Listeners if enabled in the test.settings
            if (enabledProcessListener)
                processListener.startListeners(system, settings);
        }

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
    void endTestSequence() {
        LogSerialiser.log("Releasing canvas...\n", LogSerialiser.LogLevel.Debug);
        cv.release();
        ScreenshotSerialiser.exit();
        TestSerialiser.exit();
        // String stopDateString = Util.dateString(DATE_FORMAT);
        // String durationDateString = Util.diffDateString(DATE_FORMAT,
        // startOfSutDateString, stopDateString);
        // LogSerialiser.log("TESTAR stopped execution at " + stopDateString + "\n",
        // LogSerialiser.LogLevel.Critical);
        // LogSerialiser.log("Test duration was " + durationDateString + "\n",
        // LogSerialiser.LogLevel.Critical);
        LogSerialiser.flush();
        LogSerialiser.finish();
        LogSerialiser.exit();

        // Delete the temporally testar file
        try {
            Util.delete(currentSeq);
        } catch (IOException e2) {
            final String errorMessage = "I/O exception deleting <" + currentSeq + ">";
            popupMessage(errorMessage);
            LogSerialiser.log(errorMessage + "\n", LogSerialiser.LogLevel.Critical);
        }
    }

    /**
     * Shutting down TESTAR in case of exception during Generate-mode outer loop
     * (test sequence generation)
     *
     * @param system
     * @param e
     */
    void emergencyTerminateTestSequence(SUT system, Exception e) {
        SystemProcessHandling.killTestLaunchedProcesses(this.contextRunningProcesses);
        ScreenshotSerialiser.finish();
        TestSerialiser.finish();
        ScreenshotSerialiser.exit();
        LogSerialiser.log("Exception <" + e.getMessage() + "> has been caught\n", LogSerialiser.LogLevel.Critical); // screenshots
                                                                                                                    // must
                                                                                                                    // be
                                                                                                                    // serialised
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

        // Delete the temporally testar file
        try {
            Util.delete(currentSeq);
        } catch (IOException e2) {
            final String popupMessage = "I/O exception deleting <" + currentSeq + ">";
            LogSerialiser.log(popupMessage + "a\n", LogSerialiser.LogLevel.Critical);
        }
    }

    // TODO: this method might be applicable on every test iteration start
    public void onGenerateStarted() {
    }

    public void onTestEndEvent() {
        LogSerialiser.log("The test has ended...", LogSerialiser.LogLevel.Debug);
    }

    protected void classifyAndCopySequenceIntoAppropriateDirectory(Verdict finalVerdict, String generatedSequence,
            File currentSeq) {
        if (!settings.get(ConfigTags.OnlySaveFaultySequences, false) ||
                finalVerdict.severity() >= settings().get(ConfigTags.FaultThreshold)) {
            LogSerialiser.log("Saved generated sequence (\"" + generatedSequence + "\")\n",
                    LogSerialiser.LogLevel.Info);
            FileHandling.copyClassifiedSequence(generatedSequence, currentSeq, finalVerdict);
        } else {
            LogSerialiser.log("Skipped generated sequence OK (\"" + generatedSequence + "\")\n",
                    LogSerialiser.LogLevel.Info);
        }
    }

    /**
     * This method initializes the fragment for replayable sequence
     *
     * @param state
     */
    protected void initFragmentForReplayableSequence(State state) {
        // Fragment is used for saving a replayable sequence:
        fragment = new TaggableBase();
        fragment.set(SystemState, state);
        fragment.set(OracleVerdict, getVerdict(state));
    }

    /**
     * Saving the action into the fragment for replayable sequence
     *
     * @param action
     */
    protected void saveActionIntoFragmentForReplayableSequence(Action action, State state, Set<Action> actions) {
        // create fragment
        // TaggableBase fragment = new TaggableBase();
        fragment.set(ExecutedAction, action);
        fragment.set(ActionSet, actions);
        fragment.set(ActionDuration, settings().get(ConfigTags.ActionDuration));
        fragment.set(ActionDelay, settings().get(ConfigTags.TimeToWaitAfterAction));
        fragment.set(SystemState, state);
        fragment.set(OracleVerdict, getVerdict(state).join(processVerdict));

        // Find the target widget of the current action, and save the title into the
        // fragment
        if (state != null && action.get(Tags.OriginWidget, null) != null) {
            fragment.set(Tags.Title, action.get(Tags.OriginWidget).get(Tags.Title, ""));
        }

        LogSerialiser.log("Writing fragment to sequence file...\n", LogSerialiser.LogLevel.Debug);
        TestSerialiser.write(fragment);
        // resetting the fragment:
        fragment = new TaggableBase();
    }

    /**
     * Saving the action into the fragment for replayable sequence
     *
     * @param state
     */
    protected void saveStateIntoFragmentForReplayableSequence(State state) {
        fragment.set(OracleVerdict, getVerdict(state).join(processVerdict));
        fragment.set(ActionDuration, settings().get(ConfigTags.ActionDuration));
        fragment.set(ActionDelay, settings().get(ConfigTags.TimeToWaitAfterAction));
        fragment.set(SystemState, state);
        LogSerialiser.log("Writing fragment to sequence file...\n", LogSerialiser.LogLevel.Debug);
        TestSerialiser.write(fragment);
        // resetting the fragment:
        fragment = new TaggableBase();
    }

    /**
     * Wait until fragments have been written then close the test serialiser
     */

    protected void writeAndCloseFragmentForReplayableSequence() {
        // closing ScreenshotSerialiser:
        ScreenshotSerialiser.finish();
        LogSerialiser.log("Writing fragment to sequence file...\n", LogSerialiser.LogLevel.Debug);
        TestSerialiser.write(fragment);

        // Wait since TestSerialiser write all fragments on sequence File
        while (!TestSerialiser.isSavingQueueEmpty() && !ScreenshotSerialiser.isSavingQueueEmpty()) {
            synchronized (this) {
                try {
                    this.wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        TestSerialiser.finish();
        LogSerialiser.log("Wrote fragment to sequence file!\n", LogSerialiser.LogLevel.Debug);
        LogSerialiser.log("Sequence " + sequenceCount + " finished.\n", LogSerialiser.LogLevel.Info);

        ScreenshotSerialiser.exit();
        TestSerialiser.exit();
    }

    /**
     * Saving the action information into the logs
     *
     * @param state
     * @param action
     * @param actionMode
     */
    protected void saveActionInfoInLogs(State state, Action action, String actionMode) {

        // Obtain action information
        String[] actionRepresentation = Action.getActionRepresentation(state, action, "\t");

        // Output/logs folder
        LogSerialiser.log(String.format(actionMode + " [%d]: %s\n%s",
                actionCount,
                "\n @Action ConcreteID = " + action.get(Tags.ConcreteID, "ConcreteID not available") +
                        " AbstractID = " + action.get(Tags.AbstractID, "AbstractID not available") + "\n" +
                        " ConcreteID CUSTOM = " + action.get(Tags.ConcreteIDCustom, "ConcreteID CUSTOM not available") +
                        " AbstractID CUSTOM = " + action.get(Tags.AbstractIDCustom, "AbstractID CUSTOM not available")
                        + "\n" +

                        " @State ConcreteID = " + state.get(Tags.ConcreteID, "ConcreteID not available") +
                        " AbstractID = " + state.get(Tags.Abstract_R_ID, "Abstract_R_ID not available") + "\n" +
                        " ConcreteID CUSTOM = " + state.get(Tags.ConcreteIDCustom, "ConcreteID CUSTOM not available") +
                        " AbstractID CUSTOM = " + state.get(Tags.AbstractIDCustom, "AbstractID CUSTOM not available")
                        + "\n",
                actionRepresentation[0]) + "\n",
                LogSerialiser.LogLevel.Info);
    }

    /**
     * This method is called before the first test sequence, allowing for example
     * setting up the test environment
     */
    @Override
    protected void initTestSession() {

    }

    /**
     * This methods is called before each test sequence, allowing for example using
     * external profiling software on the SUT
     */
    @Override
    protected void preSequencePreparations() {

    }

    protected Canvas buildCanvas() {
        return NativeLinker.getNativeCanvas(Pen.PEN_DEFAULT);
    }

    @Override
    protected void beginSequence(SUT system, State state) {

    }

    @Override
    protected void finishSequence() {
        SystemProcessHandling.killTestLaunchedProcesses(this.contextRunningProcesses);
    }

    protected SUT startSystem() throws SystemStartException {
        this.contextRunningProcesses = SystemProcessHandling.getRunningProcesses("START");
        try {
            for (String d : settings().get(ConfigTags.Delete))
                Util.delete(d);
            for (Pair<String, String> fromTo : settings().get(ConfigTags.CopyFromTo))
                Util.copyToDirectory(fromTo.left(), fromTo.right());
        } catch (IOException ioe) {
            throw new SystemStartException(ioe);
        }
        String sutConnectorType = settings().get(ConfigTags.SUTConnector);
        if (sutConnectorType.equals(Settings.SUT_CONNECTOR_WINDOW_TITLE)) {
            WindowsWindowTitleSutConnector sutConnector = new WindowsWindowTitleSutConnector(
                    settings().get(ConfigTags.SUTConnectorValue),
                    Math.round(settings().get(ConfigTags.StartupTime).doubleValue() * 1000.0),
                    builder,
                    settings().get(ConfigTags.ForceForeground));
            return sutConnector.startOrConnectSut();
        } else if (sutConnectorType.startsWith(Settings.SUT_CONNECTOR_PROCESS_NAME)) {
            WindowsProcessNameSutConnector sutConnector = new WindowsProcessNameSutConnector(
                    settings().get(ConfigTags.SUTConnectorValue),
                    Math.round(settings().get(ConfigTags.StartupTime).doubleValue() * 1000.0));
            return sutConnector.startOrConnectSut();
        } else {
            // COMMANDLINE and WebDriver SUT CONNECTOR:
            Assert.hasTextSetting(settings().get(ConfigTags.SUTConnectorValue), "SUTConnectorValue");

            // Read the settings to know if user wants to start the process listener
            if (settings.get(ConfigTags.ProcessListenerEnabled)) {
                enabledProcessListener = processListener.enableProcessListeners(settings);
            }

            // for most windows applications and most jar files, this is where the SUT gets
            // created!
            WindowsCommandLineSutConnector sutConnector = new WindowsCommandLineSutConnector(
                    settings.get(ConfigTags.SUTConnectorValue),
                    enabledProcessListener, settings().get(ConfigTags.StartupTime) * 1000,
                    Math.round(settings().get(ConfigTags.StartupTime).doubleValue() * 1000.0), builder,
                    settings.get(ConfigTags.FlashFeedback));
            sutConnector.setProtocolDelegate(delegate);
            // TODO startupTime and maxEngageTime seems to be the same, except one is double
            // and the other is long?
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
        System.out.println("<0>");
        Assert.notNull(system);
        System.out.println("<1>");
        State state = builder.apply(system);

        System.out.println("<2>");
        buildStateIdentifiers(state);
        System.out.println("<3>");
        state = ProtocolUtil.calculateZIndices(state);

        System.out.println("<4>");
        setStateForClickFilterLayerProtocol(state);

        System.out.println("<5>");
        if (settings.get(ConfigTags.Mode) == Modes.Spy)
            return state;

        System.out.println("<6>");
        Verdict verdict = getVerdict(state);
        System.out.println("<7>");
        state.set(Tags.OracleVerdict, verdict);

        System.out.println("<8>");
        setStateScreenshot(state);

        System.out.println("<9>");
        double severity = verdict.severity();
        System.out.println("<A>");
        double faultThreshold = settings().get(ConfigTags.FaultThreshold);
        System.out.println("<B>");
        if (mode() != Modes.Spy && severity >= faultThreshold) {
            System.out.println(String.format("<Severity: %f>", severity));
            faultySequence = true;
            LogSerialiser.log("Detected fault: " + verdict + "\n", LogSerialiser.LogLevel.Critical);
            // this was added to kill the SUT if it is frozen:
            System.out.println("<11>");
            if (verdict.severity() == Verdict.SEVERITY_NOT_RESPONDING) {
                // if the SUT is frozen, we should kill it!
                System.out.println("<12>");
                LogSerialiser.log("SUT frozen, trying to kill it!\n", LogSerialiser.LogLevel.Critical);
                System.out.println("<13>");
                SystemProcessHandling.killRunningProcesses(system, 100);
            }
        } else if (severity != Verdict.SEVERITY_OK && severity > passSeverity) {
            System.out.println("<14>");
            passSeverity = verdict.severity();
            System.out.println("<15>");
            LogSerialiser.log("Detected warning: " + verdict + "\n", LogSerialiser.LogLevel.Critical);
        }
        System.out.println("<16>");

        return state;
    }

    /**
     * Take a Screenshot of the State and associate the path into state tag
     */
    private void setStateScreenshot(State state) {
        Shape viewPort = state.get(Tags.Shape, null);
        if (viewPort != null) {
            if (NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WEBDRIVER)) {
                // System.out.println("DEBUG: Using WebDriver specific state shot.");
                state.set(Tags.ScreenshotPath, WdProtocolUtil.getStateshot(state));
            } else {
                // System.out.println("DEBUG: normal state shot");
                state.set(Tags.ScreenshotPath, ProtocolUtil.getStateshot(state));
            }
        }
    }

    @Override
    protected Verdict getVerdict(State state) {
        Assert.notNull(state);
        // -------------------
        // ORACLES FOR FREE
        // -------------------

        // if the SUT is not running, we assume it crashed
        if (!state.get(IsRunning, false))
            return new Verdict(Verdict.SEVERITY_NOT_RUNNING, "System is offline! I assume it crashed!");

        // if the SUT does not respond within a given amount of time, we assume it
        // crashed
        if (state.get(Tags.NotResponding, false)) {
            return new Verdict(Verdict.SEVERITY_NOT_RESPONDING, "System is unresponsive! I assume something is wrong!");
        }
        // ------------------------
        // ORACLES ALMOST FOR FREE
        // ------------------------

        if (this.suspiciousTitlesPattern == null)
            this.suspiciousTitlesPattern = Pattern.compile(settings().get(ConfigTags.SuspiciousTitles),
                    Pattern.UNICODE_CHARACTER_CLASS);

        // search all widgets for suspicious String Values
        Verdict suspiciousValueVerdict = Verdict.OK;
        for (Widget w : state) {
            suspiciousValueVerdict = suspiciousStringValueMatcher(w);
            if (suspiciousValueVerdict.severity() == Verdict.SEVERITY_SUSPICIOUS_TITLE) {
                return suspiciousValueVerdict;
            }
        }

        // if everything was OK ...
        return Verdict.OK;
    }

    private Verdict suspiciousStringValueMatcher(Widget w) {
        Matcher m;

        for (String tagForSuspiciousOracle : settings.get(ConfigTags.TagsForSuspiciousOracle)) {
            String tagValue = "";
            // First finding the Tag that matches the TagsToFilter string, then getting the
            // value of that Tag:
            for (Tag tag : w.tags()) {
                if (tag.name().equals(tagForSuspiciousOracle)) {
                    // Force the replacement of new line characters to avoid the usage of (?s) regex
                    // in the regular expression
                    tagValue = w.get(tag, "").toString().replace("\n", " ").replace("\r", " ");
                    break;
                    // System.out.println("DEBUG: tag found, "+tagToFilter+"="+tagValue);
                }
            }

            // Check whether the Tag value is empty or null
            if (tagValue == null || tagValue.isEmpty())
                continue; // no action

            // Ignore value ValuePattern for UIAEdit widgets
            if (tagValue.equals("ValuePattern")
                    && w.get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIAEdit")) {
                continue;
            }

            m = this.suspiciousTitlesMatchers.get(tagValue);
            if (m == null) {
                m = this.suspiciousTitlesPattern.matcher(tagValue);
                this.suspiciousTitlesMatchers.put(tagValue, m);
            }

            if (m.matches()) {
                Visualizer visualizer = Util.NullVisualizer;
                Pen RedPen = Pen.newPen().setColor(Color.Red).setFillPattern(FillPattern.None)
                        .setStrokePattern(StrokePattern.Solid).build();
                // visualize the problematic widget, by marking it with a red box
                if (w.get(Tags.Shape, null) != null)
                    visualizer = new ShapeVisualizer(RedPen, w.get(Tags.Shape), "Suspicious Title", 0.5, 0.5);
                return new Verdict(Verdict.SEVERITY_SUSPICIOUS_TITLE,
                        "Discovered suspicious widget '" + tagForSuspiciousOracle + "' : '" + tagValue + "'.",
                        visualizer);
            }
        }
        return Verdict.OK;
    }

    /**
     * This methods prepares for deriving actions, but does not really derive them
     * yet. This is left for lower
     * level protocols. Here the parameters are set in case unwanted processes need
     * to be killed or the SUT needs to be brought back
     * to foreground. The latter is then done by selectActions in the
     * AbstractProtocol.
     * 
     * @param system
     * @param state
     * @return
     * @throws ActionBuildException
     */
    @Override
    public Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {
        Assert.notNull(state);
        Set<Action> actions = new HashSet<Action>();

        // If there is an unwanted process running, we need to kill it.
        // This is an unwanted process that is defined in the filter.
        // So we set this.forceKillProcess = process.right();
        // And then select action will take care of making the next action to select the
        // killing of the process.
        String processRE = settings().get(ConfigTags.ProcessesToKillDuringTest);
        if (processRE != null && !processRE.isEmpty()) {
            state.set(Tags.RunningProcesses, system.getRunningProcesses());
            for (Pair<Long, String> process : state.get(Tags.RunningProcesses,
                    Collections.<Pair<Long, String>>emptyList())) {
                if (process.left().longValue() != system.get(Tags.PID).longValue() &&
                        process.right() != null && process.right().matches(processRE)) { // pid x name

                    String forceKillProcess = process.right();
                    System.out.println("will kill unwanted process: " + process.left().longValue() + " (SYSTEM <"
                            + system.get(Tags.PID).longValue() + ">)");

                    LogSerialiser.log("Forcing kill-process <" + forceKillProcess + "> action\n",
                            LogSerialiser.LogLevel.Info);
                    Action killProcessAction = KillProcess.byName(forceKillProcess, 0);
                    killProcessAction.set(Tags.Desc, "Kill Process with name '" + forceKillProcess + "'");
                    killProcessAction.set(Tags.OriginWidget, state);
                    return new HashSet<>(Collections.singletonList(killProcessAction));
                }
            }
        }

        // If the system is in the background, we need to force it into the foreground!
        // We set this.forceToForeground to true and selectAction will make sure that
        // the next action we will select
        // is putting the SUT back into the foreground.
        if (!state.get(Tags.Foreground, true) && system.get(Tags.SystemActivator, null) != null) {
            LogSerialiser.log("Forcing SUT activation (bring to foreground) action\n", LogSerialiser.LogLevel.Info);
            Action foregroundAction = new ActivateSystem();
            foregroundAction.set(Tags.Desc, "Bring the system to the foreground.");
            foregroundAction.set(Tags.OriginWidget, state);
            return new HashSet<>(Collections.singletonList(foregroundAction));
        }

        // Note this list is always empty in this deriveActions.
        return actions;
    }

    // TODO is this process handling Windows specific? move to SystemProcessHandling
    /**
     * If unwanted processes need to be killed, the action returns an action to do
     * that. If the SUT needs
     * to be put in the foreground, then the action that is returned is putting it
     * in the foreground.
     * Otherwise it returns null.
     * 
     * @param state
     * @param actions
     * @return null if no preSelected actions are needed.
     */
    protected Set<Action> preSelectAction(SUT system, State state, Set<Action> actions) {
        // Assert.isTrue(actions != null && !actions.isEmpty());

        // TESTAR didn't find any actions in the State of the SUT
        // It is set in a method actionExecuted that is not being called anywhere (yet?)
        if (actions.isEmpty()) {
            System.out.println(
                    "DEBUG: Forcing ESC action in preActionSelection : Actions derivation seems to be EMPTY !");
            LogSerialiser.log("Forcing ESC action\n", LogSerialiser.LogLevel.Info);
            Action escAction = new AnnotatingActionCompiler().hitKey(KBKeys.VK_ESCAPE);
            buildEnvironmentActionIdentifiers(state, escAction);
            return new HashSet<>(Collections.singletonList(escAction));
        }

        return actions;
    }

    final static double MAX_ACTION_WAIT_FRAME = 1.0; // (seconds)
    // TODO move the CPU metric to another helper class that is not default
    // "TrashBinCode" or "SUTprofiler"
    // TODO check how well the CPU usage based waiting works

    protected boolean executeAction(SUT system, State state, Action action) {

        if (NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WEBDRIVER)) {
            // System.out.println("DEBUG: Using WebDriver specific action shot.");
            WdProtocolUtil.getActionshot(state, action);
        } else {
            // System.out.println("DEBUG: normal action shot");
            ProtocolUtil.getActionshot(state, action);
        }

        double waitTime = settings.get(ConfigTags.TimeToWaitAfterAction);

        try {
            double halfWait = waitTime == 0 ? 0.01 : waitTime / 2.0; // seconds
            Util.pause(halfWait); // help for a better match of the state' actions visualization
            action.run(system, state, settings.get(ConfigTags.ActionDuration));
            int waitCycles = (int) (MAX_ACTION_WAIT_FRAME / halfWait);
            long actionCPU;
            do {
                long CPU1[] = NativeLinker.getCPUsage(system);
                Util.pause(halfWait);
                long CPU2[] = NativeLinker.getCPUsage(system);
                actionCPU = (CPU2[0] + CPU2[1] - CPU1[0] - CPU1[1]);
                waitCycles--;
            } while (actionCPU > 0 && waitCycles > 0);

            // Save the executed action information into the logs
            saveActionInfoInLogs(state, action, "ExecutedAction");

            return true;
        } catch (ActionFailedException afe) {
            return false;
        }
    }

    protected boolean replayAction(SUT system, State state, Action action, double actionWaitTime,
            double actionDuration) {
        // Get an action screenshot based on the NativeLinker platform
        if (NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WEBDRIVER)) {
            WdProtocolUtil.getActionshot(state, action);
        } else {
            ProtocolUtil.getActionshot(state, action);
        }

        try {
            double halfWait = actionWaitTime == 0 ? 0.01 : actionWaitTime / 2.0; // seconds
            Util.pause(halfWait); // help for a better match of the state' actions visualization
            action.run(system, state, actionDuration);
            int waitCycles = (int) (MAX_ACTION_WAIT_FRAME / halfWait);
            long actionCPU;
            do {
                long CPU1[] = NativeLinker.getCPUsage(system);
                Util.pause(halfWait);
                long CPU2[] = NativeLinker.getCPUsage(system);
                actionCPU = (CPU2[0] + CPU2[1] - CPU1[0] - CPU1[1]);
                waitCycles--;
            } while (actionCPU > 0 && waitCycles > 0);

            // Save the replayed action information into the logs
            saveActionInfoInLogs(state, action, "ReplayedAction");

            return true;
        } catch (ActionFailedException afe) {
            return false;
        }
    }

    /**
     * This method is here, so that ClickFilterLayerProtocol can override it, and
     * the behaviour is updated
     *
     * @param canvas
     * @param state
     * @param actions
     */
    protected void visualizeActions(Canvas canvas, State state, Set<Action> actions) {
        SutVisualization.visualizeActions(canvas, state, actions);
    }

    /**
     * Returns the next action that will be selected. If unwanted processes need to
     * be killed, the action kills them. If the SUT needs
     * to be put in the foreground, then the action is putting it in the foreground.
     * Otherwise the action is selected according to
     * action selection mechanism selected.
     * 
     * @param state
     * @param actions
     * @return
     */
    @Override
    public Action selectAction(SUT system, State state, Set<Action> actions) {
        Assert.isTrue(actions != null && !actions.isEmpty());
        return RandomActionSelector.selectAction(actions);
    }

    protected String getRandomText(Widget w) {
        return DataManager.getRandomData();
    }

    /**
     * STOP criteria for selecting more actions for a sequence
     * 
     * @param state
     * @return
     */

    @Override
    public boolean moreActions(State state) {
        System.out.println(String.format("<<< Faulty sequence: %s >>>", faultySequence ? "YES" : "NO"));
        System.out.println(String.format("<<< Is running: %s >>>", state.get(Tags.IsRunning, false) ? "YES" : "NO"));
        System.out.println(
                String.format("<<< Not respoiding: %s >>>", state.get(Tags.NotResponding, false) ? "YES" : "NO"));
        System.out.println(String.format("<<< Action: %d of %d >>>", actionCount(), lastSequenceActionNumber));
        System.out.println(String.format("<<< Time: %f of %f >>>", timeElapsed(), settings().get(ConfigTags.MaxTime)));
        return (!settings().get(ConfigTags.StopGenerationOnFault) || !faultySequence) &&
                state.get(Tags.IsRunning, false) && !state.get(Tags.NotResponding, false) &&
                // actionCount() < settings().get(ConfigTags.SequenceLength) &&
                actionCount() <= lastSequenceActionNumber &&
                timeElapsed() < settings().get(ConfigTags.MaxTime);
    }

    /**
     * STOP criteria deciding whether more sequences are required in a test run
     * 
     * @return
     */
    @Override
    public boolean moreSequences() {
        return sequenceCount() <= settings().get(ConfigTags.Sequences) &&
                timeElapsed() < settings().get(ConfigTags.MaxTime);
    }

    @Override
    protected void stopSystem(SUT system) {

        if (system != null) {
            AutomationCache ac = system.getNativeAutomationCache();
            if (ac != null)
                ac.releaseCachedAutomationElements();
        }
        if (system != null) {
            System.out.println("System class: " + system.getClass().getSimpleName());
            system.stop();
            SystemProcessHandling.killRunningProcesses(system, 0);
        }
    }

    @Override
    protected void postSequenceProcessing() {

    }

    /**
     * method for closing the internal TESTAR test session
     */
    private void closeTestarTestSession() {
        // cleaning the variables started in initialize()
        try {
            if (!settings.get(ConfigTags.UnattendedTests)) {
                System.out.println("-= 0 =-");
                if (GlobalScreen.isNativeHookRegistered()) {
                    System.out.println("-= 1 =-");
                    LogSerialiser.log("Unregistering keyboard and mouse hooks\n", LogSerialiser.LogLevel.Debug);
                    System.out.println("-= 2 =-");
                    GlobalScreen.removeNativeMouseMotionListener(eventHandler);
                    System.out.println("-= 3 =-");
                    GlobalScreen.removeNativeMouseListener(eventHandler);
                    System.out.println("-= 4 =-");
                    GlobalScreen.removeNativeKeyListener(eventHandler);
                    System.out.println("-= 5 =-");
                    // new Thread(() -> {
                    // try {
                    // GlobalScreen.unregisterNativeHook();
                    // } catch (NativeHookException e) {
                    // e.printStackTrace();
                    // }
                    // }).start();
                    GlobalScreen.unregisterNativeHook();
                    System.out.println("-= 6 =-");
                }
            }
        } catch (NativeHookException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            // no ConfigTags
            e.printStackTrace();
            System.out.println("-= ERROR =-");
        }

    }

    @Override
    protected void closeTestSession() {
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

    /**
     * Replay Mode using State Model Sequence Layer.
     * 
     * @param settings
     * @throws StateModelException
     *                             import
     *                             org.testar.monkey.alayer.windows.WinApiException;
     */
    protected void runReplayStateModelOuterLoop(final Settings settings) throws StateModelException {
        // We need at least the name of the model we want to replay (maybe created
        // without version)
        if (settings.get(ConfigTags.ReplayApplicationName, "").isEmpty()) {
            System.err.println("------------------------------------------------------------------------");
            System.err.println("ERROR: ReplayModel mode needs at least the setting ReplayApplicationName");
            System.err.println("------------------------------------------------------------------------");

            // notify the stateModelManager that the testing has finished
            stateModelManager.notifyTestingEnded();

            // Finish TESTAR execution
            mode = Modes.Quit;
            return;
        }

        // Get State Model Identifier from the model we want to replay
        // We will need this to extract the abstract actions later
        String replayName = settings.get(ConfigTags.ReplayApplicationName, "");
        String replayVersion = settings.get(ConfigTags.ReplayApplicationVersion, "");
        String replayModelIdentifier = ReplayStateModelUtil.getReplayModelIdentifier(stateModelManager, replayName,
                replayVersion);

        // Check if the model to replay and current model are using different
        // abstraction
        // If so, no sense to use replay model mode, because actions identifiers will be
        // different
        String replayAbsAtt = ReplayStateModelUtil.getReplayModelAbstractAttributes(stateModelManager,
                replayModelIdentifier);
        String currentAbsAtt = Arrays.toString(CodingManager.getCustomTagsForAbstractId());
        if (!ReplayStateModelUtil.sameAbstractionAttributes(replayAbsAtt, currentAbsAtt)) {
            System.err.println("--------------------------------------------------------------------------------");
            System.err.println("ERROR: Replay and Current StateModel are using different AbstractStateAttributes");
            System.err.println("ERROR: StateModel to replay AbstractStateAttributes: " + replayAbsAtt);
            System.err.println("ERROR: Current StateModel AbstractStateAttributes: " + currentAbsAtt);
            System.err.println("--------------------------------------------------------------------------------");

            // notify the stateModelManager that the testing has finished
            stateModelManager.notifyTestingEnded();

            // Finish TESTAR execution
            mode = Modes.Quit;
            return;
        }

        // User has indicated the specific sequence id to replay,
        // Only replay this sequence and stop
        if (!settings.get(ConfigTags.ReplayModelSequenceId, "").isEmpty()) {
            String msg = String.format(
                    "ReplayStateModelOuterLoop... Specific TestSequence %s for AbstractStateModel (%s, %s)",
                    settings.get(ConfigTags.ReplayModelSequenceId), replayName, replayVersion);
            System.out.println(msg);
            runReplayStateModelInnerLoop(settings.get(ConfigTags.ReplayModelSequenceId), replayModelIdentifier);
        }
        // User has indicated a specific sequence time to replay
        // Only replay this sequence and stop
        else if (!settings.get(ConfigTags.ReplayModelSequenceTime, "").isEmpty()) {
            String msg = String.format(
                    "ReplayStateModelOuterLoop... Specific TestSequence TIME %s for AbstractStateModel (%s, %s)",
                    settings.get(ConfigTags.ReplayModelSequenceTime), replayName, replayVersion);
            System.out.println(msg);
            String sequenceIdentifier = ReplayStateModelUtil.getReplaySequenceIdentifierByTime(stateModelManager,
                    settings.get(ConfigTags.ReplayModelSequenceTime));
            runReplayStateModelInnerLoop(sequenceIdentifier, replayModelIdentifier);
        }
        // User has indicated a complete state model (name and version) to replay
        // Replay all the existing sequences of this model
        else {
            // Get the number of TestSequences of the model we want to replay
            int numberTestSequences = ReplayStateModelUtil.getReplayTestSequenceNumber(stateModelManager,
                    replayModelIdentifier, replayName, replayVersion);
            String msg = String.format(
                    "ReplayStateModelOuterLoop... %s TestSequences found for AbstractStateModel (%s, %s)",
                    numberTestSequences, replayName, replayVersion);
            System.out.println(msg);

            // Get the counter of the initial TestSequence we want to replay.
            // Ex: maybe we need to replay 3 TestSequences from TS-7 to TS-9
            Set<String> sequenceIdsToReplay = ReplayStateModelUtil.getReplayAllSequenceIdFromModel(stateModelManager,
                    replayModelIdentifier, replayName, replayVersion);

            // Iterate over all TestSequences to reproduce them
            for (String sequenceId : sequenceIdsToReplay) {
                runReplayStateModelInnerLoop(sequenceId, replayModelIdentifier);
            }
        }

        // notify the statemodelmanager that the testing has finished
        stateModelManager.notifyTestingEnded();

        // Going back to TESTAR settings dialog if it was used to start replay:
        mode = Modes.Quit;
    }

    protected void runReplayStateModelInnerLoop(String sequenceIdentifier, String replayModelIdentifier)
            throws StateModelException {
        // Number of actions that contains the current sequence to replay
        int replayActionCount = ReplayStateModelUtil.getReplayActionStepsCount(stateModelManager, sequenceIdentifier);

        // Reset LogSerialiser
        LogSerialiser.finish();
        LogSerialiser.exit();

        synchronized (this) {
            OutputStructure.calculateInnerLoopDateString();
            OutputStructure.sequenceInnerLoopCount++;
        }

        preSequencePreparations();

        // action extracted or not successfully
        boolean success = true;
        // reset the faulty variable because we started a new execution
        faultySequence = false;

        SUT system = startSystem();

        // Generating the new sequence file that can be replayed:
        generatedSequence = getAndStoreGeneratedSequence();
        currentSeq = getAndStoreSequenceFile();

        this.cv = buildCanvas();
        State state = getState(system);

        setReplayVerdict(getVerdict(state));

        // notify the statemodelmanager
        stateModelManager.notifyTestSequencedStarted();

        double rrt = settings.get(ConfigTags.ReplayRetryTime);

        // count the action execution number
        actionCount = 1;

        while (success && !faultySequence && mode() == Modes.ReplayModel && actionCount <= replayActionCount) {
            /**
             * Extract the action we want to replay
             */
            // Get the counter of the action step
            // We need to do this because one model contains multiple sequences
            String actionSequence = sequenceIdentifier + "-" + actionCount + "-" + sequenceIdentifier + "-"
                    + (actionCount + 1);
            String concreteActionId = ReplayStateModelUtil.getReplayConcreteActionStep(stateModelManager,
                    actionSequence);
            String actionDescriptionReplay = ReplayStateModelUtil.getReplayActionDescription(stateModelManager,
                    actionSequence);

            // Now we get the AbstractActionId of the model that contains this counter
            // action step
            // This is the action we want to replay and we need to search in the state
            String abstractActionReplayId = ReplayStateModelUtil.getReplayAbstractActionIdFromConcreteAction(
                    stateModelManager, replayModelIdentifier, concreteActionId);
            // Derive Actions of the current State
            Set<Action> actions = deriveActions(system, state);
            buildStateActionsIdentifiers(state, actions);

            // Now lets see if current state contains the action we want to replay
            Action actionToReplay = null;
            // First, use the AbstractIDCustom of current state actions to find the action
            // we want to replay
            for (Action a : actions) {
                if (a.get(Tags.AbstractIDCustom, "").equals(abstractActionReplayId)) {
                    actionToReplay = a;
                    // For Type actions we need to type the same text
                    if (actionToReplay.get(Tags.Role, ActionRoles.Action).toString().contains("Type")) {
                        actionToReplay = actionTypeToReplay(actionToReplay, actionDescriptionReplay);
                    }
                    break;
                }
            }
            // State actions does not contain the action we want to replay
            if (actionToReplay == null) {
                // But lets check system preSelectedActions (ESC, foreground, kill process)
                Action systemAction = preSelectAction(system, state, actions).stream().findAny().get();
                if (systemAction != null
                        && systemAction.get(Tags.AbstractIDCustom, "").equals(abstractActionReplayId)) {
                    actionToReplay = systemAction;
                }
            }

            // notify to state model the current state
            stateModelManager.notifyNewStateReached(state, actions);

            // We did not find the action we want to replay in the current state or in the
            // system actions
            // The SUT has changed or we are using a different abstraction
            // But the sequence is not replayable
            if (actionToReplay == null) {
                String msg = String.format("Action 'AbstractIDCustom=%s' to replay '%s' not found. ",
                        abstractActionReplayId, actionDescriptionReplay);
                msg = msg.concat("\n");
                msg = msg.concat("The State is different or action is not derived");
                System.out.println(msg);
                setReplayVerdict(new Verdict(Verdict.SEVERITY_UNREPLAYABLE, msg));

                // We do not success trying to found the action to replay
                success = false;
            } else {
                // Action to Replay was found, lets execute it

                double actionDelay = settings.get(ConfigTags.TimeToWaitAfterAction, 1.0);
                double actionDuration = settings.get(ConfigTags.ActionDuration, 1.0);

                cv.begin();
                Util.clear(cv);
                cv.end();

                // In Replay-mode, we only show the red dot if visualizationOn is true:
                if (visualizationOn)
                    SutVisualization.visualizeSelectedAction(settings, cv, state, actionToReplay);

                try {
                    String replayMessage = String.format("Trying to replay (%d): %s... [time window = " + rrt + "]",
                            actionCount, actionToReplay.get(Tags.Desc, ""));
                    LogSerialiser.log(replayMessage, LogSerialiser.LogLevel.Info);

                    preSelectAction(system, state, actions);

                    // before action execution, pass it to the state model manager
                    stateModelManager.notifyActionExecution(actionToReplay);

                    actionToReplay.run(system, state, actionDuration);

                    actionCount++;
                    LogSerialiser.log("Success!\n", LogSerialiser.LogLevel.Info);
                } catch (ActionFailedException afe) {
                }

                Util.pause(actionDelay);

                state = getState(system);

                // Saving the actions and the executed action into replayable test sequence:
                saveActionIntoFragmentForReplayableSequence(actionToReplay, state, actions);

                setReplayVerdict(getVerdict(state));
            }
        }

        // notify to state model the last state
        Set<Action> actions = deriveActions(system, state);
        buildStateActionsIdentifiers(state, actions);
        for (Action a : actions)
            if (a.get(Tags.AbstractIDCustom, null) == null)
                buildEnvironmentActionIdentifiers(state, a);

        stateModelManager.notifyNewStateReached(state, actions);

        cv.release();

        if (cv != null) {
            cv.release();
        }
        if (system != null) {
            system.stop();
        }

        if (faultySequence) {
            String msg = "Replayed Sequence contains Errors: " + getReplayVerdict().info();
            System.out.println(msg);
            LogSerialiser.log(msg, LogSerialiser.LogLevel.Info);

        } else if (success) {
            String msg = "Sequence successfully replayed!\n";
            System.out.println(msg);
            LogSerialiser.log(msg, LogSerialiser.LogLevel.Info);

        } else if (getReplayVerdict().severity() == Verdict.SEVERITY_UNREPLAYABLE) {
            System.out.println(getReplayVerdict().info());
            LogSerialiser.log(getReplayVerdict().info(), LogSerialiser.LogLevel.Critical);

        } else {
            String msg = "Fail replaying sequence.\n";
            System.out.println(msg);
            LogSerialiser.log(msg, LogSerialiser.LogLevel.Critical);
        }

        // calling finishSequence() to allow scripting GUI interactions to close the
        // SUT:
        finishSequence();

        // notify the state model manager of the sequence end
        stateModelManager.notifyTestSequenceStopped();

        // Close and save the replayable fragment of the current sequence
        writeAndCloseFragmentForReplayableSequence();

        // Copy sequence file into proper directory:
        classifyAndCopySequenceIntoAppropriateDirectory(getReplayVerdict(), generatedSequence, currentSeq);

        LogSerialiser.finish();

        postSequenceProcessing();

        // Stop system and close the SUT
        stopSystem(system);
    }

    /**
     * Replay a Type action requires to reuse the same text.
     * 
     * @param actionToReplay
     * @param actionDescriptionReplay
     * @return
     */
    private Action actionTypeToReplay(Action actionToReplay, String actionDescriptionReplay) {
        for (Action compAct : ((CompoundAction) actionToReplay).getActions()) {
            if (compAct instanceof Type) {
                // Type 'kotrnrls' into 'Editor de texto
                String replayText = actionDescriptionReplay.substring(6);
                replayText = replayText.substring(0, replayText.indexOf("'"));
                ((Type) compAct).setText(replayText);
                actionToReplay.set(Tags.Desc, actionDescriptionReplay);
            }
        }
        return actionToReplay;
    }

}
