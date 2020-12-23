package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;

public class BorjaReward2 implements RewardFunction {

	// TODO Borja: Think how to move enfoque 2 rewards implementation here
	
	@Override
	public float getReward(State state, ConcreteState currentConcreteState, AbstractState currentAbstractState,
			AbstractAction executedAction, Set<Action> actions) {
		// TODO Auto-generated method stub
		return 0;
	}

}
