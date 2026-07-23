package org.testar.webstudio.analysis;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Path;

public class StateModelAnalysisServiceTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void resolvesGraphAssetsFromSharedRuntimeOutputDirectory() throws IOException {
        Path runtimeHome = temporaryFolder.newFolder("bin").toPath();

        Path graphsDirectory = StateModelAnalysisService.resolveGraphsDirectory(runtimeHome);

        Assert.assertEquals(
            runtimeHome.resolve("output").resolve("graphs").toAbsolutePath().normalize(),
            graphsDirectory
        );
    }
}
