package org.testar.statemodel;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;

import java.util.Map;
import java.util.Set;

public class DummyModelManager implements StateModelManager{

    @Override
    public void notifyNewStateReached(State newState, Set<Action> actions) {

    }

    @Override
    public void notifyActionExecution(Action action) {

    }

    @Override
    public void notifyTestingEnded() {

    }

    @Override
    public Action getAbstractActionToExecute(Set<Action> actions) {
        return null;
    }

    @Override
    public void notifyTestSequencedStarted() {

    }

    @Override
    public void notifyTestSequenceStopped() {

    }

    @Override
    public void notifyTestSequenceInterruptedByUser() {

    }

    @Override
    public void notifyTestSequenceInterruptedBySystem(String message) {

    }

    @Override
    public void associateTextInputs(AbstractState state, Set<Map<String,String>> textInputs) {

    }

    @Override
    public Set<Map<String,String>> getTextInputs(AbstractState state) {
        return null;
    }

    @Override
    public AbstractState getCurrentAbstractState() {
        return null;
    }
}
