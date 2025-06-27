package org.testar.oracles.web.invariants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testar.monkey.Assert;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Pair;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.oracles.Oracle;
import org.testar.oracles.OracleSelection;
import org.testar.settings.Settings;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class TestWebInvariantDuplicateSelectItems {

	private static List<Oracle> extendedOraclesList = new ArrayList<>();

	@BeforeClass
	public static void setup_web_invariant_duplicate_select_items() {
		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(ConfigTags.ExtendedOracles, "WebInvariantDuplicateSelectItems"));

		Settings settings = new Settings(tags, new Properties());

		extendedOraclesList = OracleSelection.loadExtendedOracles(settings.get(ConfigTags.ExtendedOracles));
	}

	@Test
	public void test_detection_web_invariant_duplicate_select_items() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, WdRoles.WdSELECT);
		widget.set(WdTags.WebId, "selectid");

		// Create a mocked static version of WdDriver
		try (MockedStatic<WdDriver> mockedStatic = Mockito.mockStatic(WdDriver.class)) {
			// Mock the static method executeScript(query)
			List<String> mockOptions = Arrays.asList("Renault", "Volvo", "Renault");
			mockedStatic.when(() -> WdDriver.executeScript(Mockito.anyString()))
			.thenReturn(new ArrayList<>(mockOptions));

			// Assert the oracle is correctly loaded
			Assert.isTrue(extendedOraclesList.size() == 1);
			Assert.isTrue(extendedOraclesList.get(0) instanceof WebInvariantDuplicateSelectItems);

			// Assert the oracle verdict is WARNING_WEB_INVARIANT_FAULT
			Verdict verdict = extendedOraclesList.get(0).getVerdict(state);
			Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.WARNING_WEB_INVARIANT_FAULT.getTitle()));
			Assert.isTrue(verdict.info().equals("Detected Select widgets 'selectid' ,  with duplicate values!"));
		}
	}

	@Test
	public void test_undetection_web_invariant_duplicate_select_items() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, WdRoles.WdSELECT);
		widget.set(WdTags.WebId, "selectid");

		// Create a mocked static version of WdDriver
		try (MockedStatic<WdDriver> mockedStatic = Mockito.mockStatic(WdDriver.class)) {
			// Mock the static method executeScript(query)
			List<String> mockOptions = Arrays.asList("Renault", "Volvo", "Opel");
			mockedStatic.when(() -> WdDriver.executeScript(Mockito.anyString()))
			.thenReturn(new ArrayList<>(mockOptions));

			// Assert the oracle is correctly loaded
			Assert.isTrue(extendedOraclesList.size() == 1);
			Assert.isTrue(extendedOraclesList.get(0) instanceof WebInvariantDuplicateSelectItems);

			// Assert the oracle verdict is OK
			Verdict verdict = extendedOraclesList.get(0).getVerdict(state);
			Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.OK.getTitle()));
			Assert.isTrue(verdict.info().equals("No problem detected."));
		}
	}
}
