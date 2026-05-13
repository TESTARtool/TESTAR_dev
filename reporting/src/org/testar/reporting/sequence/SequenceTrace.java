/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.reporting.sequence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SequenceTrace {

    private final SequenceTraceMetadata metadata;
    private final List<SequenceTraceStep> steps;

    public SequenceTrace(SequenceTraceMetadata metadata, List<SequenceTraceStep> steps) {
        this.metadata = metadata;
        this.steps = Collections.unmodifiableList(new ArrayList<>(steps == null ? Collections.emptyList() : steps));
    }

    public SequenceTraceMetadata getMetadata() {
        return metadata;
    }

    public List<SequenceTraceStep> getSteps() {
        return steps;
    }
}
