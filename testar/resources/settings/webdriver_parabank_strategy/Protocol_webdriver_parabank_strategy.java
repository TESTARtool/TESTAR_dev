/**
 * Copyright (c) 2018 - 2023 Open Universiteit - www.ou.nl
 * Copyright (c) 2019 - 2023 Universitat Politecnica de Valencia - www.upv.es
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

import org.testar.RandomActionSelector;
import org.testar.managers.InputDataManager;
import org.testar.monkey.*;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.actions.WdSelectListAction;
import org.testar.monkey.alayer.devices.KBKeys;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.alayer.exceptions.SystemStartException;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.WdElement;
import org.testar.monkey.alayer.webdriver.WdWidget;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.plugin.NativeLinker;
import org.testar.protocols.WebdriverProtocol;
import parsing.ParseUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static org.testar.OutputStructure.outerLoopName;
import static org.testar.monkey.alayer.Tags.Blocked;
import static org.testar.monkey.alayer.Tags.Enabled;

public class Protocol_webdriver_parabank_strategy extends WebdriverProtocol
{
	private ParseUtil               parseUtil;
	private RandomActionSelector    selector;
	private boolean useRandom = false;
	private Map<String, Integer>    actionsExecuted      = new HashMap<String, Integer>();
	private Map<String, Integer>    debugActionsExecuted      = new HashMap<String, Integer>();

	@Override
	protected void initialize(Settings settings)
	{
		super.initialize(settings);

		useRandom = (settings.get(ConfigTags.StrategyFile).equals("")) ? true : false;
		if (useRandom)
			selector = new RandomActionSelector();
		else
			parseUtil = new ParseUtil(settings.get(ConfigTags.StrategyFile));
	}

	@Override
	protected void beginSequence(SUT system, State state)
	{
		super.beginSequence(system, state);
		state.remove(Tags.PreviousAction);
		state.remove(Tags.PreviousActionID);
	}

	@Override
	protected SUT startSystem() throws SystemStartException
	{
		SUT system = super.startSystem();

		/** Perform the login in Start System to work for Spy and Generate mode **/

		// Parabank script login sequence
		WdDriver.executeScript("document.getElementsByName('username')[0].setAttribute('value','john');");
		WdDriver.executeScript("document.getElementsByName('password')[0].setAttribute('value','demo');");
		WdDriver.executeScript("document.getElementsByName('login')[0].submit();");
		Util.pause(1);

		/* 
		 * Load the form URL we want to test
		 * 
		 * https://para.testar.org/parabank/billpay.htm
		 * https://para.testar.org/parabank/updateprofile.htm
		 * https://para.testar.org/parabank/contact.htm
		 */
		String[] parts = settings.get(ConfigTags.SUTConnectorValue, "").split(" ");
		String formURL = parts[parts.length - 1].replace("\"", "");
		WdDriver.getRemoteWebDriver().get(formURL);
		Util.pause(1);

		return system;
	}

	@Override
	protected State getState(SUT system) throws StateBuildException
	{
		State state = super.getState(system);
		if(latestState == null)
		{
			state.set(Tags.StateChanged, true);
		}
		else
		{
			String previousStateID = latestState.get(Tags.AbstractIDCustom);
			boolean stateChanged = ! previousStateID.equals(state.get(Tags.AbstractIDCustom));
			state.set(Tags.StateChanged, stateChanged);
		}

		return state;
	}

	@Override
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException
	{
		// Kill unwanted processes, force SUT to foreground
		Set<Action> actions = super.deriveActions(system, state);

		// create an action compiler, which helps us create actions
		// such as clicks, drag&drop, typing ...
		StdActionCompiler ac = new AnnotatingActionCompiler();

		// Check if forced actions are needed to stay within allowed domains
		Set<Action> forcedActions = detectForcedActions(state, ac);
		if(forcedActions != null && !forcedActions.isEmpty()) return forcedActions;

		// iterate through all widgets
		for (Widget widget : state)
		{
			// If the web state contains a form, start a new mode that only focuses on deriving form filling actions.
			// We need to ignore activity URL because contains a persistent form that does not disappear. 
			if(widget.get(Tags.Role, Roles.Widget).equals(WdRoles.WdFORM) && !WdDriver.getCurrentUrl().contains("activity.htm"))
			{
				Set<Action> formFillingActions = formFillingModeDeriveActions(widget, ac);
				if(!formFillingActions.isEmpty()) return formFillingActions;
			}

			// only consider enabled and non-tabu widgets
			if (!widget.get(Enabled, true)) continue;

			// If the element is blocked, Testar can't click on or type in the widget
			if (widget.get(Blocked, false) && !widget.get(WdTags.WebIsShadow, false)) continue;

			// type into text boxes
			if (isAtBrowserCanvas(widget) && isTypeable(widget))
			{
				if(whiteListed(widget) || isUnfiltered(widget))
				{
					actions.add(ac.pasteTextInto(widget, getRandomParabankData(widget), true));
				}
			}

			// left clicks, but ignore links outside domain
			if (isAtBrowserCanvas(widget) && isClickable(widget))
			{
				if(whiteListed(widget) || isUnfiltered(widget))
				{
					// Click on select web items opens the menu but does not allow TESTAR to select an item,
					// thats why we need a custom action selection
					if(widget.get(Tags.Role, Roles.Widget).equals(WdRoles.WdSELECT)) {
						actions.add(randomFromSelectList(widget));
					}
					else if (!isLinkDenied(widget) && widget.get(WdTags.WebType, "").equalsIgnoreCase("submit"))
					{
						actions.add(ac.leftClickAt(widget));
					}
				}
			}
		}

		return actions;
	}

	private Set<Action> formFillingModeDeriveActions(Widget formWidget, StdActionCompiler ac)
	{
		Set<Action> formFillingActions = new HashSet<>();

		// Click and type form elements
		deriveFormFilling(formFillingActions, formWidget, ac);

		// Add the possibility to page down (scroll down) if a widget of the form is not visible below
		if(formContainsNonVisibleSubmitButtonBelow(formWidget))
		{
			Action pageDown = ac.hitKey(KBKeys.VK_PAGE_DOWN);
			pageDown.set(Tags.OriginWidget, formWidget);
			formFillingActions.add(pageDown);
		}

		return formFillingActions;
	}

	private void deriveFormFilling(Set<Action> formFillingActions, Widget widget, StdActionCompiler ac)
	{
		if(widget.get(Enabled, false) && !widget.get(Blocked, true) && isTypeable(widget) && isAtBrowserCanvas(widget))
		{
			formFillingActions.add(ac.pasteTextInto(widget, getRandomParabankData(widget), true));
		}
		if(widget.get(Enabled, false) && !widget.get(Blocked, true) && isClickable(widget) && isAtBrowserCanvas(widget)) 
		{
			// Click on select web items opens the menu but does not allow TESTAR to select an item,
			// thats why we need a custom action selection
			if(widget.get(Tags.Role, Roles.Widget).equals(WdRoles.WdSELECT)) {
				formFillingActions.add(randomFromSelectList(widget));
			}
			else {
				formFillingActions.add(ac.leftClickAt(widget));
			}
		}

		// Iterate through the form element widgets
		for(int i = 0; i < widget.childCount(); i++)
		{
			deriveFormFilling(formFillingActions, widget.child(i), ac);
		}
	}

	private boolean formContainsNonVisibleSubmitButtonBelow(Widget formChildWidget)
	{
		boolean submitButtonBelow = false;

		// If the widget is not at browser canvas
		if(!isAtBrowserCanvas(formChildWidget))
		{
			submitButtonBelow = formChildWidget.get(WdTags.WebType, "").equalsIgnoreCase("submit");
		}

		if(formChildWidget.childCount() > 0)
		{
			// Iterate through the form element widgets
			for(int i = 0; i < formChildWidget.childCount(); i++)
			{
				submitButtonBelow = submitButtonBelow || formContainsNonVisibleSubmitButtonBelow(formChildWidget.child(i));
			}
		}

		return submitButtonBelow;
	}

	@Override
	protected boolean isClickable(Widget widget)
	{
		Role role = widget.get(Tags.Role, Roles.Widget);
		if (Role.isOneOf(role, NativeLinker.getNativeClickableRoles()))
		{
			// Input type are special...
			if (role.equals(WdRoles.WdINPUT))
			{
				String type = ((WdWidget) widget).element.type;
				return WdRoles.clickableInputTypes().contains(type);
			}
			return true;
		}

		WdElement element = ((WdWidget) widget).element;
		if (element.isClickable)
		{
			return true;
		}

		Set<String> clickSet = new HashSet<>(clickableClasses);
		clickSet.retainAll(element.cssClasses);
		return clickSet.size() > 0;
	}

	@Override
	protected boolean isTypeable(Widget widget)
	{
		Role role = widget.get(Tags.Role, Roles.Widget);
		if (Role.isOneOf(role, NativeLinker.getNativeTypeableRoles()))
		{
			// Specific class="input" for parasoft SUT
			if(widget.get(WdTags.WebCssClasses, "").contains("input"))
			{
				return true;
			}

			// Input type are special...
			if (role.equals(WdRoles.WdINPUT))
			{
				String type = ((WdWidget) widget).element.type;
				return WdRoles.typeableInputTypes().contains(type.toLowerCase());
			}
			return true;
		}

		return false;
	}

	private String getRandomParabankData(Widget widget)
	{
		// The Bill Payment form requires an amount number
		if(widget.get(WdTags.WebName, "").contains("amount"))
		{
			return InputDataManager.getRandomNumberInput();
		}
		// The Bill Payment form requires adding an account number twice
		// For the moment... use a hard-coded account number
		else if(widget.get(WdTags.WebName, "").contains("payee.accountNumber")
				|| widget.get(WdTags.WebName, "").contains("verifyAccount"))
		{
			return "12345";
		}

		return InputDataManager.getRandomTextInputData(widget);
	}


	/**
	 * Randomly select one item from the select list widget. 
	 * 
	 * @param w
	 * @return
	 */
	private Action randomFromSelectList(Widget w) {
		int selectLength = 1;
		String elementName = w.get(WdTags.WebName, "");

		// Get the number of elements of the specific select list item
		try {
			String query = String.format("document.getElementsByName('%s')[0].length", elementName);
			Object response = WdDriver.executeScript(query);
			selectLength = ( response != null ? Integer.parseInt(response.toString()) : 1 );
		} catch (Exception e) {
			System.out.println("*** ACTION WARNING: problems trying to obtain select list length: " + elementName);
		}

		Action selectAction = new WdSelectListAction("", elementName, Integer.toString(selectLength-1), w);

		return selectAction;
	}

	@Override
	protected Action selectAction(State state, Set<Action> actions)
	{
		if(DefaultProtocol.lastExecutedAction != null)
		{
			state.set(Tags.PreviousAction, DefaultProtocol.lastExecutedAction);
			state.set(Tags.PreviousActionID, DefaultProtocol.lastExecutedAction.get(Tags.AbstractIDCustom, null));
		}
		Action selectedAction = (useRandom) ?
				selector.selectAction(actions):
				parseUtil.selectAction(state, actions, actionsExecuted);

		String actionID = selectedAction.get(Tags.AbstractIDCustom);
		Integer timesUsed = actionsExecuted.getOrDefault(actionID, 0); //get the use count for the action
		actionsExecuted.put(actionID, timesUsed + 1); //increase by one

		if(selectedAction.get(Tags.OriginWidget, null) != null) {
			String widgetPath = selectedAction.get(Tags.OriginWidget).get(Tags.Path).trim();
			String widgetDesc = selectedAction.get(Tags.OriginWidget).get(Tags.Desc);

			//TODO: debugActionsExecuted is not tracking non-executed actions
			String identifier = widgetPath + ":" + widgetDesc;
			Integer timesDescUsed = debugActionsExecuted.getOrDefault(identifier, 0); //get the use count for the action
			debugActionsExecuted.put(identifier, timesDescUsed + 1); //increase by one
		}

		return selectedAction;
	}

	/**
	 * TESTAR uses this method to determine when to stop the generation of actions for the
	 * current sequence. You can stop deriving more actions after:
	 * - a specified amount of executed actions, which is specified through the SequenceLength setting, or
	 * - after a specific time, that is set in the MaxTime setting
	 * @return  if <code>true</code> continue generation, else stop
	 */
	@Override
	protected boolean moreActions(State state)
	{
		for(Widget widget : state)
		{
			// IF some of the forms was filled properly, stop the testing run
			if(widget.get(WdTags.WebTextContent, "").equalsIgnoreCase("Bill Payment Complete") 
					|| widget.get(WdTags.WebTextContent, "").equalsIgnoreCase("Profile Updated")
					|| widget.get(WdTags.WebTextContent, "").equalsIgnoreCase("A Customer Care Representative will be contacting you."))
			{
				logFormValues(state);
				return false;
			}
		}
		return super.moreActions(state);
	}

	/**
	 * Print the <li> web elements corresponding to the filled form values
	 *
	 * param state
	 */
	private void logFormValues(State state)
	{
		try
		{
			FileWriter myWriter = new FileWriter(Main.outputDir + File.separator + outerLoopName + File.separator +"log_form_values.txt", true);

			myWriter.write("---------- DEBUG FORM ----------");
			myWriter.write(System.getProperty("line.separator"));

			myWriter.write(WdDriver.getCurrentUrl());
			myWriter.write(System.getProperty("line.separator"));
			myWriter.write("No. actions: " + (actionCount-1));
			myWriter.write(System.getProperty("line.separator"));
			myWriter.close();
		}
		catch (IOException e)
		{
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}


	/**
	 * This methods is called after each test sequence, allowing for example using external profiling software on the SUT
	 *
	 * super.postSequenceProcessing() is adding test verdict into the HTML sequence report
	 */
	@Override
	protected void postSequenceProcessing()
	{
		super.postSequenceProcessing();
		logActionCount(latestState);
	}

	/**
	 * Print the <li> web elements corresponding to the filled form values
	 *
	 * param state
	 */
	private void logActionCount(State state)
	{
		try
		{
			// convert the map entries to a list
			java.util.List<Map.Entry<String, Integer>> entryList = new ArrayList<>(debugActionsExecuted.entrySet());

			// Courtesy of ChatGPT
			// sort the list by the keys (which are String values)
			Collections.sort(entryList, new Comparator<Map.Entry<String, Integer>>() {
				@Override
				public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
					String[] parts1 = o1.getKey().split(":");
					String[] parts2 = o2.getKey().split(":");

					int[] arr1 = getIntArray(parts1[0]);
					int[] arr2 = getIntArray(parts2[0]);

					for (int i = 0; i < Math.min(arr1.length, arr2.length); i++) {
						if (arr1[i] != arr2[i]) {
							return arr1[i] - arr2[i];
						}
					}

					if (arr1.length != arr2.length) {
						return arr1.length - arr2.length;
					}

					return parts1[1].compareTo(parts2[1]);
				}

				private int[] getIntArray(String s) {
					String[] parts = s.replaceAll("\\[|\\]|\\s", "").split(",");
					int[] result = new int[parts.length];
					for (int i = 0; i < parts.length; i++) {
						result[i] = Integer.parseInt(parts[i]);
					}
					return result;
				}
			});

			FileWriter myWriter = new FileWriter(Main.outputDir + File.separator + outerLoopName + File.separator + "log_form_values.txt", true);

			myWriter.write("---------- Actions Executed ----------");
			myWriter.write(System.getProperty( "line.separator" ));
			for (Map.Entry<String, Integer> entry : entryList) {
				String line = entry.getKey() + " , " + entry.getValue();
				myWriter.write(line);
				myWriter.write(System.getProperty( "line.separator" ));
			}

			myWriter.close();
		}
		catch (IOException e)
		{
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	@Override
	protected void closeTestSession()
	{
		super.closeTestSession();
		if(settings.get(ConfigTags.Mode).equals(Modes.Generate))
		{
			compressOutputRunFolder();
			copyOutputToNewFolderUsingIpAddress("N:");
		}
	}
}
