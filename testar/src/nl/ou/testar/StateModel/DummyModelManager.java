package nl.ou.testar.StateModel;

import nl.ou.testar.StateModel.Persistence.OrientDB.Stats.ModelStats;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;

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
    public boolean modelIsDeterministic() {
        return true;
    }

    @Override
    public int getTotalStepsExecuted() {
        return 0;
    }

    @Override
    public ModelStats getModelStats() {
        return null;
    }

    @Override
    public int getNrOfNonDeterministicActions() {
        return 0;
    }
}
