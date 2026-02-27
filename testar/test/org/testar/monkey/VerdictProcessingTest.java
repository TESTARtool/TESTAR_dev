package org.testar.monkey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testar.monkey.alayer.Verdict;
import org.testar.settings.Settings;

public class VerdictProcessingTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@After
	public void tearDown() {
		Settings.setSettingsPath(null);
		Main.SSE_ACTIVATED = null;
	}

	@Test
	public void testNullListReturnsOk() {
		Settings settings = new Settings();
		settings.set(ConfigTags.IgnoreDuplicatedVerdicts, false);
		VerdictProcessing processing = new VerdictProcessing(settings);

		List<Verdict> filtered = processing.filterDuplicates(null);
		assertEquals(1, filtered.size());
		assertEquals(Verdict.OK.severity(), filtered.get(0).severity(), 0.0);
	}

	@Test
	public void testMultipleOkReturnsSingleOk() {
		Settings settings = new Settings();
		settings.set(ConfigTags.IgnoreDuplicatedVerdicts, false);
		VerdictProcessing processing = new VerdictProcessing(settings);

		List<Verdict> filtered = processing.filterDuplicates(Arrays.asList(Verdict.OK, Verdict.OK));
		assertEquals(1, filtered.size());
		assertEquals(Verdict.OK.severity(), filtered.get(0).severity(), 0.0);
	}

	@Test
	public void testOkAndFailureRemovesOk() {
		Settings settings = new Settings();
		settings.set(ConfigTags.IgnoreDuplicatedVerdicts, false);
		VerdictProcessing processing = new VerdictProcessing(settings);

		Verdict failure = new Verdict(Verdict.Severity.SUSPICIOUS_TAG, "Issue");
		Verdict logVerdict = new Verdict(Verdict.Severity.SUSPICIOUS_LOG, "Log exception");
		List<Verdict> filtered = processing.filterDuplicates(Arrays.asList(Verdict.OK, failure, logVerdict));
		assertEquals(2, filtered.size());
		assertTrue(filtered.get(0).severity() > Verdict.OK.severity());
		assertTrue(filtered.get(1).severity() > Verdict.OK.severity());
	}

	@Test
	public void testIgnoresKnownDuplicate() throws Exception {
		File settingsDir = tempFolder.newFolder("settings");
		Settings.setSettingsPath(settingsDir.getAbsolutePath());

		File ignoreFile = new File(settingsDir, "list_of_verdicts_with_failures.txt");
		Files.write(ignoreFile.toPath(), Collections.singletonList("duplicate message"), StandardCharsets.UTF_8);

		Settings settings = new Settings();
		settings.set(ConfigTags.IgnoreDuplicatedVerdicts, true);
		VerdictProcessing processing = new VerdictProcessing(settings);

		Verdict duplicate = new Verdict(Verdict.Severity.SUSPICIOUS_TAG, "duplicate message");
		List<Verdict> filtered = processing.filterDuplicates(Collections.singletonList(duplicate));
		assertEquals(1, filtered.size());
		assertEquals(Verdict.OK.severity(), filtered.get(0).severity(), 0.0);
	}

	@Test
	public void testVerdictIgnoreFile_PrioritizesSSE() throws Exception {
		File tempSettingsDir = tempFolder.newFolder("tempSettingsDir");
		String originalSettingsDir = Main.settingsDir;
		try {
			Main.settingsDir = tempSettingsDir.getAbsolutePath() + File.separator;
			Main.SSE_ACTIVATED = "protocol_selected";
			Settings.setSettingsPath(tempFolder.newFolder("otherProtocol").getAbsolutePath());

			File verdictIgnoreFile = VerdictProcessing.resolveVerdictIgnoreFile();
			assertEquals(new File(Main.settingsDir + "protocol_selected", "list_of_verdicts_with_failures.txt").getAbsolutePath(),
					verdictIgnoreFile.getAbsolutePath());
		} finally {
			Main.settingsDir = originalSettingsDir; // cleanup to restore static global dir
		}
	}

	@Test
	public void testVerdictIgnoreFile_UsesSettingsPathWhenNoSSE() throws Exception {
		File tempSettingsDir = tempFolder.newFolder("tempSettingsDir");
		Main.SSE_ACTIVATED = null;
		Settings.setSettingsPath(tempSettingsDir.getAbsolutePath());

		File verdictIgnoreFile = VerdictProcessing.resolveVerdictIgnoreFile();
		assertEquals(new File(tempSettingsDir, "list_of_verdicts_with_failures.txt").getAbsolutePath(),
				verdictIgnoreFile.getAbsolutePath());
	}

	@Test
	public void testVerdictIgnoreFile_IsNullWhenNoContext() {
		Main.SSE_ACTIVATED = null;
		Settings.setSettingsPath(null);

		File verdictIgnoreFile = VerdictProcessing.resolveVerdictIgnoreFile();
		assertEquals(null, verdictIgnoreFile);
	}
}
