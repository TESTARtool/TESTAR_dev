package org.testar.oracle.web.invariants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testar.core.Assert;
import org.testar.config.ConfigTags;
import org.testar.core.Pair;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.oracle.Oracle;
import org.testar.oracle.OracleSelection;
import org.testar.config.settings.Settings;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class TestWebInvariantUnsortedSelectItems {

	private static List<Oracle> extendedOraclesList = new ArrayList<>();

	@BeforeClass
	public static void setup_web_invariant_unsorted_select_items() {
		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(ConfigTags.ExtendedOracles, "WebInvariantUnsortedSelectItems"));

		Settings settings = new Settings(tags, new Properties());

		extendedOraclesList = OracleSelection.loadExtendedOracles(settings.get(ConfigTags.ExtendedOracles));
	}

	@Test
	public void test_detection_web_invariant_unsorted_select_items_month() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, WdRoles.WdSELECT);
		widget.set(WdTags.WebId, "selectid");

		// Create a mocked static version of WdDriver
		try (MockedStatic<WdDriver> mockedStatic = Mockito.mockStatic(WdDriver.class)) {
			// Mock the static method executeScript(query)
			List<String> mockOptions = Arrays.asList("January", "March", "February");
			mockedStatic.when(() -> WdDriver.executeScript(Mockito.anyString()))
			.thenReturn(new ArrayList<>(mockOptions));

			// Assert the oracle is correctly loaded
			Assert.isTrue(extendedOraclesList.size() == 1);
			Assert.isTrue(extendedOraclesList.get(0) instanceof WebInvariantUnsortedSelectItems);

			// Assert the oracle verdict is WARNING_WEB_INVARIANT_FAULT
			List<Verdict> verdicts = extendedOraclesList.get(0).getVerdicts(state);
			Assert.isEquals(1, verdicts.size());
			Verdict verdict = verdicts.get(0);
			Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.WARNING_WEB_INVARIANT_FAULT.getTitle()));
			Assert.isTrue(verdict.info().equals("Detected Select widget 'selectid' ,  with unsorted elements!"));
		}
	}

	@Test
	public void test_detection_web_invariant_unsorted_select_items_natural() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, WdRoles.WdSELECT);
		widget.set(WdTags.WebId, "selectid");

		// Create a mocked static version of WdDriver
		try (MockedStatic<WdDriver> mockedStatic = Mockito.mockStatic(WdDriver.class)) {
			// Mock the static method executeScript(query)
			List<String> mockOptions = Arrays.asList("Banana", "Apple", "Orange");
			mockedStatic.when(() -> WdDriver.executeScript(Mockito.anyString()))
			.thenReturn(new ArrayList<>(mockOptions));

			// Assert the oracle is correctly loaded
			Assert.isTrue(extendedOraclesList.size() == 1);
			Assert.isTrue(extendedOraclesList.get(0) instanceof WebInvariantUnsortedSelectItems);

			// Assert the oracle verdict is WARNING_WEB_INVARIANT_FAULT
			List<Verdict> verdicts = extendedOraclesList.get(0).getVerdicts(state);
			Assert.isEquals(1, verdicts.size());
			Verdict verdict = verdicts.get(0);
			Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.WARNING_WEB_INVARIANT_FAULT.getTitle()));
			Assert.isTrue(verdict.info().equals("Detected Select widget 'selectid' ,  with unsorted elements!"));
		}
	}

	@Test
	public void test_detection_web_invariant_unsorted_select_items_numeric() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, WdRoles.WdSELECT);
		widget.set(WdTags.WebId, "selectid");

		// Create a mocked static version of WdDriver
		try (MockedStatic<WdDriver> mockedStatic = Mockito.mockStatic(WdDriver.class)) {
			// Mock the static method executeScript(query)
			List<String> mockOptions = Arrays.asList("12345", "12367", "123");
			mockedStatic.when(() -> WdDriver.executeScript(Mockito.anyString()))
			.thenReturn(new ArrayList<>(mockOptions));

			// Assert the oracle is correctly loaded
			Assert.isTrue(extendedOraclesList.size() == 1);
			Assert.isTrue(extendedOraclesList.get(0) instanceof WebInvariantUnsortedSelectItems);

			// Assert the oracle verdict is WARNING_WEB_INVARIANT_FAULT
			List<Verdict> verdicts = extendedOraclesList.get(0).getVerdicts(state);
			Assert.isEquals(1, verdicts.size());
			Verdict verdict = verdicts.get(0);
			Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.WARNING_WEB_INVARIANT_FAULT.getTitle()));
			Assert.isTrue(verdict.info().equals("Detected Select widget 'selectid' ,  with unsorted elements!"));
		}
	}

	@Test
	public void test_undetection_web_invariant_unsorted_select_items_month() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, WdRoles.WdSELECT);
		widget.set(WdTags.WebId, "selectid");

		// Create a mocked static version of WdDriver
		try (MockedStatic<WdDriver> mockedStatic = Mockito.mockStatic(WdDriver.class)) {
			// Mock the static method executeScript(query)
			List<String> mockOptions = Arrays.asList("January", "February", "March");
			mockedStatic.when(() -> WdDriver.executeScript(Mockito.anyString()))
			.thenReturn(new ArrayList<>(mockOptions));

			// Assert the oracle is correctly loaded
			Assert.isTrue(extendedOraclesList.size() == 1);
			Assert.isTrue(extendedOraclesList.get(0) instanceof WebInvariantUnsortedSelectItems);

			// Assert the oracle verdict is OK
			List<Verdict> verdicts = extendedOraclesList.get(0).getVerdicts(state);
			Assert.isEquals(1, verdicts.size());
			Verdict verdict = verdicts.get(0);
			Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.OK.getTitle()));
			Assert.isTrue(verdict.info().equals("No problem detected."));
		}
	}

	@Test
	public void test_undetection_web_invariant_unsorted_select_items_natural() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, WdRoles.WdSELECT);
		widget.set(WdTags.WebId, "selectid");

		// Create a mocked static version of WdDriver
		try (MockedStatic<WdDriver> mockedStatic = Mockito.mockStatic(WdDriver.class)) {
			// Mock the static method executeScript(query)
			List<String> mockOptions = Arrays.asList("Apple", "Banana", "Orange");
			mockedStatic.when(() -> WdDriver.executeScript(Mockito.anyString()))
			.thenReturn(new ArrayList<>(mockOptions));

			// Assert the oracle is correctly loaded
			Assert.isTrue(extendedOraclesList.size() == 1);
			Assert.isTrue(extendedOraclesList.get(0) instanceof WebInvariantUnsortedSelectItems);

			// Assert the oracle verdict is OK
			List<Verdict> verdicts = extendedOraclesList.get(0).getVerdicts(state);
			Assert.isEquals(1, verdicts.size());
			Verdict verdict = verdicts.get(0);
			Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.OK.getTitle()));
			Assert.isTrue(verdict.info().equals("No problem detected."));
		}
	}

	@Test
	public void test_undetection_web_invariant_unsorted_select_items_numeric() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, WdRoles.WdSELECT);
		widget.set(WdTags.WebId, "selectid");

		// Create a mocked static version of WdDriver
		try (MockedStatic<WdDriver> mockedStatic = Mockito.mockStatic(WdDriver.class)) {
			// Mock the static method executeScript(query)
			List<String> mockOptions = Arrays.asList("123", "12345", "12367");
			mockedStatic.when(() -> WdDriver.executeScript(Mockito.anyString()))
			.thenReturn(new ArrayList<>(mockOptions));

			// Assert the oracle is correctly loaded
			Assert.isTrue(extendedOraclesList.size() == 1);
			Assert.isTrue(extendedOraclesList.get(0) instanceof WebInvariantUnsortedSelectItems);

			// Assert the oracle verdict is OK
			List<Verdict> verdicts = extendedOraclesList.get(0).getVerdicts(state);
			Assert.isEquals(1, verdicts.size());
			Verdict verdict = verdicts.get(0);
			Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.OK.getTitle()));
			Assert.isTrue(verdict.info().equals("No problem detected."));
		}
	}
}
