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
import org.testar.TestarDirectories;
import org.testar.monkey.alayer.Verdict;
import org.testar.settings.Settings;
import org.testar.verdict.VerdictProcessing;

public class VerdictProcessingTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@After
	public void tearDown() {
		Settings.setSettingsPath(null);
		TestarDirectories.setSelectedSse(null);
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
	public void testDoesNotIgnoreDuplicateLlmComplete() throws Exception {
		File settingsDir = tempFolder.newFolder("settings_llm_complete");
		Settings.setSettingsPath(settingsDir.getAbsolutePath());

		File ignoreFile = new File(settingsDir, "list_of_verdicts_with_failures.txt");
		Files.write(ignoreFile.toPath(), Collections.singletonList("goal completed by llm"), StandardCharsets.UTF_8);

		Settings settings = new Settings();
		settings.set(ConfigTags.IgnoreDuplicatedVerdicts, true);
		VerdictProcessing processing = new VerdictProcessing(settings);

		Verdict llmComplete = new Verdict(Verdict.Severity.LLM_COMPLETE, "goal completed by llm");
		List<Verdict> filtered = processing.filterDuplicates(Collections.singletonList(llmComplete));

		assertEquals(1, filtered.size());
		assertEquals(Verdict.Severity.LLM_COMPLETE.getValue(), filtered.get(0).severity(), 0.0);
	}

	@Test
	public void testDoesNotIgnoreDuplicateLlmInvalid() throws Exception {
		File settingsDir = tempFolder.newFolder("settings_llm_invalid");
		Settings.setSettingsPath(settingsDir.getAbsolutePath());

		File ignoreFile = new File(settingsDir, "list_of_verdicts_with_failures.txt");
		Files.write(ignoreFile.toPath(), Collections.singletonList("goal invalid by llm"), StandardCharsets.UTF_8);

		Settings settings = new Settings();
		settings.set(ConfigTags.IgnoreDuplicatedVerdicts, true);
		VerdictProcessing processing = new VerdictProcessing(settings);

		Verdict llmInvalid = new Verdict(Verdict.Severity.LLM_INVALID, "goal invalid by llm");
		List<Verdict> filtered = processing.filterDuplicates(Collections.singletonList(llmInvalid));

		assertEquals(1, filtered.size());
		assertEquals(Verdict.Severity.LLM_INVALID.getValue(), filtered.get(0).severity(), 0.0);
	}

	@Test
	public void testDoesNotIgnoreDuplicateConditionComplete() throws Exception {
		File settingsDir = tempFolder.newFolder("settings_condition_complete");
		Settings.setSettingsPath(settingsDir.getAbsolutePath());

		File ignoreFile = new File(settingsDir, "list_of_verdicts_with_failures.txt");
		Files.write(ignoreFile.toPath(), Collections.singletonList("all conditions completed"), StandardCharsets.UTF_8);

		Settings settings = new Settings();
		settings.set(ConfigTags.IgnoreDuplicatedVerdicts, true);
		VerdictProcessing processing = new VerdictProcessing(settings);

		Verdict conditionComplete = new Verdict(Verdict.Severity.CONDITION_COMPLETE, "all conditions completed");
		List<Verdict> filtered = processing.filterDuplicates(Collections.singletonList(conditionComplete));

		assertEquals(1, filtered.size());
		assertEquals(Verdict.Severity.CONDITION_COMPLETE.getValue(), filtered.get(0).severity(), 0.0);
	}

	@Test
	public void testVerdictIgnoreFile_PrioritizesSSE() throws Exception {
		File tempSettingsDir = tempFolder.newFolder("tempSettingsDir");
		String originalSettingsDir = TestarDirectories.getSettingsDir();
		try {
			TestarDirectories.setSettingsDir(tempSettingsDir.getAbsolutePath() + File.separator);
			TestarDirectories.setSelectedSse("protocol_selected");
			Settings.setSettingsPath(tempFolder.newFolder("otherProtocol").getAbsolutePath());

			File verdictIgnoreFile = VerdictProcessing.resolveVerdictIgnoreFile();
			assertEquals(new File(TestarDirectories.getSettingsDir() + "protocol_selected", "list_of_verdicts_with_failures.txt").getAbsolutePath(),
					verdictIgnoreFile.getAbsolutePath());
		} finally {
			TestarDirectories.setSettingsDir(originalSettingsDir); // cleanup to restore static global dir
		}
	}

	@Test
	public void testVerdictIgnoreFile_UsesSettingsPathWhenNoSSE() throws Exception {
		File tempSettingsDir = tempFolder.newFolder("tempSettingsDir");
		TestarDirectories.setSelectedSse(null);
		Settings.setSettingsPath(tempSettingsDir.getAbsolutePath());

		File verdictIgnoreFile = VerdictProcessing.resolveVerdictIgnoreFile();
		assertEquals(new File(tempSettingsDir, "list_of_verdicts_with_failures.txt").getAbsolutePath(),
				verdictIgnoreFile.getAbsolutePath());
	}

	@Test
	public void testVerdictIgnoreFile_IsNullWhenNoContext() {
		TestarDirectories.setSelectedSse(null);
		Settings.setSettingsPath(null);

		File verdictIgnoreFile = VerdictProcessing.resolveVerdictIgnoreFile();
		assertEquals(null, verdictIgnoreFile);
	}
}
