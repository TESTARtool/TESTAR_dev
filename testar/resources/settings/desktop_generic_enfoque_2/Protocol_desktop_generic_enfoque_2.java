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


/**
 * This protocol provides default TESTAR behaviour to test Windows desktop applications.
 *
 * It uses random action selection algorithm.
 */
public class Protocol_desktop_generic_enfoque_2 extends DesktopProtocol {
	List<String> idCustomsGlobalList = new ArrayList<String>();
	List<String> widgetNamesGlobalList = new ArrayList<String>();
	List<String> actionGroupsGlobalList = new ArrayList<String>();
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
		System.out.println("*** NEW EXECUTION ***");
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
//			System.out.println("No actions from top level widgets, changing to all widgets.");
			// Derive left-click actions, click and type actions, and scroll actions from
			// all widgets of the GUI:
			actions = deriveClickTypeScrollActionsFromAllWidgetsOfState(actions, system, state);
		}
		
		
		for(Widget w : state) {
			if(w.get(Tags.Role).toString() == "UIAButton" || w.get(Tags.Role).toString() == "UIAMenuItem") {
				Action a = getAction(w, actions);
				
				System.out.println("NEW WIDGET DETECTED:");
				
				updateListsBefore(a);
				
				// Asignacion de ActionGroup
				ActionTags.ActionGroupType actionGroup = a.get(ActionTags.ActionGroup, null);
				if(actionGroup == null) {
					Role actionRole = w.get(Tags.Role);
					String actionRoleStr = actionRole.toString();
					setActionGroup(actionRoleStr, a);
				}

				// Asignacion de ZIndex
				if(a.get(ActionTags.ZIndex, 0) == 0) {
					double wZIndex = w.get(Tags.ZIndex, 0.0);
					if(wZIndex != 0.0) {
						int wZIndexInt = (int) wZIndex;
						a.set(ActionTags.ZIndex, wZIndexInt);
					}
				}
				
				// Asignacion de QLearning inicial
				double actionQLearning = a.get(ActionTags.QLearning, 0.0);
				if(actionGroup == null && actionQLearning == 0.0) {
					a.set(ActionTags.QLearning, 1.0);
				}
				
				// 1) Si el widget no tiene ActionGroup y tampoco un valor en su tag QLearning, asignar el valor mas alto (1) a su Tag QLearning.
				// 2) Si el widget tiene un ActionGroup pero no un valor en su tag QLearning, comprobar si existen widgets en las listas con su mismo Action Group y ZIndex.
				// 3)		Si existen, comprobar si alguno de los widgets de las listas tienen un valor en su tag QLearning.
				// 4)			Si alg�n widget de las listas tiene un valor, asignar al widget inicial el valor de QLearning del widget de las listas.
				// 5)			Si ning�n widget de las listas tiene un valor, asignar al widget inicial el valor mas alto (1) a su Tag QLearning.
				// 6)		Si no existen, asignar al widget inicial el valor mas alto (1) a su Tag QLearning.
				if(actionGroup != null && actionQLearning == 0.0) {
					double auxQValue = 0.0;
					for (int i = 0; i < actionGroupsGlobalList.size(); i ++) {
						String aActionGroup = a.get(ActionTags.ActionGroup, null).toString();
						String a2ActionGroup = actionGroupsGlobalList.get(i);
						if(a2ActionGroup != null && aActionGroup != null) {
							double aZIndex = a.get(ActionTags.ZIndex, null);
							double a2ZIndex = zIndexesGlobalList.get(i);
							if(a2ActionGroup == aActionGroup && aZIndex == a2ZIndex) {
								double a2QLearning = qLearningsGlobalList.get(i);
								if ((a2QLearning > 0.0) && (a2QLearning < auxQValue)) {
									auxQValue = a2QLearning;
								}
							}
						}
					}
								
					if(auxQValue != 0.0) {
						a.set(ActionTags.QLearning, auxQValue);
					} else {
						a.set(ActionTags.QLearning, 1.0);
					}
				}
				System.out.println("Name: " + w.get(Tags.Desc, "NULL") + ".\t\t QLearning = " + a.get(ActionTags.QLearning, 0.0) + ".\t\tID: " + w.get(Tags.AbstractIDCustom));
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
		//Seleccionar el widget con el mayor valor en su tag QLearning.
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
				
		// Actualizar los valores de QLearning de los widgets. El valor en widgets de un ActionGroup sera menor
		// a medida que se ejecuten acciones de dicho ActionGroup.
		for(Action a : actions) {
			String aID = a.get(Tags.OriginWidget).get(Tags.AbstractIDCustom);
			if(aID == maxID) {
				double newQL = greaterThanZero(a.get(ActionTags.QLearning, 0.0) - 0.05);
				a.set(ActionTags.QLearning, newQL);
			
				System.out.println("Action to be executed: " + a.get(Tags.OriginWidget).get(Tags.Desc) + "\t\t New QLearning = " + a.get(ActionTags.QLearning, 0.0));
								
				updateListsAfter(a);
									
				System.out.println(" ... END ...");
				System.out.println(" ... widgetNamesGlobalList: " + widgetNamesGlobalList);
				System.out.println(" ... actionGroupsGlobalList: " + actionGroupsGlobalList);
				System.out.println(" ... qLearningsGlobalList: " + qLearningsGlobalList);
				System.out.println(" ... zIndexesGlobalList: " + zIndexesGlobalList);
									
				return a;
			}
		}
						
		return(super.selectAction(state, actions));
	}
	
	private double greaterThanZero (double d) {
		if(d >= 0.0) return d;
		else return 0.1;
	}
	
	private void setActionGroup(String actionRoleStr, Action a) {
		ActionTags.ActionGroupType actionGroup = null;
		switch(actionRoleStr) {
			case "UIAWidget":
				actionGroup = ActionGroupType.UIAWidget;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIAAppBar":
				actionGroup = ActionGroupType.UIAAppBar;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIAButton":
				actionGroup = ActionGroupType.UIAButton;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIACalendar":
				actionGroup = ActionGroupType.UIACalendar;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIACheckBox":
				actionGroup = ActionGroupType.UIACheckBox;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIAComboBox":
				actionGroup = ActionGroupType.UIAComboBox;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIACustomControl":
				actionGroup = ActionGroupType.UIACustomControl;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIADataGrid":
				actionGroup = ActionGroupType.UIADataGrid;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIADataItem":
				actionGroup = ActionGroupType.UIADataItem;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIADocument":
				actionGroup = ActionGroupType.UIADocument;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIAEdit":
				actionGroup = ActionGroupType.UIAEdit;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIAGroup":
				actionGroup = ActionGroupType.UIAGroup;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIAHeader":
				actionGroup = ActionGroupType.UIAHeader;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIAHeaderItem":
				actionGroup = ActionGroupType.UIAHeaderItem;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIAHyperlink":
				actionGroup = ActionGroupType.UIAHyperlink;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIAImage":
				actionGroup = ActionGroupType.UIAImage;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIAList":
				actionGroup = ActionGroupType.UIAList;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIAListItem":
				actionGroup = ActionGroupType.UIAListItem;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIAMenuBar":
				actionGroup = ActionGroupType.UIAMenuBar;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIAMenu":
				actionGroup = ActionGroupType.UIAMenu;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIAMenuItem":
				actionGroup = ActionGroupType.UIAMenuItem;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIAPane":
				actionGroup = ActionGroupType.UIAPane;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIAProgressBar":
				actionGroup = ActionGroupType.UIAProgressBar;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIARadioButton":
				actionGroup = ActionGroupType.UIARadioButton;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIAScrollBar":
				actionGroup = ActionGroupType.UIAScrollBar;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIASemanticZoom":
				actionGroup = ActionGroupType.UIASemanticZoom;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIASeparator":
				actionGroup = ActionGroupType.UIASeparator;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIASlider":
				actionGroup = ActionGroupType.UIASlider;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIASpinner":
				actionGroup = ActionGroupType.UIASpinner;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIASplitButton":
				actionGroup = ActionGroupType.UIASplitButton;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIAStatusBar":
				actionGroup = ActionGroupType.UIAStatusBar;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIATabControl":
				actionGroup = ActionGroupType.UIATabControl;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIATabItem":
				actionGroup = ActionGroupType.UIATabItem;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIATable":
				actionGroup = ActionGroupType.UIATable;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIAText":
				actionGroup = ActionGroupType.UIAText;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIAThumb":
				actionGroup = ActionGroupType.UIAThumb;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIATitleBar":
				actionGroup = ActionGroupType.UIATitleBar;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIAToolBar":
				actionGroup = ActionGroupType.UIAToolBar;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIAToolTip":
				actionGroup = ActionGroupType.UIAToolTip;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIATree":
				actionGroup = ActionGroupType.UIATree;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIATreeItem":
				actionGroup = ActionGroupType.UIATreeItem;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
			case "UIAWindow":
				actionGroup = ActionGroupType.UIAWindow;
				a.set(ActionTags.ActionGroup, actionGroup);
				break;
		}
	}
	
	private void updateListsBefore(Action a) {
		String idCustom = a.get(Tags.OriginWidget).get(Tags.AbstractIDCustom);
		if(idCustomsGlobalList.contains(idCustom)) {
			int index = idCustomsGlobalList.indexOf(idCustom);
			a.set(ActionTags.QLearning, qLearningsGlobalList.get(index));
			int zIndexInt = (int) Math.round(zIndexesGlobalList.get(index));
			a.set(ActionTags.ZIndex, zIndexInt);
			setActionGroup(actionGroupsGlobalList.get(index), a);
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
			actionGroupsGlobalList.add(maxActionGroup);
			qLearningsGlobalList.add(maxQL);
			zIndexesGlobalList.add(maxZIndex);
		} else {
			if(idCustomsGlobalList.contains(idCustom)) {
				int index = idCustomsGlobalList.indexOf(idCustom);
				actionGroupsGlobalList.set(index, maxActionGroup);
				qLearningsGlobalList.set(index, maxQL);
				zIndexesGlobalList.set(index, maxZIndex);
				widgetNamesGlobalList.set(index, maxDesc);
			} else {
				idCustomsGlobalList.add(idCustom);
				widgetNamesGlobalList.add(maxDesc);
				actionGroupsGlobalList.add(maxActionGroup);
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
}