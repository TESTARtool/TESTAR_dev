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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fruit.alayer.*;
import org.fruit.alayer.Action;
import org.fruit.alayer.actions.*;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.webdriver.*;
import org.fruit.alayer.webdriver.enums.WdRoles;
import org.fruit.alayer.webdriver.enums.WdTags;
import org.fruit.monkey.Main;
import org.fruit.monkey.Settings;
import org.openqa.selenium.logging.LogEntry;
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
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;

public class Protocol_webdriver_generic_ing extends WebdriverProtocol {
    protected VisitedActionsData visitedActionsData = new VisitedActionsData();
	
	protected static final Logger logger = LogManager.getLogger();
	
	private static DocumentBuilder documentBuilder;
	private static Processor saxonProcessor;
	private static XQueryCompiler xqueryCompiler;
	private static XsltCompiler xsltCompiler;
	
	private static Xslt30Transformer abstractDocumentTemplate;
	private static Xslt30Transformer abstractWidgetTemplate;
	private static Xslt30Transformer compileModelTemplate;
	private static Xslt30Transformer readRulesTemplate;
	
	private static String testarNamespace = "http://testar.org/state";
	private static List<GenRule> modelGenRules;

	private int nr_no_actions = 0;
	private boolean first_action = true;
	
	static {
		WdDriver.forceActivateTab = false;
		saxonProcessor = new Processor(false);
		xqueryCompiler = saxonProcessor.newXQueryCompiler();
		xsltCompiler = saxonProcessor.newXsltCompiler();
		
		// declare testar specific namespaces
		xqueryCompiler.declareNamespace("tst", testarNamespace);
		xqueryCompiler.declareNamespace("h", "https://testar.org/html");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {  documentBuilder = factory.newDocumentBuilder(); }
		catch (Exception e) { throw new RuntimeException("Cannot create documentBuilder", e); }
		
		abstractDocumentTemplate = compileTransformer("abstract-state.xsl");
		abstractWidgetTemplate = compileTransformer("abstract-widget.xsl");
		compileModelTemplate = compileTransformer("compile-model.xsl");
		readRulesTemplate = compileTransformer("read-rules.xsl");
	}
	
	protected List<ActionRule> actionRules = null;
	protected List<GenRule> generatorRules = null;
	protected List<FilterRule> filterRules = null;
	protected List<OracleRule> oracleRules = null;
	
	static String getSettingPath() {
		String path = Settings.getSettingsPath();
		if (path == null) {
			path = Main.settingsDir + Main.SSE_ACTIVATED;
		}
		return path;
	}
	
	static Xslt30Transformer compileTransformer(String file) {
		try {
			return xsltCompiler.compile(new StreamSource(new File(getSettingPath(), file))).load30();
		}
		catch (Exception e) {
			throw new RuntimeException("Could not compile" + file, e);
		}
	}
	
	static Document transformJson(String jsonFile, Xslt30Transformer template) {
		try {
			String modelFileName = (new File(getSettingPath(), jsonFile)).toURI().toString();
			Map<QName, XdmValue> parameters = new HashMap<>();
			parameters.put(new QName("json-file"), XdmValue.makeValue(modelFileName));
			
			template.setStylesheetParameters(parameters);
			
			Document d = documentBuilder.newDocument();
			Node n = d.createElement("root");
			d.adoptNode(n);
			
			Document r = documentBuilder.newDocument();
			
			template.transform(new DOMSource(d), new DOMDestination(r));
			return r;
		}
		catch (Exception e) {
			throw new RuntimeException("cannot transform", e);
		}
	}
	
	void consumeArcadeBuilderModel() {
		Document result = transformJson("model.json", compileModelTemplate);
		//printXML(result);
		XdmNode _this = saxonProcessor.newDocumentBuilder().wrap(result);
		
		modelGenRules = xmlToJava(_this, "//input-rule",
		node -> new GenRule(
			node.attribute("xquery"),
			node.attribute("regexp"),
			"5"));
	}
	
	void consumeRulesFile() {
		Document result = transformJson("rules.json", readRulesTemplate);
		//printXML(result);
		XdmNode _this = saxonProcessor.newDocumentBuilder().wrap(result);
		
		List<FilterRule> filterRules_ = xmlToJava(_this, "//ignore-filter",
			node -> new FilterRule(node.attribute("xquery")));
		
		List<ActionRule> actionRules_ = xmlToJava(_this, "//action-priority",
			node -> new ActionRule(
				node.attribute("xquery"),
				node.attribute("priority")));
		
		List<GenRule> generatorRules_ = xmlToJava(_this, "//generic-input",
			node -> new GenRule(
				node.attribute("xquery"),
				node.attribute("regexp"),
				node.attribute("priority")));
		
		List<OracleRule> oracleRules_ = xmlToJava(_this, "//oracle",
			node -> new OracleRule(
				node.attribute("xquery"),
				node.attribute("doc"),
				Double.parseDouble(node.attribute("severity"))));
		
		logger.info("[RULES] Finished reading rules file");
		
		filterRules = filterRules_;
		generatorRules = generatorRules_;
		actionRules = actionRules_;
		oracleRules = oracleRules_;
	}
	
	static <R> List<R> xmlToJava(XdmNode _this, String query, Function<XdmNode, R> mapper) {
		try {
			List<R> rlist = new LinkedList<>();
			
			XQueryEvaluator e = xqueryCompiler.compile(query).load();
			e.setContextItem(_this);
			e.setExternalVariable(new QName("this"), _this);
			
			XdmValue result = e.evaluate();
			
			for (XdmItem item: result) { rlist.add(mapper.apply((XdmNode)item)); }
			
			return rlist;
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
		consumeRulesFile();
	}

	protected void annotateWithXML(WdState s) {
		// check if WDState has top-level XML tag
		if (s.get(MyTags.XML, null) == null) {
			try {
				Document d = documentBuilder.newDocument();
				// recursive through all widgets
				Node n = toNode(d, s);
				d.appendChild(n);
				s.set(MyTags.XML, n);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Cannot create XML representation", e);
			}
		}
	}

	protected Node toNode(Document d, WdWidget w) {
		WdElement el = w.element;
		
		Element e =  d.createElement(escape(el.tagName));
		Map <String, String> map = el.attributeMap;

		for (String key: map.keySet()) { e.setAttribute(escape(key), map.get(key)); }

		if (el.textContent != null) { e.setTextContent(el.textContent); }
		if (el.value != null) { e.setAttribute("value", escape(el.value.toString())); }
		if (el.checked) { e.setAttribute("checked", "true"); }
		if (el.selected) { e.setAttribute("selected", "true"); }

		for (int i = 0; i < w.childCount() ; i++) { e.appendChild(toNode(d, w.child(i))); }

		w.set(MyTags.XML, e);

		return e;
	}
	
	/* TODO: correct tag-name, attribute-name escaping, for now we just replace with '-' */
	private String escape(String in) {
		final StringBuilder out = new StringBuilder();
		for (int i = 0; i < in.length(); i++) {
			final char ch = in.charAt(i);
			if (ch >= 32 && ch <= 127) out.append(ch);
			else out.append("-");
		}
		return out.toString();
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
		visitedActionsData.annotateVisits(d.getOwnerDocument(), (WdState)state);
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
				throw new RuntimeException("Cannot apply template", e);
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
		first_action = true;        // we are starting to take a first action
		nr_no_actions = 0;          // and we start counting states where there are no actions
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
		catch (Exception e) { throw new RuntimeException("Cannot evaluate", e); }
	}
	
	@Override  // Hack to inject XML state BEFORE getVerdict is called: we need XML state to calculate a verdict
	public void setStateForClickFilterLayerProtocol(State state) {
		super.setStateForClickFilterLayerProtocol(state);
		
		annotateWithXML((WdState)state); // annotate the Widget Tree with XML nodes
		
		// Append log
		List<LogEntry> entries = WdDriver.getLog().getAll();
		if (entries.size() > 0) {
			logger.info("[WebBrowser LOG] " + entries);
		}
		Node s = state.get(MyTags.XML);
		Document doc = s.getOwnerDocument();
		
		Node log = doc.createElementNS(testarNamespace, "log");
		s.appendChild(log);
		
		for (LogEntry e: entries) {
			Element entry = doc.createElement("entry");
			entry.setAttribute("timestamp", "" + e.getTimestamp());
			entry.setAttribute("level", e.getLevel().toString());
			entry.setAttribute("message", e.getMessage());
			log.appendChild(entry);
		}
	}
	@Override
	protected State getState(SUT system) throws StateBuildException {
		WdState state = (WdState)super.getState(system);
		
		return state;
	}

	@Override
	protected Verdict getVerdict(State state) {
		Verdict verdict = super.getVerdict(state);
		
		Verdict cachedVerdict = state.get(Tags.OracleVerdict, null);
		if (cachedVerdict == null) {
			if (state.get(MyTags.XML, null) != null) {
				for (Widget w : state) {
					Node n = w.get(MyTags.XML);
					XdmItem item = saxonProcessor.newDocumentBuilder().wrap(n);
					for (OracleRule r : oracleRules) {
						if (match(r.expression, item)) {
							logger.info("oracle rule match " + r.doc + ": " + r.sexpr);
							verdict = verdict.join(new Verdict(r.severity, r.doc));
						}
					}
				}
			}
		}
		else {
			return cachedVerdict;
		}

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
		List <InputRule> matches = generatorRules.stream().
				filter(r -> match(r.expression, item)).
			    map(r -> {
			    	try {
						return new InputRule(r.generator, Double.parseDouble(r.prio.evaluateSingle().getStringValue()));
					}
			    	catch (Exception e) {
			    		throw new RuntimeException("cannot set context", e);
					}
				}). // only those that match
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

		public double priority() { return priority; }
	}

	static class ActionRule {
		protected String sprio;
		protected String sexpr;
		protected XQueryEvaluator expression;
		protected XQueryEvaluator prio;

		public ActionRule(String e, String p) {
			try {
				sexpr = e;
				expression = xqueryCompiler.compile(xqueryLibrary() + e).load();
				prio = xqueryCompiler.compile(xqueryLibrary() + p).load();
				sprio = p;
			}
			catch (Exception ex) {
				throw new RuntimeException("Invalid action-priority rule", ex);
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
		}

		public String toString() {
			Object r = saxonProcessor.newDocumentBuilder().wrap(action.get(Tags.OriginWidget).get(MyTags.XML));
			return "ActionPrio(" + priority +"\n" + r + ")";
		}
	}
	
	static class InputRule extends PrioRule {
		protected RgxGen generator;
		
		public InputRule(RgxGen gen, Double prio) {
			generator = gen; priority = prio;
		}
		
	}
	
	static class OracleRule {
		protected String sexpr;
		protected String doc;
		protected double severity;
		
		protected XQueryEvaluator expression;
		
		public OracleRule(String e, String d, double s) {
			sexpr = e;
			doc = d;
			severity = s;
		
			try {
				expression = xqueryCompiler.compile(xqueryLibrary() + e).load();
			}
			catch (Exception ex) {
				throw new RuntimeException("Invalid oracle rule", ex);
			}
		}
		
		public String toString() { return "OracleRule(" +  sexpr + ", " + doc + "," + severity + ");"; }
	}
	
	static class GenRule  {
		protected String sexpr;
		protected String gexpr;
		protected String pexpr;
		
		protected XQueryEvaluator expression;
		protected XQueryEvaluator prio;
		protected RgxGen generator;
		
		public GenRule(String e, String g, String p) {
			try {
				sexpr = e;
				gexpr = g;
				pexpr = p;
				expression = xqueryCompiler.compile(xqueryLibrary() + e).load();
				generator = new RgxGen(g);
				prio = xqueryCompiler.compile(xqueryLibrary() + p).load();
			}
			catch (Exception ex) {
				throw new RuntimeException("Invalid generator rule", ex);
			}
		}

		public String toString() { return "GenRule(" +  sexpr + ", " + gexpr + "," + pexpr + ");"; }
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
			catch (Exception ex) {
				throw new RuntimeException("Invalid filter rule", ex);
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
		first_action = false;
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
					throw new RuntimeException("Cannot evaluate xquery " + r.sexpr, e);
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
		return (super.moreActions(state) && (nr_no_actions < 3)) || (!first_action);
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
		String f18 = "declare function h:inputRule($n, $i) { $n[contains(lower-case(@name), lower-case($i))] };";
		String f19 = "declare function h:formInputRule($n, $f, $i) " +
			"{ $n[contains(lower-case(@name), lower-case($i))]/ancestor::*[contains(lower-case(@name), lower-case($f))] };";
		
		return f1 + f2 + f3 + f4 + f5 + f6 + f7 + f8 + f9 + f10 + f15 + f16 + f17 + f18 + f19;
	}
}