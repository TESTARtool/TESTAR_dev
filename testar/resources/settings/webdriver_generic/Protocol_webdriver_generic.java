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

import com.orientechnologies.orient.core.command.OCommandOutputListener;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.tool.ODatabaseExport;
import org.fruit.monkey.btrace.BtraceApiClient;
import org.fruit.monkey.btrace.BtraceFinishRecordingResponse;
import org.fruit.monkey.btrace.MethodInvocation;
import org.fruit.monkey.mysql.DBConnection;
import org.fruit.monkey.mysql.SerializationUtil;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.SutVisualization;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Pair;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.CompoundAction;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.alayer.exceptions.SystemStartException;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.WdElement;
import org.testar.monkey.alayer.webdriver.WdWidget;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.plugin.NativeLinker;
import org.testar.monkey.Settings;
import org.testar.protocols.WebdriverProtocol;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;
import java.util.*;
import static org.testar.monkey.alayer.Tags.Blocked;
import static org.testar.monkey.alayer.Tags.Enabled;
import static org.testar.monkey.alayer.webdriver.Constants.scrollArrowSize;
import static org.testar.monkey.alayer.webdriver.Constants.scrollThick;


public class Protocol_webdriver_generic extends WebdriverProtocol {

	private BtraceApiClient btrace;
	Boolean flag = false;

	String yoho_docker_host =  "";
	String jacococli = "org.jacoco.cli-0.8.6-nodeps.jar";
	String coverage_dir = "yoho-dev";

	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 *
	 * @param settings the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings) {
		super.initialize(settings);
		yoho_docker_host = settings.get(ConfigTags.DockerHost, "");

		/*
		These settings are initialized in WebdriverProtocol:

		// Classes that are deemed clickable by the web framework
		// getting from the settings file:
		clickableClasses = settings.get(ConfigTags.ClickableClasses);

		// Disallow links and pages with these extensions
		// Set to null to ignore this feature
		// getting from the settings file:
		deniedExtensions = settings.get(ConfigTags.DeniedExtensions).contains("null") ? null : settings.get(ConfigTags.DeniedExtensions);

		// Define a whitelist of allowed domains for links and pages
		// An empty list will be filled with the domain from the sut connector
		// Set to null to ignore this feature
		// getting from the settings file:
		domainsAllowed = settings.get(ConfigTags.DomainsAllowed).contains("null") ? null : settings.get(ConfigTags.DomainsAllowed);

		// If true, follow links opened in new tabs
		// If false, stay with the original (ignore links opened in new tabs)
		// getting from the settings file:
		WdDriver.followLinks = settings.get(ConfigTags.FollowLinks);

		//Force the browser to run in full screen mode
		WdDriver.fullScreen = true;

		//Force webdriver to switch to a new tab if opened
		//This feature can block the correct display of select dropdown elements 
		WdDriver.forceActivateTab = true;
		*/

		// List of attributes to identify and close policy popups
		// Set to null to disable this feature
		//TODO put into settings file
		policyAttributes = new HashMap<String, String>() {{
			put("class", "lfr-btn-label");
		}};

		btrace = new BtraceApiClient(settings.get(ConfigTags.BtraceServiceHost));
	}

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

		return super.startSystem();
	}

	@Override
	protected void initTestSession() {
		super.initTestSession();
		System.out.println("PreTestingPreparation \n\n\n");
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://" + yoho_docker_host + ":9000/hooks/reset-db2"))
				.header("Authorization", "Yohohookspassword")  // Set the header
				.POST(HttpRequest.BodyPublishers.noBody())  // POST request with no body
				.build();

		try {
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			System.out.println("Response status code: " + response.statusCode());
			System.out.println("Response headers: " + response.headers());
			System.out.println("Response body: ");
			System.out.println(response.body());
			if(!response.body().contains("DONE")){
				this.mode = Modes.Quit;
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.mode = Modes.Quit;
		}
	}

	@Override
	protected void preSequencePreparations() {
		System.out.println("PreSequencePreparation \n\n\n");
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://" + yoho_docker_host + ":9000/hooks/reset-db"))
				.header("Authorization", "")  // Set the header
				.POST(HttpRequest.BodyPublishers.noBody())  // POST request with no body
				.build();

		try {
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			System.out.println("Response status code: " + response.statusCode());
			System.out.println("Response headers: " + response.headers());
			System.out.println("Response body: ");
			System.out.println(response.body());
			if(!response.body().contains("DONE")){
				this.mode = Modes.Quit;
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.mode = Modes.Quit;
		}
		super.preSequencePreparations();
	}

	protected void generateCoverage(){
		String jarPath = jacococli;
		String command = "java";
		String[] command_and_args = new String[]{"java",
				"-jar", jarPath,
				"dump", "--address", yoho_docker_host,
				"--port", "6300",
				"--destfile", coverage_dir + "\\coverage" + this.sequenceCount + ".exec", "--reset"
		};

		// Use ProcessBuilder to run the command
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command(command_and_args); // Add all the command and arguments
		processBuilder.redirectErrorStream(true); // Redirect error stream to the output stream

		try {
			System.out.println("Extracting coverage exec file");
			Process process = processBuilder.start(); // Start the process

			// Read the output from the command
			java.io.InputStream is = process.getInputStream();
			java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is));

			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}

			// Wait for the process to terminate and check the exit value
			int exitCode = process.waitFor();
			System.out.println("Exited with code " + exitCode);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt(); // Restore interrupted state
			e.printStackTrace();
		}
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

//		RemoteWebDriver driver = WdDriver.getRemoteWebDriver();
//		driver.manage().addCookie(new Cookie("testarSequenceToken", "testsession1"));

		waitLeftClickAndPasteIntoWidgetWithMatchingTag(WdTags.WebGenericTitle, "Phone Number", "+48500000005", state, system, 3, 1);
		waitLeftClickAndPasteIntoWidgetWithMatchingTag(WdTags.WebGenericTitle, "Password", "password", state, system, 3, 1);
		waitAndLeftClickWidgetWithMatchingTag(WdTags.Desc, "Log In", state, system, 3, 1);

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

		return state;
	}

	protected void saveAction(MethodInvocation method, PreparedStatement pstmt) throws SQLException {
		// Assuming sequence_number and action_number are managed by your application logic
		int sequenceNumber = this.sequenceCount(); // Set this based on your application's logic
		int actionNumber = this.actionCount(); // Set this based on your application's logic

		String className = method.className;
		String methodName = method.methodName;
		// Convert parameters to a format suitable for BLOB storage, if necessary
		byte[] params = SerializationUtil.serializeList(method.getParameterTypes());

		pstmt.setInt(1, sequenceNumber);
		pstmt.setInt(2, actionNumber);
		pstmt.setString(3, className);
		pstmt.setString(4, methodName);
		pstmt.setBytes(5, params);
		pstmt.addBatch();
	}

	protected void saveActions(List<MethodInvocation> recordedMethods){
		Connection connection = null;

		try {

			connection = DBConnection.getConnection(yoho_docker_host,"33306", "", "", "");
			connection.setAutoCommit(false); // Disable auto-commit mode
			String insertSQL = "INSERT INTO ActionMethods (sequence_number, action_number, class_name, method_name, parameters) VALUES (?, ?, ?, ?, ?);";

			try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {

				for (MethodInvocation method : recordedMethods) {
					saveAction(method, pstmt);
				}
				pstmt.executeBatch();
			}

			connection.commit();  // Commit transaction once all inserts are done

		} catch (SQLException e) {
			// If there is any error, rollback the transaction
			try {
				if (connection != null) {
					connection.rollback();
				}
			} catch (SQLException excep) {
				// Handle potential rollback error
			}
			// Handle or log the original error
			e.printStackTrace();
		} finally {
			// Remember to set auto-commit back to true
			try {
				if (connection != null) {
					connection.setAutoCommit(true);
				}
			} catch (SQLException excep) {
				// Handle or log error
			}
		}

	}

	/**
	 * This is a helper method used by the default implementation of <code>buildState()</code>
	 * It examines the SUT's current state and returns an oracle verdict.
	 *
	 * @return oracle verdict, which determines whether the state is erroneous and why.
	 */
	@Override
	protected Verdict getVerdict(State state) {
		if(flag) {
			System.out.println("TRUE");
			var recordedMethods = btrace.finishRecordingMethodInvocation();
			System.out.println("RECEIVED RECORDED METHODS: " + recordedMethods);
			flag=false;
			saveActions(recordedMethods);

		}

		Verdict verdict = super.getVerdict(state);
		// system crashes, non-responsiveness and suspicious titles automatically detected!

		//-----------------------------------------------------------------------------
		// MORE SOPHISTICATED ORACLES CAN BE PROGRAMMED HERE (the sky is the limit ;-)
		//-----------------------------------------------------------------------------

		// ... YOU MAY WANT TO CHECK YOUR CUSTOM ORACLES HERE ...

		return verdict;
	}

	@Override
	protected boolean isTypeable(Widget widget) {
		if(widget.get(Tags.Role, Roles.Widget).equals(WdRoles.WdINPUT) && (widget.get(Tags.Title).contains("Add User") || widget.get(WdTags.WebCssClasses,"").contains("mat-input-element") ) ){
			return true;
		}
		return super.isTypeable(widget);
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

			// The blackListed widgets are those that have been filtered during the SPY mode with the
			//CAPS_LOCK + SHIFT + Click clickfilter functionality.
			if(blackListed(widget) || widget.get(WdTags.WebCssClasses, "").contains("mat-chip,")){
				if(isTypeable(widget)){
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
						actions.add(ac.leftClickAt(widget));
					}else{
						// link denied:
						filteredActions.add(ac.leftClickAt(widget));
					}
				}else{
					// filtered and not white listed:
					filteredActions.add(ac.leftClickAt(widget));
				}
			}
		}

		//if(actions.isEmpty()) {
		//	return new HashSet<>(Collections.singletonList(new WdHistoryBackAction()));
		//}
		
		// If we have forced actions, prioritize and filter the other ones
		if (forcedActions != null && forcedActions.size() > 0) {
			filteredActions = actions;
			actions = forcedActions;
		}

		//Showing the grey dots for filtered actions if visualization is on:
		if(visualizationOn || mode() == Modes.Spy) SutVisualization.visualizeFilteredActions(cv, state, filteredActions);

		return actions;
	}

	@Override
	protected boolean isUnfiltered(Widget w){
		if(w.get(WdTags.WebSrc,"").contains("plus-icon.svg") )
		{
			return true;
		}
		return super.isUnfiltered(w);
	}

	@Override
	protected boolean isClickable(Widget widget) {
		if(widget.get(WdTags.WebSrc,"").contains("plus-icon.svg") ||
				widget.get(WdTags.WebSrc,"").contains("new-task-icon.svg") ||
				widget.get(WdTags.WebTextContent, "").contains("To do") ||
				widget.get(WdTags.WebTextContent, "").contains("In progress") ||
				widget.get(WdTags.WebTextContent, "").contains("Completed")||
				widget.get(WdTags.WebTextContent, "").contains("Show results")||
				widget.get(WdTags.WebTextContent, "").contains("keyboard_arrow_down")||
				widget.get(WdTags.WebTextContent, "").equals("notifications") ||
				widget.get(WdTags.WebTextContent, "").equals("more_vert") ||
				widget.get(WdTags.Desc, "").equals("Add users to the team")||
				widget.get(WdTags.WebTextContent, "").equals("person_add")||
				widget.get(WdTags.Desc, "").equals("arrow_drop_down")||
				widget.get(WdTags.WebTextContent, "").equals("send")||
				widget.get(WdTags.WebTextContent, "").equals("New Post")||
				widget.get(WdTags.WebTextContent, "").equals("Add due date")||
				widget.get(WdTags.Desc, "").equals("date_range")||
				widget.get(WdTags.Desc, "").equals("Add new task")||
				widget.get(WdTags.Desc, "").equals("New task")
		){
			return true;
		}

		if(WdDriver.getCurrentUrl().contains("teams-and-users/teams") && widget.get(WdTags.WebCssClasses, "").contains("ng-tns-")){
			return true;
		}

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


	/**
	 * Select one of the possible actions (e.g. at random)
	 *
	 * @param state   the SUT's current state
	 * @param actions the set of available actions as computed by <code>buildActionsSet()</code>
	 * @return the selected action (non-null!)
	 */
	@Override
	public Action selectAction(SUT system, State state, Set<Action> actions) {
		return super.selectAction(system, state, actions);
	}

	/**
	 * Execute the selected action.
	 *
	 * @param system the SUT
	 * @param state  the SUT's current state
	 * @param action the action to execute
	 * @return whether or not the execution succeeded
	 */
	@Override
	protected boolean executeAction(SUT system, State state, Action action) {
		btrace.startRecordingMethodInvocation();
		flag=true;
		boolean actionExecuted = super.executeAction(system, state, action);
		extractStateModelMetrics();
		return actionExecuted;
	}

	/**
	 * TESTAR uses this method to determine when to stop the generation of actions for the
	 * current sequence. You could stop the sequence's generation after a given amount of executed
	 * actions or after a specific time etc.
	 *
	 * @return if <code>true</code> continue generation, else stop
	 */
	@Override
	public boolean moreActions(State state) {
		return super.moreActions(state);
	}

	/**
	 * This method is invoked each time after TESTAR finished the generation of a sequence.
	 */
	@Override
	protected void finishSequence() {
		super.finishSequence();
		generateCoverage();
	}

	/**
	 * TESTAR uses this method to determine when to stop the entire test.
	 * You could stop the test after a given amount of generated sequences or
	 * after a specific time etc.
	 *
	 * @return if <code>true</code> continue test, else stop
	 */
	@Override
	public boolean moreSequences() {
		return super.moreSequences();
	}

	@Override
	protected void closeTestSession() {
		super.closeTestSession();
		exportActions();
		String connectionType = settings.get(ConfigTags.DataStoreType);
		String connectionString = (connectionType.equals("plocal") ? "plocal" : "remote") + ":" + (connectionType.equals("remote") || connectionType.equals("docker") ?
				settings.get(ConfigTags.DataStoreServer) : settings.get(ConfigTags.DataStoreDirectory)) + "/";
		System.out.println("connectionString " + connectionString);
		OrientDB orientDB = new OrientDB(connectionString, OrientDBConfig.defaultConfig());
		ODatabaseDocumentInternal db = (ODatabaseDocumentInternal) orientDB.open(settings.get(ConfigTags.DataStoreDB), settings.get(ConfigTags.DataStoreUser), settings.get(ConfigTags.DataStorePassword));

		try {
			String exportFilePath = "database_export.json";
			// Use ODatabaseExport to export the entire database
			// Create a command output listener
			OCommandOutputListener listener = new OCommandOutputListener() {
				@Override
				public void onMessage(String message) {
					System.out.println(message);
				}
			};

			// Use ODatabaseExport to export the entire database
//			ODatabaseDocumentInternal dbInternal = db.

			ODatabaseExport export = new ODatabaseExport(db, exportFilePath, listener);
			export.exportDatabase();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Close the database session and connection
			db.close();
			orientDB.close();
		}
	}

	protected void exportActions(){
//		connection = DBConnection.getConnection("33306", "testar", "testar", "testar");

		String command = "mysqldump -h " + yoho_docker_host + " -P 33306 -u  -p   ActionMethods > action_methods.sql";

		// Execute the command within a shell
		String[] cmd = { "cmd.exe", "/c", command };

		try {
			Process p = Runtime.getRuntime().exec(cmd);
			// Read any errors from the attempted command
			BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String line;
			while ((line = errorReader.readLine()) != null) {
				System.err.println(line);
			}

			// Wait for the process to complete
			int exitCode = p.waitFor();
			if (exitCode == 0) {
				System.out.println("Database table dumped successfully to action_methods.sql");
			} else {
				System.out.println("Error occurred during database table dump. Exit code: " + exitCode);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
