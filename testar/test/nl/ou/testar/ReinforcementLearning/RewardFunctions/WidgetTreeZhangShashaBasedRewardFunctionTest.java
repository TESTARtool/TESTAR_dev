package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import org.fruit.alayer.Tags;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This unit test is based on an example in the article
 * "Simple Fast Algorithms for the Editing Distance Between Trees and Related Problems"
 * by Zhang AND Shasha
 * DOI: 10.1137/0218082
 */
public class WidgetTreeZhangShashaBasedRewardFunctionTest {

    final StateStub previousState = new StateStub();

    final StateStub state = new StateStub();

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
        previousState.set(Tags.Title, "f");

        // tree 2
        widgetT2A.set(Tags.Title, "a");
        widgetT2B.set(Tags.Title, "b");
        widgetT2C.set(Tags.Title, "c");
        widgetT2D.set(Tags.Title, "d");
        widgetT2E.set(Tags.Title, "e");
        state.set(Tags.Title, "f");

        // tree 1
        previousState.addChild(widgetT1D);
        widgetT1D.setParent(previousState);
        previousState.addChild(widgetT1E);
        widgetT1E.setParent(previousState);
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
        widgetT2C.addChild(widgetT2D);
        widgetT2D.setParent(widgetT2C);
        widgetT2D.addChild(widgetT2A);
        widgetT2A.setParent(widgetT2D);
        widgetT2D.addChild(widgetT2B);
        widgetT2B.setParent(widgetT2D);
    }

    @Test
    public void getReward() {
        // given
        final WidgetTreeZhangShashaBasedRewardFunction widgetTreeZhangShashaBasedRewardFunction = new WidgetTreeZhangShashaBasedRewardFunction(lrKeyrootsHelper, treeDistHelper);
        WidgetTreeZhangShashaBasedRewardFunction.previousState = previousState;

        // when
        float reward = widgetTreeZhangShashaBasedRewardFunction.getReward(state, null, null, null);

        // then
        assertEquals(2f, reward, 0.00001);
    }
}
