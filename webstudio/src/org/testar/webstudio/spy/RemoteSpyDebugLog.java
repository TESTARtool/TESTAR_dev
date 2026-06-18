/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.spy;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public final class RemoteSpyDebugLog {

    private final Path logPath;

    public RemoteSpyDebugLog(Path logPath) {
        this.logPath = logPath.toAbsolutePath().normalize();
    }

    public void log(String message) {
        append(message, null);
    }

    public void log(String message, Throwable throwable) {
        append(message, throwable);
    }

    private synchronized void append(String message, Throwable throwable) {
        try {
            Files.createDirectories(logPath.getParent());
            StringBuilder builder = new StringBuilder();
            builder.append(LocalDateTime.now()).append(" ").append(message).append(System.lineSeparator());
            if (throwable != null) {
                StringWriter stringWriter = new StringWriter();
                throwable.printStackTrace(new PrintWriter(stringWriter));
                builder.append(stringWriter).append(System.lineSeparator());
            }
            Files.writeString(
                logPath,
                builder.toString(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
        } catch (IOException ignored) {
            // Avoid recursive failures while debugging Spy Mode.
        }
    }
}
