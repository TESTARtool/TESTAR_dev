/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.capability;

import org.testar.OutputStructure;
import org.testar.config.ConfigTags;
import org.testar.config.TestarMode;
import org.testar.core.alayer.Pen;
import org.testar.core.serialisation.LogSerialiser;
import org.testar.core.serialisation.ScreenshotSerialiser;
import org.testar.core.state.SUT;
import org.testar.core.util.Util;
import org.testar.core.verdict.Verdict;
import org.testar.oracle.Oracle;
import org.testar.oracle.OracleSelection;
import org.testar.oracle.generic.log.LogOracle;
import org.testar.oracle.windows.log.ProcessListenerOracle;
import org.testar.plugin.NativeLinker;
import org.testar.plugin.reporting.SessionReportingManager;
import org.testar.reporting.ReportManager;
import org.testar.scriptless.ComposedProtocol;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

/**
 * Shared sequence lifecycle support for protocol implementations.
 */
public final class SequenceLifecycleCapability {

    public void startTestSequence(ComposedProtocol protocol) {
        protocol.runtimeContext().setActionCount(1);

        protocol.runtimeContext().setCanvas(NativeLinker.getNativeCanvas(Pen.PEN_DEFAULT));

        if (protocol.settings().get(ConfigTags.Mode) != TestarMode.Spy) {
            SessionReportingManager sessionReportingManager = protocol.runtimeServices().sessionReportingManager();
            sessionReportingManager.bindReporting(new ReportManager(protocol.settings()));
        }

        if (protocol.isLogOracleEnabled()) {
            Oracle logOracle = new LogOracle(protocol.settings());
            logOracle.initialize();
            protocol.runtimeContext().setLogOracle(logOracle);
        }

        protocol.runtimeContext().setExtendedOraclesList(OracleSelection.loadExtendedOracles(
                protocol.settings().get(ConfigTags.ExtendedOracles, "")
        ));

        for (Oracle oracle : protocol.runtimeContext().extendedOraclesList()) {
            oracle.initialize();
        }
    }

    public void startSystem(ComposedProtocol protocol, SUT system) {
        LogSerialiser.finish();
        LogSerialiser.exit();
        LogSerialiser.log(Util.dateString(ComposedProtocol.DATE_FORMAT) + " Starting SUT ...\n", LogSerialiser.LogLevel.Info);
        LogSerialiser.log("SUT is running!\n", LogSerialiser.LogLevel.Debug);
        LogSerialiser.log("Building canvas...\n", LogSerialiser.LogLevel.Debug);

        if (protocol.isProcessListenerOracleEnabled()) {
            Oracle processListenerOracle = new ProcessListenerOracle(system, protocol.settings());
            processListenerOracle.initialize();
            protocol.runtimeContext().setProcessListenerOracle(processListenerOracle);
        }
    }

    public void finishTestSequence(ComposedProtocol protocol, List<Verdict> verdicts) {
        SessionReportingManager sessionReportingManager = protocol.runtimeServices().sessionReportingManager();

        sessionReportingManager.addTestVerdicts(verdicts);
        String statusInfo = buildStatusInfo(verdicts);

        protocol.runtimeContext().setGeneratedSequence(
                OutputStructure.outerLoopOutputDir + File.separator + protocol.runtimeContext().generatedSequence()
        );

        ComposedProtocol.INDEXLOG.info(
                OutputStructure.executedSUTname
                        + " " + protocol.settings().get(ConfigTags.Mode, protocol.mode())
                        + " " + protocol.runtimeContext().generatedSequence()
                        + " " + statusInfo
        );

        if (protocol.mode() == TestarMode.Generate) {
            protocol.runtimeContext().verdictProcessing().storeNewVerdicts(verdicts);
        }

        sessionReportingManager.finishReport();

		LogSerialiser.log("Releasing canvas...\n", LogSerialiser.LogLevel.Debug);
		protocol.runtimeContext().canvas().release();
		ScreenshotSerialiser.exit();
		LogSerialiser.flush();
		LogSerialiser.finish();
		LogSerialiser.exit();
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
}
