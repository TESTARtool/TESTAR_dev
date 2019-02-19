package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyGuiState;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeActionType;
import org.fruit.alayer.Role;
import org.fruit.alayer.actions.ActionRoles;

import java.util.List;
import java.util.Optional;

public class SnHitKeyAction extends StrategyNodeActionType {
    public SnHitKeyAction(final List<StrategyNode> children) {
        super(children);
    }

    public Optional<Role> getActionType(final StrategyGuiState state) {
        return Optional.of(ActionRoles.HitKey);
    }
}
