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

import es.upv.staq.testar.CodingManager;
import es.upv.staq.testar.NativeLinker;
import es.upv.staq.testar.serialisation.LogSerialiser;
import nl.ou.testar.SutVisualization;
import org.apache.commons.text.StringEscapeUtils;
import org.fruit.Pair;
import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.*;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.webdriver.WdDriver;
import org.fruit.alayer.webdriver.WdElement;
import org.fruit.alayer.webdriver.WdWidget;
import org.fruit.alayer.webdriver.enums.WdRoles;
import org.fruit.alayer.webdriver.enums.WdTags;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.testar.OutputStructure;
import org.testar.protocols.WebdriverProtocol;
import org.testar.protocols.experiments.WriterExperiments;
import org.testar.protocols.experiments.WriterExperimentsParams;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;
import static org.fruit.alayer.webdriver.Constants.scrollArrowSize;
import static org.fruit.alayer.webdriver.Constants.scrollThick;


public class Protocol_webdriver_etsy extends WebdriverProtocol {

	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 *
	 * @param settings the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings) {
		super.initialize(settings);

		/*
		These settings are initialized in WebdriverProtocol:

		// Classes that are deemed clickable by the web framework
		// getting from the settings file:
		clickableClasses = settings.get(ConfigTags.ClickableClasses);

		// Disallow links and pages with these extensions
		// Set to null to ignore this feature
		// getting from the settings file:
		deniedExtensions = settings.get(ConfigTags.DeniedExtensions).contains("null") ? null : settings.get(ConfigTags.DeniedExtensions);

		// Define a whitelist of allowed domains for links and pages
		// An empty list will be filled with the domain from the sut connector
		// Set to null to ignore this feature
		// getting from the settings file:
		domainsAllowed = settings.get(ConfigTags.DomainsAllowed).contains("null") ? null : settings.get(ConfigTags.DomainsAllowed);

		// If true, follow links opened in new tabs
		// If false, stay with the original (ignore links opened in new tabs)
		// getting from the settings file:
		WdDriver.followLinks = settings.get(ConfigTags.FollowLinks);

		//Force the browser to run in full screen mode
		WdDriver.fullScreen = true;

		//Force webdriver to switch to a new tab if opened
		//This feature can block the correct display of select dropdown elements 
		WdDriver.forceActivateTab = true;
		*/

		// URL + form name, username input id + value, password input id + value
		// Set login to null to disable this feature
		// TODO: getting from the settings file, not sure if this works:
//		login = Pair.from("https://login.awo.ou.nl/SSO/login", "OUinloggen");
//		username = Pair.from("username", "");
//		password = Pair.from("password", "");

		// List of attributes to identify and close policy popups
		// Set to null to disable this feature
		//TODO put into settings file
		policyAttributes = new HashMap<String, String>() {{
			put("class", "lfr-btn-label");
		}};
	}

	/**
	 * This method is called when TESTAR starts the System Under Test (SUT). The method should
	 * take care of
	 * 1) starting the SUT (you can use TESTAR's settings obtainable from <code>settings()</code> to find
	 * out what executable to run)
	 * 2) bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
	 * the SUT's configuratio files etc.)
	 * 3) waiting until the system is fully loaded and ready to be tested (with large systems, you might have to wait several
	 * seconds until they have finished loading)
	 *
	 * @return a started SUT, ready to be tested.
	 */

	protected void buildStateActionsIdentifiers(State state, Set<Action> actions) {
		CodingManager.buildIDs(state, actions);
		// Custom the State AbstractIDCustom identifier
		customActionBuildAbstractIDCustom(actions);
	}

	private synchronized void customActionBuildAbstractIDCustom(Set<Action> actions) {
		for (Action a : actions) {
			/* To create the AbstractIDCustom use:
			 * - AbstractIDCustom of the OriginWidget calculated with the selected abstract properties (core-StateManagementTags)
			 * - The ActionRole type of this action (LeftClick, DoubleClick, ClickTypeInto, Drag, etc)
			 */

			if(a instanceof WdHistoryBackAction || a instanceof WdCloseTabAction) continue;
			if(a.get(Tags.Role, ActionRoles.Action).equals(ActionRoles.CompoundAction) || a.get(Tags.Role, ActionRoles.Action).equals(ActionRoles.HitKey)) {
				a.set(Tags.AbstractIDCustom, CodingManager.ID_PREFIX_ACTION + CodingManager.ID_PREFIX_ABSTRACT_CUSTOM +
						CodingManager.lowCollisionID(a.get(Tags.OriginWidget).get(Tags.AbstractIDCustom) + StringEscapeUtils.escapeHtml4(a.get(Tags.Desc, ""))));
			} else {
				if (a instanceof WdNavigateTo){
					System.out.println("--------------------");
					System.out.println(a.get(Tags.OriginWidget).get(Tags.AbstractIDCustom));
				}
				a.set(Tags.AbstractIDCustom, CodingManager.ID_PREFIX_ACTION + CodingManager.ID_PREFIX_ABSTRACT_CUSTOM +
						CodingManager.lowCollisionID(a.get(Tags.OriginWidget).get(Tags.AbstractIDCustom) + a.get(Tags.Role, ActionRoles.Action)));
				if (a instanceof WdNavigateTo){
					System.out.println(a.get(Tags.AbstractIDCustom));
					System.out.println("--------------------");
				}
			}
		}
	}

	@Override
	protected SUT startSystem() throws SystemStartException {
		return super.startSystem();
	}

	/**
	 * This method is invoked each time the TESTAR starts the SUT to generate a new sequence.
	 * This can be used for example for bypassing a login screen by filling the username and password
	 * or bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
	 * the SUT's configuration files etc.)
	 */
	@Override
	protected void beginSequence(SUT system, State state) {

		super.beginSequence(system, state);

		waitAndLeftClickWidgetWithMatchingTag(WdTags.WebTextContent, "Accept", state, system, 5, 1);


	}

	/**
	 * This method is called when TESTAR requests the state of the SUT.
	 * Here you can add additional information to the SUT's state or write your
	 * own state fetching routine. The state should have attached an oracle
	 * (TagName: <code>Tags.OracleVerdict</code>) which describes whether the
	 * state is erroneous and if so why.
	 *
	 * @return the current state of the SUT with attached oracle.
	 */
	@Override
	protected State getState(SUT system) throws StateBuildException {
		State state = super.getState(system);

		return state;
	}

	/**
	 * This is a helper method used by the default implementation of <code>buildState()</code>
	 * It examines the SUT's current state and returns an oracle verdict.
	 *
	 * @return oracle verdict, which determines whether the state is erroneous and why.
	 */
	@Override
	protected Verdict getVerdict(State state) {

		Verdict verdict = super.getVerdict(state);
		// system crashes, non-responsiveness and suspicious titles automatically detected!

		//-----------------------------------------------------------------------------
		// MORE SOPHISTICATED ORACLES CAN BE PROGRAMMED HERE (the sky is the limit ;-)
		//-----------------------------------------------------------------------------

		// ... YOU MAY WANT TO CHECK YOUR CUSTOM ORACLES HERE ...

		return verdict;
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

			if(widget.get(Tags.Role, Roles.Widget).equals(WdRoles.WdSELECT) ||
					widget.get(WdTags.WebTextContent, "").contains("Sign in") ||
					widget.get(WdTags.Desc, "").contains("Continue with Google") ||
					widget.get(Tags.Desc, "").contains("Continue with Google") ||
					widget.get(WdTags.Desc, "").contains("Continue with Facebook") ||
					widget.get(Tags.Desc, "").contains("Continue with Facebook") ||
					widget.get(WdTags.Desc, "").contains("Continue with Apple") ||
					widget.get(Tags.Desc, "").contains("Continue with Apple") ||
					widget.get(WdTags.Desc, "").contains("email") ||
					widget.get(Tags.Desc, "").contains("email") ||
					widget.get(WdTags.WebTextContent, "").contains("signing in") ||
					widget.get(WdTags.WebName, "").contains("submit_attempt") ||
					widget.get(WdTags.Desc, "").contains("checkout") ||
					widget.get(Tags.Desc, "").contains("checkout") ||
					widget.get(WdTags.WebHref, "").contains("guest/favorites")
			){
				continue;
			}

		    // fill forms actions
		    if (isAtBrowserCanvas(widget) && isForm(widget)) {
				String protocol = settings.get(ConfigTags.ProtocolClass, "");
		        Action formFillingAction = new WdFillFormAction(ac, widget, protocol.substring(0, protocol.lastIndexOf('/')));
		        if(formFillingAction instanceof NOP){
		        	// do nothing with NOP actions - the form was not actionable
				}else{
		        	actions.add(formFillingAction);
				}
		    }

			// only consider enabled and non-tabu widgets
			if (!widget.get(Enabled, true)) {
				continue;
			}
			// The blackListed widgets are those that have been filtered during the SPY mode with the
			//CAPS_LOCK + SHIFT + Click clickfilter functionality.
			if(blackListed(widget)){
				if(isTypeable(widget)){
					filteredActions.add(ac.clickTypeInto(widget, this.getRandomText(widget), true));
				} else {
					filteredActions.add(ac.leftClickAt(widget));
				}
				continue;
			}

			// slides can happen, even though the widget might be blocked
//			addSlidingActions(actions, ac, scrollArrowSize, scrollThick, widget);

			// If the element is blocked, Testar can't click on or type in the widget
			if (widget.get(Blocked, false)) {
				continue;
			}

			// type into text boxes
			if (isAtBrowserCanvas(widget) && isTypeable(widget)) {
				if(whiteListed(widget) || isUnfiltered(widget)){
					actions.add(ac.clickTypeInto(widget, this.getRandomText(widget), true));
				}else{
					// filtered and not white listed:
					filteredActions.add(ac.clickTypeInto(widget, this.getRandomText(widget), true));
				}
			}

			// left clicks, but ignore links outside domain
			if (isAtBrowserCanvas(widget) && isClickable(widget)) {
				if(whiteListed(widget) || isUnfiltered(widget)){
					if (!isLinkDenied(widget)) {
						if (widget.get(WdTags.WebCssClasses, "").contains("listing-link") ||
								widget.get(WdTags.WebCssClasses, "").contains("wt-arrow-link--forward"))
						{
//							filteredActions.add(ac.leftClickAt(widget));
							String URL = widget.get(WdTags.WebHref, null);
							if (URL != null) {
								Action goToLink = new WdNavigateTo(URL);
								goToLink.set(Tags.OriginWidget, widget);
								actions.add(goToLink);
							}
						}
						else {

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

//		if(actions.isEmpty()) {
//			System.out.println("Derive Actions Empty! Wait 2 second because maybe a menu is loading");
//			Util.pause(2);
//			Action nop = new NOP();
//			nop.set(Tags.OriginWidget, state);
//			nop.set(Tags.Desc, "NOP action to wait");
//			return new HashSet<>(Collections.singletonList(nop));
//		}
		
		// If we have forced actions, prioritize and filter the other ones
		if (forcedActions != null && forcedActions.size() > 0) {
			filteredActions = actions;
			actions = forcedActions;
		}

		//Showing the grey dots for filtered actions if visualization is on:
		if(visualizationOn || mode() == Modes.Spy) SutVisualization.visualizeFilteredActions(cv, state, filteredActions);

		Action key_down = ac.hitKey(KBKeys.VK_PAGE_DOWN);
		key_down.set(Tags.OriginWidget, state);
		actions.add(key_down);

		Action key_up = ac.hitKey(KBKeys.VK_PAGE_UP);
		key_up.set(Tags.OriginWidget, state);
		actions.add(key_up);

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


	/**
	 * Select one of the possible actions (e.g. at random)
	 *
	 * @param state   the SUT's current state
	 * @param actions the set of available actions as computed by <code>buildActionsSet()</code>
	 * @return the selected action (non-null!)
	 */
	@Override
	protected Action selectAction(State state, Set<Action> actions) {
		return super.selectAction(state, actions);
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
		boolean actionExecuted = super.executeAction(system, state, action);

		String information = "Sequence | " + OutputStructure.sequenceInnerLoopCount +
				" | actionnr | " + actionCount +
				" | url | " + WdDriver.getCurrentUrl();
		WriterExperiments.writeMetrics(new WriterExperimentsParams.WriterExperimentsParamsBuilder()
				.setFilename("urlData")
				.setInformation(information)
				.build());
		try {
			extractStateModelMetrics();
		} catch(Exception e) {
			LogSerialiser.log("ERROR Extracting state model metrics: " + actionCount, LogSerialiser.LogLevel.Info);
			System.err.println("ERROR Extracting state model metrics: " + actionCount);
		}

		return actionExecuted;
	}

	/**
	 * TESTAR uses this method to determine when to stop the generation of actions for the
	 * current sequence. You could stop the sequence's generation after a given amount of executed
	 * actions or after a specific time etc.
	 *
	 * @return if <code>true</code> continue generation, else stop
	 */
	@Override
	protected boolean moreActions(State state) {
		return super.moreActions(state);
	}

	/**
	 * This method is invoked each time after TESTAR finished the generation of a sequence.
	 */
	@Override
	protected void finishSequence() {
		super.finishSequence();
	}

	/**
	 * TESTAR uses this method to determine when to stop the entire test.
	 * You could stop the test after a given amount of generated sequences or
	 * after a specific time etc.
	 *
	 * @return if <code>true</code> continue test, else stop
	 */
	@Override
	protected boolean moreSequences() {
		return super.moreSequences();
	}

	/**
	 * This method is called after the last sequence, to allow for example handling the reporting of the session
	 */
	@Override
	protected void closeTestSession() {
		super.closeTestSession();
		// Extract and create JaCoCo run coverage report for Generate Mode
		if(settings.get(ConfigTags.Mode).equals(Modes.Generate)) {
			try {
				extractStateModelMetrics();
			} catch(Exception e) {
				LogSerialiser.log("ERROR Extracting state model metrics: " + actionCount, LogSerialiser.LogLevel.Info);
				System.err.println("ERROR Extracting state model metrics: " + actionCount);
			}
			compressOutputRunFolder();
			copyOutputToNewFolderUsingIpAddress("N:");
		}
	}
}