/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2022-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2022-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testar.config.ConfigTags;
import org.testar.core.state.State;
import org.testar.core.verdict.Verdict;
import org.testar.oracle.Oracle;
import org.testar.config.settings.Settings;

/**
 *  This oracle checks messages in a file or command output for suspicious messages.
 *  It is configured in the protocol settings:
 *  LogOracleRegex: a regular expression that matches suspicious messages
 *  LogOracleFiles: a list of absolute paths of log files to monitor
 *  LogOracleCommands: a list of commands of which standard output should be monitored.
 *
 */
public class LogOracle implements Oracle {
    LogChecker checker;
    List<String> errorsList = new ArrayList<>();

    public LogOracle (Settings settings) {
        List<String> files = settings.get(ConfigTags.LogOracleFiles);
        List<String> commands = settings.get(ConfigTags.LogOracleCommands);
        LogErrorDetector detector = new RegexLogErrorDetector(settings.get(ConfigTags.LogOracleRegex));
        checker = new PlainLinebasedLogChecker(commands, files, detector);
    }

    public void initialize() {
        checker.initialRead();
    }

    public List<Verdict> getVerdicts(State state) {
        errorsList.addAll(checker.readAndCheck());
        if ( errorsList.size() == 0  ) {
            return Collections.singletonList(Verdict.OK);
        }
        else {
            return Collections.singletonList(new Verdict(Verdict.Severity.SUSPICIOUS_LOG, String.join(";", errorsList)));
        }
    }

}