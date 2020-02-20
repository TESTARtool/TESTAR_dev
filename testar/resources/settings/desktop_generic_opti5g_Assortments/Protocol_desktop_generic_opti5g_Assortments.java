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
public class Protocol_desktop_generic_opti5g_Assortments extends PonsseDesktopProtocol {

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
			System.out.println("beginSequence: btnShutDown found - Ponsse UI ready!");
		}

		// if testar button is not found, creating a new user:
		if(!widgetWithAutomationIdFound("btntestar", state, system, 1)){
			// Ponsse GUI ready and testar button not found - creating testar user
			System.out.println("creating a new user 'testar'");
			executeSequenceToCreateTestarUser(state, system);

			//updating TESTAR state:
			state=getState(system);
		}

		 // Ponsse user has been created - login testar user:
		 executeSequenceLoginTestarUser(state,system);

		 //updating TESTAR state:
		 state=getState(system);

		 if(waitAndLeftClickWidgetWithMatchingTag(UIATags.UIAAutomationId,"btnPlanningView",state,system,20,1)){
			 System.out.println("beginSequence: btnPlanningView found and clicked");
		 }else{
			 System.out.println("ERROR: beginSequence: btnPlanningView not found!");
		 }

		 //updating TESTAR state:
		 state=getState(system);

		 if(waitAndLeftClickWidgetWithMatchingTag(UIATags.UIAAutomationId,"contextMenuHandle",state,system,20,1)){
			 System.out.println("beginSequence: contextMenuHandle found and clicked");
		 }else{
			 System.out.println("ERROR: beginSequence: contextMenuHandle not found!");
		 }

		 //updating TESTAR state:
		 state=getState(system);

		 if(waitAndLeftClickWidgetWithMatchingTag(UIATags.UIAAutomationId,"buttonProducts",state,system,20,1)){
			 System.out.println("beginSequence: buttonProducts found and clicked");
		 }else{
			 System.out.println("ERROR: beginSequence: buttonProducts not found!");
		 }

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
		boolean isAssortmentsWindow = false;
		if(widgetWithAutomationIdFound("productDlg",state)){
			System.out.println("GUI is in productDlg - filtering back button");
			isAssortmentsWindow=true;
		}

		Set<Action> filteredActions = new HashSet<Action>();
		for(Action action:actions){
			try {
				// filtering back button and help button away from available actions:
				if (action.get(Tags.OriginWidget).get(UIATags.UIAAutomationId).equalsIgnoreCase("btnHelp")) {
					// filtering help button away
				} else if (isAssortmentsWindow && action.get(Tags.OriginWidget).get(UIATags.UIAAutomationId).equalsIgnoreCase("btnCancelClose")) {
					// filtering back button away
				} else {
					filteredActions.add(action);
				}
			}catch(Exception e){
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
		// pushing back button until in logout screen:
		State state = getState(system);
		System.out.println("stopSystem: trying to gracefully close all the screens and logout");
		while(
				waitAndLeftClickWidgetWithMatchingTag(UIATags.UIAAutomationId,"btnTimeUsageQuery_UnutilizedTimeOtherunutilizedtime",state,system,1,0.5)
				|| widgetWithAutomationIdFound("MessageDialog", state) && waitAndLeftClickWidgetWithMatchingTag(UIATags.UIAAutomationId,"btnOk",state,system,1,0.5)
				|| waitAndLeftClickWidgetWithMatchingTag(UIATags.UIAAutomationId,"btnPopupCancel",state,system,1,0.5)
				|| waitAndLeftClickWidgetWithMatchingTag(UIATags.UIAAutomationId,"ExpanderButtonCancel",state,system,1,0.5)
				|| waitAndLeftClickWidgetWithMatchingTag(UIATags.UIAAutomationId,"ButtonBack",state,system,1,0.5)
				|| waitAndLeftClickWidgetWithMatchingTag(UIATags.UIAAutomationId,"btnCancelClose",state,system,1,0.5)
				|| waitAndLeftClickWidgetWithMatchingTag(Tags.Title,"Cancel",state,system,1,0.5)
		){
			System.out.println("Pushing back/close/etc buttons until in logout screen");
			//update TESTAR state:
			state = getState(system);
		}
		while(waitAndLeftClickWidgetWithMatchingTag(UIATags.UIAAutomationId,"ButtonLogOut",state,system,1,0.5)){
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
