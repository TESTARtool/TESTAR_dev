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
import java.util.Locale;

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
            writeRunMetadata(outputDir, runId, modelIdentifier, runRoot);
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

        int completeCount = 0;
        int failCount = 0;
        RunMeta[] metas = new RunMeta[runDirs.length];
        for (int i = 0; i < runDirs.length; i++) {
            metas[i] = readRunMeta(runsRoot.resolve(runDirs[i]));
            if (metas[i].isComplete) completeCount++; else failCount++;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html lang=\"en\">\n");
        sb.append("<head>\n");
        sb.append("  <meta charset=\"UTF-8\" />\n");
        sb.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />\n");
        sb.append("  <title>TESTAR State Models</title>\n");
        sb.append("  <style>\n");
        sb.append("    :root{--bg:#f6f7fb;--card:#fff;--line:#d9d9e3;--txt:#222;--muted:#666;--ok:#0f7b42;--bad:#b00020;--accent:#1c9099;}\n");
        sb.append("    body{font-family:Segoe UI,Tahoma,Verdana,sans-serif;margin:24px;background:var(--bg);color:var(--txt);}\n");
        sb.append("    h1{font-size:22px;margin:0 0 10px 0;}\n");
        sb.append("    .top{display:flex;gap:12px;flex-wrap:wrap;align-items:stretch;margin-bottom:16px;}\n");
        sb.append("    .card{background:var(--card);border:1px solid var(--line);border-radius:10px;padding:12px;}\n");
        sb.append("    .summary{display:flex;gap:12px;align-items:center;flex-wrap:wrap;}\n");
        sb.append("    .pill{display:inline-flex;gap:8px;align-items:center;padding:6px 10px;border-radius:999px;font-weight:800;font-size:12px;}\n");
        sb.append("    .pill.ok{background:#e7f6ee;color:var(--ok);}\n");
        sb.append("    .pill.bad{background:#fde8ec;color:var(--bad);}\n");
        sb.append("    .bar{height:10px;border-radius:999px;background:#ececf5;overflow:hidden;flex:1;min-width:220px;}\n");
        sb.append("    .bar > span{display:block;height:100%;float:left;}\n");
        sb.append("    .bar .ok{background:var(--ok);}\n");
        sb.append("    .bar .bad{background:var(--bad);}\n");
        sb.append("    .runs{list-style:none;padding:0;margin:0;display:flex;flex-direction:column;gap:10px;}\n");
        sb.append("    .run{display:flex;gap:10px;align-items:center;}\n");
        sb.append("    .name{font-weight:800;flex:1;word-break:break-word;}\n");
        sb.append("    .meta{font-size:12px;color:var(--muted);}\n");
        sb.append("    .badge{font-size:11px;font-weight:900;padding:3px 8px;border-radius:999px;}\n");
        sb.append("    .badge.ok{background:#e7f6ee;color:var(--ok);}\n");
        sb.append("    .badge.bad{background:#fde8ec;color:var(--bad);}\n");
        sb.append("    .iconlink{display:inline-flex;align-items:center;justify-content:center;width:34px;height:34px;border-radius:8px;border:1px solid var(--line);background:#fbfbff;}\n");
        sb.append("    .iconlink:hover{border-color:#b8b8c7;}\n");
        sb.append("    .icon{width:18px;height:18px;}\n");
        sb.append("    .muted{color:var(--muted);font-size:12px;}\n");
        sb.append("  </style>\n");
        sb.append("</head>\n");
        sb.append("<body>\n");
        sb.append("  <h1>TESTAR State Models</h1>\n");
        sb.append("  <div class=\"top\">\n");
        sb.append("    <div class=\"card summary\">\n");
        sb.append("      <span class=\"pill ok\">&#10003; Complete: ").append(completeCount).append("</span>\n");
        sb.append("      <span class=\"pill bad\">&#10007; Other: ").append(failCount).append("</span>\n");
        double total = Math.max(1.0, completeCount + failCount);
        int okPct = (int)Math.round((completeCount / total) * 100.0);
        int badPct = Math.max(0, 100 - okPct);
        sb.append("      <div class=\"bar\" title=\"").append(okPct).append("% complete\">\n");
        sb.append("        <span class=\"ok\" style=\"width:").append(okPct).append("%\"></span>\n");
        sb.append("        <span class=\"bad\" style=\"width:").append(badPct).append("%\"></span>\n");
        sb.append("      </div>\n");
        sb.append("    </div>\n");
        sb.append("  </div>\n");

        if (runDirs.length == 0) {
            sb.append("  <div class=\"muted\">No runs found.</div>\n");
        } else {
            sb.append("  <ul class=\"runs\">\n");
            for (int i = 0; i < runDirs.length; i++) {
                String run = runDirs[i];
                RunMeta meta = metas[i];
                sb.append("    <li class=\"card run\">\n");
                sb.append("      <span class=\"badge ").append(meta.isComplete ? "ok" : "bad").append("\">")
                        .append(escapeHtml(meta.verdictTitle)).append("</span>\n");
                sb.append("      <div style=\"flex:1; min-width:0;\">\n");
                sb.append("        <div class=\"name\">").append(escapeHtml(run)).append("</div>\n");
                if (meta.modelIdentifier != null && !meta.modelIdentifier.isEmpty()) {
                    sb.append("        <div class=\"meta\">Model: ").append(escapeHtml(meta.modelIdentifier)).append("</div>\n");
                }
                sb.append("      </div>\n");
                sb.append("      <a class=\"iconlink\" href=\"runs/").append(run).append("/index.html\" title=\"Open model\">\n");
                sb.append("        ").append(svgEye()).append("\n");
                sb.append("      </a>\n");
                sb.append("    </li>\n");
            }
            sb.append("  </ul>\n");
        }

        sb.append("</body>\n");
        sb.append("</html>\n");

        Files.write(publicRoot.resolve("index.html"), sb.toString().getBytes());
    }

    private static void writeRunMetadata(String outputDir, String runId, String modelIdentifier, Path runRoot) {
        try {
            String runKey = normalizeRunKey(runId);
            Path runOutputDir = resolveRunOutputDir(Paths.get(outputDir), runKey);
            String verdictTitle = detectVerdictTitle(runOutputDir);
            boolean isComplete = "LLM_COMPLETE".equalsIgnoreCase(verdictTitle);

            StringBuilder sb = new StringBuilder();
            sb.append("{\n");
            sb.append("  \"runId\": \"").append(escapeJson(runKey)).append("\",\n");
            sb.append("  \"modelIdentifier\": \"").append(escapeJson(modelIdentifier)).append("\",\n");
            sb.append("  \"verdictTitle\": \"").append(escapeJson(verdictTitle)).append("\",\n");
            sb.append("  \"isComplete\": ").append(isComplete).append("\n");
            sb.append("}\n");

            Files.write(runRoot.resolve("run.json"), sb.toString().getBytes());
        } catch (Exception e) {
            log("Failed writing run metadata: " + e.getMessage());
        }
    }

    private static RunMeta readRunMeta(Path runRoot) {
        RunMeta meta = new RunMeta();
        Path json = runRoot.resolve("run.json");
        if (!Files.exists(json)) {
            meta.verdictTitle = "UNKNOWN";
            meta.isComplete = false;
            meta.modelIdentifier = "";
            return meta;
        }
        try {
            String text = new String(Files.readAllBytes(json));
            meta.verdictTitle = extractJsonString(text, "verdictTitle", "UNKNOWN");
            meta.modelIdentifier = extractJsonString(text, "modelIdentifier", "");
            meta.isComplete = "true".equalsIgnoreCase(extractJsonBool(text, "isComplete", "false"));
            return meta;
        } catch (Exception e) {
            meta.verdictTitle = "UNKNOWN";
            meta.isComplete = false;
            meta.modelIdentifier = "";
            return meta;
        }
    }

    private static String normalizeRunKey(String runId) {
        if (runId == null) return "";
        String trimmed = runId.trim();
        if (trimmed.endsWith("_")) trimmed = trimmed.substring(0, trimmed.length() - 1);
        return trimmed;
    }

    private static Path resolveRunOutputDir(Path outputRoot, String runKey) {
        if (runKey == null || runKey.isEmpty()) return outputRoot;
        Path direct = outputRoot.resolve(runKey);
        if (Files.isDirectory(direct)) return direct;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(outputRoot)) {
            for (Path p : stream) {
                if (!Files.isDirectory(p)) continue;
                String name = p.getFileName().toString();
                if (name.equals(runKey) || name.startsWith(runKey) || name.contains(runKey)) {
                    return p;
                }
            }
        } catch (IOException ignored) {
        }
        return outputRoot.resolve(runKey);
    }

    private static String detectVerdictTitle(Path runOutputDir) {
        if (runOutputDir == null || !Files.isDirectory(runOutputDir)) return "UNKNOWN";
        Path llmComplete = runOutputDir.resolve("sequences_llm_complete");
        if (Files.isDirectory(llmComplete)) return "LLM_COMPLETE";

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(runOutputDir, "sequences_*")) {
            for (Path p : stream) {
                if (Files.isDirectory(p)) {
                    String name = p.getFileName().toString();
                    String suffix = name.substring("sequences_".length());
                    return suffix.toUpperCase(Locale.ROOT);
                }
            }
        } catch (IOException ignored) {
        }
        return "UNKNOWN";
    }

    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    private static String extractJsonString(String json, String key, String fallback) {
        String pattern = "\"" + key + "\"";
        int idx = json.indexOf(pattern);
        if (idx < 0) return fallback;
        int colon = json.indexOf(":", idx + pattern.length());
        if (colon < 0) return fallback;
        int firstQuote = json.indexOf("\"", colon + 1);
        if (firstQuote < 0) return fallback;
        int secondQuote = json.indexOf("\"", firstQuote + 1);
        if (secondQuote < 0) return fallback;
        return json.substring(firstQuote + 1, secondQuote);
    }

    private static String extractJsonBool(String json, String key, String fallback) {
        String pattern = "\"" + key + "\"";
        int idx = json.indexOf(pattern);
        if (idx < 0) return fallback;
        int colon = json.indexOf(":", idx + pattern.length());
        if (colon < 0) return fallback;
        String rest = json.substring(colon + 1).trim();
        if (rest.startsWith("true")) return "true";
        if (rest.startsWith("false")) return "false";
        return fallback;
    }

    private static String svgEye() {
        return "<svg class=\"icon\" viewBox=\"0 0 24 24\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">" +
                "<path d=\"M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12z\" stroke=\"#1c9099\" stroke-width=\"2\"/>" +
                "<circle cx=\"12\" cy=\"12\" r=\"3\" stroke=\"#1c9099\" stroke-width=\"2\"/>" +
                "</svg>";
    }

    private static final class RunMeta {
        String verdictTitle;
        String modelIdentifier;
        boolean isComplete;
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
