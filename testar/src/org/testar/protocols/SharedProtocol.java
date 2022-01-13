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

package org.testar.protocols;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.actions.WdHistoryBackAction;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.exception.OConcurrentModificationException;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.ODirection;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

import nl.ou.testar.StateModel.ModelManager;
import nl.ou.testar.StateModel.Persistence.OrientDB.OrientDBManager;
import nl.ou.testar.StateModel.Persistence.OrientDB.OrientDBManagerFactory;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.EntityManager;

public class SharedProtocol extends WebdriverProtocol {

	// OrientDB variables
	protected OrientDBManager orientDbManager;
	protected ModelManager modelManager;
	protected String nodeName = "";
	protected EntityManager entityManager;
	// protected Connection connection;
	protected OrientDB database;

	// Shared Action variables
	protected String targetSharedAction = null;
	protected boolean moreSharedActions = true;
	protected boolean stopSharedProtocol = false;

	@Override
	protected void initialize(Settings settings) {
		super.initialize(settings);

		// Shared State Model mode only works with remote DB connection,
		// Only one TESTAR instance will run per environment, so not plocal allowed
		if(!settings.get(ConfigTags.DataStoreType).equals(Config.CONNECTION_TYPE_REMOTE)) {
			throw new IllegalArgumentException("Shared State Model protocol only works with DataStoreType remote mode");
		}

		System.out.println("Shared protocol constructor");

		modelManager = (ModelManager) stateModelManager;
		if (modelManager.persistenceManager != null) {
			orientDbManager = (OrientDBManager) modelManager.persistenceManager;
			entityManager = orientDbManager.entityManager;
			Config config = OrientDBManagerFactory.getDatabaseConfig(settings);
			String connectionString = config.getConnectionType() + ":" + (config.getConnectionType().equals("remote") ? config.getServer() : config.getDatabaseDirectory()) + "/";
			database = new OrientDB(connectionString, OrientDBConfig.defaultConfig());

			// Open a database connection to create a BeingExecuted vertex for this TESTAR instance
			Random r = new Random();
			nodeName = System.getenv("HOSTNAME") + "_" + r.nextInt(10000);
			System.out.println("nodeName = " + nodeName);
			ODatabaseSession dbSession = createDatabaseConnection(settings);
			ExecuteCommand(dbSession, "create vertex BeingExecuted set node = '" + nodeName + "'").close();
			dbSession.close();
		}
	}

	/**
	 * Called once during the life time of TESTAR this method can be used to perform 
	 * initial setup work.
	 *
	 * @param settings the current TESTAR settings as specified by the user.
	 */
	private ODatabaseSession createDatabaseConnection(Settings settings) {
		Config config = OrientDBManagerFactory.getDatabaseConfig(settings);
		ODatabaseSession dbSession = database.open(config.getDatabase(), config.getUser(), config.getPassword());
		System.out.println("Shared protocol: database connection opened");
		return dbSession;
	}

	/**
	 * Execute a command in the OrientDB to create or update state model entities. 
	 * 
	 * @param db
	 * @param command
	 * @return
	 */
	private OResultSet ExecuteCommand(ODatabaseSession db, String command) {
		System.out.println("Shared protocol ExecuteCommand: " + command);
		// TODO: Probably add a max of tries
		boolean repeat = false;
		do {
			repeat = false;
			try {
				//System.out.println("dbSession = " + db);
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
	private OResultSet ExecuteQuery(ODatabaseSession db, String query) {
		System.out.println("Shared protocol ExecuteQuery: " + query);
		// TODO: Probably add a max of tries
		boolean repeat = false;
		do {
			repeat = false;
			try {
				//System.out.println("dbSession = " + db);
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
	 * @return
	 */
	protected long CountInDb(String entity) {
		long number = 0;
		String sql = "SELECT count(*) as number from " + entity;
		ODatabaseSession db = createDatabaseConnection(settings);
		try {
			OResultSet rs = ExecuteQuery(db, sql);
			OResult item = rs.next();
			number = item.getProperty("number");
			rs.close();
			System.out.println(sql + "  number =  " + number);
		} finally {
			db.close();
		}

		return number;
	}

	/**
	 * Create a String,Action Map. 
	 * 
	 * @param actions
	 * @return
	 */
	protected HashMap<String, Action> ConvertActionSetToDictionary(Set<Action> actions) {
		System.out.println("SharedProtocol: Convert Set<Action> to HashMap containing actionIds as keys");
		HashMap<String, Action> actionMap = new HashMap<>();
		ArrayList<Action> actionList = new ArrayList<>(actions);
		for (Action a : actionList) {
			System.out.println("Add action " + a.get(Tags.AbstractIDCustom) + " to actionMap; description = " + a.get(Tags.Desc));
			actionMap.put(a.get(Tags.AbstractIDCustom), a);
		}
		System.out.println("actionMap.size() = " + actionMap.size());
		return actionMap;
	}

	protected String getNewTargetSharedAction(State state, Set<Action> actions) {
		String result = null;
		System.out.println("SharedProtocol: getNewTargetSharedAction state = " + state.get(Tags.AbstractIDCustom));
		boolean availableAction = false;
		do {
			try {
				ArrayList<String> availableActionsFromDb = GetUnvisitedActionsFromDatabase(state.get(Tags.AbstractIDCustom));
				System.out.println("Number of shortest path actions available in database: " + availableActionsFromDb.size());

				if (availableActionsFromDb.size() >= 1) {
					availableAction = true;
					String action = selectRandomActionFromDB(availableActionsFromDb);
					System.out.println("Action from database selected: action = " + action);
					return action;
				}

				// We have no unvisited actions to execute, but maybe because we are in the first action step
				long numberAbstractStates = CountInDb("AbstractState");
				System.out.println("No actions available in database; abstract states in database = " + numberAbstractStates);

				if (numberAbstractStates == 0) {
					Action a = super.selectAction(state, actions);
					String action = a.get(Tags.AbstractIDCustom);
					System.out.println("Since this is the first run and no abstract actions exists take a random action; statemodel is probably lagging action = " + action);
					return action;
				}

				// If we are here is because we have no more unvisited actions to execute
				moreSharedActions = false;
				stopSharedProtocol = true;
				Action a = super.selectAction(state, actions);
				String action = a.get(Tags.AbstractIDCustom);
				System.out.println("Just return random action and stop; action = " + action);
				return action;

			} catch (Exception e) {
				System.out.println("SharedProtocol: Exception during getNewTargetSharedAction " + e);
				//int sleepTime = new Random(System.currentTimeMillis()).nextInt(5000);
				//System.out.println("Exception while getting action; Wait " + sleepTime + " ms " + e);
				availableAction = false;
				try {
					Thread.sleep(1000);
				} catch (Exception th) {}
			}
		} while (!availableAction);

		return result;
	}

	private ArrayList<String> GetUnvisitedActionsFromDatabase(String currentAbstractState) {
		System.out.println("SharedProtocol: GetUnvisitedActionsFromDatabase");
		ArrayList<String> result = new ArrayList<>();
		// https://orientdb.org/docs/3.1.x/gettingstarted/demodb/queries/DemoDB-Queries-Shortest-Paths.html
		// TODO: This query assumes that there is only one state model (one black hole) per database

		// We only consider OUT edges to follow Action execution direction path
		String sql = "SELECT expand(path) FROM (SELECT shortestPath($from, $to, 'OUT') AS path LET $from = (SELECT FROM AbstractState WHERE stateId='"+ currentAbstractState + "'), "
				+ "$to = (SELECT FROM BlackHole) UNWIND path)";

		// TODO: Add state model identifier or black hole identifier to fix this query
		// SELECT expand(path) FROM (SELECT shortestPath($from, $to) AS path LET $from=(SELECT FROM abstractstate WHERE stateId='SACd588mu1b22740930749'), 
		// $to=(SELECT FROM BlackHole where blackHoleId='jp3leu283079445017') UNWIND path)

		OResultSet rs = null;
		ODatabaseSession db = createDatabaseConnection(settings);
		try {
			rs = ExecuteQuery(db, sql);

			while (rs.hasNext()) {
				OResult item = rs.next();
				if (item.isVertex()) {
					Optional<OVertex> optionalVertex = item.getVertex();
					OVertex nodeVertex = optionalVertex.get();

					for (OEdge edge : nodeVertex.getEdges(ODirection.OUT, "UnvisitedAbstractAction")) {
						result.add(edge.getProperty("actionId"));
						//System.out.println("Edge " + edge + " found with ID = " + edge.getProperty("actionId"));
					}
				}
			}
		} catch (Exception e) {
			System.out.println("SharedProtocol: Exception during GetUnvisitedActionsFromDatabase");
			e.printStackTrace();
		} finally {
			rs.close();
			db.close();
		}

		System.out.println("SharedProtocol: DONE GetUnvisitedActionsFromDatabase");
		return result;
	}

	private String selectRandomActionFromDB(ArrayList<String> actions) {
		long graphTime = System.currentTimeMillis();
		Random rnd = new Random(graphTime);

		String ac = actions.get(rnd.nextInt(actions.size()));
		UpdateAbstractActionToBeingExecuted(ac);
		System.out.println("Update action " + ac + " from BlackHole to BeingExecuted");
		return ac;
	}

	private void UpdateAbstractActionToBeingExecuted(String actionId) {
		ODatabaseSession db = createDatabaseConnection(settings);
		try {
			String sql = "update edge UnvisitedAbstractAction set in = (SELECT FROM BeingExecuted WHERE node='" + nodeName + "') where actionId='" + actionId + "'";
			ExecuteCommand(db, sql);
		} catch (Exception e) {
			System.out.println("Can not update unvisitedAbstractAction; set targetSharedAction to null");
			targetSharedAction = null;
		} finally {
			db.close();
		}
	}

	protected Action traversePath(State state, Set<Action> actions) {
		// TODO: Check if this is a problematic query if multiple state models exists in the same database
		String destStateQuery = "select stateId from AbstractState where @rid in (select outV() from UnvisitedAbstractAction where actionId = '" + targetSharedAction + "')";

		String destinationStateId = "";
		ODatabaseSession db = createDatabaseConnection(settings);

		OResultSet destinationStatResultSet = ExecuteQuery(db, destStateQuery);
		if (destinationStatResultSet.hasNext()) {
			OResult item = destinationStatResultSet.next();
			destinationStateId = item.getProperty("stateId");
			System.out.println("traversePath: way to state " + destinationStateId);
			destinationStatResultSet.close();
			db.close();
		} else {
			System.out.println("traversePath: State is stuck because no unvisited action found; set targetSharedAction to null; run historyback now");
			destinationStatResultSet.close();
			ReturnActionToBlackHole();
			targetSharedAction = null;
			Action histBackAction = new WdHistoryBackAction();
			buildEnvironmentActionIdentifiers(state, histBackAction);
			return histBackAction;
		}

		// get stateId from destStateQuery

		// SELECT @rid, stateId from (
		// SELECT expand(path) FROM ( SELECT shortestPath($from,
		// $to,'OUT','AbstractAction') AS path LET $from = (SELECT FROM abstractstate
		// WHERE stateId='SAC1jp4oysed31697927673'), $to = (SELECT FROM abstractstate
		// Where stateId='SACwpszr27b61710690312') UNWIND path))
		String stateRidQuery = "SELECT @rid, stateId from (SELECT expand(path) FROM (SELECT shortestPath($from, $to,'OUT','AbstractAction') "
				+ "AS path LET $from = (SELECT FROM AbstractState WHERE stateId='" + state.get(Tags.AbstractIDCustom) + "'), "
				+ "$to = (SELECT FROM AbstractState Where stateId='" + destinationStateId + "') UNWIND path))";
		db = createDatabaseConnection(settings);
		OResultSet pathResultSet = ExecuteQuery(db, stateRidQuery);
		Vector<TmpData> v = new Vector<>();

		while (pathResultSet.hasNext()) {
			OResult item = pathResultSet.next();
			v.add(new TmpData(item.getProperty("@rid"), item.getProperty("stateId")));
		}
		pathResultSet.close();
		db.close();

		if (v.size() < 2) {
			System.out.println("traversePath: There is no path! Execute super.selectAction; Also end sequence by setting moreSharedActions=false");
			ReturnActionToBlackHole();
			targetSharedAction = null;
			moreSharedActions = false;
			return super.selectAction(state, actions);
		}

		// Find an AbstractAction to perform
		String abstActQuery = "select from AbstractAction where out = " + v.get(0).rid + " and in = " + v.get(1).rid;

		String abstractActionId = "";
		HashMap<String, Action> availableActions = ConvertActionSetToDictionary(actions);
		db = createDatabaseConnection(settings);
		OResultSet abstractActionResultSet = ExecuteQuery(db, abstActQuery);
		while (abstractActionResultSet.hasNext()) {
			abstractActionId = abstractActionResultSet.next().getProperty("actionId");
			System.out.println("traversePath: Check if " + abstractActionId + " is available");

			if (availableActions.containsKey(abstractActionId)) {
				System.out.println("traversePath: Action " + abstractActionId + " is available in the avilableActions; this is being executed");
				abstractActionResultSet.close();
				db.close();
				return availableActions.get(abstractActionId);
			}
		}
		abstractActionResultSet.close();
		db.close();

		System.out.println("traversePath: Action that needs to be made does not exist");
		ReturnActionToBlackHole();
		targetSharedAction = null;

		return super.selectAction(state, actions);
	}

	private void ReturnActionToBlackHole() {
		ODatabaseSession db = createDatabaseConnection(settings);
		try {
			// TODO: This query assumes that there is only one state model (one black hole) per database 
			String sql = "update UnvisitedAbstractAction set in = (select from BlackHole) where actionId='" + targetSharedAction + "'";
			System.out.println("Return action to BlackHole from BeingExecuted: " + targetSharedAction + " sql = " + sql);
			ExecuteCommand(db, sql);
		} catch (Exception e) {
			System.out.println("Not possible to return targetSharedAction " + targetSharedAction + " to BlackHole : " + e);
		} finally {
			db.close();
		}
	}

	class TmpData {
		public TmpData(ORecordId rid, String stateId) {
			this.stateId = stateId;
			this.rid = rid.toString();
		}

		public String stateId;
		public String rid;
	}

}
