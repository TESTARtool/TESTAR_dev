package nl.ou.testar.StateModel.Persistence.OrientDB.Entity;

public class EdgeEntity extends DocumentEntity {

    private VertexEntity sourceEntity;
    private VertexEntity targetEntity;

    public EdgeEntity(EntityClass entityClass, VertexEntity sourceEntity, VertexEntity targetEntity) {
        super(entityClass);
        this.sourceEntity = sourceEntity;
        this.targetEntity = targetEntity;
    }

}
