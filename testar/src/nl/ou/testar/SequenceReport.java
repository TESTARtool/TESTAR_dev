package nl.ou.testar;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Verdict;

import java.util.Set;

public interface SequenceReport {
    void addState(State state, Verdict verdict);
    void addActions(Set<Action> actions);
    void addActionsAndUnvisitedActions(Set<Action> actions, Set<String> concreteIdsOfUnvisitedActions);
    void addSelectedAction(State state, Action action);
    void addTestVerdict(Verdict verdict);
    void close();
}
