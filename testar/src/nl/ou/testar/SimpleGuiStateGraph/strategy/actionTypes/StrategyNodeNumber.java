package nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyGuiState;
import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;

import java.util.ArrayList;

public abstract class StrategyNodeNumber extends StrategyNode {
    public StrategyNodeNumber(ArrayList<StrategyNode> children) {
        super(children);
    }

    public abstract int getValue(final StrategyGuiState state);
}
