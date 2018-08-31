package nl.ou.testar.StateModel.Persistence;


import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.AbstractStateModel;
import nl.ou.testar.StateModel.AbstractStateTransition;
import nl.ou.testar.StateModel.Event.StateModelEvent;
import nl.ou.testar.StateModel.Event.StateModelEventListener;

/**
 * This class serves as a black hole for when persistance is not enabled
 */
public class DummyManager implements PersistenceManager, StateModelEventListener {

    @Override
    public void eventReceived(StateModelEvent event) {

    }

    @Override
    public void persistAbstractStateModel(AbstractStateModel abstractStateModel) {

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
}
