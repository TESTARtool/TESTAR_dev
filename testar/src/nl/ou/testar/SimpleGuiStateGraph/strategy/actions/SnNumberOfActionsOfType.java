package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeActiontype;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeNumber;
import org.fruit.alayer.State;

import java.util.ArrayList;

public class SnNumberOfActionsOfType extends StrategyNodeNumber {
    private StrategyNodeActiontype child;

    public SnNumberOfActionsOfType(ArrayList<StrategyNode> children) {
        super(children);
        this.child = (StrategyNodeActiontype) children.get(0);
    }

    @Override
    public int getValue(State state) {

        return state.getNumberOfActions(this.child.getActionType(state));
    }

}
