package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.ActionExecutionStatus;
import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyGuiState;
import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeNumber;
import org.fruit.alayer.actions.ActionRoles;

import java.util.ArrayList;

public class SnNumberOfUnexecutedTypeActions extends StrategyNodeNumber {

    public SnNumberOfUnexecutedTypeActions(final ArrayList<StrategyNode> children) {
        super(children);
    }

    @Override
    public int getValue(final StrategyGuiState state) {
        return state.getNumberOfActions(ActionRoles.Type, ActionExecutionStatus.UNEX);
    }

}
