package org.testar.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Finder;
import org.testar.monkey.alayer.Shape;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;

public class ScreenshotUtilTest {

    @Test
    public void getStateshotBinary_NullStateThrows() {
        try {
            ScreenshotUtil.getStateshotBinary(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException expected) {
            assertEquals("State cannot be null", expected.getMessage());
        }
    }

    @Test
    public void getActionshot_NullStateThrows() {
        Action action = mock(Action.class);
        try {
            ScreenshotUtil.getActionshot(null, action);
            fail("Expected NullPointerException");
        } catch (NullPointerException expected) {
            assertEquals("State cannot be null", expected.getMessage());
        }
    }

    @Test
    public void getActionshot_NullActionThrows() {
        State state = mock(State.class);
        try {
            ScreenshotUtil.getActionshot(state, null);
            fail("Expected NullPointerException");
        } catch (NullPointerException expected) {
            assertEquals("Action cannot be null", expected.getMessage());
        }
    }

    @Test
    public void getActionshot_NoTargetsReturnsEmptyString() {
        State state = mock(State.class);
        Action action = mock(Action.class);
        when(action.get(Tags.Targets, null)).thenReturn(null);

        String result = ScreenshotUtil.getActionshot(state, action);

        assertEquals("", result);
    }

    @Test
    public void getActionshot_EmptyTargetsReturnsEmptyString() {
        State state = mock(State.class);
        Action action = mock(Action.class);
        when(action.get(Tags.Targets, null)).thenReturn(Collections.emptyList());

        String result = ScreenshotUtil.getActionshot(state, action);

        assertEquals("", result);
    }

    @Test
    public void getActionshot_TargetsWithNullShapesReturnsEmptyString() {
        State state = mock(State.class);
        Action action = mock(Action.class);
        Finder finder = mock(Finder.class);
        Widget widget = mock(Widget.class);

        when(action.get(Tags.Targets, null)).thenReturn(List.of(finder));
        when(finder.apply(state)).thenReturn(widget);
        when(widget.get(Tags.Shape, null)).thenReturn((Shape) null);

        String result = ScreenshotUtil.getActionshot(state, action);

        assertEquals("", result);
    }
}
