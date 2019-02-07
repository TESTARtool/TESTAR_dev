package nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import org.fruit.alayer.State;

import java.util.ArrayList;

public abstract class StrategyNodeNumber extends StrategyNode {
    public StrategyNodeNumber(ArrayList<StrategyNode> children) {
        super(children);

    }

    public abstract int getValue(State state);
}
