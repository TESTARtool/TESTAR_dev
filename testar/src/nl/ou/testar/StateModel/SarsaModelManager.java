package nl.ou.testar.StateModel;

import nl.ou.testar.ReinforcementLearning.QFunctions.QFunction;
import nl.ou.testar.ReinforcementLearning.QFunctions.SarsaQFunction;
import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.ReinforcementLearning.RewardFunctions.RewardFunction;
import nl.ou.testar.ReinforcementLearning.RewardFunctions.WidgetTreeBasedRewardFunction;
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
        return super.getAbstractActionToExecute(actions);
    }

    /**
     * This method should be called when an action is about to be executed.
     * @param action
     */
    @Override
    public void notifyActionExecution(Action action) {
        updateQValue(action);
        super.notifyActionExecution(action);
    }

    /**
     * Update the Q-value for an {@link Action}
     *
     * @param actionToExecute, can be null
     */
    private void updateQValue(final Action actionToExecute) {
        try {
            // validate input
            Validate.notNull(actionToExecute, "No action was found to execute");

            // get abstract action which is used in the reward and QFunction
            final AbstractAction abstractActionToExecute = currentAbstractState.getAction(actionToExecute.get(Tags.AbstractIDCustom, ""));

            // get reward and Q-value
            double reward = rewardFunction.getReward(currentAbstractState, abstractActionToExecute);
            final double qValue = qFunction.getQValue(previousAbstractActionToExecute, abstractActionToExecute, reward);

            // set attribute for saving in the graph database
            abstractActionToExecute.addAttribute(RLTags.SarsaValue, qValue);

            // set previousActionUnderExecute to current abstractActionToExecute for the next iteration
            previousAbstractActionToExecute = abstractActionToExecute;
        } catch (final Exception e) {
            System.out.println(String.format("Update of Q-value failed because: %s", e.getMessage()));
        }
    }

}
