package nl.ou.testar.StateModel.Sequence;

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
    private String abstractionLevelIdentifier;

    /**
     * A set of event listeners to notify of changes in the sequence.
     */
    private Set<StateModelEventListener> eventListeners;

    /**
     * Constructor
     * @param eventListeners
     */
    public SequenceManager(Set<StateModelEventListener> eventListeners, String abstractionLevelIdentifier) {
        this.eventListeners = eventListeners;
        this.abstractionLevelIdentifier = abstractionLevelIdentifier;
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

        currentSequence = new Sequence(++currentSequenceNr, eventListeners, abstractionLevelIdentifier);
        currentSequence.start();
    }

    /**
     * Stop the currently executing test sequence. This particular sequence can no longer be restarted after it has been stopped.
     */
    public void stopSequence() {
        currentSequence.stop();
    }

}
