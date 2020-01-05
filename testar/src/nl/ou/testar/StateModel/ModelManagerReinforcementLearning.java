package nl.ou.testar.StateModel;

import nl.ou.testar.ReinforcementLearning.GuiStateGraphForQlearning;
import nl.ou.testar.ReinforcementLearning.QFunctions.QFunction;
import nl.ou.testar.ReinforcementLearning.QFunctions.QLearningQFunction;
import nl.ou.testar.ReinforcementLearning.RewardFunctions.QLearningRewardFunction;
import nl.ou.testar.ReinforcementLearning.RewardFunctions.RewardFunction;
import nl.ou.testar.StateModel.ActionSelection.ActionSelector;
import nl.ou.testar.StateModel.Persistence.PersistenceManager;
import nl.ou.testar.StateModel.Sequence.SequenceManager;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;

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
    public ModelManagerReinforcementLearning(AbstractStateModel abstractStateModel, ActionSelector actionSelector, PersistenceManager persistenceManager, Set<Tag<?>> concreteStateTags, SequenceManager sequenceManager) {
        super(abstractStateModel, actionSelector, persistenceManager, concreteStateTags, sequenceManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyNewStateReached(State newState, Set<Action> actions) {
        updateQValue(newState);

        // execute other code
        super.notifyNewStateReached(newState, actions);
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
    }

}
