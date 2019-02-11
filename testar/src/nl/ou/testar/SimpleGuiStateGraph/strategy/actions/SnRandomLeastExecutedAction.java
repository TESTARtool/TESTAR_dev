package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.ActionExecutionStatus;
import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyGuiState;
import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeAction;
import org.fruit.alayer.Action;

import java.util.ArrayList;
import java.util.Optional;

public class SnRandomLeastExecutedAction extends StrategyNodeAction {

    public SnRandomLeastExecutedAction(ArrayList<StrategyNode> children) {
        super(children);
    }

    @Override
    public Optional<Action> getAction(final StrategyGuiState state) {
        return Optional.ofNullable(state.getRandomAction(ActionExecutionStatus.LEAST));
    }

}
