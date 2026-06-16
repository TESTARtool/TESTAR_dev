/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

public final class ExecutionStatusDto {

    private final String backend;
    private final String status;
    private final String message;
    private final String workspace;
    private final String mode;
    private final String consoleOutput;
    private final Long startedAtEpochMillis;
    private final Integer plannedSequenceCount;
    private final java.util.List<SequenceOutcomeDto> sequenceOutcomes;

    public ExecutionStatusDto(String backend, String status, String message) {
        this(backend, status, message, null, null, "", null, null, java.util.List.of());
    }

    public ExecutionStatusDto(
        String backend,
        String status,
        String message,
        String workspace,
        String mode,
        String consoleOutput,
        Long startedAtEpochMillis,
        Integer plannedSequenceCount,
        java.util.List<SequenceOutcomeDto> sequenceOutcomes
    ) {
        this.backend = backend;
        this.status = status;
        this.message = message;
        this.workspace = workspace;
        this.mode = mode;
        this.consoleOutput = consoleOutput;
        this.startedAtEpochMillis = startedAtEpochMillis;
        this.plannedSequenceCount = plannedSequenceCount;
        this.sequenceOutcomes = sequenceOutcomes;
    }

    public String backend() {
        return backend;
    }

    public String status() {
        return status;
    }

    public String message() {
        return message;
    }

    public String workspace() {
        return workspace;
    }

    public String mode() {
        return mode;
    }

    public String consoleOutput() {
        return consoleOutput;
    }

    public Long startedAtEpochMillis() {
        return startedAtEpochMillis;
    }

    public Integer plannedSequenceCount() {
        return plannedSequenceCount;
    }

    public java.util.List<SequenceOutcomeDto> sequenceOutcomes() {
        return sequenceOutcomes;
    }
}
