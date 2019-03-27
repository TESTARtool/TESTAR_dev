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
     * The id of the sequence this node is part of.
     */
    private String sequenceId;

    /**
     * The concrete state accessed in this node of the sequence.
     */
    private ConcreteState concreteState;

    /**
     * The ordering nr of this node in the sequence
     */
    private int nodeNr;

    /**
     * The sequence this node is part of.
     */
    private Sequence sequence;

    public SequenceNode(String sequenceId, int nodeNr, ConcreteState concreteState, Sequence sequence) {
        timestamp = Instant.now();
        this.nodeNr = nodeNr;
        nodeId = sequenceId + '-' + nodeNr;
        this.sequenceId = sequenceId;
        this.concreteState = concreteState;
        this.sequence = sequence;
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

    /**
     * Method returns true if this is the first node in the run.
     * @return
     */
    public boolean isFirstNode() {
        return nodeNr == 1;
    }

    /**
     * Method returns the sequence id for this node.
     * @return
     */
    public String getSequenceId() {
        return sequenceId;
    }

    /**
     * Method returns the sequence for this node.
     * @return
     */
    public Sequence getSequence() {
        return sequence;
    }
}
