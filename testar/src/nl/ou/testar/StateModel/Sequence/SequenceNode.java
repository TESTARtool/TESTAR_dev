package nl.ou.testar.StateModel.Sequence;

import nl.ou.testar.StateModel.ConcreteState;
import nl.ou.testar.StateModel.Persistence.Persistable;

import java.time.Instant;

public class SequenceNode implements Persistable {

    /**
     * The date and time of creation for this node.
     */
    private Instant timestamp;

    /**
     * The unique node identifier
     */
    private String nodeId;

    /**
     * The concrete state accessed in this node of the sequence.
     */
    private ConcreteState concreteState;

    /**
     * The ordering nr of this node in the sequence
     */
    private int nodeNr;

    public SequenceNode(String sequenceId, int nodeNr, ConcreteState concreteState) {
        timestamp = Instant.now();
        this.nodeNr = nodeNr;
        nodeId = sequenceId + '-' + nodeNr;
        this.concreteState = concreteState;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getNodeId() {
        return nodeId;
    }

    public int getNodeNr() {
        return nodeNr;
    }

    public ConcreteState getConcreteState() {
        return concreteState;
    }

    @Override
    public boolean canBeDelayed() {
        return true;
    }
}
