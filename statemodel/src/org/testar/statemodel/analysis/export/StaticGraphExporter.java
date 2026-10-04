/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.analysis.export;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.testar.config.ConfigTags;
import org.testar.config.StateModelTags;
import org.testar.core.tag.TaggableBase;
import org.testar.core.util.RuntimePathsUtil;
import org.testar.statemodel.analysis.AnalysisManager;
import org.testar.statemodel.persistence.PersistenceManager;
import org.testar.statemodel.persistence.orientdb.entity.Config;

/** Packages a persisted model snapshot for offline viewing after runtime datastore shutdown. */
public final class StaticGraphExporter {

    public static final String EXPORT_STARTED_SIGNAL = "TESTAR_STATIC_GRAPH_EXPORT_STARTED";
    public static final String EXPORT_FINISHED_SIGNAL = "TESTAR_STATIC_GRAPH_EXPORT_FINISHED";

    private static final List<String> ASSETS = List.of(
            "index.html",
            "widget-tree.html",
            "css/style.css",
            "css/widget-tree.css",
            "js/viewer.js",
            "js/widget-tree-inspector.js",
            "js/cytoscape.min.js",
            "js/cola.min.js",
            "js/cytoscape-cola.js"
    );
    private static final List<String> EXPORT_ASSETS = List.of("model-json-export.js", "model-export-runtime.js", "model-export-controls.js");

    private final Config databaseConfig;
    private final Path outputRoot;
    private final String modelIdentifier;
    private final String applicationName;
    private final String applicationVersion;
    private final boolean persistedModel;
    private final boolean includeWidgetTrees;
    private final Supplier<Path> runDirectory;
    private final BiFunction<Config, Path, AnalysisManager> analysisFactory;

    public StaticGraphExporter(TaggableBase settings, String modelIdentifier, Supplier<Path> runDirectory) {
        this(settings, modelIdentifier, runDirectory,
                (config, directory) -> new AnalysisManager(config, directory.toString() + File.separator));
    }

    StaticGraphExporter(TaggableBase settings, String modelIdentifier, Supplier<Path> runDirectory,
                        BiFunction<Config, Path, AnalysisManager> analysisFactory) {
        this.modelIdentifier = modelIdentifier;
        this.runDirectory = runDirectory;
        this.analysisFactory = analysisFactory;
        outputRoot = RuntimePathsUtil.resolveAgainstTestarHome(settings.get(ConfigTags.OutputDir, "./output"));
        applicationName = settings.get(ConfigTags.ApplicationName, "");
        applicationVersion = settings.get(ConfigTags.ApplicationVersion, "");
        includeWidgetTrees = settings.get(StateModelTags.StateModelExportStaticGraphIncludeWidgetTrees, false);
        persistedModel = settings.get(StateModelTags.StateModelInference, false)
                && "OrientDB".equalsIgnoreCase(settings.get(StateModelTags.DataStore, ""))
                && !PersistenceManager.DATA_STORE_MODE_NONE.equals(settings.get(StateModelTags.DataStoreMode, "none"));
        databaseConfig = new Config();
        databaseConfig.setConnectionType(settings.get(StateModelTags.DataStoreType, ""));
        databaseConfig.setServer(settings.get(StateModelTags.DataStoreServer, ""));
        databaseConfig.setDatabase(settings.get(StateModelTags.DataStoreDB, ""));
        databaseConfig.setUser(settings.get(StateModelTags.DataStoreUser, ""));
        databaseConfig.setPassword(settings.get(StateModelTags.DataStorePassword, ""));
        databaseConfig.setDatabaseDirectory(RuntimePathsUtil.resolveAgainstTestarHome(
                settings.get(StateModelTags.DataStoreDirectory, "")).toString());
    }

    public Path export() {
        if (!persistedModel || modelIdentifier == null || !modelIdentifier.matches("[A-Za-z0-9_-]+")) {
            System.out.println("Static state model export skipped: no persisted model is available.");
            return null;
        }
        Path staging = null;
        boolean exportStarted = false;
        try {
            Path run = runDirectory.get();
            if (run == null) {
                System.out.println("Static state model export skipped: no run output directory is available.");
                return null;
            }
            run = run.toAbsolutePath().normalize();
            if (!outputRoot.equals(run.getParent()) || !Files.isDirectory(run)) {
                throw new IOException("Run directory must exist directly inside the configured workspace output directory.");
            }
            Path snapshot = run.resolve("state-model");
            if (Files.exists(snapshot)) {
                throw new IOException("A static state model snapshot already exists for this run.");
            }
            System.out.println(EXPORT_STARTED_SIGNAL);
            exportStarted = true;
            System.out.println("Exporting static state model " + modelIdentifier + "...");
            long preparationStarted = System.nanoTime();
            staging = Files.createTempDirectory(run, ".state-model-");
            Path graphRoot = Files.createDirectory(staging.resolve("graph"));
            ObjectNode exportData;
            AnalysisManager analysis = analysisFactory.apply(databaseConfig, graphRoot);
            try {
                exportData = ModelExportSnapshot.read(analysis, graphRoot, modelIdentifier,
                        new ModelExportOptions("snapshot", includeWidgetTrees, true));
            } finally {
                analysis.shutdown();
            }
            Path model = Files.createDirectory(staging.resolve("model"));
            ObjectMapper mapper = new ObjectMapper();
            JsonNode elements = exportData.get("elements");
            String graphJson = mapper.writeValueAsString(elements);
            Files.writeString(model.resolve("elements.json"), graphJson, StandardCharsets.UTF_8);
            Files.writeString(model.resolve("elements.js"), "window.__TESTAR_ELEMENTS__ = " + graphJson + ";\n", StandardCharsets.UTF_8);
            String treesJson = "{}";
            if (includeWidgetTrees) {
                treesJson = mapper.writeValueAsString(exportData.get("widgetTrees"));
                Files.writeString(model.resolve("widget-trees.json"), treesJson, StandardCharsets.UTF_8);
            }
            Files.writeString(model.resolve("widget-trees.js"), "window.__TESTAR_WIDGET_TREES__ = " + treesJson + ";\n", StandardCharsets.UTF_8);
            copyImages(exportData.path("images"), model, mapper);
            copyViewerAssets(staging);
            StaticGraphIndex.writeMetadata(staging, run, modelIdentifier, applicationName, applicationVersion, exportData.path("metadata"));
            deleteDirectory(graphRoot);
            Files.move(staging, snapshot);
            staging = null;
            try {
                StaticGraphIndex.writeWorkspaceIndex(outputRoot);
            } catch (IOException exception) {
                System.err.println("Static snapshot created, but workspace index could not be updated: " + exception.getMessage());
            }
            System.out.println("Static state model viewer: " + snapshot.resolve("index.html"));
            System.out.println("Static state model preparation completed in " + (System.nanoTime() - preparationStarted) / 1_000_000 + " ms.");
            return snapshot.resolve("index.html");
        } catch (Exception exception) {
            System.err.println("Static state model export failed: " + exception.getMessage());
            return null;
        } finally {
            if (staging != null) {
                try {
                    deleteDirectory(staging);
                } catch (IOException exception) {
                    System.err.println("Unable to remove temporary static graph files: " + exception.getMessage());
                }
            }
            if (exportStarted) {
                System.out.println(EXPORT_FINISHED_SIGNAL);
            }
        }
    }

    private static void copyImages(JsonNode imageData, Path model, ObjectMapper mapper) throws IOException {
        StringBuilder images = new StringBuilder("window.__TESTAR_IMAGES__ = {};\n");
        Iterator<String> identifiers = imageData.fieldNames();
        while (identifiers.hasNext()) {
            String id = identifiers.next();
            if (!id.matches("[A-Za-z0-9_-]+")) {
                throw new IOException("Invalid screenshot identifier.");
            }
            String dataUrl = imageData.path(id).asText();
            String prefix = "data:image/png;base64,";
            if (!dataUrl.startsWith(prefix)) {
                throw new IOException("Invalid screenshot data.");
            }
            Files.write(model.resolve(id + ".png"), Base64.getDecoder().decode(dataUrl.substring(prefix.length())));
            images.append("window.__TESTAR_IMAGES__[").append(mapper.writeValueAsString(id)).append("] = ")
                    .append(mapper.writeValueAsString(dataUrl)).append(";\n");
        }
        Files.writeString(model.resolve("images.js"), images, StandardCharsets.UTF_8);
    }

    private static void copyViewerAssets(Path destination) throws IOException {
        for (String asset : ASSETS) {
            try (InputStream input = StaticGraphExporter.class.getResourceAsStream("/graphs-static/" + asset)) {
                if (input == null) {
                    throw new IOException("Missing packaged static viewer asset: " + asset);
                }
                Path target = destination.resolve(asset);
                Files.createDirectories(target.getParent());
                Files.copy(input, target);
            }
        }
        for (String asset : EXPORT_ASSETS) {
            try (InputStream input = StaticGraphExporter.class.getResourceAsStream("/graphs/js/" + asset)) {
                if (input == null) {
                    throw new IOException("Missing packaged model export asset: " + asset);
                }
                Files.copy(input, destination.resolve("js").resolve(asset));
            }
        }
        try (InputStream input = StaticGraphExporter.class.getResourceAsStream("/graphs/css/model-export.css")) {
            if (input == null) {
                throw new IOException("Missing packaged model export stylesheet.");
            }
            Files.copy(input, destination.resolve("css/model-export.css"));
        }
    }

    private static void deleteDirectory(Path directory) throws IOException {
        try (Stream<Path> paths = Files.walk(directory)) {
            for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).collect(Collectors.toList())) {
                Files.delete(path);
            }
        }
    }
}
