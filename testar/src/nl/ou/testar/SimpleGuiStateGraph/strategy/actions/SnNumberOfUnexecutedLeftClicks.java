package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyGuiState;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeNumber;
import org.fruit.alayer.actions.ActionRoles;

import java.util.List;

public class SnNumberOfUnexecutedLeftClicks extends StrategyNodeNumber {

    public SnNumberOfUnexecutedLeftClicks(final List<StrategyNode> children) {
        super(children);
    }

    @Override
    public int getValue(final StrategyGuiState state) {
        return state.getNumberOfUnexecutedActionsOfRole(ActionRoles.LeftClickAt);
    }

}
