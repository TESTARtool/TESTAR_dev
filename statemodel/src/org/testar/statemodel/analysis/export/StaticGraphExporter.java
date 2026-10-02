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
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
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
            "css/style.css",
            "js/viewer.js",
            "js/cytoscape.min.js",
            "js/cola.min.js",
            "js/cytoscape-cola.js"
    );

    private final Config databaseConfig;
    private final Path outputRoot;
    private final String modelIdentifier;
    private final String applicationName;
    private final String applicationVersion;
    private final boolean persistedModel;
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
            staging = Files.createTempDirectory(run, ".state-model-");
            Path graphRoot = Files.createDirectory(staging.resolve("graph"));
            String graphFile;
            AnalysisManager analysis = analysisFactory.apply(databaseConfig, graphRoot);
            try {
                graphFile = analysis.fetchGraphForModel(modelIdentifier, true, true, true, false);
            } finally {
                analysis.shutdown();
            }
            if (graphFile == null || graphFile.isBlank() || !Path.of(graphFile).getFileName().toString().equals(graphFile)) {
                throw new IOException("Graph generation did not return a valid graph file.");
            }
            Path generatedModel = graphRoot.resolve(modelIdentifier);
            Path model = Files.createDirectory(staging.resolve("model"));
            String graphJson = Files.readString(generatedModel.resolve(graphFile), StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode elements = mapper.readTree(graphJson);
            if (elements == null || !elements.isArray() || elements.isEmpty()) {
                throw new IOException("No graph elements were generated for this model.");
            }
            Files.writeString(model.resolve("elements.json"), graphJson, StandardCharsets.UTF_8);
            Files.writeString(model.resolve("elements.js"), "window.__TESTAR_ELEMENTS__ = " + graphJson + ";\n", StandardCharsets.UTF_8);
            copyImages(generatedModel, model, mapper);
            copyViewerAssets(staging);
            StaticGraphIndex.writeMetadata(staging, run, modelIdentifier, applicationName, applicationVersion);
            deleteDirectory(graphRoot);
            Files.move(staging, snapshot);
            staging = null;
            try {
                StaticGraphIndex.writeWorkspaceIndex(outputRoot);
            } catch (IOException exception) {
                System.err.println("Static snapshot created, but workspace index could not be updated: " + exception.getMessage());
            }
            System.out.println("Static state model viewer: " + snapshot.resolve("index.html"));
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

    private static void copyImages(Path source, Path model, ObjectMapper mapper) throws IOException {
        StringBuilder images = new StringBuilder("window.__TESTAR_IMAGES__ = {};\n");
        try (Stream<Path> files = Files.list(source)) {
            for (Path image : files.filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".png"))
                    .sorted().collect(Collectors.toList())) {
                String name = image.getFileName().toString();
                String id = name.substring(0, name.length() - 4);
                String dataUrl = "data:image/png;base64," + Base64.getEncoder().encodeToString(Files.readAllBytes(image));
                Files.copy(image, model.resolve(name), StandardCopyOption.REPLACE_EXISTING);
                images.append("window.__TESTAR_IMAGES__[").append(mapper.writeValueAsString(id)).append("] = ")
                        .append(mapper.writeValueAsString(dataUrl)).append(";\n");
            }
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
    }

    private static void deleteDirectory(Path directory) throws IOException {
        try (Stream<Path> paths = Files.walk(directory)) {
            for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).collect(Collectors.toList())) {
                Files.delete(path);
            }
        }
    }
}
