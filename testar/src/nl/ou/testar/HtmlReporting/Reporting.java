package nl.ou.testar.HtmlReporting;

import org.fruit.alayer.State;
import java.util.Set;
import org.fruit.alayer.Action;
import org.fruit.alayer.Verdict;

public interface Reporting {
    public void addSequenceStep(State state, String actionImagePath);
    public void addState(State state);
    public void addActions(Set<Action> actions);
    public void addActionsAndUnvisitedActions(Set<Action> actions, Set<String> concreteIdsOfUnvisitedActions);
    public void addSelectedAction(State state, Action action);
    public void addTestVerdict(Verdict verdict);
    public void close();
}
