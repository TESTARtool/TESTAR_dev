package org.testar.oracle.workspace;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.testar.config.TestarDirectories;
import org.testar.oracle.Oracle;
import org.testar.oracle.OracleSelection;

public abstract class WorkspaceOracleTestSupport {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private String previousWorkspacesDir;
    private String previousSelectedSse;

    @Before
    public void setupWorkspaceOracleTest() throws Exception {
        previousWorkspacesDir = TestarDirectories.getWorkspacesDir();
        previousSelectedSse = TestarDirectories.getSelectedWorkspaceName();

        File workspacesRoot = temporaryFolder.newFolder("workspaces");
        TestarDirectories.setWorkspacesDir(workspacesRoot.getAbsolutePath() + File.separator);
        TestarDirectories.setSelectedWorkspaceName("webdriver_test");

        Files.createDirectories(Path.of(TestarDirectories.getWorkspaceOracleJavaDir()));
    }

    @After
    public void teardownWorkspaceOracleTest() {
        TestarDirectories.setWorkspacesDir(previousWorkspacesDir);
        TestarDirectories.setSelectedWorkspaceName(previousSelectedSse);
    }

    protected Oracle loadWorkspaceOracle(String oracleClassName) {
        copyShippedWorkspaceOracle(oracleClassName);

        List<Oracle> oracles = OracleSelection.loadExtendedOracles(oracleClassName);

        assertEquals(1, oracles.size());
        assertEquals(oracleClassName, oracles.get(0).getClass().getSimpleName());
        return oracles.get(0);
    }

    private void copyShippedWorkspaceOracle(String oracleClassName) {
        try {
            Files.copy(
                    shippedWorkspaceOracle(oracleClassName),
                    Path.of(TestarDirectories.getWorkspaceOracleJavaDir()).resolve(oracleClassName + ".java"),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (Exception e) {
            throw new IllegalStateException("Unable to copy shipped workspace oracle: " + oracleClassName, e);
        }
    }

    private Path shippedWorkspaceOracle(String oracleClassName) {
        Path relativePath = Path.of(
                "testar",
                "resources",
                "workspaces",
                "webdriver_generic",
                "oracles",
                "java",
                oracleClassName + ".java"
        );

        Path currentDirectory = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
        while (currentDirectory != null) {
            Path candidate = currentDirectory.resolve(relativePath);
            if (Files.exists(candidate)) {
                return candidate;
            }

            currentDirectory = currentDirectory.getParent();
        }

        throw new IllegalStateException("Unable to find shipped workspace oracle: " + oracleClassName);
    }
}
