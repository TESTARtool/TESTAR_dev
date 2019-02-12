package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyGuiState;
import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeAction;
import org.fruit.alayer.Action;

import java.util.List;
import java.util.Optional;

public class SnRandomMostExecutedAction extends StrategyNodeAction {

    public SnRandomMostExecutedAction(final List<StrategyNode> children) {
        super(children);
    }

    @Override
    public Optional<Action> getAction(final StrategyGuiState state) {
        return Optional.ofNullable(state.randomMostExecutedAction());
    }

}
