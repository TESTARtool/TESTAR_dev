/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.util;

import org.testar.config.TestarDirectories;
import org.testar.config.settings.SettingsResourceResolver;
import org.testar.core.Assert;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ExternalJavaClassSupport {

    private static final Map<String, LoadedSourceRoot> LOADED_SOURCE_ROOTS = new HashMap<String, LoadedSourceRoot>();

    private ExternalJavaClassSupport() {
    }

    public static Class<?> loadClass(String className, Optional<String> resourcePath) throws ClassNotFoundException {
        Assert.notNull(className);
        Assert.notNull(resourcePath);

        if (resourcePath.isPresent()) {
            Path sourceRoot = resolveSourceRoot(resourcePath.get());
            if (sourceRoot != null) {
                Path expectedSource = sourceRoot.resolve(className.replace('.', File.separatorChar) + ".java");
                if (Files.exists(expectedSource)) {
                    URLClassLoader classLoader = ensureCompiledClassLoader(sourceRoot);
                    return Class.forName(className, true, classLoader);
                }
            }
        }

        try {
            return Class.forName(className);
        } catch (ClassNotFoundException exception) {
            if (resourcePath.isEmpty()) {
                throw exception;
            }

            Path sourceRoot = resolveSourceRoot(resourcePath.get());
            if (sourceRoot == null) {
                throw exception;
            }

            URLClassLoader classLoader = ensureCompiledClassLoader(sourceRoot);
            return Class.forName(className, true, classLoader);
        }
    }

    private static synchronized URLClassLoader ensureCompiledClassLoader(Path sourceRoot) {
        long sourceStamp = computeSourceStamp(sourceRoot);
        String sourceRootKey = sourceRoot.toAbsolutePath().normalize().toString();

        LoadedSourceRoot loadedSourceRoot = LOADED_SOURCE_ROOTS.get(sourceRootKey);
        if (loadedSourceRoot != null && loadedSourceRoot.sourceStamp == sourceStamp) {
            return loadedSourceRoot.classLoader;
        }

        Path outputDirectory = runtimeOutputDirectory(sourceRoot);
        compileSourceRoot(sourceRoot, outputDirectory);

        try {
            URLClassLoader classLoader = new URLClassLoader(
                    new URL[]{outputDirectory.toUri().toURL()},
                    ExternalJavaClassSupport.class.getClassLoader()
            );
            LOADED_SOURCE_ROOTS.put(sourceRootKey, new LoadedSourceRoot(sourceStamp, classLoader));
            return classLoader;
        } catch (MalformedURLException exception) {
            throw new IllegalStateException("Unable to create class loader for source root: " + sourceRoot, exception);
        }
    }

    private static void compileSourceRoot(Path sourceRoot, Path outputDirectory) {
        List<File> javaFiles = listJavaFiles(sourceRoot);
        if (javaFiles.isEmpty()) {
            throw new IllegalStateException("No Java source files found under: " + sourceRoot);
        }

        try {
            Files.createDirectories(outputDirectory);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to create runtime class output directory: " + outputDirectory, exception);
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("Java compiler is not available. A JDK is required to load external Java classes.");
        }

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null)) {
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(javaFiles);
            List<String> options = new ArrayList<String>();
            options.add("-classpath");
            options.add(System.getProperty("java.class.path"));
            options.add("-d");
            options.add(outputDirectory.toAbsolutePath().toString());

            JavaCompiler.CompilationTask task = compiler.getTask(
                    null,
                    fileManager,
                    diagnostics,
                    options,
                    null,
                    compilationUnits
            );

            Boolean success = task.call();
            if (!Boolean.TRUE.equals(success)) {
                throw new IllegalStateException(buildCompilationFailureMessage(sourceRoot, diagnostics));
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to compile external Java classes from: " + sourceRoot, exception);
        }
    }

    private static String buildCompilationFailureMessage(Path sourceRoot,
                                                         DiagnosticCollector<JavaFileObject> diagnostics) {
        StringBuilder builder = new StringBuilder();
        builder.append("Failed to compile external Java classes from: ")
                .append(sourceRoot);

        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            builder.append(System.lineSeparator())
                    .append("- line ")
                    .append(diagnostic.getLineNumber())
                    .append(": ")
                    .append(diagnostic.getMessage(null));
        }

        return builder.toString();
    }

    private static List<File> listJavaFiles(Path sourceRoot) {
        try (Stream<Path> pathStream = Files.walk(sourceRoot)) {
            return pathStream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".java"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to list Java source files under: " + sourceRoot, exception);
        }
    }

    private static long computeSourceStamp(Path sourceRoot) {
        try (Stream<Path> pathStream = Files.walk(sourceRoot)) {
            return pathStream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".java"))
                    .mapToLong(path -> path.toFile().lastModified())
                    .max()
                    .orElse(0L);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to inspect Java source files under: " + sourceRoot, exception);
        }
    }

    private static Path runtimeOutputDirectory(Path sourceRoot) {
        String sourceRootKey = sourceRoot.toAbsolutePath().normalize().toString();
        String folderName = buildRuntimeFolderName(sourceRoot, sourceRootKey);
        return Paths.get(TestarDirectories.getTestarDir())
                .resolve(".runtime")
                .resolve("user-classes")
                .resolve(folderName);
    }

    private static String buildRuntimeFolderName(Path sourceRoot, String sourceRootKey) {
        Path folderNamePath = sourceRoot.getFileName();
        String folderName = folderNamePath == null ? "custom_generic" : folderNamePath.toString().trim();
        String sanitizedFolderName = sanitizeFolderName(folderName);

        if (sanitizedFolderName.isEmpty()) {
            sanitizedFolderName = "custom_generic";
        }

        if (LOADED_SOURCE_ROOTS.containsKey(sourceRootKey)) {
            return sanitizedFolderName;
        }

        Path userClassesDirectory = Paths.get(TestarDirectories.getTestarDir())
                .resolve(".runtime")
                .resolve("user-classes");
        Path candidateDirectory = userClassesDirectory.resolve(sanitizedFolderName);

        if (!Files.exists(candidateDirectory)) {
            return sanitizedFolderName;
        }

        return sanitizedFolderName + "_" + Integer.toHexString(sourceRootKey.hashCode());
    }

    private static String sanitizeFolderName(String folderName) {
        String sanitized = folderName.replaceAll("[^A-Za-z0-9._-]+", "_");
        sanitized = sanitized.replaceAll("_+", "_");
        sanitized = sanitized.replaceAll("^_+", "");
        sanitized = sanitized.replaceAll("_+$", "");
        return sanitized;
    }

    private static Path resolveSourceRoot(String resourcePath) {
        File resourceFile = SettingsResourceResolver.resolve(resourcePath);
        File parentDirectory = resourceFile.getParentFile();
        if (parentDirectory == null || !parentDirectory.exists()) {
            return null;
        }

        return parentDirectory.toPath().toAbsolutePath().normalize();
    }

    private static final class LoadedSourceRoot {

        private final long sourceStamp;
        private final URLClassLoader classLoader;

        private LoadedSourceRoot(long sourceStamp, URLClassLoader classLoader) {
            this.sourceStamp = sourceStamp;
            this.classLoader = classLoader;
        }
    }
}
