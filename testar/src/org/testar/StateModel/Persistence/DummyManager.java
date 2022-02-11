package org.testar.statemodel.persistence;


import org.testar.statemodel.event.StateModelEvent;
import org.testar.statemodel.event.StateModelEventListener;
import org.testar.statemodel.sequence.Sequence;
import org.testar.statemodel.sequence.SequenceManager;
import org.testar.statemodel.sequence.SequenceNode;
import org.testar.statemodel.sequence.SequenceStep;
import org.testar.statemodel.*;

/**
 * This class serves as a black hole for when persistance is not enabled
 */
public class DummyManager implements PersistenceManager, StateModelEventListener {

    @Override
    public void eventReceived(StateModelEvent event) {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void persistAbstractState(AbstractState abstractState) {

    }

    @Override
    public void persistAbstractAction(AbstractAction abstractAction) {

    }

    @Override
    public void persistAbstractStateTransition(AbstractStateTransition abstractStateTransition) {

    }

    @Override
    public void initAbstractStateModel(AbstractStateModel abstractStateModel) {

    }

    @Override
    public void persistConcreteState(ConcreteState concreteState) {

    }

    @Override
    public void persistConcreteStateTransition(ConcreteStateTransition concreteStateTransition) {

    }

    @Override
    public void persistSequence(Sequence sequence) {

    }

    @Override
    public void setListening(boolean listening) {

    }

    @Override
    public void initSequenceManager(SequenceManager sequenceManager) {

    }

    @Override
    public void persistSequenceNode(SequenceNode sequenceNode) {

    }

    @Override
    public void persistSequenceStep(SequenceStep sequenceStep) {

    }

    @Override
    public boolean modelIsDeterministic(AbstractStateModel abstractStateModel) {
        return true;
    }

    @Override
    public int getNrOfNondeterministicActions(AbstractStateModel abstractStateModel) {
        return 0;
    }
}
