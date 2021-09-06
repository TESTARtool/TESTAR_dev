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
import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.*;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.webdriver.*;
import org.fruit.alayer.webdriver.enums.WdRoles;
import org.fruit.alayer.webdriver.enums.WdTags;
import org.testar.protocols.WebdriverProtocol;

import java.util.*;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;
import static org.fruit.alayer.webdriver.Constants.scrollArrowSize;
import static org.fruit.alayer.webdriver.Constants.scrollThick;

public class Protocol_webdriver_parabank extends WebdriverProtocol {

    /**
     * This method is invoked each time the TESTAR starts the SUT to generate a new sequence.
     * This can be used for example for bypassing a login screen by filling the username and password
     * or bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
     * the SUT's configuration files etc.)
     */
    @Override
    protected void beginSequence(SUT system, State state) {
        super.beginSequence(system, state);

        // login sequence 
        waitLeftClickAndTypeIntoWidgetWithMatchingTag("name","username", "john", state, system, 5,1.0);
        waitLeftClickAndTypeIntoWidgetWithMatchingTag("name","password", "demo", state, system, 5,1.0);
        waitAndLeftClickWidgetWithMatchingTag("value", "Log In", state, system, 5, 1.0);
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
        if(WdDriver.getCurrentUrl().contains("wsdl") || WdDriver.getCurrentUrl().contains("wadl")) {
            WdDriver.executeScript("window.history.back();");
            Util.pause(1);
        }

        return super.getState(system);
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

        Set<Action> forcedActions = detectForcedActions(state, ac);
        if (forcedActions != null && forcedActions.size() > 0) {
            return forcedActions;
        }

        // iterate through all widgets
        for (Widget widget : state) {

            if(widget.get(WdTags.WebId, "").contains("customerForm")) {
                actions.add(customerFormFill(state));
            }
            if(widget.get(Tags.Title, "").toLowerCase().trim().contains("billpaymentservice")) {
                actions.add(paymentService(state, widget));
            }

            // only consider enabled and non-tabu widgets
            if (!widget.get(Enabled, true) || blackListed(widget)) {
                continue;
            }

            // slides can happen, even though the widget might be blocked
            addSlidingActions(actions, ac, scrollArrowSize, scrollThick, widget);

            // If the element is blocked, TESTAR can't click on or type in the widget
            if (widget.get(Blocked, false)) {
                continue;
            }

            // type into text boxes
            if (isAtBrowserCanvas(widget) && isTypeable(widget) && (whiteListed(widget) || isUnfiltered(widget)) ) {
                actions.add(ac.clickTypeInto(widget, getRandomParabankData(widget), true));
            }

            // left clicks, but ignore links outside domain
            if (isAtBrowserCanvas(widget) && isClickable(widget) && !isLinkDenied(widget) && (whiteListed(widget) || isUnfiltered(widget)) ) {
                // Click on select web items opens the menu but does not allow TESTAR to select an item,
                // thats why we need a custom action selection
                if(widget.get(Tags.Role).equals(WdRoles.WdSELECT)) {
                    actions.add(randomFromSelectList(widget));
                } else {
                    actions.add(ac.leftClickAt(widget));
                }
            }
        }

        if(actions.isEmpty()) {
            return new HashSet<>(Collections.singletonList(new WdHistoryBackAction()));
        }

        return actions;
    }

    /**
     * Get specific text data for parabank text fields
     * 
     * @param w
     * @return
     */
    private String getRandomParabankData(Widget w) {
        String[] dates = {"12-12-2020", "08-19-2021", "08-26-2021"};
        String[] amounts = {"100", "1000"};
        if(w.get(WdTags.WebId, "").toLowerCase().contains("amount") || w.get(WdTags.WebId, "").toLowerCase().contains("pay")) {
            return amounts[new Random().nextInt(amounts.length)];
        }
        if(w.get(WdTags.WebId, "").toLowerCase().contains("date")) {
            return dates[new Random().nextInt(dates.length)];
        }
        return this.getRandomText(w);
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
     * Create a specific action to fill the register user form. 
     * This only works if we are not logged all the sequence. 
     * 
     * @param state
     * @return
     */
    private Action customerFormFill(State state) {
        String username = "testar" + new Random().nextInt(999);
        StdActionCompiler ac = new AnnotatingActionCompiler();
        return new CompoundAction.Builder()
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "customer.firstName", state), "testar", true), 0.5)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "customer.lastName", state), "testar", true), 0.5)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "customer.address.street", state), "a", true), 0.5)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "customer.address.city", state), "a", true), 0.5)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "customer.address.state", state), "a", true), 0.5)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "customer.address.zipCode", state), "12345", true), 0.5)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "customer.phoneNumber", state), "123456789", true), 0.5)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "customer.ssn", state), "123456789", true), 0.5)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "customer.username", state), username, true), 0.5)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "customer.password", state), "testar", true), 0.5)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "repeatedPassword", state), "testar", true), 0.5)
                .add(ac.leftClickAt(getWidgetWithMatchingTag("value", "Register", state)), 0.5)
                .build();
    }
    
    /**
     * Create a specific action to fill the register user form. 
     * This only works if we are not logged all the sequence. 
     * 
     * @param state
     * @return
     */
    private Action paymentService(State state, Widget w) {
        StdActionCompiler ac = new AnnotatingActionCompiler();
        return new CompoundAction.Builder()
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "payee.name", state), "a", true), 0.5)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "payee.address.street", state), "a", true), 0.5)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "payee.address.city", state), "a", true), 0.5)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "payee.address.state", state), "a", true), 0.5)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "payee.address.zipCode", state), "12345", true), 0.5)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "payee.phoneNumber", state), "123456789", true), 0.5)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "payee.accountNumber", state), "54321", true), 0.5)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "verifyAccount", state), "54321", true), 0.5)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "amount", state), "111", true), 0.5)
                .add(randomFromSelectList(w), 0.5)
                .build();
    }

    /**
     * Randomly select one item from the select list widget. 
     * 
     * @param w
     * @return
     */
    private Action randomFromSelectList(Widget w) {
        int selectLength = 0;
        String elementId = w.get(WdTags.WebId, "noIdDetected");
        // Get the number of values of the specific select list item
        try {
            String query = String.format("document.getElementById('%s').length;", elementId);
            Object response = WdDriver.executeScript(query);
            selectLength = Integer.parseInt(response.toString());
        } catch (Exception e) {
            System.out.println("*** ACTION WARNING: problems trying to obtain select list length: " + elementId);
        }

        // Select one of the values randomly, or the first one if previous length failed
        try {
            String query = String.format("document.getElementById('%s').item(%s).value;", elementId, new Random().nextInt(selectLength));
            Object response = WdDriver.executeScript(query);
            String selectValue = response.toString();
            return new WdSelectListAction(elementId, selectValue);
        } catch (Exception e) {
            System.out.println("*** ACTION WARNING: problems trying randomly select a list value: " + elementId);
        }

        return new NOP();
    }
}
