package nl.ou.testar.ReinforcementLearning.QFunctions;

import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.Widget;

import org.fruit.alayer.Action;
import org.fruit.alayer.Tag;

import java.util.Set;

public class QBorjaFunction3 implements QFunction {

	private final float defaultQValue;
    private State previousState;

    /**
     * Constructor
     * @param defaultQValue
     */
    public QBorjaFunction3(final float defaultQValue) {
        this.defaultQValue = defaultQValue;
    }

    @Override
    public float getQValue(Tag<Float> rl_tag, AbstractAction previouslyExecutedAction, AbstractAction actionUnderExecution, float reward, AbstractState currentAbstractState, Set<Action> actions) {
        float qValue = reward;
		
		int numWidgetsNow = getLeafWidgetsNum(currentAbstractState);
		int numWidgetsBefore = getLeafWidgetsNum(previousState);
				
		System.out.println("*** numWidgetsBefore: " + numWidgetsBefore);
		System.out.println("*** numWidgetsNow: " + numWidgetsNow);
        
        if(previouslyExecutedAction != null) {
			double persistentDecrement = getPersistentDecrement(currentAbstractState);
			double numWidgetsBeforeDouble = numWidgetsBefore;
			double numWidgetsNowDouble = numWidgetsNow;
				
			if(numWidgetsBefore < numWidgetsNow) {
				qValue = qValue - persistentDecrement + ((numWidgetsNowDouble - numWidgetsBeforeDouble) / numWidgetsBeforeDouble);
			} else if(numWidgetsBefore > numWidgetsNow) {
				qValue = greaterThanZero((float)(qValue - persistentDecrement - (numWidgetsNowDouble / numWidgetsBeforeDouble)));
			} else {
			    qValue = greaterThanZero((float)(qValue - persistentDecrement));
			}
				
			System.out.println("... ... qValue = " + qValue);
		}

		qValue = qValue - (0.01 * previouslyExecutedAction.get(Tags.OriginWidget).get(Tags.ZIndex));
		
		previouslyExecutedAction.getAttributes().set(RLTags.QBorja, qValue);
        previousState = currentAbstractState;
        return qValue;
    }

    private float greaterThanZero (float d) {
        if(d >= 0f) return d;
        else return 0.1f;
    }

    private int getLeafWidgetsNum(State givenState) {
		Set<AbstractAction> actions = givenState.getActions();
		return actions.size();
	}

    private double getPersistentDecrement(State givenState) {
		int persistentWidgetNum = 0;

		Set<AbstractAction> actions = givenState.getActions();
		Set<AbstractAction> prevActions = previousState.getActions();

		for(Action currAction : actions) {
			String currActID = currAction.get(Tags.OriginWidget).get(Tags.AbstractIDCustom);

			for(Action prevAct : actions) {
				String prevActID = prevAct.get(Tags.OriginWidget).get(Tags.AbstractIDCustom);
	
				if(currActID == prevActID) persistentWidgetNum ++;
			}
		}

		double persistentDecrement = persistentWidgetNum * 0.01;
		return persistentDecrement;
	}
}
