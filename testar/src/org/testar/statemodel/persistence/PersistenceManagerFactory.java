package org.testar.statemodel.persistence;

import org.testar.settings.Settings;

public interface PersistenceManagerFactory {

    PersistenceManager getPersistenceManager(Settings settings);

}
