package org.testar.oracle.workspace;

import java.util.List;

import org.junit.Test;
import org.testar.core.Assert;
import org.testar.core.verdict.Verdict;
import org.testar.webdriver.tag.WdTags;
import org.testar.oracle.Oracle;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class TestWebInvariantNumberWithLotOfDecimals extends WorkspaceOracleTestSupport {

    @Test
    public void test_detection_web_invariant_number_lot_decimals() {
        Oracle oracle = loadWorkspaceOracle("WebInvariantNumberWithLotOfDecimals");
        StateStub state = new StateStub();
        WidgetStub widget = new WidgetStub();
        state.addChild(widget);
        widget.setParent(state);

        widget.set(WdTags.WebTextContent, "30.123\u20AC");

        // Assert the oracle verdict is WARNING_WEB_INVARIANT_FAULT
        List<Verdict> verdicts = oracle.getVerdicts(state);
        Assert.isEquals(1, verdicts.size());
        Verdict verdict = verdicts.get(0);
        Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.WARNING_WEB_INVARIANT_FAULT.getTitle()));
        Assert.isTrue(verdict.info().equals("Detected widget '30.123&euro;' ,  with 3 decimals (max: 2)!"));
    }

    @Test
    public void test_undetection_web_invariant_number_lot_decimals() {
        Oracle oracle = loadWorkspaceOracle("WebInvariantNumberWithLotOfDecimals");
        StateStub state = new StateStub();
        WidgetStub widget = new WidgetStub();
        state.addChild(widget);
        widget.setParent(state);

        widget.set(WdTags.WebTextContent, "30.12\u20AC");

        // Assert the oracle verdict is OK
        List<Verdict> verdicts = oracle.getVerdicts(state);
        Assert.isEquals(1, verdicts.size());
        Verdict verdict = verdicts.get(0);
        Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.OK.getTitle()));
        Assert.isTrue(verdict.info().equals("No problem detected."));
    }
}
