package nl.ou.testar.StateModel.Persistence;

import org.fruit.monkey.Settings;

public interface PersistenceManagerFactory {

    PersistenceManager getPersistenceManager(Settings settings);

}
