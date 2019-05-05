package nl.ou.testar.StateModel.Persistence.OrientDB.Entity;

import com.orientechnologies.orient.core.metadata.schema.OType;

public class PropertyValue {

    // the orientdb type of the property
    OType type;

    // the value of the property
    Object value;

    /**
     * Constructor
     * @param type
     * @param value
     */
    public PropertyValue(OType type, Object value) {
        this.type = type;
        this.value = value;
    }

    public OType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }
}
