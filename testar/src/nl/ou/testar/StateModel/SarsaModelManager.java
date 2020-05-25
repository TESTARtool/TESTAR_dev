package nl.ou.testar.StateModel;

import nl.ou.testar.ReinforcementLearning.QFunctions.QFunction;
import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.ReinforcementLearning.RewardFunctions.RewardFunction;
import nl.ou.testar.StateModel.ActionSelection.ActionSelector;
import nl.ou.testar.StateModel.Persistence.PersistenceManager;
import nl.ou.testar.StateModel.Sequence.SequenceManager;
import org.apache.commons.lang.Validate;
import org.fruit.alayer.Action;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;

import java.util.Set;

/**
 * Implementation of the {@link StateModelManager} for use of Sarsa.
 * Sarsa is a reinforcement learning (Artificial Intelligence) algorithm
 * for (sequential) action selection.
 */
public class SarsaModelManager extends ModelManager implements StateModelManager {

    /** The previously executed {@link AbstractAction} */
    private AbstractAction previousAbstractActionToExecute = null;

    /*  The {@Link RewardFunction} determines the reward or penalty for executing an {@link AbstractAction}
    *  The reward is used in the {@link QFunction}
    * */
    private final RewardFunction rewardFunction;

    /** {@link QFunction} or Quality function determines the desirability of an {@link AbstractAction} */
    private final QFunction qFunction;

    /**
     * Constructor
     *
     */
    public SarsaModelManager(final AbstractStateModel abstractStateModel,
                             final ActionSelector actionSelector,
                             final PersistenceManager persistenceManager,
                             final Set<Tag<?>> concreteStateTags,
                             final SequenceManager sequenceManager,
                             final boolean storeWidgets,
                             final RewardFunction rewardFunction,
                             final QFunction qFunction) {
        super(abstractStateModel, actionSelector, persistenceManager, concreteStateTags, sequenceManager, storeWidgets);
        this.rewardFunction = rewardFunction;
        this.qFunction = qFunction;
    }

    /**
     * Gets an {@link Action} to execute and updates the Q-value of the previously executed {@link Action}
     */
    @Override
    public Action getAbstractActionToExecute(final Set<Action> actions) {
        final Action selectedAction = super.getAbstractActionToExecute(actions);
        updateQValue(selectedAction);
        return selectedAction;
    }

    /**
     * Update the Q-value for an {@link Action}
     *
     * @param selectedAction, can be null
     */
    private void updateQValue(final Action selectedAction) {
        try {
            // validate input
            Validate.notNull(selectedAction, "No action was found to execute");

            // get abstract action which is used in the reward and QFunction
            final AbstractAction selectedAbstractAction = currentAbstractState.getAction(selectedAction.get(Tags.AbstractIDCustom, ""));

            // get reward and Q-value
            float reward = rewardFunction.getReward(getCurrentConcreteState(), currentAbstractState, selectedAbstractAction);
            final double qValue = qFunction.getQValue(previousAbstractActionToExecute, selectedAbstractAction, reward);

            // set attribute for saving in the graph database
            selectedAbstractAction.addAttribute(RLTags.SarsaValue, (float) qValue);

            // set previousActionUnderExecute to current abstractActionToExecute for the next iteration
            previousAbstractActionToExecute = selectedAbstractAction;
        } catch (final Exception e) {
            System.out.println(String.format("Update of Q-value failed because: '%s'", e.getMessage()));
        }
    }
}
