package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeBoolean;
import org.fruit.alayer.State;
import org.fruit.alayer.actions.ActionRoles;

import java.util.ArrayList;

public class SnDragActionsAvailable extends StrategyNodeBoolean {

    public SnDragActionsAvailable(ArrayList<StrategyNode> children) {
        super(children);
    }

    @Override
    public boolean getValue(State state) {
        return state.isAvailable(ActionRoles.Drag);
    }

}
