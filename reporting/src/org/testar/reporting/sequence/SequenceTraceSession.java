/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.reporting.sequence;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;

import org.testar.core.action.Action;
import org.testar.core.state.State;
import org.testar.reporting.sequence.record.DefaultSemanticActionExtractor;
import org.testar.reporting.sequence.record.DefaultSemanticStateExtractor;
import org.testar.reporting.sequence.record.SemanticActionExtractor;
import org.testar.reporting.sequence.record.SemanticActionRecord;
import org.testar.reporting.sequence.record.SemanticStateExtractor;
import org.testar.reporting.sequence.record.SemanticStateRecord;
import org.testar.reporting.sequence.record.SequenceTraceRecorder;
import org.testar.reporting.sequence.writer.JsonSequenceTraceWriter;
import org.testar.reporting.sequence.writer.SequenceTraceWriter;

public final class SequenceTraceSession {

    private final SequenceTraceRecorder sequenceTraceRecorder;
    private final SequenceTraceWriter sequenceTraceWriter;
    private final SemanticStateExtractor semanticStateExtractor;
    private final SemanticActionExtractor semanticActionExtractor;
    private final Path sequenceTraceOutputPath;

    private int nextTraceStepNumber;

    public SequenceTraceSession(SequenceTraceRecorder sequenceTraceRecorder,
                              SequenceTraceWriter sequenceTraceWriter,
                              SemanticStateExtractor semanticStateExtractor,
                              SemanticActionExtractor semanticActionExtractor,
                              Path sequenceTraceOutputPath) {
        this.sequenceTraceRecorder = sequenceTraceRecorder;
        this.sequenceTraceWriter = sequenceTraceWriter;
        this.semanticStateExtractor = semanticStateExtractor;
        this.semanticActionExtractor = semanticActionExtractor;
        this.sequenceTraceOutputPath = sequenceTraceOutputPath;
        this.nextTraceStepNumber = 1;
    }

    public static SequenceTraceSession create(String sequenceId, String target, Path sequenceTraceOutputPath) {
        return new SequenceTraceSession(
                new SequenceTraceRecorder(
                        new SequenceTraceMetadata(
                                "1.0",
                                sequenceId,
                                target,
                                Instant.now().toString(),
                                ""
                        )
                ),
                new JsonSequenceTraceWriter(),
                new DefaultSemanticStateExtractor(),
                new DefaultSemanticActionExtractor(),
                sequenceTraceOutputPath
        );
    }

    public static SequenceTraceSession disabled() {
        return new SequenceTraceSession(null, null, null, null, null);
    }

    public void recordSelectedAction(State state, Action action) {
        if (sequenceTraceRecorder == null || semanticStateExtractor == null || semanticActionExtractor == null) {
            return;
        }

        SemanticStateRecord semanticState = semanticStateExtractor.extract(state);
        SemanticActionRecord semanticAction = semanticActionExtractor.extract(action);
        sequenceTraceRecorder.recordStep(
                new SequenceTraceStep(
                        nextTraceStepNumber++,
                        Instant.now().toString(),
                        semanticState,
                        semanticAction
                )
        );
    }

    public void finish() {
        if (sequenceTraceRecorder == null || sequenceTraceWriter == null || sequenceTraceOutputPath == null) {
            return;
        }

        sequenceTraceRecorder.finish(Instant.now().toString());

        try {
            sequenceTraceWriter.write(sequenceTraceRecorder.snapshot(), sequenceTraceOutputPath);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to write sequence trace", exception);
        }
    }
}
