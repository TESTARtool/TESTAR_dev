package nl.ou.testar.StateModel;

import nl.ou.testar.ReinforcementLearning.QFunctions.QFunction;
import nl.ou.testar.ReinforcementLearning.RewardFunctions.RewardFunction;
import nl.ou.testar.StateModel.ActionSelection.ActionSelector;
import nl.ou.testar.StateModel.Persistence.PersistenceManager;
import nl.ou.testar.StateModel.Sequence.SequenceManager;
import org.apache.commons.lang.Validate;
import org.fruit.alayer.Action;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;

import java.util.Set;

public class SarsaModelManager extends RLModelManager  implements StateModelManager {

    public SarsaModelManager(AbstractStateModel abstractStateModel, ActionSelector actionSelector, PersistenceManager persistenceManager, Set<Tag<?>> concreteStateTags, SequenceManager sequenceManager, boolean storeWidgets, RewardFunction rewardFunction, QFunction qFunction, Tag<?> tag) {
        super(abstractStateModel, actionSelector, persistenceManager, concreteStateTags, sequenceManager, storeWidgets, rewardFunction, qFunction, tag);
    }

    @Override
    public Action getAbstractActionToExecute(final Set<Action> actions) {
        logger.info("Number of actions available:{}", actions.size());
        final Action selectedAction = super.getAbstractActionToExecute(actions);
        logger.info("Action selected:{}", selectedAction == null ? null :selectedAction.toShortString());
        updateQValue(selectedAction, actions);
        return selectedAction;
    }

    /**
     * Update the Q-value for an {@link Action}
     *
     * @param selectedAction, can be null
     */
    private void updateQValue(final Action selectedAction, final Set<Action> actions) {
        try {
            // validate input
            Validate.notNull(selectedAction, "No action was found to execute");

            // get abstract action which is used in the reward and QFunction
            final AbstractAction selectedAbstractAction = currentAbstractState.getAction(selectedAction.get(Tags.AbstractIDCustom, ""));

            updateQValue(selectedAbstractAction, actions);

            // set previousActionUnderExecute to current abstractActionToExecute for the next iteration
            previouslyExecutedAbstractAction = selectedAbstractAction;
            previouslyExecutedTestarAction = selectedAction;
        } catch (final Exception e) {
            logger.debug("Update of Q-value failed because: '{}'", e.getMessage());
        }
    }
}
