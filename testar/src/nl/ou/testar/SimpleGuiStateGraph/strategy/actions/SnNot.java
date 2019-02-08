package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyGuiState;
import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeBoolean;

import java.util.ArrayList;

public class SnNot extends StrategyNodeBoolean {
    private StrategyNodeBoolean child;

    public SnNot(ArrayList<StrategyNode> children) {
        super(children);
        this.child = (StrategyNodeBoolean) children.get(0);
    }

    @Override
    public boolean getValue(final StrategyGuiState state) {
        return !(this.child.getValue(state));
    }

}
