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

public class TestWebInvariantDuplicateMenuItems extends WorkspaceOracleTestSupport {

	@Test
	public void test_detection_web_invariant_duplicated_menu_items() {
		Oracle oracle = loadWorkspaceOracle("WebInvariantDuplicateMenuItems");
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

		// Assert the oracle verdict is WARNING_WEB_INVARIANT_FAULT
		List<Verdict> verdicts = oracle.getVerdicts(state);
		Assert.isEquals(1, verdicts.size());
		Verdict verdict = verdicts.get(0);
		Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.WARNING_WEB_INVARIANT_FAULT.getTitle()));
		Assert.isTrue(verdict.info().equals("Detected a Unnumbered List (UL) web menu 'menuid' ,  with duplicate option elements: [menu_element]"));
	}

	@Test
	public void test_undetection_web_invariant_duplicated_menu_items() {
		Oracle oracle = loadWorkspaceOracle("WebInvariantDuplicateMenuItems");
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

		// Assert the oracle verdict is OK
		List<Verdict> verdicts = oracle.getVerdicts(state);
		Assert.isEquals(1, verdicts.size());
		Verdict verdict = verdicts.get(0);
		Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.OK.getTitle()));
		Assert.isTrue(verdict.info().equals("No problem detected."));
	}
}
