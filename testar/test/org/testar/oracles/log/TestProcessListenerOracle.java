package org.testar.oracles.log;

import static org.testar.monkey.ConfigTags.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.testar.OutputStructure;
import org.testar.monkey.Pair;
import org.testar.monkey.RuntimeControlsProtocol.Modes;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.settings.Settings;

public class TestProcessListenerOracle {

	private static File tempLogDir;

	@BeforeClass
	public static void prepareProcessOutputEnv() {
		try {
			// Create a temporary directory
			tempLogDir = Files.createTempDirectory("process_listener_logs").toFile();
			tempLogDir.deleteOnExit();

			// Set the OutputStructure path to this temp directory
			OutputStructure.processListenerDir = tempLogDir.getAbsolutePath();

			// Optional: set fake values for naming
			OutputStructure.startInnerLoopDateString = "2020-01-01";
			OutputStructure.executedSUTname = "junit_app";
			OutputStructure.sequenceInnerLoopCount = 1;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Before
	public void cleanLogOracleFile() {
		if (tempLogDir != null && tempLogDir.isDirectory()) {
			for (File file : tempLogDir.listFiles()) {
				if (!file.delete()) {
					System.err.println("Warning: Failed to delete temp file: " + file.getName());
				}
			}
		}
	}

	@Test
	public void spy_mode_is_always_ok_because_is_disabled() {
		SUT system = Mockito.mock(SUT.class);
		Mockito.when(system.get(Tags.StdErr)).thenReturn(new ByteArrayInputStream("exception error".getBytes()));
		Mockito.when(system.get(Tags.StdOut)).thenReturn(new ByteArrayInputStream("exception output".getBytes()));

		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(Mode, Modes.Spy));
		tags.add(Pair.from(SUTConnector, Settings.SUT_CONNECTOR_CMDLINE));
		tags.add(Pair.from(SuspiciousProcessOutput, ".*exception.*"));
		tags.add(Pair.from(ProcessLogs, ".*.*"));
		Settings settings = new Settings(tags, new Properties());

		ProcessListenerOracle processOracle = new ProcessListenerOracle(system, settings);
		processOracle.initialize();

		State state = Mockito.mock(State.class);
		Verdict processVerdict = processOracle.getVerdict(state);

		// Verify that the processVerdict is OK because we are in spy mode
		Assert.assertTrue(processVerdict.severity() == Verdict.Severity.OK.getValue());
	}

	@Test
	public void connect_windows_title_is_always_ok_because_is_disabled() {
		SUT system = Mockito.mock(SUT.class);
		Mockito.when(system.get(Tags.StdErr)).thenReturn(new ByteArrayInputStream("exception error".getBytes()));
		Mockito.when(system.get(Tags.StdOut)).thenReturn(new ByteArrayInputStream("exception output".getBytes()));

		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(Mode, Modes.Generate));
		tags.add(Pair.from(SUTConnector, Settings.SUT_CONNECTOR_WINDOW_TITLE));
		tags.add(Pair.from(SuspiciousProcessOutput, ".*exception.*"));
		tags.add(Pair.from(ProcessLogs, ".*.*"));
		Settings settings = new Settings(tags, new Properties());

		ProcessListenerOracle processOracle = new ProcessListenerOracle(system, settings);
		processOracle.initialize();

		State state = Mockito.mock(State.class);
		Verdict processVerdict = processOracle.getVerdict(state);

		// Verify that the processVerdict is OK because we connect with title
		Assert.assertTrue(processVerdict.severity() == Verdict.Severity.OK.getValue());
	}

	@Test
	public void connect_process_name_is_always_ok_because_is_disabled() {
		SUT system = Mockito.mock(SUT.class);
		Mockito.when(system.get(Tags.StdErr)).thenReturn(new ByteArrayInputStream("exception error".getBytes()));
		Mockito.when(system.get(Tags.StdOut)).thenReturn(new ByteArrayInputStream("exception output".getBytes()));

		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(Mode, Modes.Generate));
		tags.add(Pair.from(SUTConnector, Settings.SUT_CONNECTOR_PROCESS_NAME));
		tags.add(Pair.from(SuspiciousProcessOutput, ".*exception.*"));
		tags.add(Pair.from(ProcessLogs, ".*.*"));
		Settings settings = new Settings(tags, new Properties());

		ProcessListenerOracle processOracle = new ProcessListenerOracle(system, settings);
		processOracle.initialize();

		State state = Mockito.mock(State.class);
		Verdict processVerdict = processOracle.getVerdict(state);

		// Verify that the processVerdict is OK because we connect with process name
		Assert.assertTrue(processVerdict.severity() == Verdict.Severity.OK.getValue());
	}

	@Test
	public void webdriver_is_always_ok_because_is_disabled() {
		SUT system = Mockito.mock(SUT.class);
		Mockito.when(system.get(Tags.StdErr)).thenReturn(new ByteArrayInputStream("exception error".getBytes()));
		Mockito.when(system.get(Tags.StdOut)).thenReturn(new ByteArrayInputStream("exception output".getBytes()));

		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(Mode, Modes.Generate));
		tags.add(Pair.from(SUTConnector, Settings.SUT_CONNECTOR_WEBDRIVER));
		tags.add(Pair.from(SuspiciousProcessOutput, ".*exception.*"));
		tags.add(Pair.from(ProcessLogs, ".*.*"));
		Settings settings = new Settings(tags, new Properties());

		ProcessListenerOracle processOracle = new ProcessListenerOracle(system, settings);
		processOracle.initialize();

		State state = Mockito.mock(State.class);
		Verdict processVerdict = processOracle.getVerdict(state);

		// Verify that the processVerdict is OK because we connect with web apps
		Assert.assertTrue(processVerdict.severity() == Verdict.Severity.OK.getValue());
	}

	@Test
	public void process_detection_for_error_buffer() {
		SUT system = Mockito.mock(SUT.class);
		Mockito.when(system.get(Tags.StdErr)).thenReturn(new ByteArrayInputStream("exception error".getBytes()));
		Mockito.when(system.get(Tags.StdOut)).thenReturn(new ByteArrayInputStream("".getBytes()));

		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(Mode, Modes.Generate));
		tags.add(Pair.from(SUTConnector, Settings.SUT_CONNECTOR_CMDLINE));
		tags.add(Pair.from(SuspiciousProcessOutput, ".*exception.*"));
		tags.add(Pair.from(ProcessLogs, ""));
		Settings settings = new Settings(tags, new Properties());

		ProcessListenerOracle processOracle = new ProcessListenerOracle(system, settings);
		processOracle.initialize();

		State state = Mockito.mock(State.class);
		Verdict processVerdict = processOracle.getVerdict(state);

		// Verify that the processVerdict detects an error in the error buffer
		Assert.assertTrue(processVerdict.severity() == Verdict.Severity.SUSPICIOUS_PROCESS.getValue());
		Assert.assertTrue(processVerdict.info().contains("Process Listener suspicious process: 'exception error'"));
	}

	@Test
	public void process_detection_for_output_buffer() {
		SUT system = Mockito.mock(SUT.class);
		Mockito.when(system.get(Tags.StdErr)).thenReturn(new ByteArrayInputStream("".getBytes()));
		Mockito.when(system.get(Tags.StdOut)).thenReturn(new ByteArrayInputStream("exception output".getBytes()));

		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(Mode, Modes.Generate));
		tags.add(Pair.from(SUTConnector, Settings.SUT_CONNECTOR_CMDLINE));
		tags.add(Pair.from(SuspiciousProcessOutput, ".*exception.*"));
		tags.add(Pair.from(ProcessLogs, ""));
		Settings settings = new Settings(tags, new Properties());

		ProcessListenerOracle processOracle = new ProcessListenerOracle(system, settings);
		processOracle.initialize();

		State state = Mockito.mock(State.class);
		Verdict processVerdict = processOracle.getVerdict(state);

		// Verify that the processVerdict detects an error in the output buffer
		Assert.assertTrue(processVerdict.severity() == Verdict.Severity.SUSPICIOUS_PROCESS.getValue());
		Assert.assertTrue(processVerdict.info().contains("Process Listener suspicious process: 'exception output'"));
	}

	@Test
	public void process_detection_for_both_buffers() {
		SUT system = Mockito.mock(SUT.class);
		Mockito.when(system.get(Tags.StdErr)).thenReturn(new ByteArrayInputStream("exception error".getBytes()));
		Mockito.when(system.get(Tags.StdOut)).thenReturn(new ByteArrayInputStream("exception output".getBytes()));

		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(Mode, Modes.Generate));
		tags.add(Pair.from(SUTConnector, Settings.SUT_CONNECTOR_CMDLINE));
		tags.add(Pair.from(SuspiciousProcessOutput, ".*exception.*"));
		tags.add(Pair.from(ProcessLogs, ""));
		Settings settings = new Settings(tags, new Properties());

		ProcessListenerOracle processOracle = new ProcessListenerOracle(system, settings);
		processOracle.initialize();

		State state = Mockito.mock(State.class);
		Verdict processVerdict = processOracle.getVerdict(state);

		// Verify that the processVerdict detects an error
		Assert.assertTrue(processVerdict.severity() == Verdict.Severity.SUSPICIOUS_PROCESS.getValue());
		Assert.assertTrue(processVerdict.info().contains("Process Listener suspicious process: 'exception error'"));
	}
}
