/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless;

import org.testar.config.TestarMode;
import org.testar.config.settings.Settings;
import org.testar.config.verdict.VerdictProcessing;
import org.testar.core.alayer.Canvas;
import org.testar.core.devices.Mouse;
import org.testar.core.process.ProcessInfo;
import org.testar.core.state.State;
import org.testar.engine.devices.EventHandler;
import org.testar.oracle.Oracle;
import org.testar.plugin.reporting.SessionReportingManager;
import org.testar.statemodel.StateModelManager;

import java.util.Collections;
import java.util.List;

/**
 * Mutable runtime state for protocol execution.
 */
public final class RuntimeContext {

	private Settings settings;
	public final Settings settings() { return settings; }
    protected void setSettings(Settings settings) { this.settings = settings; }

    private TestarMode mode;
    public final synchronized TestarMode mode() {
	    return mode;
	}
	public synchronized void setMode(TestarMode mode) {
	    if (mode() == mode) return;
	    else this.mode = mode;
	}

    private SessionReportingManager sessionReportingManager;
	public final SessionReportingManager sessionReportingManager() { return sessionReportingManager; }
    protected void setSessionReportingManager(SessionReportingManager sessionReportingManager) { this.sessionReportingManager = sessionReportingManager; }

    private StateModelManager stateModelManager;
	public final StateModelManager stateModelManager() { return stateModelManager; }
    protected void setStateModelManager(StateModelManager stateModelManager) { this.stateModelManager = stateModelManager; }

	private EventHandler eventHandler;
	public final EventHandler eventHandler() { return eventHandler; }
    protected void setEventHandler(EventHandler eventHandler) { this.eventHandler = eventHandler; }

    private Mouse mouse;
    public final Mouse mouse() { return mouse; }
    public void setMouse(Mouse mouse) { this.mouse = mouse; }

    private Canvas canvas;
    public final Canvas canvas() { return canvas; }
    public void setCanvas(Canvas canvas) { this.canvas = canvas; }

    private boolean visualizationEnabled = false;
    public final boolean isVisualizationEnabled() { return visualizationEnabled; }
    public void setVisualizationEnabled(boolean visualizationEnabled) { this.visualizationEnabled = visualizationEnabled; }

    private String generatedSequence;
    public final String generatedSequence() { return generatedSequence; }
    public void setGeneratedSequence(String generatedSequence) { this.generatedSequence = generatedSequence; }

    private int actionCount;
    public final int actionCount() { return actionCount; }
    public void setActionCount(int actionCount) { this.actionCount = actionCount; }

    private int sequenceCount;
    public final int sequenceCount() { return sequenceCount; }
    public void setSequenceCount(int sequenceCount) { this.sequenceCount = sequenceCount; }

    private double startTime;
    public final double startTime() { return startTime; }
    public void setStartTime(double startTime) { this.startTime = startTime; }

    private List<ProcessInfo> contextRunningProcesses;
    public final List<ProcessInfo> contextRunningProcesses() { return contextRunningProcesses; }
    public void setContextRunningProcesses(List<ProcessInfo> contextRunningProcesses) { this.contextRunningProcesses = contextRunningProcesses; }

    private State latestState;
    public final State latestState() { return latestState; }
    public void setLatestState(State latestState) { this.latestState = latestState; }

    private Oracle logOracle;
    public final Oracle logOracle() { return logOracle; }
    public void setLogOracle(Oracle logOracle) { this.logOracle = logOracle; }

    private Oracle processListenerOracle;
    public final Oracle processListenerOracle() { return processListenerOracle; }
    public void setProcessListenerOracle(Oracle processListenerOracle) { this.processListenerOracle = processListenerOracle; }

    private List<Oracle> extendedOraclesList = Collections.emptyList();
    public final List<Oracle> extendedOraclesList() { return extendedOraclesList; }
    public void setExtendedOraclesList(List<Oracle> extendedOraclesList) { this.extendedOraclesList = extendedOraclesList; }

    private VerdictProcessing verdictProcessing;
    public final VerdictProcessing verdictProcessing() { return verdictProcessing; }
    public void setVerdictProcessing(VerdictProcessing verdictProcessing) { this.verdictProcessing = verdictProcessing; }
}
