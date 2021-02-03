package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import com.google.common.collect.Iterators;

import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;

import java.util.Set;

public class BorjaReward3 implements RewardFunction{

    @Override
    public float getReward(State state, ConcreteState currentConcreteState, AbstractState currentAbstractState, AbstractAction executedAction, Set<Action> actions) {
        float totalReward = 0f;
		
		if(executedAction.getAttributes().get(RLTags.QBorja, 0.0) == 0.0) {
			executedAction.addAttribute(RLTags.QBorja, 1.0);
		}
        
        totalReward += executedAction.getAttributes().get(RLTags.QBorja, 0.0);
        return totalReward;
    }
}
