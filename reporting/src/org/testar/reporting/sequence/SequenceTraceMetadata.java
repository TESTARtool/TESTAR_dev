/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.reporting.sequence;

public final class SequenceTraceMetadata {

    private final String formatVersion;
    private final String sequenceId;
    private final String target;
    private final String startedAtUtc;
    private final String finishedAtUtc;

    public SequenceTraceMetadata(String formatVersion,
                                 String sequenceId,
                                 String target,
                                 String startedAtUtc,
                                 String finishedAtUtc) {
        this.formatVersion = valueOrEmpty(formatVersion);
        this.sequenceId = valueOrEmpty(sequenceId);
        this.target = valueOrEmpty(target);
        this.startedAtUtc = valueOrEmpty(startedAtUtc);
        this.finishedAtUtc = valueOrEmpty(finishedAtUtc);
    }

    public String getFormatVersion() {
        return formatVersion;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public String getTarget() {
        return target;
    }

    public String getStartedAtUtc() {
        return startedAtUtc;
    }

    public String getFinishedAtUtc() {
        return finishedAtUtc;
    }

    public SequenceTraceMetadata finishAt(String finishedAtUtcValue) {
        return new SequenceTraceMetadata(
                formatVersion,
                sequenceId,
                target,
                startedAtUtc,
                finishedAtUtcValue
        );
    }

    private static String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
