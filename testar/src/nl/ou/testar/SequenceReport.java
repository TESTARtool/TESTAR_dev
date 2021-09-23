package nl.ou.testar;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Verdict;

import java.util.Set;

public interface SequenceReport {
    void addState(State state);
    void addActions(Set<Action> actions);
    void addActionsAndUnvisitedActions(Set<Action> actions, Set<String> concreteIdsOfUnvisitedActions);
    void addSelectedAction(State state, Action action);
    void addTestVerdict(Verdict verdict);
    void close();
}
