package nl.ou.testar.ReinforcementLearning.Policies;

import org.testar.statemodel.AbstractAction;
import org.testar.statemodel.AbstractState;
import org.testar.statemodel.ConcreteState;

import java.util.Set;

/**
 * Interface for a policy
 */
public interface Policy {

    /**
     * For a {@link Set<AbstractAction>} selects actions to execute
     * @return An {@link AbstractAction} selected by the {@link Policy}, can be {@code null}
     */
    AbstractAction applyPolicy(final AbstractState state, final Set<AbstractAction> actions);
}
