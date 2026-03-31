/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle.android.log;

import org.testar.config.TestarMode;
import org.testar.OutputStructure;
import org.testar.android.AndroidAppiumFramework;
import org.testar.config.ConfigTags;
import org.testar.core.state.State;
import org.testar.core.verdict.Verdict;
import org.testar.oracle.Oracle;
import org.testar.config.settings.Settings;

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
        if (settings.get(ConfigTags.Mode) != TestarMode.Generate) {
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
        if (settings.get(ConfigTags.Mode) != TestarMode.Generate) {
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
    private final Pattern THREADTIME_PATTERN = Pattern.compile(
            "^\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}\\.\\d{3}\\s+\\d+\\s+\\d+\\s+([VDIWEAF])\\s+([^:]+):\\s*(.*)$"
    );

    private String normalizeThreadtimeLine(String line) {
        if (line == null) return "";
        line = line.trim();
        Matcher m = THREADTIME_PATTERN.matcher(line);
        if (!m.matches()) {
            return normalizeNumbers(line.replaceAll("\\s+", " "));
        }

        String tag = m.group(2).trim();
        String msg = normalizeNumbers(m.group(3).trim().replaceAll("\\s+", " "));

        return tag + ": " + msg;
    }

    private String normalizeNumbers(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        Matcher matcher = Pattern.compile("\\d+").matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String num = matcher.group();
            if (isHttpFailureStatus(num)) {
                matcher.appendReplacement(sb, num);
            } else {
                matcher.appendReplacement(sb, "<num>");
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private boolean isHttpFailureStatus(String num) {
        if (num.length() != 3) {
            return false;
        }
        try {
            int value = Integer.parseInt(num);
            return value >= 300 && value <= 599;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
