/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.analysis.export;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

final class StaticGraphIndex {

    private StaticGraphIndex() { }

    static void writeMetadata(Path snapshot, Path run, String modelIdentifier, String applicationName, String applicationVersion, JsonNode artifactMetadata) throws IOException {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("runId", run.getFileName().toString());
        metadata.put("modelIdentifier", modelIdentifier);
        metadata.put("applicationName", applicationName);
        metadata.put("applicationVersion", applicationVersion);
        metadata.put("generatedAt", Instant.now().toString());
        metadata.put("modelScope", "Persisted model snapshot; may include earlier runs.");
        metadata.put("widgetTreesCaptured", artifactMetadata.path("widgetTreesCaptured").asBoolean());
        metadata.put("screenshotsCaptured", artifactMetadata.path("screenshotsCaptured").asBoolean());
        Path reports = run.resolve("reports");
        List<String> reportFiles = List.of();
        if (Files.isDirectory(reports)) {
            try (Stream<Path> files = Files.list(reports)) {
                reportFiles = files.filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".html"))
                        .map(path -> path.getFileName().toString()).sorted().collect(Collectors.toList());
            }
        }
        metadata.put("reports", reportFiles);
        String json = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(metadata);
        Files.writeString(snapshot.resolve("run.json"), json, StandardCharsets.UTF_8);
        Files.writeString(snapshot.resolve("run.js"), "window.__TESTAR_RUN__ = " + json + ";\n", StandardCharsets.UTF_8);
    }

    static void writeWorkspaceIndex(Path outputRoot) throws IOException {
        StringBuilder html = new StringBuilder("<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\">"
                + "<title>Static State Models</title></head><body><h1>Static State Models</h1>"
                + "<p>Portable snapshots of persisted models at the end of each run.</p><ul>");
        try (Stream<Path> directories = Files.list(outputRoot)) {
            for (Path run : directories.filter(Files::isDirectory).sorted(java.util.Comparator.reverseOrder())
                    .collect(Collectors.toList())) {
                Path viewer = run.resolve("state-model/index.html");
                if (Files.isRegularFile(viewer) && Files.isRegularFile(run.resolve("state-model/run.json"))) {
                    String link = outputRoot.toUri().relativize(viewer.toUri()).toASCIIString();
                    html.append("<li><a href=\"").append(escapeHtml(link)).append("\">")
                            .append(escapeHtml(run.getFileName().toString())).append("</a></li>");
                }
            }
        }
        html.append("</ul></body></html>");
        Files.writeString(outputRoot.resolve("state-models.html"), html, StandardCharsets.UTF_8);
    }

    private static String escapeHtml(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
