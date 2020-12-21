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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.*;
import org.fruit.alayer.windows.WinProcess;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.fruit.monkey.RuntimeControlsProtocol.Modes;
import org.testar.OutputStructure;
import org.testar.action.priorization.ActionTags;
import org.testar.action.priorization.ActionTags.ActionGroupType;
import org.testar.protocols.experiments.SwingSet2Protocol;

/**
 * This protocol together with the settings provides a specific behavior to test SwingSet2
 * We will use Java Access Bridge settings (AccessBridgeEnabled = true) for widget tree extraction
 *
 * It uses Action Group RL algorithm.
 */
public class Protocol_swingset2_rl_action_group extends SwingSet2Protocol {

	List<String> idCustomsGlobalList = new ArrayList<>();
	List<String> widgetNamesGlobalList = new ArrayList<>();
	List<String> actionGroupsGlobalList = new ArrayList<>();
	List<Double> zIndexesGlobalList = new ArrayList<>();
	List<Double> qLearningsGlobalList = new ArrayList<>();

	private long startSequenceTime;
	private String reportTimeDir;

	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 * @param   settings  the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings){
		super.initialize(settings);

		System.out.println("*** NEW EXECUTION ***");

		// SwingSet2: Requires Java Access Bridge
		System.out.println("Are we running Java Access Bridge ? " + settings.get(ConfigTags.AccessBridgeEnabled, false));

		// TESTAR will execute the SUT with Java
		// We need this to add JMX parameters properly (-Dcom.sun.management.jmxremote.port=5000)
		WinProcess.java_execution = true;

		// Enable the inspection of internal cells on Java Swing Tables
		//AccessBridgeFetcher.swingJavaTableDescend = true;

		// Copy "bin/settings/protocolName/build.xml" file to "bin/jacoco/build.xml"
		copyJacocoBuildFile();
	}

	/**
	 * This method is invoked each time the TESTAR starts the SUT to generate a new sequence.
	 * This can be used for example for bypassing a login screen by filling the username and password
	 * or bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
	 * the SUT's configuration files etc.)
	 */
	@Override
	protected void beginSequence(SUT system, State state){
		startSequenceTime = System.currentTimeMillis();
		try{
			reportTimeDir = new File(OutputStructure.outerLoopOutputDir).getCanonicalPath();
		} catch (Exception e) {
			System.out.println("sequenceTimeUntilActions.txt can not be created " );
			e.printStackTrace();
		}
		super.beginSequence(system, state);
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
		actions = deriveClickTypeScrollActionsFromTopLevelWidgets(actions, state);

		if(actions.isEmpty()){
			// If the top level widgets did not have any executable widgets, try all widgets:
			// Derive left-click actions, click and type actions, and scroll actions from
			// all widgets of the GUI:
			actions = deriveClickTypeScrollActionsFromAllWidgetsOfState(actions, state);
		}

		// Decremento iterativo de los QValues
		for(Widget w : state) {
			System.out.println("NEW WIDGET DETECTED:");

			updateListsBefore(w);

			// Asignacion de ActionGroup
			ActionTags.ActionGroupType actionGroup = w.get(ActionTags.ActionGroup, null);
			if(actionGroup == null) {
				Role actionRole = w.get(Tags.Role);
				String actionRoleStr = actionRole.toString();
				setActionGroup(actionRoleStr, w);
			}

			// Asignacion de ZIndex
			if(w.get(ActionTags.ZIndex, 0) == 0) {
				double wZIndex = w.get(Tags.ZIndex, 0.0);
				if(wZIndex != 0.0) {
					int ZIndexInt = (int) wZIndex;
					w.set(ActionTags.ZIndex, ZIndexInt);
				}
			}

			// Asignacion de QLearning
			double actionQLearning = w.get(ActionTags.QLearning, 0.0);
			if(actionGroup == null && actionQLearning == 0.0) {
				w.set(ActionTags.QLearning, 1.0);
			}

			// 1) Si el widget no tiene ActionGroup y tampoco un valor en su tag QLearning, asignar el valor mas alto (1) a su Tag QLearning.
			// 2) Si el widget tiene un ActionGroup pero no un valor en su tag QLearning, comprobar si existen widgets en las listas con su mismo Action Group y ZIndex.
			// 3)		Si existen, comprobar si alguno de los widgets de las listas tienen un valor en su tag QLearning.
			// 4)			Si algun widget de las listas tiene un valor, asignar al widget inicial el valor de QLearning del widget de las listas.
			// 5)			Si ningun widget de las listas tiene un valor, asignar al widget inicial el valor mas alto (1) a su Tag QLearning.
			// 6)		Si no existen, asignar al widget inicial el valor mas alto (1) a su Tag QLearning.
			if(actionGroup != null && actionQLearning == 0.0) {	// 2)
				double auxQValue = 0.0;
				for (int i = 0; i < actionGroupsGlobalList.size(); i ++) {
					String aActionGroup = w.get(ActionTags.ActionGroup, null).toString();
					String a2ActionGroup = actionGroupsGlobalList.get(i);
					if(a2ActionGroup != null && aActionGroup != null) {
						double aZIndex = w.get(ActionTags.ZIndex, null);
						double a2ZIndex = zIndexesGlobalList.get(i);
						if(a2ActionGroup.equals(aActionGroup) && aZIndex == a2ZIndex) {
							double a2QLearning = qLearningsGlobalList.get(i);
							if ((a2QLearning > 0.0) && (a2QLearning < auxQValue)) {	// 3)
								auxQValue = a2QLearning; // 4)
							}
						}
					}
				}

				if(auxQValue != 0.0) {
					w.set(ActionTags.QLearning, auxQValue);
				} else { // 5) 6)
					w.set(ActionTags.QLearning, 1.0);
				}
			}
			System.out.println("Name: " + w.get(Tags.Desc, "NULL") + ".\t\t QLearning = " + w.get(ActionTags.QLearning, 0.0) + ".\t\tID: " + w.get(Tags.AbstractIDCustom));
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
		//Seleccionar el widget con el mayor valor en su tag QLearning.
		String maxID = "";
		double maxQLearning = 0.0;
		double wQLearning = 0.0;

		for(Action a : actions) {
			String aID = a.get(Tags.OriginWidget).get(Tags.AbstractIDCustom);
			for(Widget w : state) {
				String wID = w.get(Tags.AbstractIDCustom);
				if(aID == wID) {
					wQLearning = w.get(ActionTags.QLearning, 0.0);
					if(wQLearning > maxQLearning) {
						maxQLearning = wQLearning;
						maxID = wID;
					}
				}
			}
		}

		// Actualizar los valores de QLearning de los widgets. El valor en widgets de un ActionGroup sera menor
		// a medida que se ejecuten acciones de dicho ActionGroup.

		for(Action a : actions) {
			String aID = a.get(Tags.OriginWidget).get(Tags.AbstractIDCustom);
			if(aID == maxID) {
				double newQL = greaterThanZero(a.get(Tags.OriginWidget).get(ActionTags.QLearning, 0.0) - 0.05);
				a.get(Tags.OriginWidget).set(ActionTags.QLearning, newQL);

				System.out.println("Widget to be selected: " + a.get(Tags.OriginWidget).get(Tags.Desc) + "\t\t New QLearning = " + a.get(Tags.OriginWidget).get(ActionTags.QLearning, 0.0));

				updateListsAfter(a.get(Tags.OriginWidget));

				System.out.println(" ... END ...");
				System.out.println(" ... widgetNamesGlobalList: " + widgetNamesGlobalList);
				System.out.println(" ... actionGroupsGlobalList: " + actionGroupsGlobalList);
				System.out.println(" ... qLearningsGlobalList: " + qLearningsGlobalList);
				System.out.println(" ... zIndexesGlobalList: " + zIndexesGlobalList);

				//Action maxAction = getAction(a.get(Tags.OriginWidget), actions);
				return a;
			}
		}

		System.out.println(" ... NO ENCONTRADO");

		return(super.selectAction(state, actions));
	}

	/**
	 * Execute the selected action.
	 * Extract and create JaCoCo coverage report (After each action JaCoCo report will be created).
	 */
	@Override
	protected boolean executeAction(SUT system, State state, Action action){
		boolean actionExecuted = super.executeAction(system, state, action);

		// Write sequence duration to CLI and to file
		long  sequenceDurationSoFar = System.currentTimeMillis() - startSequenceTime;
		System.out.println();
		System.out.println("Elapsed time until action " + actionCount + ": " + sequenceDurationSoFar);

		long minutes = (sequenceDurationSoFar / 1000)  / 60;
		int seconds = (int)((sequenceDurationSoFar / 1000) % 60);
		System.out.println("Elapsed time until action " + actionCount + ": " + + minutes + " minutes, "+ seconds + " seconds.");
		System.out.println();
		// Write sequence duration to file
		try {
			FileWriter myWriter = new FileWriter(reportTimeDir + "/" + OutputStructure.startInnerLoopDateString + "_" + OutputStructure.executedSUTname + "_actionTimeStamps.txt", true);
			myWriter.write(sequenceDurationSoFar + "\r\n");
			myWriter.close();
			System.out.println("Wrote time so far to file." + reportTimeDir + "/_sequenceTimeUntilAction.txt");
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

		// Extract and create JaCoCo action coverage report for Generate Mode
		if(settings.get(ConfigTags.Mode).equals(Modes.Generate)) {
			extractJacocoActionReport();
		}
		return actionExecuted;
	}

	/**
	 * This method is invoked each time the TESTAR has reached the stop criteria for generating a sequence.
	 * This can be used for example for graceful shutdown of the SUT, maybe pressing "Close" or "Exit" button
	 */
	@Override
	protected void finishSequence() {

		// Extract and create JaCoCo sequence coverage report for Generate Mode
		if(settings.get(ConfigTags.Mode).equals(Modes.Generate)) {
			extractJacocoSequenceReport();
		}

		super.finishSequence();

		// Write sequence duration to CLI and to file
		long  sequenceDuration = System.currentTimeMillis() - startSequenceTime;
		System.out.println();
		System.out.println("Sequence duration: " + sequenceDuration);
		long minutes = (sequenceDuration / 1000)  / 60;
		int seconds = (int)((sequenceDuration / 1000) % 60);
		System.out.println("Sequence duration: " + minutes + " minutes, "+ seconds + " seconds.");
		System.out.println();
		try {
			String reportDir = new File(OutputStructure.outerLoopOutputDir).getCanonicalPath();//  + File.separator;
			FileWriter myWriter = new FileWriter(reportDir + "/" + OutputStructure.startInnerLoopDateString + "_" + OutputStructure.executedSUTname + "_sequenceDuration.txt");
			myWriter.write("Sequence duration: " + minutes + " minutes, " + seconds + " seconds.   (" + sequenceDuration + " mili)");
			myWriter.close();
			System.out.println("Wrote time to file." + reportDir + "/_sequenceDuration.txt");
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	/**
	 * This methods stops the SUT
	 *
	 * @param system
	 */
	@Override
	protected void stopSystem(SUT system) {
		super.stopSystem(system);

		// This is the default JaCoCo generated file, we dumped our desired file with MBeanClient (finishSequence)
		// In this protocol this one is residual, so just delete
		if(new File("jacoco.exec").exists()) {
			System.out.println("Deleted residual jacoco.exec file ? " + new File("jacoco.exec").delete());
		}
	}

	/**
	 * This method is called after the last sequence, to allow for example handling the reporting of the session
	 */
	@Override
	protected void closeTestSession() {
		super.closeTestSession();
		// Extract and create JaCoCo run coverage report for Generate Mode
		if(settings.get(ConfigTags.Mode).equals(Modes.Generate)) {
			extractJacocoRunReport();
			compressJacocoReportFolder();
		}
	}

	private void updateListsBefore(Widget w) {
		String idCustom = w.get(Tags.AbstractIDCustom);
		if(idCustomsGlobalList.contains(idCustom)) {
			int index = idCustomsGlobalList.indexOf(idCustom);
			w.set(ActionTags.QLearning, qLearningsGlobalList.get(index));
			int zIndexInt = (int) Math.round(zIndexesGlobalList.get(index));
			w.set(ActionTags.ZIndex, zIndexInt);
			setActionGroup(actionGroupsGlobalList.get(index), w);

		}
	}

	private void updateListsAfter(Widget w) {
		String idCustom = w.get(Tags.AbstractIDCustom);
		String maxActionGroup = w.get(ActionTags.ActionGroup, ActionGroupType.UIAWidget).toString();
		double maxActionQL = w.get(ActionTags.QLearning);
		String maxActionDesc = w.get(Tags.Desc, "NULL");
		double maxActionZIndex = w.get(Tags.ZIndex);

		if(idCustomsGlobalList.isEmpty()) {
			idCustomsGlobalList.add(idCustom);
			widgetNamesGlobalList.add(maxActionDesc);
			actionGroupsGlobalList.add(maxActionGroup);
			qLearningsGlobalList.add(maxActionQL);
			zIndexesGlobalList.add(maxActionZIndex);
		} else {
			if(idCustomsGlobalList.contains(idCustom)) {
				int index = idCustomsGlobalList.indexOf(idCustom);
				actionGroupsGlobalList.set(index, maxActionGroup);
				qLearningsGlobalList.set(index, maxActionQL);
				zIndexesGlobalList.set(index, maxActionZIndex);
			} else {
				idCustomsGlobalList.add(idCustom);
				widgetNamesGlobalList.add(maxActionDesc);
				actionGroupsGlobalList.add(maxActionGroup);
				qLearningsGlobalList.add(maxActionQL);
				zIndexesGlobalList.add(maxActionZIndex);
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

	private double greaterThanZero (double d) {
		if(d >= 0.0) return d;
		else return 0.1;
	}

	private void setActionGroup(String actionRoleStr, Widget w) {
		ActionTags.ActionGroupType actionGroup = null;
		switch(actionRoleStr) {
		case "UIAWidget":
			actionGroup = ActionGroupType.UIAWidget;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIAAppBar":
			actionGroup = ActionGroupType.UIAAppBar;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIAButton":
			actionGroup = ActionGroupType.UIAButton;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIACalendar":
			actionGroup = ActionGroupType.UIACalendar;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIACheckBox":
			actionGroup = ActionGroupType.UIACheckBox;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIAComboBox":
			actionGroup = ActionGroupType.UIAComboBox;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIACustomControl":
			actionGroup = ActionGroupType.UIACustomControl;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIADataGrid":
			actionGroup = ActionGroupType.UIADataGrid;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIADataItem":
			actionGroup = ActionGroupType.UIADataItem;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIADocument":
			actionGroup = ActionGroupType.UIADocument;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIAEdit":
			actionGroup = ActionGroupType.UIAEdit;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIAGroup":
			actionGroup = ActionGroupType.UIAGroup;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIAHeader":
			actionGroup = ActionGroupType.UIAHeader;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIAHeaderItem":
			actionGroup = ActionGroupType.UIAHeaderItem;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIAHyperlink":
			actionGroup = ActionGroupType.UIAHyperlink;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIAImage":
			actionGroup = ActionGroupType.UIAImage;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIAList":
			actionGroup = ActionGroupType.UIAList;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIAListItem":
			actionGroup = ActionGroupType.UIAListItem;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIAMenuBar":
			actionGroup = ActionGroupType.UIAMenuBar;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIAMenu":
			actionGroup = ActionGroupType.UIAMenu;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIAMenuItem":
			actionGroup = ActionGroupType.UIAMenuItem;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIAPane":
			actionGroup = ActionGroupType.UIAPane;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIAProgressBar":
			actionGroup = ActionGroupType.UIAProgressBar;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIARadioButton":
			actionGroup = ActionGroupType.UIARadioButton;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIAScrollBar":
			actionGroup = ActionGroupType.UIAScrollBar;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIASemanticZoom":
			actionGroup = ActionGroupType.UIASemanticZoom;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIASeparator":
			actionGroup = ActionGroupType.UIASeparator;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIASlider":
			actionGroup = ActionGroupType.UIASlider;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIASpinner":
			actionGroup = ActionGroupType.UIASpinner;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIASplitButton":
			actionGroup = ActionGroupType.UIASplitButton;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIAStatusBar":
			actionGroup = ActionGroupType.UIAStatusBar;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIATabControl":
			actionGroup = ActionGroupType.UIATabControl;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIATabItem":
			actionGroup = ActionGroupType.UIATabItem;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIATable":
			actionGroup = ActionGroupType.UIATable;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIAText":
			actionGroup = ActionGroupType.UIAText;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIAThumb":
			actionGroup = ActionGroupType.UIAThumb;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIATitleBar":
			actionGroup = ActionGroupType.UIATitleBar;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIAToolBar":
			actionGroup = ActionGroupType.UIAToolBar;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIAToolTip":
			actionGroup = ActionGroupType.UIAToolTip;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIATree":
			actionGroup = ActionGroupType.UIATree;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIATreeItem":
			actionGroup = ActionGroupType.UIATreeItem;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		case "UIAWindow":
			actionGroup = ActionGroupType.UIAWindow;
			w.set(ActionTags.ActionGroup, actionGroup);
			break;
		}
	}
}