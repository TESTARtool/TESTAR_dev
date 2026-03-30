/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.cli;

import java.util.Collections;
import java.util.List;

import org.testar.core.Assert;

final class CliResponse {

    private final int exitCode;
    private final List<String> lines;

    CliResponse(int exitCode, List<String> lines) {
        this.exitCode = exitCode;
        this.lines = Collections.unmodifiableList(Assert.notNull(lines));
    }

    int getExitCode() {
        return exitCode;
    }

    List<String> getLines() {
        return lines;
    }
}
