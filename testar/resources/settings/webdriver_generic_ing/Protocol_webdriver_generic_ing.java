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
import es.upv.staq.testar.NativeLinker;
import net.sf.saxon.om.Item;
import net.sf.saxon.s9api.*;
import net.sf.saxon.value.BooleanValue;
import nl.ou.testar.RandomActionSelector;
import org.apache.commons.codec.binary.Base64;
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
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;

public class Protocol_webdriver_generic_ing extends WebdriverProtocol {
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

	private int nr_no_actions = 0;
	
	static {
		WdDriver.forceActivateTab = false;
		saxonProcessor = new Processor(false);
		xqueryCompiler = saxonProcessor.newXQueryCompiler();

		xqueryCompiler.declareNamespace("tst", testarNamespace);
		xqueryCompiler.declareNamespace("h", "https://testar.org/html");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {  documentBuilder = factory.newDocumentBuilder(); }
		catch (Exception e) { throw new RuntimeException("Cannot create documentBuilder: ", e); }

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
		catch (Exception e) { throw new RuntimeException("Could not compile: ", e); }
		
		consumeArcadeBuilderModel();
	}

	static void consumeArcadeBuilderModel() {
		String modelFileName = (new File(Settings.getSettingsPath(), "model.json")).toURI().toString();
		Map<QName, XdmValue> parameters = new HashMap<>();
		parameters.put(new QName("json-file"), XdmValue.makeValue(modelFileName));
		
		try {
			compileModelTemplate.setStylesheetParameters(parameters);
			
			Document d = documentBuilder.newDocument();
			Node n = d.createElement("root");
			d.adoptNode(n);
			
			Document r = documentBuilder.newDocument();
			compileModelTemplate.transform(new DOMSource(d), new DOMDestination(r));
			
			//printXML(r);
			
			// fetch all input-rules
			XQueryEvaluator e = xqueryCompiler.compile("//input-rule").load();
			XdmNode _this = saxonProcessor.newDocumentBuilder().wrap(r);
			e.setContextItem(_this);
			e.setExternalVariable(new QName("this"), _this);
			
			XdmValue result = e.evaluate();
			List<GenRule> gr = new LinkedList<>();
			
			for (XdmItem rule: result) {
				XdmNode node = (XdmNode)rule;
				gr.add(new GenRule(node.attribute("xquery"), node.attribute("regexp"), 5));
			}
			
			modelGenRules = gr;
		}
		
		catch (Exception e) { throw new RuntimeException("Could not compile: " + e); }
	}
	
	// We annotate each Widget with its XML representation and each Action with an Abstract State mapping
	static class MyTags extends TagsBase {
		public static final Tag<Node> XML = from("XML", Node.class);
		public static final Tag<String> ActionAbstractState = from("ActionAbstractState", String.class);
	}
	
	static void printXML(Node d) {
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			t.transform(new DOMSource(d), new StreamResult(System.out));
		}
		catch (Exception e) {
			throw new RuntimeException("Could not print XML", e);
		}
	}
	
	@Override
	protected void initialize(Settings settings) {
		NativeLinker.addWdDriverOS();
		super.initialize(settings);
		ensureDomainsAllowed();
		
		WdDriver.followLinks = true;
	}

	protected void annotateWithXML(WdState s) {
		// check if WDState has top-level XML tag
		if (s.get(MyTags.XML, null) == null) {
			try {
				Document d = documentBuilder.newDocument();
				// recursive through all widgets
				Node n = toNode(d, s);
				d.appendChild(n);
				s.set(MyTags.XML, d);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Cannot create XML representation ", e);
			}
		}
	}

	protected Node toNode(Document d, WdWidget w) {
		WdElement el = w.element;
		Element e = d.createElement(el.tagName);
		
		Map <String, String> map = el.attributeMap;

		for (String key: map.keySet()) { e.setAttribute(key, map.get(key)); }

		if (el.textContent != null) { e.setTextContent(el.textContent); }
		if (el.value != null) { e.setAttribute("value", el.value.toString()); }
		if (el.checked) { e.setAttribute("checked", "true"); }
		if (el.selected) { e.setAttribute("selected", "true"); }

		for (int i = 0; i < w.childCount() ; i++) { e.appendChild(toNode(d, w.child(i))); }

		w.set(MyTags.XML, e);

		return e;
	}
	
	private String hashString(String message, String algorithm) {
		try {
			MessageDigest digest = MessageDigest.getInstance(algorithm);
			return Base64.encodeBase64String(digest.digest(message.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception e) {
			throw new RuntimeException("Could not generate hash from String", e);
		}
	}

	public void buildActionsIdsAndAnnotate(State state, Set<Action> actions) {
		for (Action a: actions) { buildActionsIds(a); }
		
		// Additionally annotate XML with previously visited Actions
		Node d = state.get(MyTags.XML);
		visitedActionsData.annotateVisits((Document)d, (WdState)state);
	}

	public void buildActionsIds(Action action) {
		Widget widget = action.get(Tags.OriginWidget, null);
		if (widget != null) {
			Node d = widget.get(MyTags.XML);
			XdmValue source = saxonProcessor.newDocumentBuilder().wrap(d);

			try {
				XdmValue result = abstractWidgetTemplate.applyTemplates(source);
				widget.set(MyTags.ActionAbstractState, hashString(result.toString(), "MD5"));
			}
			catch (Exception e) {
				throw new RuntimeException("Cannot apply template: ", e);
			}
		}
		else throw new RuntimeException("Action: " + action + " has no origin!");
	}

	@Override
	protected SUT startSystem() throws SystemStartException {
		return super.startSystem();
	}
	
	@Override
	protected void beginSequence(SUT system, State state) {
		super.beginSequence(system, state);
		nr_no_actions = 0;
	}

	private boolean match(XQueryEvaluator comp, XdmItem item) {
		try {
			comp.setContextItem(item);
			comp.setExternalVariable(new QName("this"), item);
			XdmItem i = comp.evaluateSingle();
			
			if (i != null) {
				Item k = i.getUnderlyingValue();
				if (k instanceof BooleanValue) return ((BooleanValue)k).isIdentical(BooleanValue.TRUE);
				else return true;
			}
			return false;
		}
		catch (Exception e) { throw new RuntimeException("Cannot evaluate ", e); }
	}
	
	@Override
	protected State getState(SUT system) throws StateBuildException {
		WdState state = (WdState)super.getState(system);
		annotateWithXML(state); // annotate the Widget Tree with XML nodes
		return state;                                                                        
	}


	@Override
	protected Verdict getVerdict(State state) {
		Verdict verdict = super.getVerdict(state);

		return verdict;
	}

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
		
		// iterate through all widgets
		for (Widget widget : state) {
			WdWidget wdw = (WdWidget)widget;

			if (!widget.get(Enabled, true)) { continue; }

			if (widget.get(Tags.Role).isA(Roles.Process)) { continue; }

			if (widget.get(Blocked, false) && !widget.get(WdTags.WebIsShadow, false)) { continue; }

			if (blackListedWithFilter(widget)) { continue; }
			
			// type into text boxes
			if (isTypeable(widget)) {
				actions.add(new WdRemoteTypeAction(wdw, this.getRandomText(widget)));
			}

			// left clicks, but ignore links outside domain
			if (isClickable(widget) && !isLinkDenied(widget)) {
				actions.add(new WdRemoteClickAction(wdw));
			}
		}

		return actions;
	}

	Boolean blackListedWithFilter(Widget w) {
		Node n = w.get(MyTags.XML);
		XdmNode item = saxonProcessor.newDocumentBuilder().wrap(n);

		for (FilterRule f : filterRules) {
			try { if (match(f.expr, item)) return true; }
			catch (Exception e) { throw new RuntimeException("Cannot evaluate ", e); }
		}
		
		return false;
	}

	List <ActionRule> actionsRules(ActionRule ... rules) { return new LinkedList<>(Arrays.asList(rules)); }
	List <FilterRule> filterRules(FilterRule ... rules) { return new LinkedList<>(Arrays.asList(rules)); }
	List<GenRule> inputGenerators(GenRule ... rules) { return new LinkedList<>(Arrays.asList(rules)); }

	// Match the name of the element, all lower-case
	GenRule inputRule(String n, String g, int p) {
		String xquery = String.format(".[contains(lower-case(@name), '%s')]", n.toLowerCase());
		return new GenRule(xquery, g, p);
	}
	
	// Match the name of the element and the name of its (parents), all lower-case
	GenRule formInputRule(String f, String n, String g, int p) {
		String xquery = String.format(
			".[contains(lower-case(@name), '%s')]/" +
				"ancestor::*[contains(lower-case(@name), '%s')]", n.toLowerCase(), f.toLowerCase());
		return new GenRule(xquery, g, p);
	}

	// Randomly pick a Rule (with a priority) from a list of Rules, based on absolute priorities
	protected <R extends PrioRule> R prioritizedAbsolutePick(List<R> rules) {
		double sum = rules.stream().map(r -> r.priority).reduce(0.0, Double::sum);
		double x = (new Random()).nextDouble() * sum;
		double s = 0;

		for (R rule : rules) {
			s = s + rule.priority();
			if (x < s) { return rule; }
		}

		return rules.get(0);
	}

	// Randomly pick an Action (with a priority) from a list of Actions, based on their relative priorities
	protected ActionPrio prioritizedRelativePick(List<ActionPrio> actions) {
		Map<Action, List<ActionPrio>> groups = actions.stream().collect(Collectors.groupingBy(x -> x.action));
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

		Node n = w.get(MyTags.XML);
		XdmItem item = saxonProcessor.newDocumentBuilder().wrap(n);
		
		// Collect all matches rules
		List <GenRule> matches = generatorRules.stream().
				filter(GenRule::isValid).       		// only consider valid rules
				filter(r -> match(r.expression, item)). // only those that match
				collect(Collectors.toList());

		// Pick rule based on priority
		if (matches.size() > 0) {
			return prioritizedAbsolutePick(matches).generator.generate(new Random());
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

	static class ActionRule {
		protected String sprio = "";
		protected boolean isValid = false;
		protected String sexpr;
		protected XQueryEvaluator expression;
		protected XQueryEvaluator prio;

		public ActionRule(String e, String p) {
			try {
				sexpr = e;
				expression = xqueryCompiler.compile(xqueryLibrary() + e).load();
				prio = xqueryCompiler.compile(xqueryLibrary() + p).load();
				isValid = true;
				sprio = p;
			}
			catch (Exception exception) {
				System.out.println("WARNING: invalid action rule: " + exception.getMessage());
			}
		}
		public String toString() {
			return "ActionRule(" + sexpr + "," + sprio + ")";
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
			Object r = saxonProcessor.newDocumentBuilder().wrap(action.get(Tags.OriginWidget).get(MyTags.XML));
			return "ActionPrio(" + priority +"\n" + r + ")";
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
				expression = xqueryCompiler.compile(xqueryLibrary() + e).load(); generator = new RgxGen(g);
				isValid = true;
				priority = p;
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
		protected boolean isValid;

		public FilterRule(String e) {
			sexpr = e;
			try {
				expr = xqueryCompiler.compile(xqueryLibrary() + e).load();
				isValid = true;
			}
			catch (Exception exception) {
				System.out.println("WARNING: invalid filter rule: " + exception.getMessage());
			}
		}
		public String toString() { return "FilterRule(" +  sexpr + ");"; }
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
		
		if (element.isClickable) { return true; }

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
	
	@Override
	protected Action selectAction(State state, Set<Action> actions){
		buildActionsIdsAndAnnotate(state, actions);

		if (actions.size() == 0) {
			nr_no_actions += 1;
		}
		
		Action retAction = preSelectAction(state, actions);
		
		if (retAction== null) {
			retAction = stateModelManager.getAbstractActionToExecute(actions);
		}
		
		if(retAction==null) {
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
		if (success) visitedActionsData.addAction((WdState)state, action);
		return success;
	}

	/* We select a (random) Action, based on a list of ActionRule that have relative priority */
	protected Action selectRuleAction(State state, Set<Action> actions) {
		List<ActionPrio> rules = new LinkedList<>();

		for (Action action : actions) {
			for (ActionRule r: actionRules) {
				Widget w = action.get(Tags.OriginWidget);

				Node n = w.get(MyTags.XML);
				XdmItem item = saxonProcessor.newDocumentBuilder().wrap(n);

				try {
					if (match(r.expression, item)) {
						r.prio.setContextItem(item);
						r.prio.setExternalVariable(new QName("this"), item);

						XdmItem pp = r.prio.evaluateSingle();
						rules.add(new ActionPrio(action, Double.parseDouble(pp.getStringValue())));
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
	
	@Override
	protected boolean moreActions(State state) {
		return super.moreActions(state) && (nr_no_actions < 3);
	}

	@Override
	protected void finishSequence() {
		super.finishSequence();
	}
	
	@Override
	protected boolean moreSequences() {
		return super.moreSequences();
	}
	
	/**
	 * VisitedActionsData is a simple store of all actions that have been 'visited' (executed)
	 * An Action is (re-)identified via its ActionAbstractState tag.
 	 */
	static class VisitedActionsData {
		Map<String, Integer> visitedAbstractActions = new HashMap<>();

		public void addAction(WdState s, Action a) {
			if (a.get(Tags.OriginWidget, null) != null) {
				String abstractActionId = a.get(Tags.OriginWidget).get(MyTags.ActionAbstractState);
				visit(visitedAbstractActions, abstractActionId);
			}
		}

		public void annotateVisits(Document d, WdState s) {
			for (Widget w: s) {
				Node n = w.get(MyTags.XML);
				String abstractId = w.get(MyTags.ActionAbstractState, null);
				
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
	
	/* A poor man's library that is duplicated for every rule */
	static String xqueryLibrary() {
		String f1 = "declare function h:is-radio($n) { $n[(name() = 'input') and (@type = 'radio')] };";
		String f2 =	"declare function h:is-valid($n) { not($n[@aria-invalid = 'true']) };";
		String f3 =	"declare function h:is-invalid($n) { $n[@aria-invalid = 'true'] };";
		String f4 = "declare function h:is-option($n) { $n[name() = 'option'] };";
		String f5 = "declare function h:is-submit($n) { $n[@type = 'submit'] };";
		String f6 = "declare function h:is-select($n) { $n[name() = 'select'] };";
		String f7 = "declare function h:is-text($n) { $n[@type = 'text'] };";
		String f8 = "declare function h:is-radiogroup($n) { $n[@role = 'radiogroup'] };";
		String f9 = "declare function h:sibling-radios($n) { $n/ancestor::form//*[h:is-radio(.) and ($n/@name = @name)] };";
		String f10 = "declare function h:sibling-options($n) { $n/ancestor::select//*[h:is-option(.)] }; ";
		String f15 = "declare function h:select-is-empty($n) { h:is-select($n) and (string-length($n/@value) = 0) };";
		String f16 = "declare function h:text-is-empty($n) { h:is-text($n) and (string-length($n/@value) = 0) };";
		String f17 = "declare function h:radiogroup-is-empty($n) { h:is-radiogroup($n) and $n//*[h:is-radio(.) and not(@checked = 'true')] };";
		
		return f1 + f2 + f3 + f4 + f5 + f6 + f7 + f8 + f9 + f10 + f15 + f16 + f17;
	}
	
	List<ActionRule> actionRules() {
		return actionsRules(
			new ActionRule("'true'", "1"),                  // Default priority
			new ActionRule(".[not(@type = 'submit')] and " +
				"not(ancestor-or-self::*[h:is-invalid(.)]) and " +
				"tst:visits[@count > 0]",
				"1 div (1 + count(tst:visits))"),  // if action visited and not submit or invalid
			new ActionRule(".[@type = 'submit']",
				"1 + count(ancestor::form//*[" +
					"(h:radiogroup-is-empty(.) or h:text-is-empty(.) or h:select-is-empty(.)) and " +
					"h:is-valid(.)" +
					"])"),
			new ActionRule("h:is-invalid(.)", "3.0"), // boost invalid
			new ActionRule("h:is-radio(.) and ancestor::*[h:is-radiogroup(.) and h:is-invalid(.)]", "3.0"), // boost invalid
			new ActionRule("h:is-radio(.)",
				"1 div count(h:sibling-radios(.))"), // weighted radio button
			new ActionRule("h:is-option(.)",
				"1 div count(h:sibling-options(.))"), // weighted option
			new ActionRule("h:is-radio(.) and ancestor::*[h:is-radiogroup(.) and not(h:radiogroup-is-empty(.)) and h:is-valid(.)]", "0.3"),
			new ActionRule("h:is-text(.) and not(h:text-is-empty(.)) and h:is-valid(.)", "0.3"),
			new ActionRule("h:is-select(.) and not(h:select-is-empty(.)) and h:is-valid(.)", "0.3")
		);
	}
	
	List<GenRule> inputGenerators() {
		List<GenRule> l = inputGenerators(
			new GenRule("'true'", "([0-9]{5,10})|([0-9]{1,5})|([A-Za-z ]{5,20}|([A-Za-z0-9 ]{5,10})|([A-Za-z0-9 ]{10,20})", 1),                        // Generic input
			
			inputRule("firstName", "(Robbert)|(Mark)", 5),
			inputRule("lastName", "(van Dalen)|(Bommel)", 5),
			inputRule("surName", "(van Dalen)|(Bommel)", 5),
			inputRule("email", "([a-z0-9._%+-]{4,15}@[a-z0-9.-]{5,10}\\.[a-z]{2,4})|(bla@company\\.com)|(sut@bla\\.nl)", 5),
			inputRule("enterpriseNumber", "(0415580365)|(0[0-9]{9})", 5),
			inputRule("age", "[1-8][0-9]", 5),
			inputRule("income", "[1-9][0-9]{4}", 5),
			inputRule("profit", "[1-9][0-9]{4}", 5),
			formInputRule("income", "years[]", "[1-9][0-9]{4}", 5),
			inputRule("monthlyIncome", "[1-7][0-9]{3}", 5),
			inputRule("studyLoan", "[1-3][0-9]{4}", 5),
			inputRule("liabilities", "[1-3][0-9]{4}", 5),
			inputRule("alimony", "[0-9]{3}", 5),
			inputRule("annualTurnover", "[1-9][0-9]{4,6}", 5),
			inputRule("endBalance", "[1-9][0-9]{4,6}", 5),
			inputRule("availableCash", "[1-9][0-9]{4,6}", 5),
			inputRule("shareholderSupport", "[1-9][0-9]{4,6}", 5),
			inputRule("newLoan", "[1-9][0-9]{4,6}", 5),
			inputRule("needAmount", "[1-9][0-9]{4,6}", 5),
			inputRule("needDuration", "[1-3]|[0-9]", 5),
			inputRule("numberEmployees", "[1-9]{2,4}", 5),
			
			new GenRule("ancestor::ing-flow-form[contains(@name, 'expectedRevenue')]", "[1-9][0-9]{4}", 5),
			new GenRule("ancestor::ing-flow-form[contains(@name, 'expectedCosts')]", "[1-9][0-9]{4}", 5),
			new GenRule("ancestor::ing-flow-form[contains(@name, 'otherPaymentPostponement')]", "[1-9][0-9]{4}", 5),
			new GenRule("ancestor::ing-flow-form[contains(@name, 'ingLoanRepayment')]", "[1-9][0-9]{4,5}", 5)
		);
		l.addAll(modelGenRules);
		return l;
	}
	
	List<FilterRule> filterRules() {
		return filterRules(
			new FilterRule("@disabled"),
			new FilterRule(".[name() = 'label']"),
			new FilterRule(".[name() = 'select']"),  // we select options, not the select widget itself
			new FilterRule("ancestor-or-self::*[@aria-hidden = 'true']"),  // ignore aria-hidden
			new FilterRule(".[(name() = 'a') and (not(@href))]"), // ignore links without href
			
			new FilterRule("ancestor::header"), // ignore header
			new FilterRule("ancestor::ing-feat-sc-house-next-step-based-on-house-card"), // ignore next step
			new FilterRule("ancestor-or-self::ing-button[contains(@id, 'moreInfoButton')]"), // ignore more info
			
			new FilterRule(".[@id = 'action-stop']"), // ignore stop button
			new FilterRule(".[@id = 'action-back']"), // ignore back button
			
			new FilterRule(".[name() = 'body']"), // ignore body
			new FilterRule("ancestor::*[contains(@slot, 'progress')]"), // ignore progress section
			new FilterRule("ancestor::*[contains(@class, 'progress')]"), // ignore progress section
			new FilterRule("ancestor::section[@class = 'demo-menu']"), // ignore demo
			
			new FilterRule(".[contains(@href, 'bel-me-nu')]"), // ignore outside links
			new FilterRule(".[ends-with(@href, 'hypotheek-berekenen/')]"),
			new FilterRule(".[@target = '_blank']")
		);
	}
}