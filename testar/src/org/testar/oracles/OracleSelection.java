/***************************************************************************************************
 *
 * Copyright (c) 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2025 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.oracles;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.reflections8.Reflections;
import org.testar.monkey.Main;

public class OracleSelection {

	/** Extended Internal Oracles **/

	private static String[] oraclePackages = {
			"org.testar.oracles.generic.visual",
			"org.testar.oracles.web.accessibility",
			"org.testar.oracles.web.invariants",
	};

	public static List<String> getAvailableExtendedOracles() {
		List<String> oracleNames = new ArrayList<>();
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
		return oracleNames;
	}

	public static List<Oracle> loadExtendedOracles(String selectedOracles) {
		List<Oracle> oraclesList = new ArrayList<>();

		// Split the input string by commas to get individual oracle names
		String[] oracleNames = selectedOracles.split(",");

		for (String oracleName : oracleNames) {
			oracleName = oracleName.trim();

			for (String pkg : oraclePackages) {
				try {
					// Construct the full class name dynamically
					String fullClassName = pkg + "." + oracleName;

					// Use reflection to dynamically load the class
					Class<?> oracleClass = Class.forName(fullClassName);

					// Check if the class implements the Oracle interface
					if (Oracle.class.isAssignableFrom(oracleClass)) {
						// Instantiate the oracle (assuming a default constructor)
						Constructor<?> constructor = oracleClass.getDeclaredConstructor();
						Oracle oracleInstance = (Oracle) constructor.newInstance();
						oraclesList.add(oracleInstance);
						break;
					}
				} catch (ClassNotFoundException e) {
					// Continue trying other packages
				} catch (Exception e) {
					System.out.println("Error instantiating " + oracleName + ": " + e.getMessage());
				}
			}
		}

		return oraclesList;
	}

	/** External Oracles **/	

	public static Map<String, List<String>> getAvailableExternalOracles() {
		Map<String, List<String>> fileToOraclesMap = new LinkedHashMap<>();

		File javaDir = new File(Main.oraclesDir);
		File compiledDir = new File(Main.oraclesDir, "compiled");

		// Step 1: Find all .java files
		List<File> javaFiles = findJavaFiles(javaDir);

		if (javaFiles == null || javaFiles.size() == 0) {
			System.err.println("No .java files found in: " + javaDir.getAbsolutePath());
			return Map.of();
		}

		// Create or clean compiled directory
		if (!compiledDir.exists()) {
			compiledDir.mkdirs();
		} else {
			cleanObsoleteClassFiles(javaDir, compiledDir);
		}

		// Step 2: Compile .java files individually
		Set<String> successfullyCompiled = compileJavaFiles(javaFiles, compiledDir);
		if (successfullyCompiled.isEmpty()) {
			System.err.println("No oracles compiled successfully.");
			return Map.of();
		}

		// Step 3: Load all successfully compiled classes that implement Oracle
		try (URLClassLoader classLoader = new URLClassLoader(new URL[]{compiledDir.toURI().toURL()})) {
			for (File javaFile : javaFiles) {
				String fileName = javaFile.getName();
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
					fileToOraclesMap.put(fileName, oraclesInFile);
				}
			}
		} catch (IOException e) {
			System.out.println("Error scanning compiled oracles: " + e.getMessage());
		}

		return fileToOraclesMap;
	}

	public static List<Oracle> loadExternalJavaOracles(String selectedOracles) {
		File javaDir = new File(Main.oraclesDir);
		if (!javaDir.exists() || !javaDir.isDirectory()) {
			System.out.println("Oracle directory not found: " + javaDir.getAbsolutePath());
			return List.of();
		}

		Set<String> selectedNames = selectedOracles == null || selectedOracles.isBlank()
				? Set.of()  // load all oracles
						: Arrays.stream(selectedOracles.split(","))
						.map(String::trim)
						.collect(Collectors.toSet());

				List<File> javaFiles = findJavaFiles(javaDir);
				if (javaFiles == null || javaFiles.size() == 0) {
					System.out.println("No Java files found in the oracles directory.");
					return List.of();
				}

				File outputDir = new File(javaDir, "compiled");
				if (!outputDir.exists()) {
					outputDir.mkdirs();
				} else {
					// Clean previous compiled .class files
					cleanObsoleteClassFiles(javaDir, outputDir);
				}

				Set<String> compiledNames = compileJavaFiles(javaFiles, outputDir);
				if (!compiledNames.isEmpty()) {
					return loadCompiledOracles(outputDir, selectedNames.isEmpty() ? compiledNames : selectedNames);
				}
				return List.of();
	}

	private static void cleanObsoleteClassFiles(File javaSourceRoot, File outputDir) {
		try {
			Files.walk(outputDir.toPath())
			.filter(p -> p.toString().endsWith(".class"))
			.forEach(classPath -> {
				try {
					// Determine package-relative class file path
					Path relativeClassPath = outputDir.toPath().relativize(classPath);
					String relativePathWithoutExtension = relativeClassPath.toString().replaceAll("\\.class$", "");

					// Only map the base name (before $ for inner classes)
					String baseClassName = Paths.get(relativePathWithoutExtension).getFileName().toString().split("\\$")[0];

					// Replace .class with .java and use path to find corresponding source
					Path relativeJavaPath = relativeClassPath.getParent() != null
							? relativeClassPath.getParent().resolve(baseClassName + ".java")
									: Paths.get(baseClassName + ".java");

							File sourceFile = new File(javaSourceRoot, relativeJavaPath.toString());

							boolean isOrphan = !sourceFile.exists();
							boolean isStale = sourceFile.exists()
									&& classPath.toFile().lastModified() < sourceFile.lastModified();

							if (isOrphan || isStale) {
								System.out.println("Deleting stale or orphan .class: " + classPath);
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
					"-sourcepath", Main.oraclesDir
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

		try (URLClassLoader classLoader = new URLClassLoader(new URL[]{outputDir.toURI().toURL()})) {
			Files.walk(outputDir.toPath())
			.filter(p -> p.toString().endsWith(".class"))
			.forEach(classPath -> {
				try {
					String className = getClassName(outputDir.toPath(), classPath);
					Class<?> clazz = classLoader.loadClass(className);

					if (Oracle.class.isAssignableFrom(clazz) && !java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
						if (selectedNames.isEmpty() || selectedNames.contains(clazz.getSimpleName())) {
							Oracle oracle = (Oracle) clazz.getDeclaredConstructor().newInstance();
							oracles.add(oracle);
						}
					}
				} catch (Exception e) {
					System.out.println("Failed to load compiled oracle: " + e.getMessage());
				}
			});
		} catch (IOException e) {
			System.out.println("Error loading compiled oracles: " + e.getMessage());
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
		String baseName = javaFile.getName().replace(".java", "");
		Path outputPath = outputDir.toPath();

		try {
			// Check if *any* .class file that came from this source is outdated or missing
			List<Path> classFiles = Files.walk(outputPath)
					.filter(p -> {
						String name = p.getFileName().toString();
						return name.equals(baseName + ".class") || name.startsWith(baseName + "$");
					})
					.collect(Collectors.toList());

			if (classFiles.isEmpty()) {
				return true; // No .class files found for this .java
			}

			long javaLastModified = javaFile.lastModified();

			for (Path classFile : classFiles) {
				if (Files.getLastModifiedTime(classFile).toMillis() < javaLastModified) {
					return true; // At least one .class is stale
				}
			}

			return false; // All .class files are up to date
		} catch (IOException e) {
			return true; // On error, assume recompilation needed
		}
	}

	private static String getClassName(Path base, Path classFile) {
		String relative = base.relativize(classFile).toString();
		return relative.replace(File.separator, ".").replaceAll("\\.class$", "");
	}
}
