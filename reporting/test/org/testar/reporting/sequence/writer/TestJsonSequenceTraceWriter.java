package org.testar.reporting.sequence.writer;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.testar.reporting.sequence.SequenceTrace;
import org.testar.reporting.sequence.SequenceTraceMetadata;
import org.testar.reporting.sequence.SequenceTraceStep;
import org.testar.reporting.sequence.record.SemanticActionRecord;
import org.testar.reporting.sequence.record.SemanticStateRecord;

public class TestJsonSequenceTraceWriter {

    @Test
    public void testWriteTraceToJson() {
        SequenceTraceMetadata metadata = new SequenceTraceMetadata(
                "1.0",
                "sequence-1",
                "https://example.org",
                "2026-05-12T10:00:00Z",
                "2026-05-12T10:01:00Z"
        );
        SemanticStateRecord state = new SemanticStateRecord(
                Map.of(
                        "title", "Example App",
                        "link", "https://example.org/login",
                        "description", "Login page"
                )
        );
        SemanticActionRecord action = new SemanticActionRecord(
                "type",
                "Type password",
                "secret"
        );
        SequenceTraceStep step = new SequenceTraceStep(
                1,
                "2026-05-12T10:00:30Z",
                state,
                action
        );
        SequenceTrace trace = new SequenceTrace(metadata, List.of(step));

        JsonSequenceTraceWriter writer = new JsonSequenceTraceWriter();
        String json = writer.toJson(trace);

        Assert.assertTrue(json.contains("\"sequenceId\": \"sequence-1\""));
        Assert.assertTrue(json.contains("\"title\": \"Example App\""));
        Assert.assertTrue(json.contains("\"role\": \"type\""));
        Assert.assertTrue(json.contains("\"description\": \"Type password\""));
        Assert.assertTrue(json.contains("\"input\": \"secret\""));
    }
}
