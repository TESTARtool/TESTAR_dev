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

import java.util.ArrayList;
import java.util.Random;

import org.fruit.monkey.Settings;

import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.exception.OConcurrentModificationException;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.orientechnologies.orient.core.storage.ORecordDuplicatedException;

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

	/**
	 * From the detected available UnvisitedAbstractActions, select one of them randomly to be executed. 
	 * Then mark this unvisited action as BeingExecuted to avoid other instances to select it. 
	 * If something goes wrong trying to mark the action, return null. 
	 * 
	 * @param unvisitedActions
	 * @param nodeName
	 * @param settings
	 * @param database
	 * @return
	 */
	public static String selectRandomUnvisitedAction(ArrayList<String> unvisitedActions, String nodeName, Settings settings, OrientDB database) {
		long graphTime = System.currentTimeMillis();
		Random rnd = new Random(graphTime);

		String actionId = unvisitedActions.get(rnd.nextInt(unvisitedActions.size()));
		System.out.println("Update action " + actionId + " from BlackHole to BeingExecuted");
		return UpdateUnvisitedActionToBeingExecuted(actionId, nodeName, settings, database);
	}

	private static String UpdateUnvisitedActionToBeingExecuted(String actionId, String nodeName, Settings settings, OrientDB database) {
		try (ODatabaseSession db = createDatabaseConnection(settings, database)) {
			String sql = "update edge UnvisitedAbstractAction set in = (SELECT FROM BeingExecuted WHERE node='" + nodeName + "') where actionId='" + actionId + "'";
			executeCommand(db, sql);
		} catch (Exception e) {
			// We return null here to be checked in SharedProtocol, then we will need to set targetSharedAction to null
			return null;
		}
		return actionId;
	}

	/**
	 * Return the target shared action selected previously back to the black hole vertex. 
	 * This will make the action an UnvisitedAbstractAction to be chosen by other instances. 
	 * TODO: This method assumes that there is only one state model (one black hole) per database
	 * 
	 * @param settings
	 * @param targetSharedAction
	 * @param database
	 */
	public static void returnActionToBlackHole(Settings settings, String targetSharedAction, OrientDB database) {
		try (ODatabaseSession db = createDatabaseConnection(settings, database)) {
			// TODO: This query assumes that there is only one state model (one black hole) per database 
			String sql = "update edge UnvisitedAbstractAction set in = (select from BlackHole) where actionId='" + targetSharedAction + "'";
			System.out.println("Return action to BlackHole from BeingExecuted: " + targetSharedAction + " sql = " + sql);
			executeCommand(db, sql);
		} catch (Exception e) {
			System.out.println("Not possible to return targetSharedAction " + targetSharedAction + " to BlackHole : " + e);
		}
	}

	public static void markActionAsNonDeterministic(String actionId, String expectedStateId, Settings settings, OrientDB database) {
		// TODO: Here we create one NonDeterministic State for the expected state
		// TODO: But probably we also need to create a second NonDeterministic State for the current found state
		try (ODatabaseSession db = createDatabaseConnection(settings, database)) {
			// CREATE VERTEX NonDeterministic SET stateId='SAC1kwnxuh4cf2567147868'
			String sql = "create vertex NonDeterministic SET stateId='" + expectedStateId + "'";
			executeCommand(db, sql);
			// update edge AbstractAction set in = (select from NonDeterministic where stateId='SAC1sp28jf63d3211669788') where actionId='AAC1ot1fnm353944349410'
			sql = "update edge AbstractAction set in = (select from NonDeterministic where stateId='" + expectedStateId + "') where actionId='" + actionId + "'";
			executeCommand(db, sql);
		} catch (Exception e) {
			System.out.println("SharedProtocol: Not possible to create the NonDeterministic stateId " + expectedStateId + " for actionId : " + actionId);
		}

		// Now we need to change the uid properties of the non deterministic action to indicate that are non deterministic 
		// This will allow to create future AbstractAction in the state model (because uid identifies source + target + actionId)
		OResultSet rs = null;
		try (ODatabaseSession db = createDatabaseConnection(settings, database)) {
			String sql = "select uid from abstractaction where actionId='" + actionId + "'";
			rs = executeQuery(db, sql);

			while (rs.hasNext()) {
				String uid = rs.next().toString().replace("\n", "").trim();
				// {uid: 16q1mx4113647030708} to 16q1mx4113647030708
				uid = uid.replace("{", "").replace("}", "").trim().split(":")[1].trim();
				String uidNonDet = "NonDet_" + uid;
				if(!uid.contains("NonDet_")) {
					try {
						// Indicate that this abstract action is non deterministic (uid)
						sql = "update AbstractAction SET uid='" + uidNonDet + "' WHERE uid='" + uid + "'";
						executeCommand(db, sql);
					} catch (ORecordDuplicatedException orde) {
						// If duplicated key detected, means that was already marked as non deterministic, just delete
						System.out.println("Non deterministic uid action already exists : " + uidNonDet);
						sql = "delete edge AbstractAction WHERE uid='" + uid + "'";
						executeCommand(db, sql);
					}
				}
			}

		} catch (Exception e) {
			System.out.println("SharedProtocol: Exception changing uid of non non deterministic actions");
			e.printStackTrace();
		} finally {
			if(rs != null) rs.close();
		}
	}

}
