package nl.ou.testar.genetic.programming.strategy;

import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNode;
import nl.ou.testar.genetic.programming.strategy.actionTypes.StrategyNodeAction;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;

import java.util.Set;

public class StrategyActionSelectorImpl implements StrategyActionSelector {

    private StrategyNodeAction strategyTree;
    private StrategyGuiStateImpl stateManager;

    StrategyActionSelectorImpl(final StrategyNode strategy) {
        System.out.println("DEBUG: creating genetic programming strategy");
        strategyTree = (StrategyNodeAction) strategy;
        stateManager = new StrategyGuiStateImpl();
    }

    public void print() {
        strategyTree.print(0);
    }

    public Action selectAction(final State state, final Set<Action> actions) {
        stateManager.setActions(actions);
        final Action action = strategyTree.getAction(stateManager)
                .orElse(stateManager.getRandomAction());
        stateManager.addActionToPreviousActions(action);
        stateManager.addStateToPreviousStates(state);
        System.out.printf("The selected action is of type %s", action.get(Tags.Role));

        return action;
    }
}
