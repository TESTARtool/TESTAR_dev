/***************************************************************************************************
 *
 * Copyright (c) 2013, 2014, 2015, 2016, 2017, 2018, 2019 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018, 2019 Open Universiteit - www.ou.nl
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


import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.*;
import org.fruit.alayer.windows.UIARoles;
import org.fruit.monkey.Settings;
import org.testar.action.priorization.ActionTags;
import org.testar.action.priorization.ActionTags.ActionGroupType;
import org.testar.protocols.DesktopProtocol;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import javax.imageio.ImageIO;
import org.testar.OutputStructure;
import nl.ou.testar.a11y.reporting.HTMLReporter;


/**
 * This protocol provides default TESTAR behaviour to test Windows desktop applications.
 *
 * It uses random action selection algorithm.
 */
public class Protocol_desktop_generic_enfoque_4 extends DesktopProtocol {
	
	String lastWidgetID = "";
	
	private HTMLDifference htmlDifference;
	String differenceScreenshot;

	List<String> idCustomsGlobalList = new ArrayList<String>();
	List<String> widgetNamesGlobalList = new ArrayList<String>();
	List<Double> zIndexesGlobalList = new ArrayList<Double>();
	List<Double> qLearningsGlobalList = new ArrayList<Double>();
	
	
	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 * @param   settings  the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings){
		super.initialize(settings);
		htmlDifference = new HTMLDifference();
		System.out.println("*** NEW EXECUTION ***");
	}

	/**
	 * This method is called when the TESTAR requests the state of the SUT.
	 * Here you can add additional information to the SUT's state or write your
	 * own state fetching routine. The state should have attached an oracle
	 * (TagName: <code>Tags.OracleVerdict</code>) which describes whether the
	 * state is erroneous and if so why.
	 *
	 * super.getState(system) puts the state information also to the HTML sequence report
	 *
	 * @return  the current state of the SUT with attached oracle.
	 */
	@Override
	protected State getState(SUT system) throws StateBuildException{
		// Save previous state object
		State previousState = latestState;
				
		// Update state information
		State state = super.getState(system);
				
		if(previousState != null && previousState.get(Tags.ScreenshotPath, null) != null && state.get(Tags.ScreenshotPath, null) != null) {
					
			// Create and obtain the image-diff path
			differenceScreenshot = getDifferenceImage(
				previousState.get(Tags.ScreenshotPath), previousState.get(Tags.AbstractIDCustom, ""),
				state.get(Tags.ScreenshotPath), state.get(Tags.AbstractIDCustom, "")
				);
		}
				
		return state;
	}

	/**
	 * This method is used by TESTAR to determine the set of currently available actions.
	 * You can use the SUT's current state, analyze the widgets and their properties to create
	 * a set of sensible actions, such as: "Click every Button which is enabled" etc.
	 * The return value is supposed to be non-null. If the returned set is empty, TESTAR
	 * will stop generation of the current action and continue with the next one.
	 * @param system the SUT
	 * @param state the SUT's current state
	 * @return  a set of actions
	 */
	@Override
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException{
		//The super method returns a ONLY actions for killing unwanted processes if needed, or bringing the SUT to
		//the foreground. You should add all other actions here yourself.
		// These "special" actions are prioritized over the normal GUI actions in selectAction() / preSelectAction().
		Set<Action> actions = super.deriveActions(system,state);


		// Derive left-click actions, click and type actions, and scroll actions from
		// top level (highest Z-index) widgets of the GUI:
		actions = deriveClickTypeScrollActionsFromTopLevelWidgets(actions, system, state);

		if(actions.isEmpty()){
			// If the top level widgets did not have any executable widgets, try all widgets:
			//System.out.println("No actions from top level widgets, changing to all widgets.");
			// Derive left-click actions, click and type actions, and scroll actions from
			// all widgets of the GUI:
			actions = deriveClickTypeScrollActionsFromAllWidgetsOfState(actions, system, state);
		}
		
		
		// [state 1] --(a)--> [state 2]
		// Comparar la imagen de los estados [state 1] y [state 2], y ver cuántos píxeles han cambiado.
		// La recompensa de (a) será mayor cuantos más píxeles cambien entre [state 1] y [state 2].
		// Usar la información de "htmlDifference"
		
		for (Widget w : state) {
			Action a = getAction(w, actions);
			updateListsBefore(a);
				
			// Inicializacion del valor de recompensa a 1.0
			if(a.get(ActionTags.QLearning, 0.0) == 0.0) a.set(ActionTags.QLearning, 1.0);
		}
		
		if(lastWidgetID != "") {
			int index = idCustomsGlobalList.indexOf(lastWidgetID);
			if(index != -1) {
				try {
					double qLearningValue = qLearningsGlobalList.get(index);
					
					BufferedImage diffScreanshot = ImageIO.read(new File(differenceScreenshot));
					double diffPxPercentage = getDiffPxPercentage(diffScreanshot);
					
					qLearningValue += diffPxPercentage;
					qLearningsGlobalList.set(index, qLearningValue);
						
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		//return the set of derived actions
		return actions;
	}

	/**
	 * Select one of the available actions using an action selection algorithm (for example random action selection)
	 *
	 * super.selectAction(state, actions) updates information to the HTML sequence report
	 *
	 * @param state the SUT's current state
	 * @param actions the set of derived actions
	 * @return  the selected action (non-null!)
	 */
	@Override
	protected Action selectAction(State state, Set<Action> actions){
		//Seleccionar el widgetcon el mayor valor en su tag QLearning.
		String maxID = "";
		double maxQLearning = 0.0;
		double aQLearning = 0.0;
				
		for(Action a : actions) {
			String aID = a.get(Tags.OriginWidget).get(Tags.AbstractIDCustom);
			aQLearning = a.get(ActionTags.QLearning, 0.0);
			if(aQLearning > maxQLearning) {
				maxQLearning = aQLearning;
				maxID = aID;
			}
		}
		
		lastWidgetID = maxID;

		for(Action a : actions) {
			String aID = a.get(Tags.OriginWidget).get(Tags.AbstractIDCustom);
			if(aID == maxID) {
				updateListsAfter(a);
								
				System.out.println("... widgetToBeSelected: " + a.get(Tags.OriginWidget).get(Tags.Desc));
				System.out.println("... widgetToBeSelectedQL: " + maxQLearning);
				System.out.println(" ...... widgetNamesGlobalList: " + widgetNamesGlobalList);
				System.out.println(" ...... qLearningsGlobalList: " + qLearningsGlobalList);
				System.out.println(" ...... zIndexesGlobalList: " + zIndexesGlobalList);
								
				return a;
			}
		}
		
		return(super.selectAction(state, actions));
		
	}
		
	private double greaterThanZero (double d) {
		if(d >= 0.0) return d;
		else return 0.1;
	}
	
	private void updateListsBefore(Action a) {
		String idCustom = a.get(Tags.OriginWidget).get(Tags.AbstractIDCustom);
		if(idCustomsGlobalList.contains(idCustom)) {
			int index = idCustomsGlobalList.indexOf(idCustom);
			a.set(ActionTags.QLearning, qLearningsGlobalList.get(index));
			int zIndexInt = (int) Math.round(zIndexesGlobalList.get(index));
			a.set(ActionTags.ZIndex, zIndexInt);
		}
	}
	
	private void updateListsAfter(Action a) {
		Widget originWidget = a.get(Tags.OriginWidget);
		
		String idCustom = originWidget.get(Tags.AbstractIDCustom);
		String maxActionGroup = a.get(ActionTags.ActionGroup, ActionGroupType.UIAWidget).toString();
		double maxQL = a.get(ActionTags.QLearning);
		String maxDesc = originWidget.get(Tags.Desc, "NULL");
		double maxZIndex = originWidget.get(Tags.ZIndex);
				
		if(idCustomsGlobalList.isEmpty()) {
			idCustomsGlobalList.add(idCustom);
			widgetNamesGlobalList.add(maxDesc);
			qLearningsGlobalList.add(maxQL);
			zIndexesGlobalList.add(maxZIndex);
		} else {
			if(idCustomsGlobalList.contains(idCustom)) {
				int index = idCustomsGlobalList.indexOf(idCustom);
				double adjustedQL = maxQL - (0.01 * maxZIndex);
				qLearningsGlobalList.set(index, adjustedQL);
				zIndexesGlobalList.set(index, maxZIndex);
				widgetNamesGlobalList.set(index, maxDesc);
			} else {
				idCustomsGlobalList.add(idCustom);
				widgetNamesGlobalList.add(maxDesc);
				qLearningsGlobalList.add(maxQL);
				zIndexesGlobalList.add(maxZIndex);
			}
		}
	}

	private Action getAction(Widget w, Set<Action> actions) {
		Action theActions[] = new Action[actions.size()];
		actions.toArray(theActions);
		Action theAction = theActions[theActions.length - 1];
		for(Action a : actions) {
			if(w.get(Tags.AbstractIDCustom) == a.get(Tags.OriginWidget).get(Tags.AbstractIDCustom)) {
				theAction = a;
			}
		}
		return theAction;
	}
	
	private double getDiffPxPercentage(BufferedImage diffScreanshot) {
		double diffPxPercentage = 0;
		double totalPixels = diffScreanshot.getWidth() * diffScreanshot.getHeight();
		double differentPixels = 0;
		int[] pixelsArray = diffScreanshot.getRGB(0, 0, diffScreanshot.getWidth(), diffScreanshot.getHeight(), null, 0, diffScreanshot.getWidth());
		
		for (int i = 0; i < totalPixels; i++) {
		    if (pixelsArray[i] != Color.Black.argb32()) {
		    	differentPixels ++;
		    }
		}
		
		diffPxPercentage = differentPixels / totalPixels;
		
		System.out.println("*********");
		System.out.println("Totales actuales: " + totalPixels);
		System.out.println("Diferentes: " + differentPixels);
		System.out.println("Proporcion (0..1): " + diffPxPercentage);
		System.out.println("*********");
		
		return diffPxPercentage;
	}
	
	
	/**
	 * State image difference 
	 * 
	 * https://stackoverflow.com/questions/25022578/highlight-differences-between-images
	 * 
	 * @param previousStateDisk
	 * @param namePreviousState
	 * @param stateDisk
	 * @param nameState
	 * @return
	 */
	private String getDifferenceImage(String previousStateDisk, String namePreviousState, String stateDisk, String nameState) {
		try {

			// State Images paths
			String previousStatePath = new File(previousStateDisk).getCanonicalFile().toString();
			String statePath = new File(stateDisk).getCanonicalFile().toString();
			
			System.out.println("Action: " + actionCount);
			System.out.println("PreviousState: " + previousStatePath);
			System.out.println("CurrentState: " + statePath);
			
			// TESTAR launches the process to create the image and move forward without wait if exists
			// thats why we need to check and wait to obtain the image-diff
			while(!new File(previousStatePath).exists() || !new File(statePath).exists()){
				System.out.println("Waiting for Screenshot creation");
				System.out.println("Waiting... PreviousState: " + previousStatePath);
				System.out.println("Waiting... CurrentState: " + statePath);
				Util.pause(2);
			}
			
			BufferedImage img1 = ImageIO.read(new File(previousStatePath));
			BufferedImage img2 = ImageIO.read(new File(statePath));

			int width1 = img1.getWidth(); // Change - getWidth() and getHeight() for BufferedImage
			int width2 = img2.getWidth(); // take no arguments
			int height1 = img1.getHeight();
			int height2 = img2.getHeight();
			if ((width1 != width2) || (height1 != height2)) {
				System.out.println("Error: Images dimensions mismatch");
				return "";
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
			// check if we have a directory for the screenshots yet
			File screenshotDir = new File(OutputStructure.htmlOutputDir + File.separator);

			// save the file to disk
			File screenshotFile = new File(screenshotDir, "diff_"+ namePreviousState + "_" + nameState + ".png");
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
}

/**
 * Helper Class to prepare HTML State report Difference
 */
class HTMLDifference {
	
	PrintWriter out = null;

	public HTMLDifference () { 
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
		
		String htmlReportName =  OutputStructure.htmlOutputDir + File.separator + "StateDifferenceReport.html";

		try {
			out = new PrintWriter(new File(htmlReportName).getCanonicalPath(), HTMLReporter.CHARSET);
		} catch (IOException e) { e.printStackTrace(); }

		for(String s:HEADER){
			out.println(s);
			out.flush();
		}
	}
	
	public void addStateDifferenceStep(int actionCount, String previousState, String state, String difference) {
		out.println("<h2> Specific State changes for action " + actionCount + " </h2>");
		try {
			out.println("<p><img src=\"" + new File(previousState).getCanonicalFile().toString() + "\">");
			out.println("<img src=\"" + new File(state).getCanonicalFile().toString() + "\">");
			out.println("<img src=\"" + new File(difference).getCanonicalFile().toString() + "\"></p>");
		} catch (IOException e) {
			out.println("<p> ERROR ADDING IMAGES </p>");;
		}
		out.flush();
	}
}