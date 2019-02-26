package nl.ou.testar.SimpleGuiStateGraph.strategy;

import nl.ou.testar.SimpleGuiStateGraph.GuiStateTransition;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNode;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNodeAction;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnAnd;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnClickAction;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnDragAction;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnDragActionsAvailable;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnEquals;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnEqualsType;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnEscape;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnGreaterThan;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnHitKeyAction;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnIfThenElse;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnLeftClicksAvailable;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnNot;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnNumberOfActions;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnNumberOfActionsOfType;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnNumberOfDragActions;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnNumberOfLeftClicks;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnNumberOfPreviousActions;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnNumberOfTypeActions;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnNumberOfUnexecutedDragActions;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnNumberOfUnexecutedLeftClicks;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnNumberOfUnexecutedTypeActions;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnOr;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnPreviousAction;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnRandomAction;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnRandomActionOfType;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnRandomActionOfTypeOtherThan;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnRandomLeastExecutedAction;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnRandomMostExecutedAction;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnRandomNumber;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnRandomUnexecutedAction;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnRandomUnexecutedActionOfType;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnStateHasNotChanged;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnTypeAction;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnTypeActionsAvailable;
import nl.ou.testar.SimpleGuiStateGraph.strategy.actions.SnTypeOfActionOf;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

public class StrategyActionSelectorImpl implements StrategyActionSelector {

    private GuiStateGraphForStrategy graph;
    private StrategyNodeAction strategyTree;
    private Queue<AvailableReturnTypes> queue;

    StrategyActionSelectorImpl(final Queue<AvailableReturnTypes> generatedActions) {
        System.out.println("DEBUG: creating genetic programming strategy");
        graph = new GuiStateGraphForStrategyImpl();
        this.queue = generatedActions;
    }

    public void resetGraphForNewTestSequence() {
        graph.startANewTestSequence();
    }

    public void print() {
        strategyTree.print(0);
    }

    public Action selectAction(final State state, final Set<Action> actions) {
        // saving the starting node of the graph:
        if (!graph.getStartingStateAbstractId().isPresent()){
            graph.setStartingStateAbstractId(state.get(Tags.Abstract_R_T_P_ID));
        }

        final StrategyGuiStateImpl currentStrategyGuiState = this.findCurrentStateFromPreviousState(state, actions);
        currentStrategyGuiState.setState(state, actions);
        strategyTree = (StrategyNodeAction) this.getStrategyNode();
        final Action action = strategyTree.getAction(currentStrategyGuiState)
                .orElse(currentStrategyGuiState.getRandomAction());

        currentStrategyGuiState.addActionToPreviousActions(action);
        currentStrategyGuiState.addStateToPreviousStates(state);
        System.out.printf("The selected action is of type %s", action.get(Tags.Role));

        return action;

//        if (graph.getPreviousStateAbstraFctId() != null && graph.getPreviousActionAbstractId() != null) {
//            graph.getStateByAbstractId(graph.getPreviousStateAbstractId())
//                    .ifPresent(prevState -> this.updatePreviousState(prevState, currentStrategyGuiState, state, actions));
//        }
//
//        Optional<Action> optionalReturnAction;
//        ArrayList<String> actionIdsWithMaxQvalue = currentStrategyGuiState.getActionsIdsWithMaxQvalue(actions);
//        if (actionIdsWithMaxQvalue.size() == 0) {
//            optionalReturnAction = Optional.of(RandomActionSelector.selectAction(actions));
//        } else {
//            String concreteIdOfRandomAction = actionIdsWithMaxQvalue.get(this.getRandomValue().nextInt(actionIdsWithMaxQvalue.size()));
//            optionalReturnAction = graph.getActionWithAbstractId(actions, concreteIdOfRandomAction);
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
        graph.setPreviousActionAbstractId(returnAction.get(Tags.AbstractID));
        graph.setPreviousStateAbstractId(state.get(Tags.Abstract_R_T_P_ID));
    }

    private void updateGUIStatesList(final StrategyGuiStateImpl currentStrategyGuiState) {
        graph.getStrategyGuiStates().remove(currentStrategyGuiState);
        graph.getStrategyGuiStates().add(currentStrategyGuiState);
    }

    private StrategyGuiStateImpl findCurrentStateFromPreviousState(final State state, final Set<Action> actions) {
        return graph.getStateByAbstractId(state.get(Tags.Abstract_R_T_P_ID))
                .map(foundState -> foundState.updateActionIdsOfTheStateIntoModel(actions))
                .orElseGet(() -> graph.createStrategyGuiState(state, actions));
    }

    private void updatePreviousState(final StrategyGuiStateImpl prevState, final StrategyGuiStateImpl currentStrategyGuiState, final State state, final Set<Action> actions) {
        final GuiStateTransition guiStateTransition = new GuiStateTransition(graph.getPreviousStateAbstractId(), state.get(Tags.ConcreteID), graph.getPreviousActionAbstractId());
        graph.getStrategyGuiStates().remove(prevState);
        //prevState.addStateTransition(guiStateTransition, 2, currentStrategyGuiState.getMaxQValueOfTheState(actions));
        graph.getStrategyGuiStates().add(prevState);
    }

    private StrategyNode getStrategyNode() {
        final List<StrategyNode> children = new ArrayList<>();

        switch (Objects.requireNonNull(queue.poll())) {
            case AND:
                children.add(getStrategyNode());
                children.add(getStrategyNode());
                return new SnAnd(children);
            case CLICKACTION:
                return new SnClickAction(children);
            case DRAGACTION:
                return new SnDragAction(children);
            case DRAGACTIONSAVAILABLE:
                return new SnDragActionsAvailable(children);
            case EQUALS:
                children.add(getStrategyNode());
                children.add(getStrategyNode());
                return new SnEquals(children);
            case EQUALSTYPE:
                children.add(getStrategyNode());
                children.add(getStrategyNode());
                return new SnEqualsType(children);
            case ESCAPE:
                return new SnEscape(children);
            case GREATERTHAN:
                children.add(getStrategyNode());
                children.add(getStrategyNode());
                return new SnGreaterThan(children);
            case HITKEYACTION:
                return new SnHitKeyAction(children);
            case IFTHENELSE:
                children.add(getStrategyNode());
                children.add(getStrategyNode());
                children.add(getStrategyNode());
                return new SnIfThenElse(children);
            case LEFTCLICKSAVAILABLE:
                return new SnLeftClicksAvailable(children);
            case NOT:
                children.add(getStrategyNode());
                return new SnNot(children);
            case NUMBEROFACTIONS:
                return new SnNumberOfActions(children);
            case NUMBEROFACTIONSOFTYPE:
                children.add(getStrategyNode());
                return new SnNumberOfActionsOfType(children);
            case NUMBEROFDRAGACTIONS:
                return new SnNumberOfDragActions(children);
            case NUMBEROFLEFTCLICKS:
                return new SnNumberOfLeftClicks(children);
            case NUMBEROFPREVIOUSACTIONS:
                return new SnNumberOfPreviousActions(children);
            case NUMBEROFTYPEACTIONS:
                return new SnNumberOfTypeActions(children);
            case NUMBEROFUNEXECUTEDDRAGACTIONS:
                return new SnNumberOfUnexecutedDragActions(children);
            case NUMBEROFUNEXECUTEDLEFTCLICKS:
                return new SnNumberOfUnexecutedLeftClicks(children);
            case NUMBEROFUNEXECUTEDTYPEACTIONS:
                return new SnNumberOfUnexecutedTypeActions(children);
            case OR:
                children.add(getStrategyNode());
                children.add(getStrategyNode());
                return new SnOr(children);
            case PREVIOUSACTION:
                return new SnPreviousAction(children);
            case RANDOMACTION:
                return new SnRandomAction(children);
            case RANDOMACTIONOFTYPE:
                children.add(getStrategyNode());
                return new SnRandomActionOfType(children);
            case RANDOMACTIONOFTYPEOTHERTHAN:
                children.add(getStrategyNode());
                return new SnRandomActionOfTypeOtherThan(children);
            case RANDOMLEASTEXECUTEDACTION:
                return new SnRandomLeastExecutedAction(children);
            case RANDOMMOSTEXECUTEDACTION:
                return new SnRandomMostExecutedAction(children);
            case RANDOMNUMBER:
                return new SnRandomNumber(children);
            case RANDOMUNEXECUTEDACTION:
                return new SnRandomUnexecutedAction(children);
            case RANDOMUNEXECUTEDACTIONOFTYPE:
                children.add(getStrategyNode());
                return new SnRandomUnexecutedActionOfType(children);
            case STATEHASNOTCHANGED:
                return new SnStateHasNotChanged(children);
            case TYPEACTION:
                return new SnTypeAction(children);
            case TYPEACTIONSAVAILABLE:
                return new SnTypeActionsAvailable(children);
            case TYPEOFACTIONOF:
                children.add(getStrategyNode());
                return new SnTypeOfActionOf(children);
            default:
                throw new RuntimeException("Action not supported");
        }
    }
}
