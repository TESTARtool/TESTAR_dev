/***************************************************************************************************
 *
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.reporting;

import org.testar.monkey.ConfigTags;
import org.testar.monkey.Main;
import org.testar.settings.Settings;
import org.testar.statemodel.StateModelManager;
import org.testar.statemodel.StateModelTags;
import org.testar.statemodel.analysis.AnalysisManager;
import org.testar.statemodel.persistence.orientdb.entity.Config;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class StaticGraphExporter {

    protected static final Logger logger = LogManager.getLogger();

    private StaticGraphExporter() {}

    public static void exportStaticSite(Settings settings, StateModelManager stateModelManager, String runId) {
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

            // 1) Generate graph JSON and screenshots into outputDir/graphs/<modelIdentifier>
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

            // 2) Copy static viewer assets
            Path viewerRoot = Paths.get(Main.testarDir, "output", "graphs-static");
            if (viewerRoot == null || !Files.isDirectory(viewerRoot)) {
                log("Static export skipped: viewer assets not found.");
                return;
            }
            copyDirectory(viewerRoot, runRoot);

            // 3) Copy elements.json and screenshots
            Path elementsJsonTarget = modelRoot.resolve("elements.json");
            Files.copy(graphJsonPath, elementsJsonTarget, StandardCopyOption.REPLACE_EXISTING);
            writeElementsJs(elementsJsonTarget, modelRoot.resolve("elements.js"));

            StringBuilder imagesJs = new StringBuilder();
            imagesJs.append("window.__TESTAR_IMAGES__ = {};\n");
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(modelGraphsDir, "*.png")) {
                for (Path img : stream) {
                    Files.copy(img, modelRoot.resolve(img.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                    String id = stripExtension(img.getFileName().toString());
                    String dataUrl = toDataUrl(img);
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

            // 4) Rebuild public index page
            writeIndexPage(publicRoot, runsRoot);
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
            // Export abstract + concrete + sequence layers for the static viewer
            return analysisManager.fetchGraphForModel(modelIdentifier, true, true, true, false);
        } catch (Exception e) {
            log("Graph generation failed: " + e.getMessage());
            return "";
        }
    }

    private static void copyDirectory(Path src, Path dst) throws IOException {
        try {
            Files.walk(src).forEach(path -> {
                try {
                    Path relative = src.relativize(path);
                    Path target = dst.resolve(relative);
                    if (Files.isDirectory(path)) {
                        Files.createDirectories(target);
                    } else {
                        Files.copy(path, target, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            throw e;
        }
    }

    private static void writeIndexPage(Path publicRoot, Path runsRoot) throws IOException {
        Files.createDirectories(publicRoot);
        Files.createDirectories(runsRoot);

        String[] runDirs = runsRoot.toFile().list((dir, name) -> new File(dir, name).isDirectory());
        if (runDirs == null) runDirs = new String[0];
        Arrays.sort(runDirs, Comparator.reverseOrder());

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html lang=\"en\">\n");
        sb.append("<head>\n");
        sb.append("  <meta charset=\"UTF-8\" />\n");
        sb.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />\n");
        sb.append("  <title>TESTAR State Models</title>\n");
        sb.append("  <style>\n");
        sb.append("    body{font-family:Segoe UI,Tahoma,Verdana,sans-serif;margin:24px;background:#f6f7fb;color:#222;}\n");
        sb.append("    h1{font-size:22px;margin-bottom:12px;}\n");
        sb.append("    ul{list-style:none;padding:0;}\n");
        sb.append("    li{margin:8px 0;}\n");
        sb.append("    a{color:#1c9099;text-decoration:none;font-weight:600;}\n");
        sb.append("    a:hover{text-decoration:underline;}\n");
        sb.append("    .muted{color:#666;font-size:12px;}\n");
        sb.append("  </style>\n");
        sb.append("</head>\n");
        sb.append("<body>\n");
        sb.append("  <h1>TESTAR State Models</h1>\n");

        if (runDirs.length == 0) {
            sb.append("  <div class=\"muted\">No runs found.</div>\n");
        } else {
            sb.append("  <ul>\n");
            for (String run : runDirs) {
                sb.append("    <li><a href=\"runs/").append(run).append("/index.html\">").append(run).append("</a></li>\n");
            }
            sb.append("  </ul>\n");
        }

        sb.append("</body>\n");
        sb.append("</html>\n");

        Files.write(publicRoot.resolve("index.html"), sb.toString().getBytes());
    }

    private static void writeElementsJs(Path elementsJson, Path elementsJs) throws IOException {
        byte[] json = Files.readAllBytes(elementsJson);
        StringBuilder sb = new StringBuilder();
        sb.append("window.__TESTAR_ELEMENTS__ = ");
        sb.append(new String(json));
        sb.append(";\n");
        Files.write(elementsJs, sb.toString().getBytes());
    }

    private static String stripExtension(String name) {
        int idx = name.lastIndexOf('.');
        return idx > 0 ? name.substring(0, idx) : name;
    }

    private static String toDataUrl(Path file) {
        try {
            byte[] bytes = Files.readAllBytes(file);
            String base64 = java.util.Base64.getEncoder().encodeToString(bytes);
            return "data:image/png;base64," + base64;
        } catch (IOException e) {
            log("Failed to read image for data URL: " + file);
            return null;
        }
    }

    private static void log(String message) {
        logger.log(Level.WARN, message);
    }
}
