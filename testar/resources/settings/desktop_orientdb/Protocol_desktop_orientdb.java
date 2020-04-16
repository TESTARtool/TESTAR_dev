/***************************************************************************************************
 *
 * Copyright (c) 2013, 2014, 2015, 2016, 2017, 2018 Universitat Politecnica de Valencia - www.upv.es
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


import java.util.Set;
import nl.ou.testar.RandomActionSelector;
import org.fruit.Drag;
import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.*;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;
import es.upv.staq.testar.protocols.ClickFilterLayerProtocol;
import org.fruit.monkey.Settings;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;
import org.testar.protocols.DesktopProtocol;

public class Protocol_desktop_orientdb extends DesktopProtocol {

	/**
	 * Select one of the available actions (e.g. at random)
	 * @param state the SUT's current state
	 * @param actions the set of derived actions
	 * @return  the selected action (non-null!)
	 */
	@Override
	protected Action selectAction(State state, Set<Action> actions){
		//Call the preSelectAction method from the AbstractProtocol so that, if necessary,
		//unwanted processes are killed and SUT is put into foreground.
		Action a = preSelectAction(state, actions);
		if (a!= null) {
			return a;
		} else
			//if no preSelected actions are needed, then implement your own strategy
			System.out.println("Asking state model manager for action");
			Action modelAction = stateModelManager.getAbstractActionToExecute(actions);
			if (modelAction != null) return modelAction;
			System.out.println("StateModelManager did not return an action. Returning random");
			return RandomActionSelector.selectAction(actions);
	}

	/*@Override
	protected Set<Action> deriveClickTypeScrollActionsFromAllWidgetsOfState(Set<Action> actions, SUT system, State state){
		// To derive actions (such as clicks, drag&drop, typing ...) we should first create an action compiler.
		StdActionCompiler ac = new AnnotatingActionCompiler();

		// To find all possible actions that TESTAR can click on we should iterate through all widgets of the state.
		for(Widget w : state){
			//optional: iterate through top level widgets based on Z-index:
			//for(Widget w : getTopWidgets(state)){

			if(w.get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIAMenu")){
				// filtering out actions on menu-containers (that would add an action in the middle of the menu)
				continue; // skip this iteration of the for-loop
			}

			// Only consider enabled and non-blocked widgets
			if(w.get(Enabled, true) && !w.get(Blocked, false)){

				// Do not build actions for widgets on the blacklist
				// The blackListed widgets are those that have been filtered during the SPY mode with the
				//CAPS_LOCK + SHIFT + Click clickfilter functionality.
				if (!blackListed(w)){

					//For widgets that are:
					// - clickable
					// and
					// - unFiltered by any of the regular expressions in the Filter-tab, or
					// - whitelisted using the clickfilter functionality in SPY mode (CAPS_LOCK + SHIFT + CNTR + Click)
					// We want to create actions that consist of left clicking on them
					if(isClickable(w) && (isUnfiltered(w) || whiteListed(w))) {
						//Create a left click action with the Action Compiler, and add it to the set of derived actions
						actions.add(ac.leftClickAt(w));
					}

//					//For widgets that are:
//					// - typeable
//					// and
//					// - unFiltered by any of the regular expressions in the Filter-tab, or
//					// - whitelisted using the clickfilter functionality in SPY mode (CAPS_LOCK + SHIFT + CNTR + Click)
//					// We want to create actions that consist of typing into them
//					if(isTypeable(w) && (isUnfiltered(w) || whiteListed(w))) {
//						//Create a type action with the Action Compiler, and add it to the set of derived actions
//						actions.add(ac.clickTypeInto(w, this.getRandomText(w), true));
//					}
//					//Add sliding actions (like scroll, drag and drop) to the derived actions
//					//method defined below.
//					addSlidingActions(actions,ac,SCROLL_ARROW_SIZE,SCROLL_THICK,w, state);
				}
			}
		}
		return actions;
	}

	@Override
	protected Set<Action> deriveClickTypeScrollActionsFromTopLevelWidgets(Set<Action> actions, SUT system, State state){
		// To derive actions (such as clicks, drag&drop, typing ...) we should first create an action compiler.
		StdActionCompiler ac = new AnnotatingActionCompiler();

		// To find all possible actions that TESTAR can click on we should iterate through all widgets of the state.
		for(Widget w : getTopWidgets(state)){
			//optional: iterate through top level widgets based on Z-index:
			//for(Widget w : getTopWidgets(state)){

			if(w.get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIAMenu")){
				// filtering out actions on menu-containers (that would add an action in the middle of the menu)
				continue; // skip this iteration of the for-loop
			}

			// Only consider enabled and non-blocked widgets
			if(w.get(Enabled, true) && !w.get(Blocked, false)){

				// Do not build actions for widgets on the blacklist
				// The blackListed widgets are those that have been filtered during the SPY mode with the
				//CAPS_LOCK + SHIFT + Click clickfilter functionality.
				if (!blackListed(w)){

					//For widgets that are:
					// - clickable
					// and
					// - unFiltered by any of the regular expressions in the Filter-tab, or
					// - whitelisted using the clickfilter functionality in SPY mode (CAPS_LOCK + SHIFT + CNTR + Click)
					// We want to create actions that consist of left clicking on them
					if(isClickable(w) && (isUnfiltered(w) || whiteListed(w))) {
						//Create a left click action with the Action Compiler, and add it to the set of derived actions
						actions.add(ac.leftClickAt(w));
					}

//					//For widgets that are:
//					// - typeable
//					// and
//					// - unFiltered by any of the regular expressions in the Filter-tab, or
//					// - whitelisted using the clickfilter functionality in SPY mode (CAPS_LOCK + SHIFT + CNTR + Click)
//					// We want to create actions that consist of typing into them
//					if(isTypeable(w) && (isUnfiltered(w) || whiteListed(w))) {
//						//Create a type action with the Action Compiler, and add it to the set of derived actions
//						actions.add(ac.clickTypeInto(w, this.getRandomText(w), true));
//					}
//					//Add sliding actions (like scroll, drag and drop) to the derived actions
//					//method defined below.
//					addSlidingActions(actions,ac,SCROLL_ARROW_SIZE,SCROLL_THICK,w, state);
				}
			}
		}
		return actions;
	}*/

}
