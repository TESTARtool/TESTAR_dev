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

        List<String> newLines = List.of(dump.split("\\r?\\n"));

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
            String message = AndroidLogcatNormalizer.stripThreadtime(raw);
            if (p.matcher(message).find()) {
                matches.add(AndroidLogcatNormalizer.normalize(message));
            }
        }
        return matches;
    }
}
