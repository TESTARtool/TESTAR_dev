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
    public void returnsSharedRuntimeHomeForSelectedWorkspace() throws IOException {
        TestWorkspaceRoots roots = createWorkspaceRoots();
        Files.createDirectories(roots.testarWorkspacesRoot.resolve("webdriver_generic"));

        WorkspaceService workspaceService = new WorkspaceService(
            roots.testarWorkspacesRoot,
            roots.cliWorkspacesRoot
        );

        Assert.assertEquals(
            roots.testarHome,
            workspaceService.workspaceRuntimeHomeDirectory("webdriver_generic")
        );
    }

    @Test
    public void resolvesConfiguredWorkspacesRoot() throws IOException {
        TestWorkspaceRoots roots = createWorkspaceRoots();
        String previousRoot = System.getProperty("testar.webstudio.workspacesRoot");
        System.setProperty("testar.webstudio.workspacesRoot", roots.testarWorkspacesRoot.toString());

        try {
            WorkspaceService workspaceService = new WorkspaceService();

            Assert.assertEquals(roots.testarWorkspacesRoot, workspaceService.workspacesRoot());
        } finally {
            if (previousRoot == null) {
                System.clearProperty("testar.webstudio.workspacesRoot");
            } else {
                System.setProperty("testar.webstudio.workspacesRoot", previousRoot);
            }
        }
    }

    @Test
    public void keepsCompatibilityWithCliOnlyWorkspaceRoots() throws IOException {
        TestWorkspaceRoots roots = createWorkspaceRoots();
        Files.createDirectories(roots.cliWorkspacesRoot.resolve("cli_generic"));

        WorkspaceService workspaceService = new WorkspaceService(
            roots.testarWorkspacesRoot,
            roots.cliWorkspacesRoot
        );

        Assert.assertEquals(
            roots.cliHome,
            workspaceService.workspaceRuntimeHomeDirectory("cli_generic")
        );
    }

    @Test
    public void sharedWorkspaceNamesResolveToSharedTestarRuntimeHome() throws IOException {
        TestWorkspaceRoots roots = createWorkspaceRoots();
        Files.createDirectories(roots.testarWorkspacesRoot.resolve("shared_generic"));
        Files.createDirectories(roots.cliWorkspacesRoot.resolve("shared_generic"));

        WorkspaceService workspaceService = new WorkspaceService(
            roots.testarWorkspacesRoot,
            roots.cliWorkspacesRoot
        );

        Assert.assertEquals(
            roots.testarHome,
            workspaceService.workspaceRuntimeHomeDirectory("shared_generic")
        );
    }

    @Test
    public void returnsSharedRuntimeHomeWhenTestarAndCliUseSameWorkspacesRoot() throws IOException {
        TestWorkspaceRoots roots = createWorkspaceRoots();
        Files.createDirectories(roots.testarWorkspacesRoot.resolve("webdriver_generic"));

        WorkspaceService workspaceService = new WorkspaceService(
            roots.testarWorkspacesRoot,
            roots.testarWorkspacesRoot
        );

        Assert.assertEquals(
            roots.testarHome,
            workspaceService.workspaceRuntimeHomeDirectory("webdriver_generic")
        );
    }

    private TestWorkspaceRoots createWorkspaceRoots() throws IOException {
        Path root = temporaryFolder.newFolder("webstudio-runtime-roots").toPath();
        Path testarHome = root.resolve("testar").resolve("target").resolve("install").resolve("testar").resolve("bin");
        Path cliHome = root.resolve("cli").resolve("target").resolve("install").resolve("testar-cli");
        Path testarWorkspacesRoot = testarHome.resolve("workspaces");
        Path cliWorkspacesRoot = cliHome.resolve("workspaces");
        Files.createDirectories(testarWorkspacesRoot);
        Files.createDirectories(cliWorkspacesRoot);

        return new TestWorkspaceRoots(
            testarHome.toAbsolutePath().normalize(),
            testarWorkspacesRoot.toAbsolutePath().normalize(),
            cliHome.toAbsolutePath().normalize(),
            cliWorkspacesRoot.toAbsolutePath().normalize()
        );
    }

    private static final class TestWorkspaceRoots {

        private final Path testarHome;
        private final Path testarWorkspacesRoot;
        private final Path cliHome;
        private final Path cliWorkspacesRoot;

        private TestWorkspaceRoots(Path testarHome,
                                   Path testarWorkspacesRoot,
                                   Path cliHome,
                                   Path cliWorkspacesRoot) {
            this.testarHome = testarHome;
            this.testarWorkspacesRoot = testarWorkspacesRoot;
            this.cliHome = cliHome;
            this.cliWorkspacesRoot = cliWorkspacesRoot;
        }
    }
}
