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

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.CodingManager;
import org.testar.RandomActionSelector;
import org.testar.managers.InputDataManager;
import org.testar.monkey.*;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.ActionRoles;
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
import org.testar.settings.Settings;
import org.testar.plugin.NativeLinker;
import org.testar.plugin.OperatingSystems;
import org.testar.protocols.WebdriverProtocol;
import parsing.ParseUtil;
import strategynodes.enums.ActionType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;

import static org.testar.OutputStructure.outerLoopName;
import static org.testar.monkey.alayer.Tags.Blocked;
import static org.testar.monkey.alayer.Tags.Enabled;

/**
 * Existing Parabank forms:
 * 
 * 1- OpenAccount: openaccount.htm
 * 2- Transfer Funds: transfer.htm
 * 3- Bill Payment Service: billpay.htm
 * 4- Find Transactions: findtrans.htm
 * 5- Update Profile: updateprofile.htm
 * 6- Apply for a Loan: requestloan.htm
 * 7- Customer Care: contact.htm
 * 
 * (filtered) Account Activity: activity.htm
 */
public class Protocol_webdriver_parabank_strategy extends WebdriverProtocol
{
	private ParseUtil parseUtil;
	private boolean strategyRandom = false;
	private Widget formFillingWidget = null;
	private MultiMap<String, Object> strategyActionsExecuted      = new MultiMap<>();

	// form.htm , 1 : [[n_fill_field1, n_fill_field2, n_fill_field3], num_succes_submit, num_unsucces_submit]
	private Map<String, Metrics> metricsFormsCompleted = new HashMap<String, Metrics>();
	private ArrayList<String> operatingSystems = new ArrayList<>();

	@Override
	protected void buildStateActionsIdentifiers(State state, Set<Action> actions) {
		CodingManager.buildIDs(state, actions);
		for(Action action : actions) {
			if(action.get(Tags.OriginWidget) != null) {

				Widget widget = action.get(Tags.OriginWidget);

				String collisionId = CodingManager.lowCollisionID(state.get(Tags.AbstractID)
						+ widget.get(Tags.AbstractID)
						+ action.get(Tags.Role));

				String actionAbstractId = CodingManager.ID_PREFIX_ACTION 
						+ CodingManager.ID_PREFIX_ABSTRACT 
						+ collisionId;

				action.set(Tags.AbstractID, actionAbstractId);
			}
		}
	}
	
	@Override
	protected void initialize(Settings settings)
	{
		super.initialize(settings);

		strategyRandom = settings.get(ConfigTags.StrategyFile).isEmpty();
		if(!strategyRandom) parseUtil = new ParseUtil(settings.get(ConfigTags.StrategyFile));
		
		for(OperatingSystems OS : NativeLinker.getPLATFORM_OS())
			operatingSystems.add(OS.toString());
	}

	@Override
	protected SUT startSystem() throws SystemStartException
	{
		try {
			File parabankFolder = new File(Main.settingsDir + File.separator + "webdriver_parabank_strategy");

			// First stop any possible apache parabank instance
			File parabankStop = new File(parabankFolder + File.separator + "apache_parabank_stop.bat").getCanonicalFile();
			if(parabankStop.exists()) {
				Process proc = Runtime.getRuntime().exec("cmd /c " + parabankStop, null, parabankFolder);
				Util.pause(10); // Wait seconds for apache parabank to stop
			} else {
				throw new SystemStartException("parabankStop does not exists");
			}

			// bat file that downloads and deploy the parabank SUT
			File parabankStart = new File(parabankFolder + File.separator + "apache_parabank_start.bat").getCanonicalFile();
			if(parabankStart.exists()) {
				Process proc = Runtime.getRuntime().exec("cmd /c " + parabankStart, null, parabankFolder);
				// Wait until apache web server is deployed
				while(!apacheWebIsReady()) {
					try {
						System.out.println("Waiting for a web service in localhost:8080 ...");
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} else {
				throw new SystemStartException("parabankStart does not exists");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new SystemStartException("ERROR running Apache Parabank");
		}

		SUT system = super.startSystem();

		/** Perform the login in Start System to work for Spy and Generate mode **/

		// Parabank script login sequence
		WdDriver.executeScript("document.getElementsByName('username')[0].setAttribute('value','john');");
		WdDriver.executeScript("document.getElementsByName('password')[0].setAttribute('value','demo');");
		WdDriver.executeScript("document.getElementsByName('login')[0].submit();");
		Util.pause(1);

		// Reset formFillingWidget in StartSystem because beginSequence is after getState
		formFillingWidget = null;

		return system;
	}

	private static boolean apacheWebIsReady() {
		try {
			// Try to connect to the localhost apache tomcat web server
			URL url = new URL("http://localhost:8080/parabank");
			HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.connect();

			// If we have get connection with the web app, everything is ready
			if(httpConnection.getResponseCode() == 200) {
				httpConnection.disconnect();
				return true;
			} 
			// If not, server is probably being deployed
			else {
				httpConnection.disconnect();
				return false;
			}
		} 
		catch (Exception e) {
			System.out.println("*** http://localhost:8080 is NOT ready ***");
		}
		return false;
	}

	@Override
	protected void beginSequence(SUT system, State state)
	{
		super.beginSequence(system, state);
		state.remove(Tags.PreviousAction);
		state.remove(Tags.PreviousActionID);

		// Reset the strategy calculation before each sequence
		strategyActionsExecuted = new MultiMap<>();

		// Reset the last executed action track
		lastExecutedAction = null;

		// Reset the metrics before each sequence
		metricsFormsCompleted = new HashMap<String, Metrics>();
		metricsFormsCompleted.put("total", new Metrics());
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
			String previousStateID = latestState.get(Tags.AbstractID);
			boolean stateChanged = ! previousStateID.equals(state.get(Tags.AbstractID));
			state.set(Tags.StateChanged, stateChanged);
		}

		// In the TESTAR state check if we are in form filling mode
		Widget currentWidgetForm = null;
		for (Widget widget : state)
		{
			if(widget.get(Tags.Role, Roles.Widget).equals(WdRoles.WdFORM))
			{
				currentWidgetForm = widget;
			} 
		}
		formFillingModeChecking(state, currentWidgetForm);

		return state;
	}

	private void formFillingModeChecking(State state, Widget currentWidgetForm)
	{
		// If the web state contains a form, start a new mode that only focuses on deriving form filling actions.
		if(currentWidgetForm != null)
		{
			formFillingWidget = currentWidgetForm;

			// If last action was a submit click, but we still are in a form filling mode
			// This means that we unsuccessfully clicked submit
			// Example: Bill Pay clicking submit without filling all inputs
			if(lastExecutedAction != null 
					&& lastExecutedAction.get(Tags.OriginWidget) != null
					&& lastExecutedAction.get(Tags.Role, ActionRoles.Action) == ActionRoles.LeftClickAt
					&& lastExecutedAction.get(Tags.OriginWidget).get(WdTags.WebType, "").equalsIgnoreCase("submit"))
			{
				Metrics formMetrics = metricsFormsCompleted.get(getHTM());
				formMetrics.increaseUnsuccess();
			}

		}
		else
		{
			// If current state does not contains a form, and it was previously in form filling mode
			// This means that a form was submitted
			if(formFillingWidget != null)
			{
				// Increase the filled form by 1
				Metrics formMetrics = metricsFormsCompleted.get(getHTM());
				if(formMetrics != null)
				{
					formMetrics.increaseCount();
				}
				// Check if submitted success by reading Error message in the state
				if(checkSuccessForm(state)) {
					formMetrics.increaseSuccess();
				} else {
					formMetrics.increaseUnsuccess();
				}

				// Also track the total count of filled forms
				metricsFormsCompleted.get("total").increaseCount();
			}
			// Finally set as null
			formFillingWidget = null;
		}
	}

	private boolean checkSuccessForm(State state) {
		for (Widget widget : state)
		{
			if(widget.get(WdTags.WebTextContent, "").contains("Error"))
			{
				return false;
			} 
		}
		return true;
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

		// If the web state contains a formFillingWidget, start a new mode that only focuses on deriving form filling actions.
		if(formFillingWidget != null)
		{
			// If we found a form, initialize the page htm as 0 value (only if absent)
			Metrics formMetrics = metricsFormsCompleted.get(getHTM());
			if(formMetrics == null)
			{
				metricsFormsCompleted.put(getHTM(), new Metrics());
			}

			Set<Action> formFillingActions = formFillingModeDeriveActions(formFillingWidget, ac);
			if(!formFillingActions.isEmpty()) return formFillingActions;
		}

		// Else, iterate through all widgets
		for (Widget widget : state)
		{
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
					if(widget.get(Tags.Role, Roles.Widget).equals(WdRoles.WdSELECT))
					{
						actions.add(randomFromSelectList(widget));
					}
					else if (!isLinkDenied(widget))
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
			formWidget.set(WdTags.WebId, getHTM()+".page.down"); // Ex: findtrans.htm.page.down
			pageDown.set(Tags.Role, ActionRoles.HitKeyScrollDownAction);
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
			if(widget.get(Tags.Role, Roles.Widget).equals(WdRoles.WdSELECT))
			{
				formFillingActions.add(randomFromSelectList(widget));
			}
			else
			{
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
		return !clickSet.isEmpty();
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
		// Input widgets that require a correct amount number
		if(widget.get(WdTags.WebName, "").contains("amount") 
				|| widget.get(WdTags.WebId, "").contains("amount")
				|| widget.get(WdTags.WebId, "").contains("downPayment"))
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
		// The Find Transactions page requires specific data for different inputs
		else if(widget.get(WdTags.WebId, "").contains("criteria.transactionId"))
		{
			return "12145";
		}
		else if(widget.get(WdTags.WebId, "").contains("Date"))
		{
			return "12-11-2022";
		}
		// Customer Care: contact.htm
		else if(isSonOfContactForm(widget))
		{
			return InputDataManager.getRandomAlphabeticInput(10);
		}

		return InputDataManager.getRandomTextInputData(widget);
	}

	private boolean isSonOfContactForm(Widget widget)
	{
		if(widget.parent() == null) return false;
		else if (widget.parent().get(Tags.Role, Roles.Widget).equals(WdRoles.WdFORM) 
				&& widget.parent().get(WdTags.WebName, "").equals("contact"))
		{
			return true;
		}
		else return isSonOfContactForm(widget.parent());
	}


	/**
	 * Randomly select one item from the select list widget. 
	 * 
	 * @param w
	 * @return
	 */
	private Action randomFromSelectList(Widget w)
	{
		int selectLength = 1;

		String elementId = w.get(WdTags.WebId, "");
		if(!elementId.isEmpty())
		{
			// Get the number of elements of the specific select list item
			try
			{
				String query = String.format("return document.getElementById('%s').length", elementId);
				Object response = WdDriver.executeScript(query);
				selectLength = ( response != null ? Integer.parseInt(response.toString()) : 1 );
				selectLength = new Random().nextInt(selectLength);
			}
			catch (Exception e)
			{
				System.out.println("*** ACTION WARNING: problems trying to obtain select list length: " + elementId);
			}

			return new WdSelectListAction(elementId, "", Integer.toString(selectLength), w);
		}

		String elementName = w.get(WdTags.WebName, "");
		if(!elementName.isEmpty())
		{
			// Get the number of elements of the specific select list item
			try
			{
				String query = String.format("return document.getElementsByName('%s')[0].length", elementName);
				Object response = WdDriver.executeScript(query);
				selectLength = ( response != null ? Integer.parseInt(response.toString()) : 1 );
				selectLength = new Random().nextInt(selectLength);
			}
			catch (Exception e)
			{
				System.out.println("*** ACTION WARNING: problems trying to obtain select list length: " + elementName);
			}

			return new WdSelectListAction("", elementName, Integer.toString(selectLength), w);
		}

		return new AnnotatingActionCompiler().leftClickAt(w);
	}

	@Override
	protected Action selectAction(State state, Set<Action> actions)
	{
		if(DefaultProtocol.lastExecutedAction != null)
		{
			state.set(Tags.PreviousAction, DefaultProtocol.lastExecutedAction);
			state.set(Tags.PreviousActionID, DefaultProtocol.lastExecutedAction.get(Tags.AbstractID, null));
		}

		// If we are in form filling mode and the strategy is not random,
		// Use the strategy to fill the form as a human
		Action selectedAction = (formFillingWidget != null && !strategyRandom) ?
				parseUtil.selectAction(state, actions, strategyActionsExecuted, operatingSystems):
					RandomActionSelector.selectRandomAction(actions);

		String actionID = selectedAction.get(Tags.AbstractID);

		//get the use count for the action
		List<Object> entry = strategyActionsExecuted.getOrDefault(actionID, null);

		int timesUsed = (entry == null) ? 0 : (Integer) entry.get(0); //default to zero if null
		ActionType actionType = (entry == null) ? ActionType.getActionType(selectedAction) : (ActionType) entry.get(1);

		ArrayList<Object> updatedEntry = new ArrayList<>();
		updatedEntry.add(timesUsed + 1); //increase usage by one
		updatedEntry.add(actionType);
		strategyActionsExecuted.replace(actionID, updatedEntry); //replace or create entry

		// If we are in form filling mode, initialize in the metrics the existing input elements
		if(formFillingWidget != null)
		{
			for(Action action : actions)
			{
				if(!action.get(Tags.OriginWidget).get(WdTags.WebType, "").equalsIgnoreCase("submit"))
				{
					// Update the input count metrics information
					Widget inputWidget = action.get(Tags.OriginWidget);
					// Get the web id of the input to identity it
					String inputId = inputWidget.get(WdTags.WebId, "");
					// If there is no web id, get the web name
					// Also, some web elements contain a dynamic web id we can not use to track the form input element
					// In this case, also use the web name (bill payment -> phone)
					if(inputId.isEmpty() || inputId.length() > 25) inputId = inputWidget.get(WdTags.WebName, "input");
					Metrics formMetricsHTM = metricsFormsCompleted.get(getHTM());
					formMetricsHTM.emptyInputInitialization(inputId);
				}
			}
		}

		return selectedAction;
	}

	@Override
	protected boolean executeAction(SUT system, State state, Action action)
	{
		// If we are in form filling mode and we executed an action that is not submit
		if(formFillingWidget != null
				&& !action.get(Tags.OriginWidget).get(WdTags.WebType, "").equalsIgnoreCase("submit"))
		{
			// Update the input count metrics information
			Widget inputWidget = action.get(Tags.OriginWidget);
			// Get the web id of the input to identity it
			String inputId = inputWidget.get(WdTags.WebId, "");
			// If there is no web id, get the web name
			// Also, some web elements contain a dynamic web id we can not use to track the form input element
			// In this case, also use the web name (bill payment -> phone)
			if(inputId.isEmpty() || inputId.length() > 25) inputId = inputWidget.get(WdTags.WebName, "input");
			Metrics formMetricsHTM = metricsFormsCompleted.get(getHTM());
			formMetricsHTM.addOrUpdateInputCount(inputId);
		}

		// If we are in form filling mode and last action was click in a submit button
		if(formFillingWidget != null 
				&& action.get(Tags.Role, ActionRoles.Action) == ActionRoles.LeftClickAt
				&& action.get(Tags.OriginWidget).get(WdTags.WebType, "").equalsIgnoreCase("submit"))
		{
			// Reset form actions because next time we need to fill completely again
			strategyActionsExecuted = new MultiMap<>();
			// Submit actions require extra time to load next state
			boolean executed = super.executeAction(system, state, action);
			Util.pause(5);
			return executed;
		}

		return super.executeAction(system, state, action);
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
		logFilledForms();
	}

	private void logFilledForms()
	{
		try
		{
			FileWriter myWriter = new FileWriter(Main.outputDir + File.separator + outerLoopName + File.separator + "log_filled_forms.txt", true);

			myWriter.write("---------- SEQUENCE " + sequenceCount() + "----------");
			myWriter.write(System.getProperty("line.separator"));

			for (Entry<String, Metrics> forms : metricsFormsCompleted.entrySet())
			{
				myWriter.write(forms.getKey() + " : " + forms.getValue().getFormCount() + " : " + Arrays.toString(forms.getValue().getFormActions()));
				myWriter.write(System.getProperty("line.separator"));
			}

			myWriter.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void closeTestSession()
	{
		super.closeTestSession();
		if(settings.get(ConfigTags.Mode).equals(Modes.Generate))
		{
//			compressOutputRunFolder();
			//copyOutputToNewFolderUsingIpAddress("N:");
		}
	}

	private String getHTM() {
		String formHTM = "failed";
		try {
			formHTM = new URL(WdDriver.getCurrentUrl()).getPath();
			formHTM = formHTM.substring(formHTM.lastIndexOf('/') + 1);
		}
		catch(MalformedURLException e)
		{
			e.printStackTrace();
		}
		return formHTM;
	}
}

class Metrics {
	private Integer formCount;
	// [{n_fill_field1, n_fill_field2, n_fill_field3}, num_succes_submit, num_unsucces_submit]
	private Object[] formActions;
	// {inputId=n_fill, inputId=n_fill, inputId=n_fill}
	private Map<String, Integer> formInputCount = new HashMap<>();

	public Metrics() {
		this.formCount = 0;
		this.formActions = new Object[3];
		this.formActions[0] = formInputCount;
		this.formActions[1] = 0;
		this.formActions[2] = 0;
	}

	public void increaseCount() {
		this.formCount ++;
	}

	public Integer getFormCount() {
		return formCount;
	}

	public void increaseSuccess() {
		int value = (int) formActions[1];
		formActions[1] = value + 1;
	}

	public void increaseUnsuccess() {
		int value = (int) formActions[2];
		formActions[2] = value + 1;
	}

	public Object[] getFormActions() {
		return formActions;
	}

	public void setFormActions(Object[] formActions) {
		this.formActions = formActions;
	}

	public Map<String, Integer> getInputs() {
		return formInputCount;
	}

	public void addOrUpdateInputCount(String inputId) {
		if(!formInputCount.containsKey(inputId)) {
			formInputCount.put(inputId, 1);
		} else {
			formInputCount.put(inputId, formInputCount.get(inputId) + 1);
		}
	}

	public void emptyInputInitialization(String inputId) {
		if(!formInputCount.containsKey(inputId)) {
			formInputCount.put(inputId, 0);
		}
	}
}
