package nl.ou.testar.genetic.programming.strategy.actions;

import nl.ou.testar.genetic.programming.strategy.StrategyGuiState;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNode;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNodeBoolean;

import java.util.List;

public class SnStateHasNotChanged extends StrategyNodeBoolean {

    public SnStateHasNotChanged(final List<StrategyNode> children) {
        super(children);
    }

    @Override
    public boolean getValue(final StrategyGuiState state) {
        return state.hasStateNotChanged();
    }

}
