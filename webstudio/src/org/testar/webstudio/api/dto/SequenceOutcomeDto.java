/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

public final class SequenceOutcomeDto {

    private final int sequenceNumber;
    private final String status;
    private final String outputPath;
    private final String label;

    public SequenceOutcomeDto(int sequenceNumber, String status) {
        this(sequenceNumber, status, null, null);
    }

    public SequenceOutcomeDto(int sequenceNumber, String status, String outputPath) {
        this(sequenceNumber, status, outputPath, null);
    }

    public SequenceOutcomeDto(int sequenceNumber, String status, String outputPath, String label) {
        this.sequenceNumber = sequenceNumber;
        this.status = status;
        this.outputPath = outputPath;
        this.label = label;
    }

    public int sequenceNumber() {
        return sequenceNumber;
    }

    public String status() {
        return status;
    }

    public String outputPath() {
        return outputPath;
    }

    public String label() {
        return label;
    }
}
