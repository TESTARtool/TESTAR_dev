package nl.ou.testar.ReinforcementLearning.QFunctions;

import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;

import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.Tag;

/**
 * Implements the default Q-function of QLearning
 */
public class QBorjaFunction4 implements QFunction {

    /**
     * {@inheritDoc}
     */
    @Override
    public float getQValue(Tag<Float> rl_tag, final AbstractAction previouslyExecutedAction, final AbstractAction selectedAbstractAction, final float reward, final AbstractState currentAbstractState, final Set<Action> actions, float vValue, Tag<Float> rl_tagB) {
        if(previouslyExecutedAction == null) {
            return 0f;
        }
        
        float currentQValue = previouslyExecutedAction.getAttributes().get(RLTags.QBorja, 0f);
        
		/*if(currentQValue == 0f) {				//QValue initialization
			previouslyExecutedAction.addAttribute(RLTags.QBorja, 1f);
            currentQValue = 1f;
		}*/

		float qValue = greaterThanZero(currentQValue + reward);

        return qValue;
    }

	private float greaterThanZero (float d) {
        if(d >= 0f) return d;
        else return 0.1f;
    }
}

