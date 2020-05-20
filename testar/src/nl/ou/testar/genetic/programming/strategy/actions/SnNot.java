package nl.ou.testar.genetic.programming.strategy.actions;

import nl.ou.testar.genetic.programming.strategy.StrategyGuiState;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNode;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNodeBoolean;

import java.util.List;

public class SnNot extends StrategyNodeBoolean {
    private StrategyNodeBoolean child;

    public SnNot(final List<StrategyNode> children) {
        super(children);
        this.child = (StrategyNodeBoolean) children.get(0);
    }

    @Override
    public boolean getValue(final StrategyGuiState state) {
        return !(this.child.getValue(state));
    }

}
