package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyGuiState;
import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeActionType;
import org.fruit.alayer.Role;
import org.fruit.alayer.actions.ActionRoles;

import java.util.ArrayList;

public class SnDragAction extends StrategyNodeActionType {

    public SnDragAction(ArrayList<StrategyNode> children) {
        super(children);
    }

    @Override
    public Role getActionType(final StrategyGuiState state) {
        return ActionRoles.Drag;
    }

}
