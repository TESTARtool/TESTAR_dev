package nl.ou.testar;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Verdict;

import java.util.Set;

public interface TestReport {
    void addState(State state); //not used
    void addActions(Set<Action> actions); //not used
    void addSelectedAction(State state, Action action);
    void addTestVerdict(Verdict verdict, Action action, State state);
    void saveReport(final int actionsPerSequence, final int totalSequences, final String url);
}
