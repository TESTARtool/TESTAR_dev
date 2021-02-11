package nl.ou.testar.ReinforcementLearning.QFunctions;

import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.fruit.alayer.Action;

public class SarsaQFunctionTest {

    private final static QFunction Q_FUNCTION = new SarsaQFunction(1.0f, 0.99f, 0.0f);

    @Test
    public void getQValue () {
        // given
        final AbstractAction previousActionUnderExecution = new AbstractAction("actionId");
        previousActionUnderExecution.getAttributes().set(RLTags.SarsaValue, 1f);
        final AbstractAction actionUnderExecution = new AbstractAction("actionId");
        actionUnderExecution.getAttributes().set(RLTags.SarsaValue, 2f);
        final float reward = 0f;
        
        // Empty for compilation, not used
        Set<Action> actions = new HashSet<Action>();
        Set<AbstractAction> abstractActions = new HashSet<AbstractAction>();
        final AbstractState abstractState = new AbstractState("stateId", abstractActions);

        // when
        float qValue = Q_FUNCTION.getQValue(RLTags.SarsaValue, previousActionUnderExecution, actionUnderExecution, reward, abstractState, actions);

        //then
        final float expectedQValue = 1f + 1f * (reward + 0.99f * (2.0f) - 1f);
        assertEquals(expectedQValue, qValue, 0f);
    }

    @Test
    public void getQValue_whenPreviouslyExecutedActionIsNull () {
        // given
        final AbstractAction actionUnderExecution = new AbstractAction("actionId");
        actionUnderExecution.getAttributes().set(RLTags.SarsaValue, 2f);
        final float reward = 0f;
        
        // Empty for compilation, not used
        Set<Action> actions = new HashSet<Action>();
        Set<AbstractAction> abstractActions = new HashSet<AbstractAction>();
        final AbstractState abstractState = new AbstractState("stateId", abstractActions);

        // when
        float qValue = Q_FUNCTION.getQValue(RLTags.SarsaValue, null, actionUnderExecution, reward, abstractState, actions);

        //then
        final float expectedQValue = 0f + 1f * (reward + 0.99f * (2f) - 0f);
        assertEquals(expectedQValue, qValue, 0f);
    }
}
