package nl.ou.testar.ReinforcementLearning.ActionSelectors;

import nl.ou.testar.RandomActionSelector;
import nl.ou.testar.ReinforcementLearning.Policies.Policy;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.AbstractStateModel;
import nl.ou.testar.StateModel.ActionSelection.ActionSelector;
import nl.ou.testar.StateModel.Exception.ActionNotFoundException;
import org.apache.commons.collections.CollectionUtils;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import java.util.*;

/**
 * Implementation of an action selector for a reinforcement learning algorithm
 */
public class ReinforcementLearningActionSelector implements ActionSelector {

    final Policy policy;

    public ReinforcementLearningActionSelector(final Policy policy) {
        this.policy = policy;
    }

    @Override
    public AbstractAction selectAction(final AbstractState currentState, final AbstractStateModel abstractStateModel) {
        final Set<AbstractAction> actions = currentState.getActions();
        if (CollectionUtils.isEmpty(actions)) {
            return null;
        }

        return policy.applyPolicy(actions);
    }
}
