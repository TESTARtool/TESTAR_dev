package org.testar.oracles.web.accessibility;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;
import org.testar.monkey.Assert;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Pair;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.oracles.Oracle;
import org.testar.oracles.OracleSelection;
import org.testar.settings.Settings;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class TestWebAccessibilityClickableSizeOracle {

	private static List<Oracle> extendedOraclesList = new ArrayList<>();

	@BeforeClass
	public static void setup_accessibility_clickable_size_oracle() {
		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(ConfigTags.ExtendedOracles, "Nothing,WebAccessibilityClickableSizeOracle,"));

		Settings settings = new Settings(tags, new Properties());

		extendedOraclesList = OracleSelection.loadExtendedOracles(settings.get(ConfigTags.ExtendedOracles));
	}

	@Test
	public void test_detection_accessibility_clickable_size() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, WdRoles.WdA);
		widget.set(WdTags.WebIsFullOnScreen, true);
		widget.set(Tags.Shape, Rect.fromCoordinates(10, 10, 10, 10));
		widget.set(WdTags.WebOuterHTML, "<a>link</a>");

		// Assert the oracle is correctly loaded
		Assert.isTrue(extendedOraclesList.size() == 1);
		Assert.isTrue(extendedOraclesList.get(0) instanceof WebAccessibilityClickableSizeOracle);

		// Assert the oracle verdict is WARNING_ACCESSIBILITY_FAULT
		Verdict verdict = extendedOraclesList.get(0).getVerdict(state);
		Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.WARNING_ACCESSIBILITY_FAULT.getTitle()));
		Assert.isTrue(verdict.info().equals("Clickable web widgets '&lt;a&gt;link&lt;/a&gt;' ,  are too small (Minimum: 24 px)."));
	}

	@Test
	public void test_undetection_accessibility_clickable_size() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, WdRoles.WdA);
		widget.set(WdTags.WebIsFullOnScreen, true);
		widget.set(Tags.Shape, Rect.fromCoordinates(10, 10, 34, 34));
		widget.set(WdTags.WebOuterHTML, "<a>link</a>");

		// Assert the oracle is correctly loaded
		Assert.isTrue(extendedOraclesList.size() == 1);
		Assert.isTrue(extendedOraclesList.get(0) instanceof WebAccessibilityClickableSizeOracle);

		// Assert the oracle verdict is OK
		Verdict verdict = extendedOraclesList.get(0).getVerdict(state);
		Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.OK.getTitle()));
		Assert.isTrue(verdict.info().equals("No problem detected."));
	}
}
