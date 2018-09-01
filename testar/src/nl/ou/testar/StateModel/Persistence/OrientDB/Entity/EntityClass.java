package nl.ou.testar.StateModel.Persistence.OrientDB.Entity;

import java.util.HashSet;
import java.util.Set;

public class EntityClass {

    public enum EntityType {Vertex, Edge}

    // name of the class
    private String className;

    // name of the superclass, if any
    private String superClassName;

    // is the entity an edge or a vertex?
    private EntityType entityType;

    // a set of properties to add to the class
    private Set<Property> properties;

    /**
     * Consstructor
     * @param classname classname for the orientdb class
     */
    public EntityClass(String classname, EntityType entityType) {
        this.className = classname;
        this.entityType = entityType;
        properties = new HashSet<>();
    }

    public String getSuperClassName() {
        return superClassName;
    }

    public void setSuperClassName(String superClassName) {
        this.superClassName = superClassName;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    /**
     * Adds a property to this class
     * @param property
     */
    public void addProperty(Property property) {
        properties.add(property);
    }

    public String getClassName() {
        return className;
    }

    public Set<Property> getProperties() {
        return properties;
    }
}
