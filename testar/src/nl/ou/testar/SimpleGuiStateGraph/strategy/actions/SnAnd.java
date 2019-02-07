package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeBoolean;
import org.fruit.alayer.State;

import java.util.ArrayList;

public class SnAnd extends StrategyNodeBoolean {
    private StrategyNodeBoolean child;
    private StrategyNodeBoolean child1;

    public SnAnd(final ArrayList<StrategyNode> children) {
        super(children);
        this.child = (StrategyNodeBoolean) children.get(0);
        this.child1 = (StrategyNodeBoolean) children.get(1);
    }

    public boolean getValue(final State state) {
        return (this.child.getValue(state) && this.child1.getValue(state));
    }
}
