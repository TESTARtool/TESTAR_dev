package nl.ou.testar.SimpleGuiStateGraph.strategy.actions;

import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyGuiState;
import nl.ou.testar.SimpleGuiStateGraph.strategy.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeAction;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeBoolean;
import org.fruit.alayer.Action;

import java.util.ArrayList;
import java.util.Optional;

public class SnIfThenElse extends StrategyNodeAction {
    private StrategyNodeBoolean child;
    private StrategyNodeAction child1;
    private StrategyNodeAction child2;

    public SnIfThenElse(ArrayList<StrategyNode> children) {
        super(children);
        this.child = (StrategyNodeBoolean) children.get(0);
        this.child1 = (StrategyNodeAction) children.get(1);
        this.child2 = (StrategyNodeAction) children.get(2);
    }

    @Override
    public Optional<Action> getAction(final StrategyGuiState state) {
        return (this.child.getValue(state)) ? this.child1.getAction(state) : this.child2.getAction(state);
    }

}
