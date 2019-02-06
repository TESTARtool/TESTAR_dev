package nl.ou.testar.StateModel.Persistence.OrientDB.Entity;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.fruit.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class DocumentEntity {

    /**
     * The entity class this entity `belongs` to.
     */
    private EntityClass entityClass;

    /**
     * A map of properties for this entity.
     */
    private Map<String, PropertyValue> entityProperties;

    public DocumentEntity(EntityClass entityClass) {
        this.entityClass = entityClass;
        entityProperties = new HashMap<>();
    }

    public EntityClass getEntityClass() {
        return entityClass;
    }

    /**
     * Add a property to this document entity
     * @param propertyName
     * @param propertyValue
     */
    public void addPropertyValue(String propertyName, PropertyValue propertyValue) {
        entityProperties.put(propertyName, propertyValue);
    }

    /**
     * Get a property and its value from this entity
     * @param propertyName
     * @return
     */
    public PropertyValue getPropertyValue(String propertyName) {
        return entityProperties.getOrDefault(propertyName, null);
    }

    /**
     * Returns a set of names of all the properties connected to this entity
     * @return
     */
    public Set<String> getPropertyNames() {
        return entityProperties.keySet();
    }

}
