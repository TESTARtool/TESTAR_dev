/***************************************************************************************************
 *
 * Copyright (c) 2022 - 2023 Open Universiteit - www.ou.nl
 * Copyright (c) 2022 - 2023 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.oracles.log;

import java.util.ArrayList;
import java.util.List;

import org.testar.monkey.ConfigTags;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Verdict;
import org.testar.oracles.Oracle;
import org.testar.settings.Settings;

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

    public Verdict getVerdict(State state) {
        errorsList.addAll(checker.readAndCheck());
        if ( errorsList.size() == 0  ) {
            return Verdict.OK;
        }
        else {
            return new Verdict(Verdict.Severity.SUSPICIOUS_LOG, String.join(";", errorsList));
        }
    }

}