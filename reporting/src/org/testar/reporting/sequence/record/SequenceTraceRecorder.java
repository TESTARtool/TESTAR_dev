/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.reporting.sequence.record;

import java.util.ArrayList;
import java.util.List;

import org.testar.reporting.sequence.SequenceTrace;
import org.testar.reporting.sequence.SequenceTraceMetadata;
import org.testar.reporting.sequence.SequenceTraceStep;

public final class SequenceTraceRecorder {

    private SequenceTraceMetadata metadata;
    private final List<SequenceTraceStep> steps;

    public SequenceTraceRecorder(SequenceTraceMetadata metadata) {
        this.metadata = metadata;
        this.steps = new ArrayList<>();
    }

    public void recordStep(SequenceTraceStep step) {
        steps.add(step);
    }

    public void finish(String finishedAtUtc) {
        metadata = metadata.finishAt(finishedAtUtc);
    }

    public SequenceTrace snapshot() {
        return new SequenceTrace(metadata, steps);
    }
}
