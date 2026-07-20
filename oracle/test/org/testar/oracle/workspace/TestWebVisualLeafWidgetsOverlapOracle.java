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
import org.testar.webdriver.tag.WdTags;

public class TestWebVisualLeafWidgetsOverlapOracle extends WorkspaceOracleTestSupport {

    @Test
    public void detectsOverlappingVisibleLeafWidgetsFromWorkspaceOracleSource() {
        Oracle oracle = loadWorkspaceOracle();
        State state = stateWithWidgets(
                leafWidget("First", Rect.from(10, 10, 40, 40)),
                leafWidget("Second", Rect.from(25, 25, 40, 40))
        );

        oracle.resetApplicationStatus();
        List<Verdict> verdicts = oracle.getVerdicts(state);

        assertEquals(1, verdicts.size());
        assertEquals(Verdict.Severity.WARNING_UI_VISUAL_OR_RENDERING_FAULT.getTitle(), verdicts.get(0).verdictSeverityTitle());
        assertTrue(verdicts.get(0).info().contains("Two leaf widgets are overlapping."));
        assertTrue(verdicts.get(0).info().contains("First"));
        assertTrue(verdicts.get(0).info().contains("Second"));
        assertFalse(oracle.isVacuousPass());
    }

    @Test
    public void applicableNonOverlappingVisibleLeafWidgetsAreNotVacuous() {
        Oracle oracle = loadWorkspaceOracle();
        State state = stateWithWidgets(
                leafWidget("First", Rect.from(10, 10, 20, 20)),
                leafWidget("Second", Rect.from(80, 80, 20, 20))
        );

        oracle.resetApplicationStatus();
        List<Verdict> verdicts = oracle.getVerdicts(state);

        assertEquals(1, verdicts.size());
        assertEquals(Verdict.Severity.OK.getTitle(), verdicts.get(0).verdictSeverityTitle());
        assertFalse(oracle.isVacuousPass());
    }

    private Oracle loadWorkspaceOracle() {
        return loadWorkspaceOracle("WebVisualLeafWidgetsOverlapOracle");
    }

    private State stateWithWidgets(Widget... widgets) {
        State state = mock(State.class);
        when(state.iterator()).thenAnswer(invocation -> List.of(widgets).iterator());
        return state;
    }

    private Widget leafWidget(String text, Rect shape) {
        Widget widget = mock(Widget.class);
        when(widget.get(WdTags.WebIsFullOnScreen, false)).thenReturn(true);
        when(widget.get(WdTags.WebTextContent, "")).thenReturn(text);
        when(widget.get(Tags.Shape, null)).thenReturn(shape);
        when(widget.childCount()).thenReturn(0);
        return widget;
    }
}
