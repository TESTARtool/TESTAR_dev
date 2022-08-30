package org.testar.statemodel.persistence;

import org.testar.monkey.Settings;

public class DummyManagerFactory implements PersistenceManagerFactory {

    @Override
    public PersistenceManager getPersistenceManager(Settings settings) {
        return new DummyManager();
    }
}
