package nl.ou.testar.StateModel.Util;

import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Property;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.TypeConvertor;
import org.fruit.alayer.Tag;
import org.fruit.alayer.TaggableBase;

import java.util.Set;
import java.util.zip.CRC32;

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

    /**
     * This method searches a set of properties for a given property name and returns it if found.
     * @param properties
     * @param propertyName
     * @return
     */
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

    /**
     * This method returns a unique identifier for a given input string.
     * @param text
     * @return
     */
    public static String lowCollisionID(String text){ // reduce ID collision probability
        CRC32 crc32 = new CRC32(); crc32.update(text.getBytes());
        return Integer.toUnsignedString(text.hashCode(), Character.MAX_RADIX) +
                Integer.toHexString(text.length()) +
                crc32.getValue();
    }

    /**
     * This method returns a unique id for an action/transition that can be used in OrientDB.
     * @param sourceId
     * @param targetId
     * @param actionId
     * @param modelIdentifier
     * @return
     */
    public static String createOrientDbActionId(String sourceId, String targetId, String actionId, String modelIdentifier) {
        // this creates a unique id that is needed for OrientDB storage
        String id = sourceId + "-" + actionId + "-" + targetId;
        if (modelIdentifier != null) {
            id += "-" + modelIdentifier;
        }
        return lowCollisionID(sourceId + "-" + actionId + "-" + targetId + "-" + modelIdentifier);
    }

}
