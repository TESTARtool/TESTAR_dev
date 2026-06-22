/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

public final class WorkspaceJavaCompileDiagnosticDto {

    private final String fileName;
    private final String relativePath;
    private final long line;
    private final long column;
    private final String severity;
    private final String message;

    public WorkspaceJavaCompileDiagnosticDto(String fileName,
                                             String relativePath,
                                             long line,
                                             long column,
                                             String severity,
                                             String message) {
        this.fileName = fileName;
        this.relativePath = relativePath;
        this.line = line;
        this.column = column;
        this.severity = severity;
        this.message = message;
    }

    public String fileName() {
        return fileName;
    }

    public String relativePath() {
        return relativePath;
    }

    public long line() {
        return line;
    }

    public long column() {
        return column;
    }

    public String severity() {
        return severity;
    }

    public String message() {
        return message;
    }
}
