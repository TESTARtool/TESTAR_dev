package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;

public class BorjaReward2 implements RewardFunction {
	
	@Override
	public float getReward(State state, ConcreteState currentConcreteState, AbstractState currentAbstractState,
			AbstractAction executedAction, Set<Action> actions) {
		
		float totalReward = 0f;

		getLeafWidgets(state, actions);
		String thisWID = w.get(Tags.AbstractIDCustom);
			
		if(executedAction.getAttributes().get(RLTags.QBorja, 0.0) == 0.0) {
			executedAction.addAttribute(RLTags.QBorja, 1.0);
		}

		totalReward += executedAction.getAttributes().get(RLTags.QBorja, 0.0);
        return totalReward;
	}
}
