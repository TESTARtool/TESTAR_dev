package nl.ou.testar.genetic.programming.strategy.actionTypes;

import nl.ou.testar.genetic.programming.strategy.StrategyGuiState;

import java.util.List;

public abstract class StrategyNodeBoolean extends StrategyNode {
    public StrategyNodeBoolean(List<StrategyNode> children) {
        super(children);
    }

    public abstract boolean getValue(final StrategyGuiState state);

}
