package org.testar.webstudio.execution;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Path;

public class ResultWorkspacePathsTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void resolvesOutputDirectoryUnderWorkspaceName() throws IOException {
        Path runtimeHome = temporaryFolder.newFolder("bin").toPath();

        Path outputDirectory = ResultWorkspacePaths.workspaceOutputDirectory(runtimeHome, "webdriver_generic");

        Assert.assertEquals(
            runtimeHome.resolve("output").resolve("webdriver_generic").toAbsolutePath().normalize(),
            outputDirectory
        );
    }

    @Test
    public void createsWorkspaceOutputSettingValue() {
        Assert.assertEquals(
            "./output/webdriver_generic",
            ResultWorkspacePaths.workspaceOutputSettingValue("webdriver_generic")
        );
    }

    @Test
    public void rejectsUnsafeWorkspaceNames() throws IOException {
        Path runtimeHome = temporaryFolder.newFolder("bin").toPath();

        try {
            ResultWorkspacePaths.workspaceOutputDirectory(runtimeHome, "../android_generic");
            Assert.fail("Expected unsafe workspace name to be rejected");
        } catch (IllegalArgumentException exception) {
            Assert.assertTrue(exception.getMessage().contains("Invalid workspace name"));
        }
    }
}
