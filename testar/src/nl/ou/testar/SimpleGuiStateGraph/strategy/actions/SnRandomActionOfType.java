package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyGuiState;
import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeAction;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeActionType;
import org.fruit.alayer.Action;

import java.util.ArrayList;

public class SnRandomActionOfType extends StrategyNodeAction {
    private StrategyNodeActionType child;

    public SnRandomActionOfType(ArrayList<StrategyNode> children) {
        super(children);
        this.child = (StrategyNodeActionType) children.get(0);
    }

    @Override
    public Action getAction(final StrategyGuiState state) {
        return state.getRandomAction(this.child.getActionType(state));
    }

}
