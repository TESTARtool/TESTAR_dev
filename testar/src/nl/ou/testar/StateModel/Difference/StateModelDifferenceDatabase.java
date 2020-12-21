/***************************************************************************************************
 *
 * Copyright (c) 2020 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 Open Universiteit - www.ou.nl
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

package nl.ou.testar.StateModel.Difference;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.fruit.Pair;
import org.fruit.monkey.Main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.record.ODirection;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.record.impl.ORecordBytes;
import com.orientechnologies.orient.core.record.impl.OVertexDocument;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

import nl.ou.testar.StateModel.Analysis.Json.Edge;
import nl.ou.testar.StateModel.Analysis.Json.Element;
import nl.ou.testar.StateModel.Analysis.Json.Vertex;

public class StateModelDifferenceDatabase {
	
	// Database session to execute queries
	private ODatabaseSession sessionDB;

	private String modelDifferenceReportDirectory; 
	
	public StateModelDifferenceDatabase(ODatabaseSession sessionDB, String modelDifferenceReportDirectory) {
		this.sessionDB = sessionDB;
		this.modelDifferenceReportDirectory = modelDifferenceReportDirectory;
	}
	
	/**
	 * Obtain the existing State Model Identifier from the name and version of one application
	 * 
	 * @param appName
	 * @param appVer
	 * @return modelIdentifier
	 */
	public String abstractStateModelIdentifier(String appName, String appVer) {

		String stmt = "SELECT FROM AbstractStateModel where applicationName = :applicationName and "
				+ "applicationVersion = :applicationVersion";

		Map<String, Object> params = new HashMap<>();
		params.put("applicationName", appName);
		params.put("applicationVersion", appVer);

		OResultSet resultSet = sessionDB.query(stmt, params);

		if (resultSet.hasNext()) {
			OResult result = resultSet.next();
			// we're expecting a vertex
			if (result.isVertex()) {
				Optional<OVertex> op = result.getVertex();
				if (!op.isPresent()) {resultSet.close(); return "";}
				OVertex modelVertex = op.get();

				resultSet.close();
				return modelVertex.getProperty("modelIdentifier");
			}
		}
		resultSet.close();
		
		return "";
	}
	
	/**
	 * Obtain the Abstract Attributes of one Abstract State Model using the Model Identifier
	 * 
	 * @param modelIdentifier
	 * @return
	 */
	public String abstractStateModelAbstractionAttributes(String modelIdentifier) {
		String stmt = "SELECT FROM AbstractStateModel where modelIdentifier = :modelIdentifier";

		Map<String, Object> params = new HashMap<>();
		params.put("modelIdentifier", modelIdentifier);

		OResultSet resultSet = sessionDB.query(stmt, params);

		if (resultSet.hasNext()) {
			OResult result = resultSet.next();
			// we're expecting a vertex
			if (result.isVertex()) {
				Optional<OVertex> op = result.getVertex();
				if (!op.isPresent()) {resultSet.close(); return "";}
				OVertex modelVertex = op.get();

				resultSet.close();
				Set<String> abstractionAttributes = (Set)modelVertex.getProperty("abstractionAttributes");
				return String.join(",", abstractionAttributes);
			}
		}
		resultSet.close();
		
		return "";
	}

	/**
	 * Obtain a Collection of all existing abstractStates identifiers of one State Model using his Identifier
	 * 
	 * @param sessionDB
	 * @param modelIdentifier
	 * @return Collection of Abstract States 
	 */
	public Set<String> abstractState(String modelIdentifier) {

		Set<String> abstractStates = new HashSet<>();

		String stmt = "SELECT FROM AbstractState where modelIdentifier = :modelIdentifier";

		Map<String, Object> params = new HashMap<>();
		params.put("modelIdentifier", modelIdentifier);

		OResultSet resultSet = sessionDB.query(stmt, params);

		while (resultSet.hasNext()) {
			OResult result = resultSet.next();
			// we're expecting a vertex
			if (result.isVertex()) {
				Optional<OVertex> op = result.getVertex();
				if (!op.isPresent()) continue;
				OVertex modelVertex = op.get();

				abstractStates.add(modelVertex.getProperty("stateId"));
			}
		}
		resultSet.close();

		return abstractStates;
	}

	/**
	 * The State we are going from an Action
	 * 
	 * @param sessionDB
	 * @param abstractActionId
	 * @return
	 */
	public String abstractStateFromAction(String abstractActionId) {

		String abstractState = "";

		String stmt = "SELECT FROM AbstractAction where actionId = :abstractActionId";

		Map<String, Object> params = new HashMap<>();
		params.put("abstractActionId", abstractActionId);

		OResultSet resultSet = sessionDB.query(stmt, params);

		if (resultSet.hasNext()) {
			OResult result = resultSet.next();
			// we're expecting a vertex
			if (result.isEdge()) {
				Optional<OEdge> op = result.getEdge();
				if (!op.isPresent()) {resultSet.close(); return "";}
				OEdge modelEdge = op.get();

				resultSet.close();
				return modelEdge.getVertex(ODirection.IN).getProperty("stateId");
			}
		}
		resultSet.close();

		return abstractState;
	}

	/**
	 * Return a List of Pairs with Concrete Actions <Id, Description> from one Specific AbstractState
	 * 
	 * @param sessionDB
	 * @param modelIdentifier
	 * @param abstractStateId
	 * @return
	 */
	public Set<Pair<String, String>> outgoingActionIdDesc(String modelIdentifier, String abstractStateId) {

		Set<Pair<String, String>> outgoingActionIdDesc = new HashSet<>();

		String stmt = "SELECT FROM AbstractState where modelIdentifier = :modelIdentifier and stateId = :abstractStateId";

		Map<String, Object> params = new HashMap<>();
		params.put("modelIdentifier", modelIdentifier);
		params.put("abstractStateId", abstractStateId);

		OResultSet resultSet = sessionDB.query(stmt, params);

		if (resultSet.hasNext()) {
			OResult result = resultSet.next();
			// Abstract State Vertex
			if (result.isVertex()) {
				Optional<OVertex> op = result.getVertex();
				if (!op.isPresent()) {resultSet.close(); return outgoingActionIdDesc;}
				OVertex modelVertex = op.get();

				// Abstract Actions Edges
				for(OEdge modelEdge : modelVertex.getEdges(ODirection.OUT)) {
					String abstractActionId = modelEdge.getProperty("actionId");
					Pair<String,String> pair = new Pair(abstractActionId, concreteActionDesc(abstractActionId));
					outgoingActionIdDesc.add(pair);
				}
			}
		}
		resultSet.close();

		return outgoingActionIdDesc;
	}

	/**
	 * Return a Map of all Incoming AbstractActions to one Specific AbstractState
	 * 
	 * @param sessionDB
	 * @param modelIdentifier
	 * @return
	 */
	public Set<Pair<String,String>> incomingActionsIdDesc(String modelIdentifier, String abstractStateId) {

		Set<Pair<String,String>> incomingActionsIdDesc = new HashSet<>();

		String stmt = "SELECT FROM AbstractState where modelIdentifier = :modelIdentifier and stateId = :abstractStateId";

		Map<String, Object> params = new HashMap<>();
		params.put("modelIdentifier", modelIdentifier);
		params.put("abstractStateId", abstractStateId);

		OResultSet resultSet = sessionDB.query(stmt, params);

		if (resultSet.hasNext()) {
			OResult result = resultSet.next();
			// Abstract State Vertex
			if (result.isVertex()) {
				Optional<OVertex> op = result.getVertex();
				if (!op.isPresent()) {resultSet.close(); return incomingActionsIdDesc;}
				OVertex modelVertex = op.get();

				// Abstract Actions Edges
				for(OEdge modelEdge : modelVertex.getEdges(ODirection.IN)) {
					String abstractActionId = modelEdge.getProperty("actionId");
					Pair<String,String> pair = new Pair(abstractActionId, concreteActionDesc(abstractActionId));
					incomingActionsIdDesc.add(pair);
				}
			}
		}
		resultSet.close();

		return incomingActionsIdDesc;
	}

	/**
	 * Obtain the ConcreteAction Description from an Abstract Action Identifier
	 * 
	 * @param sessionDB
	 * @param AbstractActionId
	 * @return Concrete Action Description
	 */
	public String concreteActionDesc(String abstractActionId) {

		String concreteActionsDescription = "";

		String stmtAbstract = "SELECT FROM AbstractAction WHERE actionId = :actionId";

		Map<String, Object> paramsAbstract = new HashMap<>();
		paramsAbstract.put("actionId", abstractActionId);

		OResultSet resultSetAbstract = sessionDB.query(stmtAbstract, paramsAbstract);

		if (resultSetAbstract.hasNext()) {
			OResult resultAbstract = resultSetAbstract.next();
			// we're expecting an edge
			if (resultAbstract.isEdge()) {
				Optional<OEdge> opAbstract = resultAbstract.getEdge();
				if (!opAbstract.isPresent()) {resultSetAbstract.close(); return "";}
				OEdge modelEdgeAbstract = opAbstract.get();

				try {
					for(String concreteActionId : (Set<String>) modelEdgeAbstract.getProperty("concreteActionIds"))
						if(!concreteActionId.isEmpty()) {

							String stmtConcrete = "SELECT FROM ConcreteAction WHERE actionId = :actionId";

							Map<String, Object> paramsConcrete = new HashMap<>();
							paramsConcrete.put("actionId", concreteActionId);

							OResultSet resultSetConcrete = sessionDB.query(stmtConcrete, paramsConcrete);

							if (resultSetConcrete.hasNext()) {
								OResult resultConcrete = resultSetConcrete.next();
								// we're expecting a vertex
								if (resultConcrete.isEdge()) {
									Optional<OEdge> opConcrete = resultConcrete.getEdge();
									if (!opConcrete.isPresent()) continue;
									OEdge modelEdgeConcrete = opConcrete.get();

									concreteActionsDescription = modelEdgeConcrete.getProperty("Desc");
								}
							}

							resultSetConcrete.close();

							if(!concreteActionsDescription.isEmpty()) break;

						}
				}catch (Exception e) {System.out.println("ERROR: ModelDifferenceManager concreteActionIds() ");}
			}
		}

		resultSetAbstract.close();

		return concreteActionsDescription;
	}

	/**
	 * Obtain one Concrete State Identifier of one Abstract State Identifier
	 * 
	 * @param sessionDB
	 * @param stateId
	 * @return Concrete State Identifier
	 */
	public String concreteStateId(String stateId) {

		String stmt = "SELECT FROM AbstractState WHERE stateId = :stateId LIMIT 1";

		Map<String, Object> params = new HashMap<>();
		params.put("stateId", stateId);

		OResultSet resultSet = sessionDB.query(stmt, params);

		while (resultSet.hasNext()) {
			OResult result = resultSet.next();
			// we're expecting a vertex
			if (result.isVertex()) {
				Optional<OVertex> op = result.getVertex();
				if (!op.isPresent()) continue;
				OVertex modelVertex = op.get();

				try {
					for(String concreteStateId : (Set<String>) modelVertex.getProperty("concreteStateIds"))
						if(!concreteStateId.isEmpty()) {
							resultSet.close();
							return concreteStateId;
						}
				}catch (Exception e) {System.out.println("ERROR: ModelDifferenceManager concreteStateId() ");}

			}
		}
		resultSet.close();

		return "";
	}

	/**
	 * Create and return the path of one Concrete State Screenshot by the identifier
	 * 
	 * @param sessionDB
	 * @param concreteId
	 * @param folderName
	 * @return path of existing screenshot
	 */
	public String screenshotConcreteState(String concreteId, String folderName) {

		String stmt = "SELECT FROM ConcreteState WHERE ConcreteIDCustom = :concreteId LIMIT 1";

		Map<String, Object> params = new HashMap<>();
		params.put("concreteId", concreteId);

		OResultSet resultSet = sessionDB.query(stmt, params);

		while (resultSet.hasNext()) {
			OResult result = resultSet.next();
			// we're expecting a vertex
			if (result.isVertex()) {
				Optional<OVertex> op = result.getVertex();
				if (!op.isPresent()) continue;
				OVertex modelVertex = op.get();

				String sourceScreenshot = "n" + formatId(modelVertex.getIdentity().toString());
				
				resultSet.close();
				return processScreenShot(modelVertex.getProperty("screenshot"), sourceScreenshot, folderName);
			}
		}
		resultSet.close();
		return "";
	}
	
	/**
	 * This method saves screenshots to disk.
	 * @param recordBytes
	 * @param identifier
	 */
	private String processScreenShot(ORecordBytes recordBytes, String identifier, String folderName) {
		if (!Main.outputDir.substring(Main.outputDir.length() - 1).equals(File.separator)) {
			Main.outputDir += File.separator;
		}

		// see if we have a directory for the screenshots yet
		File screenshotDir = new File(modelDifferenceReportDirectory + File.separator);

		// save the file to disk
		File screenshotFile = new File(screenshotDir, identifier + ".png");
		if (screenshotFile.exists()) {
			return new File(screenshotFile.getName()).getPath();
		}
		try {
			FileOutputStream outputStream = new FileOutputStream(screenshotFile.getCanonicalPath());
			outputStream.write(recordBytes.toStream());
			outputStream.flush();
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return new File(screenshotFile.getName()).getPath();
	}
	
	/*
	 *  Extract Widget Tree information, based on AnalysisManager fetchWidgetTree
	 */

	/**
	 * This method fetches a complete widget tree for a given concrete state id.
	 * @param concreteStateIdentifier
	 * @return
	 */
	public String fetchWidgetTree(String concreteStateIdentifier) {
		ArrayList<Element> elements = new ArrayList<>();

		// first get all the widgets using the State ConcreteIDCustom property
		String stmt = "SELECT FROM (TRAVERSE IN('isChildOf') FROM (SELECT FROM Widget WHERE ConcreteIDCustom = :concreteId))";
		Map<String, Object> params = new HashMap<>();
		params.put("concreteId", concreteStateIdentifier);
		OResultSet resultSet = sessionDB.query(stmt, params);
		elements.addAll(fetchNodes(resultSet, "Widget", null, concreteStateIdentifier));
		resultSet.close();

		// then get the parent/child relationship between the widgets
		stmt = "SELECT FROM isChildOf WHERE in IN(SELECT ConcreteIDCustom FROM (TRAVERSE in('isChildOf') FROM (SELECT FROM Widget WHERE ConcreteIDCustom = :concreteId)))";
		resultSet = sessionDB.query(stmt, params);
		elements.addAll(fetchEdges(resultSet, "isChildOf"));
		resultSet.close();

		// create a filename
		StringBuilder builder = new StringBuilder(concreteStateIdentifier);
		builder.append("_widget_tree.json");
		String filename = builder.toString();
		return writeJson(elements, filename);

	}

	/**
	 * This method transforms a resultset of nodes into elements.
	 * @param resultSet
	 * @param className
	 * @return
	 */
	private ArrayList<Element> fetchNodes(OResultSet resultSet, String className, String parent, String modelIdentifier) {
		ArrayList<Element> elements = new ArrayList<>();

		while (resultSet.hasNext()) {
			OResult result = resultSet.next();
			// we're expecting a vertex
			if (result.isVertex()) {
				Optional<OVertex> op = result.getVertex();
				if (!op.isPresent()) continue;
				OVertex stateVertex = op.get();
				Vertex jsonVertex = new Vertex("n" + formatId(stateVertex.getIdentity().toString()));
				for (String propertyName : stateVertex.getPropertyNames()) {
					if (propertyName.contains("in_") || propertyName.contains("out_")) {
						// these are edge indicators. Ignore
						continue;
					}
					if (propertyName.equals("screenshot")) {
						// process the screenshot separately
						processScreenShot(stateVertex.getProperty("screenshot"), "n" + formatId(stateVertex.getIdentity().toString()), modelIdentifier);
						continue;
					}
					jsonVertex.addProperty(propertyName, stateVertex.getProperty(propertyName).toString());
				}
				// optionally add a parent
				if (parent != null) {
					jsonVertex.addProperty("parent", parent);
				}
				Element element = new Element(Element.GROUP_NODES, jsonVertex, className);
				if(stateVertex.getPropertyNames().contains("isInitial") && (Boolean)stateVertex.getProperty("isInitial")) {
					element.addClass("isInitial");
				}
				elements.add(element);
			}
		}
		return elements;
	}

	/**
	 * This method transforms a resultset of edges into elements.
	 * @param resultSet
	 * @param className
	 * @return
	 */
	private ArrayList<Element> fetchEdges(OResultSet resultSet, String className) {
		ArrayList<Element> elements = new ArrayList<>();
		while (resultSet.hasNext()) {
			OResult result = resultSet.next();
			// we're expecting a vertex
			if (result.isEdge()) {
				Optional<OEdge> op = result.getEdge();
				if (!op.isPresent()) continue;
				OEdge actionEdge = op.get();
				OVertexDocument source = actionEdge.getProperty("out");
				OVertexDocument target = actionEdge.getProperty("in");
				Edge jsonEdge = new Edge("e" + formatId(actionEdge.getIdentity().toString()), "n" + formatId(source.getIdentity().toString()), "n" + formatId(target.getIdentity().toString()));
				for (String propertyName : actionEdge.getPropertyNames()) {
					if (propertyName.contains("in") || propertyName.contains("out")) {
						// these are edge indicators. Ignore
						continue;
					}
					jsonEdge.addProperty(propertyName, actionEdge.getProperty(propertyName).toString());
				}
				elements.add(new Element(Element.GROUP_EDGES, jsonEdge, className));
			}
		}
		return elements;
	}
    
	// this helper method formats the @RID property into something that can be used in a web frontend
	private String formatId(String id) {
		if (id.indexOf("#") != 0) return id; // not an orientdb id
		id = id.replaceAll("[#]", "");
		return id.replaceAll("[:]", "_");
	}
    
    // this helper method will write elements to a file in json format
    private String writeJson(ArrayList<Element> elements, String filename) {
        // check if the subfolder already exists
        File subFolder = new File(modelDifferenceReportDirectory + File.separator + "widgetTrees");
        
        if(!subFolder.exists()) {subFolder.mkdirs();}

        File output = new File(subFolder, filename);

        try {
            ObjectMapper mapper = new ObjectMapper();
            String result = mapper.writeValueAsString(elements);
            // let's write the resulting json to a file
            if (output.exists() || output.createNewFile()) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(output.getAbsolutePath()));
                writer.write(result);
                writer.close();
            }
            
            return output.getCanonicalPath();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return output.toString();
    }
}
