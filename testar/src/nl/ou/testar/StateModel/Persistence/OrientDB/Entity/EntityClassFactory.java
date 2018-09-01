package nl.ou.testar.StateModel.Persistence.OrientDB.Entity;

import com.orientechnologies.orient.core.metadata.schema.OType;

public class EntityClassFactory {

    public enum EntityClassName {AbstractState, AbstractAction}

    /**
     * This method generates an EntityClass.
     * @param className
     * @return
     */
    public static EntityClass createEntityClass(EntityClassName className) {
        //@todo this class will need refactoring as the number of vertex types grows
        switch (className) {
            case AbstractState:
                return createAbstractStateClass();

            case AbstractAction:
            default:
                return createAbstractActionClass();
        }
    }

    private static EntityClass createAbstractStateClass() {
        EntityClass abstractStateClass = new EntityClass("AbstractState", EntityClass.EntityType.Vertex);
        Property id = new Property("id", OType.STRING);
        id.setMandatory(true);
        id.setNullable(false);
        abstractStateClass.addProperty(id);
        return abstractStateClass;
    }

    private static EntityClass createAbstractActionClass() {
        EntityClass abstractActionClass = new EntityClass("AbstractAction", EntityClass.EntityType.Edge);
        Property id = new Property("id", OType.STRING);
        id.setMandatory(true);
        id.setNullable(false);
        abstractActionClass.addProperty(id);
        return abstractActionClass;
    }



}
