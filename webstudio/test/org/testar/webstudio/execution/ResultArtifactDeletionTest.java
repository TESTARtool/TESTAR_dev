package org.testar.webstudio.execution;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResultArtifactDeletionTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void deletesSingleResultFileInsideOutputDirectory() throws IOException {
        Path outputDirectory = temporaryFolder.newFolder("output").toPath();
        Path resultFile = outputDirectory.resolve("run").resolve("reports").resolve("sequence_1_V001_OK.html");
        Files.createDirectories(resultFile.getParent());
        Files.writeString(resultFile, "<html></html>");

        ResultArtifactDeletion.deleteResultFile(outputDirectory, resultFile.toString());

        Assert.assertFalse(Files.exists(resultFile));
        Assert.assertTrue(Files.isDirectory(outputDirectory.resolve("run")));
    }

    @Test
    public void deletesResultGroupInsideOutputDirectory() throws IOException {
        Path outputDirectory = temporaryFolder.newFolder("output").toPath();
        Path resultGroup = outputDirectory.resolve("run");
        Path resultFile = resultGroup.resolve("reports").resolve("sequence_1_V001_OK.html");
        Files.createDirectories(resultFile.getParent());
        Files.writeString(resultFile, "<html></html>");

        ResultArtifactDeletion.deleteResultGroup(outputDirectory, resultGroup.toString());

        Assert.assertFalse(Files.exists(resultGroup));
        Assert.assertTrue(Files.isDirectory(outputDirectory));
    }

    @Test
    public void rejectsFileOutsideOutputDirectory() throws IOException {
        Path outputDirectory = temporaryFolder.newFolder("output").toPath();
        Path externalFile = temporaryFolder.newFile("external.html").toPath();

        try {
            ResultArtifactDeletion.deleteResultFile(outputDirectory, externalFile.toString());
            Assert.fail("Expected invalid result path to be rejected");
        } catch (IllegalArgumentException exception) {
            Assert.assertTrue(exception.getMessage().contains("Invalid result path"));
        }

        Assert.assertTrue(Files.exists(externalFile));
    }
}
