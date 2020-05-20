package nl.ou.testar.genetic.programming.strategy.actions;

import nl.ou.testar.genetic.programming.strategy.StrategyGuiState;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNode;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNodeActionType;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNodeBoolean;

import java.util.List;

public class SnEqualsType extends StrategyNodeBoolean {
    private StrategyNodeActionType child;
    private StrategyNodeActionType child1;

    public SnEqualsType(final List<StrategyNode> children) {
        super(children);
        this.child = (StrategyNodeActionType) children.get(0);
        this.child1 = (StrategyNodeActionType) children.get(1);
    }

    @Override
    public boolean getValue(final StrategyGuiState state) {
        return this.child.getActionType(state) == this.child1.getActionType(state);
    }

}
