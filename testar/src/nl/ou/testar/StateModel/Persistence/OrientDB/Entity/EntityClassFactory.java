package nl.ou.testar.StateModel.Persistence.OrientDB.Entity;

import com.orientechnologies.orient.core.metadata.schema.OType;

import java.util.HashMap;
import java.util.Map;


public class EntityClassFactory {

    public enum EntityClassName {AbstractState, AbstractAction}

    // a repo for generated classes, so we don't execute the same generation code over and over if not needed
    private static Map<EntityClassName, EntityClass> entityClasses = new HashMap<>();

    /**
     * This method generates an EntityClass.
     * @param className
     * @return
     */
    public static EntityClass createEntityClass(EntityClassName className) {
        //@todo this class will need refactoring as the number of vertex types grows
        switch (className) {
            case AbstractState:
                return entityClasses.containsKey(EntityClassName.AbstractState) ? entityClasses.get(EntityClassName.AbstractState)
                            : createAbstractStateClass();

            case AbstractAction:
            default:
                return entityClasses.containsKey(EntityClassName.AbstractAction) ? entityClasses.get(EntityClassName.AbstractAction)
                            : createAbstractActionClass();
        }
    }

    private static EntityClass createAbstractStateClass() {
        EntityClass abstractStateClass = new EntityClass("AbstractState", EntityClass.EntityType.Vertex);
        Property id = new Property("id", OType.STRING);
        id.setMandatory(true);
        id.setNullable(false);
        id.setIdentifier(true);
        abstractStateClass.addProperty(id);
        entityClasses.put(EntityClassName.AbstractState, abstractStateClass);
        return abstractStateClass;
    }

    private static EntityClass createAbstractActionClass() {
        EntityClass abstractActionClass = new EntityClass("AbstractAction", EntityClass.EntityType.Edge);
        Property id = new Property("id", OType.STRING);
        id.setMandatory(true);
        id.setNullable(false);
        id.setIdentifier(true);
        abstractActionClass.addProperty(id);
        entityClasses.put(EntityClassName.AbstractAction, abstractActionClass);
        return abstractActionClass;
    }



}
