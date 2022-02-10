package org.testar.StateModel.Persistence.OrientDB;

import org.testar.StateModel.Persistence.OrientDB.Entity.Config;
import org.testar.StateModel.Persistence.OrientDB.Entity.EntityManager;
import org.testar.StateModel.Persistence.PersistenceManager;
import org.testar.StateModel.Persistence.PersistenceManagerFactory;
import org.testar.StateModel.Persistence.QueueManager;
import org.testar.StateModel.Util.EventHelper;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Settings;

public class OrientDBManagerFactory implements PersistenceManagerFactory {

    @Override
    public PersistenceManager getPersistenceManager(Settings settings) {
        EventHelper eventHelper = new EventHelper();

        // create a config object for the orientdb database connection info
        Config config = new Config();
        config.setConnectionType(settings.get(ConfigTags.DataStoreType));
        config.setServer(settings.get(ConfigTags.DataStoreServer));
        config.setDatabase(settings.get(ConfigTags.DataStoreDB));
        config.setUser(settings.get(ConfigTags.DataStoreUser));
        config.setPassword(settings.get(ConfigTags.DataStorePassword));
        config.setResetDataStore(settings.get(ConfigTags.ResetDataStore));
        config.setDatabaseDirectory(settings.get(ConfigTags.DataStoreDirectory));
        EntityManager entityManager = new EntityManager(config);

        // check if the data needs to be stored instantaneously or delayed (after sequence).
        PersistenceManager persistenceManager;
        switch (settings.get(ConfigTags.DataStoreMode)) {
            case PersistenceManager.DATA_STORE_MODE_DELAYED:
                persistenceManager = new QueueManager(new OrientDBManager(eventHelper, entityManager), new EventHelper(), false);
                break;

            case PersistenceManager.DATA_STORE_MODE_HYBRID:
                persistenceManager = new QueueManager(new OrientDBManager(eventHelper, entityManager), new EventHelper(), true);
                break;

            case PersistenceManager.DATA_STORE_MODE_INSTANT:
             default:
                 persistenceManager = new OrientDBManager(eventHelper, entityManager);

        }
        return persistenceManager;
    }

}
