package nl.ou.testar.genetic.programming.strategy.actions;

import nl.ou.testar.genetic.programming.strategy.StrategyGuiState;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNode;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNodeActionType;
import org.fruit.alayer.Role;
import org.fruit.alayer.actions.ActionRoles;

import java.util.List;
import java.util.Optional;

public class SnClickAction extends StrategyNodeActionType {
    public SnClickAction(final List<StrategyNode> children) {
        super(children);
    }

    public Optional<Role> getActionType(final StrategyGuiState state) {
        return Optional.of(ActionRoles.LeftClickAt);
    }
}
