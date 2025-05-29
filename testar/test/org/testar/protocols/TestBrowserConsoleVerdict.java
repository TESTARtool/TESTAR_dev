package org.testar.protocols;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.testar.monkey.Assert;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.settings.Settings;
import org.testar.stub.StateStub;

public class TestBrowserConsoleVerdict extends WebdriverProtocol {

	private StateStub state;

	@Before
	public void settings_setup() {
		settings = new Settings();
		settings.set(ConfigTags.SuspiciousTags, "(?!x)x");
		settings.set(ConfigTags.TagsForSuspiciousOracle, Collections.singletonList("Title"));

		state = new StateStub();
		state.set(Tags.IsRunning, true);
		state.set(Tags.NotResponding, false);
	}

	@Test
	public void test_console_ok() {
		settings.set(ConfigTags.WebConsoleErrorOracle, true);
		settings.set(ConfigTags.WebConsoleErrorPattern, ".*.*");
		settings.set(ConfigTags.WebConsoleWarningOracle, true);
		settings.set(ConfigTags.WebConsoleWarningPattern, ".*.*");

		LogEntry infoEntry = new LogEntry(Level.INFO, System.currentTimeMillis(), "this is an info message");
		LogEntries logEntries = new LogEntries(Collections.singletonList(infoEntry));

		Verdict verdict = runWithMockedLogs(logEntries);

		Assert.isEquals(Verdict.OK.severity(), verdict.severity());
		Assert.isEquals("No problem detected.", verdict.info());
	}

	@Test
	public void test_console_error() {
		settings.set(ConfigTags.WebConsoleErrorOracle, true);
		settings.set(ConfigTags.WebConsoleErrorPattern, ".*.*");
		settings.set(ConfigTags.WebConsoleWarningOracle, true);
		settings.set(ConfigTags.WebConsoleWarningPattern, ".*.*");

		LogEntry severeEntry = new LogEntry(Level.SEVERE, System.currentTimeMillis(), "some severe error occurred");
		LogEntries logEntries = new LogEntries(Collections.singletonList(severeEntry));

		Verdict verdict = runWithMockedLogs(logEntries);

		Assert.isEquals(Verdict.Severity.SUSPICIOUS_TAG.getValue(), verdict.severity());
		Assert.isEquals("Web Browser Console Error: some severe error occurred", verdict.info());
	}

	@Test
	public void test_console_warning() {
		settings.set(ConfigTags.WebConsoleErrorOracle, false);
		settings.set(ConfigTags.WebConsoleErrorPattern, ".*.*");
		settings.set(ConfigTags.WebConsoleWarningOracle, true);
		settings.set(ConfigTags.WebConsoleWarningPattern, ".*.*");

		LogEntry severeEntry = new LogEntry(Level.SEVERE, System.currentTimeMillis(), "some severe error occurred");
		LogEntry warningEntry = new LogEntry(Level.WARNING, System.currentTimeMillis(), "this is a warning message");
		LogEntries logEntries = new LogEntries(Arrays.asList(severeEntry, warningEntry));

		Verdict verdict = runWithMockedLogs(logEntries);

		Assert.isEquals(Verdict.Severity.SUSPICIOUS_TAG.getValue(), verdict.severity());
		Assert.isEquals("Web Browser Console Warning: this is a warning message", verdict.info());
	}

	private Verdict runWithMockedLogs(LogEntries mockedEntries) {
		try (MockedStatic<WdDriver> mockedStatic = mockStatic(WdDriver.class)) {
			mockedStatic.when(WdDriver::getBrowserLogs).thenReturn(mockedEntries);
			return getVerdict(state);
		}
	}
}

