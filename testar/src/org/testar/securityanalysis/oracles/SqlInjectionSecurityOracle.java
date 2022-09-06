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

import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v101.network.Network;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.WdSecurityInjectionAction;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.WdWidget;
import org.testar.securityanalysis.SecurityResultWriter;

import java.util.HashSet;
import java.util.Set;

public class SqlInjectionSecurityOracle extends ActiveSecurityOracle {
    private boolean errorReceived = false;
    protected Set<Action> proposedActions = new HashSet<>();

    public SqlInjectionSecurityOracle(SecurityResultWriter securityResultWriter, RemoteWebDriver webDriver)
    {
        super(securityResultWriter, webDriver);
    }

    @Override
    public void addListener(DevTools devTools)
    {
        devTools.addListener(Network.responseReceivedExtraInfo(),
                responseReceived -> {
                    if (responseReceived.getStatusCode() == 500)
                        errorReceived = true;
                });
    }

    @Override
    public Set<Action> getActions(State state)
    {
        Set<Action> actions = new HashSet<>();
        for (Widget widget : state)
        {
            if (isAtBrowserCanvas(widget) && isTypeable(widget)) {
                actions.add(new WdSecurityInjectionAction(webDriver, (WdWidget)widget, getInjectionText()));
            }
        }

        proposedActions = actions;
        return actions;
    }

    @Override
    public Verdict getVerdict()
    {
        if (errorReceived)
            securityResultWriter.WriteResult(WdDriver.getCurrentUrl(), "89", "SQL injection detected");

        return Verdict.OK;
    }

    private String getInjectionText()
    {
        return "'";
    }
}
