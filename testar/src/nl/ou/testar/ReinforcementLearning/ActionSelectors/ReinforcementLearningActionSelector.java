package nl.ou.testar.ReinforcementLearning.ActionSelectors;

import nl.ou.testar.ReinforcementLearning.Policies.Policy;
import org.testar.statemodel.AbstractAction;
import org.testar.statemodel.AbstractState;
import org.testar.statemodel.AbstractStateModel;
import org.testar.statemodel.actionselector.ActionSelector;
import org.apache.commons.collections.CollectionUtils;

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

        return policy.applyPolicy(currentState, actions);
    }
}
