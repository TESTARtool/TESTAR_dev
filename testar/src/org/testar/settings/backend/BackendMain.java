package org.testar.settings.backend;

import static org.testar.monkey.Main.SETTINGS_FILE;
import static org.testar.monkey.Main.TESTAR_VERSION;
import static org.testar.monkey.Main.SUT_SETTINGS_EXT;

import io.javalin.Javalin;
import io.javalin.http.Context;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JFileChooser;

import org.sikuli.script.App;
import org.testar.OutputStructure;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.RuntimeControlsProtocol;
import org.testar.monkey.RuntimeControlsProtocol.Modes;
import org.testar.monkey.alayer.Tag;
import org.testar.settings.Settings;
import org.testar.settings.dialog.tagsvisualization.DefaultTagFilter;

public class BackendMain {

    public static Path SSE_FOLDER = Path.of("./settings");

    private final Javalin app;
    private final Object runLock = new Object();
    private Optional<SettingsFile> settings = Optional.empty();

    private static class BackendError {
        @SuppressWarnings("unused")
		public final String error;
		BackendError(String error) {
            this.error = error;
        }
    }

    public BackendMain(
        Settings settings,
        String settingsFile,
        boolean serveStaticFiles
    ) {
        this(serveStaticFiles);
        this.settings = Optional.of(new SettingsFile(settingsFile, settings));
    }

    public BackendMain(boolean serveStaticFiles) {
        this.app = Javalin.create(config -> {
            if (serveStaticFiles) {
                config.staticFiles.add(staticFiles -> {
                    staticFiles.hostedPath = "/";
                    staticFiles.directory = "dist";
                });
            }
        });

        WebSocket.makeWebSocket(app);

        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(500).json(new BackendError(e.getMessage()));
        });

        app.post("/api/start", ctx -> startPost(ctx));

        app.get("/api/conf", ctx -> confGet(ctx));
        app.post("/api/conf", ctx -> confPost(ctx));

        app.get("/api/conf/spy/tags", ctx -> confSpyTagsGet(ctx));

        app.post("/api/conf/protocol", ctx -> confProtocolPost(ctx));
        app.get("/api/conf/protocol", ctx -> confProtocolGet(ctx));
        app.get("/api/conf/protocol/available", ctx ->
            confProtocolAvailableGet(ctx)
        );

        app.get("/api/conf/{mode}/available", ctx ->
            confModeAvailableGet(ctx)
        );

        app.get("/api/results/download", ctx -> resultsDownloadGet(ctx));

        app.get("/api/reports", ctx -> reportsGet(ctx));
        app.get("/api/reports/get", ctx -> reportsPathGet(ctx));

        if (serveStaticFiles) {
            // SPA fallback: anything not /api and not a real file -> index.html
            app.error(404, ctx -> {
                String path = ctx.path();

                if (path.startsWith("/api")) return;

                // If it looks like a file request (has an extension), keep 404
                if (path.matches(".*\\.[a-zA-Z0-9]+$")) return;

                ctx.contentType("text/html");
                ctx.result(
                    Objects.requireNonNull(
                        App.class.getResourceAsStream("/dist/index.html")
                    )
                );
            });
        }

        this.app.start(7070);
    }

    public void stop() {
        System.out.println("Stopping testar...");
        app.stop();
    }

    public static void openWebInterfaceInBrowser() {
        try {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(new URI("http://localhost:7070"));
            }
        } catch (URISyntaxException e) {
            System.err.println("Invalid URI: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error opening browser" + e.getMessage());
        }
    }

    private String extractProtocolName(String settingsFilePath) {
        Path path = Paths.get(settingsFilePath);
        return path.getParent().getFileName().toString();
    }

    private void createSSEFile(String protocolName) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(SSE_FOLDER)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file) && file.toString().endsWith(SUT_SETTINGS_EXT)) {
                    Files.delete(file);
                }
            }
        } catch (IOException ignored) {
            // If we fail to delete old files, we still want to try making the new file
        }

        Path SSEFile = SSE_FOLDER.resolve(protocolName + SUT_SETTINGS_EXT);

        try {
            Files.createFile(SSEFile);
        } catch (IOException e) {
            System.err.print("Error creating new SSE file");
        }
    }

    private void startPost(Context ctx) {
        if (this.settings.isEmpty()) {
            ctx.status(409).json(new BackendError("Protocol not configured"));
            return;
        }

        Settings settings = this.settings.get().getSettings();

        System.out.println("TESTAR version is <" + TESTAR_VERSION + ">");
        System.out.println("Starting testar...");

       Modes mode = settings.get(ConfigTags.Mode);

        if (mode == Modes.Replay || mode == Modes.View) {
            openFilePicker(settings);
        }

        TestarThread t;
        synchronized (runLock) {
            if (WebSocket.isTestarRunning()) {
                ctx.status(409).json(new Error("TESTAR is already running"));
                return;
            }

            t = new TestarThread(settings);
            WebSocket.registerActiveThread(t);
            t.start();
        }

        ctx.status(200).result("OK");
    }

    private void openFilePicker(Settings settings) {
            JFileChooser fd = new JFileChooser();
            fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fd.setCurrentDirectory(new File(settings.get(ConfigTags.PathToReplaySequence)).getParentFile());

            if (fd.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                String file = fd.getSelectedFile().getAbsolutePath();
                settings.set(ConfigTags.PathToReplaySequence, file);
            }
    }

    private void confGet(Context ctx) {
        if (settings.isEmpty()) {
            ctx.status(409).json(new BackendError("Protocol not configured"));
            return;
        }

        try {
            settings.get().reloadSettingsIfModified();

            Settings settings = this.settings.get().getSettings();
            Map<String, Object> configMap = new HashMap<>();

            for (Tag<?> tag : settings.tags()) {
                configMap.put(tag.name(), settings.get(tag));
            }

            ctx.status(200).json(configMap);
        } catch (IOException e) {
            ctx
                .status(500)
                .json(new BackendError("Failed to reload settings from file"));
            System.err.println(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void confPost(Context ctx) {
        if (settings.isEmpty()) {
            ctx.status(409).json(new BackendError("Protocol not configured"));
            return;
        }

        // Expect JSON like: {"Mode":"Generate","Sequences":"10"}
        // Only the provided keys are updated; all other settings remain unchanged.
        HashMap<String, Object> map = ctx.bodyAsClass(HashMap.class);
        Settings currentSettings = settings.get().getSettings();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            Tag<?> matchingTag = null;
            for (Tag<?> tag : currentSettings.tags()) {
                if (tag.name().equals(key)) {
                    matchingTag = tag;
                    break;
                }
            }

            if (matchingTag != null) {
                try {
                    String valueString = valueAsString(
                        value,
                        Settings.getStringSeparator(matchingTag)
                    );

                    Object parsedValue = Settings.parse(
                        valueString,
                        matchingTag
                    );

                    currentSettings.set((Tag<Object>) matchingTag, parsedValue);
                } catch (Settings.ConfigParseException e) {
                    ctx
                        .status(400)
                        .json(
                            new BackendError(
                                "Invalid value for " +
                                    key +
                                    ": " +
                                    e.getMessage()
                            )
                        );
                    return;
                }
            } else {
                currentSettings.set(
                    Tag.from(key, String.class),
                    value.toString()
                );
            }
        }

        try {
            settings.get().saveToFile();
            ctx.status(200).result("OK");
        } catch (IOException e) {
            ctx.status(500).json(new BackendError("Failed to save settings to file"));
            System.err.println(e);
        }
    }

    private static String valueAsString(Object value, String separator) {
        if (value == null) return null;

        if (value instanceof List<?>) {
            return ((List<?>) value).stream()
                .map(v -> v == null ? "" : v.toString())
                .collect(Collectors.joining(separator));
        }

        return value.toString();
    }

    private void confProtocolPost(Context ctx) {
        String fileName = ctx.bodyAsClass(String.class);

        try {
            settings = Optional.of(new SettingsFile(fileName));
            String protocolName = extractProtocolName(fileName);
            createSSEFile(protocolName);
            ctx.status(200).result("OK");
        } catch (IOException e) {
            ctx.status(500).json(new BackendError("Failed to update protocol" + e));
            System.err.println(e);
        }
    }

    private void confModeAvailableGet(Context ctx) {
        String mode = ctx.pathParam("mode");
        try {
            SettingsPerMode.getSettingsForMode(mode).ifPresentOrElse(
                ctx::json,
                () -> ctx.status(404).json(new BackendError("Unknown mode: " + mode))
            );
        } catch (IOException e) {
            ctx.status(500).json(new BackendError("Cannot read JSON file: " + e));
        }
    }

    private void confProtocolGet(Context ctx) {
        ctx
            .status(200)
            .json(
                settings
                    .map(sf -> sf.fileName)
                    .orElse("Protocol not configured")
            );
    }

    private void confSpyTagsGet(Context ctx) {
        Map<String, List<String>> map = new HashMap<>();

        map.put(
            "UIATags",
            DefaultTagFilter.getUiatags()
                .stream()
                .map(Tag::name)
                .collect(Collectors.toList())
        );

        map.put(
            "WdTags",
            DefaultTagFilter.getWdtags()
                .stream()
                .map(Tag::name)
                .collect(Collectors.toList())
        );

        map.put(
            "Tags",
            DefaultTagFilter.getTags()
                .stream()
                .map(Tag::name)
                .collect(Collectors.toList())
        );

        ctx.status(200).json(map);
    }

    private void confProtocolAvailableGet(Context ctx) {
        try {
            if (!Files.isDirectory(SSE_FOLDER)) {
                ctx.status(409).json(new BackendError("Settings directory not found"));
                return;
            }

            Map<String, String> protocolMap = new HashMap<>();

            try (
                DirectoryStream<Path> ds = Files.newDirectoryStream(SSE_FOLDER)
            ) {
                for (Path dir : ds) {
                    if (!Files.isDirectory(dir)) continue;

                    String key = dir.getFileName().toString();
                    Path settingsFile = dir.resolve(SETTINGS_FILE);

                    protocolMap.put(key, settingsFile.toString());
                }
            }

            ctx.json(protocolMap);
        } catch (IOException e) {
            ctx
                .status(500)
                .json(new BackendError("Failed to read settings directory"));
            System.err.println(e);
        }
    }

    private void resultsDownloadGet(Context ctx) throws IOException {
        String outputDir = OutputStructure.outerLoopOutputDir;

        if (outputDir == null || outputDir.isEmpty()) {
            ctx
                .status(409)
                .json(
                    new BackendError(
                        "No output directory set, run a test before downloading results"
                    )
                );
            return;
        }

        Path outputPath = Paths.get(outputDir);
        if (!Files.exists(outputPath)) {
            ctx
                .status(409)
                .json(
                    new BackendError(
                        "Test results directory not found, run a test before downloading results"
                    )
                );
            return;
        }

        ctx.header("Content-Type", "application/zip");
        ctx.header(
            "Content-Disposition",
            "attachment; filename=\"test_results.zip\""
        );

        ZipOutputStream zos = new ZipOutputStream(ctx.outputStream());

        Files.walk(outputPath)
            .filter(Files::isRegularFile)
            .forEach(file -> {
                try {
                    String relativePath = outputPath
                        .relativize(file)
                        .toString();
                    zos.putNextEntry(new ZipEntry(relativePath));

                    try (
                        FileInputStream fis = new FileInputStream(file.toFile())
                    ) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = fis.read(buffer)) > 0) {
                            zos.write(buffer, 0, len);
                        }
                    }
                    zos.closeEntry();
                } catch (IOException e) {
                    System.err.println(
                        "Error adding file to ZIP: " +
                            file +
                            ", " +
                            e.getMessage()
                    );
                }
            });

        zos.finish();
        zos.close();
        ctx.status(200).result("OK");
    }

    private void reportsGet(Context ctx) {
        String reportsDir = WebSocket.getReportsDir();

        if (reportsDir == null) {
            ctx.status(409).json(new BackendError("No reports directory made, run generate mode before viewing reports"));
            return;
       }

        File runDir = new File(reportsDir).getParentFile();
        File outputDir = runDir.getParentFile();

        if (outputDir == null || !outputDir.exists()) {
            ctx.status(404).json(new BackendError("Output directory not found"));
            return;
        }

        List<Map<String, Object>> allRuns = new ArrayList<>();

        File[] runDirs = outputDir.listFiles(File::isDirectory);
        if (runDirs != null) {
            Arrays.sort(runDirs, (a, b) -> b.getName().compareTo(a.getName()));

            for (File dir : runDirs) {
                File reportsSubDir = new File(dir, "reports");
                if (reportsSubDir.exists()) {
                    File[] files = reportsSubDir.listFiles((d, name) -> name.endsWith(".html"));
                    if (files != null && files.length > 0) {
                        Map<String, Object> runInfo = new LinkedHashMap<>();
                        runInfo.put("sequenceID", dir.getName());
                        runInfo.put("directory", reportsSubDir.getAbsolutePath());
                        runInfo.put("files", Arrays.stream(files).collect(Collectors.toList()));
                        allRuns.add(runInfo);
                    }
                }
            }
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("runs", allRuns);

        ctx.json(response);
    }

    private void reportsPathGet(Context ctx) {
        String filePath = ctx.queryParam("file");

        if (filePath == null || filePath.isEmpty()) {
            ctx.status(400).json(new BackendError("Missing 'file' parameter"));
            return;
        }

        Path path = Path.of(filePath);

        try {
            if (!Files.exists(path)) {
                ctx
                    .status(404)
                    .json(new BackendError("HTML file not found: " + path));
                return;
            }

            if (!Files.isReadable(path)) {
                ctx.status(403).json(new BackendError("Cannot read file"));
                return;
            }

            String htmlContent = Files.readString(path);
            ctx.contentType("text/html").html(htmlContent);
        } catch (IOException e) {
            ctx
                .status(500)
                .json(
                    new BackendError("Error reading report: " + e.getMessage())
                );
        }
    }
}
