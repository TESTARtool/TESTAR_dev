/**
 * Copyright (c) 2018 - 2022 Open Universiteit - www.ou.nl
 * Copyright (c) 2019 - 2022 Universitat Politecnica de Valencia - www.upv.es
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

import org.testar.CodingManager;
import org.testar.plugin.NativeLinker;
import org.testar.RandomActionSelector;
import org.testar.SutVisualization;

import nl.ou.testar.ReinforcementLearning.ActionSelectors.ProtocolAction;
import nl.ou.testar.ReinforcementLearning.ActionSelectors.ReinforcementLearningActionSelector;
import nl.ou.testar.ReinforcementLearning.Policies.Policy;
import nl.ou.testar.ReinforcementLearning.Policies.PolicyFactory;
import nl.ou.testar.ReinforcementLearning.ReinforcementLearningSettings;
import org.testar.statemodel.actionselector.ActionSelector;
import org.apache.commons.text.StringEscapeUtils;
import org.fruit.monkey.btrace.BtraceApiClient;
import org.fruit.monkey.weights.FileAnalysedMethodEntryRepository;
import org.fruit.monkey.weights.WeightCalculator;
import org.fruit.monkey.weights.WeightProvider;
import org.fruit.monkey.weights.WeightVerdict;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.*;
import org.testar.monkey.alayer.devices.KBKeys;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.NoSuchTagException;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.WdElement;
import org.testar.monkey.alayer.webdriver.WdWidget;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Settings;
import org.testar.OutputStructure;
import org.testar.protocols.WebdriverProtocol;
import org.testar.protocols.experiments.WriterExperiments;
import org.testar.protocols.experiments.WriterExperimentsParams;
import org.testar.extendedsettings.ExtendedSettingsFactory;

import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

import static org.testar.monkey.alayer.Tags.Blocked;
import static org.testar.monkey.alayer.Tags.Enabled;

import static org.testar.monkey.alayer.webdriver.Constants.scrollArrowSize;
import static org.testar.monkey.alayer.webdriver.Constants.scrollThick;

public class Protocol_yoho extends WebdriverProtocol {

	// Custom email for registration and login
	// private String email = "testar@testar.com";

	private ActionSelector actionSelector = null;
	private Policy policy = null;

	private RandomActionSelector<State, Action> randomActionSelector = new RandomActionSelector<>();

	protected WeightProvider weightProvider;
    private static final BigDecimal WEIGHT_VERDICT_THRESHOLD = new BigDecimal(40);

    private boolean weightProviderEnabled = false;
	private boolean jacocoReportingEnabled = false;

	@Override
	protected void buildStateIdentifiers(State state) {
		CodingManager.buildIDs(state);
		// Reset widgets AbstractIDCustom identifier values to empty
		for(Widget w : state) { w.set(Tags.AbstractIDCustom, ""); }
		// Custom the State AbstractIDCustom identifier
		customBuildAbstractIDCustom(state);
	}

//	protected void buildStateActionsIdentifiers(State state, Set<Action> actions) {
//		CodingManager.buildIDs(state, actions);
//		// Custom the State AbstractIDCustom identifier
//		customActionBuildAbstractIDCustom(actions);
//	}

	private synchronized void customActionBuildAbstractIDCustom(Set<Action> actions) {
		for (Action a : actions) {
			/* To create the AbstractIDCustom use:
			 * - AbstractIDCustom of the OriginWidget calculated with the selected abstract properties (core-StateManagementTags)
			 * - The ActionRole type of this action (LeftClick, DoubleClick, ClickTypeInto, Drag, etc)
			 */
			if(a.get(Tags.Role, ActionRoles.Action).equals(ActionRoles.CompoundAction) || a.get(Tags.Role, ActionRoles.Action).equals(ActionRoles.HitKey)) {
				System.out.println(a.get(Tags.Desc));
				System.out.println(a.get(Tags.Role, ActionRoles.Action));
				a.set(Tags.AbstractIDCustom, CodingManager.ID_PREFIX_ACTION + CodingManager.ID_PREFIX_ABSTRACT_CUSTOM +
						CodingManager.lowCollisionID(a.get(Tags.OriginWidget).get(Tags.AbstractIDCustom) + StringEscapeUtils.escapeHtml4(a.get(Tags.Desc, ""))));
			} else {
				a.set(Tags.AbstractIDCustom, CodingManager.ID_PREFIX_ACTION + CodingManager.ID_PREFIX_ABSTRACT_CUSTOM +
						CodingManager.lowCollisionID(a.get(Tags.OriginWidget).get(Tags.AbstractIDCustom) + a.get(Tags.Role, ActionRoles.Action)));
			}
		}
	}

	private synchronized void customBuildAbstractIDCustom(Widget widget){
		if (widget.parent() != null) {

			// Account bill direction may be dynamically changed in the exploration process
			// Ignore all properties to create widget id
			// http://localhost:8080/shop/customer/billing.html
			if(isSonOfBillAddressBox(widget) || isSonOfShopAddressBox(widget)) { return; }

			// dropdown widgets that come from fa-angle-down do not have interesting properties that differentiate them from other dropdowns
			if (widget.get(WdTags.WebCssClasses, "").contains("fa-angle-down")) {
				// Create the default String hash code using the abstract tags selected from the settings file (gh23483ghhk)
				// All dropdown widgets will potentially have this same hash identifier (gh23483ghhk)
				String dropdownWidgetAbstractId = CodingManager.codify(widget, CodingManager.getCustomTagsForAbstractId());
				// Obtain the parent WebTextContent that will help to differentiate one dropdown from others (Products)
				// This WebTextContent will help to differentiate dropdowns (Products, Account, ShopCart, etc)
				String parentDescription = widget.parent() != null ? widget.parent().get(WdTags.WebTextContent,"") : "";
				// Create a new String that contains the widget abstract id and the parent WebTextContent (gh23483ghhkProducts)
				String mergedAbstractId = dropdownWidgetAbstractId + parentDescription;
				// Then calculate the new hash id using new unique string (gh23483ghhkProducts)
				widget.set(Tags.AbstractIDCustom, CodingManager.ID_PREFIX_WIDGET + CodingManager.ID_PREFIX_ABSTRACT_CUSTOM + CodingManager.lowCollisionID(mergedAbstractId));
				// Also set new description using parent description
				// TODO: Maybe only set a new description and use Tags.Desc in the settings file
				widget.set(Tags.Desc, widget.get(Tags.Desc,"") + parentDescription);
				return;
			}

			// Register web page shows information about name, surname, password, etc.. in the text content of the registrationError web widget
			// Because this web text content is dynamic, use only web id for the abstract id
			if(widget.get(WdTags.WebId, "").equals("registrationError")) {
				widget.set(Tags.AbstractIDCustom, CodingManager.ID_PREFIX_WIDGET + CodingManager.ID_PREFIX_ABSTRACT_CUSTOM + CodingManager.codify(widget, WdTags.WebId));
				return;
			}

			// Same for edit address or change password form pages
			// http://localhost:8080/shop/customer/editAddress.html
			// http://localhost:8080/shop/customer/password.html
			if(widget.get(WdTags.WebId, "").equals("formError")) {
				widget.set(Tags.AbstractIDCustom, CodingManager.ID_PREFIX_WIDGET + CodingManager.ID_PREFIX_ABSTRACT_CUSTOM + CodingManager.codify(widget, WdTags.WebId));
				return;
			}

			// For shopping cart (number of bags) widget we should use only the id to avoid an state explosion
			if(widget.get(WdTags.WebId, "").equals("miniCartSummary")) {
				widget.set(Tags.AbstractIDCustom, CodingManager.ID_PREFIX_WIDGET + CodingManager.ID_PREFIX_ABSTRACT_CUSTOM + CodingManager.codify(widget, WdTags.WebId));
				return;
			}

			// Shopping cart widget that shows the price, is a dynamic widget that we have to ignore from the abstract id point of view
			if(widget.get(WdTags.WebCssClasses, "").contains("pull-left")) { return; }

			// Shopping cart may contains dynamic widgets if TESTAR buys stuff, we have to ignore these widgets from the abstract id point of view
			if(isSonOfCartItems(widget)) { return; }

			// In Shopizer we have two home href buttons, but they are leading to two different states
			// We need to use widget href in the abstraction level to differentiate them
			if(widget.get(WdTags.WebTextContent, "").contains("Inicio") || widget.get(WdTags.WebTextContent, "").contains("Home")) {
				widget.set(Tags.AbstractIDCustom, CodingManager.ID_PREFIX_WIDGET + CodingManager.ID_PREFIX_ABSTRACT_CUSTOM + CodingManager.codify(widget, WdTags.WebTextContent, WdTags.WebHref));
				return;
			}

			// For all widgets that are sons of My Account (customerAccount) widget
			// Use only the web id to build the AbstractIDCustom
			// TODO: Prob not a good idea because widgets from this dropdown are different, so this will provoke non determinism
//			if(isSonOfCustomerAccount(widget)) {
//				widget.set(Tags.AbstractIDCustom, CodingManager.ID_PREFIX_WIDGET + CodingManager.ID_PREFIX_ABSTRACT_CUSTOM + CodingManager.codify(widget, WdTags.WebId));
//				return;
//			}

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

	private boolean isSonOfBillAddressBox(Widget widget) {
		if(widget.parent() == null) return false;
		else if (widget.parent().get(WdTags.WebId, "").equals("editBillingAddress_100")) return true;
		else return isSonOfBillAddressBox(widget.parent());
	}

	private boolean isSonOfShopAddressBox(Widget widget) {
		if(widget.parent() == null) return false;
		else if (widget.parent().get(WdTags.WebId, "").equals("editShippingAddress_100")) return true;
		else return isSonOfShopAddressBox(widget.parent());
	}

	private boolean isSonOfCartItems(Widget widget) {
		if(widget.parent() == null) return false;
		else if (widget.parent().get(WdTags.WebId, "").equals("miniCartDetails")) return true;
		else return isSonOfCartItems(widget.parent());
	}

	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 * @param   settings  the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings){

		// Disconnect from Windows Remote Desktop, without close the GUI session
		// User will need to disable or accept UAC permission prompt message
		//disconnectRDP();

		//Create Abstract Model with Reinforcement Learning Implementation
		settings.set(ConfigTags.StateModelReinforcementLearningEnabled, "sarsaModelManager");

		// Extended settings framework, set ConfigTags settings with XML framework values
		// test.setting -> ExtendedSettingsFile
		ReinforcementLearningSettings rlXmlSetting = ExtendedSettingsFactory.createReinforcementLearningSettings();
		settings = rlXmlSetting.updateXMLSettings(settings);

		policy = PolicyFactory.getPolicy(settings);
		actionSelector = new ReinforcementLearningActionSelector(policy);

		super.initialize(settings);

		// Copy "bin/settings/protocolName/build.xml" file to "bin/jacoco/build.xml"
		if (jacocoReportingEnabled) {
			copyJacocoBuildFile();
		}

		startRunTime = System.currentTimeMillis();
        if (weightProviderEnabled) {
            weightProvider = new WeightProvider(
                    new FileAnalysedMethodEntryRepository(),
                    new BtraceApiClient(settings.get(ConfigTags.BtraceServiceHost)),
                    new WeightCalculator()
            );
        }

        // List of atributes to identify and close policy popups
        // Set to null to disable this feature
        policyAttributes = new HashMap<String, String>() {
            {
                put("id", "sncmp-banner-btn-agree");
            }
        };
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
		// Cookies button
		WdDriver.executeScript("document.getElementsByClassName('cc-btn cc-dismiss')[0].click();");
		Util.pause(1);
		// First time we load the page resources are not loaded correctly, we need to refresh the web page
		WdDriver.getRemoteWebDriver().navigate().refresh();

		// TODO: force Shopizer login?
		// http://aws-demo.shopizer.com/shop/customer/customLogon.html
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
		State state = super.getState(system);

		System.out.println("State AbstractIDCustom: " + state.get(Tags.AbstractIDCustom));

		for (Widget widget: state){
			if(widget.get(WdTags.WebTextContent, "").contains("HTTP Status 404") || widget.get(WdTags.WebTextContent, "").contains("An error occurred in the request")){
				WdDriver.executeScript("window.history.back();");
				Util.pause(1);
				state = super.getState(system);
			}
		}

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
    public Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {
        // Kill unwanted processes, force SUT to foreground
        Set<Action> actions = super.deriveActions(system, state);
        Set<Action> filteredActions = new HashSet<>();

        // create an action compiler, which helps us create actions
        // such as clicks, drag&drop, typing ...
        StdActionCompiler ac = new AnnotatingActionCompiler();

        // Check if forced actions are needed to stay within allowed domains
        Set<Action> forcedActions = detectForcedActions(state, ac);

        // iterate through all widgets
        for (Widget widget : state) {
            // only consider enabled and non-tabu widgets
            if (!widget.get(Enabled, true)) {
                continue;
            }
            // The blackListed widgets are those that have been filtered during the SPY mode
            // with the
            // CAPS_LOCK + SHIFT + Click clickfilter functionality.
            if (blackListed(widget)) {
                if (isTypeable(widget)) {
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
                if (whiteListed(widget) || isUnfiltered(widget)) {
                    actions.add(ac.clickTypeInto(widget, this.getRandomText(widget), true));
                } else {
                    // filtered and not white listed:
                    filteredActions.add(ac.clickTypeInto(widget, this.getRandomText(widget), true));
                }
            }

            // left clicks, but ignore links outside domain
            if (isAtBrowserCanvas(widget) && isClickable(widget)) {
                if (whiteListed(widget) || isUnfiltered(widget)) {
                    if (!isLinkDenied(widget) && !((widget instanceof WdWidget) && isActionUnwanted((WdWidget) widget))) {
                        actions.add(ac.leftClickAt(widget));
                    } else {
                        // link denied:
                        filteredActions.add(ac.leftClickAt(widget));
                    }
                } else {
                    // filtered and not white listed:
                    filteredActions.add(ac.leftClickAt(widget));
                }
            }
        }
	        // if(actions.isEmpty()) {
        // return new HashSet<>(Collections.singletonList(new WdHistoryBackAction()));
        // }

        // If we have forced actions, prioritize and filter the other ones
        if (forcedActions != null && forcedActions.size() > 0) {
            System.out.println("--- Forced actions available ---");
            filteredActions = actions;
            actions = forcedActions;
        }

        // Showing the grey dots for filtered actions if visualization is on:
        if (visualizationOn || mode() == Modes.Spy)
            SutVisualization.visualizeFilteredActions(cv, state, filteredActions);

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

	// /**
	//  * Create a specific action to fill the register user form. 
	//  * This only works if we are not logged all the sequence. 
	//  * 
	//  * @param state
	//  * @return
	//  */
	// private Action registrationFormFill(State state, Widget widget) {
	// 	// http://aws-demo.shopizer.com/shop/customer/registration.html
	// 	StdActionCompiler ac = new AnnotatingActionCompiler();
	// 	return new CompoundAction.Builder()
	// 			.add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "billing.firstName", state), "testar", true), 50)
	// 			.add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "billing.lastName", state), "testar", true), 50)
	// 			// Ignore country and state, use default values
	// 			.add(ac.pasteTextInto(getWidgetWithMatchingTag("name", "emailAddress", state), email, true), 50)
	// 			.add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "password", state), "testar", true), 50)
	// 			.add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "checkPassword", state), "testar", true), 50)
	// 			.add(ac.hitKey(KBKeys.VK_ENTER), 50)
	// 			.build(widget);
	// }

	// private Action loginFormFill(State state, Widget widget) {
	// 	// http://aws-demo.shopizer.com/shop/customer/customLogon.html
	// 	StdActionCompiler ac = new AnnotatingActionCompiler();
	// 	return new CompoundAction.Builder()
	// 			.add(ac.pasteTextInto(getWidgetWithMatchingTag("name", "signin_userName", state), email, true), 50)
	// 			.add(ac.clickTypeInto(getWidgetWithMatchingTag("name", "signin_password", state), "testar", true), 50)
	// 			.add(ac.leftClickAt(getWidgetWithMatchingTag("WebId", "genericLogin-button", state)), 50)
	// 			.build(widget);
	// }

	// /**
	//  * Randomly select one item from the select list widget. 
	//  * 
	//  * @param w
	//  * @return
	//  */
	// private Action randomFromSelectList(Widget w) {
	// 	int selectLength = 1;
	// 	String elementId = w.get(WdTags.WebId, "");
	// 	String elementName = w.get(WdTags.WebName, "");

	// 	// Get the number of values of the specific select list item
	// 	// Comment out because we are going to select always the first value of the select list
	// 	/*try {
    //         String query = String.format("return document.getElementById('%s').length", elementId);
    //         Object response = WdDriver.executeScript(query);
    //         selectLength = ( response != null ? Integer.parseInt(response.toString()) : 1 );
    //     } catch (Exception e) {
    //         System.out.println("*** ACTION WARNING: problems trying to obtain select list length: " + elementId);
    //     }*/

	// 	// Select one of the values randomly, or the first one if previous length failed
	// 	if(!elementId.isEmpty()) {
	// 		try {
	// 			//String query = String.format("return document.getElementById('%s').item(%s).value", elementId, new Random().nextInt(selectLength));
	// 			String query = String.format("return document.getElementById('%s').item(%s).value", elementId, selectLength);
	// 			Object response = WdDriver.executeScript(query);
	// 			return (response != null ?  new WdSelectListAction(elementId, response.toString(), w) : new AnnotatingActionCompiler().leftClickAt(w) );
	// 		} catch (Exception e) {
	// 			System.out.println("*** ACTION WARNING: problems trying randomly select a list value using WebId: " + elementId);
	// 			e.printStackTrace();
	// 		}
	// 	} else if(!elementName.isEmpty()) {
	// 		try {
	// 			String query = String.format("return document.getElementsByName('%s')[0].item(%s).value", elementName, selectLength);
	// 			Object response = WdDriver.executeScript(query);
	// 			return (response != null ?  new WdSelectListAction(elementName, response.toString(), w) : new AnnotatingActionCompiler().leftClickAt(w) );
	// 		} catch (Exception e) {
	// 			System.out.println("*** ACTION WARNING: problems trying randomly select a list value using WebName: " + elementName);
	// 			e.printStackTrace();
	// 		}
	// 	}

	// 	return new AnnotatingActionCompiler().leftClickAt(w);
	// }

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
	 * Select one of the available actions using a reinforcement learning action selection algorithm
	 *
	 * Normally super.selectAction(state, actions) updates information to the HTML sequence report, but since we
	 * overwrite it, not always running it, we have take care of the HTML report here
	 *
	 * @param state the SUT's current state
	 * @param actions the set of derived actions
	 * @return the selected action (non-null!)
	 */
	@Override
	public Action selectAction(final SUT system, final State state, final Set<Action> actions) {
		//Call the preSelectAction method from the DefaultProtocol so that, if necessary,
		//unwanted processes are killed and SUT is put into foreground.
		final Action preSelectedAction = preSelectAction(system, state, actions).stream().findAny().get();
		if (preSelectedAction != null) {
			return preSelectedAction;
		}
		Action modelAction = stateModelManager.getAbstractActionToExecute(actions);
		if(modelAction==null) {
			System.out.println("State model based action selection did not find an action. Using random action selection.");
			// if state model fails, use random (default would call preSelectAction() again, causing double actions HTML report):
			return randomActionSelector.selectAction(state, actions);
		}
		return modelAction;
	}

	protected Action getProtocolAction(final State state){
		Widget widget = getWidgetWithMatchingTag(Tags.Desc,"Products", state);
		if (widget == null){
			return null;
		}
		StdActionCompiler ac = new AnnotatingActionCompiler();
		ProtocolAction protocol = new ProtocolAction(ac.mouseMove(widget));
		protocol.set(Tags.OriginWidget, widget);
		protocol.set(Tags.Desc, "Shopizer Protocol Action");
		return protocol;
	}

	protected void getFullProtocolAction(final SUT system, final State state){
		State new_state = getState(system);
			List<String> options = Arrays.asList(
				"Compra / Venta",
				"Contactos",
				"Clases / Cursos",
				"Empleos",
				"Autos",
				"Servicios",
				"Vivienda",
				"Computadoras");
		Random rand = new Random();
		String randomElement = options.get(rand.nextInt(options.size()));
		Widget widget = getWidgetWithMatchingTag(WdTags.WebTextContent,randomElement, new_state);
//		if (widget == null){
//			return null;
//		}
		System.out.println(widget);
		StdActionCompiler ac = new AnnotatingActionCompiler();
		ac.leftClickAt(widget).run(system, new_state, 1);
//		ProtocolAction protocol = new ProtocolAction(ac.mouseMove(widget));
//		protocol.set(Tags.OriginWidget, widget);
//		protocol.set(Tags.Desc, "Shopizer Protocol Action");
//		return protocol;
	}

	/**
	 * Execute the selected action.
	 * Extract and create JaCoCo coverage report (After each action JaCoCo report will be created).
	 */
	@Override
	protected boolean executeAction(SUT system, State state, Action action){

        if (weightProviderEnabled) {
            weightProvider.startPreparingVerdict();
        }

		boolean actionExecuted = super.executeAction(system, state, action);

		if (action instanceof ProtocolAction){
			System.out.println("Finishing protocol action");
			getFullProtocolAction(system, state);
		}

		String information = "Sequence | " + OutputStructure.sequenceInnerLoopCount +
				" | actionnr | " + actionCount +
				" | url | " + WdDriver.getCurrentUrl();
		WriterExperiments.writeMetrics(new WriterExperimentsParams.WriterExperimentsParamsBuilder()
				.setFilename("urlData")
				.setInformation(information)
				.build());

		// Extract and create JaCoCo action coverage report for Generate Mode
		if(jacocoReportingEnabled && settings.get(ConfigTags.Mode).equals(Modes.Generate)) {
			extractJacocoActionReport();
		}

		return actionExecuted;
	}

	/**
	 * This method is invoked each time the TESTAR has reached the stop criteria for generating a sequence.
	 * This can be used for example for graceful shutdown of the SUT, maybe pressing "Close" or "Exit" button
	 */
	@Override
	protected void finishSequence() {

		// Extract and create JaCoCo sequence coverage report for Generate Mode
		if(jacocoReportingEnabled && settings.get(ConfigTags.Mode).equals(Modes.Generate)) {
			extractJacocoSequenceReport();
		}

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

		// This is the default JaCoCo generated file, we dumped our desired file with MBeanClient (finishSequence)
		// In this protocol this one is residual, so just delete
		if(jacocoReportingEnabled && new File("jacoco.exec").exists()) {
			System.out.println("Deleted residual jacoco.exec file ? " + new File("jacoco.exec").delete());
		}
	}

	/**
	 * This method is called after the last sequence, to allow for example handling the reporting of the session
	 */
	@Override
	protected void closeTestSession() {
		super.closeTestSession();
		// Extract and create JaCoCo run coverage report for Generate Mode
		if(jacocoReportingEnabled && settings.get(ConfigTags.Mode).equals(Modes.Generate)) {
			extractJacocoRunReport();
			compressOutputRunFolder();
			copyOutputToNewFolderUsingIpAddress("N:");
		}
	}


    @Override
    /*
     * Locate and press Ionic button with "Sign in" caption
     */
    protected Action generateCustomLoginAction(State state, WidgetActionCompiler ac) {
        for (Widget widget : state) {
            if (!WdWidget.class.isInstance(widget)) {
                continue;
            }
            WdWidget wdWidget = (WdWidget) widget;
            // System.out.println(String.format("??? %s ???",
            // wdWidget.get(WdTags.WebItemType)));
            if ("ion-button".equals(wdWidget.element.tagName) &&
                    wdWidget.element.textContent.toLowerCase(Locale.ROOT).contains("sign in")) {
                System.out.println("!!! Custom button found !!!");
                return ac.leftClickAt(wdWidget);
            }
        }
        return null;
    }

    @Override
    protected Verdict getVerdict(State state) {
        Verdict stateVerdict = super.getVerdict(state);
        if(weightProviderEnabled && weightProvider.isPreparing() && weightProvider.isStaticAnalysisAvailable()) {
            var weightVerdict = weightProvider.provideWeightVerdict();
            stateVerdict.join(deriveVerdict(weightVerdict));
        }
        return stateVerdict;
    }

    private Verdict deriveVerdict(WeightVerdict weightVerdict) {
        if(weightVerdict.getSeverityVerdict().compareTo(WEIGHT_VERDICT_THRESHOLD) >= 0) {
            return new Verdict(Verdict.SEVERITY_MAX,
                               String.format("Static analysis verdict: Static analysis score above threshold: %s",
                                             weightVerdict.getSeverityVerdict()));
        } else {
            return new Verdict(calculateSeverity(weightVerdict.getSeverityVerdict()),
                               String.format("Static analysis verdict: Static analysis under threshold: %s",
                                                                               weightVerdict.getSeverityVerdict()));
        }
    }

    private double calculateSeverity(BigDecimal weightVerdictSeverity) {
        return weightVerdictSeverity.divide(WEIGHT_VERDICT_THRESHOLD, MathContext.DECIMAL64)
                                    .doubleValue();

    }

    private boolean isActionUnwanted(WdWidget widget) {
        if (("yoho-avatar".equals(widget.get(WdTags.WebTagName)) && "true".equals(widget.getAttribute("ng-reflect-big"))) ||
        ("img".equals(widget.get(WdTags.WebTagName))) && "avatar-image".equals(widget.getAttribute("alt"))) {
            return true;
        }
        WdWidget parent = widget.parent();
        if (parent == null) {
            return false;
        }
        return isActionUnwanted(parent);
    }
}
