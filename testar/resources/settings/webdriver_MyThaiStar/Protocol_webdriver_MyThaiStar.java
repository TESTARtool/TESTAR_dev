/**
 * 
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
import org.fruit.alayer.*;
import org.fruit.alayer.actions.*;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.webdriver.*;
import org.fruit.alayer.webdriver.enums.WdRoles;
import org.fruit.alayer.webdriver.enums.WdTags;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Main;
import org.fruit.monkey.Settings;
import org.testar.protocols.WebdriverProtocol;

import java.io.File;
import java.io.IOException;
import java.util.*;
import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;
import static org.fruit.alayer.webdriver.Constants.scrollArrowSize;
import static org.fruit.alayer.webdriver.Constants.scrollThick;


public class Protocol_webdriver_MyThaiStar extends WebdriverProtocol {

	private static List<String> alwaysClickableClasses = Arrays.asList("owl-dt-control-button-content");

	private static List<String> typeableClasses = Arrays.asList(
			//Text input of Menu page
			"mat-form-field-label-wrapper", //bookTable Page, Sign UP and Email
			"owl-dt-timer-input" //Calendar dates
			);

	private static List<String> slidesClasses = Arrays.asList(
			"mat-slider" //Not working yet, customize these kind of actions
			);

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
		clickableClasses = Arrays.asList(
				"v-menubar-menuitem", "v-menubar-menuitem-caption",
				//Main page
				"mat-button-ripple", "flag-icon", "mat-menu-ripple", "mat-icon", "mat-tab-label-content", //OK
				//Menu page
				"mat-checkbox-label",
				"mat-select-arrow",
				"mat-expansion-panel-header-title",
				"order",
				//Sort by and options
				"mat-select-placeholder", "mat-option-ripple",
				//Calendar
				"owl-dt-calendar-cell-content",
				// Reservation cells
				"mat-cell",
				"ng-star-inserted"
				);

		// Disallow links and pages with these extensions
		// Set to null to ignore this feature
		deniedExtensions = Arrays.asList("pdf", "jpg", "png");

		// Define a whitelist of allowed domains for links and pages
		// An empty list will be filled with the domain from the sut connector
		// Set to null to ignore this feature
		domainsAllowed = null;

		// If true, follow links opened in new tabs
		// If false, stay with the original (ignore links opened in new tabs)
		followLinks = true;

		// Propagate followLinks setting
		WdDriver.followLinks = followLinks;
		WdDriver.fullScreen = true;

		// List of atributes to identify and close policy popups
		// Set to null to disable this feature
		policyAttributes = null;
		
		login = null;

		// Override ProtocolUtil to allow WebDriver screenshots
		protocolUtil = new WdProtocolUtil();
		
		// Set desired License
		licenseSUT = new MyThaiStarLicense();
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
	@Override
	protected SUT startSystem() throws SystemStartException {
		return super.startSystem();
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

		loginMyThaiStarAction("waiter", "waiter", actions, state, ac);

		registerMyThaiStarAction("email@email.com", "password", actions, state, ac);

		// Check if forced actions are needed to stay within allowed domains
		Set<Action> forcedActions = detectForcedActions(state, ac);
		if (forcedActions != null && forcedActions.size() > 0) {
			return forcedActions;
		}

		// iterate through all widgets
		for (Widget widget : state) {
			// left clicks, but ignore links outside domain
			if (isAlwaysClickable(widget)) {
				actions.add(ac.leftClickAt(widget));
			}

			// only consider enabled and non-tabu widgets
			if (!widget.get(Enabled, true) || blackListed(widget)) {
				continue;
			}

			// slides can happen, even though the widget might be blocked
			//addSlidingActions(actions, ac, scrollArrowSize, scrollThick, widget, state);

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
				}
			}

			// slider widgets
			if (isAtBrowserCanvas(widget) && isSlider(widget) && (whiteListed(widget) || isUnfiltered(widget))) {
				addSlidingActions(actions, ac, scrollArrowSize, scrollThick, widget, state);
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
	
	protected boolean isAlwaysClickable(Widget widget) {
		WdElement element = ((WdWidget) widget).element;
		Set<String> clickSet = new HashSet<>(alwaysClickableClasses);
		clickSet.retainAll(element.cssClasses);
		return clickSet.size() > 0;
	}

	@Override
	protected boolean isTypeable(Widget widget) {
		WdElement element = ((WdWidget) widget).element;
		Set<String> clickSet = new HashSet<>(typeableClasses);
		clickSet.retainAll(element.cssClasses);
		if(clickSet.size() > 0)
			return true;

		return false;
	}

	protected boolean isSlider(Widget widget) {
		WdElement element = ((WdWidget) widget).element;
		Set<String> clickSet = new HashSet<>(slidesClasses);
		clickSet.retainAll(element.cssClasses);
		if(clickSet.size() > 0)
			return true;

		return false;
	}

	/**
	 * Select one of the available actions using an action selection algorithm (for example random action selection)
	 *
	 * @param state the SUT's current state
	 * @param actions the set of derived actions
	 * @return  the selected action (non-null!)
	 */
	@Override
	protected Action selectAction(State state, Set<Action> actions){

		//Call the preSelectAction method from the AbstractProtocol so that, if necessary,
		//unwanted processes are killed and SUT is put into foreground.
		Action retAction = preSelectAction(state, actions);
		if (retAction== null) {
			//if no preSelected actions are needed, then implement your own action selection strategy
			//using the action selector of the state model:
			retAction = stateModelManager.getAbstractActionToExecute(actions);
		}
		if(retAction==null) {
			System.out.println("State model based action selection did not find an action. Using default action selection.");
			// if state model fails, use default:
			retAction = super.selectAction(state, actions);
		}
		return retAction;
	}
	
	/**
	 * All TESTAR test sequence sessions are closed (State Model + OrientDB included)
	 * We can start other connection to create State Model Difference Report
	 */
	@Override
	protected void closeTestSession() {
		super.closeTestSession();

		// Install Node JS packages
		installNodePackages(new HashSet<>(Arrays.asList("mongodb", "ajv")));

		// Prepare NodeJS query based on created Test Results Artifact Files
		String commandTestResults = prepareTestResultNodeCommand();

		// Execute the NodeJS query and obtain the TestResults ArtefactId
		String artefactIdTestResults = executeNodeJSQueryPKM(commandTestResults);

		// Update the JSON Map that we will use to launch TESTAR HttpReportServer web service
		updateTestResultsJsonMap(artefactIdTestResults);

		if(settings.get(ConfigTags.StateModelEnabled, false)) {

			// Prepare NodeJS query based on created State Model Artifact Files
			String commandStateModel = prepareStateModelNodeCommand();

			// Execute the NodeJS query and obtain the StateModels ArtefactId
			String artefactIdStateModel = executeNodeJSQueryPKM(commandStateModel);
			
			// Update the JSON Map that we will use to launch TESTAR HttpReportServer web service
			updateStateModelDifferenceJsonMap(artefactIdStateModel);
		}
	}

	//Method to customize login action
	private void loginMyThaiStarAction(String username, String userPass, Set<Action> actions, State state, StdActionCompiler ac) {
		Action typeUsername = new NOP();
		Action typePassword = new NOP();

		for(Widget w : state) {
			if(w.get(WdTags.WebName,"").equals("username")) {
				typeUsername = ac.clickTypeInto(w, username, true);
			}
			if(w.get(WdTags.WebName,"").equals("password")) {
				typePassword = ac.clickTypeInto(w, userPass, true);
			}
		}

		String NOP_ID = "No Operation";

		if(!typeUsername.toString().contains(NOP_ID) && !typePassword.toString().contains(NOP_ID)){
			Action userLogin = new CompoundAction.Builder()
					.add(typeUsername, 1)
					.add(typePassword, 1)
					.add(new KeyDown(KBKeys.VK_ENTER),0.5).build();
			userLogin.set(Tags.OriginWidget, typeUsername.get(Tags.OriginWidget));
			actions.add(userLogin);
		}
	}

	//Method to customize register action
	private void registerMyThaiStarAction(String email, String emailPass, Set<Action> actions, State state, StdActionCompiler ac) {
		Action typeEmail = new NOP();
		Action typePassword = new NOP();
		Action typeConfirmPassword = new NOP();
		Action clickAcceptTerms = new NOP();

		for(Widget w : state) {
			if(w.get(WdTags.WebName,"").equals("email")) {
				typeEmail = ac.clickTypeInto(w, email, true);
			}
			if(w.get(WdTags.WebName,"").equals("password")) {
				typePassword = ac.clickTypeInto(w, emailPass, true);
			}
			if(w.get(WdTags.WebName,"").equals("confirmPassword")) {
				typeConfirmPassword = ac.clickTypeInto(w, emailPass, true);
			}
			if(w.get(WdTags.WebName,"").equals("registerTerms")) {
				clickAcceptTerms = ac.leftClickAt(w);
			}
		}

		String NOP_ID = "No Operation";

		if(!typeEmail.toString().contains(NOP_ID) 
				&& !typePassword.toString().contains(NOP_ID)&& !typeConfirmPassword.toString().contains(NOP_ID)
				&& !clickAcceptTerms.toString().contains(NOP_ID)){
			Action userRegister = new CompoundAction.Builder()
					.add(typeEmail, 1)
					.add(typePassword, 1)
					.add(typeConfirmPassword, 1)
					.add(clickAcceptTerms, 1)
					.add(new KeyDown(KBKeys.VK_ENTER),0.5).build();
			userRegister.set(Tags.OriginWidget, typeEmail.get(Tags.OriginWidget));
			actions.add(userRegister);
		}
	}

}

/**
 *  Helper class to customize SUT License as desires
 */
class MyThaiStarLicense {
	
	String sutTitle = "MyThaiStar";
	String sutName = "My Thai Star reference application for devonfw";
	String sutUrl = "https://github.com/devonfw/my-thai-star";
	String product = "devonfw";
	String licenseDetails = "https://devonfw.com/website/pages/docs/devonfw-ide-support.asciidoc.html";
	boolean isOpenSource = true;
	String sourceCodeLicense = "Apache Public License 2.0";
	String sourceCodeLicenseUrl = "https://github.com/devonfw/ide/blob/master/LICENSE";
	String documentationLicense = "Creative Commons License - Attribution-NoDerivatives 4.0 International";
	String documentationLicenseUrl = "https://creativecommons.org/licenses/by-nd/4.0/";

	public MyThaiStarLicense () { 
		// Create object for JSON Artefact purposes
	}
}
