package org.testar.oracle.workspace;

import java.util.List;

import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testar.core.Assert;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.webdriver.alayer.WdRoles;
import org.testar.webdriver.state.WdDriver;
import org.testar.webdriver.tag.WdTags;
import org.testar.oracle.Oracle;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class TestWebInvariantManySelectItems extends WorkspaceOracleTestSupport {

	@Test
	public void test_detection_web_invariant_many_select_items() {
		Oracle oracle = loadWorkspaceOracle("WebInvariantManySelectItems");
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, WdRoles.WdSELECT);
		widget.set(WdTags.WebId, "selectid");

		// Create a mocked static version of WdDriver
		try (MockedStatic<WdDriver> mockedStatic = Mockito.mockStatic(WdDriver.class)) {
			// Mock the static method executeScript(query)
			mockedStatic.when(() -> WdDriver.executeScript(Mockito.anyString()))
			.thenReturn(101L);

			// Assert the oracle verdict is WARNING_WEB_INVARIANT_FAULT
			List<Verdict> verdicts = oracle.getVerdicts(state);
			Assert.isEquals(1, verdicts.size());
			Verdict verdict = verdicts.get(0);
			Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.WARNING_WEB_INVARIANT_FAULT.getTitle()));
			Assert.isTrue(verdict.info().equals("Detected Select widget 'selectid' ,  which has 101 items (threshold: 100)"));
		}
	}

	@Test
	public void test_undetection_web_invariant_many_select_items() {
		Oracle oracle = loadWorkspaceOracle("WebInvariantManySelectItems");
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, WdRoles.WdSELECT);
		widget.set(WdTags.WebId, "selectid");

		// Create a mocked static version of WdDriver
		try (MockedStatic<WdDriver> mockedStatic = Mockito.mockStatic(WdDriver.class)) {
			// Mock the static method executeScript(query)
			mockedStatic.when(() -> WdDriver.executeScript(Mockito.anyString()))
			.thenReturn(99L);

			// Assert the oracle verdict is OK
			List<Verdict> verdicts = oracle.getVerdicts(state);
			Assert.isEquals(1, verdicts.size());
			Verdict verdict = verdicts.get(0);
			Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.OK.getTitle()));
			Assert.isTrue(verdict.info().equals("No problem detected."));
		}
	}
}
