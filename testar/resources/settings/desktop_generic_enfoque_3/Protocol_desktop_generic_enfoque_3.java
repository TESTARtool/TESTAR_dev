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
public class Protocol_desktop_generic_enfoque_3 extends DesktopProtocol {
	
	String lastWidgetID = "";
	
	int numWidgetsBefore = 0;
	int numWidgetsNow = 0;

	List<String> idCustomsGlobalList = new ArrayList<String>();
	List<String> widgetNamesGlobalList = new ArrayList<String>();
	List<Double> zIndexesGlobalList = new ArrayList<Double>();
	List<Double> qLearningsGlobalList = new ArrayList<Double>();

	List<String> lastStateWIDList = new ArrayList<String>();
	List<String> thisStateWIDList = new ArrayList<String>();
	
	
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
			//System.out.println("No actions from top level widgets, changing to all widgets.");
			// Derive left-click actions, click and type actions, and scroll actions from
			// all widgets of the GUI:
			actions = deriveClickTypeScrollActionsFromAllWidgetsOfState(actions, system, state);
		}
		
		
		
		// Numero de widgets en el estado previo y en el actual
		getLeafWidgets(state);
		numWidgetsNow = thisStateWIDList.size();
				
		System.out.println("*** numWidgetsBefore: " + numWidgetsBefore);
		System.out.println("*** numWidgetsNow: " + numWidgetsNow);
		
		for (Widget w : state) {
			Action a = getAction(w, actions);
			updateListsBefore(a);
				
			// Inicializacion del valor de recompensa a 1.0
			if(a.get(ActionTags.QLearning, 0.0) == 0.0) a.set(ActionTags.QLearning, 1.0);
				
			System.out.println("Name: " + w.get(Tags.Desc, "NULL") + ".\t\t QLearning = " + a.get(ActionTags.QLearning, 0.0) + ".\t\tID: " + w.get(Tags.AbstractIDCustom));
		}
		

		// Si no existe widget tree anterior, no hacer nada
		if(numWidgetsBefore > 0) {
			double persistentDecrement = getPersistentDecrement(state);
			int index = idCustomsGlobalList.indexOf(lastWidgetID);
			if(index != -1) {
				double numWidgetsBeforeDouble = numWidgetsBefore;
				double numWidgetsNowDouble = numWidgetsNow;
				double newQLearningValue = qLearningsGlobalList.get(index);
				
				if(numWidgetsBefore < numWidgetsNow) {
					newQLearningValue = newQLearningValue - persistentDecrement +
							((numWidgetsNowDouble - numWidgetsBeforeDouble) / numWidgetsBeforeDouble);
				} else if(numWidgetsBefore > numWidgetsNow) {
					newQLearningValue = greaterThanZero(newQLearningValue - persistentDecrement -
							(numWidgetsNowDouble / numWidgetsBeforeDouble));
				} else {
					newQLearningValue = greaterThanZero(newQLearningValue - persistentDecrement);
				}
				
				System.out.println("... ... newQLearningValue = " + newQLearningValue);
				qLearningsGlobalList.set(index, newQLearningValue);
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
		numWidgetsBefore = numWidgetsNow;
		lastStateWIDList = thisStateWIDList;
		
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

	private double getPersistentDecrement(State state) {
		int persistentWidgetNum = 0;
		
		for(String thisWID : thisStateWIDList) {
			if(lastStateWIDList.contains(thisWID)) {
				persistentWidgetNum ++;
			}
		}
		lastStateWIDList.clear();

		double persistentDecrement = persistentWidgetNum * 0.01;
		return persistentDecrement;
	}
	
	private void getLeafWidgets(State state) {
		for(Widget w : state) {
			if(w.childCount() == 0) {
				if(w.get(Tags.Role).toString() == "UIAButton" || w.get(Tags.Role).toString() == "UIAMenuItem") {
					String wID = w.get(Tags.AbstractIDCustom);
					if(!thisStateWIDList.contains(wID)) {
						thisStateWIDList.add(wID);
					}
				}
			}
			else {
				getLeafWidgets(w);
			}
		}
	}
	
	private void getLeafWidgets(Widget widget) {
		for(int i = 0; i < widget.childCount(); i ++) {
			Widget w = widget.child(i);
			if(w.childCount() == 0) {
				if(w.get(Tags.Role).toString() == "UIAButton" || w.get(Tags.Role).toString() == "UIAMenuItem") {
					String wID = w.get(Tags.AbstractIDCustom);
					if(!thisStateWIDList.contains(wID)) {
						thisStateWIDList.add(wID);
					}
				}
			}
			else {
				getLeafWidgets(w);
			}
		}
	}

}