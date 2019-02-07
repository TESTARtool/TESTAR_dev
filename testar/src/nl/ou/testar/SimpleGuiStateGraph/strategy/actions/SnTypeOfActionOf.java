package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeAction;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeActiontype;
import org.fruit.alayer.Action;
import org.fruit.alayer.Role;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;

import java.util.ArrayList;

public class SnTypeOfActionOf extends StrategyNodeActiontype {
    private StrategyNodeAction child;

    public SnTypeOfActionOf(ArrayList<StrategyNode> children) {
        super(children);
        this.child = (StrategyNodeAction) children.get(0);
    }

    @Override
    public Role getActionType(State state) {
        Action action = this.child.getAction(state);
        if (action != null) {
            return action.get(Tags.Role);
        } else {
            return null;
        }
    }

}
