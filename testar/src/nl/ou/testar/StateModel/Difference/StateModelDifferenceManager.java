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

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import org.fruit.Pair;
import org.fruit.monkey.Main;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.exception.OSecurityAccessException;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;
import nl.ou.testar.a11y.reporting.HTMLReporter;

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

	// Set Collection with all existing Abstract States of the State Models we want to compare
	private Set<String> allAbstractStatesModelOne = new HashSet<>();
	private Set<String> allAbstractStatesModelTwo = new HashSet<>();

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

			allAbstractStatesModelOne = new HashSet<>(modelDifferenceDatabase.abstractState(identifierModelOne));
			allAbstractStatesModelTwo = new HashSet<>(modelDifferenceDatabase.abstractState(identifierModelTwo));

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

			createHTMLreport(sessionDB);

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

	private void createHTMLreport(ODatabaseSession sessionDB) {

		try {
			String[] HEADER = new String[] {
					"<!DOCTYPE html>",
					"<html>",
					"<style>",
					".container {display: flex;}",
					".float {display:inline-block;}",
					"</style>",
					"<head>",
					"<title>TESTAR State Model difference report</title>",
					"</head>",
					"<body>"
			};

			String htmlReportName = modelDifferenceReportDirectory + File.separator + "DifferenceReport.html";

			PrintWriter out = new PrintWriter(new File(htmlReportName).getCanonicalPath(), HTMLReporter.CHARSET);

			for(String s:HEADER){
				out.println(s);
				out.flush();
			}

			out.println("<h2> Disappeared Abstract States: " + disappearedAbstractStates.size() + "</h2>");
			out.println("<div class=\"container\">");
			out.flush();


			for(String dissState :  disappearedAbstractStates) {

				out.println("<div class=<\"float\">");

				out.println("<p><img src=\"" + disappearedStatesImages.get(dissState) + "\"></p>");

				out.println("<h4> Disappeared Actions of this State, Concrete Description </h4>");
				out.flush();

				for(Pair<String,String> action : disappearedActions.get(dissState)) {

					// This if will not happen because Actions are currently State dependent (new State means all Actions are new)
					if(modelDifferenceDatabase.incomingActionsIdDesc(identifierModelTwo, modelDifferenceDatabase.abstractStateFromAction(action.left())).contains(action)) {
						out.println("<p style=\"color:green;\">" + action.right() + "</p>");
						out.flush();
					} else {
						out.println("<p style=\"color:red;\">" + action.right() + "</p>");
						out.flush();
					}
				}

				out.println("</div>");
				out.flush();
			}

			out.println("</div>");

			out.println("<h2> New Abstract States: " + newAbstractStates.size() + "</h2>");
			out.println("<div class=\"container\">");
			out.flush();


			for(String newState : newAbstractStates) {

				out.println("<div class=<\"float\">");

				out.println("<p><img src=\"" + newStatesImages.get(newState) + "\"></p>");

				out.println("<h4> New Actions Discovered on this State, Concrete Description </h4>");
				out.flush();


				for(Pair<String,String> action : newActions.get(newState)) {

					// This if will not happen because Actions are currently State dependent (new State means all Actions are new)
					if(modelDifferenceDatabase.incomingActionsIdDesc(identifierModelOne, modelDifferenceDatabase.abstractStateFromAction(action.left())).contains(action)) {
						out.println("<p style=\"color:green;\">" + action.right() + "</p>");
						out.flush();
					} else {
						out.println("<p style=\"color:red;\">" + action.right() + "</p>");
						out.flush();
					}
				}

				out.println("</div>");
				out.flush();
			}

			out.println("</div>");

			// Image or Widget Tree comparison
			out.println("<h2> Specific State changes </h2>");
			out.flush();

			// new States of Model Two to be compared with Model One
			for(String newStateModelTwo : newAbstractStates) {

				Set<Pair<String, String>> incomingActionsModelTwo = modelDifferenceDatabase.incomingActionsIdDesc(identifierModelTwo, newStateModelTwo);
				incomingActionsModelTwo.remove(new Pair<String, String>(null,""));

				for(String dissStateModelOne :  disappearedAbstractStates) {

					Set<Pair<String, String>> incomingActionsModelOne = modelDifferenceDatabase.incomingActionsIdDesc(identifierModelOne, dissStateModelOne);
					incomingActionsModelOne.remove(new Pair<String, String>(null,""));

					if(!Sets.intersection(incomingActionsModelTwo, incomingActionsModelOne).isEmpty()) {

						// Image Difference
						String diffDisk = getDifferenceImage(disappearedStatesImages.get(dissStateModelOne), dissStateModelOne,
								newStatesImages.get(newStateModelTwo), newStateModelTwo);

						out.println("<p><img src=\"" + disappearedStatesImages.get(dissStateModelOne) + "\">");
						out.println("<img src=\"" + newStatesImages.get(newStateModelTwo) + "\">");
						out.println("<img src=\"" + diffDisk + "\"></p>");

						out.println("<p style=\"color:blue;\">" + "We have reached this State with Action: " + Sets.intersection(incomingActionsModelTwo, incomingActionsModelOne) + "</p>");

						// Widget Tree Abstract Properties Difference
						jsonWidgetTreeDifference(dissStateModelOne, newStateModelTwo);
						
						out.flush();
					}
				}

			}

			out.close();

			System.out.println("\n ****************************************************************************************************** \n");
			System.out.println("TESTAR State Model Difference report created in: " + htmlReportName);
			System.out.println("\n ****************************************************************************************************** \n");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// https://stackoverflow.com/questions/25022578/highlight-differences-between-images
	private String getDifferenceImage(String img1Disk, String idImg1, String img2Disk, String idImg2) {
		try {

			BufferedImage img1 = ImageIO.read(new File(img1Disk));
			BufferedImage img2 = ImageIO.read(new File(img2Disk));

			int width1 = img1.getWidth(); // Change - getWidth() and getHeight() for BufferedImage
			int width2 = img2.getWidth(); // take no arguments
			int height1 = img1.getHeight();
			int height2 = img2.getHeight();
			if ((width1 != width2) || (height1 != height2)) {
				System.err.println("Error: Images dimensions mismatch");
				System.exit(1);
			}

			// NEW - Create output Buffered image of type RGB
			BufferedImage outImg = new BufferedImage(width1, height1, BufferedImage.TYPE_INT_RGB);

			// Modified - Changed to int as pixels are ints
			int diff;
			int result; // Stores output pixel
			for (int i = 0; i < height1; i++) {
				for (int j = 0; j < width1; j++) {
					int rgb1 = img1.getRGB(j, i);
					int rgb2 = img2.getRGB(j, i);
					int r1 = (rgb1 >> 16) & 0xff;
					int g1 = (rgb1 >> 8) & 0xff;
					int b1 = (rgb1) & 0xff;
					int r2 = (rgb2 >> 16) & 0xff;
					int g2 = (rgb2 >> 8) & 0xff;
					int b2 = (rgb2) & 0xff;
					diff = Math.abs(r1 - r2); // Change
					diff += Math.abs(g1 - g2);
					diff += Math.abs(b1 - b2);
					diff /= 3; // Change - Ensure result is between 0 - 255
					// Make the difference image gray scale
					// The RGB components are all the same
					result = (diff << 16) | (diff << 8) | diff;
					outImg.setRGB(j, i, result); // Set result
				}
			}

			// Now save the image on disk

			// see if we have a directory for the screenshots yet
			File screenshotDir = new File(modelDifferenceReportDirectory + File.separator);

			// save the file to disk
			File screenshotFile = new File(screenshotDir, "diff_"+ idImg1 + "_" + idImg2 + ".png");
			if (screenshotFile.exists()) {
				try {
					return screenshotFile.getCanonicalPath();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			FileOutputStream outputStream = new FileOutputStream(screenshotFile.getCanonicalPath());

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(outImg, "png", baos);
			byte[] bytes = baos.toByteArray();

			outputStream.write(bytes);
			outputStream.flush();
			outputStream.close();

			return screenshotFile.getCanonicalPath();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return "";
	}
	
	/**
	 * 
	 * 
	 * @param dissStateModelOne
	 * @param newStateModelTwo
	 */
	private void jsonWidgetTreeDifference(String dissStateModelOne, String newStateModelTwo) {
		try {
			// From the Abstract State that disappeared, get one Concrete State Id and fetch the widget tree
			String dissapearedConcreteStateIdentifier = modelDifferenceDatabase.concreteStateId(dissStateModelOne);
			String jsonDissapearedWidgetTree = modelDifferenceDatabase.fetchWidgetTree(dissapearedConcreteStateIdentifier);
			FileReader readerDissapearedWidgetTree = new FileReader(new File(jsonDissapearedWidgetTree).getCanonicalFile());
			JsonArray jsonArrayDissapearedWidgetTree = new JsonParser().parse(readerDissapearedWidgetTree).getAsJsonArray();

			// From the new Abstract State, get one Concrete State Id and fetch the widget tree
			String newConcreteStateIdentifier = modelDifferenceDatabase.concreteStateId(newStateModelTwo);
			String jsonNewWidgetTree = modelDifferenceDatabase.fetchWidgetTree(newConcreteStateIdentifier);
			FileReader readerNewWidgetTree = new FileReader(new File(jsonNewWidgetTree).getCanonicalFile());
			JsonArray jsonObjectNewWidgetTree = new JsonParser().parse(readerNewWidgetTree).getAsJsonArray();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
