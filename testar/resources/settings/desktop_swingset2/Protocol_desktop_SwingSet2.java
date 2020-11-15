/***************************************************************************************************
 *
 * Copyright (c) 2017 - 2020 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2019 - 2020 Open Universiteit - www.ou.nl
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

import java.util.Objects;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.Rect;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.Tags;
import org.testar.protocols.JavaSwingProtocol;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;

/**
 * Protocol that provides TESTAR behavior to test Java Swing applications.
 * 
 * Generic and interesting Swing behavior:
 * - forceWidgetTreeClickAction
 * - forceListElemetsClickAction
 * - forceComboBoxClickAction
 * 
 * Specific SwingSet2 behavior:
 * - isSourceCodeEditWidget
 */
public class Protocol_desktop_SwingSet2 extends JavaSwingProtocol {

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

		//return the set of derived actions
		return actions;
	}

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

		// To find all possible actions that TESTAR can click on we should iterate through all widgets of the state.
		for(Widget w :state){

			if(w.get(Enabled, true) && !w.get(Blocked, false)){ // only consider enabled and non-blocked widgets

				if (!blackListed(w)){  // do not build actions for tabu widgets  

					// left clicks
					if(isClickable(w) && (isUnfiltered(w) || whiteListed(w))) {
						actions.add(ac.leftClickAt(w));
					}

					// type into text boxes
					if((isTypeable(w) && (isUnfiltered(w) || whiteListed(w))) && !isSourceCodeEditWidget(w)) {
						actions.add(ac.clickTypeInto(w, this.getRandomText(w), true));
					}

					/** Force actions on some widgets with a wrong accessibility **/
					// Optional feature, comment out this changes if your Swing applications doesn't need it

					// Tree List elements have plain "text" items have child nodes
					// We need to derive a click action on them
					if(w.get(Tags.Role).toString().contains("Tree")) {
						forceWidgetTreeClickAction(w, actions);
					}
					// Combo Box elements also have List Elements
					// Lists elements needs a special derivation to check widgets visibility
					if(w.get(Tags.Role).toString().contains("List")) {
						forceListElemetsClickAction(w, actions);
					}
					/** End of Force action **/
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

		// iterate through top level widgets based on Z-index
		for(Widget w : getTopWidgets(state)){

			if(w.get(Enabled, true) && !w.get(Blocked, false)){ // only consider enabled and non-blocked widgets

				if (!blackListed(w)){  // do not build actions for tabu widgets  

					// left clicks
					if(isClickable(w) && (isUnfiltered(w) || whiteListed(w))) {
						actions.add(ac.leftClickAt(w));
					}

					// type into text boxes
					if((isTypeable(w) && (isUnfiltered(w) || whiteListed(w))) && !isSourceCodeEditWidget(w)) {
						actions.add(ac.clickTypeInto(w, this.getRandomText(w), true));
					}

					/** Force actions on some widgets with a wrong accessibility **/
					// Optional feature, comment out this changes if your Swing applications doesn't need it

					// Tree List elements have plain "text" items have child nodes
					// We need to derive a click action on them
					if(w.get(Tags.Role).toString().contains("Tree")) {
						forceWidgetTreeClickAction(w, actions);
					}
					// Combo Box elements also have List Elements
					// Lists elements needs a special derivation to check widgets visibility
					if(w.get(Tags.Role).toString().contains("List")) {
						forceListElemetsClickAction(w, actions);
					}
					/** End of Force action **/
				}
			}
		}

		return actions;
	}

	/**
	 * SwingSet2 application contains a TabElement called "SourceCode"
	 * that internally contains UIAEdit widgets that are not modifiable.
	 * Because these widgets have the property ToolTipText with the value "text/html",
	 * use this Tag to recognize and ignore.
	 */
	private boolean isSourceCodeEditWidget(Widget w) {
		return w.get(Tags.ToolTipText, "").contains("text/html");
	}

	/**
	 * Iterate through the child of the specified widget to derive a click Action
	 */
	private void forceWidgetTreeClickAction(Widget w, Set<Action> actions) {
		StdActionCompiler ac = new AnnotatingActionCompiler();
		actions.add(ac.leftClickAt(w));
		w.set(Tags.ActionSet, actions);
		for(int i = 0; i<w.childCount(); i++) {
			forceWidgetTreeClickAction(w.child(i), actions);
		}
	}

	/**
	 * Derive a click Action on visible List dropdown elements
	 */
	public void forceListElemetsClickAction(Widget w, Set<Action> actions) {
		if(!Objects.isNull(w.parent())) {
			Widget parentContainer = w.parent();
			Rect visibleContainer = Rect.from(parentContainer.get(Tags.Shape).x(), parentContainer.get(Tags.Shape).y(),
					parentContainer.get(Tags.Shape).width(), parentContainer.get(Tags.Shape).height());

			forceComboBoxClickAction(w, visibleContainer, actions);
		}
	}

	/**
	 * Derive a click Action if widget rect bounds are inside the visible container
	 */
	public void forceComboBoxClickAction(Widget w, Rect visibleContainer, Set<Action> actions) {
		StdActionCompiler ac = new AnnotatingActionCompiler();
		try {
			Rect widgetContainer = Rect.from(w.get(Tags.Shape).x(), w.get(Tags.Shape).y(),
					w.get(Tags.Shape).width(), w.get(Tags.Shape).height());

			if(Rect.contains(visibleContainer, widgetContainer)) {
				actions.add(ac.leftClickAt(w));
				w.set(Tags.ActionSet, actions);
			}

			for(int i = 0; i<w.childCount(); i++) {
				forceComboBoxClickAction(w.child(i), visibleContainer, actions);
			}
		} catch(Exception e) {}
	}
}
