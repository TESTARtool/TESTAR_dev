package nl.ou.testar.SimpleGuiStateGraph.strategy;

import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeAction;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;

import java.util.Set;

public class StrategyActionSelectorImpl implements StrategyActionSelector {

    private GuiStateGraphForStrategy graph;
    private StrategyNodeAction strategyTree;

    StrategyActionSelectorImpl(final StrategyNode strategy) {
        System.out.println("DEBUG: creating genetic programming strategy");
        graph = new GuiStateGraphForStrategyImpl();
        strategyTree = (StrategyNodeAction) strategy;
    }

    public void resetGraphForNewTestSequence() {
        graph.startANewTestSequence();
    }

    public void print() {
        strategyTree.print(0);
    }

    public Action selectAction(final State state, final Set<Action> actions) {
        // saving the starting node of the graph:
        if (!graph.getStartingStateAbstractId().isPresent()) {
            graph.setStartingStateAbstractId(state.get(Tags.Abstract_R_T_P_ID));
        }

        final StrategyGuiStateImpl currentStrategyGuiState = this.findCurrentStateFromPreviousState(state, actions);
        currentStrategyGuiState.setState(state, actions);
        final Action action = strategyTree.getAction(currentStrategyGuiState)
                .orElse(currentStrategyGuiState.getRandomAction());
        currentStrategyGuiState.addActionToPreviousActions(action);
        currentStrategyGuiState.addStateToPreviousStates(state);
        System.out.printf("The selected action is of type %s", action.get(Tags.Role));

        return action;
    }

    private StrategyGuiStateImpl findCurrentStateFromPreviousState(final State state, final Set<Action> actions) {
        return graph.getStateByAbstractId(state.get(Tags.Abstract_R_T_P_ID))
                .map(foundState -> foundState.updateActionIdsOfTheStateIntoModel(actions))
                .orElseGet(() -> graph.createStrategyGuiState(state, actions));
    }
}
