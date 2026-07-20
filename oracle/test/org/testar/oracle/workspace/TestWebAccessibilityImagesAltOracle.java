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

public class TestWebAccessibilityImagesAltOracle extends WorkspaceOracleTestSupport {

    @Test
    public void test_detection_accessibility_images_alt() {
        Oracle oracle = loadWorkspaceOracle("WebAccessibilityImagesAltOracle");
        StateStub state = new StateStub();
        WidgetStub widget = new WidgetStub();
        state.addChild(widget);
        widget.setParent(state);

        widget.set(Tags.Role, WdRoles.WdIMG);
        widget.set(WdTags.WebAlt, "");
        widget.set(WdTags.WebOuterHTML, "<img src='url'>whatever</img>");

        // Assert the oracle verdict is WARNING_ACCESSIBILITY_FAULT
        oracle.resetApplicationStatus();
        List<Verdict> verdicts = oracle.getVerdicts(state);
        Assert.isEquals(1, verdicts.size());
        Verdict verdict = verdicts.get(0);
        Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.WARNING_ACCESSIBILITY_FAULT.getTitle()));
        Assert.isTrue(verdict.info().equals("Detected web image widget '&lt;img src='url'&gt;whatever&lt;/img&gt;' ,  without alternative text!"));
        Assert.isTrue(!oracle.isVacuousPass());
    }

    @Test
    public void test_undetection_accessibility_images_alt() {
        Oracle oracle = loadWorkspaceOracle("WebAccessibilityImagesAltOracle");
        StateStub state = new StateStub();
        WidgetStub widget = new WidgetStub();
        state.addChild(widget);
        widget.setParent(state);

        widget.set(Tags.Role, WdRoles.WdIMG);
        widget.set(WdTags.WebAlt, "This is a whatever image");
        widget.set(WdTags.WebOuterHTML, "<img src='url'>whatever</img>");

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
