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
import java.util.HashSet;
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

	public static List<String> getAvailableBuiltInOracles() {
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
		return new ArrayList<>(oracleNames);
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

		Set<String> successfullyCompiled = compileJavaFiles(javaFiles, compiledDir);
		if (successfullyCompiled.isEmpty()) {
			System.err.println("No oracles compiled successfully.");
			return Map.of();
		}

		try (URLClassLoader classLoader = new URLClassLoader(
				new URL[]{compiledDir.toURI().toURL()},
				OracleSelection.class.getClassLoader()
		)) {
			for (File javaFile : javaFiles) {
				String fileName = javaFile.getName();
				String sourcePath = javaDir.toPath()
						.relativize(javaFile.toPath())
						.toString()
						.replace(File.separatorChar, '/');
				List<String> oraclesInFile = new ArrayList<>();

				Files.walk(compiledDir.toPath())
				.filter(p -> p.toString().endsWith(".class"))
				.forEach(classPath -> {
					try {
						String className = getClassName(compiledDir.toPath(), classPath);
						Class<?> clazz = classLoader.loadClass(className);

						if (clazz.getProtectionDomain().getCodeSource().getLocation().getFile().endsWith("compiled/") &&
								Oracle.class.isAssignableFrom(clazz) &&
								!Modifier.isAbstract(clazz.getModifiers())) {

							if (javaFile.getName().replace(".java", "").equalsIgnoreCase(clazz.getSimpleName()) ||
									classPath.toString().contains(fileName.replace(".java", ""))) {

								oraclesInFile.add(clazz.getSimpleName());
							}
						}
					} catch (Exception e) {
						System.out.println("Skipping class: " + e.getMessage());
					}
				});

				if (!oraclesInFile.isEmpty()) {
					fileToOraclesMap.put(sourcePath, oraclesInFile);
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

		File outputDir = new File(TestarDirectories.getWorkspaceOracleCompiledDir());
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		} else {
			cleanObsoleteClassFiles(javaDir, outputDir);
		}

		Set<String> compiledNames = compileJavaFiles(javaFiles, outputDir);
		if (!compiledNames.isEmpty()) {
			Set<String> namesToLoad = selectedNames.isEmpty() ? compiledNames : selectedNames;
			for (Oracle oracle : loadCompiledOracles(outputDir, namesToLoad)) {
				oracles.put(oracle.getClass().getSimpleName(), oracle);
			}
		}
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

	private static Set<String> compileJavaFiles(List<File> javaFiles, File outputDir) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null) {
			System.out.println("No Java compiler available.");
			return Set.of();
		}

		Set<String> compiledClasses = new HashSet<>();

		for (File javaFile : javaFiles) {
			if (needsCompilation(javaFile, outputDir)) {
				boolean success = compileJavaFile(compiler, javaFile, outputDir);
				if (success) {
					compiledClasses.add(javaFile.getName().replace(".java", ""));
				}
			} else {
				compiledClasses.add(javaFile.getName().replace(".java", ""));
			}
		}

		return compiledClasses;
	}

	private static boolean compileJavaFile(JavaCompiler compiler, File javaFile, File outputDir) {
		System.out.println("Compiling added or modified external oracles... " + javaFile.getName());

		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

		try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null)) {
			Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(javaFile);
			List<String> options = List.of(
					"-d", outputDir.getAbsolutePath(),
					"-sourcepath", TestarDirectories.getWorkspaceOracleJavaDir(),
					"-classpath", System.getProperty("java.class.path")
					);

			JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits);
			boolean success = task.call();

			if (!success) {
				System.out.println("Failed to compile: " + javaFile.getName());
				diagnostics.getDiagnostics().forEach(d -> System.out.println(d.toString()));
			}

			return success;
		} catch (IOException e) {
			System.out.println("Compilation error: " + javaFile.getName() + " - " + e.getMessage());
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
			sourceClassInfos.add(new SourceClassInfo(classPath, topLevelClassName, javaFile.lastModified()));
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

		private final Path outerClassPath;
		private final String topLevelClassName;
		private final long lastModified;

		private SourceClassInfo(Path outerClassPath, String topLevelClassName, long lastModified) {
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
