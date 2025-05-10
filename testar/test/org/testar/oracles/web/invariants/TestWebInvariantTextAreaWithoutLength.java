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

public class TestWebInvariantTextAreaWithoutLength {

	private static List<Oracle> extendedOraclesList = new ArrayList<>();

	@BeforeClass
	public static void setup_web_invariant_textarea_without_length() {
		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(ConfigTags.ExtendedOracles, "WebInvariantTextAreaWithoutLength"));

		Settings settings = new Settings(tags, new Properties());

		extendedOraclesList = OracleSelection.loadExtendedOracles(settings.get(ConfigTags.ExtendedOracles));
	}

	@Test
	public void test_detection_web_invariant_textarea_without_length() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, WdRoles.WdTEXTAREA);
		widget.set(WdTags.WebMaxLength, 0);
		widget.set(WdTags.WebOuterHTML, "<textarea maxlength=0></textarea>");

		// Assert the oracle is correctly loaded
		Assert.isTrue(extendedOraclesList.size() == 1);
		Assert.isTrue(extendedOraclesList.get(0) instanceof WebInvariantTextAreaWithoutLength);

		// Assert the oracle verdict is WARNING_WEB_INVARIANT_FAULT
		Verdict verdict = extendedOraclesList.get(0).getVerdict(state);
		Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.WARNING_WEB_INVARIANT_FAULT.getTitle()));
		Assert.isTrue(verdict.info().equals("Detected TextArea widgets '&lt;textarea maxlength=0&gt;&lt;/textarea&gt;' ,  with 0 max length!"));
	}

	@Test
	public void test_undetection_web_invariant_textarea_without_length() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, WdRoles.WdTEXTAREA);
		widget.set(WdTags.WebMaxLength, 50);
		widget.set(WdTags.WebOuterHTML, "<textarea maxlength=50></textarea>");

		// Assert the oracle is correctly loaded
		Assert.isTrue(extendedOraclesList.size() == 1);
		Assert.isTrue(extendedOraclesList.get(0) instanceof WebInvariantTextAreaWithoutLength);

		// Assert the oracle verdict is OK
		Verdict verdict = extendedOraclesList.get(0).getVerdict(state);
		Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.OK.getTitle()));
		Assert.isTrue(verdict.info().equals("No problem detected."));
	}
}
