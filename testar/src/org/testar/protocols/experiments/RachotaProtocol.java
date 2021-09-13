/***************************************************************************************************
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
 *******************************************************************************************************/

package org.testar.protocols.experiments;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Set;

import org.fruit.Util;
import org.fruit.alayer.Action;
import org.fruit.alayer.Rect;
import org.fruit.alayer.Roles;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.CompoundAction;
import org.fruit.alayer.actions.KeyDown;
import org.fruit.alayer.actions.KeyUp;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.windows.UIATags;
import org.fruit.alayer.windows.WinProcess;
import org.testar.OutputStructure;
import org.testar.protocols.JavaSwingProtocol;

public class RachotaProtocol extends JavaSwingProtocol {

	// rachota: sometimes SUT stop responding, we need this empty actions countdown
	protected int countEmptyStateTimes = 0;

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
	}

	/**
	 * This method is called when the TESTAR requests the state of the SUT.
	 * Here you can add additional information to the SUT's state or write your
	 * own state fetching routine. The state should have attached an oracle
	 * (TagName: <code>Tags.OracleVerdict</code>) which describes whether the
	 * state is erroneous and if so why.
	 *
	 * super.getState(system) puts the state information also to the HTML sequence report
	 *
	 * @return  the current state of the SUT with attached oracle.
	 */
	@Override
	protected State getState(SUT system) throws StateBuildException {
		State state = super.getState(system);
		// rachota: check at windows level if SUT stops responding
		// Does not seems to work, at least with Rachota
		try {
			if(WinProcess.isHungWindow(system.get(Tags.HWND, (long)0))) {
				Verdict freezeVerdict = new Verdict(Verdict.SEVERITY_NOT_RESPONDING, "WinProcess.isHungWindow(system) Rachota stops responding");
				state.set(Tags.OracleVerdict, freezeVerdict);
			}
		} catch(Exception e) {
			System.out.println("Error trying to apply not responding verdict to system.get(Tags.HWND)");
		}
		return state;
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
			return new Verdict(Verdict.SEVERITY_NOT_RESPONDING, "Seems that rachota SUT is not responding");
		}

		return verdict;
	}

	/**
	 * Iterating through all widgets of the given state
	 *
	 * Adding derived actions into the given set of actions and returning the modified set of actions.
	 *
	 * @param actions
	 * @param system
	 * @param state
	 * @return
	 */
	protected Set<Action> deriveClickTypeScrollActionsFromAllWidgetsOfState(Set<Action> actions, State state){
		// To derive actions (such as clicks, drag&drop, typing ...) we should first create an action compiler.
		StdActionCompiler ac = new AnnotatingActionCompiler();

		// iterate through top level widgets
		for(Widget w : state){

			// rachota: add filename report
			if(w.get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIAEdit")
					&& w.get(Tags.Title,"").contains("Filename:")) {
				addFilenameReportAction(w, actions, ac);
			}

			if(w.get(Enabled, true) && !w.get(Blocked, false)){ // only consider enabled and non-blocked widgets

				if (!blackListed(w)){  // do not build actions for tabu widgets  

					// left clicks
					if((isClickable(w)) && (isUnfiltered(w) || whiteListed(w))) {
						actions.add(ac.leftClickAt(w));
					}

					// rachota: left clicks on specific widgets
					if((isEditToClickWidget(w) || isCalendarTextWidget(w)) && (isUnfiltered(w) || whiteListed(w))) {
						actions.add(ac.leftClickAt(w));
					}

					// left click in Table Cells
					if(isTableCell(w) && (isUnfiltered(w) || whiteListed(w))) {
						actions.add(ac.leftClickAt(w));
					}

					// rachota: use spinboxes
					if(isSpinBoxWidget(w) && (isUnfiltered(w) || whiteListed(w))) {
						addIncreaseDecreaseActions(w, actions, ac);
					}

					// type into text boxes
					// rachota: edit widgets have tooltiptext (isEditableWidget)
					if((isTypeable(w) && (isUnfiltered(w) || whiteListed(w))) && isEditableWidget(w)) {
						actions.add(ac.clickTypeInto(w, this.getRandomText(w), true));
					}

					// rachota: custom number for this generate reporting field
					if(w.get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIAEdit")
							&& w.get(Tags.Title,"").contains("Price per hour")) {
						forcePricePerHourAndFinish(w, actions, ac);
					}
					if(w.get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIAEdit")
							&& w.get(Tags.Title,"").contains("Tax:")) {
						actions.add(ac.clickTypeInto(w, "3", true));
					}

					/** Force actions on some widgets with a wrong accessibility **/
					// Optional feature, comment out this changes if your Swing applications doesn't need it

					// Tree List elements have plain "text" items have child nodes
					// We need to derive a click action on them
					if(w.get(Tags.Role).toString().contains("Tree")) {
						forceWidgetTreeClickAction(w, actions);
					}
					// Combo Box elements also have List Elements
					// Lists elements needs a special derivation to check widgets visibility
					if(w.get(Tags.Role).toString().contains("List")) {
						forceListElemetsClickAction(w, actions);
					}
					/** End of Force action **/
				}
			}
		}

		return actions;
	}

	/**
	 * Adding derived actions into the given set of actions and returning the modified set of actions.
	 *
	 * @param actions
	 * @param system
	 * @param state
	 * @return
	 */
	protected Set<Action> deriveClickTypeScrollActionsFromTopLevelWidgets(Set<Action> actions, State state){
		// To derive actions (such as clicks, drag&drop, typing ...) we should first create an action compiler.
		StdActionCompiler ac = new AnnotatingActionCompiler();

		// iterate through top level widgets
		for(Widget w : getTopWidgets(state)){

			// rachota: add filename report
			if(w.get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIAEdit")
					&& w.get(Tags.Title,"").contains("Filename:")) {
				addFilenameReportAction(w, actions, ac);
			}

			if(w.get(Enabled, true) && !w.get(Blocked, false)){ // only consider enabled and non-blocked widgets

				if (!blackListed(w)){  // do not build actions for tabu widgets  

					// left clicks
					if((isClickable(w)) && (isUnfiltered(w) || whiteListed(w))) {
						actions.add(ac.leftClickAt(w));
					}

					// rachota: left clicks on specific widgets
					if((isEditToClickWidget(w) || isCalendarTextWidget(w)) && (isUnfiltered(w) || whiteListed(w))) {
						actions.add(ac.leftClickAt(w));
					}

					// left click in Table Cells
					// but filtering duration table cell widgets
					if(isTableCell(w) && !isDurationTableCell(w) && (isUnfiltered(w) || whiteListed(w))) {
						actions.add(ac.leftClickAt(w));
					}

					// rachota: use spinboxes
					if(isSpinBoxWidget(w) && (isUnfiltered(w) || whiteListed(w))) {
						addIncreaseDecreaseActions(w, actions, ac);
					}

					// type into text boxes
					// rachota: edit widgets have tooltiptext (isEditableWidget)
					if((isTypeable(w) && (isUnfiltered(w) || whiteListed(w))) && isEditableWidget(w)) {
						actions.add(ac.clickTypeInto(w, this.getRandomText(w), true));
					}

					// rachota: custom number for this generate reporting field
					if(w.get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIAEdit")
							&& w.get(Tags.Title,"").contains("Price per hour")) {
						forcePricePerHourAndFinish(w, actions, ac);
					}
					if(w.get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIAEdit")
							&& w.get(Tags.Title,"").contains("Tax:")) {
						actions.add(ac.clickTypeInto(w, "3", true));
					}

					/** Force actions on some widgets with a wrong accessibility **/
					// Optional feature, comment out this changes if your Swing applications doesn't need it

					// Tree List elements have plain "text" items have child nodes
					// We need to derive a click action on them
					if(w.get(Tags.Role).toString().contains("Tree")) {
						forceWidgetTreeClickAction(w, actions);
					}
					// Combo Box elements also have List Elements
					// Lists elements needs a special derivation to check widgets visibility
					if(w.get(Tags.Role).toString().contains("List")) {
						forceListElemetsClickAction(w, actions);
					}
					/** End of Force action **/
				}
			}
		}

		return actions;
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
		Action addFilename = new CompoundAction.Builder()   
				.add(ac.clickTypeInto(filenameWidget, Util.dateString(OutputStructure.DATE_FORMAT), true), 0.5) // Click and type
				.add(new KeyDown(KBKeys.VK_TAB),0.5) // Press TAB keyboard
				.add(new KeyUp(KBKeys.VK_TAB),0.5) // Release Keyboard
				.add(ac.leftClickAt(nextButton), 0.5).build(); //Click next

		addFilename.set(Tags.Role, Roles.Button);
		addFilename.set(Tags.OriginWidget, filenameWidget);
		addFilename.set(Tags.Desc, "Add Filename Report");
		actions.add(addFilename);
	}

	/**
	 * Rachota:
	 * Force the input of a correct price and click finish to create the invoice report
	 */
	private void forcePricePerHourAndFinish(Widget priceWidget, Set<Action> actions, StdActionCompiler ac) {
		// Get Finish widget
		Widget finishButton = priceWidget;
		for(Widget checkWidget: priceWidget.root()) { 
			if(checkWidget.get(Tags.Title,"").contains("Finish")) {
				finishButton = checkWidget;
			}
		}

		// type price, use tab to complete the path, and click finish
		Action forcePriceFinish = new CompoundAction.Builder()   
				.add(ac.clickTypeInto(priceWidget, "3", true), 0.5) // Click and type
				.add(new KeyDown(KBKeys.VK_TAB),0.5) // Press TAB keyboard
				.add(new KeyUp(KBKeys.VK_TAB),0.5) // Release Keyboard
				.add(ac.leftClickAt(finishButton), 0.5).build(); //Click next

		forcePriceFinish.set(Tags.Role, Roles.Button);
		forcePriceFinish.set(Tags.OriginWidget, priceWidget);
		forcePriceFinish.set(Tags.Desc, "forcePriceAndFinish");
		actions.add(forcePriceFinish);
	}

	/**
	 * Rachota:
	 * Some Edit widgets allow click actions
	 */
	private boolean isEditToClickWidget(Widget w) {
		if(w.get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIAEdit")){
			return w.get(Tags.ToolTipText,"").contains("Mouse click");
		}
		return false;
	}

	/**
	 * Rachota:
	 * Seems that interactive Edit elements have tool type text with instructions
	 * Then if ToolTipText exists, the widget is interactive
	 * Disable price per hour random text, customize number
	 * Disable taxes prize, customize number
	 */
	private boolean isEditableWidget(Widget w) {
		String toolTipText = w.get(Tags.ToolTipText,"");
		return !toolTipText.trim().isEmpty() && !toolTipText.contains("text/plain") 
				&& !toolTipText.contains("Mouse click") &&
				!w.get(Tags.Title,"").contains("Price per hour") && !w.get(Tags.Title,"").contains("Tax:");
	}

	/**
	 * Rachota + Swing:
	 * Check if it is a Table Cell widget
	 */
	private boolean isTableCell(Widget w) {
		return w.get(UIATags.UIAAutomationId, "").contains("TableCell");
	}

	/**
	 * Rachota:
	 * Tricky way to check if current text widgets is a potential calendar number
	 */
	private boolean isCalendarTextWidget(Widget w) {
		if(w.parent() != null && w.parent().get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIAPane")) {
			int calendarDay = getNumericInt(w.get(Tags.Title, ""));
			if(0 < calendarDay && calendarDay < 31){
				return true;
			}
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
	 * Rachota + Swing:
	 * Check if it is a Spinner widget
	 */
	private boolean isSpinBoxWidget(Widget w) {
		return w.get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIASpinner");
	}

	/**
	 * Rachota + Swing
	 * We need to filter dynamic Task Duration Table Cell widget, 
	 * because prioritize actions is using Action Description to compare actions
	 */
	private boolean isDurationTableCell(Widget w) {
		if(isTableCell(w)) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
			String duration = w.get(Tags.Title, "noTitle");
			try {
				LocalTime time = LocalTime.parse(duration, formatter);
				// If parsed correctly, this is the duration widget we need to filter
				return true;
			} catch(DateTimeParseException | NullPointerException e) {
				// If error parsing, this is not the duration widget
				return false;
			}
		}
		return false;
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
	 * Iterate through the child of the specified widget to derive a click Action
	 */
	private void forceWidgetTreeClickAction(Widget w, Set<Action> actions) {
		StdActionCompiler ac = new AnnotatingActionCompiler();
		actions.add(ac.leftClickAt(w));
		w.set(Tags.ActionSet, actions);
		for(int i = 0; i<w.childCount(); i++) {
			forceWidgetTreeClickAction(w.child(i), actions);
		}
	}

	/**
	 * Derive a click Action on visible List dropdown elements
	 */
	private void forceListElemetsClickAction(Widget w, Set<Action> actions) {
		if(!Objects.isNull(w.parent())) {
			Widget parentContainer = w.parent();
			Rect visibleContainer = Rect.from(parentContainer.get(Tags.Shape).x(), parentContainer.get(Tags.Shape).y(),
					parentContainer.get(Tags.Shape).width(), parentContainer.get(Tags.Shape).height());

			forceComboBoxClickAction(w, visibleContainer, actions);
		}
	}

	/**
	 * Derive a click Action if widget rect bounds are inside the visible container
	 */
	private void forceComboBoxClickAction(Widget w, Rect visibleContainer, Set<Action> actions) {
		StdActionCompiler ac = new AnnotatingActionCompiler();
		try {
			Rect widgetContainer = Rect.from(w.get(Tags.Shape).x(), w.get(Tags.Shape).y(),
					w.get(Tags.Shape).width(), w.get(Tags.Shape).height());

			if(Rect.contains(visibleContainer, widgetContainer)) {
				actions.add(ac.leftClickAt(w));
				w.set(Tags.ActionSet, actions);
			}

			for(int i = 0; i<w.childCount(); i++) {
				forceComboBoxClickAction(w.child(i), visibleContainer, actions);
			}
		} catch(Exception e) {}
	}

	@Override
	protected boolean isUnfiltered(Widget w) {
		// Rachota: Filter Cancel button widgets if we are in the report generation window
		if(w.get(Tags.Title,"").equals("Cancel")) {
			for(Widget widget : w.root()) {
				if(widget.get(Tags.Title,"").contains("Report generation wizard")) {
					return false;
				}
			}
		}
		return super.isUnfiltered(w);
	}
}