package org.testar.statemodel;

import com.orientechnologies.orient.core.sql.executor.OResultSet;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;

import java.util.Set;

public interface StateModelManager {
    void notifyNewStateReached(State newState, Set<Action> actions);

    void notifyActionExecution(Action action);

    void notifyTestingEnded();

    Action getAbstractActionToExecute(Set<Action> actions);

    void notifyTestSequencedStarted();

    void notifyTestSequenceStopped();

    void notifyTestSequenceInterruptedByUser();

    void notifyTestSequenceInterruptedBySystem(String message);
    OResultSet queryStateModel(String query);
}
