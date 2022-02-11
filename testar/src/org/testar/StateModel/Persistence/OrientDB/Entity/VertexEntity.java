package org.testar.statemodel.persistence.orientdb.Entity;

import java.util.HashSet;
import java.util.Set;

public class VertexEntity extends DocumentEntity {

    // set of this vertex's outgoing edges
    private Set<EdgeEntity> outgoingEdges;

    // set of this vertex's incoming edges
    private Set<EdgeEntity> incomingEdges;

    /**
     * Constructor
     * @param entityClass
     */
    public VertexEntity(EntityClass entityClass) {
        super(entityClass);
        outgoingEdges = new HashSet<>();
        incomingEdges = new HashSet<>();
    }

    /**
     * Retrieves the outgoing edges for this vertex.
     * @return
     */
    public Set<EdgeEntity> getOutgoingEdges() {
        return outgoingEdges;
    }

    /**
     * Retrieves the incoming edges for this vertex.
     * @return
     */
    public Set<EdgeEntity> getIncomingEdges() {
        return incomingEdges;
    }

    /**
     * Adds an outgoing edge to this vertex.
     * @param edge
     */
    public void addOutgoingEdge(EdgeEntity edge) {
        outgoingEdges.add(edge);
    }

    /**
     * Adds an incoming edge to this vertex.
     * @param edge
     */
    public void addIncomingEdge(EdgeEntity edge) {
        incomingEdges.add(edge);
    }
}
