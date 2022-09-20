/***************************************************************************************************
 *
 * Copyright (c) 2022 Open Universiteit - www.ou.nl
 * Copyright (c) 2022 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.securityanalysis.oracles;

import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.actions.WdSecurityInjectionAction;
import org.testar.monkey.alayer.actions.WdSecurityUrlInjectionAction;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.WdWidget;
import org.testar.securityanalysis.SecurityResultWriter;

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
                actions.add(new WdSecurityInjectionAction(webDriver, (WdWidget)widget, xssInjectionText));
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
        LogEntries logs = webDriver.manage().logs().get(LogType.BROWSER);
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
        String url = WdDriver.getCurrentUrl();

        if (url.contains("?"))
        {
            String newUrl = url.replaceAll("=.*" + "&", xssInjectionURL + "&");
            newUrl = newUrl.replaceFirst("[^=]*$", xssInjectionURL);

            if (!newUrl.equals(url)) {
                return new WdSecurityUrlInjectionAction(newUrl);
            }
        }
        return null;
    }
}
