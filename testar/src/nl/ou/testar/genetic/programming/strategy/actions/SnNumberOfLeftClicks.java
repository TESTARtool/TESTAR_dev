package nl.ou.testar.genetic.programming.strategy.actions;

import nl.ou.testar.genetic.programming.strategy.StrategyGuiState;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNode;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNodeNumber;
import org.fruit.alayer.actions.ActionRoles;

import java.util.List;


public class SnNumberOfLeftClicks extends StrategyNodeNumber {

    public SnNumberOfLeftClicks(final List<StrategyNode> children) {
        super(children);
    }

    @Override
    public int getValue(final StrategyGuiState state) {
        return state.getNumberOfActions(ActionRoles.LeftClickAt);
    }

}
