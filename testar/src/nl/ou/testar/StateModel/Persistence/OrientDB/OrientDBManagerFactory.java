/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2021 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2021 Universitat Politecnica de Valencia - www.upv.es
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

package nl.ou.testar.StateModel.Persistence.OrientDB;

import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.EntityManager;
import nl.ou.testar.StateModel.Persistence.PersistenceManager;
import nl.ou.testar.StateModel.Persistence.PersistenceManagerFactory;
import nl.ou.testar.StateModel.Persistence.QueueManager;
import nl.ou.testar.StateModel.Util.EventHelper;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

public class OrientDBManagerFactory implements PersistenceManagerFactory {

    public static Config getDatabaseConfig(Settings settings) {
        Config config = new Config();
        config.setConnectionType(settings.get(ConfigTags.DataStoreType));
        config.setServer(settings.get(ConfigTags.DataStoreServer));
        config.setDatabase(settings.get(ConfigTags.DataStoreDB));
        config.setUser(settings.get(ConfigTags.DataStoreUser));
        config.setPassword(settings.get(ConfigTags.DataStorePassword));
        config.setResetDataStore(settings.get(ConfigTags.ResetDataStore));
        config.setDatabaseDirectory(settings.get(ConfigTags.DataStoreDirectory));
        return config;
    }

    @Override
    public PersistenceManager getPersistenceManager(Settings settings) {
        EventHelper eventHelper = new EventHelper();

        // create a config object for the orientdb database connection info
        EntityManager entityManager = new EntityManager(OrientDBManagerFactory.getDatabaseConfig(settings));

        // check if the data needs to be stored instantaneously or delayed (after sequence).
        PersistenceManager persistenceManager;
        switch (settings.get(ConfigTags.DataStoreMode)) {
          case PersistenceManager.DATA_STORE_MODE_DELAYED:
            persistenceManager = new QueueManager(new OrientDBManager(eventHelper, entityManager, settings), new EventHelper(), false);
            break;

          case PersistenceManager.DATA_STORE_MODE_HYBRID:
            persistenceManager = new QueueManager(new OrientDBManager(eventHelper, entityManager, settings), new EventHelper(), true);
            break;

          case PersistenceManager.DATA_STORE_MODE_INSTANT:
          default:
            persistenceManager = new OrientDBManager(eventHelper, entityManager, settings);
        }
        return persistenceManager;
    }

}
