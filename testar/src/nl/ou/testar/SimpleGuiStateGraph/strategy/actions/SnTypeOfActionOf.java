package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyGuiState;
import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeAction;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeActionType;
import org.fruit.alayer.Action;
import org.fruit.alayer.Role;
import org.fruit.alayer.Tags;

import java.util.ArrayList;

public class SnTypeOfActionOf extends StrategyNodeActionType {
    private StrategyNodeAction child;

    public SnTypeOfActionOf(ArrayList<StrategyNode> children) {
        super(children);
        this.child = (StrategyNodeAction) children.get(0);
    }

    @Override
    public Role getActionType(final StrategyGuiState state) {
        final Action action = this.child.getAction(state);
        return (action != null) ? action.get(Tags.Role) : null;
    }

}
