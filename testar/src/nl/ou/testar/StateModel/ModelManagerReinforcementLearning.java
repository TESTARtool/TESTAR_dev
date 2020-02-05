package nl.ou.testar.StateModel;

import nl.ou.testar.ReinforcementLearning.GuiStateGraphForQlearning;
import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.ReinforcementLearning.QFunctions.QFunction;
import nl.ou.testar.ReinforcementLearning.QFunctions.QLearningQFunction;
import nl.ou.testar.ReinforcementLearning.RewardFunctions.QLearningRewardFunction;
import nl.ou.testar.ReinforcementLearning.RewardFunctions.RewardFunction;
import nl.ou.testar.StateModel.ActionSelection.ActionSelector;
import nl.ou.testar.StateModel.Exception.ActionNotFoundException;
import nl.ou.testar.StateModel.Persistence.PersistenceManager;
import nl.ou.testar.StateModel.Sequence.SequenceManager;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;

import javax.annotation.Nullable;
import java.util.Set;

public class ModelManagerReinforcementLearning extends ModelManager implements StateModelManager  {
    // TODO should this be a singleton?
    private final static GuiStateGraphForQlearning graph = new GuiStateGraphForQlearning();

    /**
     * Constructor
     *
     * @param abstractStateModel
     * @param actionSelector
     * @param persistenceManager
     * @param concreteStateTags
     * @param sequenceManager
     */
    public ModelManagerReinforcementLearning(AbstractStateModel abstractStateModel, ActionSelector actionSelector, PersistenceManager persistenceManager, Set<Tag<?>> concreteStateTags, SequenceManager sequenceManager, boolean storeWidgets) {
        super(abstractStateModel, actionSelector, persistenceManager, concreteStateTags, sequenceManager, storeWidgets);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyNewStateReached(State newState, Set<Action> actions) {
        updateQValue(newState);
        
        // execute other code
        super.notifyNewStateReached(newState, actions);
        
        for(Action a : actions)
        	getAbstractActionQValue(a);
    }

    /**
     * Update the Q-value
     * @param incomingState
     */
    private void updateQValue(@Nullable final State incomingState) {
        // validate
        final AbstractAction executedAction = actionUnderExecution;
        final AbstractState outgoingState = currentAbstractState;
        if (outgoingState == null || executedAction == null) {
            return;
        }

        final QFunction qFunction = new QLearningQFunction(graph, new QLearningRewardFunction(graph));
        double qValue = qFunction.getQValue(outgoingState, incomingState, executedAction);

        // save Q-value
        graph.saveqValue(outgoingState, executedAction, qValue);
        
        // Set RL Tag in the AbstractAction
        actionUnderExecution.addAttribute(RLTags.SarsaValue, qValue);
    }
    
    public double getAbstractActionQValue(Action action) {

    	if (this.currentAbstractState == null) {
    		// This should not happen
    		System.out.println("Abstract State is null");
    		return 0.0;
    	}
    	
    	AbstractAction absAction = null;
    	try {
    		absAction = currentAbstractState.getAction(action.get(Tags.AbstractIDCustom,""));
    	} catch (ActionNotFoundException e) {
    		// This should not happen
    		// Action does not exist in the current Abstract State
    		// Max Q-Value ?
    		System.out.println(String.format("Action: %s doesnt exists yet, returning X value", action.get(Tags.AbstractIDCustom,"")));
    		return 1.0;
    	}

    	double qValue = absAction.getAttributes().get(RLTags.SarsaValue, 0.0);

    	if(qValue != 0.0) {
    		System.out.println(String.format("Action %s has a Q-Value of %s", action.get(Tags.AbstractIDCustom,""), qValue));
    		return qValue;
    	}

    	else {
    		// Not Q-Value associated, Max Q-Value ?
    		System.out.println(String.format("Action %s has not a Q-Value associated, returning X value", action.get(Tags.AbstractIDCustom,"")));
    		return 1.0;
    	}
    }

}
