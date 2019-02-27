package nl.ou.testar.genetic.programming.strategy.actions;

import nl.ou.testar.genetic.programming.strategy.StrategyGuiState;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNode;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNodeBoolean;
import org.fruit.alayer.actions.ActionRoles;

import java.util.List;

public class SnLeftClicksAvailable extends StrategyNodeBoolean {

    public SnLeftClicksAvailable(final List<StrategyNode> children) {
        super(children);
    }

    @Override
    public boolean getValue(final StrategyGuiState state) {
        return state.isAvailable(ActionRoles.LeftClickAt);
    }

}
