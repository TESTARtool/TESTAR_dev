/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2022-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2022-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle.security;

import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v145.network.Network;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.core.verdict.Verdict;
import org.testar.webdriver.action.WdSecurityInjectionAction;
import org.testar.webdriver.action.WdSecurityUrlInjectionAction;
import org.testar.webdriver.state.WdDriver;
import org.testar.core.action.Action;
import org.testar.core.state.State;
import org.testar.core.state.Widget;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SqlInjectionSecurityOracle extends ActiveSecurityOracle {
    private boolean errorReceived = false;
    protected Set<Action> proposedActions = new HashSet<>();
    private static String sqlInjectionText = "'";
    private static String sqlInjectionURL = "%27";

    // Use always the 500 Internal Server Error by default
    private static Set<Integer> serverErrorCodes = new HashSet<>(Arrays.asList(500));

    public SqlInjectionSecurityOracle(SecurityResultWriter securityResultWriter, RemoteWebDriver webDriver)
    {
        super(securityResultWriter, webDriver);
    }

    @Override
    public void addListener(DevTools devTools)
    {
        devTools.addListener(Network.responseReceivedExtraInfo(),
                responseReceived -> {
                    if (serverErrorCodes.contains(responseReceived.getStatusCode())) {
                        errorReceived = true;
                    }
                });
    }

    @Override
    public Set<Action> getActions(State state)
    {
        Set<Action> actions = new HashSet<>();
        for (Widget widget : state)
        {
            if (isAtBrowserCanvas(widget) && isTypeable(widget)) {
                actions.add(new WdSecurityInjectionAction(webDriver, widget, sqlInjectionText));
            }
        }

        proposedActions = actions;

        Action urlInjection = getUrlInjectionOrDefault();
        if (urlInjection != null)
        	actions.add(urlInjection);

        return actions;
    }

    @Override
    public Verdict getVerdict()
    {
        if (errorReceived)
            securityResultWriter.WriteResult(WdDriver.getCurrentUrl(), "89", "SQL injection detected");

        return Verdict.OK;
    }

    public static void addServerErrorCodes(Set<Integer> serverErrorCode) {
        SqlInjectionSecurityOracle.serverErrorCodes.addAll(serverErrorCode);
    }

    public static void setSqlInjectionText(String sqlInjectionText) {
    	SqlInjectionSecurityOracle.sqlInjectionText = sqlInjectionText;
    }

    public static void setSqlInjectionURL(String sqlInjectionURL) {
    	SqlInjectionSecurityOracle.sqlInjectionURL = sqlInjectionURL;
    }

    private Action getUrlInjectionOrDefault()
    {
    	String url = webDriver.getCurrentUrl();

    	if (url.contains("?"))
    	{
    		// TODO: Maybe add the injection character instead of replace
    		// Replace the parameter with the SQL injection character
    		// regex (?<=X)(.*?)(?=Y) with X,Y delimiters
    		String newUrl = url.replaceAll("(?<==)(.*?)(?=&)", sqlInjectionURL);
    		// regex = to end of line
    		newUrl = newUrl.replaceFirst("[^=]*$", sqlInjectionURL);
    		if (!newUrl.equals(url)) {
    			return new WdSecurityUrlInjectionAction(newUrl);
    		}
    	}
    	return null;
    }
}
