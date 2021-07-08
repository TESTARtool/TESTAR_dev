package nl.ou.testar.StateModel;

import nl.ou.testar.ReinforcementLearning.QFunctions.QFunction;
import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.ReinforcementLearning.RewardFunctions.RewardFunction;
import nl.ou.testar.StateModel.ActionSelection.ActionSelector;
import nl.ou.testar.StateModel.Exception.ActionNotFoundException;
import nl.ou.testar.StateModel.Persistence.PersistenceManager;
import nl.ou.testar.StateModel.Sequence.SequenceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;

import java.util.Set;

/**
 * Implementation of the {@link StateModelManager} for use of Sarsa.
 * Sarsa is a reinforcement learning (Artificial Intelligence) algorithm
 * for (sequential) action selection.
 */
public class RLModelManager extends ModelManager implements StateModelManager {

    private static final Logger logger = LogManager.getLogger(RLModelManager.class);

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

    private final Tag<?> tag;

    /**
     * Constructor
     *
     */
    public RLModelManager(final AbstractStateModel abstractStateModel,
            final ActionSelector actionSelector,
            final PersistenceManager persistenceManager,
            final Set<Tag<?>> concreteStateTags,
            final SequenceManager sequenceManager,
            final boolean storeWidgets,
            final RewardFunction rewardFunction,
            final QFunction qFunction,
            final Tag<?> tag) {
        super(abstractStateModel, actionSelector, persistenceManager, concreteStateTags, sequenceManager, storeWidgets);
        this.rewardFunction = rewardFunction;
        this.qFunction = qFunction;
        this.tag = tag;
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
        final Action selectedAction = super.getAbstractActionToExecute(actions);
        final AbstractAction selectedAbstractAction = getAbstractAction(currentAbstractState, selectedAction);
        float reward = rewardFunction.getReward(state, getCurrentConcreteState(), currentAbstractState, selectedAbstractAction);

        logger.info("reward={} found for sequenceNumber={} and actionNumber={}", reward,
                getSequenceManager().getCurrentSequence().getNodes().size(),
                getSequenceManager().getCurrentSequenceNr());

        final double rlQValue = getQValue(previouslySelectedAbstractAction, reward, actions);

        updateQValue(previouslySelectedAbstractAction, rlQValue);
        previouslySelectedAbstractAction = selectedAbstractAction;

        log(actions, selectedAction, selectedAbstractAction);

        return selectedAction;
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
     * Get the Q-value for an {@link Action}
     *
     * @param selectedAbstractAction, can be null
     * @param reward
     */
    private double getQValue(final AbstractAction selectedAbstractAction, final float reward, final Set<Action> actions) {
        if (selectedAbstractAction == null) {
            logger.info("Update of Q-value failed because no action was found to execute");
        }
        return qFunction.getQValue(previouslySelectedAbstractAction, selectedAbstractAction, reward, currentAbstractState, actions);
    }

    private void log(final Set<Action> actions, final Action selectedAction,final AbstractAction selectedAbstractAction) {
        logger.info("Number of actions available={}", actions.size());
        if (selectedAction != null) {
            logger.info("Action selected shortString={}", selectedAction.toShortString());
        }
        if(selectedAbstractAction != null) {
            logger.info("Abstract action selected abstractActionID={}, id={}", selectedAbstractAction.getActionId(), selectedAbstractAction.getId());

            // add counter
            final int counterSelectedAbstractAction = selectedAbstractAction.getAttributes().get(RLTags.ActionCounter, 0);
            selectedAbstractAction.getAttributes().set(RLTags.ActionCounter, counterSelectedAbstractAction + 1);
            logger.info("Action selected counter={}", selectedAbstractAction.getAttributes().get(RLTags.ActionCounter));
        }
        logger.info("SequenceID={}", getSequenceManager().getSequenceID());
    }

    /**
     * Update the Q-value for an {@link Action}
     *
     * @param selectedAbstractAction, can be null
     * @param qValue
     */
    private void updateQValue(final AbstractAction selectedAbstractAction, double qValue) {
        if (selectedAbstractAction == null) {
            logger.warn("Update of Q-value failed because no action was found to execute");
            return;
        }

        if (previouslySelectedAbstractAction == null) {
            logger.warn("Update of Q-value failed because no previous action was found");
            return;
        }
        logger.info("Q-value of abstractAction before updating with ID={} and q-value={}", previouslySelectedAbstractAction.getId(),  previouslySelectedAbstractAction.getAttributes().get(RLTags.SarsaValue, 0f));
        previouslySelectedAbstractAction.addAttribute(RLTags.SarsaValue, (float) qValue);
        logger.info("Q-value of abstractAction after updating with ID={} and q-value={}", previouslySelectedAbstractAction.getId(),  previouslySelectedAbstractAction.getAttributes().get(RLTags.SarsaValue));
    }
}
