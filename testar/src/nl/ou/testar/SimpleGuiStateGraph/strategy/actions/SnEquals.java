package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyGuiState;
import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeBoolean;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeNumber;

import java.util.ArrayList;

public class SnEquals extends StrategyNodeBoolean {
    private StrategyNodeNumber child;
    private StrategyNodeNumber child1;

    public SnEquals(ArrayList<StrategyNode> children) {
        super(children);
        this.child = (StrategyNodeNumber) children.get(0);
        this.child1 = (StrategyNodeNumber) children.get(1);
    }

    @Override
    public boolean getValue(final StrategyGuiState state) {
        return this.child.getValue(state) == this.child1.getValue(state);
    }

}
