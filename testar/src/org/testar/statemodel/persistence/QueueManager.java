package org.testar.statemodel.persistence;

import org.testar.statemodel.event.StateModelEvent;
import org.testar.statemodel.event.StateModelEventListener;
import org.testar.statemodel.exceptions.InvalidEventException;
import org.testar.statemodel.persistence.orientdb.entity.EntityManager;
import org.testar.statemodel.sequence.Sequence;
import org.testar.statemodel.sequence.SequenceManager;
import org.testar.statemodel.sequence.SequenceNode;
import org.testar.statemodel.sequence.SequenceStep;
import org.testar.statemodel.util.EventHelper;
import org.testar.statemodel.*;

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

    /**
     * Is the queue manager running in hybrid mode?
     */
    private boolean hybridMode;

    public QueueManager(PersistenceManager persistenceManager, EventHelper eventHelper, boolean hybridMode) {
        delegateManager = persistenceManager;
        queue = new ArrayDeque<>();
        this.eventHelper = eventHelper;
        this.hybridMode = hybridMode;
    }

    private void processRequest(Runnable runnable, Persistable persistable) {
        if (!hybridMode || persistable.canBeDelayed()) {
            queue.add(runnable);
        }
        else {
            runnable.run();
        }
    }

    @Override
    public void shutdown() {
        // TODO: port queue visualizer
//        if (!queue.isEmpty()) {
//            int nrOfItemsProcessed = 0;
//            int totalNrOfItems = queue.size();
//            QueueVisualizer visualizer = new QueueVisualizer("Processing persistence queue");
//            visualizer.updateMessage("Processing persistence queue : " + nrOfItemsProcessed + " / " + totalNrOfItems + " processed");
//            while (!queue.isEmpty()) {
//                queue.remove().run();
//                nrOfItemsProcessed++;
//                visualizer.updateMessage("Processing persistence queue : " + nrOfItemsProcessed + " / " + totalNrOfItems + " processed");
//            }
//            visualizer.stop();
//        }
        delegateManager.shutdown();
    }

    @Override
    public void persistAbstractState(AbstractState abstractState) {
        processRequest(() -> delegateManager.persistAbstractState(abstractState), abstractState);
    }

    @Override
    public void persistAbstractAction(AbstractAction abstractAction) {
        processRequest(() -> delegateManager.persistAbstractAction(abstractAction), abstractAction);
    }

    @Override
    public void persistAbstractStateTransition(AbstractStateTransition abstractStateTransition) {
        processRequest(() -> delegateManager.persistAbstractStateTransition(abstractStateTransition), abstractStateTransition);
    }

    @Override
    public void persistAbstractActionAttributeUpdated(AbstractStateTransition abstractStateTransition) {
        processRequest(() -> delegateManager.persistAbstractActionAttributeUpdated(abstractStateTransition), abstractStateTransition);
    }

    @Override
    public void persistConcreteState(IConcreteState concreteState) {
        processRequest(() -> delegateManager.persistConcreteState(concreteState), concreteState);
    }

    @Override
    public void persistConcreteStateTransition(ConcreteStateTransition concreteStateTransition) {
        processRequest(() -> delegateManager.persistConcreteStateTransition(concreteStateTransition), concreteStateTransition);
    }

    @Override
    public void initAbstractStateModel(AbstractStateModel abstractStateModel) {
        setListening(false);
        delegateManager.initAbstractStateModel(abstractStateModel);
        setListening(true);
    }

    @Override
    public void persistSequence(Sequence sequence) {
        processRequest(() -> delegateManager.persistSequence(sequence), sequence);
    }

    @Override
    public void initSequenceManager(SequenceManager sequenceManager) {
        setListening(false);
        delegateManager.initSequenceManager(sequenceManager);
        setListening(true);
    }

    @Override
    public void persistSequenceNode(SequenceNode sequenceNode) {
        processRequest(() -> delegateManager.persistSequenceNode(sequenceNode), sequenceNode);
    }

    @Override
    public void persistSequenceStep(SequenceStep sequenceStep) {
        processRequest(() -> delegateManager.persistSequenceStep(sequenceStep), sequenceStep);
    }

    @Override
    public boolean modelIsDeterministic(AbstractStateModel abstractStateModel) {
        return delegateManager.modelIsDeterministic(abstractStateModel);
    }

    @Override
    public int getNrOfNondeterministicActions(AbstractStateModel abstractStateModel) {
        return delegateManager.getNrOfNondeterministicActions(abstractStateModel);
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

            case ABSTRACT_ACTION_ATTRIBUTE_UPDATED:
                persistAbstractActionAttributeUpdated((AbstractStateTransition) (event.getPayload()));
                break;

            case ABSTRACT_STATE_MODEL_INITIALIZED:
              initAbstractStateModel((AbstractStateModel) (event.getPayload()));
              break;

            case SEQUENCE_STARTED:
                persistSequence((Sequence) event.getPayload());
                break;

            case SEQUENCE_MANAGER_INITIALIZED:
                initSequenceManager((SequenceManager) event.getPayload());
                break;

            case SEQUENCE_NODE_ADDED:
                persistSequenceNode((SequenceNode) event.getPayload());
                break;

            case SEQUENCE_STEP_ADDED:
                persistSequenceStep((SequenceStep) event.getPayload());
        }
    }

    @Override
    public void setListening(boolean listening) {
        this.listening = listening;
    }

    @Override
    public EntityManager getEntityManager() {
    	return null;
    }
}
