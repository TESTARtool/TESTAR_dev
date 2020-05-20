package nl.ou.testar.genetic.programming.strategy.actions;

import nl.ou.testar.genetic.programming.strategy.StrategyGuiState;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNode;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNodeBoolean;

import java.util.List;

public class SnOr extends StrategyNodeBoolean {
    private StrategyNodeBoolean child;
    private StrategyNodeBoolean child1;

    public SnOr(final List<StrategyNode> children) {
        super(children);
        this.child = (StrategyNodeBoolean) children.get(0);
        this.child1 = (StrategyNodeBoolean) children.get(1);

    }

    @Override
    public boolean getValue(final StrategyGuiState state) {
        return (this.child.getValue(state) || this.child1.getValue(state));
    }

}
