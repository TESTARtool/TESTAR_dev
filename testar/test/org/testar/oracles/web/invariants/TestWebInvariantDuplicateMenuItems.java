package org.testar.oracles.web.invariants;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;
import org.testar.monkey.Assert;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Pair;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.oracles.Oracle;
import org.testar.oracles.OracleSelection;
import org.testar.settings.Settings;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class TestWebInvariantDuplicateMenuItems {

	private static List<Oracle> extendedOraclesList = new ArrayList<>();

	@BeforeClass
	public static void setup_web_invariant_duplicated_menu_items() {
		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(ConfigTags.ExtendedOracles, "WebInvariantDuplicateMenuItems"));

		Settings settings = new Settings(tags, new Properties());

		extendedOraclesList = OracleSelection.loadExtendedOracles(settings.get(ConfigTags.ExtendedOracles));
	}

	@Test
	public void test_detection_web_invariant_duplicated_menu_items() {
		StateStub state = new StateStub();
		WidgetStub widgetUL = new WidgetStub();
		widgetUL.set(Tags.Role, WdRoles.WdUL);
		widgetUL.set(WdTags.WebId, "menuid");
		state.addChild(widgetUL);
		widgetUL.setParent(state);

		WidgetStub firstWidgetLI = new WidgetStub();
		firstWidgetLI.set(Tags.Role, WdRoles.WdLI);
		firstWidgetLI.set(WdTags.WebTextContent, "menu_element");
		widgetUL.addChild(firstWidgetLI);

		WidgetStub secondWidgetLI = new WidgetStub();
		secondWidgetLI.set(Tags.Role, WdRoles.WdLI);
		secondWidgetLI.set(WdTags.WebTextContent, "menu_element");
		widgetUL.addChild(secondWidgetLI);

		// Assert the oracle is correctly loaded
		Assert.isTrue(extendedOraclesList.size() == 1);
		Assert.isTrue(extendedOraclesList.get(0) instanceof WebInvariantDuplicateMenuItems);

		// Assert the oracle verdict is WARNING_WEB_INVARIANT_FAULT
		Verdict verdict = extendedOraclesList.get(0).getVerdict(state);
		Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.WARNING_WEB_INVARIANT_FAULT.getTitle()));
		Assert.isTrue(verdict.info().equals("Detected a Unnumbered List (UL) web menu 'menuid' ,  with duplicate option elements!"));
	}

	@Test
	public void test_undetection_web_invariant_duplicated_menu_items() {
		StateStub state = new StateStub();
		WidgetStub widgetUL = new WidgetStub();
		widgetUL.set(Tags.Role, WdRoles.WdUL);
		widgetUL.set(WdTags.WebId, "menuid");
		state.addChild(widgetUL);
		widgetUL.setParent(state);

		WidgetStub firstWidgetLI = new WidgetStub();
		firstWidgetLI.set(Tags.Role, WdRoles.WdLI);
		firstWidgetLI.set(WdTags.WebTextContent, "menu_element_one");
		widgetUL.addChild(firstWidgetLI);

		WidgetStub secondWidgetLI = new WidgetStub();
		secondWidgetLI.set(Tags.Role, WdRoles.WdLI);
		secondWidgetLI.set(WdTags.WebTextContent, "menu_element_two");
		widgetUL.addChild(secondWidgetLI);

		// Assert the oracle is correctly loaded
		Assert.isTrue(extendedOraclesList.size() == 1);
		Assert.isTrue(extendedOraclesList.get(0) instanceof WebInvariantDuplicateMenuItems);

		// Assert the oracle verdict is OK
		Verdict verdict = extendedOraclesList.get(0).getVerdict(state);
		Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.OK.getTitle()));
		Assert.isTrue(verdict.info().equals("No problem detected."));
	}
}
