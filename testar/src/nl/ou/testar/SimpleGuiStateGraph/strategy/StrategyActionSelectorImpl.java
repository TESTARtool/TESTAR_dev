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

    private GuiStateGraphForStrategyImpl graph;
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
        if (graph.getStartingStateConcreteId() == null) {
            graph.setStartingStateConcreteId(state.get(Tags.ConcreteID));
        }

        final StrategyGuiState currentStrategyGuiState = this.findCurrentStateFromPreviousState(state, actions);

        // adding state transition to the graph: previous state + previous action = current state
        // else the first action and there is no transition yet
        if (graph.getPreviousStateConcreteId() != null && graph.getPreviousActionConcreteId() != null) {
            graph.getStateByConcreteId(graph.getPreviousStateConcreteId())
                    .ifPresent(prevState -> this.updatePreviousState(prevState, currentStrategyGuiState, state, actions));
        }

        Optional<Action> optionalReturnAction;
        ArrayList<String> actionIdsWithMaxQvalue = currentStrategyGuiState.getActionsIdsWithMaxQvalue(actions);
        if (actionIdsWithMaxQvalue.size() == 0) {
            System.out.println("ERROR: GP strategy did not find actions with max Q value!");
            optionalReturnAction = Optional.of(RandomActionSelector.selectAction(actions));
        } else {
            String concreteIdOfRandomAction = actionIdsWithMaxQvalue.get(this.getRandomValue().nextInt(actionIdsWithMaxQvalue.size()));
            System.out.println("DEBUG: randomly chosen id=" + concreteIdOfRandomAction);
            System.out.println("DEBUG: stateID from state=" + state.get(Tags.ConcreteID));
            optionalReturnAction = graph.getActionWithConcreteId(actions, concreteIdOfRandomAction);
        }

        final Action returnAction = optionalReturnAction.orElseGet(() -> RandomActionSelector.selectAction(actions));
        this.updateGUIStatesList(currentStrategyGuiState);
        this.updateState(returnAction, state);

        return returnAction;
    }

    private Random getRandomValue() {
        return new Random(System.currentTimeMillis());
    }

    private void updateState(final Action returnAction, final State state) {
        // saving the state and action for state transition after knowing the target state:
        graph.setPreviousActionConcreteId(returnAction.get(Tags.ConcreteID));
        graph.setPreviousStateConcreteId(state.get(Tags.ConcreteID));
    }

    private void updateGUIStatesList(final StrategyGuiState currentStrategyGuiState) {
        // TODO: verify: should not be a problem if state not there (new state)?
        graph.getStrategyGuiStates().remove(currentStrategyGuiState);
        graph.getStrategyGuiStates().add(currentStrategyGuiState);
    }

    private StrategyGuiState findCurrentStateFromPreviousState(final State state, final Set<Action> actions) {
        return graph.getStateByConcreteId(state.get(Tags.ConcreteID))
                .map(foundState -> {
                    foundState.updateActionIdsOfTheStateIntoModel(actions, R_MAX);
                    return foundState;
                }).orElseGet(() -> graph.createStrategyGuiState(state, actions));
    }

    private void updatePreviousState(final StrategyGuiState prevState, final StrategyGuiState currentStrategyGuiState, final State state, final Set<Action> actions) {
        final GuiStateTransition guiStateTransition = new GuiStateTransition(graph.getPreviousStateConcreteId(), state.get(Tags.ConcreteID), graph.getPreviousActionConcreteId());
        graph.getStrategyGuiStates().remove(prevState);
        prevState.addStateTransition(guiStateTransition, gammaDiscount, currentStrategyGuiState.getMaxQValueOfTheState(actions));
        graph.getStrategyGuiStates().add(prevState);
    }
}
