/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.statemodel.analysis;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

import org.testar.core.util.RuntimePathsUtil;

public final class StateModelDebugLog {

    private static final String DEBUG_LOG_PROPERTY = "testar.analysis.debugLog";
    private static volatile boolean installed;

    private StateModelDebugLog() {
    }

    public static synchronized void install(Path debugLogPath) {
        if (debugLogPath != null) {
            System.setProperty(DEBUG_LOG_PROPERTY, debugLogPath.toAbsolutePath().normalize().toString());
        }

        if (installed) {
            return;
        }

        try {
            Path resolvedPath = resolveDebugLogPath();
            Files.createDirectories(resolvedPath.getParent());
            OutputStream fileOutputStream = Files.newOutputStream(
                resolvedPath,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
            PrintStream debugPrintStream = new PrintStream(fileOutputStream, true, StandardCharsets.UTF_8);
            System.setOut(debugPrintStream);
            System.setErr(debugPrintStream);
            installed = true;
            log("Installed state model debug log at " + resolvedPath);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to initialize state model debug log", exception);
        }
    }

    public static void log(String message) {
        append(message, null);
    }

    public static void log(String message, Throwable throwable) {
        append(message, throwable);
    }

    private static synchronized void append(String message, Throwable throwable) {
        try {
            Path debugLogPath = resolveDebugLogPath();
            Files.createDirectories(debugLogPath.getParent());
            StringBuilder builder = new StringBuilder();
            builder.append(LocalDateTime.now()).append(" ").append(message).append(System.lineSeparator());
            if (throwable != null) {
                StringWriter stringWriter = new StringWriter();
                throwable.printStackTrace(new PrintWriter(stringWriter));
                builder.append(stringWriter).append(System.lineSeparator());
            }
            Files.writeString(
                debugLogPath,
                builder.toString(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
        } catch (IOException ignored) {
            // Avoid recursive logging failures.
        }
    }

    private static Path resolveDebugLogPath() {
        String configuredPath = System.getProperty(DEBUG_LOG_PROPERTY, "").trim();
        if (!configuredPath.isEmpty()) {
            return Path.of(configuredPath).toAbsolutePath().normalize();
        }

        return RuntimePathsUtil.resolveTestarHome().resolve("debug.log").toAbsolutePath().normalize();
    }
}
