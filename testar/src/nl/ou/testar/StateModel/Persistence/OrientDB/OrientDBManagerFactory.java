package nl.ou.testar.StateModel.Persistence.OrientDB;

import nl.ou.testar.StateModel.Util.EventHelper;

public abstract class OrientDBManagerFactory {

    /**
     * This method returns a new OrientDBManager instance.
     * @return
     */
    public static OrientDBManager createOrientDBManager() {
        return new OrientDBManager(new EventHelper());
    }

}
