package nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import java.util.ArrayList;


public abstract class StrategyNodeAction extends StrategyNode {
    public StrategyNodeAction(ArrayList<StrategyNode> children) {
        super(children);

    }

    public abstract Action getAction(State state);
}
