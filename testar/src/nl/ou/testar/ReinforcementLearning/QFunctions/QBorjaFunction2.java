package nl.ou.testar.ReinforcementLearning.QFunctions;

import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.Tag;

import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;

public class QBorjaFunction2 implements QFunction {
	
	@Override
	public float getQValue(Tag<Float> rl_tag, AbstractAction previousActionUnderExecution, AbstractAction actionUnderExecution, float reward, final AbstractState currentAbstractState, final Set<Action> actions) {
        if(previousActionUnderExecution == null) {
            return 0f;
        }
	    
	    float qValue;
		float currentQValue = previousActionUnderExecution.getAttributes().get(RLTags.QBorja, 0f);
        
		if(currentQValue == 0f) {
		    previousActionUnderExecution.addAttribute(RLTags.QBorja, 1f);
            currentQValue = 1f;
		}

		qValue = greaterThanZero(currentQValue + reward);
		previousActionUnderExecution.getAttributes().set(RLTags.QBorja, qValue);

        return qValue;
	}

	private float greaterThanZero (float d) {
        if(d >= 0f) return d;
        else return 0.1f;
    }
}
