package org.testar.oracle.workspace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.testar.core.alayer.Rect;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.oracle.Oracle;
import org.testar.webdriver.alayer.WdRoles;
import org.testar.webdriver.tag.WdTags;

public class TestWebVisualImageDownscalingOracle extends WorkspaceOracleTestSupport {

    @Test
    public void detectsProportionalScaleBelowFortyPercent() {
        Oracle oracle = loadWorkspaceOracle();
        State state = stateWithWidgets(imageWidget(1000L, 500L, 390L, 195L, "image.png"));

        oracle.resetApplicationStatus();
        List<Verdict> verdicts = oracle.getVerdicts(state);

        assertEquals(1, verdicts.size());
        assertEquals(Verdict.Severity.WARNING_UI_VISUAL_OR_RENDERING_FAULT.getTitle(), verdicts.get(0).verdictSeverityTitle());
        assertTrue(verdicts.get(0).info().contains("Detected image displayed below 40% of its natural size."));
        assertTrue(verdicts.get(0).info().contains("Natural size: 1000 x 500 px"));
        assertTrue(verdicts.get(0).info().contains("displayed size: 390 x 195 px"));
        assertEquals(1, verdicts.get(0).visualizer().getShapes().size());
        assertFalse(oracle.isVacuousPass());
    }

    @Test
    public void acceptsExactlyFortyPercent() {
        assertApplicableImageIsOk(imageWidget(1000L, 500L, 400L, 200L, "image.png"));
    }

    @Test
    public void acceptsScaleAboveFortyPercent() {
        assertApplicableImageIsOk(imageWidget(1000L, 500L, 500L, 250L, "image.png"));
    }

    @Test
    public void detectsWhenOnlyWidthIsBelowFortyPercent() {
        assertApplicableImageProducesWarning(imageWidget(1000L, 500L, 390L, 250L, "image.png"));
    }

    @Test
    public void detectsWhenOnlyHeightIsBelowFortyPercent() {
        assertApplicableImageProducesWarning(imageWidget(1000L, 500L, 500L, 195L, "image.png"));
    }

    @Test
    public void acceptsUpscaledImage() {
        assertApplicableImageIsOk(imageWidget(1000L, 500L, 1200L, 600L, "image.png"));
    }

    @Test
    public void ignoresSvgImages() {
        Oracle oracle = loadWorkspaceOracle();
        State state = stateWithWidgets(imageWidget(1000L, 500L, 100L, 50L, "icon.svg"));

        oracle.resetApplicationStatus();
        List<Verdict> verdicts = oracle.getVerdicts(state);

        assertEquals(1, verdicts.size());
        assertEquals(Verdict.Severity.OK.getTitle(), verdicts.get(0).verdictSeverityTitle());
        assertTrue(oracle.isVacuousPass());
    }

    private void assertApplicableImageProducesWarning(Widget widget) {
        Oracle oracle = loadWorkspaceOracle();
        State state = stateWithWidgets(widget);

        oracle.resetApplicationStatus();
        List<Verdict> verdicts = oracle.getVerdicts(state);

        assertEquals(1, verdicts.size());
        assertEquals(Verdict.Severity.WARNING_UI_VISUAL_OR_RENDERING_FAULT.getTitle(), verdicts.get(0).verdictSeverityTitle());
        assertFalse(oracle.isVacuousPass());
    }

    private void assertApplicableImageIsOk(Widget widget) {
        Oracle oracle = loadWorkspaceOracle();
        State state = stateWithWidgets(widget);

        oracle.resetApplicationStatus();
        List<Verdict> verdicts = oracle.getVerdicts(state);

        assertEquals(1, verdicts.size());
        assertEquals(Verdict.Severity.OK.getTitle(), verdicts.get(0).verdictSeverityTitle());
        assertFalse(oracle.isVacuousPass());
    }

    private Oracle loadWorkspaceOracle() {
        return loadWorkspaceOracle("WebVisualImageDownscalingOracle");
    }

    private State stateWithWidgets(Widget... widgets) {
        State state = mock(State.class);
        when(state.iterator()).thenAnswer(invocation -> List.of(widgets).iterator());
        return state;
    }

    private Widget imageWidget(long naturalWidth, long naturalHeight, long displayedWidth, long displayedHeight, String source) {
        Widget widget = mock(Widget.class);
        when(widget.get(Tags.Role, org.testar.core.alayer.Roles.Widget)).thenReturn(WdRoles.WdIMG);
        when(widget.get(WdTags.WebIsHidden, false)).thenReturn(false);
        when(widget.get(WdTags.WebSrc, "")).thenReturn(source);
        when(widget.get(WdTags.WebAlt, "")).thenReturn("Example image");
        when(widget.get(Tags.Shape, null)).thenReturn(Rect.from(10, 10, 40, 40));
        when(widget.get(WdTags.WebNaturalWidth, 0L)).thenReturn(naturalWidth);
        when(widget.get(WdTags.WebNaturalHeight, 0L)).thenReturn(naturalHeight);
        when(widget.get(WdTags.WebDisplayedWidth, 0L)).thenReturn(displayedWidth);
        when(widget.get(WdTags.WebDisplayedHeight, 0L)).thenReturn(displayedHeight);
        return widget;
    }
}
