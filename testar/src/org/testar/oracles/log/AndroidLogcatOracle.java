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

    // Pattern list for dynamic normalization
    // 02-09 08:59:33.844 17550 17575 E Accessibility exception content...
    private static final Pattern THREADTIME_PATTERN = Pattern.compile(
            "^\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}\\.\\d{3}\\s+\\d+\\s+\\d+\\s+([VDIWEAF])\\s+([^:]+):\\s*(.*)$"
    );
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");
    private static final Pattern NORMALIZABLE_NUMBER_PATTERN = Pattern.compile("(?<![A-Za-z])\\d+(?![A-Za-z])");
    private static final Pattern MEANINGFUL_NUMBER_CONTEXT_PATTERN = Pattern.compile(
            "(?i)(?:status|code|count|attempts?|retries?|version|line|column|port|offset|length|size|index)\\s*(?:=|:)?\\s*$"
    );
    private static final Pattern JAVA_OBJECT_IDENTITY_PATTERN = Pattern.compile("@(?i:[a-f0-9]{6,})");
    private static final Pattern ANDROID_ABSOLUTE_PATH_PATTERN = Pattern.compile(
            "((?:/data/user/\\d+|/data/data|/storage/emulated/\\d+|/sdcard|/mnt/sdcard|/cache|/system|/vendor|/product|/apex)"
                    + "(?:/[^\\s:(),]+)+)"
    );
    private static final Pattern PACKAGE_SEGMENT_PATTERN = Pattern.compile(
            "[a-zA-Z_][\\w]*(?:\\.[a-zA-Z_][\\w]*)+"
    );
    private static final Pattern HEX_OR_HASH_SEGMENT_PATTERN = Pattern.compile(
            "(?i)[a-f0-9]{16,}"
    );
    private static final Pattern UUID_SEGMENT_PATTERN = Pattern.compile(
            "(?i)[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}"
    );
    private static final Pattern TIMESTAMP_SEGMENT_PATTERN = Pattern.compile("\\d{10,17}");
    private static final Pattern MIXED_ENTROPY_SEGMENT_PATTERN = Pattern.compile(
            "(?i)(?=[a-z0-9_-]{16,}$)(?=.*[a-z])(?=.*\\d)[a-z0-9_-]+"
    );

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
            String unnormalizedMessage = stripThreadtimeLine(raw);
            try {
                if (p.matcher(unnormalizedMessage).find()) {
                    matches.add(normalizeThreadtimeLine(unnormalizedMessage));
                }
            } catch (Exception ignored) {
            }
        }
        return matches;
    }

    private String normalizeThreadtimeLine(String line) {
        String message = stripThreadtimeLine(line);
        return normalizeNumbers(
                normalizeDynamicObjectIdentities(
                        normalizeAndroidPaths(message)
                )
        );
    }

    private String stripThreadtimeLine(String line) {
        if (line == null) {
            return "";
        }
        line = line.trim();
        Matcher m = THREADTIME_PATTERN.matcher(line);
        if (!m.matches()) {
            return line.replaceAll("\\s+", " ");
        }

        String tag = m.group(2).trim();
        return tag + ": " + m.group(3).trim().replaceAll("\\s+", " ");
    }

    private String normalizeDynamicObjectIdentities(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        return JAVA_OBJECT_IDENTITY_PATTERN.matcher(text).replaceAll("@<id>");
    }

    private String normalizeAndroidPaths(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        Matcher matcher = ANDROID_ABSOLUTE_PATH_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String normalizedPath = normalizeAndroidPath(matcher.group(1));
            matcher.appendReplacement(sb, Matcher.quoteReplacement(normalizedPath));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String normalizeAndroidPath(String path) {
        String[] segments = path.split("/");
        StringBuilder normalized = new StringBuilder();
        boolean withinVolatileDirectory = false;
        boolean userIndexExpected = false;
        boolean packageSegmentExpected = false;
        String previousSegment = "";

        for (String segment : segments) {
            if (segment.isEmpty()) {
                normalized.append("/");
                continue;
            }

            boolean currentPackageSegment = packageSegmentExpected;
            boolean currentUserIndex = userIndexExpected;
            packageSegmentExpected = false;
            userIndexExpected = false;

            normalized.append(normalizePathSegment(
                    segment,
                    withinVolatileDirectory,
                    currentPackageSegment,
                    currentUserIndex
            )).append("/");

            if ("user".equals(segment) || "emulated".equals(segment)) {
                userIndexExpected = true;
            } else if (currentUserIndex) {
                packageSegmentExpected = true;
            }

            if ("data".equals(segment)
                    && ("data".equals(previousSegment) || "Android".equals(previousSegment))) {
                packageSegmentExpected = true;
            }

            if (isVolatilePathSegment(segment)) {
                withinVolatileDirectory = true;
            }
            previousSegment = segment;
        }

        if (normalized.length() > 1 && normalized.charAt(normalized.length() - 1) == '/') {
            normalized.setLength(normalized.length() - 1);
        }

        return normalized.toString();
    }

    private String normalizePathSegment(
            String segment,
            boolean withinVolatileDirectory,
            boolean packageSegment,
            boolean userIndex
    ) {
        if (segment == null || segment.isEmpty()) {
            return "";
        }

        if (isStablePathSegment(segment)) {
            return segment;
        }

        if (packageSegment && PACKAGE_SEGMENT_PATTERN.matcher(segment).matches()) {
            return "<package>";
        }

        if (UUID_SEGMENT_PATTERN.matcher(segment).matches()) {
            return "<uuid>";
        }

        if (HEX_OR_HASH_SEGMENT_PATTERN.matcher(segment).matches()) {
            return "<id>";
        }

        if (TIMESTAMP_SEGMENT_PATTERN.matcher(segment).matches()
                || (userIndex && NUMBER_PATTERN.matcher(segment).matches())
                || (withinVolatileDirectory && NUMBER_PATTERN.matcher(segment).matches())) {
            return "<num>";
        }

        int dotIndex = segment.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < segment.length() - 1) {
            String name = segment.substring(0, dotIndex);
            String extension = segment.substring(dotIndex);
            if (isDynamicFileName(name)) {
                return "<file>" + extension;
            }
            return segment;
        }

        int underscoreIndex = segment.indexOf('_');
        if (underscoreIndex > 0 && underscoreIndex < segment.length() - 1) {
            String prefix = segment.substring(0, underscoreIndex);
            String suffix = segment.substring(underscoreIndex + 1);
            if (isDynamicFileName(suffix)) {
                return prefix + "_<id>";
            }
        }

        if (isMixedEntropySegment(segment)) {
            return "<id>";
        }

        return withinVolatileDirectory ? "<path>" : segment;
    }

    private boolean isVolatilePathSegment(String segment) {
        switch (segment) {
            case "cache":
            case "code_cache":
            case "tmp":
                return true;
            default:
                return false;
        }
    }

    private boolean isStablePathSegment(String segment) {
        switch (segment) {
            case "data":
            case "user":
            case "cache":
            case "files":
            case "shared_prefs":
            case "databases":
            case "lib":
            case "storage":
            case "emulated":
            case "sdcard":
            case "mnt":
            case "system":
            case "vendor":
            case "product":
            case "apex":
                return true;
            default:
                return false;
        }
    }

    private boolean isDynamicFileName(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }

        return TIMESTAMP_SEGMENT_PATTERN.matcher(value).matches()
                || (NUMBER_PATTERN.matcher(value).matches() && value.length() >= 4)
                || HEX_OR_HASH_SEGMENT_PATTERN.matcher(value).matches()
                || UUID_SEGMENT_PATTERN.matcher(value).matches()
                || isMixedEntropySegment(value);
    }

    private boolean isMixedEntropySegment(String value) {
        return value != null && MIXED_ENTROPY_SEGMENT_PATTERN.matcher(value).matches();
    }

    private String normalizeNumbers(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        Matcher matcher = NORMALIZABLE_NUMBER_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String num = matcher.group();
            if (isHttpFailureStatus(num)
                    || isWithinAndroidPath(text, matcher.start(), matcher.end())
                    || isMeaningfulNumber(text, matcher.start())
                    || !isHighConfidenceDynamicNumber(text, matcher.start(), num)) {
                matcher.appendReplacement(sb, num);
            } else {
                matcher.appendReplacement(sb, "<num>");
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private boolean isMeaningfulNumber(String text, int start) {
        int contextStart = Math.max(0, start - 32);
        String context = text.substring(contextStart, start);
        return MEANINGFUL_NUMBER_CONTEXT_PATTERN.matcher(context).find();
    }

    private boolean isWithinAndroidPath(String text, int start, int end) {
        Matcher pathMatcher = ANDROID_ABSOLUTE_PATH_PATTERN.matcher(text);
        while (pathMatcher.find()) {
            if (start >= pathMatcher.start(1) && end <= pathMatcher.end(1)) {
                return true;
            }
        }
        return false;
    }

    private boolean isHighConfidenceDynamicNumber(String text, int start, String number) {
        if (number.length() >= 4) {
            return true;
        }

        return start > 0 && (text.charAt(start - 1) == '@' || text.charAt(start - 1) == ':');
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
