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

import es.upv.staq.testar.CodingManager;
import es.upv.staq.testar.NativeLinker;
import es.upv.staq.testar.serialisation.LogSerialiser;
import nl.ou.testar.RandomActionSelector;
import nl.ou.testar.ReinforcementLearning.ActionSelectors.ReinforcementLearningActionSelector;
import nl.ou.testar.ReinforcementLearning.Policies.Policy;
import nl.ou.testar.ReinforcementLearning.Policies.PolicyFactory;
import nl.ou.testar.ReinforcementLearning.RLTags;
import nl.ou.testar.ReinforcementLearning.ReinforcementLearningSettings;
import nl.ou.testar.StateModel.ActionSelection.ActionSelector;
import org.apache.commons.text.StringEscapeUtils;
import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.*;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.webdriver.WdDriver;
import org.fruit.alayer.webdriver.WdElement;
import org.fruit.alayer.webdriver.WdWidget;
import org.fruit.alayer.webdriver.enums.WdRoles;
import org.fruit.alayer.webdriver.enums.WdTags;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.testar.OutputStructure;
import org.testar.protocols.WebdriverProtocol;
import org.testar.protocols.experiments.WriterExperiments;
import org.testar.protocols.experiments.WriterExperimentsParams;
import org.testar.settings.ExtendedSettingsFactory;

import java.util.HashSet;
import java.util.Set;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;

public class Protocol_webdriver_bol_reinforcement_learning extends WebdriverProtocol {

	// Custom email for registration and login
	private String email = "testar@testar.com";

	private ActionSelector actionSelector = null;
	private Policy policy = null;



	protected void buildStateActionsIdentifiers(State state, Set<Action> actions) {
		CodingManager.buildIDs(state, actions);
		// Custom the State AbstractIDCustom identifier
		customActionBuildAbstractIDCustom(actions);
	}

	private synchronized void customActionBuildAbstractIDCustom(Set<Action> actions) {
		for (Action a : actions) {
			/* To create the AbstractIDCustom use:
			 * - AbstractIDCustom of the OriginWidget calculated with the selected abstract properties (core-StateManagementTags)
			 * - The ActionRole type of this action (LeftClick, DoubleClick, ClickTypeInto, Drag, etc)
			 */

			if(a instanceof WdHistoryBackAction || a instanceof WdCloseTabAction) continue;
			if(a.get(Tags.Role, ActionRoles.Action).equals(ActionRoles.CompoundAction) || a.get(Tags.Role, ActionRoles.Action).equals(ActionRoles.HitKey)) {
				a.set(Tags.AbstractIDCustom, CodingManager.ID_PREFIX_ACTION + CodingManager.ID_PREFIX_ABSTRACT_CUSTOM +
						CodingManager.lowCollisionID(a.get(Tags.OriginWidget).get(Tags.AbstractIDCustom) + StringEscapeUtils.escapeHtml4(a.get(Tags.Desc, ""))));
			} else {
				if (a instanceof WdNavigateTo){
					System.out.println("--------------------");
					System.out.println(a.get(Tags.OriginWidget).get(Tags.AbstractIDCustom));
				}
				a.set(Tags.AbstractIDCustom, CodingManager.ID_PREFIX_ACTION + CodingManager.ID_PREFIX_ABSTRACT_CUSTOM +
						CodingManager.lowCollisionID(a.get(Tags.OriginWidget).get(Tags.AbstractIDCustom) + a.get(Tags.Role, ActionRoles.Action)));
				if (a instanceof WdNavigateTo){
					System.out.println(a.get(Tags.AbstractIDCustom));
					System.out.println(a.get(RLTags.ExCounter, -1));
					System.out.println("Q-Value: " + a.get(RLTags.QLearningValue, -1f));
					System.out.println("--------------------");
				}
			}
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
		copyJacocoBuildFile();

		startRunTime = System.currentTimeMillis();
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

		waitAndLeftClickWidgetWithMatchingTag(WdTags.WebTextContent, "Alles accepteren", state, system, 5, 1);

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
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {
		// Kill unwanted processes, force SUT to foreground
		Set<Action> actions = super.deriveActions(system, state);
		Set<Action> filteredActions = new HashSet<>();

		// create an action compiler, which helps us create actions
		// such as clicks, drag&drop, typing ...
		StdActionCompiler ac = new AnnotatingActionCompiler();

		Set<Action> forcedActions = detectForcedActions(state, ac);
		if (forcedActions != null && forcedActions.size() > 0) {
			return forcedActions;
		}

		// iterate through all widgets
		for (Widget widget : state) {

			if(widget.get(Tags.Role, Roles.Widget).equals(WdRoles.WdSELECT)){
				continue;
			}

		    // fill forms actions
		    if (isAtBrowserCanvas(widget) && isForm(widget)) {
		    	String protocol = settings.get(ConfigTags.ProtocolClass, "");
		        Action formFillingAction = new WdFillFormAction(ac, widget, protocol.substring(0, protocol.lastIndexOf('/')));
		        if(((WdFillFormAction)formFillingAction).isHiddenForm()) {
		            System.out.println("DEBUG: we derive a NOP action, but lets ignore");
		            // do nothing with NOP actions - the form was not actionable
		        } else {
		            System.out.println("DEBUG: form action found: ");
//		            actions.add(formFillingAction);
		        }
		    }

			// only consider enabled and non-tabu widgets
			if (!widget.get(Enabled, true) || blackListed(widget)) {
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

//			 slides can happen, even though the widget might be blocked
//			addSlidingActions(actions, ac, scrollArrowSize, scrollThick, widget);

			// If the element is blocked, TESTAR can't click on or type in the widget
			if (widget.get(Blocked, false)) {
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
//						if (widget.get(WdTags.WebCssClasses, "").contains("listing-link") ||
//								widget.get(WdTags.WebCssClasses, "").contains("wt-arrow-link--forward"))
//						{
////							filteredActions.add(ac.leftClickAt(widget));
//							String URL = widget.get(WdTags.WebHref, null);
//							if (URL != null) {
//								Action goToLink = new WdNavigateTo(URL);
//								goToLink.set(Tags.OriginWidget, widget);
//								actions.add(goToLink);
//							}
//						}
//						else {

							actions.add(ac.leftClickAt(widget));
//						}
					}else{
						// link denied:
						filteredActions.add(ac.leftClickAt(widget));
					}
				}else{
					// filtered and not white listed:
					filteredActions.add(ac.leftClickAt(widget));
				}
			}
//		}
			// left clicks, but ignore links outside domain
//			if (isAtBrowserCanvas(widget) && isClickable(widget) && !isLinkDenied(widget) && (whiteListed(widget) || isUnfiltered(widget)) ) {
//				System.out.println("-------------------- 2");
//				System.out.println(widget.get(WdTags.Desc, ""));
//				// Click on select web items opens the menu but does not allow TESTAR to select an item,
//				// thats why we need a custom action selection
//				if(widget.get(Tags.Role, Roles.Widget).equals(WdRoles.WdSELECT) || widget.get(WdTags.WebCssClasses, "").contains("hidden-xs")) {
//					System.out.println(widget.get(WdTags.Desc, ""));
//					//actions.add(randomFromSelectList(widget));
//				} else {
//						actions.add(ac.leftClickAt(widget));
//				}
//			}
		}

		// TODO: Check how this affects the Shared Algorithm, move to default derived actions
//		if(actions.isEmpty()) {
//			System.out.println("Derive Actions Empty! Wait 2 second because maybe a menu is loading");
//			Util.pause(2);
//			Action nop = new NOP();
//			nop.set(Tags.OriginWidget, state);
//			nop.set(Tags.Desc, "NOP action to wait");
//			return new HashSet<>(Collections.singletonList(nop));
//		}

		Action key_down = ac.hitKey(KBKeys.VK_PAGE_DOWN);
		key_down.set(Tags.OriginWidget, state);
		actions.add(key_down);


		Action key_up = ac.hitKey(KBKeys.VK_PAGE_UP);
		key_up.set(Tags.OriginWidget, state);
		actions.add(key_up);
//		Action protocol = getProtocolAction(state);
//		if(protocol!=null)
//			actions.add(protocol);
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

//	@Override
//	protected boolean isTypeable(Widget widget) {
//		Role role = widget.get(Tags.Role, Roles.Widget);
//		if (Role.isOneOf(role, NativeLinker.getNativeTypeableRoles())) {
//			// Specific class="input" for parasoft SUT
//			if(widget.get(WdTags.WebCssClasses, "").contains("input")) {
//				return true;
//			}
//
//			// Input type are special...
//			if (role.equals(WdRoles.WdINPUT)) {
//				String type = ((WdWidget) widget).element.type;
//				return WdRoles.typeableInputTypes().contains(type);
//			}
//			return true;
//		}
//
//		return false;
//	}

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
	protected Action selectAction(final State state, final Set<Action> actions) {
		//Call the preSelectAction method from the DefaultProtocol so that, if necessary,
		//unwanted processes are killed and SUT is put into foreground.
		final Action preSelectedAction = preSelectAction(state, actions);
		if (preSelectedAction != null) {
			return preSelectedAction;
		}
		if(actions.isEmpty()){
			return super.selectAction(state, actions);
		}
		Action modelAction = stateModelManager.getAbstractActionToExecute(actions);
		if(modelAction==null) {
			System.out.println("State model based action selection did not find an action. Using random action selection.");
			// if state model fails, use random (default would call preSelectAction() again, causing double actions HTML report):
			return RandomActionSelector.selectAction(actions);
		}
		return modelAction;
	}

	/**
	 * Execute the selected action.
	 * Extract and create JaCoCo coverage report (After each action JaCoCo report will be created).
	 */
	@Override
	protected boolean executeAction(SUT system, State state, Action action){
		boolean actionExecuted = super.executeAction(system, state, action);

		String information = "Sequence | " + OutputStructure.sequenceInnerLoopCount +
				" | actionnr | " + actionCount +
				" | url | " + WdDriver.getCurrentUrl();
		WriterExperiments.writeMetrics(new WriterExperimentsParams.WriterExperimentsParamsBuilder()
				.setFilename("urlData")
				.setInformation(information)
				.build());

		try {
			extractStateModelMetrics();
		} catch(Exception e) {
			LogSerialiser.log("ERROR Extracting state model metrics: " + actionCount, LogSerialiser.LogLevel.Info);
			System.err.println("ERROR Extracting state model metrics: " + actionCount);
		}

		return actionExecuted;
	}

	/**
	 * This method is invoked each time the TESTAR has reached the stop criteria for generating a sequence.
	 * This can be used for example for graceful shutdown of the SUT, maybe pressing "Close" or "Exit" button
	 */
	@Override
	protected void finishSequence() {
//
//		// Extract and create JaCoCo sequence coverage report for Generate Mode
//		if(settings.get(ConfigTags.Mode).equals(Modes.Generate)) {
//			extractJacocoSequenceReport();
//		}

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
//		if(new File("jacoco.exec").exists()) {
//			System.out.println("Deleted residual jacoco.exec file ? " + new File("jacoco.exec").delete());
//		}
	}

	/**
	 * This method is called after the last sequence, to allow for example handling the reporting of the session
	 */
	@Override
	protected void closeTestSession() {
		super.closeTestSession();
		// Extract and create JaCoCo run coverage report for Generate Mode
		if(settings.get(ConfigTags.Mode).equals(Modes.Generate)) {
			try {
				extractStateModelMetrics();
			} catch(Exception e) {
				LogSerialiser.log("ERROR Extracting state model metrics: " + actionCount, LogSerialiser.LogLevel.Info);
				System.err.println("ERROR Extracting state model metrics: " + actionCount);
			}
			compressOutputRunFolder();
			copyOutputToNewFolderUsingIpAddress("N:");
		}
	}
}
