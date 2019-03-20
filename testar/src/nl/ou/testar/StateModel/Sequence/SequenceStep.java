package nl.ou.testar.StateModel.Sequence;

import nl.ou.testar.StateModel.ConcreteAction;

import java.time.Instant;

public class SequenceStep {

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

    public SequenceStep(ConcreteAction concreteAction, SequenceNode sourceNode, SequenceNode targetNode) {
        this.concreteAction = concreteAction;
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
        timestamp = Instant.now();
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
}
