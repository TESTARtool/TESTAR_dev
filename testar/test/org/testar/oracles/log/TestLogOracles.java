package org.testar.oracles.log;

import static org.testar.monkey.ConfigTags.LogOracleRegex;
import static org.testar.monkey.ConfigTags.LogOracleFiles;
import static org.testar.monkey.ConfigTags.LogOracleCommands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.testar.monkey.Pair;
import org.testar.monkey.Settings;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Verdict;

public class TestLogOracles {

	String logOracleFile = "test_oracle.log";

	/**
	 * Create a temporal test_oracle.log file for all the unit tests
	 */
	@BeforeClass
	public static void createLogOracleFile() {
		try {
			File file = File.createTempFile("test_oracle", ".log");
			file.deleteOnExit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Clean the content of the temporal test_oracle.log file before each unit test
	 */
	@Before
	public void cleanLogOracleFile() {
		try {
			new FileOutputStream(logOracleFile).close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("serial")
	@Test
	public void wrongConfigurationLogOracleFile() {
		// The log file exists
		Assert.assertTrue(new File(logOracleFile).exists());

		// But an erroneous settings configuration does not indicate the correct log name
		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(LogOracleRegex, ".*([Ee]xception|[Ee]rror).*"));
		tags.add(Pair.from(LogOracleFiles, new ArrayList<String>() { {add("wrong.log");} }));
		tags.add(Pair.from(LogOracleCommands, new ArrayList<String>()));

		Settings settings = new Settings(tags, new Properties());
		LogOracle logOracle = new LogOracle(settings);
		logOracle.initialize();

		State state = Mockito.mock(State.class);
		Verdict logVerdict = logOracle.getVerdict(state);

		// Verify that the logVerdict is OK
		Assert.assertTrue(logVerdict.severity() == Verdict.SEVERITY_OK);
	}

	@SuppressWarnings("serial")
	@Test
	public void initialSuspiciousLogOracleErrorMessage() {
		// The log file exists
		Assert.assertTrue(new File(logOracleFile).exists());
		// Create an initial suspicious messages on the log
		// This must be ignored because is not created on runtime while testing
		String initialMessage = "This is an initial log error message";
		addTextToFile(initialMessage);
		System.out.println("initialSuspiciousLogOracleErrorMessage logFileContent: " + fileContent());
		Assert.assertTrue(fileContent().contains(initialMessage));

		// TESTAR is configured to detect possible suspiciousMessages
		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(LogOracleRegex, ".*([Ee]xception|[Ee]rror).*"));
		tags.add(Pair.from(LogOracleFiles, new ArrayList<String>() { {add(logOracleFile);} }));
		tags.add(Pair.from(LogOracleCommands, new ArrayList<String>()));

		Settings settings = new Settings(tags, new Properties());
		LogOracle logOracle = new LogOracle(settings);
		logOracle.initialize();

		State state = Mockito.mock(State.class);
		Verdict logVerdict = logOracle.getVerdict(state);
		System.out.println("initialSuspiciousLogOracleErrorMessage logVerdict: " + logVerdict.info());

		// Verify that the logVerdict is OK
		Assert.assertTrue(logVerdict.severity() == Verdict.SEVERITY_OK);
	}

	@SuppressWarnings("serial")
	@Test
	public void runtimeValidLogOracleFile() {
		// The log file exists
		Assert.assertTrue(new File(logOracleFile).exists());

		// TESTAR is configured to detect possible suspiciousMessages
		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(LogOracleRegex, ".*([Ee]xception|[Ee]rror).*"));
		tags.add(Pair.from(LogOracleFiles, new ArrayList<String>() { {add(logOracleFile);} }));
		tags.add(Pair.from(LogOracleCommands, new ArrayList<String>()));

		Settings settings = new Settings(tags, new Properties());
		LogOracle logOracle = new LogOracle(settings);
		logOracle.initialize();

		// Create a valid messages on it, simulating a runtime SUT writing while testing
		String validMessage = "This is a valid message printed by a SUT";
		addTextToFile(validMessage);
		System.out.println("runtimeValidLogOracleFile logFileContent: " + fileContent());
		Assert.assertTrue(fileContent().contains(validMessage));

		State state = Mockito.mock(State.class);
		Verdict logVerdict = logOracle.getVerdict(state);
		System.out.println("runtimeValidLogOracleFile logVerdict: " + logVerdict.info());

		// Verify that the logVerdict is OK
		Assert.assertTrue(logVerdict.severity() == Verdict.SEVERITY_OK);
	}

	@SuppressWarnings("serial")
	@Test
	public void runtimeSuspiciousLogOracleErrorMessage() {
		// The log file exists
		Assert.assertTrue(new File(logOracleFile).exists());

		// TESTAR is configured to detect possible suspiciousMessages
		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(LogOracleRegex, ".*([Ee]xception|[Ee]rror).*"));
		tags.add(Pair.from(LogOracleFiles, new ArrayList<String>() { {add(logOracleFile);} }));
		tags.add(Pair.from(LogOracleCommands, new ArrayList<String>()));

		Settings settings = new Settings(tags, new Properties());
		LogOracle logOracle = new LogOracle(settings);
		logOracle.initialize();

		// Create a suspicious messages on the log, simulating a runtime SUT writing while testing
		String suspiciousMessage = "This is a log error printed by a SUT";
		addTextToFile(suspiciousMessage);
		System.out.println("runtimeSuspiciousLogOracleErrorMessage logFileContent: " + fileContent());
		Assert.assertTrue(fileContent().contains(suspiciousMessage));

		State state = Mockito.mock(State.class);
		Verdict logVerdict = logOracle.getVerdict(state);
		System.out.println("runtimeSuspiciousLogOracleErrorMessage logVerdict: " + logVerdict.info());

		// Verify that the logVerdict detected the suspiciousMessage
		Assert.assertTrue(logVerdict.severity() == Verdict.SEVERITY_SUSPICIOUS_LOG);
		Assert.assertTrue(logVerdict.info().contains(suspiciousMessage));
	}

	private void addTextToFile(String text) {
		try (FileOutputStream fos = new FileOutputStream(logOracleFile, true)) {
			// Convert the text to bytes
			byte[] bytes = text.getBytes();

			// Write the bytes to the file and add a new line
			fos.write(bytes);
			fos.write(System.lineSeparator().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String fileContent() {
		String content = "";
		try (BufferedReader br = new BufferedReader(new FileReader(logOracleFile))) {
			String line;
			while ((line = br.readLine()) != null) {
				content = content + line;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return content;
	}

}
