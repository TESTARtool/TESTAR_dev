/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.service;

import org.testar.config.ConfigTags;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.oracle.Oracle;
import org.testar.plugin.process.SystemProcessHandling;
import org.testar.scriptless.RuntimeContext;

import java.util.ArrayList;
import java.util.List;

public class ScriptlessOracleComposer {

    public List<Verdict> composeVerdicts(RuntimeContext runtimeContext, SUT system, State state, List<Verdict> verdicts) {
        List<Verdict> composedVerdicts = new ArrayList<Verdict>(verdicts);

        if (runtimeContext.settings().get(ConfigTags.ProcessListenerEnabled, false)) {
            List<Verdict> processVerdicts = runtimeContext.processListenerOracle().getVerdicts(state);
            for (Verdict processVerdict : processVerdicts) {
                if (processVerdict.severity() == Verdict.Severity.SUSPICIOUS_PROCESS.getValue()) {
                    composedVerdicts.add(processVerdict);
                }
            }
        }

        if (runtimeContext.settings().get(ConfigTags.LogOracleEnabled, false)) {
            List<Verdict> logVerdicts = runtimeContext.logOracle().getVerdicts(state);
            for (Verdict logVerdict : logVerdicts) {
                if (logVerdict.severity() == Verdict.Severity.SUSPICIOUS_LOG.getValue()) {
                    composedVerdicts.add(logVerdict);
                }
            }
        }

        for (Oracle extendedOracle : runtimeContext.extendedOraclesList()) {
            List<Verdict> extendedVerdicts = extendedOracle.getVerdicts(state);
            if (extendedVerdicts != null) {
                composedVerdicts.addAll(extendedVerdicts);
            }
        }

        if (composedVerdicts.isEmpty()) {
            composedVerdicts.add(Verdict.OK);
        } else if (composedVerdicts.size() > 1) {
            composedVerdicts.removeIf(verdict -> verdict.severity() == Verdict.OK.severity());
        }

        state.set(Tags.OracleVerdicts, composedVerdicts);

        for (Verdict verdict : composedVerdicts) {
            if (verdict.severity() == Verdict.Severity.NOT_RESPONDING.getValue()) {
                SystemProcessHandling.killRunningProcesses(system, 100);
            }
        }

        return composedVerdicts;
    }
}
