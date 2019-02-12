package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyGuiState;
import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeActionType;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeNumber;
import org.fruit.alayer.Role;

import java.util.ArrayList;
import java.util.Optional;

public class SnNumberOfActionsOfType extends StrategyNodeNumber {
    private StrategyNodeActionType child;

    public SnNumberOfActionsOfType(final ArrayList<StrategyNode> children) {
        super(children);
        this.child = (StrategyNodeActionType) children.get(0);
    }

    @Override
    public int getValue(final StrategyGuiState state) {
        final Optional<Role> role = this.child.getActionType(state);
        if (!role.isPresent()) {
            throw new RuntimeException("Role not set");
        }
        return state.getNumberOfActions(role.get());
    }

}
