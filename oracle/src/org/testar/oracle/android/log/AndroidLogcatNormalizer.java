/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle.android.log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class AndroidLogcatNormalizer {

    private static final Pattern THREADTIME = Pattern.compile(
            "^\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}\\.\\d{3}\\s+\\d+\\s+\\d+\\s+([VDIWEAF])\\s+([^:]+):\\s*(.*)$");
    private static final Pattern ANDROID_PATH = Pattern.compile(
            "((?:/data/user/\\d+|/data/data|/storage/emulated/\\d+|/sdcard|/mnt/sdcard|/cache|/system|/vendor|/product|/apex)"
                    + "(?:/[^\\s:(),]+)+)");
    private static final Pattern OBJECT_ID = Pattern.compile("@(?i:[a-f0-9]{6,})");
    private static final Pattern NUMBER = Pattern.compile("(?<![A-Za-z])\\d+(?![A-Za-z])");
    private static final Pattern MEANINGFUL_NUMBER = Pattern.compile(
            "(?i)(?:status|code|count|attempts?|retries?|version|line|column|port|offset|length|size|index)\\s*(?:=|:)?\\s*$");
    private static final Pattern PACKAGE = Pattern.compile("[a-zA-Z_][\\w]*(?:\\.[a-zA-Z_][\\w]*)+");
    private static final Pattern UUID = Pattern.compile("(?i)[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");
    private static final Pattern HASH = Pattern.compile("(?i)[a-f0-9]{16,}");
    private static final Pattern TIMESTAMP = Pattern.compile("\\d{10,17}");
    private static final Pattern MIXED_ID = Pattern.compile("(?i)(?=[a-z0-9_-]{16,}$)(?=.*[a-z])(?=.*\\d)[a-z0-9_-]+");

    private AndroidLogcatNormalizer() {
    }

    static String stripThreadtime(String line) {
        if (line == null) {
            return "";
        }
        Matcher matcher = THREADTIME.matcher(line.trim());
        if (!matcher.matches()) {
            return line.trim().replaceAll("\\s+", " ");
        }
        return matcher.group(2).trim() + ": " + matcher.group(3).trim().replaceAll("\\s+", " ");
    }

    static String normalize(String message) {
        String withoutObjectIds = OBJECT_ID.matcher(message).replaceAll("@<id>");
        return normalizeNumbers(normalizePaths(withoutObjectIds));
    }

    private static String normalizePaths(String message) {
        Matcher matcher = ANDROID_PATH.matcher(message);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(result, Matcher.quoteReplacement(normalizePath(matcher.group(1))));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private static String normalizePath(String path) {
        StringBuilder normalized = new StringBuilder();
        boolean volatileDirectory = false;
        boolean userIndex = false;
        boolean packageSegment = false;
        String previousSegment = "";
        for (String segment : path.split("/")) {
            if (segment.isEmpty()) {
                normalized.append('/');
                continue;
            }
            boolean currentUserIndex = userIndex;
            boolean currentPackageSegment = packageSegment;
            userIndex = false;
            packageSegment = false;
            normalized.append(normalizePathSegment(segment, volatileDirectory, currentPackageSegment, currentUserIndex)).append('/');
            if ("user".equals(segment) || "emulated".equals(segment)) {
                userIndex = true;
            } else if (currentUserIndex) {
                packageSegment = true;
            }
            if ("data".equals(segment) && ("data".equals(previousSegment) || "Android".equals(previousSegment))) {
                packageSegment = true;
            }
            if ("cache".equals(segment) || "code_cache".equals(segment) || "tmp".equals(segment)) {
                volatileDirectory = true;
            }
            previousSegment = segment;
        }
        normalized.setLength(normalized.length() - 1);
        return normalized.toString();
    }

    private static String normalizePathSegment(String segment, boolean volatileDirectory, boolean packageSegment, boolean userIndex) {
        if (isStablePathSegment(segment)) {
            return segment;
        }
        if (packageSegment && PACKAGE.matcher(segment).matches()) {
            return "<package>";
        }
        if (UUID.matcher(segment).matches()) {
            return "<uuid>";
        }
        if (HASH.matcher(segment).matches()) {
            return "<id>";
        }
        if (TIMESTAMP.matcher(segment).matches()
                || (userIndex && NUMBER.matcher(segment).matches())
                || (volatileDirectory && NUMBER.matcher(segment).matches())) {
            return "<num>";
        }
        int extensionIndex = segment.lastIndexOf('.');
        if (extensionIndex > 0 && extensionIndex < segment.length() - 1) {
            String name = segment.substring(0, extensionIndex);
            if (isDynamicName(name)) {
                return "<file>" + segment.substring(extensionIndex);
            }
            return segment;
        }
        int underscoreIndex = segment.indexOf('_');
        if (underscoreIndex > 0 && isDynamicName(segment.substring(underscoreIndex + 1))) {
            return segment.substring(0, underscoreIndex) + "_<id>";
        }
        if (MIXED_ID.matcher(segment).matches()) {
            return "<id>";
        }
        return volatileDirectory ? "<path>" : segment;
    }

    private static boolean isStablePathSegment(String segment) {
        return switch (segment) {
            case "data", "user", "cache", "files", "shared_prefs", "databases", "lib", "storage",
                    "emulated", "sdcard", "mnt", "system", "vendor", "product", "apex" -> true;
            default -> false;
        };
    }

    private static boolean isDynamicName(String name) {
        return TIMESTAMP.matcher(name).matches()
                || (NUMBER.matcher(name).matches() && name.length() >= 4)
                || HASH.matcher(name).matches()
                || UUID.matcher(name).matches()
                || MIXED_ID.matcher(name).matches();
    }

    private static String normalizeNumbers(String message) {
        Matcher matcher = NUMBER.matcher(message);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String number = matcher.group();
            int start = matcher.start();
            if (isHttpFailureStatus(number)
                    || isInsidePath(message, start, matcher.end())
                    || isMeaningfulNumber(message, start)
                    || !isLikelyDynamicNumber(message, start, number)) {
                matcher.appendReplacement(result, number);
            } else {
                matcher.appendReplacement(result, "<num>");
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private static boolean isInsidePath(String message, int start, int end) {
        Matcher matcher = ANDROID_PATH.matcher(message);
        while (matcher.find()) {
            if (start >= matcher.start(1) && end <= matcher.end(1)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isMeaningfulNumber(String message, int start) {
        return MEANINGFUL_NUMBER.matcher(message.substring(Math.max(0, start - 32), start)).find();
    }

    private static boolean isLikelyDynamicNumber(String message, int start, String number) {
        return number.length() >= 4
                || (start > 0 && (message.charAt(start - 1) == '@' || message.charAt(start - 1) == ':'));
    }

    private static boolean isHttpFailureStatus(String number) {
        if (number.length() != 3) {
            return false;
        }
        int value = Integer.parseInt(number);
        return value >= 300 && value <= 599;
    }
}
