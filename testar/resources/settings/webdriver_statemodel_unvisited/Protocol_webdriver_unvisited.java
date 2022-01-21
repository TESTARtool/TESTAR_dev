/**
 * 
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
import org.fruit.alayer.*;
import org.fruit.alayer.actions.*;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.webdriver.*;
import org.fruit.alayer.webdriver.enums.WdRoles;
import org.fruit.monkey.Settings;
import org.testar.protocols.WebdriverProtocol;
import nl.ou.testar.StateModel.*;
import nl.ou.testar.StateModel.Persistence.OrientDB.*;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.EntityManager;

import java.util.*;
import java.lang.Thread;
import java.net.*;

import org.w3c.dom.*;

import javax.xml.parsers.*;
import java.io.*;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;

import com.orientechnologies.orient.core.exception.OConcurrentModificationException;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.db.*;
import com.orientechnologies.orient.core.sql.executor.*;
import com.orientechnologies.orient.core.record.*;

public class Protocol_webdriver_unvisited extends WebdriverProtocol {
	OrientDBManager odb;
	ModelManager m;
	String nodeName = "";
	EntityManager em;
	String selectedAction = null;
	// Connection connection;
	OrientDB database;

    // List of atributes to identify and close policy popups
    // Set to null to disable this feature
    private static Map<String, String> policyAttributes = new HashMap<String, String>() {
        {
            put("id", "sncmp-banner-btn-agree");
        }
    };

	@Override
	protected void initialize(Settings settings) {
	    super.initialize(settings);

	    System.out.println("State model constructor");

	    m = (ModelManager) stateModelManager;
	    if (m.persistenceManager != null) {
	        odb = (OrientDBManager) m.persistenceManager;
	        em = odb.entityManager;
			Config config = OrientDBManagerFactory.getDatabaseConfig(settings);
			String connectionString = config.getConnectionType() + ":" + (config.getConnectionType().equals("remote") ? config.getServer() : config.getDatabaseDirectory()) + "/";
			database = new OrientDB(connectionString, OrientDBConfig.defaultConfig());

	        Random r = new Random();
	        nodeName = System.getenv("HOSTNAME") + "_" + r.nextInt(10000);
	        System.out.println("nodeName = " + nodeName);
	        ODatabaseSession dbSession = createDatabaseConnection();
	        ExecuteCommand(dbSession, "create vertex BeingExecuted set node = '" + nodeName + "'").close();
	        dbSession.close();
	    }
	}

	/*
	 * private void getLogin() { Form form = new Form(); form.add("x", "foo");
	 * form.add("y", "bar");
	 * 
	 * ClientResource resource = new
	 * ClientResource("http://localhost:8080/someresource");
	 * 
	 * Response response = resource.post(form.getWebRepresentation());
	 * 
	 * if (response.getStatus().isSuccess()) { System.out.println("Success! " +
	 * response.getStatus()); System.out.println(response.getEntity().getText()); }
	 * else { System.out.println("ERROR! " + response.getStatus());
	 * System.out.println(response.getEntity().getText()); } }
	 */

	/**
	 * Called once during the life time of TESTAR This method can be used to perform
	 * initial setup work
	 *
	 * @param settings the current TESTAR settings as specified by the user.
	 */
	ODatabaseSession createDatabaseConnection() {
		// connection = new Connection(database, OrientDBManagerFactory.getDatabaseConfig(settings));
		Config config = OrientDBManagerFactory.getDatabaseConfig(settings);
		ODatabaseSession dbSession = database.open(config.getDatabase(), config.getUser(), config.getPassword());
		System.out.println("Own database connection created");
		return dbSession;
	}

	@Override
	protected void beginSequence(SUT system, State state) {
		super.beginSequence(system, state);
		System.out.println("BeginSequence");
		_moreActions = true;
		stopSequences = false;
	}

	boolean hasHadActionsInDb = false;
	boolean stopSequences = false;

	public OResultSet ExecuteCommand(ODatabaseSession db, String actie) {
		System.out.println("ExecuteCommand " + actie);
		boolean repeat = false;
		do {
			repeat = false;
			try {
				System.out.println("dbSession = " + db);
				return db.command(actie);
			} catch (OConcurrentModificationException ex) {
				repeat = true;
				try {
					Thread.sleep(2000);
				} catch (Exception e) {
				}

			}
		} while (repeat);
		return null;
	}

	public OResultSet ExecuteQuery(ODatabaseSession db, String actie) {
		System.out.println("ExecuteQuery " + actie);
		boolean repeat = false;
		do {
			repeat = false;
			try {
				System.out.println("dbSession = " + db);

				return db.query(actie);
			} catch (OConcurrentModificationException ex) {
				repeat = true;
				try {
					Thread.sleep(2000);
				} catch (Exception e) {
				}

			}
		} while (repeat);

		return null;
	}

	/**
	 * This method is used by TESTAR to determine the set of currently available
	 * actions. You can use the SUT's current state, analyze the widgets and their
	 * properties to create a set of sensible actions, such as: "Click every Button
	 * which is enabled" etc. The return value is supposed to be non-null. If the
	 * returned set is empty, TESTAR will stop generation of the current action and
	 * continue with the next one.
	 *
	 * @param system the SUT
	 * @param state  the SUT's current state
	 * @return a set of actions
	 */

	/*
	 * public CompoundAction fillForm(HashMap<org.fruit.alayer.Widget, String>
	 * values) { StdActionCompiler ac = new AnnotatingActionCompiler();
	 * 
	 * CompoundAction a = CompoundAction.Builder(); values.forEach((k,v)->
	 * a.add(ac.clickTypeInto(k, v, true)));
	 * 
	 * return a; }
	 */
	@Override
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {
		// Kill unwanted processes, force SUT to foreground
		System.out.println("Protocol file: deriveActions: unvisited actions");
		Set<Action> actions = super.deriveActions(system, state);

		// create an action compiler, which helps us create actions
		// such as clicks, drag&drop, typing ...
		StdActionCompiler ac = new AnnotatingActionCompiler();

		// Check if forced actions are needed to stay within allowed domains
		if (WdDriver.getCurrentUrl().contains("wsdl") || WdDriver.getCurrentUrl().contains("wadl")) {
			return new HashSet<>(Collections.singletonList(new WdHistoryBackAction()));
		}

		Set<Action> forcedActions = detectForcedActions(state, ac);
		if (forcedActions != null && forcedActions.size() > 0) {
			System.out.println("Executing forced action");
			return forcedActions;
		}

		// iterate through all widgets
		for (org.fruit.alayer.Widget widget : state) {
			// only consider enabled and non-tabu widgets

			// System.out.println("DeriveAction widget = "+widget+" class =
			// "+widget.getClass());
			WdWidget wd = (WdWidget) widget;
			WdElement element = wd.element;
			// System.out.println("DeriveAction widget = "+widget+" class =
			// "+widget.getClass()+" wd = "+wd+" elem= "+element.tagName);
			// if (isForm(widget))
			// {
			// System.out.println("Form gevonden");
			// fillForm(actions, ac, state, wd,null);

			// }
			if (!widget.get(Enabled, true) || blackListed(widget)) {
				continue;
			}

			// slides can happen, even though the widget might be blocked
			// addSlidingActions(actions, ac, scrollArrowSize, scrollThick, widget, state);

			// If the element is blocked, Testar can't click on or type in the widget
			if (widget.get(Blocked, false)) {
				continue;
			}

			// type into text boxes
			// if (isAtBrowserCanvas(widget) && isTypeable(widget) && (whiteListed(widget)
			// || isUnfiltered(widget))) {
			// actions.add(ac.clickTypeInto(widget, this.getRandomText(widget), true));
			// }

			// left clicks, but ignore links outside domain
			if (isAtBrowserCanvas(widget) && isClickable(widget) && (whiteListed(widget) || isUnfiltered(widget))) {
				if (!isLinkDenied(widget) && !mijnIgnore(widget)) {
					actions.add(ac.leftClickAt(widget));
				}
			}
		}

		if (actions.size() == 0) {
			System.out.println("Actions.size == 0; Return historyback and done with DeriveActions");
			return new HashSet<>(Collections.singletonList(new WdHistoryBackAction()));
		}

		System.out.println("Done with DeriveActions");

		return actions;
	}

	public boolean mijnIgnore(org.fruit.alayer.Widget widget) {
		String linkUrl = widget.get(Tags.ValuePattern, "");

		if (linkUrl.contains("wsdl") || linkUrl.contains("parasoft.com") || linkUrl.contains("/services/")
				|| linkUrl.contains("api-docs") || linkUrl == "" || linkUrl.contains(".pfx") || linkUrl.contains("wadl")
				|| linkUrl.contains("xml")) {
			System.out.println("ignore " + linkUrl);
			return true;
		}

		System.out.println("accept " + linkUrl);
		return false;
	}

	public HashMap<String, String> readFormFile(String fileName) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new File(fileName));
			document.getDocumentElement().normalize();
			Element root = document.getDocumentElement();
			System.out.println("readFormFile");
			NodeList items = root.getChildNodes();
			HashMap<String, String> result = new HashMap<>();

			for (int i = 0; i < items.getLength(); i++) {
				Node item = items.item(i);
				Element node = (Element) item;
				String value = node.getTextContent();
				// System.out.println(node.getNodeName() +"=" +value);
				result.put(node.getNodeName(), value);
			}

			System.out.println("End form files");
			return result;
		} catch (Exception e) {
		}
		return null;
	}

	public void storeToFile(String fileName, HashMap<String, String> data) {
		String result = "<form><performSubmit>true</performSubmit>";

		for (Map.Entry<String, String> entry : data.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			result += "<" + key + ">" + value + "</" + key + ">";
		}
		result += "</form>";
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
			writer.write(result);

			writer.close();
		} catch (Exception e) {
		}
		// System.out.println(result);
	}

	boolean inForm = false;

	@Override
	protected boolean blackListed(org.fruit.alayer.Widget w) {
		if (inForm)
			return false;

		return super.blackListed(w);
	}

	public int buildForm(CompoundAction.Builder caB, WdWidget widget, HashMap<String, String> fields, boolean storeFile,
			StdActionCompiler ac) {
		int sum = 0;
		WdElement element = widget.element;
		String defaultValue = "write-random-genenerated-value";
		if (isTypeable(widget)) {
			// System.out.println("field "+element.name+" found it");
			if (storeFile) {
				fields.put(element.name, defaultValue);
			}
			if (fields.containsKey(element.name) && fields.get(element.name) != null) {
				caB.add(ac.clickTypeInto(widget, fields.get(element.name), true), 2);
				sum += 2;
			}
		}

		String baseElem = element.tagName;
		// System.out.println("check children: current element "+element.tagName+" number
		// childs: "+widget.childCount());
		for (int i = 0; i < widget.childCount(); i++) {
			WdWidget w = widget.child(i);
			// System.out.println("child "+i+" of element "+baseElem);
			// WdElement
			element = w.element;

			// System.out.println("buildForm :"+ element.tagName +" "+element.name);
			if (isTypeable(w)) {
				// System.out.println("field "+element.name+" found it");
				if (storeFile) {
					fields.put(element.name, defaultValue);
				}
				if (fields.containsKey(element.name) && fields.get(element.name) != null) {
					caB.add(ac.clickTypeInto(widget, fields.get(element.name), true), 2);
					sum += 2;
					// System.out.println("element.name in the fields; add to caB som+2 som =
					// "+sum);
				}
			} else {
				// System.out.println("Element "+element.tagName+" is not typeable");
				sum += buildForm(caB, widget.child(i), fields, storeFile, ac);
			}
		}
		// System.out.println("done with baseElem "+baseElem+" sum = "+sum);
		return sum;
	}

	public void fillForm(Set<Action> actions, StdActionCompiler ac, State state, WdWidget widget,
			HashMap<String, String> fields) {
		// System.out.println("Url = "+WdDriver.getCurrentUrl());
		inForm = true;
		if (fields == null) {
			fields = new HashMap<String, String>();
		}

		URI uri = null;
		try {
			uri = new URI(WdDriver.getCurrentUrl());
		} catch (Exception e) {
		}

		String formId = widget.getAttribute("name");
		if (formId == null) {
			formId = "";
		}

		String path = (uri.getPath() + "/" + formId).replace("/", "_") + ".xml";
		System.out.println("Look for file " + path);
		File f = new File(path);
		Boolean storeFile = true;
		if (f.exists()) {
			storeFile = false;
			fields = readFormFile(path);
			System.out.println("File exists, read the data from file");
		}

		CompoundAction.Builder caB = new CompoundAction.Builder();
		int sum = buildForm(caB, widget, fields, storeFile, ac);

		if (fields.containsKey("performSubmit")) {
			boolean submit = Boolean.getBoolean(fields.get("performSubmit"));
			if (submit && formId != "") {
				caB.add(new WdSubmitAction(formId), 2);
			}
		}
		if (storeFile) {
			storeToFile(path, fields);
		}
		if (sum > 0) {
			CompoundAction ca = caB.build();
			actions.add(ca);
		}
		inForm = false;
		System.out.println("fillForm ready");
	}

	boolean _moreActions = true;

	@Override
	protected boolean moreActions(State state) {
		System.out.println("MoreActions: _moreActions = " + _moreActions);
		return _moreActions;
	}

	boolean stop = false;

	@Override
	protected boolean moreSequences() {
		boolean result = (CountInDb("unvisitedabstractaction") > 0) || !stop;

		System.out.println("moreSequences: " + result);
		return result;
	}

	@Override
	protected boolean isClickable(org.fruit.alayer.Widget widget) {
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
	protected boolean isForm(org.fruit.alayer.Widget widget) {
		Role r = widget.get(Tags.Role, Roles.Widget);
		if (Role.isOneOf(r, new Role[] { WdRoles.WdFORM })) {
			return r.equals(WdRoles.WdFORM);
		}
		return false;
	}

	@Override
	protected boolean isTypeable(org.fruit.alayer.Widget widget) {
		Role role = widget.get(Tags.Role, Roles.Widget);
		if (Role.isOneOf(role, NativeLinker.getNativeTypeableRoles())) {
			// Input type are special...
			if (role.equals(WdRoles.WdINPUT)) {
				String type = ((WdWidget) widget).element.type.toLowerCase();
				return WdRoles.typeableInputTypes().contains(type);
			}
			return true;
		}
		return false;
	}

	/**
	 * Select one of the available actions using an action selection algorithm (for
	 * example random action selection)
	 *
	 * @param state   the SUT's current state
	 * @param actions the set of derived actions
	 * @return the selected action (non-null!)
	 */

	private Boolean finishedAction = false;

	public ArrayList<String> GetUnvisitedActionsFromDatabase(String currentAbstractState) {
		System.out.println("GetUnvisitedActionsFromDatabase");
		ArrayList<String> result = new ArrayList<>();
		String sql = "SELECT expand(path) FROM (  SELECT shortestPath($from, $to) AS path   LET     $from = (SELECT FROM abstractstate WHERE stateId='"
				+ currentAbstractState + "'),     $to = (SELECT FROM BlackHole)   UNWIND path)";
		System.out.println(sql);
		OResultSet rs = null;
		ODatabaseSession db = createDatabaseConnection();
		try {
			rs = ExecuteQuery(db, sql);

			while (rs.hasNext()) {
				OResult item = rs.next();
				if (item.isVertex()) {
					System.out.println("Item is a vertex");
					Optional<OVertex> optionalVertex = item.getVertex();

					OVertex nodeVertex = optionalVertex.get();
					for (OEdge edge : nodeVertex.getEdges(ODirection.OUT, "UnvisitedAbstractAction")) {
						result.add(edge.getProperty("actionId"));
						System.out.println("Edge " + edge + " found with ID = " + edge.getProperty("actionId"));
					}

				}

				System.out.println("friend: " + item);
			}
		} catch (Exception e) {
			System.out.println("Exception during GetUnvisitedActionsFromDatabase " + e);
			e.printStackTrace();
		} finally {
			rs.close();
			db.close();
		}

		System.out.println("Ready with pick up actions");
		return result;
	}

	// TODO: Check if rename aantal to number affects the sql query
	public long CountInDb(String table) {
		long aantal = 0;
		String sql = "SELECT count(*) as aantal from " + table;
		ODatabaseSession db = createDatabaseConnection();
		try {
			OResultSet rs = ExecuteQuery(db, sql);
			OResult item = rs.next();
			aantal = item.getProperty("aantal");
			rs.close();
			System.out.println(sql + "  aantal =  " + aantal);
		} finally {
			db.close();
		}
		if (aantal > 0) {
			hasHadActionsInDb = true;
		}

		return aantal;
	}

	public void UpdateAbstractActionInProgress(String actionId) {
		ODatabaseSession db = createDatabaseConnection();
		try {
			System.out.println("action AbstractID = " + actionId);

			String sql = "update edge UnvisitedAbstractAction set in = (SELECT FROM BeingExecuted WHERE node='"
					+ nodeName + "') where actionId='" + actionId + "'";
			System.out.println("Execute" + sql);

			ExecuteCommand(db, sql);
		} catch (Exception e) {
			System.out.println("Can not update unvisitedAbstractAction; set selectedAction to null");
			selectedAction = null;
		} finally {
			db.close();
		}
	}

	public HashMap<String, Action> ConvertActionSetToDictionary(Set<Action> actions) {
		System.out.println("Convert Set<Action> to HashMap containing actionIds as keys");
		HashMap<String, Action> actionMap = new HashMap<>();
		ArrayList<Action> actionList = new ArrayList<>(actions);
		for (Action a : actionList) {
			System.out.println(
					"Add action " + a.get(Tags.AbstractIDCustom) + "  to actionMap; description = " + a.get(Tags.Desc));
			actionMap.put(a.get(Tags.AbstractIDCustom), a);
		}
		System.out.println("ActionMap initialized");
		return actionMap;
	}

	public String selectRandomAction(ArrayList<String> actions) {
		long graphTime = System.currentTimeMillis();
		Random rnd = new Random(graphTime);

		String ac = actions.get(rnd.nextInt(actions.size()));
		UpdateAbstractActionInProgress(ac);
		System.out.println("Update action " + ac + " from BlackHole to BeingExecuted");
		return ac;
	}

	int cyclesWaitBeforeNewAction = 0;

	String lastState = "";
	int sameState = 0;

	private String getNewSelectedAction(State state, Set<Action> actions) {
		String result = null;
		System.out.println("getNewSelectedAction state = " + state.get(Tags.AbstractIDCustom));
		Boolean ok = false;
		do {
			try {
				ArrayList<String> availableActionsFromDb = GetUnvisitedActionsFromDatabase(
						state.get(Tags.AbstractIDCustom));

				System.out.println(
						"Number of shortest path actions available in database: " + availableActionsFromDb.size());

				if (availableActionsFromDb.size() >= 1) {
					ok = true;
					String action = selectRandomAction(availableActionsFromDb);
					System.out.println("Action from database selected: action = " + action);
					return action;
				}

				long numberAbstractActions = CountInDb("abstractstate");

				System.out.println(
						"No actions available in database; abstract states in database = " + numberAbstractActions);

				if (numberAbstractActions == 0) {
					Action a = super.selectAction(state, actions);
					String action = a.get(Tags.AbstractIDCustom);
					System.out.println(
							"Since this is the first run and no abstractactions exists take a random action; statemodel is probably lagging action = "
									+ action);
					return action;
				}

				// System.out.println("Not allowed to be here! or really done");
				_moreActions = false;
				stop = true;
				Action a = super.selectAction(state, actions);
				String action = a.get(Tags.AbstractIDCustom);
				System.out.println("Just return random action and stop; action = " + action);
				return action;

			} catch (Exception e) {
				int sleepTime = new Random(System.currentTimeMillis()).nextInt(5000);
				System.out.println("Exception while getting action; Wait " + sleepTime + " ms " + e);

				ok = false;
				try {
					Thread.sleep(sleepTime);
				} catch (Exception th) {

				}
			}

		} while (!ok);
		return result;
	}

	class TmpData {
		public TmpData(ORecordId rid, String stateId) {
			this.stateId = stateId;
			this.rid = rid.toString();
			System.out.println("TmpData " + stateId + "  " + rid);
		}

		public String stateId;
		public String rid;
	}

	public void ReturnActionToBlackHole() {
		ODatabaseSession db = createDatabaseConnection();
		try {
			String sql = "update unvisitedabstractaction set in = (select from blackhole) where actionId='"
					+ selectedAction + "'";
			System.out.println("Return action to blackhole from beingexecuted: " + selectedAction + " sql = " + sql);
			ExecuteCommand(db, sql);
		} catch (Exception e) {
			System.out.println("Not possible to return selectedAction " + selectedAction + "  to blackhole" + e);
		} finally {
			db.close();
		}
	}

	public Action traversePath(State state, Set<Action> actions) {

		String q1 = "select stateId from abstractstate where @rid in (select outV() from UnvisitedAbstractAction where actionId = '"
				+ selectedAction + "')";

		String destinationStateId = "";
		ODatabaseSession db = createDatabaseConnection();

		OResultSet destinationStatResultSet = ExecuteQuery(db, q1);
		if (destinationStatResultSet.hasNext()) {
			OResult item = destinationStatResultSet.next();
			System.out.println("destinationResultSet item = " + item);
			// Optional<OVertex> optionalVertex = item.getProperty("stateId");
			destinationStateId = item.getProperty("stateId");
			System.out.println("traversePath: onderweg naar " + destinationStateId);
			destinationStatResultSet.close();
			db.close();
		} else {
			System.out.println(
					"State is stuck beacuse the unvisitedaction not found; set selectedAction to null; run historyback now");
			destinationStatResultSet.close();
			ReturnActionToBlackHole();
			selectedAction = null;
			return new WdHistoryBackAction();
		}

		// get stateId from q1

		// SELECT @rid, stateId from (
		// SELECT expand(path) FROM ( SELECT shortestPath($from,
		// $to,'OUT','AbstractAction') AS path LET $from = (SELECT FROM abstractstate
		// WHERE stateId='SAC1jp4oysed31697927673'), $to = (SELECT FROM abstractstate
		// Where stateId='SACwpszr27b61710690312') UNWIND path))
		String q2 = "SELECT @rid, stateId from (SELECT expand(path) FROM (  SELECT shortestPath($from, $to,'OUT','AbstractAction') AS path LET $from = (SELECT FROM abstractstate WHERE stateId='"
				+ state.get(Tags.AbstractIDCustom) + "'), $to = (SELECT FROM abstractstate Where stateId='"
				+ destinationStateId + "') UNWIND path))";
		db = createDatabaseConnection();
		OResultSet pathResultSet = ExecuteQuery(db, q2);
		Vector<TmpData> v = new Vector<>();

		while (pathResultSet.hasNext()) {
			OResult item = pathResultSet.next();
			v.add(new TmpData(item.getProperty("@rid"), item.getProperty("stateId")));

		}
		pathResultSet.close();
		db.close();

		if (v.size() < 2) {
			System.out.println(
					"There is no path! Execute super.selectAction; Also end sequence by setting _moreActions=false");
			ReturnActionToBlackHole();
			selectedAction = null;
			_moreActions = false;
			return super.selectAction(state, actions);
		}

		// Find an abstractaction to perform
		String q3 = "select from abstractaction where out = " + v.get(0).rid + " and in = " + v.get(1).rid;

		String abstractActionId = "";
		HashMap<String, Action> availableActions = ConvertActionSetToDictionary(actions);
		db = createDatabaseConnection();
		OResultSet abstractActionResultSet = ExecuteQuery(db, q3);
		while (abstractActionResultSet.hasNext()) {
			abstractActionId = abstractActionResultSet.next().getProperty("actionId");
			System.out.println("traversePath: use action " + abstractActionId);

			System.out.println("Check if " + abstractActionId + " is available");
			if (availableActions.containsKey(abstractActionId)) {
				System.out.println(
						"Action " + abstractActionId + " is available in the avilableActions; this is being executed");
				abstractActionResultSet.close();
				db.close();
				return availableActions.get(abstractActionId);
			}
		}
		abstractActionResultSet.close();
		db.close();

		System.out.println("Action that needs to be made does not exist");
		ReturnActionToBlackHole();
		selectedAction = null;

		return super.selectAction(state, actions);
	}

	@Override
	protected Action selectAction(State state, Set<Action> actions) {
		// Call the preSelectAction method from the AbstractProtocol so that, if
		// necessary,
		// unwanted processes are killed and SUT is put into foreground.
		Action retAction = preSelectAction(state, actions);

		if (retAction != null) {
			return retAction;
		}

		// First check whether we do have a selected action; if not select one
		if (selectedAction == null) {
			selectedAction = getNewSelectedAction(state, actions);
		}

		if (selectedAction != null) {
			System.out.println("selectedAction != null actions.length = " + actions.size() + " state id = "
					+ state.get(Tags.AbstractIDCustom));
			HashMap<String, Action> actionMap = ConvertActionSetToDictionary(actions);
			System.out.println("actionMap.size() = " + actionMap.size());
			// select path towards the selected action
			if (actionMap.containsKey(selectedAction)) {
				System.out.println("Needed action is currently available, so select this action");
				// Perform desired action since it's available from this point
				Action a = actionMap.get(selectedAction); // Get the AbstractAction matching the selectedAction
				selectedAction = null; // Reset selectedAction so next time a new one will be choosen.
				return a;
			} else {
				System.out.println("Needed action is unavailable, select from path to be followed");
				Action a = traversePath(state, actions);
				return a;
			}
		}

		if (retAction != null)
			return retAction;
		System.out.println("Return a fallback action");

		retAction = super.selectAction(state, actions);

		return retAction;
	}
}
