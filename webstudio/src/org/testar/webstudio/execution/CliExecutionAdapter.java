/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.execution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.testar.agent.codex.CodexAgentRunner;
import org.testar.agent.codex.CodexAgentSettings;
import org.testar.agent.codex.CodexRunResult;
import org.testar.config.ConfigTags;
import org.testar.webstudio.api.dto.ResultFileDto;
import org.testar.webstudio.api.dto.ResultFileSummaryDto;
import org.testar.webstudio.api.dto.ResultOutputGroupDto;
import org.testar.webstudio.api.dto.ScriptlessResultsDto;
import org.testar.webstudio.api.dto.CliAgentSettingsDto;
import org.testar.webstudio.api.dto.ExecutionStatusDto;
import org.testar.webstudio.workspace.WorkspaceService;

public final class CliExecutionAdapter implements ExecutionAdapter {

    private static final int MAX_CONSOLE_LINES = 250;
    private static final Pattern COMMAND_TOKEN_PATTERN = Pattern.compile("\"([^\"]*)\"|'([^']*)'|(\\S+)");
    private static final Pattern HTML_RESOURCE_ATTRIBUTE_PATTERN = Pattern.compile("(\\b(?:src|href)\\s*=\\s*[\"'])([^\"']*)([\"'])");
    private static final Pattern STATIC_HTML_ASSET_PATTERN = Pattern.compile(".+\\.(?:html?|css|js|png|jpe?g|gif|svg|ico|bmp|webp|woff2?|ttf|eot)$", Pattern.CASE_INSENSITIVE);

    private final WorkspaceService workspaceService;
    private final CliDebugLog debugLog;
    private final Gson gson;
    private final List<String> consoleLines;
    private final CodexAgentRunner codexAgentRunner;
    private String currentWorkspace;
    private String currentCliMode;
    private String lastMessage;
    private String lastStatus;
    private boolean manualSessionExpected;
    private CodexAgentRunner.RunningCodexRun activeAgentRun;
    private Long agentStartedAtEpochMillis;

    public CliExecutionAdapter(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
        this.debugLog = new CliDebugLog(workspaceService.testarHomeDirectory().resolve("cli-debug.log"));
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.consoleLines = new ArrayList<>();
        this.codexAgentRunner = new CodexAgentRunner(workspaceService.testarHomeDirectory());
        this.lastMessage = "CLI execution adapter is available";
        this.lastStatus = "idle";
        this.debugLog.log("CliExecutionAdapter initialized. testarHome=" + workspaceService.testarHomeDirectory());
    }

    @Override
    public ExecutionBackend backend() {
        return ExecutionBackend.CLI;
    }

    @Override
    public synchronized ExecutionStatusDto status() {
        if (isAgentExecutionRunning()) {
            lastStatus = "running";
            lastMessage = "Agent CLI execution active";
            return buildStatus(lastStatus, lastMessage);
        }

        if ("agent".equals(currentCliMode) && activeAgentRun != null) {
            completeAgentExecution();
            return buildStatus(lastStatus, lastMessage);
        }

        if (manualSessionExpected) {
            try {
                CliInvocationResult result = invokeCliCommand(List.of("sessionStatus"), false, false);
                manualSessionExpected = isActiveSession(result.lines());
                currentCliMode = manualSessionExpected ? "manual" : null;
                lastMessage = manualSessionExpected
                    ? "Manual CLI session active"
                    : "Manual CLI session is idle";
                lastStatus = manualSessionExpected ? "running" : "idle";
            } catch (RuntimeException exception) {
                lastMessage = "Unable to refresh CLI session status: " + exception.getMessage();
                lastStatus = "error";
                return buildStatus(lastStatus, lastMessage);
            }
        }

        return buildStatus(lastStatus, lastMessage);
    }

    public synchronized ExecutionStatusDto startManualSession(String workspaceName) {
        debugLog.log("startManualSession workspace=" + workspaceName);
        if (isAgentExecutionRunning()) {
            throw new IllegalStateException("Stop the agent CLI execution before starting a manual CLI session.");
        }

        if (workspaceName == null || workspaceName.isBlank()) {
            throw new IllegalArgumentException("CLI workspace is required");
        }

        currentWorkspace = workspaceName;
        currentCliMode = "manual";

        CliInvocationResult result = invokeCliCommand(List.of("startSession", workspaceName), true);
        manualSessionExpected = result.exitCode() == 0;
        lastMessage = manualSessionExpected
            ? "Manual CLI session started for workspace " + workspaceName
            : "Unable to start manual CLI session";
        lastStatus = manualSessionExpected ? "running" : "error";
        return status();
    }

    public synchronized List<String> profiles() {
        Path settingsDirectory = resolveCliInstallDirectory().resolve("settings");
        if (!Files.isDirectory(settingsDirectory)) {
            return List.of();
        }

        try (Stream<Path> children = Files.list(settingsDirectory)) {
            return children
                .filter(Files::isDirectory)
                .filter(path -> Files.isRegularFile(path.resolve("test.settings")))
                .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                .map(path -> path.getFileName().toString())
                .collect(Collectors.toList());
        } catch (IOException exception) {
            debugLog.log("profiles failed", exception);
            throw new IllegalStateException("Unable to list CLI profiles", exception);
        }
    }

    public synchronized ExecutionStatusDto runManualCommand(String commandLine) {
        debugLog.log("runManualCommand commandLine=" + commandLine);
        if (isAgentExecutionRunning()) {
            throw new IllegalStateException("Stop the agent CLI execution before sending manual CLI commands.");
        }

        if (commandLine == null || commandLine.isBlank()) {
            throw new IllegalArgumentException("CLI command line is required");
        }

        List<String> arguments = parseCommandLine(commandLine);
        if (arguments.isEmpty()) {
            throw new IllegalArgumentException("CLI command line is required");
        }

        String commandName = arguments.get(0);
        CliInvocationResult result = invokeCliCommand(arguments, true);
        if ("stopSession".equalsIgnoreCase(commandName)) {
            CliInvocationResult shutdownResult = invokeCliCommand(List.of("shutdownDaemon"), true);
            manualSessionExpected = false;
            currentCliMode = null;
            lastMessage = result.exitCode() == 0 && shutdownResult.exitCode() == 0
                ? "CLI command executed: stopSession + shutdownDaemon"
                : "CLI command failed: stopSession + shutdownDaemon";
            lastStatus = result.exitCode() == 0 && shutdownResult.exitCode() == 0 ? "idle" : "error";
            return buildStatus(lastStatus, lastMessage);
        } else if ("shutdownDaemon".equalsIgnoreCase(commandName)) {
            manualSessionExpected = false;
            currentCliMode = null;
            lastMessage = result.exitCode() == 0
                ? "CLI command executed: shutdownDaemon"
                : "CLI command failed: shutdownDaemon";
            lastStatus = result.exitCode() == 0 ? "idle" : "error";
            return buildStatus(lastStatus, lastMessage);
        } else if ("startSession".equalsIgnoreCase(commandName) && result.exitCode() == 0) {
            manualSessionExpected = true;
            currentCliMode = "manual";
        }

        lastMessage = result.exitCode() == 0
            ? "CLI command executed: " + commandName
            : "CLI command failed: " + commandName;
        lastStatus = result.exitCode() == 0 ? "running" : "error";
        return status();
    }

    public synchronized ExecutionStatusDto stopManualSession() {
        debugLog.log("stopManualSession invoked");
        if (isAgentExecutionRunning()) {
            throw new IllegalStateException("Use Stop Agent Execution while an agent CLI execution is running.");
        }

        CliInvocationResult result = invokeCliCommand(List.of("stopSession"), true);
        CliInvocationResult shutdownResult = invokeCliCommand(List.of("shutdownDaemon"), true);
        manualSessionExpected = false;
        currentCliMode = null;
        boolean success = result.exitCode() == 0 && shutdownResult.exitCode() == 0;
        lastMessage = success
            ? "Manual CLI session stopped and daemon shutdown"
            : "Unable to stop manual CLI session cleanly";
        lastStatus = success ? "idle" : "error";
        return buildStatus(lastStatus, lastMessage);
    }

    public synchronized ExecutionStatusDto startAgentSession(String workspaceName) {
        debugLog.log("startAgentSession workspace=" + workspaceName);
        if (manualSessionExpected) {
            throw new IllegalStateException("Stop the manual CLI session before starting an agent CLI execution.");
        }

        if (isAgentExecutionRunning()) {
            throw new IllegalStateException("An agent CLI execution is already running.");
        }

        if (workspaceName == null || workspaceName.isBlank()) {
            throw new IllegalArgumentException("CLI workspace is required");
        }

        CliAgentSettingsDto settings = loadAgentSettings(workspaceName);
        Path projectRoot = resolveProjectRoot();
        Path cliLauncher = resolveCliLauncher();
        String prompt = buildAgentPrompt(settings, workspaceName, cliLauncher);

        appendConsoleLine("> agent workspace=" + workspaceName);
        appendConsoleLine("> Codex SDK runner");
        appendConsoleLine("> working directory: " + projectRoot);

        try {
            activeAgentRun = codexAgentRunner.start(
                toCodexAgentSettings(settings),
                prompt,
                projectRoot,
                List.of(resolveCliInstallDirectory()),
                new CodexAgentRunner.LogListener() {
                    @Override
                    public void onStdout(String line) {
                        appendConsoleLine(line);
                    }

                    @Override
                    public void onStderr(String line) {
                        appendConsoleLine("[stderr] " + line);
                    }
                }
            );

            currentWorkspace = workspaceName;
            currentCliMode = "agent";
            agentStartedAtEpochMillis = System.currentTimeMillis();
            lastStatus = "running";
            lastMessage = "Agent CLI execution started for workspace " + workspaceName;
            return buildStatus(lastStatus, lastMessage);
        } catch (IOException | RuntimeException exception) {
            debugLog.log("startAgentSession failed", exception);
            lastStatus = "error";
            lastMessage = "Unable to start agent CLI execution: " + exception.getMessage();
            throw new IllegalStateException(lastMessage, exception);
        }
    }

    public synchronized ExecutionStatusDto stopAgentSession() {
        debugLog.log("stopAgentSession invoked");
        if (!isAgentExecutionRunning()) {
            lastStatus = "idle";
            lastMessage = "Agent CLI execution is not running";
            return buildStatus(lastStatus, lastMessage);
        }

        appendConsoleLine("[agent] stop requested");
        activeAgentRun.requestStop();
        completeAgentExecution();
        return buildStatus(lastStatus, lastMessage);
    }

    public synchronized ScriptlessResultsDto cliResults() {
        List<ResultOutputGroupDto> groups = loadResultGroups();
        if (groups.isEmpty()) {
            return new ScriptlessResultsDto("", List.of(), List.of());
        }

        ResultOutputGroupDto latestGroup = groups.get(groups.size() - 1);
        return new ScriptlessResultsDto(latestGroup.path(), groups, latestGroup.files());
    }

    public synchronized ResultFileDto readCliResultFile(String fileName, String filePath) {
        ScriptlessResultsDto results = cliResults();
        ResultFileSummaryDto summary = null;

        if (filePath != null && !filePath.isBlank()) {
            summary = results.groups()
                .stream()
                .flatMap(group -> group.files().stream())
                .filter(file -> file.path().equals(filePath))
                .findFirst()
                .orElse(null);
        }

        if (summary == null) {
            summary = results.groups()
                .stream()
                .flatMap(group -> group.files().stream())
                .filter(file -> file.name().equals(fileName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Result file not found: " + fileName));
        }

        Path resultFilePath = Paths.get(summary.path()).normalize();
        try {
            String content = Files.readString(resultFilePath, StandardCharsets.UTF_8);
            if ("text/html".equals(summary.contentType())) {
                content = rewriteHtmlAssetUrls(content, resultFilePath);
            }
            return new ResultFileDto(summary.name(), summary.path(), summary.contentType(), content);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read CLI result file: " + resultFilePath, exception);
        }
    }

    public synchronized ScriptlessResultsDto deleteCliResultFile(String filePath) {
        Path outputDirectory = resolveCliInstallDirectory().resolve("output").toAbsolutePath().normalize();
        ResultArtifactDeletion.deleteResultFile(outputDirectory, filePath);
        return cliResults();
    }

    public synchronized ScriptlessResultsDto deleteCliResultGroup(String groupPath) {
        Path outputDirectory = resolveCliInstallDirectory().resolve("output").toAbsolutePath().normalize();
        ResultArtifactDeletion.deleteResultGroup(outputDirectory, groupPath);
        return cliResults();
    }

    public synchronized Path resolveCliResultAsset(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("Result asset path is required");
        }

        Path outputDirectory = resolveCliInstallDirectory().resolve("output").toAbsolutePath().normalize();
        Path assetPath = Paths.get(filePath).toAbsolutePath().normalize();

        if (!assetPath.startsWith(outputDirectory)) {
            throw new IllegalArgumentException("Invalid result asset path: " + assetPath);
        }

        if (!Files.isRegularFile(assetPath)) {
            throw new IllegalArgumentException("Result asset not found: " + assetPath.getFileName());
        }

        return assetPath;
    }

    public synchronized String cliResultAssetContentType(String filePath) {
        String lowerCasePath = filePath == null ? "" : filePath.toLowerCase();
        if (lowerCasePath.endsWith(".png")) {
            return "image/png";
        }
        if (lowerCasePath.endsWith(".jpg") || lowerCasePath.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (lowerCasePath.endsWith(".gif")) {
            return "image/gif";
        }
        if (lowerCasePath.endsWith(".svg")) {
            return "image/svg+xml";
        }
        if (lowerCasePath.endsWith(".css")) {
            return "text/css";
        }
        if (lowerCasePath.endsWith(".js")) {
            return "application/javascript";
        }
        return "application/octet-stream";
    }

    private CliAgentSettingsDto loadAgentSettings(String workspaceName) {
        Path settingsFile = workspaceService.workspaceDirectory(workspaceName).resolve("test.settings");
        if (!Files.isRegularFile(settingsFile)) {
            return defaultAgentSettings();
        }

        Properties properties = new Properties();
        try (BufferedReader reader = Files.newBufferedReader(settingsFile, StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read Agent CLI settings from: " + settingsFile, exception);
        }

        return normalizeAgentSettings(new CliAgentSettingsDto(
            properties.getProperty(ConfigTags.AgentCLIApiKeyEnvVar.name()),
            properties.getProperty(ConfigTags.AgentCLIBaseUrl.name()),
            properties.getProperty(ConfigTags.AgentCLIModel.name()),
            properties.getProperty(ConfigTags.AgentCLIReasoningEffort.name()),
            properties.getProperty(ConfigTags.AgentCLISandboxMode.name()),
            properties.getProperty(ConfigTags.AgentCLIApprovalPolicy.name()),
            booleanProperty(properties, ConfigTags.AgentCLINetworkAccessEnabled.name()),
            booleanProperty(properties, ConfigTags.AgentCLISkipGitRepoCheck.name()),
            properties.getProperty(ConfigTags.AgentCLIPromptTitle.name()),
            properties.getProperty(ConfigTags.AgentCLIPromptText.name())
        ));
    }

    private Boolean booleanProperty(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            return null;
        }

        return Boolean.parseBoolean(value.trim());
    }

    private CliAgentSettingsDto normalizeAgentSettings(CliAgentSettingsDto settings) {
        CliAgentSettingsDto source = settings == null ? defaultAgentSettings() : settings;
        return new CliAgentSettingsDto(
            source.apiKeyEnvVarName(),
            source.baseUrl(),
            source.model(),
            source.reasoningEffort(),
            source.sandboxMode(),
            source.approvalPolicy(),
            source.networkAccessEnabled(),
            source.skipGitRepoCheck(),
            source.promptTitle(),
            source.promptText()
        );
    }

    private CliAgentSettingsDto defaultAgentSettings() {
        return new CliAgentSettingsDto(
            "OPENAI_API_KEY",
            "",
            "gpt-5.4-mini",
            "medium",
            "danger-full-access",
            "never",
            Boolean.FALSE,
            Boolean.TRUE,
            "Test Parabank Login",
            "As a test agent verify that you can log in with the credentials john/demo. Then the Welcome John Smith message is shown."
        );
    }

    private CliInvocationResult invokeCliCommand(List<String> arguments, boolean recordOutput) {
        return invokeCliCommand(arguments, recordOutput, true);
    }

    private CliInvocationResult invokeCliCommand(List<String> arguments, boolean recordOutput, boolean logInvocation) {
        List<String> command = new ArrayList<>();
        Path launcher = resolveCliLauncher();
        if (logInvocation) {
            debugLog.log("invokeCliCommand launcher=" + launcher + ", arguments=" + arguments);
        }

        if (recordOutput) {
            appendConsoleLine("> " + String.join(" ", arguments));
        }

        if (isWindows()) {
            command.add("cmd");
            command.add("/c");
            command.add(launcher.toString());
        } else {
            command.add(launcher.toString());
        }
        command.addAll(arguments);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(resolveCliInstallDirectory().toFile());
        processBuilder.redirectErrorStream(true);
        if (logInvocation) {
            debugLog.log("invokeCliCommand processDir=" + processBuilder.directory());
        }

        try {
            Process process = processBuilder.start();
            List<String> lines = readProcessOutput(process);
            int exitCode = process.waitFor();
            if (logInvocation) {
                debugLog.log("invokeCliCommand completed exitCode=" + exitCode + ", outputLines=" + lines.size());
            }
            if (recordOutput) {
                appendProcessOutput(lines);
            }
            return new CliInvocationResult(exitCode, lines);
        } catch (IOException exception) {
            debugLog.log("invokeCliCommand failed", exception);
            throw new IllegalStateException("Unable to invoke CLI launcher", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            debugLog.log("invokeCliCommand interrupted", exception);
            throw new IllegalStateException("CLI command execution interrupted", exception);
        }
    }

    private List<String> readProcessOutput(Process process) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)
        )) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    private void appendProcessOutput(List<String> lines) {
        if (lines.isEmpty()) {
            appendConsoleLine("(no output)");
            return;
        }

        for (String line : lines) {
            appendConsoleLine(line);
        }
    }

    private boolean isActiveSession(List<String> lines) {
        for (String line : lines) {
            if ("active=true".equalsIgnoreCase(line.trim())) {
                return true;
            }
        }

        return false;
    }

    private List<String> parseCommandLine(String commandLine) {
        List<String> arguments = new ArrayList<>();
        Matcher matcher = COMMAND_TOKEN_PATTERN.matcher(commandLine);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                arguments.add(matcher.group(1));
            } else if (matcher.group(2) != null) {
                arguments.add(matcher.group(2));
            } else if (matcher.group(3) != null) {
                arguments.add(matcher.group(3));
            }
        }
        return arguments;
    }

    private synchronized void appendConsoleLine(String line) {
        consoleLines.add(line == null ? "" : line);
        while (consoleLines.size() > MAX_CONSOLE_LINES) {
            consoleLines.remove(0);
        }
    }

    private synchronized String consoleOutput() {
        StringBuilder builder = new StringBuilder();
        for (String line : consoleLines) {
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(line);
        }
        return builder.toString();
    }

    private ExecutionStatusDto buildStatus(String status, String message) {
        return new ExecutionStatusDto(
            "cli",
            status,
            message,
            currentWorkspace,
            currentCliMode,
            consoleOutput(),
            agentStartedAtEpochMillis,
            null,
            List.of()
        );
    }

    private boolean isAgentExecutionRunning() {
        return activeAgentRun != null && activeAgentRun.isAlive();
    }

    private void completeAgentExecution() {
        if (activeAgentRun == null) {
            currentCliMode = null;
            agentStartedAtEpochMillis = null;
            return;
        }

        try {
            CodexRunResult result = activeAgentRun.awaitCompletion();
            if (!result.finalResponse().isBlank()) {
                appendConsoleLine("[agent] final response:");
                for (String line : result.finalResponse().split("\\R")) {
                    appendConsoleLine(line);
                }
            }
            if (result.runDirectory() != null) {
                appendConsoleLine("[agent] run directory: " + result.runDirectory());
            }
            lastStatus = "idle";
            lastMessage = result.success()
                ? result.toNotificationMessage()
                : "Agent CLI execution finished with error: " + result.toNotificationMessage();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            debugLog.log("completeAgentExecution interrupted", exception);
            lastStatus = "idle";
            lastMessage = "Agent CLI execution finished with error: interrupted.";
        } catch (IOException | RuntimeException exception) {
            debugLog.log("completeAgentExecution failed", exception);
            lastStatus = "idle";
            lastMessage = "Agent CLI execution finished with error: " + exception.getMessage();
        } finally {
            shutdownDaemonBestEffort(false);
            currentCliMode = null;
            agentStartedAtEpochMillis = null;
            activeAgentRun = null;
        }
    }

    private CodexAgentSettings toCodexAgentSettings(CliAgentSettingsDto settings) {
        return new CodexAgentSettings(
            settings.apiKeyEnvVarName(),
            settings.baseUrl(),
            settings.model(),
            settings.reasoningEffort(),
            settings.sandboxMode(),
            settings.approvalPolicy(),
            Boolean.TRUE.equals(settings.networkAccessEnabled()),
            Boolean.TRUE.equals(settings.skipGitRepoCheck()),
            settings.promptTitle(),
            settings.promptText()
        );
    }

    private String buildAgentPrompt(CliAgentSettingsDto settings,
                                    String workspaceName,
                                    Path cliLauncher) {
        Path skillDirectory = resolveCliSkillDirectory();
        Path skillFile = skillDirectory.resolve("SKILL.md");
        String skillInstructions = readCliSkillInstructions(skillDirectory);
        StringBuilder prompt = new StringBuilder();
        prompt.append(settings.promptTitle()).append('\n').append('\n');
        prompt.append("You are controlling TESTAR through testar-cli.").append('\n');
        prompt.append("Selected workspace: ").append(workspaceName).append('\n');
        prompt.append("TESTAR CLI launcher: ").append(cliLauncher).append('\n');
        prompt.append("Repository root: ").append(resolveProjectRoot()).append('\n');
        prompt.append("CLI skill directory: ").append(skillDirectory).append('\n');
        prompt.append("CLI skill file: ").append(skillFile).append('\n').append('\n');
        prompt.append("Execution rule: read and follow the TESTAR CLI skill instructions before issuing commands.").append('\n');
        prompt.append("Use startSession ").append(workspaceName)
            .append(" so TESTAR CLI derives platform and target from the selected workspace settings.")
            .append('\n')
            .append('\n');
        if (!settings.apiKeyEnvVarName().isBlank()) {
            prompt.append("Expected API key environment variable for Codex authentication: ")
                .append(settings.apiKeyEnvVarName())
                .append('\n')
                .append('\n');
        }
        prompt.append("Authoritative TESTAR CLI skill instructions:").append('\n');
        prompt.append(skillInstructions).append('\n').append('\n');
        prompt.append("User goal:").append('\n');
        prompt.append(
            settings.promptText().isBlank()
                ? "Inspect the target and perform a meaningful CLI-driven exploration."
                : settings.promptText()
        ).append('\n');
        return prompt.toString();
    }

    private Path resolveProjectRoot() {
        return Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
    }

    private Path resolveCliSkillDirectory() {
        Path skillDirectory = resolveCliInstallDirectory()
            .resolve(".agents")
            .resolve("skills")
            .resolve("testar-cli");

        if (!Files.isDirectory(skillDirectory)) {
            throw new IllegalStateException(
                "CLI skill directory not found: " + skillDirectory + ". Run :cli:installCliIntoTestarDistribution again."
            );
        }

        return skillDirectory;
    }

    private String readCliSkillInstructions(Path skillDirectory) {
        try (Stream<Path> files = Files.list(skillDirectory)) {
            List<Path> markdownFiles = files
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().toLowerCase().endsWith(".md"))
                .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                .collect(Collectors.toList());

            if (markdownFiles.isEmpty()) {
                throw new IllegalStateException("No CLI skill markdown files found in: " + skillDirectory);
            }

            StringBuilder content = new StringBuilder();
            for (Path markdownFile : markdownFiles) {
                if (content.length() > 0) {
                    content.append('\n').append('\n');
                }

                content.append("File: ").append(markdownFile.getFileName()).append('\n').append('\n');
                content.append(Files.readString(markdownFile, StandardCharsets.UTF_8).trim());
            }

            return content.toString();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read CLI skill instructions from: " + skillDirectory, exception);
        }
    }

    private Path resolveCliInstallDirectory() {
        debugLog.log("resolveCliInstallDirectory invoked");
        Path workingDirectory = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        Path current = workingDirectory;

        while (current != null) {
            Path sharedCandidate = current.resolve("testar").resolve("target").resolve("install").resolve("testar").resolve("bin");
            if (Files.isRegularFile(sharedCandidate.resolve(isWindows() ? "testar-cli.bat" : "testar-cli"))) {
                debugLog.log("resolveCliInstallDirectory found shared distribution at " + sharedCandidate);
                return sharedCandidate;
            }

            Path candidate = current.resolve("cli").resolve("target").resolve("install").resolve("testar-cli");
            if (Files.isDirectory(candidate)) {
                debugLog.log("resolveCliInstallDirectory found existing distribution at " + candidate);
                return candidate;
            }

            current = current.getParent();
        }

        debugLog.log("resolveCliInstallDirectory failed. CLI distribution not found.");
        throw new IllegalStateException("Unable to find TESTAR CLI launcher. Run :cli:installCliIntoTestarDistribution first.");
    }

    private Path resolveCliLauncher() {
        Path installDirectory = resolveCliInstallDirectory();
        Path launcher = installDirectory.resolve(isWindows() ? "testar-cli.bat" : "testar-cli");
        if (!Files.isRegularFile(launcher)) {
            debugLog.log("resolveCliLauncher failed. Missing launcher=" + launcher);
            throw new IllegalStateException("CLI launcher not found: " + launcher);
        }

        debugLog.log("resolveCliLauncher resolved launcher=" + launcher);
        return launcher;
    }

    private boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("windows");
    }

    private void shutdownDaemonBestEffort(boolean updateMessage) {
        try {
            CliInvocationResult shutdownResult = invokeCliCommand(List.of("shutdownDaemon"), true);
            if (updateMessage && shutdownResult.exitCode() == 0) {
                lastMessage = "Agent CLI execution stopped and daemon shutdown";
                lastStatus = "idle";
            }
        } catch (RuntimeException exception) {
            debugLog.log("shutdownDaemonBestEffort failed", exception);
        }
        manualSessionExpected = false;
        currentCliMode = null;
    }

    private List<ResultOutputGroupDto> loadResultGroups() {
        Path outputDirectory = resolveCliInstallDirectory().resolve("output");
        if (!Files.isDirectory(outputDirectory)) {
            return List.of();
        }

        try (Stream<Path> children = Files.list(outputDirectory)) {
            return children
                .filter(Files::isDirectory)
                .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                .map(this::toResultOutputGroup)
                .filter(group -> group != null && !group.files().isEmpty())
                .toList();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to inspect CLI output directory: " + outputDirectory, exception);
        }
    }

    private ResultOutputGroupDto toResultOutputGroup(Path outputGroupDirectory) {
        Path reportsDirectory = outputGroupDirectory.resolve("reports");
        if (!Files.isDirectory(reportsDirectory)) {
            return null;
        }

        List<ResultFileSummaryDto> files = new ArrayList<>();
        collectPreviewableFiles(reportsDirectory, files);
        files.sort(Comparator.comparing(ResultFileSummaryDto::path));

        return new ResultOutputGroupDto(
            outputGroupDirectory.getFileName().toString(),
            outputGroupDirectory.toString(),
            resultStatusFor(files),
            files.size(),
            files
        );
    }

    private void collectPreviewableFiles(Path reportsDirectory, List<ResultFileSummaryDto> files) {
        try (Stream<Path> children = Files.list(reportsDirectory)) {
            children
                .filter(Files::isRegularFile)
                .filter(this::isPreviewableResult)
                .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                .map(path -> new ResultFileSummaryDto(
                    path.getFileName().toString(),
                    path.toString(),
                    contentTypeFor(path),
                    resultStatusFor(path)
                ))
                .forEach(files::add);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to list CLI result files under: " + reportsDirectory, exception);
        }
    }

    private String resultStatusFor(Path path) {
        String upperCaseFileName = path.getFileName().toString().toUpperCase();
        Matcher verdictMatcher = Pattern.compile("_V\\d+_([^.]+)\\.HTML?$").matcher(upperCaseFileName);
        if (verdictMatcher.find()) {
            return "OK".equals(verdictMatcher.group(1)) ? "ok" : "failed";
        }

        return "ok";
    }

    private String resultStatusFor(List<ResultFileSummaryDto> files) {
        for (ResultFileSummaryDto file : files) {
            if ("failed".equals(file.status())) {
                return "failed";
            }
        }

        return "ok";
    }

    private String contentTypeFor(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
            return "text/html";
        }
        return "text/plain";
    }

    private boolean isPreviewableResult(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        return fileName.endsWith(".html") || fileName.endsWith(".htm");
    }

    private String rewriteHtmlAssetUrls(String htmlContent, Path htmlFilePath) {
        Matcher matcher = HTML_RESOURCE_ATTRIBUTE_PATTERN.matcher(htmlContent);
        StringBuffer rewrittenHtml = new StringBuffer();

        while (matcher.find()) {
            String attributePrefix = matcher.group(1);
            String relativeAssetPath = matcher.group(2).trim();
            String attributeSuffix = matcher.group(3);
            String replacement = matcher.group(0);

            if (!isStaticHtmlAssetReference(relativeAssetPath)) {
                matcher.appendReplacement(rewrittenHtml, Matcher.quoteReplacement(replacement));
                continue;
            }

            Path resolvedAssetPath = htmlFilePath.getParent().resolve(relativeAssetPath).normalize();

            try {
                Path validAssetPath = resolveCliResultAsset(resolvedAssetPath.toString());
                String encodedAssetPath = URLEncoder.encode(validAssetPath.toString(), StandardCharsets.UTF_8);
                String assetUrl = "/api/execution/cli/result-asset?path=" + encodedAssetPath;
                replacement = attributePrefix + assetUrl + attributeSuffix;
            } catch (IllegalArgumentException ignored) {
                replacement = matcher.group(0);
            }

            matcher.appendReplacement(rewrittenHtml, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(rewrittenHtml);
        return rewrittenHtml.toString();
    }

    private boolean isStaticHtmlAssetReference(String relativeAssetPath) {
        if (relativeAssetPath == null || relativeAssetPath.isBlank()) {
            return false;
        }

        String trimmedAssetPath = relativeAssetPath.trim();
        String lowerCaseAssetPath = trimmedAssetPath.toLowerCase();

        if (lowerCaseAssetPath.startsWith("http:")
            || lowerCaseAssetPath.startsWith("https:")
            || lowerCaseAssetPath.startsWith("data:")
            || lowerCaseAssetPath.startsWith("javascript:")
            || lowerCaseAssetPath.startsWith("//")
            || lowerCaseAssetPath.startsWith("#")) {
            return false;
        }

        if (trimmedAssetPath.contains("&")
            || trimmedAssetPath.contains("?")
            || trimmedAssetPath.contains("+")
            || trimmedAssetPath.contains("<")
            || trimmedAssetPath.contains(">")
            || trimmedAssetPath.contains("{")
            || trimmedAssetPath.contains("}")
            || trimmedAssetPath.contains("(")
            || trimmedAssetPath.contains(")")
            || trimmedAssetPath.contains("\n")
            || trimmedAssetPath.contains("\r")
            || trimmedAssetPath.contains("\t")) {
            return false;
        }

        String normalizedAssetPath = trimmedAssetPath.replace('\\', '/');
        return STATIC_HTML_ASSET_PATTERN.matcher(normalizedAssetPath).matches();
    }

    private static final class CliInvocationResult {

        private final int exitCode;
        private final List<String> lines;

        private CliInvocationResult(int exitCode, List<String> lines) {
            this.exitCode = exitCode;
            this.lines = lines;
        }

        private int exitCode() {
            return exitCode;
        }

        private List<String> lines() {
            return lines;
        }
    }
}
