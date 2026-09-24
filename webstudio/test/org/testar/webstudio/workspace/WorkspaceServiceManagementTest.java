package org.testar.webstudio.workspace;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class WorkspaceServiceManagementTest {

    // Verifies WS-FUNC-WORKSPACE-MANAGEMENT-001: workspace clone and rename filesystem behavior.
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void createsWorkspaceByCloningBaseWorkspaceWithTestGoals() throws IOException {
        TestWorkspaceRoots roots = createWorkspaceRoots();
        Path baseWorkspace = createBaseWorkspace(roots.testarWorkspacesRoot);
        WorkspaceService workspaceService = new WorkspaceService(roots.testarWorkspacesRoot, roots.cliWorkspacesRoot);

        workspaceService.createWorkspace("webdriver_cloned", "webdriver_generic", true, true);

        Path clonedWorkspace = roots.testarWorkspacesRoot.resolve("webdriver_cloned");
        Assert.assertTrue(Files.isDirectory(clonedWorkspace));
        String clonedSettings = Files.readString(clonedWorkspace.resolve("test.settings"));
        Assert.assertTrue(clonedSettings.contains(
            "CustomCompositionResource = ./workspaces/webdriver_cloned/composition.properties"
        ));
        Assert.assertTrue(clonedSettings.contains(
            "CustomPoliciesResource = ./workspaces/webdriver_cloned/policies.properties"
        ));
        Assert.assertTrue(Files.readString(baseWorkspace.resolve("test.settings")).contains(
            "CustomCompositionResource = ./workspaces/webdriver_generic/composition.properties"
        ));
        Assert.assertTrue(Files.isRegularFile(clonedWorkspace.resolve("composition.properties")));
        Assert.assertTrue(Files.isRegularFile(clonedWorkspace.resolve("policies.properties")));
        Assert.assertEquals(
            Files.readString(baseWorkspace.resolve("WebdriverGenericSystemService.java")),
            Files.readString(clonedWorkspace.resolve("WebdriverGenericSystemService.java"))
        );
        Assert.assertTrue(Files.isRegularFile(clonedWorkspace.resolve("test_goals").resolve("login.yaml")));
        Assert.assertTrue(Files.isRegularFile(clonedWorkspace.resolve("oracles").resolve("java").resolve("WebOracle.java")));
        Assert.assertTrue(Files.isRegularFile(clonedWorkspace.resolve("oracles").resolve("dsl").resolve("web_oracle.testar")));
    }

    @Test
    public void createsEmptyTestGoalsDirectoryWhenCopyTestGoalsIsDisabled() throws IOException {
        TestWorkspaceRoots roots = createWorkspaceRoots();
        createBaseWorkspace(roots.testarWorkspacesRoot);
        WorkspaceService workspaceService = new WorkspaceService(roots.testarWorkspacesRoot, roots.cliWorkspacesRoot);

        workspaceService.createWorkspace("webdriver_no_goals", "webdriver_generic", false);

        Path clonedWorkspace = roots.testarWorkspacesRoot.resolve("webdriver_no_goals");
        Assert.assertTrue(Files.isDirectory(clonedWorkspace.resolve("test_goals")));
        Assert.assertFalse(Files.exists(clonedWorkspace.resolve("test_goals").resolve("login.yaml")));
        Assert.assertTrue(Files.isRegularFile(clonedWorkspace.resolve("test.settings")));
    }

    @Test
    public void createsEmptyOraclesDirectoryWhenCopyOraclesIsDisabled() throws IOException {
        TestWorkspaceRoots roots = createWorkspaceRoots();
        createBaseWorkspace(roots.testarWorkspacesRoot);
        WorkspaceService workspaceService = new WorkspaceService(roots.testarWorkspacesRoot, roots.cliWorkspacesRoot);

        workspaceService.createWorkspace("webdriver_no_oracles", "webdriver_generic", true, false);

        Path clonedWorkspace = roots.testarWorkspacesRoot.resolve("webdriver_no_oracles");
        Assert.assertTrue(Files.isDirectory(clonedWorkspace.resolve("oracles").resolve("java")));
        Assert.assertTrue(Files.isDirectory(clonedWorkspace.resolve("oracles").resolve("dsl")));
        Assert.assertTrue(Files.isDirectory(clonedWorkspace.resolve("oracles").resolve("compiled")));
        Assert.assertFalse(Files.exists(clonedWorkspace.resolve("oracles").resolve("java").resolve("WebOracle.java")));
        Assert.assertFalse(Files.exists(clonedWorkspace.resolve("oracles").resolve("dsl").resolve("web_oracle.testar")));
        Assert.assertTrue(Files.isRegularFile(clonedWorkspace.resolve("test_goals").resolve("login.yaml")));
    }

    @Test
    public void rejectsDuplicateWorkspaceNamesAcrossRuntimeRoots() throws IOException {
        TestWorkspaceRoots roots = createWorkspaceRoots();
        createBaseWorkspace(roots.testarWorkspacesRoot);
        Files.createDirectories(roots.cliWorkspacesRoot.resolve("existing_cli_workspace"));
        WorkspaceService workspaceService = new WorkspaceService(roots.testarWorkspacesRoot, roots.cliWorkspacesRoot);

        IllegalArgumentException exception = expectIllegalArgumentException(
            () -> workspaceService.createWorkspace("existing_cli_workspace", "webdriver_generic", true)
        );

        Assert.assertTrue(exception.getMessage().contains("Workspace already exists"));
    }

    @Test
    public void rejectsUnsafeWorkspaceNames() throws IOException {
        TestWorkspaceRoots roots = createWorkspaceRoots();
        createBaseWorkspace(roots.testarWorkspacesRoot);
        WorkspaceService workspaceService = new WorkspaceService(roots.testarWorkspacesRoot, roots.cliWorkspacesRoot);

        IllegalArgumentException exception = expectIllegalArgumentException(
            () -> workspaceService.createWorkspace("../bad", "webdriver_generic", true)
        );

        Assert.assertTrue(exception.getMessage().contains("letters, numbers, underscores, and hyphens"));
    }

    @Test
    public void renamesWorkspaceInSharedWorkspacesRoot() throws IOException {
        TestWorkspaceRoots roots = createWorkspaceRoots();
        createBaseWorkspace(roots.testarWorkspacesRoot);
        Path outputFile = roots.testarOutputRoot.resolve("webdriver_generic").resolve("run").resolve("reports")
            .resolve("sequence_1.html");
        Files.createDirectories(outputFile.getParent());
        Files.writeString(outputFile, "<html></html>", StandardCharsets.UTF_8);
        WorkspaceService workspaceService = new WorkspaceService(roots.testarWorkspacesRoot, roots.cliWorkspacesRoot);

        workspaceService.renameWorkspace("webdriver_generic", "webdriver_renamed");

        Assert.assertFalse(Files.exists(roots.testarWorkspacesRoot.resolve("webdriver_generic")));
        Assert.assertTrue(Files.isDirectory(roots.testarWorkspacesRoot.resolve("webdriver_renamed")));
        Assert.assertTrue(Files.isRegularFile(roots.testarWorkspacesRoot.resolve("webdriver_renamed").resolve("test.settings")));
        Assert.assertTrue(Files.isRegularFile(
            roots.testarWorkspacesRoot.resolve("webdriver_renamed").resolve("test_goals").resolve("login.yaml")
        ));
        Assert.assertFalse(Files.exists(roots.testarOutputRoot.resolve("webdriver_generic")));
        Assert.assertTrue(Files.isRegularFile(
            roots.testarOutputRoot.resolve("webdriver_renamed").resolve("run").resolve("reports").resolve("sequence_1.html")
        ));
        String renamedSettings = Files.readString(
            roots.testarWorkspacesRoot.resolve("webdriver_renamed").resolve("test.settings")
        );
        Assert.assertTrue(renamedSettings.contains(
            "CustomCompositionResource = ./workspaces/webdriver_renamed/composition.properties"
        ));
        Assert.assertTrue(renamedSettings.contains(
            "CustomPoliciesResource = ./workspaces/webdriver_renamed/policies.properties"
        ));
    }

    @Test
    public void renamesWorkspaceWhenOutputDirectoryDoesNotExist() throws IOException {
        TestWorkspaceRoots roots = createWorkspaceRoots();
        createBaseWorkspace(roots.testarWorkspacesRoot);
        WorkspaceService workspaceService = new WorkspaceService(roots.testarWorkspacesRoot, roots.cliWorkspacesRoot);

        workspaceService.renameWorkspace("webdriver_generic", "webdriver_renamed");

        Assert.assertTrue(Files.isDirectory(roots.testarWorkspacesRoot.resolve("webdriver_renamed")));
        Assert.assertFalse(Files.exists(roots.testarOutputRoot.resolve("webdriver_generic")));
        Assert.assertFalse(Files.exists(roots.testarOutputRoot.resolve("webdriver_renamed")));
        String renamedSettings = Files.readString(
            roots.testarWorkspacesRoot.resolve("webdriver_renamed").resolve("test.settings")
        );
        Assert.assertTrue(renamedSettings.contains(
            "CustomCompositionResource = ./workspaces/webdriver_renamed/composition.properties"
        ));
        Assert.assertTrue(renamedSettings.contains(
            "CustomPoliciesResource = ./workspaces/webdriver_renamed/policies.properties"
        ));
    }

    @Test
    public void renamesWorkspaceAndPreservesCustomResourceFileNames() throws IOException {
        TestWorkspaceRoots roots = createWorkspaceRoots();
        createBaseWorkspace(
            roots.testarWorkspacesRoot,
            "custom-composition.properties",
            "custom-policies.properties"
        );
        WorkspaceService workspaceService = new WorkspaceService(roots.testarWorkspacesRoot, roots.cliWorkspacesRoot);

        workspaceService.renameWorkspace("webdriver_generic", "webdriver_renamed");

        Path renamedWorkspace = roots.testarWorkspacesRoot.resolve("webdriver_renamed");
        String renamedSettings = Files.readString(renamedWorkspace.resolve("test.settings"));
        Assert.assertTrue(renamedSettings.contains(
            "CustomCompositionResource = ./workspaces/webdriver_renamed/custom-composition.properties"
        ));
        Assert.assertTrue(renamedSettings.contains(
            "CustomPoliciesResource = ./workspaces/webdriver_renamed/custom-policies.properties"
        ));
        Assert.assertTrue(Files.isRegularFile(renamedWorkspace.resolve("custom-composition.properties")));
        Assert.assertTrue(Files.isRegularFile(renamedWorkspace.resolve("custom-policies.properties")));
    }

    @Test
    public void rejectsRenameWhenTargetOutputDirectoryAlreadyExists() throws IOException {
        TestWorkspaceRoots roots = createWorkspaceRoots();
        createBaseWorkspace(roots.testarWorkspacesRoot);
        Files.createDirectories(roots.testarOutputRoot.resolve("webdriver_generic"));
        Files.createDirectories(roots.testarOutputRoot.resolve("webdriver_parabank"));
        WorkspaceService workspaceService = new WorkspaceService(roots.testarWorkspacesRoot, roots.cliWorkspacesRoot);

        IllegalArgumentException exception = expectIllegalArgumentException(
            () -> workspaceService.renameWorkspace("webdriver_generic", "webdriver_parabank")
        );

        Assert.assertTrue(exception.getMessage().contains("Output results already exist"));
        Assert.assertTrue(Files.isDirectory(roots.testarWorkspacesRoot.resolve("webdriver_generic")));
        Assert.assertFalse(Files.exists(roots.testarWorkspacesRoot.resolve("webdriver_parabank")));
        Assert.assertTrue(Files.isDirectory(roots.testarOutputRoot.resolve("webdriver_generic")));
        Assert.assertTrue(Files.isDirectory(roots.testarOutputRoot.resolve("webdriver_parabank")));
    }

    @Test
    public void rejectsRenameWhenOnlyTargetOutputDirectoryAlreadyExists() throws IOException {
        TestWorkspaceRoots roots = createWorkspaceRoots();
        createBaseWorkspace(roots.testarWorkspacesRoot);
        Files.createDirectories(roots.testarOutputRoot.resolve("webdriver_parabank"));
        WorkspaceService workspaceService = new WorkspaceService(roots.testarWorkspacesRoot, roots.cliWorkspacesRoot);

        IllegalArgumentException exception = expectIllegalArgumentException(
            () -> workspaceService.renameWorkspace("webdriver_generic", "webdriver_parabank")
        );

        Assert.assertTrue(exception.getMessage().contains("Output results already exist"));
        Assert.assertTrue(Files.isDirectory(roots.testarWorkspacesRoot.resolve("webdriver_generic")));
        Assert.assertFalse(Files.exists(roots.testarWorkspacesRoot.resolve("webdriver_parabank")));
        Assert.assertTrue(Files.isDirectory(roots.testarOutputRoot.resolve("webdriver_parabank")));
    }

    @Test
    public void rejectsRenameToExistingWorkspace() throws IOException {
        TestWorkspaceRoots roots = createWorkspaceRoots();
        createBaseWorkspace(roots.testarWorkspacesRoot);
        Files.createDirectories(roots.testarWorkspacesRoot.resolve("windows_generic"));
        WorkspaceService workspaceService = new WorkspaceService(roots.testarWorkspacesRoot, roots.cliWorkspacesRoot);

        IllegalArgumentException exception = expectIllegalArgumentException(
            () -> workspaceService.renameWorkspace("webdriver_generic", "windows_generic")
        );

        Assert.assertTrue(exception.getMessage().contains("Workspace already exists"));
    }

    @Test
    public void rejectsRenameOfCliOnlyWorkspace() throws IOException {
        TestWorkspaceRoots roots = createWorkspaceRoots();
        Files.createDirectories(roots.cliWorkspacesRoot.resolve("cli_only"));
        WorkspaceService workspaceService = new WorkspaceService(roots.testarWorkspacesRoot, roots.cliWorkspacesRoot);

        IllegalArgumentException exception = expectIllegalArgumentException(
            () -> workspaceService.renameWorkspace("cli_only", "cli_renamed")
        );

        Assert.assertTrue(exception.getMessage().contains("shared workspaces root"));
    }

    private Path createBaseWorkspace(Path workspacesRoot) throws IOException {
        return createBaseWorkspace(workspacesRoot, "composition.properties", "policies.properties");
    }

    private Path createBaseWorkspace(
        Path workspacesRoot,
        String compositionFileName,
        String policiesFileName
    ) throws IOException {
        Path baseWorkspace = workspacesRoot.resolve("webdriver_generic");
        Files.createDirectories(baseWorkspace.resolve("test_goals"));
        Files.createDirectories(baseWorkspace.resolve("oracles").resolve("java"));
        Files.createDirectories(baseWorkspace.resolve("oracles").resolve("dsl"));
        Files.writeString(
            baseWorkspace.resolve("test.settings"),
            "SUTConnector = WEB_DRIVER\n"
                + "CustomCompositionResource = ./workspaces/webdriver_generic/" + compositionFileName + "\n"
                + "CustomPoliciesResource = ./workspaces/webdriver_generic/" + policiesFileName + "\n",
            StandardCharsets.UTF_8
        );
        Files.writeString(
            baseWorkspace.resolve(compositionFileName),
            "systemServiceClass = WebdriverGenericSystemService\n",
            StandardCharsets.UTF_8
        );
        Files.writeString(
            baseWorkspace.resolve(policiesFileName),
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
        Files.writeString(
            baseWorkspace.resolve("oracles").resolve("java").resolve("WebOracle.java"),
            "public final class WebOracle {}\n",
            StandardCharsets.UTF_8
        );
        Files.writeString(
            baseWorkspace.resolve("oracles").resolve("dsl").resolve("web_oracle.testar"),
            "module web_oracle\n",
            StandardCharsets.UTF_8
        );
        return baseWorkspace;
    }

    private TestWorkspaceRoots createWorkspaceRoots() throws IOException {
        Path root = temporaryFolder.newFolder("workspace-clone").toPath();
        Path testarWorkspacesRoot = root.resolve("testar").resolve("target").resolve("install").resolve("testar")
            .resolve("bin").resolve("workspaces");
        Path testarOutputRoot = testarWorkspacesRoot.getParent().resolve("output");
        Path cliWorkspacesRoot = root.resolve("cli").resolve("target").resolve("install").resolve("testar-cli")
            .resolve("workspaces");
        Files.createDirectories(testarWorkspacesRoot);
        Files.createDirectories(cliWorkspacesRoot);
        return new TestWorkspaceRoots(testarWorkspacesRoot, testarOutputRoot, cliWorkspacesRoot);
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

        private final Path testarWorkspacesRoot;
        private final Path testarOutputRoot;
        private final Path cliWorkspacesRoot;

        private TestWorkspaceRoots(Path testarWorkspacesRoot, Path testarOutputRoot, Path cliWorkspacesRoot) {
            this.testarWorkspacesRoot = testarWorkspacesRoot;
            this.testarOutputRoot = testarOutputRoot;
            this.cliWorkspacesRoot = cliWorkspacesRoot;
        }
    }
}
