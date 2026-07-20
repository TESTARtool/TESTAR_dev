package org.testar.oracle.workspace;

import java.util.List;

import org.junit.Test;
import org.testar.core.Assert;
import org.testar.core.alayer.Rect;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.webdriver.tag.WdTags;
import org.testar.oracle.Oracle;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class TestWebAccessibilityFontSizeOracle extends WorkspaceOracleTestSupport {

	@Test
	public void test_detection_accessibility_font_size() {
		Oracle oracle = loadWorkspaceOracle("WebAccessibilityFontSizeOracle");
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(WdTags.WebTextContent, "widgettext");
		widget.set(WdTags.WebComputedFontSize, "11px");
		widget.set(WdTags.WebIsFullOnScreen, true);
		widget.set(Tags.Shape, Rect.fromCoordinates(10, 10, 50, 50));

		// Assert the oracle verdict is WARNING_ACCESSIBILITY_FAULT
		oracle.resetApplicationStatus();
		List<Verdict> verdicts = oracle.getVerdicts(state);
		Assert.isEquals(1, verdicts.size());
		Verdict verdict = verdicts.get(0);
		Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.WARNING_ACCESSIBILITY_FAULT.getTitle()));
		Assert.isTrue(verdict.info().equals("Widget text 'widgettext' ,  is too small (11 px). Minimum recommended is 12 px."));
		Assert.isTrue(!oracle.isVacuousPass());
	}

	@Test
	public void test_undetection_accessibility_font_size() {
		Oracle oracle = loadWorkspaceOracle("WebAccessibilityFontSizeOracle");
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(WdTags.WebTextContent, "widgettext");
		widget.set(WdTags.WebComputedFontSize, "12px");
		widget.set(WdTags.WebIsFullOnScreen, true);
		widget.set(Tags.Shape, Rect.fromCoordinates(10, 10, 50, 50));

		// Assert the oracle verdict is OK
		oracle.resetApplicationStatus();
		List<Verdict> verdicts = oracle.getVerdicts(state);
		Assert.isEquals(1, verdicts.size());
		Verdict verdict = verdicts.get(0);
		Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.OK.getTitle()));
		Assert.isTrue(verdict.info().equals("No problem detected."));
		Assert.isTrue(!oracle.isVacuousPass());
	}
}
