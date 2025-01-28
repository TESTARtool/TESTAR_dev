package org.testar.statemodel;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;

import java.util.Set;

public class DummyModelManager implements StateModelManager{

    @Override
    public void notifyNewStateReached(State newState, Set<Action> actions) {

    }

    @Override
    public void notifyConcurrenceStateReached(State newState, Set<Action> actions, Action unknown) {

    }

    @Override
    public void notifyActionExecution(Action action) {

    }

    @Override
    public void notifyListenedAction(Action action) {

    }

    @Override
    public void notifyTestingEnded() {

    }

    @Override
    public Action getAbstractActionToExecute(Set<Action> actions) {
        return null;
    }

    @Override
    public Set<Action> getInterestingActions(Set<Action> actions) {
    	return java.util.Collections.<Action>emptySet();
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
    public String getModelIdentifier() {
    	return "";
    }

    @Override
    public String queryStateModel(String query, Object... params) {
    	return "";
    }
}
