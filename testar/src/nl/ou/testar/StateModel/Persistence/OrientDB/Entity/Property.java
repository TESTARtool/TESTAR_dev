package nl.ou.testar.StateModel.Persistence.OrientDB.Entity;

import com.orientechnologies.orient.core.metadata.schema.OType;

public class Property {

    // name of the property
    private String propertyName;

    // orientdb type to use for the property
    private OType propertyType;

    // needed child type in case of embedded list, sets, maps
    private OType childType;

    private boolean isMandatory = false;

    private boolean isReadOnly = false;

    private boolean isNullable = true;

    // is this property the identifying property for a given class?
    private boolean identifier = false;

    // should the property be regarded as an autoincrement value?
    private boolean autoIncrement = false;

    // is this property indexable?
    private boolean indexAble = false;

    /**
     * Constructor
     * @param propertyName
     * @param propertyType
     */
    public Property(String propertyName, OType propertyType) {
        this(propertyName, propertyType, null);
    }

    /**
     * Constructor
     * @param propertyName
     * @param propertyType
     */
    public Property(String propertyName, OType propertyType, OType childType) {
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.childType = childType;
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

    public OType getChildType() {
        return childType;
    }

    public void setChildType(OType childType) {
        this.childType = childType;
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

    public boolean isIdentifier() {
        return identifier;
    }

    public void setIdentifier(boolean identifier) {
        this.identifier = identifier;
    }

    public boolean isIndexAble() {
        return indexAble;
    }

    public void setIndexAble(boolean indexAble) {
        this.indexAble = indexAble;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }
}
