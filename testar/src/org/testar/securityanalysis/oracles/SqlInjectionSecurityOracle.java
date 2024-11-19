/***************************************************************************************************
 *
 * Copyright (c) 2022 - 2024 Open Universiteit - www.ou.nl
 * Copyright (c) 2022 - 2024 Universitat Politecnica de Valencia - www.upv.es
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

import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v130.network.Network;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.WdSecurityInjectionAction;
import org.testar.monkey.alayer.actions.WdSecurityUrlInjectionAction;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.securityanalysis.SecurityResultWriter;

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
