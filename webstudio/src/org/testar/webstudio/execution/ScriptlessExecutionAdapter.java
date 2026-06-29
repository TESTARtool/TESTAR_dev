/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.execution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testar.core.verdict.Verdict;
import org.testar.webstudio.api.dto.ExecutionStatusDto;
import org.testar.webstudio.api.dto.ResultFileDto;
import org.testar.webstudio.api.dto.ResultFileSummaryDto;
import org.testar.webstudio.api.dto.ResultOutputGroupDto;
import org.testar.webstudio.api.dto.SequenceOutcomeDto;
import org.testar.webstudio.api.dto.ScriptlessResultsDto;

public final class ScriptlessExecutionAdapter implements ExecutionAdapter {

    private static final int MAX_CONSOLE_LINES = 250;
    private static final long COMPLETED_RUN_IDLE_GRACE_MILLIS = 8000L;
    private static final Pattern SEQUENCE_SUMMARY_PATTERN = Pattern.compile("_sequence_(\\d+)");
    private static final Pattern SEQUENCE_OUTPUT_PATH_PATTERN = Pattern.compile("Generate\\s+([^\\s]+_sequence_(\\d+))");
    private static final Pattern HTML_RESOURCE_ATTRIBUTE_PATTERN = Pattern.compile("(\\b(?:src|href)\\s*=\\s*[\"'])([^\"']*)([\"'])");
    private static final Pattern STATIC_HTML_ASSET_PATTERN = Pattern.compile(".+\\.(?:html?|css|js|png|jpe?g|gif|svg|ico|bmp|webp|woff2?|ttf|eot)$", Pattern.CASE_INSENSITIVE);
    private static final Set<String> NON_OK_VERDICT_TITLES = buildNonOkVerdictTitles();

    private Process currentProcess;
    private Path currentInstallBinDirectory;
    private String currentWorkspace;
    private String currentMode;
    private long startedAtEpochMillis;
    private long lastOutputEpochMillis;
    private int plannedSequenceCount;
    private boolean currentSequenceFailed;
    private String lastMessage = "Scriptless execution adapter is available";
    private final List<String> consoleLines = new ArrayList<>();
    private final List<SequenceOutcomeDto> sequenceOutcomes = new ArrayList<>();

    @Override
    public ExecutionBackend backend() {
        return ExecutionBackend.SCRIPTLESS;
    }

    @Override
    public synchronized ExecutionStatusDto status() {
        if (currentProcess != null && currentProcess.isAlive() && shouldStopCompletedButStuckRun()) {
            appendConsoleLine("Generate run reached the configured sequence count and remained idle. Forcing process shutdown.");
            destroyProcessTree(currentProcess);
        }

        if (currentProcess != null && !currentProcess.isAlive()) {
            int exitCode = currentProcess.exitValue();
            currentProcess = null;
            currentInstallBinDirectory = null;
            currentWorkspace = null;
            currentMode = null;
            startedAtEpochMillis = 0L;
            lastOutputEpochMillis = 0L;
            plannedSequenceCount = 0;
            currentSequenceFailed = false;
            lastMessage = "Scriptless run finished with exit code " + exitCode;
        }

        if (currentProcess == null) {
            return buildStatus("idle", lastMessage);
        }

        return buildStatus("running", "Running " + currentMode + " for workspace " + currentWorkspace);
    }

    public synchronized ExecutionStatusDto startGenerate(String workspaceName, Path settingsRoot) {
        return startMode(workspaceName, settingsRoot, "Generate");
    }

    public synchronized ExecutionStatusDto startLocalSpy(String workspaceName, Path settingsRoot) {
        return startMode(workspaceName, settingsRoot, "Spy");
    }

    private synchronized ExecutionStatusDto startMode(String workspaceName, Path settingsRoot, String mode) {
        if (currentProcess != null && currentProcess.isAlive()) {
            return buildStatus("running", "A scriptless run is already active for workspace " + currentWorkspace);
        }

        Path installBinDirectory;
        try {
            resetConsoleOutput();
            installBinDirectory = resolveInstallBinDirectory();
            prepareWorkspaceForRun(workspaceName, settingsRoot, installBinDirectory, mode);
            currentInstallBinDirectory = installBinDirectory;
        } catch (RuntimeException exception) {
            currentProcess = null;
            currentInstallBinDirectory = null;
            currentWorkspace = null;
            currentMode = null;
            startedAtEpochMillis = 0L;
            lastMessage = "Unable to prepare " + mode + " mode: " + exception.getMessage();
            appendConsoleLine(lastMessage);
            return buildStatus("error", lastMessage);
        }

        ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "testar.bat");
        processBuilder.directory(installBinDirectory.toFile());
        processBuilder.redirectErrorStream(true);

        try {
            currentProcess = processBuilder.start();
            currentWorkspace = workspaceName;
            currentMode = mode;
            startedAtEpochMillis = System.currentTimeMillis();
            lastOutputEpochMillis = startedAtEpochMillis;
            lastMessage = "Started " + mode + " mode for workspace " + workspaceName;
            appendConsoleLine(lastMessage);
            startOutputDrain(currentProcess);
            return status();
        } catch (IOException exception) {
            currentProcess = null;
            currentInstallBinDirectory = null;
            currentWorkspace = null;
            currentMode = null;
            startedAtEpochMillis = 0L;
            lastMessage = "Unable to start " + mode + " mode: " + exception.getMessage();
            appendConsoleLine(lastMessage);
            return buildStatus("error", lastMessage);
        }
    }

    public synchronized ExecutionStatusDto stop() {
        if (currentProcess == null || !currentProcess.isAlive()) {
            currentProcess = null;
            currentInstallBinDirectory = null;
            currentWorkspace = null;
            currentMode = null;
            startedAtEpochMillis = 0L;
            lastOutputEpochMillis = 0L;
            return buildStatus("idle", "No scriptless run is active");
        }

        destroyProcessTree(currentProcess);
        currentProcess = null;
        lastMessage = "Scriptless run stopped";
        appendConsoleLine(lastMessage);
        return buildStatus("idle", lastMessage);
    }

    public synchronized ScriptlessResultsDto scriptlessResults() {
        List<ResultOutputGroupDto> groups = loadResultGroups();
        if (groups.isEmpty()) {
            return new ScriptlessResultsDto("", List.of(), List.of());
        }

        ResultOutputGroupDto latestGroup = groups.get(groups.size() - 1);
        return new ScriptlessResultsDto(latestGroup.path(), groups, latestGroup.files());
    }

    public synchronized ResultFileDto readScriptlessResultFile(String fileName, String filePath) {
        ScriptlessResultsDto results = scriptlessResults();
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
            return new ResultFileDto(
                summary.name(),
                summary.path(),
                summary.contentType(),
                content
            );
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read scriptless result file: " + resultFilePath, exception);
        }
    }

    public synchronized ScriptlessResultsDto deleteScriptlessResultFile(String filePath) {
        Path outputDirectory = resolveInstallBinDirectory().resolve("output").toAbsolutePath().normalize();
        ResultArtifactDeletion.deleteResultFile(outputDirectory, filePath);
        return scriptlessResults();
    }

    public synchronized ScriptlessResultsDto deleteScriptlessResultGroup(String groupPath) {
        Path outputDirectory = resolveInstallBinDirectory().resolve("output").toAbsolutePath().normalize();
        ResultArtifactDeletion.deleteResultGroup(outputDirectory, groupPath);
        return scriptlessResults();
    }

    public synchronized Path resolveScriptlessResultAsset(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("Result asset path is required");
        }

        Path installBinDirectory = resolveInstallBinDirectory();
        Path outputDirectory = installBinDirectory.resolve("output").toAbsolutePath().normalize();
        Path assetPath = Paths.get(filePath).toAbsolutePath().normalize();

        if (!assetPath.startsWith(outputDirectory)) {
            throw new IllegalArgumentException("Invalid result asset path: " + assetPath);
        }

        if (!Files.isRegularFile(assetPath)) {
            throw new IllegalArgumentException("Result asset not found: " + assetPath.getFileName());
        }

        return assetPath;
    }

    private Path resolveInstallBinDirectory() {
        Path workingDirectory = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        Path current = workingDirectory;

        while (current != null) {
            Path candidate = current.resolve("testar").resolve("target").resolve("install").resolve("testar").resolve("bin");
            if (Files.isDirectory(candidate)) {
                return candidate;
            }

            current = current.getParent();
        }

        throw new IllegalStateException("Unable to find testar/target/install/testar/bin. Run :testar:installDist first.");
    }

    private void prepareWorkspaceForRun(String workspaceName, Path settingsRoot, Path installBinDirectory, String mode) {
        Path sourceWorkspace = settingsRoot.resolve(workspaceName).normalize();
        Path installSettingsDirectory = installBinDirectory.resolve("settings");
        Path targetWorkspace = installSettingsDirectory.resolve(workspaceName);
        Path markerFile = installSettingsDirectory.resolve(workspaceName + ".sse");
        boolean workspaceAlreadyInInstallDirectory = sourceWorkspace.equals(targetWorkspace);

        if (!Files.isDirectory(sourceWorkspace)) {
            throw new IllegalArgumentException("Workspace not found: " + workspaceName);
        }

        try {
            Files.createDirectories(installSettingsDirectory);
            clearExistingMarkers(installSettingsDirectory);
            if (!workspaceAlreadyInInstallDirectory) {
                replaceDirectory(targetWorkspace, sourceWorkspace);
            }
            patchTestSettings(targetWorkspace.resolve("test.settings"), mode);
            plannedSequenceCount = readPlannedSequenceCount(targetWorkspace.resolve("test.settings"));
            Files.writeString(markerFile, "", StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to prepare workspace for scriptless execution: " + workspaceName, exception);
        }
    }

    private void clearExistingMarkers(Path installSettingsDirectory) throws IOException {
        try (var children = Files.list(installSettingsDirectory)) {
            for (Path child : children.filter(path -> path.getFileName().toString().endsWith(".sse")).toList()) {
                Files.deleteIfExists(child);
            }
        }
    }

    private void replaceDirectory(Path targetDirectory, Path sourceDirectory) throws IOException {
        if (Files.exists(targetDirectory)) {
            try (var paths = Files.walk(targetDirectory)) {
                for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(path);
                }
            }
        }

        try (var paths = Files.walk(sourceDirectory)) {
            for (Path sourcePath : paths.toList()) {
                Path relativePath = sourceDirectory.relativize(sourcePath);
                Path targetPath = targetDirectory.resolve(relativePath);
                if (Files.isDirectory(sourcePath)) {
                    Files.createDirectories(targetPath);
                } else {
                    Files.createDirectories(targetPath.getParent());
                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    private void patchTestSettings(Path testSettingsFile, String mode) throws IOException {
        List<String> lines = Files.readAllLines(testSettingsFile, StandardCharsets.UTF_8);
        List<String> updatedLines = new ArrayList<>();
        boolean modeUpdated = false;
        boolean dialogUpdated = false;

        for (String line : lines) {
            if (line.trim().startsWith("Mode")) {
                updatedLines.add("Mode = " + mode);
                modeUpdated = true;
            } else if (line.trim().startsWith("ShowVisualSettingsDialogOnStartup")) {
                updatedLines.add("ShowVisualSettingsDialogOnStartup = false");
                dialogUpdated = true;
            } else {
                updatedLines.add(line);
            }
        }

        if (!modeUpdated) {
            updatedLines.add("Mode = " + mode);
        }

        if (!dialogUpdated) {
            updatedLines.add("ShowVisualSettingsDialogOnStartup = false");
        }

        Files.write(testSettingsFile, updatedLines, StandardCharsets.UTF_8);
    }

    private void startOutputDrain(Process process) {
        Thread drainThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)
            )) {
                String line;
                while ((line = reader.readLine()) != null) {
                    appendConsoleLine(line);
                }
            } catch (IOException ignored) {
                // Ignore stream shutdown when the process exits.
            }
        });
        drainThread.setDaemon(true);
        drainThread.start();
    }

    private synchronized ExecutionStatusDto buildStatus(String status, String message) {
        return new ExecutionStatusDto(
            "scriptless",
            status,
            message,
            currentWorkspace,
            currentMode,
            consoleOutput(),
            startedAtEpochMillis > 0L ? startedAtEpochMillis : null,
            plannedSequenceCount > 0 ? plannedSequenceCount : null,
            List.copyOf(sequenceOutcomes)
        );
    }

    private synchronized void resetConsoleOutput() {
        consoleLines.clear();
        sequenceOutcomes.clear();
        currentInstallBinDirectory = null;
        plannedSequenceCount = 0;
        currentSequenceFailed = false;
        startedAtEpochMillis = 0L;
        lastOutputEpochMillis = 0L;
    }

    private synchronized void appendConsoleLine(String line) {
        lastOutputEpochMillis = System.currentTimeMillis();
        consoleLines.add(line);
        if (consoleLines.size() > MAX_CONSOLE_LINES) {
            consoleLines.remove(0);
        }
        processSequenceLine(line);
    }

    private synchronized String consoleOutput() {
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        for (String line : consoleLines) {
            joiner.add(line);
        }
        return joiner.toString();
    }

    private int readPlannedSequenceCount(Path testSettingsFile) throws IOException {
        Properties properties = new Properties();
        try (var inputStream = Files.newInputStream(testSettingsFile)) {
            properties.load(inputStream);
        }

        String configuredSequences = properties.getProperty("Sequences", "").trim();
        if (configuredSequences.isEmpty()) {
            return 0;
        }

        try {
            return Integer.parseInt(configuredSequences);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private void processSequenceLine(String line) {
        if (isSequenceFailureSignal(line)) {
            currentSequenceFailed = true;
        }

        boolean explicitOkSequenceLine = line != null && line.contains("No problem detected.");

        Matcher pathMatcher = SEQUENCE_OUTPUT_PATH_PATTERN.matcher(line);
        if (pathMatcher.find()) {
            int sequenceNumber = Integer.parseInt(pathMatcher.group(2));
            String outputPath = resolveSequenceOutputPath(pathMatcher.group(1));
            boolean failedSequence = explicitOkSequenceLine ? false : (currentSequenceFailed || line.contains("["));
            recordSequenceOutcome(sequenceNumber, failedSequence ? "failed" : "ok", outputPath);
            currentSequenceFailed = false;
            return;
        }

        Matcher summaryMatcher = SEQUENCE_SUMMARY_PATTERN.matcher(line);
        if (summaryMatcher.find()) {
            int sequenceNumber = Integer.parseInt(summaryMatcher.group(1));
            boolean failedSequence = explicitOkSequenceLine ? false : (currentSequenceFailed || line.contains("["));
            recordSequenceOutcome(sequenceNumber, failedSequence ? "failed" : "ok", null);
            currentSequenceFailed = false;
            return;
        }

        if (line.contains("End of test sequence - shutting down the SUT...")) {
            recordSequenceOutcome(sequenceOutcomes.size() + 1, currentSequenceFailed ? "failed" : "ok", null);
            currentSequenceFailed = false;
        }
    }

    private boolean isSequenceFailureSignal(String line) {
        if (line == null || line.isBlank()) {
            return false;
        }

        if (line.contains("No problem detected.")) {
            return false;
        }

        return line.contains("Sequence contained faults!")
            || isNonOkVerdictSummaryLine(line)
            || containsNonOkVerdictTitle(line);
    }

    private boolean isNonOkVerdictSummaryLine(String line) {
        return line.contains("Test verdict for this sequence:");
    }

    private boolean containsNonOkVerdictTitle(String line) {
        for (String verdictTitle : NON_OK_VERDICT_TITLES) {
            if (line.contains(verdictTitle)) {
                return true;
            }
        }

        return false;
    }

    private static Set<String> buildNonOkVerdictTitles() {
        Set<String> verdictTitles = new HashSet<>();

        for (Verdict.Severity severity : Verdict.Severity.values()) {
            if (severity.getValue() > Verdict.Severity.OK.getValue()) {
                verdictTitles.add(severity.getTitle());
            }
        }

        return verdictTitles;
    }

    private boolean shouldStopCompletedButStuckRun() {
        if (plannedSequenceCount <= 0) {
            return false;
        }

        if (sequenceOutcomes.size() < plannedSequenceCount) {
            return false;
        }

        return System.currentTimeMillis() - lastOutputEpochMillis >= COMPLETED_RUN_IDLE_GRACE_MILLIS;
    }

    private void recordSequenceOutcome(int sequenceNumber, String status, String outputPath) {
        String resolvedLabel = resolveSequenceOutcomeLabel(sequenceNumber, status, outputPath);

        for (int index = 0; index < sequenceOutcomes.size(); index++) {
            if (sequenceOutcomes.get(index).sequenceNumber() == sequenceNumber) {
                String existingStatus = sequenceOutcomes.get(index).status();
                if ("failed".equals(existingStatus)) {
                    return;
                }

                String resolvedOutputPath = outputPath != null ? outputPath : sequenceOutcomes.get(index).outputPath();
                String existingLabel = sequenceOutcomes.get(index).label();
                String effectiveLabel = resolvedLabel != null ? resolvedLabel : existingLabel;
                sequenceOutcomes.set(index, new SequenceOutcomeDto(sequenceNumber, status, resolvedOutputPath, effectiveLabel));
                return;
            }
        }

        sequenceOutcomes.add(new SequenceOutcomeDto(sequenceNumber, status, outputPath, resolvedLabel));
    }

    private String resolveSequenceOutcomeLabel(int sequenceNumber, String status, String outputPath) {
        if (outputPath == null || outputPath.isBlank()) {
            return fallbackSequenceOutcomeLabel(sequenceNumber);
        }

        try {
            Path outputBasePath = Paths.get(outputPath).normalize();
            Path runOutputDirectory = outputBasePath.getParent();
            if (runOutputDirectory == null) {
                return fallbackSequenceOutcomeLabel(sequenceNumber);
            }

            Path reportsDirectory = runOutputDirectory.resolve("reports");
            if (!Files.isDirectory(reportsDirectory)) {
                return fallbackSequenceOutcomeLabel(sequenceNumber);
            }

            String baseName = outputBasePath.getFileName() == null ? null : outputBasePath.getFileName().toString();
            List<ResultFileSummaryDto> files = new ArrayList<>();
            collectPreviewableFiles(reportsDirectory, baseName, files);
            if (files.isEmpty()) {
                return fallbackSequenceOutcomeLabel(sequenceNumber);
            }

            files.sort(Comparator.comparing(ResultFileSummaryDto::path));
            ResultFileSummaryDto selectedFile = selectSequenceOutcomeFile(files, status);
            return formatSequenceOutcomeLabel(selectedFile.name(), sequenceNumber);
        } catch (Exception ignored) {
            return fallbackSequenceOutcomeLabel(sequenceNumber);
        }
    }

    private ResultFileSummaryDto selectSequenceOutcomeFile(List<ResultFileSummaryDto> files, String status) {
        if ("failed".equals(status)) {
            for (ResultFileSummaryDto file : files) {
                if ("failed".equals(file.status())) {
                    return file;
                }
            }
        }

        for (ResultFileSummaryDto file : files) {
            if ("ok".equals(file.status())) {
                return file;
            }
        }

        return files.get(0);
    }

    private String formatSequenceOutcomeLabel(String fileName, int sequenceNumber) {
        if (fileName == null || fileName.isBlank()) {
            return fallbackSequenceOutcomeLabel(sequenceNumber);
        }

        String trimmedExtension = fileName.replaceAll("\\.html?$", "");
        int sequenceIndex = trimmedExtension.indexOf("_sequence_");
        if (sequenceIndex >= 0) {
            return trimmedExtension.substring(sequenceIndex + 1);
        }

        return trimmedExtension;
    }

    private String fallbackSequenceOutcomeLabel(int sequenceNumber) {
        return "sequence_" + sequenceNumber;
    }

    private void destroyProcessTree(Process process) {
        ProcessHandle processHandle = process.toHandle();
        processHandle
            .descendants()
            .forEach(descendant -> {
                try {
                    descendant.destroyForcibly();
                } catch (Exception ignored) {
                    // Ignore failing descendant termination attempts.
                }
            });

        try {
            processHandle.destroyForcibly();
        } catch (Exception ignored) {
            // Ignore failing root termination attempts.
        }
    }

    private String latestSequenceOutputPath() {
        if (sequenceOutcomes.isEmpty()) {
            return "";
        }

        SequenceOutcomeDto lastOutcome = sequenceOutcomes.get(sequenceOutcomes.size() - 1);
        return lastOutcome.outputPath() == null ? "" : lastOutcome.outputPath();
    }

    private String resolveSequenceOutputPath(String loggedPath) {
        Path basePath = Paths.get(loggedPath).normalize();
        if (basePath.isAbsolute()) {
            return basePath.toString();
        }

        if (currentInstallBinDirectory != null) {
            return currentInstallBinDirectory.resolve(basePath).normalize().toString();
        }

        return basePath.toString();
    }

    private void collectPreviewableFiles(Path reportsDirectory, String baseName, List<ResultFileSummaryDto> files) {
        try (var children = Files.list(reportsDirectory)) {
            children
                .filter(Files::isRegularFile)
                .filter(path -> baseName == null || path.getFileName().toString().startsWith(baseName))
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
            throw new IllegalStateException("Unable to list scriptless result files under: " + reportsDirectory, exception);
        }
    }

    private List<ResultOutputGroupDto> loadResultGroups() {
        Path installBinDirectory;
        try {
            installBinDirectory = resolveInstallBinDirectory();
        } catch (RuntimeException exception) {
            return List.of();
        }

        Path outputDirectory = installBinDirectory.resolve("output");
        if (!Files.isDirectory(outputDirectory)) {
            return List.of();
        }

        try (var children = Files.list(outputDirectory)) {
            List<ResultOutputGroupDto> groups = children
                .filter(Files::isDirectory)
                .filter(path -> !"graphs".equalsIgnoreCase(path.getFileName().toString()))
                .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                .map(this::toResultOutputGroup)
                .filter(group -> group != null && !group.files().isEmpty())
                .toList();
            return groups;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to inspect scriptless output directory: " + outputDirectory, exception);
        }
    }

    private ResultOutputGroupDto toResultOutputGroup(Path outputGroupDirectory) {
        Path reportsDirectory = outputGroupDirectory.resolve("reports");
        if (!Files.isDirectory(reportsDirectory)) {
            return null;
        }

        List<ResultFileSummaryDto> files = new ArrayList<>();
        collectPreviewableFiles(reportsDirectory, null, files);
        files.sort(Comparator.comparing(ResultFileSummaryDto::path));
        int totalSequenceCount = countSequenceLogs(outputGroupDirectory.resolve("logs"));

        return new ResultOutputGroupDto(
            outputGroupDirectory.getFileName().toString(),
            outputGroupDirectory.toString(),
            resultStatusFor(files),
            totalSequenceCount,
            files
        );
    }

    private String resolveLatestResultsPath(List<ResultFileSummaryDto> files) {
        if (!files.isEmpty()) {
            return files.get(files.size() - 1).path();
        }

        return latestSequenceOutputPath();
    }

    private String resultStatusFor(Path path) {
        return ResultVerdictStatus.forResultFile(path);
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
        if (fileName.endsWith(".json")) {
            return "application/json";
        }
        if (fileName.endsWith(".log") || fileName.endsWith(".txt")) {
            return "text/plain";
        }
        return "text/plain";
    }

    private boolean isPreviewableResult(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        return fileName.endsWith(".html")
            || fileName.endsWith(".htm");
    }

    private int countSequenceLogs(Path logsDirectory) {
        if (!Files.isDirectory(logsDirectory)) {
            return 0;
        }

        try (var children = Files.list(logsDirectory)) {
            return (int) children
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().matches(".*_sequence_\\d+\\.log$"))
                .count();
        } catch (IOException exception) {
            return 0;
        }
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
                Path validAssetPath = resolveScriptlessResultAsset(resolvedAssetPath.toString());
                String encodedAssetPath = URLEncoder.encode(validAssetPath.toString(), StandardCharsets.UTF_8);
                String assetUrl = "/api/execution/scriptless/result-asset?path=" + encodedAssetPath;
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
}
