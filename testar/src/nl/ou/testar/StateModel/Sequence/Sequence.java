package nl.ou.testar.StateModel.Sequence;

import nl.ou.testar.StateModel.ConcreteAction;
import nl.ou.testar.StateModel.ConcreteState;
import nl.ou.testar.StateModel.Event.StateModelEvent;
import nl.ou.testar.StateModel.Event.StateModelEventListener;
import nl.ou.testar.StateModel.Event.StateModelEventType;
import nl.ou.testar.StateModel.Persistence.Persistable;
import org.fruit.alayer.Tag;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Sequence implements Persistable {

    private boolean active = false;

    /**
     * The unique hash that identifies the current sequence.
     */
    private String currentSequenceId;

    /**
     * The nr that identifies the sequence order in the test run.
     */
    private int currentSequenceNr;

    /**
     * The nr that identifies the last node in the sequence.
     */
    private int currentNodeNr = 0;

    /**
     * A list of nodes in this sequence
     */
    private List<SequenceNode> nodes;

    /**
     * The identifier for the abstract state model version that we are currently testing in.
     */
    private String abstractionLevelIdentifier;

    /**
     * Tags containing the attributes that were used in creating the concrete state ID.
     */
    private Set<Tag<?>> concreteStateTags;

    /**
     * The starting date and time for this sequence.
     */
    private Instant startDateTime;

    /**
     * The current node in the run.
     */
    private SequenceNode currentNode;

    /**
     * A set of event listeners to notify of changes in the sequence.
     */
    private Set<StateModelEventListener> eventListeners;

    public Sequence(int currentSequenceNr, Set<StateModelEventListener> eventListeners, String abstractionLevelIdentifier) {
        currentSequenceId = UUID.randomUUID().toString();
        this.eventListeners = eventListeners;
        this.currentSequenceNr = currentSequenceNr;
        this.abstractionLevelIdentifier = abstractionLevelIdentifier;
        nodes = new ArrayList<>();
    }

    /**
     * Start the sequence.
     */
    public void start() {
        startDateTime = Instant.now();
        active = true;
        emitEvent(new StateModelEvent(StateModelEventType.SEQUENCE_STARTED, this));
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

    public String getCurrentSequenceId() {
        return currentSequenceId;
    }

    public int getCurrentSequenceNr() {
        return currentSequenceNr;
    }

    public String getAbstractionLevelIdentifier() {
        return abstractionLevelIdentifier;
    }

    public Set<Tag<?>> getConcreteStateTags() {
        return concreteStateTags;
    }

    public Instant getStartDateTime() {
        return startDateTime;
    }

    public boolean isRunning() {
        return active;
    }

    public void stop() {
        active = false;
        //todo event here?
    }

    /**
     * Add a new node to the sequence.
     * @param concreteState the state that was reached and has to be connected to the node
     * @param concreteAction (Optionally) the action that was executed to reach the node
     */
    public void addNode(ConcreteState concreteState, ConcreteAction concreteAction) {
        if (concreteAction == null) {
            addFirstNode(concreteState);
        }
        else {
            addStep(concreteState, concreteAction);
        }
    }

    private void addFirstNode(ConcreteState concreteState) {
        SequenceNode node = new SequenceNode(currentSequenceId, ++currentNodeNr, concreteState, this);
        currentNode = node;
        nodes.add(node);
        emitEvent(new StateModelEvent(StateModelEventType.SEQUENCE_NODE_ADDED, node));
    }

    private void addStep(ConcreteState concreteState, ConcreteAction concreteAction) {
        SequenceNode targetNode = new SequenceNode(currentSequenceId, ++currentNodeNr, concreteState, null);
        SequenceStep sequenceStep = new SequenceStep(concreteAction, currentNode, targetNode);
        nodes.add(targetNode);
        currentNode = targetNode;
        emitEvent(new StateModelEvent(StateModelEventType.SEQUENCE_STEP_ADDED, sequenceStep));
    }

    @Override
    public boolean canBeDelayed() {
        return true;
    }

    /**
     * Method returns the first sequence node in the sequence;
     * @return
     */
    public SequenceNode getFirstNode() {
        return nodes.get(0);
    }
}
