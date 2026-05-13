/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.reporting.sequence;

import org.testar.reporting.sequence.record.SemanticActionRecord;
import org.testar.reporting.sequence.record.SemanticStateRecord;

public final class SequenceTraceStep {

    private final int stepNumber;
    private final String timestampUtc;
    private final SemanticStateRecord state;
    private final SemanticActionRecord action;

    public SequenceTraceStep(int stepNumber,
                             String timestampUtc,
                             SemanticStateRecord state,
                             SemanticActionRecord action) {
        this.stepNumber = stepNumber;
        this.timestampUtc = timestampUtc == null ? "" : timestampUtc;
        this.state = state;
        this.action = action;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public String getTimestampUtc() {
        return timestampUtc;
    }

    public SemanticStateRecord getState() {
        return state;
    }

    public SemanticActionRecord getAction() {
        return action;
    }
}
