/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2025 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.statemodel.persistence.orientdb;

import org.testar.statemodel.persistence.orientdb.entity.Config;
import org.testar.statemodel.persistence.orientdb.entity.EntityManager;
import org.testar.statemodel.StateModelTags;
import org.testar.statemodel.persistence.PersistenceManager;
import org.testar.statemodel.persistence.PersistenceManagerFactory;
import org.testar.statemodel.persistence.QueueManager;
import org.testar.statemodel.util.EventHelper;
import org.testar.monkey.alayer.TaggableBase;

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
