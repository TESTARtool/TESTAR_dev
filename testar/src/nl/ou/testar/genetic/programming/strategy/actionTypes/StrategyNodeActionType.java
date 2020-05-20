package nl.ou.testar.genetic.programming.strategy.actionTypes;

import nl.ou.testar.genetic.programming.strategy.StrategyGuiState;
import org.fruit.alayer.Role;

import java.util.List;
import java.util.Optional;

public abstract class StrategyNodeActionType extends StrategyNode {
    public StrategyNodeActionType(List<StrategyNode> children) {
        super(children);

    }

    public abstract Optional<Role> getActionType(final StrategyGuiState state);
}
