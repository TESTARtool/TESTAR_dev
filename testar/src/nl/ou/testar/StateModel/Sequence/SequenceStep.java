package nl.ou.testar.StateModel.Sequence;

import nl.ou.testar.StateModel.ConcreteAction;
import nl.ou.testar.StateModel.Persistence.Persistable;

import java.time.Instant;

public class SequenceStep implements Persistable {

    /**
     * The action that was executed and is attached to this step.
     */
    private ConcreteAction concreteAction;

    /**
     * The source node for this step.
     */
    private SequenceNode sourceNode;

    /**
     * The target node for this step.
     */
    private SequenceNode targetNode;

    /**
     * A timestamp indicating the time of execution for this step.
     */
    private Instant timestamp;

    /**
     * A string offering a description of the concrete action that this step represents.
     */
    private String actionDescription;

    /**
     * A boolean value indicating whether this sequence step introduces non-determinism into the model.
     */
    private boolean nonDeterministic;

    public SequenceStep(ConcreteAction concreteAction, SequenceNode sourceNode, SequenceNode targetNode, String actionDescription) {
        this.concreteAction = concreteAction;
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
        this.actionDescription = actionDescription;
        timestamp = Instant.now();
        nonDeterministic = false;
    }

    public ConcreteAction getConcreteAction() {
        return concreteAction;
    }

    public SequenceNode getSourceNode() {
        return sourceNode;
    }

    public SequenceNode getTargetNode() {
        return targetNode;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean canBeDelayed() {
        return true;
    }

    public String getActionDescription() {
        return actionDescription;
    }

    /**
     * This method sets whether or not the sequence step introduced non-determinism into the model.
     * @param nonDeterministic
     */
    public void setNonDeterministic(boolean nonDeterministic) {
        this.nonDeterministic = nonDeterministic;
    }

    /**
     * Returns true if the action associated with this sequence step introduced non-determinism into the model.
     * @return
     */
    public boolean isNonDeterministic() {
        return nonDeterministic;
    }
}
