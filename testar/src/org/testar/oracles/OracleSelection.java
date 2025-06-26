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
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

	public static List<String> getExternalOracleFileNames() {
		String oraclesPath = Main.oraclesDir;
		List<String> oracleNames = new ArrayList<>();
		File dir = new File(oraclesPath);

		if (!dir.exists() || !dir.isDirectory()) {
			System.err.println("External oracle directory not found: " + oraclesPath);
			return oracleNames;
		}

		File[] javaFiles = dir.listFiles((d, name) -> name.endsWith(".java"));
		if (javaFiles == null) return oracleNames;

		for (File javaFile : javaFiles) {
			String fileName = javaFile.getName();
			if (fileName.endsWith(".java")) {
				String className = fileName.substring(0, fileName.length() - 5);
				oracleNames.add(className);
			}
		}

		return oracleNames;
	}

	public static List<Oracle> loadExternalJavaOracles(String selectedOracles) {
		File javaDir = new File(Main.oraclesDir);
		if (!javaDir.exists() || !javaDir.isDirectory()) {
			System.out.println("Oracle directory not found: " + javaDir.getAbsolutePath());
			return List.of();
		}

		// Split and clean the selected oracle names
		Set<String> selectedNames = Arrays.stream(selectedOracles.split(","))
				.map(String::trim)
				.collect(Collectors.toSet());

		// Filter .java files based on selected class names
		File[] javaFiles = javaDir.listFiles((dir, name) -> {
			if (!name.endsWith(".java")) return false;
			String className = name.substring(0, name.length() - 5); // strip ".java"
			return selectedNames.contains(className);
		});

		if (javaFiles == null || javaFiles.length == 0) {
			System.out.println("No matching Java files found for selected oracles.");
			return List.of();
		}

		File outputDir = new File(javaDir, "compiled");
		outputDir.mkdirs();

		if (compileJavaFiles(javaFiles, outputDir)) {
			return loadCompiledOracles(outputDir, selectedNames);
		}

		return List.of();
	}


	private static boolean compileJavaFiles(File[] javaFiles, File outputDir) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null) {
			System.out.println("No Java compiler available. Are you running a JDK?");
			return false;
		}

		try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
			Iterable<? extends JavaFileObject> compilationUnits =
					fileManager.getJavaFileObjects(javaFiles);
			List<String> options = List.of("-d", outputDir.getAbsolutePath());

			JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, options, null, compilationUnits);
			return task.call();
		} catch (IOException e) {
			e.printStackTrace();
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
					String simpleName = className.substring(className.lastIndexOf('.') + 1);
					String baseName = simpleName.split("\\$")[0];
					if (!selectedNames.contains(baseName)) return;

					Class<?> clazz = classLoader.loadClass(className);
					if (Oracle.class.isAssignableFrom(clazz)) {
						Oracle oracle = (Oracle) clazz.getDeclaredConstructor().newInstance();
						oracles.add(oracle);
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

	private static String getClassName(Path base, Path classFile) {
		String relative = base.relativize(classFile).toString();
		return relative.replace(File.separator, ".").replaceAll("\\.class$", "");
	}
}
