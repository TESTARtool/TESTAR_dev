package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyGuiState;
import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeAction;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeActionType;
import org.fruit.alayer.Role;
import org.fruit.alayer.Tags;

import java.util.ArrayList;
import java.util.Optional;

public class SnTypeOfActionOf extends StrategyNodeActionType {
    private StrategyNodeAction child;

    public SnTypeOfActionOf(final ArrayList<StrategyNode> children) {
        super(children);
        this.child = (StrategyNodeAction) children.get(0);
    }

    @Override
    public Optional<Role> getActionType(final StrategyGuiState state) {
        return this.child.getAction(state).map(action -> action.get(Tags.Role));
    }

}
