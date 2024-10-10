package org.testar.statemodel;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;

import java.util.Set;

public interface StateModelManager {
    void notifyNewStateReached(State newState, Set<Action> actions);

    void notifyConcurrenceStateReached(State newState, Set<Action> actions, Action unknown);

    void notifyActionExecution(Action action);

    void notifyListenedAction(Action action);

    void notifyTestingEnded();

    Action getAbstractActionToExecute(Set<Action> actions);

    Set<Action> getInterestingActions(Set<Action> actions);

    void notifyTestSequencedStarted();

    void notifyTestSequenceStopped();

    void notifyTestSequenceInterruptedByUser();

    void notifyTestSequenceInterruptedBySystem(String message);
}
