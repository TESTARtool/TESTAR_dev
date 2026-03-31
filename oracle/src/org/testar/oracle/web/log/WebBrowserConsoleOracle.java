/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle.web.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.testar.config.ConfigTags;
import org.testar.core.state.State;
import org.testar.core.verdict.Verdict;
import org.testar.oracle.Oracle;
import org.testar.webdriver.state.WdDriver;
import org.testar.config.settings.Settings;

/**
 * Web browser console oracle (WebDriver).
 * Scans browser logs for error/warning lines that match regular expression patterns.
 */
public class WebBrowserConsoleOracle implements Oracle {

    private final boolean errorEnabled;
    private final boolean warningEnabled;
    private final String errorPatternText;
    private final String warningPatternText;

    public WebBrowserConsoleOracle(Settings settings) {
        this.errorEnabled = settings.get(ConfigTags.WebConsoleErrorOracle, false);
        this.warningEnabled = settings.get(ConfigTags.WebConsoleWarningOracle, false);
        this.errorPatternText = settings.get(ConfigTags.WebConsoleErrorPattern, "");
        this.warningPatternText = settings.get(ConfigTags.WebConsoleWarningPattern, "");
    }

    @Override
    public void initialize() {
        // Nothing extra to initialize
    }

    @Override
    public List<Verdict> getVerdicts(State state) {
        List<Verdict> verdicts = new ArrayList<>();

        LogEntries logEntries = WdDriver.getBrowserLogs();

        // If Web Console Error Oracle is enabled and we have some pattern to match
        if (errorEnabled && !errorPatternText.isEmpty()) {
            // Load the web console error pattern
            Pattern errorPattern = Pattern.compile(errorPatternText, Pattern.UNICODE_CHARACTER_CLASS);
            // Check Severe messages in the WebDriver logs
            for (LogEntry logEntry : logEntries) {
                if (logEntry.getLevel().equals(Level.SEVERE)) {
                    // Check if the severe error message matches with the web console error pattern
                    String consoleErrorMsg = logEntry.getMessage();
                    Matcher matcherError = errorPattern.matcher(consoleErrorMsg);
                    if (matcherError.matches()) {
                        verdicts.add(new Verdict(Verdict.Severity.SUSPICIOUS_LOG, "Web Browser Console Error: " + consoleErrorMsg));
                    }
                }
            }
        }

        // If Web Console Warning Oracle is enabled and we have some pattern to match
        if (warningEnabled && !warningPatternText.isEmpty()) {
            // Load the web console warning pattern
            Pattern warningPattern = Pattern.compile(warningPatternText, Pattern.UNICODE_CHARACTER_CLASS);
            // Check Warning messages in the WebDriver logs
            for (LogEntry logEntry : logEntries) {
                if (logEntry.getLevel().equals(Level.WARNING)) {
                    // Check if the warning message matches with the web console error pattern
                    String consoleWarningMsg = logEntry.getMessage();
                    Matcher matcherWarning = warningPattern.matcher(consoleWarningMsg);
                    if (matcherWarning.matches()) {
                        verdicts.add(new Verdict(Verdict.Severity.SUSPICIOUS_LOG, "Web Browser Console Warning: " + consoleWarningMsg));
                    }
                }
            }
        }

        if (verdicts.isEmpty()) {
            return Collections.singletonList(Verdict.OK);
        }
        return verdicts;
    }
}
