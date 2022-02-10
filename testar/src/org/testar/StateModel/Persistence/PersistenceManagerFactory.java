package org.testar.StateModel.Persistence;

import org.testar.monkey.Settings;

public interface PersistenceManagerFactory {

    PersistenceManager getPersistenceManager(Settings settings);

}
