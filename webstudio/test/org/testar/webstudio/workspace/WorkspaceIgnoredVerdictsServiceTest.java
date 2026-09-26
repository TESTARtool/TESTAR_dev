package org.testar.webstudio.workspace;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testar.config.verdict.VerdictProcessing;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class WorkspaceIgnoredVerdictsServiceTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void removesOnlySelectedEntriesFromSelectedWorkspace() throws IOException {
        Path root = temporaryFolder.newFolder("workspaces").toPath();
        Path first = Files.createDirectories(root.resolve("first"));
        Path second = Files.createDirectories(root.resolve("second"));
        String filename = VerdictProcessing.LIST_VERDICTS_FAILURES_FILENAME;
        Files.write(first.resolve(filename), List.of("first issue", "second issue"), StandardCharsets.UTF_8);
        Files.write(second.resolve(filename), List.of("other workspace issue"), StandardCharsets.UTF_8);
        WorkspaceIgnoredVerdictsService service = service(root);

        Assert.assertEquals(List.of("second issue"), service.remove("first", List.of("first issue")));
        Assert.assertEquals(List.of("other workspace issue"), service.list("second"));
    }

    @Test
    public void clearingDoesNotCreateOrChangeOtherWorkspaceFile() throws IOException {
        Path root = temporaryFolder.newFolder("workspaces").toPath();
        Files.createDirectories(root.resolve("first"));
        Files.createDirectories(root.resolve("second"));
        WorkspaceIgnoredVerdictsService service = service(root);

        Assert.assertEquals(List.of(), service.list("first"));
        Assert.assertEquals(List.of(), service.clear("first"));
        Assert.assertFalse(Files.exists(root.resolve("first")
            .resolve(VerdictProcessing.LIST_VERDICTS_FAILURES_FILENAME)));
        Assert.assertFalse(Files.exists(root.resolve("second")
            .resolve(VerdictProcessing.LIST_VERDICTS_FAILURES_FILENAME)));
    }

    private WorkspaceIgnoredVerdictsService service(Path root) {
        return new WorkspaceIgnoredVerdictsService(new WorkspaceService(root, root));
    }
}
