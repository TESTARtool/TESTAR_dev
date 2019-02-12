package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.ActionExecutionStatus;
import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyGuiState;
import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeAction;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeActionType;
import org.fruit.alayer.Action;
import org.fruit.alayer.Role;

import java.util.List;
import java.util.Optional;

public class SnRandomUnexecutedActionOfType extends StrategyNodeAction {
    private StrategyNodeActionType child;

    public SnRandomUnexecutedActionOfType(final List<StrategyNode> children) {
        super(children);
        this.child = (StrategyNodeActionType) children.get(0);
    }

    @Override
    public Optional<Action> getAction(final StrategyGuiState state) {
        final Optional<Role> role = this.child.getActionType(state);
        if (!role.isPresent()) {
            throw new RuntimeException("Role not set!");
        }
        return Optional.ofNullable(state.getRandomUnexecutedActionOfType(role.get()));
    }

}
