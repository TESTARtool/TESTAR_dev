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

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComboBox;
import org.fruit.Pair;
import org.fruit.alayer.Tag;
import org.fruit.alayer.webdriver.enums.WdMapping;
import org.fruit.alayer.windows.UIAMapping;
import org.fruit.monkey.Main;

import com.google.common.collect.Sets;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.exception.OSecurityAccessException;

import es.upv.staq.testar.StateManagementTags;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;

public class StateModelDifferenceManager {

	// orient db instance that will create database sessions
	private OrientDB orientDB;

	// orient db configuration object
	private Config dbConfig;

	// Helper class to execute queries
	private StateModelDifferenceDatabase modelDifferenceDatabase;
	
	private String modelDifferenceReportDirectory = "";

	public StateModelDifferenceManager(Config initialConfig, String modelDifferenceReportDirectory) {
		this.dbConfig = initialConfig;
		this.modelDifferenceReportDirectory = modelDifferenceReportDirectory;
	}

	public void obtainAvailableDatabases(Config config, JComboBox<String> listDatabases) {
		this.dbConfig = config;

		try{

			listDatabases.removeAllItems();

			String connectionString = dbConfig.getConnectionType() + ":" + (dbConfig.getConnectionType().equals("remote") ?
					dbConfig.getServer() : dbConfig.getDatabaseDirectory()) + "/";

			orientDB = new OrientDB(connectionString, dbConfig.getUser(), dbConfig.getPassword(), OrientDBConfig.defaultConfig());

			if(!orientDB.list().isEmpty())
				for(String database : orientDB.list())
					listDatabases.addItem(database);

		} catch(OSecurityAccessException e) {
			String message = "EXCEPTION: User or password not valid for database: " +
					listDatabases.getSelectedItem().toString() + 
					"\n plocal databases do not use 'root' user" + 
					"\n try with customized user";
			System.out.println(message);

		} catch(Exception e) {
			System.out.println(e.getMessage());
		} finally {
			orientDB.close();
		}

	}

	public void closeOrientDB() {
		if(orientDB!=null && orientDB.isOpen())
			orientDB.close();
	}

	// Identifiers of the State Models we want to compare
	private String identifierModelOne;
	private String identifierModelTwo;
	
	// Abstract Attributes used to create the Abstract Layer of the State Model
	private Set<Tag<?>> abstractAttributesTags = new HashSet<>();

	// Prepare a Collection to save all disappeared Abstract States
	private Set<String> disappearedAbstractStates = new HashSet<>();
	// Prepare a Map to associate an Abstract State identifier with a visual Screenshot (we will find a Concrete State)  
	private HashMap<String, String> disappearedStatesImages = new HashMap<>();
	// Prepare a Map to associate an Abstract State identifier with all the disappeared Action Description
	private HashMap<String, Set<Pair<String, String>>> disappearedActions = new HashMap<>();

	// Prepare a Collection to save all new Abstract States
	private Set<String> newAbstractStates = new HashSet<>();
	// Prepare a Map to associate an Abstract State identifier with a visual Screenshot (we will find a Concrete State)
	private HashMap<String, String> newStatesImages = new HashMap<>();
	// Prepare a Map to associate an Abstract State identifier with all the new Action Description
	private HashMap<String, Set<Pair<String, String>>> newActions = new HashMap<>();

	public void calculateModelDifference(Config config, Pair<String,String> stateModelOne, Pair<String,String> stateModelTwo) {
		if(stateModelOne.left().isEmpty() || stateModelOne.right().isEmpty() || stateModelTwo.left().isEmpty() || stateModelTwo.right().isEmpty()) {
			System.out.println("ERROR: Some State Model field Application/Version seems Empty");
			return;
		}

		this.dbConfig = config;

		String connectionString = dbConfig.getConnectionType() + ":" + (dbConfig.getConnectionType().equals("remote") ?
				dbConfig.getServer() : dbConfig.getDatabaseDirectory()) + "/";

		orientDB = new OrientDB(connectionString, dbConfig.getUser(), dbConfig.getPassword(), OrientDBConfig.defaultConfig());

		try (ODatabaseSession sessionDB = orientDB.open(dbConfig.getDatabase(), dbConfig.getUser(), dbConfig.getPassword())){

			// If no directory exists (manual State Model Diff execution)
			// Create a default directory that will contain Screenshots and HTML report
			if(modelDifferenceReportDirectory.isEmpty()) {
				modelDifferenceReportDirectory = Main.outputDir + "StateModelDifference_"
						+ stateModelOne.left() + "_" + stateModelOne.right() + "_vs_"
						+ stateModelTwo.left() + "_" + stateModelTwo.right();
			}
			
			File fileModelDiff = new File(modelDifferenceReportDirectory);
			
			if ((fileModelDiff).getCanonicalFile().exists()) {
				System.out.println("\n ************************************************************************************ \n");
				System.out.println("WARNING: This State Model Difference report already exists: " + fileModelDiff);
				System.out.println("RECOMMENDATION: Save or Delete and execute again");
				System.out.println("Current functionality is under development, there may be merged results");
				System.out.println("\n ************************************************************************************ \n");
			} else {
				fileModelDiff.getCanonicalFile().mkdirs();
			}
			
			this.modelDifferenceDatabase = new StateModelDifferenceDatabase(sessionDB, modelDifferenceReportDirectory);

			identifierModelOne = modelDifferenceDatabase.abstractStateModelIdentifier(stateModelOne.left(), stateModelOne.right());
			identifierModelTwo = modelDifferenceDatabase.abstractStateModelIdentifier(stateModelTwo.left(), stateModelTwo.right());
			
			// Extract and Update abstractAttributesTags
			if(!checkStateModelAbstractAttributes()) {
				// IF they are different, no sense to create the model difference
				return;
			}
			
			Set<String> allAbstractStatesModelOne = new HashSet<>(modelDifferenceDatabase.abstractState(identifierModelOne));
			Set<String> allAbstractStatesModelTwo = new HashSet<>(modelDifferenceDatabase.abstractState(identifierModelTwo));

			disappearedAbstractStates = new HashSet<>();
			disappearedStatesImages = new HashMap<>();
			disappearedActions = new HashMap<>();

			newAbstractStates = new HashSet<>();
			newStatesImages = new HashMap<>();
			newActions = new HashMap<>();

			/**
			 * Check which Abstract States of Model One don't exists at Model Two
			 * Disappeared Abstract States
			 */

			allAbstractStatesModelOne.forEach( abstractStateId -> {
				// Only if doesn't exists in the State Model Two
				if(!allAbstractStatesModelTwo.contains(abstractStateId)) {

					disappearedAbstractStates.add(abstractStateId);

					String screenshotPath = modelDifferenceDatabase.screenshotConcreteState(modelDifferenceDatabase.concreteStateId(abstractStateId), "disappearedState");
					disappearedStatesImages.put(abstractStateId, screenshotPath);

					disappearedActions.put(abstractStateId, modelDifferenceDatabase.outgoingActionIdDesc(identifierModelOne, abstractStateId));
				}

			});

			/**
			 * Check which Abstract States of Model Two don't exists at Model One
			 * New Abstract States
			 */

			allAbstractStatesModelTwo.forEach( abstractStateId -> {
				// Only if doesn't exists in the State Model One
				if(!allAbstractStatesModelOne.contains(abstractStateId)) {

					newAbstractStates.add(abstractStateId);

					String screenshotPath = modelDifferenceDatabase.screenshotConcreteState(modelDifferenceDatabase.concreteStateId(abstractStateId), "NewState");
					newStatesImages.put(abstractStateId, screenshotPath);

					newActions.put(abstractStateId, modelDifferenceDatabase.outgoingActionIdDesc(identifierModelTwo, abstractStateId));
				}
			});

			createHTMLreport();

		} catch(OSecurityAccessException e) {
			String message = "EXCEPTION: User or password not valid for database: " + dbConfig.getDatabase() + 
					"\n plocal databases do not use 'root' user" + 
					"\n try with customized user";
			System.out.println(message);

		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			orientDB.close();
		}

	}
	
	/**
	 * Extract and Check if both State Models were created with the same Abstract Attributes
	 */
	private boolean checkStateModelAbstractAttributes() {
		String abstractAttributesModelOne = modelDifferenceDatabase.abstractStateModelAbstractionAttributes(identifierModelOne);
		String abstractAttributesModelTwo = modelDifferenceDatabase.abstractStateModelAbstractionAttributes(identifierModelTwo);

		// IF Abstract Attributes are different, Abstract Layer is different and no sense to continue
		if(!abstractAttributesModelOne.equals(abstractAttributesModelTwo)) {
			System.out.println("\n ************************************************************************************ \n");
			System.out.println("ERROR: Abstract Attributes are different ");
			System.out.println("Model One: " + abstractAttributesModelOne);
			System.out.println("Model Two: " + abstractAttributesModelTwo);
			System.out.println("\n ************************************************************************************ \n");
			return false;
		} else {
			// Update Set object "abstractAttributesTags" with the Tags
			// we need to check for Widget Tree difference
			String[] stateModelTags = abstractAttributesModelOne.split(",");

			// Transform the String of abstractAttributesTag into a StateManagementTag
			Set<Tag<?>> stateManagementTags = new HashSet<>();
			for(int i = 0; i < stateModelTags.length; i++) {
				String settingString = capitalizeWordsAndRemoveSpaces(stateModelTags[i]);
				stateManagementTags.add(StateManagementTags.getTagFromSettingsString(settingString));
			}

			// Now check UIAWindows and WdWebriver Maps
			// To extract the specific API Tag attribute that matches with the State Management Tag
			// It could match for both:  
			// StateManagementTag: Widget title matches with UIAWindows: UIAName
			// StateManagementTag: Widget title matches with WdTag: WebGenericTitle
			for(Tag<?> stateManagementTag : stateManagementTags) {
				Tag<?> windowsTag = UIAMapping.getMappedStateTag(stateManagementTag);
				if(windowsTag != null) {
					abstractAttributesTags.add(windowsTag);
				}
				Tag<?> webdriverTag = WdMapping.getMappedStateTag(stateManagementTag);
				if(webdriverTag != null) {
					abstractAttributesTags.add(webdriverTag);
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Helper class to transform State Model String attribute
	 * into a StateManagementTag Setting String
	 * 
	 * From: widget control type
	 * To: WidgetControlType
	 * 
	 * @return
	 */
	private String capitalizeWordsAndRemoveSpaces(String attribute) {
		String words[] = attribute.split("\\s");  
		StringBuilder capitalizeWord = new StringBuilder("");  
		for(String w : words){  
			String first = w.substring(0,1);  
			String afterfirst = w.substring(1);  
			capitalizeWord.append(first.toUpperCase() + afterfirst);  
		}  
		return capitalizeWord.toString().trim();  
	}

	/**
	 * Based on the information extracted from the comparison of State Models.
	 * Create an HTML report.
	 */
	private void createHTMLreport() {

		String htmlReportName = modelDifferenceReportDirectory + File.separator + "DifferenceReport.html";

		StateModelDifferenceHTMLreport differenceHTML = new StateModelDifferenceHTMLreport(htmlReportName);

		differenceHTML.startDisappearedAbstractStates(disappearedAbstractStates.size());

		for(String dissState :  disappearedAbstractStates) {
			differenceHTML.addDisappearedAbstractState(disappearedStatesImages.get(dissState));

			for(Pair<String,String> action : disappearedActions.get(dissState)) {
				differenceHTML.addActionDesc(action.right());

				// This if will not happen because Actions are currently State dependent (new State means all Actions are new)
				/*if(modelDifferenceDatabase.incomingActionsIdDesc(identifierModelTwo, modelDifferenceDatabase.abstractStateFromAction(action.left())).contains(action)) {
					out.println("<p style=\"color:green;\">" + action.right() + "</p>");
					out.flush();
				} else {
					out.println("<p style=\"color:red;\">" + action.right() + "</p>");
					out.flush();
				}*/
			}

			differenceHTML.addEndDiv();
		}

		differenceHTML.addEndDiv();

		differenceHTML.startNewAbstractStates(newAbstractStates.size());

		for(String newState : newAbstractStates) {
			differenceHTML.addNewAbstractState(newStatesImages.get(newState));

			for(Pair<String,String> action : newActions.get(newState)) {
				differenceHTML.addActionDesc(action.right());
				
				// This if will not happen because Actions are currently State dependent (new State means all Actions are new)
				/*if(modelDifferenceDatabase.incomingActionsIdDesc(identifierModelOne, modelDifferenceDatabase.abstractStateFromAction(action.left())).contains(action)) {
					out.println("<p style=\"color:green;\">" + action.right() + "</p>");
					out.flush();
				} else {
					out.println("<p style=\"color:red;\">" + action.right() + "</p>");
					out.flush();
				}*/
			}

			differenceHTML.addEndDiv();
		}

		differenceHTML.addEndDiv();

		// Image or Widget Tree comparison
		differenceHTML.startSpecificStateChanges();

		// new States of Model Two to be compared with Model One
		for(String newStateModelTwo : newAbstractStates) {

			Set<Pair<String, String>> incomingActionsModelTwo = modelDifferenceDatabase.incomingActionsIdDesc(identifierModelTwo, newStateModelTwo);
			incomingActionsModelTwo.remove(new Pair<String, String>(null,""));

			for(String dissStateModelOne :  disappearedAbstractStates) {

				Set<Pair<String, String>> incomingActionsModelOne = modelDifferenceDatabase.incomingActionsIdDesc(identifierModelOne, dissStateModelOne);
				incomingActionsModelOne.remove(new Pair<String, String>(null,""));

				if(!Sets.intersection(incomingActionsModelTwo, incomingActionsModelOne).isEmpty()) {

					// Create the Image Difference
					String diffDisk = StateModelDifferenceImages.getDifferenceImage(
							disappearedStatesImages.get(dissStateModelOne), dissStateModelOne,
							newStatesImages.get(newStateModelTwo), newStateModelTwo,
							modelDifferenceReportDirectory);

					differenceHTML.addSpecificStateChange(disappearedStatesImages.get(dissStateModelOne), newStatesImages.get(newStateModelTwo), diffDisk);

					differenceHTML.addSpecificActionReached(Sets.intersection(incomingActionsModelTwo, incomingActionsModelOne).toString());

					// Widget Tree Abstract Properties Difference
					StateModelDifferenceJsonWidget stateModelDifferenceJsonWidget = new StateModelDifferenceJsonWidget(modelDifferenceDatabase, abstractAttributesTags);
					List<String> widgetTreeDifference = stateModelDifferenceJsonWidget.jsonWidgetTreeDifference(dissStateModelOne, newStateModelTwo);
					for(String widgetInformation : widgetTreeDifference) {
						differenceHTML.addSpecificWidgetInfo(widgetInformation);
					}
				}
			}

		}

		differenceHTML.closeHTMLreport();

		System.out.println("\n ****************************************************************************************************** \n");
		System.out.println("TESTAR State Model Difference report created in: " + differenceHTML.getHtmlFileName());
		System.out.println("\n ****************************************************************************************************** \n");

	}
}
