/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.execution;

import org.testar.webstudio.api.dto.ExecutionStatusDto;

public final class CliExecutionAdapter implements ExecutionAdapter {

    @Override
    public ExecutionBackend backend() {
        return ExecutionBackend.CLI;
    }

    @Override
    public ExecutionStatusDto status() {
        return new ExecutionStatusDto("cli", "idle", "CLI execution adapter is available");
    }
}
