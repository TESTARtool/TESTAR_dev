/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

public final class SequenceVerdictDto {

    private final String label;
    private final String status;
    private final String outputPath;

    public SequenceVerdictDto(String label, String status, String outputPath) {
        this.label = label;
        this.status = status;
        this.outputPath = outputPath;
    }

    public String label() {
        return label;
    }

    public String status() {
        return status;
    }

    public String outputPath() {
        return outputPath;
    }
}
