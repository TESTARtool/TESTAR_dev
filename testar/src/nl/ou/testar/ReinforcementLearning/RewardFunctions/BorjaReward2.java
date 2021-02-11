package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;

public class BorjaReward2 implements RewardFunction {
	
	@Override
	public float getReward(State state, ConcreteState currentConcreteState, AbstractState currentAbstractState, AbstractAction executedAction, Set<Action> actions) {
		float reward = 0.05f;

        return reward;
	}

    @Override
    public void reset() {
        // TODO Auto-generated method stub
        
    }
}
