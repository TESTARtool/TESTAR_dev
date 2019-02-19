package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyGuiState;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeBoolean;

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
