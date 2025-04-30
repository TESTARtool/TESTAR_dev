package org.testar;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.testar.monkey.alayer.Verdict;

import java.io.File;
import java.io.IOException;
import static org.junit.Assert.*;

public class TestFileHandling {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	private File testFile;
	private File outputDir;

	@Before
	public void setUp() throws IOException {
		// Create a temporary file that will act as our sequence file
		testFile = tempFolder.newFile("2025-01-01_12h12m12s_notepad_sequence_1.testar");

		// Create a temporary output directory
		outputDir = tempFolder.newFolder("output");
		OutputStructure.outerLoopOutputDir = outputDir.getAbsolutePath();
	}

	@Test
	public void testCopyClassifiedSequence_okSeverity() throws IOException {
		FileHandling.copyClassifiedSequence("okSequence", testFile, Verdict.OK);

		File targetDir = new File(outputDir, "sequences_ok");
		assertTrue("OK sequence folder should be created", targetDir.exists());
		assertTrue("Copied file should exist in sequences_ok", new File(targetDir, testFile.getName()).exists());
	}

	@Test
	public void testCopyClassifiedSequence_failSeverity() throws IOException {
		FileHandling.copyClassifiedSequence("failSequence", testFile, Verdict.FAIL);

		File targetDir = new File(outputDir, "sequences_fail");
		assertTrue("Fail sequence folder should be created", targetDir.exists());
		assertTrue("Copied file should exist in sequences_fail", new File(targetDir, testFile.getName()).exists());
	}

	@Test
	public void testCopyClassifiedSequence_suspiciousLogSeverity() throws IOException {
		Verdict verdict = new Verdict(Verdict.Severity.SUSPICIOUS_LOG, "Suspicious log entry found");

		FileHandling.copyClassifiedSequence("logSequence", testFile, verdict);

		File targetDir = new File(outputDir, "sequences_suspicious_log");
		assertTrue("Suspicious log folder should be created", targetDir.exists());
		assertTrue("Copied file should exist in sequences_suspicious_log", new File(targetDir, testFile.getName()).exists());
	}

}
