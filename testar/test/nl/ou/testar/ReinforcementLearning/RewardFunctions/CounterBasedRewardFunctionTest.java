package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import org.fruit.alayer.TaggableBase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class CounterBasedRewardFunctionTest {

    @Spy
    private final CounterBasedRewardFunction rewardFunction = new CounterBasedRewardFunction(0f);

    @Mock
    private AbstractState currentAbstractState;

    @Mock
    private AbstractAction executedAction;

    @Spy
    private TaggableBase taggableBase;

    @Before
    public void setup () {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getReward() {
        // given
        when(executedAction.getAttributes()).thenReturn(taggableBase);
        taggableBase.set(RLTags.Counter, 0); //this is an final method, therefore it can not be mocked
        doNothing().when(taggableBase).set(eq(RLTags.Counter), anyInt());

        // when
        float reward = rewardFunction.getReward(null, null, currentAbstractState, executedAction);

        // then
        assertEquals(1f, reward , 0f);
    }

    @Test
    public void getReward_whenExecutedActionIsNull_returnDefaultReward() {
        // when
        float reward = rewardFunction.getReward(null, null, currentAbstractState, null);

        // then
        assertEquals(0f, reward , 0f);
    }

    @Test
    public void getReward_whenExecutedActionGetAttributesIsNull_returnDefaultReward() {
        // given
        when(executedAction.getAttributes()).thenReturn(null);

        // when
        float reward = rewardFunction.getReward(null, null, currentAbstractState, executedAction);

        // then
        assertEquals(0f, reward , 0f);
    }
}