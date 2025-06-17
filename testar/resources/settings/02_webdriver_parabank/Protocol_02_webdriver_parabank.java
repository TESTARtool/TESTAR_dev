/**
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2019 - 2025 Universitat Politecnica de Valencia - www.upv.es
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

import static org.testar.monkey.alayer.Tags.Blocked;
import static org.testar.monkey.alayer.Tags.Enabled;
import static org.testar.monkey.alayer.webdriver.Constants.scrollArrowSize;
import static org.testar.monkey.alayer.webdriver.Constants.scrollThick;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.testar.SutVisualization;
import org.testar.managers.InputDataManager;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.actions.WdSelectListAction;
import org.testar.monkey.alayer.actions.WdSelectListAction.JsTargetMethod;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.plugin.NativeLinker;
import org.testar.protocols.WebdriverProtocol;

public class Protocol_02_webdriver_parabank extends WebdriverProtocol {
	
	/**
	 * This method is invoked each time the TESTAR starts the SUT to generate a new sequence.
	 * This can be used for example for bypassing a login screen by filling the username and password
	 * or bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
	 * the SUT's configuration files etc.)
	 */
	@Override
	protected void beginSequence(SUT system, State state) {
		super.beginSequence(system, state);
		
		waitLeftClickAndTypeIntoWidgetWithMatchingTag("name","username", "john", state, system, 5,1.0);
		
		waitLeftClickAndTypeIntoWidgetWithMatchingTag("name","password", "demo", state, system, 5,1.0);
		
		waitAndLeftClickWidgetWithMatchingTag("value", "Log In", state, system, 5, 1.0);
		
	}
	
	
	/**
	 * This is a helper method used by the default implementation of <code>buildState()</code>
	 * It examines the SUT's current state and returns an oracle verdict.
	 *
	 * @return oracle verdict, which determines whether the state is erroneous and why.
	 */
	@Override
	protected Verdict getVerdict(State state) {
		// System crashes, non-responsiveness and suspicious tags automatically detected!
		// For web applications, web browser errors and warnings can also be enabled via settings
		Verdict verdict = super.getVerdict(state);
		
		/**
		 ORACLE 'test'
		 {
		 FOR_ALL WIDGET "list"
		 PROP 'type' IN LIST('textarea','button','slider'),
		 HAS PROP 'position'
		 CHECK
		 HAS PROP 'active'
		 }
		 
		 ORACLE 'test'
		 {
		 CHECK
		 HAS PROP 'active'
		 }
		 **/
		
		java.util.function.Predicate<Boolean> predicateWidgetHasIdLeftPanel = b -> stateHasWidgetWithTagValue(state, "WebId", "leftPanel"); // Left Panel widget exists
		java.util.function.Predicate<Boolean> predicateWidgetHasHrefOpenAccount = b -> stateHasWidgetWithValue(state, "openaccount.htm"); // OpenAccount widget exists
		java.util.function.Predicate<Boolean> predicateWidgetIsChildOfParent = b -> childOfParent(getStateWidgetWithTagValue(state, "WebHref", "openaccount.htm"), getStateWidgetWithTagValue(state, "WebId", "leftPanel")); // Specific widgets have child-parent relation
		Set<GrammarPredicate> grammarPredicates = new HashSet<>(Arrays.asList(
				new GrammarPredicate(predicateWidgetHasIdLeftPanel, "HAS PROP WebId VALUE 'leftPanel'"),
				new GrammarPredicate(predicateWidgetHasHrefOpenAccount, "HAS VALUE 'openaccount.htm'"),
				new GrammarPredicate(predicateWidgetIsChildOfParent, "'openaccount.htm' CHILD OF 'leftPanel'")
																			 ));
		if (validateConditions(grammarPredicates)) {
			verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, "All GrammarPredicate conditions are met: " + String.join(", ",
																																grammarPredicates.stream().map(GrammarPredicate::toString).collect(Collectors.toList()))));
		}
		
		return verdict;
	}
	
	private Boolean widgetHasTag(Widget widget, String tagName) {
		for(Tag<?> tag : widget.tags()){
			if(tag.name().equals(tagName) && widget.get(tag, null) != null) {
				return true;
			}
		}
		return false;
	}
	
	private Boolean widgetHasTagWithValue(Widget widget, String tagName, Object value) {
		for(Tag<?> tag : widget.tags()){
			if(tag.name().equals(tagName)
			   && widget.get(tag, null) != null
			   && widget.get(tag).equals(value)) {
				return true;
			}
		}
		return false;
	}
	
	private Boolean stateHasWidgetWithTag(State state, String tagName) {
		for(Widget widget : state) {
			for(Tag<?> tag : widget.tags()){
				if(tag.name().equals(tagName) && widget.get(tag, null) != null) {
					return true;
				}
			}
		}
		return false;
	}
	
	private Widget getStateWidgetWithTag(State state, String tagName) {
		for(Widget widget : state) {
			for(Tag<?> tag : widget.tags()){
				if(tag.name().equals(tagName) && widget.get(tag, null) != null) {
					return widget;
				}
			}
		}
		return null;
	}
	
	private Boolean stateHasWidgetWithValue(State state, Object value) {
		for(Widget widget : state) {
			for(Tag<?> tag : widget.tags()){
				if(widget.get(tag, null) != null && widget.get(tag).equals(value)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private Widget getStateWidgetWithValue(State state, Object value) {
		for(Widget widget : state) {
			for(Tag<?> tag : widget.tags()){
				if(widget.get(tag, null) != null && widget.get(tag).equals(value)) {
					return widget;
				}
			}
		}
		return null;
	}
	
	private Boolean stateHasWidgetWithTagValue(State state, String tagName, Object value) {
		for(Widget widget : state) {
			for(Tag<?> tag : widget.tags()){
				if(tag.name().equals(tagName)
				   && widget.get(tag, null) != null
				   && widget.get(tag).equals(value)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private Widget getStateWidgetWithTagValue(State state, String tagName, Object value) {
		for(Widget widget : state) {
			for(Tag<?> tag : widget.tags()){
				if(tag.name().equals(tagName)
				   && widget.get(tag, null) != null
				   && widget.get(tag).equals(value)) {
					return widget;
				}
			}
		}
		return null;
	}
	
	private boolean childOfParent(Widget child, Widget parent) {
		if(child == null || parent == null) return false;
		if(child.parent() == null) return false;
		else if (child.parent().get(Tags.AbstractID, "childId").equals(parent.get(Tags.AbstractID, "parentId"))) return true;
		else return childOfParent(child.parent(), parent);
	}
	
	private boolean validateConditions(Set<GrammarPredicate> grammarPredicates) {
		System.out.println("**** validateConditions ****");
		try {
			for (GrammarPredicate grammarPredicate : grammarPredicates) {
				System.out.println("GrammarPredicate: " + grammarPredicate.toString());
				java.util.function.Predicate<Boolean> predicate = grammarPredicate.getGrammarPredicate();
				if (!predicate.test(true)) {
					System.out.println("Result: false");
					return false; // If any condition fails, return false immediately
				}
			}
		} catch(Exception e) {
			// If any condition throws and exception, return false
			e.printStackTrace();
			System.out.println("Result: false");
			return false;
		}
		System.out.println("Result: true");
		return true; // If all conditions pass, return true
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
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {
		// Kill unwanted processes, force SUT to foreground
		Set<Action> actions = super.deriveActions(system, state);
		Set<Action> filteredActions = new HashSet<>();
		
		// create an action compiler, which helps us create actions
		// such as clicks, drag&drop, typing ...
		StdActionCompiler ac = new AnnotatingActionCompiler();
		
		// Check if forced actions are needed to stay within allowed domains
		Set<Action> forcedActions = detectForcedActions(state, ac);
		
		// iterate through all widgets
		for (Widget widget : state) {
			
			// only consider enabled and non-tabu widgets
			if (!widget.get(Enabled, true)) {
				continue;
			}
			
			// The blackListed widgets are those that have been filtered during the SPY mode with the
			//CAPS_LOCK + SHIFT + Click clickfilter functionality.
			if(blackListed(widget)){
				if(isTypeable(widget)){
					filteredActions.add(ac.clickTypeInto(widget, InputDataManager.getRandomTextInputData(widget), true));
				} else {
					filteredActions.add(ac.leftClickAt(widget));
				}
				continue;
			}
			
			// slides can happen, even though the widget might be blocked
			addSlidingActions(actions, ac, scrollArrowSize, scrollThick, widget);
			
			// If the element is blocked, Testar can't click on or type in the widget
			if (widget.get(Blocked, false) && !widget.get(WdTags.WebIsShadow, false)) {
				continue;
			}
			
			// type into text boxes
			if (isAtBrowserCanvas(widget) && isTypeable(widget)) {
				if(whiteListed(widget) || isUnfiltered(widget)){
					// Type a random Number, Alphabetic, URL, Date or Email input
					actions.add(ac.clickTypeInto(widget, InputDataManager.getRandomTextInputData(widget), true));
					// Paste a random input from a customizable input data file
					// Check testar/bin/settings/custom_input_data.txt
					//actions.add(ac.pasteTextInto(widget, InputDataManager.getRandomTextFromCustomInputDataFile(System.getProperty("user.dir") + "/settings/custom_input_data.txt"), true));
				}else{
					// filtered and not white listed:
					filteredActions.add(ac.clickTypeInto(widget, InputDataManager.getRandomTextInputData(widget), true));
				}
			}
			
			// left clicks, but ignore links outside domain
			if (isAtBrowserCanvas(widget) && isClickable(widget)) {
				if(whiteListed(widget) || isUnfiltered(widget)){
					if (!isLinkDenied(widget)) {
						if(widget.get(Tags.Role, Roles.Widget).equals(WdRoles.WdSELECT)) {
							actions.add(randomFromSelectList(widget));
						} else {
							actions.add(ac.leftClickAt(widget));
						}
					}else{
						// link denied:
						filteredActions.add(ac.leftClickAt(widget));
					}
				}else{
					// filtered and not white listed:
					filteredActions.add(ac.leftClickAt(widget));
				}
			}
		}
		
		// If we have forced actions, prioritize and filter the other ones
		if (forcedActions != null && forcedActions.size() > 0) {
			filteredActions = actions;
			actions = forcedActions;
		}
		
		//Showing the grey dots for filtered actions if visualization is on:
		if(visualizationOn || mode() == Modes.Spy) SutVisualization.visualizeFilteredActions(cv, state, filteredActions);
		
		return actions;
	}
	
	@Override
	protected boolean isTypeable(Widget widget) {
		Role role = widget.get(Tags.Role, Roles.Widget);
		if (Role.isOneOf(role, NativeLinker.getNativeTypeableRoles())) {
			
			// Specific class="input" for parasoft SUT
			if(widget.get(WdTags.WebCssClasses, "").contains("input")) {
				return true;
			}
		}
		
		return super.isTypeable(widget);
	}
	
	private Action randomFromSelectList(Widget w) {
		String elementId = w.get(WdTags.WebId, "");
		String elementName = w.get(WdTags.WebName, "");

		if (!elementId.isEmpty()) {
			return selectRandomValue(elementId, JsTargetMethod.ID, w);
		} else if (!elementName.isEmpty()) {
			return selectRandomValue(elementName, JsTargetMethod.NAME, w);
		}

		return new AnnotatingActionCompiler().leftClickAt(w);
	}

	private Action selectRandomValue(String identifier, JsTargetMethod targetMethod, Widget w) {
		try {
			String lengthQuery;
			String valueQuery;

			switch (targetMethod) {
			case ID:
				lengthQuery = String.format("return document.getElementById('%s').options.length;", identifier);
				break;
			case NAME:
				lengthQuery = String.format("return document.getElementsByName('%s')[0].options.length;", identifier);
				break;
			default:
				return new AnnotatingActionCompiler().leftClickAt(w);
			}

			Object lengthResponse = WdDriver.executeScript(lengthQuery);
			int selectLength = (lengthResponse != null) ? Integer.parseInt(lengthResponse.toString()) : 1;

			int randomIndex = new Random().nextInt(selectLength);

			switch (targetMethod) {
			case ID:
				valueQuery = String.format("return document.getElementById('%s').options[%d].value;", identifier, randomIndex);
				break;
			case NAME:
				valueQuery = String.format("return document.getElementsByName('%s')[0].options[%d].value;", identifier, randomIndex);
				break;
			default:
				return new AnnotatingActionCompiler().leftClickAt(w);
			}

			Object valueResponse = WdDriver.executeScript(valueQuery);

			return (valueResponse != null)
					? new WdSelectListAction(identifier, valueResponse.toString(), w, targetMethod)
							: new AnnotatingActionCompiler().leftClickAt(w);

		} catch (Exception e) {
			return new AnnotatingActionCompiler().leftClickAt(w);
		}
	}
	
}

class GrammarPredicate {
	final java.util.function.Predicate<Boolean> grammarPredicate;
	final String description;
	public GrammarPredicate(java.util.function.Predicate<Boolean> grammarPredicate, String description) {
		this.grammarPredicate = grammarPredicate;
		this.description = description;
	}
	public java.util.function.Predicate<Boolean> getGrammarPredicate() { return grammarPredicate; }
	@Override
	public String toString() { return description; }
}