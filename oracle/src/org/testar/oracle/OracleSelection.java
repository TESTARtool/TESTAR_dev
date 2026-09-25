/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.reflections8.Reflections;
import org.testar.config.TestarDirectories;

public class OracleSelection {

    /** Extended Internal Oracles **/

    private static final Pattern PACKAGE_PATTERN = Pattern.compile("(?m)^\\s*package\\s+([A-Za-z_$][\\w$]*(?:\\.[A-Za-z_$][\\w$]*)*)\\s*;");
    private static final Pattern TOP_LEVEL_CLASS_PATTERN = Pattern.compile("(?m)^(?:public\\s+)?(?:abstract\\s+|final\\s+)?class\\s+([A-Za-z_$][\\w$]*)\\b");

    private static String[] oraclePackages = {
            "org.testar.oracle.generic.visual"
    };
    private static volatile List<String> availableBuiltInOracles;

    public static List<String> getAvailableBuiltInOracles() {
        List<String> cachedOracles = availableBuiltInOracles;
        if (cachedOracles != null) {
            return cachedOracles;
        }

        Set<String> oracleNames = new LinkedHashSet<>();
        for (String pkg : oraclePackages) {
            try {
                Reflections reflections = new Reflections(pkg);
                Set<Class<? extends Oracle>> oracleClasses = reflections.getSubTypesOf(Oracle.class);

                for (Class<? extends Oracle> oracleClass : oracleClasses) {
                    oracleNames.add(oracleClass.getSimpleName());
                }
            } catch (Exception e) {
                System.out.println("Error loading package: " + pkg);
            }
        }
        availableBuiltInOracles = List.copyOf(oracleNames);
        return availableBuiltInOracles;
    }

    public static List<String> getAvailableExtendedOracles() {
        Set<String> oracleNames = new LinkedHashSet<>();
        oracleNames.addAll(getAvailableBuiltInOracles());

        // ExtendedOracles is the single active list. Names are resolved first from
        // workspace Java oracles, then from built-in oracle packages.
        for (List<String> workspaceOracleNames : getAvailableWorkspaceJavaOracles().values()) {
            oracleNames.addAll(workspaceOracleNames);
        }
        return new ArrayList<>(oracleNames);
    }

    public static List<Oracle> loadExtendedOracles(String selectedOracles) {
        List<Oracle> oraclesList = new ArrayList<>();
        List<String> selectedNames = parseSelectedOracleNames(selectedOracles);

        if (selectedNames.isEmpty()) {
            return oraclesList;
        }

        // Workspace oracles intentionally override built-ins with the same simple name.
        Map<String, Oracle> workspaceOracles = loadWorkspaceOracleMap(new LinkedHashSet<>(selectedNames));

        for (String oracleName : selectedNames) {
            Oracle workspaceOracle = workspaceOracles.get(oracleName);
            if (workspaceOracle != null) {
                oraclesList.add(workspaceOracle);
                continue;
            }

            Optional<Oracle> builtInOracle = loadBuiltInOracle(oracleName);
            builtInOracle.ifPresent(oraclesList::add);
        }

        return oraclesList;
    }

    private static Optional<Oracle> loadBuiltInOracle(String oracleName) {
        for (String pkg : oraclePackages) {
            try {
                String fullClassName = pkg + "." + oracleName;
                Class<?> oracleClass = Class.forName(fullClassName);

                if (Oracle.class.isAssignableFrom(oracleClass)) {
                    Constructor<?> constructor = oracleClass.getDeclaredConstructor();
                    return Optional.of((Oracle) constructor.newInstance());
                }
            } catch (ClassNotFoundException e) {
                // Continue trying other packages
            } catch (Exception e) {
                System.out.println("Error instantiating " + oracleName + ": " + e.getMessage());
            }
        }

        return Optional.empty();
    }

    /** Workspace Java Oracles **/

    public static Map<String, List<String>> getAvailableWorkspaceJavaOracles() {
        Map<String, List<String>> fileToOraclesMap = new LinkedHashMap<>();

        File javaDir = new File(TestarDirectories.getWorkspaceOracleJavaDir());
        File compiledDir = new File(TestarDirectories.getWorkspaceOracleCompiledDir());

        List<File> javaFiles = findJavaFiles(javaDir);

        if (javaFiles == null || javaFiles.size() == 0) {
            return Map.of();
        }

        if (!compiledDir.exists()) {
            compiledDir.mkdirs();
        } else {
            cleanObsoleteClassFiles(javaDir, compiledDir);
        }

        compileJavaFiles(javaFiles, compiledDir);
        List<SourceClassInfo> sourceClassInfos = sourceClassInfos(javaFiles, compiledDir.toPath());

        try (URLClassLoader classLoader = new URLClassLoader(
                new URL[]{compiledDir.toURI().toURL()},
                OracleSelection.class.getClassLoader()
        )) {
            List<Path> classFiles;
            try (java.util.stream.Stream<Path> paths = Files.walk(compiledDir.toPath())) {
                classFiles = paths
                        .filter(path -> path.toString().endsWith(".class"))
                        .sorted()
                        .collect(Collectors.toList());
            }

            for (Path classPath : classFiles) {
                Optional<SourceClassInfo> sourceClassInfo = matchingSourceClassInfo(sourceClassInfos, classPath);
                if (sourceClassInfo.isEmpty()) {
                    continue;
                }

                try {
                    String className = getClassName(compiledDir.toPath(), classPath);
                    Class<?> oracleClass = classLoader.loadClass(className);
                    if (!Oracle.class.isAssignableFrom(oracleClass) || Modifier.isAbstract(oracleClass.getModifiers())) {
                        continue;
                    }

                    String sourcePath = javaDir.toPath()
                            .relativize(sourceClassInfo.get().sourceFile.toPath())
                            .toString()
                            .replace(File.separatorChar, '/');
                    fileToOraclesMap
                            .computeIfAbsent(sourcePath, ignored -> new ArrayList<>())
                            .add(oracleClass.getSimpleName());
                } catch (Exception exception) {
                    System.out.println("Skipping class: " + exception.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error scanning compiled oracles: " + e.getMessage());
        }

        return fileToOraclesMap;
    }

    public static List<Oracle> loadWorkspaceJavaOracles(String selectedOracles) {
        File javaDir = new File(TestarDirectories.getWorkspaceOracleJavaDir());
        if (!javaDir.exists() || !javaDir.isDirectory()) {
            return List.of();
        }

        Set<String> selectedNames = new LinkedHashSet<>(parseSelectedOracleNames(selectedOracles));
        return new ArrayList<>(loadWorkspaceOracleMap(selectedNames).values());
    }

    private static Map<String, Oracle> loadWorkspaceOracleMap(Set<String> selectedNames) {
        Map<String, Oracle> oracles = new LinkedHashMap<>();
        File javaDir = new File(TestarDirectories.getWorkspaceOracleJavaDir());
        if (!javaDir.exists() || !javaDir.isDirectory()) {
            return oracles;
        }

        List<File> javaFiles = findJavaFiles(javaDir);
        if (javaFiles == null || javaFiles.size() == 0) {
            return oracles;
        }

        System.out.println("Loading workspace Java oracles from: " + javaDir.getAbsolutePath());

        File outputDir = new File(TestarDirectories.getWorkspaceOracleCompiledDir());
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        } else {
            cleanObsoleteClassFiles(javaDir, outputDir);
        }

        compileJavaFiles(javaFiles, outputDir);
        for (Oracle oracle : loadCompiledOracles(outputDir, selectedNames)) {
            oracles.put(oracle.getClass().getSimpleName(), oracle);
        }
        System.out.println("Loaded workspace Java oracles: " + oracles.size());
        return oracles;
    }

    private static void cleanObsoleteClassFiles(File javaSourceRoot, File outputDir) {
        try {
            List<SourceClassInfo> sourceClassInfos = sourceClassInfos(findJavaFiles(javaSourceRoot), outputDir.toPath());

            Files.walk(outputDir.toPath())
                    .filter(p -> p.toString().endsWith(".class"))
                    .forEach(classPath -> {
                        try {
                            Optional<SourceClassInfo> sourceClassInfo = matchingSourceClassInfo(sourceClassInfos, classPath);
                            boolean isOrphan = sourceClassInfo.isEmpty();
                            boolean isStale = sourceClassInfo.isPresent()
                                    && Files.getLastModifiedTime(classPath).toMillis() < sourceClassInfo.get().lastModified;

                            if (isOrphan || isStale) {
                                classPath.toFile().delete();
                            }
                        } catch (Exception e) {
                            System.out.println("Error cleaning class file: " + classPath + " -> " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            System.out.println("Failed to walk output dir for cleanup: " + e.getMessage());
        }
    }

    private static boolean compileJavaFiles(List<File> javaFiles, File outputDir) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.out.println("No Java compiler available.");
            return false;
        }

        List<File> modifiedJavaFiles = javaFiles.stream()
                .filter(javaFile -> needsCompilation(javaFile, outputDir))
                .collect(Collectors.toList());
        if (modifiedJavaFiles.isEmpty()) {
            return true;
        }

        return compileJavaFiles(compiler, modifiedJavaFiles, outputDir);
    }

    private static boolean compileJavaFiles(JavaCompiler compiler, List<File> javaFiles, File outputDir) {
        String fileNames = javaFiles.stream()
                .map(File::getName)
                .collect(Collectors.joining(", "));
        System.out.println("Compiling added or modified external oracles... " + fileNames);

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null)) {
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(javaFiles);
            List<String> options = List.of(
                    "-d", outputDir.getAbsolutePath(),
                    "-sourcepath", TestarDirectories.getWorkspaceOracleJavaDir(),
                    "-classpath", System.getProperty("java.class.path") + File.pathSeparator + outputDir.getAbsolutePath()
                    );

            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits);
            boolean success = task.call();

            if (!success) {
                System.out.println("Failed to compile workspace Java oracles: " + fileNames);
                diagnostics.getDiagnostics().forEach(d -> System.out.println(d.toString()));
            }

            return success;
        } catch (IOException e) {
            System.out.println("Workspace Java oracle compilation error: " + e.getMessage());
            return false;
        }
    }

    public static List<Oracle> loadCompiledOracles(File outputDir, Set<String> selectedNames) {
        List<Oracle> oracles = new ArrayList<>();
        Map<String, Class<?>> oracleClassesBySimpleName = new LinkedHashMap<>();
        Set<String> duplicateOracleNames = new LinkedHashSet<>();

        try (URLClassLoader classLoader = new URLClassLoader(
                new URL[]{outputDir.toURI().toURL()},
                OracleSelection.class.getClassLoader()
        )) {
            List<Path> classFiles = Files.walk(outputDir.toPath())
                    .filter(p -> p.toString().endsWith(".class"))
                    .collect(Collectors.toList());

            for (Path classPath : classFiles) {
                try {
                    String className = getClassName(outputDir.toPath(), classPath);
                    Class<?> clazz = classLoader.loadClass(className);

                    if (Oracle.class.isAssignableFrom(clazz) && !java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
                        String simpleName = clazz.getSimpleName();
                        Class<?> existingClass = oracleClassesBySimpleName.putIfAbsent(simpleName, clazz);
                        if (existingClass != null && !existingClass.getName().equals(clazz.getName())) {
                            duplicateOracleNames.add(simpleName);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Failed to load compiled oracle: " + e.getMessage());
                }
            }

            if (!duplicateOracleNames.isEmpty()) {
                throw new IllegalStateException(
                        "Duplicate workspace oracle class name(s): "
                        + String.join(", ", duplicateOracleNames)
                        + ". Oracle class names must be unique in the workspace."
                );
            }

            for (Class<?> oracleClass : oracleClassesBySimpleName.values()) {
                if (selectedNames.isEmpty() || selectedNames.contains(oracleClass.getSimpleName())) {
                    Oracle oracle = (Oracle) oracleClass.getDeclaredConstructor().newInstance();
                    oracles.add(oracle);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading compiled oracles: " + e.getMessage());
        } catch (ReflectiveOperationException e) {
            System.out.println("Error instantiating compiled oracle: " + e.getMessage());
        }

        return oracles;
    }

    public static List<File> findJavaFiles(File dir) {
        List<File> javaFiles = new ArrayList<>();
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    javaFiles.addAll(findJavaFiles(file));
                } else if (file.getName().endsWith(".java")) {
                    javaFiles.add(file);
                }
            }
        }

        return javaFiles;
    }

    private static boolean needsCompilation(File javaFile, File outputDir) {
        try {
            List<SourceClassInfo> sourceClassInfos = sourceClassInfos(List.of(javaFile), outputDir.toPath());
            if (sourceClassInfos.isEmpty()) {
                return true;
            }

            for (SourceClassInfo sourceClassInfo : sourceClassInfos) {
                if (!Files.exists(sourceClassInfo.outerClassPath)) {
                    return true;
                }

                Path classDirectory = sourceClassInfo.outerClassPath.getParent();
                if (classDirectory == null || !Files.exists(classDirectory)) {
                    return true;
                }

                List<Path> classFiles = Files.walk(classDirectory)
                        .filter(sourceClassInfo::matchesClassFile)
                        .collect(Collectors.toList());

                if (classFiles.isEmpty()) {
                    return true;
                }

                for (Path classFile : classFiles) {
                    if (Files.getLastModifiedTime(classFile).toMillis() < sourceClassInfo.lastModified) {
                        return true;
                    }
                }
            }

            return false;
        } catch (IOException e) {
            return true;
        }
    }

    private static List<SourceClassInfo> sourceClassInfos(List<File> javaFiles, Path outputDir) {
        List<SourceClassInfo> sourceClassInfos = new ArrayList<>();
        for (File javaFile : javaFiles) {
            sourceClassInfos.addAll(sourceClassInfos(javaFile, outputDir));
        }

        return sourceClassInfos;
    }

    private static List<SourceClassInfo> sourceClassInfos(File javaFile, Path outputDir) {
        String source;
        try {
            source = Files.readString(javaFile.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return List.of();
        }

        List<String> topLevelClassNames = topLevelClassNames(source);
        if (topLevelClassNames.isEmpty()) {
            topLevelClassNames = List.of(javaFile.getName().replace(".java", ""));
        }

        Path packagePath = packagePath(source);
        List<SourceClassInfo> sourceClassInfos = new ArrayList<>();
        for (String topLevelClassName : topLevelClassNames) {
            Path classPath = outputDir.resolve(packagePath).resolve(topLevelClassName + ".class");
            sourceClassInfos.add(new SourceClassInfo(javaFile, classPath, topLevelClassName, javaFile.lastModified()));
        }

        return sourceClassInfos;
    }

    private static Path packagePath(String source) {
        Matcher matcher = PACKAGE_PATTERN.matcher(source);
        if (!matcher.find()) {
            return Paths.get("");
        }

        return Paths.get(matcher.group(1).replace('.', File.separatorChar));
    }

    private static List<String> topLevelClassNames(String source) {
        List<String> classNames = new ArrayList<>();
        Matcher matcher = TOP_LEVEL_CLASS_PATTERN.matcher(source);
        while (matcher.find()) {
            classNames.add(matcher.group(1));
        }

        return classNames;
    }

    private static Optional<SourceClassInfo> matchingSourceClassInfo(List<SourceClassInfo> sourceClassInfos, Path classPath) {
        for (SourceClassInfo sourceClassInfo : sourceClassInfos) {
            if (sourceClassInfo.matchesClassFile(classPath)) {
                return Optional.of(sourceClassInfo);
            }
        }

        return Optional.empty();
    }

    private static final class SourceClassInfo {

        private final File sourceFile;
        private final Path outerClassPath;
        private final String topLevelClassName;
        private final long lastModified;

        private SourceClassInfo(File sourceFile, Path outerClassPath, String topLevelClassName, long lastModified) {
            this.sourceFile = sourceFile;
            this.outerClassPath = outerClassPath;
            this.topLevelClassName = topLevelClassName;
            this.lastModified = lastModified;
        }

        private boolean matchesClassFile(Path classPath) {
            Path classParent = classPath.getParent();
            Path expectedParent = outerClassPath.getParent();
            if (classParent == null || expectedParent == null || !classParent.equals(expectedParent)) {
                return false;
            }

            String classFileName = classPath.getFileName().toString();
            return classFileName.equals(topLevelClassName + ".class")
                    || classFileName.startsWith(topLevelClassName + "$");
        }
    }

    private static String getClassName(Path base, Path classFile) {
        String relative = base.relativize(classFile).toString();
        return relative.replace(File.separator, ".").replaceAll("\\.class$", "");
    }

    private static List<String> parseSelectedOracleNames(String selectedOracles) {
        if (selectedOracles == null || selectedOracles.isBlank()) {
            return List.of();
        }

        return Arrays.stream(selectedOracles.split(","))
                .map(String::trim)
                .filter(name -> !name.isBlank())
                .collect(Collectors.toList());
    }
}
