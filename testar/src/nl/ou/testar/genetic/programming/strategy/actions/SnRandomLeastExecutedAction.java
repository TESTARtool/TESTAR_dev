package nl.ou.testar.genetic.programming.strategy.actions;

import nl.ou.testar.genetic.programming.strategy.StrategyGuiState;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNode;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNodeAction;
import org.fruit.alayer.Action;

import java.util.List;
import java.util.Optional;

public class SnRandomLeastExecutedAction extends StrategyNodeAction {

    public SnRandomLeastExecutedAction(final List<StrategyNode> children) {
        super(children);
    }

    @Override
    public Optional<Action> getAction(final StrategyGuiState state) {
        return Optional.ofNullable(state.randomLeastExecutedAction());
    }

}
