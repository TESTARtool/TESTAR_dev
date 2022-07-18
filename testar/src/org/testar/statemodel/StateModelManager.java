package org.testar.statemodel;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;

import java.util.Map;
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

    void associateTextInputs(AbstractState state, Set<Map<String,String>> textInputs);

    AbstractState getCurrentAbstractState();

    Set<Map<String,String>> getTextInputs(AbstractState state);
}
