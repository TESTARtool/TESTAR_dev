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
import java.util.Optional;
import java.util.Random;

import org.fruit.monkey.Settings;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.record.ODirection;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.orientechnologies.orient.core.storage.ORecordDuplicatedException;

public class SharedNonDeterminism {

	private SharedNonDeterminism() {}

	public static ArrayList<String> getNonDeterministicActionsFromDatabase(String currentAbstractState, Settings settings, OrientDB database) {
		System.out.println("SharedDatabase: getNonDeterministicActionsFromDatabase");
		ArrayList<String> result = new ArrayList<>();
		// https://orientdb.org/docs/3.1.x/gettingstarted/demodb/queries/DemoDB-Queries-Shortest-Paths.html
		// TODO: This query assumes that there is only one state model per database
		// TODO: Add state model identifier or non deterministic hole identifier to fix this query

		// We only consider OUT edges to follow Action execution direction path
		String sql = "SELECT expand(path) FROM (SELECT shortestPath($from, $to, 'OUT') AS path LET $from = (SELECT FROM AbstractState WHERE stateId='" + currentAbstractState + "'), "
				+ "$to = (SELECT FROM NonDeterministicHole) UNWIND path)";

		OResultSet rs = null;
		try (ODatabaseSession db = SharedDatabase.createDatabaseConnection(settings, database)) {
			rs = SharedDatabase.executeQuery(db, sql);

			while (rs.hasNext()) {
				OResult item = rs.next();
				if (item.isVertex()) {
					Optional<OVertex> optionalVertex = item.getVertex();
					OVertex nodeVertex = optionalVertex.get();
					// Only check the vertex of the path that is NonDeterministicHole (we use nonDeterministicHoleId property to identify NonDeterministicHole)
					if(nodeVertex.getProperty("nonDeterministicHoleId") != null && !nodeVertex.getProperty("nonDeterministicHoleId").toString().isEmpty()) {
						// Then check the AbstractActions that enter this NonDeterministicHole
						for (OEdge edge : nodeVertex.getEdges(ODirection.IN, "AbstractAction")) {
							// For non determinism actions we have duplicated actionIds because one action leads to two or more states
							// So just add it if does not exist in the list
							if(!result.contains(edge.getProperty("actionId"))) { result.add(edge.getProperty("actionId")); }
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("SharedDatabase: Exception during getNonDeterministicActionsFromDatabase");
			e.printStackTrace();
		} finally {
			if(rs != null) { rs.close(); }
		}

		return result;
	}

	public static String selectRandomNonDeterministicAction(ArrayList<String> nonDeterministicActions, String nodeName, Settings settings, OrientDB database) {
		long graphTime = System.currentTimeMillis();
		Random rnd = new Random(graphTime);

		String actionId = nonDeterministicActions.get(rnd.nextInt(nonDeterministicActions.size()));
		System.out.println("Update nonDeterministicActions " + actionId + " from NonDeterministicHole to BeingExecuted");
		return updateNonDeterministicActionToBeingExecuted(actionId, nodeName, settings, database);
	}

	private static String updateNonDeterministicActionToBeingExecuted(String actionId, String nodeName, Settings settings, OrientDB database) {
		try (ODatabaseSession db = SharedDatabase.createDatabaseConnection(settings, database)) {
			// TODO: Update only the AbstractAction edge that is going to NonDeterministicHole and not to NonDeterministicAction
			// TODO: So add filtering to this query
			String sql = "update edge AbstractAction set in = (SELECT FROM BeingExecuted WHERE node='" + nodeName + "') where actionId='" + actionId + "'";
			SharedDatabase.executeCommand(db, sql);
		} catch (Exception e) {
			// We return null here to be checked in SharedProtocol, then we will need to set targetSharedAction to null
			return null;
		}
		return actionId;
	}

	public static void returnActionToNonDeterministicHole(Settings settings, String targetSharedAction, OrientDB database) {
		try (ODatabaseSession db = SharedDatabase.createDatabaseConnection(settings, database)) {
			// TODO: Update only the AbstractAction edge that is mark as BeingExecuted and not those from NonDeterministicAction vertex
			// TODO: So add filtering to this query
			String sql = "update edge AbstractAction set in = (select from NonDeterministicHole) where actionId='" + targetSharedAction + "'";
			System.out.println("Return action to NonDeterministicHole from BeingExecuted: " + targetSharedAction + " sql = " + sql);
			SharedDatabase.executeCommand(db, sql);
		} catch (Exception e) {
			System.out.println("Not possible to return targetSharedAction " + targetSharedAction + " to NonDeterministicHole : " + e);
		}
	}

	public static void cleanNonDeterministicActionFromHole(Settings settings, String targetSharedAction, OrientDB database) {
		try (ODatabaseSession db = SharedDatabase.createDatabaseConnection(settings, database)) { 
			// TODO: Delete only the AbstractAction edge that is mark as BeingExecuted and not those from NonDeterministicAction vertex
			String sql = "delete edge AbstractAction where actionId='" + targetSharedAction + "'";
			System.out.println("Clean NonDeterministic action because we already found from BeingExecuted: " + targetSharedAction + " sql = " + sql);
			SharedDatabase.executeCommand(db, sql);
		} catch (Exception e) {
			System.out.println("Not possible to return targetSharedAction " + targetSharedAction + " to NonDeterministic : " + e);
		}
	}

	public static void markActionAsNonDeterministic(String actionId, String expectedStateId, Settings settings, OrientDB database) {
		// TODO: Here we create one NonDeterministic State for the expected state
		// TODO: But probably we also need to create a second NonDeterministic State for the current found state
		try (ODatabaseSession db = SharedDatabase.createDatabaseConnection(settings, database)) {
			String sql = "update edge AbstractAction set in = (select from NonDeterministicHole) where actionId='" + actionId + "'";
			SharedDatabase.executeCommand(db, sql);

			// TODO: Also implement NonDeterministicAction vertex to persist the non deterministic information
			// Because NonDeterministicHole will be dynamically updated and deleted
			//sql = "create vertex NonDeterministicAction set stateId='SAC1sp28jf63d3211669788'";
			//SharedDatabase.executeCommand(db, sql);
			//sql = "update edge AbstractAction set in = (select from NonDeterministicAction where stateId='SAC1sp28jf63d3211669788') where actionId='AAC1ot1fnm353944349410'";
			//SharedDatabase.executeCommand(db, sql);

		} catch (Exception e) {
			System.out.println("SharedDatabase: Not possible to create the NonDeterministicHole vertex for actionId : " + actionId);
		}

		// Now we need to change the uid properties of the non deterministic action to indicate that are non deterministic 
		// This will allow to create future AbstractAction in the state model (because uid identifies source + target + actionId)
		OResultSet rs = null;
		try (ODatabaseSession db = SharedDatabase.createDatabaseConnection(settings, database)) {
			String sql = "select uid from abstractaction where actionId='" + actionId + "'";
			rs = SharedDatabase.executeQuery(db, sql);

			while (rs.hasNext()) {
				String uid = rs.next().toString().replace("\n", "").trim();
				// {uid: 16q1mx4113647030708} to 16q1mx4113647030708
				uid = uid.replace("{", "").replace("}", "").trim().split(":")[1].trim();
				String uidNonDet = "NonDet_" + uid;
				if(!uid.contains("NonDet_")) {
					try {
						// Indicate that this abstract action is non deterministic (uid)
						sql = "update AbstractAction SET uid='" + uidNonDet + "' WHERE uid='" + uid + "'";
						SharedDatabase.executeCommand(db, sql);
					} catch (ORecordDuplicatedException orde) {
						// If duplicated key detected, means that was already marked as non deterministic, just delete
						System.out.println("Non deterministic uid action already exists : " + uidNonDet);
						sql = "delete edge AbstractAction WHERE uid='" + uid + "'";
						SharedDatabase.executeCommand(db, sql);
					}
				}
			}

		} catch (Exception e) {
			System.out.println("SharedDatabase: Exception changing uid of non non deterministic actions");
			e.printStackTrace();
		} finally {
			if(rs != null) rs.close();
		}
	}

}
