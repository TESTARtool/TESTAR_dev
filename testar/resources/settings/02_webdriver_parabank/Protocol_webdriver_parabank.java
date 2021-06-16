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

import es.upv.staq.testar.NativeLinker;
import nl.ou.testar.SutVisualization;

import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.*;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.webdriver.*;
import org.fruit.alayer.webdriver.enums.WdRoles;
import org.fruit.alayer.webdriver.enums.WdTags;
import org.fruit.monkey.Settings;
import org.testar.protocols.WebdriverProtocol;

import java.util.*;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;
import static org.fruit.alayer.webdriver.Constants.scrollArrowSize;
import static org.fruit.alayer.webdriver.Constants.scrollThick;


public class Protocol_webdriver_parabank extends WebdriverProtocol {

  /**
   * Called once during the life time of TESTAR
   * This method can be used to perform initial setup work
   *
   * @param settings the current TESTAR settings as specified by the user.
   */
  @Override
  protected void initialize(Settings settings) {
    super.initialize(settings);

    // List of atributes to identify and close policy popups
    // Set to null to disable this feature
    policyAttributes = new HashMap<String, String>() {{ put("class", "lfr-btn-label"); }};
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
   * This method is invoked each time the TESTAR starts the SUT to generate a new sequence.
   * This can be used for example for bypassing a login screen by filling the username and password
   * or bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
   * the SUT's configuration files etc.)
   */
  @Override
  protected void beginSequence(SUT system, State state) {
      super.beginSequence(system, state);

    // Add your login sequence here

/*
    waitLeftClickAndTypeIntoWidgetWithMatchingTag("name","username", "john", state, system, 5,1.0);

    waitLeftClickAndTypeIntoWidgetWithMatchingTag("name","password", "demo", state, system, 5,1.0);

    waitAndLeftClickWidgetWithMatchingTag("value", "Log In", state, system, 5, 1.0);
*/
	  
	/*
	 * If you have issues typing special characters
	 * 
	 * Try to use Paste Action with method:
	 * waitLeftClickAndPasteIntoWidgetWithMatchingTag
	 */

	// waitLeftClickAndPasteIntoWidgetWithMatchingTag("name", "username", "john", state, system, 5,1.0);
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
      // parabank wsdl pages have no widgets, we need to force a webdriver history back action
      if(WdDriver.getCurrentUrl().contains("wsdl")) {
          WdDriver.executeScript("window.history.back();");
          Util.pause(1);
      }

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

    for(Widget w : state) {
      if(w.get(WdTags.WebTextContent,"").contains("internal error")) {
        return new Verdict(Verdict.SEVERITY_SUSPICIOUS_TITLE,
                "Discovered suspicious widget 'Web Text Content' : '" + w.get(WdTags.WebTextContent,"") + "'.");
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

    // create an action compiler, which helps us create actions
    // such as clicks, drag&drop, typing ...
    StdActionCompiler ac = new AnnotatingActionCompiler();

    // Check if forced actions are needed to stay within allowed domains
    Set<Action> forcedActions = detectForcedActions(state, ac);

    // Add triggered actions here, before deriving the actions in a normal way:
      /*
      //Check if the trigger element is found:
      Widget triggerWidget = getWidgetWithMatchingTag("name","payee.name", state);
      if(triggerWidget!=null){
          // The element was found, create the triggered action and return it:
          // Creating a builder for a compound action that includes multiple actions as one item:
          CompoundAction.Builder multiAction = new CompoundAction.Builder();
          // Creating an action to type text into the Payee Name field:
          multiAction.add(ac.clickTypeInto(triggerWidget, "Triggered Payer Name", true),1.0);
          // Creating an action to type text into name="payee.address.street":
          multiAction.add(ac.clickTypeInto(getWidgetWithMatchingTag("name","payee.address.street", state),
                  "Triggered Payer Street", true),1.0);
          // Creating an action to type text into name="payee.address.city":
          multiAction.add(ac.clickTypeInto(getWidgetWithMatchingTag("name","payee.address.city", state),
                  "Triggered City", true),1.0);
          // Creating an action to type text into name="payee.address.state":
          multiAction.add(ac.clickTypeInto(getWidgetWithMatchingTag("name","payee.address.state", state),
                  "Triggered State", true),1.0);
          // Creating an action to type text into name="payee.zipCode":
          multiAction.add(ac.clickTypeInto(getWidgetWithMatchingTag("name","payee.address.zipCode", state),
                  "12345", true),1.0);
          // Creating an action to type text into name="payee.phoneNumber":
          multiAction.add(ac.clickTypeInto(getWidgetWithMatchingTag("name","payee.phoneNumber", state),
                  "123456789", true),1.0);
          // Creating an action to type text into name="payee.accountNumber":
          multiAction.add(ac.clickTypeInto(getWidgetWithMatchingTag("name","payee.accountNumber", state),
                  "12341234", true),1.0);
          // Creating an action to type text into name="verifyAccount":
          multiAction.add(ac.clickTypeInto(getWidgetWithMatchingTag("name","verifyAccount", state),
                  "12341234", true),1.0);
          // Creating an action to type text into name="amount":
          multiAction.add(ac.clickTypeInto(getWidgetWithMatchingTag("name","amount", state),
                  "100", true),1.0);
          // Creating a click action on Send Payment button, <input type="submit" class="button" value="Send Payment">
          multiAction.add(ac.leftClickAt(getWidgetWithMatchingTag("value","Send Payment", state)),1.0);
          // Adding the compound action into the actions that will be returned:
          actions.add(multiAction.build());
          // Returning actions having only the triggered action, before the normal derive actions:
          return actions;
      }
*/

    // iterate through all widgets
    for (Widget widget : state) {

    	// Skip Admin and logout page widget
    	if(widget.get(WdTags.WebHref,"").contains("admin.htm")
    			|| widget.get(WdTags.WebHref,"").contains("logout.htm")) {
    		continue;
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
      addSlidingActions(actions, ac, scrollArrowSize, scrollThick, widget);

      // If the element is blocked, Testar can't click on or type in the widget
      if (widget.get(Blocked, false) && !widget.get(WdTags.WebIsShadow, false)) {
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
                  actions.add(ac.leftClickAt(widget));
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

	if(actions.isEmpty()) {
		return new HashSet<>(Collections.singletonList(new WdHistoryBackAction()));
	}
	
	// If we have forced actions, prioritize and filter the other ones
	if (forcedActions != null && forcedActions.size() > 0) {
		filteredActions = actions;
		actions = forcedActions;
	}

	//Showing the grey dots for filtered actions if visualization is on:
    if(visualizationOn || mode() == Modes.Spy) SutVisualization.visualizeFilteredActions(cv, state, filteredActions);
    
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
}
