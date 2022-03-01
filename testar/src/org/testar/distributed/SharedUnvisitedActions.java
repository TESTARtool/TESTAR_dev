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

public class SharedUnvisitedActions {

	private SharedUnvisitedActions() {}

	/**
	 * State Model contains UnvisitedActions leading to Black Hole, 
	 * now we calculate the shortest path to reach the Black Hole, 
	 * and we return a list of shortest UnvisitedActions. 
	 * 
	 * @param currentAbstractState
	 * @param settings
	 * @param database
	 * @return
	 */
	public static ArrayList<String> getUnvisitedActionsFromDatabase(String currentAbstractState, Settings settings, OrientDB database) {
		System.out.println("SharedDatabase: getUnvisitedActionsFromDatabase");
		ArrayList<String> result = new ArrayList<>();
		// https://orientdb.org/docs/3.1.x/gettingstarted/demodb/queries/DemoDB-Queries-Shortest-Paths.html
		// TODO: This query assumes that there is only one state model (one black hole) per database

		// We only consider OUT edges to follow Action execution direction path
		String sql = "SELECT expand(path) FROM (SELECT shortestPath($from, $to, 'OUT') AS path LET $from = (SELECT FROM AbstractState WHERE stateId='" + currentAbstractState + "'), "
				+ "$to = (SELECT FROM BlackHole) UNWIND path)";

		// TODO: For databases with multiples state models, add state model identifier or black hole identifier to fix this query
		// SELECT expand(path) FROM (SELECT shortestPath($from, $to) AS path LET $from=(SELECT FROM abstractstate WHERE stateId='SACd588mu1b22740930749'), 
		// $to=(SELECT FROM BlackHole where blackHoleId='jp3leu283079445017') UNWIND path)

		OResultSet rs = null;
		try (ODatabaseSession db = SharedDatabase.createDatabaseConnection(settings, database)) {
			rs = SharedDatabase.executeQuery(db, sql);

			while (rs.hasNext()) {
				OResult item = rs.next();
				if (item.isVertex()) {
					Optional<OVertex> optionalVertex = item.getVertex();
					OVertex nodeVertex = optionalVertex.get();

					for (OEdge edge : nodeVertex.getEdges(ODirection.OUT, "UnvisitedAbstractAction")) {
						result.add(edge.getProperty("actionId"));
					}
				}
			}
		} catch (Exception e) {
			System.out.println("SharedDatabase: Exception during getUnvisitedActionsFromDatabase");
			e.printStackTrace();
		} finally {
			if(rs != null) { rs.close(); }
		}

		return result;
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
		return updateUnvisitedActionToBeingExecuted(actionId, nodeName, settings, database);
	}

	private static String updateUnvisitedActionToBeingExecuted(String actionId, String nodeName, Settings settings, OrientDB database) {
		try (ODatabaseSession db = SharedDatabase.createDatabaseConnection(settings, database)) {
			String sql = "update edge UnvisitedAbstractAction set in = (SELECT FROM BeingExecuted WHERE node='" + nodeName + "') where actionId='" + actionId + "'";
			SharedDatabase.executeCommand(db, sql);
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
		try (ODatabaseSession db = SharedDatabase.createDatabaseConnection(settings, database)) {
			// TODO: This query assumes that there is only one state model (one black hole) per database 
			String sql = "update edge UnvisitedAbstractAction set in = (select from BlackHole) where actionId='" + targetSharedAction + "'";
			System.out.println("Return action to BlackHole from BeingExecuted: " + targetSharedAction + " sql = " + sql);
			SharedDatabase.executeCommand(db, sql);
		} catch (Exception e) {
			System.out.println("Not possible to return targetSharedAction " + targetSharedAction + " to BlackHole : " + e);
		}
	}

}
