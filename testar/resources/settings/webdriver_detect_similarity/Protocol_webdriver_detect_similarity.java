/**
 * Copyright (c) 2018 - 2021 Open Universiteit - www.ou.nl
 * Copyright (c) 2019 - 2021 Universitat Politecnica de Valencia - www.upv.es
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
 *
 */

import es.upv.staq.testar.NativeLinker;
import org.fruit.Pair;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.*;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.webdriver.*;
import org.fruit.alayer.webdriver.enums.WdRoles;
import org.fruit.alayer.webdriver.enums.WdTags;
import org.fruit.monkey.Settings;
import org.testar.action.priorization.ActionTags;
import org.testar.action.priorization.SimilarityDetection;
import org.testar.action.priorization.WeightedAction;
import org.testar.protocols.WebdriverProtocol;

import java.util.*;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;
import static org.fruit.alayer.webdriver.Constants.scrollArrowSize;
import static org.fruit.alayer.webdriver.Constants.scrollThick;

/**
 * Sample protocol that tries to detect similar Actions between previous and current State,
 * to decrease the possibilities to select a static widget and previous executed Action.
 * 
 * Actions have an OriginWidget associated, and Widgets have an AbtractIDCustom property
 * that allows TESTAR to identify web elements based on Abstract Properties.
 * Example: WebWidgetId (test.settings -> AbstractStateAttributes)
 * 
 * If some Action.OriginWidget still existing between previous and current State
 * or if some Action.OriginWidget was executed previously,
 * increase a numeric similarity weight that will reduce the % to be selected
 */
public class Protocol_webdriver_detect_similarity extends WebdriverProtocol {
	
	private SimilarityDetection similarActions;

	/**
	 * This method is invoked each time the TESTAR starts the SUT to generate a new sequence.
	 * This can be used for example for bypassing a login screen by filling the username and password
	 * or bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
	 * the SUT's configuration files etc.)
	 */
	@Override
	protected void beginSequence(SUT system, State state) {
		super.beginSequence(system, state);
		// 5 is the default maximum numeric weight
		// the more it is increased, the more the probability % of selecting "similar" actions is reduced
		similarActions = new SimilarityDetection(deriveActions(system, state), 5);
	}

	/**
	 * This method is used by TESTAR to determine the set of currently available actions.
	 * You can use the SUT's current state, analyze the widgets and their properties to create
	 * a set of sensible actions, such as: "Click every Button which is enabled" etc.
	 * The return value is supposed to be non-null. If the returned set is empty, TESTAR
	 * will stop generation of the current action and continue with the next one.
	 *
	 * @param system the SUT
	 * @param state  the SUT's current state
	 * @return a set of actions
	 */
	@Override
	protected Set<Action> deriveActions(SUT system, State state)
			throws ActionBuildException {
		// Kill unwanted processes, force SUT to foreground
		Set<Action> actions = super.deriveActions(system, state);

		// create an action compiler, which helps us create actions
		// such as clicks, drag&drop, typing ...
		StdActionCompiler ac = new AnnotatingActionCompiler();

		// Check if forced actions are needed to stay within allowed domains
		Set<Action> forcedActions = detectForcedActions(state, ac);
		if (forcedActions != null && forcedActions.size() > 0) {
			return forcedActions;
		}

		// iterate through all widgets
		for (Widget widget : state) {
			// only consider enabled and non-tabu widgets
			if (!widget.get(Enabled, true) || blackListed(widget)) {
				continue;
			}

			// slides can happen, even though the widget might be blocked
			addSlidingActions(actions, ac, scrollArrowSize, scrollThick, widget, state);

			// If the element is blocked, Testar can't click on or type in the widget
			if (widget.get(Blocked, false) && !widget.get(WdTags.WebIsShadow, false)) {
				continue;
			}

			// type into text boxes
			if (isAtBrowserCanvas(widget) && isTypeable(widget) && (whiteListed(widget) || isUnfiltered(widget))) {
				actions.add(ac.clickTypeInto(widget, this.getRandomText(widget), true));
			}

			// left clicks, but ignore links outside domain
			if (isAtBrowserCanvas(widget) && isClickable(widget) && (whiteListed(widget) || isUnfiltered(widget))) {
				if (!isLinkDenied(widget)) {
					actions.add(ac.leftClickAt(widget));
				}
			}
		}

		return actions;
	}

	@Override
	protected boolean isClickable(Widget widget) {
		Role role = widget.get(Tags.Role, Roles.Widget);
		if (Role.isOneOf(role, NativeLinker.getNativeClickableRoles())) {
			// Input type are special...
			if (role.equals(WdRoles.WdINPUT)) {
				String type = ((WdWidget) widget).element.type;
				return WdRoles.clickableInputTypes().contains(type);
			}
			return true;
		}

		WdElement element = ((WdWidget) widget).element;
		if (element.isClickable) {
			return true;
		}

		Set<String> clickSet = new HashSet<>(clickableClasses);
		clickSet.retainAll(element.cssClasses);
		return clickSet.size() > 0;
	}

	@Override
	protected boolean isTypeable(Widget widget) {
		Role role = widget.get(Tags.Role, Roles.Widget);
		if (Role.isOneOf(role, NativeLinker.getNativeTypeableRoles())) {
			// Input type are special...
			if (role.equals(WdRoles.WdINPUT)) {
				String type = ((WdWidget) widget).element.type;
				return WdRoles.typeableInputTypes().contains(type);
			}
			return true;
		}

		return false;
	}

	/**
	 * Select one of the possible actions (e.g. at random)
	 *
	 * @param state   the SUT's current state
	 * @param actions the set of available actions as computed by <code>buildActionsSet()</code>
	 * @return the selected action (non-null!)
	 */
	@Override
	protected Action selectAction(State state, Set<Action> actions) {
		
		// Given the current set of Actions of the State take the OriginWidget AbstractCustomID,
		// and compare with the previous existing Actions/OriginWidget to increase the similarity value.
		// Minimal similarity value 1, Maximal similarity is given in the constructor. 
		// Higher similarity value means that Action/OriginWidget remains more time static in the State.
		
		actions = similarActions.modifySimilarActions(actions);

		System.out.println("---------------------- DEBUG SIMILARITY VALUES ----------------------------------------");
		for(Action a : actions) {
			System.out.println("Widget : " + a.get(Tags.OriginWidget).get(WdTags.WebId) +
					" with similarity : " + a.get(ActionTags.SimilarityValue, 0));
		}
		
		return reduceProbabilityBySimilarity(state, actions);
	}
	
	private Action reduceProbabilityBySimilarity(State state, Set<Action> actions) {
		// Check the totalWeight
		double totalWeight = 0;
		for(Action a : actions) {
			totalWeight = totalWeight + (1.0 / a.get(ActionTags.SimilarityValue));
		}
		
		// Just in case, if something wrong return purely random
		if(totalWeight == 0) {
			return super.selectAction(state, actions);
		}
		
		System.out.println("*************************** DEBUG SIMILARITY PROBABILITY ***************************");
		
		// Create a percentage weight of each action based on the totalWeight
		WeightedAction actionProbability = new WeightedAction();
		for(Action a : actions) {
			double actionWeight = (1.0 / a.get(ActionTags.SimilarityValue)) / totalWeight * 100;
			actionProbability.addEntry(a, actionWeight);
			
			System.out.println("Widget : " + a.get(Tags.OriginWidget).get(WdTags.WebId) +
					" with similarity : " + actionWeight);
		}
		
		return actionProbability.getRandom();
	}

	/**
	 * Execute the selected action.
	 *
	 * @param system the SUT
	 * @param state  the SUT's current state
	 * @param action the action to execute
	 * @return whether or not the execution succeeded
	 */
	@Override
	protected boolean executeAction(SUT system, State state, Action action) {
		// Increase the similarity (weight value) of an executed action
		// to reduce the % to be selected next iteration
		similarActions.increaseSpecificExecutedAction(action);
		return super.executeAction(system, state, action);
	}

}
