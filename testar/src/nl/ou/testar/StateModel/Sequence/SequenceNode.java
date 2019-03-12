package nl.ou.testar.StateModel.Sequence;

import java.time.Instant;

public class SequenceNode {

    /**
     * The date and time of creation for this node.
     */
    private Instant timestamp;

    /**
     * The unique node identifier
     */
    private String nodeId;

    /**
     * The ordering nr of this node in the sequence
     */
    private int nodeNr;

    public SequenceNode(String sequenceId, int nodeNr) {
        timestamp = Instant.now();
        this.nodeNr = nodeNr;
        nodeId = sequenceId + '-' + nodeNr;
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
}
