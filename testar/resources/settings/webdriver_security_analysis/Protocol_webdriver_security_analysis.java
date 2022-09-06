/**
 * Copyright (c) 2018 - 2022 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2022 Universitat Politecnica de Valencia - www.upv.es
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

import org.apache.commons.lang.NotImplementedException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v104.network.Network;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.SutVisualization;
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
import org.testar.plugin.NativeLinker;
import org.testar.protocols.WebdriverProtocol;
import org.testar.securityanalysis.*;
import org.testar.securityanalysis.helpers.SecurityOracleOrchestrator;

import java.util.*;

/**
 * We recommend running this example of security analysis protocol with the OWASP benchmark SUT. 
 * https://github.com/OWASP-Benchmark/BenchmarkJava 
 * https://owasp.org/www-project-benchmark/ 
 * 
 * Required Java 8
 * git clone https://github.com/OWASP-Benchmark/BenchmarkJava
 * Execute runBenchmark.bat or runBenchmark.sh to deploy the vulnerable SUT example:
 * https://localhost:8443/benchmark/
 * 
 * Other websites that offer web vulnerabilities examples
 * https://www.acunetix.com/blog/web-security-zone/test-xss-skills-vulnerable-sites/
 */
public class Protocol_webdriver_security_analysis extends WebdriverProtocol {
    private SecurityResultWriter securityResultWriter;
    private NavigationHelper navigationHelper;
    private RemoteWebDriver webDriver;
    private SecurityOracleOrchestrator oracleOrchestrator;
    private SecurityConfiguration securityConfiguration = new SecurityConfiguration();

    @Override
    protected void initialize(Settings settings) {
    	super.initialize(settings);
    	// Disable web security such as "ignore-certificate-errors" for local testing purposes
    	//WdDriver.disableSecurity = true;
    }

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
        System.out.println("Started analysis");
        navigationHelper = new NavigationHelper();
    }

    //region InnerLoop
    @Override
    protected State getState(SUT system) throws StateBuildException {
        State state = super.getState(system);
        return state;
    }

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

        /** replace typable widgets when an oracle is active **/
        if (oracleOrchestrator.hasActiveOracle())
        {
            actions.addAll(oracleOrchestrator.getActions(state));
        }
        else {
            for (Widget widget : state) {
                // type into text boxes
                if (isAtBrowserCanvas(widget) && isTypeable(widget)) {
                    if(whiteListed(widget) || isUnfiltered(widget))
                        actions.add(ac.clickTypeInto(widget, getRandomText(widget), true));
                    else
                        // filtered and not white listed:
                        filteredActions.add(ac.clickTypeInto(widget, getRandomText(widget), true));
                }
            }
        }

        // iterate through all widgets
        for (Widget widget : state) {
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

        // If we have forced actions, prioritize and filter the other ones
        if (forcedActions != null && forcedActions.size() > 0) {
            System.out.println("Action forced");
            filteredActions = actions;
            actions = forcedActions;
        }

        //Showing the grey dots for filtered actions if visualization is on:
        if(visualizationOn || mode() == Modes.Spy) SutVisualization
                .visualizeFilteredActions(cv, state, filteredActions);

        return actions;
    }

    @Override
    protected Set<Action> preSelectAction(SUT system, State state, Set<Action> actions){
        actions = navigationHelper.filterActions(actions);
        actions = oracleOrchestrator.preSelect(actions);
        return super.preSelectAction(system, state, actions);
    }

    @Override
    protected boolean executeAction(SUT system, State state, Action action) {
        oracleOrchestrator.actionSelected(action);

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

    @Override
    protected Verdict getVerdict(State state) {
        securityResultWriter.WriteVisit(WdDriver.getCurrentUrl());
        Verdict verdict = Verdict.OK;
        oracleOrchestrator.getVerdict(verdict);
        return verdict;
    }
    //endregion

    //region PrivateFunctions
    private void startSecurityResultWriter(){
        if (securityConfiguration.resultWriterOutput.compareToIgnoreCase("json") == 0)
            securityResultWriter = new JsonSecurityResultWriter();
        else
            throw new NotImplementedException("Unknown output type '" + securityConfiguration.resultWriterOutput + "'");
    }

    private void coordinate() {
        startSecurityResultWriter();
        webDriver = WdDriver.getRemoteWebDriver();
        DevTools devTools = ((HasDevTools) webDriver).getDevTools();
        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
        lastSequenceActionNumber = 0;
        oracleOrchestrator = new SecurityOracleOrchestrator(securityResultWriter, securityConfiguration.oracles, webDriver, devTools);
    }
    //endregion

    //region Overrides
    /** Enables TESTAR to click components that are outside the window **/
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
    //endregion
}
