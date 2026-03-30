package org.testar.oracle.log;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.testar.core.Assert;
import org.testar.config.ConfigTags;
import org.testar.core.verdict.Verdict;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.config.settings.Settings;
import org.testar.stub.StateStub;

public class TestBrowserConsoleVerdict {

	private StateStub state;
	private Settings settings;

	@Before
	public void settings_setup() {
		settings = new Settings();
		settings.set(ConfigTags.SuspiciousTags, "(?!x)x");
		settings.set(ConfigTags.TagsForSuspiciousOracle, Collections.singletonList("Title"));

		state = new StateStub();
	}

	@Test
	public void test_console_ok() {
		settings.set(ConfigTags.WebConsoleErrorOracle, true);
		settings.set(ConfigTags.WebConsoleErrorPattern, ".*.*");
		settings.set(ConfigTags.WebConsoleWarningOracle, true);
		settings.set(ConfigTags.WebConsoleWarningPattern, ".*.*");

		WebBrowserConsoleOracle oracle = new WebBrowserConsoleOracle(settings);
		LogEntry infoEntry = new LogEntry(Level.INFO, System.currentTimeMillis(), "this is an info message");
		LogEntries logEntries = new LogEntries(Collections.singletonList(infoEntry));

		List<Verdict> verdicts = runWithMockedLogs(oracle, logEntries);
		Assert.isEquals(1, verdicts.size());
		Verdict verdict = verdicts.get(0);

		Assert.isEquals(Verdict.OK.severity(), verdict.severity());
		Assert.isEquals("No problem detected.", verdict.info());
	}

	@Test
	public void test_console_error() {
		settings.set(ConfigTags.WebConsoleErrorOracle, true);
		settings.set(ConfigTags.WebConsoleErrorPattern, ".*.*");
		settings.set(ConfigTags.WebConsoleWarningOracle, true);
		settings.set(ConfigTags.WebConsoleWarningPattern, ".*.*");

		WebBrowserConsoleOracle oracle = new WebBrowserConsoleOracle(settings);
		LogEntry severeEntry = new LogEntry(Level.SEVERE, System.currentTimeMillis(), "some severe error occurred");
		LogEntries logEntries = new LogEntries(Collections.singletonList(severeEntry));

		List<Verdict> verdicts = runWithMockedLogs(oracle, logEntries);
		Assert.isEquals(1, verdicts.size());
		Verdict verdict = verdicts.get(0);

		Assert.isEquals(Verdict.Severity.SUSPICIOUS_LOG.getValue(), verdict.severity());
		Assert.isEquals("Web Browser Console Error: some severe error occurred", verdict.info());
	}

	@Test
	public void test_console_warning() {
		settings.set(ConfigTags.WebConsoleErrorOracle, false);
		settings.set(ConfigTags.WebConsoleErrorPattern, ".*.*");
		settings.set(ConfigTags.WebConsoleWarningOracle, true);
		settings.set(ConfigTags.WebConsoleWarningPattern, ".*.*");

		WebBrowserConsoleOracle oracle = new WebBrowserConsoleOracle(settings);
		LogEntry severeEntry = new LogEntry(Level.SEVERE, System.currentTimeMillis(), "some severe error occurred");
		LogEntry warningEntry = new LogEntry(Level.WARNING, System.currentTimeMillis(), "this is a warning message");
		LogEntries logEntries = new LogEntries(Arrays.asList(severeEntry, warningEntry));

		List<Verdict> verdicts = runWithMockedLogs(oracle, logEntries);
		Assert.isEquals(1, verdicts.size());
		Verdict verdict = verdicts.get(0);

		Assert.isEquals(Verdict.Severity.SUSPICIOUS_LOG.getValue(), verdict.severity());
		Assert.isEquals("Web Browser Console Warning: this is a warning message", verdict.info());
	}

	@Test
	public void test_console_error_and_warning() {
		settings.set(ConfigTags.WebConsoleErrorOracle, true);
		settings.set(ConfigTags.WebConsoleErrorPattern, ".*.*");
		settings.set(ConfigTags.WebConsoleWarningOracle, true);
		settings.set(ConfigTags.WebConsoleWarningPattern, ".*.*");

		WebBrowserConsoleOracle oracle = new WebBrowserConsoleOracle(settings);
		LogEntry severeEntry = new LogEntry(Level.SEVERE, System.currentTimeMillis(), "some severe error occurred");
		LogEntry warningEntry = new LogEntry(Level.WARNING, System.currentTimeMillis(), "this is a warning message");
		LogEntries logEntries = new LogEntries(Arrays.asList(severeEntry, warningEntry));

		List<Verdict> verdicts = runWithMockedLogs(oracle, logEntries);
		Assert.isEquals(2, verdicts.size());
		Assert.isEquals("Web Browser Console Error: some severe error occurred", verdicts.get(0).info());
		Assert.isEquals("Web Browser Console Warning: this is a warning message", verdicts.get(1).info());
	}

	private List<Verdict> runWithMockedLogs(WebBrowserConsoleOracle oracle, LogEntries mockedEntries) {
		try (MockedStatic<WdDriver> mockedStatic = mockStatic(WdDriver.class)) {
			mockedStatic.when(WdDriver::getBrowserLogs).thenReturn(mockedEntries);
			return oracle.getVerdicts(state);
		}
	}
}
