/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.workspace;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.testar.config.composition.helper.ModuleWorkspaceHelper;
import org.testar.config.policy.helper.PolicyWorkspaceHelper;
import org.testar.config.settings.Settings;
import org.testar.config.settings.SettingsDefaults;
import org.testar.webstudio.api.dto.WorkspaceDocumentDto;
import org.testar.webstudio.api.dto.WorkspaceFileDto;
import org.testar.webstudio.api.dto.WorkspaceJavaCompileDiagnosticDto;
import org.testar.webstudio.api.dto.WorkspaceJavaCompileResultDto;
import org.testar.webstudio.api.dto.WorkspaceModuleDefinitionDto;
import org.testar.webstudio.api.dto.WorkspacePolicyDefinitionDto;
import org.testar.webstudio.api.dto.WorkspaceSummaryDto;
import org.testar.webstudio.api.dto.DebugFileDto;
import org.testar.webstudio.api.dto.DebugFileSummaryDto;

public final class WorkspaceService {

    private static final String TEST_SETTINGS_FILE = "test.settings";
    private static final String COMPOSITION_FILE = "composition.properties";
    private static final String POLICIES_FILE = "policies.properties";
    private static final String CUSTOM_COMPOSITION_RESOURCE = "CustomCompositionResource";
    private static final String CUSTOM_POLICIES_RESOURCE = "CustomPoliciesResource";
    private static final String TEST_GOALS_DIRECTORY = "test_goals";
    private static final String ORACLES_DIRECTORY = "oracles";
    private static final String ORACLE_DSL_DIRECTORY = "dsl";
    private static final String ORACLE_JAVA_DIRECTORY = "java";
    private static final String ORACLE_COMPILED_DIRECTORY = "compiled";
    private static final Pattern WORKSPACE_NAME_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

    private final Path settingsRoot;
    private final Path cliSettingsRoot;

    public WorkspaceService() {
        this(resolveDefaultSettingsRoot(), resolveDefaultCliSettingsRoot());
    }

    public WorkspaceService(Path settingsRoot) {
        this(settingsRoot, resolveDefaultCliSettingsRoot());
    }

    public WorkspaceService(Path settingsRoot, Path cliSettingsRoot) {
        this.settingsRoot = settingsRoot;
        this.cliSettingsRoot = cliSettingsRoot;
    }

    public List<WorkspaceSummaryDto> listWorkspaces() {
        Map<String, WorkspaceSummaryDto> summaries = new TreeMap<>();
        collectWorkspaceSummaries(summaries, settingsRoot, true, false);
        collectWorkspaceSummaries(summaries, cliSettingsRoot, false, true);
        return List.copyOf(summaries.values());
    }

    public Path settingsRoot() {
        return settingsRoot;
    }

    public Path cliSettingsRoot() {
        return cliSettingsRoot;
    }

    public Path testarHomeDirectory() {
        Path parent = settingsRoot.getParent();
        if (parent == null) {
            throw new IllegalStateException("Unable to resolve TESTAR home from settings root: " + settingsRoot);
        }

        return parent.toAbsolutePath().normalize();
    }

    public Path cliHomeDirectory() {
        Path parent = cliSettingsRoot.getParent();
        if (parent == null) {
            throw new IllegalStateException("Unable to resolve TESTAR CLI home from settings root: " + cliSettingsRoot);
        }

        return parent.toAbsolutePath().normalize();
    }

    public Path workspaceDirectory(String workspaceName) {
        return resolveWorkspaceDirectory(workspaceName);
    }

    public Path workspaceRuntimeHomeDirectory(String workspaceName) {
        return resolveWorkspaceRuntimeHomeDirectory(workspaceName);
    }

    // Implements WS-FUNC-WORKSPACE-MANAGEMENT-001: creates workspace clones and renames workspace/output folders.
    public WorkspaceSummaryDto createWorkspace(String workspaceName, String baseWorkspaceName, boolean copyTestGoals) {
        return createWorkspace(workspaceName, baseWorkspaceName, copyTestGoals, true);
    }

    public WorkspaceSummaryDto createWorkspace(
        String workspaceName,
        String baseWorkspaceName,
        boolean copyTestGoals,
        boolean copyOracles
    ) {
        String normalizedWorkspaceName = normalizeNewWorkspaceName(workspaceName);
        String normalizedBaseWorkspaceName = normalizeExistingWorkspaceName(baseWorkspaceName, "Base workspace is required.");
        Path sourceDirectory = resolveWorkspaceDirectory(normalizedBaseWorkspaceName);
        Path targetDirectory = settingsRoot.resolve(normalizedWorkspaceName).normalize();

        if (!targetDirectory.startsWith(settingsRoot)) {
            throw new IllegalArgumentException("Invalid workspace name: " + normalizedWorkspaceName);
        }

        if (workspaceExists(settingsRoot, normalizedWorkspaceName) || workspaceExists(cliSettingsRoot, normalizedWorkspaceName)) {
            throw new IllegalArgumentException("Workspace already exists: " + normalizedWorkspaceName);
        }

        try {
            Files.createDirectories(settingsRoot);
            copyWorkspaceDirectory(sourceDirectory, targetDirectory, copyTestGoals, copyOracles);
            ensureWorkspaceAssetDirectories(targetDirectory);
            updateWorkspaceResourceSettings(
                targetDirectory,
                normalizedBaseWorkspaceName,
                normalizedWorkspaceName,
                sourceDirectory,
                targetDirectory
            );
            return listWorkspaces().stream()
                .filter(workspace -> normalizedWorkspaceName.equals(workspace.name()))
                .findFirst()
                .orElseGet(() -> new WorkspaceSummaryDto(
                    normalizedWorkspaceName,
                    targetDirectory.toString(),
                    true,
                    workspaceExists(cliSettingsRoot, normalizedWorkspaceName)
                ));
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to create workspace: " + normalizedWorkspaceName, exception);
        }
    }

    public WorkspaceSummaryDto renameWorkspace(String currentWorkspaceName, String newWorkspaceName) {
        String normalizedCurrentWorkspaceName = normalizeExistingWorkspaceName(
            currentWorkspaceName,
            "Current workspace name is required."
        );
        String normalizedNewWorkspaceName = normalizeNewWorkspaceName(newWorkspaceName);

        if (normalizedCurrentWorkspaceName.equals(normalizedNewWorkspaceName)) {
            throw new IllegalArgumentException("New workspace name must be different from the current workspace name.");
        }

        Path sourceDirectory = settingsRoot.resolve(normalizedCurrentWorkspaceName).normalize();
        Path targetDirectory = settingsRoot.resolve(normalizedNewWorkspaceName).normalize();
        Path sourceOutputDirectory = workspaceOutputDirectory(normalizedCurrentWorkspaceName);
        Path targetOutputDirectory = workspaceOutputDirectory(normalizedNewWorkspaceName);

        if (!sourceDirectory.startsWith(settingsRoot) || !targetDirectory.startsWith(settingsRoot)) {
            throw new IllegalArgumentException("Invalid workspace name.");
        }

        if (!Files.isDirectory(sourceDirectory)) {
            if (workspaceExists(cliSettingsRoot, normalizedCurrentWorkspaceName)) {
                throw new IllegalArgumentException(
                    "Workspace cannot be renamed because it is not available in the shared settings root: "
                        + normalizedCurrentWorkspaceName
                );
            }

            throw new IllegalArgumentException("Workspace not found: " + normalizedCurrentWorkspaceName);
        }

        if (workspaceExists(settingsRoot, normalizedNewWorkspaceName) || workspaceExists(cliSettingsRoot, normalizedNewWorkspaceName)) {
            throw new IllegalArgumentException("Workspace already exists: " + normalizedNewWorkspaceName);
        }

        if (Files.exists(targetOutputDirectory)) {
            throw new IllegalArgumentException("Output results already exist for workspace: " + normalizedNewWorkspaceName);
        }

        boolean settingsUpdated = false;
        boolean workspaceMoved = false;
        boolean outputMoved = false;
        try {
            settingsUpdated = updateWorkspaceResourceSettings(
                sourceDirectory,
                normalizedCurrentWorkspaceName,
                normalizedNewWorkspaceName,
                sourceDirectory,
                targetDirectory
            );
            Files.move(sourceDirectory, targetDirectory);
            workspaceMoved = true;
            outputMoved = moveWorkspaceOutputDirectory(sourceOutputDirectory, targetOutputDirectory);
        } catch (IOException exception) {
            rollbackWorkspaceRename(
                sourceDirectory,
                targetDirectory,
                sourceOutputDirectory,
                targetOutputDirectory,
                settingsUpdated,
                workspaceMoved,
                outputMoved,
                normalizedCurrentWorkspaceName,
                normalizedNewWorkspaceName,
                exception
            );
            throw new IllegalStateException(
                "Unable to rename workspace " + normalizedCurrentWorkspaceName + " to " + normalizedNewWorkspaceName,
                exception
            );
        }

        return listWorkspaces().stream()
            .filter(workspace -> normalizedNewWorkspaceName.equals(workspace.name()))
            .findFirst()
            .orElseGet(() -> new WorkspaceSummaryDto(
                normalizedNewWorkspaceName,
                targetDirectory.toString(),
                true,
                workspaceExists(cliSettingsRoot, normalizedNewWorkspaceName)
            ));
    }

    private Path workspaceOutputDirectory(String workspaceName) {
        Path outputRoot = testarHomeDirectory().resolve("output").toAbsolutePath().normalize();
        Path workspaceOutputDirectory = outputRoot.resolve(workspaceName).normalize();
        if (!workspaceOutputDirectory.startsWith(outputRoot)) {
            throw new IllegalArgumentException("Invalid workspace output name: " + workspaceName);
        }

        return workspaceOutputDirectory;
    }

    private boolean moveWorkspaceOutputDirectory(
        Path sourceOutputDirectory,
        Path targetOutputDirectory
    ) throws IOException {
        if (!Files.exists(sourceOutputDirectory)) {
            return false;
        }

        Files.createDirectories(targetOutputDirectory.getParent());
        Files.move(sourceOutputDirectory, targetOutputDirectory);
        return true;
    }

    private void rollbackWorkspaceRename(
        Path sourceWorkspaceDirectory,
        Path targetWorkspaceDirectory,
        Path sourceOutputDirectory,
        Path targetOutputDirectory,
        boolean settingsUpdated,
        boolean workspaceMoved,
        boolean outputMoved,
        String sourceWorkspaceName,
        String targetWorkspaceName,
        IOException originalException
    ) {
        if (outputMoved) {
            rollbackMove(targetOutputDirectory, sourceOutputDirectory, originalException);
        }

        if (workspaceMoved) {
            rollbackMove(targetWorkspaceDirectory, sourceWorkspaceDirectory, originalException);
        }

        if (settingsUpdated && Files.isDirectory(sourceWorkspaceDirectory)) {
            try {
                updateWorkspaceResourceSettings(
                    sourceWorkspaceDirectory,
                    targetWorkspaceName,
                    sourceWorkspaceName,
                    targetWorkspaceDirectory,
                    sourceWorkspaceDirectory
                );
            } catch (IOException rollbackException) {
                originalException.addSuppressed(rollbackException);
            }
        }
    }

    private void rollbackMove(Path currentPath, Path originalPath, IOException originalException) {
        if (!Files.exists(currentPath) || Files.exists(originalPath)) {
            return;
        }

        try {
            Files.move(currentPath, originalPath);
        } catch (IOException rollbackException) {
            originalException.addSuppressed(rollbackException);
        }
    }

    // Implements WS-FUNC-DEBUG-FILES-001: lists .log files from the shared runtime home.
    public List<DebugFileSummaryDto> listDebugFiles() {
        Path testarHomeDirectory = testarHomeDirectory();
        if (!Files.isDirectory(testarHomeDirectory)) {
            return List.of();
        }

        try (Stream<Path> children = Files.list(testarHomeDirectory)) {
            return children
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().toLowerCase().endsWith(".log"))
                .sorted(Comparator.comparing(path -> path.getFileName().toString().toLowerCase()))
                .map(path -> new DebugFileSummaryDto(
                    path.getFileName().toString(),
                    path.toAbsolutePath().normalize().toString()
                ))
                .collect(Collectors.toList());
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to list debug files under: " + testarHomeDirectory, exception);
        }
    }

    public DebugFileDto readDebugFile(String fileName, String filePath) {
        Path testarHomeDirectory = testarHomeDirectory();
        Path resolvedFile;

        if (filePath != null && !filePath.isBlank()) {
            resolvedFile = Path.of(filePath).toAbsolutePath().normalize();
        } else {
            resolvedFile = testarHomeDirectory.resolve(fileName).toAbsolutePath().normalize();
        }

        if (!resolvedFile.startsWith(testarHomeDirectory)) {
            throw new IllegalArgumentException("Invalid debug file path: " + resolvedFile);
        }

        if (!resolvedFile.getFileName().toString().toLowerCase().endsWith(".log")) {
            throw new IllegalArgumentException("Invalid debug file name: " + resolvedFile.getFileName());
        }

        if (!Files.isRegularFile(resolvedFile)) {
            throw new IllegalArgumentException("Debug file not found: " + resolvedFile.getFileName());
        }

        try {
            return new DebugFileDto(
                resolvedFile.getFileName().toString(),
                resolvedFile.toString(),
                readDebugFileContent(resolvedFile)
            );
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read debug file: " + resolvedFile, exception);
        }
    }

    // Implements WS-FUNC-WORKSPACE-SOURCE-EDITOR-001 and WS-FUNC-TEST-SETTINGS-001:
    // reads workspace files, source summaries, references, and visual settings groups.
    public WorkspaceDocumentDto readWorkspaceDocument(String workspaceName) {
        Path workspaceDirectory = resolveWorkspaceDirectory(workspaceName);
        WorkspaceFileDto testSettings = readWorkspaceFile(workspaceName, TEST_SETTINGS_FILE, "settings");
        WorkspaceFileDto composition = readWorkspaceFile(workspaceName, COMPOSITION_FILE, "composition");
        WorkspaceFileDto policies = readWorkspaceFile(workspaceName, POLICIES_FILE, "policies");
        Properties compositionProperties = loadProperties(workspaceDirectory.resolve(COMPOSITION_FILE));
        Properties policyProperties = loadProperties(workspaceDirectory.resolve(POLICIES_FILE));
        Properties settingsProperties = loadProperties(workspaceDirectory.resolve(TEST_SETTINGS_FILE));
        List<WorkspaceFileDto> sourceFiles = listWorkspaceSourceFiles(workspaceDirectory, compositionProperties, policyProperties);

        return new WorkspaceDocumentDto(
            workspaceName,
            workspaceDirectory.toString(),
            testSettings,
            composition,
            policies,
            sourceFiles,
            collectReferences(compositionProperties, policyProperties),
            WorkspaceSettingsCatalog.buildSettingsGroups(settingsProperties),
            buildModuleDefinitions(compositionProperties),
            buildPolicyDefinitions(policyProperties)
        );
    }

    public WorkspaceFileDto readWorkspaceFile(String workspaceName, String fileName, String category) {
        Path workspaceDirectory = resolveWorkspaceDirectory(workspaceName);
        Path file = workspaceDirectory.resolve(fileName).normalize();
        if (!file.startsWith(workspaceDirectory)) {
            throw new IllegalArgumentException("Invalid workspace file path: " + fileName);
        }

        if (!Files.isRegularFile(file)) {
            return new WorkspaceFileDto(fileName, file.toString(), "", category);
        }

        try {
            String content = Files.readString(file, StandardCharsets.UTF_8);
            return new WorkspaceFileDto(file.getFileName().toString(), file.toString(), content, category);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read workspace file: " + file, exception);
        }
    }

    public WorkspaceFileDto readWorkspaceSourceFile(String workspaceName, String sourceName) {
        ensureJavaSourceName(sourceName);
        Path workspaceDirectory = resolveWorkspaceDirectory(workspaceName);
        Properties compositionProperties = loadProperties(workspaceDirectory.resolve(COMPOSITION_FILE));
        Properties policyProperties = loadProperties(workspaceDirectory.resolve(POLICIES_FILE));
        String category = inferSourceCategory(sourceName, compositionProperties, policyProperties);
        return readWorkspaceFile(workspaceName, sourceName, category);
    }

    public WorkspaceFileDto saveWorkspaceFile(String workspaceName, String fileName, String category, String content) {
        Path workspaceDirectory = resolveWorkspaceDirectory(workspaceName);
        Path file = workspaceDirectory.resolve(fileName).normalize();
        if (!file.startsWith(workspaceDirectory)) {
            throw new IllegalArgumentException("Invalid workspace file path: " + fileName);
        }

        try {
            Files.createDirectories(workspaceDirectory);
            Files.writeString(file, content, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to save workspace file: " + file, exception);
        }

        return new WorkspaceFileDto(file.getFileName().toString(), file.toString(), content, category);
    }

    public WorkspaceFileDto saveWorkspaceSourceFile(String workspaceName, String sourceName, String content) {
        ensureJavaSourceName(sourceName);
        Path workspaceDirectory = resolveWorkspaceDirectory(workspaceName);
        Properties compositionProperties = loadProperties(workspaceDirectory.resolve(COMPOSITION_FILE));
        Properties policyProperties = loadProperties(workspaceDirectory.resolve(POLICIES_FILE));
        String category = inferSourceCategory(sourceName, compositionProperties, policyProperties);
        return saveWorkspaceFile(workspaceName, sourceName, category, content);
    }

    // Implements WS-FUNC-COMPOSITION-FLOW-001: creates missing Java module sources and updates composition.properties.
    public WorkspaceFileDto createOrOpenModuleSource(String workspaceName, String propertyKey) {
        ModuleWorkspaceHelper.ModuleDefinition moduleDefinition = findModuleDefinition(propertyKey);
        Path workspaceDirectory = resolveWorkspaceDirectory(workspaceName);
        String resourcePath = workspaceDirectory.resolve(COMPOSITION_FILE).toString();
        Properties properties = ModuleWorkspaceHelper.loadCompositionProperties(resourcePath);
        String className = properties.getProperty(moduleDefinition.propertyKey, "").trim();
        Settings settings = loadWorkspaceSettings(workspaceDirectory);

        if (className.isEmpty()) {
            className = ModuleWorkspaceHelper.buildDefaultClassName(resourcePath, moduleDefinition);
            properties.setProperty(moduleDefinition.propertyKey, className);
            ModuleWorkspaceHelper.saveCompositionProperties(resourcePath, properties, settings);
        }

        java.io.File sourceFile = ModuleWorkspaceHelper.ensureModuleSourceFile(resourcePath, className, moduleDefinition);
        return readWorkspaceFile(
            sourceFile.toPath().getParent() == null ? workspaceDirectory : sourceFile.toPath().getParent(),
            sourceFile.getName(),
            inferSourceCategory(sourceFile.getName(), properties, loadProperties(workspaceDirectory.resolve(POLICIES_FILE)))
        );
    }

    // Implements WS-FUNC-POLICIES-001: creates Java policy sources and updates policies.properties.
    public WorkspaceFileDto createOrOpenPolicySource(String workspaceName, String propertyKey) {
        PolicyWorkspaceHelper.PolicyDefinition policyDefinition = findPolicyDefinition(propertyKey);
        Path workspaceDirectory = resolveWorkspaceDirectory(workspaceName);
        String resourcePath = workspaceDirectory.resolve(POLICIES_FILE).toString();
        Properties properties = PolicyWorkspaceHelper.loadPoliciesProperties(resourcePath);
        List<String> classNames = PolicyWorkspaceHelper.parseClassList(properties.getProperty(policyDefinition.propertyKey, ""));
        String className = PolicyWorkspaceHelper.buildDefaultClassName(resourcePath, policyDefinition, classNames.size());
        classNames.add(className);
        properties.setProperty(policyDefinition.propertyKey, PolicyWorkspaceHelper.joinClassNames(classNames));
        PolicyWorkspaceHelper.savePoliciesProperties(resourcePath, properties);

        java.io.File sourceFile = PolicyWorkspaceHelper.ensurePolicySourceFile(resourcePath, className, policyDefinition);
        return readWorkspaceFile(
            sourceFile.toPath().getParent() == null ? workspaceDirectory : sourceFile.toPath().getParent(),
            sourceFile.getName(),
            "policy"
        );
    }

    public WorkspaceJavaCompileResultDto compileWorkspaceSource(String workspaceName, String sourceName) {
        ensureJavaSourceName(sourceName);
        Path workspaceDirectory = resolveWorkspaceDirectory(workspaceName);
        return compileWorkspaceJava(workspaceDirectory, "source", sourceName);
    }

    public WorkspaceJavaCompileResultDto compileWorkspaceProfile(String workspaceName) {
        Path workspaceDirectory = resolveWorkspaceDirectory(workspaceName);
        return compileWorkspaceJava(workspaceDirectory, "profile", workspaceName);
    }

    private static Path resolveDefaultSettingsRoot() {
        String configuredSettingsRoot = System.getProperty("testar.webstudio.settingsRoot");
        if (configuredSettingsRoot != null && !configuredSettingsRoot.isBlank()) {
            return Paths.get(configuredSettingsRoot).toAbsolutePath().normalize();
        }

        Path workingDirectory = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        Path current = workingDirectory;

        while (current != null) {
            Path candidate = current.resolve("testar").resolve("target").resolve("install").resolve("testar").resolve("bin").resolve("settings");
            if (Files.isDirectory(candidate)) {
                return candidate;
            }

            current = current.getParent();
        }

        current = workingDirectory;

        while (current != null) {
            Path candidate = current.resolve("testar").resolve("resources").resolve("settings");
            if (Files.isDirectory(candidate)) {
                return candidate;
            }

            current = current.getParent();
        }

        return workingDirectory.resolve("testar").resolve("target").resolve("install").resolve("testar").resolve("bin").resolve("settings");
    }

    private static Path resolveDefaultCliSettingsRoot() {
        Path workingDirectory = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        Path current = workingDirectory;

        while (current != null) {
            Path sharedBinDirectory = current.resolve("testar").resolve("target").resolve("install").resolve("testar").resolve("bin");
            Path sharedSettingsDirectory = sharedBinDirectory.resolve("settings");
            if (Files.isDirectory(sharedSettingsDirectory)
                    && (Files.isRegularFile(sharedBinDirectory.resolve("testar-cli.bat"))
                    || Files.isRegularFile(sharedBinDirectory.resolve("testar-cli")))) {
                return sharedSettingsDirectory;
            }

            current = current.getParent();
        }

        current = workingDirectory;

        while (current != null) {
            Path candidate = current.resolve("cli").resolve("target").resolve("install").resolve("testar-cli").resolve("settings");
            if (Files.isDirectory(candidate)) {
                return candidate;
            }

            current = current.getParent();
        }

        current = workingDirectory;

        while (current != null) {
            Path candidate = current.resolve("cli").resolve("resources").resolve("settings");
            if (Files.isDirectory(candidate)) {
                return candidate;
            }

            current = current.getParent();
        }

        return workingDirectory.resolve("cli").resolve("target").resolve("install").resolve("testar-cli").resolve("settings");
    }

    private Path resolveWorkspaceDirectory(String workspaceName) {
        for (Path root : List.of(settingsRoot, cliSettingsRoot)) {
            Path workspaceDirectory = root.resolve(workspaceName).normalize();
            if (!workspaceDirectory.startsWith(root)) {
                continue;
            }

            if (Files.isDirectory(workspaceDirectory)) {
                return workspaceDirectory;
            }
        }

        throw new IllegalArgumentException("Workspace not found: " + workspaceName);
    }

    private String normalizeNewWorkspaceName(String workspaceName) {
        String normalizedWorkspaceName = normalizeExistingWorkspaceName(workspaceName, "Workspace name is required.");
        if (!WORKSPACE_NAME_PATTERN.matcher(normalizedWorkspaceName).matches()) {
            throw new IllegalArgumentException(
                "Workspace name can only contain letters, numbers, underscores, and hyphens."
            );
        }

        return normalizedWorkspaceName;
    }

    private String normalizeExistingWorkspaceName(String workspaceName, String emptyMessage) {
        if (workspaceName == null || workspaceName.isBlank()) {
            throw new IllegalArgumentException(emptyMessage);
        }

        return workspaceName.trim();
    }

    private boolean workspaceExists(Path root, String workspaceName) {
        Path workspaceDirectory = root.resolve(workspaceName).normalize();
        return workspaceDirectory.startsWith(root) && Files.exists(workspaceDirectory);
    }

    private void copyWorkspaceDirectory(
        Path sourceDirectory,
        Path targetDirectory,
        boolean copyTestGoals,
        boolean copyOracles
    ) throws IOException {
        try (Stream<Path> sourcePaths = Files.walk(sourceDirectory)) {
            List<Path> paths = sourcePaths
                .filter(path -> shouldCopyWorkspacePath(sourceDirectory, path, copyTestGoals, copyOracles))
                .sorted(Comparator.comparingInt(path -> path.getNameCount()))
                .collect(Collectors.toList());

            for (Path sourcePath : paths) {
                Path relativePath = sourceDirectory.relativize(sourcePath);
                Path targetPath = targetDirectory.resolve(relativePath).normalize();
                if (!targetPath.startsWith(targetDirectory)) {
                    throw new IOException("Invalid workspace clone target path: " + targetPath);
                }

                if (Files.isDirectory(sourcePath)) {
                    Files.createDirectories(targetPath);
                } else {
                    Files.createDirectories(targetPath.getParent());
                    Files.copy(sourcePath, targetPath);
                }
            }
        }
    }

    private boolean updateWorkspaceResourceSettings(
        Path workspaceDirectory,
        String sourceWorkspaceName,
        String targetWorkspaceName,
        Path sourceDirectory,
        Path targetDirectory
    ) throws IOException {
        Path testSettingsFile = workspaceDirectory.resolve(TEST_SETTINGS_FILE);
        if (!Files.isRegularFile(testSettingsFile)) {
            return false;
        }

        List<String> lines = Files.readAllLines(testSettingsFile, StandardCharsets.UTF_8);
        List<String> updatedLines = lines.stream()
            .map(line -> updateWorkspaceResourceSettingLine(
                line,
                CUSTOM_COMPOSITION_RESOURCE,
                sourceWorkspaceName,
                targetWorkspaceName,
                sourceDirectory,
                targetDirectory
            ))
            .map(line -> updateWorkspaceResourceSettingLine(
                line,
                CUSTOM_POLICIES_RESOURCE,
                sourceWorkspaceName,
                targetWorkspaceName,
                sourceDirectory,
                targetDirectory
            ))
            .collect(Collectors.toList());

        if (!lines.equals(updatedLines)) {
            writeSettingsAtomically(testSettingsFile, updatedLines);
            return true;
        }

        return false;
    }

    private void writeSettingsAtomically(Path testSettingsFile, List<String> lines) throws IOException {
        Path temporaryFile = Files.createTempFile(testSettingsFile.getParent(), "test.settings-", ".tmp");
        try {
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            try {
                Files.move(
                    temporaryFile,
                    testSettingsFile,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING
                );
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, testSettingsFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    private String updateWorkspaceResourceSettingLine(
        String line,
        String settingName,
        String sourceWorkspaceName,
        String targetWorkspaceName,
        Path sourceDirectory,
        Path targetDirectory
    ) {
        int separatorIndex = line.indexOf('=');
        if (separatorIndex < 0 || !line.substring(0, separatorIndex).trim().equals(settingName)) {
            return line;
        }

        int valueStart = separatorIndex + 1;
        while (valueStart < line.length() && Character.isWhitespace(line.charAt(valueStart))) {
            valueStart++;
        }

        int valueEnd = line.length();
        while (valueEnd > valueStart && Character.isWhitespace(line.charAt(valueEnd - 1))) {
            valueEnd--;
        }

        String configuredValue = line.substring(valueStart, valueEnd);
        String updatedValue = updateWorkspaceResourceValue(
            configuredValue,
            sourceWorkspaceName,
            targetWorkspaceName,
            sourceDirectory,
            targetDirectory
        );
        if (configuredValue.equals(updatedValue)) {
            return line;
        }

        return line.substring(0, valueStart)
            + updatedValue
            + line.substring(valueEnd);
    }

    private String updateWorkspaceResourceValue(
        String configuredValue,
        String sourceWorkspaceName,
        String targetWorkspaceName,
        Path sourceDirectory,
        Path targetDirectory
    ) {
        String normalizedValue = configuredValue.replace('\\', '/');
        String relativeSourcePrefix = "./settings/" + sourceWorkspaceName + "/";
        if (normalizedValue.startsWith(relativeSourcePrefix)) {
            return "./settings/" + targetWorkspaceName + "/"
                + normalizedValue.substring(relativeSourcePrefix.length());
        }

        String settingsSourcePrefix = "settings/" + sourceWorkspaceName + "/";
        if (normalizedValue.startsWith(settingsSourcePrefix)) {
            return "settings/" + targetWorkspaceName + "/"
                + normalizedValue.substring(settingsSourcePrefix.length());
        }

        Path configuredPath = Paths.get(configuredValue);
        if (!configuredPath.isAbsolute()) {
            return configuredValue;
        }

        Path normalizedConfiguredPath = configuredPath.toAbsolutePath().normalize();
        Path normalizedSourceDirectory = sourceDirectory.toAbsolutePath().normalize();
        if (!normalizedConfiguredPath.startsWith(normalizedSourceDirectory)) {
            return configuredValue;
        }

        Path relativeResourcePath = normalizedSourceDirectory.relativize(normalizedConfiguredPath);
        return targetDirectory.resolve(relativeResourcePath).toString();
    }

    private boolean shouldCopyWorkspacePath(
        Path sourceDirectory,
        Path sourcePath,
        boolean copyTestGoals,
        boolean copyOracles
    ) {
        Path normalizedSourcePath = sourcePath.normalize();

        if (!copyTestGoals) {
            Path testGoalsDirectory = sourceDirectory.resolve(TEST_GOALS_DIRECTORY).normalize();
            if (normalizedSourcePath.startsWith(testGoalsDirectory)) {
                return false;
            }
        }

        if (!copyOracles) {
            Path oraclesDirectory = sourceDirectory.resolve(ORACLES_DIRECTORY).normalize();
            if (normalizedSourcePath.startsWith(oraclesDirectory)) {
                return false;
            }
        }

        return true;
    }

    private void ensureWorkspaceAssetDirectories(Path workspaceDirectory) throws IOException {
        Files.createDirectories(workspaceDirectory.resolve(TEST_GOALS_DIRECTORY));
        Files.createDirectories(workspaceDirectory.resolve(ORACLES_DIRECTORY).resolve(ORACLE_DSL_DIRECTORY));
        Files.createDirectories(workspaceDirectory.resolve(ORACLES_DIRECTORY).resolve(ORACLE_JAVA_DIRECTORY));
        Files.createDirectories(workspaceDirectory.resolve(ORACLES_DIRECTORY).resolve(ORACLE_COMPILED_DIRECTORY));
    }

    private Path resolveWorkspaceRuntimeHomeDirectory(String workspaceName) {
        Path testarWorkspaceDirectory = settingsRoot.resolve(workspaceName).normalize();
        if (testarWorkspaceDirectory.startsWith(settingsRoot) && Files.isDirectory(testarWorkspaceDirectory)) {
            return testarHomeDirectory();
        }

        Path cliWorkspaceDirectory = cliSettingsRoot.resolve(workspaceName).normalize();
        if (cliWorkspaceDirectory.startsWith(cliSettingsRoot) && Files.isDirectory(cliWorkspaceDirectory)) {
            return cliHomeDirectory();
        }

        throw new IllegalArgumentException("Workspace not found: " + workspaceName);
    }

    private List<WorkspaceFileDto> listWorkspaceSourceFiles(
        Path workspaceDirectory,
        Properties compositionProperties,
        Properties policyProperties
    ) {
        try (Stream<Path> children = Files.list(workspaceDirectory)) {
            return children
                .filter(path -> Files.isRegularFile(path) && path.getFileName().toString().endsWith(".java"))
                .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                .map(path -> {
                    String sourceName = path.getFileName().toString();
                    String category = inferSourceCategory(sourceName, compositionProperties, policyProperties);
                    return readWorkspaceFile(workspaceDirectory, sourceName, category);
                })
                .collect(Collectors.toList());
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to list workspace sources under: " + workspaceDirectory, exception);
        }
    }

    private WorkspaceFileDto readWorkspaceFile(Path workspaceDirectory, String fileName, String category) {
        Path file = workspaceDirectory.resolve(fileName).normalize();
        if (!file.startsWith(workspaceDirectory)) {
            throw new IllegalArgumentException("Invalid workspace file path: " + fileName);
        }

        if (!Files.isRegularFile(file)) {
            return new WorkspaceFileDto(fileName, file.toString(), "", category);
        }

        try {
            String content = Files.readString(file, StandardCharsets.UTF_8);
            return new WorkspaceFileDto(file.getFileName().toString(), file.toString(), content, category);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read workspace file: " + file, exception);
        }
    }

    private WorkspaceJavaCompileResultDto compileWorkspaceJava(Path workspaceDirectory, String scope, String targetName) {
        List<Path> javaFiles = listWorkspaceJavaFiles(workspaceDirectory);
        if (javaFiles.isEmpty()) {
            return new WorkspaceJavaCompileResultDto(
                false,
                scope,
                targetName,
                "No Java source files were found in the selected workspace.",
                List.of()
            );
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            return new WorkspaceJavaCompileResultDto(
                false,
                scope,
                targetName,
                "Java compiler is not available. Run Web Studio with a JDK.",
                List.of()
            );
        }

        Path outputDirectory = testarHomeDirectory()
            .resolve(".runtime")
            .resolve("webstudio-compile")
            .resolve(workspaceDirectory.getFileName().toString());

        try {
            Files.createDirectories(outputDirectory);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to create Java compile output directory: " + outputDirectory, exception);
        }

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null)) {
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(
                javaFiles.stream().map(Path::toFile).collect(Collectors.toList())
            );

            List<String> options = List.of(
                "-classpath",
                System.getProperty("java.class.path"),
                "-d",
                outputDirectory.toAbsolutePath().toString()
            );

            Boolean success = compiler.getTask(
                null,
                fileManager,
                diagnostics,
                options,
                null,
                compilationUnits
            ).call();

            List<WorkspaceJavaCompileDiagnosticDto> diagnosticDtos = diagnostics.getDiagnostics()
                .stream()
                .map(diagnostic -> toCompileDiagnostic(workspaceDirectory, diagnostic))
                .collect(Collectors.toList());

            return new WorkspaceJavaCompileResultDto(
                Boolean.TRUE.equals(success),
                scope,
                targetName,
                buildCompileMessage(Boolean.TRUE.equals(success), scope, targetName, diagnosticDtos),
                diagnosticDtos
            );
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to compile Java sources under: " + workspaceDirectory, exception);
        }
    }

    private List<Path> listWorkspaceJavaFiles(Path workspaceDirectory) {
        try (Stream<Path> children = Files.walk(workspaceDirectory)) {
            return children
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().endsWith(".java"))
                .sorted(Comparator.comparing(path -> path.toAbsolutePath().normalize().toString()))
                .collect(Collectors.toList());
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to list Java source files under: " + workspaceDirectory, exception);
        }
    }

    private WorkspaceJavaCompileDiagnosticDto toCompileDiagnostic(Path workspaceDirectory,
                                                                  Diagnostic<? extends JavaFileObject> diagnostic) {
        String fileName = "";
        String relativePath = "";

        if (diagnostic.getSource() != null && diagnostic.getSource().toUri() != null) {
            Path sourcePath = Path.of(diagnostic.getSource().toUri()).toAbsolutePath().normalize();
            fileName = sourcePath.getFileName() == null ? "" : sourcePath.getFileName().toString();
            relativePath = sourcePath.startsWith(workspaceDirectory)
                ? workspaceDirectory.relativize(sourcePath).toString()
                : sourcePath.toString();
        }

        return new WorkspaceJavaCompileDiagnosticDto(
            fileName,
            relativePath,
            diagnostic.getLineNumber(),
            diagnostic.getColumnNumber(),
            diagnostic.getKind().name(),
            diagnostic.getMessage(null)
        );
    }

    private String buildCompileMessage(boolean success,
                                       String scope,
                                       String targetName,
                                       List<WorkspaceJavaCompileDiagnosticDto> diagnostics) {
        if (success) {
            if ("profile".equals(scope)) {
                return "Compile Profile succeeded.";
            }

            return "Compile Java succeeded for " + targetName + ".";
        }

        long errorCount = diagnostics.stream()
            .filter(diagnostic -> "ERROR".equals(diagnostic.severity()))
            .count();

        if ("profile".equals(scope)) {
            return "Compile Profile failed with " + errorCount + " error(s).";
        }

        return "Compile Java failed for " + targetName + " with " + errorCount + " error(s).";
    }

    private void collectWorkspaceSummaries(Map<String, WorkspaceSummaryDto> summaries,
                                           Path root,
                                           boolean availableInTestar,
                                           boolean availableInCli) {
        if (!Files.isDirectory(root)) {
            return;
        }

        try (Stream<Path> children = Files.list(root)) {
            List<Path> directories = children
                    .filter(Files::isDirectory)
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .collect(Collectors.toList());

            for (Path directory : directories) {
                String workspaceName = directory.getFileName().toString();
                WorkspaceSummaryDto existingSummary = summaries.get(workspaceName);
                if (existingSummary == null) {
                    summaries.put(
                            workspaceName,
                            new WorkspaceSummaryDto(
                                    workspaceName,
                                    directory.toString(),
                                    availableInTestar,
                                    availableInCli
                            )
                    );
                    continue;
                }

                summaries.put(
                        workspaceName,
                        new WorkspaceSummaryDto(
                                workspaceName,
                                existingSummary.location(),
                                existingSummary.availableInTestar() || availableInTestar,
                                existingSummary.availableInCli() || availableInCli
                        )
                );
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to list settings workspaces under: " + root, exception);
        }
    }

    private Properties loadProperties(Path file) {
        Properties properties = new Properties();
        if (!Files.isRegularFile(file)) {
            return properties;
        }

        try (var inputStream = Files.newInputStream(file)) {
            properties.load(inputStream);
            return properties;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read properties file: " + file, exception);
        }
    }

    private Settings loadWorkspaceSettings(Path workspaceDirectory) {
        return new Settings(
            SettingsDefaults.getSettingsDefaults(),
            loadProperties(workspaceDirectory.resolve(TEST_SETTINGS_FILE))
        );
    }

    private List<WorkspaceModuleDefinitionDto> buildModuleDefinitions(Properties compositionProperties) {
        return Arrays.stream(ModuleWorkspaceHelper.MODULE_DEFINITIONS)
            .map(moduleDefinition -> new WorkspaceModuleDefinitionDto(
                moduleDefinition.propertyKey,
                moduleDefinition.label,
                compositionProperties.getProperty(moduleDefinition.propertyKey, "").trim()
            ))
            .collect(Collectors.toList());
    }

    private List<WorkspacePolicyDefinitionDto> buildPolicyDefinitions(Properties policyProperties) {
        return Arrays.stream(PolicyWorkspaceHelper.POLICY_DEFINITIONS)
            .map(policyDefinition -> new WorkspacePolicyDefinitionDto(
                policyDefinition.propertyKey,
                policyDefinition.label,
                PolicyWorkspaceHelper.parseClassList(policyProperties.getProperty(policyDefinition.propertyKey, ""))
            ))
            .collect(Collectors.toList());
    }

    private ModuleWorkspaceHelper.ModuleDefinition findModuleDefinition(String propertyKey) {
        return Arrays.stream(ModuleWorkspaceHelper.MODULE_DEFINITIONS)
            .filter(moduleDefinition -> moduleDefinition.propertyKey.equals(propertyKey))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown composition module property: " + propertyKey));
    }

    private PolicyWorkspaceHelper.PolicyDefinition findPolicyDefinition(String propertyKey) {
        return Arrays.stream(PolicyWorkspaceHelper.POLICY_DEFINITIONS)
            .filter(policyDefinition -> policyDefinition.propertyKey.equals(propertyKey))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown policy property: " + propertyKey));
    }

    private Map<String, List<String>> collectReferences(Properties compositionProperties, Properties policyProperties) {
        Map<String, List<String>> references = new LinkedHashMap<>();
        references.put("capabilities", referencedCompositionValues(compositionProperties, "CapabilityClass"));
        references.put("services", referencedCompositionValues(compositionProperties, "ServiceClass"));
        references.put("policies", referencedPolicyValues(policyProperties));
        return references;
    }

    private List<String> referencedCompositionValues(Properties properties, String keySuffix) {
        return properties
            .stringPropertyNames()
            .stream()
            .filter(name -> name.endsWith(keySuffix))
            .map(properties::getProperty)
            .map(String::trim)
            .filter(value -> !value.isEmpty())
            .collect(Collectors.toList());
    }

    private List<String> referencedPolicyValues(Properties properties) {
        return properties
            .stringPropertyNames()
            .stream()
            .filter(name -> name.endsWith("Policies"))
            .map(properties::getProperty)
            .flatMap(value -> splitPropertyValues(value).stream())
            .collect(Collectors.toList());
    }

    private List<String> splitPropertyValues(String value) {
        return List.of(value.split(";"))
            .stream()
            .map(String::trim)
            .filter(item -> !item.isEmpty())
            .collect(Collectors.toList());
    }

    private String inferSourceCategory(String sourceName, Properties compositionProperties, Properties policyProperties) {
        String className = sourceName.substring(0, sourceName.length() - ".java".length());
        if (referencedPolicyValues(policyProperties).contains(className)) {
            return "policy";
        }
        if (referencedCompositionValues(compositionProperties, "CapabilityClass").contains(className)) {
            return "capability";
        }
        if (referencedCompositionValues(compositionProperties, "ServiceClass").contains(className)) {
            return "service";
        }
        if (className.contains("Policy")) {
            return "policy";
        }
        if (className.contains("Capability")) {
            return "capability";
        }
        if (className.contains("Service") || className.contains("Composer")) {
            return "service";
        }
        return "source";
    }

    private void ensureJavaSourceName(String sourceName) {
        if (sourceName.contains("/") || sourceName.contains("\\") || !sourceName.endsWith(".java")) {
            throw new IllegalArgumentException("Invalid workspace source name: " + sourceName);
        }
    }

    private String readDebugFileContent(Path file) throws IOException {
        IOException lastException = null;

        for (Charset charset : List.of(StandardCharsets.UTF_8, Charset.defaultCharset(), Charset.forName("windows-1252"))) {
            try {
                return Files.readString(file, charset);
            } catch (IOException exception) {
                lastException = exception;
            }
        }

        throw lastException != null ? lastException : new IOException("Unable to read debug file content: " + file);
    }
}
