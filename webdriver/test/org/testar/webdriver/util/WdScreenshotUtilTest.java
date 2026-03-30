package org.testar.webdriver.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.testar.core.action.Action;
import org.testar.core.alayer.Finder;
import org.testar.core.alayer.Rect;
import org.testar.core.alayer.Role;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.core.state.Widget;
import org.testar.core.action.ActionRoles;
import org.testar.webdriver.action.WdActionRoles;

public class WdScreenshotUtilTest {

    @Test
    public void getStateshotBinary_NullStateThrows() {
        try {
            WdScreenshotUtil.getStateshotBinary(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException expected) {
            assertEquals("State cannot be null", expected.getMessage());
        }
    }

    @Test
    public void getActionshot_NullStateThrows() {
        Action action = mock(Action.class);
        try {
            WdScreenshotUtil.getActionshot(null, action);
            fail("Expected NullPointerException");
        } catch (NullPointerException expected) {
            assertEquals("State cannot be null", expected.getMessage());
        }
    }

    @Test
    public void getActionshot_NullActionThrows() {
        State state = mock(State.class);
        try {
            WdScreenshotUtil.getActionshot(state, null);
            fail("Expected NullPointerException");
        } catch (NullPointerException expected) {
            assertEquals("Action cannot be null", expected.getMessage());
        }
    }

    @Test
    public void getActionshot_NoTargetsReturnsEmptyString() {
        State state = mock(State.class);
        Action action = mock(Action.class);
        when(action.get(Tags.Role, ActionRoles.Action)).thenReturn((Role) ActionRoles.Action);
        when(action.get(Tags.Targets, null)).thenReturn(null);

        String result = WdScreenshotUtil.getActionshot(state, action);

        assertEquals("", result);
    }

    @Test
    public void getActionshot_EmptyTargetsReturnsEmptyString() {
        State state = mock(State.class);
        Action action = mock(Action.class);
        when(action.get(Tags.Role, ActionRoles.Action)).thenReturn((Role) ActionRoles.Action);
        when(action.get(Tags.Targets, null)).thenReturn(Collections.emptyList());

        String result = WdScreenshotUtil.getActionshot(state, action);

        assertEquals("", result);
    }

    @Test
    public void getActionshot_RemoteActionWithoutOriginWidgetReturnsEmptyString() {
        State state = mock(State.class);
        Action action = mock(Action.class);

        when(action.get(Tags.Role, ActionRoles.Action)).thenReturn((Role) WdActionRoles.RemoteAction);
        when(action.get(Tags.OriginWidget)).thenReturn(null);

        String result = WdScreenshotUtil.getActionshot(state, action);

        assertEquals("", result);
    }

    @Test
    public void getActionshot_RemoteActionWithOutOfViewportShapeReturnsEmptyString() {
        State state = mock(State.class);
        Action action = mock(Action.class);
        Widget origin = mock(Widget.class);

        when(action.get(Tags.Role, ActionRoles.Action)).thenReturn((Role) WdActionRoles.RemoteAction);
        when(action.get(Tags.OriginWidget)).thenReturn(origin);
        when(origin.get(Tags.Shape)).thenReturn(Rect.from(-10, -10, 5, 5));

        String result = WdScreenshotUtil.getActionshot(state, action);

        assertEquals("", result);
    }

    @Test
    public void getActionshot_TargetShapeOutsideViewportReturnsEmptyString() {
        State state = mock(State.class);
        Action action = mock(Action.class);
        Finder finder = mock(Finder.class);
        Widget widget = mock(Widget.class);

        when(action.get(Tags.Role, ActionRoles.Action)).thenReturn((Role) ActionRoles.Action);
        when(action.get(Tags.Targets, null)).thenReturn(List.of(finder));
        when(finder.apply(state)).thenReturn(widget);
        when(widget.get(Tags.Shape)).thenReturn(Rect.from(-10, -10, 5, 5));

        String result = WdScreenshotUtil.getActionshot(state, action);

        assertEquals("", result);
    }
}
