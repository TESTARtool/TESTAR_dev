/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

public final class TestOracleDslDiagnosticDto {

    private final String severity;
    private final int line;
    private final int column;
    private final int endLine;
    private final int endColumn;
    private final String message;

    public TestOracleDslDiagnosticDto(String severity, int line, int column, String message) {
        this(severity, line, column, line, column > 0 ? column + 1 : -1, message);
    }

    public TestOracleDslDiagnosticDto(String severity, int line, int column, int endLine, int endColumn, String message) {
        this.severity = severity;
        this.line = line;
        this.column = column;
        this.endLine = endLine;
        this.endColumn = endColumn;
        this.message = message;
    }

    public String severity() {
        return severity;
    }

    public int line() {
        return line;
    }

    public int column() {
        return column;
    }

    public int endLine() {
        return endLine;
    }

    public int endColumn() {
        return endColumn;
    }

    public String message() {
        return message;
    }
}
