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
import org.testar.core.alayer.Canvas;
import org.testar.core.alayer.Pen;
import org.testar.core.devices.AWTMouse;
import org.testar.core.devices.DummyMouse;
import org.testar.core.exceptions.ActionBuildException;
import org.testar.core.exceptions.StateBuildException;
import org.testar.core.exceptions.SystemStartException;
import org.testar.core.serialisation.LogSerialiser;
import org.testar.core.serialisation.ScreenshotSerialiser;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.core.util.Util;
import org.testar.core.verdict.Verdict;
import org.testar.engine.devices.EventHandler;
import org.testar.engine.manager.NativeHookManager;
import org.testar.oracle.Oracle;
import org.testar.oracle.generic.log.LogOracle;
import org.testar.plugin.NativeLinker;
import org.testar.plugin.process.SystemProcessHandling;
import org.testar.scriptless.capability.ScriptlessCapabilities;
import org.testar.statemodel.StateModelManager;

import java.awt.GraphicsEnvironment;
import java.util.List;
import java.util.Set;

/**
 * Protocol base class for the TESTAR runtime flow.
 */
public abstract class ComposedProtocol extends VisualizationProtocol {

    public static final Logger INDEXLOG = LogManager.getLogger();
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    protected RuntimeServices runtimeServices;
    public final RuntimeServices runtimeServices() {
        return Assert.notNull(runtimeServices);
    }

    protected EventHandler eventHandler;
    protected StateModelManager stateModelManager;

    protected boolean logOracleEnabled;
    protected boolean processListenerOracleEnabled;

    protected ComposedProtocol() {
        this(ScriptlessCapabilities.defaults());
    }

    protected ComposedProtocol(ScriptlessCapabilities scriptlessCapabilities) {
        this.scriptlessCapabilities = Assert.notNull(scriptlessCapabilities);
    }

    @Override
    public final void accept(final Settings settings) {
        this.settings = settings;

        if (!GraphicsEnvironment.isHeadless()) {
            mouse = AWTMouse.build();
        } else {
            System.out.println("Headless environment! Initializing a DummyMouse device");
            mouse = DummyMouse.build();
        }

        initializeSettings(settings);

        try {
            if (mode() == TestarMode.Spy) {
                new SpyMode().runSpyLoop(this);
            } else if (mode() == TestarMode.Generate) {
                new GenerateMode().runGenerateOuterLoop(this);
            }
        } finally {
            closeTestSession();
        }
    }

    @Override
    protected void initializeSettings(Settings settings) {
        Assert.notNull(settings);
        this.settings = settings;

        visualizationOn = settings.get(ConfigTags.VisualizeActions);
        runtimeContext.setStartTime(Util.time());

        mode = settings.get(ConfigTags.Mode);
        runtimeContext.setVerdictProcessing(new VerdictProcessing(settings));
        runtimeServices = RuntimeFactory.resolve(settings);
        stateModelManager = runtimeServices.stateModelManager();

        logOracleEnabled = settings.get(ConfigTags.LogOracleEnabled, false);
        processListenerOracleEnabled = settings.get(ConfigTags.ProcessListenerEnabled, false);

        if (mode() == TestarMode.Generate) {
            OutputStructure.calculateOuterLoopDateString();
            OutputStructure.sequenceInnerLoopCount = 0;
            OutputStructure.createOutputSUTname(settings);
            OutputStructure.createOutputFolders();
        }

        eventHandler = initializeEventHandler();
        NativeHookManager.registerNativeHook(eventHandler);
        LogSerialiser.log("'" + mode() + "' mode active.\n", LogSerialiser.LogLevel.Info);
    }

    @Override
    protected void initializeTestSession() {
    }

    @Override
    protected void startTestSequence() {
        scriptlessCapabilities.sequenceLifecycleCapability().startTestSequence(this);

        String sequenceCountDir = "_sequence_" + OutputStructure.sequenceInnerLoopCount;
        String generatedSequence = OutputStructure.startInnerLoopDateString + "_"
                + OutputStructure.executedSUTname + sequenceCountDir;

        runtimeContext.setGeneratedSequence(generatedSequence);

        runtimeServices.sessionReportingManager().prepareGeneratedSequenceOutput(settings());

        stateModelManager.notifyTestSequencedStarted();

        LogSerialiser.log(
            "Starting sequence " + runtimeContext.sequenceCount()
            + " (output as: " + runtimeContext.generatedSequence() + ")\n\n",
            LogSerialiser.LogLevel.Info
        );
    }

    @Override
    protected SUT startSystem() throws SystemStartException {
        SUT system = runtimeServices().systemService().startSystem();
        scriptlessCapabilities.sequenceLifecycleCapability().startSystem(this, system);

        return system;
    }

    @Override
    protected void beginSequence(SUT system, State initialState) {
    }

    @Override
    protected State getState(SUT system) throws StateBuildException {
        Assert.notNull(system);

        State state = runtimeServices().stateService().getState(system);
        runtimeContext.setLatesState(state);

        runtimeServices.sessionReportingManager().prepareState(state);
        runtimeServices.sessionReportingManager().addState(state);

        return state;
    }

    @Override
    protected List<Verdict> getVerdicts(SUT system, State state) {
        Assert.notNull(system, state);

        List<Verdict> verdicts = scriptlessCapabilities.stateVerdictCapability().evaluateVerdicts(this, state);
        state.set(Tags.OracleVerdicts, verdicts);

        for (Verdict verdict : verdicts) {
            if (verdict.severity() == Verdict.Severity.NOT_RESPONDING.getValue()) {
                SystemProcessHandling.killRunningProcesses(system, 100);
            }
        }

        return verdicts;
    }

    @Override
    protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {
        Assert.notNull(system, state);
        return runtimeServices().actionDerivationService().deriveActions(system, state);
    }

    @Override
    protected Action selectAction(State state, Set<Action> actions) {
        Assert.notNull(state, actions);
        Assert.isTrue(!actions.isEmpty());

        Action selectedAction = runtimeServices().actionSelectorService().selectAction(state, actions);
        Assert.notNull(selectedAction);

        runtimeServices().sessionReportingManager().addSelectedAction(state, selectedAction);

        return selectedAction;
    }

    @Override
    protected boolean executeAction(SUT system, State state, Action action) {
        Assert.notNull(system, state, action);
        return runtimeServices().actionExecutionService().executeAction(system, state, action);
    }

    @Override
    protected boolean moreActions(State state) {
        return scriptlessCapabilities.stopCriteriaCapability().moreActions(this, state);
    }

    @Override
    protected boolean moreSequences() {
        return scriptlessCapabilities.stopCriteriaCapability().moreSequences(this);
    }

    @Override
    protected void stopSystem(SUT system) {
        SystemProcessHandling.killTestLaunchedProcesses(runtimeContext.contextRunningProcesses());
        runtimeServices().systemService().stopSystem(system);
    }

    @Override
    protected void finishTestSequence(List<Verdict> verdicts) {
        scriptlessCapabilities.sequenceLifecycleCapability().finishTestSequence(this, verdicts);
    }

    @Override
    protected void closeTestSession() {
        NativeHookManager.unregisterNativeListener(eventHandler);
    }

	void emergencyTerminateTestSequence(SUT system, Exception e) {
		ScreenshotSerialiser.finish();
		ScreenshotSerialiser.exit();
		LogSerialiser.log("Exception <" + e.getMessage() + "> has been caught\n", LogSerialiser.LogLevel.Critical); // screenshots must be serialised
		int i = 1;
		StringBuffer trace = new StringBuffer();
		for (StackTraceElement t : e.getStackTrace())
			trace.append("\n\t[" + i++ + "] " + t.toString());
		System.out.println("Exception <" + e.getMessage() + "> has been caught; Stack trace:" + trace.toString());
		stopSystem(system);
		LogSerialiser.flush();
		LogSerialiser.finish();
		LogSerialiser.exit();
		setMode(TestarMode.Quit);
	}










    public final boolean isLogOracleEnabled() {
        return logOracleEnabled;
    }

    public final boolean isProcessListenerOracleEnabled() {
        return processListenerOracleEnabled;
    }

    public Oracle createLogOracle(Settings settings) {
        return new LogOracle(settings);
    }

    protected Canvas buildCanvas() {
        return NativeLinker.getNativeCanvas(Pen.PEN_DEFAULT);
    }









}
