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
    
    private State previousState;

    @Override
    public float getReward(State state, ConcreteState currentConcreteState, AbstractState currentAbstractState, AbstractAction executedAction, Set<Action> actions) {
        float reward = 0f;
        int numWidgetsNow = getLeafWidgetsNum(currentAbstractState);
		int numWidgetsBefore = getLeafWidgetsNum(previousState);
				
		System.out.println("*** numWidgetsBefore: " + numWidgetsBefore);
		System.out.println("*** numWidgetsNow: " + numWidgetsNow);

        if(numWidgetsBefore > 0) {
			double persistentDecrement = getPersistentDecrement(currentAbstractState);
			double numWidgetsBeforeDouble = numWidgetsBefore;
			double numWidgetsNowDouble = numWidgetsNow;
				
			if(numWidgetsBefore < numWidgetsNow) {
				reward = (float)((- persistentDecrement) + ((numWidgetsNowDouble - numWidgetsBeforeDouble) / numWidgetsBeforeDouble));
			} else if(numWidgetsBefore > numWidgetsNow) {
				reward = (float)((- persistentDecrement) - (numWidgetsNowDouble / numWidgetsBeforeDouble));
			} else {
			    reward = (float)(- persistentDecrement);
			}

            reward -= (0.01 * executedAction.get(Tags.OriginWidget).get(Tags.ZIndex));
		}
        
        previousState = currentAbstractState;

        return reward;
    }

    private int getLeafWidgetsNum(State givenState) {
		Set<AbstractAction> actions = givenState.getActions();
		return actions.size();
	}

    private double getPersistentDecrement(State givenState) {
		int persistentWidgetNum = 0;

		Set<AbstractAction> actions = givenState.getActions();
		Set<AbstractAction> prevActions = previousState.getActions();

		for(Action currAct : actions) {
			String currActID = currAct.get(Tags.OriginWidget).get(Tags.AbstractIDCustom);

			for(Action prevAct : prevActions) {
				String prevActID = prevAct.get(Tags.OriginWidget).get(Tags.AbstractIDCustom);
	
				if(currActID == prevActID) persistentWidgetNum ++;
			}
		}

		double persistentDecrement = persistentWidgetNum * 0.01;
		return persistentDecrement;
	}
}
