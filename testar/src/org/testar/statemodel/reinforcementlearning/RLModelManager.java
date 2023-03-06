package org.testar.statemodel.reinforcementlearning;

import org.testar.reinforcementlearning.qfunctions.QFunction;
import org.testar.reinforcementlearning.rewardfunctions.RewardFunction;
import org.testar.statemodel.*;
import org.testar.statemodel.actionselector.ActionSelector;
import org.testar.statemodel.exceptions.ActionNotFoundException;
import org.testar.statemodel.persistence.PersistenceManager;
import org.testar.statemodel.sequence.SequenceManager;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testar.protocols.experiments.WriterExperiments;
import org.testar.protocols.experiments.WriterExperimentsParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Implementation of the {@link StateModelManager} for use of Sarsa.
 * Sarsa is a reinforcement learning (Artificial Intelligence) algorithm
 * for (sequential) action selection.
 */
public class RLModelManager extends ModelManager implements StateModelManager {

    protected static final Logger logger = LoggerFactory.getLogger(RLModelManager.class);

    /** The previously executed {@link AbstractAction} */
    protected AbstractAction previouslyExecutedAbstractAction = null;
    protected Action previouslyExecutedTestarAction = null;

    /**  The {@Link RewardFunction} determines the reward or penalty for executing an {@link AbstractAction}
     *  The reward is used in the {@link QFunction}
     */
    protected final RewardFunction rewardFunction;

    /**
     * The {@link QFunction} or Quality function determines the desirability of an {@link AbstractAction}
     */
    protected final QFunction qFunction;

    protected State state = null;

    protected final Tag<Float> tag;

    protected AbstractState previousAbstractState = null;

    protected Set<Action> previousTestarActions;

    //*** FOR DEBUGGING PURPOSES
//    List<Float> qValuesList = new ArrayList<Float>();
    //*** FOR DEBUGGING PURPOSES

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
                          final Tag<Float> tag) {
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

    /**
     * Gets an {@link Action} to execute and updates the Q-value of the previously executed {@link Action}
     */
    @Override
    public Action getAbstractActionToExecute(final Set<Action> actions) {
        logger.info("Number of actions available:{}", actions.size());
        final Action selectedAction = super.getAbstractActionToExecute(actions);
        logger.info("Action selected:{}", selectedAction == null ? null :selectedAction.toShortString());
        return selectedAction;
    }

    @Override
    public void notifyTestSequenceStopped() {
        super.notifyTestSequenceStopped();
        rewardFunction.reset();
    }
}
