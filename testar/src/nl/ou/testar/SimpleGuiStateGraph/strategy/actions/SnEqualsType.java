package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyGuiState;
import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeActionType;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeBoolean;

import java.util.ArrayList;

public class SnEqualsType extends StrategyNodeBoolean {
    private StrategyNodeActionType child;
    private StrategyNodeActionType child1;

    public SnEqualsType(ArrayList<StrategyNode> children) {
        super(children);
        this.child = (StrategyNodeActionType) children.get(0);
        this.child1 = (StrategyNodeActionType) children.get(1);
    }

    @Override
    public boolean getValue(final StrategyGuiState state) {
        return this.child.getActionType(state) == this.child1.getActionType(state);
    }

}
