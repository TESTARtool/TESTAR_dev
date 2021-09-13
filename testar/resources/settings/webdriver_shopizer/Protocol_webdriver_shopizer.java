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
import org.fruit.monkey.Settings;
import org.testar.protocols.WebdriverProtocol;

import java.util.*;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;
import static org.fruit.alayer.webdriver.Constants.scrollArrowSize;
import static org.fruit.alayer.webdriver.Constants.scrollThick;

public class Protocol_webdriver_shopizer extends WebdriverProtocol {

    /**
     * Called once during the life time of TESTAR
     * This method can be used to perform initial setup work
     *
     * @param settings the current TESTAR settings as specified by the user.
     */
    @Override
    protected void initialize(Settings settings) {
        super.initialize(settings);

        // List of attributes to identify and close policy popups
        // Set to null to disable this feature
        policyAttributes = new HashMap<String, String>() {{
            put("class", "cc-dismiss");
        }};
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

        // TODO: prepare Shopizer login
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

            if(widget.get(WdTags.WebId, "").contains("registrationForm")) {
                actions.add(registrationFormFill(state));
            }

            // only consider enabled and non-tabu widgets
            if (!widget.get(Enabled, true) || blackListed(widget)) {
                continue;
            }

            // slides can happen, even though the widget might be blocked
            //addSlidingActions(actions, ac, scrollArrowSize, scrollThick, widget);

            // If the element is blocked, TESTAR can't click on or type in the widget
            if (widget.get(Blocked, false)) {
                continue;
            }

            // type into text boxes
            if (isAtBrowserCanvas(widget) && isTypeable(widget) && (whiteListed(widget) || isUnfiltered(widget)) ) {
                actions.add(ac.clickTypeInto(widget, getRandomShopizerData(widget), true));
            }

            // left clicks, but ignore links outside domain
            if (isAtBrowserCanvas(widget) && isClickable(widget) && !isLinkDenied(widget) && (whiteListed(widget) || isUnfiltered(widget)) ) {
                // Click on select web items opens the menu but does not allow TESTAR to select an item,
                // thats why we need a custom action selection
                if(widget.get(Tags.Role, Roles.Widget).equals(WdRoles.WdSELECT)) {
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
     * Get specific text data for shopizer text fields
     * 
     * @param w
     * @return
     */
    private String getRandomShopizerData(Widget w) {
        String[] example = {"aaaa", "1234", "01-01-2021"};
        if(w.get(WdTags.WebId, "").toLowerCase().contains("example")) {
            return example[new Random().nextInt(example.length)];
        }
        return this.getRandomText(w);
    }

    /**
     * Create a specific action to fill the register user form. 
     * This only works if we are not logged all the sequence. 
     * 
     * @param state
     * @return
     */
    private Action registrationFormFill(State state) {
        // http://aws-demo.shopizer.com:8080/shop/customer/registration.html
        String email = "testar" + new Random().nextInt(999) + "@testar.com";
        StdActionCompiler ac = new AnnotatingActionCompiler();
        return new CompoundAction.Builder()
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "billing.firstName", state), "testar", true), 50)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "billing.lastName", state), "testar", true), 50)
                // Ignore country and state, use default values
                .add(ac.pasteTextInto(getWidgetWithMatchingTag("name", "emailAddress", state), email, true), 50)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "password", state), "testar", true), 50)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "checkPassword", state), "testar", true), 50)
                .add(ac.leftClickAt(getWidgetWithMatchingTag("class", "login-btn", state)), 50)
                .build();
    }

    /**
     * Randomly select one item from the select list widget. 
     * 
     * @param w
     * @return
     */
    private Action randomFromSelectList(Widget w) {
        int selectLength = 1;
        String elementId = w.get(WdTags.WebId, "noIdDetected");
        // Get the number of values of the specific select list item
        try {
            String query = String.format("return document.getElementById('%s').length", elementId);
            Object response = WdDriver.executeScript(query);
            selectLength = ( response != null ? Integer.parseInt(response.toString()) : 1 );
        } catch (Exception e) {
            System.out.println("*** ACTION WARNING: problems trying to obtain select list length: " + elementId);
        }

        // Select one of the values randomly, or the first one if previous length failed
        try {
            String query = String.format("return document.getElementById('%s').item(%s).value", elementId, new Random().nextInt(selectLength));
            Object response = WdDriver.executeScript(query);
            return (response != null ?  new WdSelectListAction(elementId, response.toString(), w) : new AnnotatingActionCompiler().leftClickAt(w) );
        } catch (Exception e) {
            System.out.println("*** ACTION WARNING: problems trying randomly select a list value: " + elementId);
        }

        return new AnnotatingActionCompiler().leftClickAt(w);
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
}
