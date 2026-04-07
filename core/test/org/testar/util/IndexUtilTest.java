package org.testar.util;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.testar.monkey.alayer.Tags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class IndexUtilTest {

    @Test
    public void calculateZIndices_SingleWidget() {
        StateStub state = new StateStub();
        WidgetStub w1 = new WidgetStub();

        state.addChild(w1);
        w1.setParent(state);

        state.set(Tags.ZIndex, 3.0);
        w1.set(Tags.ZIndex, 3.0);

        IndexUtil.calculateZIndices(state);

        assertEquals(3.0, state.get(Tags.MinZIndex), 0.0);
        assertEquals(3.0, state.get(Tags.MaxZIndex), 0.0);
    }

    @Test
    public void calculateZIndices_MultipleWidgets() {
        StateStub state = new StateStub();
        WidgetStub w1 = new WidgetStub();
        WidgetStub w2 = new WidgetStub();
        WidgetStub w3 = new WidgetStub();

        state.addChild(w1);
        state.addChild(w2);
        state.addChild(w3);
        w1.setParent(state);
        w2.setParent(state);
        w3.setParent(state);

        state.set(Tags.ZIndex, 4.0);
        w1.set(Tags.ZIndex, 5.0);
        w2.set(Tags.ZIndex, -1.0);
        w3.set(Tags.ZIndex, 2.0);

        StateStub result = (StateStub) IndexUtil.calculateZIndices(state);

        assertSame(state, result);
        assertEquals(-1.0, state.get(Tags.MinZIndex), 0.0);
        assertEquals(5.0, state.get(Tags.MaxZIndex), 0.0);
    }

    @Test
    public void calculateZIndices_WidgetHasNoZIndexTag() {
        StateStub state = new StateStub();
        WidgetStub w1 = new WidgetStub();

        state.addChild(w1);
        w1.setParent(state);
        // Keep state ZIndex unset on purpose.
        w1.set(Tags.ZIndex, 1.0);

        IndexUtil.calculateZIndices(state);
        assertEquals(0.0, state.get(Tags.MinZIndex), 0.0);
        assertEquals(1.0, state.get(Tags.MaxZIndex), 0.0);
    }

    @Test(expected = NullPointerException.class)
    public void calculateZIndices_NullState() {
        IndexUtil.calculateZIndices(null);
    }
}
