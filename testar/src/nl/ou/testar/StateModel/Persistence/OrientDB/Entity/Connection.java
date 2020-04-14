package nl.ou.testar.StateModel.Persistence.OrientDB.Entity;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;

public class Connection {

    /**
     * A configuration object
     */
    private Config config;

    /**
     * The orientDB instance that provides access to the orientdb data store
     */
    private OrientDB orientDB;

    public Connection(OrientDB orientDB, Config config) {
        this.orientDB = orientDB;
        this.config = config;
    }

    /**
     * This method fetches a database session to the configured data store instance.
     * @return
     */
    public ODatabaseSession getDatabaseSession() {
        return orientDB.open(config.getDatabase(), config.getUser(), config.getPassword());
    }

    /**
     * This method tells the connection object to close the connection to the datastore instance.
     */
    void releaseConnection() {
        if (orientDB.isOpen()) {
            orientDB.close();
        }
    }

    /**
     * Returns the configuration object for this connection.
     * @return
     */
    public Config getConfig() {
        return config;
    }

}
