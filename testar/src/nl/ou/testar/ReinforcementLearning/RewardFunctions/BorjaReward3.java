package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import com.google.common.collect.Iterables;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;

import java.util.Set;

public class BorjaReward3 implements RewardFunction{
    
    private State previousState = null;
    //private Set<Action> previousActions;

    @Override
    public float getReward(State state, ConcreteState currentConcreteState, AbstractState currentAbstractState, Action executedAction, AbstractAction executedAbstractAction, AbstractAction selectedAction, Set<Action> actions, AbstractState previousAbstractState) {
        System.out.println(". . . . . Enfoque 3 . . . . .");
        float reward = 0f;
        
        if(previousState == null) {
            previousState = state;
        	return reward;
        }

		// Consider all widgets (widget tree)
		int numWidgetsNow = getWidgetsNum(state);
		int numWidgetsBefore = getWidgetsNum(previousState);

		System.out.println("*** numWidgetsBefore: " + numWidgetsBefore);
		System.out.println("*** numWidgetsNow: " + numWidgetsNow);

		double persistentWidgetNum = getPersistentWidgetNum(state);

		// Widget Tree difference reward		
		if (numWidgetsBefore < numWidgetsNow) {
			float persistentDecrement = (float) (0.01 * (persistentWidgetNum / numWidgetsBefore));
			float widgetDifference = (float) (numWidgetsBefore / numWidgetsNow);
			float provisionalReward = halfAssociator((greaterThanOne(persistentDecrement + widgetDifference)), 1);
			
			reward = (float) (- provisionalReward);
		} else if (numWidgetsBefore > numWidgetsNow) {
			float persistentDecrement = (float) (0.01 * (persistentWidgetNum / numWidgetsNow));
			float widgetDifference = (float) (numWidgetsNow / numWidgetsBefore);
			float provisionalReward = 1 - halfAssociator((greaterThanOne(persistentDecrement + widgetDifference)), 2);
			
			reward = (float) (- provisionalReward);
		} else {
			float persistentDecrement = (float) (persistentWidgetNum / numWidgetsNow);			
			reward = (float) (- persistentDecrement);
		}

		// Also decrement reward based on Widget Tree ZIndex

		// TODO: OriginWidget is not saved as Abstract Attribute
		// reward -= (0.01 *
		// executedAction.getAttributes().get(Tags.OriginWidget).get(Tags.ZIndex));

		System.out.println(". . . . . Provisional Reward: " + reward);
		
		if (executedAction != null) {
			reward -= (0.01 * executedAction.get(Tags.OriginWidget).get(Tags.ZIndex));
		} else {
			System.out.println("WARNING: It was not possible to get the OriginWidget");
		}
        
        previousState = state;
        //previousActions = actions;

        return reward;
    }

    /*private int getActionsNum(Set<Action> actions) {
		return actions.size();
	}

    private int getLeafWidgetsNum(State state) {
        int countLeafs = 0;
        for(Widget w : state) {
            if(w.childCount() == 0) {
                countLeafs = countLeafs + 1;
            }
        }
        return countLeafs;
    }*/
    
    private int getWidgetsNum(State state) {
        if(state == null)
            return 0;
        return Iterables.size(state);
    }
    
    private float greaterThanOne(float value) {
    	if(value > 1f) {
    		return 1f;
    	} else {
    		return value;
    	}
    }
    
    private float halfAssociator(float value, int half) {
    	float newValue = 0;
    	
    	if(half == 1) {
    		newValue = value * 0.5f;
    	} else {
    		newValue = (value * 0.5f) + 0.5f;
    	}
    	
    	return newValue;
    }
    
    private double getPersistentWidgetNum(State currentState) {
        int persistentWidgetNum = 0;

        for(Widget widget : currentState) {
            String currWidgetId = widget.get(Tags.AbstractIDCustom, "CurrentId");

            for(Widget prevWidget : previousState) {
                String prevWidgetId = prevWidget.get(Tags.AbstractIDCustom, "PrevId");
    
                if(currWidgetId.equals(prevWidgetId)) persistentWidgetNum ++;
            }
        }
        
        //double persistentDecrement = persistentWidgetNum * 0.01
        
        return persistentWidgetNum;
    }

    /*private double getPersistentDecrementOfActions(State givenState, Set<Action> actions) {
		int persistentWidgetNum = 0;

		for(Action currAct : actions) {
			String currActID = currAct.get(Tags.OriginWidget).get(Tags.AbstractIDCustom);

			for(Action prevAct : previousActions) {
				String prevActID = prevAct.get(Tags.OriginWidget).get(Tags.AbstractIDCustom);
	
				if(currActID == prevActID) persistentWidgetNum ++;
			}
		}

		double persistentDecrement = persistentWidgetNum * 0.01;
		return persistentDecrement;
	}*/
    
    @Override
    public void reset() {
        // TODO Auto-generated method stub
        
    }
}
