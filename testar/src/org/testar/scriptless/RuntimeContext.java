/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless;

import org.testar.config.verdict.VerdictProcessing;
import org.testar.core.alayer.Canvas;
import org.testar.core.process.ProcessInfo;
import org.testar.core.state.State;
import org.testar.oracle.Oracle;

import java.util.Collections;
import java.util.List;

/**
 * Mutable runtime state for protocol execution.
 */
public final class RuntimeContext {

    private Canvas canvas;
    public Canvas canvas() { return canvas; }
    public void setCanvas(Canvas canvas) { this.canvas = canvas; }

    private String generatedSequence;
    public String generatedSequence() { return generatedSequence; }
    public void setGeneratedSequence(String generatedSequence) { this.generatedSequence = generatedSequence; }

    private int actionCount;
    public int actionCount() { return actionCount; }
    public void setActionCount(int actionCount) { this.actionCount = actionCount; }

    private int sequenceCount;
    public int sequenceCount() { return sequenceCount; }
    public void setSequenceCount(int sequenceCount) { this.sequenceCount = sequenceCount; }

    private double startTime;
    public double startTime() { return startTime; }
    public void setStartTime(double startTime) { this.startTime = startTime; }

    private Oracle logOracle;
    public Oracle logOracle() { return logOracle; }
    public void setLogOracle(Oracle logOracle) { this.logOracle = logOracle; }

    private Oracle processListenerOracle;
    public Oracle processListenerOracle() { return processListenerOracle; }
    public void setProcessListenerOracle(Oracle processListenerOracle) { this.processListenerOracle = processListenerOracle; }

    private List<Oracle> extendedOraclesList = Collections.emptyList();
    public List<Oracle> extendedOraclesList() { return extendedOraclesList; }
    public void setExtendedOraclesList(List<Oracle> extendedOraclesList) { this.extendedOraclesList = extendedOraclesList; }

    private List<ProcessInfo> contextRunningProcesses;
    public List<ProcessInfo> contextRunningProcesses() { return contextRunningProcesses; }
    public void setContextRunningProcesses(List<ProcessInfo> contextRunningProcesses) { this.contextRunningProcesses = contextRunningProcesses; }

    private State latesState;
    public State latesState() { return latesState; }
    public void setLatesState(State latesState) { this.latesState = latesState; }

    private VerdictProcessing verdictProcessing;
    public VerdictProcessing verdictProcessing() { return verdictProcessing; }
    public void setVerdictProcessing(VerdictProcessing verdictProcessing) { this.verdictProcessing = verdictProcessing; }
}
