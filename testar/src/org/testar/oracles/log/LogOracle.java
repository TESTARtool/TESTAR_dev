package org.testar.oracles.log;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
 *  LogOracleLogLines: whether to log all log lines that are processed by the Oracle
 *  LogOracleRestartSequence: whether to reinitialize the LogOracle at the start of each sequence. This is
 *  useful if SUT log data is deleted after each sequence.
 */

public class LogOracle implements Oracle {
    LogChecker checker;
    boolean restartEachSequence = false;

    public LogOracle (Settings settings) {
        String regex = settings.get(ConfigTags.LogOracleRegex);
        List<String> files = settings.get(ConfigTags.LogOracleFiles);
        List<String> commands = settings.get(ConfigTags.LogOracleCommands);
        LogErrorDetector detector = new RegexLogErrorDetector(settings.get(ConfigTags.LogOracleRegex));
        this.checker = new PlainLinebasedLogChecker(commands, files, detector, settings.get(ConfigTags.LogOracleLogLines));
        restartEachSequence = settings.get(ConfigTags.LogOracleRestartSequence);
    }

    public void initialize() {
        checker.initialRead();
    }

    public void notifyBeginSequence() {
        if ( restartEachSequence )  {
            this.initialize();
        }
    }

    public Verdict getVerdict(State state) {
        List<String> errors = checker.readAndCheck();
        if ( errors.size() == 0  ) {
            return new Verdict(Verdict.SEVERITY_OK, "OK");
        }
        else {
            LogManager.getLogger().info("LogOracle verdict ERROR" + String.join(";", errors) );
            return new Verdict(Verdict.SEVERITY_SUSPICIOUS_LOG, String.join(";", errors));
        }
    }

}