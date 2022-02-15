package org.testar.statemodelling.persistence;

import org.testar.monkey.Settings;

public interface PersistenceManagerFactory {

    PersistenceManager getPersistenceManager(Settings settings);

}
