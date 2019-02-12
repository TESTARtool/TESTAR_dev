package nl.ou.testar.SimpleGuiStateGraph.strategy;

import nl.ou.testar.RandomActionSelector;
import nl.ou.testar.SimpleGuiStateGraph.GuiStateTransition;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeAction;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class StrategyActionSelectorImpl implements StrategyActionSelector {

    private GuiStateGraphForStrategy graph;
    private StrategyNodeAction strategyTree;
    private Action result;

    public StrategyActionSelectorImpl() {
        System.out.println("DEBUG: creating genetic programming strategy");
        graph = new GuiStateGraphForStrategyImpl();
    }

    public void resetGraphForNewTestSequence() {
        graph.startANewTestSequence();
    }

    public Action selectAction(final State state, final Set<Action> actions) {
        // saving the starting node of the graph:
        if (!graph.getStartingStateConcreteId().isPresent()){
            graph.setStartingStateConcreteId(state.get(Tags.ConcreteID));
        }

        final StrategyGuiStateImpl currentStrategyGuiState = this.findCurrentStateFromPreviousState(state, actions);

        currentStrategyGuiState.setState(state, actions);
        final Action result = strategyTree.getAction(currentStrategyGuiState)
                .orElse(currentStrategyGuiState.getRandomAction());

        currentStrategyGuiState.setPreviousAction(result);
        currentStrategyGuiState.setPreviousState(state);
        System.out.printf("The selected action is of type %s", result.get(Tags.Role));

        return result;

//        if (graph.getPreviousStateConcreteId() != null && graph.getPreviousActionConcreteId() != null) {
//            graph.getStateByConcreteId(graph.getPreviousStateConcreteId())
//                    .ifPresent(prevState -> this.updatePreviousState(prevState, currentStrategyGuiState, state, actions));
//        }
//
//        Optional<Action> optionalReturnAction;
//        ArrayList<String> actionIdsWithMaxQvalue = currentStrategyGuiState.getActionsIdsWithMaxQvalue(actions);
//        if (actionIdsWithMaxQvalue.size() == 0) {
//            optionalReturnAction = Optional.of(RandomActionSelector.selectAction(actions));
//        } else {
//            String concreteIdOfRandomAction = actionIdsWithMaxQvalue.get(this.getRandomValue().nextInt(actionIdsWithMaxQvalue.size()));
//            optionalReturnAction = graph.getActionWithConcreteId(actions, concreteIdOfRandomAction);
//        }
//
//        final Action returnAction = optionalReturnAction.orElseGet(() -> RandomActionSelector.selectAction(actions));
//        this.updateGUIStatesList(currentStrategyGuiState);
//        this.updateState(returnAction, state);
//
//        return returnAction;
    }

    private Random getRandomValue() {
        return new Random(System.currentTimeMillis());
    }

    private void updateState(final Action returnAction, final State state) {
        // saving the state and action for state transition after knowing the target state:
        graph.setPreviousActionConcreteId(returnAction.get(Tags.ConcreteID));
        graph.setPreviousStateConcreteId(state.get(Tags.ConcreteID));
    }

    private void updateGUIStatesList(final StrategyGuiStateImpl currentStrategyGuiState) {
        graph.getStrategyGuiStates().remove(currentStrategyGuiState);
        graph.getStrategyGuiStates().add(currentStrategyGuiState);
    }

    private StrategyGuiStateImpl findCurrentStateFromPreviousState(final State state, final Set<Action> actions) {
        return graph.getStateByConcreteId(state.get(Tags.ConcreteID))
                .map(foundState -> foundState.updateActionIdsOfTheStateIntoModel(actions, 1.0))
                .orElseGet(() -> graph.createStrategyGuiState(state, actions));
    }

    private void updatePreviousState(final StrategyGuiStateImpl prevState, final StrategyGuiStateImpl currentStrategyGuiState, final State state, final Set<Action> actions) {
        final GuiStateTransition guiStateTransition = new GuiStateTransition(graph.getPreviousStateConcreteId(), state.get(Tags.ConcreteID), graph.getPreviousActionConcreteId());
        graph.getStrategyGuiStates().remove(prevState);
        prevState.addStateTransition(guiStateTransition, 2, currentStrategyGuiState.getMaxQValueOfTheState(actions));
        graph.getStrategyGuiStates().add(prevState);
    }
}
