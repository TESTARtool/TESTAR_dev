package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyGuiState;
import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeAction;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeActionType;
import org.fruit.alayer.Action;
import org.fruit.alayer.Role;

import java.util.ArrayList;

public class SnRandomActionOfTypeOtherThan extends StrategyNodeAction {
    private StrategyNodeActionType child;

    public SnRandomActionOfTypeOtherThan(ArrayList<StrategyNode> children) {
        super(children);
        this.child = (StrategyNodeActionType) children.get(0);

    }

    @Override
    public Action getAction(final StrategyGuiState state) {
        Role role = this.child.getActionType(state);
        return (role != null) ? state.getRandomActionOfTypeOtherThan(role) : null;
    }

}
