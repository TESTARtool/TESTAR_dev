package org.testar.reporting.graph;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Main;
import org.testar.settings.Settings;
import org.testar.statemodel.StateModelManager;
import org.testar.statemodel.StateModelTags;
import org.testar.statemodel.analysis.AnalysisManager;
import org.testar.statemodel.persistence.orientdb.entity.Config;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public final class StaticGraphExporter {

    private static final Logger logger = LogManager.getLogger();

    private StaticGraphExporter() {}

    public static void exportStaticGraph(Settings settings, StateModelManager stateModelManager, String runId) {
        if (settings == null || stateModelManager == null) {
            log("Static export skipped: settings or stateModelManager is null.");
            return;
        }
        if (!settings.get(StateModelTags.StateModelEnabled)) {
            log("Static export skipped: StateModelEnabled is false.");
            return;
        }

        String modelIdentifier = stateModelManager.getModelIdentifier();
        if (modelIdentifier == null || modelIdentifier.trim().isEmpty()) {
            log("Static export skipped: modelIdentifier not available.");
            return;
        }

        try {
            String outputDir = settings.get(ConfigTags.OutputDir);
            if (outputDir == null || outputDir.trim().isEmpty()) {
                log("Static export skipped: OutputDir not configured.");
                return;
            }

            Path publicRoot = Paths.get(outputDir, "public");
            Path runsRoot = publicRoot.resolve("runs");
            Path runRoot = runsRoot.resolve(runId);
            Path modelRoot = runRoot.resolve("model");
            Files.createDirectories(modelRoot);

            Path graphsRoot = Paths.get(outputDir, "graphs");
            Files.createDirectories(graphsRoot);
            String graphFileName = generateGraphFiles(settings, graphsRoot.toString(), modelIdentifier);
            if (graphFileName == null || graphFileName.isEmpty()) {
                log("Static export skipped: graph JSON not generated.");
                return;
            }

            Path modelGraphsDir = graphsRoot.resolve(modelIdentifier);
            Path graphJsonPath = modelGraphsDir.resolve(graphFileName);
            if (!Files.exists(graphJsonPath)) {
                log("Static export skipped: graph JSON not found at " + graphJsonPath);
                return;
            }

            Path viewerRoot = Paths.get(Main.testarDir, "output", "graphs-static");
            if (!Files.isDirectory(viewerRoot)) {
                log("Static export skipped: viewer assets not found.");
                return;
            }
            StaticGraphFileUtils.copyDirectory(viewerRoot, runRoot);

            Path elementsJsonTarget = modelRoot.resolve("elements.json");
            Files.copy(graphJsonPath, elementsJsonTarget, StandardCopyOption.REPLACE_EXISTING);
            StaticGraphFileUtils.writeElementsJs(elementsJsonTarget, modelRoot.resolve("elements.js"));

            StringBuilder imagesJs = new StringBuilder();
            imagesJs.append("window.__TESTAR_IMAGES__ = {};\n");
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(modelGraphsDir, "*.png")) {
                for (Path img : stream) {
                    Files.copy(img, modelRoot.resolve(img.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                    String id = StaticGraphFileUtils.stripExtension(img.getFileName().toString());
                    String dataUrl = StaticGraphFileUtils.toDataUrl(img);
                    if (dataUrl != null) {
                        imagesJs.append("window.__TESTAR_IMAGES__[\"")
                                .append(id)
                                .append("\"] = \"")
                                .append(dataUrl)
                                .append("\";\n");
                    }
                }
            }
            Files.write(modelRoot.resolve("images.js"), imagesJs.toString().getBytes());

            StaticGraphRunMetadataProvider.writeRunMetadata(settings, outputDir, runId, modelIdentifier, runRoot);
            StaticGraphIndexPageBuilder.writeIndexPage(publicRoot, runsRoot);
        } catch (Exception e) {
            log("Static export failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String generateGraphFiles(Settings settings, String graphsRoot, String modelIdentifier) {
        try {
            Config config = new Config();
            config.setConnectionType(settings.get(StateModelTags.DataStoreType));
            config.setServer(settings.get(StateModelTags.DataStoreServer));
            config.setDatabase(settings.get(StateModelTags.DataStoreDB));
            config.setUser(settings.get(StateModelTags.DataStoreUser));
            config.setPassword(settings.get(StateModelTags.DataStorePassword));
            config.setDatabaseDirectory(settings.get(StateModelTags.DataStoreDirectory));

            AnalysisManager analysisManager = new AnalysisManager(config, graphsRoot + File.separator);
            return analysisManager.fetchGraphForModel(modelIdentifier, true, true, true, false);
        } catch (Exception e) {
            log("Graph generation failed: " + e.getMessage());
            return "";
        }
    }

    static void log(String message) {
        logger.log(Level.WARN, message);
    }
}
