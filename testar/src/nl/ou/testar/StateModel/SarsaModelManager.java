package nl.ou.testar.StateModel;

import nl.ou.testar.ReinforcementLearning.QFunctions.QFunction;
import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.ReinforcementLearning.RewardFunctions.RewardFunction;
import nl.ou.testar.StateModel.ActionSelection.ActionSelector;
import nl.ou.testar.StateModel.Exception.ActionNotFoundException;
import nl.ou.testar.StateModel.Persistence.PersistenceManager;
import nl.ou.testar.StateModel.Sequence.SequenceManager;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Implementation of the {@link StateModelManager} for use of Sarsa.
 * Sarsa is a reinforcement learning (Artificial Intelligence) algorithm
 * for (sequential) action selection.
 */
public class SarsaModelManager extends ModelManager implements StateModelManager {

    private static final Logger logger = LoggerFactory.getLogger(SarsaModelManager.class);

    /** The previously executed {@link AbstractAction} */
    private AbstractAction previouslySelectedAbstractAction = null;

    /**  The {@link RewardFunction} determines the reward or penalty for executing an {@link AbstractAction}
    *  The reward is used in the {@link QFunction}
    */
    private final RewardFunction rewardFunction;

    /**
     * The {@link QFunction} or Quality function determines the desirability of an {@link AbstractAction}
     */
    private final QFunction qFunction;

    private State state = null;

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

    @Override
    public void notifyNewStateReached(final State newState, final Set<Action> actions) {
        super.notifyNewStateReached(newState, actions);
        state = newState;
    }

    @Override
    public void notifyTestSequenceStopped() {
        super.notifyTestSequenceStopped();
        rewardFunction.reset();
    }

    /**
     * Gets an {@link Action} to execute and updates the Q-value of the previously executed {@link Action}
     */
    @Override
    public Action getAbstractActionToExecute(final Set<Action> actions) {
        logger.info("Number of actions available:{}", actions.size());
        final Action selectedAction = super.getAbstractActionToExecute(actions);
        logger.info("Action selected:{}", selectedAction == null ? null :selectedAction.toShortString());
        final AbstractAction selectedAbstractAction = getAbstractAction(currentAbstractState, selectedAction);
        float reward = rewardFunction.getReward(state, getCurrentConcreteState(), currentAbstractState, selectedAbstractAction);
        final double sarsaQValue = getQValue(previouslySelectedAbstractAction, reward);

        updateQValue(previouslySelectedAbstractAction, sarsaQValue);
        previouslySelectedAbstractAction = selectedAbstractAction;

        return selectedAction;
    }

    /**
     * Get the Q-value for an {@link Action}
     *
     * @param selectedAbstractAction, can be null
     * @param reward
     */
    private double getQValue(final AbstractAction selectedAbstractAction, final float reward) {
        if (selectedAbstractAction == null) {
            logger.debug("Update of Q-value failed because no action was found to execute");
        }
        return qFunction.getQValue(previouslySelectedAbstractAction, selectedAbstractAction, reward);
    }

    /**
     * Gets the {@link AbstractAction}
     * @param currentAbstractState
     * @param selectedAction
     * @return The found {@link AbstractAction} or null
     */
    private AbstractAction getAbstractAction(final AbstractState currentAbstractState, final Action selectedAction) {
        if (currentAbstractState == null || selectedAction == null) {
            return null;
        }

        try {
            return currentAbstractState.getAction(selectedAction.get(Tags.AbstractIDCustom, ""));
        } catch (final ActionNotFoundException e) {
            return null;
        }
    }

    /**
     * Update the Q-value for an {@link Action}
     *
     * @param selectedAbstractAction, can be null
     * @param qValue
     */
    private void updateQValue(final AbstractAction selectedAbstractAction, double qValue) {
        if (selectedAbstractAction == null) {
            logger.debug("Update of Q-value failed because no action was found to execute");
            return;
        }

        if (previouslySelectedAbstractAction == null) {
            logger.debug("Update of Q-value failed because no previous action was found");
            return;
        }

        previouslySelectedAbstractAction.addAttribute(RLTags.SarsaValue, (float) qValue);
    }
}
