/**
 * Copyright (c) 2018, 2019, 2020 Open Universiteit - www.ou.nl
 * Copyright (c) 2019, 2020 Universitat Politecnica de Valencia - www.upv.es
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
import es.upv.staq.testar.protocols.ClickFilterLayerProtocol;
import nl.ou.testar.ActionSelectionUtils;

import org.apache.commons.lang.StringEscapeUtils;
import org.fruit.Pair;
import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.*;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.webdriver.*;
import org.fruit.alayer.webdriver.enums.WdRoles;
import org.fruit.alayer.webdriver.enums.WdTags;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.fruit.monkey.RuntimeControlsProtocol.Modes;
import org.testar.protocols.WebdriverProtocol;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;
import static org.fruit.alayer.webdriver.Constants.scrollArrowSize;
import static org.fruit.alayer.webdriver.Constants.scrollThick;

public class Protocol_webdriver_kuveyt_turk extends WebdriverProtocol {

	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 *
	 * @param settings the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings) {
		NativeLinker.addWdDriverOS();
		super.initialize(settings);
		ensureDomainsAllowed();

		// Classes that are deemed clickable by the web framework
		clickableClasses = Arrays.asList("owl-dot", "participation", "rdMaturity", "link");
		clickableLabels = Arrays.asList("rdParticipation", "rdInvestment", "rdMaturityDay", "rdMaturityMonth");

		// Disallow links and pages with these extensions
		// Set to null to ignore this feature
		deniedExtensions = Arrays.asList("pdf", "jpg", "png", "vsf");

		// Define a whitelist of allowed domains for links and pages
		// An empty list will be filled with the domain from the sut connector
		// Set to null to ignore this feature
		domainsAllowed = Arrays.asList("www.kuveytturk.com.tr", "isube.kuveytturk.com.tr", "internetbranchtest", "internetbranchprep");

		// If true, follow links opened in new tabs
		// If false, stay with the original (ignore links opened in new tabs)
		followLinks = false;
		// Propagate followLinks setting
		WdDriver.followLinks = followLinks;

		// List of atributes to identify and close policy popups
		// Set to null to disable this feature
		policyAttributes = new HashMap<String, String>() {{
			put("id", "_cookieDisplay_WAR_corpcookieportlet_okButton");
			//put("name","BtnOK");
		}};

		WdDriver.fullScreen = true;
		
		// List of strings which have a high probability of causing issues when used as user-input data
		// https://github.com/minimaxir/big-list-of-naughty-strings
		naughtyStrings = false;

		// Override ProtocolUtil to allow WebDriver screenshots
		protocolUtil = new WdProtocolUtil();
	}


	/**
	 * This method is called when TESTAR starts the System Under Test (SUT). The method should
	 * take care of
	 * 1) starting the SUT (you can use TESTAR's settings obtainable from <code>settings()</code> to find
	 * out what executable to run)
	 * 2) bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
	 * the SUT's configuration files etc.)
	 * 3) waiting until the system is fully loaded and ready to be tested (with large systems, you might have to wait several
	 * seconds until they have finished loading)
	 *
	 * @return a started SUT, ready to be tested.
	 */
	@Override
	protected SUT startSystem() throws SystemStartException {
		SUT sut = super.startSystem();

		/**
		 * If you are experiencing problems with the coordinates of the executed actions.
		 * Please activate the following functionalities, capture the mouse (if you have the option disabled),
		 * and if it continues, contact us https://testar.org/contact/
		 */
		//visualizationOn = true;
		//mouse.setCursorDisplayScale(1.0);

		return sut;
	}

	/**
	 * This method is invoked each time the TESTAR starts the SUT to generate a new sequence.
	 * This can be used for example for bypassing a login screen by filling the username and password
	 * or bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
	 * the SUT's configuration files etc.)
	 */
	@Override
	protected void beginSequence(SUT system, State state) {
		// this is an example how to use database connection to Microsoft SQL to get input:
		// Create a variable for the connection string.
		String connectionUrl = "jdbc:sqlserver://srvtest\\atlas;databaseName=BOA;user=BTTester;password=Test_2014";

		System.out.println("DEBUG: MS SQL: Trying to connect: " + connectionUrl);

		try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement();) {
			System.out.println("DEBUG: MS SQL: Connection successful!");
			String SQL = "SELECT TOP 10 * FROM Person.Contact";
			ResultSet rs = stmt.executeQuery(SQL);
			System.out.println("DEBUG: MS SQL: Executing SQL Query: " + SQL);

			// Iterate through the data in the result set and display it.
			while (rs.next()) {
				System.out.println(rs.getString("FirstName") + " " + rs.getString("LastName"));
			}
		}
		// Handle any errors that may have occurred.
		catch (SQLException e) {
			System.out.println("DEBUG: MS SQL: ERROR trying to connect: " + e.getMessage());
			e.printStackTrace();
		}

		/**
		 * TODO: Customize the credentials obtained from DB
		 */
		
		// This login sequence is based on the information on: https://isube.kuveytturk.com.tr/Login/InitialLogin
		// But the username and password works only in internal development environment of Kuveyt Turk
		System.out.println("DEBUG 1: looking for BtnOK (expecting a pop-up info screen)");

		waitAndLeftClickWidgetWithMatchingTag(Tags.Title,"BtnOK", state, system, 20, 0.5);

		System.out.println("DEBUG 2: changing language to English (expecting to be in Turkish login screen)");

		waitAndLeftClickWidgetWithMatchingTag(Tags.Title,"english", state, system, 20,0.5);

		// widget.get(Tags.ValuePattern, "not available").equalsIgnoreCase("/Login/InitialLoginEnglish"))

		System.out.println("DEBUG 3: looking for password field");

		waitLeftClickAndTypeIntoWidgetWithMatchingTag(Tags.Title,"Password", "12121212", state, system, 20,0.5);

		Util.pause(1);

		new CompoundAction.Builder().add(new KeyDown(KBKeys.VK_SHIFT), 0.2)
		.add(new KeyDown(KBKeys.VK_TAB), 0.2)
		.add(new KeyUp(KBKeys.VK_TAB), 0.2)
		.add(new KeyUp(KBKeys.VK_SHIFT), 0.2).build().run(system, state, 0.2);

		Util.pause(1);

		new CompoundAction.Builder().add(new Type("94444281"), 1)
		.build().run(system, state, 0.2);
		
		Util.pause(1);
		
		System.out.println("DEBUG 4: looking for login/next button");

		waitAndLeftClickWidgetWithMatchingTag(WdTags.WebName,"Login", state, system, 20,0.5);

		state = getState(system);

		System.out.println("DEBUG 5: clicking login button");

		waitAndLeftClickWidgetWithMatchingTag(WdTags.WebId,"LoginButton",state,system,20,0.5 );

		state = getState(system);

		System.out.println("DEBUG 6: entering smspassword field");

		waitLeftClickAndTypeIntoWidgetWithMatchingTag(WdTags.WebName,"SmsPassword","121212",state,system,20,0.5 );

		System.out.println("DEBUG 7: clicking login button");

		waitAndLeftClickWidgetWithMatchingTag(WdTags.WebId,"OtpLoginButton",state,system,20,0.5 );
		
		System.out.println("-- DEBUG beginSequence_webdriver_statemodel: Internal Web loading widgets...");
		
		// Wait Internal Web Page to load widgets
		Util.pause(10);

		super.beginSequence(system, state);

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

		// If we are in Generate mode check and force KuveytTurk desired actions
		if(mode().equals(Modes.Generate)) {
			for(Widget widget : state) {

				// If the state contains the web element with the id property "addressCode"
				// We are in a specific page to introduce desired Email and addressCode
				if(widget.get(WdTags.WebId,"").equals("addressCode")) {
					// Type desired values and update the State Model
					waitLeftClickAndTypeIntoWidgetWithMatchingTag(WdTags.WebId, "Email", "testar@pros.upv.es", state, system, 5, 1);
					waitLeftClickAndTypeIntoWidgetWithMatchingTag(WdTags.WebId, "addressCode", "0123456789", state, system, 5, 1);
					// Click Next button to continue
					waitAndLeftClickWidgetWithMatchingTag(WdTags.WebValue, "Next", state, system, 5, 1);
					// Update before finish
					state = super.getState(system);
				}

				// Check current state to find the web element with the id property "CategoryContract_Limit"
				if(widget.get(WdTags.WebId,"").equals("CategoryContract_Limit")) {
					// Type desired value and update the State Model
					waitLeftClickAndTypeIntoWidgetWithMatchingTag(WdTags.WebId, "CategoryContract_Limit", "0123", state, system, 5, 1);
					// Click Save button to continue
					waitAndLeftClickWidgetWithMatchingTag(WdTags.WebValue, "Save", state, system, 5, 1);
					// Update before finish
					state = super.getState(system);
				}
			}
		}

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
			if (widget.get(Blocked, false)) {
				continue;
			}

			// type into text boxes
			if (isAtBrowserCanvas(widget) && isTypeable(widget) && (whiteListed(widget) || isUnfiltered(widget))) {

				if(naughtyStrings) {
					//Create a type action with the Action Compiler, and add it to the set of derived actions
					final Optional<String[]> textList = Optional.ofNullable(getTextInputsFromFile(settings.get(ConfigTags.InputFileText)));
					final String textToInsert = textList.isPresent() ? textList.get()[new Random().nextInt(textList.get().length)] : this.getRandomText(widget);

					Action paste = ac.pasteTextInto(widget, textToInsert, true);
					paste.set(Tags.Desc, "Paste Text: " + StringEscapeUtils.escapeHtml(textToInsert));
					actions.add(paste);
				} else {
					actions.add(ac.clickTypeInto(widget, this.getRandomText(widget), true));
				}
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
	/**
	 * Select one of the available actions using an action selection algorithm (for example random action selection)
	 *
	 * super.selectAction(state, actions) updates information to the HTML sequence report
	 *
	 * @param state the SUT's current state
	 * @param actions the set of derived actions
	 * @return the selected action (non-null!)
	 */
	private Set<Action> executedActions = new HashSet<Action>();
	private Set<Action> previousActions;

	@Override
	protected Action selectAction(State state, Set<Action> actions) {
		System.out.println("DEBUG: *** Sequence " + sequenceCount + ", Action " + actionCount() + " ***");

		Set<Action> prioritizedActions = new HashSet<Action>();
		//checking if it is the first round of actions:
		if(previousActions == null) {
			//all actions are new actions:
			System.out.println("DEBUG: the first round of actions");
			prioritizedActions = actions;
		} else {
			//if not the first round, get the new actions compared to previous state:
			prioritizedActions = ActionSelectionUtils.getSetOfNewActions(actions, previousActions);
		}

		if(prioritizedActions.size() > 0) {
			//there are new actions to choose from, checking if they have been already executed:
			prioritizedActions = ActionSelectionUtils.getSetOfNewActions(prioritizedActions, executedActions);
		}

		if(prioritizedActions.size() > 0) {
			// found new actions that have not been executed before - choose randomly
			System.out.println("DEBUG: found NEW actions that have not been executed before");
		} else{
			// no new unexecuted actions, checking if any unexecuted actions:
			prioritizedActions = ActionSelectionUtils.getSetOfNewActions(actions, executedActions);
		}

		if(prioritizedActions.size() > 0) {
			// found actions that have not been executed before - choose randomly
			System.out.println("DEBUG: found actions that have not been executed before");
		} else{
			// no unexecuted actions, choose randomly on any of the available actions:
			System.out.println("DEBUG: NO actions that have not been executed before");
			prioritizedActions = actions;
		}
		//saving the current actions for the next round:
		previousActions = actions;

		return(super.selectAction(state, prioritizedActions));
	}

	/**
	 * Execute the selected action.
	 *
	 * super.executeAction(system, state, action) is updating the HTML sequence report with selected action
	 *
	 * @param system the SUT
	 * @param state the SUT's current state
	 * @param action the action to execute
	 * @return whether or not the execution succeeded
	 */
	@Override
	protected boolean executeAction(SUT system, State state, Action action) {
		executedActions.add(action);
		System.out.println("DEBUG: executed action: " + action.get(Tags.Desc, "NoCurrentDescAvailable"));
		return super.executeAction(system, state, action);
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

	/** KuveytTurk: This method clicks a button*/
	protected boolean waitAndClickButtonByTitle(String title, State state, SUT system, int maxNumberOfRetries) {
		boolean uiElementFound = false;
		int numberOfRetries = 0;
		while (!uiElementFound && numberOfRetries < maxNumberOfRetries) {
			for (Widget widget : state) {
				if (widget.get(Tags.Title, "NoTitleAvailable").equalsIgnoreCase(title)) {
					uiElementFound = true;
					StdActionCompiler ac = new AnnotatingActionCompiler();
					System.out.println("DEBUG: waitAndClickButtonByTitle: left mouse click on " + title);
					Action a = ac.leftClickAt(widget);
					nl.ou.testar.SutVisualization.visualizeSelectedAction(settings, cv, state, a);
					executeAction(system, state, a);
					Util.pause(1);
					Util.clear(cv);

					state = getState(system);

					return true;
				}
			}

			if(!uiElementFound) {
				Util.pause(1);
				state = getState(system);
				numberOfRetries++;
			}
		}

		state = getState(system);

		return false;
	}

	/** KuveytTurk: This method type a button*/
	protected boolean waitAndClickAtAndTypeByTitle(String title, String textInput, State state, SUT system, int maxNumberOfRetries) {
		boolean uiElementFound = false;
		int numberOfRetries = 0;
		while(!uiElementFound && numberOfRetries < maxNumberOfRetries) {
			for(Widget widget : state) {
				if(widget.get(Tags.Title, "NoTitleAvailable").equalsIgnoreCase(title)) {
					uiElementFound = true;
					StdActionCompiler ac = new AnnotatingActionCompiler();
					System.out.println("DEBUG: waitAndClickButtonByTitle: left mouse click on " + title);
					Action a = ac.clickTypeInto(widget, textInput, true);
					nl.ou.testar.SutVisualization.visualizeSelectedAction(settings, cv, state, a);
					executeAction(system, state, a);
					Util.pause(1);
					Util.clear(cv);

					state = getState(system);

					return true;
				}
			}

			if(!uiElementFound) {
				Util.pause(1);
				state = getState(system);
				numberOfRetries++;
			}
		}

		state = getState(system);

		return false;
	}
}