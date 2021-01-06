package nl.ou.testar.ReinforcementLearning.Utils;

import nl.ou.testar.ReinforcementLearning.RewardFunctions.StateStub;
import nl.ou.testar.ReinforcementLearning.RewardFunctions.TreeDistHelper;
import nl.ou.testar.ReinforcementLearning.RewardFunctions.WidgetStub;
import org.apache.commons.collections.map.MultiKeyMap;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.junit.Before;
import org.junit.Test;

import java.util.Deque;

import static org.junit.Assert.*;

public class WidgetUtilTest {

    final StateStub previousState = new StateStub();

    final StateStub state = new StateStub();

    // tree 1
    final WidgetStub widgetT1A = new WidgetStub();
    final WidgetStub widgetT1B = new WidgetStub();
    final WidgetStub widgetT1C = new WidgetStub();
    final WidgetStub widgetT1D = new WidgetStub();
    final WidgetStub widgetT1E = new WidgetStub();
    final WidgetStub widgetT1F = new WidgetStub();

    // tree 2
    final WidgetStub widgetT2A = new WidgetStub();
    final WidgetStub widgetT2B = new WidgetStub();
    final WidgetStub widgetT2C = new WidgetStub();
    final WidgetStub widgetT2D = new WidgetStub();
    final WidgetStub widgetT2E = new WidgetStub();
    final WidgetStub widgetT2F = new WidgetStub();

    @Before
    public void setup() {
        // tree 1
        widgetT1A.set(Tags.Title, "a");
        widgetT1B.set(Tags.Title, "b");
        widgetT1C.set(Tags.Title, "c");
        widgetT1D.set(Tags.Title, "d");
        widgetT1E.set(Tags.Title, "e");
        widgetT1F.set(Tags.Title, "f");
        previousState.set(Tags.Title, "f");

        // tree 2
        widgetT2A.set(Tags.Title, "a");
        widgetT2B.set(Tags.Title, "b");
        widgetT2C.set(Tags.Title, "c");
        widgetT2D.set(Tags.Title, "d");
        widgetT2E.set(Tags.Title, "e");
        widgetT2F.set(Tags.Title, "f");
        state.set(Tags.Title, "f");

        // tree 1
        previousState.addChild(widgetT1D);
        widgetT1D.setParent(previousState);
        previousState.addChild(widgetT1E);
        widgetT1E.setParent(previousState);
        widgetT1F.addChild(widgetT1D);
        widgetT1D.setParent(widgetT1F);
        widgetT1F.addChild(widgetT1E);
        widgetT1E.setParent(widgetT1F);
        widgetT1D.addChild(widgetT1A);
        widgetT1A.setParent(widgetT1D);
        widgetT1D.addChild(widgetT1C);
        widgetT1C.setParent(widgetT1D);
        widgetT1C.addChild(widgetT1B);
        widgetT1B.setParent(widgetT1C);

        // tree 2
        state.addChild(widgetT2C);
        widgetT2C.setParent(state);
        state.addChild(widgetT2E);
        widgetT2E.setParent(state);
        widgetT2F.addChild(widgetT2C);
        widgetT2C.setParent(widgetT2F);
        widgetT2F.addChild(widgetT2E);
        widgetT2E.setParent(widgetT2F);
        widgetT2C.addChild(widgetT2D);
        widgetT2D.setParent(widgetT2C);
        widgetT2D.addChild(widgetT2A);
        widgetT2A.setParent(widgetT2D);
        widgetT2D.addChild(widgetT2B);
        widgetT2B.setParent(widgetT2D);
    }

    @Test
    public void getLeftMostArray () {
        // when
        final Deque<Widget> result = WidgetUtil.getLeftMostArray(state);

        // then
        assertEquals(widgetT2A, result.removeFirst());
        assertEquals(widgetT2D, result.removeFirst());
        assertEquals(widgetT2C, result.removeFirst());
        assertEquals(state, result.removeFirst());
    }

    @Test
    public void getEarlierNode () {
        // given
        final Deque<Widget> deque = WidgetUtil.getLeftMostArray(state);

        // when
        assertEquals(widgetT2C, WidgetUtil.getEarlierNode(state, deque));

    }

    @Test
    public void testWidgetUtil_widgetsAreEqual(){
        assertTrue(WidgetUtil.equals(widgetT1A, widgetT2A));
        assertTrue(WidgetUtil.equals(widgetT1B, widgetT2B));
        assertTrue(WidgetUtil.equals(widgetT1C, widgetT2C));
    }

    @Test
    public void testWidgetUtil_widgetsAreUnEquals(){
        assertFalse(WidgetUtil.equals(widgetT1B, widgetT2A));
    }

    @Test
    public void test_NodesT1CT2B() {
        // given
        final MultiKeyMap forestDist = new MultiKeyMap();
        final MultiKeyMap treeDist = new MultiKeyMap();

        // when
        new TreeDistHelper().treeDist(widgetT1C, widgetT2B, forestDist, treeDist);

        // then
        assertEquals(0, forestDist.get(null, null));
        assertEquals(0, forestDist.get(widgetT1B, widgetT2B));
        assertEquals(1, forestDist.get(widgetT1C, widgetT2B));
        assertEquals(2, forestDist.get(widgetT1C, null));
        assertEquals(1, forestDist.get(widgetT1B, null));
        assertEquals(1, forestDist.get(null, widgetT2B));

        assertEquals(6, forestDist.size());

        assertEquals(1, treeDist.get(widgetT1C, widgetT2B));

    }

    @Test
    public void test_NodesT1CT2E() {
        // given
        final MultiKeyMap forestDist = new MultiKeyMap();
        final MultiKeyMap treeDist = new MultiKeyMap();

        // when
        new TreeDistHelper().treeDist(widgetT1C, widgetT2E, forestDist, treeDist);

        // then
        assertEquals(0, forestDist.get(null, null));
        assertEquals(1, forestDist.get(widgetT1B, widgetT2E));
        assertEquals(2, forestDist.get(widgetT1C, widgetT2E));
        assertEquals(2, forestDist.get(widgetT1C, null));
        assertEquals(1, forestDist.get(widgetT1B, null));
        assertEquals(1, forestDist.get(null, widgetT2E));

        assertEquals(6, forestDist.size());

        assertEquals(2, treeDist.get(widgetT1C, widgetT2E));
    }

    @Test
    public void test_NodesT1ET2B() {
        // given
        final MultiKeyMap forestDist = new MultiKeyMap();
        final MultiKeyMap treeDist = new MultiKeyMap();

        // when
        new TreeDistHelper().treeDist(widgetT1E, widgetT2B, forestDist, treeDist);

        // then
        assertEquals(0, forestDist.get(null, null));
        assertEquals(1, forestDist.get(widgetT1E, widgetT2B));
        assertEquals(1, forestDist.get(widgetT1E, null));
        assertEquals(1, forestDist.get(null, widgetT2B));

        assertEquals(1, treeDist.get(widgetT1E, widgetT2B));
    }

    @Test
    public void test_NodesT1ET2E() {
        // given
        final MultiKeyMap forestDist = new MultiKeyMap();
        final MultiKeyMap treeDist = new MultiKeyMap();

        // when
        new TreeDistHelper().treeDist(widgetT1E, widgetT2E, forestDist, treeDist);

        // then
        assertEquals(0, forestDist.get(null, null));
        assertEquals(0, forestDist.get(widgetT1E, widgetT2E));
        assertEquals(1, forestDist.get(widgetT1E, null));
        assertEquals(1, forestDist.get(null, widgetT2E));

        assertEquals(0, treeDist.get(widgetT1E, widgetT2E));
    }

    @Test
    public void test_NodesT1ET2F() {
        // given
        final MultiKeyMap forestDist = new MultiKeyMap();
        final MultiKeyMap treeDist = new MultiKeyMap();

        // when
        new TreeDistHelper().treeDist(widgetT1E, state, forestDist, treeDist);

        // then
        assertEquals(0, forestDist.get(null, null));
        assertEquals(3, forestDist.get(widgetT1E, state));
        assertEquals(1, forestDist.get(widgetT1E, null));
        assertEquals(4, forestDist.get(null, state));

        assertEquals(5, treeDist.get(widgetT1E, state));
    }

    @Test
    public void test_NodesT1CT2F() {
        // given
        final MultiKeyMap forestDist = new MultiKeyMap();
        final MultiKeyMap treeDist = new MultiKeyMap();

        // when
//        new TreeDistHelper().treeDist(widgetT1C, widgetT2B, forestDist, treeDist);
//        new TreeDistHelper().treeDist(widgetT1C, widgetT2E, forestDist, treeDist);
        new TreeDistHelper().treeDist(widgetT1C, widgetT2F, forestDist, treeDist);

        // then
//        assertEquals(0, forestDist.get(null, null));
//        assertEquals(1, forestDist.get(widgetT1B, widgetT2E));
//        assertEquals(2, forestDist.get(widgetT1C, widgetT2E));
//        assertEquals(2, forestDist.get(widgetT1C, null));
//        assertEquals(1, forestDist.get(widgetT1B, null));
//        assertEquals(1, forestDist.get(null, widgetT2E));
//
//        assertEquals(2, treeDist.get(widgetT1C, widgetT2E));
//
//        assertEquals(21, forestDist.size());
        assertEquals(4, forestDist.get(widgetT1C, widgetT2F));
//        assertEquals(4, treeDist.get(widgetT1C, widgetT2F));
    }

    @Test
    public void test_NodesPreviousStateState() {
        // given
        final MultiKeyMap forestDist = new MultiKeyMap();
        final MultiKeyMap treeDist = new MultiKeyMap();

        // when
        new TreeDistHelper().treeDist(previousState, state, forestDist, treeDist);

        // then
        assertEquals(0, forestDist.get(null, null));
//        assertEquals(1, forestDist.get(widgetT1B, widgetT2E));
//        assertEquals(2, forestDist.get(widgetT1C, widgetT2E));
//        assertEquals(2, forestDist.get(widgetT1C, null));
//        assertEquals(1, forestDist.get(widgetT1B, null));
//        assertEquals(1, forestDist.get(null, widgetT2E));

        assertEquals(2, treeDist.get(previousState, state));
    }
}
