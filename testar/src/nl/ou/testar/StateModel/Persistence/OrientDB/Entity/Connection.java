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

package nl.ou.testar.StateModel.Persistence.OrientDB.Entity;

import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;

public class Connection {

    /**
     * A configuration object
     */
    private Config config;

    /**
     * The orientDB instance that provides access to the orientdb data store
     */
    private OrientDB orientDB;

    public Connection(OrientDB orientDB, Config config) {
        this.orientDB = orientDB;
        this.config = config;
        // https://github.com/orientechnologies/orientdb/blob/master/core/src/main/java/com/orientechnologies/orient/core/config/OGlobalConfiguration.java
        // Because multiple and simultaneous connections are hanging the orientdb session (distributed inference)
        // Set max Request completion timeout to 5s, 3 tries
        OGlobalConfiguration.NETWORK_REQUEST_TIMEOUT.setValue(5000);
        OGlobalConfiguration.NETWORK_SOCKET_RETRY.setValue(3);
    }

    /**
     * This method fetches a database session to the configured data store instance.
     * @return
     */
    public ODatabaseSession getDatabaseSession() {
        ODatabaseSession dbSession = orientDB.open(config.getDatabase(), config.getUser(), config.getPassword());
        return dbSession;
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
