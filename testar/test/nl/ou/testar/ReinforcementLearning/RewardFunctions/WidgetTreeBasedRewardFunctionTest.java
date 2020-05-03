package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class WidgetTreeBasedRewardFunctionTest {

    public static final Set<String> NEW_ACTIONS = Collections.singleton("actionId");
    @Spy
    private WidgetTreeBasedRewardFunction rewardFunction = new WidgetTreeBasedRewardFunction();

    @Mock
    private AbstractState currentAbstractState;

    @Mock
    private AbstractAction executedAction;

    @Before
    public void setup () {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void after () {
        rewardFunction.actionsInPreviousState = new HashSet<>();
    }

    @Test
    public void getReward_whenAllAvailableActionsAreNew () {
        // given
        when(currentAbstractState.getActionIds()).thenReturn(NEW_ACTIONS);

        // when
        double reward = rewardFunction.getReward(currentAbstractState, executedAction);

        // then
        assertEquals(1d, reward, 0.01d);
    }

    @Test
    public void getReward_whenAllAvailableActionsDidNotChange () {
        // given
        rewardFunction.actionsInPreviousState = NEW_ACTIONS;
        when(currentAbstractState.getActionIds()).thenReturn(NEW_ACTIONS);

        // when
        double reward = rewardFunction.getReward(currentAbstractState, executedAction);

        // then
        assertEquals(0d, reward, 0.01d);
    }

    @Test
    public void getReward_whenHalfOfAllAvaiableActionsAreNew () {
        // given
        rewardFunction.actionsInPreviousState = NEW_ACTIONS;
        final HashSet<String> actionsInNewState = new HashSet<>();
        actionsInNewState.add("actionId");
        actionsInNewState.add("new actionId");
        when(currentAbstractState.getActionIds()).thenReturn(actionsInNewState);

        // when
        double reward = rewardFunction.getReward(currentAbstractState, executedAction);

        // then
        assertEquals(0.5d, reward, 0.01d);
    }

}