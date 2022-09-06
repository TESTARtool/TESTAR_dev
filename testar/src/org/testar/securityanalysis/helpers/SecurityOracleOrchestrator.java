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

package org.testar.securityanalysis.helpers;

import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Verdict;
import org.testar.securityanalysis.SecurityResultWriter;
import org.testar.securityanalysis.oracles.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** The SecurityOracleOrchestrator connects the oracles to the protocol.
 * The protocol calls the SecurityOracleOrchestrator for certain actions and these actions are passed on to the oracles.
 */
public class SecurityOracleOrchestrator {
    private Set<BaseSecurityOracle> securityOracles = new HashSet<>();
    private ActiveSecurityOracle activeSecurityOracle;
    private SecurityResultWriter securityResultWriter;

    public SecurityOracleOrchestrator(SecurityResultWriter securityResultWriter, List<String> securityOracleNames, RemoteWebDriver webDriver, DevTools devTools)
    {
        this.securityResultWriter = securityResultWriter;
        setSecurityOracles(securityOracleNames, webDriver);
        addListeners(devTools);
    }

    public boolean hasActiveOracle()
    {
        return (activeSecurityOracle != null);
    }

    public Set<Action> getActions(State state)
    {
        if (activeSecurityOracle != null)
            return activeSecurityOracle.getActions(state);

        return new HashSet<>();
    }

    public Set<Action> preSelect(Set<Action> actions)
    {
        if (activeSecurityOracle != null)
            return activeSecurityOracle.preSelect(actions);

        return actions;
    }

    public void actionSelected(Action action)
    {
        if (activeSecurityOracle != null)
            activeSecurityOracle.actionSelected(action);
    }

    //region configuration
    private void setSecurityOracles(List<String> securityOracleNames, RemoteWebDriver webDriver)
    {
        for (String name : securityOracleNames)
        {
            if (name.compareToIgnoreCase("HeaderAnalysisSecurityOracle") == 0)
                securityOracles.add(new HeaderAnalysisSecurityOracle(securityResultWriter));
            if (name.compareToIgnoreCase("SqlInjectionSecurityOracle") == 0)
                setActiveOracle(new SqlInjectionSecurityOracle(securityResultWriter, webDriver));
            if (name.compareToIgnoreCase("XssSecurityOracle") == 0)
                setActiveOracle(new XssSecurityOracle(securityResultWriter, webDriver));
            if (name.compareToIgnoreCase("TokenInvalidationSecurityOracle") == 0)
                setActiveOracle(new TokenInvalidationSecurityOracle(securityResultWriter, webDriver));
        }
    }

    private void setActiveOracle(ActiveSecurityOracle securityOracle)
    {
        if (activeSecurityOracle == null)
            activeSecurityOracle = securityOracle;
        else
            throw new UnsupportedOperationException("Only one active security oracle at a time please!");
    }
    //endregion

    private void addListeners(DevTools devTools)
    {
        if (activeSecurityOracle != null)
            activeSecurityOracle.addListener(devTools);

        for (BaseSecurityOracle securityOracle : securityOracles)
            securityOracle.addListener(devTools);
    }

    public Verdict getVerdict(Verdict verdict)
    {
        for (BaseSecurityOracle securityOracle : securityOracles) {
            Verdict newVerdict = securityOracle.getVerdict();
            if (newVerdict != null)
                verdict.join(newVerdict);
        }
        if (activeSecurityOracle != null)
            verdict.join(activeSecurityOracle.getVerdict());

        return verdict;
    }
}
