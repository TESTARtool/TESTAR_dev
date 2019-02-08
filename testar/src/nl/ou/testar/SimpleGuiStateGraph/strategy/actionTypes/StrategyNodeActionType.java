package nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyGuiState;
import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import org.fruit.alayer.Role;

import java.util.ArrayList;

public abstract class StrategyNodeActionType extends StrategyNode {
    public StrategyNodeActionType(ArrayList<StrategyNode> children) {
        super(children);

    }

    public abstract Role getActionType(final StrategyGuiState state);
}
