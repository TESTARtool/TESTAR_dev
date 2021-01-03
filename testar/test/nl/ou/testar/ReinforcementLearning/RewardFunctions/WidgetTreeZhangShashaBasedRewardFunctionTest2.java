package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.ReinforcementLearning.Utils.WidgetUtil;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Deque;

import static org.junit.Assert.*;

public class WidgetTreeZhangShashaBasedRewardFunctionTest2 {

    private StateStub state = new StateStub();

    private StateStub previousState = new StateStub();

    private final LRKeyrootsHelper lrKeyrootsHelper = new LRKeyrootsHelper();

    private final TreeDistHelper treeDistHelper = new TreeDistHelper();

    // tree 1
    final WidgetStub widgetT1A = new WidgetStub();
    final WidgetStub widgetT1B = new WidgetStub();
    final WidgetStub widgetT1C = new WidgetStub();
    final WidgetStub widgetT1D = new WidgetStub();
    final WidgetStub widgetT1E = new WidgetStub();

    // tree 2
    final WidgetStub widgetT2A = new WidgetStub();
    final WidgetStub widgetT2B = new WidgetStub();
    final WidgetStub widgetT2C = new WidgetStub();
    final WidgetStub widgetT2D = new WidgetStub();
    final WidgetStub widgetT2E = new WidgetStub();

    @After
    public void cleanUp() {
        WidgetTreeZhangShashaBasedRewardFunction.treeDist.clear();
        WidgetTreeZhangShashaBasedRewardFunction.forestDist.clear();
        WidgetTreeZhangShashaBasedRewardFunction.previousState = null;
    }

    @Before
    public void setup() {
        // tree 1
        widgetT1A.set(Tags.Title, "a");
        widgetT1B.set(Tags.Title, "b");
        widgetT1C.set(Tags.Title, "c");
        widgetT1D.set(Tags.Title, "d");
        widgetT1E.set(Tags.Title, "e");

        // tree 2
        widgetT2A.set(Tags.Title, "a");
        widgetT2B.set(Tags.Title, "b");
        widgetT2C.set(Tags.Title, "c");
        widgetT2D.set(Tags.Title, "d");
        widgetT2E.set(Tags.Title, "e");

        // tree 1
        previousState.set(Tags.Title, "f");
        previousState.addChild(widgetT1D);
        previousState.addChild(widgetT1E);
        widgetT1D.addChild(widgetT1A);
        widgetT1D.addChild(widgetT1C);
        widgetT1C.addChild(widgetT1B);

        // tree 2
        state.set(Tags.Title, "f");
        state.addChild(widgetT2C);
        state.addChild(widgetT2E);
        widgetT2C.addChild(widgetT2D);
        widgetT2D.addChild(widgetT2A);
        widgetT2D.addChild(widgetT2B);
    }

    @Test
    public void lrKeyrootsHelperTestTree1() {
        final Deque<Widget> lrKeyRoots = lrKeyrootsHelper.getLRKeyroots(previousState);
        assertEquals(3, lrKeyRoots.size());
        assertEquals(widgetT1C, lrKeyRoots.pop());
        assertEquals(widgetT1E, lrKeyRoots.pop());
        assertEquals(previousState, lrKeyRoots.pop());
    }

    @Test
    public void lrKeyrootsHelperTestTree2() {
        final Deque<Widget> lrKeyRoots = lrKeyrootsHelper.getLRKeyroots(state);
        assertEquals(3, lrKeyRoots.size());
        assertEquals(widgetT2B, lrKeyRoots.pop());
        assertEquals(widgetT2E, lrKeyRoots.pop());
        assertEquals(state, lrKeyRoots.pop());
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
    public void test() {
        // given
        final WidgetTreeZhangShashaBasedRewardFunction widgetTreeZhangShashaBasedRewardFunction = new WidgetTreeZhangShashaBasedRewardFunction(lrKeyrootsHelper, treeDistHelper);
        WidgetTreeZhangShashaBasedRewardFunction.previousState = previousState;

        // when
        float reward = widgetTreeZhangShashaBasedRewardFunction.getReward(state, null, null, null);

        // then
        assertEquals(2f, reward, 0.00001);
    }
}
