package org.testar.statemodel;

import org.testar.statemodel.actionselector.ActionSelector;
import org.testar.statemodel.exceptions.ActionNotFoundException;
import org.testar.statemodel.persistence.PersistenceManager;
import org.testar.statemodel.sequence.SequenceManager;

import nl.ou.testar.ReinforcementLearning.QFunctions.QFunction;
import nl.ou.testar.ReinforcementLearning.RewardFunctions.RewardFunction;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Tags;

import java.util.Set;

public class QLearningModelManager extends RLModelManager implements StateModelManager {

    public QLearningModelManager(AbstractStateModel abstractStateModel, ActionSelector actionSelector, PersistenceManager persistenceManager, Set<Tag<?>> concreteStateTags, SequenceManager sequenceManager, boolean storeWidgets, RewardFunction rewardFunction, QFunction qFunction, Tag<Float> tag) {
        super(abstractStateModel, actionSelector, persistenceManager, concreteStateTags, sequenceManager, storeWidgets, rewardFunction, qFunction, tag);
    }

    @Override
    public Action getAbstractActionToExecute(Set<Action> actions) {
        // First update Q, then select action
        updateQValue(null, actions);
        Action selectedAction = null;
        // set previousActionUnderExecute to current abstractActionToExecute for the next iteration
        try {
            selectedAction = super.getAbstractActionToExecute(actions);
            if(selectedAction != null) {
                previouslyExecutedAbstractAction = currentAbstractState.getAction(selectedAction.get(Tags.AbstractIDCustom, ""));
                previouslyExecutedTestarAction = selectedAction;
            }

            previousTestarActions = actions;
        }
        catch (ActionNotFoundException e){
            logger.debug("Update of previous action failed because: '{}'", e.getMessage());
        }

        return selectedAction;
    }

}
