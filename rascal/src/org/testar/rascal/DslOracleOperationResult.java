/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.rascal;

import java.util.List;

public final class DslOracleOperationResult {

    private final boolean success;
    private final String message;
    private final String generatedJavaSource;
    private final List<DslOracleDiagnostic> diagnostics;

    public DslOracleOperationResult(boolean success,
                                    String message,
                                    String generatedJavaSource,
                                    List<DslOracleDiagnostic> diagnostics) {
        this.success = success;
        this.message = message;
        this.generatedJavaSource = generatedJavaSource;
        this.diagnostics = diagnostics;
    }

    public boolean success() {
        return success;
    }

    public String message() {
        return message;
    }

    public String generatedJavaSource() {
        return generatedJavaSource;
    }

    public List<DslOracleDiagnostic> diagnostics() {
        return diagnostics;
    }
}
