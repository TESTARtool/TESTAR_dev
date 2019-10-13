package nl.ou.testar.StateModel.Sequence;

import nl.ou.testar.StateModel.ConcreteAction;
import nl.ou.testar.StateModel.ConcreteState;
import nl.ou.testar.StateModel.Event.StateModelEvent;
import nl.ou.testar.StateModel.Event.StateModelEventListener;
import nl.ou.testar.StateModel.Event.StateModelEventType;
import nl.ou.testar.StateModel.Persistence.Persistable;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;

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
    private String modelIdentifier;

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
     * The execution verdict, ie: did the sequence execute succesfully.
     */
    private SequenceVerdict verdict;

    /**
     * If the sequence was interrupted by the system, this should hold the termination message generated.
     */
    private String terminationMessage;

    /**
     * A set of event listeners to notify of changes in the sequence.
     */
    private Set<StateModelEventListener> eventListeners;

    public Sequence(int currentSequenceNr, Set<StateModelEventListener> eventListeners, String modelIdentifier) {
        currentSequenceId = UUID.randomUUID().toString();
        this.eventListeners = eventListeners;
        this.currentSequenceNr = currentSequenceNr;
        this.modelIdentifier = modelIdentifier;
        verdict = SequenceVerdict.CURRENTLY_EXECUTING;
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

    public String getModelIdentifier() {
        return modelIdentifier;
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
        emitEvent(new StateModelEvent(StateModelEventType.SEQUENCE_ENDED, this));
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
        SequenceNode node = new SequenceNode(currentSequenceId, ++currentNodeNr, concreteState, this, eventListeners);
        currentNode = node;
        nodes.add(node);
        emitEvent(new StateModelEvent(StateModelEventType.SEQUENCE_NODE_ADDED, node));
    }

    private void addStep(ConcreteState concreteState, ConcreteAction concreteAction) {
        SequenceNode targetNode = new SequenceNode(currentSequenceId, ++currentNodeNr, concreteState, null, eventListeners);
        SequenceStep sequenceStep = new SequenceStep(concreteAction, currentNode, targetNode, concreteAction.getAttributes().get(Tags.Desc));
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
        return !nodes.isEmpty() ?  nodes.get(0) : null;
    }

    /**
     * This method will return the last node in the sequence.
     * @return
     */
    public SequenceNode getLastNode() {
        return !nodes.isEmpty() ? nodes.get(nodes.size() - 1) : null;
    }

    /**
     * Set the final sequence verdict.
     * @param verdict
     */
    public void setSequenceVerdict(SequenceVerdict verdict) {
        this.verdict = verdict;
    }

    /**
     * Returns the final sequence verdict.
     * @return
     */
    public SequenceVerdict getSequenceVerdict() {
        return verdict;
    }

    /**
     * Set the termination message when the sequence has been interrupted by the system.
     * @param terminationMessage
     */
    public void setTerminationMessage(String terminationMessage) {
        this.terminationMessage = terminationMessage;
    }

    /**
     * Return the message that was provided for termination by the system.
     */
    public String getTerminationMessage() {
        return terminationMessage;
    }
}
