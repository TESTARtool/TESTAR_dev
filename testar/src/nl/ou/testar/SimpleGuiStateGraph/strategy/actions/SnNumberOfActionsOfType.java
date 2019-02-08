package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyGuiState;
import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeActionType;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeNumber;

import java.util.ArrayList;

public class SnNumberOfActionsOfType extends StrategyNodeNumber {
    private StrategyNodeActionType child;

    public SnNumberOfActionsOfType(ArrayList<StrategyNode> children) {
        super(children);
        this.child = (StrategyNodeActionType) children.get(0);
    }

    @Override
    public int getValue(final StrategyGuiState state) {
        return state.getNumberOfActions(this.child.getActionType(state));
    }

}
