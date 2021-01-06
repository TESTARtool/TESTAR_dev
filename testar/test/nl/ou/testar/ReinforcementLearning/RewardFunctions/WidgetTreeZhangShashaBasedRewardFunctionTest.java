package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import org.fruit.alayer.State;
import org.fruit.alayer.Widget;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WidgetTreeZhangShashaBasedRewardFunctionTest {

    @Mock
    private State state;

    @Mock
    private State previousState;

    @Mock
    private LRKeyrootsHelper lrKeyrootsHelper;

    private TreeDistHelper treeDistHelper = new TreeDistHelper();

    @After
    public void cleanUp() {
        WidgetTreeZhangShashaBasedRewardFunction.treeDist.clear();
        WidgetTreeZhangShashaBasedRewardFunction.forestDist.clear();
        WidgetTreeZhangShashaBasedRewardFunction.previousState = null;
    }

    @Test
    public void widgetTreeZhangShashaBasedRewardFunction_stateOldHasOneWidget_stateTwoHasOneWidget() {
        // given
        final WidgetTreeZhangShashaBasedRewardFunction widgetTreeZhangShashaBasedRewardFunction = new WidgetTreeZhangShashaBasedRewardFunction(lrKeyrootsHelper, treeDistHelper);
        widgetTreeZhangShashaBasedRewardFunction.previousState = previousState;

        final Widget widget1 = new WidgetStub();
        final Widget widget2 = new WidgetStub();

        final Deque<Widget> widgets1 = new ArrayDeque<>();
        widgets1.add(widget1);
        final Deque<Widget> widgets2 = new ArrayDeque<>();
        widgets2.add(widget2);

        when(lrKeyrootsHelper.getLRKeyroots(state)).thenReturn(widgets1);
        when(lrKeyrootsHelper.getLRKeyroots(previousState)).thenReturn(widgets2);

        // when
        float reward = widgetTreeZhangShashaBasedRewardFunction.getReward(state, null, null, null);

        // then
        assertEquals(1f, reward, 0.00001);
        assertEquals((Integer)widgetTreeZhangShashaBasedRewardFunction.forestDist.get(null, null), 0f, 0.00001);
        assertEquals((Integer)widgetTreeZhangShashaBasedRewardFunction.forestDist.get(widget2, widget1), 1f, 0.00001);
        assertEquals((Integer)widgetTreeZhangShashaBasedRewardFunction.treeDist.get(widget2, widget1), 1f, 0.00001);
    }

    @Test
    public void widgetTreeZhangShashaBasedRewardFunction_stateOldHasNoWidget_stateTwoHasNoWidget() {
        // given
        final WidgetTreeZhangShashaBasedRewardFunction widgetTreeZhangShashaBasedRewardFunction = new WidgetTreeZhangShashaBasedRewardFunction(lrKeyrootsHelper, treeDistHelper);
        widgetTreeZhangShashaBasedRewardFunction.previousState = previousState;

        final Deque<Widget> widgets1 = new ArrayDeque<>();
        final Deque<Widget> widgets2 = new ArrayDeque<>();

        when(lrKeyrootsHelper.getLRKeyroots(state)).thenReturn(widgets1);
        when(lrKeyrootsHelper.getLRKeyroots(previousState)).thenReturn(widgets2);

        // when
        float reward = widgetTreeZhangShashaBasedRewardFunction.getReward(state, null, null, null);

        // then
        assertEquals(0f, reward, 0.00001);
        assertTrue(widgetTreeZhangShashaBasedRewardFunction.forestDist.isEmpty());
        assertTrue(widgetTreeZhangShashaBasedRewardFunction.treeDist.isEmpty());
    }

    @Test
    public void widgetTreeZhangShashaBasedRewardFunction_stateHasOneWidget_stateTwoHasAWidgetWhichIsAChildItemOfWidgetOne() {
        // given
        final WidgetTreeZhangShashaBasedRewardFunction widgetTreeZhangShashaBasedRewardFunction = new WidgetTreeZhangShashaBasedRewardFunction(lrKeyrootsHelper, treeDistHelper);
        widgetTreeZhangShashaBasedRewardFunction.previousState = previousState;

        final Widget widget2 = new WidgetStub();
        final Widget widget1 = new WidgetStub(widget2);

        final Deque<Widget> widgets1 = new ArrayDeque<>();
        widgets1.add(widget1);
        final Deque<Widget> widgets2 = new ArrayDeque<>();
        widgets2.add(widget2);

        when(lrKeyrootsHelper.getLRKeyroots(state)).thenReturn(widgets1);
        when(lrKeyrootsHelper.getLRKeyroots(previousState)).thenReturn(widgets2);

        // when
        float reward = widgetTreeZhangShashaBasedRewardFunction.getReward(state, null, null, null);

        // then
        assertEquals(1f, reward, 0.00001);
        assertEquals((Integer)widgetTreeZhangShashaBasedRewardFunction.forestDist.get(null, null), 0f, 0.00001);
        assertEquals((Integer)widgetTreeZhangShashaBasedRewardFunction.forestDist.get(widget2, widget1), 1f, 0.00001);
        assertEquals((Integer)widgetTreeZhangShashaBasedRewardFunction.treeDist.get(widget2, widget1), 1f, 0.00001);
    }

    @Test
    public void widgetTreeZhangShashaBasedRewardFunction_stateOneHasAWidgetWhichIsAChildItemOfWidgetTwo_stateTwoHasOneWidget() {
        // given
        final WidgetTreeZhangShashaBasedRewardFunction widgetTreeZhangShashaBasedRewardFunction = new WidgetTreeZhangShashaBasedRewardFunction(lrKeyrootsHelper, treeDistHelper);
        widgetTreeZhangShashaBasedRewardFunction.previousState = previousState;

        final Widget widget1 = new WidgetStub();
        final Widget widget2 = new WidgetStub(widget1);

        final Deque<Widget> widgets1 = new ArrayDeque<>();
        widgets1.add(widget1);
        final Deque<Widget> widgets2 = new ArrayDeque<>();
        widgets2.add(widget2);

        when(lrKeyrootsHelper.getLRKeyroots(state)).thenReturn(widgets1);
        when(lrKeyrootsHelper.getLRKeyroots(previousState)).thenReturn(widgets2);

        // when
        float reward = widgetTreeZhangShashaBasedRewardFunction.getReward(state, null, null, null);

        // then
        assertEquals(1f, reward, 0.00001);
        assertEquals((Integer)widgetTreeZhangShashaBasedRewardFunction.forestDist.get(null, null), 0f, 0.00001);
        assertEquals((Integer)widgetTreeZhangShashaBasedRewardFunction.forestDist.get(widget2, widget1), 1f, 0.00001);
        assertEquals((Integer)widgetTreeZhangShashaBasedRewardFunction.treeDist.get(widget2, widget1), 1f, 0.00001);
    }

}