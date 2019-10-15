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


import java.util.HashSet;
import java.util.Set;

import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.exceptions.*;
import org.fruit.alayer.windows.UIATags;
import org.fruit.monkey.Settings;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;
import org.testar.protocols.DesktopProtocol;
import org.testar.protocols.PonsseDesktopProtocol;

/**
 * This protocol provides default TESTAR behaviour to test Windows desktop applications.
 *
 * It uses random action selection algorithm.
 */
public class Protocol_desktop_generic_opti5g_SpeciesGroup extends PonsseDesktopProtocol {

	/**
	 * This method is invoked each time the TESTAR starts the SUT to generate a new sequence.
	 * This can be used for example for bypassing a login screen by filling the username and password
	 * or bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
	 * the SUT's configuration files etc.)
	 */
	 @Override
	protected void beginSequence(SUT system, State state){
		System.out.println("DEBUG: Begin sequence, looking if shutdown button is available");
		if(widgetWithAutomationIdFound("btnShutDown", state, system, 20)){
			System.out.println("DEBUG: Ponsse UI ready!");
		}

		// if testar button is not found, creating new user:
		if(!widgetWithAutomationIdFound("btntestar", state, system, 1)){
			// Ponsse GUI ready and testar button not found - creating testar user
			System.out.println("DEBUG: looking for btnAddUser");
			waitAndClickButtonByAutomationId("btnAddUser", state, system, 20);

			//updating TESTAR state:
			state=getState(system);

			System.out.println("DEBUG: looking for NextButton");
			waitAndClickButtonByAutomationId("NextButton", state, system, 20);

			//updating TESTAR state:
			state=getState(system);

			System.out.println("DEBUG: looking for NextButton");
			waitAndClickButtonByAutomationId("NextButton", state, system, 20);

			//updating TESTAR state:
			state=getState(system);

			System.out.println("DEBUG: typing first name");
			waitAndTypeTextByAutomationId("txtBoxFirstName", "testar", state, system, 20);

			System.out.println("DEBUG: typing last name");
			waitAndTypeTextByAutomationId("txtBoxLastName", "test", state, system, 20);

			System.out.println("DEBUG: typing user ID");
			waitAndTypeTextByAutomationId("txtBoxUserId", "testar@ponsse.com", state, system, 20);

			System.out.println("DEBUG: looking for NextButton");
			waitAndClickButtonByAutomationId("NextButton", state, system, 20);

			//updating TESTAR state:
			state=getState(system);

			System.out.println("DEBUG: looking for NextButton");
			waitAndClickButtonByAutomationId("NextButton", state, system, 20);

			//updating TESTAR state:
			state=getState(system);

			System.out.println("DEBUG: looking for NextButton");
			waitAndClickButtonByAutomationId("NextButton", state, system, 20);

			//updating TESTAR state:
			state=getState(system);

			System.out.println("DEBUG: looking for NextButton");
			waitAndClickButtonByAutomationId("NextButton", state, system, 20);

			//updating TESTAR state:
			state=getState(system);

			System.out.println("DEBUG: typing new PIN code");
			waitAndTypeTextByAutomationId("passwordBoxPinBox1", "1111", state, system, 20);

			System.out.println("DEBUG: typing new PIN code again");
			waitAndTypeTextByAutomationId("passwordBoxPinBox2", "1111", state, system, 20);

			System.out.println("DEBUG: looking for buttonFinish");
			waitAndClickButtonByAutomationId("buttonFinish", state, system, 20);
		}

		 System.out.println("DEBUG: closing virtual keyboard if visible");
//		 waitAndClickButton("btnHideVkb", state, system, 20);
		 executeClickOnTextOrImagePath("settings/desktop_generic_opti5g_SpeciesGroup/nappain_keyboard.PNG");

		// Ponsse user created - selecting testar user
		 System.out.println("DEBUG: looking for btntestar");
		 waitAndClickButtonByAutomationId("btntestar", state, system, 20);

		 //updating TESTAR state:
		 state=getState(system);
		 
		 System.out.println("DEBUG: looking for pin code");
		 waitAndTypeTextByAutomationId("passwordBoxPinBoxCode", "1111", state, system, 20);
		 
		 System.out.println("DEBUG: looking for btnLogOn");
		 waitAndClickButtonByAutomationId("btnLogOn", state, system, 20);

		 //updating TESTAR state:
		 state=getState(system);

		 System.out.println("DEBUG: looking for btnPlanningView");
		 waitAndClickButtonByAutomationId("btnPlanningView", state, system, 20);

		 //updating TESTAR state:
		 state=getState(system);
		 
		 System.out.println("DEBUG: looking for contextMenuHandle");
		 waitAndClickButtonByAutomationId("contextMenuHandle", state, system, 20);

		 //updating TESTAR state:
		 state=getState(system);
		 
		 System.out.println("DEBUG: looking for buttonSpeciesGroups");
		 waitAndClickButtonByAutomationId("buttonSpeciesGroups", state, system, 20);

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

		if(actions.size()==0){
			// If the top level widgets did not have any executable widgets, try all widgets:
//			System.out.println("No actions from top level widgets, changing to all widgets.");
			// Derive left-click actions, click and type actions, and scroll actions from
			// all widgets of the GUI:
			actions = deriveClickTypeScrollActionsFromAllWidgetsOfState(actions, system, state);
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
		//checking if the state is in "SpeciesGroupsWindow"
		boolean isSpeciesGroupsWindow = false;
		if(widgetWithAutomationIdFound("SpeciesGroupsWindow",state)){
			System.out.println("GUI is in SpeciesGroupsWindow - filtering back button");
			isSpeciesGroupsWindow=true;
		}

		Set<Action> filteredActions = new HashSet<Action>();
		for(Action action:actions){
			try{
				// filtering back button and help button away from available actions:
				if(action.get(Tags.OriginWidget).get(UIATags.UIAAutomationId).equalsIgnoreCase("btnHelp")){
					// filtering help button away
				}else if(isSpeciesGroupsWindow && action.get(Tags.OriginWidget).get(UIATags.UIAAutomationId).equalsIgnoreCase("btnCancelClose")){
					// filtering back button away
				}
				else{
					filteredActions.add(action);
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		return(super.selectAction(state, filteredActions));
	}

	/**
	 * Here you can put graceful shutdown sequence for your SUT
	 * @param system
	 */
	@Override
	protected void stopSystem(SUT system) {
		//super.stopSystem(system);
		// pushing back button until in logout screen:
		State state = getState(system);
		while(widgetWithAutomationIdFound("MessageDialog", state) && waitAndClickButtonByAutomationId("btnOk", state, system, 1)
				|| waitAndClickButtonByAutomationId("ExpanderButtonCancel", state, system, 1)
				|| waitAndClickButtonByAutomationId("ButtonBack", state, system, 1)
				|| waitAndClickButtonByAutomationId("btnCancelClose", state, system, 1)
				|| waitAndClickButtonByTitle("Cancel", state, system, 1)){
			System.out.println("Pushing back button until in logout screen");
			//update TESTAR state:
			state = getState(system);
		}
		while(waitAndClickButtonByAutomationId("ButtonLogOut", state, system, 1)){
			System.out.println("Pushing logout button");
			//update TESTAR state:
			state = getState(system);
		}
		if(widgetWithAutomationIdFound("btnShutDown", state, system, 1)){
			System.out.println("Shutdown button found - graceful logout successful!");
		}else{
			System.out.println("ERROR: graceful logout failed!");
		}
	}

}
