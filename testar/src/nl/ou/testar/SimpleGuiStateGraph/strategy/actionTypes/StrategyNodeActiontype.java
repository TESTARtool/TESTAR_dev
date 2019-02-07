package nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import org.fruit.alayer.Role;
import org.fruit.alayer.State;

import java.util.ArrayList;

public abstract class StrategyNodeActiontype extends StrategyNode {
    public StrategyNodeActiontype(ArrayList<StrategyNode> children) {
        super(children);

    }

    public abstract Role getActionType(State state);

}
