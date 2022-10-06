/***************************************************************************************************
 *
 * Copyright (c) 2019 - 2021 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2019 - 2021 Open Universiteit - www.ou.nl
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


package org.testar.protocols;

import static org.testar.monkey.alayer.Tags.Blocked;
import static org.testar.monkey.alayer.Tags.Enabled;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.fruit.monkey.TestarServiceException;
import org.fruit.monkey.mysql.MySqlService;
import org.fruit.monkey.mysql.MySqlServiceDelegate;
import org.fruit.monkey.mysql.MySqlServiceImpl;
import org.fruit.monkey.orientdb.OrientDBService;
import org.fruit.monkey.orientdb.OrientDBServiceDelegate;
import org.fruit.monkey.orientdb.OrientDbServiceImpl;
//import org.fruit.monkey.reporting.ReportingWebService;
//import org.fruit.monkey.reporting.ReportingWebServiceImpl;
import org.fruit.monkey.webserver.ReportingBuilder;
import org.fruit.monkey.webserver.ReportingService;
import org.fruit.monkey.webserver.ReportingServiceDelegate;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.OutputStructure;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Environment;
import org.testar.monkey.Main;
import org.testar.monkey.Settings;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.Shape;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.actions.CompoundAction;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.actions.Wait;
import org.testar.monkey.alayer.actions.WdCloseTabAction;
import org.testar.monkey.alayer.actions.WdHistoryBackAction;
import org.testar.monkey.alayer.actions.WdSubmitAction;
import org.testar.monkey.alayer.actions.WidgetActionCompiler;
import org.testar.monkey.alayer.devices.KBKeys;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.alayer.exceptions.SystemStartException;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.WdElement;
import org.testar.monkey.alayer.webdriver.WdWidget;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.monkey.alayer.windows.WinProcess;
import org.testar.monkey.alayer.windows.Windows;
import org.testar.plugin.NativeLinker;
import org.testar.reporting.HtmlSequenceReport;
import org.testar.reporting.HtmlTestReport;
import org.testar.serialisation.LogSerialiser;

import nl.ou.testar.SequenceReport;
import nl.ou.testar.TestReport;
import nl.ou.testar.DatabaseReporting.DatabaseSequenceReport;
import nl.ou.testar.DatabaseReporting.DatabaseTestReport;

public class WebdriverProtocol extends GenericUtilsProtocol {
    //Attributes for adding slide actions
    protected static double SCROLL_ARROW_SIZE = 36; // sliding arrows
    protected static double SCROLL_THICK = 16; //scroll thickness
    protected SequenceReport sequenceReport;
	protected TestReport testReport;
    protected State latestState;

    protected String firstNonNullUrl;

    protected static Set<String> existingCssClasses = new HashSet<>();

	// WedDriver settings from file:
	protected List<String> clickableClasses, deniedExtensions, domainsAllowed;

	protected String loginURL;
	protected String loginButtonName;
	protected String loginFormID;
	protected String loginInputKeyAttribute;
	protected String loginUsernameInputName;
	protected String loginUsername;
	protected String loginPasswordInputName;
	protected String loginPassword;

	public MySqlService sqlService;

	protected OrientDBService orientService;

	private boolean isLocalDatabaseActive = false;
	private boolean isForcedLoginInProgress = false;

	// List of atributes to identify and close policy popups
	// Set to null to disable this feature
	@SuppressWarnings("serial")
	protected Map<String, String> policyAttributes = new HashMap<String, String>()
	{
		{
			put("id", "_cookieDisplay_WAR_corpcookieportlet_okButton");
		}
	};

	// Verdict obtained from messages coming from the web browser console
	protected Verdict webConsoleVerdict = Verdict.OK;

	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 * @param   settings  the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings){

		loginURL = settings.get(ConfigTags.ForcedLoginUrl, null);
		loginButtonName = settings.get(ConfigTags.ForcedLoginButtonName, null);
		loginFormID = settings.get(ConfigTags.ForcedLoginFormId, null);
		loginInputKeyAttribute = settings.get(ConfigTags.ForcedLoginInputKeyAttribute, "id");
		loginUsernameInputName = settings.get(ConfigTags.ForcedLoginUsernameInputName, null);
		loginUsername = settings.get(ConfigTags.ForcedLoginUsername, null);
		loginPasswordInputName = settings.get(ConfigTags.ForcedLoginPasswordInputName, null);
		loginPassword = settings.get(ConfigTags.ForcedLoginPassword, null);

		// Indicate to TESTAR we want to use webdriver package implementation
		NativeLinker.addWdDriverOS();

		Thread mysqlThread = null;
		Thread orientdbThread = null;

		delegate.startProgress(settings, "Initialising database(s)");

		if(settings.get(ConfigTags.StateModelEnabled) && settings.get(ConfigTags.DataStoreType).equals("docker")) {
			if (!Main.getReportingService().isDockerAvailable()) {
				delegate.endProgress();
				delegate.popupMessage(TestarServiceException.DOCKER_UNAVAILABLE);
				return;
			}

//			if (progressMonitor != null) {
//				progressMonitor.beginTask("Prepare database instance(s)", 0);
//			}
			OrientDbServiceImpl			orientService = new OrientDbServiceImpl(Main.getReportingService(), settings);
			// TODO: Re-enable progress dialog
//			ProgressDialog progressDialog = new ProgressDialog();
//			progressDialog.setStatusString("Preparing OrientDB");

			orientService.setDelegate(new OrientDBServiceDelegate() {
				@Override
				public void onStateChanged(State state, String description) {
					// TODO: Re-enable progress dialog
//					progressDialog.setStatusString(description);
				}

				@Override
				public void onServiceReady() {
					// TODO: Re-enable progress dialog
//					progressDialog.endProgress(null, true);
				}
			});

			orientdbThread = new Thread() {
				@Override
				public void run() {
					try {
						// There is no other user which can be used
						// There should be a better way to implement this.
						settings.set(ConfigTags.DataStoreUser, "root");
						settings.set(ConfigTags.DataStoreServer, "orientdb");
						orientService.startLocalDatabase(settings.get(ConfigTags.DataStoreDB), settings.get(ConfigTags.DataStoreUser), settings.get(ConfigTags.DataStorePassword));
					} catch (Exception e) {
						final String errorMessage = "Cannot initialize OrientDB";
						delegate.endProgress();
						delegate.popupMessage(errorMessage);
						System.err.println(errorMessage);
						e.printStackTrace();
					}
					System.out.println("OrientDB docker image finished.");
				}
			};
			orientdbThread.start();
//			progressDialog.pack();
//			progressDialog.setLocationRelativeTo(null);
//			progressDialog.setVisible(true);


		}

		 if (settings.get(ConfigTags.ReportType).equals(Settings.SUT_REPORT_DATABASE)) {
		 	System.out.println("*** Create a new SQL service ***");
			//TODO: warn and fallback to static HTML reporting if state model disabled or Docker isn't available
				sqlService = new MySqlServiceImpl(Main.getReportingService(), settings);
			final String databaseName = settings.get(ConfigTags.SQLReporting);
			final String userName = settings.get(ConfigTags.SQLReportingUser);
			final String userPassword = settings.get(ConfigTags.SQLReportingPassword);

			// TODO: Re-enable progress dialog
//			ProgressDialog progressDialog = new ProgressDialog();
//			progressDialog.setStatusString("Starting database connection");

			sqlService.setDelegate(new MySqlServiceDelegate() {
				@Override
				public void onStateChanged(State state, String description) {

					// TODO: Re-enable progress dialog
//					progressDialog.setStatusString(description);
				}

				@Override
				public void onServiceReady(String str) {
					// TODO: Re-enable progress dialog
//					progressDialog.endProgress(null, true);
				}
			});

			 mysqlThread = new Thread() {
				@Override
				public void run() {
					try {
						if (settings.get(ConfigTags.SQLReportingType).equals("local")) {
							sqlService.startLocalDatabase(databaseName, userName, userPassword);
							isLocalDatabaseActive = true;
						}
						else {
							sqlService.connectExternalDatabase(settings.get(ConfigTags.SQLReportingServer),
									databaseName, userName, userPassword);
						}
//						ReportingWebService reportingService = new ReportingWebServiceImpl(sqlService.getDockerPoolService());
//						reportingService.start(8888, 1080, "mysql", 3306, "testar", "testar", "testar");
					} catch (ClassNotFoundException | IOException | SQLException | TestarServiceException e) {
						final String errorMessage = "Cannot initialize a database";
						delegate.endProgress();
						delegate.popupMessage(errorMessage);
						System.err.println(errorMessage);
						e.printStackTrace();
					}
					System.out.println("OrientDB docker image finished.");
				}
			};
			mysqlThread.start();


			// TODO: Re-enable progress dialog
//			progressDialog.pack();
//			progressDialog.setLocationRelativeTo(null);
//			progressDialog.setVisible(true);
		}

		try {
			if (mysqlThread != null) {
				mysqlThread.join();
			}
			if (orientdbThread != null) {
				orientdbThread.join();
			}
		} catch (InterruptedException e) {
			System.out.println("Database thread interrupted: " + e.getMessage());
			e.printStackTrace();
		}

		delegate.updateStatus("Building a model", 0);

		super.initialize(settings);

		delegate.updateStatus("Starting a web driver", 0);

		// Initialize HTML Report (Dashboard)
		if (sqlService != null) {
			this.testReport = new DatabaseTestReport(sqlService, settings.get(ConfigTags.SQLReporting));
		}
		else {
			this.testReport = new HtmlTestReport();
		}
		this.firstNonNullUrl = null; // FIXME: There should be a better way to find the URL right?

		// reads the settings from file:
//		super.initialize(settings);

		// Classes that are deemed clickable by the web framework
		clickableClasses = settings.get(ConfigTags.ClickableClasses);

		// Disallow links and pages with these extensions
		// Set to null to ignore this feature
		deniedExtensions = settings.get(ConfigTags.DeniedExtensions).contains("null") ? null : settings.get(ConfigTags.DeniedExtensions);

		// Define a whitelist of allowed domains for links and pages
		// An empty list will be filled with the domain from the sut connector
		// Set to null to ignore this feature
		domainsAllowed = settings.get(ConfigTags.DomainsAllowed).contains("null") ? null : settings.get(ConfigTags.DomainsAllowed);

		// If true, follow links opened in new tabs
		// If false, stay with the original (ignore links opened in new tabs)
		WdDriver.followLinks = settings.get(ConfigTags.FollowLinks);

		//Force the browser to run in full screen mode
		WdDriver.fullScreen = settings.get(ConfigTags.BrowserFullScreen);

		//Force webdriver to switch to a new tab if opened
		//This feature can block the correct display of select dropdown elements
		WdDriver.forceActivateTab = settings.get(ConfigTags.SwitchNewTabs);

		delegate.updateStage("Testing");
		delegate.updateStatus("", 0);
	}

	@Override
	protected void onTestEndEvent() {
		delegate.updateStatus("Preparing a report", 0);
		this.testReport.saveReport(
				this.settings().get(ConfigTags.SequenceLength),
				this.settings().get(ConfigTags.Sequences),
				this.firstNonNullUrl // FIXME: Use less if statements to find the first URL
		);

		if (sqlService != null) {
			final boolean dbEnabled = settings.get(ConfigTags.ReportType).equals(Settings.SUT_REPORT_DATABASE);
			final int port = settings.get(ConfigTags.ReportServicePort);
			final String dbHostname = (isLocalDatabaseActive ? "mysql" : settings.get(ConfigTags.SQLReportingServer));
			final String dbName = settings.get(ConfigTags.SQLReportingDB);
			final String dbUsername = settings.get(ConfigTags.SQLReportingUser);
			final String dbPassword = settings.get(ConfigTags.SQLReportingPassword);

			final String oHostname = settings.get(ConfigTags.DataStoreServer);
			final String oDatabase = settings.get(ConfigTags.DataStoreDB);
			final String oUsername = settings.get(ConfigTags.DataStoreUser);
			final String oPassword = settings.get(ConfigTags.DataStorePassword);

			// TODO: Re-enable progress dialog
//			ProgressDialog progressDialog = new ProgressDialog();
//			progressDialog.setStatusString("Preparing report");

			try {
				ReportingBuilder reportingBuilder = new ReportingBuilder(port, Main.getReportingService(), dbEnabled, settings.get(ConfigTags.StateModelEnabled));

				reportingBuilder.setMysqlDBConfiguraton(dbHostname, dbUsername, dbPassword, dbName, 3306);
				reportingBuilder.setOrientDBConfiguraton(oHostname, oUsername, oPassword, oDatabase, 2424);

				final ReportingService reportingService = reportingBuilder.build();

				reportingService.setDelegate(new ReportingServiceDelegate() {
					@Override
					public void onStateChanged(State state, String description) {
						// TODO: Re-enable progress dialog
//						progressDialog.setStatusString(description);
					}

					@Override
					public void onServiceReady(String url) {
						// TODO: Re-enable progress dialog
//						progressDialog.endProgress(null, true);

						// TODO: open web link
						try {
						  delegate.openURI(new URI(url));
//							Desktop.getDesktop().browse(new URI("http://localhost:" + port));
						}
						catch (Exception e) {
							System.err.println("Cannot browse report: " + e.getMessage());
							e.printStackTrace();
						}
					}
				});
				new Thread() {
					@Override
					public void run() {
						try {
							reportingService.start();
						} catch (IOException | TestarServiceException e) {
							delegate.popupMessage(e.getMessage());
							e.printStackTrace();
						}
					}
				}.start();

				// TODO: Re-enable progress dialog
//				progressDialog.pack();
//				progressDialog.setLocationRelativeTo(null);
//				progressDialog.setVisible(true);
			}
			catch (IOException e) {
				System.err.println("Cannot init web service: " + e.getMessage());
				e.printStackTrace();
			}
		}
		delegate.endProgress();
	}
    /**
     * This methods is called before each test sequence, allowing for example using external profiling software on the SUT
     */
    @Override
    protected void preSequencePreparations() {

        //initializing the HTML sequence report:
		if (sqlService != null) {
			sequenceReport = new DatabaseSequenceReport(sqlService);
		}
		else {
			sequenceReport = new HtmlSequenceReport();
		}
		// reset web browser console verdict
		webConsoleVerdict = Verdict.OK;
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
    	SUT sut = super.startSystem();

    	// Add the domain from the SUTConnectorValue to domainsAllowed List
    	ensureDomainsAllowed();

    	// Check if TESTAR runs in Windows 10 to set webdriver browser handle identifier
    	setWindowHandleForWebdriverBrowser(sut);

    	double displayScale = getDisplayScale(sut);

    	// See remarks in WdMouse
    	mouse = sut.get(Tags.StandardMouse);
    	mouse.setCursorDisplayScale(displayScale);

        // Start database service
		// TODO: take from settings

    	return sut;
    }

    /**
     * A workaround to obtain the browsers window handle, ideally this information is acquired when starting the
     * webdriver in the constructor of WdDriver.
     * A possible solution could be creating a snapshot of the running browser processes before and after.
     */
    private void setWindowHandleForWebdriverBrowser(SUT sut) {
    	try {
    		if(System.getProperty("os.name").contains("Windows 10") && sut.get(Tags.HWND, null) == null) {
    			// Note don't place a breakpoint here since the outcome of the function call will result in the IDE pid and
    			// window handle. The running browser needs to be in the foreground when we reach this part.
    			long hwnd = Windows.GetForegroundWindow();
    			long pid = Windows.GetWindowProcessId(Windows.GetForegroundWindow());
    			// Safe to set breakpoints again.
    			if (WinProcess.procName(pid).contains("chrome")) {
    				sut.set(Tags.HWND, hwnd);
    				sut.set(Tags.PID, pid);
    				System.out.printf("INFO System PID %d and window handle %d have been set\n", pid, hwnd);
    			}
    		}
    	} catch (ExceptionInInitializerError | NoClassDefFoundError e) {
    		System.out.println("INFO: We can not obtain the Windows 10 windows handle of WebDriver browser instance");
    	}
    }

	/**
	 * Returns the display scale based on the settings, if the user has set the override webdriver display scale
	 * we return the override value otherwise the display scale obtained from the system.
	 * @param sut The system under test
	 * @return The display scale.
	 */
	private double getDisplayScale(SUT sut) {
		// Call specific OS API to obtain the display scale value of the system
		// Ex: windows - org.testar.monkey.alayer.windows.Windows10 calls MonitorFromWindow native function
		// If something fails these specific getDisplayScale OS implementations must return a default value
		double displayScale = Environment.getInstance().getDisplayScale(sut.get(Tags.HWND, (long)0));

		// If the user has specified a scale override the display scale obtained from the system.
		String overrideDisplayScaleAsString = settings().get(ConfigTags.OverrideWebDriverDisplayScale, "");
		if (!overrideDisplayScaleAsString.isEmpty()) {
			try {
				double webDriverDisplayScaleOverride = Double.parseDouble(overrideDisplayScaleAsString);
				if (webDriverDisplayScaleOverride != 0) {
					displayScale = webDriverDisplayScaleOverride;
				}
			} catch (NumberFormatException nfe) {
				System.out.printf("WARNING Unable to convert display scale override to double: %s, will use %f\n", overrideDisplayScaleAsString, displayScale);
			}
		}
		return displayScale;
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

    /**
     * This method is called when the TESTAR requests the state of the SUT.
     * Here you can add additional information to the SUT's state or write your
     * own state fetching routine. The state should have attached an oracle
     * (TagName: <code>Tags.OracleVerdict</code>) which describes whether the
     * state is erroneous and if so why.
     * @return  the current state of the SUT with attached oracle.
     */
    @Override
    protected State getState(SUT system) throws StateBuildException {

    	try {
    		WdDriver.waitDocumentReady();
    	} catch(org.openqa.selenium.WebDriverException wde) {
    		delegate.popupMessage("WEBDRIVER ERROR: Selenium Chromedriver seems not to respond!");
    		LogSerialiser.log("WEBDRIVER ERROR: Selenium Chromedriver seems not to respond!\n", LogSerialiser.LogLevel.Critical);
    		System.out.println("******************************************************************");
    		System.out.println("** WEBDRIVER ERROR: Selenium Chromedriver seems not to respond! **");
    		System.out.println("******************************************************************");
    		System.out.println(wde.getMessage());
    		system.set(Tags.IsRunning, false);
    	}

    	updateCssClassesFromTestSettingsFile();

    	State state = super.getState(system);

    	if(settings.get(ConfigTags.ForceForeground)
    			&& System.getProperty("os.name").contains("Windows 10")
    			&& system.get(Tags.IsRunning, false) && !system.get(Tags.NotResponding, false)
    			&& system.get(Tags.PID, (long)-1) != (long)-1
    			&& WinProcess.procName(system.get(Tags.PID)).contains("chrome")
    			&& !WinProcess.isForeground(system.get(Tags.PID))){

    		WinProcess.politelyToForeground(system.get(Tags.HWND));
    		LogSerialiser.log("Trying to set Chrome Browser to Foreground... "
    		+ WinProcess.procName(system.get(Tags.PID)) + "\n");

    	}

    	latestState = state;

    	//Spy mode didn't use the html report
    	if(settings.get(ConfigTags.Mode) == Modes.Spy) {

    		for(Widget w : state) {
    			WdElement element = ((WdWidget) w).element;
    			for(String s : element.cssClasses) {
    				existingCssClasses.add(s);
    			}
    		}

        	return state;
    	}

        //adding state to the HTML sequence report:
        sequenceReport.addState(latestState);
        testReport.addState(latestState);

        if (lastExecutedAction != null && latestState != null) {
			testReport.setTargetState(lastExecutedAction, latestState);
		}

		return latestState;
    }

    @Override
	protected void runGenerateOuterLoop(SUT system) {
    	isForcedLoginInProgress = false;
    	super.runGenerateOuterLoop(system);
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
		// Derive actions didn't find any action, inform the user and force WdHistoryBackAction
		if(actions == null || actions.isEmpty()) {
			System.out.println(String.format("** WEBDRIVER WARNING: In Action number %s the State seems to have no interactive widgets", actionCount()));
			System.out.println(String.format("** URL: %s", WdDriver.getCurrentUrl()));
			System.out.println("** Please try to navigate with SPY mode and configure clickableClasses inside Java protocol");
			actions = new HashSet<>(Collections.singletonList(new WdHistoryBackAction()));
		}

		return super.selectAction(system, state, actions);
	}

	protected void saveDerivedActions(State state, Set<Action> actions) {
		int stateId = -1;
		try {
			stateId = sqlService.findState(state.get(Tags.ConcreteIDCustom), state.get(Tags.AbstractID));
		} catch (SQLException e) {
			System.err.println("Invalid state when saving actions: " + e.getMessage());
		}

		for (Action action: actions) {
//			sqlService.registerAction(action.)
		}
	}

    /**
     * The getVerdict methods implements the online state oracles that
     * examine the SUT's current state and returns an oracle verdict.
     *
     * @return oracle verdict, which determines whether the state is erroneous and why.
     */
    @Override
    protected Verdict getVerdict(State state) {
    	Verdict stateVerdict = super.getVerdict(state);

    	// If Web Console Error Oracle is enabled and we have some pattern to match
    	if(settings.get(ConfigTags.WebConsoleErrorOracle, false) && !settings.get(ConfigTags.WebConsoleErrorPattern, "").isEmpty()) {
    		// Load the web console error pattern
    		Pattern errorPattern = Pattern.compile(settings.get(ConfigTags.WebConsoleErrorPattern), Pattern.UNICODE_CHARACTER_CLASS);
    		// Check Severe messages in the WebDriver logs
    		RemoteWebDriver driver = WdDriver.getRemoteWebDriver();
    		LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
    		for(LogEntry logEntry : logEntries) {
    			if(logEntry.getLevel().equals(Level.SEVERE)) {
    				// Check if the severe error message matches with the web console error pattern
    				String consoleErrorMsg = logEntry.getMessage();
    				Matcher matcherError = errorPattern.matcher(consoleErrorMsg);
    				if(matcherError.matches()) {
    					webConsoleVerdict = new Verdict(Verdict.SEVERITY_SUSPICIOUS_TITLE, "Web Browser Console Error: " + consoleErrorMsg);
    				}
    			}
    		}
    		// Join GUI verdict with WebDriver console verdict
    		stateVerdict = stateVerdict.join(webConsoleVerdict);
    	}

    	// If Web Console Warning Oracle is enabled and we have some pattern to match
    	if(settings.get(ConfigTags.WebConsoleWarningOracle, false) && !settings.get(ConfigTags.WebConsoleWarningPattern, "").isEmpty()) {
    		// Load the web console warning pattern
    		Pattern warningPattern = Pattern.compile(settings.get(ConfigTags.WebConsoleWarningPattern), Pattern.UNICODE_CHARACTER_CLASS);
    		// Check Warning messages in the WebDriver logs
    		RemoteWebDriver driver = WdDriver.getRemoteWebDriver();
    		LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
    		for(LogEntry logEntry : logEntries) {
    			if(logEntry.getLevel().equals(Level.WARNING)) {
    				// Check if the warning message matches with the web console error pattern
    				String consoleWarningMsg = logEntry.getMessage();
    				Matcher matcherWarning = warningPattern.matcher(consoleWarningMsg);
    				if(matcherWarning.matches()) {
    					webConsoleVerdict = new Verdict(Verdict.SEVERITY_SUSPICIOUS_TITLE, "Web Browser Console Warning: " + consoleWarningMsg);
    				}
    			}
    		}
    		// Join GUI verdict with WebDriver console verdict
    		stateVerdict = stateVerdict.join(webConsoleVerdict);
    	}

    	return stateVerdict;
    }

    /**
     * Overwriting to add HTML report writing into it
     *
     * @param state
     * @param actions
     * @return
     */
    @Override
    protected Set<Action> preSelectAction(SUT system, State state, Set<Action> actions){
    	// Derive actions didn't find any action, inform the user and force WdHistoryBackAction
    	if(actions == null || actions.isEmpty()) {
    		System.out.println(String.format("** WEBDRIVER WARNING: In Action number %s the State seems to have no interactive widgets", actionCount()));
    		System.out.println(String.format("** URL: %s", WdDriver.getCurrentUrl()));
    		System.out.println("** Please try to navigate with SPY mode and configure clickableClasses inside Java protocol");
    		// Create and build the id of the HistoryBackAction
    		Action histBackAction = new WdHistoryBackAction();
    		buildEnvironmentActionIdentifiers(state, histBackAction);
    		actions = new HashSet<>(Collections.singletonList(histBackAction));
    	}
    	// super preSelectAction will not derive ESC action
    	actions = super.preSelectAction(system, state, actions);
    	// adding available actions into the HTML report:
		sequenceReport.addActions(actions);
		testReport.addActions(actions);
    	return actions;
    }

    /**
     * Execute the selected action.
     * @param system the SUT
     * @param state the SUT's current state
     * @param action the action to execute
     * @return whether or not the execution succeeded
     */
    @Override
    protected boolean executeAction(SUT system, State state, Action action){
        // adding the action that is going to be executed into HTML report:
        sequenceReport.addSelectedAction(state, action);
        testReport.addSelectedAction(state, action);
        boolean result = super.executeAction(system, state, action);
        System.out.println("Executed action: " + action.toShortString());
        return result;
    }

    /**
     * Replay the saved action
     */
    @Override
    protected boolean replayAction(SUT system, State state, Action action, double actionWaitTime, double actionDuration){
        // adding the action that is going to be executed into HTML report:
		sequenceReport.addSelectedAction(state, action);
        boolean result = super.replayAction(system, state, action, actionWaitTime, actionDuration);
        System.out.println("Replayed action: " + action.toShortString());
        return result;
    }

    /**
     * This methods is called after each test sequence, allowing for example using external profiling software on the SUT
     */
    @Override
    protected void postSequenceProcessing() {
        String status = "";
        String statusInfo = "";

		System.out.println("*** Postprocessing a sequence ***");

    	String sequencesPath = getGeneratedSequenceName();
    	try {
    		sequencesPath = new File(getGeneratedSequenceName()).getCanonicalPath();
    	}catch (Exception e) {}

        try {
            sequencesPath = new File(getGeneratedSequenceName()).getCanonicalPath();
        } catch (IOException e) {
			e.printStackTrace();
		}

		status = (getVerdict(latestState).join(processVerdict)).verdictSeverityTitle();
		statusInfo = (getVerdict(latestState).join(processVerdict)).info();

        if(mode() == Modes.Replay || mode() == Modes.ReplayModel) {
			System.out.println("*** Adding a test verdict from replay ***");
            sequenceReport.addTestVerdict(getReplayVerdict().join(processVerdict));
            testReport.addTestVerdict(getReplayVerdict().join(processVerdict), lastExecutedAction, latestState);
            status = (getReplayVerdict().join(processVerdict)).verdictSeverityTitle();
            statusInfo = (getReplayVerdict().join(processVerdict)).info();
        }
        else {
        	System.out.println("*** Adding a test verdict ***");
			sequenceReport.addTestVerdict(getVerdict(latestState).join(processVerdict));
			testReport.addTestVerdict(getVerdict(latestState), lastExecutedAction, latestState);
            status = (getVerdict(latestState).join(processVerdict)).verdictSeverityTitle();
            statusInfo = (getVerdict(latestState).join(processVerdict)).info();
        }

		statusInfo = statusInfo.replace("\n"+Verdict.OK.info(), "");

        //Timestamp(generated by logger) SUTname Mode SequenceFileObject Status "StatusInfo"
        INDEXLOG.info(OutputStructure.executedSUTname
                + " " + settings.get(ConfigTags.Mode, mode())
                + " " + sequencesPath
                + " " + status + " \"" + statusInfo + "\"" );

    	sequenceReport.close();
    }

    @Override
	protected void finishSequence(){
		//With webdriver version we don't use the call SystemProcessHandling.killTestLaunchedProcesses
	}

    @Override
    protected void stopSystem(SUT system) {
        if(settings.get(ConfigTags.Mode) == Modes.Spy) {

            try {
                if(Settings.getSettingsPath() != null) {
                    File folder = new File(Settings.getSettingsPath());
                    File file = new File(folder, "existingCssClasses.txt");
                    if(!file.exists())
                        file.createNewFile();

                    Stream<String> stream = Files.lines(Paths.get(file.getCanonicalPath()));
                    stream.forEach(line -> existingCssClasses.add(line));
                    stream.close();

                    PrintWriter write = new PrintWriter(new FileWriter(file.getCanonicalPath()));
                    for(String s : existingCssClasses)
                        write.println(s);
                    write.close();
                }

            } catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Done exporting");

		super.stopSystem(system);
    }

    @Override
	protected void closeTestSession() {
		super.closeTestSession();
		NativeLinker.cleanWdDriverOS();
	}

	/*
	 * Check the state if we need to force an action
	 */
	protected Set<Action> detectForcedActions(State state, WidgetActionCompiler ac) {
		Set<Action> forcedActions = null;
		Set<Action> actions = detectForcedDeniedUrl();
		if (actions != null && actions.size() > 0) {
			forcedActions = actions;
		}
		else {
			actions = detectForcedLogin(state);
			if (actions != null && actions.size() > 0) {
				System.out.println("--- Forced login prepared ---");
				forcedActions = actions;
			}
			else {
				actions = detectForcedPopupClick(state, ac);
				if (actions != null && actions.size() > 0) {
					forcedActions = actions;
				}
			}
		}

		if (forcedActions != null) {
			for (Action action: forcedActions) {
				System.out.println(String.format("*** Action %s is forced ***", action.toShortString()));
				action.set(WdTags.WebIsForced, true);
			}
		}

		return forcedActions;
	}

	/*
	 * Detect and perform login if defined
	 */
	protected Set<Action> detectForcedLogin(State state) {
		if (loginURL == null ||
			loginUsernameInputName == null ||
			loginUsername == null ||
			loginPasswordInputName == null ||
			loginPassword == null) {
			return null;
		}

		// Check if the current page is a login page
		String currentUrl = WdDriver.getCurrentUrl();
		if (this.firstNonNullUrl == null) this.firstNonNullUrl = currentUrl;
		StdActionCompiler actionCompiler = new StdActionCompiler();
		if (currentUrl.startsWith(loginURL)) {
			if (isForcedLoginInProgress) {
				return null;
			}

			CompoundAction.Builder builder = new CompoundAction.Builder();
			WdWidget usernameWidget = null;
			WdWidget passwordWidget = null;
			WdWidget buttonWidget = null;
			// Set username and password
//			System.out.println(String.format("--- %s ---", loginInputKeyAttribute));
			for (Widget widget : state) {
				WdWidget wdWidget = (WdWidget) widget;
				// Only enabled, visible widgets
//				if (!widget.get(Enabled, true) || widget.get(Blocked, false)) {
//					continue;
//				}

				String widgetName = wdWidget.getAttribute(loginInputKeyAttribute);
				System.out.println(String.format("+++ %s, %s, %s +++", wdWidget.element.tagName, widgetName, wdWidget.element.textContent));
//				System.out.println(String.format("+++ Attributes available: %s +++", String.join(", ", wdWidget.getAllAttributes().keySet())));
				if (loginUsernameInputName.equals(widgetName)/*wdWidget.element.getElementDescription().equals(loginUsernameInputName)*/) {
					usernameWidget = wdWidget;
				}
				else if (loginPasswordInputName.equals(widgetName)/*wdWidget.element.getElementDescription().equals(loginPasswordInputName)*/) {
					passwordWidget = wdWidget;
				}
				else if (loginButtonName != null && wdWidget.element.getElementDescription().equals(loginButtonName)) {
					buttonWidget = wdWidget;
				}
			}
			// Submit form, but only if user and pass are filled
			if (usernameWidget != null && passwordWidget != null) {
				System.out.println("--- Widgets found ---");
				final Action usernameTypeAction = actionCompiler.clickTypeInto(usernameWidget, loginUsername, true);
				usernameTypeAction.set(Tags.OriginWidget, usernameWidget);
				final Action passwordTypeAction = actionCompiler.clickTypeInto(passwordWidget, loginPassword, true);
				passwordTypeAction.set(Tags.OriginWidget, passwordWidget);
				builder.add(usernameTypeAction, 1);
				builder.add(new Wait(0.2), 1);
				builder.add(passwordTypeAction, 1);
				builder.add(new Wait(0.2), 1);
//				Action customLoginAction = generateCustomLoginAction(state, actionCompiler);
				Action submitAction = generateCustomLoginAction(state, actionCompiler);
				if (submitAction == null) {
					if (buttonWidget != null) {
						System.out.println("--- Button found ---");
						submitAction = actionCompiler.leftClickAt(buttonWidget);
						submitAction.set(Tags.OriginWidget, buttonWidget);
					}
//				else if (customLoginAction != null) {
//					builder.add(customLoginAction, 1);
//				}
					if (loginFormID != null) {
						submitAction = new WdSubmitAction(loginFormID);
					}
					else/* if (loginFormID == null)*/ {
						submitAction = actionCompiler.hitKey(KBKeys.VK_ENTER);
						submitAction.set(Tags.OriginWidget, passwordWidget);
					}
				}
//				else {
//					builder.add(new WdSubmitAction(loginFormID), 2);
//				}
				builder.add(submitAction, 1);

				isForcedLoginInProgress = true;
				CompoundAction actions = builder.build();
				return new HashSet<>(Collections.singletonList(actions));
			}
		}
		else {
			isForcedLoginInProgress = false;
		}

		return null;
	}

	@Override
	public boolean moreActions(State state) {
		if (!isForcedLoginInProgress) {
		  String currentURL = WdDriver.getCurrentUrl();
		  if (currentURL != null && loginURL != null && currentURL.startsWith(loginURL)) {
        // Forced login expected
        return true;
      }
		}
		return super.moreActions(state);
	}

	/*
	 * Locate and press login button, etc.
	 */
	protected Action generateCustomLoginAction(State state, WidgetActionCompiler ac) {
		// Override if needed
		return null;
	}

	/*
	 * Force closing of Policies Popup
	 */
	protected Set<Action> detectForcedPopupClick(State state,
			WidgetActionCompiler ac) {
		if (policyAttributes == null || policyAttributes.size() == 0) {
			return null;
		}

		for (Widget widget : state) {
			// Only enabled, visible widgets
			if (!widget.get(Enabled, true) || widget.get(Blocked, false)) {
				continue;
			}

			WdElement element = ((WdWidget) widget).element;
			boolean isPopup = true;
			for (Map.Entry<String, String> entry : policyAttributes.entrySet()) {
				String attribute = element.attributeMap.get(entry.getKey());
				isPopup &= entry.getValue().equals(attribute);
			}
			if (isPopup) {
				return new HashSet<>(Collections.singletonList(ac.leftClickAt(widget)));
			}
		}

		return null;
	}

	/*
	 * Force back action due to disallowed domain or extension
	 */
	protected Set<Action> detectForcedDeniedUrl() {
		String currentUrl = WdDriver.getCurrentUrl();
		if (this.firstNonNullUrl == null) this.firstNonNullUrl = currentUrl;

		// Don't get caught in PDFs etc. and non-whitelisted domains
		if (isUrlDenied(currentUrl) || isExtensionDenied(currentUrl)) {
			// If opened in new tab, close it
			if (WdDriver.getWindowHandles().size() > 1) {
				return new HashSet<>(Collections.singletonList(new WdCloseTabAction()));
			}
			// Single tab, go back to previous page
			else {
				return new HashSet<>(Collections.singletonList(new WdHistoryBackAction()));
			}
		}

		return null;
	}

	/*
	 * Check if the current address has a denied extension (PDF etc.)
	 */
	protected boolean isExtensionDenied(String currentUrl) {
		if (this.firstNonNullUrl == null) this.firstNonNullUrl = currentUrl;
		// If the current page doesn't have an extension, always allow
		if (!currentUrl.contains(".")) {
			return false;
		}

		if (deniedExtensions == null || deniedExtensions.size() == 0) {
			return false;
		}

		// Deny if the extension is in the list
		String ext = currentUrl.substring(currentUrl.lastIndexOf(".") + 1);
		ext = ext.replace("/", "").toLowerCase();
		return deniedExtensions.contains(ext);
	}

	/*
	 * Check if the URL is denied
	 */
	protected boolean isUrlDenied(String currentUrl) {
		if (this.firstNonNullUrl == null) this.firstNonNullUrl = currentUrl;
		if (currentUrl.startsWith("mailto:")) {
			return true;
		}

		// Always allow local file
		if (currentUrl.startsWith("file:///")) {
			return false;
		}

		// User wants to allow all
		if (domainsAllowed == null) {
			return false;
		}

		// Only allow pre-approved domains
		String domain = getDomain(currentUrl);
		return !domainsAllowed.contains(domain);
	}

	/*
	 * Check if the widget has a denied URL as hyperlink
	 */
	protected boolean isLinkDenied(Widget widget) {
		String linkUrl = widget.get(Tags.ValuePattern, "");

		// Not a link or local file, allow
		if (linkUrl == null || linkUrl.startsWith("file:///")) {
			return false;
		}

		// Deny the link based on extension
		if (isExtensionDenied(linkUrl)) {
			return true;
		}

		// Mail link, deny
		if (linkUrl.startsWith("mailto:")) {
			return true;
		}

		// Not a web link (or link to the same domain), allow
		if (!(linkUrl.startsWith("https://") || linkUrl.startsWith("http://"))) {
			return false;
		}

		// User wants to allow all
		if (domainsAllowed == null) {
			return false;
		}

		// Only allow pre-approved domains if
		String domain = getDomain(linkUrl);
		return !domainsAllowed.contains(domain);
	}

	/*
	 * Get the domain from a full URL
	 */
	protected String getDomain(String url) {
		if (url == null) {
			return null;
		}

		// When serving from file, 'domain' is filesystem
		if (url.startsWith("file://")) {
			return "file://";
		}

		url = url.replace("https://", "").replace("http://", "").replace("file://", "");
		return (url.split("/")[0]).split("\\?")[0];
	}

	/*
	 * If domainsAllowed from SUTConnectorValue is not set, include it in the domainsAllowed
	 */
	protected void ensureDomainsAllowed() {
		try{
			// Adding default domain from SUTConnectorValue if is not included in the domainsAllowed list
			//TODO try-catch for nullpointer if sut connector missing
			String[] parts = settings().get(ConfigTags.SUTConnectorValue).split(" ");
			String sutConnectorUrl = parts[parts.length - 1].replace("\"", "");

			if(domainsAllowed != null && !domainsAllowed.contains(getDomain(sutConnectorUrl))) {
				System.out.println(String.format("WEBDRIVER INFO: Automatically adding %s SUT Connector domain to domainsAllowed List", getDomain(sutConnectorUrl)));
				String[] newDomainsAllowed = domainsAllowed.stream().toArray(String[]::new);
				domainsAllowed = Arrays.asList(ArrayUtils.insert(newDomainsAllowed.length, newDomainsAllowed, getDomain(sutConnectorUrl)));
				System.out.println(String.format("domainsAllowed: %s", String.join(",", domainsAllowed)));
			}

			// Also add the default starting domain of the SUT if is not included in the domainsAllowed list
			String initialUrl = WdDriver.getCurrentUrl();

			if(domainsAllowed != null && !domainsAllowed.contains(getDomain(initialUrl))) {
				System.out.println(String.format("WEBDRIVER INFO: Automatically adding initial %s Web domain to domainsAllowed List", getDomain(initialUrl)));
				String[] newDomainsAllowed = domainsAllowed.stream().toArray(String[]::new);
				domainsAllowed = Arrays.asList(ArrayUtils.insert(newDomainsAllowed.length, newDomainsAllowed, getDomain(initialUrl)));
				System.out.println(String.format("domainsAllowed: %s", String.join(",", domainsAllowed)));
			}
		} catch(Exception e) {
			final String errorMessage = "WEBDRIVER ERROR: Trying to add the startup domain to domainsAllowed List\nPlease review domainsAllowed List inside Webdriver Java Protocol";
			delegate.popupMessage(errorMessage);
			System.out.println(errorMessage);
		}
	}

	/*
	 * We need to check if click position is within the canvas
	 */
	protected boolean isAtBrowserCanvas(Widget widget) {
		Shape shape = widget.get(Tags.Shape, null);
		if (shape == null) {
			return false;
		}

		// Widget must be completely visible on viewport for screenshots
		return widget.get(WdTags.WebIsFullOnScreen, false);
	}

	protected boolean isForm(Widget widget) {
	    Role role = widget.get(Tags.Role, Roles.Widget);
	    return role.equals(WdRoles.WdFORM);
	}

	@Override
	protected boolean isTypeable(Widget widget) {
		Role role = widget.get(Tags.Role, Roles.Widget);
		if (Role.isOneOf(role, NativeLinker.getNativeTypeableRoles())) {
			// Input type are special...
			if (role.equals(WdRoles.WdINPUT)) {
				String type = ((WdWidget) widget).element.type;
				return WdRoles.typeableInputTypes().contains(type.toLowerCase());
			}
			return true;
		}

		return false;
	}

	/**
	 * Read the ClickableClasses property from test.settings file
	 * to update the clickableClasses while TESTAR is running in Spy mode.
	 */
	private void updateCssClassesFromTestSettingsFile() {
		// Feature only for Spy mode
		if(settings.get(ConfigTags.Mode) != Modes.Spy) {
			return;
		}

		try {
			try(BufferedReader br = new BufferedReader(new FileReader(Main.getTestSettingsFile()))) {
				for(String line; (line = br.readLine()) != null;) {
					if(line.contains(ConfigTags.ClickableClasses.name())){
						List<String> fileClickableClasses = Arrays.asList(line.split("=")[1].trim().split(";"));
						// Check if user added new CSS Classes from test settings file to update the clickableClasses
						for(String webClass : fileClickableClasses) {
							if(!webClass.isEmpty() && !clickableClasses.contains(webClass)) {
								System.out.println("Adding new clickable class from settings file: " + webClass);
								clickableClasses.add(webClass);
								settings.set(ConfigTags.ClickableClasses, clickableClasses);
							}
						}
						// Check if user removed CSS Classes from test settings file to update the clickableClasses
						for(String clickClass : clickableClasses) {
							if(!clickClass.isEmpty() && !fileClickableClasses.contains(clickClass)) {
								System.out.println("Removing the clickable class: " + clickClass);
								clickableClasses.remove(clickClass);
								settings.set(ConfigTags.ClickableClasses, clickableClasses);
							}
						}
					}
				}
			}
		} catch (Exception e) {}
	}
}
