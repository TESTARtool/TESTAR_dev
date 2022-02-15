package org.testar.statemodelling.persistence.orientdb.Util;

public abstract class Validation {

    /**
     * This method replaces characters that are not legal in OrientDB for use in attribute names.
     * @param attributeName
     * @return
     */
    public static String sanitizeAttributeName(String attributeName) {
        attributeName = attributeName.replaceAll(":", "_c_");
        attributeName = attributeName.replaceAll(",", "_k_");
        attributeName = attributeName.replaceAll("\\s", "_s_");
        return attributeName;
    }

}
