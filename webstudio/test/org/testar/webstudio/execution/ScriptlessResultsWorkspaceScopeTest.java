package org.testar.webstudio.execution;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testar.webstudio.api.dto.ScriptlessResultsDto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ScriptlessResultsWorkspaceScopeTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private String originalUserDirectory;

    @Before
    public void storeUserDirectory() {
        originalUserDirectory = System.getProperty("user.dir");
    }

    @After
    public void restoreUserDirectory() {
        System.setProperty("user.dir", originalUserDirectory);
    }

    @Test
    public void loadsOnlySelectedWorkspaceResults() throws IOException {
        Path projectRoot = temporaryFolder.newFolder("project").toPath();
        Path installBin = projectRoot.resolve("testar").resolve("target").resolve("install").resolve("testar").resolve("bin");
        createResultFile(installBin, "webdriver_generic", "webdriver_run", "sequence_1_V001_OK.html");
        createResultFile(installBin, "android_generic", "android_run", "sequence_1_V001_LLM_INVALID.html");
        System.setProperty("user.dir", projectRoot.toString());

        ScriptlessExecutionAdapter adapter = new ScriptlessExecutionAdapter();

        ScriptlessResultsDto results = adapter.scriptlessResults("webdriver_generic");

        Assert.assertEquals(1, results.groups().size());
        Assert.assertEquals("webdriver_run", results.groups().get(0).name());
        Assert.assertEquals(1, results.groups().get(0).files().size());
        Assert.assertEquals("sequence_1_V001_OK.html", results.groups().get(0).files().get(0).name());
    }

    private void createResultFile(Path installBin,
                                  String workspaceName,
                                  String runName,
                                  String fileName) throws IOException {
        Path resultFile = installBin
            .resolve("output")
            .resolve(workspaceName)
            .resolve(runName)
            .resolve("reports")
            .resolve(fileName);
        Files.createDirectories(resultFile.getParent());
        Files.writeString(resultFile, "<html></html>");
    }
}
