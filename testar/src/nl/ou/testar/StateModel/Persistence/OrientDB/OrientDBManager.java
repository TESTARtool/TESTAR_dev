package nl.ou.testar.StateModel.Persistence.OrientDB;

import com.tinkerpop.blueprints.Vertex;
import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.AbstractStateModel;
import nl.ou.testar.StateModel.AbstractStateTransition;
import nl.ou.testar.StateModel.Event.StateModelEvent;
import nl.ou.testar.StateModel.Event.StateModelEventListener;
import nl.ou.testar.StateModel.Exception.EntityNotFoundException;
import nl.ou.testar.StateModel.Exception.InvalidEventException;
import nl.ou.testar.StateModel.Persistence.PersistenceManager;
import nl.ou.testar.StateModel.Util.EventHelper;

public class OrientDBManager implements PersistenceManager, StateModelEventListener {

    /**
     * Helper class for dealing with events
     */
    private EventHelper eventHelper;

    /**
     * Manager class that will handle the OrientDB specific communications with the database
     */
    private EntityManager entityManager;

    /**
     * Constructor
     * @param eventHelper
     */
    public OrientDBManager(EventHelper eventHelper, EntityManager entityManager) {
        this.eventHelper = eventHelper;
        this.entityManager = entityManager;
    }

    @Override
    public void persistAbstractStateModel(AbstractStateModel abstractStateModel) {

    }

    @Override
    public void persistAbstractState(AbstractState abstractState) {
        // first see if the state is already present in the orientdb database
        Vertex abstractStateVertex;
        try {
            abstractStateVertex = entityManager.getVertexWithFilter("AbstractState.id", abstractState.getStateId());
        }
        catch (EntityNotFoundException ex) {
            // state not found, create one
            //@todo call the state creation code here in the entityManager
        }
        // found it. Update the existing abstract state
    }

    @Override
    public void persistAbstractAction(AbstractAction abstractAction) {

    }

    @Override
    public void persistAbstractStateTransition(AbstractStateTransition abstractStateTransition) {

    }

    @Override
    public void eventReceived(StateModelEvent event) {
        try {
            eventHelper.validateEvent(event);
        } catch (InvalidEventException e) {
            // invalid event. we do nothing and exit
            return;
        }

        switch (event.getEventType()) {
            case ABSTRACT_STATE_ADDED:
                persistAbstractState((AbstractState) (event.getPayload()));
                break;

            case ABSTRACT_ACTION_ADDED:
                persistAbstractAction((AbstractAction) (event.getPayload()));
                break;

            case ABSTRACT_STATE_TRANSITION_ADDED:
                persistAbstractStateTransition((AbstractStateTransition) (event.getPayload()));
                break;
        }

    }
}
