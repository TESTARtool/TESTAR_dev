package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;
import org.fruit.alayer.Tag;
import org.fruit.alayer.TaggableBase;
import org.fruit.alayer.Tags;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class WidgetTreeBasedRewardFunctionTest {
    @Spy
    private WidgetTreeBasedRewardFunction rewardFunction = new WidgetTreeBasedRewardFunction();

    @Mock
    private ConcreteState concreteState;

    @Mock
    private AbstractState currentAbstractState;

    @Mock
    private AbstractAction executedAction;

    @Mock
    private TaggableBase taggableBase;

    @Before
    public void setup () {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void after () {
        rewardFunction.attributesInPreviousState = new HashMap<>();
    }

    @Test
    public void getReward_whenAllAvailableActionsAreNew () {
        // given
        when(concreteState.getAttributes()).thenReturn(taggableBase);
        when(taggableBase.getTagValues()).thenReturn(Collections.singletonMap(Tags.ZIndex , Double.MIN_VALUE));

        // when
        float reward = rewardFunction.getReward(concreteState, currentAbstractState, executedAction);

        // then
        assertEquals(1f, reward, 0f);
    }

    @Test
    public void getReward_whenAllAvailableActionsDidNotChange () {
        // given
        final Map<Tag<?>, Object> tagValues = Collections.singletonMap(Tags.ZIndex, Double.MIN_VALUE);
        rewardFunction.attributesInPreviousState = tagValues;
        when(concreteState.getAttributes()).thenReturn(taggableBase);
        when(taggableBase.getTagValues()).thenReturn(tagValues);

        // when
        float reward = rewardFunction.getReward(concreteState, currentAbstractState, executedAction);

        // then
        assertEquals(0f, reward, 0f);
    }

    @Test
    public void getReward_whenHalfOfAllAvailableActionsAreNew () {
        // given
        final Map<Tag<?>, Object> oldTagValues = new HashMap<>();
        oldTagValues.put(Tags.ZIndex, Double.MIN_VALUE);
        oldTagValues.put(Tags.Text, "text");
        final Map<Tag<?>, Object> newTagValues = new HashMap<>();
        newTagValues.put(Tags.ZIndex, Double.MIN_VALUE);
        newTagValues.put(Tags.Text, "t");
        rewardFunction.attributesInPreviousState = oldTagValues;
        when(concreteState.getAttributes()).thenReturn(taggableBase);
        when(taggableBase.getTagValues()).thenReturn(newTagValues);

        // when
        float reward = rewardFunction.getReward(concreteState, currentAbstractState, executedAction);

        // then
        assertEquals(0.5f, reward, 0f);
    }

    @Test
    public void getReward_whenThereAreNoTags() {
        // given
        final Map<Tag<?>, Object> tagValues = new HashMap<>();
        rewardFunction.attributesInPreviousState = tagValues;
        when(concreteState.getAttributes()).thenReturn(taggableBase);
        when(taggableBase.getTagValues()).thenReturn(tagValues);

        // when
        float reward = rewardFunction.getReward(concreteState, currentAbstractState, executedAction);

        // then
        assertEquals(0f, reward, 0f);
    }

}