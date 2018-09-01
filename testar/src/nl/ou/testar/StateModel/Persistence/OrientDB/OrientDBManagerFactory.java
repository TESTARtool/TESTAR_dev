package nl.ou.testar.StateModel.Persistence.OrientDB;

import nl.ou.testar.StateModel.Persistence.DummyManager;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.EntityManager;
import nl.ou.testar.StateModel.Persistence.PersistenceManager;
import nl.ou.testar.StateModel.Persistence.PersistenceManagerFactory;
import nl.ou.testar.StateModel.Util.EventHelper;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

public class OrientDBManagerFactory implements PersistenceManagerFactory {

    @Override
    public PersistenceManager getPersistenceManager(Settings settings) {
        // not enabled means we feed it a dummy manager
        if (!settings.get(ConfigTags.GraphDBEnabled))  {
            return new DummyManager();
        }

        EventHelper eventHelper = new EventHelper();
        EntityManager entityManager = new EntityManager(
                settings.get(ConfigTags.GraphDBUrl),
                settings.get(ConfigTags.GraphDBUser),
                settings.get(ConfigTags.GraphDBPassword)
        );
        return new OrientDBManager(eventHelper, entityManager);
    }

}
