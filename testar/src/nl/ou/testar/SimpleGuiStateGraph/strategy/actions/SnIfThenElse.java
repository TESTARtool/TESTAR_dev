package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeAction;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeBoolean;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import java.util.ArrayList;

public class SnIfThenElse extends StrategyNodeAction {
    private StrategyNodeBoolean child;
    private StrategyNodeAction child1;
    private StrategyNodeAction child2;

    public SnIfThenElse(ArrayList<StrategyNode> children) {
        super(children);
        this.child = (StrategyNodeBoolean) children.get(0);
        this.child1 = (StrategyNodeAction) children.get(1);
        this.child2 = (StrategyNodeAction) children.get(2);
    }

    @Override
    public Action getAction(State state) {
        if (this.child.getValue(state)) {
            return this.child1.getAction(state);
        } else {
            return this.child2.getAction(state);
        }
    }

}
