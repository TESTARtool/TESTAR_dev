/***************************************************************************************************
 *
 * Copyright (c) 2021 - 2022 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2021 - 2022 Open Universiteit - www.ou.nl
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

package org.testar.distributed;

import org.fruit.monkey.Settings;

import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.exception.OConcurrentModificationException;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import nl.ou.testar.StateModel.Persistence.OrientDB.OrientDBManagerFactory;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;

public class SharedDatabase {

	private SharedDatabase() {}

	/**
	 * Open a session connection with testar database. 
	 *
	 * @param settings the current TESTAR settings as specified by the user.
	 */
	public static ODatabaseSession createDatabaseConnection(Settings settings, OrientDB database) {
		Config config = OrientDBManagerFactory.getDatabaseConfig(settings);
		// Do I need this here? Probably not because global configuration set in Connection
		OGlobalConfiguration.NETWORK_REQUEST_TIMEOUT.setValue(5000);
		OGlobalConfiguration.NETWORK_SOCKET_RETRY.setValue(3);
		ODatabaseSession dbSession = database.open(config.getDatabase(), config.getUser(), config.getPassword());
		return dbSession;
	}

	/**
	 * Execute a command in the OrientDB to create or update state model entities. 
	 * 
	 * @param db
	 * @param command
	 * @return
	 */
	public static OResultSet executeCommand(ODatabaseSession db, String command) {
		System.out.println("Shared protocol ExecuteCommand: " + command);
		// TODO: Probably add a max of tries
		boolean repeat = false;
		do {
			repeat = false;
			try {
				return db.command(command);
			} catch (OConcurrentModificationException ex) {
				repeat = true;
				try {
					Thread.sleep(1000);
				} catch (Exception e) {}
			}
		} while (repeat);

		return null;
	}

	/**
	 * Execute a query in the OrientDB to obtain an entity information result. 
	 * 
	 * @param db
	 * @param query
	 * @return
	 */
	public static OResultSet executeQuery(ODatabaseSession db, String query) {
		System.out.println("Shared protocol ExecuteQuery: " + query);
		// TODO: Probably add a max of tries
		boolean repeat = false;
		do {
			repeat = false;
			try {
				return db.query(query);
			} catch (OConcurrentModificationException ex) {
				repeat = true;
				try {
					Thread.sleep(1000);
				} catch (Exception e) {}
			}
		} while (repeat);

		return null;
	}

	/**
	 * Count the number of existing entities in the OrientDB. 
	 * 
	 * @param entity
	 * @param settings
	 * @param database
	 * @return
	 */
	public static long countInDb(String entity, Settings settings, OrientDB database) {
		long number = 0;
		String sql = "SELECT count(*) as number from " + entity;
		try (ODatabaseSession db = createDatabaseConnection(settings, database)) {
			OResultSet rs = executeQuery(db, sql);
			OResult item = rs.next();
			number = item.getProperty("number");
			rs.close();
			System.out.println(sql + "  number =  " + number);
		} catch (Exception e) {
			System.out.println("Exception CountInDb database query");
		}

		return number;
	}
}
