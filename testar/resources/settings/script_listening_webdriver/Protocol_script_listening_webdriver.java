/**
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
 *
 */

import es.upv.staq.testar.NativeLinker;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.*;
import org.fruit.alayer.devices.MouseButtons;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.webdriver.*;
import org.fruit.alayer.webdriver.enums.WdRoles;
import org.fruit.alayer.webdriver.enums.WdTags;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.testar.protocols.WebdriverProtocol;

import java.util.*;
import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;


public class Protocol_script_listening_webdriver extends WebdriverProtocol {

	private Script scriptClass;
	
	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 *
	 * @param settings the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings) {
		//Set before initialize StateModel
		settings.set(ConfigTags.ListeningMode, true);
		
		super.initialize(settings);

		// Classes that are deemed clickable by the web framework
		clickableClasses = Arrays.asList("v-menubar-menuitem", "v-menubar-menuitem-caption");

		// Disallow links and pages with these extensions
		// Set to null to ignore this feature
		deniedExtensions = Arrays.asList("pdf", "jpg", "png");

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
		policyAttributes = null;
		/*policyAttributes = new HashMap<String, String>() {{ 
			put("id", "sncmp-banner-btn-agree");
		}};*/

		//Force the browser to run in full screen mode
		WdDriver.fullScreen = true;

		//Force webdriver to switch to a new tab if opened
		//This feature can block the correct display of select dropdown elements 
		WdDriver.forceActivateTab = true;
	}
	
	@Override
	protected SUT startSystem() throws SystemStartException {
		SUT system = super.startSystem();
		
		// Initialize helping class to execute scripted actions
		scriptClass = new Script(WdDriver.getRemoteWebDriver());
		
		return system;
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

			// Skip Admin and logout page widget
			if(widget.get(WdTags.WebHref,"").contains("admin.htm") || widget.get(WdTags.WebHref,"").contains("logout.htm")) {
				continue;
			}

			// only consider enabled and non-tabu widgets
			if (!widget.get(Enabled, true) || blackListed(widget)) {
				continue;
			}

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

		if(actions.isEmpty()) {
			return new HashSet<>(Collections.singletonList(new WdHistoryBackAction()));
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
	
	@Override
	protected void executeScriptEvent(SUT system, State state) {
		if(!scriptClass.executeScriptedAction()) {
			this.mode = Modes.Quit;
		}
	}
	
	private class Script extends AbstractWebDriverEventListener {
		EventFiringWebDriver eventDriver;
		int stepCounter; 
		
		public Script(WebDriver driver) {
			//initalize event-firing driver using Firefox webdriver instance.
			EventFiringWebDriver eventDriver = new EventFiringWebDriver(driver);
			//register event listener to even-firing webdriver instance
			eventDriver.register(this);
			this.eventDriver = eventDriver;
			this.stepCounter = 0;
		}
		
		/**
		 * I want to synch Script Actions with TESTAR. 
		 * To send a request like: Go ahead execute one step
		 * Then listen what's going on in the GUI.
		 * 
		 * @return if script action executed
		 */
		public boolean executeScriptedAction() {
			// step Counter is a bad way to do this, need to think a better approach
			if(stepCounter == 0) {
				eventDriver.findElement(By.className("home")).click();
			}
			else if(stepCounter == 1) {
				eventDriver.findElement(By.name("username")).sendKeys("john");
			}
			else if(stepCounter == 2) {
				eventDriver.findElement(By.name("password")).sendKeys("demo");
			}
			else if(stepCounter == 3) {
				eventDriver.findElement(By.xpath("//input[@value='Log In']")).click();
			}
			else {
				return false;
			}
			
			stepCounter = stepCounter + 1;
			return true;
		}
		
		/**
		 * Create an Event Object that represents Selenium Webdriver Click Action
		 */
		@Override
		public void beforeClickOn(WebElement element, WebDriver driver) {
			// Aim to the middle of the element
			double x = element.getRect().getX() + (element.getRect().getWidth() /2.0);
			double y = element.getRect().getY() + (element.getRect().getHeight() /2.0);
			// Create a TESTAR userEvent to be recorded
			userEvent = new Object[]{MouseButtons.BUTTON1, x, y};
		}
		
		/**
		 * Create an Event Object that represents a Selenium Webdriver Type Action
		 */
		@Override
		public void beforeChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
			// Aim to the middle of the element
			double x = element.getRect().getX() + (element.getRect().getWidth() /2.0);
			double y = element.getRect().getY() + (element.getRect().getHeight() /2.0);
			typedText = Arrays.toString(keysToSend).replace("[", "").replace("]", "");
			// Create a TESTAR userEvent to be recorded
			userEvent = new Object[]{"script", x, y};
		}
	}
}
