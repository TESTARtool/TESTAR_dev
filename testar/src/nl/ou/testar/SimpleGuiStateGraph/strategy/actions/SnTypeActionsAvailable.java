package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyGuiState;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeBoolean;
import org.fruit.alayer.actions.ActionRoles;

import java.util.List;

public class SnTypeActionsAvailable extends StrategyNodeBoolean {

    public SnTypeActionsAvailable(final List<StrategyNode> children) {
        super(children);
    }

    @Override
    public boolean getValue(final StrategyGuiState state) {
        return state.isAvailable(ActionRoles.Type);
    }

}
