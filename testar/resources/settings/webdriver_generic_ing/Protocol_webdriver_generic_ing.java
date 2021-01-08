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
import org.apache.commons.codec.binary.Base64;
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
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;
import static org.fruit.alayer.webdriver.Constants.scrollArrowSize;
import static org.fruit.alayer.webdriver.Constants.scrollThick;

public class Protocol_webdriver_generic_ing extends WebdriverProtocol {
    protected int highWebModalZIndex = 0;
    protected Widget widgetModal;

    protected VisitedActionsData visitedActionsData = new VisitedActionsData();
	protected List<ActionRule> actionRules = actionRules();
	protected List<GenRule> generatorRules = inputGenerators();
	protected List<FilterRule> filterRules = filterRules();

	private static DocumentBuilder documentBuilder;
	private static Processor saxonProcessor;
	private static XQueryCompiler xqueryCompiler;

	private static Xslt30Transformer abstractDocumentTemplate;
	private static Xslt30Transformer abstractWidgetTemplate;
	private static Xslt30Transformer compileModelTemplate;

	private static String testarNamespace = "http://testar.org/state";

	private static List<GenRule> modelGenRules;
	
	static {
		saxonProcessor = new Processor(false);
		xqueryCompiler = saxonProcessor.newXQueryCompiler();

		xqueryCompiler.setFastCompilation(true);
		xqueryCompiler.declareNamespace("tst", testarNamespace);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			documentBuilder = factory.newDocumentBuilder();
		}
		catch (Exception e)  {
			throw new RuntimeException("Cannot create documentBuilder: ", e);
		}

		try {
			File xsl1 = new File(Settings.getSettingsPath(), "abstract-state.xsl");
			File xsl2 = new File(Settings.getSettingsPath(), "abstract-widget.xsl");
			File xsl3 = new File(Settings.getSettingsPath(), "compile-model.xsl");


			XsltCompiler compiler = saxonProcessor.newXsltCompiler();
			XsltExecutable s1 = compiler.compile(new StreamSource(xsl1));
			XsltExecutable s2 = compiler.compile(new StreamSource(xsl2));
			XsltExecutable s3 = compiler.compile(new StreamSource(xsl3));

			abstractDocumentTemplate = s1.load30();
			abstractWidgetTemplate = s2.load30();
			compileModelTemplate = s3.load30();
		}
		catch (Exception e) {
			throw new RuntimeException("Could not compile: ",  e);
		}

		String modelFileName = (new File(Settings.getSettingsPath(), "model.json")).toURI().toString();
		Map<QName, XdmValue> parameters = new HashMap();
		parameters.put(new QName("json-file"), XdmValue.makeValue(modelFileName));

		try {
			compileModelTemplate.setStylesheetParameters(parameters);
			
			Document d = documentBuilder.newDocument();
			Node n = d.createElement("root");
			d.adoptNode(n);

			Document r = documentBuilder.newDocument();
			compileModelTemplate.transform(new DOMSource(d), new DOMDestination(r));
			
			printXML(r);

			// fetch all input-rules
			XQueryEvaluator e = xqueryCompiler.compile("//input-rule ").load();
			e.setContextItem(saxonProcessor.newDocumentBuilder().wrap(r));
			
			XdmValue result = e.evaluate();
			List<GenRule> gr = new LinkedList<>();
			
			for (XdmItem rule: result) {
				XdmNode node = (XdmNode)rule;
				gr.add(new GenRule(node.attribute("xquery"), node.attribute("regexp"), 5));
			}

			modelGenRules = gr;
		}
		
		catch (Exception e) {
			throw new RuntimeException("Could not compile: " + e);
		}
	}

	static void printXML(Node d) {
		try {
			Transformer tform = TransformerFactory.newInstance().newTransformer();
			tform.setOutputProperty(OutputKeys.INDENT, "yes");
			tform.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			tform.transform(new DOMSource(d), new StreamResult(System.out));
		}
		catch (Exception e) {
			throw new RuntimeException("Could not print XML", e);
		}
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
		protected Node toDom(Widget widget) {
			WdState s = (WdState)widget.root();

			// check if WDState has top level DOM attribute
			if (s.get(WdTags.DOM, null) == null) {
				try {
					Document d = documentBuilder.newDocument();
					// recursive through all widgets
					Node n = toNode(d, s);
					d.appendChild(n);
					s.set(WdTags.DOM, d);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("Cannot create DOM representation ", e);
				}
			}

			// this widget should have a DOM attribute
			return widget.get(WdTags.DOM);
		}

		protected void asNode(Document d, String namespace, Element e, Object o) {
			if (o instanceof Map) {
				Map<?, ?> m = (Map<?, ?>)o;
				for (Object key : m.keySet()) {
					Element cn = d.createElementNS(namespace, "node");
					cn.setAttributeNS(namespace, "key", key.toString());
					asNode(d, namespace, cn, m.get(key));
					e.appendChild(cn);
				}
			}
			else if (o instanceof List) {
				List<?> l = (List<?>)o;
				int i = 0;
				for (Object el: l) {
					Element cn = d.createElementNS(namespace, "node");
					cn.setAttributeNS(namespace, "index", "" + i);
					asNode(d, namespace, cn, el);
					e.appendChild(cn);
					i++;
				}
			}
			else if (o == null) {
				// do nothing
			}
			else {
				e.setTextContent(o.toString());
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

			if (el.customElementState != null) {
				Element ce = d.createElementNS(testarNamespace, "customState");
				asNode(d, testarNamespace, ce, el.customElementState);
				e.appendChild(ce);
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
			e.setUserData("widget", w, null);

			return e;
		}


		private String hashString(String message, String algorithm) {
			try {
				MessageDigest digest = MessageDigest.getInstance(algorithm);
				return Base64.encodeBase64String(digest.digest(message.getBytes("UTF-8")));
			} catch (Exception e) {
				throw new RuntimeException("Could not generate hash from String", e);
			}
		}

		@Override
		public void buildIDs(Widget widget) {
			Node d = toDom(widget);
			XdmValue source = saxonProcessor.newDocumentBuilder().wrap(d);
			super.buildIDs(widget);
			
			try {
				if (widget.parent() == null) {
					XdmValue result = abstractDocumentTemplate.applyTemplates(source);

					widget.set(Tags.ConcreteIDCustom, hashString(source.toString(), "MD5"));
					widget.set(Tags.AbstractIDCustom, hashString(result.toString(), "MD5"));
					
					//System.out.println("ABSTRACT DATA: " + result);
					//System.out.println("ABSTRACT ID CUSTOM: " + widget.get(Tags.AbstractIDCustom));

					for (Widget w : widget.root()) {
						if (w != widget) {
							buildIDs(w);
						}
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("abstract mapping failed", e);
			}
		}

		@Override
		public void buildIDs(State state, Set<Action> actions) {
			super.buildIDs(state, actions);
			for (Action a: actions) {
				buildIDs(state, a);
			}
			// Annotate DOM with previously visited Actions
			Node d = state.get(WdTags.DOM);
			visitedActionsData.annotateVisits((Document)d, (WdState)state);
		}

		@Override
		public void buildIDs(State state, Action action) {
			super.buildIDs(state, action);
			Widget widget = action.get(Tags.OriginWidget, null);
			if (widget != null) {
				Node d = toDom(widget);
				XdmValue source = saxonProcessor.newDocumentBuilder().wrap(d);

				try {
					XdmValue result = abstractWidgetTemplate.applyTemplates(source);
					//System.out.println("CONCRETE ID " + source);
					widget.set(WdTags.ActionAbstractState, hashString(result.toString(), "MD5"));
				}
				catch (Exception e) {
					throw new RuntimeException("Cannot apply template: ", e);
				}
			}
			else throw new RuntimeException("Action: " + action + " has no origin!");
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

	private boolean match(XQueryEvaluator comp, XdmItem item) {
		try {
			comp.setContextItem(item);
			XdmItem i = comp.evaluateSingle();
			
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
			throw new RuntimeException("Cannot evaluate ", e);
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
		
		// Reset because modal element may disappear
    	highWebModalZIndex = 0;
    	widgetModal = state;

    	for(Widget w : state) {
			Object st = w.get(WdTags.WebCustomElementState, null);

			// Set highWebModalZIndex value. And the widget that represents that block modal.
    		// It can be useful for users in their specific protocols.
    		if(w.get(WdTags.WebIsWindowModal, false) && w.get(WdTags.WebZIndex, -1) > highWebModalZIndex) {
    			highWebModalZIndex = w.get(WdTags.WebZIndex, -1);
    			widgetModal = w;
    		}
    	}

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

			// only consider enabled and non-tabu widgets
			if (!widget.get(Enabled, true)) {
				continue;
			}

			// If the element is blocked, TESTAR can't click on or type in the widget
			if (widget.get(Blocked, false) && !widget.get(WdTags.WebIsShadow, false)) {
				continue;
			}

			if (blackListedWithFilter(widget)) {
				continue;
			}
			
			// NOTE: this doesn't work for this specific ING use case - the z-index blocking algo is too naive
			// Check if the element is blocked by web modal element with high z-index
			/*if (isBlockedByModal(widget)) {
				continue;
			} */

			// type into text boxes
			if (isAtBrowserCanvas(widget) && isTypeable(widget) && (isUnfiltered(widget))) {
				actions.add(ac.clickTypeInto(widget, this.getRandomText(widget), true));
			}

			// left clicks, but ignore links outside domain
			if (isAtBrowserCanvas(widget) && isClickable(widget) && (isUnfiltered(widget))) {
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
				if (match(f.expr, item)) {
					isBlack = isBlack || f.isBlack;
					isWhite = isWhite || !f.isBlack;
				}
			}
			catch (Exception e){
				throw new RuntimeException("Cannot evaluate ", e);
			}
		}

		// TODO: turn this into a boolean expression
		if (isWhite) { return false; }
		else { return isBlack; }  
	}

	List<ActionRule> actionRules() {
		return actionsRules(
			new ActionRule("'true'", 1),                  // Default priority
			new ActionRule(".[not(@type = 'submit') and not(@aria-invalid = 'true')] and tst:visits[@count > 0]", 0.2),  // if action already visited, except submit!
			new ActionRule(".[(string-length(string(@value)) = 0) and (@type = 'text')]", 5), // Empty text fields
			new ActionRule(".[@aria-invalid = 'true']", 5), // self is invalid
			new ActionRule(".[@type = 'submit']", 3), // submit
			new ActionRule("let $r := ancestor::*[@role = 'radiogroup']//ing-radio return .[@type = 'radio'] and (count($r) = count($r[@checked = 'false']))", 5), // unselected radiogroup
			new ActionRule("ancestor::*[(@role = 'radiogroup') and (@aria-invalid = 'true')]", 5) // radiogroup is invalid
		);
	}
	
	List<GenRule> inputGenerators() {
		List<GenRule> l = inputGenerators(
			new GenRule("'true'", "[A-Za-z0-9]{1,20}", 1),                        // Generic input

			formInputRule("mySituationForm", "age", "[1-8][0-9]", 5000),
			formInputRule("income", "income", "[1-9][0-9]{4}", 5),
			formInputRule("income", "years[]", "[1-9][0-9]{4}", 5),
			formInputRule("monthlyIncome", "monthlyIncome", "[1-7][0-9]{3}", 10),
			formInputRule("expensesForm", "studyLoan", "[1-3][0-9]{4}", 5),
			formInputRule("expensesForm", "liabilities", "[1-3][0-9]{4}", 5),
			formInputRule("expensesForm", "alimony", "[0-9]{3}", 5),
			formInputRule("entrepreneurIncomeForm", "companyProfit", "[1-9][0-9]{2}", 5)
		);
		l.addAll(modelGenRules);
		return l;
	}

	List<FilterRule> filterRules() {
		return filterRules(
				new FilterRule("ancestor-or-self::*[@aria-hidden = 'true']", true),  // ignore aria-hidden
				new FilterRule(".[(name() = 'a') and (not(@href))]", true), // ignore links without href

				new FilterRule("ancestor::header", true), // ignore header
				new FilterRule("ancestor::ing-feat-sc-house-next-step-based-on-house-card", true), // ignore next step
				new FilterRule("ancestor-or-self::ing-button[contains(@id, 'moreInfoButton')]", true), // ignore more info

				new FilterRule(".[@id = 'action-stop']", true), // ignore stop button
				new FilterRule(".[@id = 'action-back']", true), // ignore back button

				new FilterRule("ancestor::*[contains(@slot, 'progress')]", true), // ignore progress section
		 		new FilterRule("ancestor::*[contains(@class, 'progress')]", true), // ignore progress section

				new FilterRule(".[contains(@href, 'bel-me-nu')]", true), // ignore outside links
				new FilterRule(".[ends-with(@href, 'hypotheek-berekenen/')]", true),
				new FilterRule(".[@target = '_blank']", true)
		);
	}

	List <ActionRule> actionsRules(ActionRule ... rules) {
		return new LinkedList<>(Arrays.asList(rules));
	}

	List <FilterRule> filterRules(FilterRule ... rules) {
		return new LinkedList<>(Arrays.asList(rules));
	}

	List<GenRule> inputGenerators(GenRule ... rules) {
		return new LinkedList<>(Arrays.asList(rules));
	}

	GenRule formInputRule(String f, String n, String g, int p) {
		String lf = f.toLowerCase();
		String ln = n.toLowerCase();
		String xquery = String.format(".[contains(lower-case(@name), '%s')]/ancestor::*[contains(lower-case(@name), '%s')]", ln, lf);
		return new GenRule(xquery, g, p);
	}

	// Choose Rule based on priorities
	protected <R extends PrioRule> R prioritizedAbsolutePick(List<R> rules) {
		double sum = rules.stream().map(r -> r.priority).reduce(0.0, Double::sum);
		double x = (new Random()).nextDouble() * sum;
		double s = 0;

		for (R rule : rules) {
			s = s + rule.priority();
			if (x < s) { return rule; }
		}

		throw new RuntimeException("there are no rules to pick");
	}

	protected ActionPrio prioritizedRelativePick(List<ActionPrio> rules) {
		Map<Action, List<ActionPrio>> groups = rules.stream().collect(Collectors.groupingBy(x -> x.action));
		List<ActionPrio> absPicks =
				groups.
						keySet().
						stream().
						map(k -> new ActionPrio(k,
								groups.get(k).
										stream().
										map(m -> m.priority).
										reduce(1.0, (r1, r2) -> r1 * r2))).
						collect(Collectors.toList());

		return prioritizedAbsolutePick(absPicks);
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
				filter(r -> match(r.expression, item)). // only those that match
				collect(Collectors.toList());

		// Pick rule based on priority
		if (matches.size() > 0) {
			return prioritizedAbsolutePick(matches).generator.generate();
		}

		// else, return default behaviour
		return sText;
	}  

	static abstract class PrioRule {
		protected double priority = 0;
		protected boolean isValid = false;

		public double priority() { return priority; }
		public boolean isValid() { return isValid; }
	}

	static class ActionRule extends PrioRule {
		protected String sexpr;
		protected XQueryEvaluator expression;

		public ActionRule(String e, double p) {
			try {
				sexpr = e;
				expression = xqueryCompiler.compile(e).load();
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

	static class ActionPrio extends PrioRule {
		public final Action action;

		public ActionPrio(Action a, double prio) {
			action = a;
			priority = prio;
			isValid = true;
		}

		public String toString() {
			return "ActionPrio(" + action.get(Tags.OriginWidget) + "," + priority + ")";
		}
	}
	
	static class GenRule extends PrioRule {
		protected String sexpr;
		protected String gexpr;
		protected XQueryEvaluator expression;
		protected RgxGen generator;

		public GenRule(String e, String g, double p) {
			try {
				sexpr = e;
				gexpr = g;
				expression = xqueryCompiler.compile(e).load(); generator = new RgxGen(g);
				isValid = true; priority = p;
			}
			catch (Exception exception) {
				System.out.println("WARNING: invalid generator rule: " + exception.getMessage());
			}
		}

		public String toString() { return "GenRule(" +  sexpr + ", " + gexpr + "," + priority + ");"; }
	}

	static class FilterRule  {

		protected String sexpr;
		protected XQueryEvaluator expr;

		protected boolean isBlack;
		protected boolean isValid;

		public FilterRule(String e, boolean b) {
			sexpr = e;
			try {
				expr = xqueryCompiler.compile(e).load();
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
			//System.out.println("State model based action selection did not find an action. Using random action selection.");
			// if state model fails, using random:
			retAction = selectRuleAction(state, actions);
			if (retAction == null) {
				retAction = RandomActionSelector.selectAction(actions);
			}
		}

		return retAction;                 
	}
	                                                                              
	@Override
	protected boolean executeAction(SUT system, State state, Action action) {
		boolean success = super.executeAction(system, state, action);
		visitedActionsData.addAction((WdState)state, action);
		return success;
	}

	protected Action selectRuleAction(State state, Set<Action> actions) {
		List<ActionPrio> rules = new LinkedList();

		for (Action action : actions) {
			for (ActionRule r: actionRules) {
				Widget w = action.get(Tags.OriginWidget);

				Node n = w.get(WdTags.DOM);
				XdmItem item = saxonProcessor.newDocumentBuilder().wrap(n);

				try {
					if (match(r.expression, item)) {
						if (r.priority < 1.0) {
							/*System.out.println("RULE: " + r.sexpr);
							System.out.println("PRIO: " + r.priority);*/
							//printXML(n);
							
						}
						rules.add(new ActionPrio(action, r.priority));
					}

				}
				catch (Exception e) {
					throw new RuntimeException("Cannot evaluate xquery: ", e);
				}
			}
		}

		if (rules.size() > 0) {
			return prioritizedRelativePick(rules).action;
		}
		return null;
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

	class VisitedActionsData {
		Map<String, Integer> visitedAbstractActions = new HashMap();

		public void addAction(WdState s, Action a) {
			if (a.get(Tags.OriginWidget, null) != null) {
				String abstractActionId = a.get(Tags.OriginWidget).get(WdTags.ActionAbstractState);
				visit(visitedAbstractActions, abstractActionId);
			}
		}

		public void annotateVisits(Document d, WdState s) {
			for (Widget w: s) {
				Node n = w.get(WdTags.DOM);
				String abstractId = w.get(WdTags.ActionAbstractState, null);
				
				if (visitedAbstractActions.containsKey(abstractId)) {
					Element e = d.createElementNS(testarNamespace, "visits");
					e.setAttribute("count", visitedAbstractActions.get(abstractId).toString());
					n.appendChild(e);
				}
			}
		}

		private void visit(Map<String, Integer> m, String id) {
			m.put(id, m.getOrDefault(id, 0) + 1);
		}
	}
}
