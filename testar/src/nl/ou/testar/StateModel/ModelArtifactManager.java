/***************************************************************************************************
 *
 * Copyright (c) 2019 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2019 Open Universiteit - www.ou.nl
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

package nl.ou.testar.StateModel;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.testar.json.object.JsonArtefactStateModel;
import org.testar.json.object.StateModelTestSequenceJsonObject;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

import nl.ou.testar.StateModel.Analysis.Representation.TestSequence;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;
import nl.ou.testar.StateModel.Sequence.SequenceVerdict;

public class ModelArtifactManager {

	private ModelArtifactManager() {}

	// orient db instance that will create database sessions
	private static OrientDB orientDB;

	// orient db configuration object
	private static Config dbConfig;

	public static String connectionStuff(String storeType, String storeServer, String root, String passField,
			String database) {
		dbConfig = new Config();
		dbConfig.setConnectionType(storeType);
		dbConfig.setServer(storeServer);
		dbConfig.setUser(root);
		dbConfig.setPassword(passField);
		dbConfig.setDatabase(database);

		orientDB = new OrientDB(dbConfig.getConnectionType() + ":" + dbConfig.getServer(), 
				dbConfig.getUser(), dbConfig.getPassword(), OrientDBConfig.defaultConfig());

		return dbConfig.getConnectionType() + ":" + dbConfig.getServer() + "/database/" + dbConfig.getDatabase();
	}

	public static void closeOrientDB() {
		if(orientDB!=null && orientDB.isOpen())
			orientDB.close();
	}

	public static void createAutomaticArtefact(Settings settings) {
		
		String storeType = settings.get(ConfigTags.DataStoreType);
		String storeServer = settings.get(ConfigTags.DataStoreServer);
		String root = settings.get(ConfigTags.DataStoreUser);
		String passField = settings.get(ConfigTags.DataStorePassword);
		String database = settings.get(ConfigTags.DataStoreDB);
		String appName = settings.get(ConfigTags.ApplicationName);
		String appVersion = settings.get(ConfigTags.ApplicationVersion);
		boolean storeWidgets = settings.get(ConfigTags.StateModelStoreWidgets);
		
		if(appName == null || appVersion == null) {
			System.out.println("To create an Artefact of the State Model, "
					+ "a cutomized Name of the application and the version is required");
			return;
		}

		String dbConnection = connectionStuff(storeType, storeServer, root, passField, database);

		try (ODatabaseSession sessionDB = orientDB.open(dbConnection, dbConfig.getUser(), dbConfig.getPassword())){

			// Search and get the State Model identifier to start the queries
			String stateModelId = getAbstractStateModelIdentifier(sessionDB, appName, appVersion);
			
			if(stateModelId.isEmpty()) {
				System.out.println(String.format("State Model with name %s and version %s was not found in the dabatase %s",
						appName, appVersion, database));
			}
			
			String abstractionLevelProperties = getStateModelAbstractionLevel(sessionDB, stateModelId);
			long numberOfUnvisitedAbstractActions = getStateModelNumberOfUnvisitedActions(sessionDB, stateModelId);
			boolean isDeterministic = getStateModelIsDeterministic(sessionDB, stateModelId);
			long numberOfAbstractStates = getStateModelNumberOfAbstractStates(sessionDB, stateModelId);
			long numberOfAbstractActions = getStateModelNumberOfAbstractActions(sessionDB, stateModelId);
			long numberOfConcreteStates = getStateModelNumberOfConcreteStates(sessionDB, stateModelId);
			long numberOfConcreteActions = getStateModelNumberOfConcreteActions(sessionDB, stateModelId);
			long numberOfWidgets = getStateModelNumberOfWidgets(sessionDB, stateModelId);
			
			long numberOfTestSequences = getStateModelNumberOfTestSequences(sessionDB, stateModelId);
			Set<StateModelTestSequenceJsonObject> testSequenceObject = getStateModelTestSequencesObject(sessionDB, stateModelId);

            System.out.println("Creating JSON State Model artefact...");
        	JsonArtefactStateModel.automaticStateModelArtefact(appName, appVersion, stateModelId,
        			abstractionLevelProperties, isDeterministic, numberOfUnvisitedAbstractActions,
        			numberOfAbstractStates, numberOfAbstractActions, numberOfConcreteStates, numberOfConcreteActions,
        			storeWidgets, numberOfWidgets, numberOfTestSequences, testSequenceObject);

		}catch(Exception e) {
			e.printStackTrace();
		} finally {
			orientDB.close();
		}

	}

	private static String getAbstractStateModelIdentifier(ODatabaseSession sessionDB, String appName, String appVer) {
		OResultSet resultSet = sessionDB.query("SELECT FROM AbstractStateModel where applicationName=\"" + appName
				+ "\" and applicationVersion=\"" + appVer + "\"");

		while (resultSet.hasNext()) {
			OResult result = resultSet.next();
			// we're expecting a vertex
			if (result.isVertex()) {
				Optional<OVertex> op = result.getVertex();
				if (!op.isPresent()) continue;
				OVertex modelVertex = op.get();

				return modelVertex.getProperty("modelIdentifier");
			}
		}
		resultSet.close();

		return "";
	}
	
	private static String getStateModelAbstractionLevel(ODatabaseSession sessionDB, String stateModelIdentifier) {
		OResultSet resultSet = sessionDB.query("SELECT FROM AbstractStateModel where modelIdentifier=\""
				+ stateModelIdentifier + "\"");

		while (resultSet.hasNext()) {
			OResult result = resultSet.next();
			// we're expecting a vertex
			if (result.isVertex()) {
				Optional<OVertex> op = result.getVertex();
				if (!op.isPresent()) continue;
				OVertex modelVertex = op.get();
				
	        	Set abstractionAttributes = (Set)getConvertedValue(OType.EMBEDDEDSET, modelVertex.getProperty("abstractionAttributes"));
	        	String abstractionId = getAbstractionAttributesAsString(abstractionAttributes);

	        	if(abstractionId!=null)
	        		return abstractionId;
			}
		}
		resultSet.close();

		return "";
	}
	
	private static long getStateModelNumberOfUnvisitedActions(ODatabaseSession sessionDB, String stateModelIdentifier) {
		long numberUnvisitedActions = 0;
    	
    	String stmt = "SELECT count(*) FROM UnvisitedAbstractAction WHERE modelIdentifier = \""
    			+ stateModelIdentifier + "\"";
    	
    	OResultSet resultSet = sessionDB.query(stmt);
        OResult result = resultSet.next();
        numberUnvisitedActions = result.getProperty("count(*)");
        resultSet.close();
        
		return numberUnvisitedActions;
	}
	
	
	private static boolean getStateModelIsDeterministic(ODatabaseSession sessionDB, String stateModelIdentifier) {

		// we will use a custom query to search for states in which the same action occurs more than once, leading to
		// different target states
		String query = "SELECT FROM (SELECT stateId, actionId, COUNT(*) "
				+ "as nrOfActions FROM (select @rid as stateId, oute(\"abstractaction\").actionId "
				+ "as actionId from abstractstate UNWIND actionId) group by stateId, actionId) WHERE nrOfActions > 1";
		OResultSet resultSet = sessionDB.query(query);
		boolean isDeterministic = !resultSet.hasNext(); // no states were found where the same action occurs twice
		resultSet.close();
		return isDeterministic;

	}
	
	private static long getStateModelNumberOfAbstractStates(ODatabaseSession sessionDB, String stateModelIdentifier) {
		long numberAbstractStates = 0;
    	
    	String stmt = "SELECT count(*) FROM AbstractState WHERE modelIdentifier = \""
    			+ stateModelIdentifier + "\"";
    	
    	OResultSet resultSet = sessionDB.query(stmt);
        OResult result = resultSet.next();
        numberAbstractStates = result.getProperty("count(*)");
        resultSet.close();
        
		return numberAbstractStates;
	}
	
	private static long getStateModelNumberOfAbstractActions(ODatabaseSession sessionDB, String stateModelIdentifier) {
		long numberAbstractActions = 0;
    	
    	String stmt = "SELECT count(*) FROM AbstractAction WHERE modelIdentifier = \""
    			+ stateModelIdentifier + "\"";
    	
    	OResultSet resultSet = sessionDB.query(stmt);
        OResult result = resultSet.next();
        numberAbstractActions = result.getProperty("count(*)");
        resultSet.close();
        
		return numberAbstractActions;
	}
	
	private static long getStateModelNumberOfConcreteStates(ODatabaseSession sessionDB, String stateModelIdentifier) {
		long numberConcreteStates = 0;
    	
		String stmt = "SELECT count(*) FROM (TRAVERSE in() FROM (SELECT FROM AbstractState "
				+ "WHERE modelIdentifier = \"" + stateModelIdentifier + "\")) "
				+ "WHERE @class = 'ConcreteState'";
    	
    	OResultSet resultSet = sessionDB.query(stmt);
        OResult result = resultSet.next();
        numberConcreteStates = result.getProperty("count(*)");
        resultSet.close();
        
		return numberConcreteStates;
	}
	
	private static long getStateModelNumberOfConcreteActions(ODatabaseSession sessionDB, String stateModelIdentifier) {
		long numberConcreteActions = 0;
		
		String stmt = "SELECT count(*) FROM (TRAVERSE in('isAbstractedBy').outE('ConcreteAction') FROM (SELECT "
				+ "FROM AbstractState WHERE modelIdentifier = \"" + stateModelIdentifier + "\")) "
				+ "WHERE @class = 'ConcreteAction'";
    	
    	OResultSet resultSet = sessionDB.query(stmt);
        OResult result = resultSet.next();
        numberConcreteActions = result.getProperty("count(*)");
        resultSet.close();
        
		return numberConcreteActions;
	}
	
	private static long getStateModelNumberOfWidgets(ODatabaseSession sessionDB, String stateModelIdentifier) {
		long numberWidgets = 0;
		
		/*String stmt = "";
    	
    	OResultSet resultSet = sessionDB.query(stmt);
        OResult result = resultSet.next();
        numberWidgets = result.getProperty("count(*)");
        resultSet.close();*/
        
		return numberWidgets;
	}
	
	private static long getStateModelNumberOfTestSequences(ODatabaseSession sessionDB, String stateModelIdentifier) {
		long numberTestSequences = 0;
		
		String stmt = "SELECT count(*) FROM TestSequence WHERE modelIdentifier = \"" + stateModelIdentifier + "\" "
				+ "ORDER BY startDateTime ASC";
    	
    	OResultSet resultSet = sessionDB.query(stmt);
        OResult result = resultSet.next();
        numberTestSequences = result.getProperty("count(*)");
        resultSet.close();
        
		return numberTestSequences;
	}
	
	private static Set<StateModelTestSequenceJsonObject> getStateModelTestSequencesObject(ODatabaseSession sessionDB, String stateModelIdentifier) {
		Set<StateModelTestSequenceJsonObject> testSequences = new HashSet<>();
		String sequenceId;
		int numberSequenceNodes;
		String startDateTime;
		String verdict;
		boolean foundErrors;
		int numberErrors;
		boolean sequenceDeterministic;
		
		String sequenceStmt = "SELECT FROM TestSequence WHERE modelIdentifier = :identifier ORDER BY startDateTime ASC";
        Map<String, Object> params = new HashMap<>();
        params.put("identifier", stateModelIdentifier);
        OResultSet resultSet = sessionDB.query(sequenceStmt, params);
        while (resultSet.hasNext()) {
            OResult sequenceResult = resultSet.next();
            // we're expecting a vertex
            if (sequenceResult.isVertex()) {
                Optional<OVertex> sequenceOp = sequenceResult.getVertex();
                if (!sequenceOp.isPresent()) continue;
                OVertex sequenceVertex = sequenceOp.get();

                // fetch the nr of nodes for the sequence
                String nodeStmt = "SELECT COUNT(*) as nr FROM SequenceNode WHERE sequenceId = :sequenceId";
                params = new HashMap<>();
                params.put("sequenceId", getConvertedValue(OType.STRING, sequenceVertex.getProperty("sequenceId")));
                OResultSet nodeResultSet = sessionDB.query(nodeStmt, params);
                int nrOfNodes = 0;
                if (nodeResultSet.hasNext()) {
                    OResult nodeResult = nodeResultSet.next();
                    nrOfNodes = (int)getConvertedValue(OType.INTEGER, nodeResult.getProperty("nr"));
                    if (nrOfNodes > 0) {
                        nrOfNodes--;
                    }
                }
                nodeResultSet.close();
                
                numberSequenceNodes = nrOfNodes;
                sequenceId = (String) getConvertedValue(OType.STRING, sequenceVertex.getProperty("sequenceId"));
                startDateTime = ((Date)getConvertedValue(OType.DATETIME, sequenceVertex.getProperty("startDateTime"))).toString();

                // not the best piece of code, but it works for now
                verdict = (String) getConvertedValue(OType.ANY.STRING, sequenceVertex.getProperty("verdict"));

                // fetch the number of errors that were encountered during the test run
                String errorStmt = "SELECT COUNT(*) as nr FROM(TRAVERSE out(\"FirstNode\"), out(\"SequenceStep\") FROM ( SELECT FROM TestSequence WHERE sequenceId = :sequenceId)) WHERE @class = \"SequenceNode\" AND containsErrors = true";
                params = new HashMap<>();
                params.put("sequenceId", getConvertedValue(OType.STRING, sequenceVertex.getProperty("sequenceId")));
                OResultSet errorResultSet = sessionDB.query(errorStmt, params);
                int nrOfErrors = 0;
                if (errorResultSet.hasNext()) {
                    OResult errorResult = errorResultSet.next();
                    nrOfErrors = (int)getConvertedValue(OType.INTEGER, errorResult.getProperty("nr"));
                }
                errorResultSet.close();
                
                foundErrors = (nrOfErrors>0);
                numberErrors = nrOfErrors;

                // check if the sequence was deterministic
                String deterministicQuery = "SELECT FROM (TRAVERSE outE(\"SequenceStep\") FROM (\n" +
                        "\n" +
                        "TRAVERSE out(\"SequenceStep\")\n" +
                        "FROM(\n" +
                        "\n" +
                        "SELECT FROM (\n" +
                        "TRAVERSE out(\"FirstNode\")\n" +
                        "\n" +
                        "FROM (\n" +
                        "select from TestSequence where sequenceId = :sequenceId)) where @class = \"SequenceNode\"))) where @class = \"SequenceStep\" AND nonDeterministic = true";
                params = new HashMap<>();
                params.put("sequenceId", getConvertedValue(OType.STRING, sequenceVertex.getProperty("sequenceId")));
                OResultSet determinismResultSet = sessionDB.query(deterministicQuery, params);
                boolean sequenceIsDeterministic = !determinismResultSet.hasNext();
                determinismResultSet.close();
                
                sequenceDeterministic = sequenceIsDeterministic;

                testSequences.add(new StateModelTestSequenceJsonObject(sequenceId, numberSequenceNodes, startDateTime,
                		verdict, foundErrors, numberErrors, sequenceDeterministic));
            }
        }
        resultSet.close();
        
		return testSequences;
	}

	// this helper method formats the @RID property into something that can be used in a web frontend
	private static String formatId(String id) {
		if (id.indexOf("#") != 0) return id; // not an orientdb id
		id = id.replaceAll("[#]", "");
		return id.replaceAll("[:]", "_");
	}
	
	/**
     * Helper method that converts an object value based on a specified OrientDB data type.
     * @param oType
     * @param valueToConvert
     * @return
     */
    private static Object getConvertedValue(OType oType, Object valueToConvert) {
        Object convertedValue = null;
        switch (oType) {
            case BOOLEAN:
                convertedValue = OType.convert(valueToConvert, Boolean.class);
                break;

            case STRING:
                convertedValue = OType.convert(valueToConvert, String.class);
                break;

            case LINKBAG:
                // we don't process these as a separate attribute
                break;

            case EMBEDDEDSET:
                convertedValue = OType.convert(valueToConvert, Set.class);
                break;

            case INTEGER:
                convertedValue = OType.convert(valueToConvert, Integer.class);
                break;

            case DATETIME:
                convertedValue = OType.convert(valueToConvert, Date.class);
                break;
        }
        return  convertedValue;
    }
    
    /**
     * This method will return a string containing the abstraction attributes used in creating the model.
     * @return
     */
    public static String getAbstractionAttributesAsString(Set abstractionAttributes) {
        return (String)abstractionAttributes.stream().sorted().reduce("", (base, string) -> base.equals("") ? string : base + ", " + string);
    }

}
