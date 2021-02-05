package nl.ou.testar.ReinforcementLearning.QFunctions;

import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.Widget;

import org.fruit.alayer.Action;
import org.fruit.alayer.Tag;

import java.util.Set;

public class QBorjaFunction3 implements QFunction {

    /**
     * Constructor
     * @param defaultQValue
     */
    public QBorjaFunction3(final float defaultQValue) {
        this.defaultQValue = defaultQValue;
    }

    @Override
    public float getQValue(Tag<Float> rl_tag, AbstractAction previouslyExecutedAction, AbstractAction actionUnderExecution, float reward, AbstractState currentAbstractState, Set<Action> actions) {
        float qValue;
		float currentQValue = previouslyExecutedAction.getAttributes().get(RLTags.QBorja, 0f);
        
		if(currentQValue == 0f) {
			previouslyExecutedAction.addAttribute(RLTags.QBorja, 1f);
            currentQValue = 1f;
		}

		qValue = greaterThanZero(currentQValue + reward);
		previouslyExecutedAction.getAttributes().set(RLTags.QBorja, qValue);

        return qValue;
    }

    private float greaterThanZero (float d) {
        if(d >= 0f) return d;
        else return 0.1f;
    }
}