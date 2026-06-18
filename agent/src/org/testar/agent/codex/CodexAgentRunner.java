/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.agent.codex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class CodexAgentRunner {

    private static final String RESOURCE_ROOT = "/codex";
    private static final String PACKAGE_JSON = "package.json";
    private static final String RUNNER_SCRIPT = "testar-codex-runner.mjs";
    private static final DateTimeFormatter RUN_TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").withZone(ZoneId.systemDefault());
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path runtimeHomeDirectory;

    public CodexAgentRunner(Path runtimeHomeDirectory) {
        this.runtimeHomeDirectory = runtimeHomeDirectory.toAbsolutePath().normalize();
    }

    public RunningCodexRun start(CodexAgentSettings settings,
                                 String prompt,
                                 Path workingDirectory,
                                 List<Path> additionalDirectories,
                                 LogListener logListener) throws IOException {
        if (settings == null) {
            throw new IllegalArgumentException("Codex agent settings are required.");
        }

        if (workingDirectory == null) {
            throw new IllegalArgumentException("Codex working directory is required.");
        }

        Path runtimeDirectory = resolveRuntimeDirectory();
        ensureNodeAvailable();
        validateRuntime(runtimeDirectory);

        Path runDirectory = createRunDirectory(settings);
        Path requestFile = runDirectory.resolve("request.json");
        Path stdoutFile = runDirectory.resolve("stdout.log");
        Path stderrFile = runDirectory.resolve("stderr.log");
        Path resultFile = runDirectory.resolve("result.json");

        Map<String, Object> request = new LinkedHashMap<>();
        request.put("apiKeyEnvVarName", settings.apiKeyEnvVarName());
        request.put("baseUrl", settings.baseUrl());
        request.put("model", settings.model());
        request.put("reasoningEffort", settings.reasoningEffort());
        request.put("sandboxMode", settings.sandboxMode());
        request.put("approvalPolicy", settings.approvalPolicy());
        request.put("networkAccessEnabled", settings.networkAccessEnabled());
        request.put("skipGitRepoCheck", settings.skipGitRepoCheck());
        request.put("workingDirectory", workingDirectory.toAbsolutePath().normalize().toString());
        request.put("additionalDirectories", normalizeAdditionalDirectories(additionalDirectories));
        request.put("prompt", prompt == null ? "" : prompt);
        request.put("outputDirectory", runDirectory.toAbsolutePath().normalize().toString());
        Files.writeString(requestFile, GSON.toJson(request), StandardCharsets.UTF_8);

        ProcessBuilder processBuilder = new ProcessBuilder(
                "node",
                runtimeDirectory.resolve(RUNNER_SCRIPT).toString(),
                requestFile.toString()
        );
        processBuilder.directory(runtimeDirectory.toFile());

        Process process = processBuilder.start();
        RunningCodexRun runningRun = new RunningCodexRun(
                process,
                runDirectory,
                stdoutFile,
                stderrFile,
                resultFile,
                logListener
        );
        runningRun.startStreaming();
        return runningRun;
    }

    public interface LogListener {

        void onStdout(String line);

        void onStderr(String line);
    }

    public final class RunningCodexRun {

        private final Process process;
        private final Path runDirectory;
        private final Path stdoutFile;
        private final Path stderrFile;
        private final Path resultFile;
        private final LogListener logListener;
        private final AtomicBoolean stopRequested;
        private final CountDownLatch streamLatch;

        private RunningCodexRun(Process process,
                                Path runDirectory,
                                Path stdoutFile,
                                Path stderrFile,
                                Path resultFile,
                                LogListener logListener) {
            this.process = process;
            this.runDirectory = runDirectory;
            this.stdoutFile = stdoutFile;
            this.stderrFile = stderrFile;
            this.resultFile = resultFile;
            this.logListener = logListener;
            this.stopRequested = new AtomicBoolean(false);
            this.streamLatch = new CountDownLatch(2);
        }

        public Path runDirectory() {
            return runDirectory;
        }

        public boolean isAlive() {
            return process.isAlive();
        }

        public void requestStop() {
            if (!stopRequested.compareAndSet(false, true)) {
                return;
            }

            destroyProcessTree(process.toHandle());
            process.destroy();
            if (process.isAlive()) {
                process.destroyForcibly();
            }
        }

        public CodexRunResult awaitCompletion() throws InterruptedException, IOException {
            int exitCode = process.waitFor();
            streamLatch.await();

            if (stopRequested.get() && !Files.exists(resultFile)) {
                return new CodexRunResult(
                        false,
                        "cancelled",
                        "",
                        "",
                        "Codex agent execution cancelled.",
                        runDirectory,
                        null
                );
            }

            if (!Files.exists(resultFile)) {
                String stderr = Files.exists(stderrFile) ? Files.readString(stderrFile) : "";
                throw new IllegalStateException("Codex runner did not produce result.json. " + stderr.trim());
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> result = GSON.fromJson(Files.readString(resultFile), Map.class);
            String status = readString(result, "status");
            String threadId = readString(result, "threadId");
            String finalResponse = readString(result, "finalResponse");
            String errorMessage = readString(result, "errorMessage");

            CodexRunResult.Usage usage = null;
            Object usageObject = result.get("usage");
            if (usageObject instanceof Map<?, ?> usageMap) {
                usage = new CodexRunResult.Usage(
                        readInt(usageMap, "input_tokens"),
                        readInt(usageMap, "cached_input_tokens"),
                        readInt(usageMap, "output_tokens"),
                        readInt(usageMap, "reasoning_output_tokens")
                );
            }

            boolean success = exitCode == 0 && "completed".equalsIgnoreCase(status);
            if (!success && errorMessage.isBlank()) {
                String stderr = Files.exists(stderrFile) ? Files.readString(stderrFile) : "";
                errorMessage = stderr.isBlank() ? "Codex agent execution failed." : stderr.trim();
            }

            return new CodexRunResult(
                    success,
                    status,
                    threadId,
                    finalResponse,
                    errorMessage,
                    runDirectory,
                    usage
            );
        }

        private void startStreaming() throws IOException {
            Files.deleteIfExists(stdoutFile);
            Files.deleteIfExists(stderrFile);

            Thread stdoutThread = new Thread(
                    () -> consumeStream(process.getInputStream(), stdoutFile, false),
                    "codex-agent-stdout"
            );
            stdoutThread.setDaemon(true);
            stdoutThread.start();

            Thread stderrThread = new Thread(
                    () -> consumeStream(process.getErrorStream(), stderrFile, true),
                    "codex-agent-stderr"
            );
            stderrThread.setDaemon(true);
            stderrThread.start();
        }

        private void consumeStream(InputStream inputStream, Path targetFile, boolean stderr) {
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                    if (logListener != null) {
                        if (stderr) {
                            logListener.onStderr(line);
                        } else {
                            logListener.onStdout(line);
                        }
                    }
                }
                Files.write(targetFile, lines, StandardCharsets.UTF_8);
            } catch (IOException exception) {
                throw new UncheckedIOException(exception);
            } finally {
                streamLatch.countDown();
            }
        }

        private void destroyProcessTree(ProcessHandle processHandle) {
            processHandle.descendants().forEach(child -> {
                child.destroy();
                if (child.isAlive()) {
                    child.destroyForcibly();
                }
            });
        }
    }

    private Path resolveRuntimeDirectory() throws IOException {
        Path sourceRuntimeDirectory = resolveSourceRuntimeDirectory();
        if (Files.exists(sourceRuntimeDirectory.resolve(PACKAGE_JSON))) {
            return sourceRuntimeDirectory;
        }

        Path extractedRuntimeDirectory = runtimeHomeDirectory.resolve(".runtime").resolve("codex-agent").resolve("runtime");
        Files.createDirectories(extractedRuntimeDirectory);
        copyResource(PACKAGE_JSON, extractedRuntimeDirectory.resolve(PACKAGE_JSON));
        copyResource(RUNNER_SCRIPT, extractedRuntimeDirectory.resolve(RUNNER_SCRIPT));
        return extractedRuntimeDirectory;
    }

    private Path resolveSourceRuntimeDirectory() {
        Path current = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        while (current != null) {
            Path candidate = current.resolve("agent").resolve("resources").resolve("codex");
            if (Files.exists(candidate.resolve(PACKAGE_JSON))) {
                return candidate;
            }
            current = current.getParent();
        }

        return runtimeHomeDirectory.resolve(".runtime").resolve("codex-agent").resolve("runtime");
    }

    private void copyResource(String resourceName, Path targetFile) throws IOException {
        try (InputStream inputStream = CodexAgentRunner.class.getResourceAsStream(RESOURCE_ROOT + "/" + resourceName)) {
            if (inputStream == null) {
                throw new IOException("Missing Codex runtime resource: " + resourceName);
            }
            Files.write(targetFile, inputStream.readAllBytes());
        }
    }

    private void validateRuntime(Path runtimeDirectory) {
        Path packageJson = runtimeDirectory.resolve(PACKAGE_JSON);
        Path sdkPackage = runtimeDirectory.resolve("node_modules").resolve("@openai").resolve("codex-sdk").resolve("package.json");
        if (!Files.exists(packageJson) || !Files.exists(sdkPackage)) {
            throw new IllegalStateException(
                    "Codex SDK runtime dependencies are missing under " + runtimeDirectory
                            + ". Run `npm install` in that directory or execute :agent:installCodexAgent."
            );
        }
    }

    private void ensureNodeAvailable() {
        ProcessBuilder processBuilder = new ProcessBuilder("node", "--version");
        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IllegalStateException("Node.js is required for the Codex SDK runtime.");
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Node.js is required for the Codex SDK runtime.", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while checking Node.js runtime.", exception);
        }
    }

    private Path createRunDirectory(CodexAgentSettings settings) throws IOException {
        String promptTitle = settings.promptTitle().isBlank() ? "Codex Run" : settings.promptTitle();
        String safeName = promptTitle.replaceAll("[\\\\/?:*\"|><]", "_");
        String timestamp = RUN_TIMESTAMP.format(OffsetDateTime.now());
        Path runDirectory = runtimeHomeDirectory.resolve(".runtime")
                .resolve("codex-agent")
                .resolve("runs")
                .resolve(safeName + "_" + timestamp);
        Files.createDirectories(runDirectory);
        return runDirectory;
    }

    private List<String> normalizeAdditionalDirectories(List<Path> additionalDirectories) {
        List<String> normalizedDirectories = new ArrayList<>();
        for (Path directory : additionalDirectories) {
            if (directory == null) {
                continue;
            }
            normalizedDirectories.add(directory.toAbsolutePath().normalize().toString());
        }
        return normalizedDirectories;
    }

    private String readString(Map<String, Object> values, String key) {
        Object value = values.get(key);
        return value == null ? "" : String.valueOf(value);
    }

    private int readInt(Map<?, ?> values, String key) {
        Object value = values.get(key);
        if (value instanceof Number number) {
            return number.intValue();
        }
        return 0;
    }
}
