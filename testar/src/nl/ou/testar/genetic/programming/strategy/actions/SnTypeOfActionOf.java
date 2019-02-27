package nl.ou.testar.genetic.programming.strategy.actions;

import nl.ou.testar.genetic.programming.strategy.StrategyGuiState;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNode;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNodeAction;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNodeActionType;
import org.fruit.alayer.Role;
import org.fruit.alayer.Tags;

import java.util.List;
import java.util.Optional;

public class SnTypeOfActionOf extends StrategyNodeActionType {
    private StrategyNodeAction child;

    public SnTypeOfActionOf(final List<StrategyNode> children) {
        super(children);
        this.child = (StrategyNodeAction) children.get(0);
    }

    @Override
    public Optional<Role> getActionType(final StrategyGuiState state) {
        return this.child.getAction(state).map(action -> action.get(Tags.Role));
    }

}
