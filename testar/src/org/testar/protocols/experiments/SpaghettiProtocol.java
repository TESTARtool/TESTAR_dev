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

package org.testar.protocols.experiments;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;

import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;
import org.testar.protocols.JavaSwingProtocol;

public class SpaghettiProtocol extends JavaSwingProtocol {

	/**
	 * Iterating through all widgets of the given state
	 *
	 * Adding derived actions into the given set of actions and returning the modified set of actions.
	 *
	 * @param actions
	 * @param system
	 * @param state
	 * @return
	 */
	protected Set<Action> deriveClickTypeScrollActionsFromAllWidgetsOfState(Set<Action> actions, State state){
		// create an action compiler, which helps us create actions, such as clicks, drag&drop, typing ...
		StdActionCompiler ac = new AnnotatingActionCompiler();

		// iterate through all widgets
		for(Widget w : state){

			if(w.get(Enabled, true) && !w.get(Blocked, false)){ // only consider enabled and non-blocked widgets

				if (!blackListed(w)){  // do not build actions for tabu widgets  

					// left clicks
					if(isClickable(w) && (isUnfiltered(w) || whiteListed(w))) {
						actions.add(ac.leftClickAt(w));
					}

					// type into text boxes
					if(isTypeable(w) && (isUnfiltered(w) || whiteListed(w))) {
						actions.add(ac.clickTypeInto(w, this.getRandomText(w), true));
					}

					//Force actions on some widgets with a wrong accessibility
					//Optional, comment this changes if your Swing applications doesn't need it
					if(w.get(Tags.Role).toString().contains("Tree") ||
							w.get(Tags.Role).toString().contains("ComboBox") ||
							w.get(Tags.Role).toString().contains("List")) {
						widgetTree(w, actions);
					}
					//End of Force action
				}
			}
		}

		return actions;
	}

	/**
	 * Adding derived actions into the given set of actions and returning the modified set of actions.
	 *
	 * @param actions
	 * @param system
	 * @param state
	 * @return
	 */
	protected Set<Action> deriveClickTypeScrollActionsFromTopLevelWidgets(Set<Action> actions, State state){
		// create an action compiler, which helps us create actions, such as clicks, drag&drop, typing ...
		StdActionCompiler ac = new AnnotatingActionCompiler();

		// iterate through all widgets
		for(Widget w : getTopWidgets(state)){

			if(w.get(Enabled, true) && !w.get(Blocked, false)){ // only consider enabled and non-blocked widgets

				if (!blackListed(w)){  // do not build actions for tabu widgets  

					// left clicks
					if(isClickable(w) && (isUnfiltered(w) || whiteListed(w))) {
						actions.add(ac.leftClickAt(w));
					}

					// type into text boxes
					if(isTypeable(w) && (isUnfiltered(w) || whiteListed(w))) {
						actions.add(ac.clickTypeInto(w, this.getRandomText(w), true));
					}

					//Force actions on some widgets with a wrong accessibility
					//Optional, comment this changes if your Swing applications doesn't need it
					if(w.get(Tags.Role).toString().contains("Tree") ||
							w.get(Tags.Role).toString().contains("ComboBox") ||
							w.get(Tags.Role).toString().contains("List")) {
						widgetTree(w, actions);
					}
					//End of Force action
				}
			}
		}

		return actions;
	}

	//Force actions on Tree widgets with a wrong accessibility
	private void widgetTree(Widget w, Set<Action> actions) {
		StdActionCompiler ac = new AnnotatingActionCompiler();
		actions.add(ac.leftClickAt(w));
		w.set(Tags.ActionSet, actions);
		for(int i = 0; i<w.childCount(); i++) {
			widgetTree(w.child(i), actions);
		}
	}
}