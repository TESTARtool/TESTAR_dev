package nl.ou.testar.genetic.programming.strategy.actions;

import nl.ou.testar.genetic.programming.strategy.StrategyGuiState;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNode;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNodeNumber;
import org.fruit.alayer.actions.ActionRoles;

import java.util.List;

public class SnNumberOfUnexecutedTypeActions extends StrategyNodeNumber {

    public SnNumberOfUnexecutedTypeActions(final List<StrategyNode> children) {
        super(children);
    }

    @Override
    public int getValue(final StrategyGuiState state) {
        return state.getNumberOfUnexecutedActionsOfRole(ActionRoles.Type);
    }

}
