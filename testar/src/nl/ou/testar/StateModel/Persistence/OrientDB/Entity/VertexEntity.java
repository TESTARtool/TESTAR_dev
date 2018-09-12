package nl.ou.testar.StateModel.Persistence.OrientDB.Entity;

import java.util.HashSet;
import java.util.Set;

public class VertexEntity extends DocumentEntity {

    private Set<EdgeEntity> outgoingEdges;

    /**
     * Constructor
     * @param entityClass
     */
    public VertexEntity(EntityClass entityClass) {
        super(entityClass);
        outgoingEdges = new HashSet<>();
    }

    /**
     * Add an outgoing edge to this vertex
     * @param edgeEntity
     */
    public void addOutgoingEdge(EdgeEntity edgeEntity) {
        outgoingEdges.add(edgeEntity);
    }



}
