package org.testar.settings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.testar.monkey.Assert;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.RuntimeControlsProtocol.Modes;

public class TestLoadSettings {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void loadSettingsWithNullFilePath() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Settings.loadSettings: filePath cannot be null or empty");

		Settings.loadSettings(new String[0], null);
	}

	@Test
	public void loadSettingsWithEmptyFilePath() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Settings.loadSettings: filePath cannot be null or empty");

		Settings.loadSettings(new String[0], "");
	}

	@Test
	public void loadSettingsWithNonExistentFile() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("Settings.loadSettings: The specified file does not exist: ./settings/unknown/test.settings");

		Settings.loadSettings(new String[0], "./settings/unknown/test.settings");
	}

	@Test
	public void loadSettingsWithDesktopSettings() throws IOException {
		// Create a temporary folder and test.settings folder
		tempFolder.newFolder("tempSettingsFolder");
		File tempFile = tempFolder.newFile("test.settings");

		// Write the test.settings content to the temporary file
		try (FileWriter writer = new FileWriter(tempFile)) {
			writer.write("Sequences = 12345\n");
			writer.write("StartupTime = 12.34\n");
			writer.write("ApplicationVersion = testversion");
		} catch (IOException e) {
			e.printStackTrace();
		}

		Settings settings = Settings.loadSettings(new String[] {"Discount=0.123", "ApplicationName=custom"}, tempFile.getCanonicalPath());

		// Verify default settings that were not modified
		Assert.isTrue(settings.get(ConfigTags.Mode).equals(Modes.Spy));
		Assert.isTrue(settings.get(ConfigTags.SequenceLength) == 10);

		// Verify settings loaded with test.settings file
		Assert.isTrue(settings.get(ConfigTags.Sequences) == 12345);
		Assert.isTrue(settings.get(ConfigTags.StartupTime) == 12.34);
		Assert.isTrue(settings.get(ConfigTags.ApplicationVersion).equals("testversion"));

		// Verify settings loaded with command line args
		Assert.isTrue(settings.get(ConfigTags.Discount) == 0.123);
		Assert.isTrue(settings.get(ConfigTags.ApplicationName).equals("custom"));
	}
}
