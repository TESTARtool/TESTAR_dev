package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeAction;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeActiontype;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import java.util.ArrayList;

public class SnRandomUnexecutedActionOfType extends StrategyNodeAction {
    private StrategyNodeActiontype child;

    public SnRandomUnexecutedActionOfType(ArrayList<StrategyNode> children) {
        super(children);
        this.child = (StrategyNodeActiontype) children.get(0);
    }

    @Override
    public Action getAction(State state) {
        return state.getRandomAction(this.child.getActionType(state), "UNEX");
    }

}
