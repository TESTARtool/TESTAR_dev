/***************************************************************************************************
 *
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
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
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.oracles.Oracle;
import org.testar.settings.Settings;

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
