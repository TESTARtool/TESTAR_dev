package nl.ou.testar.SimpleGuiStateGraph.strategy;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class GuiStateGraphForStrategyImpl implements GuiStateGraphForStrategy {
    private Set<StrategyGuiStateImpl> strategyGuiStates;
    private String startingStateAbstractId;
    private String previousStateAbstractId;
    private String previousActionAbstractId;

    GuiStateGraphForStrategyImpl() {
        strategyGuiStates = new HashSet<>();
    }

    public Set<StrategyGuiStateImpl> getStrategyGuiStates() {
        return strategyGuiStates;
    }

    public Optional<String> getStartingStateAbstractId() {
        return Optional.ofNullable(startingStateAbstractId);
    }

    public void setStartingStateAbstractId(final String startingStateAbstractId) {
        this.startingStateAbstractId = startingStateAbstractId;
    }

    public String getPreviousStateAbstractId() {
        return previousStateAbstractId;
    }

    public void setPreviousStateAbstractId(String previousStateAbstractId) {
        this.previousStateAbstractId = previousStateAbstractId;
    }

    public String getPreviousActionAbstractId() {
        return previousActionAbstractId;
    }

    public void setPreviousActionAbstractId(String previousActionAbstractId) {
        this.previousActionAbstractId = previousActionAbstractId;
    }

    public void startANewTestSequence() {
        previousActionAbstractId = null;
        previousStateAbstractId = null;
    }

    public Optional<Action> getActionWithAbstractId(final Set<Action> actions, final String abstractActionId) {
        return actions.stream()
                .filter(action -> action.get(Tags.AbstractID).equals(abstractActionId))
                .findFirst();
    }

    public Optional<StrategyGuiStateImpl> getStateByAbstractId(final String abstractStateId) {
        return strategyGuiStates.stream()
                .filter(state -> state.getAbstractStateId().equals(abstractStateId))
                .findFirst();
    }

    public StrategyGuiStateImpl createStrategyGuiState(final State state, final Set<Action> actions) {
        final List<String> actionIds = new ArrayList<>();
        actions.forEach(action -> actionIds.add(action.get(Tags.ConcreteID)));
        return new StrategyGuiStateImpl(state.get(Tags.ConcreteID), actionIds);
    }
}
