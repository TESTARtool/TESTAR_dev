package nl.ou.testar.StateModel.Persistence.OrientDB;

import nl.ou.testar.StateModel.Persistence.DummyManager;
import nl.ou.testar.StateModel.Persistence.PersistenceManager;
import nl.ou.testar.StateModel.Persistence.PersistenceManagerFactory;
import org.fruit.monkey.Settings;

public class DummyManagerFactory implements PersistenceManagerFactory {

    @Override
    public PersistenceManager getPersistenceManager(Settings settings) {
        return new DummyManager();
    }
}
