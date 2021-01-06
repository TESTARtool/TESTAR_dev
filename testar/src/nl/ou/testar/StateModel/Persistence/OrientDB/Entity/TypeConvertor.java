package nl.ou.testar.StateModel.Persistence.OrientDB.Entity;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.orientechnologies.orient.core.metadata.schema.OType;

public class TypeConvertor {

    // map connecting Java types to OrientDb Types
    private BiMap<Class<?>, OType> typeMatches;

    private static TypeConvertor instance;

    private TypeConvertor() {
        typeMatches = HashBiMap.create();
        typeMatches.put(String.class, OType.STRING);
        typeMatches.put(Boolean.class, OType.BOOLEAN);
        typeMatches.put(Double.class, OType.DOUBLE);
        typeMatches.put(Integer.class, OType.INTEGER);
        typeMatches.put(Float.class, OType.FLOAT);
    }

    public static TypeConvertor getInstance() {
        if (instance == null) {
            instance = new TypeConvertor();
        }
        return instance;
    }

    public OType getOrientDBType(Class<?> clazz) {
        return typeMatches.get(clazz);
    }

    public Class<?> getClass(OType oType) {
        return typeMatches.inverse().get(oType);
    }


}
