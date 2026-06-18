/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

public final class CliManualCommandRequestDto {

    private String commandLine;

    public String commandLine() {
        if (commandLine == null) {
            return "";
        }

        return commandLine.trim();
    }
}
