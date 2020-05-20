package nl.ou.testar.genetic.programming.strategy.actions;

import nl.ou.testar.genetic.programming.strategy.StrategyGuiState;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNode;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNodeBoolean;
import org.fruit.alayer.actions.ActionRoles;

import java.util.List;

public class SnDragActionsAvailable extends StrategyNodeBoolean {

    public SnDragActionsAvailable(final List<StrategyNode> children) {
        super(children);
    }

    @Override
    public boolean getValue(final StrategyGuiState state) {
        return state.isAvailable(ActionRoles.Drag);
    }

}
