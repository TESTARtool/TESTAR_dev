package org.testar.oracle.workspace;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.rules.TemporaryFolder;
import org.testar.config.TestarDirectories;
import org.testar.oracle.Oracle;
import org.testar.oracle.OracleSelection;

public abstract class WorkspaceOracleTestSupport {

    protected Oracle loadWorkspaceOracle(String oracleClassName) {
        Class<? extends Oracle> oracleClass = SharedWorkspaceOracles.CLASSES.get(oracleClassName);
        if (oracleClass == null) {
            throw new IllegalArgumentException("Shipped workspace oracle was not loaded: " + oracleClassName);
        }

        try {
            return oracleClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to instantiate shipped workspace oracle: " + oracleClassName,
                    exception);
        }
    }

    private static Map<String, Class<? extends Oracle>> loadShippedOracles() {
        TemporaryFolder temporaryFolder = new TemporaryFolder();
        try {
            temporaryFolder.create();
            Runtime.getRuntime().addShutdownHook(new Thread(temporaryFolder::delete));

            File workspacesRoot = temporaryFolder.newFolder("workspaces");
            Path javaDirectory = workspacesRoot.toPath().resolve("webdriver_test/oracles/java");
            Files.createDirectories(javaDirectory);

            Path sourceDirectory = shippedWorkspaceOracleDirectory();
            try (Stream<Path> sources = Files.walk(sourceDirectory)) {
                for (Path source : sources.filter(path -> path.toString().endsWith(".java")).toList()) {
                    Path destination = javaDirectory.resolve(sourceDirectory.relativize(source));
                    Files.createDirectories(destination.getParent());
                    Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                }
            }

            String previousWorkspacesDir = TestarDirectories.getWorkspacesDir();
            String previousSelectedWorkspace = TestarDirectories.getSelectedWorkspaceName();
            try {
                TestarDirectories.setWorkspacesDir(workspacesRoot.getAbsolutePath() + File.separator);
                TestarDirectories.setSelectedWorkspaceName("webdriver_test");

                List<Oracle> oracles = OracleSelection.loadWorkspaceJavaOracles("");
                Map<String, Class<? extends Oracle>> classes = new LinkedHashMap<>();
                for (Oracle oracle : oracles) {
                    classes.put(oracle.getClass().getSimpleName(), oracle.getClass());
                }
                if (classes.isEmpty()) {
                    throw new IllegalStateException("No shipped workspace oracles were loaded");
                }
                return Map.copyOf(classes);
            } finally {
                TestarDirectories.setWorkspacesDir(previousWorkspacesDir);
                TestarDirectories.setSelectedWorkspaceName(previousSelectedWorkspace);
            }
        } catch (Exception exception) {
            temporaryFolder.delete();
            throw new IllegalStateException("Unable to load shipped workspace oracles", exception);
        }
    }

    private static Path shippedWorkspaceOracleDirectory() {
        Path relativePath = Path.of(
                "testar",
                "resources",
                "workspaces",
                "webdriver_generic",
                "oracles",
                "java"
        );

        Path currentDirectory = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
        while (currentDirectory != null) {
            Path candidate = currentDirectory.resolve(relativePath);
            if (Files.isDirectory(candidate)) {
                return candidate;
            }

            currentDirectory = currentDirectory.getParent();
        }

        throw new IllegalStateException("Unable to find shipped workspace Java oracles");
    }

    private static class SharedWorkspaceOracles {
        private static final Map<String, Class<? extends Oracle>> CLASSES = loadShippedOracles();
    }
}
