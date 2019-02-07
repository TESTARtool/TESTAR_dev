package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeNumber;
import org.fruit.alayer.State;

import java.util.ArrayList;

public class SnNumberOfPreviousActions extends StrategyNodeNumber {

    public SnNumberOfPreviousActions(ArrayList<StrategyNode> children) {
        super(children);
    }

    @Override
    public int getValue(State state) {
        return state.getNumberOfPreviousActions();
    }

}
