/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.workspace;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.testar.webstudio.api.dto.WorkspaceDocumentDto;
import org.testar.webstudio.api.dto.WorkspaceFileDto;
import org.testar.webstudio.api.dto.WorkspaceSummaryDto;

public final class WorkspaceService {

    private static final String TEST_SETTINGS_FILE = "test.settings";
    private static final String COMPOSITION_FILE = "composition.properties";
    private static final String POLICIES_FILE = "policies.properties";

    private final Path settingsRoot;

    public WorkspaceService() {
        this(resolveDefaultSettingsRoot());
    }

    public WorkspaceService(Path settingsRoot) {
        this.settingsRoot = settingsRoot;
    }

    public List<WorkspaceSummaryDto> listWorkspaces() {
        if (!Files.isDirectory(settingsRoot)) {
            return List.of();
        }

        try (Stream<Path> children = Files.list(settingsRoot)) {
            return children
                .filter(Files::isDirectory)
                .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                .map(path -> new WorkspaceSummaryDto(path.getFileName().toString(), path.toString()))
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to list settings workspaces under: " + settingsRoot, e);
        }
    }

    public Path settingsRoot() {
        return settingsRoot;
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

    private Path resolveWorkspaceDirectory(String workspaceName) {
        Path workspaceDirectory = settingsRoot.resolve(workspaceName).normalize();
        if (!workspaceDirectory.startsWith(settingsRoot)) {
            throw new IllegalArgumentException("Invalid workspace name: " + workspaceName);
        }

        if (!Files.isDirectory(workspaceDirectory)) {
            throw new IllegalArgumentException("Workspace not found: " + workspaceName);
        }

        return workspaceDirectory;
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
                    return readWorkspaceFile(workspaceDirectory.getFileName().toString(), sourceName, category);
                })
                .collect(Collectors.toList());
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to list workspace sources under: " + workspaceDirectory, exception);
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
}
