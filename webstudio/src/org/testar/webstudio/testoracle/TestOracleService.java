/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.testoracle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.testar.config.ConfigTags;
import org.testar.config.TestarDirectories;
import org.testar.oracle.OracleSelection;
import org.testar.rascal.DslOracleCompiler;
import org.testar.rascal.DslOracleMetadata;
import org.testar.rascal.DslOracleMetadataGenerator;
import org.testar.rascal.DslOracleOperationResult;
import org.testar.webstudio.api.dto.TestOracleDslDiagnosticDto;
import org.testar.webstudio.api.dto.TestOracleDslResultDto;
import org.testar.webstudio.api.dto.TestOracleInventoryDto;
import org.testar.webstudio.api.dto.TestOracleItemDto;
import org.testar.webstudio.api.dto.WorkspaceFileDto;
import org.testar.webstudio.api.dto.WorkspaceJavaCompileDiagnosticDto;
import org.testar.webstudio.api.dto.WorkspaceJavaCompileResultDto;
import org.testar.webstudio.workspace.WorkspaceService;

public final class TestOracleService {

    private static final String BUILT_IN_ORIGIN = "BUILT_IN";
    private static final String WORKSPACE_JAVA_ORIGIN = "WORKSPACE_JAVA";
    private static final String DSL_SOURCE_ORIGIN = "DSL_SOURCE";
    private static final String TEST_SETTINGS_FILE = "test.settings";
    private static final Pattern ORACLE_CLASS_PATTERN = Pattern.compile(
        "\\b(?:public\\s+)?(?:static\\s+)?class\\s+([A-Za-z_$][A-Za-z\\d_$]*)\\s+(?:extends|implements)\\s+(?:[A-Za-z_$][A-Za-z\\d_$]*\\.)*(?:DslOracle|Oracle)\\b"
    );

    private final WorkspaceService workspaceService;
    private DslOracleCompiler dslOracleCompiler;
    private DslOracleMetadata dslOracleMetadata;

    public TestOracleService(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    public TestOracleInventoryDto inventory(String workspaceName) {
        Path workspaceDirectory = workspaceService.workspaceDirectory(workspaceName);
        List<String> activeOracles = readActiveOracles(workspaceDirectory);

        return withWorkspaceOracleDirectories(workspaceName, () -> {
            Set<String> activeOracleSet = new LinkedHashSet<>(activeOracles);
            List<String> builtInOracles = OracleSelection.getAvailableBuiltInOracles();
            Set<String> builtInOracleSet = new LinkedHashSet<>(builtInOracles);
            Map<String, List<String>> workspaceJavaOracles = OracleSelection.getAvailableWorkspaceJavaOracles();

            List<TestOracleItemDto> items = new ArrayList<>();
            for (String builtInOracle : builtInOracles) {
                items.add(new TestOracleItemDto(
                    builtInOracle,
                    BUILT_IN_ORIGIN,
                    "",
                    activeOracleSet.contains(builtInOracle),
                    false,
                    false
                ));
            }

            workspaceJavaOracles.forEach((fileName, oracleNames) -> {
                for (String oracleName : oracleNames) {
                    items.add(new TestOracleItemDto(
                        oracleName,
                        WORKSPACE_JAVA_ORIGIN,
                        fileName,
                        activeOracleSet.contains(oracleName),
                        true,
                        builtInOracleSet.contains(oracleName)
                    ));
                }
            });

            items.addAll(listDslItems(workspaceDirectory));

            items.sort(Comparator
                .comparing(TestOracleItemDto::origin)
                .thenComparing(TestOracleItemDto::name)
                .thenComparing(TestOracleItemDto::path));

            return new TestOracleInventoryDto(workspaceName, activeOracles, items);
        });
    }

    public synchronized DslOracleMetadata dslMetadata() {
        if (dslOracleMetadata == null) {
            dslOracleMetadata = new DslOracleMetadataGenerator().generate();
        }

        return dslOracleMetadata;
    }

    public WorkspaceFileDto readDslFile(String workspaceName, String relativePath) {
        Path dslRoot = dslRoot(workspaceName);
        Path file = resolveDslPath(dslRoot, relativePath);
        if (!Files.isRegularFile(file)) {
            throw new IllegalArgumentException("DSL oracle file does not exist: " + relativePath);
        }

        try {
            return new WorkspaceFileDto(
                file.getFileName().toString(),
                toRelativePath(dslRoot, file),
                Files.readString(file, StandardCharsets.UTF_8),
                "dsl-oracle"
            );
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read DSL oracle file: " + relativePath, exception);
        }
    }

    public WorkspaceFileDto saveDslFile(String workspaceName, String relativePath, String content) {
        Path dslRoot = dslRoot(workspaceName);
        Path file = resolveDslPath(dslRoot, relativePath);
        rejectNonDslFile(file, relativePath);

        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, content == null ? "" : content, StandardCharsets.UTF_8);
            return readDslFile(workspaceName, toRelativePath(dslRoot, file));
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to save DSL oracle file: " + relativePath, exception);
        }
    }

    public WorkspaceFileDto createDslFile(String workspaceName, String relativePath) {
        Path dslRoot = dslRoot(workspaceName);
        Path file = resolveDslPath(dslRoot, relativePath);
        rejectNonDslFile(file, relativePath);
        if (Files.exists(file)) {
            throw new IllegalArgumentException("DSL oracle file already exists: " + relativePath);
        }

        return saveDslFile(workspaceName, relativePath, defaultDslOracleContent(file));
    }

    public TestOracleInventoryDto deleteDslFile(String workspaceName, String relativePath) {
        Path dslRoot = dslRoot(workspaceName);
        Path file = resolveDslPath(dslRoot, relativePath);
        rejectNonDslFile(file, relativePath);

        if (Files.isDirectory(file)) {
            throw new IllegalArgumentException("DSL oracle path is a directory: " + relativePath);
        }

        try {
            Files.deleteIfExists(file);
            return inventory(workspaceName);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to delete DSL oracle file: " + relativePath, exception);
        }
    }

    public TestOracleDslResultDto validateDslFile(String workspaceName, String relativePath, String content) {
        Path dslRoot = dslRoot(workspaceName);
        Path file = resolveDslPath(dslRoot, relativePath);
        rejectNonDslFile(file, relativePath);

        DslOracleOperationResult result = dslOracleCompiler().validate(
            toRelativePath(dslRoot, file),
            content == null ? "" : content
        );

        return toDslResultDto(result, "");
    }

    public TestOracleDslResultDto generateJavaFromDslFile(String workspaceName, String relativePath, String content) {
        WorkspaceFileDto savedDslFile = saveDslFile(workspaceName, relativePath, content);
        DslOracleOperationResult result = dslOracleCompiler().generateJava(savedDslFile.location(), savedDslFile.content());
        if (!result.success()) {
            return toDslResultDto(result, "");
        }

        Path javaRoot = javaRoot(workspaceName);
        Path generatedJavaFile = generatedJavaPath(javaRoot, savedDslFile.location());
        saveJavaFile(
            workspaceName,
            toRelativePath(javaRoot, generatedJavaFile),
            result.generatedJavaSource()
        );
        enableOracleClassNames(workspaceName, oracleClassNamesFromJavaSource(result.generatedJavaSource()));

        return toDslResultDto(result, toRelativePath(javaRoot, generatedJavaFile));
    }

    public WorkspaceFileDto readJavaFile(String workspaceName, String relativePath) {
        Path javaRoot = javaRoot(workspaceName);
        Path file = resolveOracleSourcePath(javaRoot, relativePath, "Java oracle");
        if (!Files.isRegularFile(file)) {
            throw new IllegalArgumentException("Java oracle file does not exist: " + relativePath);
        }

        try {
            return new WorkspaceFileDto(
                file.getFileName().toString(),
                toRelativePath(javaRoot, file),
                Files.readString(file, StandardCharsets.UTF_8),
                "java-oracle"
            );
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read Java oracle file: " + relativePath, exception);
        }
    }

    public WorkspaceFileDto saveJavaFile(String workspaceName, String relativePath, String content) {
        Path javaRoot = javaRoot(workspaceName);
        Path file = resolveOracleSourcePath(javaRoot, relativePath, "Java oracle");
        rejectExtension(file, relativePath, ".java", "Java oracle");

        try {
            Files.createDirectories(file.getParent());
            String sourceContent = content == null ? "" : content;
            Files.writeString(file, sourceContent, StandardCharsets.UTF_8);
            return readJavaFile(workspaceName, toRelativePath(javaRoot, file));
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to save Java oracle file: " + relativePath, exception);
        }
    }

    public WorkspaceFileDto createJavaFile(String workspaceName, String relativePath) {
        Path javaRoot = javaRoot(workspaceName);
        Path file = resolveOracleSourcePath(javaRoot, relativePath, "Java oracle");
        rejectExtension(file, relativePath, ".java", "Java oracle");
        if (Files.exists(file)) {
            throw new IllegalArgumentException("Java oracle file already exists: " + relativePath);
        }

        WorkspaceFileDto createdFile = saveJavaFile(workspaceName, relativePath, defaultJavaOracleContent(file));
        enableOracleClassNames(workspaceName, List.of(classNameFromJavaFile(file)));
        return createdFile;
    }

    public TestOracleInventoryDto deleteJavaFile(String workspaceName, String relativePath) {
        Path javaRoot = javaRoot(workspaceName);
        Path file = resolveOracleSourcePath(javaRoot, relativePath, "Java oracle");
        rejectExtension(file, relativePath, ".java", "Java oracle");

        if (Files.isDirectory(file)) {
            throw new IllegalArgumentException("Java oracle path is a directory: " + relativePath);
        }

        try {
            Files.deleteIfExists(file);
            return inventory(workspaceName);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to delete Java oracle file: " + relativePath, exception);
        }
    }

    public WorkspaceJavaCompileResultDto compileJavaFile(String workspaceName, String relativePath, String content) {
        WorkspaceFileDto savedFile = saveJavaFile(workspaceName, relativePath, content);
        Path javaRoot = javaRoot(workspaceName);
        List<Path> javaFiles = listJavaFiles(javaRoot);
        if (javaFiles.isEmpty()) {
            return new WorkspaceJavaCompileResultDto(
                false,
                "oracle-source",
                savedFile.name(),
                "No Java oracle source files were found.",
                List.of()
            );
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            return new WorkspaceJavaCompileResultDto(
                false,
                "oracle-source",
                savedFile.name(),
                "Java compiler is not available. Run Web Studio with a JDK.",
                List.of()
            );
        }

        Path compiledRoot = javaRoot(workspaceName).getParent().resolve("compiled").toAbsolutePath().normalize();
        try {
            Files.createDirectories(compiledRoot);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to create oracle compile output directory: " + compiledRoot, exception);
        }

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null)) {
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(
                javaFiles.stream().map(Path::toFile).collect(Collectors.toList())
            );
            List<String> options = List.of(
                "-classpath",
                System.getProperty("java.class.path"),
                "-sourcepath",
                javaRoot.toAbsolutePath().toString(),
                "-d",
                compiledRoot.toString()
            );

            Boolean success = compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits).call();
            List<WorkspaceJavaCompileDiagnosticDto> diagnosticDtos = diagnostics.getDiagnostics()
                .stream()
                .map(diagnostic -> toCompileDiagnostic(javaRoot, diagnostic))
                .collect(Collectors.toList());
            boolean succeeded = Boolean.TRUE.equals(success);
            if (succeeded) {
                enableOracleClassNames(workspaceName, oracleClassNamesFromJavaSource(savedFile.content()));
            }

            return new WorkspaceJavaCompileResultDto(
                succeeded,
                "oracle-source",
                savedFile.name(),
                succeeded
                    ? "Java oracle compilation succeeded for " + savedFile.name() + "."
                    : "Java oracle compilation failed for " + savedFile.name() + ".",
                diagnosticDtos
            );
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to compile Java oracle files under: " + javaRoot, exception);
        }
    }

    private List<String> readActiveOracles(Path workspaceDirectory) {
        Properties properties = new Properties();
        Path settingsFile = workspaceDirectory.resolve(TEST_SETTINGS_FILE);
        if (!Files.isRegularFile(settingsFile)) {
            return List.of();
        }

        try (InputStream input = Files.newInputStream(settingsFile)) {
            properties.load(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read settings file: " + settingsFile, exception);
        }

        String activeOracles = properties.getProperty(ConfigTags.ExtendedOracles.name(), "");
        if (activeOracles.isBlank()) {
            return List.of();
        }

        return Stream.of(activeOracles.split(","))
            .map(String::trim)
            .filter(name -> !name.isBlank())
            .collect(Collectors.toList());
    }

    private List<TestOracleItemDto> listDslItems(Path workspaceDirectory) {
        Path dslDirectory = workspaceDirectory.resolve("oracles").resolve("dsl").normalize();
        if (!dslDirectory.startsWith(workspaceDirectory) || !Files.isDirectory(dslDirectory)) {
            return List.of();
        }

        try (Stream<Path> files = Files.walk(dslDirectory)) {
            return files
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().toLowerCase().endsWith(".testar"))
                .map(path -> new TestOracleItemDto(
                    path.getFileName().toString(),
                    DSL_SOURCE_ORIGIN,
                    dslDirectory.relativize(path).toString().replace(File.separatorChar, '/'),
                    false,
                    true,
                    false
                ))
                .sorted(Comparator.comparing(TestOracleItemDto::path))
                .collect(Collectors.toList());
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to list DSL oracle files under: " + dslDirectory, exception);
        }
    }

    private Path dslRoot(String workspaceName) {
        Path workspaceDirectory = workspaceService.workspaceDirectory(workspaceName);
        Path dslRoot = workspaceDirectory.resolve("oracles").resolve("dsl").toAbsolutePath().normalize();
        if (!dslRoot.startsWith(workspaceDirectory)) {
            throw new IllegalArgumentException("Invalid workspace oracle DSL root for workspace: " + workspaceName);
        }

        try {
            Files.createDirectories(dslRoot);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to create DSL oracle root: " + dslRoot, exception);
        }

        return dslRoot;
    }

    private Path javaRoot(String workspaceName) {
        Path workspaceDirectory = workspaceService.workspaceDirectory(workspaceName);
        Path javaRoot = workspaceDirectory.resolve("oracles").resolve("java").toAbsolutePath().normalize();
        if (!javaRoot.startsWith(workspaceDirectory)) {
            throw new IllegalArgumentException("Invalid workspace oracle Java root for workspace: " + workspaceName);
        }

        try {
            Files.createDirectories(javaRoot);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to create Java oracle root: " + javaRoot, exception);
        }

        return javaRoot;
    }

    private Path generatedJavaPath(Path javaRoot, String dslRelativePath) {
        String javaRelativePath = dslRelativePath.replaceFirst("(?i)\\.testar$", ".java");
        Path generatedJavaFile = javaRoot.resolve(javaRelativePath).toAbsolutePath().normalize();
        if (!generatedJavaFile.startsWith(javaRoot)) {
            throw new IllegalArgumentException("Invalid generated Java oracle path: " + dslRelativePath);
        }

        return generatedJavaFile;
    }

    private Path resolveDslPath(Path dslRoot, String relativePath) {
        return resolveOracleSourcePath(dslRoot, relativePath, "DSL oracle");
    }

    private Path resolveOracleSourcePath(Path root, String relativePath, String sourceType) {
        String normalizedRelativePath = relativePath == null ? "" : relativePath.replace('\\', '/').trim();
        if (normalizedRelativePath.isBlank()) {
            throw new IllegalArgumentException(sourceType + " path is required.");
        }

        Path resolvedPath = root.resolve(normalizedRelativePath).toAbsolutePath().normalize();
        if (!resolvedPath.startsWith(root)) {
            throw new IllegalArgumentException("Invalid " + sourceType + " path: " + relativePath);
        }

        rejectExistingSymlinkEscape(root, resolvedPath, relativePath, sourceType);
        return resolvedPath;
    }

    private void rejectExistingSymlinkEscape(Path root, Path resolvedPath, String relativePath, String sourceType) {
        if (!Files.exists(resolvedPath)) {
            return;
        }

        try {
            Path realRoot = root.toRealPath();
            Path realPath = resolvedPath.toRealPath();
            if (!realPath.startsWith(realRoot)) {
                throw new IllegalArgumentException("Invalid " + sourceType + " path: " + relativePath);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to resolve " + sourceType + " path: " + relativePath, exception);
        }
    }

    private String toRelativePath(Path root, Path path) {
        return root.relativize(path.toAbsolutePath().normalize()).toString().replace('\\', '/');
    }

    private void rejectNonDslFile(Path file, String relativePath) {
        rejectExtension(file, relativePath, ".testar", "DSL oracle");
    }

    private void rejectExtension(Path file, String relativePath, String extension, String sourceType) {
        String fileName = file.getFileName() == null ? "" : file.getFileName().toString().toLowerCase();
        if (!fileName.endsWith(extension)) {
            throw new IllegalArgumentException(sourceType + " file must use " + extension + " extension: " + relativePath);
        }
    }

    private String defaultDslOracleContent(Path file) {
        String baseName = file.getFileName().toString().replaceFirst("(?i)\\.testar$", "");
        return String.join("\r\n",
            "// " + baseName,
            "// Write a TESTAR oracle DSL rule here.",
            ""
        );
    }

    private String defaultJavaOracleContent(Path file) {
        String className = file.getFileName().toString().replaceFirst("(?i)\\.java$", "");
        return String.join("\r\n",
            "import java.util.Collections;",
            "import java.util.List;",
            "",
            "import org.testar.core.state.State;",
            "import org.testar.core.verdict.Verdict;",
            "import org.testar.oracle.Oracle;",
            "",
            "public class " + className + " implements Oracle {",
            "",
            "    @Override",
            "    public List<Verdict> getVerdicts(State state) {",
            "        markAsNonVacuous();",
            "        return Collections.singletonList(Verdict.OK);",
            "    }",
            "}",
            ""
        );
    }

    private List<Path> listJavaFiles(Path javaRoot) {
        try (Stream<Path> files = Files.walk(javaRoot)) {
            return files
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().endsWith(".java"))
                .sorted(Comparator.comparing(path -> path.toAbsolutePath().normalize().toString()))
                .collect(Collectors.toList());
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to list Java oracle files under: " + javaRoot, exception);
        }
    }

    private WorkspaceJavaCompileDiagnosticDto toCompileDiagnostic(Path javaRoot,
                                                                  Diagnostic<? extends JavaFileObject> diagnostic) {
        String fileName = "";
        String relativePath = "";

        if (diagnostic.getSource() != null && diagnostic.getSource().toUri() != null) {
            Path sourcePath = Path.of(diagnostic.getSource().toUri()).toAbsolutePath().normalize();
            fileName = sourcePath.getFileName() == null ? "" : sourcePath.getFileName().toString();
            relativePath = sourcePath.startsWith(javaRoot)
                ? javaRoot.relativize(sourcePath).toString().replace('\\', '/')
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

    private TestOracleDslResultDto toDslResultDto(DslOracleOperationResult result, String generatedJavaPath) {
        List<TestOracleDslDiagnosticDto> diagnostics = result.diagnostics().stream()
            .map(diagnostic -> new TestOracleDslDiagnosticDto(
                diagnostic.severity(),
                diagnostic.line(),
                diagnostic.column(),
                diagnostic.message()
            ))
            .collect(Collectors.toList());

        return new TestOracleDslResultDto(
            result.success(),
            result.message(),
            generatedJavaPath,
            diagnostics
        );
    }

    private DslOracleCompiler dslOracleCompiler() {
        if (dslOracleCompiler == null) {
            dslOracleCompiler = new DslOracleCompiler();
        }

        return dslOracleCompiler;
    }

    private List<String> oracleClassNamesFromJavaSource(String javaSource) {
        if (javaSource == null || javaSource.isBlank()) {
            return List.of();
        }

        List<String> oracleClassNames = new ArrayList<>();
        Matcher matcher = ORACLE_CLASS_PATTERN.matcher(javaSource);
        while (matcher.find()) {
            String className = matcher.group(1);
            if (!oracleClassNames.contains(className)) {
                oracleClassNames.add(className);
            }
        }

        return oracleClassNames;
    }

    private String classNameFromJavaFile(Path file) {
        return file.getFileName().toString().replaceFirst("(?i)\\.java$", "");
    }

    private void enableOracleClassNames(String workspaceName, List<String> oracleClassNames) {
        List<String> normalizedNames = oracleClassNames.stream()
            .map(name -> name == null ? "" : name.trim())
            .filter(name -> !name.isBlank())
            .distinct()
            .collect(Collectors.toList());
        if (normalizedNames.isEmpty()) {
            return;
        }

        Path settingsFile = workspaceService.workspaceDirectory(workspaceName).resolve(TEST_SETTINGS_FILE);
        try {
            String content = Files.isRegularFile(settingsFile)
                ? Files.readString(settingsFile, StandardCharsets.UTF_8)
                : "";
            List<String> enabledOracles = new ArrayList<>(readActiveOracles(workspaceService.workspaceDirectory(workspaceName)));
            boolean changed = false;
            for (String oracleName : normalizedNames) {
                if (!enabledOracles.contains(oracleName)) {
                    enabledOracles.add(oracleName);
                    changed = true;
                }
            }
            if (!changed) {
                return;
            }

            Files.writeString(
                settingsFile,
                settingsContentWithExtendedOracles(content, enabledOracles),
                StandardCharsets.UTF_8
            );
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to enable workspace Java oracle classes.", exception);
        }
    }

    private String settingsContentWithExtendedOracles(String content, List<String> enabledOracles) {
        String nextValue = String.join(",", enabledOracles);
        String[] lines = content.split("\\R", -1);
        StringBuilder builder = new StringBuilder();
        boolean replaced = false;

        for (int index = 0; index < lines.length; index++) {
            String line = lines[index];
            if (!replaced && line.trim().startsWith(ConfigTags.ExtendedOracles.name())) {
                builder.append(ConfigTags.ExtendedOracles.name()).append(" = ").append(nextValue);
                replaced = true;
            } else {
                builder.append(line);
            }

            if (index < lines.length - 1) {
                builder.append("\r\n");
            }
        }

        if (replaced) {
            return builder.toString();
        }

        if (!content.isEmpty() && !content.endsWith("\n") && !content.endsWith("\r")) {
            builder.append("\r\n");
        }
        builder.append(ConfigTags.ExtendedOracles.name()).append(" = ").append(nextValue).append("\r\n");
        return builder.toString();
    }

    private <T> T withWorkspaceOracleDirectories(String workspaceName, WorkspaceOracleInventorySupplier<T> supplier) {
        String previousSettingsDirectory = TestarDirectories.getSettingsDir();
        String previousSelectedSse = TestarDirectories.getSelectedSse();

        try {
            TestarDirectories.setSettingsDir(workspaceService.settingsRoot().toString() + File.separator);
            TestarDirectories.setSelectedSse(workspaceName);
            return supplier.get();
        } finally {
            TestarDirectories.setSettingsDir(previousSettingsDirectory);
            TestarDirectories.setSelectedSse(previousSelectedSse);
        }
    }

    private interface WorkspaceOracleInventorySupplier<T> {

        T get();
    }
}
