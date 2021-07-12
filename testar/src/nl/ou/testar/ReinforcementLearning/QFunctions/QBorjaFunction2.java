package nl.ou.testar.ReinforcementLearning.QFunctions;

import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;

import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.Exception.ActionNotFoundException;

public class QBorjaFunction2 implements QFunction {
	
	@Override
	public float getQValue(Tag<Float> rl_tag, AbstractAction previouslyExecutedAction, AbstractAction selectedAbstractAction, float reward, final AbstractState currentAbstractState, final Set<Action> actions, float vValue) {
        
	    // selectedAbstractAction will be null because BorjaModelManager is sending a null selectedAction
	    
	    if(previouslyExecutedAction == null) {
            return 0f;
        }
	    
        // Update Q-value with the reward
	    float currentQValue = previouslyExecutedAction.getAttributes().get(RLTags.QBorja, 0f);
        
		/*if(currentQValue == 0f) {				//QValue initialization
			previouslyExecutedAction.addAttribute(RLTags.QBorja, 1f);
            currentQValue = 1f;
		}*/

		float qValue = greaterThanZero(currentQValue + reward);
		
		/*
		// Update Q-value of actions of the same type and depth, with the new calculated Q-value
		Action previousAction = null;
		for(Action a : actions) {
		    if(a.get(Tags.AbstractIDCustom).equals(previouslyExecutedAction.getActionId())) {
		    	previousAction = a;			// Get the action to access the Role and ZIndex
		        break;
		    }
		}
		
		if(previousAction == null) {return qValue;}
		
		String previousActionType = previousAction.get(Tags.OriginWidget).get(Tags.Role).toString();
		double previousActionDepth = previousAction.get(Tags.OriginWidget).get(Tags.ZIndex);
		
		for(Action a : actions) {
			String aType = a.get(Tags.OriginWidget).get(Tags.Role).toString();
			double aDepth = a.get(Tags.OriginWidget).get(Tags.ZIndex);
			if((previousActionType == aType) && (previousActionDepth == aDepth)) {
	            //a.set(RLTags.QBorja, qValue);
				try {
                    AbstractAction abstractAction = currentAbstractState.getAction(a.get(Tags.AbstractIDCustom, "Nothing"));
                    abstractAction.addAttribute(RLTags.QBorja, qValue);
                } catch (ActionNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
			}
		}*/
		

        return qValue;
	}

	private float greaterThanZero (float d) {
        if(d >= 0f) return d;
        else return 0.1f;
    }
}
