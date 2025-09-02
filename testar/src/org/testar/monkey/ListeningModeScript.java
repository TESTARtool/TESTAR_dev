/***************************************************************************************************
 *
 * Copyright (c) 2023 - 2024 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2023 - 2024 Open Universiteit - www.ou.nl
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

package org.testar.monkey;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testar.CodingManager;
import org.testar.OutputStructure;
import org.testar.monkey.RuntimeControlsProtocol.Modes;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.UnknownEventAction;
import org.testar.monkey.alayer.devices.KBKeys;
import org.testar.monkey.alayer.webdriver.WdDriver;
import com.google.common.collect.Sets;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;

public class ListeningModeScript {

	/**
	 * Method to run TESTAR on Listening Script Mode.
	 * @param system
	 */
	public void runListeningLoop(DefaultProtocol protocol) {
		String listeningScript = protocol.settings().get(ConfigTags.ListeningScript);

		if(!new File(listeningScript).exists()) {
			System.err.println("The indicated ListeningScript does not exists: " + listeningScript);
			return;
		}

		String scriptContent = readScriptFile(listeningScript);
		if(scriptContent.isEmpty()) {
			System.err.println("There is an issue reading the ListeningScript content");
			return;
		}

		String scriptURL = extractNavigateUrl(scriptContent);
		if(scriptURL.isEmpty() || !protocol.settings().get(ConfigTags.SUTConnectorValue, "").contains(scriptURL)) {
			System.err.println("The SUTConnectorValue URL '%s' and ListeningScript URL '%s' do not match");
			return;
		}

		List<String[]> scriptActions = extractScriptActions(scriptContent);
		if(scriptActions.isEmpty()) {
			System.err.println("The ListeningScript content does not contain Playwright Page actions!");
			return;
		}

		System.out.println(String.format("Running Listening mode for script '%s' and URL '%s')", listeningScript, scriptURL));

		// Enable remote debugging for subsequent Playwright connection
		WdDriver.remoteDebugging = true;

		// Prepare the output folders structure
		synchronized(this){
			OutputStructure.calculateInnerLoopDateString();
			OutputStructure.sequenceInnerLoopCount++;
		}

		//empty method in defaultProtocol - allowing implementation in application specific protocols
		//HTML report is created here in Desktop and Webdriver protocols
		protocol.preSequencePreparations();

		//We need to invoke the SUT & the canvas representation
		SUT system = protocol.startSUTandLogger();
		protocol.cv = protocol.buildCanvas();
		protocol.actionCount = 1;

		//Generating the sequence file that can be replayed:
		protocol.getAndStoreGeneratedSequence();
		protocol.getAndStoreSequenceFile();

		// notify the statemodelmanager
		protocol.stateModelManager.notifyTestSequencedStarted();

		//Once the SUT and chrome with webdriver is launched
		//Initialize the script listener and the Playwright Page
		Playwright playwright = Playwright.create();
		Browser browser = playwright.chromium().connectOverCDP("http://localhost:9222");
		BrowserContext context = browser.contexts().get(0);
		Page page = context.pages().get(0);

		/**
		 * Listening Script Actions Loop
		 */
		for(int i = 0; (i < scriptActions.size() && protocol.mode() == Modes.ListeningScript && system.isRunning()); i++) {
			State state = protocol.getState(system);
			protocol.cv.begin();
			Util.clear(protocol.cv);

			Set<Action> actions = protocol.deriveActions(system, state);
			protocol.buildStateActionsIdentifiers(state, actions);
			// Add TESTAR derived actions into the report
			protocol.reportManager.addActions(actions);

			//notify the state model manager of the new state
			protocol.stateModelManager.notifyNewStateReached(state, actions);

			// If no actions are derived, create an ESC action
			if(actions.isEmpty()){
				//----------------------------------
				// THERE MUST ALMOST BE ONE ACTION!
				//----------------------------------
				// if we did not find any actions, then we just hit escape, maybe that works ;-)
				Action escAction = new AnnotatingActionCompiler().hitKey(KBKeys.VK_ESCAPE);
				protocol.buildEnvironmentActionIdentifiers(state, escAction);
				actions.add(escAction);
			}

			// Execute the script action
			Action listenedAction = executePageAction(page, scriptActions.get(i), state);

			protocol.buildStateActionsIdentifiers(state, Collections.singleton(listenedAction));

			// Map Listened Actions, that are not Type, with existing actions
			if(!listenedAction.get(Tags.Desc, "Nothing").contains("Type")) {
				// Search MapEventUser action on previous builded actions (To match AbstractID)
				for(Action a : actions) {
					if(a.get(Tags.Desc, "Nothing").equals(listenedAction.get(Tags.Desc, "None"))) {
						listenedAction = a;
						break;
					}
				}
			}
			// Listened Type Actions will have specific input text, 
			// so mapping with previously random generated Type Actions is not an appropriate solution
			// The listenedAction will be a new Concrete Action and maybe an existing Abstract Action

			// If something went wrong trying to find the action, we need to create the AbstractID
			if(listenedAction.get(Tags.AbstractID, null) == null) {
				System.out.println("Listened Action has not AbstractID, creating...");
				CodingManager.buildIDs(state, Sets.newHashSet(listenedAction));
				System.out.println(listenedAction.get(Tags.AbstractID));
			}

			// Add listened action into the report
			protocol.reportManager.addSelectedAction(state, listenedAction);
			System.out.println("DEBUG: Listened Action: " + listenedAction.get(Tags.Desc, "NoDesc"));

			//notify the state model manager of the listened action
			protocol.stateModelManager.notifyListenedAction(listenedAction);

			protocol.saveActionInfoInLogs(state, listenedAction, "ListenedAction");
			DefaultProtocol.lastExecutedAction = listenedAction;
			protocol.actionCount++;

			protocol.saveActionIntoFragmentForReplayableSequence(listenedAction, state, actions);

			Util.clear(protocol.cv);
			protocol.cv.end();

			Util.pause(1);
		}

		protocol.mode = Modes.Quit;

		// notify to state model the last state
		State state = protocol.getState(system);
		Set<Action> actions = protocol.deriveActions(system, state);
		protocol.buildStateActionsIdentifiers(state, actions);
		protocol.stateModelManager.notifyNewStateReached(state, actions);

		// notify the statemodelmanager
		protocol.stateModelManager.notifyTestSequenceStopped();

		// notify the state model manager of the sequence end
		protocol.stateModelManager.notifyTestingEnded();

		//Closing fragment for listened replayable test sequence:
		protocol.writeAndCloseFragmentForReplayableSequence();

		//Copy sequence file into proper directory:
		protocol.classifyAndCopySequenceIntoAppropriateDirectory(protocol.getFinalVerdict());

		protocol.postSequenceProcessing();

		//If we want to Quit the current execution we stop the system
		protocol.stopSystem(system);
	}

	private String readScriptFile(String filePath) {
		try {
			return new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	// Extract the URL from the page.navigate() method
	private String extractNavigateUrl(String scriptContent) {
		// Regex pattern to match page.navigate("URL")
		String navigatePattern = "page\\.navigate\\(\"(.*?)\"\\)";

		// Compile the regex pattern
		Pattern pattern = Pattern.compile(navigatePattern);
		Matcher matcher = pattern.matcher(scriptContent);

		// Find and return the first URL found
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			return "";
		}
	}

	private List<String[]> extractScriptActions(String scriptContent) {
		List<String[]> actions = new ArrayList<>();

		// Split the script content line by line
		String[] lines = scriptContent.split("\\n");

		// Regex for click action: page.locator("selector").click();
		String clickPattern = "page\\.locator\\(\"(.*?)\"\\)\\.click\\(\\);";
		Pattern clickRegex = Pattern.compile(clickPattern);

		// Regex for fill action: page.locator("selector").fill("value");
		String fillPattern = "page\\.locator\\(\"(.*?)\"\\)\\.fill\\(\"(.*?)\"\\);";
		Pattern fillRegex = Pattern.compile(fillPattern);

		// Regex for roleClick action: page.getByRole(AriaRole.BUTTON, setName("name")).click();
		String roleClickPattern = "page\\.getByRole\\(AriaRole\\.(.*?),.*?setName\\(\"(.*?)\"\\)\\)\\.click\\(\\);";
		Pattern roleClickRegex = Pattern.compile(roleClickPattern);

		// Iterate over each line of the script content
		for (String line : lines) {
			Matcher clickMatcher = clickRegex.matcher(line);
			Matcher fillMatcher = fillRegex.matcher(line);
			Matcher roleClickMatcher = roleClickRegex.matcher(line);

			if (clickMatcher.find()) {
				// Found a click action
				actions.add(new String[]{"click", clickMatcher.group(1)});  // Add the click action and the locator
			} else if (fillMatcher.find()) {
				// Found a fill action
				actions.add(new String[]{"fill", fillMatcher.group(1), fillMatcher.group(2)});  // Add the fill action, locator, and value
			} else if (roleClickMatcher.find()) {
				// Found a roleClick action
				actions.add(new String[]{"roleClick", roleClickMatcher.group(1), roleClickMatcher.group(2)});  // Add the role click action, role, and name
			}
		}

		return actions;  // Return the list of actions in the order they were found
	}

	// Function to execute Playwright Page
	private Action executePageAction(Page page, String[] action, State state) {
		switch (action[0]) {
		case "click":
			return scriptClick(page, action, state);

		case "fill":
			return scriptType(page, action, state);

		case "roleClick":
			return scriptRoleClick(page, action, state);

		default:
			System.out.println("Unknown action: " + action[0]);
			return scriptUnknownAction(state);
		}
	}

	private Action scriptClick(Page page, String[] action, State state) {
		String clickSelector = action[1].replace("\\", "");
		System.out.println("Clicking on: " + clickSelector);

		// Create a TESTAR action
		Widget listenedWidget = scriptWidget(page, state, clickSelector);
		Action listenedAction = (new AnnotatingActionCompiler()).leftClickAt(listenedWidget, 0.5, 0.5);

		// Execute the script action
		page.locator(clickSelector).click();
		return listenedAction;
	}

	private Action scriptType(Page page, String[] action, State state) {
		String fillSelector = action[1].replace("\\", "");
		String typedText = action[2];
		System.out.println("Filling field: " + fillSelector + " with value: " + typedText);

		// Create a TESTAR action
		Widget listenedWidget = scriptWidget(page, state, fillSelector);
		Action listenedAction = (new AnnotatingActionCompiler()).clickTypeInto(listenedWidget, typedText, true);

		// Execute the script action
		page.locator(fillSelector).fill(typedText);
		return listenedAction;
	}

	private Action scriptRoleClick(Page page, String[] action, State state) {
		System.out.println("Clicking button with role: " + action[1] + " and name: " + action[2]);
		Locator locator = page.getByRole(AriaRole.valueOf(action[1]), new Page.GetByRoleOptions().setName(action[2]));

		List<ElementHandle> elements = locator.elementHandles();

		if(elements.size() == 0) return scriptUnknownAction(state);

		if(elements.size() > 2) {
			System.out.println("WARNING: There seems to be multiple elements for the same RoleClick locator");
			System.out.println("WARNING: Returning first element of the RoleClick locator");
		}

		// Create a TESTAR action
		Widget listenedWidget = scriptWidget(page, state, elements.get(0));
		Action listenedAction = (new AnnotatingActionCompiler()).leftClickAt(listenedWidget, 0.5, 0.5);

		// Execute the script action
		locator.click();
		return listenedAction;
	}

	private Action scriptUnknownAction(State state) {
		// Create unknown script Action which will create a model transition
		Action unknownAction = new UnknownEventAction();
		unknownAction.set(Tags.Role, Roles.System);
		unknownAction.set(Tags.OriginWidget, state);
		unknownAction.set(Tags.Desc, "Unknown Script Action");
		return unknownAction;
	}

	// Obtain the widget interacted by the script
	private Widget scriptWidget(Page page, State state, String selector) {
		ElementHandle element = page.waitForSelector(selector);
		return scriptWidget(page, state, element);
	}
	private Widget scriptWidget(Page page, State state, ElementHandle element) {
		// Aim to the middle of the element
		double x = element.boundingBox().x + (element.boundingBox().width /2.0);
		double y = element.boundingBox().y + (element.boundingBox().height /2.0);

		return Util.widgetFromPoint(state, x, y);
	}

	//	class ScriptListener implements WebDriverListener {
	//		private EventFiringDecorator<WebDriver> decorator = new EventFiringDecorator<>(this);
	//		private DefaultProtocol protocol;
	//		private Action executedAction;
	//
	//		public ScriptListener(WebDriver driver, DefaultProtocol protocol) {
	//			decorator.decorate(driver);
	//			this.protocol = protocol;
	//		}
	//
	//		public Action getScriptAction() {
	//			return executedAction;
	//		}
	//
	//		@Override
	//		public void beforeClick(WebElement element) {
	//			System.out.println("ScriptListener - beforeClick: " + element.toString());
	//
	//			// Aim to the middle of the element
	//			double x = element.getRect().getX() + (element.getRect().getWidth() /2.0);
	//			double y = element.getRect().getY() + (element.getRect().getHeight() /2.0);
	//
	//			// Extract the widget from the point by subtracting the browser deviation
	//			Widget w = Util.widgetFromPoint(protocol.latestState, x, y);
	//			x = 0.5; y = 0.5;
	//
	//			// Create a TESTAR action
	//			executedAction = (new AnnotatingActionCompiler()).leftClickAt(w,x,y);
	//		}
	//
	//		@Override
	//		public void beforeSendKeys(WebElement element, CharSequence... keysToSend) {
	//			System.out.println("ScriptListener - beforeSendKeys: " + element.toString() + " keya: " + keysToSend);
	//
	//			// Aim to the middle of the element
	//			double x = element.getRect().getX() + (element.getRect().getWidth() /2.0);
	//			double y = element.getRect().getY() + (element.getRect().getHeight() /2.0);
	//
	//			String typedText = Arrays.toString(keysToSend).replace("[", "").replace("]", "");
	//
	//			// Extract the widget from the point by subtracting the browser deviation
	//			Widget w = Util.widgetFromPoint(protocol.latestState, x, y);
	//			x = 0.5; y = 0.5;
	//
	//			// Create a TESTAR action
	//			executedAction = (new AnnotatingActionCompiler()).clickTypeInto(w, typedText, true);
	//		}
	//
	//	}
}
