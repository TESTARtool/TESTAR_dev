/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.persistence.orientdb;

import org.testar.statemodel.persistence.orientdb.entity.Config;
import org.testar.statemodel.persistence.orientdb.entity.EntityManager;
import org.testar.config.StateModelTags;
import org.testar.statemodel.persistence.PersistenceManager;
import org.testar.statemodel.persistence.PersistenceManagerFactory;
import org.testar.statemodel.persistence.QueueManager;
import org.testar.statemodel.util.EventHelper;
import org.testar.core.tag.TaggableBase;

public class OrientDBManagerFactory implements PersistenceManagerFactory {

    @Override
    public PersistenceManager getPersistenceManager(TaggableBase configTags) {
        EventHelper eventHelper = new EventHelper();

        // create a config object for the orientdb database connection info
        Config config = new Config();
        config.setConnectionType(configTags.get(StateModelTags.DataStoreType));
        config.setServer(configTags.get(StateModelTags.DataStoreServer));
        config.setDatabase(configTags.get(StateModelTags.DataStoreDB));
        config.setUser(configTags.get(StateModelTags.DataStoreUser));
        config.setPassword(configTags.get(StateModelTags.DataStorePassword));
        config.setResetDataStore(configTags.get(StateModelTags.ResetDataStore));
        config.setDatabaseDirectory(configTags.get(StateModelTags.DataStoreDirectory));
        EntityManager entityManager = new EntityManager(config);

        // check if the data needs to be stored instantaneously or delayed (after sequence).
        PersistenceManager persistenceManager;
        switch (configTags.get(StateModelTags.DataStoreMode)) {
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
