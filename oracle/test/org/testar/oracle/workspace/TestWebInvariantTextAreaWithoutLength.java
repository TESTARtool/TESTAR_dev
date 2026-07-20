package org.testar.oracle.workspace;

import java.util.List;

import org.junit.Test;
import org.testar.core.Assert;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.webdriver.alayer.WdRoles;
import org.testar.webdriver.tag.WdTags;
import org.testar.oracle.Oracle;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class TestWebInvariantTextAreaWithoutLength extends WorkspaceOracleTestSupport {

	@Test
	public void test_detection_web_invariant_textarea_without_length() {
		Oracle oracle = loadWorkspaceOracle("WebInvariantTextAreaWithoutLength");
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, WdRoles.WdTEXTAREA);
		widget.set(WdTags.WebMaxLength, 0);
		widget.set(WdTags.WebOuterHTML, "<textarea maxlength=0></textarea>");

		// Assert the oracle verdict is WARNING_WEB_INVARIANT_FAULT
		List<Verdict> verdicts = oracle.getVerdicts(state);
		Assert.isEquals(1, verdicts.size());
		Verdict verdict = verdicts.get(0);
		Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.WARNING_WEB_INVARIANT_FAULT.getTitle()));
		Assert.isTrue(verdict.info().equals("Detected TextArea widget '&lt;textarea maxlength=0&gt;&lt;/textarea&gt;' ,  with 0 max length!"));
	}

	@Test
	public void test_undetection_web_invariant_textarea_without_length() {
		Oracle oracle = loadWorkspaceOracle("WebInvariantTextAreaWithoutLength");
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, WdRoles.WdTEXTAREA);
		widget.set(WdTags.WebMaxLength, 50);
		widget.set(WdTags.WebOuterHTML, "<textarea maxlength=50></textarea>");

		// Assert the oracle verdict is OK
		List<Verdict> verdicts = oracle.getVerdicts(state);
		Assert.isEquals(1, verdicts.size());
		Verdict verdict = verdicts.get(0);
		Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.OK.getTitle()));
		Assert.isTrue(verdict.info().equals("No problem detected."));
	}
}
