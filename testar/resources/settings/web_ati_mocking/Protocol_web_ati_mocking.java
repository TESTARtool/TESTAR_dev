package web_ati_mocking;

/***************************************************************************************************
*
* Copyright (c) 2013, 2014, 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
* Copyright (c) 2017 Open Universiteit - www.ou.nl
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

/*
 * A generic desktop protocol
 *
 * @author Urko Rueda Molina, Govert Buijs
 */

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;

import es.upv.staq.testar.NativeLinker;
import es.upv.staq.testar.protocols.ClickFilterLayerProtocol;
import nl.ou.testar.tgherkin.protocol.UrlActionCompiler;

import java.io.File;
import java.util.*;
import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

public class Protocol_web_ati_mocking extends ClickFilterLayerProtocol {
	
	private static final String PREVENT_BY_MOCK_TITLE = "INLOGGEN";
	private static final String MOCK_URL = "www.google.nl";
	private static final String MOCK_ACTION_TITLE = "Adres- en zoekbalk";
	private static final String RETURN_URL = "www.youtube.com";
	
	// If we encounter a login URL, determine the 'login button'and force click
	private static String loginTitle = "inloggen"; // lower case
	private static String loginUrl = "https://login.awo.ou.nl/sso/login";
	
	private static boolean toBeMocked = true;
	// Each browser (and locale!) uses different names for standard elements
	private enum Browser {
		// Dutch version
		explorerNL("address", "UIAEdit", "back", "close"), 
		firefoxNL("voer zoekterm of adres in", "UIAEdit", "terug",null), 
		chromeNL("Adres- en zoekbalk", "UIAEdit", "vorige", "sluiten"),
		// English version
		explorerEN("address", "UIAEdit", "back", "close"), 
		firefoxEN("voer zoekterm of adres in", "UIAEdit", "terug",null),
		chromeEN("address and search bar", "UIAEdit", "back", "close");

		private String addressTitle;
		private String addressRole;
		private String backTitle;
		private String closeTitle;

		Browser(String addressTitle, String addressRole, String backTitle, String closeTitle) {
			this.addressTitle = addressTitle;
			this.addressRole = addressRole;
			this.backTitle = backTitle;
			this.closeTitle = closeTitle;
		}
	}


	private static Role webText; // browser dependent
	private static double browserToolbarFilter;
	private static Browser browser;

	private static final double SCROLLARROWSIZE = 36; // sliding arrows (iexplorer)
	private static final double SCROLLTHICK = 16; // scroll thickness (iexplorer)

	// Go back once we encounter certain files
	private static String[] deniedExtensions 
	   = new String[] { "pdf" };
	// If set to NULL, only the SUT connector domain will be used
	private static String[] domainsAllowed 
	    = new String[] { "mijn.awo.ou.nl", "cws.awo.ou.nl", "ati.awo.ou.nl" };

	private String oldAddress = null;
	private String currentAddress = null;
	private Widget backWidget = null;
	private Widget closeWidget = null;
	private Widget loginWidget = null;

	/**
	 * Called once during the life time of TESTAR This method can be used to perform
	 * initial setup work.
	 *
	 * @param settings the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings) {
		super.initialize(settings);
		ensureDomainsAllowed();
		initBrowser();
	}

	// check browser
	private void initBrowser() {
		// just init with some value
		webText = NativeLinker.getNativeRole("UIAEdit");
		browser = Browser.explorerNL;

		String sutPath = settings().get(ConfigTags.SUTConnectorValue);
		if (sutPath.contains("iexplore.exe")) {
			webText = NativeLinker.getNativeRole("UIAEdit");
			browser = Browser.explorerNL;
		} else if (sutPath.contains("firefox")) {
			webText = NativeLinker.getNativeRole("UIAText");
			browser = Browser.firefoxNL;
		} else if (sutPath.contains("chrome") || sutPath.contains("chromium")) {
			webText = NativeLinker.getNativeRole("UIAEdit");
			browser = Browser.chromeNL;
		}
	}

	/**
	 * This method is invoked each time TESTAR starts to generate a new sequence.
	 */
	@Override
	protected void beginSequence(SUT sut, State state) {
		super.beginSequence(sut, state);
	}

	/**
	 * This method is called when TESTAR starts the System Under Test (SUT). The
	 * method should take care of 1) starting the SUT (you can use TESTAR's settings
	 * obtainable from <code>settings()</code> to find out what executable to run)
	 * 2) bringing the system into a specific start state which is identical on each
	 * start (e.g. one has to delete or restore the SUT's configuration files etc.)
	 * 3) waiting until the system is fully loaded and ready to be tested (with
	 * large systems, you might have to wait several seconds until they have
	 * finished loading)
	 *
	 * @return a started SUT, ready to be tested.
	 */
	@Override
	protected SUT startSystem() throws SystemStartException {
		SUT sut = super.startSystem();
		return sut;
	}

	/**
	 * This method is called when TESTAR requests the state of the SUT. Here you can
	 * add additional information to the SUT's state or write your own state
	 * fetching routine. The state should have attached an oracle (TagName:
	 * <code>Tags.OracleVerdict</code>) which describes whether the state is
	 * erroneous and if so why.
	 *
	 * @return the current state of the SUT with attached oracle.
	 */
	@Override
	protected State getState(SUT system) throws StateBuildException {
		State state = super.getState(system);

		for (Widget w : state) {

			Role role = w.get(Tags.Role, Roles.Widget);
			if (Role.isOneOf(role, NativeLinker.getNativeRole("UIAToolBar"))) {
				browserToolbarFilter = w.get(Tags.Shape, null).y() + w.get(Tags.Shape, null).height();
			}
		}
		return state;
	}

	/**
	 * This is a helper method used by the default implementation of
	 * <code>buildState()</code> It examines the SUT's current state and returns an
	 * oracle verdict.
	 *
	 * @return oracle verdict, which determines whether the state is erroneous and
	 *         why.
	 */
	@Override
	protected Verdict getVerdict(State state) {
		Verdict verdict = super.getVerdict(state); // by urueda
		// system crashes, non-responsiveness and suspicious titles automatically
		// detected!

		// -----------------------------------------------------------------------------
		// MORE SOPHISTICATED ORACLES CAN BE PROGRAMMED HERE (the sky is the limit ;-)
		// -----------------------------------------------------------------------------

		// ... YOU MAY WANT TO CHECK YOUR CUSTOM ORACLES HERE ...

		return verdict;
	}

	/**
	 * This method is used by TESTAR to determine the widget leading to an external.
	 * domain where you want to continue by mocking the procedure
	 * @param w widget
	 * @return boolean true if widget is not allowed
	 */

	protected boolean blackListed(Widget w) {
		String desc = w.get(Tags.Desc, null);
		boolean loginParm = desc.equalsIgnoreCase("Wachtwoord") 
				|| desc.equalsIgnoreCase("Gebruikersnaam");
		boolean blacklisted = super.blackListed(w) || loginParm;
		return blacklisted;
	}

	private boolean preventByMock(State state, Action action, String preventByMockActionTitle) {
		if (action != null) {
			List<Widget> targets = Util.targets(state, action);
			if (targets != null) {
				for (Widget widget: targets) {
					if (widget != null) {
						String title = widget.get(Tags.Desc, null);
						if (title != null 
							&& title.equalsIgnoreCase(preventByMockActionTitle)) {
								return true;
						}
					}
				}
			}
		}
		return false;
	}

	private Action rerouteByUrl(State state, String mockUrl) {
		Action action = null;
		UrlActionCompiler ac = new UrlActionCompiler();
		
		for (Widget widget : getTopWidgets(state)) {
			String title = widget.get(Tags.Title, null);
			if (title.equalsIgnoreCase(browser.addressTitle)) {
				System.out.println("[" + getClass().getSimpleName() 
						+ "/rerouteByUrl temp 3] " + widget.getRepresentation("\t"));			
				action = ac.clickTypeUrl(widget, mockUrl);
				action.set(Tags.ConcreteID, widget.get(Tags.ConcreteID));
				action.set(Tags.AbstractID, widget.get(Tags.Abstract_R_ID));
				System.out.println("[" + getClass().getSimpleName() 
						+ "] Url wordt geactiveerd (" + title +"; " + mockUrl + ")");
				toBeMocked = false;
			}
		}
		return action;
	}

	/**
	 * This method is used by TESTAR to determine the set of currently available
	 * actions. You can use the SUT's current state, analyse the widgets and their
	 * properties to create a set of sensible actions, such as: "Click every Button
	 * which is enabled" etc. The return value is supposed to be non-null. If the
	 * returned set is empty, TESTAR will stop generation of the current action and
	 * continue with the next one.
	 *
	 * @param system the SUT
	 * @param state the SUT's current state
	 * @return a set of actions
	 */
	@Override
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {
		Set<Action> actions = super.deriveActions(system, state);
		// Ignore this protocol if Prolog is activated
		if (settings().get(ConfigTags.PrologActivated)) {
			return actions;
		}

		// Check if forced actions are needed to stay within allowed domains
		Set<Action> forcedActions = detectForcedActions();
		if (forcedActions != null && forcedActions.size() > 0) {
			return forcedActions;
		}

		// iterate through all (top) widgets
		StdActionCompiler ac = new UrlActionCompiler();
		
		for (Widget widget : getTopWidgets(state)) {
			String title = widget.get(Tags.Title, null);
			String role = widget.get(Tags.Role).toString();

			// only consider enabled and non-blocked widgets
			if (widget.get(Enabled, true) && !widget.get(Blocked, false)
				&& ((MOCK_ACTION_TITLE.equalsIgnoreCase(title) && !toBeMocked)
					|| ("UIAButton".equals(role) 
						&& PREVENT_BY_MOCK_TITLE.equalsIgnoreCase(title) 
						&& toBeMocked))) {

				// do not build actions for tabu widget
				if (blackListed(widget)) {
					continue;
				}

				// left clicks
				if (whiteListed(widget) || isClickable(widget)) {
					// Don't allow Testar to test outside domains
					if (isLinkDenied(widget)) {
						continue;
					}

					storeWidget(state.get(Tags.ConcreteID), widget);
					actions.add(ac.leftClickAt(widget));
				}

				// type into text boxes
				if (whiteListed(widget) || isTypeable(widget)) {
					storeWidget(state.get(Tags.ConcreteID), widget);
					actions.add(ac.clickTypeInto(widget, this.getRandomText(widget)));
				}

				// slides
				addSlidingActions(actions, ac, SCROLLARROWSIZE, SCROLLTHICK, widget);
			}
		}
		return actions;
	}

	@Override
	protected boolean isClickable(Widget w) {
		if (!isAtBrowserCanvas(w)) {
			return false;
		} else {
			return super.isClickable(w);
		}
	}

	@Override
	protected boolean isTypeable(Widget w) {
		if (!isAtBrowserCanvas(w)) {
			return false;
		}

		Role role = w.get(Tags.Role, null);
		if (role != null && Role.isOneOf(role, webText)) {
			return isUnfiltered(w);
		}

		return false;
	}

	private boolean isAtBrowserCanvas(Widget w) {
		Shape shape = w.get(Tags.Shape, null);

		return shape != null && shape.y() > browserToolbarFilter;
	}

	/**
	 * Select one of the possible actions (e.g. at random)
	 *
	 * @param state the SUT's current state
	 * @param actions the set of available actions as computed by
	 *        <code>buildActionsSet()</code>
	 * @return the selected action (non-null!)
	 */
	@Override
	protected Action selectAction(State state, Set<Action> actions) {
		Action action = super.selectAction(state, actions);
	     // optional mock actions
		if (toBeMocked) {
			if (preventByMock(state, action, PREVENT_BY_MOCK_TITLE)) {
				System.out.println("[" + getClass().getSimpleName() + "] Mock actie wordt uitgevoerd");
				action = rerouteByUrl(state, MOCK_URL);
			}
		} else {
			if (preventByMock(state, action, MOCK_ACTION_TITLE)) {
				System.out.println("[" + getClass().getSimpleName() + "] Return actie wordt geactiveerd");
			}
		}
		System.out.println(action.toShortString());
		return action;
	}

	/**
	 * Execute the selected action.
	 *
	 * @param system the SUT
	 * @param state the SUT's current state
	 * @param action the action to execute
	 * @return whether or not the execution succeeded
	 */
	@Override
	protected boolean executeAction(SUT system, State state, Action action) {
		return super.executeAction(system, state, action);
	}

	/**
	 * TESTAR uses this method to determine when to stop the generation of actions
	 * for the current sequence. You could stop the sequence's generation after a
	 * given amount of executed actions or after a specific time etc.
	 *
	 * @return if <code>true</code> continue generation, else stop
	 */
	@Override
	protected boolean moreActions(State state) {
		return super.moreActions(state);
	}

	/**
	 * This method is invoked each time after TESTAR finished the generation of a
	 * sequence.
	 */
	@Override
	protected void finishSequence(File recordedSequence) {
		super.finishSequence(recordedSequence);
	}

	/**
	 * TESTAR uses this method to determine when to stop the entire test. You could
	 * stop the test after a given amount of generated sequences or after a specific
	 * time etc.
	 *
	 * @return if <code>true</code> continue test, else stop
	 */
	@Override
	protected boolean moreSequences() {
		return super.moreSequences();
	}

	/*
	 * Check the state if we need to force an action
	 */
	private Set<Action> detectForcedActions() {
		extractAddressWidgets();

		// SUT is probably in the background, let Testar handle it
		if (currentAddress == null) {
			return null;
		}

		StdActionCompiler ac = new AnnotatingActionCompiler();
		Set<Action> actions = new HashSet<>();

		// This assumes the user/pass is auto-filled by the browser
		if (currentAddress.startsWith(loginUrl) && loginWidget != null) {
			actions.add(ac.leftClickAt(loginWidget));
			storeWidget(state.get(Tags.ConcreteID), loginWidget);
		}

		// Don't get caught in a PDFs etc. (no action back)
		else if (isExtensionDenied()) {
			actions.add(ac.leftClickAt(backWidget));
			storeWidget(state.get(Tags.ConcreteID), backWidget);
		}

		// Back action didn't succeed, probably opened in new tab
		else if (oldAddress != null && isUrlDenied(oldAddress) && isUrlDenied(currentAddress)) {
			actions.add(ac.leftClickAt(closeWidget));
			storeWidget(state.get(Tags.ConcreteID), closeWidget);
		}

		// Try back action first, might have opened in same tab
		else if (isUrlDenied(currentAddress)) {
			actions.add(ac.leftClickAt(backWidget));
			storeWidget(state.get(Tags.ConcreteID), backWidget);
		}

		oldAddress = currentAddress;
		return actions;
	}

	/*
	 * Get current address and the action-widgets
	 */
	private void extractAddressWidgets() {
		currentAddress = null;

		for (Widget widget : getTopWidgets(state)) {
			Shape shape = widget.get(Tags.Shape, null);
			String title = widget.get(Tags.Title, "").toLowerCase().trim();
			String value = widget.get(Tags.ValuePattern, "").toLowerCase().trim();
			String role = widget.get(Tags.Role).toString();

			// If not in the header, we only need to look for the loginWidget
			if (shape == null || shape.y() > browserToolbarFilter) {
				if (title.contains(loginTitle) && "UIAButton".equals(role)) {
					loginWidget = widget;
				}
				continue;
			}

			// Application / language setting specific
			if (title.equalsIgnoreCase(browser.addressTitle) && role.equalsIgnoreCase(browser.addressRole)) {
				currentAddress = value;
			} else if ((browser == Browser.explorerNL || browser == Browser.chromeNL)
					&& (title.contains(browser.backTitle) || value.contains(browser.backTitle))) {
				backWidget = widget;
			} else if (title.contains(browser.closeTitle) || value.contains(browser.closeTitle)) {
				closeWidget = widget;
			}
		}
	}

	/*
	 * Get the domain from a full URL
	 */
	private String getDomain(String url) {
		if (url == null) {
			return null;
		}

		url = url.replace("https://", "").replace("http://", "");
		return (url.split("/")[0]).split("\\?")[0];
	}

	/*
	 * Check if the current address has a denied extension (PDF etc.)
	 */
	private boolean isExtensionDenied() {
		// If the current page doesn't have an extension, always allow
		if (!currentAddress.contains(".")) {
			return false;
		}

		// Deny if the extension is in the list
		String ext = currentAddress.substring(currentAddress.lastIndexOf("."));
		return Arrays.asList(deniedExtensions).contains(ext);
	}

	/*
	 * Check if the widget has a denied URL as hyperlink
	 */
	private boolean isLinkDenied(Widget widget) {
		return isUrlDenied(widget.get(Tags.ValuePattern, ""));
	}

	/*
	 * Check if the URL is denied
	 */
	private boolean isUrlDenied(String url) {
		// Not a link
		if (url == null || !(url.startsWith("http://") || url.startsWith("https://"))) {
			return false;
		}

		// Only allow pre-approved domains
		String domain = getDomain(url);
		return !Arrays.asList(domainsAllowed).contains(domain);
	}

	/*
	 * If domainsAllowed not set, allow the domain from the SUT Connector
	 */
	private void ensureDomainsAllowed() {
		if (domainsAllowed != null && domainsAllowed.length > 0) {
			return;
		}

		String[] parts = settings().get(ConfigTags.SUTConnectorValue).split(" ");
		String url = parts[parts.length - 1].replace("\"", "");
		domainsAllowed = new String[]
		{ getDomain(url) };
	}
}
