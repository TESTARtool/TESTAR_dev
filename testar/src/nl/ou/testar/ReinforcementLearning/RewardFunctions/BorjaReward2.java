package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;

public class BorjaReward2 implements RewardFunction {

    private State previousState = null;
    
	@Override
	public float getReward(State state, ConcreteState currentConcreteState, AbstractState currentAbstractState, AbstractAction selectedAbstractAction, Set<Action> actions) {
		System.out.println(". . . . . Enfoque 2 . . . . .");
		float reward = 0f;
        
        if(previousState != null) {
        	reward = -0.05f;
        }

        previousState = state;
        return reward;
	}

    @Override
    public void reset() {
        // TODO Auto-generated method stub
        
    }
}
