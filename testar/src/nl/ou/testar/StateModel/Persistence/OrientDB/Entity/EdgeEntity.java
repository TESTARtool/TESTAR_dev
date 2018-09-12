package nl.ou.testar.StateModel.Persistence.OrientDB.Entity;

public class EdgeEntity extends DocumentEntity {

    private VertexEntity targetEntity;

    public EdgeEntity(EntityClass entityClass, VertexEntity targetEntity) {
        super(entityClass);
        this.targetEntity = targetEntity;
    }

}
