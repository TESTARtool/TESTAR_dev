package nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyGuiState;
import org.fruit.alayer.Action;

import java.util.List;
import java.util.Optional;


public abstract class StrategyNodeAction extends StrategyNode {
    public StrategyNodeAction(List<StrategyNode> children) {
        super(children);
    }

    public abstract Optional<Action> getAction(final StrategyGuiState state);
}
