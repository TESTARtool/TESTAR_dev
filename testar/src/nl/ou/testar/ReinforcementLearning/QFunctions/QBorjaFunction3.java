package nl.ou.testar.ReinforcementLearning.QFunctions;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import org.fruit.alayer.Action;
import org.fruit.alayer.Tag;

import java.util.Set;

public class QBorjaFunction3 implements QFunction {
    @Override
    public float getQValue(Tag<Float> rl_tag, AbstractAction previouslyExecutedAction, AbstractAction actionUnderExecution, float reward, AbstractState currentAbstractState, Set<Action> actions) {
        float oldQValue = 0f;
        if (previouslyExecutedAction != null) {
            System.out.println("previouslyExecutedAction != null ---> True");
            System.out.println("rl_tag ---> " + rl_tag.toString());

            // TODO: Right now Q is per abstract action, not per widget, but in the original protocol Q seems to be attached to Widgets
            // TODO: What to do if no previous Q
            oldQValue = previouslyExecutedAction.getAttributes().get(rl_tag, 0f);

            System.out.println("oldQValue ---> " + Float.toString(oldQValue));
        }

        //TODO: the first case (numWidgetsBefore < numWidgetsNow) is also now under the greaterThanZero function. Does this change the result?
        return greaterThanZero(oldQValue - reward);
    }

    private float greaterThanZero (float d) {
        if(d >= 0f) return d;
        else return 0.1f;
    }
}
