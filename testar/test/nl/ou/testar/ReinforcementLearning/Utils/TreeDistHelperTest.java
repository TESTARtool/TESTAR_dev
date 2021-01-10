package nl.ou.testar.ReinforcementLearning.Utils;

import nl.ou.testar.ReinforcementLearning.RewardFunctions.StateStub;
import nl.ou.testar.ReinforcementLearning.RewardFunctions.TreeDistHelper;
import nl.ou.testar.ReinforcementLearning.RewardFunctions.WidgetStub;
import org.apache.commons.collections.map.MultiKeyMap;
import org.fruit.alayer.Tags;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This unit test is based on an example in the article
 * "Simple Fast Algorithms for the Editing Distance Between Trees and Related Problems"
 * by Zhang AND Shasha
 * DOI: 10.1137/0218082
 */
public class TreeDistHelperTest {
    // tree 1
    final StateStub previousState = new StateStub();
    final WidgetStub widgetT1A = new WidgetStub();
    final WidgetStub widgetT1B = new WidgetStub();
    final WidgetStub widgetT1C = new WidgetStub();
    final WidgetStub widgetT1D = new WidgetStub();
    final WidgetStub widgetT1E = new WidgetStub();

    // tree 2
    final StateStub state = new StateStub();
    final WidgetStub widgetT2A = new WidgetStub();
    final WidgetStub widgetT2B = new WidgetStub();
    final WidgetStub widgetT2C = new WidgetStub();
    final WidgetStub widgetT2D = new WidgetStub();
    final WidgetStub widgetT2E = new WidgetStub();

    @Before
    public void setup() {
        // tree 1
        widgetT1A.set(Tags.AbstractIDCustom, "a");
        widgetT1B.set(Tags.AbstractIDCustom, "b");
        widgetT1C.set(Tags.AbstractIDCustom, "c");
        widgetT1D.set(Tags.AbstractIDCustom, "d");
        widgetT1E.set(Tags.AbstractIDCustom, "e");
        previousState.set(Tags.AbstractIDCustom, "f");

        // tree 2
        widgetT2A.set(Tags.AbstractIDCustom, "a");
        widgetT2B.set(Tags.AbstractIDCustom, "b");
        widgetT2C.set(Tags.AbstractIDCustom, "c");
        widgetT2D.set(Tags.AbstractIDCustom, "d");
        widgetT2E.set(Tags.AbstractIDCustom, "e");
        state.set(Tags.AbstractIDCustom, "f");

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
    public void treeDist_NodesT1CT2B() {
        // given
        final MultiKeyMap forestDist = new MultiKeyMap();
        final MultiKeyMap treeDist = new MultiKeyMap();

        // when
        new TreeDistHelper().treeDist(widgetT1C, widgetT2B, forestDist, treeDist);

        // then
        assertEquals(1, treeDist.get(widgetT1C, widgetT2B));
    }

    @Test
    public void treeDist_NodesT1CT2E() {
        // given
        final MultiKeyMap forestDist = new MultiKeyMap();
        final MultiKeyMap treeDist = new MultiKeyMap();

        // when
        new TreeDistHelper().treeDist(widgetT1C, widgetT2E, forestDist, treeDist);

        // then
        assertEquals(2, treeDist.get(widgetT1C, widgetT2E));
    }

    @Test
    public void treeDist_NodesT1CT2F() {
        // given
        final MultiKeyMap forestDist = new MultiKeyMap();
        final MultiKeyMap treeDist = new MultiKeyMap();

        // when
        new TreeDistHelper().treeDist(widgetT1C, state, forestDist, treeDist);

        // then
        assertEquals(4, treeDist.get(widgetT1C, state));
    }

    @Test
    public void treeDist_NodesT1ET2B() {
        // given
        final MultiKeyMap forestDist = new MultiKeyMap();
        final MultiKeyMap treeDist = new MultiKeyMap();

        // when
        new TreeDistHelper().treeDist(widgetT1E, widgetT2B, forestDist, treeDist);

        // then
        assertEquals(1, treeDist.get(widgetT1E, widgetT2B));
    }

    @Test
    public void treeDist_NodesT1ET2E() {
        // given
        final MultiKeyMap forestDist = new MultiKeyMap();
        final MultiKeyMap treeDist = new MultiKeyMap();

        // when
        new TreeDistHelper().treeDist(widgetT1E, widgetT2E, forestDist, treeDist);

        // then
        assertEquals(0, treeDist.get(widgetT1E, widgetT2E));
    }

    @Test
    public void treeDist_NodesT1ET2F() {
        // given
        final MultiKeyMap forestDist = new MultiKeyMap();
        final MultiKeyMap treeDist = new MultiKeyMap();

        // when
        new TreeDistHelper().treeDist(widgetT1E, state, forestDist, treeDist);

        // then
        assertEquals(5, treeDist.get(widgetT1E, state));
    }

    @Test
    public void treeDist_NodesT1FT2B() {
        // given
        final MultiKeyMap forestDist = new MultiKeyMap();
        final MultiKeyMap treeDist = new MultiKeyMap();

        // when
        new TreeDistHelper().treeDist(widgetT1C, widgetT2B, new MultiKeyMap(), treeDist);
        new TreeDistHelper().treeDist(widgetT1E, widgetT2B, new MultiKeyMap(), treeDist);
        new TreeDistHelper().treeDist(previousState, widgetT2B, forestDist, treeDist);

        // then
        assertEquals(5, treeDist.get(previousState, widgetT2B));
    }

    @Test
    public void treeDist_NodesT1FT2E() {
        // given
        final MultiKeyMap forestDist = new MultiKeyMap();
        final MultiKeyMap treeDist = new MultiKeyMap();

        // when
        new TreeDistHelper().treeDist(widgetT1C, widgetT2B, new MultiKeyMap(), treeDist);
        new TreeDistHelper().treeDist(widgetT1E, widgetT2B, new MultiKeyMap(), treeDist);
        new TreeDistHelper().treeDist(widgetT1C, widgetT2E, new MultiKeyMap(), treeDist);
        new TreeDistHelper().treeDist(widgetT1E, widgetT2E, new MultiKeyMap(), treeDist);
        new TreeDistHelper().treeDist(previousState, widgetT2B, new MultiKeyMap(), treeDist);
        new TreeDistHelper().treeDist(previousState, widgetT2E, forestDist, treeDist);

        // then
        assertEquals(5, treeDist.get(previousState, widgetT2E));
    }

    @Test
    public void treeDist_NodesPreviousStateState() {
        // given
        final MultiKeyMap forestDist = new MultiKeyMap();
        final MultiKeyMap treeDist = new MultiKeyMap();

        // when
        new TreeDistHelper().treeDist(widgetT1C, widgetT2B, new MultiKeyMap(), treeDist);
        new TreeDistHelper().treeDist(widgetT1E, widgetT2B, new MultiKeyMap(), treeDist);
        new TreeDistHelper().treeDist(previousState, widgetT2B, new MultiKeyMap(), treeDist);

        new TreeDistHelper().treeDist(widgetT1C, widgetT2E, new MultiKeyMap(), treeDist);
        new TreeDistHelper().treeDist(widgetT1E, widgetT2E, new MultiKeyMap(), treeDist);
        new TreeDistHelper().treeDist(previousState, widgetT2E, new MultiKeyMap(), treeDist);

        new TreeDistHelper().treeDist(widgetT1C, state, new MultiKeyMap(), treeDist);
        new TreeDistHelper().treeDist(widgetT1E, state, new MultiKeyMap(), treeDist);
        new TreeDistHelper().treeDist(previousState, state, forestDist, treeDist);

        // then
        assertEquals(2, treeDist.get(previousState, state));
    }
}
