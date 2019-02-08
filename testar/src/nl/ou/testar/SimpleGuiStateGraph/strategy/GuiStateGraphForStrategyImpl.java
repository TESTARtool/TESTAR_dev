package nl.ou.testar.SimpleGuiStateGraph.strategy;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class GuiStateGraphForStrategyImpl implements GuiStateGraphForStrategy {
    private Set<StrategyGuiStateImpl> strategyGuiStates;
    private String startingStateConcreteId;
    private String previousStateConcreteId;
    private String previousActionConcreteId;

    GuiStateGraphForStrategyImpl() {
        strategyGuiStates = new HashSet<>();
    }

    public Set<StrategyGuiStateImpl> getStrategyGuiStates() {
        return strategyGuiStates;
    }

    public Optional<String> getStartingStateConcreteId() {
        return Optional.ofNullable(startingStateConcreteId);
    }

    public void setStartingStateConcreteId(final String startingStateConcreteId) {
        this.startingStateConcreteId = startingStateConcreteId;
    }

    public String getPreviousStateConcreteId() {
        return previousStateConcreteId;
    }

    public void setPreviousStateConcreteId(String previousStateConcreteId) {
        this.previousStateConcreteId = previousStateConcreteId;
    }

    public String getPreviousActionConcreteId() {
        return previousActionConcreteId;
    }

    public void setPreviousActionConcreteId(String previousActionConcreteId) {
        this.previousActionConcreteId = previousActionConcreteId;
    }

    public void startANewTestSequence() {
        previousActionConcreteId = null;
        previousStateConcreteId = null;
    }

    public Optional<Action> getActionWithConcreteId(final Set<Action> actions, final String concreteActionId) {
        return actions.stream()
                .filter(action -> action.get(Tags.ConcreteID).equals(concreteActionId))
                .findFirst();
    }

    public Optional<StrategyGuiStateImpl> getStateByConcreteId(final String concreteStateId) {
        return strategyGuiStates.stream()
                .filter(state -> state.getConcreteStateId().equals(concreteStateId))
                .findFirst();
    }

    public StrategyGuiStateImpl createStrategyGuiState(final State state, final Set<Action> actions) {
        final HashMap<String, Double> actionIds = new HashMap<>();
        actions.forEach(action -> actionIds.put(action.get(Tags.ConcreteID), null));
        return new StrategyGuiStateImpl(state.get(Tags.ConcreteID), actionIds);
    }
}
