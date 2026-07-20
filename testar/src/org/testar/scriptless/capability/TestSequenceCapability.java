/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.capability;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

import org.testar.OutputStructure;
import org.testar.config.ConfigTags;
import org.testar.config.TestarMode;
import org.testar.core.Assert;
import org.testar.core.alayer.Pen;
import org.testar.core.serialisation.LogSerialiser;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.util.Util;
import org.testar.core.verdict.Verdict;
import org.testar.oracle.Oracle;
import org.testar.oracle.OracleSelection;
import org.testar.oracle.generic.log.LogOracle;
import org.testar.plugin.NativeLinker;
import org.testar.reporting.ReportManager;
import org.testar.scriptless.ComposedProtocol;
import org.testar.scriptless.RuntimeContext;

public class TestSequenceCapability {

    public void startTestSequence(RuntimeContext runtimeContext) {
        Assert.notNull(runtimeContext);
        String sequenceCountDir = "_sequence_" + OutputStructure.sequenceInnerLoopCount;
        String generatedSequence = OutputStructure.startInnerLoopDateString + "_"
                + OutputStructure.executedSUTname + sequenceCountDir;

        runtimeContext.setGeneratedSequence(generatedSequence);

        runtimeContext.setActionCount(1);

        runtimeContext.setCanvas(NativeLinker.getNativeCanvas(Pen.PEN_DEFAULT));

        if (runtimeContext.settings().get(ConfigTags.Mode) != TestarMode.Spy) {
            runtimeContext.sessionReportingManager().bindReporting(new ReportManager(runtimeContext.settings()));
        }

        if (runtimeContext.settings().get(ConfigTags.LogOracle, false)) {
            Oracle logOracle = new LogOracle(runtimeContext.settings());
            logOracle.initialize();
            runtimeContext.setLogOracle(logOracle);
        }

        runtimeContext.setExtendedOraclesList(OracleSelection.loadExtendedOracles(
                runtimeContext.settings().get(ConfigTags.ExtendedOracles, "")
        ));

        for (Oracle oracle : runtimeContext.extendedOraclesList()) {
            oracle.resetApplicationStatus();
            oracle.initialize();
        }

        runtimeContext.sessionReportingManager().prepareGeneratedSequenceOutput(runtimeContext.settings());

        runtimeContext.stateModelManager().notifyTestSequencedStarted();

        LogSerialiser.log(
            "Starting sequence " + runtimeContext.sequenceCount()
            + " (output as: " + runtimeContext.generatedSequence() + ")\n\n",
            LogSerialiser.LogLevel.Info
        );
    }

    public void beginSequence(RuntimeContext runtimeContext, SUT system, State initialState) {
        Assert.notNull(runtimeContext, system, initialState);
        LogSerialiser.log(Util.dateString(ComposedProtocol.DATE_FORMAT) + " Starting SUT ...\n", LogSerialiser.LogLevel.Info);
        LogSerialiser.log("SUT is running!\n", LogSerialiser.LogLevel.Debug);
        LogSerialiser.log("Building canvas...\n", LogSerialiser.LogLevel.Debug);
    }

    public void finishTestSequence(RuntimeContext runtimeContext, List<Verdict> verdicts) {
        Assert.notNull(runtimeContext, verdicts);

        List<Verdict> filteredVerdicts = runtimeContext.verdictProcessing().filterDuplicates(verdicts);

        runtimeContext.sessionReportingManager().addTestVerdicts(filteredVerdicts);
        String statusInfo = buildStatusInfo(filteredVerdicts);

        runtimeContext.setGeneratedSequence(
                OutputStructure.outerLoopOutputDir + File.separator + runtimeContext.generatedSequence()
        );

        ComposedProtocol.INDEXLOG.info(
                OutputStructure.executedSUTname
                        + " " + runtimeContext.settings().get(ConfigTags.Mode, runtimeContext.mode())
                        + " " + runtimeContext.generatedSequence()
                        + " " + statusInfo
        );

        runtimeContext.verdictProcessing().storeNewVerdicts(filteredVerdicts);

        logVacuousPassOracles(runtimeContext.extendedOraclesList());

        runtimeContext.sessionReportingManager().finishReport();

        LogSerialiser.log("Releasing canvas...\n", LogSerialiser.LogLevel.Debug);
        runtimeContext.canvas().release();
        runtimeContext.sessionReportingManager().endSequenceOutput();
    }

    private String buildStatusInfo(List<Verdict> verdicts) {
        List<Verdict> normalized = (verdicts == null || verdicts.isEmpty())
                ? Collections.singletonList(Verdict.OK)
                : verdicts;

        if (normalized.size() == 1) {
            return normalized.get(0).info();
        }

        StringJoiner joiner = new StringJoiner(" | ");
        for (Verdict verdict : normalized) {
            joiner.add("[" + verdict.verdictSeverityTitle() + "] " + verdict.info());
        }

        return joiner.toString();
    }

    private void logVacuousPassOracles(List<Oracle> extendedOracles) {
        if (extendedOracles == null || extendedOracles.isEmpty()) {
            return;
        }

        StringJoiner joiner = new StringJoiner(", ");
        for (Oracle oracle : extendedOracles) {
            if (oracle.isVacuousPass()) {
                String oracleName = oracle.getClass().getSimpleName();
                String oracleMessage = oracle.getMessage();
                joiner.add(oracleMessage == null || oracleMessage.isBlank()
                        ? oracleName
                        : oracleName + " (" + oracleMessage + ")");
            }
        }

        String vacuousOracles = joiner.toString();
        if (!vacuousOracles.isBlank()) {
            logOracleApplicationStatus("Vacuous pass extended oracles: " + vacuousOracles);
            return;
        }

        logOracleApplicationStatus("No vacuous pass extended oracles.");
    }

    private void logOracleApplicationStatus(String message) {
        System.out.println(message);
        LogSerialiser.log(message + "\n", LogSerialiser.LogLevel.Info);
    }

}
