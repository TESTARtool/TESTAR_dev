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
    
    private State previousState;
    private Set<Action> previousActions;

    @Override
    public float getReward(State state, ConcreteState currentConcreteState, AbstractState currentAbstractState, AbstractAction executedAction, Set<Action> actions) {
        float reward = 0f;
        
        // Consider all widgets (widget tree)
        int numWidgetsNow = getWidgetsNum(state);
		int numWidgetsBefore = getWidgetsNum(previousState);
				
		System.out.println("*** numWidgetsBefore: " + numWidgetsBefore);
		System.out.println("*** numWidgetsNow: " + numWidgetsNow);

        if(numWidgetsBefore > 0) {
			double persistentDecrement = getPersistentDecrementOfWidgets(state);
			double numWidgetsBeforeDouble = numWidgetsBefore;
			double numWidgetsNowDouble = numWidgetsNow;
			
			// Widget Tree difference reward
			if(numWidgetsBefore < numWidgetsNow) {
				reward = (float)((- persistentDecrement) + ((numWidgetsNowDouble - numWidgetsBeforeDouble) / numWidgetsBeforeDouble));
			} else if(numWidgetsBefore > numWidgetsNow) {
				reward = (float)((- persistentDecrement) - (numWidgetsNowDouble / numWidgetsBeforeDouble));
			} else {
			    reward = (float)(- persistentDecrement);
			}

			// Also decrement reward based on Widget Tree ZIndex

			// TODO: OriginWidget is not saved as Abstract Attribute
			//reward -= (0.01 * executedAction.getAttributes().get(Tags.OriginWidget).get(Tags.ZIndex));

			Action desiredAction = null;
			for(Action a : actions) {
			    if(a.get(Tags.AbstractIDCustom).equals(executedAction.getActionId())) {
			        desiredAction = a;
			        break;
			    }
			}
			if(desiredAction != null){
			    reward -= (0.01 * desiredAction.get(Tags.OriginWidget).get(Tags.ZIndex));
			} else {
			    System.out.println("WARNING: It was not possible to get the OriginWidget");
			}

        }
        
        previousState = state;
        previousActions = actions;

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
    
    private double getPersistentDecrementOfWidgets(State currentState) {
        int persistentWidgetNum = 0;

        for(Widget widget : currentState) {
            String currWidgetId = widget.get(Tags.AbstractIDCustom, "CurrentId");

            for(Widget prevWidget : previousState) {
                String prevWidgetId = prevWidget.get(Tags.AbstractIDCustom, "PrevId");
    
                if(currWidgetId.equals(prevWidgetId)) persistentWidgetNum ++;
            }
        }

        double persistentDecrement = persistentWidgetNum * 0.01;
        return persistentDecrement;
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
