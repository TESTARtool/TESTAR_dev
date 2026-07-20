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

    private String previousSettingsDir;
    private String previousSelectedSse;

    @Before
    public void setupWorkspaceOracleTest() throws Exception {
        previousSettingsDir = TestarDirectories.getSettingsDir();
        previousSelectedSse = TestarDirectories.getSelectedSse();

        File settingsRoot = temporaryFolder.newFolder("settings");
        TestarDirectories.setSettingsDir(settingsRoot.getAbsolutePath() + File.separator);
        TestarDirectories.setSelectedSse("webdriver_test");

        Files.createDirectories(Path.of(TestarDirectories.getWorkspaceOracleJavaDir()));
    }

    @After
    public void teardownWorkspaceOracleTest() {
        TestarDirectories.setSettingsDir(previousSettingsDir);
        TestarDirectories.setSelectedSse(previousSelectedSse);
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
                "settings",
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
