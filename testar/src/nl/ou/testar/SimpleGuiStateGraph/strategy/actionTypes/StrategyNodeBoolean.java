package nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import org.fruit.alayer.State;

import java.util.ArrayList;

public abstract class StrategyNodeBoolean extends StrategyNode {
    public StrategyNodeBoolean(ArrayList<StrategyNode> children) {
        super(children);
    }

    public abstract boolean getValue(State state);

}
