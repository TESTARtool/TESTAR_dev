/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.persistence.orientdb.entity;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;

public class Connection {

    /**
     * A configuration object
     */
    private final Config config;

    /**
     * The orientDB instance that provides access to the orientdb data store
     */
    private final OrientDB orientDB;

    public Connection(OrientDB orientDB, Config config) {
        this.orientDB = orientDB;
        this.config = config;
    }

    /**
     * This method fetches a database session to the configured data store instance.
     * @return
     */
    public ODatabaseSession getDatabaseSession() {
        return orientDB.open(config.getDatabase(), config.getUser(), config.getPassword());
    }

    /**
     * This method tells the connection object to close the connection to the datastore instance.
     */
    void releaseConnection() {
        if (orientDB.isOpen()) {
            orientDB.close();
        }
    }

    /**
     * Returns the configuration object for this connection.
     * @return
     */
    public Config getConfig() {
        return config;
    }

}
