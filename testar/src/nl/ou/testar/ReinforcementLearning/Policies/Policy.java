package nl.ou.testar.ReinforcementLearning.Policies;

import nl.ou.testar.StateModel.AbstractAction;

import java.util.Set;

/**
 * Interface for a policy
 */
public interface Policy {

    /**
     * For a {@link Set<AbstractAction>} selects actions to execute
     * @return An {@link AbstractAction} selected by the {@link Policy}, can be {@code null}
     */
    AbstractAction applyPolicy(final Set<AbstractAction> actions);
}
