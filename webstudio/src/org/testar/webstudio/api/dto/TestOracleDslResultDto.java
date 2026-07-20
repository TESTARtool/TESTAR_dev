/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

import java.util.List;

public final class TestOracleDslResultDto {

    private final boolean success;
    private final String message;
    private final String generatedJavaPath;
    private final List<TestOracleDslDiagnosticDto> diagnostics;

    public TestOracleDslResultDto(boolean success,
                                  String message,
                                  String generatedJavaPath,
                                  List<TestOracleDslDiagnosticDto> diagnostics) {
        this.success = success;
        this.message = message;
        this.generatedJavaPath = generatedJavaPath;
        this.diagnostics = diagnostics;
    }

    public boolean success() {
        return success;
    }

    public String message() {
        return message;
    }

    public String generatedJavaPath() {
        return generatedJavaPath;
    }

    public List<TestOracleDslDiagnosticDto> diagnostics() {
        return diagnostics;
    }
}
