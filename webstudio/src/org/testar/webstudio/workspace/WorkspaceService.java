/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.workspace;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.testar.webstudio.api.dto.WorkspaceDocumentDto;
import org.testar.webstudio.api.dto.WorkspaceFileDto;
import org.testar.webstudio.api.dto.WorkspaceSummaryDto;
import org.testar.webstudio.api.dto.DebugFileDto;
import org.testar.webstudio.api.dto.DebugFileSummaryDto;

public final class WorkspaceService {

    private static final String TEST_SETTINGS_FILE = "test.settings";
    private static final String COMPOSITION_FILE = "composition.properties";
    private static final String POLICIES_FILE = "policies.properties";

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

    public Path workspaceDirectory(String workspaceName) {
        return resolveWorkspaceDirectory(workspaceName);
    }

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
            WorkspaceSettingsCatalog.buildSettingsGroups(settingsProperties)
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
