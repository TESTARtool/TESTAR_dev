package nl.ou.testar.StateModel.Persistence.OrientDB.Entity;

public class EdgeEntity extends DocumentEntity {

    private VertexEntity sourceEntity;
    private VertexEntity targetEntity;

    public EdgeEntity(EntityClass entityClass, VertexEntity sourceEntity, VertexEntity targetEntity) {
        super(entityClass);
        this.sourceEntity = sourceEntity;
        this.targetEntity = targetEntity;
    }

    /**
     * Returns this edge's source vertex entity
     * @return
     */
    public VertexEntity getSourceEntity() {
        return sourceEntity;
    }

    /**
     * Returns this edge's target vertex entity
     * @return
     */
    public VertexEntity getTargetEntity() {
        return targetEntity;
    }
}
