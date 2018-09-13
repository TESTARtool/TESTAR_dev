package nl.ou.testar.StateModel.Persistence.OrientDB;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.AbstractStateModel;
import nl.ou.testar.StateModel.AbstractStateTransition;
import nl.ou.testar.StateModel.Event.StateModelEvent;
import nl.ou.testar.StateModel.Event.StateModelEventListener;
import nl.ou.testar.StateModel.Exception.HydrationException;
import nl.ou.testar.StateModel.Exception.InvalidEventException;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.EntityClass;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.EntityClassFactory;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.EntityManager;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.VertexEntity;
import nl.ou.testar.StateModel.Persistence.OrientDB.Hydrator.EntityHydrator;
import nl.ou.testar.StateModel.Persistence.OrientDB.Hydrator.HydratorFactory;
import nl.ou.testar.StateModel.Persistence.PersistenceManager;
import nl.ou.testar.StateModel.Util.EventHelper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
     * A set of orientdb classes that this class needs to operate
     */
    private Set<EntityClassFactory.EntityClassName> entityClassNames = new HashSet<>(Arrays.asList(
            EntityClassFactory.EntityClassName.AbstractAction,
            EntityClassFactory.EntityClassName.AbstractState));

    /**
     * Constructor
     * @param eventHelper
     */
    public OrientDBManager(EventHelper eventHelper, EntityManager entityManager) {
        this.eventHelper = eventHelper;
        this.entityManager = entityManager;
        init();
    }

    /**
     * Initialization code goes here.
     */
    private void init() {
        // we need to make sure before operation that the required classes exist.
        for (EntityClassFactory.EntityClassName className : entityClassNames) {
            EntityClass entityClass = EntityClassFactory.createEntityClass(className);
            entityManager.createClass(entityClass);
        }
    }

    @Override
    public void persistAbstractStateModel(AbstractStateModel abstractStateModel) {

    }

    @Override
    public void persistAbstractState(AbstractState abstractState) {
        // create an entity to persist to the database
        EntityClass entityClass = EntityClassFactory.createEntityClass(EntityClassFactory.EntityClassName.AbstractState);
        VertexEntity vertexEntity = new VertexEntity(entityClass);

        // hydrate the entity to a format the orient database can store
        EntityHydrator hydrator = HydratorFactory.getHydrator(HydratorFactory.HYDRATOR_ABSTRACT_STATE);
        try {
            hydrator.hydrate(vertexEntity, abstractState);
        } catch (HydrationException e) {
            System.out.println("Encounted a problem while saving abstract state with id " + abstractState.getStateId() + " to the orient database");
            return;
        }

        // save the entity!
        entityManager.saveEntity(vertexEntity);
    }

    @Override
    public void persistAbstractAction(AbstractAction abstractAction) {

    }

    @Override
    public void persistAbstractStateTransition(AbstractStateTransition abstractStateTransition) {
        // persisting a transition basically means we want to add an edge between two vertices in our database

    }

    @Override
    public void eventReceived(StateModelEvent event) {
        try {
            eventHelper.validateEvent(event);
        } catch (InvalidEventException e) {
            // There is something wrong with the event. we do nothing and exit
            return;
        }

        switch (event.getEventType()) {
            case ABSTRACT_STATE_ADDED:
            case ABSTRACT_STATE_CHANGED:
                persistAbstractState((AbstractState) (event.getPayload()));
                break;

            case ABSTRACT_STATE_TRANSITION_ADDED:
                persistAbstractStateTransition((AbstractStateTransition) (event.getPayload()));
                break;
        }

    }
}
