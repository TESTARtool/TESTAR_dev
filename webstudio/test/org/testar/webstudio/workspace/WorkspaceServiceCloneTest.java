package org.testar.webstudio.workspace;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class WorkspaceServiceCloneTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void createsWorkspaceByCloningBaseWorkspaceWithTestGoals() throws IOException {
        TestWorkspaceRoots roots = createWorkspaceRoots();
        Path baseWorkspace = createBaseWorkspace(roots.testarSettingsRoot);
        WorkspaceService workspaceService = new WorkspaceService(roots.testarSettingsRoot, roots.cliSettingsRoot);

        workspaceService.createWorkspace("webdriver_parabank", "webdriver_generic", true);

        Path clonedWorkspace = roots.testarSettingsRoot.resolve("webdriver_parabank");
        Assert.assertTrue(Files.isDirectory(clonedWorkspace));
        Assert.assertEquals(
            Files.readString(baseWorkspace.resolve("test.settings")),
            Files.readString(clonedWorkspace.resolve("test.settings"))
        );
        Assert.assertEquals(
            Files.readString(baseWorkspace.resolve("WebdriverGenericSystemService.java")),
            Files.readString(clonedWorkspace.resolve("WebdriverGenericSystemService.java"))
        );
        Assert.assertTrue(Files.isRegularFile(clonedWorkspace.resolve("test_goals").resolve("login.yaml")));
    }

    @Test
    public void createsEmptyTestGoalsDirectoryWhenCopyTestGoalsIsDisabled() throws IOException {
        TestWorkspaceRoots roots = createWorkspaceRoots();
        createBaseWorkspace(roots.testarSettingsRoot);
        WorkspaceService workspaceService = new WorkspaceService(roots.testarSettingsRoot, roots.cliSettingsRoot);

        workspaceService.createWorkspace("webdriver_no_goals", "webdriver_generic", false);

        Path clonedWorkspace = roots.testarSettingsRoot.resolve("webdriver_no_goals");
        Assert.assertTrue(Files.isDirectory(clonedWorkspace.resolve("test_goals")));
        Assert.assertFalse(Files.exists(clonedWorkspace.resolve("test_goals").resolve("login.yaml")));
        Assert.assertTrue(Files.isRegularFile(clonedWorkspace.resolve("test.settings")));
    }

    @Test
    public void rejectsDuplicateWorkspaceNamesAcrossRuntimeRoots() throws IOException {
        TestWorkspaceRoots roots = createWorkspaceRoots();
        createBaseWorkspace(roots.testarSettingsRoot);
        Files.createDirectories(roots.cliSettingsRoot.resolve("existing_cli_workspace"));
        WorkspaceService workspaceService = new WorkspaceService(roots.testarSettingsRoot, roots.cliSettingsRoot);

        IllegalArgumentException exception = expectIllegalArgumentException(
            () -> workspaceService.createWorkspace("existing_cli_workspace", "webdriver_generic", true)
        );

        Assert.assertTrue(exception.getMessage().contains("Workspace already exists"));
    }

    @Test
    public void rejectsUnsafeWorkspaceNames() throws IOException {
        TestWorkspaceRoots roots = createWorkspaceRoots();
        createBaseWorkspace(roots.testarSettingsRoot);
        WorkspaceService workspaceService = new WorkspaceService(roots.testarSettingsRoot, roots.cliSettingsRoot);

        IllegalArgumentException exception = expectIllegalArgumentException(
            () -> workspaceService.createWorkspace("../bad", "webdriver_generic", true)
        );

        Assert.assertTrue(exception.getMessage().contains("letters, numbers, underscores, and hyphens"));
    }

    private Path createBaseWorkspace(Path settingsRoot) throws IOException {
        Path baseWorkspace = settingsRoot.resolve("webdriver_generic");
        Files.createDirectories(baseWorkspace.resolve("test_goals"));
        Files.writeString(
            baseWorkspace.resolve("test.settings"),
            "SUTConnector = WEB_DRIVER\n",
            StandardCharsets.UTF_8
        );
        Files.writeString(
            baseWorkspace.resolve("composition.properties"),
            "systemServiceClass = WebdriverGenericSystemService\n",
            StandardCharsets.UTF_8
        );
        Files.writeString(
            baseWorkspace.resolve("policies.properties"),
            "clickablePolicies = WebdriverGenericClickablePolicy\n",
            StandardCharsets.UTF_8
        );
        Files.writeString(
            baseWorkspace.resolve("WebdriverGenericSystemService.java"),
            "public final class WebdriverGenericSystemService {}\n",
            StandardCharsets.UTF_8
        );
        Files.writeString(
            baseWorkspace.resolve("test_goals").resolve("login.yaml"),
            "id: login\n",
            StandardCharsets.UTF_8
        );
        return baseWorkspace;
    }

    private TestWorkspaceRoots createWorkspaceRoots() throws IOException {
        Path root = temporaryFolder.newFolder("workspace-clone").toPath();
        Path testarSettingsRoot = root.resolve("testar").resolve("target").resolve("install").resolve("testar")
            .resolve("bin").resolve("settings");
        Path cliSettingsRoot = root.resolve("cli").resolve("target").resolve("install").resolve("testar-cli")
            .resolve("settings");
        Files.createDirectories(testarSettingsRoot);
        Files.createDirectories(cliSettingsRoot);
        return new TestWorkspaceRoots(testarSettingsRoot, cliSettingsRoot);
    }

    private IllegalArgumentException expectIllegalArgumentException(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException exception) {
            return exception;
        }

        Assert.fail("Expected IllegalArgumentException.");
        return null;
    }

    private static final class TestWorkspaceRoots {

        private final Path testarSettingsRoot;
        private final Path cliSettingsRoot;

        private TestWorkspaceRoots(Path testarSettingsRoot, Path cliSettingsRoot) {
            this.testarSettingsRoot = testarSettingsRoot;
            this.cliSettingsRoot = cliSettingsRoot;
        }
    }
}
