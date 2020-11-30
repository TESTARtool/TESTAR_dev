/**
 * Copyright (c) 2018, 2019, 2020 Open Universiteit - www.ou.nl
 * Copyright (c) 2019, 2020 Universitat Politecnica de Valencia - www.upv.es
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

import com.github.curiousoddman.rgxgen.RgxGen;
import es.upv.staq.testar.CodingManager;
import es.upv.staq.testar.DefaultIDGenerator;
import es.upv.staq.testar.NativeLinker;
import net.sf.saxon.om.Item;
import net.sf.saxon.s9api.*;
import net.sf.saxon.value.BooleanValue;
import nl.ou.testar.RandomActionSelector;
import org.fruit.Pair;
import org.fruit.alayer.*;
import org.fruit.alayer.Action;
import org.fruit.alayer.actions.*;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.webdriver.*;
import org.fruit.alayer.webdriver.enums.WdRoles;
import org.fruit.alayer.webdriver.enums.WdTags;
import org.fruit.monkey.Settings;
import org.testar.protocols.WebdriverProtocol;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.stream.Collectors;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;


public class Protocol_webdriver_generic_ing extends WebdriverProtocol {
    protected int highWebModalZIndex = 0;
    protected Widget widgetModal;

	protected List<ActionRule> actionRules = actionRules();
	protected List<GenRule> generatorRules = inputGenerators();
	protected List<FilterRule> filterRules = filterRules();

	public static String lowercase(String str) {
		return str.toLowerCase();
	}
	public static Boolean ends(String str1, String str2) {
		return str1.endsWith(str2);
	}

	private static Processor saxonProcessor;
	private static XPathCompiler xpathCompiler;

	static {
		saxonProcessor = new Processor(false);
		xpathCompiler = saxonProcessor.newXPathCompiler();
		xpathCompiler.setCaching(true);
	}

	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 *
	 * @param settings the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings) {

		CodingManager.setIdGenerator(new MyIdGenerator());
		
		NativeLinker.addWdDriverOS();
		super.initialize(settings);
		ensureDomainsAllowed();

		// Classes that are deemed clickable by the web framework
		clickableClasses = Arrays.asList("v-menubar-menuitem", "v-menubar-menuitem-caption");

		// Disallow links and pages with these extensions
		// Set to null to ignore this feature
		deniedExtensions = Arrays.asList("pdf", "jpg", "png");

		// Define a whitelist of allowed domains for links and pages
		// An empty list will be filled with the domain from the sut connector
		// Set to null to ignore this feature
		domainsAllowed = null; //Arrays.asList("www.ou.nl", "mijn.awo.ou.nl", "login.awo.ou.nl");

		// If true, follow links opened in new tabs
		// If false, stay with the original (ignore links opened in new tabs)
		followLinks = true;
		// Propagate followLinks setting
		WdDriver.followLinks = followLinks;

		// URL + form name, username input id + value, password input id + value
		// Set login to null to disable this feature
		login = null ; //Pair.from("https://login.awo.ou.nl/SSO/login", "OUinloggen");
		username = Pair.from("username", "");
		password = Pair.from("password", "");

		// List of atributes to identify and close policy popups
		// Set to null to disable this feature
		policyAttributes = null; /*new HashMap<String, String>() {{
			put("class", "lfr-btn-label");
		}};*/

		WdDriver.fullScreen = true;

		// Override ProtocolUtil to allow WebDriver screenshots
		protocolUtil = new WdProtocolUtil();

	}

	public class MyIdGenerator extends DefaultIDGenerator {
		@Override
		public void buildIDs(Widget widget) {
			super.buildIDs(widget);
		}

		@Override
		public void buildIDs(State state, Set<Action> actions) {
			super.buildIDs(state, actions);
		}

		@Override
		public void buildIDs(State state, Action action) {
			super.buildIDs(state, action);
		}

		@Override
		public void buildEnvironmentActionIDs(State state, Action action) {
			super.buildEnvironmentActionIDs(state, action);
		}

		@Override
		public String getAbstractStateModelHash(String applicationName, String applicationVersion) {
			return super.getAbstractStateModelHash(applicationName, applicationVersion);
		}
	} ;

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
		SUT sut = super.startSystem();

		/* Capture ing-flow state */
		String customElementStateLambda =
				"(element => { if (element.tagName.toLowerCase() === 'ing-flow') " +
						"{ return element._router.state; } else { return null; } } )";

		sut.set(WdTags.WebCustomElementStateLambda, customElementStateLambda);

		xpathCompiler.setCaching(true);
		
		return sut;
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
	}

	protected Object abstractINGFlowState(Object obj) {
		if (obj instanceof Map) {
			Map map = (Map)obj;
			Map newmap = new HashMap();

			for (Object key : map.keySet()) {
				newmap.put(key, abstractINGFlowState(map.get(key)));
			}
			return newmap;
		}
		else if (obj == null) {
			return null;
		}
		else {
			return obj.getClass().getCanonicalName(); // abstract value to class
		}
	}

	private boolean match(String expr, XdmItem item) {
		try {
			XdmItem i = xpathCompiler.evaluateSingle(expr, item);
			if (i != null) {
				Item k = i.getUnderlyingValue();
				if (k instanceof BooleanValue) {
					BooleanValue vv = (BooleanValue)k;
					return vv.isIdentical(BooleanValue.TRUE);
				}
				else {
					return true;
				}
			}
			return false;
		}
		catch (Exception e) {
			throw new RuntimeException("Cannot evaluate " + e);
		}
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

		Document d = toDom(state);
		//printDocument(d);
		
		// Reset because modal element may disappear
    	highWebModalZIndex = 0;
    	widgetModal = state;

    	Boolean modalMode = false;

    	for(Widget w : state) {
			Object st = w.get(WdTags.WebCustomElementState, null);

			if (st != null) {
				Object filtered = abstractINGFlowState(st);
				System.out.println("ing-flow state: " + st);
				System.out.println("ing-flow abstract state: " + abstractINGFlowState(st));
				state.set(WdTags.WebCustomElementState, filtered);
			}

			String cl = (((WdWidget)w).element).attributeMap.getOrDefault("class", "");
			// if the ING global-overlays element contains children, there is a modal displayed
			if ("global-overlays".equals(cl) && w.childCount() > 0) {
				modalMode = true;
			}

			// Set highWebModalZIndex value. And the widget that represents that block modal.
    		// It can be useful for users in their specific protocols.
    		if(w.get(WdTags.WebIsWindowModal, false) && w.get(WdTags.WebZIndex, -1) > highWebModalZIndex) {
    			highWebModalZIndex = w.get(WdTags.WebZIndex, -1);
    			widgetModal = w;
    		}
    	}

    	state.set(WdTags.WebIsWindowModal, modalMode);

		CodingManager.buildIDs(state);

		return state;                                                                        
	}

	protected Document toDom(State state) {
		WdState s = (WdState)state;

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = factory.newDocumentBuilder();

			Document d = dBuilder.newDocument();
			Node n = toNode(d, s);
			d.appendChild(n);

			s.set(WdTags.DOM, d);
			//d.setUserData("widget", s, null);
			
			return d;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot create DOM representation");
		}
	}

	public static void printDocument(Document doc) {
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			transformer.transform(new DOMSource(doc),
					new StreamResult(new OutputStreamWriter(System.out, "UTF-8")));
		}
		catch (Exception e) {
			throw new RuntimeException("Cannot print doc: " + e);
		}
	}

	protected Node toNode(Document d, WdWidget w) {
		WdElement el = w.element;
		
		Element e = d.createElement(el.tagName);

		e.setTextContent(e.getTextContent());
		
		Map <String, String> map = el.attributeMap;

		for (String key: map.keySet()) {
			e.setAttribute(key, map.get(key));
		}

		if (el.value != null) {
			e.setAttribute("value", el.value.toString());
		}
		if (el.checked != null) {
			e.setAttribute("checked", el.checked.toString());
		}
		
		for (int i = 0; i < w.childCount() ; i++) {
			e.appendChild(toNode(d, w.child(i)));
		}

		// Associate bi-directional
		w.set(WdTags.DOM, e);
		//e.setUserData("widget", w, null);
		
		return e;
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
	protected Set<Action> deriveActions(SUT system, State state)
			throws ActionBuildException {

		// Kill unwanted processes, force SUT to foreground
		Set<Action> actions = super.deriveActions(system, state);

		// create an action compiler, which helps us create actions
		// such as clicks, drag&drop, typing ...
		StdActionCompiler ac = new AnnotatingActionCompiler();

		// Check if forced actions are needed to stay within allowed domains
		Set<Action> forcedActions = detectForcedActions(state, ac);
		if (forcedActions != null && forcedActions.size() > 0) {
			return forcedActions;
		}

		boolean modalMode = state.get(WdTags.WebIsWindowModal, false);

		// iterate through all widgets
		for (Widget widget : state) {
			// slides can happen, even though the widget might be blocked
			//addSlidingActions(actions, ac, scrollArrowSize, scrollThick, widget, state);

			if (blackListedWithFilter(widget)) {
				continue;
			}

			// only consider enabled and non-tabu widgets
			if (!widget.get(Enabled, true) || blackListed(widget)) {
				continue;
			}

			// If the element is blocked, TESTAR can't click on or type in the widget
			if (widget.get(Blocked, false) && !widget.get(WdTags.WebIsShadow, false)) {
				continue;
			}

			// NOTE: this doesn't work for this specific ING use case - the z-index blocking algo is too naive
			// Check if the element is blocked by web modal element with high z-index
			/*if (isBlockedByModal(widget)) {
				continue;
			} */

			// type into text boxes
			if (isAtBrowserCanvas(widget) && isTypeable(widget) && (whiteListed(widget) || isUnfiltered(widget))) {
				actions.add(ac.clickTypeInto(widget, this.getRandomText(widget), true));
			}

			// left clicks, but ignore links outside domain
			if (isAtBrowserCanvas(widget) && isClickable(widget) && (whiteListed(widget) || isUnfiltered(widget))) {
				if (!isLinkDenied(widget)) {
					WdElement element = ((WdWidget) widget).element;
					Role role = widget.get(Tags.Role, Roles.Widget);
					actions.add(ac.leftClickAt(widget));
				}
			}
		}

		selectRuleAction(state, actions);

		return actions;
	}

	Boolean blackListedWithFilter(Widget w) {
		boolean isWhite = false;
		boolean isBlack = false;

		Node n = w.get(WdTags.DOM);
		XdmItem item = saxonProcessor.newDocumentBuilder().wrap(n);

		for (FilterRule f : filterRules) {
			try {
				if (match(f.sexpr, item)) {
					isBlack = isBlack || f.isBlack;
					isWhite = isWhite || !f.isBlack;
				}
			}
			catch (Exception e){
				throw new RuntimeException("Cannot evaluate " + e);
			}
		}

		// TODO: turn this into a boolean expression
		if (isWhite) { return false; }
		else { return isBlack; }  
	}

	List<ActionRule> actionRules() {
		return actionsRules(
			new ActionRule("'true'", 10),                  // Default priority
			new ActionRule(".[(string-length(string(@value)) = 0) and (@type = 'text')]", 20), // Empty text fields
			new ActionRule(".[@type = 'radio'] and (count(ancestor::*[@role = 'radiogroup']//ing-radio[not(@checked = 'true')]) = count(ancestor::*[@role = 'radiogroup']//ing-radio))", 20) // radio-groups
		);
	}
	
	List<GenRule> inputGenerators() {
		return inputGenerators(
			new GenRule("'true'", "[A-Za-z0-9]{1,20}", 10),                        // Generic input

			formInputRule("mySituationForm", "age", "[1-8][0-9]", 50),
			formInputRule("income", "income", "[1-9][0-9]{4}", 50),
			formInputRule("income", "years[]", "[1-9][0-9]{4}", 50),
			formInputRule("monthlyIncome", "monthlyIncome", "[1-7][0-9]{3}", 100),
			formInputRule("expensesForm", "studyLoan", "[1-3][0-9]{4}", 50),
			formInputRule("expensesForm", "liabilities", "[1-3][0-9]{4}", 50),
			formInputRule("expensesForm", "alimony", "[0-9]{3}", 50) ,
			formInputRule("entrepreneurIncomeForm", "companyProfit", "[1-9][0-9]{2}", 50)
		);
	}

	List<FilterRule> filterRules() {
		return filterRules(
				new FilterRule("ancestor-or-self::*[@aria-hidden = 'true']", true),  // ignore aria-hidden
				new FilterRule("a[@href]", true),
				new FilterRule("ancestor::header", true),
				new FilterRule(".[@id = 'action-top']", true),
				new FilterRule(".[@id = 'action-back']", true),
				new FilterRule("ancestor::*[contains(@slot, 'progress')]", true),
		 		new FilterRule("ancestor::*[contains(@class, 'progress')]", true),
				new FilterRule(".[contains(@href, 'bel-me-nu')]", true),
				new FilterRule(".[ends-with(@href, 'hypotheek-berekenen')]", true),
				new FilterRule(".[@target = '_blank']", true)
		);
	}

	List <ActionRule> actionsRules(ActionRule ... rules) {
		return Arrays.asList(rules);
	}

	List <FilterRule> filterRules(FilterRule ... rules) {
		return Arrays.asList(rules);
	}

	List<GenRule> inputGenerators(GenRule ... rules) {
		return Arrays.asList(rules);
	}

	GenRule formInputRule(String f, String n, String g, int p) {
		String lf = f.toLowerCase();
		String ln = n.toLowerCase();
		String xpath = String.format(".[contains(lower-case(@name), '%s')]/ancestor::*[contains(lower-case(@name), '%s')]", ln, lf);
		return new GenRule(xpath, g, p);
	}

	// Choose Rule based on priorities
	protected <R extends PrioRule> R prioritizedPick(List<R> rules) {
		int sum = rules.stream().map(r -> r.priority).reduce(0, Integer::sum);
		int x = (new Random()).nextInt(sum);
		int s = 0;

		for (R rule : rules) {
			s = s + rule.priority();
			if (x < s) { return rule; }
		}

		throw new RuntimeException("there are no rules to pick");
	}

	@Override
	protected String getRandomText(Widget w) {
		// Default behaviour
		String sText = super.getRandomText(w);

		Node n = w.get(WdTags.DOM);
		XdmItem item = saxonProcessor.newDocumentBuilder().wrap(n);
		
		// Collect all matches rules
		List <GenRule> matches = generatorRules.stream().
				filter(GenRule::isValid).       				// only consider valid rules
				filter(r -> match(r.sexpr, item)). // only those that match
				collect(Collectors.toList());

		// Pick rule based on priority
		if (matches.size() > 0) {
			return prioritizedPick(matches).generator.generate();
		}

		// else, return default behaviour
		return sText;
	}  

	abstract class PrioRule {
		protected int priority = 0;
		protected boolean isValid = false;

		public int priority() { return priority; }
		public boolean isValid() { return isValid; }
	}

	class ActionRule extends PrioRule {
		protected String sexpr;
		protected XPathExecutable expression;

		public ActionRule(String e, int p) {
			try {
				sexpr = e;
				expression = xpathCompiler.compile(e);
				isValid = true; priority = p;
			}
			catch (Exception exception) {
				System.out.println("WARNING: invalid action rule: " + exception.getMessage());
			}
		}
		public String toString() {
			return "ActionRule(" + sexpr + "," + priority + ")";
		}
	}

	class ActionPrio extends PrioRule {
		public final Action action;

		public ActionPrio(Action a, int prio) {
			action = a;
			priority = prio;
			isValid = true;
		}

		public String toString() {
			return "ActionPrio(" + action + "," + priority + ")";
		}
	}
	
	class GenRule extends PrioRule {
		protected String sexpr;
		protected String gexpr;
		protected XPathExecutable expression;
		protected RgxGen generator;

		public GenRule(String e, String g, int p) {
			try {
				sexpr = e;
				gexpr = g;
				expression = xpathCompiler.compile(e); generator = new RgxGen(g);
				isValid = true; priority = p;
			}
			catch (Exception exception) {
				System.out.println("WARNING: invalid generator rule: " + exception.getMessage());
			}
		}

		public String toString() { return "GenRule(" +  sexpr + ", " + gexpr + "," + priority + ");"; }
	}

	class FilterRule  {

		protected String sexpr;
		protected XPathExecutable expr;

		protected boolean isBlack;
		protected boolean isValid;

		public FilterRule(String e, boolean b) {
			sexpr = e;
			try {
				expr = xpathCompiler.compile(e);
				isBlack = b;
				isValid = true;
			}
			catch (Exception exception) {
				System.out.println("WARNING: invalid filter rule: " + exception.getMessage());
			}
		}
		public boolean isValid() { return isValid; }
		public String toString() { return "FilterRule(" +  sexpr + ", " + isBlack + ");"; }
	}

	@Override
	protected boolean isClickable(Widget widget) {
		WdElement element = ((WdWidget) widget).element;

		Role role = widget.get(Tags.Role, Roles.Widget);
		if (Role.isOneOf(role, NativeLinker.getNativeClickableRoles())) {
			// Input type are special...
			if (role.equals(WdRoles.WdINPUT)) {
				String type = ((WdWidget) widget).element.type;
				return WdRoles.clickableInputTypes().contains(type);
			}
			return true;
		}
		
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
	 * Check if the desired widget is blocked by some widgetModal.
	 * By default, this widgetModal object is the TESTAR State widget.
	 * 
	 * If a web element with a display "block" property appears,
	 * the z-index property will be used to determine the new web modal element
	 * 
	 * @param w
	 * @return
	 */
	private boolean isBlockedByModal(Widget w) {
		return !widgetIsChildOfParent(w, widgetModal);
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
			retAction = selectRuleAction(state, actions);
			if (retAction == null) {
				retAction = RandomActionSelector.selectAction(actions);
			}
		}
		return retAction;
	}

	protected Action selectRuleAction(State state, Set<Action> actions) {
		List<ActionPrio> rules = new LinkedList();

		for (Action action : actions) {
			for (ActionRule r: actionRules) {
				Widget w = action.get(Tags.OriginWidget);

				Node n = w.get(WdTags.DOM);
				XdmItem item = saxonProcessor.newDocumentBuilder().wrap(n);

				try {
					if (match(r.sexpr, item)) {
						if (r.priority > 10) {
							System.out.println("PRIO > 10: " + item);
						}
						rules.add(new ActionPrio(action, r.priority));
					}

				}
				catch (Exception e) {
					throw new RuntimeException("Cannot evaluate xpath: " + e);
				}
			}
		}

		return prioritizedPick(rules).action;
	}

	/**
	 * Execute the selected action
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
