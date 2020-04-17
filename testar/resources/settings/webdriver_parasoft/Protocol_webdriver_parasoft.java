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

import nl.ou.testar.SutVisualization;
import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.*;
import org.fruit.alayer.devices.AWTKeyboard;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.devices.Keyboard;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.webdriver.*;
import org.fruit.alayer.webdriver.enums.WdRoles;
import org.fruit.alayer.webdriver.enums.WdTags;
import org.fruit.monkey.Settings;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;
import org.testar.OutputStructure;
import org.testar.protocols.WebdriverProtocol;

import java.util.*;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;


public class Protocol_webdriver_parasoft extends WebdriverProtocol {

	String extensionId = "";
	String extensionVersion = "";
	
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
		//clickableClasses = Arrays.asList("v-menubar-menuitem", "v-menubar-menuitem-caption");

		// Disallow links and pages with these extensions
		// Set to null to ignore this feature
		deniedExtensions = Arrays.asList("pdf", "jpg", "png","pfx");

		// Define a whitelist of allowed domains for links and pages
		// An empty list will be filled with the domain from the sut connector
		// Set to null to ignore this feature
		domainsAllowed = Arrays.asList("parabank.parasoft.com");

		// If true, follow links opened in new tabs
		// If false, stay with the original (ignore links opened in new tabs)
		followLinks = false;

		// Propagate followLinks setting
		WdDriver.followLinks = followLinks;

		// List of atributes to identify and close policy popups
		// Set to null to disable this feature
		/*policyAttributes = new HashMap<String, String>() {{
			put("class", "iAgreeButton");
		}};*/

		WdDriver.fullScreen = true;
		
		/**
		 * Local Path to the desired Chrome Extension
		 * Example: "C:\\Users\\*username*\\AppData\\Local\\Google\\Chrome\\User Data\\Default\\Extensions\\*extension id*\\*extension version*";
		 */
		
		WdDriver.additionalExtension = "C:\\Users\\testar\\AppData\\Local\\Google\\Chrome\\User Data\\Default\\Extensions\\"
		+ extensionId + "\\" + extensionVersion;

		// Override ProtocolUtil to allow WebDriver screenshots
		protocolUtil = new WdProtocolUtil();
	}
	
	/**
	 * This method is invoked each time the TESTAR starts the SUT to generate a new sequence.
	 * This can be used for example for bypassing a login screen by filling the username and password
	 * or bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
	 * the SUT's configuration files etc.)
	 */
	@Override
	protected void beginSequence(SUT system, State state){

		// Starting Parasoft Chrome plugin
		WdDriver.loadingExtension = true;
		executeClickOnTextOrImagePath("settings/webdriver_parasoft/parasoft_recorder_chrome_icon.jpg");
		executeClickOnTextOrImagePath("settings/webdriver_parasoft/parasoft_start_recording_icon.jpg");
		WdDriver.loadingExtension = false;
		//parasoftStartRecording();

		Util.pause(5);

		// When a TESTAR sequence begins we will login in to the application
		for(Widget w : state) {
			
			// Find username Input widget to type the username
			if(w.get(WdTags.WebName,"").equals("username")) {
				StdActionCompiler ac = new AnnotatingActionCompiler();
				Action a = ac.clickTypeInto(w, "parasoft", true);
				executeAction(system, state, a);
			}
			
			// Find password Input widget to type the password
			if(w.get(WdTags.WebName,"").equals("password")) {
				StdActionCompiler ac = new AnnotatingActionCompiler();
				Action a = ac.clickTypeInto(w, "demo", true);
				executeAction(system, state, a);
			}
		}

		// Credentials are typed, now we need to find Log In button and click
		for(Widget w : state) {
			if(w.get(WdTags.WebValue,"").equals("Log In")) {
				StdActionCompiler ac = new AnnotatingActionCompiler();
				Action a = ac.leftClickAt(w);
				executeAction(system, state, a);
			}
		}
		
		// Pause a bit, SUT will refresh
		Util.pause(5);
		
		// Update the state to retrieve the new one after login
		state = super.getState(system);
		
		super.beginSequence(system, state);
	}
	
	/**
	 * The getVerdict methods implements the online state oracles that
	 * examine the SUT's current state and returns an oracle verdict.
	 * @return oracle verdict, which determines whether the state is erroneous and why.
	 */
	@Override
	protected Verdict getVerdict(State state){
		// The super methods implements the implicit online state oracles for:
		// system crashes
		// non-responsiveness
		// suspicious titles
		Verdict verdict = super.getVerdict(state);

		//--------------------------------------------------------
		// MORE SOPHISTICATED STATE ORACLES CAN BE PROGRAMMED HERE
		//--------------------------------------------------------

		for(Widget w : state) {
			if(w.get(WdTags.WebTextContext,"").contains("internal error")) {
				return new Verdict(Verdict.SEVERITY_SUSPICIOUS_TITLE, 
						"Discovered suspicious widget 'Web Text Content' : '" + w.get(WdTags.WebTextContext,"") + "'.");
			}
		}
		
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

		//If we are on the admin web page, go back to the previous page
		if(WdDriver.getCurrentUrl().contains("parabank.parasoft.com/parabank/admin.htm")) {
			return new HashSet<>(Collections.singletonList(new WdHistoryBackAction()));
		}
		
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
			
			// Skip Admin and logout page widget
			if(widget.get(WdTags.WebHref,"").contains("admin.htm")
					|| widget.get(WdTags.WebHref,"").contains("logout.htm")
					|| widget.get(WdTags.WebHref,"").contains("wadl")
					|| widget.get(WdTags.WebHref,"").contains("wsdl")) {
				filteredActions.add(ac.leftClickAt(widget));
				continue;
			}
			
			// If the State contains the login panel, create a login action
			if(widget.get(WdTags.WebId,"").contains("loginPanel")) {
				loginParasoft("username", "password", state, actions, ac);
			}
			
			// only consider enabled widgets
			if (!widget.get(Enabled, true)) {
				continue;
			}

			// filter tabu widgets
			if (blackListed(widget)) {
				filteredActions.add(ac.leftClickAt(widget));
				continue;
			}

			// slides can happen, even though the widget might be blocked
			addSlidingActions(actions, ac, SCROLL_ARROW_SIZE, SCROLL_THICK, widget, state);

			// If the element is blocked, Testar can't click on or type in the widget
			if (widget.get(Blocked, false)) {
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
				}else{
					filteredActions.add(ac.leftClickAt(widget));
				}
			}
		}
		//Showing the grey dots for filtered actions if visualization is on:
		if(visualizationOn || mode() == Modes.Spy) SutVisualization.visualizeFilteredActions(cv, state, filteredActions);

		if(actions.isEmpty()) {
			return new HashSet<>(Collections.singletonList(new WdHistoryBackAction()));
		}
		
		return actions;
	}
	
	@Override
	protected void stopSystem(SUT system) {
		// Stop recording with Parasoft Chrome plugin
		WdDriver.loadingExtension = true;
		executeClickOnTextOrImagePath("settings/webdriver_parasoft/parasoft_recorder_chrome_icon_stop.jpg");
		executeClickOnTextOrImagePath("settings/webdriver_parasoft/parasoft_stop_recording_icon.jpg");
		
		Util.pause(1);
		
		executeClickOnTextOrImagePath("settings/webdriver_parasoft/parasoft_test_name.jpg");
		
		Util.pause(1);
		
		Keyboard kb = AWTKeyboard.build();
		kb.press(KBKeys.VK_TAB);
		kb.release(KBKeys.VK_TAB);
		
		new CompoundAction.Builder()   
		.add(new Type(OutputStructure.startOuterLoopDateString +"_"+ OutputStructure.executedSUTname),1).build()
		.run(system, null, 1);
		
		Util.pause(1);
		
		executeClickOnTextOrImagePath("settings/webdriver_parasoft/parasoft_download_record.jpg");
		
		Util.pause(5);
		
		kb.press(KBKeys.VK_ENTER);
		kb.release(KBKeys.VK_ENTER);
		
		Util.pause(5);
		
		WdDriver.loadingExtension = false;
		
		super.stopSystem(system);
	}
	
	/**
	 * If all necessary widgets exists, create a compound action for login
	 */
	private void loginParasoft(String username, String password, State state, Set<Action> actions, StdActionCompiler ac) {
		Action typeUsername = new NOP();
		Action typePassword = new NOP();
		Action clickLogin = new NOP();
		String nop = "No Operation";

		for(Widget w : state) {
			if(w.get(WdTags.WebName,"").equals("username")) {
				typeUsername = ac.clickTypeInto(w, username, true);
			}
			if(w.get(WdTags.WebName,"").equals("password")) {
				typePassword = ac.clickTypeInto(w, password, true);
			}
			if(w.get(WdTags.WebValue,"").equals("Log In")) {
				clickLogin = ac.leftClickAt(w);
			}
		}
		
		if(!typeUsername.toString().contains(nop) && !typePassword.toString().contains(nop) && !clickLogin.toString().contains(nop)) {
			Action userLogin = new CompoundAction.Builder()
					.add(typeUsername, 1)
					.add(typePassword, 1)
					.add(clickLogin, 1).build();
			userLogin.set(Tags.OriginWidget, typeUsername.get(Tags.OriginWidget));
			actions.add(userLogin);
		}
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
			
			// Specific class="input" for parasoft SUT
			if(widget.get(WdTags.WebCssClasses, "").contains("input")) {
				return true;
			}
			
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
	 * Using SikuliX library to click on text on screen
	 * @param textToFindOrImagePath
	 */
	protected  static void executeClickOnTextOrImagePath(String textToFindOrImagePath){
		Screen sikuliScreen = new Screen();
		try {
			//System.out.println("DEBUG: sikuli clicking on text (or image path): "+textToFindOrImagePath);
			sikuliScreen.click(textToFindOrImagePath);
		} catch (FindFailed findFailed) {
			findFailed.printStackTrace();
		}
	}

	protected  static boolean textOrImageExists(String textOrImagePath){
		if(getRegionOfTextOrImage(textOrImagePath)==null){
			// text or image not found
			return false;
		}
		return true;
	}

	/**
	 *
	 * @param textOrImagePath
	 * @return null if not found
	 */
	protected  static Region getRegionOfTextOrImage(String textOrImagePath){
		Screen sikuliScreen = new Screen();
		Pattern pattern = new Pattern(textOrImagePath).similar(new Float(0.90));
		Region region = sikuliScreen.exists(pattern);
		return region;
	}
	
	protected void parasoftStartRecording() {
		WdDriver.followLinks = true;
		String extensionURL = "\"chrome-extension://" + extensionId + "/html/popup.html\"";
		WdDriver.executeScript("window.open(" + extensionURL + ");");
		Util.pause(1);
		WdDriver.executeScript("$(\"#startRecordingButton\").click()");
		WdDriver.followLinks = followLinks;
	}
}
