package nl.ou.testar.StateModel.Util;

import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Property;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.TypeConvertor;
import org.fruit.alayer.Tag;
import org.fruit.alayer.TaggableBase;

import java.util.Set;

public abstract class HydrationHelper {

    /**
     * Returns a tag for a given tag name
     * @param tags
     * @param tagName
     * @return
     */
    public static Tag<?> getTag(TaggableBase tags, String tagName) {
        Tag<?> tag = null;
        for (Tag t : tags.tags()) {
            if (t.name().equals(tagName)) {
                tag = t;
                break;
            }
        }
        return tag;
    }

    public static boolean typesMatch(Property property, Class clazz) {
        return property.getPropertyType() == TypeConvertor.getInstance().getOrientDBType(clazz);
    }

    public static Property getProperty(Set<Property> properties, String propertyName) {
        Property property = null;
        for (Property prop : properties) {
            if (prop.getPropertyName().equals(propertyName)) {
                property = prop;
                break;
            }
        }
        return property;
    }

}
