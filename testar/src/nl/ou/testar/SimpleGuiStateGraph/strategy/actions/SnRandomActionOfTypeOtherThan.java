package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeAction;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeActiontype;
import org.fruit.alayer.Action;
import org.fruit.alayer.Role;
import org.fruit.alayer.State;

import java.util.ArrayList;

public class SnRandomActionOfTypeOtherThan extends StrategyNodeAction {
    private StrategyNodeActiontype child;

    public SnRandomActionOfTypeOtherThan(ArrayList<StrategyNode> children) {
        super(children);
        this.child = (StrategyNodeActiontype) children.get(0);

    }

    @Override
    public Action getAction(State state) {
        Role role = this.child.getActionType(state);
        if (role != null) {
            return state.getRandomActionOfTypeOtherThan(role);
        } else {
            return null;
        }
    }

}
