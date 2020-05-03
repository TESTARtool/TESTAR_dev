package nl.ou.testar.ReinforcementLearning.QFunctions;

import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.StateModel.AbstractAction;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class SarsaQFunctionTest {

    private final static QFunction Q_FUNCTION = new SarsaQFunction(1.0d, 0.99d, 0.0d);

    @Test
    public void getQValue () {
        // given
        final AbstractAction previousActionUnderExecution = new AbstractAction("actionId");
        previousActionUnderExecution.getAttributes().set(RLTags.SarsaValue, 1.0d);
        final AbstractAction actionUnderExecution = new AbstractAction("actionId");
        actionUnderExecution.getAttributes().set(RLTags.SarsaValue, 2.0d);
        final double reward = 0.0d;

        // when
        double qValue = Q_FUNCTION.getQValue(previousActionUnderExecution, actionUnderExecution, reward);

        //then
        final double expectedQValue = 1.0d + 1.0d * (reward + 0.99d * (2.0d) - 1.0d);
        assertEquals(expectedQValue, qValue, 0.01d);
    }

    @Test
    public void getQValue_whenPreviouslyExecutedActionIsNull () {
        // given
        final AbstractAction actionUnderExecution = new AbstractAction("actionId");
        actionUnderExecution.getAttributes().set(RLTags.SarsaValue, 2.0d);
        final double reward = 0.0d;

        // when
        double qValue = Q_FUNCTION.getQValue(null, actionUnderExecution, reward);

        //then
        final double expectedQValue = 0.0d + 1.0d * (reward + 0.99d * (2.0d) - 0.0d);
        assertEquals(expectedQValue, qValue, 0.01d);
    }
}
