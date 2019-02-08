package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyGuiState;
import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeAction;
import org.fruit.alayer.Action;

import java.util.ArrayList;

public class SnPreviousAction extends StrategyNodeAction {

    public SnPreviousAction(ArrayList<StrategyNode> children) {
        super(children);
    }

    @Override
    public Action getAction(final StrategyGuiState state) {
        return state.previousAction();
    }

}
