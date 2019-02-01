package nl.ou.testar.StateModel.Persistence;

import nl.ou.testar.StateModel.*;
import nl.ou.testar.StateModel.Event.StateModelEvent;
import nl.ou.testar.StateModel.Event.StateModelEventListener;
import nl.ou.testar.StateModel.Exception.InvalidEventException;
import nl.ou.testar.StateModel.Util.EventHelper;

import java.util.ArrayDeque;

public class QueueManager implements PersistenceManager, StateModelEventListener {

    /**
     * A queue holding the commands to execute
     */
    private ArrayDeque<Runnable> queue;

    /**
     * Composite persistencemanager that will do the actual work for us.
     */
    private PersistenceManager delegateManager;

    /**
     * Helper class for dealing with events
     */
    private EventHelper eventHelper;

    /**
     * Is the event listener processing events?
     */
    private boolean listening = true;

    public QueueManager(PersistenceManager persistenceManager, EventHelper eventHelper) {
        delegateManager = persistenceManager;
        queue = new ArrayDeque<>();
        this.eventHelper = eventHelper;
    }

    @Override
    public void persistAbstractStateModel(AbstractStateModel abstractStateModel) {
        while (!queue.isEmpty()) {
            queue.remove().run();
        }
    }

    @Override
    public void persistAbstractState(AbstractState abstractState) {
        queue.add(() -> delegateManager.persistAbstractState(abstractState));
    }

    @Override
    public void persistAbstractAction(AbstractAction abstractAction) {
        queue.add(() -> delegateManager.persistAbstractAction(abstractAction));
    }

    @Override
    public void persistAbstractStateTransition(AbstractStateTransition abstractStateTransition) {
        queue.add(() -> delegateManager.persistAbstractStateTransition(abstractStateTransition));
    }

    @Override
    public void persistConcreteState(ConcreteState concreteState) {
        queue.add(() -> delegateManager.persistConcreteState(concreteState));
    }

    @Override
    public void initAbstractStateModel(AbstractStateModel abstractStateModel) {
        delegateManager.initAbstractStateModel(abstractStateModel);
    }

    @Override
    public void eventReceived(StateModelEvent event) {
        if (!listening) return;

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
            case ABSTRACT_ACTION_CHANGED:
                persistAbstractStateTransition((AbstractStateTransition) (event.getPayload()));
                break;

            case ABSTRACT_STATE_MODEL_INITIALIZED:
                initAbstractStateModel((AbstractStateModel) (event.getPayload()));
        }
    }

    @Override
    public void setListening(boolean listening) {
        this.listening = listening;
    }
}
