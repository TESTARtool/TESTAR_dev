package nl.ou.testar.genetic.programming.strategy.actionTypes;

import nl.ou.testar.genetic.programming.strategy.StrategyGuiState;

import java.util.List;

public abstract class StrategyNodeNumber extends StrategyNode {
    public StrategyNodeNumber(List<StrategyNode> children) {
        super(children);
    }

    public abstract int getValue(final StrategyGuiState state);
}
