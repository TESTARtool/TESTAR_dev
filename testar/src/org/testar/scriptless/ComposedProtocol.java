/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.OutputStructure;
import org.testar.config.ConfigTags;
import org.testar.config.TestarMode;
import org.testar.config.settings.Settings;
import org.testar.config.verdict.VerdictProcessing;
import org.testar.core.Assert;
import org.testar.core.action.Action;
import org.testar.core.devices.AWTMouse;
import org.testar.core.devices.DummyMouse;
import org.testar.core.exceptions.ActionBuildException;
import org.testar.core.exceptions.StateBuildException;
import org.testar.core.exceptions.SystemStartException;
import org.testar.core.serialisation.LogSerialiser;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.util.Util;
import org.testar.core.verdict.Verdict;
import org.testar.engine.devices.EventHandler;
import org.testar.plugin.process.SystemProcessHandling;
import org.testar.scriptless.listener.ModeListener;
import org.testar.scriptless.listener.EventListener;
import org.testar.scriptless.listener.VisualizationListener;
import org.testar.scriptless.mode.GenerateMode;
import org.testar.scriptless.mode.SpyMode;

import java.awt.GraphicsEnvironment;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * This is the composition flow of TESTAR (generate mode):
 *
 * - InitializeSettings (TESTAR settings)
 * - InitializeTestSession (before starting the first sequence)
 * - OUTER LOOP:
 * 		StartTestSequence (before each sequence, for example starting WebDriver, JaCoCo or another process for sequence)
 * 		StartSystem
 *      GetState
 * 		BeginSequence (starting "script" on the GUI of the SUT, for example login)
 * 		INNER LOOP
 * 			GetState
 * 			GetVerdicts
 * 			StopCriteria (moreActions/moreSequences/time?)
 * 			DeriveActions
 * 			SelectAction
 * 			ExecuteAction
 * 		StopSUT
 * 		FinishTestSequence (after each sequence)
 * - CloseTestSession (after finishing the last sequence)
 *
 */
public abstract class ComposedProtocol implements Consumer<Settings> {

    public static final Logger INDEXLOG = LogManager.getLogger();
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    protected TestingServices testingServices;
    public final TestingServices testingServices() {
        return Assert.notNull(testingServices);
    }

    protected ScriptlessCapabilities scriptlessCapabilities;
	public final ScriptlessCapabilities scriptlessCapabilities() { 
        return Assert.notNull(scriptlessCapabilities);
    }

    protected final RuntimeContext runtimeContext = new RuntimeContext();
	public final RuntimeContext runtimeContext() {
        return Assert.notNull(runtimeContext);
    }

    protected ModeListener modeListener;
    public final ModeListener modeListener() {
        return Assert.notNull(modeListener);
    }

    protected VisualizationListener visualizationListener;
    public final VisualizationListener visualizationListener() {
        return Assert.notNull(visualizationListener);
    }

    @Override
    public final void accept(final Settings protocolSettings) {
        if (!GraphicsEnvironment.isHeadless()) {
            runtimeContext().setMouse(AWTMouse.build());
        } else {
            System.out.println("Headless environment! Initializing a DummyMouse device");
            runtimeContext().setMouse(DummyMouse.build());
        }

        runtimeContext.setSettings(initializeSettings(protocolSettings));

        if (runtimeContext().mode() == TestarMode.Generate) {
            OutputStructure.calculateOuterLoopDateString();
            OutputStructure.sequenceInnerLoopCount = 0;
            OutputStructure.createOutputSUTname(runtimeContext.settings());
            OutputStructure.createOutputFolders();
        }

        runtimeContext.setMode(runtimeContext.settings().get(ConfigTags.Mode));
        runtimeContext.setStartTime(Util.time());
        runtimeContext.setVisualizationEnabled(runtimeContext.settings().get(ConfigTags.VisualizeActions));
        runtimeContext.setVerdictProcessing(new VerdictProcessing(runtimeContext.settings()));

        modeListener = new ModeListener(runtimeContext, runtimeContext.settings().get(ConfigTags.KeyBoardListener, false));
        visualizationListener = new VisualizationListener(runtimeContext);
        EventHandler eventHandler = new EventHandler(new EventListener(modeListener(), visualizationListener()));
        runtimeContext.setEventHandler(eventHandler);

        // Build the core testing services
        testingServices = ScriptlessFactory.buildServices(runtimeContext);
        runtimeContext.setSessionReportingManager(testingServices.sessionReportingManager());
        runtimeContext.setStateModelManager(testingServices.stateModelManager());

        // Build the scriptless testing services
        scriptlessCapabilities = ScriptlessFactory.buildCapabilities(runtimeContext);

        LogSerialiser.log("'" + runtimeContext().mode() + "' mode active.\n", LogSerialiser.LogLevel.Info);

        try {
            if (runtimeContext().mode() == TestarMode.Spy) {
                new SpyMode().runSpyLoop(this);
            } else if (runtimeContext().mode() == TestarMode.Generate) {
                new GenerateMode().runGenerateOuterLoop(this);
            }
        } finally {
            closeTestSession();
        }
    }

	/**
	 * Initialize is run as the first thing to initialize TESTAR with the given settings
	 */
    public Settings initializeSettings(Settings settings) {
        Assert.notNull(settings);
        return scriptlessCapabilities.settingsCapability().initializeSettings(settings);
    }

	/**
	 * This method is called before the first test sequence, allowing for example setting up the test environment
	 */
    public void initializeTestSession() {
        scriptlessCapabilities.testSessionCapability().initializeTestSession(runtimeContext);
    }

	/**
	 * This methods is called before each test sequence, allowing for example using external profiling software on the SUT
	 */
    public void startTestSequence() {
        scriptlessCapabilities.testSequenceCapability().startTestSequence(runtimeContext);
    }

	/**
	 * This method is called when TESTAR starts the System Under Test (SUT). The method should
	 * take care of
	 *   1) starting the SUT (you can use TESTAR's settings obtainable from <code>settings()</code> to find
	 *      out what executable to run)
	 *   2) waiting until the system is fully loaded and ready to be tested (with large systems, you might have to wait several
	 *      seconds until they have finished loading)
	 *
	 * @return  a started SUT, ready to be tested.
	 * @throws SystemStartException
	 */
    public SUT startSystem() throws SystemStartException {
        return testingServices.systemService().startSystem();
    }

	/**
	 * This method is invoked each time the TESTAR starts the SUT to generate a new sequence.
	 * This can be used for example for bypassing a login screen by filling the username and password
	 * or bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
	 * the SUT's configuration files etc.)
	 *
	 * @param system
	 * @param state
	 */
    public void beginSequence(SUT system, State initialState) {
        Assert.notNull(system, initialState);
        scriptlessCapabilities.testSequenceCapability().beginSequence(runtimeContext, system, initialState);
    }

	/**
	 * This method is called when the TESTAR requests the state of the SUT.
	 * Here you can add additional information to the SUT's state or write your
	 * own state fetching routine. The state should have attached an oracle
	 * (TagName: <code>Tags.OracleVerdict</code>) which describes whether the
	 * state is erroneous and if so why.
	 *
	 * @param system
	 * @return the current state of the SUT with attached oracle.
	 * @throws StateBuildException
	 */
    public State getState(SUT system) throws StateBuildException {
        Assert.notNull(system);

        State state = testingServices.stateService().getState(system);
        runtimeContext.setLatestState(state);

        testingServices.sessionReportingManager().prepareState(state);
        testingServices.sessionReportingManager().addState(state);

        return state;
    }

	/**
	 * The getVerdicts methods implements the online state oracles that
	 * examine the SUT's current state and returns oracle verdicts.
	 *
	 * @return list of oracle verdicts, which determine whether the state is erroneous and why.
	 */
    public List<Verdict> getVerdicts(SUT system, State state) {
        Assert.notNull(system, state);
        List<Verdict> verdicts = testingServices.oracleEvaluationService().getVerdicts(system, state);
        return scriptlessCapabilities.scriptlessOracleComposer().composeVerdicts(runtimeContext, system, state, verdicts);
    }

	/**
	 * This method is used by TESTAR to determine the set of currently available actions.
	 * You can use the SUT's current state, analyze the widgets and their properties to create
	 * a set of sensible actions, such as: "Click every Button which is enabled" etc.
	 *
	 * @param system the SUT
	 * @param state the SUT's current state
	 * @return  a set of actions
	 */
    public Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {
        Assert.notNull(system, state);
        return testingServices.actionDerivationService().deriveActions(system, state);
    }

	/**
	 * Select one of the available actions using the action selection algorithm of your choice (e.g. at random)
	 *
	 * @param state the SUT's current state
	 * @param actions the set of derived actions
	 * @return  the selected action
	 */
    public Action selectAction(State state, Set<Action> actions) {
        Assert.notNull(state, actions);
        Assert.isTrue(!actions.isEmpty());

        Action selectedAction = testingServices.actionSelectorService().selectAction(state, actions);
        Assert.notNull(selectedAction);

        testingServices.sessionReportingManager().addSelectedAction(state, selectedAction);

        return selectedAction;
    }

	/**
	 * Execute the selected action.
	 *
	 * @param system the SUT
	 * @param state the SUT's current state
	 * @param action the action to execute
	 * @return whether or not the execution succeeded
	 */
    public boolean executeAction(SUT system, State state, Action action) {
        Assert.notNull(system, state, action);
        return testingServices.actionExecutionService().executeAction(system, state, action);
    }

	/**
	 * StopCriteria for a sequence:
	 *
	 * TESTAR uses this method to determine when to stop the generation of actions for the
	 * current sequence. You can stop deriving more actions after:
	 * - a specified amount of executed actions, which is specified through the SequenceLength setting, or
	 * - after a specific time, that is set in the MaxTime setting
	 *
	 * @return  if <code>true</code> continue generation, else stop
	 */
    public boolean stopCriteriaTestSequence(State state) {
        Assert.notNull(state);
        return scriptlessCapabilities.stopCriteriaCapability().stopTestSequence(runtimeContext, state);
    }

	/**
	 * StopCriteria for a test session:
	 *
	 * TESTAR uses this method to determine when to stop the entire test sequence
	 * You could stop the test after:
	 * - a specified amount of sequences, which is specified through the Sequences setting, or
	 * - after a specific time, that is set in the MaxTime setting
	 *
	 * @return  if <code>true</code> continue test, else stop
	 */
    public boolean stopCriteriaTestSession() {
        return scriptlessCapabilities.stopCriteriaCapability().stopTestSession(runtimeContext);
    }

	/**
	 * This methods stops the SUT
	 *
	 * @param system
	 */
    public void stopSystem(SUT system) {
        Assert.notNull(system);
        SystemProcessHandling.killTestLaunchedProcesses(runtimeContext.contextRunningProcesses());
        testingServices.systemService().stopSystem(system);
    }

	/**
	 * This method is called after each sequence, to allow for example checking the coverage of the sequence
	 */
    public void finishTestSequence(List<Verdict> verdicts) {
        Assert.notNull(verdicts);
        scriptlessCapabilities.testSequenceCapability().finishTestSequence(runtimeContext, verdicts);
    }

	/**
	 * This method is called after the last sequence, to allow for example handling the reporting of the session
	 */
    public void closeTestSession() {
        scriptlessCapabilities.testSessionCapability().closeTestSession(runtimeContext);
    }
}
