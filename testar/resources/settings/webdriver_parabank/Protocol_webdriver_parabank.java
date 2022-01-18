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

import es.upv.staq.testar.CodingManager;
import es.upv.staq.testar.NativeLinker;
import nl.ou.testar.RandomActionSelector;

import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.*;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.webdriver.*;
import org.fruit.alayer.webdriver.enums.WdRoles;
import org.fruit.alayer.webdriver.enums.WdTags;
import org.testar.protocols.WebdriverProtocol;

import java.util.*;
import java.util.zip.CRC32;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;
import static org.fruit.alayer.webdriver.Constants.scrollArrowSize;
import static org.fruit.alayer.webdriver.Constants.scrollThick;

public class Protocol_webdriver_parabank extends WebdriverProtocol {

	@Override
	protected void buildStateIdentifiers(State state) {
		CodingManager.buildIDs(state);
		// Reset widgets AbstractIDCustom identifier values to empty
		for(Widget w : state) { w.set(Tags.AbstractIDCustom, ""); }
		// Custom the State AbstractIDCustom identifier to ignore Format menu bar widgets
		customBuildAbstractIDCustom(state);
	}

	private synchronized void customBuildAbstractIDCustom(Widget widget){
		if (widget.parent() != null) {
			// If the widget is a son of a WdSELECT widget, force to use static web id for the main menu but ignore options
			if(isSonOfSelect(widget)) {
				// Use web id property of main select menu <select id="transactionType" name="transactionType"/>
				if(!widget.get(WdTags.WebId, "").isEmpty()) {
					widget.set(Tags.AbstractIDCustom, CodingManager.ID_PREFIX_WIDGET + CodingManager.ID_PREFIX_ABSTRACT_CUSTOM + CodingManager.codify(widget, WdTags.WebId));
				}
				// <option value="January">January</option> or <option value="February">February</option> or <option value="March">March</option> or etc
				return;
			}

			// If the widget is a son of a WdTABLE widget, ignore when creating the state abstract id
			if(isSonOfTable(widget)) { return; }

			// FILTERED because is son of table
			// Bill pay, phone number widget has a dynamic WebId property, always force to use WebName
			if(widget.get(WdTags.WebName, "").equals("payee.phoneNumber")) { 
				widget.set(Tags.AbstractIDCustom, CodingManager.ID_PREFIX_WIDGET + CodingManager.ID_PREFIX_ABSTRACT_CUSTOM + CodingManager.codify(widget, WdTags.WebName));
				return;
			}
			// Open New Account creates a widget with dynamic text content and web name based on the new number, force to use static web id
			if(widget.get(WdTags.WebId, "").equals("newAccountId")) { 
				widget.set(Tags.AbstractIDCustom, CodingManager.ID_PREFIX_WIDGET + CodingManager.ID_PREFIX_ABSTRACT_CUSTOM + CodingManager.codify(widget, WdTags.WebId));
				return;
			}
			// Bill payment complete, contains a dynamic amount and account id in the state
			// Use the span element and web id to detect these dynamic values
			if(widget.get(Tags.Role, Roles.Widget).equals(WdRoles.WdSPAN) && 
					(widget.get(WdTags.WebId, "").equals("payeeName") || widget.get(WdTags.WebId, "").equals("amount") || widget.get(WdTags.WebId, "").equals("fromAccountId"))) {
				widget.set(Tags.AbstractIDCustom, CodingManager.ID_PREFIX_WIDGET + CodingManager.ID_PREFIX_ABSTRACT_CUSTOM + CodingManager.codify(widget, WdTags.WebId));
				return;
			}

			widget.set(Tags.AbstractIDCustom, CodingManager.ID_PREFIX_WIDGET + CodingManager.ID_PREFIX_ABSTRACT_CUSTOM + CodingManager.codify(widget, CodingManager.getCustomTagsForAbstractId()));

		} else if (widget instanceof State) {
			StringBuilder abstractIdCustom;
			abstractIdCustom = new StringBuilder();
			for (Widget childWidget : (State) widget) {
				if (childWidget != widget) {
					customBuildAbstractIDCustom(childWidget);
					abstractIdCustom.append(childWidget.get(Tags.AbstractIDCustom));
				}
			}
			widget.set(Tags.AbstractIDCustom, CodingManager.ID_PREFIX_STATE + CodingManager.ID_PREFIX_ABSTRACT_CUSTOM + CodingManager.lowCollisionID(abstractIdCustom.toString()));
		}
	}

	@Override
	protected void buildStateActionsIdentifiers(State state, Set<Action> actions) {
		CodingManager.buildIDs(state, actions);
		// Custom the Action AbstractIDCustom identifier
		for(Action a : actions) {
			if(a.get(Tags.OriginWidget) != null) {
				Widget widget = a.get(Tags.OriginWidget);
				if(isSonOfTable(widget)) {
					// If the widget of the action is a dynamic element of a table, consider the state id and widget role
					String customIdentifier = CodingManager.ID_PREFIX_ACTION + CodingManager.ID_PREFIX_ABSTRACT_CUSTOM + CodingManager.lowCollisionID(state.get(Tags.AbstractIDCustom) + widget.get(Tags.Role));
					a.set(Tags.AbstractIDCustom, customIdentifier);
				}
			}
		}
	}

	private boolean isSonOfTable(Widget widget) {
		if(widget.parent() == null) return false;
		else if (widget.parent().get(Tags.Role, Roles.Widget).equals(WdRoles.WdTABLE)) return true;
		else return isSonOfTable(widget.parent());
	}

	private boolean isSonOfSelect(Widget widget) {
		if(widget.parent() == null) return false;
		else if (widget.parent().get(Tags.Role, Roles.Widget).equals(WdRoles.WdSELECT)) return true;
		else return isSonOfSelect(widget.parent());
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

        // GUI login sequence 
        //waitLeftClickAndTypeIntoWidgetWithMatchingTag("name", "username", "john", state, system, 5, 1.0);
        //waitLeftClickAndTypeIntoWidgetWithMatchingTag("name", "password", "demo", state, system, 5, 1.0);
        //waitAndLeftClickWidgetWithMatchingTag("value", "Log In", state, system, 5, 1.0);
        //Util.pause(1);

        // Script login sequence
        WdDriver.executeScript("document.getElementsByName('username')[0].setAttribute('value','john');");
        WdDriver.executeScript("document.getElementsByName('password')[0].setAttribute('value','demo');");
        WdDriver.executeScript("document.getElementsByName('login')[0].submit();");
        Util.pause(1);
        //WdDriver.getRemoteWebDriver().get("http://localhost:8080/parabank/billpay.htm");
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
        
        State state = super.getState(system);

        System.out.println("State AbstractIDCustom: " + state.get(Tags.AbstractIDCustom));
        
        return state;
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

        // Triggered action https://para.testar.org/parabank/transfer.htm
        if(WdDriver.getCurrentUrl().contains("transfer.htm")) {
            Widget widget = getWidgetWithMatchingTag("WebId", "amount", state);
            if(widget != null) {
                // Correct
                actions.add(new CompoundAction.Builder()
                        .add(ac.clickTypeInto(widget, "100", true), 10)
                        .add(ac.hitKey(KBKeys.VK_ENTER), 10)
                        .build(widget));
                // Error
                actions.add(new CompoundAction.Builder()
                        .add(ac.clickTypeInto(widget, "string", true), 10)
                        .add(ac.hitKey(KBKeys.VK_ENTER), 10)
                        .build(widget));
            }
        }

        //Triggered action https://para.testar.org/parabank/requestloan.htm
        if(WdDriver.getCurrentUrl().contains("requestloan.htm")) {
            Widget widget = getWidgetWithMatchingTag("WebId", "amount", state);
            if(widget != null) {
                // Correct
                actions.add(new CompoundAction.Builder()
                        .add(ac.clickTypeInto(widget, "111", true), 10)
                        .add(ac.clickTypeInto(getWidgetWithMatchingTag("WebId", "downPayment", state), "222", true), 10)
                        .add(ac.hitKey(KBKeys.VK_ENTER), 10)
                        .build(widget));
                // Error
                actions.add(new CompoundAction.Builder()
                        .add(ac.clickTypeInto(widget, "aaa", true), 10)
                        .add(ac.clickTypeInto(getWidgetWithMatchingTag("WebId", "downPayment", state), "bbb", true), 10)
                        .add(ac.hitKey(KBKeys.VK_ENTER), 10)
                        .build(widget));
            }
        }

        // iterate through all widgets
        for (Widget widget : state) {

            // Triggered action for register if user is not logged
            if(widget.get(WdTags.WebId, "").contains("customerForm")) {
                actions.add(customerFormFill(state, widget));
            }

            // Triggered action https://para.testar.org/parabank/billpay.htm
            if(widget.get(WdTags.WebName, "").contains("payee.name")) {
                actions.add(paymentService(state, widget));
            }

            // Triggered action https://para.testar.org/parabank/findtrans.htm
            if(widget.get(WdTags.WebId, "").contains("criteria.transactionId")) {
                // Correct
                actions.add(new CompoundAction.Builder()
                        .add(ac.clickTypeInto(getWidgetWithMatchingTag("WebId", "criteria.transactionId", state), "12145", true), 10)
                        .add(ac.hitKey(KBKeys.VK_TAB), 10)
                        .add(ac.hitKey(KBKeys.VK_ENTER), 10)
                        .build(widget));
                // Error
                actions.add(new CompoundAction.Builder()
                        .add(ac.clickTypeInto(getWidgetWithMatchingTag("WebId", "criteria.transactionId", state), "1", true), 10)
                        .add(ac.hitKey(KBKeys.VK_TAB), 10)
                        .add(ac.hitKey(KBKeys.VK_ENTER), 10)
                        .build(widget));
            }
            if(widget.get(WdTags.WebId, "").contains("criteria.onDate")) {
                // Correct
                actions.add(new CompoundAction.Builder()
                        .add(ac.clickTypeInto(getWidgetWithMatchingTag("WebId", "criteria.onDate", state), "12-12-2020", true), 10)
                        .add(ac.hitKey(KBKeys.VK_TAB), 10)
                        .add(ac.hitKey(KBKeys.VK_ENTER), 10)
                        .build(widget));
                // Error
                actions.add(new CompoundAction.Builder()
                        .add(ac.clickTypeInto(getWidgetWithMatchingTag("WebId", "criteria.onDate", state), "1", true), 10)
                        .add(ac.hitKey(KBKeys.VK_TAB), 10)
                        .add(ac.hitKey(KBKeys.VK_ENTER), 10)
                        .build(widget));
            }
            if(widget.get(WdTags.WebId, "").contains("criteria.fromDate")) {
                // Correct
                actions.add(new CompoundAction.Builder()
                        .add(ac.clickTypeInto(getWidgetWithMatchingTag("WebId", "criteria.fromDate", state), "01-01-2020", true), 10)
                        .add(ac.clickTypeInto(getWidgetWithMatchingTag("WebId", "criteria.toDate", state), "09-09-2021", true), 10)
                        .add(ac.hitKey(KBKeys.VK_TAB), 10)
                        .add(ac.hitKey(KBKeys.VK_ENTER), 10)
                        .build(widget));
                // Error
                actions.add(new CompoundAction.Builder()
                        .add(ac.clickTypeInto(getWidgetWithMatchingTag("WebId", "criteria.fromDate", state), "01-01-2020", true), 10)
                        .add(ac.clickTypeInto(getWidgetWithMatchingTag("WebId", "criteria.toDate", state), "1", true), 10)
                        .add(ac.hitKey(KBKeys.VK_TAB), 10)
                        .add(ac.hitKey(KBKeys.VK_ENTER), 10)
                        .build(widget));
            }
            if(widget.get(WdTags.WebId, "").contains("criteria.amount")) {
                // Correct
                actions.add(new CompoundAction.Builder()
                        .add(ac.clickTypeInto(getWidgetWithMatchingTag("WebId", "criteria.transactionId", state), "100", true), 10)
                        .add(ac.hitKey(KBKeys.VK_TAB), 10)
                        .add(ac.hitKey(KBKeys.VK_ENTER), 10)
                        .build(widget));
                // Error
                actions.add(new CompoundAction.Builder()
                        .add(ac.clickTypeInto(getWidgetWithMatchingTag("WebId", "criteria.transactionId", state), "a", true), 10)
                        .add(ac.hitKey(KBKeys.VK_TAB), 10)
                        .add(ac.hitKey(KBKeys.VK_ENTER), 10)
                        .build(widget));
            }

            //Triggered action https://para.testar.org/parabank/updateprofile.htm
            if(widget.get(WdTags.WebId, "").contains("customer.lastName")) {
                // Correct
                actions.add(new CompoundAction.Builder()
                        .add(ac.clickTypeInto(getWidgetWithMatchingTag("WebId", "customer.lastName", state), "testar", true), 10)
                        .add(ac.hitKey(KBKeys.VK_ENTER), 10)
                        .build(widget));
                // Error
                actions.add(new CompoundAction.Builder()
                        .add(ac.pasteTextInto(getWidgetWithMatchingTag("WebId", "customer.lastName", state), "1234567890123456789012345678901234567890", true), 10)
                        .add(ac.hitKey(KBKeys.VK_ENTER), 10)
                        .build(widget));
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
                actions.add(ac.clickTypeInto(widget, getRandomParabankData(widget), true));
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
     * Get specific text data for parabank text fields
     * 
     * @param w
     * @return
     */
    private String getRandomParabankData(Widget w) {
        String[] dates = {"12-12-2020", "08-19-2021", "08-26-2021"};
        String[] amounts = {"100", "1000"};
        String[] transactions = {"12145", "14143", "13255", "12478", "13366", "12700"};
        String[] accounts = {"12345", "12456", "12567"};

        if(w.get(WdTags.WebId, "").toLowerCase().contains("amount") 
                || w.get(WdTags.WebId, "").toLowerCase().contains("pay") 
                || w.get(WdTags.WebName, "").toLowerCase().contains("amount")) {
            return amounts[new Random().nextInt(amounts.length)];
        }
        if(w.get(WdTags.WebId, "").toLowerCase().contains("date")) {
            return dates[new Random().nextInt(dates.length)];
        }
        if(w.get(WdTags.WebId, "").toLowerCase().contains("transaction")) {
            return transactions[new Random().nextInt(transactions.length)];
        }
        if(w.get(WdTags.WebName, "").toLowerCase().contains("account")) {
            return accounts[new Random().nextInt(accounts.length)];
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
    private Action customerFormFill(State state, Widget widget) {
        // https://para.testar.org/parabank/register.htm
        String username = "testar" + new Random().nextInt(999);
        StdActionCompiler ac = new AnnotatingActionCompiler();
        return new CompoundAction.Builder()
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "customer.firstName", state), "testar", true), 50)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "customer.lastName", state), "testar", true), 50)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "customer.address.street", state), "a", true), 50)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "customer.address.city", state), "a", true), 50)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "customer.address.state", state), "a", true), 50)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "customer.address.zipCode", state), "12345", true), 50)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "customer.phoneNumber", state), "123456789", true), 50)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "customer.ssn", state), "123456789", true), 50)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "customer.username", state), username, true), 50)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "customer.password", state), "testar", true), 50)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "repeatedPassword", state), "testar", true), 50)
                .add(ac.leftClickAt(getWidgetWithMatchingTag("value", "Register", state)), 50)
                .build(widget);
    }

    /**
     * Create a specific action to fill the register user form. 
     * This only works if we are not logged all the sequence. 
     * 
     * @param state
     * @return
     */
    private Action paymentService(State state, Widget widget) {
        StdActionCompiler ac = new AnnotatingActionCompiler();
        return new CompoundAction.Builder()
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "payee.name", state), "a", true), 50)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "payee.address.street", state), "a", true), 50)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "payee.address.city", state), "a", true), 50)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "payee.address.state", state), "a", true), 50)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "payee.address.zipCode", state), "12345", true), 50)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "payee.phoneNumber", state), "123456789", true), 50)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "payee.accountNumber", state), "54321", true), 50)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "verifyAccount", state), "54321", true), 50)
                .add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "amount", state), "111", true), 50)
                .add(ac.leftClickAt(getWidgetWithMatchingTag("value", "Send Payment", state)), 50)
                .build(widget);
    }

    /**
     * Randomly select one item from the select list widget. 
     * 
     * @param w
     * @return
     */
    private Action randomFromSelectList(Widget w) {
        int selectLength = 1;
        String elementId = w.get(WdTags.WebId, "");
        String elementName = w.get(WdTags.WebName, "");

        // Get the number of values of the specific select list item
        // Comment out because we are going to select always the first value of the select list
        /*try {
            String query = String.format("return document.getElementById('%s').length", elementId);
            Object response = WdDriver.executeScript(query);
            selectLength = ( response != null ? Integer.parseInt(response.toString()) : 1 );
        } catch (Exception e) {
            System.out.println("*** ACTION WARNING: problems trying to obtain select list length: " + elementId);
        }*/

        // Select one of the values randomly, or the first one if previous length failed
        if(!elementId.isEmpty()) {
        	try {
        		//String query = String.format("return document.getElementById('%s').item(%s).value", elementId, new Random().nextInt(selectLength));
        		String query = String.format("return document.getElementById('%s').item(%s).value", elementId, selectLength);
        		Object response = WdDriver.executeScript(query);
        		return (response != null ?  new WdSelectListAction(elementId, response.toString(), w) : new AnnotatingActionCompiler().leftClickAt(w) );
        	} catch (Exception e) {
        		System.out.println("*** ACTION WARNING: problems trying randomly select a list value using WebId: " + elementId);
        		e.printStackTrace();
        	}
        } else if(!elementName.isEmpty()) {
        	try {
        		String query = String.format("return document.getElementsByName('%s')[0].item(%s).value", elementName, selectLength);
        		Object response = WdDriver.executeScript(query);
        		return (response != null ?  new WdSelectListAction(elementName, response.toString(), w) : new AnnotatingActionCompiler().leftClickAt(w) );
        	} catch (Exception e) {
        		System.out.println("*** ACTION WARNING: problems trying randomly select a list value using WebName: " + elementName);
        		e.printStackTrace();
        	}
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
    		System.out.println("State model based action selection did not find an action. Using random action selection.");
    		// if state model fails, using random:
    		retAction = RandomActionSelector.selectAction(actions);
    	}
    	return retAction;
    }
}
