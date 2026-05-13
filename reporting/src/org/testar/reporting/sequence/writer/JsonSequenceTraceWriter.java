/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.reporting.sequence.writer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;

import org.testar.reporting.sequence.SequenceTrace;
import org.testar.reporting.sequence.SequenceTraceMetadata;
import org.testar.reporting.sequence.SequenceTraceStep;
import org.testar.reporting.sequence.record.SemanticActionRecord;
import org.testar.reporting.sequence.record.SemanticStateRecord;

public final class JsonSequenceTraceWriter implements SequenceTraceWriter {

    @Override
    public void write(SequenceTrace trace, Path outputPath) throws IOException {
        Files.writeString(outputPath, toJson(trace), StandardCharsets.UTF_8);
    }

    public String toJson(SequenceTrace trace) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        appendNamedMetadata(builder, "metadata", trace.getMetadata(), 2, true);
        appendNamedSteps(builder, "steps", trace, 2, false);
        builder.append("}\n");
        return builder.toString();
    }

    private void appendNamedMetadata(StringBuilder builder,
                                     String name,
                                     SequenceTraceMetadata metadata,
                                     int indent,
                                     boolean comma) {
        builder.append(indent(indent)).append("\"").append(name).append("\": ");
        builder.append("{\n");
        appendStringField(builder, "formatVersion", metadata.getFormatVersion(), indent + 2, true);
        appendStringField(builder, "sequenceId", metadata.getSequenceId(), indent + 2, true);
        appendStringField(builder, "target", metadata.getTarget(), indent + 2, true);
        appendStringField(builder, "startedAtUtc", metadata.getStartedAtUtc(), indent + 2, true);
        appendStringField(builder, "finishedAtUtc", metadata.getFinishedAtUtc(), indent + 2, false);
        builder.append(indent(indent)).append("}");
        appendTrailingCommaAndLineBreak(builder, comma);
    }

    private void appendNamedSteps(StringBuilder builder,
                                  String name,
                                  SequenceTrace trace,
                                  int indent,
                                  boolean comma) {
        builder.append(indent(indent)).append("\"").append(name).append("\": [\n");
        for (int index = 0; index < trace.getSteps().size(); index++) {
            if (index > 0) {
                builder.append(",\n");
            }
            appendStep(builder, trace.getSteps().get(index), indent + 2);
        }
        builder.append("\n").append(indent(indent)).append("]");
        appendTrailingCommaAndLineBreak(builder, comma);
    }

    private void appendStep(StringBuilder builder, SequenceTraceStep step, int indent) {
        builder.append(indent(indent)).append("{\n");
        appendNumericField(builder, "stepNumber", step.getStepNumber(), indent + 2, true);
        appendStringField(builder, "timestampUtc", step.getTimestampUtc(), indent + 2, true);
        appendNamedState(builder, "state", step.getState(), indent + 2, true);
        appendNamedAction(builder, "action", step.getAction(), indent + 2, false);
        builder.append(indent(indent)).append("}");
    }

    private void appendNamedState(StringBuilder builder,
                                  String name,
                                  SemanticStateRecord state,
                                  int indent,
                                  boolean comma) {
        builder.append(indent(indent)).append("\"").append(name).append("\": ");
        appendState(builder, state, indent);
        appendTrailingCommaAndLineBreak(builder, comma);
    }

    private void appendNamedAction(StringBuilder builder,
                                   String name,
                                   SemanticActionRecord action,
                                   int indent,
                                   boolean comma) {
        builder.append(indent(indent)).append("\"").append(name).append("\": ");
        appendAction(builder, action, indent);
        appendTrailingCommaAndLineBreak(builder, comma);
    }

    private void appendState(StringBuilder builder, SemanticStateRecord state, int indent) {
        if (state == null) {
            builder.append("null");
            return;
        }

        builder.append("{\n");
        appendStringMap(builder, "properties", state.getProperties(), indent + 2, false);
        builder.append(indent(indent)).append("}");
    }

    private void appendAction(StringBuilder builder, SemanticActionRecord action, int indent) {
        if (action == null) {
            builder.append("null");
            return;
        }

        builder.append("{\n");
        appendStringField(builder, "role", action.getRole(), indent + 2, true);
        if (action.getInput().isEmpty()) {
            appendStringField(builder, "description", action.getDescription(), indent + 2, false);
        } else {
            appendStringField(builder, "description", action.getDescription(), indent + 2, true);
            appendStringField(builder, "input", action.getInput(), indent + 2, false);
        }
        builder.append(indent(indent)).append("}");
    }

    private void appendStringMap(StringBuilder builder,
                                 String name,
                                 Map<String, String> values,
                                 int indent,
                                 boolean comma) {
        builder.append(indent(indent)).append("\"").append(name).append("\": {");
        Iterator<Map.Entry<String, String>> iterator = values.entrySet().iterator();
        if (iterator.hasNext()) {
            builder.append("\n");
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                builder.append(indent(indent + 2))
                        .append("\"").append(escape(entry.getKey())).append("\": ")
                        .append("\"").append(escape(entry.getValue())).append("\"");
                if (iterator.hasNext()) {
                    builder.append(",");
                }
                builder.append("\n");
            }
            builder.append(indent(indent));
        }
        builder.append("}");
        appendTrailingCommaAndLineBreak(builder, comma);
    }

    private void appendStringField(StringBuilder builder,
                                   String name,
                                   String value,
                                   int indent,
                                   boolean comma) {
        builder.append(indent(indent))
                .append("\"").append(name).append("\": ")
                .append("\"").append(escape(value)).append("\"");
        appendTrailingCommaAndLineBreak(builder, comma);
    }

    private void appendNumericField(StringBuilder builder,
                                    String name,
                                    int value,
                                    int indent,
                                    boolean comma) {
        builder.append(indent(indent))
                .append("\"").append(name).append("\": ")
                .append(value);
        appendTrailingCommaAndLineBreak(builder, comma);
    }

    private void appendTrailingCommaAndLineBreak(StringBuilder builder, boolean comma) {
        if (comma) {
            builder.append(",");
        }
        builder.append("\n");
    }

    private String indent(int size) {
        return " ".repeat(Math.max(0, size));
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }
}
