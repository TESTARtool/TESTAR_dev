package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeActiontype;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeBoolean;
import org.fruit.alayer.State;

import java.util.ArrayList;

public class SnEqualsType extends StrategyNodeBoolean {
    private StrategyNodeActiontype child;
    private StrategyNodeActiontype child1;

    public SnEqualsType(ArrayList<StrategyNode> children) {
        super(children);
        this.child = (StrategyNodeActiontype) children.get(0);
        this.child1 = (StrategyNodeActiontype) children.get(1);
    }

    @Override
    public boolean getValue(State state) {
        return (this.child.getActionType(state) == this.child1.getActionType(state));
    }

}
