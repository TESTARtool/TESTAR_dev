package org.testar.webstudio.workspace;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class WorkspaceServiceRuntimeHomeTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void returnsTestarRuntimeHomeForTestarWorkspace() throws IOException {
        TestWorkspaceRoots roots = createWorkspaceRoots();
        Files.createDirectories(roots.testarSettingsRoot.resolve("webdriver_generic"));

        WorkspaceService workspaceService = new WorkspaceService(
            roots.testarSettingsRoot,
            roots.cliSettingsRoot
        );

        Assert.assertEquals(
            roots.testarHome,
            workspaceService.workspaceRuntimeHomeDirectory("webdriver_generic")
        );
    }

    @Test
    public void returnsCliRuntimeHomeForCliWorkspace() throws IOException {
        TestWorkspaceRoots roots = createWorkspaceRoots();
        Files.createDirectories(roots.cliSettingsRoot.resolve("cli_generic"));

        WorkspaceService workspaceService = new WorkspaceService(
            roots.testarSettingsRoot,
            roots.cliSettingsRoot
        );

        Assert.assertEquals(
            roots.cliHome,
            workspaceService.workspaceRuntimeHomeDirectory("cli_generic")
        );
    }

    @Test
    public void returnsExplicitTestarRuntimeHomeWhenWorkspaceExistsInBothRuntimes() throws IOException {
        TestWorkspaceRoots roots = createWorkspaceRoots();
        Files.createDirectories(roots.testarSettingsRoot.resolve("shared_generic"));
        Files.createDirectories(roots.cliSettingsRoot.resolve("shared_generic"));

        WorkspaceService workspaceService = new WorkspaceService(
            roots.testarSettingsRoot,
            roots.cliSettingsRoot
        );

        Assert.assertEquals(
            roots.testarHome,
            workspaceService.workspaceRuntimeHomeDirectory("shared_generic", "testar")
        );
    }

    @Test
    public void returnsExplicitCliRuntimeHomeWhenWorkspaceExistsInBothRuntimes() throws IOException {
        TestWorkspaceRoots roots = createWorkspaceRoots();
        Files.createDirectories(roots.testarSettingsRoot.resolve("shared_generic"));
        Files.createDirectories(roots.cliSettingsRoot.resolve("shared_generic"));

        WorkspaceService workspaceService = new WorkspaceService(
            roots.testarSettingsRoot,
            roots.cliSettingsRoot
        );

        Assert.assertEquals(
            roots.cliHome,
            workspaceService.workspaceRuntimeHomeDirectory("shared_generic", "cli")
        );
    }

    private TestWorkspaceRoots createWorkspaceRoots() throws IOException {
        Path root = temporaryFolder.newFolder("webstudio-runtime-roots").toPath();
        Path testarHome = root.resolve("testar").resolve("target").resolve("install").resolve("testar").resolve("bin");
        Path cliHome = root.resolve("cli").resolve("target").resolve("install").resolve("testar-cli");
        Path testarSettingsRoot = testarHome.resolve("settings");
        Path cliSettingsRoot = cliHome.resolve("settings");
        Files.createDirectories(testarSettingsRoot);
        Files.createDirectories(cliSettingsRoot);

        return new TestWorkspaceRoots(
            testarHome.toAbsolutePath().normalize(),
            testarSettingsRoot.toAbsolutePath().normalize(),
            cliHome.toAbsolutePath().normalize(),
            cliSettingsRoot.toAbsolutePath().normalize()
        );
    }

    private static final class TestWorkspaceRoots {

        private final Path testarHome;
        private final Path testarSettingsRoot;
        private final Path cliHome;
        private final Path cliSettingsRoot;

        private TestWorkspaceRoots(Path testarHome,
                                   Path testarSettingsRoot,
                                   Path cliHome,
                                   Path cliSettingsRoot) {
            this.testarHome = testarHome;
            this.testarSettingsRoot = testarSettingsRoot;
            this.cliHome = cliHome;
            this.cliSettingsRoot = cliSettingsRoot;
        }
    }
}
