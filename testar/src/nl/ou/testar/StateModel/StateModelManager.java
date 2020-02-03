package nl.ou.testar.StateModel;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import java.util.Set;

public interface StateModelManager {
    void notifyNewStateReached(State newState, Set<Action> actions);
    
    void notifyConcurrenceStateReached(State newState, Set<Action> actions, Action unknown);

    void notifyActionExecution(Action action);
    
    void notifyRecordedAction(Action action);

    void notifyTestingEnded();

    Action getAbstractActionToExecute(Set<Action> actions);
    
    Set<Action> getInterestingActions(Set<Action> actions);

    void notifyTestSequencedStarted();

    void notifyTestSequenceStopped();

    void notifyTestSequenceInterruptedByUser();

    void notifyTestSequenceInterruptedBySystem(String message);
}
