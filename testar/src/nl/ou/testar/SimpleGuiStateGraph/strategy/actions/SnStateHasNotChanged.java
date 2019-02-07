package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeBoolean;
import org.fruit.alayer.State;

import java.util.ArrayList;

public class SnStateHasNotChanged extends StrategyNodeBoolean {

    public SnStateHasNotChanged(ArrayList<StrategyNode> children) {
        super(children);
    }

    @Override
    public boolean getValue(State state) {
        return state.hasStateNotChanged();
    }

}
