package org.testar;

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testar.config.TestarDirectories;

public class TestOutputStructure {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private final String previousOutputDir = TestarDirectories.getOutputDir();

    @After
    public void tearDown() {
        TestarDirectories.setOutputDir(previousOutputDir);
        OutputStructure.setExecutionModeName("");
    }

    @Test
    public void outputFolderIncludesExecutionModeWhenConfigured() {
        TestarDirectories.setOutputDir(tempFolder.getRoot().getAbsolutePath());
        OutputStructure.startOuterLoopDateString = "2026-06-29_13h23m23s";
        OutputStructure.executedSUTname = "webdriver_parabank_1";
        OutputStructure.setExecutionModeName("cli");

        OutputStructure.createOutputFolders();

        Assert.assertEquals(
                tempFolder.getRoot().getAbsolutePath()
                        + File.separator + "2026-06-29_13h23m23s_cli_webdriver_parabank_1",
                OutputStructure.outerLoopOutputDir
        );
        Assert.assertTrue(new File(OutputStructure.outerLoopOutputDir).isDirectory());
    }

    @Test
    public void outputFolderKeepsLegacyNameWhenModeIsEmpty() {
        TestarDirectories.setOutputDir(tempFolder.getRoot().getAbsolutePath());
        OutputStructure.startOuterLoopDateString = "2026-06-29_13h23m23s";
        OutputStructure.executedSUTname = "webdriver_parabank_1";
        OutputStructure.setExecutionModeName("");

        OutputStructure.createOutputFolders();

        Assert.assertEquals(
                tempFolder.getRoot().getAbsolutePath()
                        + File.separator + "2026-06-29_13h23m23s_webdriver_parabank_1",
                OutputStructure.outerLoopOutputDir
        );
    }
}
