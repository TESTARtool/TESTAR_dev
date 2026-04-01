/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2022-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2022-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle.security;

import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.core.action.Action;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.verdict.Verdict;
import org.testar.webdriver.action.WdSecurityInjectionAction;
import org.testar.webdriver.action.WdSecurityUrlInjectionAction;
import org.testar.webdriver.state.WdDriver;

import java.util.HashSet;
import java.util.Set;

public class XssSecurityOracle extends ActiveSecurityOracle {
    private Set<Action> preferredActions = new HashSet<>();
    private static String xssInjectionText = "<script> console.log('XSS detected!'); </script>";
    private static String xssInjectionURL = "<script>console.log(%27XSS%20detected!%27);</script>";

    public XssSecurityOracle(SecurityResultWriter securityResultWriter, RemoteWebDriver webDriver)
    {
        super(securityResultWriter, webDriver);
    }

    @Override
    public Set<Action> getActions(State state)
    {
        Set<Action> actions = new HashSet<>();
        for (Widget widget : state)
        {
            if (isAtBrowserCanvas(widget) && isTypeable(widget)) {
                actions.add(new WdSecurityInjectionAction(webDriver, widget, xssInjectionText));
            }
        }
        preferredActions.addAll(actions);

        Action urlInjection = getUrlInjectionOrDefault();
        if (urlInjection != null)
            actions.add(urlInjection);

        return actions;
    }

    @Override
    public Set<Action> preSelect(Set<Action> actions)
    {
        Set<Action> intersectingSet = new HashSet<>();
        for (Action action : preferredActions) {
            if (actions.contains(action))
                intersectingSet.add(action);
        }

        if (!intersectingSet.isEmpty())
            return intersectingSet;
        return actions;
    }

    // An XSS injection with a character that is not processed properly 
    // may provoke a 500 server error or Uncaught SyntaxError. 
    // TODO: Enrich this verdict or allow customization
    @Override
    public Verdict getVerdict()
    {
        LogEntries logs = WdDriver.getBrowserLogs();
        for (LogEntry entry : logs) {
            if (entry.getMessage().contains("XSS"))
                securityResultWriter.WriteResult(WdDriver.getCurrentUrl(), "79", "XSS detected");
        }

        return Verdict.OK;
    }

    public static void setXssInjectionText(String xssInjectionText) {
    	XssSecurityOracle.xssInjectionText = xssInjectionText;
    }

    public static void setXssInjectionURL(String xssInjectionURL) {
    	XssSecurityOracle.xssInjectionURL = xssInjectionURL;
    }

    private Action getUrlInjectionOrDefault()
    {
        String url = webDriver.getCurrentUrl();

        if (url.contains("?"))
        {
            // Replace the parameter with the XSS injection
            // regex (?<=X)(.*?)(?=Y) with X,Y delimiters
            String newUrl = url.replaceAll("(?<==)(.*?)(?=&)", xssInjectionURL);
            newUrl = newUrl.replaceFirst("[^=]*$", xssInjectionURL);

            if (!newUrl.equals(url)) {
                return new WdSecurityUrlInjectionAction(newUrl);
            }
        }
        return null;
    }
}
