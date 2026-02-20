/***************************************************************************************************
 *
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
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

import org.testar.OutputStructure;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.RuntimeControlsProtocol;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.android.AndroidAppiumFramework;
import org.testar.oracles.Oracle;
import org.testar.settings.Settings;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Android logcat-backed oracle.
 * Clears logcat at the start of each sequence, 
 * dumps logcat each getVerdict, 
 * and returns SUSPICIOUS_LOG if any new log line matches LogOracleRegex. 
 */
public class AndroidLogcatOracle implements Oracle {

    private static final AtomicInteger SEQUENCE_COUNTER = new AtomicInteger(0);

    private final Settings settings;
    private final String regex;

    private int processedLineCount = 0;
    private int sequenceNumber = 0;
    private Path sequenceLogPath = null;

    public AndroidLogcatOracle(Settings settings) {
        this.settings = settings;
        this.regex = settings.get(ConfigTags.LogOracleRegex);
    }

    @Override
    public void initialize() {
        if (settings.get(ConfigTags.Mode) != RuntimeControlsProtocol.Modes.Generate) {
            sequenceLogPath = null;
            return;
        }

        sequenceNumber = SEQUENCE_COUNTER.incrementAndGet();
        processedLineCount = 0;

        AndroidAppiumFramework.clearLogcat();

        try {
            String logcatFileName = OutputStructure.logsOutputDir
                    + File.separator + OutputStructure.startInnerLoopDateString + "_"
                    + OutputStructure.executedSUTname + sequenceNumber + "_android_logcat.log";
            sequenceLogPath = Paths.get(logcatFileName);

            Files.writeString(sequenceLogPath,
                    "# TESTAR Android logcat (threadtime)\n\n", 
                    StandardCharsets.UTF_8, 
                    StandardOpenOption.CREATE, 
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception ignored) {
            sequenceLogPath = null;
        }
    }

    @Override
    public List<Verdict> getVerdicts(State state) {
        if (settings.get(ConfigTags.Mode) != RuntimeControlsProtocol.Modes.Generate) {
            return Collections.singletonList(Verdict.OK);
        }

        String packageName = AndroidAppiumFramework.getAppPackageFromCapabilitiesOrCurrent();
        String dump = AndroidAppiumFramework.dumpLogcatThreadtimeForPackage(packageName);

        if (dump == null || dump.isBlank()) {
            AndroidAppiumFramework.clearLogcat();
            return Collections.singletonList(Verdict.OK);
        }

        List<String> relevantLines = dump == null ? List.of() : List.of(dump.split("\\r?\\n"));

        if (relevantLines.size() < processedLineCount) {
            processedLineCount = 0;
        }

        List<String> newLines = relevantLines.subList(processedLineCount, relevantLines.size());
        processedLineCount = relevantLines.size();

        // Save the complete threadtime format in the debug log
        appendToSequenceLog(newLines);

        // Normalize the tag+message without threadtime for suspicious titles
        List<String> matches = detectRegexMatches(newLines, regex);
        if (matches.isEmpty()) {
            AndroidAppiumFramework.clearLogcat();
            return Collections.singletonList(Verdict.OK);
        }

        Set<String> uniqueSorted = new TreeSet<>(matches);
        StringBuilder info = new StringBuilder();
        info.append("Suspicious Android logcat line(s) detected ");
        info.append(String.join(" | ", uniqueSorted));

        AndroidAppiumFramework.clearLogcat();
        return Collections.singletonList(new Verdict(Verdict.Severity.SUSPICIOUS_LOG, info.toString().trim()));
    }

    private void appendToSequenceLog(List<String> lines) {
        if (sequenceLogPath == null || lines == null || lines.isEmpty()) {
            return;
        }
        try {
            Files.write(sequenceLogPath,
                    (String.join("\n", lines) + "\n").getBytes(StandardCharsets.UTF_8), 
                    StandardOpenOption.CREATE, 
                    StandardOpenOption.APPEND);
        } catch (Exception ignored) {
        }
    }

    private List<String> detectRegexMatches(List<String> lines, String regex) {
        List<String> matches = new ArrayList<>();
        if (lines == null || lines.isEmpty() || regex == null || regex.isEmpty()) {
            return matches;
        }

        Pattern p;
        try {
            p = Pattern.compile(regex);
        } catch (Exception ignored) {
            return matches;
        }

        for (String raw : lines) {
            String normalized = normalizeThreadtimeLine(raw);
            try {
                if (p.matcher(normalized).find()) {
                    matches.add(normalized);
                }
            } catch (Exception ignored) {
            }
        }
        return matches;
    }

    // logcat threadtime format:
    // 02-09 08:59:33.844 17550 17575 E Accessibility exception content...
    private static final Pattern THREADTIME_PATTERN = Pattern.compile(
            "^\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}\\.\\d{3}\\s+\\d+\\s+\\d+\\s+([VDIWEAF])\\s+([^:]+):\\s*(.*)$"
    );

    private static String normalizeThreadtimeLine(String line) {
        if (line == null) return "";
        line = line.trim();
        Matcher m = THREADTIME_PATTERN.matcher(line);
        if (!m.matches()) {
            return line.replaceAll("\\s+", " ");
        }

        String tag = m.group(2).trim();
        String msg = m.group(3).trim().replaceAll("\\s+", " ");

        return tag + ": " + msg;
    }

}
