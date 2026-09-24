package org.testar.cli.settings;

import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class CliSettingsLoaderTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void installsBundledProfileUnderWorkspaceDirectory() throws Exception {
        String previousTestarHome = System.getProperty("testar.home");
        Path testarHome = temporaryFolder.newFolder("testar-home").toPath();
        System.setProperty("testar.home", testarHome.toString());

        try {
            List<String> profiles = CliSettingsLoader.listProfiles();
            Path profileDirectory = testarHome.resolve("workspaces").resolve("cli_generic");

            assertTrue(profiles.contains("cli_generic"));
            assertTrue(Files.isRegularFile(profileDirectory.resolve("test.settings")));
            assertTrue(Files.isRegularFile(profileDirectory.resolve("composition.properties")));
            assertTrue(Files.isRegularFile(profileDirectory.resolve("policies.properties")));
        } finally {
            if (previousTestarHome == null) {
                System.clearProperty("testar.home");
            } else {
                System.setProperty("testar.home", previousTestarHome);
            }
        }
    }
}
