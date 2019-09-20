package nl.ou.testar.StateModel.Sequence;

import nl.ou.testar.StateModel.ConcreteAction;
import nl.ou.testar.StateModel.ConcreteState;
import nl.ou.testar.StateModel.Event.StateModelEvent;
import nl.ou.testar.StateModel.Event.StateModelEventListener;
import nl.ou.testar.StateModel.Event.StateModelEventType;

import java.util.Set;

public class SequenceManager {

    /**
     * For the current abstraction level, this indicates the nr of the last sequence that has run or is running.
     */
    private int currentSequenceNr = 0;

    /**
     * The current test sequence that is being run.
     */
    private Sequence currentSequence;

    /**
     * The abstraction level identifier that indicates the state model that we are testing against.
     */
    private String modelIdentifier;

    /**
     * A set of event listeners to notify of changes in the sequence.
     */
    private Set<StateModelEventListener> eventListeners;

    /**
     * Constructor
     * @param eventListeners
     */
    public SequenceManager(Set<StateModelEventListener> eventListeners, String modelIdentifier) {
        this.eventListeners = eventListeners;
        this.modelIdentifier = modelIdentifier;
        init();
    }

    private void init() {
        // initialization code here
        emitEvent(new StateModelEvent(StateModelEventType.SEQUENCE_MANAGER_INITIALIZED, this));
    }

    /**
     * Notify our listeners of emitted events
     * @param event
     */
    private void emitEvent(StateModelEvent event) {
        for (StateModelEventListener eventListener: eventListeners) {
            eventListener.eventReceived(event);
        }
    }

    /**
     * Start a new test sequence.
     */
    public void startNewSequence() {
        if (currentSequence != null && currentSequence.isRunning()) {
            // sequence wasn't terminated, so terminate it now
            currentSequence.stop();
        }

        currentSequence = new Sequence(++currentSequenceNr, eventListeners, modelIdentifier);
        currentSequence.start();
    }

    /**
     * Stop the currently executing test sequence. This particular sequence can no longer be restarted after it has been stopped.
     */
    public void stopSequence() {
        currentSequence.setSequenceVerdict(SequenceVerdict.COMPLETED_SUCCESFULLY);
        currentSequence.stop();
    }

    public void notifyInterruptionByUser() {
        currentSequence.setSequenceVerdict(SequenceVerdict.INTERRUPTED_BY_USER);
        currentSequence.stop();
    }

    public void notifyInterruptionBySystem(String message) {
        currentSequence.setSequenceVerdict(SequenceVerdict.INTERRUPTED_BY_ERROR);
        currentSequence.setTerminationMessage(message);
        currentSequence.stop();
    }

    /**
     * Use this method to notify the sequence manager that a new state was reached in the sequence.
     * @param concreteState the concrete state reached
     * @param concreteAction (Optionally) a concrete action that was executed.
     */
    public void notifyStateReached(ConcreteState concreteState, ConcreteAction concreteAction) {
        if (concreteState == null || currentSequence == null || !currentSequence.isRunning()) {
            return;
        }

        currentSequence.addNode(concreteState, concreteAction);
    }

    /**
     * Notify that an error has occurred in the current state, which will have to be stored on the current sequence node.
     * @param errorMessage
     */
    public void notifyErrorInCurrentState(String errorMessage) {
        SequenceNode lastNode = currentSequence.getLastNode();
        if (lastNode != null) {
            lastNode.addErrorMessage(errorMessage);
        }
    }

}
