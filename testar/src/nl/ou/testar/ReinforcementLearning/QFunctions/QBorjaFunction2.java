package nl.ou.testar.ReinforcementLearning.QFunctions;

import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.Tag;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;

public class QBorjaFunction2 implements QFunction {
	
	@Override
	public float getQValue(Tag<Float> rl_tag, AbstractAction previousActionUnderExecution,
			AbstractAction actionUnderExecution, float reward, AbstractState currentAbstractState,
			Set<Action> actions) {
		
		float qValue = greaterThanZero(reward - 0.05);
		
		previouslyExecutedAction.getAttributes().set(RLTags.QBorja, qValue);
        return qValue;
	}
}
