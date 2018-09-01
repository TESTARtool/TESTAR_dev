package nl.ou.testar.StateModel.Persistence.OrientDB.Entity;

import com.orientechnologies.orient.core.metadata.schema.OType;

public class Property {

    // name of the property
    private String propertyName;

    // orientdb type to use for the property
    private OType propertyType;

    private boolean isMandatory = false;

    private boolean isReadOnly = false;

    private boolean isNullable = true;

    /**
     * Constructor
     * @param propertyName
     * @param propertyType
     */
    public Property(String propertyName, OType propertyType) {
        this.propertyName = propertyName;
        this.propertyType = propertyType;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public OType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(OType propertyType) {
        this.propertyType = propertyType;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean mandatory) {
        isMandatory = mandatory;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public void setReadOnly(boolean readOnly) {
        isReadOnly = readOnly;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public void setNullable(boolean nullable) {
        isNullable = nullable;
    }
}
