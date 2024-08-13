/***************************************************************************************************
 *
 * Copyright (c) 2017 - 2024 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2019 - 2024 Open Universiteit - www.ou.nl
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

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.testar.OutputStructure;
import org.testar.coverage.CodeCoverageManager;
import org.testar.managers.InputDataManager;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.*;
import org.testar.monkey.alayer.devices.KBKeys;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.alayer.exceptions.SystemStartException;
import org.testar.plugin.NativeLinker;
import org.testar.plugin.OperatingSystems;
import org.testar.protocols.DesktopProtocol;
import org.testar.settings.Settings;
import parsing.StrategyManager;

import static org.testar.monkey.alayer.Tags.Blocked;
import static org.testar.monkey.alayer.Tags.Enabled;

public class Protocol_desktop_swing_coverage extends DesktopProtocol {

	private CodeCoverageManager codeCoverageManager;
	private StrategyManager strategyManager;

	// rachota: sometimes SUT stop responding, we need this empty actions countdown
	protected int countEmptyStateTimes = 0;

	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 * @param   settings  the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings){
		super.initialize(settings);
		// Initialize the code coverage extractor using the test settings values
		codeCoverageManager = new CodeCoverageManager(settings);

		ArrayList<String> operatingSystems = new ArrayList<>();
		NativeLinker.getPLATFORM_OS().forEach(OS -> operatingSystems.add(OS.toString()));
		strategyManager = new StrategyManager(settings.get(ConfigTags.StrategyFile),operatingSystems);
	}

	/**
	 * This methods is called before each test sequence, allowing for example using external profiling software on the SUT
	 */
	@Override
	protected void preSequencePreparations() {
		super.preSequencePreparations();
		deleteRachotaConfig();
		try {
			// Create rachota settings configuration file, and disable detectInactivity feature
			File rachotaFile = new File("C:\\Users\\" + System.getProperty("user.name") + "\\.rachota");
			if(!rachotaFile.exists()) {
				rachotaFile.mkdirs();
			}
			File rachotaSettings = new File("C:\\Users\\" + System.getProperty("user.name") + "\\.rachota\\settings.cfg");
			if(rachotaSettings.createNewFile() || rachotaFile.exists()) {
				FileWriter settingsWriter = new FileWriter("C:\\Users\\" + System.getProperty("user.name") + "\\.rachota\\settings.cfg");
				settingsWriter.write("detectInactivity = false");
				settingsWriter.close();
			}
		} catch (Exception e) {
			System.out.println("ERROR trying to disable detectInactivity configuration feature");
		}
	}

	@Override
	protected SUT startSystem() throws SystemStartException
	{
		SUT system = super.startSystem();
		Util.pause(10);
        return system;
    }

	/**
	 * This method is invoked each time the TESTAR starts the SUT to generate a new sequence.
	 * This can be used for example for bypassing a login screen by filling the username and password
	 * or bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
	 * the SUT's configuration files etc.)
	 */
	@Override
	protected void beginSequence(SUT system, State state){
		// reset value
		countEmptyStateTimes = 0;

		// Before executing the first SUT action, extract the initial coverage
		codeCoverageManager.getActionCoverage("0");
		super.beginSequence(system, state);

		// rachota: predefined action to deal with initial pop-up question
		// If stopSystem configuration works (delete rachota folder) we will not need this
		for(Widget w : state) {
			if(w.get(Tags.Title,"").contains("Rachota is already running or it was not")) {
				waitAndLeftClickWidgetWithMatchingTag(Tags.Title, "Yes", state, system, 5, 1);
				// Wait and update the state
				Util.pause(10);
				state = super.getState(system);
			}
			// Dutch
			if(w.get(Tags.Title,"").contains("was de vorige keer niet normaal afgesloten")) {
				waitAndLeftClickWidgetWithMatchingTag(Tags.Title, "Ja", state, system, 5, 1);
				// Wait and update the state
				Util.pause(10);
				state = super.getState(system);
			}
		}

		strategyManager.beginSequence(state);
	}

	@Override
	protected State getState(SUT system) throws StateBuildException
	{
		State state = super.getState(system);
		return strategyManager.getState(state, latestState);
	}

	/**
	 * The getVerdict methods implements the online state oracles that
	 * examine the SUT's current state and returns an oracle verdict.
	 * @return oracle verdict, which determines whether the state is erroneous and why.
	 */
	@Override
	protected Verdict getVerdict(State state){
		Verdict verdict = super.getVerdict(state);

		if(countEmptyStateTimes > 2) {
			System.out.println("Seems that rachota SUT is not responding");
			return new Verdict(Verdict.SEVERITY_NOT_RESPONDING, "Seems that rachota SUT is not responding");
		}

		return verdict;
	}

	/**
	 * This method is used by TESTAR to determine the set of currently available actions.
	 * You can use the SUT's current state, analyze the widgets and their properties to create
	 * a set of sensible actions, such as: "Click every Button which is enabled" etc.
	 * The return value is supposed to be non-null. If the returned set is empty, TESTAR
	 * will stop generation of the current action and continue with the next one.
	 * @param system the SUT
	 * @param state the SUT's current state
	 * @return  a set of actions
	 */

	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException{

		Set<Action> actions = super.deriveActions(system,state);
		// unwanted processes, force SUT to foreground, ... actions automatically derived!

		// create an action compiler, which helps us create actions, such as clicks, drag&drop, typing ...
		StdActionCompiler ac = new AnnotatingActionCompiler();

		//----------------------
		// BUILD CUSTOM ACTIONS
		//----------------------

		// iterate through all widgets
		//for(Widget w : state){

		// iterate through the top widgets of the state (used for menu items)
		for(Widget w : getTopWidgets(state)){

			// rachota: add filename report
			if(w.get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIAEdit")
					&& w.get(Tags.Title,"").contains("Filename:")) {
				addFilenameReportAction(w, actions, ac);
			}

			if(w.get(Enabled, true) && !w.get(Blocked, false)){ // only consider enabled and non-blocked widgets

				if (!blackListed(w)){  // do not build actions for tabu widgets  

					// left clicks
					if(isClickable(w) && (isUnfiltered(w) || whiteListed(w))) {
						actions.add(ac.leftClickAt(w));
					}

					// rachota: use spinboxes
					if(isSpinBoxWidget(w) && (isUnfiltered(w) || whiteListed(w))) {
						addIncreaseDecreaseActions(w, actions, ac);
					}

					// rachota: left clicks on calendar number widgets
					if(isCalendarTextWidget(w) && (isUnfiltered(w) || whiteListed(w))) {
						actions.add(ac.leftClickAt(w));
					}

					// rachota: left clicks on edit widgets with tool tip indications
					if(isEditToClickWidget(w) && (isUnfiltered(w) || whiteListed(w))) {
						actions.add(ac.leftClickAt(w));
					}

					// type into text boxes
					if((isTypeable(w) && (isUnfiltered(w) || whiteListed(w))) && isEditableWidget(w)) {
						actions.add(ac.clickTypeInto(w, InputDataManager.getRandomTextInputData(w), true));
					}

					//Force actions on some widgets with a wrong accessibility
					//Optional, comment this changes if your Swing applications doesn't need it

					if(w.get(Tags.Role).toString().contains("Tree") ||
							w.get(Tags.Role).toString().contains("ComboBox") ||
							w.get(Tags.Role).toString().contains("List")) {
						widgetTree(w, actions);
					}
					//End of Force action
				}
			}
		}

		// rachota: sometimes rachota freezes, TESTAR detects the SUT
		// but cannot extract anything at JavaAccessBridge level
		// Use this count for Verdict
		if(actions.isEmpty())
		{
			System.out.println("Empty Actions on State!");
			countEmptyStateTimes++;
		}
		else
			countEmptyStateTimes = 0;

		return actions;

	}

	/**
	 * Rachota + Swing:
	 * Check if it is a Spinner widget
	 */
	private boolean isSpinBoxWidget(Widget w) {
		return w.get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIASpinner");
	}

	/**
	 * Rachota:
	 * Tricky way to check if current text widgets is a potential calendar number
	 */
	private boolean isCalendarTextWidget(Widget w) {
		if(w.parent() != null && w.parent().get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIAPane")) {
			int calendarDay = getNumericInt(w.get(Tags.Title, ""));
			if(0 < calendarDay && calendarDay <= 31){
				return true;
			}
		}
		return false;
	}

	/**
	 * Rachota:
	 * Some Edit widgets allow click actions (indicated in the tool tip text)
	 */
	private boolean isEditToClickWidget(Widget w) {
		if(w.get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIAEdit")){
			return w.get(Tags.ToolTipText,"").contains("Mouse click");
		}
		return false;
	}

	private int getNumericInt(String strNum) {
		if (strNum == null) {
			return -1;
		}
		try {
			return Integer.parseInt(strNum);
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}

	/**
	 * Rachota:
	 * Seems that interactive Edit elements have tool type text with instructions
	 * Then if ToolTipText exists, the widget is interactive
	 */
	private boolean isEditableWidget(Widget w) {
		String toolTipText = w.get(Tags.ToolTipText,"");
		return !toolTipText.trim().isEmpty() && !toolTipText.contains("text/plain") 
				&& !toolTipText.contains("Mouse click");
	}

	/**
	 * Rachota + Swing:
	 * SpinBox widgets buttons seems that do not exist as unique element,
	 * derive click + keyboard action to increase or decrease
	 */
	private void addIncreaseDecreaseActions(Widget w, Set<Action> actions, StdActionCompiler ac) {
		Action increase = new CompoundAction.Builder()
				.add(ac.leftClickAt(w), 0.5) // Click for focus
				.add(new KeyDown(KBKeys.VK_UP),0.5) // Press Up Arrow keyboard
				.add(new KeyUp(KBKeys.VK_UP),0.5).build(); // Release Keyboard

		increase.set(Tags.Role, Roles.Button);
		increase.set(Tags.OriginWidget, w);
		increase.set(Tags.Desc, "Increase Spinner");

		Action decrease = new CompoundAction.Builder()
				.add(ac.leftClickAt(w), 0.5) // Click for focus
				.add(new KeyDown(KBKeys.VK_DOWN),0.5) // Press Down Arrow keyboard
				.add(new KeyUp(KBKeys.VK_DOWN),0.5).build(); // Release Keyboard

		decrease.set(Tags.Role, Roles.Button);
		decrease.set(Tags.OriginWidget, w);
		decrease.set(Tags.Desc, "Decrease Spinner");

		actions.add(increase);
		actions.add(decrease);
	}

	/**
	 * Rachota:
	 * This SUT have the functionality of create invoices and reports
	 * Create an action that prepares a filename to create this report
	 */
	private void addFilenameReportAction(Widget filenameWidget, Set<Action> actions, StdActionCompiler ac) {

		// Get Next widget
		Widget nextButton = filenameWidget;
		for(Widget checkWidget: filenameWidget.root()) { 
			if(checkWidget.get(Tags.Title,"").contains("Next")) {
				nextButton = checkWidget;
			}
		}

		// type filename, use tab to complete the path, and click next
		Action typeDate = ac.clickTypeInto(filenameWidget, Util.dateString(OutputStructure.DATE_FORMAT), true);
		Action addFilename = new CompoundAction.Builder()   
				.add(typeDate, 0.5) // Click and type
				.add(new KeyDown(KBKeys.VK_TAB),0.5) // Press TAB keyboard
				.add(new KeyUp(KBKeys.VK_TAB),0.5) // Release Keyboard
				.add(ac.leftClickAt(nextButton), 0.5).build(); //Click next

		addFilename.set(Tags.Role, Roles.Button);
		addFilename.set(Tags.OriginWidget, filenameWidget);
		addFilename.set(Tags.Desc, "Add Filename Report");
		addFilename.set(Tags.Visualizer, typeDate.get(Tags.Visualizer));
		actions.add(addFilename);
	}

	/**
	 * SwingSet2 application contains a TabElement called "SourceCode"
	 * that internally contains UIAEdit widgets that are not modifiable.
	 * Because these widgets have the property ToolTipText with the value "text/html",
	 * use this Tag to recognize and ignore.
	 */
	private boolean isSourceCodeEditWidget(Widget w) {
		return w.get(Tags.ToolTipText, "").contains("text/html");
	}

	//Force actions on Tree widgets with a wrong accessibility
	public void widgetTree(Widget w, Set<Action> actions) {
		StdActionCompiler ac = new AnnotatingActionCompiler();
		actions.add(ac.leftClickAt(w));
		w.set(Tags.ActionSet, actions);
		for(int i = 0; i<w.childCount(); i++) {
			widgetTree(w.child(i), actions);
		}
	}

	@Override
	protected Action selectAction(State state, Set<Action> actions)
	{
		return strategyManager.selectAction(state, actions);
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
	protected boolean executeAction(SUT system, State state, Action action){
		boolean executed = super.executeAction(system, state, action);
		// After executing the SUT action, extract the action coverage
		codeCoverageManager.getActionCoverage(String.valueOf(actionCount));
		strategyManager.recordSelectedAction(state, action);
		return executed;
	}

	/**
	 * This method is invoked each time after TESTAR finished the generation of a sequence.
	 */
	@Override
	protected void finishSequence() {
		// Before finishing the sequence and closing the SUT, extract the sequence coverage
		codeCoverageManager.getSequenceCoverage();
		super.finishSequence();
	}

	/**
	 * This methods stops the SUT
	 *
	 * @param system
	 */
	@Override
	protected void stopSystem(SUT system) {
		super.stopSystem(system);
		deleteRachotaConfig();
	}

	/**
	 * Delete rachota files to have same initial state without tasks
	 */
	private void deleteRachotaConfig() {
		String rachotaPath = "C:\\Users\\" + System.getProperty("user.name") + "\\.rachota";

		if(new File(rachotaPath).exists()) {
			try {
				FileUtils.deleteDirectory(new File(rachotaPath));
			} catch(Exception e) {System.out.println("ERROR deleting rachota folder");}
		}
	}
}
