/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.execution;

import java.nio.file.Path;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class ResultVerdictStatus {

    private static final Pattern VERDICT_FILE_PATTERN = Pattern.compile("_V\\d+_([^.]+)\\.HTML?$");

    private ResultVerdictStatus() { }

    static String forResultFile(Path path) {
        return forResultFileName(path.getFileName().toString());
    }

    static String forResultFileName(String fileName) {
        String upperCaseFileName = fileName == null ? "" : fileName.toUpperCase(Locale.ROOT);
        Matcher verdictMatcher = VERDICT_FILE_PATTERN.matcher(upperCaseFileName);
        if (verdictMatcher.find()) {
            return isSuccessfulVerdict(verdictMatcher.group(1)) ? "ok" : "failed";
        }

        return "ok";
    }

    private static boolean isSuccessfulVerdict(String verdictName) {
        return "OK".equals(verdictName) || "LLM_COMPLETE".equals(verdictName);
    }
}
