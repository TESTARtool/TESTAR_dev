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

public class TestWebVisualImageResolutionOracle extends WorkspaceOracleTestSupport {

    @Test
    public void detectsImageResolutionDifferenceFromWorkspaceOracleSource() {
        Oracle oracle = loadWorkspaceOracle();
        State state = stateWithWidgets(imageWidget(100L, 100L, 200L, 100L, "image.png"));

        oracle.resetApplicationStatus();
        List<Verdict> verdicts = oracle.getVerdicts(state);

        assertEquals(1, verdicts.size());
        assertEquals(Verdict.Severity.WARNING_UI_VISUAL_OR_RENDERING_FAULT.getTitle(), verdicts.get(0).verdictSeverityTitle());
        assertTrue(verdicts.get(0).info().contains("Detected image resolution difference."));
        assertTrue(verdicts.get(0).info().contains("Natural size: 100 x 100 px"));
        assertTrue(verdicts.get(0).info().contains("displayed size: 200 x 100 px"));
        assertFalse(oracle.isVacuousPass());
    }

    @Test
    public void applicableImageWithMatchingResolutionIsNotVacuous() {
        Oracle oracle = loadWorkspaceOracle();
        State state = stateWithWidgets(imageWidget(100L, 100L, 100L, 100L, "image.png"));

        oracle.resetApplicationStatus();
        List<Verdict> verdicts = oracle.getVerdicts(state);

        assertEquals(1, verdicts.size());
        assertEquals(Verdict.Severity.OK.getTitle(), verdicts.get(0).verdictSeverityTitle());
        assertFalse(oracle.isVacuousPass());
    }

    @Test
    public void ignoresSvgImages() {
        Oracle oracle = loadWorkspaceOracle();
        State state = stateWithWidgets(imageWidget(100L, 100L, 200L, 100L, "icon.svg"));

        oracle.resetApplicationStatus();
        List<Verdict> verdicts = oracle.getVerdicts(state);

        assertEquals(1, verdicts.size());
        assertEquals(Verdict.Severity.OK.getTitle(), verdicts.get(0).verdictSeverityTitle());
        assertTrue(oracle.isVacuousPass());
    }

    private Oracle loadWorkspaceOracle() {
        return loadWorkspaceOracle("WebVisualImageResolutionOracle");
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
