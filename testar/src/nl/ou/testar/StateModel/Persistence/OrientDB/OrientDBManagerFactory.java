package nl.ou.testar.StateModel.Persistence.OrientDB;

import nl.ou.testar.StateModel.Persistence.DummyManager;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.EntityManager;
import nl.ou.testar.StateModel.Persistence.PersistenceManager;
import nl.ou.testar.StateModel.Persistence.PersistenceManagerFactory;
import nl.ou.testar.StateModel.Persistence.QueueManager;
import nl.ou.testar.StateModel.Util.EventHelper;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

public class OrientDBManagerFactory implements PersistenceManagerFactory {


    public static Config getDatabaseConfig(Settings settings)
    {
        Config config = new Config();
        config.setConnectionType(settings.get(ConfigTags.DataStoreType));
        config.setServer(settings.get(ConfigTags.DataStoreServer));
        config.setDatabase(settings.get(ConfigTags.DataStoreDB));
        config.setUser(settings.get(ConfigTags.DataStoreUser));
        config.setPassword(settings.get(ConfigTags.DataStorePassword));
        config.setResetDataStore(settings.get(ConfigTags.ResetDataStore));
        config.setDatabaseDirectory(settings.get(ConfigTags.DataStoreDirectory));
        return config;
    }
    @Override
    public PersistenceManager getPersistenceManager(Settings settings) {
        EventHelper eventHelper = new EventHelper();

        // create a config object for the orientdb database connection info
       
        EntityManager entityManager = new EntityManager(OrientDBManagerFactory.getDatabaseConfig(settings));

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
