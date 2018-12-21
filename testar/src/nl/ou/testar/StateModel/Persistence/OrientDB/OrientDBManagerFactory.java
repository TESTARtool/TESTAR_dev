package nl.ou.testar.StateModel.Persistence.OrientDB;

import nl.ou.testar.StateModel.Persistence.DummyManager;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.EntityManager;
import nl.ou.testar.StateModel.Persistence.PersistenceManager;
import nl.ou.testar.StateModel.Persistence.PersistenceManagerFactory;
import nl.ou.testar.StateModel.Util.EventHelper;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

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
        EntityManager entityManager = new EntityManager(config);

        return new OrientDBManager(eventHelper, entityManager);
    }

}
