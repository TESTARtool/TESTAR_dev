package org.testar.oracles.log;

import java.util.ArrayList;
import java.util.List;

import org.testar.monkey.ConfigTags;
import org.testar.monkey.Settings;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Verdict;
import org.testar.oracles.Oracle;

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
        String regex = settings.get(ConfigTags.LogOracleRegex);
        List<String> files = settings.get(ConfigTags.LogOracleFiles);
        List<String> commands = settings.get(ConfigTags.LogOracleCommands);
        LogErrorDetector detector = new RegexLogErrorDetector(settings.get(ConfigTags.LogOracleRegex));
        this.checker = new PlainLinebasedLogChecker(commands, files, detector);
    }

    public void initialize() {
        checker.initialRead();
    }

    public Verdict getVerdict(State state) {
        errorsList.addAll(checker.readAndCheck());
        if ( errorsList.size() == 0  ) {
            return new Verdict(Verdict.SEVERITY_OK, "OK");
        }
        else {
            System.out.println("LogOracle verdict ERROR" + String.join(";", errorsList) );
            return new Verdict(Verdict.SEVERITY_SUSPICIOUS_LOG, String.join(";", errorsList));
        }
    }

}