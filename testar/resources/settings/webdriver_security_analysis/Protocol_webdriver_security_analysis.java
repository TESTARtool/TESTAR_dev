import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.NetworkInterceptor;
import org.openqa.selenium.devtools.v101.network.Network;
import org.openqa.selenium.devtools.v101.network.model.BlockedCookieWithReason;
import org.openqa.selenium.devtools.v101.network.model.Headers;
import org.openqa.selenium.devtools.v101.network.model.Response;
import org.openqa.selenium.devtools.v101.network.model.ResponseReceived;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.CodingManager;
import org.testar.SutVisualization;
import org.testar.action.priorization.ActionTags;
import org.testar.action.priorization.SimilarityDetection;
import org.testar.action.priorization.WeightedAction;
import org.testar.monkey.Drag;
import org.testar.monkey.Pair;
import org.testar.monkey.Settings;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.*;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.alayer.exceptions.SystemStartException;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.WdElement;
import org.testar.monkey.alayer.webdriver.WdWidget;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.plugin.NativeLinker;
import org.testar.protocols.WebdriverProtocol;
import org.testar.screenshotjson.JsonUtils;
import org.testar.securityanalysis.NavigationHelper;
import org.testar.securityanalysis.NetworkCollector;
import org.testar.securityanalysis.NetworkDataDto;
import org.testar.securityanalysis.SecurityResultWriter;

import java.awt.datatransfer.StringSelection;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static org.testar.monkey.alayer.Tags.Blocked;
import static org.testar.monkey.alayer.Tags.Enabled;
import static org.testar.monkey.alayer.webdriver.Constants.scrollArrowSize;
import static org.testar.monkey.alayer.webdriver.Constants.scrollThick;

public class Protocol_webdriver_security_analysis extends WebdriverProtocol {
    private String vulnerability = "headers";
    private NetworkCollector networkCollector;
    private SecurityResultWriter securityResultWriter;
    private NavigationHelper navigationHelper;
    private Integer lastSequenceActionNumber;
    private RemoteWebDriver webDriver;

    @Override
    protected void preSequencePreparations() {
        super.preSequencePreparations();
    }

    @Override
    protected SUT startSystem() throws  SystemStartException {
        SUT sut = super.startSystem();
        coordinate();
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

        /*try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        System.out.println("Started analysis");

        navigationHelper = new NavigationHelper();
    }

    private void startNetworkCollector()
    {
        networkCollector = new NetworkCollector();
    }

    private void startSecurityResultWriter(){securityResultWriter = new SecurityResultWriter();}

    private void coordinate() {
        startNetworkCollector();
        startSecurityResultWriter();
        webDriver = WdDriver.getRemoteWebDriver();
        DevTools devTools = ((HasDevTools) webDriver).getDevTools();
        devTools.createSession();
        addListnerForHeaders(devTools);
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
        lastSequenceActionNumber = 0;
    }

    private void addListnerForHeaders(DevTools devTools)
    {
        System.out.println("Listner added");
        devTools.addListener(Network.responseReceivedExtraInfo(),
                responseReceived -> {
                    if (responseReceived.getStatusCode() == 500)
                    {
                        // move!
                        if (vulnerability.contains("SQLI"))
                        securityResultWriter.WriteResult(WdDriver.getCurrentUrl(), "89", "SQLI detected");
                    }
                    // TODO: add statuscode to network listener for SQL test
                    /*printHeadersText2(responseReceived);
                    printHeadersText(responseReceived.getResponse());*/
                    Headers headers = responseReceived.getHeaders();
                    if (!headers.isEmpty()) {
                        NetworkDataDto data = new NetworkDataDto();
                        data.type = "Headers";
                        data.requestId = responseReceived.getRequestId().toString();
                        data.data = new HashMap<>();
                        headers.forEach((key, value) -> {
                            data.data.put(key, value.toString());
                        });
                        networkCollector.addData(data);
                    }
                });
    }

    private int progress = 1;

    private Action login(State state)
    {
        cookieManipulator();

        System.out.println("Login");
        CompoundAction.Builder builder = new CompoundAction.Builder();
        Action submitAction = null;
        for (Widget widget : state) {
            if (widget.get(Tags.Title).contains("email")){
                builder.add(new WdSecurityInjectionAction(webDriver, (WdWidget)widget, "jeroen@stratory.nl"), 0.1);
            }
            else if (widget.get(Tags.Title).contains("password")){
                builder.add(new WdSecurityInjectionAction(webDriver, (WdWidget)widget, "1234"), 0.1);
            }
            else if (widget.get(Tags.Path).contains("0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 2")){
                StdActionCompiler ac = new AnnotatingActionCompiler();
                submitAction = ac.leftClickAt(widget);
            }
        }

        if (submitAction != null) {
            System.out.println("Add submit");
            builder.add(submitAction, 0.1);
        }
        else
            System.out.println("No submit found");

        progress = 2;

        return builder.build();
    }

    private void cookieManipulator() {
        Set<Cookie> cookies = webDriver.manage().getCookies();
        System.out.println("Init cookies");
        for (Cookie cookie : cookies)
        {
            System.out.println(cookie.getName() + " " + cookie.getValue());
        }
        Cookie nCookie = new Cookie("Token", "value of Token");
        webDriver.manage().addCookie(nCookie);

        System.out.println("Added cookies");
        for (Cookie cookie : cookies)
        {
            System.out.println(cookie.getName() + " " + cookie.getValue());
        }
    }

    private Action logout(State state)
    {
        //cookieManipulator();

        String email = "";
        String password = "";

        System.out.println("Login");
        CompoundAction.Builder builder = new CompoundAction.Builder();
        Action submitAction = null;
        for (Widget widget : state) {
            if (widget.get(Tags.Title).contains("email")){
                builder.add(new WdSecurityInjectionAction(webDriver, (WdWidget)widget, email), 0.1);
            }
            else if (widget.get(Tags.Title).contains("password")){
                builder.add(new WdSecurityInjectionAction(webDriver, (WdWidget)widget, password), 0.1);
            }
            else if (widget.get(Tags.Path).contains("0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 2")){
                StdActionCompiler ac = new AnnotatingActionCompiler();
                submitAction = ac.leftClickAt(widget);
            }
        }

        if (submitAction != null) {
            System.out.println("Add submit");
            builder.add(submitAction, 0.1);
        }
        else
            System.out.println("No submit found");

        progress = 3;

        return builder.build();
    }

    private void printHeadersText2(ResponseReceived response) {
        System.out.println("Network responseReceived:");
        System.out.println(response.toString());
        System.out.println(response.getHasExtraInfo());
        System.out.println(response.getType());
        System.out.println(response.toString());
        System.out.println(response.toString());
        System.out.println(response.toString());
        System.out.println(response.toString());
    }
    private void printHeadersText(Response response) {
        System.out.println("Network response:");
        System.out.println(response.toString());
    }

    private String getValues(String key, Object object)
    {
        System.out.println("Header object: " + key);
        System.out.println(object);
        return object.toString();
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

/*
        try{
            wait(1000);
        }
        catch(Exception e){}

        */
        String url = "";

        webDriver.navigate().to(url);
        /*

        try{
            wait(1000);
        }
        catch(Exception e){}*/

        State state = super.getState(system);
        networkCollector.printData();
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
        securityResultWriter.WriteVisit(WdDriver.getCurrentUrl());

        if (vulnerability.equals("headers"))
        {
            List<NetworkDataDto> datas = networkCollector.getDataBySequence(lastSequenceActionNumber);
            for (NetworkDataDto data : datas) {
                if (lastSequenceActionNumber < data.sequence)
                    lastSequenceActionNumber = data.sequence;

                if (data.type == "Headers") {
                    for (Map.Entry<String, String> header : data.data.entrySet()) {
                        if (header.getKey().equals("Set-Cookie")) {
                            if (!header.getValue().contains("Secure;")) {
                                securityResultWriter.WriteResult(WdDriver.getCurrentUrl(), "614", "cookie not set secure: " + header.getKey() + " " + header.getValue());
                                System.out.println("Header insecure:");
                                System.out.println(header.getValue());
                            } else {
                                System.out.println("Header secure:");
                                System.out.println(header.getValue());
                            }
                        }
                    }
                }
            }
        }
        else if (vulnerability.contains("XSS"))
        {
            LogEntries logs = webDriver.manage().logs().get(LogType.BROWSER);
            for (LogEntry entry : logs) {
                if (entry.getMessage().contains("XSS"))
                    securityResultWriter.WriteResult(WdDriver.getCurrentUrl(), "79", "XSS detected");
            }
        }
        /*else if (vulnerability.contains("SQL"))
        {
            securityResultWriter.WriteResult(WdDriver.getCurrentUrl(), "89", "SQLI detected");
        }*/

        //securityResultWriter.WriteResult(WdDriver.getCurrentUrl(), "result");
        // system crashes, non-responsiveness and suspicious titles automatically detected!

        //-----------------------------------------------------------------------------
        // MORE SOPHISTICATED ORACLES CAN BE PROGRAMMED HERE (the sky is the limit ;-)
        //-----------------------------------------------------------------------------

        // ... YOU MAY WANT TO CHECK YOUR CUSTOM ORACLES HERE ...


        /*Verdict verdict = super.getVerdict(state);
        htmlReport.addTestVerdict(verdict);*/

        return Verdict.OK;
    }

    private void printState(State state)
    {
        int i = 0;
        for(Widget action : state)
        {
            i++;
        }
        System.out.println("Statecount: " + i);
        String url = WdDriver.getCurrentUrl();
        System.out.println("url: " + url);
    }

    private void printActions(Set<Action> actions)
    {
        int i = 0;
        for(Action action : actions)
        {
            i++;
        }
        System.out.println("ActionCount: " + i);
        String url = WdDriver.getCurrentUrl();
        System.out.println("url: " + url);
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
        //printState(state);
        // Kill unwanted processes, force SUT to foreground
        Set<Action> actions = super.deriveActions(system, state);
        Set<Action> filteredActions = new HashSet<>();

        // create an action compiler, which helps us create actions
        // such as clicks, drag&drop, typing ...
        StdActionCompiler ac = new AnnotatingActionCompiler();

        // Check if forced actions are needed to stay within allowed domains
        Set<Action> forcedActions = detectForcedActions(state, ac);
        if (forcedActions != null)
            printActions(forcedActions);

        // iterate through all widgets
        for (Widget widget : state) {
            // only consider enabled and non-tabu widgets
            /*if (!widget.get(Enabled, true)) {
                continue;
            }*/
            // The blackListed widgets are those that have been filtered during the SPY mode with the
            //CAPS_LOCK + SHIFT + Click clickfilter functionality.
            /*if(blackListed(widget)){
                if(isTypeable(widget)){
                    filteredActions.add(ac.clickTypeInto(widget, getRandomText(widget), true));
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
*/
            // type into text boxes
            if (isAtBrowserCanvas(widget) && isTypeable(widget)) {
                if(whiteListed(widget) || isUnfiltered(widget)){
                    if (!vulnerability.contains("headers"))
                        actions.add(new WdSecurityInjectionAction(webDriver, (WdWidget)widget, getRandomText(widget)));
                    else
                        actions.add(ac.clickTypeInto(widget, getRandomText(widget), true));
                }else{
                    // filtered and not white listed:
                    filteredActions.add(ac.clickTypeInto(widget, getRandomText(widget), true));
                }
            }


            // left clicks, but ignore links outside domain
            if (/*isAtBrowserCanvas(widget) && */isClickable(widget)) {
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

        //printActions(actions);
        //printActions(filteredActions);

        Action urlInjection = getUrlInjectionOrDefault();
        if (urlInjection != null)
            actions.add(urlInjection);

        /*if(actions.isEmpty()) {
            System.out.println("actions is empty");
            Action urlInjection = getUrlInjectionOrDefault();
            if (urlInjection != null)
        	    return new HashSet<>(Collections.singletonList(urlInjection));
        }*/

        // If we have forced actions, prioritize and filter the other ones
        if (forcedActions != null && forcedActions.size() > 0) {
            System.out.println("Action forced");
            filteredActions = actions;
            actions = forcedActions;
        }

        // Enable TESTAR to navigate back
        /*if (ThreadLocalRandom.current().nextInt(0, 10 + 1) == 1)
            actions.add(new WdHistoryBackAction());*/

        //Showing the grey dots for filtered actions if visualization is on:
        if(visualizationOn || mode() == Modes.Spy) SutVisualization
                .visualizeFilteredActions(cv, state, filteredActions);

        Set<Action> newSet = new HashSet<>();
        //if (progress == 1) {
            newSet.add(login(state));
            System.out.println("add login");
            return newSet;
        /*}
        else if (progress == 2)
        {
            newSet.add(logout(state));
        }

        return actions;*/
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
        actions = navigationHelper.filterActions(actions);
        return super.preSelectAction(system, state, actions);
    }

    private Action getUrlInjectionOrDefault()
    {
        String url = WdDriver.getCurrentUrl();

        String injection = "<script>console.log(%27XSS%20detected!%27);</script>";
        if (url.contains("?"))
        {
            String newUrl = url.replaceAll("=.*" + "&", injection + "&");
            newUrl = newUrl.replaceFirst("[^=]*$", injection);
            /*String variables = url.split("\\?")[1];
            System.out.println("variables: " + variables);
            String first = variables.split("&")[0];
            System.out.println("first: " + first);
            String value = first.split("=")[1];
            System.out.println("value: " + value);*/

            System.out.println("newUrl: " + newUrl);
            /*if (!value.contains(("script")))
            {
                url = url.replace(value, "<script>console.log(%27XSS%20detected!%27);</script>");
                return new WdSecurityUrlInjectionAction(url);
            }*/

            if (!newUrl.equals(url)) {
                System.out.println("UrlInjection added");
                return new WdSecurityUrlInjectionAction(newUrl);
            }
        }
        return null;
    }

    /** Enables input text to be overwritten with injections **/
    @Override
    public String getRandomText(Widget widget)
    {
        // TODO: Only be true while analysing injection
        if (!vulnerability.contains("headers")) //Injection analysis
        {
            /*if ((new Random()).nextBoolean())
                return "'";
            else // TODO: Generate random string to identify alert later (and save string)
            {*/
                //String string = (new Random()).nextDouble().toString();
                //return "'";
            if (vulnerability.contains("XSS"))
                return "<script> console.log('XSS detected!'); </script>";
            else
                return "'";
            /*}*/
        }
        else {
            return super.getRandomText(widget);
        }
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
        StdActionCompiler ac = new AnnotatingActionCompiler();
        boolean clicked = false;
        if (state != null) {
            List<Finder> targets = action.get(Tags.Targets, null);
            if (targets != null) {
                for (Finder f : targets) {
                    Widget w = f.apply(state);
                    if (isAtBrowserCanvas(w))
                        continue;

                    WebElement element = webDriver.findElement(new By.ByPartialLinkText(((WdWidget) w).element.name));
                    Actions SeleniumAction = new Actions(webDriver);
                    SeleniumAction.moveToElement(element).perform();
                    SeleniumAction.click().perform();
                    clicked = true;
                }
            }
        }

        navigationHelper.setExecution(action);

        if(!clicked)
            return super.executeAction(system, state, action);
        else
            return super.executeAction(system, state, new NOP());
    }
}
